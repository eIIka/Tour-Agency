import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx';
import api from '../api/axiosConfig.js';
import ProfitDetails from './ProfitDetails.jsx';

const TourProfitCard = ({ tours: tourList, error: tourError, loading: tourLoading }) => {
    const { user } = useContext(AuthContext);

    const [selectedTour, setSelectedTour] = useState(null);
    const [profitData, setProfitData] = useState(null);
    const [profitLoading, setProfitLoading] = useState(false);
    const [profitError, setProfitError] = useState('');

    // URL для універсальної статичної заглушки
    const STATIC_IMAGE_PLACEHOLDER = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=60";

    const fetchProfitDetails = async (tourId, tourPrice) => {
        setProfitLoading(true);
        setProfitError('');
        setProfitData(null);

        try {
            // РЕАЛЬНИЙ API ВИКЛИК: GET /v1/tour/profit/{id}
            const response = await api.get(`/tour/profit/${tourId}`);

            const totalProfit = response.data;

            setProfitData({
                totalProfit: totalProfit,
                tourPrice: tourPrice
            });

        } catch (e) {
            console.error("Failed to fetch profit data:", e);
            setProfitError(e.response?.data?.message || "Failed to calculate profit.");
        } finally {
            setProfitLoading(false);
        }
    };

    const handleTourSelect = (tour) => {
        setSelectedTour(tour);
        fetchProfitDetails(tour.id, tour.price);
    };

    const handleBackToList = () => {
        setSelectedTour(null);
        setProfitData(null);
    };


    // Обробка, коли список турів завантажується
    if (tourLoading) return <div className="text-center p-6 text-gray-500">Loading tours for analytics...</div>;
    if (tourError) return <div className="text-center p-6 text-red-500">{tourError}</div>;

    // Стан 1: Відображення детального звіту
    if (selectedTour) {
        return (
            <div className="dashboard-card profit-details-view">
                {profitLoading && (
                    <div className="text-center p-6 text-indigo-500">
                        Loading profit report...
                    </div>
                )}
                {profitError && (
                    <div className="text-center p-6 text-red-500">{profitError}</div>
                )}

                {/* Передаємо обробник onBack */}
                {profitData && <ProfitDetails tour={selectedTour} profitData={profitData} onBack={handleBackToList} />}
            </div>
        );
    }

    // Стан 2: Відображення списку турів
    return (
        <div className="tour-list-for-analytics dashboard-card">
            <h2 className="text-2xl font-bold text-gray-800 mb-4" style={{marginBottom: '20px'}}>Select Tour for Profit Analysis</h2>

            {tourList.length === 0 ? (
                <div className="p-4 bg-yellow-100 text-yellow-800 rounded-lg">
                    No tours found for analysis. {user.role === 'ROLE_GUIDE' ? 'You must create a tour first.' : 'The system has no tours.'}
                </div>
            ) : (
                // ВИКОРИСТОВУЄМО ГРІД ДЛЯ ВІДОБРАЖЕННЯ КАРТОК
                <div className="tours-grid-analytics">
                    {tourList.map((tour) => (
                        <div
                            key={tour.id}
                            className="tour-card profit-card"
                        >
                            <TourCardContent tour={tour} onSelect={handleTourSelect} staticImage={STATIC_IMAGE_PLACEHOLDER} />
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

// Допоміжний компонент для рендерингу вмісту картки Profit
const TourCardContent = ({ tour, onSelect, staticImage }) => {
    const displayImageUrl = tour.imageUrl || staticImage;

    return (
        <>
            <div className="tour-image-container">
                <img
                    src={displayImageUrl}
                    alt={tour.name}
                    className="tour-image"
                    onError={(e) => { e.target.onerror = null; e.target.src=staticImage }}
                />
            </div>

            <div className="tour-content">
                <div className="tour-location">
                    📍 {tour.countryName || 'Unknown Country'}
                </div>

                <h3 className="tour-title">{tour.name}</h3>

                <div className="tour-dates">
                    📅 {tour.startDate} — {tour.endDate}
                </div>

                <div className="tour-footer">
                    <span className="tour-price-small">
                         Price: ${tour.price}
                    </span>

                    {/* Кнопка дії */}
                    <button onClick={() => onSelect(tour)} className="tour-btn profit-btn">
                        View Profit
                    </button>
                </div>
            </div>
        </>
    );
};


export default TourProfitCard;