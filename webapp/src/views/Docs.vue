<template>
  <el-container class="docs-container">
    <el-aside width="260px" class="docs-sidebar">
      <div class="sidebar-header">
        <el-button @click="goBack" class="back-btn" size="small">返回工作台</el-button>
        <h3>开发使用文档</h3>
      </div>
      <el-menu
        :default-active="activeDoc"
        class="docs-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="intro">
          <el-icon><InfoFilled /></el-icon>
          <span>项目简介</span>
        </el-menu-item>
        
        <el-sub-menu index="features">
          <template #title>
            <el-icon><Star /></el-icon>
            <span>核心技术特性</span>
          </template>
          <el-menu-item index="feature-cache">语义缓存 (Cache)</el-menu-item>
          <el-menu-item index="feature-route">动态路由 (Routing)</el-menu-item>
          <el-menu-item index="feature-compress">提示词压缩</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="api">
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>接口参考 (API)</span>
          </template>
          <el-menu-item index="api-proxy">对话代理 (OpenAI)</el-menu-item>
          <el-menu-item index="api-agent">AI 智能体 (Agent)</el-menu-item>
          <el-menu-item index="api-rag">知识库上传 (RAG)</el-menu-item>
          <el-menu-item index="api-inject">手动数据投喂</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="auth">
          <el-icon><Lock /></el-icon>
          <span>鉴权与凭证</span>
        </el-menu-item>

        <el-menu-item index="monitoring">
          <el-icon><Odometer /></el-icon>
          <span>健康监测</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-main class="docs-content">
      <div class="content-wrapper">
        <!-- 项目简介 -->
        <section v-if="activeDoc === 'intro'" class="doc-section">
          <h1>项目简介</h1>
          <p>LLM Proxy 是一款面向企业级应用的<strong>大模型统一网关</strong>。基于 Spring Boot 2.7 与 LangChain4j 构建，提供高性能的模型中转、Token 节流优化以及语义化的 RAG 能力。</p>
          <div class="feature-tags">
            <el-tag>高性能</el-tag>
            <el-tag type="success">Token 节流</el-tag>
            <el-tag type="warning">OpenAI 兼容</el-tag>
            <el-tag type="danger">RAG 增强</el-tag>
          </div>
          <h3>快速开始三步走：</h3>
          <el-steps direction="vertical" :active="3">
            <el-step title="配置上游" description="在 application.yml 中配置 DeepSeek 或其他供应商的 API Key。" />
            <el-step title="获取凭证" description="在控制台生成以 sk- 开头的开发者 API Key。" />
            <el-step title="发起请求" description="修改客户端 Base URL 为本网关地址，携带鉴权头即可调用。" />
          </el-steps>
        </section>

        <!-- 语义缓存 -->
        <section v-else-if="activeDoc === 'feature-cache'" class="doc-section">
          <h1>语义缓存 (Semantic Cache)</h1>
          <p>网关会在存储层记录用户的请求与响应。对于相似度极高的请求，网关将直接通过向量搜索命中结果并返回。</p>
          <el-alert type="info" :closable="false" show-icon>
            <strong>优势：</strong>毫秒级响应，零 Token 消耗，不依赖上游模型可用性。
          </el-alert>
          <h3>配置建议</h3>
          <p>默认阈值设置为 0.95。适用于客服问答、常见问题、重复指令等高频场景。</p>
        </section>

        <!-- 动态路由 -->
        <section v-else-if="activeDoc === 'feature-route'" class="doc-section">
          <h1>动态路由 (Dynamic Routing)</h1>
          <p>网关可以根据请求的 <code>model</code> 参数动态映射到不同的物理模型。例如：</p>
          <ul>
            <li>复杂指令：路由至 <code>deepseek-reasoner</code> (R1)。</li>
            <li>简单对话：路由至 <code>deepseek-chat</code> (V3)。</li>
          </ul>
        </section>

        <!-- 对话代理接口 -->
        <section v-else-if="activeDoc === 'api-proxy'" class="doc-section">
          <h1>对话代理接口</h1>
          <p>完全兼容 OpenAI API 格式。支持流式与非流式调用（本示例为非流式）。</p>
          <div class="api-card">
            <span class="method post">POST</span>
            <span class="url">/v1/proxy/chat/completions</span>
          </div>
          <h3>请求体示例：</h3>
          <div class="code-block">
            <pre>{
  "model": "deepseek-chat",
  "messages": [{"role": "user", "content": "帮我写一段代码"}],
  "temperature": 0.7
}</pre>
          </div>
        </section>

        <!-- 智能体接口 -->
        <section v-else-if="activeDoc === 'api-agent'" class="doc-section">
          <h1>AI 智能体 (Agent)</h1>
          <p>基于 LangChain4j 实现的智能助理，具备使用工具和查询知识库的能力。</p>
          <div class="api-card">
            <span class="method get">GET</span>
            <span class="url">/v1/agent/ask</span>
          </div>
          <h3>参数：</h3>
          <el-table :data="[{p:'prompt', t:'String', d:'咨询的问题内容', r:'是'}]" border size="small">
            <el-table-column prop="p" label="参数名" width="100"/>
            <el-table-column prop="t" label="类型" width="100"/>
            <el-table-column prop="r" label="必须" width="80"/>
            <el-table-column prop="d" label="含义"/>
          </el-table>
        </section>

        <!-- RAG 知识库 -->
        <section v-else-if="activeDoc === 'api-rag'" class="doc-section">
          <h1>知识库上传 (RAG)</h1>
          <p>投喂本地文档以增强模型的知识。支持 PDF、DOCX 以及 TXT 格式。</p>
          <div class="api-card">
            <span class="method post">POST</span>
            <span class="url">/v1/proxy/knowledge/upload</span>
          </div>
          <p>请求格式：<code>multipart/form-data</code></p>
          <p>参数：<code>file</code> (File 对象)</p>
        </section>

        <!-- 数据投喂 -->
        <section v-else-if="activeDoc === 'api-inject'" class="doc-section">
          <h1>手动数据投喂</h1>
          <p>用于在不使用文档的情况下，直接注入特定的 Q&A 对到语义缓存向量库中。</p>
          <div class="api-card">
            <span class="method post">POST</span>
            <span class="url">/v1/proxy/data/inject</span>
          </div>
          <div class="code-block">
            <pre>[
  {"question": "公司几点下班？", "answer": "我们的标准下班时间是 18:00。"}
]</pre>
          </div>
        </section>

        <!-- 鉴权 -->
        <section v-else-if="activeDoc === 'auth'" class="doc-section">
          <h1>鉴权与凭证</h1>
          <p>所有代理相关的接口（带有 <code>/v1/proxy</code> 前缀）均需通过 API Key 进行鉴权。</p>
          <el-descriptions title="请求头配置" :column="1" border>
            <el-descriptions-item label="Header Name">Authorization</el-descriptions-item>
            <el-descriptions-item label="Value Format">Bearer sk-xxxxxxxxxxxxxx</el-descriptions-item>
          </el-descriptions>
        </section>

        <!-- 健康监测 -->
        <section v-else-if="activeDoc === 'monitoring'" class="doc-section">
          <h1>健康监测</h1>
          <p>用于外部容器（如 Docker, K8s）或自研监控系统检查网关状态。</p>
          <div class="api-card">
            <span class="method get">GET</span>
            <span class="url">/sys/health</span>
          </div>
          <p>返回 <code>200 OK</code> 表示系统运行正常。</p>
        </section>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { InfoFilled, Star, Connection, Lock, Odometer } from '@element-plus/icons-vue'

const router = useRouter()
const activeDoc = ref('intro')

const handleMenuSelect = (index) => {
  activeDoc.value = index
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.docs-container {
  height: 100vh;
  background-color: #fff;
}

.docs-sidebar {
  border-right: 1px solid #e6e6e6;
  background-color: #f8f9fa;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.sidebar-header h3 {
  margin: 10px 0 0 0;
  font-size: 16px;
  color: #303133;
}

.docs-menu {
  border-right: none;
  background-color: transparent;
}

.docs-content {
  padding: 40px;
  max-width: 1000px;
}

.content-wrapper {
  color: #2c3e50;
  line-height: 1.6;
}

.doc-section h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #1a1a1a;
  border-bottom: 2px solid #409EFF;
  display: inline-block;
  padding-bottom: 5px;
}

.doc-section h3 {
  font-size: 20px;
  margin-top: 30px;
  margin-bottom: 15px;
  color: #303133;
}

.feature-tags {
  margin: 15px 0 25px 0;
  display: flex;
  gap: 10px;
}

.api-card {
  background-color: #f1f3f5;
  padding: 12px 18px;
  border-radius: 6px;
  font-family: monospace;
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.method {
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.method.post { background-color: #49cc90; color: white; }
.method.get { background-color: #61affe; color: white; }

.url {
  color: #3b4151;
  font-weight: bold;
}

.code-block {
  background-color: #282c34;
  color: #abb2bf;
  padding: 18px;
  border-radius: 8px;
  font-family: 'Fira Code', 'Courier New', Courier, monospace;
  margin: 15px 0;
  overflow-x: auto;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.code-block pre {
  margin: 0;
}

.back-btn {
  margin-bottom: 10px;
}
</style>
