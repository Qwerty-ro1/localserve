import { useState, useEffect } from "react";
import {
  getProviderBookings, acceptBooking,
  rejectBooking, completeBooking
} from "../api/bookingApi";
import BookingCard from "../components/BookingCard";

const STATUS_FILTERS = ["ALL", "REQUESTED", "ACCEPTED", "COMPLETED", "REJECTED"];

const IncomingBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [error, setError] = useState(null);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 9;

  useEffect(() => { fetchBookings(0); }, []);

  useEffect(() => {
    setFiltered(
      statusFilter === "ALL"
        ? bookings
        : bookings.filter(b => b.status === statusFilter)
    );
  }, [statusFilter, bookings]);

  const fetchBookings = async (page) => {
    setLoading(true);
    setError(null);
    try {
      const res = await getProviderBookings(page, pageSize);
      if (res.success) {
        setBookings(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
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

  const handleAction = async (action, bookingId) => {
    setActionLoading(bookingId);
    setError(null);
    try {
      const actions = { accept: acceptBooking, reject: rejectBooking, complete: completeBooking };
      await actions[action](bookingId);
      await fetchBookings(currentPage);
    } catch (err) {
      setError(err.response?.data?.message || "Action failed");
    } finally {
      setActionLoading(null);
    }
  };

  const pendingCount = bookings.filter(b => b.status === "REQUESTED").length;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h5 className="fw-bold mb-0">
            Incoming bookings
            {pendingCount > 0 && (
              <span className="badge bg-warning text-dark ms-2">{pendingCount} pending</span>
            )}
          </h5>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger py-2 small alert-dismissible">
          {error}
          <button type="button" className="btn-close" onClick={() => setError(null)} />
        </div>
      )}

      {/* Status filter tabs */}
      <div className="d-flex gap-2 flex-wrap mb-4">
        {STATUS_FILTERS.map(s => (
          <button
            key={s}
            className={`btn btn-sm ${statusFilter === s ? "btn-primary" : "btn-outline-secondary"}`}
            onClick={() => setStatusFilter(s)}
          >
            {s === "ALL" ? "All" : s.charAt(0) + s.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-5">
          <p className="text-muted">
            {statusFilter === "ALL"
              ? "No bookings yet."
              : `No ${statusFilter.toLowerCase()} bookings.`}
          </p>
        </div>
      ) : (
        <>
          <div className="row g-3 mb-4">
            {filtered.map(booking => (
              <div key={booking.id} className="col-md-6 col-lg-4">
                <BookingCard
                  booking={booking}
                  isProviderView={true}
                  onAccept={(id) => handleAction("accept", id)}
                  onReject={(id) => handleAction("reject", id)}
                  onComplete={(id) => handleAction("complete", id)}
                />
              </div>
            ))}
          </div>

          {totalPages > 1 && statusFilter === "ALL" && (
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

export default IncomingBookings;