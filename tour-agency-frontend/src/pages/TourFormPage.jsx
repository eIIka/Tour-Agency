import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig.js';
import { AuthContext } from '../context/AuthContext.jsx';

const TourFormPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useContext(AuthContext);

    // Стан форми
    const [formData, setFormData] = useState({
        name: '',
        price: '',
        startDate: '',
        endDate: '',
        countryName: '',
        countryRegion: '',
        imageUrl: ''
    });

    const [loading, setLoading] = useState(false);
    const [submitError, setSubmitError] = useState('');
    const [initialLoadError, setInitialLoadError] = useState('');
    const [myGuideId, setMyGuideId] = useState(null);

    // Стан для повідомлення
    const [notification, setNotification] = useState(null);

    const isEditMode = !!id;
    const pageTitle = isEditMode ? 'Edit Tour' : 'Create New Tour';

    // Функція для показу повідомлення (залишаємо без змін)
    const showNotification = (message, type = 'success') => {
        setNotification({ message, type });
        setTimeout(() => {
            setNotification(null);
        }, 3000);
    };


    // 1. Ефект для завантаження Guide ID та даних туру
    useEffect(() => {
        if (user?.role !== 'ROLE_ADMIN' && user?.role !== 'ROLE_GUIDE') {
            navigate('/');
            return;
        }

        const fetchInitialData = async () => {
            setLoading(true);

            // Отримання Guide ID
            if (user?.role === 'ROLE_GUIDE') {
                try {
                    const meResponse = await api.get('/guide/me');
                    setMyGuideId(meResponse.data.id);
                } catch (e) {
                    setInitialLoadError("Cannot load Guide ID. Check guide profile status.");
                    setLoading(false);
                    return;
                }
            }

            // Завантаження даних для редагування
            if (isEditMode) {
                try {
                    const response = await api.get(`/tour/${id}`);
                    const data = response.data;

                    setFormData({
                        name: data.name || '',
                        description: data.description || '',
                        price: data.price || '',
                        startDate: data.startDate?.split('T')[0] || '',
                        endDate: data.endDate?.split('T')[0] || '',
                        countryName: data.countryName || '',
                        countryRegion: data.countryRegion || '',
                        imageUrl: data.imageUrl || ''
                    });
                } catch (err) {
                    setInitialLoadError('Failed to load tour data.');
                }
            }
            setLoading(false);
        };
        fetchInitialData();
    }, [id, isEditMode, navigate, user]);

    // Обробка змін
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleStartBooking = () => {
        if (!user || user.role !== 'ROLE_CLIENT') {
            showNotification("Please log in as a Client to book a tour.", "error");
            return;
        }
        // Перекидаємо на нову сторінку з ID туру
        navigate(`/book/${id}`);
    };

    // Обробка відправки форми
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setSubmitError('');

        // 2. Формуємо PAYLOAD для бекенду
        const tourPayload = {
            name: formData.name,
            price: parseFloat(formData.price),
            startDate: formData.startDate,
            endDate: formData.endDate,
            countryName: formData.countryName,
            countryRegion: formData.countryRegion,

            // Надсилаємо опис та URL завжди
            description: formData.description,
            imageUrl: formData.imageUrl,

            // При створенні туру Guide ID береться з поточного гіда
            ...(!isEditMode && user.role === 'ROLE_GUIDE' && { guideId: myGuideId }),
        };

        // Тимчасова перевірка на Guide ID
        if (!isEditMode && user.role === 'ROLE_GUIDE' && !myGuideId) {
            setSubmitError('Guide ID is missing. Cannot create tour.');
            setLoading(false);
            return;
        }

        try {
            if (isEditMode) {
                await api.put(`/tour/${id}`, tourPayload);
                showNotification('Tour updated successfully!', 'success');
            } else {
                await api.post('/tour', tourPayload);
                showNotification('Tour created successfully!', 'success');

                // Після успішного створення очищаємо форму
                setFormData({
                    name: '', price: '', startDate: '', endDate: '',
                    countryName: '', countryRegion: '', description: '', imageUrl: ''
                });
            }

        } catch (err) {
            console.error("Submission error:", err);
            const message = err.response?.data?.message || `Failed to ${isEditMode ? 'update' : 'create'} tour. Please check all fields.`;
            setSubmitError(message);
            showNotification(message, 'error');
        } finally {
            setLoading(false);
        }
    };

    if (initialLoadError) {
        return <div className="container" style={{padding: '40px', textAlign: 'center', color: 'red'}}>{initialLoadError}</div>;
    }

    if (loading) {
        return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Loading...</div>;
    }

    // Рендер форми
    return (
        <div className="container" style={{ paddingBottom: '60px' }}>
            {/* Компонент сповіщення */}
            {notification && (
                <div className={`notification-toast ${notification.type}`}>
                    {notification.message}
                </div>
            )}

            <button onClick={() => navigate(-1)} className="back-btn">
                ← Back
            </button>
            <h1 className="section-title" style={{marginTop: '20px', marginBottom: '30px'}}>{pageTitle}</h1>

            <form onSubmit={handleSubmit} className="tour-form-card">
                {/* 1. Загальні дані */}
                <div className="form-group">
                    <label htmlFor="name">Tour Name</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        placeholder="Enter the tour title"
                        required
                        disabled={loading}
                    />
                </div>

                {/* 2. Деталі туру */}
                <div className="form-grid">
                    <div className="form-group">
                        <label htmlFor="countryName">Country Name</label>
                        <input
                            type="text"
                            id="countryName"
                            name="countryName"
                            value={formData.countryName}
                            onChange={handleChange}
                            placeholder="e.g., Ukraine"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="countryRegion">Country Region</label>
                        <input
                            type="text"
                            id="countryRegion"
                            name="countryRegion"
                            value={formData.countryRegion}
                            onChange={handleChange}
                            placeholder="e.g., Europe"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="price">Price ($)</label>
                        <input
                            type="number"
                            id="price"
                            name="price"
                            value={formData.price}
                            onChange={handleChange}
                            placeholder="e.g., 1500.00"
                            step="0.01"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="startDate">Start Date</label>
                        <input
                            type="text"
                            id="startDate"
                            name="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                            placeholder="YYYY-MM-DD"
                            pattern="\d{4}-\d{2}-\d{2}"
                            title="Format must be YYYY-MM-DD"
                            required
                            disabled={loading}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="endDate">End Date</label>
                        <input
                            type="text"
                            id="endDate"
                            name="endDate"
                            value={formData.endDate}
                            onChange={handleChange}
                            placeholder="YYYY-MM-DD"
                            pattern="\d{4}-\d{2}-\d{2}"
                            title="Format must be YYYY-MM-DD"
                            required
                            disabled={loading}
                        />
                    </div>
                </div>

                {/* 3. Додаткові поля, які з'являються завжди */}
                <div className="form-group">
                    <label htmlFor="imageUrl">Image URL</label>
                    <input
                        type="url"
                        id="imageUrl"
                        name="imageUrl"
                        value={formData.imageUrl}
                        onChange={handleChange}
                        placeholder="https://example.com/image.jpg"
                    />
                </div>

                {submitError && (
                    <div className="error-message" style={{color: 'red', marginTop: '10px'}}>{submitError}</div>
                )}

                {/* Кнопка відправки */}
                <button
                    type="submit"
                    className="hero-button"
                    disabled={loading}
                    style={{marginTop: '30px', padding: '12px 30px', fontSize: '1.1rem'}}
                >
                    {loading ? 'Processing...' : (isEditMode ? 'Save Changes' : 'Create Tour')}
                </button>
            </form>
            {/* Кнопка бронювання */}
            {user?.role === 'ROLE_CLIENT' && (
                <div className="booking-section">
                    <button className="book-now-btn" onClick={handleStartBooking}>
                        Book This Tour Now
                    </button>
                </div>
            )}
        </div>
    );
};

export default TourFormPage;