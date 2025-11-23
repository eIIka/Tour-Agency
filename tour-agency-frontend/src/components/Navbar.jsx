import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext.jsx'; // Виправлено шлях
import '../App.css';

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="container navbar-content">
                <Link to="/" className="logo">
                    🌴 Tour Agency
                </Link>

                <div className="nav-links">
                    <Link to="/" className="nav-item">Home</Link>

                    {user ? (
                        <>
                            {/* Посилання на Dashboard (ГІД / АДМІН) */}
                            {(user.role === 'ROLE_GUIDE' || user.role === 'ROLE_ADMIN') && (
                                <Link to="/dashboard" className="nav-item" style={{fontWeight: 600}}>
                                    Dashboard
                                </Link>
                            )}

                            {/* Посилання на User Management (ТІЛЬКИ АДМІН) */}
                            {user.role === 'ROLE_ADMIN' && (
                                <Link to="/admin/users" className="nav-item" style={{fontWeight: 600}}>
                                    Users
                                </Link>
                            )}

                            {/* Посилання на бронювання (ТІЛЬКИ КЛІЄНТ) */}
                            {user.role === 'ROLE_CLIENT' && (
                                <Link to="/my-bookings" className="nav-item" style={{fontWeight: 600}}>
                                    My Bookings
                                </Link>
                            )}

                            {/* Іконка та Email поточного користувача */}
                            <Link to="/profile" className="nav-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <span style={{ fontSize: '1.2rem' }}>👤</span>
                                <span>{user.email}</span>
                            </Link>

                            {/* Кнопка Logout */}
                            <button onClick={handleLogout} className="nav-logout">
                                Logout
                            </button>
                        </>
                    ) : (
                        // --- Якщо користувач не залогінений ---
                        <>
                            <Link to="/login" className="nav-item">Login</Link>
                            <Link to="/register" className="nav-item nav-btn">Register</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;