package com.example.llm.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WorkAssistantAgent {

    @SystemMessage({
        "你是一个专业的企业工作助理。",
        "你拥有访问公司数据库和知识库的权限。",
        "1. 关于用户和系统状态：请利用 Database 提供的工具进行查询。",
        "2. 关于公司政策、产品手册或背景资料：请利用提供的 Context 片段进行检索和总结。",
        "在回复时，要保持礼貌、准确。如果工具和知识库都无法提供答案，请如实告知用户。",
        "除非用户明确要求展示技术细节，否则请用自然的语言描述内容。"
    })
    String chat(@UserMessage String userMessage);
}
