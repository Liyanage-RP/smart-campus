import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/tickets';

export const ticketApi = {

    // Create a new ticket
    createTicket: async (ticketData) => {
        const response = await axios.post(API_BASE_URL, ticketData);
        return response.data;
    },

    // Get current user's tickets
    getMyTickets: async (userId) => {
        const response = await axios.get(`${API_BASE_URL}/my`, { params: { userId } });
        return response.data;
    },

    // Admin: get all tickets
    getAllTickets: async () => {
        const response = await axios.get(`${API_BASE_URL}/all`);
        return response.data;
    },

    // Get a single ticket by ID
    getTicketById: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/${id}`);
        return response.data;
    },

    // Update ticket status (Admin/Technician)
    updateTicketStatus: async (id, status, resolutionNotes = null, adminReason = null) => {
        const payload = { status };
        if (resolutionNotes) payload.resolutionNotes = resolutionNotes;
        if (adminReason) payload.adminReason = adminReason;
        const response = await axios.put(`${API_BASE_URL}/${id}/status`, payload);
        return response.data;
    },

    // Assign a technician to a ticket
    assignTechnician: async (ticketId, technicianId) => {
        const response = await axios.put(`${API_BASE_URL}/${ticketId}/assign`, { technicianId });
        return response.data;
    },

    // Delete a ticket
    deleteTicket: async (id) => {
        await axios.delete(`${API_BASE_URL}/${id}`);
    },

    // Upload an image attachment (max 3)
    addAttachment: async (ticketId, file) => {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axios.post(`${API_BASE_URL}/${ticketId}/attachments`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
    },

    // Get comments for a ticket
    getComments: async (ticketId) => {
        const response = await axios.get(`${API_BASE_URL}/${ticketId}/comments`);
        return response.data;
    },

    // Add a comment
    addComment: async (ticketId, commentData) => {
        const response = await axios.post(`${API_BASE_URL}/${ticketId}/comments`, commentData);
        return response.data;
    },

    // Edit a comment (owner only)
    editComment: async (ticketId, commentId, userId, content) => {
        const response = await axios.put(`${API_BASE_URL}/${ticketId}/comments/${commentId}`, { userId, content });
        return response.data;
    },

    // Delete a comment (owner only)
    deleteComment: async (ticketId, commentId, userId) => {
        await axios.delete(`${API_BASE_URL}/${ticketId}/comments/${commentId}`, { params: { userId } });
    }
};
