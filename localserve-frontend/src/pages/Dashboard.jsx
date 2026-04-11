import { useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { FaUser, FaCrown, FaBuilding, FaSearch, FaCalendarAlt, FaStore, FaMapMarkerAlt, FaStar } from "react-icons/fa";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isProvider = user?.role === "PROVIDER";

  useEffect(() => {
    if (user?.role === "PROVIDER") {
      navigate("/provider/dashboard", { replace: true });
    }
  }, [user]);

  const getRoleBadge = (role) => {
    const map = {
      USER: "badge bg-primary",
      ADMIN: "badge bg-danger",
      PROVIDER: "badge bg-success"
    };
    return map[role] || "badge bg-secondary";
  };

  const getRoleIcon = (role) => {
    const map = {
      USER: <FaUser />,
      ADMIN: <FaCrown />,
      PROVIDER: <FaBuilding />
    };
    return map[role] || <FaUser />;
  };

  return (
    <div>
      <div className="bg-primary text-white py-4">
        <div className="container">
          <h5 className="fw-bold mb-0">
            Welcome back, {user?.name || "User"}!
          </h5>
          <small className="opacity-75">
            {isProvider
              ? "Manage your services and incoming bookings"
              : "Find and book local services near you"}
          </small>
        </div>
      </div>

      <div className="container py-4">
        <div className="row g-4">

          <div className="col-lg-8">
            <div className="card border-0 shadow-sm">
              <div className="card-body p-4">
                <div className="d-flex align-items-center mb-4">
                  <div
                    className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center fw-bold flex-shrink-0"
                    style={{ width: 52, height: 52, fontSize: 20 }}
                  >
                    {user?.name?.charAt(0)?.toUpperCase() || "U"}
                  </div>
                  <div className="ms-3 flex-grow-1">
                    <h6 className="mb-0">{user?.name}</h6>
                    <small className="text-muted">{user?.email}</small>
                  </div>
                  <span className={getRoleBadge(user?.role)}>
                    {getRoleIcon(user?.role)}{" "}{user?.role}
                  </span>
                </div>

                <hr />

                <div className="row">
                  <div className="col-sm-6 mb-3">
                    <p className="text-muted small mb-1">Email</p>
                    <p className="fw-semibold mb-0">{user?.email}</p>
                  </div>
                  <div className="col-sm-6 mb-3">
                    <p className="text-muted small mb-1">Account type</p>
                    <span className={getRoleBadge(user?.role)}>
                      {user?.role}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-4">
            <div className="card border-0 shadow-sm">
              <div className="card-header bg-white">
                <h6 className="mb-0">Quick actions</h6>
              </div>
              <div className="card-body d-grid gap-2">
                {!isProvider ? (
                  <>
                    <button
                      className="btn btn-outline-primary btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/providers")}
                    >
                      <FaSearch /> Browse services
                    </button>
                    <button
                      className="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/bookings/my")}
                    >
                      <FaCalendarAlt /> My bookings
                    </button>
                    <button
                      className="btn btn-outline-success btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/become-provider")}
                    >
                      <FaStore /> Become a provider
                    </button>
                    <button
                      className="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/addresses")}
                    >
                      <FaMapMarkerAlt /> Manage addresses
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      className="btn btn-outline-primary btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/provider/dashboard")}
                    >
                      <FaBuilding /> Provider dashboard
                    </button>
                    <button
                      className="btn btn-outline-secondary btn-sm d-flex align-items-center gap-2"
                      onClick={() => navigate("/provider/bookings")}
                    >
                      <FaCalendarAlt /> Incoming bookings
                    </button>
                  </>
                )}
              </div>
            </div>

            <div className="card border-0 shadow-sm mt-3 text-center">
              <div className="card-body py-3">
                <span className="badge bg-success mb-2">Active</span>
                <p className="small text-muted mb-0">All systems operational</p>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default Dashboard;