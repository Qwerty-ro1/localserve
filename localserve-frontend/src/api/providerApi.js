import api from "./axios";

/**
 * Fetch providers with search and pagination
 * @param {number} latitude - User latitude
 * @param {number} longitude - User longitude
 * @param {number} radius - Search radius in km
 * @param {number} page - Page number (0-indexed)
 * @param {number} size - Items per page
 * @param {number} serviceCategoryId - Optional service category filter
 * @param {string} sortBy - Sort field (distance, rating, etc)
 * @param {string} direction - Sort direction (asc, desc)
 */
export const searchProviders = async (
  latitude,
  longitude,
  radius,
  page = 0,
  size = 10,
  serviceCategoryId = null,
  sortBy = "distance",
  direction = "asc"
) => {
  const params = new URLSearchParams({
    latitude,
    longitude,
    radius,
    page,
    size,
    sortBy,
    direction,
  });

  if (serviceCategoryId) {
    params.append("serviceCategoryId", serviceCategoryId);
  }

  const res = await api.get(`/api/providers/search?${params}`);
  return res.data;
};

/**
 * Get provider details by ID
 */
export const getProviderById = async (id) => {
  const res = await api.get(`/api/providers/${id}`);
  return res.data;
};

/**
 * Register as a provider
 */
export const registerProvider = async (data) => {
  const res = await api.post("/api/providers", data);
  return res.data;
};

/**
 * Update provider profile
 */
export const updateProvider = async (id, data) => {
  const res = await api.put(`/api/providers/${id}`, data);
  return res.data;
};

