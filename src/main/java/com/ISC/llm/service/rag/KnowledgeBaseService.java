package com.ISC.llm.service.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public void ingestDocument(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            log.info("📂 正在解析文档: {}", file.getOriginalFilename());
            
            // 1. 使用 Apache Tika 解析文档（支持 PDF, Docx 等）
            DocumentParser parser = new ApacheTikaDocumentParser();
            Document document = parser.parse(inputStream);
            
            // 2. 将文档拆分为片段（每个片段 300 字符，包含 30 字符重叠）
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
            List<TextSegment> segments = splitter.split(document);
            
            // 3. 计算向量并存入向量库
            log.info("✂️ 已拆分为 {} 个片段。开始计算向量并入库...", segments.size());
            embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);
            
            // 4. 持久化到本地文件
            ((InMemoryEmbeddingStore<TextSegment>) embeddingStore).serializeToFile(Paths.get(com.ISC.llm.config.LangChain4jConfig.CACHE_FILE));
            
            log.info("✅ 文档 [{}] 已成功导入知识库。", file.getOriginalFilename());
            
        } catch (Exception e) {
            log.error("❌ 文档导入失败: {}", e.getMessage(), e);
            throw new RuntimeException("知识库导入异常: " + e.getMessage());
        }
    }
}
