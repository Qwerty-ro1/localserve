import { useState, useEffect } from "react";
import { getMyProvider, updateProvider } from "../api/providerApi";
import { getCategories } from "../api/categoryApi";

const EditProviderProfile = () => {
  const [form, setForm] = useState(null);
  const [providerId, setProviderId] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [geocoding, setGeocoding] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    Promise.all([getMyProvider(), getCategories()])
      .then(([providerRes, catRes]) => {
        if (providerRes.success) {
          const p = providerRes.data;
          setProviderId(p.id);
          // map service names back to category IDs
          const matchedIds = catRes.success
            ? catRes.data
                .filter(c => p.services.includes(c.name))
                .map(c => c.id)
            : [];
          setForm({
            businessName: p.businessName || "",
            description: p.description || "",
            experienceYears: p.experienceYears ?? 0,
            serviceRadius: p.serviceRadius ?? 15,
            location: p.location || "",
            latitude: p.latitude ?? "",
            longitude: p.longitude ?? "",
            serviceCategoryIds: matchedIds
          });
        }
        if (catRes.success) setCategories(catRes.data);
      })
      .catch(() => setError("Failed to load provider profile"))
      .finally(() => setFetchLoading(false));
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
    if (!form.location.trim()) { setError("Enter a location first"); return; }
    setGeocoding(true);
    setError(null);
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(form.location)}&limit=1`;
      const res = await fetch(url, { headers: { "Accept-Language": "en" } });
      const data = await res.json();
      if (data.length === 0) { setError("Location not found."); return; }
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
    if (form.serviceCategoryIds.length === 0) return "Select at least one service";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    const validationError = validate();
    if (validationError) { setError(validationError); return; }

    setLoading(true);
    try {
      const payload = {
        ...form,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude)
      };
      const res = await updateProvider(providerId, payload);
      if (res.success) {
        setSuccess(true);
        window.scrollTo({ top: 0, behavior: "smooth" });
      } else {
        setError(res.message);
      }
    } catch (err) {
      const data = err.response?.data;
      if (data?.data && typeof data.data === "object") {
        setError(Object.values(data.data).join(", "));
      } else {
        setError(data?.message || "Update failed");
      }
    } finally {
      setLoading(false);
    }
  };

  if (fetchLoading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" />
      </div>
    );
  }

  if (!form) {
    return (
      <div className="container py-4">
        <div className="alert alert-danger">Failed to load provider profile.</div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-8">

          <div className="mb-4">
            <h5 className="fw-bold mb-1">Edit provider profile</h5>
            <p className="text-muted small mb-0">
              Update your business info, location, and services.
            </p>
          </div>

          {success && (
            <div className="alert alert-success py-2 small alert-dismissible">
              Profile updated successfully.
              <button
                type="button"
                className="btn-close"
                onClick={() => setSuccess(false)}
              />
            </div>
          )}

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
                  </div>
                </div>

                <hr className="my-3" />

                {/* Location */}
                <h6 className="fw-semibold mb-3 text-muted">Location</h6>
                <div className="row g-3 mb-4">
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Business address</label>
                    <div className="input-group">
                      <input
                        type="text"
                        className="form-control"
                        name="location"
                        value={form.location}
                        onChange={(e) => {
                          handleChange(e);
                          // clear coords when address is edited — forces re-geocode
                          setForm(f => ({ ...f, latitude: "", longitude: "" }));
                        }}
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
                    {form.latitude && form.longitude ? (
                      <div className="form-text text-success">
                        Location confirmed.
                      </div>
                    ) : (
                      <div className="form-text">
                        Click "Find on map" to confirm your location coordinates.
                      </div>
                    )}
                  </div>
                </div>

                <hr className="my-3" />

                {/* Services */}
                <h6 className="fw-semibold mb-3 text-muted">Services you offer</h6>
                <div className="row g-2 mb-4">
                  {categories.map(cat => (
                    <div key={cat.id} className="col-6 col-md-4">
                      <div
                        className={`card border py-2 px-3 small ${
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

                <button
                  type="submit"
                  className="btn btn-primary px-4"
                  disabled={loading}
                >
                  {loading ? (
                    <><span className="spinner-border spinner-border-sm me-2" />Saving...</>
                  ) : "Save changes"}
                </button>

              </form>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default EditProviderProfile;