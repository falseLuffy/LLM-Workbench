package com.example.llm.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WorkAssistantAgent {

    @SystemMessage({
        "你是一个专业的企业工作助理。",
        "你拥有访问公司数据库的权限。当用户询问关于用户统计、用户资料或系统状态的问题时，请利用提供的工具进行查询。",
        "在回复时，要保持礼貌、准确。如果工具返回结果为空，请如实告知用户。",
        "除非用户明确要求展示技术细节，否则请用自然的语言描述查询结果。"
    })
    String chat(@UserMessage String userMessage);
}
