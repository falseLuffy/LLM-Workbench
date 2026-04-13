package com.ISC.llm.controller;

import com.ISC.llm.common.Result;
import com.ISC.llm.service.rag.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "知识库管理", description = "RAG 知识库上传与处理接口")
@RestController
@RequestMapping("/v1/proxy/knowledge")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "上传并导入文档到知识库 (支持 PDF, DOCX, TXT)")
    @PostMapping("/upload")
    public Result<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("File is empty");
        }
        knowledgeBaseService.ingestDocument(file);
        return Result.success("Document uploaded and processed successfully.");
    }
}
