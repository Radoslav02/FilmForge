
import { useNavigate } from "react-router-dom";
import "./Header.css"
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";

export default function Header() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);

  return (
    <div className="header-container">
      <div className="logo-wrapper" onClick={() => navigate("/home")}>
        <h1 className="logo">My IMDb</h1>
      </div>
        <div className="login-wrapper" onClick={!user ? () => navigate("/login") : () => navigate("/profile")  }>
          <AccountCircleOutlinedIcon sx={{fontSize: 30}} />
          {!user ? <label>Log in</label> : <label>Profil</label>}
        </div>
      
    </div>
  )
}
