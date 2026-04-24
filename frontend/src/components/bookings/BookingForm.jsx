import React, { useState } from 'react';
import { bookingApi } from '../../api/bookingApi';
import './bookings.css'; // Assume some basic CSS exists

const BookingForm = ({ userId = 1 }) => {
    const [formData, setFormData] = useState({
        resourceId: '',
        startTime: '',
        endTime: ''
    });
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        try {
            const bookingRequest = {
                userId,
                resourceId: parseInt(formData.resourceId),
                startTime: new Date(formData.startTime).toISOString(),
                endTime: new Date(formData.endTime).toISOString()
            };
            
            await bookingApi.createBooking(bookingRequest);
            setMessage('Booking created successfully! It is pending approval.');
            setFormData({ resourceId: '', startTime: '', endTime: '' });
        } catch (err) {
            if (err.response && err.response.status === 409) {
                setError('Conflict: The resource is already booked for this time.');
            } else if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError('An unexpected error occurred while creating the booking.');
            }
        }
    };

    return (
        <div className="booking-form-container">
            <h2>Create a Booking</h2>
            {message && <div className="alert alert-success">{message}</div>}
            {error && <div className="alert alert-error">{error}</div>}
            
            <form onSubmit={handleSubmit} className="booking-form">
                <div className="form-group">
                    <label>Resource ID:</label>
                    <input 
                        type="number" 
                        name="resourceId" 
                        value={formData.resourceId} 
                        onChange={handleChange} 
                        required 
                    />
                </div>
                <div className="form-group">
                    <label>Start Time:</label>
                    <input 
                        type="datetime-local" 
                        name="startTime" 
                        value={formData.startTime} 
                        onChange={handleChange} 
                        required 
                    />
                </div>
                <div className="form-group">
                    <label>End Time:</label>
                    <input 
                        type="datetime-local" 
                        name="endTime" 
                        value={formData.endTime} 
                        onChange={handleChange} 
                        required 
                    />
                </div>
                <button type="submit" className="btn btn-primary">Book Resource</button>
            </form>
        </div>
    );
};

export default BookingForm;
