import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
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

                    {/* === НОВЕ: Посилання на Dashboard === */}
                    {(user?.role === 'ROLE_GUIDE' || user?.role === 'ROLE_ADMIN') && (
                        <Link to="/dashboard" className="nav-item" style={{fontWeight: 600}}>
                            Dashboard
                        </Link>
                    )}
                    {/* ================================== */}

                    {user ? (
                        // ... (якщо залогінений)
                        <>
                            {/* NEW: Посилання на бронювання для Клієнтів */}
                            {user.role === 'ROLE_CLIENT' && (
                                <Link to="/my-bookings" className="nav-item" style={{fontWeight: 600}}>
                                    My Bookings
                                </Link>
                            )}

                            <Link to="/profile" className="nav-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <span style={{ fontSize: '1.2rem' }}>👤</span>
                                <span>{user.email}</span>
                            </Link>

                            <button onClick={handleLogout} className="nav-logout">
                                Logout
                            </button>
                        </>
                    ) : (
                        // ... (якщо гість)
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