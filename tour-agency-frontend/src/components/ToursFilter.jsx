import React, { useState } from 'react';
import api from '../api/axiosConfig.js';
import TourCard from './TourCard.jsx';
import { AuthContext } from '../context/AuthContext.jsx';


// ToursFilter тепер приймає функцію зворотного виклику
const ToursFilter = ({ onFilterComplete, currentTourList }) => {

    const [searchType, setSearchType] = useState('country');
    const [searchValue, setSearchValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchValue) return;

        setLoading(true);
        setError('');

        let endpoint = '';

        if (searchType === 'country') {
            // ВИПРАВЛЕНО: Надсилаємо назву країни
            // Бекенд тепер очікує: /v1/tour/country/{countryName}
            endpoint = `/tour/country/${searchValue}`;

        } else if (searchType === 'guide') {
            // ВИПРАВЛЕНО: Надсилаємо ім'я гіда
            // Бекенд тепер очікує: /v1/tour/guide/{guideName}
            endpoint = `/tour/guide/${searchValue}`;
        }

        try {
            const response = await api.get(endpoint);

            // Надсилаємо результат назад батьківському компоненту
            onFilterComplete(response.data, null);
        } catch (err) {
            const errMsg = err.response?.status === 404
                ? "No tours found matching your criteria."
                : "Error during filtering. Check your input or ensure the backend is available.";

            onFilterComplete([], errMsg); // Надсилаємо порожній список з помилкою
            setError(errMsg);
        } finally {
            setLoading(false);
        }
    };

    // Вміст бічної панелі
    return (
        <div className="dashboard-card">
            <h3>Search Tours</h3>
            <p>Search for tours by Country Name or Guide Name.</p>

            <form onSubmit={handleSearch} className="filter-form" style={{marginTop: '20px', flexDirection: 'column', alignItems: 'stretch'}}>
                <select value={searchType} onChange={(e) => setSearchType(e.target.value)} style={{padding: '10px'}}>
                    <option value="country">By Country Name</option>
                    <option value="guide">By Guide Name</option>
                </select>
                <input
                    type="text"
                    value={searchValue}
                    onChange={(e) => setSearchValue(e.target.value)}
                    placeholder={searchType === 'country' ? "Enter Country Name (e.g. Italy)" : "Enter Guide Name (e.g. Vlad)"}
                    required
                    style={{flexGrow: 1, padding: '10px'}}
                />
                <button type="submit" className="hero-button" disabled={loading} style={{padding: '10px 20px'}}>
                    Search
                </button>
            </form>

            {error && <div style={{color: 'red', marginTop: '15px'}}>{error}</div>}

            <div style={{marginTop: '20px', paddingTop: '15px', borderTop: '1px solid #f1f5f9'}}>
                <h4 style={{fontSize: '0.9rem', color: '#64748b'}}>Current Active List: {currentTourList.length} tours</h4>
            </div>
        </div>
    );
};

export default ToursFilter;