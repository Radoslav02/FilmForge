import { useNavigate } from "react-router-dom";
import "./Header.css";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import HomeIcon from "@mui/icons-material/Home";
import AddIcon from "@mui/icons-material/Add";
import UserSearch from "../UserSearch/UserSearch";
import MenuIcon from "@mui/icons-material/Menu";
import PeopleIcon from "@mui/icons-material/People";
import { useState, useRef, useEffect } from "react";
import TextsmsIcon from '@mui/icons-material/Textsms';

export default function Header() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuActive, setMenuActive] = useState(false); // New state for tracking active menu
  const menuRef = useRef<HTMLDivElement | null>(null);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
    setMenuActive(!menuActive); // Toggle the active state
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setMenuOpen(false);
        setMenuActive(false); // Close the menu and reset active state when clicking outside
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div className="header-container">
      <div className="logo-wrapper" onClick={() => navigate("/home")}>
        <h1 className="logo">My IMDb</h1>
      </div>
      <div className="search-wrapper">
        <UserSearch />
      </div>
      <div className="header-buttons-wrapper">
        {user && (
          <div
            className="add-post-button"
            onClick={() => navigate("/add-post")}
          >
            <AddIcon sx={{ fontSize: 35 }} />
            <label>Add post</label>
          </div>
        )}
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
        <div className={`menu-wrapper ${menuActive ? 'active' : ''}`} onClick={toggleMenu} ref={menuRef}>
          <MenuIcon sx={{ fontSize: 30 }}  />
          <label>Menu</label>
          {menuOpen && (
            <div className="menu-dropdown">
              <div
                className="menu-item"
                onClick={(e) => {
                  e.stopPropagation();
                  !user ? navigate("/login") : navigate("/profile");
                  setMenuOpen(false);
                  setMenuActive(false); // Close menu and reset active state
                }}
              >
                <AccountCircleOutlinedIcon sx={{ fontSize: 30 }} />
                {!user ? (
                  <label className="log-label">Log in</label>
                ) : (
                  <label>Profile</label>
                )}
              </div>
              <div
                className="menu-item"
                onClick={(e) => {
                  e.stopPropagation();
                  navigate("/friend-request");
                  setMenuOpen(false);
                  setMenuActive(false); // Close menu and reset active state
                }}
              >
                <PeopleIcon sx={{ fontSize: 30 }} />
                <label>Friend Requests</label>
              </div>
              <div className="menu-item">
                <TextsmsIcon />
                <label>Messages</label>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
