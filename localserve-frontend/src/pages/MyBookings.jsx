import { useState, useEffect } from "react";
import { getMyBookings } from "../api/bookingApi";
import BookingCard from "../components/BookingCard";

const MyBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 9;

  useEffect(() => { fetchBookings(0); }, []);

  const fetchBookings = async (page) => {
    setLoading(true);
    setError(null);
    try {
      const res = await getMyBookings(page, pageSize);
      if (res.success) {
        setBookings(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
        setCurrentPage(page);
      } else {
        setError("Failed to load bookings");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load bookings");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0">My bookings</h5>
          {!loading && (
            <small className="text-muted">{totalElements} total</small>
          )}
        </div>
      </div>

      {error && (
        <div className="alert alert-danger py-2 small">{error}</div>
      )}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" />
        </div>
      ) : bookings.length === 0 ? (
        <div className="text-center py-5">
          <p className="text-muted mb-3">You have no bookings yet.</p>
          <a href="/providers" className="btn btn-primary btn-sm">
            Browse providers
          </a>
        </div>
      ) : (
        <>
          <div className="row g-3 mb-4">
            {bookings.map(booking => (
              <div key={booking.id} className="col-md-6 col-lg-4">
                <BookingCard
                  booking={booking}
                  isProviderView={false}
                />
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <nav>
              <ul className="pagination justify-content-center pagination-sm">
                <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                  <button
                    className="page-link"
                    onClick={() => fetchBookings(currentPage - 1)}
                  >
                    Previous
                  </button>
                </li>
                {Array.from({ length: totalPages }, (_, i) => (
                  <li key={i} className={`page-item ${i === currentPage ? "active" : ""}`}>
                    <button className="page-link" onClick={() => fetchBookings(i)}>
                      {i + 1}
                    </button>
                  </li>
                ))}
                <li className={`page-item ${currentPage === totalPages - 1 ? "disabled" : ""}`}>
                  <button
                    className="page-link"
                    onClick={() => fetchBookings(currentPage + 1)}
                  >
                    Next
                  </button>
                </li>
              </ul>
              <p className="text-center text-muted small">
                Page {currentPage + 1} of {totalPages}
              </p>
            </nav>
          )}
        </>
      )}
    </div>
  );
};

export default MyBookings;