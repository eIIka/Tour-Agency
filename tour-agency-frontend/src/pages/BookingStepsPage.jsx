import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig.js';
import { AuthContext } from '../context/AuthContext.jsx';
import { jwtDecode } from "jwt-decode";

const BookingStepsPage = () => {
    const { tourId } = useParams();
    const navigate = useNavigate();
    const { user } = useContext(AuthContext);

    const [step, setStep] = useState(1);
    const [tour, setTour] = useState(null);
    const [clientData, setClientData] = useState(null);

    // Стан для зберігання даних, які клієнт підтверджує/змінює
    const [confirmingData, setConfirmingData] = useState({
        name: '',
        passportNumber: '',
        phone: ''
    });

    // === NEW FIX: Стан для зберігання даних оплати ===
    const [paymentData, setPaymentData] = useState({
        cardNumber: '',
        cardHolderName: '',
        expiryDate: '',
        cvv: ''
    });

    const [loading, setLoading] = useState(true);
    const [bookingProcessing, setBookingProcessing] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user || user.role !== 'ROLE_CLIENT') {
            navigate('/login');
            return;
        }

        // Завантаження даних туру та профілю клієнта
        const loadInitialData = async () => {
            try {
                const tourResponse = await api.get(`/tour/${tourId}`);
                setTour(tourResponse.data);

                const clientResponse = await api.get('/client/me');
                const loadedClientData = clientResponse.data;
                setClientData(loadedClientData);

                // Заповнюємо форму даними з профілю
                setConfirmingData({
                    name: loadedClientData.name || '',
                    passportNumber: loadedClientData.passportNumber || '',
                    phone: loadedClientData.phone || '',
                });

            } catch (err) {
                setError("Failed to load tour or client profile.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        loadInitialData();
    }, [tourId, user, navigate]);

    // Обробка змін у формі підтвердження
    const handleChange = (e) => {
        setConfirmingData({
            ...confirmingData,
            [e.target.name]: e.target.value
        });
    };

    // === NEW FIX: Обробка змін у формі оплати ===
    const handlePaymentChange = (e) => {
        const { name, value } = e.target;
        let formattedValue = value;

        if (name === 'cardNumber') {
            const digits = value.replace(/\D/g, '');
            formattedValue = digits.slice(0, 16).match(/.{1,4}/g)?.join(' ') || '';
        } else if (name === 'expiryDate') {
            const digits = value.replace(/\D/g, '');

            if (digits.length > 2) {
                formattedValue = `${digits.slice(0, 2)}/${digits.slice(2, 4)}`;
            } else {
                formattedValue = digits;
            }
        } else if (name === 'cvv') {
            formattedValue = value.replace(/\D/g, '').slice(0, 3);
        }

        setPaymentData(prev => ({
            ...prev,
            [name]: formattedValue
        }));
    };


    // Step 1: Підтвердження даних та перехід до оплати
    const handleConfirmDetails = () => {
        if (!confirmingData.passportNumber || !confirmingData.phone || !confirmingData.name) {
            setError("Name, Passport, and Phone number are required.");
            return;
        }
        setError('');
        setStep(2);
    };

    // Step 2: Імітація оплати та фіналізація бронювання
    const handlePayment = async () => {
        // Локальна валідація, що всі поля заповнені
        if (paymentData.cardNumber.length < 19 || paymentData.expiryDate.length < 5 || paymentData.cvv.length < 3 || !paymentData.cardHolderName) {
            setError("Please fill in all card details correctly.");
            return;
        }

        setBookingProcessing(true);
        setError('');

        try {
            await new Promise(resolve => setTimeout(resolve, 1500));

            const bookingPayload = {
                tourId: parseInt(tourId)
            };

            await api.post('/booking', bookingPayload);

            setStep(3);

        } catch (err) {
            console.error("Payment/Booking error:", err);
            const msg = err.response?.data?.message || "Payment failed. Please try again.";
            setError(msg);
            setBookingProcessing(false);
        }
    };

    if (loading) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Loading data...</div>;
    if (error && step < 3) return <div className="container" style={{padding: '40px', textAlign: 'center', color: 'red'}}>Error: {error}</div>;
    if (!tour || !clientData) return <div className="container" style={{padding: '40px', textAlign: 'center'}}>Required data missing.</div>;

    // Стан 3: Успіх (квитанція)
    if (step === 3) {
        const confirmationDate = new Date().toLocaleDateString('uk-UA', { year: 'numeric', month: 'long', day: 'numeric' });

        return (
            <div className="container booking-main-card">
                <div className="ticket-header">
                    <h1 className="ticket-title">Booking Confirmed!</h1>
                    <p className="ticket-date">Confirmation Date: {confirmationDate}</p>
                </div>

                <div className="ticket-body">
                    <h2 className="section-title-small">E-Ticket Voucher</h2>

                    <div className="ticket-section-tour">
                        <p className="ticket-label">Destination</p>
                        <p className="ticket-value-large">✈️ {tour.countryName} - {tour.name}</p>

                        <div className="ticket-info-grid">
                            <div>
                                <p className="ticket-label">Departure</p>
                                <p className="ticket-value">{tour.startDate}</p>
                            </div>
                            <div>
                                <p className="ticket-label">Return</p>
                                <p className="ticket-value">{tour.endDate}</p>
                            </div>
                            <div>
                                <p className="ticket-label">Guide</p>
                                <p className="ticket-value">{tour.guideName}</p>
                            </div>
                        </div>
                    </div>

                    <div className="ticket-section-passenger">
                        <h3 className="section-title-small">Passenger Details</h3>
                        <div className="ticket-info-grid">
                            <div>
                                <p className="ticket-label">Passenger Name</p>
                                <p className="ticket-value">{confirmingData.name}</p>
                            </div>
                            <div>
                                <p className="ticket-label">Passport Number</p>
                                <p className="ticket-value">{confirmingData.passportNumber}</p>
                            </div>
                            <div>
                                <p className="ticket-label">Total Price Paid</p>
                                <p className="ticket-value-price">${tour.price}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="ticket-footer">
                    <p className="footer-note">Thank you for choosing Tour Agency.</p>
                    <button onClick={() => navigate('/my-bookings')} className="hero-button" style={{marginTop: '20px', background: '#10b981'}}>
                        View My Bookings
                    </button>
                </div>
            </div>
        );
    }

    // Відображення
    return (
        <div className="container booking-main-card">
            <h1 className="section-title">Booking Tour: {tour.name}</h1>
            {/* NEW: Виділяємо індикатор кроку */}
            <p className="subtitle highlight">Step {step} of 2</p>

            <div className="step-indicator">
                <div className={`step ${step >= 1 ? 'active' : ''}`}>1. Details</div>
                <div className={`step ${step >= 2 ? 'active' : ''}`}>2. Payment</div>
            </div>

            {error && <div className="error-box" style={{color: 'red', marginBottom: '20px'}}>{error}</div>}

            {/* Step 1: Деталі Клієнта */}
            {step === 1 && (
                <div className="step-content step-1-content">
                    <h3 style={{marginBottom: '20px', marginLeft:'20px', marginRight:'20px'}}>1. Confirm Passenger Details</h3>
                    <div className="client-details-grid">

                        {/* Поля для підтвердження */}
                        <div className="form-group">
                            <label>Name</label>
                            <input type="text" name="name" value={confirmingData.name} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label>Email</label>
                            <input type="email" name="email" value={clientData.email} disabled />
                        </div>
                        <div className="form-group">
                            <label>Passport Number</label>
                            <input type="text" name="passportNumber" value={confirmingData.passportNumber} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label>Phone</label>
                            <input type="text" name="phone" value={confirmingData.phone} onChange={handleChange} required />
                        </div>

                    </div>

                    <div className="booking-summary">
                        <h3>Summary</h3>
                        <p>Tour: {tour.name}</p>
                        <p className="price">Total Price: <span>${tour.price}</span></p>
                    </div>

                    <button onClick={handleConfirmDetails} className="hero-button proceed-btn">
                        Proceed to Payment &rarr;
                    </button>
                </div>
            )}

            {/* Step 2: Оплата */}
            {step === 2 && (
                <div className="step-content payment-step" style={{marginLeft:'20px', marginRight:'20px'}}>
                    <h3 style={{marginBottom: '20px'}}>2. Complete Payment</h3>
                    <p className="payment-note">Payment simulation for ${tour.price} will finalize your booking.</p>

                    <div className="payment-fields">
                        <div className="form-group">
                            <label>Card Number</label>
                            <input
                                type="text"
                                name="cardNumber"
                                value={paymentData.cardNumber}
                                onChange={handlePaymentChange}
                                placeholder="XXXX XXXX XXXX XXXX"
                                maxLength="19"
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Card Holder Name</label>
                            <input
                                type="text"
                                name="cardHolderName"
                                value={paymentData.cardHolderName}
                                onChange={handlePaymentChange}
                                placeholder="Anna Rossi"
                                required
                            />
                        </div>
                        <div className="form-grid" style={{gridTemplateColumns: '1fr 1fr'}}>
                            <div className="form-group">
                                <label>Expiry Date</label>
                                <input
                                    type="text"
                                    name="expiryDate"
                                    value={paymentData.expiryDate}
                                    onChange={handlePaymentChange}
                                    placeholder="MM/YY"
                                    maxLength="5"
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>CVV</label>
                                <input
                                    type="text"
                                    name="cvv"
                                    value={paymentData.cvv}
                                    onChange={handlePaymentChange}
                                    placeholder="XXX"
                                    maxLength="3"
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <button onClick={handlePayment} className="book-now-btn" disabled={bookingProcessing}>
                        {bookingProcessing ? 'Processing Payment...' : `Pay $${tour.price} & Confirm Booking`}
                    </button>
                </div>
            )}
        </div>
    );
};

export default BookingStepsPage;