import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig.js';
import { AuthContext } from '../context/AuthContext.jsx';
import ConfirmModal from '../components/ConfirmModal.jsx';

const TourDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useContext(AuthContext);

    const [tour, setTour] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [canEdit, setCanEdit] = useState(false);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [notification, setNotification] = useState(null);

    // Функція для показу Toast сповіщення (залишаємо без змін)
    const showNotification = (message, type = 'success') => {
        setNotification({ message, type });
        setTimeout(() => {
            setNotification(null);
        }, 3000);
    };

    useEffect(() => {
        const loadData = async () => {
            try {
                // 1. Завантажуємо дані туру
                const tourResponse = await api.get(`/tour/${id}`);
                const tourData = tourResponse.data;
                setTour(tourData);

                // 2. Перевіряємо права на редагування
                if (user?.role === 'ROLE_ADMIN') {
                    setCanEdit(true);
                } else if (user?.role === 'ROLE_GUIDE') {
                    try {
                        const meResponse = await api.get('/guide/me');
                        const myGuideId = meResponse.data.id;

                        if (myGuideId === tourData.guideId) {
                            setCanEdit(true);
                        }
                    } catch (e) {
                        console.error("Failed to fetch guide profile for ownership check", e);
                    }
                }
            } catch (err) {
                console.error("Error loading data:", err);
                setError('Failed to load tour details.');
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [id, user]);

    // NEW: Функція для початку покрокового бронювання
    const handleStartBooking = () => {
        if (!user || user.role !== 'ROLE_CLIENT') {
            showNotification("Please log in as a Client to book a tour.", "error");
            return;
        }
        // Перекидаємо на сторінку покрокового бронювання
        navigate(`/book/${id}`);
    };

    // NEW: Функція для бронювання туру
    const handleBookNow = async () => {
        if (!user || user.role !== 'ROLE_CLIENT') {
            showNotification("Please log in as a Client to book a tour.", "error");
            return;
        }

        try {
            const bookingPayload = {
                tourId: parseInt(id)
            };

            await api.post('/booking', bookingPayload);

            showNotification(`Successfully booked tour: ${tour.name}!`, "success");

            setTimeout(() => navigate('/my-bookings'), 1000);

        } catch (err) {
            console.error("Booking error:", err);
            const msg = err.response?.data?.message || "Booking failed. The tour may be full or the dates are invalid.";
            showNotification(msg, "error");
        }
    };


    // Функція для підтвердження видалення
    const handleConfirmDelete = async () => {
        setIsModalOpen(false); // Закриваємо модальне вікно

        try {
            await api.delete(`/tour/${id}`);
            showNotification("Tour deleted successfully!", "success");

            // Навігація на Dashboard, оскільки це зона управління
            setTimeout(() => navigate('/dashboard'), 500);
        } catch (err) {
            console.error("Failed to delete tour", err);
            showNotification("Failed to delete tour.", "error");
        }
    };


    if (loading) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Loading...</div>;
    if (error) return <div className="container" style={{padding: '40px', textAlign: 'center', color: 'red'}}>{error}</div>;
    if (!tour) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Tour not found</div>;

    const IMAGE_FALLBACK_URL = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=60";
    const largeImageUrl = (tour.imageUrl && tour.imageUrl.trim() !== '') ? tour.imageUrl : IMAGE_FALLBACK_URL;


    return (
        <div className="container" style={{ paddingBottom: '60px' }}>

            {/* Компонент сповіщення */}
            {notification && (
                <div className={`notification-toast ${notification.type}`}>
                    {notification.message}
                </div>
            )}

            {/* Модальне вікно підтвердження */}
            {isModalOpen && (
                <ConfirmModal
                    message={`Are you sure you want to delete the tour: ${tour.name}?`}
                    onConfirm={handleConfirmDelete}
                    onCancel={() => {
                        setIsModalOpen(false);
                        showNotification("Deletion canceled.", "info");
                    }}
                />
            )}

            <button onClick={() => navigate(-1)} className="back-btn">
                ← Back to Tours
            </button>

            <div className="tour-details-card">
                <div className="tour-details-image-wrapper">
                    <img
                        src={largeImageUrl}
                        alt={tour.name}
                        className="tour-details-image"
                        onError={(e) => { e.target.onerror = null; e.target.src=IMAGE_FALLBACK_URL }}
                    />
                    <div className="tour-details-price">${tour.price}</div>
                </div>

                <div className="tour-details-content">
                    <div className="tour-header-row">
                        <h1 className="tour-details-title">{tour.name}</h1>

                        {canEdit && (
                            <div className="admin-actions">
                                <Link to={`/tour/edit/${tour.id}`} className="action-btn edit">Edit</Link>
                                <button onClick={() => setIsModalOpen(true)} className="action-btn delete">Delete</button>
                            </div>
                        )}
                    </div>

                    <div className="tour-meta-grid">
                        <div className="meta-item">
                            <span className="meta-label">Country</span>
                            <span className="meta-value">📍 {tour.countryName || 'Unknown'}</span>
                        </div>
                        <div className="meta-item">
                            <span className="meta-label">Region</span>
                            <span className="meta-value">📍 {tour.countryRegion || 'Unknown'}</span>
                        </div>
                        <div className="meta-item">
                            <span className="meta-label">Dates</span>
                            <span className="meta-value">📅 {tour.startDate} — {tour.endDate}</span>
                        </div>
                        <div className="meta-item">
                            <span className="meta-label">Guide</span>
                            <span className="meta-value">👤 {tour.guideName || 'N/A'}</span>
                        </div>
                    </div>

                    {tour.description && (
                        <div className="tour-description-section">
                            <h3>About this tour</h3>
                            <p style={{whiteSpace: 'pre-line'}}>
                                {tour.description}
                            </p>
                        </div>
                    )}

                    {/* Кнопка бронювання */}
                    {user?.role === 'ROLE_CLIENT' && (
                        <div className="booking-section">
                            <button className="book-now-btn" onClick={handleStartBooking}>
                                Book This Tour Now
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TourDetailsPage;