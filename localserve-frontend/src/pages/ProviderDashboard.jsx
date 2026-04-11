import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getMyProvider } from "../api/providerApi";
import { getProviderBookings } from "../api/bookingApi";

const ProviderDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [provider, setProvider] = useState(null);
  const [stats, setStats] = useState({
    total: 0, requested: 0, accepted: 0, completed: 0, rejected: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getMyProvider(), getProviderBookings(0, 100)])
      .then(([provRes, bookRes]) => {
        if (provRes.success) setProvider(provRes.data);
        if (bookRes.success) {
          const all = bookRes.data.content || [];
          setStats({
            total:     all.length,
            requested: all.filter(b => b.status === "REQUESTED").length,
            accepted:  all.filter(b => b.status === "ACCEPTED").length,
            completed: all.filter(b => b.status === "COMPLETED").length,
            rejected:  all.filter(b => b.status === "REJECTED").length
          });
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" />
      </div>
    );
  }

  return (
    <div>
      {/* Hero */}
      <div className="bg-primary text-white py-4">
        <div className="container">
          <h5 className="fw-bold mb-0">
            {provider?.businessName || user?.name}
          </h5>
          <small className="opacity-75">
            {provider?.location || "Provider dashboard"}
          </small>
        </div>
      </div>

      <div className="container py-4">

        {/* Stats */}
        <div className="row g-3 mb-4">
          <div className="col-6 col-md-3">
            <div className="card border-0 shadow-sm text-center py-3">
              <h4 className="fw-bold text-primary mb-0">{stats.total}</h4>
              <small className="text-muted">Total bookings</small>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border-0 shadow-sm text-center py-3">
              <h4 className="fw-bold text-warning mb-0">{stats.requested}</h4>
              <small className="text-muted">Pending</small>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border-0 shadow-sm text-center py-3">
              <h4 className="fw-bold text-success mb-0">{stats.accepted}</h4>
              <small className="text-muted">Accepted</small>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border-0 shadow-sm text-center py-3">
              <h4 className="fw-bold text-secondary mb-0">{stats.completed}</h4>
              <small className="text-muted">Completed</small>
            </div>
          </div>
        </div>

        <div className="row g-4">

          {/* Profile summary */}
          <div className="col-lg-8">
            <div className="card border-0 shadow-sm">
              <div className="card-header bg-white">
                <h6 className="mb-0 fw-semibold">Profile summary</h6>
              </div>
              <div className="card-body">
                {provider ? (
                  <div className="row g-3">
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Business name</p>
                      <p className="fw-semibold mb-0">{provider.businessName}</p>
                    </div>
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Rating</p>
                      <p className="fw-semibold mb-0">⭐ {provider.rating}/5</p>
                    </div>
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Experience</p>
                      <p className="fw-semibold mb-0">{provider.experienceYears} years</p>
                    </div>
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Service radius</p>
                      <p className="fw-semibold mb-0">{provider.serviceRadius} km</p>
                    </div>
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Location</p>
                      <p className="fw-semibold mb-0">{provider.location}</p>
                    </div>
                    <div className="col-sm-6">
                      <p className="text-muted small mb-1">Services</p>
                      <div className="d-flex flex-wrap gap-1">
                        {provider.services?.map((s, i) => (
                          <span key={i} className="badge bg-primary bg-opacity-10 text-primary">
                            {s}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                ) : (
                  <p className="text-muted small mb-0">Profile not found.</p>
                )}
              </div>
            </div>
          </div>

          {/* Quick actions */}
          <div className="col-lg-4">
            <div className="card border-0 shadow-sm">
              <div className="card-header bg-white">
                <h6 className="mb-0 fw-semibold">Quick actions</h6>
              </div>
              <div className="card-body d-grid gap-2">
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => navigate("/provider/bookings")}
                >
                  View incoming bookings
                  {stats.requested > 0 && (
                    <span className="badge bg-warning text-dark ms-2">
                      {stats.requested}
                    </span>
                  )}
                </button>
                <button
                  className="btn btn-outline-secondary btn-sm"
                  onClick={() => navigate("/provider/profile/edit")}
                >
                  Edit profile
                </button>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default ProviderDashboard;