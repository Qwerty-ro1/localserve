import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../api/authApi";

const Register = () => {
  const [formData, setFormData] = useState({ name: "", email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  // moved inside component so it can access formData
  const validate = () => {
    if (formData.name.trim().length < 2) return "Name must be at least 2 characters";
    if (!/\S+@\S+\.\S+/.test(formData.email)) return "Enter a valid email";
    if (formData.password.length < 8) return "Password must be at least 8 characters";
    if (!/[A-Za-z]/.test(formData.password)) return "Password must contain at least one letter";
    if (!/[0-9]/.test(formData.password)) return "Password must contain at least one number";
    return null;
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError(null);

    // run frontend validation first — catches issues before hitting backend
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    try {
      const res = await register(formData);
      if (res.success) {
        navigate("/login", { replace: true });
      } else {
        setError(res.message);
      }
    } catch (err) {
      const responseData = err.response?.data;
      if (responseData?.data && typeof responseData.data === "object") {
        // field-level validation errors from backend e.g. { password: "too short" }
        setError(Object.values(responseData.data).join(", "));
      } else {
        setError(responseData?.message || "Registration failed");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-sm-8 col-md-5 col-lg-4">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <h5 className="fw-bold mb-1 text-center">Create account</h5>
              <p className="text-muted text-center small mb-4">Join LocalServe today</p>

              {error && (
                <div className="alert alert-danger py-2 small mb-3">{error}</div>
              )}

              <form onSubmit={handleRegister}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Full name</label>
                  <input
                    type="text"
                    className="form-control"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="John Doe"
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="you@example.com"
                    required
                  />
                </div>
                <div className="mb-4">
                  <label className="form-label small fw-semibold">Password</label>
                  <input
                    type="password"
                    className="form-control"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Min. 8 characters"
                    required
                  />
                  <div className="form-text">
                    At least 8 characters with a letter and a number.
                  </div>
                </div>
                <button
                  type="submit"
                  className="btn btn-primary w-100"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" />
                      Creating account...
                    </>
                  ) : "Create account"}
                </button>
              </form>

              <p className="text-center text-muted small mt-3 mb-0">
                Already have an account? <Link to="/login">Sign in</Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;