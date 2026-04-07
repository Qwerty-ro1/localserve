import { useState, useEffect } from "react";
import { searchProviders } from "../api/providerApi";
import AuthGuard from "../components/AuthGuard";
import BackButtonWarning from "../components/BackButtonWarning";
const Providers = () => {
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalProviders, setTotalProviders] = useState(0);
  // Search & Sort
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState("distance");
  const [sortDirection, setSortDirection] = useState("asc");
  // Location
  const [latitude] = useState(40.7128);
  const [longitude] = useState(-74.0060);
  const [radius] = useState(50);
  useEffect(() => {
    fetchProviders(0);
  }, [searchTerm, sortBy, sortDirection]);
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
        null,
        sortBy,
        sortDirection
      );
      if (response.success) {
        // Filter by search term if provided
        let filtered = response.data.content || [];
        if (searchTerm.trim()) {
          filtered = filtered.filter(provider =>
            (provider.businessName || "").toLowerCase().includes(searchTerm.toLowerCase()) ||
            (provider.location || "").toLowerCase().includes(searchTerm.toLowerCase())
          );
        }
        setProviders(filtered);
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
  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      fetchProviders(currentPage + 1);
    }
  };
  const handlePreviousPage = () => {
    if (currentPage > 0) {
      fetchProviders(currentPage - 1);
    }
  };
  const handlePageClick = (page) => {
    fetchProviders(page);
  };
  const renderPaginationButtons = () => {
    const buttons = [];
    const maxButtons = 5;
    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);
    if (endPage - startPage < maxButtons - 1) {
      startPage = Math.max(0, endPage - maxButtons + 1);
    }
    for (let i = startPage; i <= endPage; i++) {
      buttons.push(
        <li key={i} className="page-item">
          <button
            className={page-link }
            onClick={() => handlePageClick(i)}
          >
            {i + 1}
          </button>
        </li>
      );
    }
    return buttons;
  };
  return (
    <AuthGuard>
      <BackButtonWarning />
      <div className="container my-5">
        <h1 className="mb-4">Service Providers</h1>
        {error && <div className="alert alert-danger">{error}</div>}
        {/* Search & Sort Controls */}
        <div className="row g-3 mb-4">
          <div className="col-md-6">
            <input
              type="text"
              className="form-control"
              placeholder="Search by provider name or location..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <select
              className="form-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="distance">Sort by: Distance</option>
              <option value="rating">Sort by: Rating</option>
            </select>
          </div>
          <div className="col-md-3">
            <select
              className="form-select"
              value={sortDirection}
              onChange={(e) => setSortDirection(e.target.value)}
            >
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
          </div>
        </div>
        {loading ? (
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : providers.length === 0 ? (
          <div className="alert alert-info">No providers found in your area</div>
        ) : (
          <>
            <p className="text-muted">Showing {providers.length} of {totalProviders} providers</p>
            <div className="row g-4 mb-5">
              {providers.map((provider) => (
                <div key={provider.id} className="col-md-6 col-lg-4">
                  <div className="card h-100 shadow-sm">
                    <div className="card-header bg-primary text-white">
                      <h5 className="card-title mb-0">
                        {provider.businessName || "Unknown Provider"}
                      </h5>
                    </div>
                    <div className="card-body">
                      <p className="card-text">
                        <strong>Location:</strong> {provider.location || "N/A"}
                      </p>
                      <p className="card-text">
                        <strong>Experience:</strong> {provider.experienceYears || 0} years
                      </p>
                      <p className="card-text">
                        <strong>Rating:</strong> ⭐ {provider.rating || 0}/5
                      </p>
                      <p className="card-text">
                        <strong>Service Radius:</strong> {provider.serviceRadius || 0} km
                      </p>
                      {provider.distance && (
                        <p className="card-text">
                          <strong>Distance:</strong> {provider.distance.toFixed(2)} km
                        </p>
                      )}
                      {provider.description && (
                        <p className="card-text">
                          <strong>About:</strong> {provider.description}
                        </p>
                      )}
                      {provider.services && provider.services.length > 0 && (
                        <p className="card-text">
                          <strong>Services:</strong> {provider.services.join(", ")}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
            {/* Pagination */}
            {totalPages > 1 && (
              <>
                <nav aria-label="Page navigation">
                  <ul className="pagination justify-content-center">
                    <li className={page-item }>
                      <button
                        className="page-link"
                        onClick={handlePreviousPage}
                        disabled={currentPage === 0}
                      >
                        Previous
                      </button>
                    </li>
                    {renderPaginationButtons()}
                    <li className={page-item }>
                      <button
                        className="page-link"
                        onClick={handleNextPage}
                        disabled={currentPage === totalPages - 1}
                      >
                        Next
                      </button>
                    </li>
                  </ul>
                </nav>
                <p className="text-center text-muted">
                  Page {currentPage + 1} of {totalPages || 1}
                </p>
              </>
            )}
          </>
        )}
      </div>
    </AuthGuard>
  );
};
export default Providers;
