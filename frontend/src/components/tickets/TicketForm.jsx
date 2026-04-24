import React, { useState } from 'react';
import { ticketApi } from '../../api/ticketApi';

const CATEGORIES = ['Electrical', 'Plumbing', 'IT Equipment', 'Furniture', 'Safety', 'Cleaning', 'Other'];
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

const TicketForm = ({ userId = 1 }) => {
    const [formData, setFormData] = useState({
        resourceLocation: '',
        category: '',
        description: '',
        priority: 'MEDIUM',
        contactDetails: ''
    });
    const [attachments, setAttachments] = useState([]);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleFileChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length > 3) {
            setError('You can upload a maximum of 3 images.');
            return;
        }
        setAttachments(files);
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');
        setLoading(true);

        try {
            // Step 1: Create the ticket
            const ticket = await ticketApi.createTicket({ ...formData, userId });

            // Step 2: Upload attachments one by one
            for (const file of attachments) {
                await ticketApi.addAttachment(ticket.id, file);
            }

            setMessage(`Ticket #${ticket.id} created successfully! Our team will review it shortly.`);
            setFormData({ resourceLocation: '', category: '', description: '', priority: 'MEDIUM', contactDetails: '' });
            setAttachments([]);
        } catch (err) {
            if (err.response?.data?.message) {
                setError(err.response.data.message);
            } else {
                setError('An unexpected error occurred while creating the ticket.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ticket-form-container">
            <h2>Report an Incident</h2>
            {message && <div className="alert alert-success">{message}</div>}
            {error && <div className="alert alert-error">{error}</div>}

            <form onSubmit={handleSubmit} className="ticket-form">
                <div className="form-group">
                    <label>Resource / Location:</label>
                    <input
                        type="text"
                        name="resourceLocation"
                        placeholder="e.g. Lab 3, Room 204, Main Hall"
                        value={formData.resourceLocation}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Category:</label>
                    <select name="category" value={formData.category} onChange={handleChange} required>
                        <option value="">-- Select Category --</option>
                        {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                    </select>
                </div>

                <div className="form-group">
                    <label>Priority:</label>
                    <select name="priority" value={formData.priority} onChange={handleChange} required>
                        {PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}
                    </select>
                </div>

                <div className="form-group">
                    <label>Description:</label>
                    <textarea
                        name="description"
                        rows="5"
                        placeholder="Describe the issue in detail..."
                        value={formData.description}
                        onChange={handleChange}
                        required
                    ></textarea>
                </div>

                <div className="form-group">
                    <label>Preferred Contact Details:</label>
                    <input
                        type="text"
                        name="contactDetails"
                        placeholder="e.g. ext. 2234 or email@university.edu"
                        value={formData.contactDetails}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label>Attachments (up to 3 images):</label>
                    <input
                        type="file"
                        accept="image/*"
                        multiple
                        onChange={handleFileChange}
                    />
                    <small>{attachments.length} file(s) selected</small>
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Submitting...' : 'Submit Ticket'}
                </button>
            </form>
        </div>
    );
};

export default TicketForm;
