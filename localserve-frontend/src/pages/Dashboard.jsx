import { useAuth } from "../context/AuthContext";
const Dashboard = () => {
  const { user, handleLogout } = useAuth();
  return (
    <div className="container my-5">
      <div className="row">
        <div className="col-md-8">
          <h1 className="mb-4">Dashboard</h1>
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">Welcome back!</h5>
              <p className="card-text">
                You are logged in as: <strong>{user?.email || "User"}</strong>
              </p>
              <p className="card-text">
                Role: <strong>{user?.role || "Unknown"}</strong>
              </p>
              <div className="mt-4">
                <button
                  className="btn btn-primary me-2"
                  onClick={() => window.location.href = "/providers"}
                >
                  Browse Providers
                </button>
                <button
                  className="btn btn-outline-danger"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">Quick Actions</h5>
            </div>
            <div className="card-body">
              <div className="d-grid gap-2">
                <button
                  className="btn btn-outline-primary"
                  onClick={() => window.location.href = "/providers"}
                >
                  Find Services
                </button>
                <button
                  className="btn btn-outline-secondary"
                  onClick={handleLogout}
                >
                  Sign Out
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Dashboard;
