package com.ISC.llm.service.proxy;

import com.ISC.llm.model.dto.ChatRequest;
import com.ISC.llm.model.dto.ChatResponse;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmOptimizationService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private static final double SIMILARITY_THRESHOLD = 0.95;

    /**
     * 3.1 高级语义缓存（模型隔离版）
     * 仅返回由相同模型生成的缓存结果，以确保响应质量和格式的一致性。
     */
    public ChatResponse checkSemanticCache(ChatRequest request) {
        String lastMessage = getUserLastMessage(request);
        if (lastMessage.isEmpty()) return null;

        Embedding queryEmbedding = embeddingModel.embed(lastMessage).content();

        // 在向量数据库中搜索
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 5, SIMILARITY_THRESHOLD);

        for (EmbeddingMatch<TextSegment> match : relevant) {
            String cachedModel = match.embedded().metadata().getString("model");
            
            // 模型隔离：只有缓存模型与当前请求模型匹配时才返回
            if (request.getModel().equals(cachedModel)) {
                String cachedJson = match.embedded().metadata().getString("response_json");
                log.info("🚀 语义缓存命中（模型匹配）! 模型: {} | 相似度: {}", cachedModel, match.score());
                
                try {
                    return new com.fasterxml.jackson.databind.ObjectMapper().readValue(cachedJson, ChatResponse.class);
                } catch (Exception e) {
                    log.error("解析缓存响应失败", e);
                }
            }
        }
        return null;
    }

    public void saveToCache(ChatRequest request, ChatResponse response) {
        String lastMessage = getUserLastMessage(request);
        if (lastMessage.isEmpty()) return;

        try {
            String responseJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(response);
            
            // 存储文本、向量和模型特定的元数据
            TextSegment segment = TextSegment.from(lastMessage, Metadata.from("response_json", responseJson)
                    .add("model", request.getModel())); // 将缓存与特定模型绑定
            
            Embedding embedding = embeddingModel.embed(lastMessage).content();
            
            embeddingStore.add(embedding, segment);
            
            // 持久化到本地文件
            ((InMemoryEmbeddingStore<TextSegment>) embeddingStore).serializeToFile(Paths.get(com.ISC.llm.config.LangChain4jConfig.CACHE_FILE));
            
            log.info("💾 已将模型 [{}] 的结果保存至持久化向量库。", request.getModel());
        } catch (Exception e) {
            log.error("保存语义缓存失败", e);
        }
    }

    private String getUserLastMessage(ChatRequest request) {
        return request.getMessages().stream()
                .filter(m -> "user".equals(m.getRole()))
                .map(ChatRequest.Message::getContent)
                .reduce((first, second) -> second)
                .orElse("");
    }

    /**
     * 3.2 提示词压缩（数据冗余清理）
     */
    public void compressPrompt(ChatRequest request) {
        log.info("✂️ Compressing prompt for model: {}", request.getModel());
        for (ChatRequest.Message msg : request.getMessages()) {
            if ("user".equals(msg.getRole())) {
                String content = msg.getContent();
                // Simple logic: Remove excessive whitespace and common filler words
                String simplified = content.replaceAll("\\s+", " ").trim();
                msg.setContent(simplified);
            }
        }
    }

    /**
     * 3.4 动态模型路由
     * 如果任务意图简单，自动路由至低成本的 DeepSeek 模型
     */
    public void routeModel(ChatRequest request) {
        String lastMessage = getUserLastMessage(request);

        // 简单逻辑：如果消息长度 < 50 且不包含 "代码" 或 "分析"，则使用标准模型
        if (lastMessage.length() < 50 && !lastMessage.toLowerCase().contains("code") 
            && "deepseek-reasoner".equals(request.getModel())) {
            log.info("🔀 任务较简单，自动路由至标准模型 (deepseek-chat) 以节省成本");
            request.setModel("deepseek-chat");
        }
    }
}
