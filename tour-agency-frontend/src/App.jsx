import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import HomePage from './pages/HomePage';
import TourDetailsPage from './pages/TourDetailsPage';
import DashboardPage from './pages/DashboardPage';
import TourFormPage from './pages/TourFormPage.jsx';
import MyBookingsPage from './pages/MyBookingsPage.jsx';
import BookingStepsPage from './pages/BookingStepsPage.jsx';
import UserManagementPage from './pages/UserManagementPage.jsx';
import './App.css';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Navbar />
                <main className="main-content">
                    <Routes>
                        <Route path="/" element={<HomePage />} /> {/* Використовуємо HomePage */}
                        <Route path="/login" element={
                            <div className="auth-container"><LoginPage /></div>
                        } />
                        <Route path="/register" element={
                            <div className="auth-container"><RegisterPage /></div>
                        } />
                        <Route path="/profile" element={<ProfilePage />} />
                        <Route path="/tour/:id" element={<TourDetailsPage />} />
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/tour/create" element={<TourFormPage />} />
                        <Route path="/tour/edit/:id" element={<TourFormPage />} />
                        <Route path="/my-bookings" element={<MyBookingsPage />} />
                        <Route path="/book/:tourId" element={<BookingStepsPage />} />
                        <Route path="/admin/users" element={<UserManagementPage />} />
                    </Routes>
                </main>
            </Router>
        </AuthProvider>
    );
}

export default App;