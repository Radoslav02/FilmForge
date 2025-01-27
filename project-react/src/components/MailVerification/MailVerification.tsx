import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { toast } from "react-toastify";

export default function VerifyEmail() {
  const navigate = useNavigate();
  const location = useLocation();
  
  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");
  
    if (token) {
      fetch(`${import.meta.env.VITE_APP_API_URL}/api/users/verify-email?token=${token}`, {
        method: "GET", 
      })
        .then((response) => {
          if (response.ok) {
            toast.success("Email verified successfully!");
            navigate("/login");
          } else {
            toast.error("Invalid token or token expired.");
          }
        })
        .catch((error) => {
          console.error("Error during email verification:", error);
          toast.error("Error occurred. Please try again.");
        });
    }
  }, [location.search, navigate]);
  
  

  return <div>Verifying email...</div>;
}
