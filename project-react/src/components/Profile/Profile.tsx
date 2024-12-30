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
        username: editedUser.username,
        city: editedUser.city,
        country: editedUser.country,
        street: editedUser.street,
        number: editedUser.number,
      };

      if (password) {
        updatedUserPayload.password = password;
      }

      console.log("Payload being sent to backend:", updatedUserPayload);

      const response = await fetch(
        "http://localhost:8080/api/users/editProfile",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(updatedUserPayload),
        }
      );

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
    return (
      <div className="profile-page-container">Korisnik nije prijavljen.</div>
    );
  }

  return (
    <div className="profile-page-container">
      <h1>Profil korisnika</h1>
      {!isEditing ? (
        <div className="profile-info-container">
          <div className="profile-info">
            <span className="profile-wrapper">
              <label>
                <strong>Email:</strong>
              </label>
              <label> {user.email}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>First Name:</strong>
              </label>
              <label>{user.firstName || "First name is not available"}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>Last Name:</strong>
              </label>
              <label>{user.lastName || "Last name is not available"}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>Username:</strong>
              </label>
              <label>{user.username || "Username is not available"}</label>
            </span>
          </div>
          <div className="profile-info">
            <span className="profile-wrapper">
              <label>
                <strong>City:</strong>
              </label>
              <label>{user.city || "City is not available"}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>Country:</strong>
              </label>
              <label>{user.country || "Country is not available"}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>Street:</strong>
              </label>
              <label>{user.street || "Street is not available"}</label>
            </span>
            <span className="profile-wrapper">
              <label>
                <strong>Number:</strong>
              </label>
              <label>{user.number || "Number is not available"}</label>
            </span>
          </div>
        </div>
      ) : (
        <div className="profile-edit">
          <div className="edit-container">
            <div className="edit-wrapper">
              <label>Email:</label>
              <input
                type="email"
                value={editedUser?.email || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, email: e.target.value })
                }
                disabled
              />
            </div>
            <div className="edit-wrapper">
              <label>First Name:</label>
              <input
                type="text"
                value={editedUser?.firstName || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, firstName: e.target.value })
                }
              />
            </div>
            <div className="edit-wrapper">
              <label>Last Name:</label>
              <input
                type="text"
                value={editedUser?.lastName || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, lastName: e.target.value })
                }
              />
            </div>
            <div className="edit-wrapper">
              <label>New Password:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Leave empty to keep current password"
              />
            </div>
            <div className="edit-wrapper">
              <label>Username:</label>
              <input
                type="text"
                value={editedUser?.username || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, username: e.target.value })
                }
              />
            </div>
          </div>
          <div className="edit-container">
            <div className="edit-wrapper">
              <label>City:</label>
              <input
                type="text"
                value={editedUser?.city || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, city: e.target.value })
                }
              />
            </div>
            <div className="edit-wrapper">
              <label>Country:</label>
              <input
                type="text"
                value={editedUser?.country || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, country: e.target.value })
                }
              />
            </div>
            <div className="edit-wrapper">
              <label>Street:</label>
              <input
                type="text"
                value={editedUser?.street || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, street: e.target.value })
                }
              />
            </div>
            <div className="edit-wrapper">
              <label>Number:</label>
              <input
                type="text"
                value={editedUser?.number || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser!, number: e.target.value })
                }
              />
            </div>
          </div>
        </div>
      )}
      {error && <p className="error-message">{error}</p>}
      <div className="profile-actions">
        {!isEditing ? (
          <div className="profile-button-wrapper">
            <button className="logout-button" onClick={handleLogOut}>
              Log Out
            </button>
            <button className="edit-on-button" onClick={handleEditToggle}>
              Edit Info
            </button>
          </div>
        ) : (
          <div className="edit-buttons-wrapper">
            <button className="cancel-edit-button" onClick={handleEditToggle} disabled={isLoading}>
              Cancel
            </button>
            <button className="save-edit-button" onClick={handleSave} disabled={isLoading}>
              {isLoading ? "Saving..." : "Save"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
