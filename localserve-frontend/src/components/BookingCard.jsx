const STATUS_MAP = {
  REQUESTED:  { badge: "bg-warning text-dark", label: "Requested"  },
  ACCEPTED:   { badge: "bg-success",            label: "Accepted"   },
  REJECTED:   { badge: "bg-danger",             label: "Rejected"   },
  COMPLETED:  { badge: "bg-secondary",          label: "Completed"  }
};

const formatLabel = (label) => {
  const map = { HOME: "Home", OFFICE: "Office", OTHER: "Other" };
  return map[label] || label;
};

const formatDateTime = (dt) => {
  if (!dt) return "N/A";
  return new Date(dt).toLocaleString("en-IN", {
    day: "numeric", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit"
  });
};

const BookingCard = ({ booking, onAccept, onReject, onComplete, isProviderView }) => {
  const status = STATUS_MAP[booking.status] || { badge: "bg-secondary", label: booking.status };

  return (
    <div className="card border-0 shadow-sm h-100">
      <div className="card-body p-3">

        {/* Header row */}
        <div className="d-flex justify-content-between align-items-start mb-3">
          <div>
            <h6 className="fw-bold mb-0">
              {isProviderView ? booking.userName : booking.providerName}
            </h6>
            <small className="text-muted">{booking.serviceName}</small>
          </div>
          <span className={`badge ${status.badge}`}>{status.label}</span>
        </div>

        {/* Booking time */}
        <p className="small mb-2">
          <span className="text-muted">Scheduled: </span>
          <span className="fw-semibold">{formatDateTime(booking.bookingTime)}</span>
        </p>

        {/* Service address — shown to BOTH user and provider */}
        {booking.serviceAddress ? (
          <div className="bg-light rounded p-2 mb-2 small">
            <p className="mb-1 text-muted" style={{ fontSize: "11px" }}>
              {isProviderView ? "Go to" : "Service at"}
            </p>
            <p className="mb-0 fw-semibold">
              {formatLabel(booking.serviceAddress.label)} — {booking.serviceAddress.addressLine}
            </p>
          </div>
        ) : (
          <div className="bg-light rounded p-2 mb-2 small">
            <p className="mb-0 text-muted" style={{ fontSize: "11px" }}>
              No address provided
            </p>
          </div>
        )}

        {/* User contact — provider view only */}
        {isProviderView && booking.userPhone && (
          <p className="small mb-2">
            <span className="text-muted">Contact: </span>
            <span className="fw-semibold">{booking.userPhone}</span>
          </p>
        )}

        {/* Provider actions — provider view only */}
        {isProviderView && (
          <div className="d-flex gap-2 mt-3 flex-wrap">
            {booking.status === "REQUESTED" && (
              <>
                <button
                  className="btn btn-success btn-sm"
                  onClick={() => onAccept(booking.id)}
                >
                  Accept
                </button>
                <button
                  className="btn btn-outline-danger btn-sm"
                  onClick={() => onReject(booking.id)}
                >
                  Reject
                </button>
              </>
            )}
            {booking.status === "ACCEPTED" && (
              <button
                className="btn btn-outline-secondary btn-sm"
                onClick={() => onComplete(booking.id)}
              >
                Mark complete
              </button>
            )}
          </div>
        )}

      </div>
    </div>
  );
};

export default BookingCard;