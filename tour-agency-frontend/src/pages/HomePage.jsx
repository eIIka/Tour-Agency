import React, { useEffect, useState, useContext } from 'react';
import api from '../api/axiosConfig.js';
import TourCard from '../components/TourCard.jsx';
import { AuthContext } from '../context/AuthContext.jsx';
import { Link } from 'react-router-dom';
import ToursFilter from '../components/ToursFilter.jsx';

const TABS = {
    ALL: 'all_tours',
    POPULAR: 'popular',
    SEARCH: 'search'
};

const HomePage = () => {
    const { user } = useContext(AuthContext);

    // Стан для відображення
    const [tours, setTours] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState(TABS.ALL);

    // Стан для фільтрації
    const [isFilterVisible, setIsFilterVisible] = useState(false);
    const [filterResults, setFilterResults] = useState(null);
    const [searchError, setSearchError] = useState('');

    // Функція для завантаження загального списку/популярних
    const fetchTours = async (tab) => {
        setLoading(true);
        setError('');
        setFilterResults(null);

        let endpoint = '/tour';
        if (tab === TABS.POPULAR) {
            endpoint = '/tour/popular';
        }

        try {
            const response = await api.get(endpoint);
            setTours(response.data);
            if (response.data.length === 0) {
                setError(tab === TABS.POPULAR ? "No popular tours found." : "No tours found in the system.");
            }
        } catch (err) {
            setError("Failed to load tours.");
            setTours([]);
        } finally {
            setLoading(false);
        }
    };

    // Ініціалізація: завантажуємо всі тури
    useEffect(() => {
        // Завантажуємо тури лише для авторизованих користувачів
        if (user) {
            fetchTours(TABS.ALL);
        } else {
            setLoading(false); // Якщо не залогінений, просто знімаємо індикатор
        }
    }, [user]);

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        if (tab !== TABS.SEARCH) {
            fetchTours(tab);
        }
    };

    // Функція для обробки результатів фільтрації
    const handleFilterSubmit = (results, err) => {
        setFilterResults(results);
        setSearchError(err);
        setActiveTab(TABS.SEARCH);
        setIsFilterVisible(true);
    };

    const finalTourList = activeTab === TABS.SEARCH ? filterResults : tours;
    const currentError = activeTab === TABS.SEARCH ? searchError : error;

    return (
        <div className="home-page">
            <div className="hero-wrapper">
                <div className="container hero-content">
                    <div className="hero-text-box">
                        <div style={{ fontSize: '4rem', marginBottom: '10px' }}>🌴✈️</div>
                        <h1 className="hero-title">Explore the World</h1>
                        <p className="hero-subtitle">
                            Unforgettable journeys await you. Book your next adventure today.
                        </p>

                        {!user && (
                            <Link to="/login" className="hero-button" style={{marginTop: '20px'}}>
                                Start Your Journey
                            </Link>
                        )}
                    </div>
                </div>
            </div>

            {/* === КРИТИЧНА ЗМІНА: Відображаємо контент ТІЛЬКИ, якщо юзер залогінений === */}
            {user && (
                <div className="container" style={{ paddingBottom: '60px' }}>
                    <h2 className="section-title">Available Tours</h2>

                    {/* Навігація фільтрів */}
                    <div className="dashboard-tabs" style={{marginBottom: '30px'}}>
                        <button
                            className={`tab-btn ${activeTab === TABS.ALL ? 'active' : ''}`}
                            onClick={() => handleTabChange(TABS.ALL)}
                        >
                            All Tours
                        </button>
                        <button
                            className={`tab-btn ${activeTab === TABS.POPULAR ? 'active' : ''}`}
                            onClick={() => handleTabChange(TABS.POPULAR)}
                        >
                            5 Most Popular Tours
                        </button>
                        <button
                            onClick={() => setIsFilterVisible(!isFilterVisible)}
                            className={`tab-btn ${isFilterVisible ? 'active' : ''}`}
                            style={{marginLeft: 'auto'}}
                        >
                            {isFilterVisible ? 'Hide Search' : 'Show Search'}
                        </button>
                    </div>

                    {/* Головний контейнер з гнучким макетом (Фільтр + Сітка) */}
                    <div className="main-management-layout">
                        {/* 1. Блок Фільтрації */}
                        {isFilterVisible && (
                            <div className="filter-sidebar">
                                <ToursFilter
                                    onFilterComplete={handleFilterSubmit}
                                    currentTourList={tours}
                                />
                            </div>
                        )}

                        {/* 2. Блок Списку Турів */}
                        <div className="tour-list-area">
                            {loading && <div style={{textAlign: 'center', padding: '40px'}}>Loading...</div>}

                            {currentError && finalTourList.length === 0 && (
                                <div style={{textAlign: 'center', color: 'red', padding: '40px'}}>
                                    {currentError}
                                </div>
                            )}

                            {!loading && finalTourList.length > 0 && (
                                <div className="tours-grid">
                                    {finalTourList.map(tour => (
                                        <TourCard key={tour.id} tour={tour} />
                                    ))}
                                </div>
                            )}

                            {/* Якщо немає турів взагалі */}
                            {!loading && !currentError && finalTourList.length === 0 && (
                                <div style={{textAlign: 'center', padding: '40px', color: '#64748b'}}>
                                    No tours found. Try adjusting your search criteria.
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomePage;