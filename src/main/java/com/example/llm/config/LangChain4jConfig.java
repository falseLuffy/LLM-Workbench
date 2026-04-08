package com.example.llm.config;

import com.example.llm.agent.WorkAssistantAgent;
import com.example.llm.agent.tool.DatabaseTools;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
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

    @Value("${llm.openai.api-key:sk-your-key}")
    private String apiKey;

    @Value("${llm.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    public static final String CACHE_FILE = "semantic-cache.json";

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("gpt-4o") // Use gpt-4o for complex reasoning as an agent
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
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

    /**
     * Assemble the WorkAssistantAgent
     */
    @Bean
    public WorkAssistantAgent workAssistantAgent(ChatLanguageModel chatModel, DatabaseTools databaseTools) {
        return AiServices.builder(WorkAssistantAgent.class)
                .chatLanguageModel(chatModel)
                .tools(databaseTools) // Give the agent its "hands" (database tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // Give the agent short-term memory
                .build();
    }
}
