import { useDispatch, useSelector } from "react-redux";
import { useState } from "react";
import "./Profile.css";
import { RootState } from "../Redux/store";
import { login, logout } from "../Redux/authSlice";

export default function Profile() {
  const dispatch = useDispatch();
  const user = useSelector((state: RootState) => state.auth.user); // Get user from Redux

  const [isEditing, setIsEditing] = useState(false); 
  const [editedUser, setEditedUser] = useState(user);
  const [password, setPassword] = useState(""); 
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null); 

  const handleLogOut = () => {
    dispatch(logout()); 
  };

  const handleEditToggle = () => {
    setError(null); 
    setIsEditing((prev) => !prev); 
  };

  const handleSave = async () => {
    if (!editedUser) return;
  
    setIsLoading(true);
    setError(null);
  
    try {
      const token = localStorage.getItem("jwtToken");
      console.log("JWT token:", token);
      const updatedUserPayload: any = {
        email: editedUser.email,
        firstName: editedUser.firstName,
        lastName: editedUser.lastName,
      };
  
      if (password) {
        updatedUserPayload.password = password;
      }
  
      console.log("Payload being sent to backend:", updatedUserPayload);
  
      const response = await fetch("http://localhost:8080/api/users/editProfile", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedUserPayload),
      });
  
      console.log("Response from backend:", response);
  
      if (!response.ok) {
        throw new Error("Failed to update profile");
      }
  
      const updatedUser = await response.json();
      dispatch(login(updatedUser));
      setIsEditing(false);
      setPassword("");
    } catch (err: any) {
      console.error("Error during save:", err);
      setError(err.message || "An error occurred.");
    } finally {
      setIsLoading(false);
    }
  };
  
  
  
  
  if (!user) {
    return <div className="profile-page-container">Korisnik nije prijavljen.</div>;
  }

  return (
    <div className="profile-page-container">
      <h1>Profil korisnika</h1>
      {!isEditing ? (
        <div className="profile-info">
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>First Name:</strong> {user.firstName || "N/A"}</p>
          <p><strong>Last Name:</strong> {user.lastName || "N/A"}</p>
        </div>
      ) : (
        <div className="profile-edit">
          <label>Email:</label>
          <input
            type="email"
            value={editedUser?.email || ""}
            onChange={(e) => setEditedUser({ ...editedUser!, email: e.target.value })}
            disabled
          />
          <label>First Name:</label>
          <input
            type="text"
            value={editedUser?.firstName || ""}
            onChange={(e) => setEditedUser({ ...editedUser!, firstName: e.target.value })}
          />
          <label>Last Name:</label>
          <input
            type="text"
            value={editedUser?.lastName || ""}
            onChange={(e) => setEditedUser({ ...editedUser!, lastName: e.target.value })}
          />
          <label>New Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Leave empty to keep current password"
          />
        </div>
      )}
      {error && <p className="error-message">{error}</p>}
      <div className="profile-actions">
        {!isEditing ? (
          <>
            <button onClick={handleEditToggle}>Edit Info</button>
            <button onClick={handleLogOut}>Log Out</button>
          </>
        ) : (
          <>
            <button onClick={handleSave} disabled={isLoading}>
              {isLoading ? "Saving..." : "Save"}
            </button>
            <button onClick={handleEditToggle} disabled={isLoading}>
              Cancel
            </button>
          </>
        )}
      </div>
    </div>
  );
}
