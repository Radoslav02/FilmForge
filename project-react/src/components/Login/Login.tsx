import { useNavigate } from "react-router-dom";
import "./Login.css";
import { useState } from "react";
import { useDispatch } from "react-redux";
import { login } from "../Redux/authSlice";
import { toast } from "react-toastify";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      console.log("Login data being sent:", { email, password });

      const response = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/users/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

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
        username,
        city,
        country,
        street,
        number,
        id,
        isAdmin,
      } = await response.json();

      console.log("Response data:", {
        token,
        email: responseEmail,
        firstName,
        lastName,
        id,
        isAdmin,
      });

      localStorage.setItem("jwtToken", token);

      dispatch(
        login({
          id,
          email: responseEmail,
          isAdmin,
          firstName,
          lastName,
          username,
          city,
          country,
          street,
          number,
        })
      );

      navigate("/profile");
    } catch (error: any) {
      console.error("Login failed:", error.message);
      toast.error(error.message);
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
            <p className="create-p">Create one!</p>
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
