import { useState } from "react";
import { useNavigate } from "react-router-dom";

import "./Register.css";
import { toast } from "react-toastify";

export default function Register() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmationPassword: "",
    city: "",
    street: "",
    number: "",
    username: "",
    country: "",
  });

  const navigate = useNavigate();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (formData.password !== formData.confirmationPassword) {
      toast.error("Passwords do not match");
      return;
    }

    try {
      const response = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/users/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      console.log(formData)

      if (response.ok) {
        toast.success(
          "Registration successful. Please check your email to verify your account."
        );
        navigate("/check-email");
      } else {
        const errorData = await response.json();
        toast.error(errorData.message || "Failed to register!");
      }
    } catch (error) {
      console.error("Error during registration:", error);
      toast.error("An error occurred. Please try again later.");
    }
  };

  return (
    <div className="register-page-container">
      <div className="register-page-title">
        <h1>Registration</h1>
      </div>

      <form className="register-form" onSubmit={handleSubmit}>
        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>First name:</label>
            <input
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              type="text"
            />
          </div>
          <div className="register-form-wrapper">
            <label>Last name:</label>
            <input
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
              type="text"
            />
          </div>
        </div>

        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>E-mail:</label>
            <input
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              type="email"
            />
          </div>
          <div className="register-form-wrapper">
            <label>Password:</label>
            <input
              name="password"
              type="password"
              onChange={handleInputChange}
              value={formData.password}
            />
          </div>
        </div>

        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>Password confirmation:</label>
            <input
              name="confirmationPassword"
              value={formData.confirmationPassword}
              onChange={handleInputChange}
              type="password"
            />
          </div>
          <div className="register-form-wrapper">
            <label>City:</label>
            <input
              name="city"
              type="text"
              onChange={handleInputChange}
              value={formData.city}
            />
          </div>
        </div>

        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>Street:</label>
            <input
              name="street"
              value={formData.street}
              onChange={handleInputChange}
              type="text"
            />
          </div>
          <div className="register-form-wrapper">
            <label>Number:</label>
            <input
              name="number"
              type="text"
              onChange={handleInputChange}
              value={formData.number}
            />
          </div>
        </div>

        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>Country:</label>
            <input
              name="country"
              value={formData.country}
              onChange={handleInputChange}
              type="text"
            />
          </div>
          <div className="register-form-wrapper">
            <label>Username:</label>
            <input
              name="username"
              type="text"
              onChange={handleInputChange}
              value={formData.username}
            />
          </div>
        </div>

        <div className="register-form-container">
          <button
            className="back-button"
            type="button"
            onClick={() => navigate(-1)}
          >
            Back
          </button>
          <button className="register-button" type="submit">
            Register
          </button>
        </div>
      </form>
    </div>
  );
}