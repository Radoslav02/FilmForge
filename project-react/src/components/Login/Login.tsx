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
      try{
        const response = await fetch("http://localhost:8080/api/users/authenticate", {
          method : "POST",
          headers: {
            "Content-Type":"application/json",
          },
          body:JSON.stringify({email, password}),
        });
        if(!response.ok){
          throw new Error("Invalid email or password");
        }

        const userData = await response.json();

        dispatch(login(userData));

        navigate("/profile");
      }catch(error){
        console.error("Login failed: ", error);
        alert("Invalid email or password");
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
            type="text"
          />
        </div>

        <div className="register-form">
          <span onClick={() => navigate("/register")}>
            <p className="dont">Dont have account?</p>
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
