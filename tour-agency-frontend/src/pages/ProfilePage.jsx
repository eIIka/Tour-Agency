import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../api/axiosConfig';
import { jwtDecode } from "jwt-decode";

const ProfilePage = () => {
    const { user, logout } = useContext(AuthContext);

    const [profileData, setProfileData] = useState(null);
    const [editData, setEditData] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);

    // === NEW: Стан для повідомлення ===
    const [notification, setNotification] = useState(null); // { message: '', type: 'success' | 'error' }

    // === NEW: Функція для показу повідомлення ===
    const showNotification = (message, type = 'success') => {
        setNotification({ message, type });
        // Приховуємо через 3 секунди
        setTimeout(() => {
            setNotification(null);
        }, 3000);
    };

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                let endpoint = '';
                if (user?.role === 'ROLE_CLIENT') endpoint = '/client/me';
                else if (user?.role === 'ROLE_GUIDE') endpoint = '/guide/me';
                else if (user?.role === 'ROLE_ADMIN') {
                    setLoading(false);
                    return;
                }

                if (endpoint) {
                    const response = await api.get(endpoint);
                    setProfileData(response.data);
                    setEditData(response.data);
                }
            } catch (err) {
                console.error("Error fetching profile:", err);
                // Використовуємо нову функцію замість console.error або alert, якщо треба
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchProfile();
    }, [user]);

    const handleChange = (e) => {
        setEditData({
            ...editData,
            [e.target.name]: e.target.value
        });
    };

    const handleSave = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) return;

            const decoded = jwtDecode(token);
            const userId = decoded.jti;

            let endpoint = '';
            if (user?.role === 'ROLE_CLIENT') endpoint = `/client/${userId}`;
            else if (user?.role === 'ROLE_GUIDE') endpoint = `/guide/${userId}`;

            await api.put(endpoint, editData);

            setProfileData(editData); // Оновлюємо UI локально
            setIsEditing(false);

            // === NEW: Викликаємо красиве повідомлення замість alert ===
            showNotification("Profile updated successfully!", "success");

        } catch (err) {
            console.error("Update failed", err);
            const msg = err.response?.data?.message || "Failed to update profile";
            // === NEW: Повідомлення про помилку ===
            showNotification(msg, "error");
        }
    };

    const handleCancel = () => {
        setEditData(profileData);
        setIsEditing(false);
    };

    const renderField = (label, name, value) => (
        <div className="profile-field">
            <label>{label}</label>
            {isEditing ? (
                <input
                    type="text"
                    name={name}
                    value={value || ''}
                    onChange={handleChange}
                    className="profile-input"
                />
            ) : (
                <div className="field-value">{value}</div>
            )}
        </div>
    );

    if (loading) return <div style={{textAlign: 'center', marginTop: '50px'}}>Loading...</div>;

    return (
        <div className="auth-container" style={{ maxWidth: '600px', position: 'relative' }}>
            {/* === NEW: Компонент сповіщення === */}
            {notification && (
                <div className={`notification-toast ${notification.type}`}>
                    {notification.message}
                </div>
            )}

            <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                <div style={{ fontSize: '4rem', background: '#f0f2f5', width: '100px', height: '100px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto' }}>
                    👤
                </div>
                <h2 className="auth-title" style={{marginTop: '15px'}}>My Profile</h2>
                <span style={{ background: '#2563eb', color: 'white', padding: '4px 12px', borderRadius: '20px', fontSize: '0.8rem', fontWeight: 'bold' }}>
                    {user?.role?.replace('ROLE_', '')}
                </span>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div className="profile-field">
                    <label>Email</label>
                    <div className="field-value" style={{color: '#777'}}>{user?.email}</div>
                </div>

                {user?.role === 'ROLE_CLIENT' && editData && (
                    <>
                        {renderField("Full Name", "name", editData.name)}
                        {renderField("Phone Number", "phone", editData.phone)}
                        {renderField("Passport Number", "passportNumber", editData.passportNumber)}
                    </>
                )}

                {user?.role === 'ROLE_GUIDE' && editData && (
                    <>
                        {renderField("Full Name", "name", editData.name)}
                        {renderField("Language", "language", editData.language)}
                    </>
                )}
            </div>

            <div style={{ display: 'flex', gap: '10px', marginTop: '30px' }}>
                {!isEditing ? (
                    <button onClick={() => setIsEditing(true)} className="hero-button" style={{ width: '100%', padding: '10px' }}>
                        Edit Profile
                    </button>
                ) : (
                    <>
                        <button onClick={handleSave} className="hero-button" style={{ width: '100%', padding: '10px', background: '#10b981' }}>
                            Save
                        </button>
                        <button onClick={handleCancel} className="hero-button" style={{ width: '100%', padding: '10px', background: '#6b7280' }}>
                            Cancel
                        </button>
                    </>
                )}
            </div>
        </div>
    );
};

export default ProfilePage;