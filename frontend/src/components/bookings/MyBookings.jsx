import React, { useEffect, useState } from 'react';
import { bookingApi } from '../../api/bookingApi';

const MyBookings = ({ userId = 1 }) => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchMyBookings();
    }, [userId]);

    const fetchMyBookings = async () => {
        try {
            setLoading(true);
            const data = await bookingApi.getMyBookings(userId);
            setBookings(data);
            setError('');
        } catch (err) {
            setError('Failed to load your bookings.');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (bookingId) => {
        if (!window.confirm('Are you sure you want to cancel this booking?')) return;
        
        try {
            await bookingApi.updateBookingStatus(bookingId, 'CANCELLED');
            fetchMyBookings(); // Refresh the list
        } catch (err) {
            setError('Failed to cancel booking.');
        }
    };

    if (loading) return <div>Loading bookings...</div>;

    return (
        <div className="my-bookings-container">
            <h2>My Bookings</h2>
            {error && <div className="alert alert-error">{error}</div>}
            
            {bookings.length === 0 ? (
                <p>You have no bookings yet.</p>
            ) : (
                <table className="bookings-table">
                    <thead>
                        <tr>
                            <th>Resource ID</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {bookings.map(booking => (
                            <tr key={booking.id}>
                                <td>{booking.resourceId}</td>
                                <td>{new Date(booking.startTime).toLocaleString()}</td>
                                <td>{new Date(booking.endTime).toLocaleString()}</td>
                                <td>
                                    <span className={`status-badge status-${booking.status.toLowerCase()}`}>
                                        {booking.status}
                                    </span>
                                </td>
                                <td>
                                    {(booking.status === 'PENDING' || booking.status === 'APPROVED') && (
                                        <button 
                                            onClick={() => handleCancel(booking.id)}
                                            className="btn btn-danger btn-sm"
                                        >
                                            Cancel
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default MyBookings;
