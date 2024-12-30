import { useNavigate } from "react-router-dom";
import "./Header.css";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import HomeIcon from "@mui/icons-material/Home";
import AddIcon from "@mui/icons-material/Add";

export default function Header() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);

  return (
    <div className="header-container">
      <div className="logo-wrapper" onClick={() => navigate("/home")}>
        <h1 className="logo">My IMDb</h1>
      </div>
      <div className="header-buttons-wrapper">
        {user && 
        <div className="add-post-button" onClick={() => navigate("/add-post")}>
          <AddIcon sx={{ fontSize: 35 }} />
          <label>Add post</label>
        </div>
        }
        <div
          className="home-button-wrapper"
          onClick={(e) => {
            e.stopPropagation();
            navigate("/home");
          }}
        >
          <HomeIcon sx={{ fontSize: 30 }} />
          <label>Home</label>
        </div>
        <div
          className="login-wrapper"
          onClick={(e) => {
            e.stopPropagation();
            !user ? navigate("/login") : navigate("/profile");
          }}
        >
          <AccountCircleOutlinedIcon sx={{ fontSize: 30 }} />
          {!user ? (
            <label className="log-label">Log in</label>
          ) : (
            <label>Profile</label>
          )}
        </div>
      </div>
    </div>
  );
}
