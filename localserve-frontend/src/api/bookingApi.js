import api from "./axios";

export const createBooking = async (data) => {
  const res = await api.post("/api/bookings", data);
  return res.data;
};

export const getMyBookings = async (page = 0, size = 10) => {
  const res = await api.get(`/api/bookings/my?page=${page}&size=${size}`);
  return res.data;
};

export const getProviderBookings = async (page = 0, size = 10) => {
  const res = await api.get(`/api/bookings/provider/my?page=${page}&size=${size}`);
  return res.data;
};

export const acceptBooking = async (id) => {
  const res = await api.put(`/api/bookings/${id}/accept`);
  return res.data;
};

export const rejectBooking = async (id) => {
  const res = await api.put(`/api/bookings/${id}/reject`);
  return res.data;
};

export const completeBooking = async (id) => {
  const res = await api.put(`/api/bookings/${id}/complete`);
  return res.data;
};