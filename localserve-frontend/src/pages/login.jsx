import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/authApi";
import { useAuth } from "../context/AuthContext";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const { handleLogin } = useAuth();
  const navigate = useNavigate();

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await login({ email, password });
      // response.data is the raw JWT string from Backend
      if (response.success) {
        handleLogin(response.data);
        navigate("/dashboard");
      }
    } catch (err) {
      alert(err.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      <form onSubmit={handleFormSubmit}>
        <input type="email" placeholder="Email" required onChange={e => setEmail(e.target.value)} />
        <input type="password" placeholder="Password" required onChange={e => setPassword(e.target.value)} />
        <button type="submit" disabled={loading}>
          {loading ? "Verifying..." : "Login"}
        </button>
      </form>
    </div>
  );
};

export default Login;

