import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { registerProvider } from "../api/providerApi";
import { getCategories } from "../api/categoryApi";
import { useAuth } from "../context/AuthContext";

const EMPTY_FORM = {
  businessName: "",
  description: "",
  experienceYears: 0,
  serviceRadius: 15,
  location: "",
  latitude: "",
  longitude: "",
  serviceCategoryIds: []
};

const BecomeProvider = () => {
  const navigate = useNavigate();
  const { handleLogout } = useAuth();

  const [form, setForm] = useState(EMPTY_FORM);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [geocoding, setGeocoding] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    getCategories()
      .then(res => { if (res.success) setCategories(res.data); })
      .catch(() => {});
  }, []);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setForm(f => ({
      ...f,
      [name]: type === "number" ? parseFloat(value) || 0 : value
    }));
  };

  const handleCategoryToggle = (id) => {
    setForm(f => ({
      ...f,
      serviceCategoryIds: f.serviceCategoryIds.includes(id)
        ? f.serviceCategoryIds.filter(c => c !== id)
        : [...f.serviceCategoryIds, id]
    }));
  };

  const geocodeLocation = async () => {
    if (!form.location.trim()) {
      setError("Enter a location first");
      return;
    }
    setGeocoding(true);
    setError(null);
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(form.location)}&limit=1`;
      const res = await fetch(url, { headers: { "Accept-Language": "en" } });
      const data = await res.json();
      if (data.length === 0) {
        setError("Location not found. Try a more specific address.");
        return;
      }
      setForm(f => ({
        ...f,
        latitude: parseFloat(data[0].lat).toFixed(6),
        longitude: parseFloat(data[0].lon).toFixed(6)
      }));
    } catch {
      setError("Geocoding failed. Enter coordinates manually.");
    } finally {
      setGeocoding(false);
    }
  };

  const validate = () => {
    if (!form.businessName.trim()) return "Business name is required";
    if (!form.description.trim()) return "Description is required";
    if (!form.location.trim()) return "Location is required";
    if (!form.latitude || !form.longitude) return "Click 'Find on map' to get coordinates";
    if (form.serviceCategoryIds.length === 0) return "Select at least one service category";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const validationError = validate();
    if (validationError) { setError(validationError); return; }

    setLoading(true);
    try {
      const payload = {
        ...form,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude)
      };
      const res = await registerProvider(payload);
      if (res.success) {
        setSuccess(true);
      } else {
        setError(res.message);
      }
    } catch (err) {
      const data = err.response?.data;
      if (data?.data && typeof data.data === "object") {
        setError(Object.values(data.data).join(", "));
      } else {
        setError(data?.message || "Registration failed");
      }
    } finally {
      setLoading(false);
    }
  };

  // success screen — force re-login for new JWT with PROVIDER role
  if (success) {
    return (
      <div className="container py-5">
        <div className="row justify-content-center">
          <div className="col-md-6">
            <div className="card border-0 shadow-sm text-center p-4">
              <div className="mb-3">
                <span className="badge bg-success fs-6 px-3 py-2">
                  Provider account created!
                </span>
              </div>
              <h5 className="fw-bold mb-2">You're almost there</h5>
              <p className="text-muted small mb-4">
                Your provider account has been set up. You need to sign in again
                to activate your provider access — your session needs to refresh
                with your new role.
              </p>
              <button
                className="btn btn-primary"
                onClick={handleLogout}
              >
                Sign in again to activate
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-8">

          <div className="mb-4">
            <h5 className="fw-bold mb-1">Become a provider</h5>
            <p className="text-muted small mb-0">
              Set up your business profile and start receiving bookings.
            </p>
          </div>

          {error && (
            <div className="alert alert-danger py-2 small alert-dismissible">
              {error}
              <button
                type="button"
                className="btn-close"
                onClick={() => setError(null)}
              />
            </div>
          )}

          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit}>

                {/* Business info */}
                <h6 className="fw-semibold mb-3 text-muted">Business info</h6>
                <div className="row g-3 mb-4">
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Business name</label>
                    <input
                      type="text"
                      className="form-control"
                      name="businessName"
                      value={form.businessName}
                      onChange={handleChange}
                      placeholder="e.g. John's Plumbing Services"
                      required
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Description</label>
                    <textarea
                      className="form-control"
                      name="description"
                      value={form.description}
                      onChange={handleChange}
                      rows={3}
                      placeholder="Describe your services, experience, and what makes you stand out..."
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">
                      Years of experience
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      name="experienceYears"
                      value={form.experienceYears}
                      onChange={handleChange}
                      min={0}
                      max={50}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">
                      Service radius (km)
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      name="serviceRadius"
                      value={form.serviceRadius}
                      onChange={handleChange}
                      min={1}
                      max={200}
                    />
                    <div className="form-text">
                      How far are you willing to travel?
                    </div>
                  </div>
                </div>

                <hr className="my-3" />

                {/* Location */}
                <h6 className="fw-semibold mb-3 text-muted">Your location</h6>
                <div className="row g-3 mb-4">
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Business address</label>
                    <div className="input-group">
                      <input
                        type="text"
                        className="form-control"
                        name="location"
                        value={form.location}
                        onChange={handleChange}
                        placeholder="123 Main St, New York, NY"
                        required
                      />
                      <button
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={geocodeLocation}
                        disabled={geocoding}
                      >
                        {geocoding
                          ? <span className="spinner-border spinner-border-sm" />
                          : "Find on map"}
                      </button>
                    </div>
                    <div className="form-text">
                      Type your address then click "Find on map" to get coordinates.
                    </div>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Latitude</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      name="latitude"
                      value={form.latitude}
                      onChange={handleChange}
                      placeholder="Auto-filled"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Longitude</label>
                    <input
                      type="number"
                      step="any"
                      className="form-control"
                      name="longitude"
                      value={form.longitude}
                      onChange={handleChange}
                      placeholder="Auto-filled"
                    />
                  </div>
                </div>

                <hr className="my-3" />

                {/* Service categories */}
                <h6 className="fw-semibold mb-3 text-muted">Services you offer</h6>
                {categories.length === 0 ? (
                  <div className="spinner-border spinner-border-sm text-primary" />
                ) : (
                  <div className="row g-2 mb-4">
                    {categories.map(cat => (
                      <div key={cat.id} className="col-6 col-md-4">
                        <div
                          className={`card border py-2 px-3 small cursor-pointer ${
                            form.serviceCategoryIds.includes(cat.id)
                              ? "border-primary bg-primary bg-opacity-10 text-primary fw-semibold"
                              : "border-secondary-subtle text-muted"
                          }`}
                          style={{ cursor: "pointer" }}
                          onClick={() => handleCategoryToggle(cat.id)}
                        >
                          <div className="d-flex align-items-center gap-2">
                            <input
                              type="checkbox"
                              className="form-check-input mt-0"
                              checked={form.serviceCategoryIds.includes(cat.id)}
                              onChange={() => handleCategoryToggle(cat.id)}
                            />
                            {cat.name}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                <button
                  type="submit"
                  className="btn btn-primary px-4"
                  disabled={loading}
                >
                  {loading ? (
                    <><span className="spinner-border spinner-border-sm me-2" />Registering...</>
                  ) : "Register as provider"}
                </button>

              </form>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default BecomeProvider;