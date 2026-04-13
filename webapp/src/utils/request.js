import axios from 'axios';

const service = axios.create({
  baseURL: '/',
  timeout: 10000,
});

// Request Interceptor
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      // 携带 JWT 或者 api_key
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 如果业务 code 是 401，也跳转登录
    if (res.code === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(new Error(res.msg || 'Error'));
    }
    return res;
  },
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // 未授权或禁止访问，跳转登录 并 清除 token
      localStorage.removeItem('token');
      // 如果不是已经在登录页，则进行跳转
      if (!window.location.pathname.endsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default service;
