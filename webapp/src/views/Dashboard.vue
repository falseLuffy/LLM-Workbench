<template>
  <el-container class="dashboard-container">
    <!-- 左侧菜单栏 -->
    <el-aside width="240px" class="aside">
      <div class="logo-area">
        <h1>LLM Proxy</h1>
      </div>
      <el-menu
        :default-active="activeIndex"
        class="menu-vertical"
        background-color="#2b303b"
        text-color="#a3b1c6"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <el-menu-item index="1">
          <span>总览 Dashboard</span>
        </el-menu-item>
        <el-menu-item index="2">
          <span>API 密钥管理</span>
        </el-menu-item>
        <el-menu-item index="3">
          <span>令牌消耗统计</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主体内容 -->
    <el-container>
      <!-- 顶部 Header -->
      <el-header class="header">
        <div class="header-breadcrumb">
          智能体与大模型网关工作台 - 
          <span v-if="activeIndex === '1'">总览</span>
          <span v-else-if="activeIndex === '2'">API 密钥管理</span>
          <span v-else-if="activeIndex === '3'">令牌消耗统计</span>
        </div>
        <div class="header-user">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link user-dropdown-trigger">
              <el-avatar :size="32" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="user-avatar" />
              <span class="user-name">{{ userInfo.nickname || userInfo.username || '加载中...' }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="profileDialogVisible = true">个人资料</el-dropdown-item>
                <el-dropdown-item @click="passwordDialogVisible = true">修改密码</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主视图区 -->
      <el-main class="main-content">
        <!-- 面板 1: 总览 -->
        <div v-if="activeIndex === '1'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="hover" class="box-card">
                <template #header>
                  <div class="card-header">
                    <span>系统状态监控</span>
                  </div>
                </template>
                <el-descriptions column="1" border>
                  <el-descriptions-item label="网关状态">
                    <el-tag type="success">运行中 (Active)</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="当前认证机制">
                    <el-tag type="primary">JSON Web Token</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="额度与配额">
                    <el-tag type="info">未受限</el-tag>
                  </el-descriptions-item>
                </el-descriptions>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover" class="box-card">
                <template #header>
                  <div class="card-header">
                    <span>欢迎使用</span>
                  </div>
                </template>
                <p style="color: #606266; line-height: 1.6;">
                  欢迎访问 LLM Proxy 控制台。在这里您可以：<br/>
                  1. 在 <b>API 密钥管理</b> 页面生成和吊销提供给第三方应用拉起大模型代理的密钥。<br/>
                  2. 在 <b>令牌消耗统计</b> 页面查看由于缓存和节流策略为您节省的 Token 费用。<br/>
                  请在左侧菜单进行切换体验。
                </p>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 面板 2: 密钥管理 -->
        <div v-if="activeIndex === '2'">
          <el-row :gutter="20">
            <!-- 密钥生成卡片 -->
            <el-col :span="24" style="margin-bottom: 20px;">
              <el-card shadow="hover" class="box-card">
                <template #header>
                  <div class="card-header">
                    <span>快速生成 API Key</span>
                    <el-tag type="info" size="small">代理层凭证</el-tag>
                  </div>
                </template>
                <div class="text-info" style="margin-bottom: 20px;">
                  在第三方应用、脚手架或直接发起 HTTP 代理请求时，请携带以此生成的 SK Key 作为凭证。
                </div>
                
                <el-form :inline="true" @submit.prevent="generateKey">
                  <el-form-item>
                    <el-input v-model="keyAlias" placeholder="为您的密钥设置一个别名" style="width: 250px;"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" native-type="submit" :loading="loading">生成密钥</el-button>
                  </el-form-item>
                </el-form>

                <el-alert
                  v-if="newKey"
                  title="创建成功！请妥善保存此 Key，它将不会再次显示"
                  type="success"
                  :description="newKey"
                  show-icon
                  :closable="false"
                  style="margin-top: 15px;"
                >
                </el-alert>
              </el-card>
            </el-col>

            <!-- 密钥列表卡片 -->
            <el-col :span="24">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>我的 API 密钥列表</span>
                  </div>
                </template>
                <el-table :data="apiKeys" border style="width: 100%" v-loading="keysLoading">
                  <el-table-column prop="id" label="ID" width="80" />
                  <el-table-column prop="name" label="别名" width="180" />
                  <el-table-column prop="keyValue" label="密钥(遮蔽显示)" min-width="200">
                    <template #default="scope">
                      <span>{{ scope.row.keyValue.substring(0, 7) }}...{{ scope.row.keyValue.substring(scope.row.keyValue.length - 4) }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                        {{ scope.row.status === 1 ? '正常' : '已吊销' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120" fixed="right">
                    <template #default="scope">
                      <el-button 
                        size="small" 
                        type="danger" 
                        :disabled="scope.row.status !== 1"
                        @click="handleDeleteKey(scope.row.id)"
                      >吊销</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 面板 3: 消耗统计 -->
        <div v-if="activeIndex === '3'">
          <el-card shadow="hover" >
            <template #header>
              <div class="card-header">
                <span>全局 Token 节省统计报告</span>
                <el-button size="small" type="primary" @click="fetchStats">刷新数据</el-button>
              </div>
            </template>
            <el-descriptions :column="2" border v-loading="statsLoading">
              <el-descriptions-item v-for="(value, key) in statsData" :key="key" :label="key">
                <el-tag size="large" type="warning" v-if="typeof value === 'number'">{{ value }}</el-tag>
                <span v-else>{{ value }}</span>
              </el-descriptions-item>
              
              <!-- 如果没有任何数据展示默认内容 -->
              <el-descriptions-item label="提示" v-if="Object.keys(statsData).length === 0">
                暂无统计数据，请产生几次大模型代理调用后再查看。
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </div>

      </el-main>
    </el-container>
    
    <!-- 个人资料弹窗 -->
    <el-dialog v-model="profileDialogVisible" title="个人资料修改" width="400px">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="头像展示">
          <el-avatar :size="64" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="90px">
        <el-form-item label="旧密码">
          <el-input type="password" v-model="passwordForm.oldPassword" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="passwordForm.newPassword" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword">修改</el-button>
        </span>
      </template>
    </el-dialog>

  </el-container>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const activeIndex = ref('1')

// 用户信息
const userInfo = ref({})
const profileDialogVisible = ref(false)
const passwordDialogVisible = ref(false)

const profileForm = reactive({ nickname: '', email: '', avatar: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '' })

// Api key 管理
const keyAlias = ref('')
const newKey = ref('')
const loading = ref(false)
const apiKeys = ref([])
const keysLoading = ref(false)

// 统计
const statsData = ref({})
const statsLoading = ref(false)

onMounted(async () => {
  fetchUserInfo()
})

const handleMenuSelect = (index) => {
  activeIndex.value = index
  if (index === '2') fetchApiKeys()
  if (index === '3') fetchStats()
}

// ------ API Key 逻辑 ------
const fetchApiKeys = async () => {
  keysLoading.value = true
  try {
    const res = await request.get('/v1/apikey/list')
    if (res && res.data) {
      apiKeys.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取 API 密钥列表失败')
  } finally {
    keysLoading.value = false
  }
}

const generateKey = async () => {
  if (!keyAlias.value) {
    ElMessage.warning('请输入密钥别名以方便管理')
    return
  }
  
  loading.value = true
  newKey.value = ''
  
  try {
    const res = await request.post(`/v1/apikey/generate?name=${encodeURIComponent(keyAlias.value)}`)
    if (res && res.data) {
      newKey.value = res.data
    } else {
      newKey.value = res // 兼容之前直接返回字符串的情况
    }
    ElMessage.success('密钥生成成功')
    fetchApiKeys() // 刷新列表
  } catch (error) {
    ElMessage.error('生成失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const handleDeleteKey = (id) => {
  ElMessageBox.confirm('确定要吊销此密钥吗？吊销后无法恢复。', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post(`/v1/apikey/delete?id=${id}`)
      ElMessage.success('已吊销密钥')
      fetchApiKeys()
    } catch (e) {
      ElMessage.error('吊销失败')
    }
  }).catch(() => {})
}

// ------ 统计逻辑 ------
const fetchStats = async () => {
  statsLoading.value = true
  try {
    const res = await request.get('/v1/proxy/stats/overall')
    if (res && res.data) {
      statsData.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

// ------ 用户资料逻辑 ------
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/v1/user/info')
    if (res && res.data) {
      userInfo.value = res.data
      profileForm.nickname = res.data.nickname
      profileForm.email = res.data.email
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleUpdateProfile = async () => {
  try {
    await request.post('/v1/user/update', profileForm)
    ElMessage.success('个人资料更新成功')
    profileDialogVisible.value = false
    fetchUserInfo()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

const handleUpdatePassword = async () => {
  try {
    await request.post('/v1/user/password', passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    localStorage.removeItem('token')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '修改密码失败')
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    router.push('/login')
    ElMessage.success('已安全退出系统')
  }).catch(() => {})
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  width: 100vw;
}

.aside {
  background-color: #2b303b;
  display: flex;
  flex-direction: column;
}

.logo-area {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  border-bottom: 1px solid #1f232b;
}

.logo-area h1 {
  margin: 0;
  font-size: 18px;
  letter-spacing: 1px;
}

.menu-vertical {
  border-right: none;
}

.header {
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
  padding: 0 20px;
}

.header-breadcrumb {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.header-breadcrumb span {
  font-weight: 400;
  color: #606266;
}

.el-dropdown-link {
  cursor: pointer;
  color: #606266;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.user-dropdown-trigger {
  gap: 8px; /* 头像和名字之间的间距 */
}

.user-avatar {
  border: 1px solid #e4e7ed;
}

.user-name {
  font-size: 14px;
}  

.main-content {
  background-color: #f0f2f5;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.text-info {
  font-size: 13px;
  color: #909399;
}
</style>
