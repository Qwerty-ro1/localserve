import api from "./axios";

export const getAddresses = async () => {
  const res = await api.get("/api/addresses");
  return res.data;
};

export const addAddress = async (data) => {
  const res = await api.post("/api/addresses", data);
  return res.data;
};

export const updateAddress = async (id, data) => {
  const res = await api.put(`/api/addresses/${id}`, data);
  return res.data;
};

export const deleteAddress = async (id) => {
  const res = await api.delete(`/api/addresses/${id}`);
  return res.data;
};

export const setDefaultAddress = async (id) => {
  const res = await api.put(`/api/addresses/${id}/default`);
  return res.data;
};