import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAddresses } from "../api/addressApi";

const AddressSelector = ({ onSelect }) => {
  const [addresses, setAddresses] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    getAddresses()
      .then(res => {
        if (res.success) {
          setAddresses(res.data);
          const def = res.data.find(a => a.isDefault) || res.data[0];
          if (def) {
            setSelected(def);
            onSelect(def);
          }
        }
      })
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    const addr = addresses.find(a => a.id === parseInt(e.target.value));
    setSelected(addr);
    onSelect(addr);
  };

  if (loading) return null;

  if (addresses.length === 0) {
    return (
      <div className="alert alert-warning py-2 d-flex justify-content-between align-items-center">
        <small>No saved addresses. Add one to find nearby providers.</small>
        <button
          className="btn btn-warning btn-sm"
          onClick={() => navigate("/addresses")}
        >
          Add address
        </button>
      </div>
    );
  }

  return (
    <div className="d-flex align-items-center gap-2 mb-3">
      <small className="text-muted text-nowrap">Service at:</small>
      <select
        className="form-select form-select-sm"
        value={selected?.id || ""}
        onChange={handleChange}
      >
        {addresses.map(a => (
          <option key={a.id} value={a.id}>
            {a.label} — {a.addressLine}
          </option>
        ))}
      </select>
      <button
        className="btn btn-outline-secondary btn-sm text-nowrap"
        onClick={() => navigate("/addresses")}
      >
        Manage
      </button>
    </div>
  );
};

export default AddressSelector;