import React, { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
const AuthContext = createContext(null);
export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState(null);
  // Check if token is expired
  const isTokenExpired = (token) => {
    if (!token) return true;
    try {
      const decoded = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decoded.exp < currentTime;
    } catch (error) {
      return true; // Invalid token
    }
  };
  // Initialize auth state
  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    if (storedToken && !isTokenExpired(storedToken)) {
      try {
        const decoded = jwtDecode(storedToken);
        setToken(storedToken);
        setUser(decoded);
      } catch (e) {
        // Invalid token, clear it
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
      }
    } else if (storedToken) {
      // Token exists but is expired
      localStorage.removeItem("token");
      setToken(null);
      setUser(null);
    }
  }, []);
  // Update user when token changes
  useEffect(() => {
    if (token && !isTokenExpired(token)) {
      try {
        const decoded = jwtDecode(token);
        setUser(decoded);
      } catch (e) {
        handleLogout();
      }
    } else if (token) {
      // Token is expired
      handleLogout();
    }
  }, [token]);
  const handleLogin = (newToken) => {
    if (newToken && !isTokenExpired(newToken)) {
      localStorage.setItem("token", newToken);
      setToken(newToken);
      // User will be set by the useEffect above
    } else {
      console.error("Invalid or expired token provided");
    }
  };
  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
    // Redirect to login
    window.location.href = "/login";
  };
  return (
    <AuthContext.Provider value={{
      token,
      user,
      isAuthenticated: !!token && !isTokenExpired(token),
      handleLogin,
      handleLogout
    }}>
      {children}
    </AuthContext.Provider>
  );
};
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
};
