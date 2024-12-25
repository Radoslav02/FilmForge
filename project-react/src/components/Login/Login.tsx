import { useNavigate } from "react-router-dom";
import "./Login.css";
import { useState } from "react";
import { useDispatch } from "react-redux";
import { login } from "../Redux/authSlice";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      // Logovanje unetih podataka pre slanja na backend
      console.log("Login data being sent:", { email, password });

      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      // Logovanje HTTP statusa odgovora
      console.log("Response status:", response.status);

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || "Invalid email or password");
      }

      const {
        token,
        email: responseEmail,
        firstName,
        lastName,
        id,
        isAdmin,
      } = await response.json();

      // Logovanje odgovora sa servera
      console.log("Response data:", {
        token,
        email: responseEmail,
        firstName,
        lastName,
        id,
        isAdmin,
      });

      // Čuvanje tokena u localStorage
      localStorage.setItem("jwtToken", token);

      // Ažuriranje Redux store-a sa korisničkim podacima
      dispatch(
        login({
          id,
          email: responseEmail, // Koristi email iz odgovora
          isAdmin,
          firstName,
          lastName,
        })
      );

      navigate("/profile");
    } catch (error: any) {
      // Logovanje greške
      console.error("Login failed:", error.message);
      alert(error.message);
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-form-container">
        <div className="login-form">
          <h1>Login</h1>
        </div>

        <div className="login-form">
          <label>E-mail:</label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="login-input"
            type="text"
          />
        </div>

        <div className="login-form">
          <label>Password:</label>
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="login-input"
            type="password"
          />
        </div>

        <div className="register-form">
          <span onClick={() => navigate("/register")}>
            <p className="dont">Don't have an account?</p>
            <p>Create one</p>
          </span>
        </div>
        <div className="login-button-wrapper">
          <button className="login-button" onClick={handleLogin}>
            Log in
          </button>
        </div>
      </div>
    </div>
  );
}
