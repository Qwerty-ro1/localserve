import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../api/authApi";

const Register = () => {
  const [formData, setFormData] = useState({ name: "", email: "", password: "" });
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const res = await register(formData);
      if (res.success) {
        alert(res.message); // "User registered successfully"
        navigate("/login");
      }
    } catch (err) {
      alert(err.response?.data?.message || "Registration failed");
    }
  };

  return (
    <form onSubmit={handleRegister}>
      <input type="text" placeholder="Name" onChange={e => setFormData({...formData, name: e.target.value})} />
      <input type="email" placeholder="Email" onChange={e => setFormData({...formData, email: e.target.value})} />
      <input type="password" placeholder="Password" onChange={e => setFormData({...formData, password: e.target.value})} />
      <button type="submit">Sign Up</button>
    </form>
  );
};

export default Register;

