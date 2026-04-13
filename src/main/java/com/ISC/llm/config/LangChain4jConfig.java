package com.ISC.llm.config;

import com.ISC.llm.agent.WorkAssistantAgent;
import com.ISC.llm.agent.tool.DatabaseTools;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LangChain4jConfig {

    @Value("${llm.deepseek.api-key:sk-your-deepseek-key}")
    private String apiKey;

    @Value("${llm.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    public static final String CACHE_FILE = "semantic-cache.json";

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        // DeepSeek 深度求索兼容 OpenAI 协议
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("deepseek-chat") // DeepSeek 主聊天模型
                .build();
    }

    /**
     * 注意：DeepSeek 官方目前不直接提供 Embedding API。
     * 我们保留通用的 OpenAI 向量模型（或者您可以切换为本地 BGE/all-MiniLM 运行）
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey) // 如果 Embedding 使用不同的 Key，请在此修改
                .baseUrl("https://api.openai.com/v1") // 保留 OpenAI 用于向量计算或使用本地模型
                .modelName("text-embedding-3-small")
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        Path path = Paths.get(CACHE_FILE);
        File file = path.toFile();
        if (file.exists() && file.length() > 0) {
            return InMemoryEmbeddingStore.fromFile(path);
        } else {
            return new InMemoryEmbeddingStore<>();
        }
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.7)
                .build();
    }

    @Bean
    public WorkAssistantAgent workAssistantAgent(ChatLanguageModel chatModel, 
                                                 DatabaseTools databaseTools, 
                                                 ContentRetriever contentRetriever) {
        return AiServices.builder(WorkAssistantAgent.class)
                .chatLanguageModel(chatModel)
                .tools(databaseTools)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) 
                .build();
    }
}
