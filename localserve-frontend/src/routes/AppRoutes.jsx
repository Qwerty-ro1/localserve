import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Layout from "../components/Layout";
import PublicRoute from "./PublicRoute";
import PrivateRoute from "./PrivateRoute";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Dashboard from "../pages/Dashboard";
import Providers from "../pages/Providers";
import ManageAddresses from "../pages/ManageAddresses";
import BecomeProvider from "../pages/BecomeProvider";
import EditProviderProfile from "../pages/EditProviderProfile";

// Phase 2 — uncomment when built
import ProviderDetail from "../pages/ProviderDetail";
import MyBookings from "../pages/MyBookings";

// Phase 4 — uncomment when built
import ProviderDashboard from "../pages/ProviderDashboard";
import IncomingBookings from "../pages/IncomingBookings";

const ProviderRoute = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role !== "PROVIDER") return <Navigate to="/dashboard" replace />;
  return children;
};

// blocks PROVIDER from accessing USER-only pages like BecomeProvider
const UserOnlyRoute = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role === "PROVIDER") return <Navigate to="/provider/dashboard" replace />;
  return children;
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<Layout />}>

        {/* Public */}
        <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

        {/* Any authenticated user */}
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/addresses" element={<PrivateRoute><ManageAddresses /></PrivateRoute>} />

        {/* USER only */}
        <Route path="/providers" element={<UserOnlyRoute><Providers /></UserOnlyRoute>} />
        <Route path="/become-provider" element={<UserOnlyRoute><BecomeProvider /></UserOnlyRoute>} />
        <Route path="/providers/:id" element={<UserOnlyRoute><ProviderDetail /></UserOnlyRoute>} />
        <Route path="/bookings/my" element={<UserOnlyRoute><MyBookings /></UserOnlyRoute>} />

        {/* PROVIDER only */}
        <Route path="/provider/profile/edit" element={<ProviderRoute><EditProviderProfile /></ProviderRoute>} />
        <Route path="/provider/dashboard" element={<ProviderRoute><ProviderDashboard /></ProviderRoute>} />
        <Route path="/provider/bookings" element={<ProviderRoute><IncomingBookings /></ProviderRoute>} />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/login" replace />} />

      </Route>
    </Routes>
  );
};

export default AppRoutes;