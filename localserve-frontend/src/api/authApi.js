import api from "./axios";

export const login = async (data) => {
  const res = await api.post("/api/auth/login", data);
  return res.data;
};

export const register = async (data) => {
  const res = await api.post("/api/auth/register", data);
  return res.data;
};

