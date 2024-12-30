import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";

export default function VerifyEmail() {
  const navigate = useNavigate();
  const location = useLocation();
  
  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");
  
    if (token) {
      fetch(`http://localhost:8080/api/users/verify-email?token=${token}`, {
        method: "GET", 
      })
        .then((response) => {
          if (response.ok) {
            alert("Email verified successfully!");
            navigate("/login");
          } else {
            alert("Invalid token or token expired.");
          }
        })
        .catch((error) => {
          console.error("Error during email verification:", error);
          alert("Error occurred. Please try again.");
        });
    }
  }, [location.search, navigate]);
  
  

  return <div>Verifying email...</div>;
}
