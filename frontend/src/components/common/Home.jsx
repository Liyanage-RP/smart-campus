import React from 'react';
import logo from '../../assets/logo.png';

const Home = ({ onNavigate }) => {
    return (
        <div className="home-container">
            <div className="hero-section">
                <h1 className="hero-title">Welcome to Smart Campus</h1>
                <p className="hero-subtitle">Streamlining Facility Management & Incident Reporting</p>
            </div>

            <div className="feature-grid">
                <div className="feature-card" onClick={() => onNavigate('new-booking')}>
                    <div className="feature-icon">📅</div>
                    <h3>Facility Booking</h3>
                    <p>Reserve labs, lecture halls, and equipment instantly.</p>
                    <button className="btn btn-primary">Book Now</button>
                </div>

                <div className="feature-card" onClick={() => onNavigate('new-ticket')}>
                    <div className="feature-icon">🚨</div>
                    <h3>Incident Reporting</h3>
                    <p>Report maintenance issues or safety concerns directly.</p>
                    <button className="btn btn-danger">Report Issue</button>
                </div>

                <div className="feature-card" onClick={() => onNavigate('my-bookings')}>
                    <div className="feature-icon">📊</div>
                    <h3>Dashboard</h3>
                    <p>Track your active bookings and ticket status in real-time.</p>
                    <button className="btn btn-secondary">View Status</button>
                </div>
            </div>

            <div className="stats-section">
                <div className="stat-item">
                    <span className="stat-number">24/7</span>
                    <span className="stat-label">Support</span>
                </div>
                <div className="stat-item">
                    <span className="stat-number">100+</span>
                    <span className="stat-label">Resources</span>
                </div>
                <div className="stat-item">
                    <span className="stat-number">Fast</span>
                    <span className="stat-label">Resolution</span>
                </div>
            </div>
        </div>
    );
};

export default Home;
