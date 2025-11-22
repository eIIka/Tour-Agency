import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx';
import api from '../api/axiosConfig.js';
import TourCard from '../components/TourCard.jsx';
import { useNavigate } from 'react-router-dom';
// Видаляємо STATIC_IMAGE_PLACEHOLDER, оскільки він використовується лише всередині TourCard

const MyBookingsPage = () => {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const [tours, setTours] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user || user.role !== 'ROLE_CLIENT') {
            navigate('/');
            return;
        }

        const fetchBookings = async () => {
            setLoading(true);
            setError('');

            try {
                // Крок 1: Отримати ID поточного Клієнта
                const clientResponse = await api.get('/client/me');
                const clientId = clientResponse.data.id;

                if (!clientId) {
                    throw new Error("Client profile ID is null. Please complete your profile registration.");
                }

                // Крок 2: Використати Client ID для завантаження бронювань
                const bookingsResponse = await api.get(`/booking/client/${clientId}`);

                // BookingDTO має містити повний об'єкт Tour (TourDTO)
                const bookedTours = bookingsResponse.data.map(booking => booking.tour);

                setTours(bookedTours);

            } catch (err) {
                console.error("Booking load error:", err);

                let errMsg = "Failed to load bookings.";

                if (err.message?.includes("ID is null")) {
                    errMsg = "Error: Your client profile is incomplete. Cannot load bookings.";
                } else if (err.response) {
                    if (err.response.status === 404) {
                        errMsg = "You currently have no active bookings. Time to explore!";
                    } else {
                        errMsg = err.response.data?.message || `API error: ${err.response.status}`;
                    }
                }

                setError(errMsg);
                setTours([]);
            } finally {
                setLoading(false);
            }
        };

        fetchBookings();
    }, [user, navigate]);

    if (!user || user.role !== 'ROLE_CLIENT') return null;

    if (loading) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Loading bookings...</div>;

    // Визначаємо, чи потрібно показати повідомлення про відсутність бронювань/помилку
    const showEmptyMessage = (tours.length === 0 && error);

    return (
        <div className="container" style={{ paddingBottom: '60px' }}>
            <h1 className="section-title" style={{marginTop: '40px', marginBottom: '20px'}}>My Booked Tours</h1>

            {showEmptyMessage && (
                <div style={{textAlign: 'center', color: (error.includes("no active bookings") ? '#64748b' : 'red'), padding: '40px'}}>
                    {error}
                </div>
            )}

            {tours.length > 0 && (
                <div className="tours-grid">
                    {tours.map(tour => (
                        <TourCard key={tour.id} tour={tour} />
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyBookingsPage;