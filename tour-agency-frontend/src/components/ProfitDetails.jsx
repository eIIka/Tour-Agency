import React from 'react';

// Компонент для відображення детального звіту про прибуток для обраного туру
const ProfitDetails = ({ tour, profitData }) => {
    // Якщо дані ще не завантажені
    if (!profitData) {
        return (
            <div className="text-center p-6 text-gray-500">
                Loading profit details...
            </div>
        );
    }

    // Ми очікуємо, що profitData.totalProfit буде числовим значенням,
    // оскільки бекенд повертає BigDecimal.
    const totalProfit = parseFloat(profitData.totalProfit);
    const pricePerPerson = parseFloat(tour.price);

    return (
        <div className="p-6 bg-white rounded-lg shadow-lg">
            <h3 className="text-2xl font-bold text-gray-800 border-b pb-2 mb-4">
                Profit Report: {tour.name}
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

                {/* Метрика 1: Загальний Прибуток (відповідь від бекенду) */}
                <div className="bg-green-50 p-4 rounded-lg border border-green-200 col-span-full">
                    <p className="text-sm font-medium text-green-600">Total Profit (Revenue based on bookings)</p>
                    <p className="text-5xl font-extrabold text-green-700 mt-1">
                        ${totalProfit.toFixed(2)}
                    </p>
                </div>

                {/* Додаткова інформація */}
                <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <p className="text-sm font-medium text-gray-500">Tour Price per person</p>
                    <p className="text-xl font-bold text-gray-900 mt-1">${pricePerPerson.toFixed(2)}</p>
                </div>
            </div>

            <p className="mt-4 text-sm text-gray-600">
                * This value represents the total revenue before calculating actual costs.
            </p>

            <button
                onClick={() => window.location.reload()} // Оновлення сторінки, щоб повернутися до списку
                className="mt-6 px-4 py-2 bg-indigo-500 text-white font-medium rounded-lg hover:bg-indigo-600 transition duration-150"
            >
                &larr; Back to Tour List
            </button>
        </div>
    );
};

export default ProfitDetails;