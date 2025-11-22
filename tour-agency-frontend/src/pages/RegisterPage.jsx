import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx'; // Перевірте шлях
import { useNavigate } from 'react-router-dom';

const RegisterPage = () => {
    const { register } = useContext(AuthContext);
    const navigate = useNavigate();

    const [role, setRole] = useState('ROLE_CLIENT');
    const [formData, setFormData] = useState({
        email: '', password: '', name: '',
        phone: '', passportNumber: '', language: ''
    });

    // NEW: Стан для повідомлення
    const [notification, setNotification] = useState(null);

    // NEW: Функція для показу Toast сповіщення (логіка взята з інших компонентів)
    const showNotification = (message, type = 'success') => {
        setNotification({ message, type });
        setTimeout(() => {
            setNotification(null);
        }, 3000);
    };


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Формуємо DTO згідно з RegisterDTO на бекенді
            const payload = {
                email: formData.email,
                password: formData.password,
                name: formData.name,
                role: role,
                // Додаємо поля залежно від ролі
                ...(role === 'ROLE_CLIENT' && {
                    phone: formData.phone,
                    passportNumber: formData.passportNumber
                }),
                ...(role === 'ROLE_GUIDE' && {
                    language: formData.language
                })
            };

            await register(payload);

            // ВИПРАВЛЕНО: Замінюємо alert на Toast
            showNotification('Registration successful! Please login.', 'success');

            // Перенаправляємо після невеликої затримки, щоб юзер побачив сповіщення
            setTimeout(() => navigate('/login'), 1000);

        } catch (err) {
            console.error("Registration failed:", err);
            const msg = err.response?.data?.message || 'Registration failed.';
            showNotification(msg, 'error');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (
        <>
            {/* NEW: Компонент сповіщення */}
            {notification && (
                <div className={`notification-toast ${notification.type}`}>
                    {notification.message}
                </div>
            )}

            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Role:</label>
                    <select value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="ROLE_CLIENT">Client</option>
                        <option value="ROLE_GUIDE">Guide</option>
                    </select>
                </div>

                {/* Загальні поля */}
                <div className="form-group">
                    <input placeholder="Email" type="email" required
                           name="email" value={formData.email} onChange={handleChange} />
                </div>
                <div className="form-group">
                    <input placeholder="Password" type="password" required
                           name="password" value={formData.password} onChange={handleChange} />
                </div>
                <div className="form-group">
                    <input placeholder="Full Name" required
                           name="name" value={formData.name} onChange={handleChange} />
                </div>

                {/* Поля для Клієнта */}
                {role === 'ROLE_CLIENT' && (
                    <>
                        <div className="form-group">
                            <input placeholder="Phone" required
                                   name="phone" value={formData.phone} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <input placeholder="Passport Number" required
                                   name="passportNumber" value={formData.passportNumber} onChange={handleChange} />
                        </div>
                    </>
                )}

                {/* Поля для Гіда */}
                {role === 'ROLE_GUIDE' && (
                    <div className="form-group">
                        <input placeholder="Language (e.g. English)" required
                               name="language" value={formData.language} onChange={handleChange} />
                    </div>
                )}

                <button type="submit" className="hero-button" style={{marginTop: '10px'}}>Register</button>
            </form>
        </>
    );
};

export default RegisterPage;