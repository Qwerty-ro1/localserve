import { useState, useEffect } from "react";
import {
  getAddresses, addAddress, updateAddress,
  deleteAddress, setDefaultAddress
} from "../api/addressApi";

const EMPTY_FORM = {
  label: "", addressLine: "", latitude: "", longitude: "", isDefault: false
};

const formatLabel = (label) => {
  const map = { HOME: "Home", OFFICE: "Office", OTHER: "Other" };
  return map[label] || label;
};

const ManageAddresses = () => {
  const [addresses, setAddresses] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [error, setError] = useState(null);
  const [geocoding, setGeocoding] = useState(false);
  const [locating, setLocating] = useState(false);

  useEffect(() => { fetchAddresses(); }, []);

  const fetchAddresses = async () => {
    setFetchLoading(true);
    try {
      const res = await getAddresses();
      if (res.success) setAddresses(res.data);
    } catch {
      setError("Failed to load addresses");
    } finally {
      setFetchLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  };

  // Geocode typed address using Nominatim (free, no API key)
  const geocodeAddress = async () => {
    if (!form.addressLine.trim()) {
      setError("Enter an address first");
      return;
    }
    setGeocoding(true);
    setError(null);
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(form.addressLine)}&limit=1`;
      const res = await fetch(url, {
        headers: { "Accept-Language": "en" }
      });
      const data = await res.json();
      if (data.length === 0) {
        setError("Address not found. Try a more specific address.");
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

  // GPS — get current location
  const useMyLocation = () => {
    if (!navigator.geolocation) {
      setError("Geolocation not supported by your browser.");
      return;
    }
    setLocating(true);
    setError(null);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setForm(f => ({
          ...f,
          latitude: pos.coords.latitude.toFixed(6),
          longitude: pos.coords.longitude.toFixed(6)
        }));
        setLocating(false);
      },
      () => {
        setError("Could not get GPS location. Try typing your address.");
        setLocating(false);
      }
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!form.latitude || !form.longitude) {
      setError("Click 'Find on map' or 'Use GPS' to get coordinates first.");
      return;
    }

    setLoading(true);
    try {
      const payload = {
        label: form.label,
        addressLine: form.addressLine,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude),
        isDefault: form.isDefault
      };

      if (editingId) {
        await updateAddress(editingId, payload);
      } else {
        await addAddress(payload);
      }
      setForm(EMPTY_FORM);
      setEditingId(null);
      await fetchAddresses();
    } catch (err) {
      const data = err.response?.data;
      if (data?.data && typeof data.data === "object") {
        setError(Object.values(data.data).join(", "));
      } else {
        setError(data?.message || "Failed to save address");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (addr) => {
    setEditingId(addr.id);
    setError(null);
    setForm({
      label: addr.label,
      addressLine: addr.addressLine,
      latitude: addr.latitude,
      longitude: addr.longitude,
      isDefault: addr.isDefault
    });
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this address?")) return;
    setError(null);
    try {
      await deleteAddress(id);
      await fetchAddresses();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete address");
    }
  };

  const handleSetDefault = async (id) => {
    setError(null);
    try {
      await setDefaultAddress(id);
      await fetchAddresses();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update default");
    }
  };

  const handleCancel = () => {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setError(null);
  };

  return (
    <div className="container py-4">
      <h5 className="fw-bold mb-4">My addresses</h5>

      {error && (
        <div className="alert alert-danger alert-dismissible py-2 small">
          {error}
          <button
            type="button"
            className="btn-close"
            onClick={() => setError(null)}
          />
        </div>
      )}

      {/* Saved addresses */}
      {fetchLoading ? (
        <div className="text-center py-4">
          <div className="spinner-border spinner-border-sm text-primary" />
        </div>
      ) : addresses.length === 0 ? (
        <div className="alert alert-info small mb-4">
          No addresses saved yet. Add one below.
        </div>
      ) : (
        <div className="row g-3 mb-4">
          {addresses.map(addr => (
            <div key={addr.id} className="col-md-6 col-lg-4">
              <div className={`card border-0 shadow-sm h-100 ${addr.default ? "border border-primary" : ""}`}>
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-1">
                    <span className="fw-semibold">{formatLabel(addr.label)}</span>
                    {addr.default && (
                      <span className="badge bg-primary">Default</span>
                    )}
                  </div>
                  <p className="text-muted small mb-1">{addr.addressLine}</p>
                  <p className="text-muted small mb-3" style={{fontSize:"11px"}}>
                    {parseFloat(addr.latitude).toFixed(4)}, {parseFloat(addr.longitude).toFixed(4)}
                  </p>
                  <div className="d-flex gap-2 flex-wrap">
                    {!addr.default && (
                      <button
                        className="btn btn-outline-primary btn-sm"
                        onClick={() => handleSetDefault(addr.id)}
                      >
                        Set default
                      </button>
                    )}
                    <button
                      className="btn btn-outline-secondary btn-sm"
                      onClick={() => handleEdit(addr)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-outline-danger btn-sm"
                      onClick={() => handleDelete(addr.id)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit form */}
      <div className="card border-0 shadow-sm">
        <div className="card-header bg-white">
          <h6 className="mb-0">{editingId ? "Edit address" : "Add new address"}</h6>
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="row g-3">

              <div className="col-md-4">
                <label className="form-label small fw-semibold">Label</label>
                <select
                  className="form-select"
                  name="label"
                  value={form.label}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select label</option>
                  <option value="HOME">Home</option>
                  <option value="OFFICE">Office</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>

              <div className="col-md-8">
                <label className="form-label small fw-semibold">Address</label>
                <div className="input-group">
                  <input
                    type="text"
                    className="form-control"
                    name="addressLine"
                    value={form.addressLine}
                    onChange={handleChange}
                    placeholder="123 Main St, New York, NY"
                    required
                  />
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={geocodeAddress}
                    disabled={geocoding}
                  >
                    {geocoding
                      ? <span className="spinner-border spinner-border-sm" />
                      : "Find on map"}
                  </button>
                </div>
                <div className="form-text">
                  Type your address then click "Find on map" to auto-fill coordinates.
                </div>
              </div>

              <div className="col-md-5">
                <label className="form-label small fw-semibold">Latitude</label>
                <input
                  type="number"
                  step="any"
                  className="form-control"
                  name="latitude"
                  value={form.latitude}
                  onChange={handleChange}
                  placeholder="Auto-filled"
                  required
                />
              </div>

              <div className="col-md-5">
                <label className="form-label small fw-semibold">Longitude</label>
                <input
                  type="number"
                  step="any"
                  className="form-control"
                  name="longitude"
                  value={form.longitude}
                  onChange={handleChange}
                  placeholder="Auto-filled"
                  required
                />
              </div>

              <div className="col-md-2 d-flex align-items-end">
                <button
                  type="button"
                  className="btn btn-outline-secondary w-100"
                  onClick={useMyLocation}
                  disabled={locating}
                >
                  {locating
                    ? <span className="spinner-border spinner-border-sm" />
                    : "Use GPS"}
                </button>
              </div>

              <div className="col-12">
                <div className="form-check">
                  <input
                    type="checkbox"
                    className="form-check-input"
                    id="isDefault"
                    name="isDefault"
                    checked={form.isDefault}
                    onChange={handleChange}
                  />
                  <label className="form-check-label small" htmlFor="isDefault">
                    Set as default address
                  </label>
                </div>
              </div>

              <div className="col-12 d-flex gap-2">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? (
                    <><span className="spinner-border spinner-border-sm me-2" />Saving...</>
                  ) : editingId ? "Update address" : "Save address"}
                </button>
                {editingId && (
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={handleCancel}
                  >
                    Cancel
                  </button>
                )}
              </div>

            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ManageAddresses;