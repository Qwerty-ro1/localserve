import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Header = () => {
  const { user, isAuthenticated, handleLogout } = useAuth();
  const isProvider = user?.role === "PROVIDER";

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">LocalServe</Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNav"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="mainNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            {isAuthenticated && !isProvider && (
              <>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/providers">Browse</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/bookings/my">My Bookings</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/become-provider">Become a Provider</NavLink>
                </li>
              </>
            )}
            {isAuthenticated && isProvider && (
              <>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/provider/dashboard">Dashboard</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/provider/bookings">Bookings</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/provider/profile/edit">My Profile</NavLink>
                </li>
              </>
            )}
          </ul>

          <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center">
            {isAuthenticated ? (
              <>
                <li className="nav-item me-2">
                  <span className="navbar-text text-white-50 small">
                    {user?.name}
                    <span className={`badge ms-2 ${isProvider ? "bg-success" : "bg-light text-primary"}`}>
                      {user?.role}
                    </span>
                  </span>
                </li>
                <li className="nav-item">
                  <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                    Sign Out
                  </button>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/login">Login</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/addresses">Addresses</NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="btn btn-outline-light btn-sm ms-2" to="/register">Register</NavLink>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Header;