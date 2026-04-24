import React, { useEffect, useState } from 'react';
import { ticketApi } from '../../api/ticketApi';
import TicketDetail from './TicketDetail';

const MyTickets = ({ userId = 1 }) => {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedTicketId, setSelectedTicketId] = useState(null);

    useEffect(() => {
        fetchMyTickets();
    }, [userId]);

    const fetchMyTickets = async () => {
        try {
            setLoading(true);
            const data = await ticketApi.getMyTickets(userId);
            setTickets(data);
            setError('');
        } catch (err) {
            setError('Failed to load your tickets.');
        } finally {
            setLoading(false);
        }
    };

    const getPriorityClass = (priority) => {
        const map = { LOW: 'priority-low', MEDIUM: 'priority-medium', HIGH: 'priority-high', CRITICAL: 'priority-critical' };
        return map[priority] || '';
    };

    if (loading) return <div>Loading your tickets...</div>;
    if (selectedTicketId) return <TicketDetail ticketId={selectedTicketId} userId={userId} onBack={() => { setSelectedTicketId(null); fetchMyTickets(); }} />;

    return (
        <div className="my-tickets-container">
            <h2>My Incident Tickets</h2>
            {error && <div className="alert alert-error">{error}</div>}

            {tickets.length === 0 ? (
                <p>You have not submitted any tickets yet.</p>
            ) : (
                <table className="tickets-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Location</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {tickets.map(ticket => (
                            <tr key={ticket.id}>
                                <td>{ticket.id}</td>
                                <td>{ticket.resourceLocation}</td>
                                <td>{ticket.category}</td>
                                <td>
                                    <span className={`priority-badge ${getPriorityClass(ticket.priority)}`}>
                                        {ticket.priority}
                                    </span>
                                </td>
                                <td>
                                    <span className={`status-badge status-${ticket.status.toLowerCase()}`}>
                                        {ticket.status}
                                    </span>
                                    {ticket.status === 'REJECTED' && ticket.adminReason && (
                                        <div><small>Reason: {ticket.adminReason}</small></div>
                                    )}
                                </td>
                                <td>{new Date(ticket.createdAt).toLocaleDateString()}</td>
                                <td>
                                    <button
                                        onClick={() => setSelectedTicketId(ticket.id)}
                                        className="btn btn-secondary btn-sm"
                                    >
                                        View / Comment
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default MyTickets;
