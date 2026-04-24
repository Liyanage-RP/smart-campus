import React, { useEffect, useState } from 'react';
import { bookingApi } from '../../api/bookingApi';

const AdminBookings = () => {
    // In a real application, you would fetch all bookings, perhaps paginated or filtered.
    // For this example, we assume there's an endpoint or we just filter locally.
    // Assuming we have an endpoint that fetches all bookings for admin, but for now
    // let's just show a simulated structure based on the existing API.
    // We will just use myBookings for demo, but normally it'd be getAllBookings()
    
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchAllBookings();
    }, []);

    const fetchAllBookings = async () => {
        try {
            setLoading(true);
            // Replace with actual endpoint to get all bookings
            // const data = await axios.get('http://localhost:8080/api/admin/bookings');
            // For now, this is just a placeholder mock to demonstrate the UI
            setBookings([]);
            setError('');
        } catch (err) {
            setError('Failed to load bookings.');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = async (bookingId, status) => {
        try {
            await bookingApi.updateBookingStatus(bookingId, status);
            // In a real scenario, refresh the list: fetchAllBookings();
            // Since we use mock data, let's just update local state
            setBookings(bookings.map(b => b.id === bookingId ? { ...b, status } : b));
        } catch (err) {
            setError(`Failed to ${status.toLowerCase()} booking.`);
        }
    };

    if (loading) return <div>Loading admin dashboard...</div>;

    return (
        <div className="admin-bookings-container">
            <h2>Admin: Manage Bookings</h2>
            {error && <div className="alert alert-error">{error}</div>}
            
            {bookings.length === 0 ? (
                <p>No bookings to manage. (Connect to a proper GET /all endpoint in backend)</p>
            ) : (
                <table className="bookings-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>User ID</th>
                            <th>Resource ID</th>
                            <th>Time</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {bookings.map(booking => (
                            <tr key={booking.id}>
                                <td>{booking.id}</td>
                                <td>{booking.userId}</td>
                                <td>{booking.resourceId}</td>
                                <td>
                                    {new Date(booking.startTime).toLocaleString()} - <br/>
                                    {new Date(booking.endTime).toLocaleString()}
                                </td>
                                <td>
                                    <span className={`status-badge status-${booking.status.toLowerCase()}`}>
                                        {booking.status}
                                    </span>
                                </td>
                                <td>
                                    {booking.status === 'PENDING' && (
                                        <div className="action-buttons">
                                            <button 
                                                onClick={() => handleUpdateStatus(booking.id, 'APPROVED')}
                                                className="btn btn-success btn-sm"
                                            >
                                                Approve
                                            </button>
                                            <button 
                                                onClick={() => handleUpdateStatus(booking.id, 'REJECTED')}
                                                className="btn btn-danger btn-sm"
                                            >
                                                Reject
                                            </button>
                                        </div>
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

export default AdminBookings;
