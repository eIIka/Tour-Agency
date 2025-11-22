// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/v1', // Ваш префікс з AuthController
    headers: {
        'Content-Type': 'application/json',
    },
});

// Інтерсептор для додавання токена до кожного запиту
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;