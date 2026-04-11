import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProviderById } from "../api/providerApi";
import CreateBooking from "./CreateBooking";
import { FaStar } from "react-icons/fa";

const ProviderDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [provider, setProvider] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showBooking, setShowBooking] = useState(false);

  useEffect(() => {
    getProviderById(id)
      .then(res => {
        if (res.success) setProvider(res.data);
        else setError("Failed to load provider");
      })
      .catch(() => setError("Failed to load provider"))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" />
      </div>
    );
  }

  if (error || !provider) {
    return (
      <div className="container py-4">
        <div className="alert alert-danger">{error || "Provider not found"}</div>
        <button className="btn btn-outline-secondary btn-sm" onClick={() => navigate("/providers")}>
          Back to providers
        </button>
      </div>
    );
  }

  return (
    <div className="container py-4">

      {/* Back */}
      <button
        className="btn btn-link ps-0 text-decoration-none text-muted small mb-3"
        onClick={() => navigate("/providers")}
      >
        &larr; Back to providers
      </button>

      <div className="row g-4">

        {/* Main info */}
        <div className="col-lg-8">
          <div className="card border-0 shadow-sm">
            <div className="card-header bg-primary text-white py-3">
              <h5 className="mb-0 fw-bold">{provider.businessName}</h5>
              <small className="opacity-75">{provider.location}</small>
            </div>
            <div className="card-body p-4">

              {provider.description && (
                <div className="mb-4">
                  <h6 className="fw-semibold text-muted mb-2">About</h6>
                  <p className="mb-0">{provider.description}</p>
                </div>
              )}

              <hr />

              <div className="row g-3 mb-4">
                <div className="col-6 col-md-3">
                  <p className="text-muted small mb-1">Rating</p>
                  <p className="fw-semibold mb-0">
                    <FaStar className="text-warning me-1" style={{ fontSize: 13 }} />
                    {provider.rating}/5
                  </p>
                </div>
                <div className="col-6 col-md-3">
                  <p className="text-muted small mb-1">Experience</p>
                  <p className="fw-semibold mb-0">{provider.experienceYears} yrs</p>
                </div>
                <div className="col-6 col-md-3">
                  <p className="text-muted small mb-1">Service radius</p>
                  <p className="fw-semibold mb-0">{provider.serviceRadius} km</p>
                </div>
                {provider.distance != null && (
                  <div className="col-6 col-md-3">
                    <p className="text-muted small mb-1">Distance</p>
                    <p className="fw-semibold mb-0">{provider.distance.toFixed(2)} km</p>
                  </div>
                )}
              </div>

              <hr />

              {/* Services offered */}
              <h6 className="fw-semibold text-muted mb-3">Services offered</h6>
              {provider.offerings && provider.offerings.length > 0 ? (
                <div className="d-flex flex-wrap gap-2">
                  {provider.offerings.map(o => (
                    <span key={o.id} className="badge bg-primary bg-opacity-10 text-primary px-3 py-2">
                      {o.categoryName}
                    </span>
                  ))}
                </div>
              ) : (
                <p className="text-muted small">No services listed.</p>
              )}

            </div>
          </div>
        </div>

        {/* Booking card */}
        <div className="col-lg-4">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4 text-center">
              <h6 className="fw-bold mb-1">Ready to book?</h6>
              <p className="text-muted small mb-4">
                Select a service and your preferred time.
              </p>
              <button
                className="btn btn-primary w-100"
                onClick={() => setShowBooking(true)}
                disabled={!provider.offerings || provider.offerings.length === 0}
              >
                Book now
              </button>
              {(!provider.offerings || provider.offerings.length === 0) && (
                <small className="text-muted d-block mt-2">
                  This provider has no services listed yet.
                </small>
              )}
            </div>
          </div>
        </div>

      </div>

      {/* CreateBooking modal */}
      {showBooking && (
        <CreateBooking
          provider={provider}
          onClose={() => setShowBooking(false)}
        />
      )}

    </div>
  );
};

export default ProviderDetail;