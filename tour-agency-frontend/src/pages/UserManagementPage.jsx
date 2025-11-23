import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx';
import api from '../api/axiosConfig.js';
import { useNavigate } from 'react-router-dom';

const ROLES = ['ALL', 'ROLE_ADMIN', 'ROLE_GUIDE', 'ROLE_CLIENT'];

const UserManagementPage = () => {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const [allUsers, setAllUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeRole, setActiveRole] = useState('ALL');
    const [notification, setNotification] = useState(null);

    // Функція для показу Toast сповіщення
    const showNotification = (message, type = 'success') => {
        setNotification({ message, type });
        setTimeout(() => {
            setNotification(null);
        }, 3000);
    };

    // Завантаження всіх користувачів
    useEffect(() => {
        if (user?.role !== 'ROLE_ADMIN') {
            navigate('/dashboard'); // Захист: тільки для адміна
            return;
        }

        const fetchUsers = async () => {
            try {
                const response = await api.get('/users');
                setAllUsers(response.data);
                setFilteredUsers(response.data);
            } catch (err) {
                setError('Failed to load user list. Access denied or API error.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, [user, navigate]);

    // Логіка фільтрації
    useEffect(() => {
        if (activeRole === 'ALL') {
            setFilteredUsers(allUsers);
        } else {
            setFilteredUsers(allUsers.filter(u => u.role === activeRole));
        }
    }, [activeRole, allUsers]);

    // Видалення користувача
    const handleDelete = async (userId, userEmail) => {
        if (userId === user.id) {
            showNotification('Error: You cannot delete your own account.', 'error');
            return;
        }

        if (!window.confirm(`Are you sure you want to delete user ${userEmail} (ID: ${userId})?`)) {
            return;
        }

        try {
            await api.delete(`/users/${userId}`);

            // Оновлення списку без перезавантаження
            setAllUsers(prev => prev.filter(u => u.id !== userId));
            showNotification(`User ${userEmail} deleted successfully.`, 'success');

        } catch (err) {
            console.error("Deletion failed:", err);
            // Відображаємо повідомлення від бекенду, включаючи "Admins cannot delete their own account"
            const msg = err.response?.data?.message || 'Deletion failed.';
            showNotification(msg, 'error');
        }
    };

    if (user?.role !== 'ROLE_ADMIN') return null;
    if (loading) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Loading users...</div>;

    return (
        <div className="container" style={{ paddingBottom: '60px' }}>
            {notification && (
                <div className={`notification-toast ${notification.type}`}>
                    {notification.message}
                </div>
            )}

            <h1 className="section-title" style={{marginTop: '40px', marginBottom: '20px'}}>User Management</h1>

            {error && <div style={{textAlign: 'center', color: 'red', padding: '20px'}}>{error}</div>}

            {/* Фільтрація по ролях */}
            <div className="dashboard-tabs" style={{marginBottom: '30px'}}>
                {ROLES.map(role => (
                    <button
                        key={role}
                        className={`tab-btn ${activeRole === role ? 'active' : ''}`}
                        onClick={() => setActiveRole(role)}
                    >
                        {role === 'ALL' ? `All Users (${allUsers.length})` : role.replace('ROLE_', '')}
                    </button>
                ))}
            </div>

            {/* Таблиця користувачів */}
            <div className="user-table-wrapper">
                <table className="user-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredUsers.length === 0 && (
                        <tr><td colSpan="4" style={{textAlign: 'center', color: '#64748b'}}>No users found matching filter.</td></tr>
                    )}
                    {filteredUsers.map(u => (
                        <tr key={u.id}>
                            <td>{u.id}</td>
                            <td>{u.email}</td>
                            <td className={`role-${u.role.toLowerCase().replace('role_', '')}`}>
                                {u.role.replace('ROLE_', '')}
                            </td>
                            <td>
                                {/* Не дозволяємо видаляти самого себе */}
                                {u.id !== user.id && (
                                    <button
                                        onClick={() => handleDelete(u.id, u.email)}
                                        className="action-btn delete"
                                    >
                                        Delete
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

        </div>
    );
};

export default UserManagementPage;