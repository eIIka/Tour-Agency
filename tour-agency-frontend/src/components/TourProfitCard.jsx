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

    // Ефект, що викликає реальний API для отримання профіту
    const fetchProfitDetails = async (tourId, tourPrice) => {
        setProfitLoading(true);
        setProfitError('');
        setProfitData(null);

        try {
            // РЕАЛЬНИЙ API ВИКЛИК: GET /v1/tour/profit/{id}
            const response = await api.get(`/tour/profit/${tourId}`);

            // Ми отримуємо одне число (BigDecimal), загортаємо його в об'єкт для ProfitDetails
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
        // Передаємо ID туру та його ціну
        fetchProfitDetails(tour.id, tour.price);
    };

    // Якщо завантаження ще триває або є помилка зі списком турів
    if (tourLoading) return <div className="text-center p-6 text-gray-500">Loading tours for analytics...</div>;
    if (tourError) return <div className="text-center p-6 text-red-500">{tourError}</div>;

    // Стан 1: Відображення детального звіту
    if (selectedTour) {
        return (
            <div className="profit-details-view">
                {profitLoading && (
                    <div className="text-center p-6 text-indigo-500">
                        Loading profit report...
                    </div>
                )}
                {profitError && (
                    <div className="text-center p-6 text-red-500">{profitError}</div>
                )}

                {/* Відображаємо деталі лише якщо вони успішно завантажені */}
                {profitData && <ProfitDetails tour={selectedTour} profitData={profitData} />}
            </div>
        );
    }

    // Стан 2: Відображення списку турів
    return (
        <div className="tour-list-for-analytics">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Select Tour for Profit Analysis</h2>

            {tourList.length === 0 ? (
                <div className="p-4 bg-yellow-100 text-yellow-800 rounded-lg">
                    No tours found for analysis. {user.role === 'ROLE_GUIDE' ? 'You must create a tour first.' : 'The system has no tours.'}
                </div>
            ) : (
                <div className="space-y-3">
                    {tourList.map((tour) => (
                        <div
                            key={tour.id}
                            onClick={() => handleTourSelect(tour)}
                            className="flex justify-between items-center p-4 bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition duration-150 cursor-pointer"
                        >
                            <div className="flex flex-col">
                                <span className="font-semibold text-lg text-indigo-600">Tour name: "{tour.name}"</span>
                                <p></p>
                                <span className="text-sm text-gray-500">Price: ${tour.price}</span>
                            </div>
                            <button className="text-sm font-medium text-indigo-500 hover:text-indigo-700">
                                View Profit &rarr;
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default TourProfitCard;