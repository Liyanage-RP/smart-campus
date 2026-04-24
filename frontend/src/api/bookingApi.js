import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/bookings';

export const bookingApi = {
    createBooking: async (bookingData) => {
        const response = await axios.post(API_BASE_URL, bookingData);
        return response.data;
    },

    getMyBookings: async (userId) => {
        // userId should ideally come from an auth token, but we pass it as query param for this assignment
        const response = await axios.get(`${API_BASE_URL}/my`, { params: { userId } });
        return response.data;
    },

    getBookingById: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/${id}`);
        return response.data;
    },

    updateBookingStatus: async (id, status) => {
        const response = await axios.put(`${API_BASE_URL}/${id}/status`, { status });
        return response.data;
    },

    deleteBooking: async (id) => {
        const response = await axios.delete(`${API_BASE_URL}/${id}`);
        return response.data;
    }
};
