import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createBooking } from "../api/bookingApi";
import { getAddresses } from "../api/addressApi";

const CreateBooking = ({ provider, onClose }) => {
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [form, setForm] = useState({
    serviceOfferingId: "",
    serviceAddressId: "",
    bookingTime: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [addressLoading, setAddressLoading] = useState(true);

  // min datetime — now + 1 hour, rounded to next hour
  const getMinDateTime = () => {
    const now = new Date();
    now.setHours(now.getHours() + 1, 0, 0, 0);
    return now.toISOString().slice(0, 16);
  };

  useEffect(() => {
    getAddresses()
      .then(res => {
        if (res.success) {
          setAddresses(res.data);
          const def = res.data.find(a => a.isDefault);
          if (def) setForm(f => ({ ...f, serviceAddressId: def.id }));
        }
      })
      .finally(() => setAddressLoading(false));
  }, []);

  const validate = () => {
    if (!form.serviceOfferingId) return "Select a service";
    if (!form.serviceAddressId) return "Select a service address";
    if (!form.bookingTime) return "Select a date and time";
    if (new Date(form.bookingTime) <= new Date()) return "Booking time must be in the future";
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
        serviceOfferingId: parseInt(form.serviceOfferingId),
        serviceAddressId: parseInt(form.serviceAddressId),
        bookingTime: new Date(form.bookingTime).toISOString()
      };
      const res = await createBooking(payload);
      if (res.success) {
        navigate("/bookings/my");
      } else {
        setError(res.message);
      }
    } catch (err) {
      const data = err.response?.data;
      if (data?.data && typeof data.data === "object") {
        setError(Object.values(data.data).join(", "));
      } else {
        setError(data?.message || "Booking failed");
      }
    } finally {
      setLoading(false);
    }
  };

  const formatLabel = (label) => {
    const map = { HOME: "Home", OFFICE: "Office", OTHER: "Other" };
    return map[label] || label;
  };

  return (
    <>
      {/* Backdrop */}
      <div
        className="modal-backdrop fade show"
        style={{ zIndex: 1040 }}
        onClick={onClose}
      />

      {/* Modal */}
      <div
        className="modal fade show d-block"
        style={{ zIndex: 1050 }}
        tabIndex="-1"
      >
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content border-0 shadow">

            <div className="modal-header border-0 pb-0">
              <div>
                <h6 className="modal-title fw-bold mb-0">Book a service</h6>
                <small className="text-muted">{provider.businessName}</small>
              </div>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
              />
            </div>

            <div className="modal-body pt-3">
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

              <form onSubmit={handleSubmit}>

                {/* Service selection */}
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Service</label>
                  <select
                    className="form-select"
                    value={form.serviceOfferingId}
                    onChange={e => setForm(f => ({ ...f, serviceOfferingId: e.target.value }))}
                    required
                  >
                    <option value="">Select a service</option>
                    {provider.offerings.map(o => (
                      <option key={o.id} value={o.id}>
                        {o.categoryName}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Address selection */}
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Service address</label>
                  {addressLoading ? (
                    <div className="spinner-border spinner-border-sm text-primary" />
                  ) : addresses.length === 0 ? (
                    <div className="alert alert-warning py-2 small mb-0">
                      No saved addresses.{" "}
                      <a href="/addresses" className="alert-link">Add one first</a>.
                    </div>
                  ) : (
                    <select
                      className="form-select"
                      value={form.serviceAddressId}
                      onChange={e => setForm(f => ({ ...f, serviceAddressId: e.target.value }))}
                      required
                    >
                      <option value="">Select address</option>
                      {addresses.map(a => (
                        <option key={a.id} value={a.id}>
                          {formatLabel(a.label)} — {a.addressLine}
                        </option>
                      ))}
                    </select>
                  )}
                </div>

                {/* Date and time */}
                <div className="mb-4">
                  <label className="form-label small fw-semibold">Date and time</label>
                  <input
                    type="datetime-local"
                    className="form-control"
                    value={form.bookingTime}
                    min={getMinDateTime()}
                    onChange={e => setForm(f => ({ ...f, bookingTime: e.target.value }))}
                    required
                  />
                  <div className="form-text">
                    Must be at least 1 hour from now.
                  </div>
                </div>

                <div className="d-flex gap-2">
                  <button
                    type="submit"
                    className="btn btn-primary flex-grow-1"
                    disabled={loading || addresses.length === 0}
                  >
                    {loading ? (
                      <><span className="spinner-border spinner-border-sm me-2" />Booking...</>
                    ) : "Confirm booking"}
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={onClose}
                  >
                    Cancel
                  </button>
                </div>

              </form>
            </div>

          </div>
        </div>
      </div>
    </>
  );
};

export default CreateBooking;