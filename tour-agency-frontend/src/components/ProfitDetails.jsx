import React from 'react';

// URL для універсальної статичної заглушки (менший розмір)
const STATIC_IMAGE_PLACEHOLDER = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=60";

// Компонент для відображення детального звіту про прибуток для обраного туру
const ProfitDetails = ({ tour, profitData, onBack }) => {
    // Якщо дані ще не завантажені
    if (!profitData) {
        return (
            <div className="text-center p-6 text-gray-500">
                Loading profit details...
            </div>
        );
    }

    // Ми очікуємо, що profitData.totalProfit буде числовим значенням.
    const totalProfit = parseFloat(profitData.totalProfit);
    const pricePerPerson = parseFloat(tour.price);

    // Додаємо імітацію даних для презентабельності
    const bookingsCount = Math.floor(totalProfit / pricePerPerson);
    const costs = totalProfit * 0.15; // Імітація 15% витрат
    const netProfit = totalProfit - costs;


    return (
        <div className="profit-details-report">
            {/* Кнопка повернення до списку */}
            <button onClick={onBack} className="back-to-list-btn">
                &larr; Back to Tour Selection
            </button>

            <h3 className="report-main-title">
                Profit Report: {tour.name}
            </h3>

            <p className="report-subtitle">Analysis for tour name: {tour.name}</p>

            {/* Сітка метрик */}
            <div className="profit-metrics-grid">

                {/* Метрика 1: Загальний Дохід (Фактичний) */}
                <div className="profit-metric metric-revenue">
                    <p className="metric-label">Total Revenue (Based on Bookings)</p>
                    <p className="metric-value">${totalProfit.toFixed(2)}</p>
                </div>

                {/* Метрика 2: Чистий Прибуток (Імітація) */}
                <div className="profit-metric metric-net-profit">
                    <p className="metric-label">Estimated Net Profit</p>
                    <p className="metric-value">${netProfit.toFixed(2)}</p>
                </div>

                {/* Метрика 3: Кількість Бронювань (Імітація) */}
                <div className="profit-metric metric-bookings">
                    <p className="metric-label">Booked Seats (Est.)</p>
                    <p className="metric-value">{bookingsCount}</p>
                </div>

                {/* Метрика 4: Витрати (Імітація) */}
                <div className="profit-metric metric-costs">
                    <p className="metric-label">Estimated Costs (15%)</p>
                    <p className="metric-value">${costs.toFixed(2)}</p>
                </div>
            </div>

            <p className="report-note">
                <span className="font-semibold">Tour Price:</span> ${pricePerPerson.toFixed(2)} per person.
                (Gross revenue calculation: Total Price * Booked Seats).
            </p>
        </div>
    );
};

export default ProfitDetails;