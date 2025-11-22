import React from 'react';
import { Link } from 'react-router-dom';
import '../App.css';

// URL для універсальної статичної заглушки (менший розмір)
const STATIC_IMAGE_PLACEHOLDER = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=60";

const TourCard = ({ tour }) => {
    // === ВИПРАВЛЕНО: Якщо tour.imageUrl є 'falsy' (null, undefined, або пустий рядок), використовуємо заглушку.
    const displayImageUrl = tour.imageUrl || STATIC_IMAGE_PLACEHOLDER;

    return (
        <div className="tour-card">
            <div className="tour-image-container">
                <img
                    src={displayImageUrl} // Використовуємо коректну змінну
                    alt={tour.name}
                    className="tour-image"
                    // Якщо зображення не завантажиться, використовуємо запасну заглушку
                    onError={(e) => { e.target.onerror = null; e.target.src=STATIC_IMAGE_PLACEHOLDER }}
                />
                <span className="tour-price-badge">${tour.price}</span>
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
                    <span className="tour-guide">
                        👤 Guide: {tour.guideName || 'N/A'}
                    </span>
                    <Link to={`/tour/${tour.id}`} className="tour-btn">
                        Details
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default TourCard;