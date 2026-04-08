# LLM Workbench Service (Advanced Agentic Architecture)

这是一个基于 **Spring Boot 2.7.x + LangChain4j** 构建的现代化大模型中转与智能体工作站。它集成了 Token 极致优化、向量语义缓存、RAG 知识库以及具备业务操作能力的智能助手。

---

## 🌟 核心功能 (Key Features)

### 1. 智能模型中转代理 (LLM Proxy Gateway)
- **标准兼容**：完全兼容 OpenAI API 格式，支持快速切换底层模型供应商。
- **动态模型路由**：自动识别任务难度。对于简单、字数少的任务，自动路由至低成本模型（如 `gpt-4o-mini`），成本降幅高达 **90%**。
- **提示词压缩**：在发送请求前自动进行冗余清理与空格剔除，节省输入 Token。

### 2. 极致 Token 优化与语义缓存
- **向量语义缓存**：不同于传统 KV 缓存，系统通过 **LangChain4j** 对问题进行向量化处理。
- **语义命中**：即使提问方式不同（语义相近），也能命中缓存。
- **模型隔离策略**：缓存具备模型感知能力，确保 GPT-4 的回答只会被 GPT-4 的后续请求复用，防止质量污染。
- **持久化文件引擎**：使用本地 `semantic-cache.json` 进行向量存储，**无需部署 Docker 版 Milvus** 即可享受企业级记忆能力。

### 3. 企业级双核智能体 (Agentic Intelligence)
- **RAG 知识库 (阅读文档)**：集成 **Apache Tika** 引擎，支持 PDF、Word、TXT 等文档上传。系统自动进行分片与向量化，构建企业私有知识库。
- **过程助理 (操作业务)**：具备 **Tool Use (函数调用)** 能力。支持通过自然语言直接查询 MySQL 数据库（如用户统计、用户资料检索）。
- **长短期记忆**：内置对话窗口记忆（Message Window Chat Memory），支持多轮复杂对话。

---

## 🛠️ 技术栈 (Tech Stack)

- **核心框架**：Spring Boot 2.7.x (Java 8 稳健版)
- **大模型接入**：LangChain4j (0.30.0+)
- **持久层**：MyBatis Plus + MySQL 8.0
- **向量存储**：Persistent InMemoryStore (Serverless) + Lucene 逻辑
- **文档处理**：Apache Tika
- **接口规范**：SpringDoc / Swagger 3 & RESTful API

---

## 🚀 快速启动

### 1. 环境准备
- JDK 1.8+
- MySQL 5.7+ (执行 `sql/init.sql` 初始化数据库)
- 安装 Redis (可选，用于基础元数据缓存)

### 2. 配置 (application.yml)
在配置文件中填入您的 **OpenAI API Key**：
```yaml
llm:
  openai:
    api-key: sk-xxxx
    base-url: https://api.openai.com/v1
```

### 3. 运行
```bash
mvn spring-boot:run
```

---

## 📡 核心 API 概览

| 模块 | 接口地址 | 说明 |
| :--- | :--- | :--- |
| **智能体对话** | `GET /v1/agent/ask` | 双核助理（查文档+查库） |
| **中转代理** | `POST /v1/proxy/chat/completions` | 标准 OpenAI 格式，带 Token 优化 |
| **知识库上传** | `POST /v1/proxy/knowledge/upload` | 投喂 PDF/Word 等业务文档 |
| **数据投喂** | `POST /v1/proxy/data/inject` | 手动注入高质量 Q&A 缓存对 |
| **健康监测** | `GET /sys/health` | 检查系统运行状态 |

---

## 📦 部署建议 (Production)

- **Windows Server**：推荐使用 **WinSW** 将项目注册为 Windows 系统服务，实现开机自启。
- **Linux**：使用 Systemd 或 Docker 部署。
- **数据持久化**：务必备份项目根目录下的 `semantic-cache.json`，这是您的核心资产（知识库与缓存）。

---

> [!TIP]
> **关于成本**：本项目设计的初衷是**“消灭重复 Token 支出”**。通过预设的语义缓存，您的常见业务问题的 API 成本将趋近于 **$0**。
