import { useState } from "react";
import "./Register.css";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) =>{
    const { name, value } = e.target;
    setFormData({...formData, [name]:value})
  }

  const handleSubmit = async(e: React.FormEvent) => {

    e.preventDefault();

    try{
      const response = await fetch("http://localhost:8080/api/users",{
        method: "POST",
        headers: {
          "Content-Type" : "application/json",
        },
        body: JSON.stringify(formData),
      });

      if(response.ok){
        alert("Registration succesful");
        setFormData({firstName: "", lastName: "", email: "", password: "" });
      }
      else{
        alert("Failed to register!")
      }
    }catch(error){
      console.error("Error during registration:", error);
      alert("An error occurred. Please try again later.");
    }
  }

  return (
    <div className="register-page-container">
      <div className="register-page-title">
        <h1>Registration</h1>
      </div>

      <form className="register-form" onSubmit={handleSubmit}>
        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>First name:</label>
            <input name="firstName" value={formData.firstName} onChange={handleInputChange} type="text" />
          </div>
          <div className="register-form-wrapper">
            <label>Last name:</label>
            <input name="lastName" value={formData.lastName} onChange={handleInputChange} type="text" />
          </div>
        </div>

        <div className="register-form-container">
          <div className="register-form-wrapper">
            <label>E-mail:</label>
            <input name="email" value={formData.email} onChange={handleInputChange} type="email" />
          </div>
          <div className="register-form-wrapper">
            <label>Password:</label>
            <input name="password" type="password" onChange={handleInputChange} value={formData.password} />
          </div>
        </div>

        <div className="register-form-container">
          <button className="back-button" type="button"  onClick={() => navigate(-1)}>
            Back
          </button>
          <button className="register-button" type="submit">Register</button>
        </div>
      </form>
    </div>
  );
}
