<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <div class="login-header">
        <h2>LLM Workbench</h2>
        <p>代理网关统一登录中心</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form 
            :model="loginForm" 
            :rules="loginRules" 
            ref="loginFormRef"
            @keyup.enter="handleLogin(loginFormRef)"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名" 
                size="large"
                clearable>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                show-password>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                class="login-btn" 
                size="large" 
                :loading="loading" 
                @click="handleLogin(loginFormRef)">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册账号" name="register">
          <el-form 
            :model="registerForm" 
            :rules="registerRules" 
            ref="registerFormRef"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名" 
                size="large"
                clearable>
              </el-input>
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input 
                v-model="registerForm.nickname" 
                placeholder="请输入昵称" 
                size="large"
                clearable>
              </el-input>
            </el-form-item>

            <el-form-item prop="email">
              <el-input 
                v-model="registerForm.email" 
                placeholder="请输入邮箱" 
                size="large"
                clearable>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                show-password>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="success" 
                class="login-btn" 
                size="large" 
                :loading="loading" 
                @click="handleRegister(registerFormRef)">
                注 册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', nickname: '', email: '' })

const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const registerRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
})

const handleLogin = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/v1/auth/login', {
          username: loginForm.username,
          password: loginForm.password
        })
        if (res && res.token) {
          localStorage.setItem('token', res.token)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('登录失败，未获取到鉴权信息')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '账号或密码错误')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/v1/auth/register', {
          username: registerForm.username,
          nickname: registerForm.nickname,
          email: registerForm.email,
          password: registerForm.password
        })
        ElMessage.success('注册成功，请登录！')
        activeTab.value = 'login'
        loginForm.username = registerForm.username
        loginForm.password = registerForm.password
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  /* 科技风深色渐变背景 */
  background: linear-gradient(135deg, #1f1c2c 0%, #928dab 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
  border: none;
  background-color: rgba(255, 255, 255, 0.95);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  margin: 0;
  color: #303133;
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}

.login-tabs {
  margin-top: -10px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}
</style>
