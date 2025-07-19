import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:5555', // 替换成后端地址
  timeout: 5000,
});

export default instance;
