import { useEffect } from "react";
import { useLocation } from "react-router-dom";

const BackButtonWarning = () => {
  const location = useLocation();

  useEffect(() => {
    const handleBeforeUnload = (e) => {
      // Show browser's native "unsaved changes" warning
      e.preventDefault();
      e.returnValue = "You will lose your current session and need to log in again. Are you sure you want to leave?";
      return e.returnValue;
    };

    // Add the event listener
    window.addEventListener("beforeunload", handleBeforeUnload);

    // Cleanup function
    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, []);

  // Also handle React Router navigation
  useEffect(() => {
    const handlePopState = (e) => {
      const confirmLeave = window.confirm(
        "You will lose your current session and need to log in again. Are you sure you want to go back?"
      );
      if (!confirmLeave) {
        // Push the current location back to prevent navigation
        window.history.pushState(null, "", location.pathname);
      }
    };

    window.addEventListener("popstate", handlePopState);

    return () => {
      window.removeEventListener("popstate", handlePopState);
    };
  }, [location.pathname]);

  return null; // This component doesn't render anything
};

export default BackButtonWarning;

