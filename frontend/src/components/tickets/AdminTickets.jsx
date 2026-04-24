import React, { useEffect, useState } from 'react';
import { ticketApi } from '../../api/ticketApi';
import TicketDetail from './TicketDetail';

const STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'REJECTED'];

const AdminTickets = ({ adminId = 1 }) => {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedTicketId, setSelectedTicketId] = useState(null);
    const [filterStatus, setFilterStatus] = useState('');

    useEffect(() => {
        fetchAllTickets();
    }, []);

    const fetchAllTickets = async () => {
        try {
            setLoading(true);
            const data = await ticketApi.getAllTickets();
            setTickets(data);
            setError('');
        } catch (err) {
            setError('Failed to load tickets.');
        } finally {
            setLoading(false);
        }
    };

    const handleAssign = async (ticketId) => {
        const techId = window.prompt('Enter Technician ID to assign:');
        if (!techId) return;
        try {
            await ticketApi.assignTechnician(ticketId, parseInt(techId));
            fetchAllTickets();
        } catch (err) {
            setError('Failed to assign technician.');
        }
    };

    const handleUpdateStatus = async (ticketId, status) => {
        let resolutionNotes = null;
        let adminReason = null;

        if (status === 'RESOLVED') {
            resolutionNotes = window.prompt('Enter resolution notes:');
            if (resolutionNotes === null) return;
        }
        if (status === 'REJECTED') {
            adminReason = window.prompt('Enter reason for rejection:');
            if (adminReason === null) return;
        }
        try {
            await ticketApi.updateTicketStatus(ticketId, status, resolutionNotes, adminReason);
            fetchAllTickets();
        } catch (err) {
            setError(`Failed to update ticket to ${status}.`);
        }
    };

    const filteredTickets = filterStatus ? tickets.filter(t => t.status === filterStatus) : tickets;

    if (loading) return <div>Loading admin dashboard...</div>;
    if (selectedTicketId) return <TicketDetail ticketId={selectedTicketId} userId={adminId} onBack={() => { setSelectedTicketId(null); fetchAllTickets(); }} />;

    return (
        <div className="admin-tickets-container">
            <h2>Admin: Manage Incident Tickets</h2>
            {error && <div className="alert alert-error">{error}</div>}

            <div className="filter-bar">
                <label>Filter by Status: </label>
                <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
                    <option value="">All</option>
                    {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
            </div>

            {filteredTickets.length === 0 ? (
                <p>No tickets found.</p>
            ) : (
                <table className="tickets-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>User ID</th>
                            <th>Location</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Technician</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredTickets.map(ticket => (
                            <tr key={ticket.id}>
                                <td>{ticket.id}</td>
                                <td>{ticket.userId}</td>
                                <td>{ticket.resourceLocation}</td>
                                <td>{ticket.category}</td>
                                <td>
                                    <span className={`priority-badge priority-${ticket.priority.toLowerCase()}`}>
                                        {ticket.priority}
                                    </span>
                                </td>
                                <td>
                                    <span className={`status-badge status-${ticket.status.toLowerCase()}`}>
                                        {ticket.status}
                                    </span>
                                    {ticket.adminReason && <div><small>Reason: {ticket.adminReason}</small></div>}
                                </td>
                                <td>{ticket.assignedTechnicianId || 'Unassigned'}</td>
                                <td>
                                    <div className="action-buttons">
                                        <button onClick={() => setSelectedTicketId(ticket.id)} className="btn btn-secondary btn-sm">View</button>
                                        {(ticket.status === 'OPEN' || ticket.status === 'IN_PROGRESS') && (
                                            <button onClick={() => handleAssign(ticket.id)} className="btn btn-warning btn-sm">Assign</button>
                                        )}
                                        {ticket.status === 'OPEN' && (
                                            <>
                                                <button onClick={() => handleUpdateStatus(ticket.id, 'IN_PROGRESS')} className="btn btn-info btn-sm">Start</button>
                                                <button onClick={() => handleUpdateStatus(ticket.id, 'REJECTED')} className="btn btn-danger btn-sm">Reject</button>
                                            </>
                                        )}
                                        {ticket.status === 'IN_PROGRESS' && (
                                            <button onClick={() => handleUpdateStatus(ticket.id, 'RESOLVED')} className="btn btn-success btn-sm">Resolve</button>
                                        )}
                                        {ticket.status === 'RESOLVED' && (
                                            <button onClick={() => handleUpdateStatus(ticket.id, 'CLOSED')} className="btn btn-dark btn-sm">Close</button>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default AdminTickets;
