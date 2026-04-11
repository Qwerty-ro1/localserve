import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { searchProviders } from "../api/providerApi";
import { getCategories } from "../api/categoryApi";
import AddressSelector from "../components/AddressSelector";
import { FaStar } from "react-icons/fa";


const Providers = () => {
  const navigate = useNavigate();

  const [providers, setProviders] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalProviders, setTotalProviders] = useState(0);

  // Search & filter
  const [sortBy, setSortBy] = useState("distance");
  const [sortDirection, setSortDirection] = useState("asc");
  const [selectedCategory, setSelectedCategory] = useState("");

  // Location — set by AddressSelector, no GPS needed
  const [latitude, setLatitude] = useState(null);
  const [longitude, setLongitude] = useState(null);
  const [radius] = useState(50);

  // Fetch categories once on mount
  useEffect(() => {
    getCategories()
      .then(res => { if (res.success) setCategories(res.data); })
      .catch(() => {});
  }, []);

  // Re-fetch when location or filters change
  useEffect(() => {
    if (latitude && longitude) fetchProviders(0);
  }, [latitude, longitude, sortBy, sortDirection, selectedCategory]);

  const handleAddressSelect = (address) => {
    if (address) {
      setLatitude(address.latitude);
      setLongitude(address.longitude);
    }
  };

  const fetchProviders = async (page) => {
    setLoading(true);
    setError(null);
    try {
      const response = await searchProviders(
        latitude,
        longitude,
        radius,
        page,
        pageSize,
        selectedCategory || null,
        sortBy,
        sortDirection
      );
      if (response.success) {
        setProviders(response.data.content || []);
        setTotalPages(response.data.totalPages || 0);
        setTotalProviders(response.data.totalElements || 0);
        setCurrentPage(page);
      } else {
        setError("Failed to fetch providers");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Error fetching providers");
    } finally {
      setLoading(false);
    }
  };

  const renderPaginationButtons = () => {
    const buttons = [];
    const maxButtons = 5;
    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);
    if (endPage - startPage < maxButtons - 1)
      startPage = Math.max(0, endPage - maxButtons + 1);

    for (let i = startPage; i <= endPage; i++) {
      buttons.push(
        <li key={i} className={`page-item ${i === currentPage ? "active" : ""}`}>
          <button className="page-link" onClick={() => fetchProviders(i)}>
            {i + 1}
          </button>
        </li>
      );
    }
    return buttons;
  };

  return (
    <div className="container py-4">
      <h5 className="fw-bold mb-3">Service Providers</h5>

      {/* Address selector — drives the search location */}
      <AddressSelector onSelect={handleAddressSelect} />

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Filters */}
      <div className="row g-2 mb-4">
        <div className="col-md-4">
          <select
            className="form-select form-select-sm"
            value={selectedCategory}
            onChange={e => setSelectedCategory(e.target.value)}
          >
            <option value="">All categories</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>
        <div className="col-md-4">
          <select
            className="form-select form-select-sm"
            value={sortBy}
            onChange={e => setSortBy(e.target.value)}
          >
            <option value="distance">Sort: Distance</option>
            <option value="rating">Sort: Rating</option>
          </select>
        </div>
        <div className="col-md-4">
          <select
            className="form-select form-select-sm"
            value={sortDirection}
            onChange={e => setSortDirection(e.target.value)}
          >
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>
        </div>
      </div>

      {/* Content */}
      {!latitude ? (
        <div className="text-center py-5">
          <p className="text-muted small">
            Select a service address above to find providers near you.
          </p>
        </div>
      ) : loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
        </div>
      ) : providers.length === 0 ? (
        <div className="text-center py-5">
          <p className="text-muted">No providers found near this address.</p>
          <button
            className="btn btn-outline-primary btn-sm"
            onClick={() => fetchProviders(0)}
          >
            Try again
          </button>
        </div>
      ) : (
        <>
          <p className="text-muted small mb-3">
            Showing {providers.length} of {totalProviders} providers
          </p>

          <div className="row g-3 mb-4">
            {providers.map(provider => (
              <div key={provider.id} className="col-md-6 col-lg-4">
                <div className="card h-100 border-0 shadow-sm">
                  <div className="card-header bg-primary text-white py-2">
                    <h6 className="mb-0">
                      {provider.businessName || "Unknown Provider"}
                    </h6>
                  </div>
                  <div className="card-body small">
                    <p className="mb-1">
                      <strong>Location:</strong> {provider.location || "N/A"}
                    </p>
                    <p className="mb-1">
                      <strong>Experience:</strong> {provider.experienceYears || 0} yrs
                    </p>
                    <p className="mb-1">
                      <strong>Rating:</strong>{" "}
                      <FaStar className="text-warning me-1" style={{ fontSize: 12 }} />
                      {provider.rating || 0}/5
                    </p>
                    <p className="mb-1">
                      <strong>Radius:</strong> {provider.serviceRadius || 0} km
                    </p>
                    {provider.distance != null && (
                      <p className="mb-1">
                        <strong>Distance:</strong> {provider.distance.toFixed(2)} km
                      </p>
                    )}
                    {provider.description && (
                      <p className="mb-1 text-muted">{provider.description}</p>
                    )}
                  </div>
                  <div className="card-footer bg-white border-0 pt-0">
                    <button
                      className="btn btn-primary btn-sm w-100"
                      onClick={() => navigate(`/providers/${provider.id}`)}
                    >
                      View & Book
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <nav>
              <ul className="pagination justify-content-center pagination-sm">
                <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                  <button
                    className="page-link"
                    onClick={() => fetchProviders(currentPage - 1)}
                  >
                    Previous
                  </button>
                </li>
                {renderPaginationButtons()}
                <li className={`page-item ${currentPage === totalPages - 1 ? "disabled" : ""}`}>
                  <button
                    className="page-link"
                    onClick={() => fetchProviders(currentPage + 1)}
                  >
                    Next
                  </button>
                </li>
              </ul>
              <p className="text-center text-muted small">
                Page {currentPage + 1} of {totalPages}
              </p>
            </nav>
          )}
        </>
      )}
    </div>
  );
};

export default Providers;