// src/api/axiosConfig.js
import axios from 'axios';

const API_BASE_URL = 'https://tour-agency-backend.onrender.com/v1';

const api = axios.create({
    baseURL: API_BASE_URL,
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