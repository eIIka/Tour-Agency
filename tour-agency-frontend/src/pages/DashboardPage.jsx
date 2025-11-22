import React, { useEffect, useState, useContext } from 'react';
import api from '../api/axiosConfig.js';
import { AuthContext } from '../context/AuthContext.jsx';
import TourCard from '../components/TourCard.jsx';
import { Link, useNavigate } from 'react-router-dom';
import TourProfitCard from '../components/TourProfitCard.jsx';

const TABS = {
    MAIN: 'main_view',
    PROFIT: 'profit_analytics'
};

const DashboardPage = () => {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const [tours, setTours] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [pageTitle, setPageTitle] = useState("Dashboard");
    const [activeTab, setActiveTab] = useState(TABS.MAIN);

    // Функція для завантаження турів для MAIN вкладки (і для аналітики)
    const fetchMainTours = async (role) => {
        setLoading(true);
        setError('');

        let endpoint = '/tour';
        let currentGuideId = null;
        let title = "Dashboard";

        if (role === 'ROLE_CLIENT') {
            try {
                // КЛІЄНТ: Отримуємо заброньовані тури
                const clientResponse = await api.get('/booking/client/me');
                const bookedTours = clientResponse.data.map(booking => booking.tour);
                setTours(bookedTours);
                title = "My Booked Tours";
            } catch (e) {
                // Обробка 404, якщо бронювань немає
                setError("You have no booked tours.");
                setTours([]);
            }
        } else if (role === 'ROLE_GUIDE') {
            try {
                // ГІД: Отримуємо свій Guide ID
                const meResponse = await api.get('/guide/me');
                currentGuideId = meResponse.data.id;

                // Використовуємо новий API для пошуку турів за Guide ID
                endpoint = `/tour/guide/id/${currentGuideId}`;
                title = "My Created Tours";

                const tourResponse = await api.get(endpoint);
                setTours(tourResponse.data);
            } catch (e) {
                // Обробка 404: No tours found
                if (e.response && e.response.status === 404) {
                    setError("You have not created any tours yet.");
                    setTours([]);
                } else {
                    // Будь-яка інша помилка
                    setError("Failed to load your tours.");
                    setTours([]);
                }
            }
        } else if (role === 'ROLE_ADMIN') {
            // АДМІН: Отримуємо всі тури
            title = "All System Tours";
            try {
                const tourResponse = await api.get(endpoint);
                setTours(tourResponse.data);
            } catch (e) {
                setError("No tours found in the system.");
                setTours([]);
            }
        }
        setPageTitle(title);
        setLoading(false);
    };

    // Ініціалізація та зміна вкладок
    useEffect(() => {
        if (user) {
            fetchMainTours(user.role);
        } else {
            navigate('/');
        }
    }, [user, navigate]);

    const handleTabChange = (tab) => {
        setActiveTab(tab);

        if (tab === TABS.MAIN) {
            // Перезавантажуємо, щоб скинути стан ProfitDetails
            fetchMainTours(user.role);
        }
    };

    if (!user || (user.role !== 'ROLE_GUIDE' && user.role !== 'ROLE_ADMIN' && user.role !== 'ROLE_CLIENT')) {
        return <div className="container" style={{padding: '40px', textAlign: 'center', color: '#ef4444'}}>Access Denied.</div>;
    }

    // Компонент для відображення списку турів
    const TourList = () => {
        if (loading) return <div style={{textAlign: 'center', padding: '40px'}}>Loading...</div>;
        if (error && tours.length === 0) return <div style={{textAlign: 'center', color: 'red', padding: '40px'}}>{error}</div>;

        return (
            <div className="tours-grid">
                {tours.map(tour => (
                    <TourCard key={tour.id} tour={tour} />
                ))}
            </div>
        )
    }

    const showProfitTab = user.role !== 'ROLE_CLIENT';
    const showCreateButton = user.role !== 'ROLE_CLIENT';

    return (
        <div className="container" style={{ paddingBottom: '60px' }}>
            <h1 className="section-title" style={{marginTop: '40px', marginBottom: '20px'}}>{pageTitle}</h1>

            {/* Навігація по вкладках */}
            <div className="dashboard-tabs">
                <button
                    className={`tab-btn ${activeTab === TABS.MAIN ? 'active' : ''}`}
                    onClick={() => handleTabChange(TABS.MAIN)}
                >
                    {user.role === 'ROLE_CLIENT' ? 'My Bookings' : (user.role === 'ROLE_ADMIN' ? 'All Tours' : 'My Tours')}
                </button>

                {/* Profit Analytics - Тільки для Адмінів/Гідів */}
                {showProfitTab && (
                    <button
                        className={`tab-btn ${activeTab === TABS.PROFIT ? 'active' : ''}`}
                        onClick={() => handleTabChange(TABS.PROFIT)}
                    >
                        Profit Analytics
                    </button>
                )}

                {/* Кнопка створення туру - Тільки для Адмінів/Гідів */}
                {showCreateButton && (
                    <Link to="/tour/create" className="hero-button create-btn">
                        + Create New Tour
                    </Link>
                )}
            </div>

            {/* Головний контейнер контенту */}
            <div className="dashboard-content" style={{marginTop: '30px'}}>
                {activeTab === TABS.MAIN && <TourList />}

                {/* Передаємо список турів і стани завантаження/помилки */}
                {activeTab === TABS.PROFIT && showProfitTab && (
                    <TourProfitCard
                        tours={tours}
                        error={error}
                        loading={loading}
                    />
                )}
            </div>
        </div>
    );
};

export default DashboardPage;