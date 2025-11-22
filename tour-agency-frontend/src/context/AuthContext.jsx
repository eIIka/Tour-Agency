// src/context/AuthContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { jwtDecode } from "jwt-decode"; // Виправлено імпорт

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Перевірка токена при завантаженні сторінки
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                // Перевірка терміну дії токена (exp в секундах)
                if (decoded.exp * 1000 < Date.now()) {
                    logout();
                } else {
                    // Опціонально: можна зробити запит /auth/current, щоб отримати свіжі дані
                    setUser({ email: decoded.sub, role: decoded.roles?.[0] || decoded.role });
                }
            } catch (e) {
                logout();
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            // Див. AuthController -> login повертає AuthDTO (user + token)
            const response = await api.post('/auth/login', { email, password });
            const { token, user } = response.data;

            localStorage.setItem('token', token);
            setUser(user); // UserDTO з бекенду
            return true;
        } catch (error) {
            console.error("Login failed", error);
            throw error;
        }
    };

    const register = async (registerData) => {
        try {
            // AuthController -> register повертає UserDTO
            const response = await api.post('/auth/register', registerData);
            return response.data;
        } catch (error) {
            console.error("Registration failed", error);
            throw error;
        }
    }

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
        // Опціонально: викликати ендпоінт /auth/logout, якщо потрібно інвалідувати сесію на бекенді
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};