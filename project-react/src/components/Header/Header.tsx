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
import TextsmsIcon from "@mui/icons-material/Textsms";
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import SearchIcon from "@mui/icons-material/Search";
import Diversity3Icon from "@mui/icons-material/Diversity3";
import GradeIcon from "@mui/icons-material/Grade";
import RecommendIcon from "@mui/icons-material/Recommend";

export default function Header() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuActive, setMenuActive] = useState(false);
  const menuRef = useRef<HTMLDivElement | null>(null);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
    setMenuActive(!menuActive);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setMenuOpen(false);
        setMenuActive(false);
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
        <h1 className="logo">FilmForge</h1>
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
        <div
          className={`menu-wrapper ${menuActive ? "active" : ""}`}
          onClick={toggleMenu}
          ref={menuRef}
        >
          <MenuIcon sx={{ fontSize: 30 }} />
          <label>Menu</label>
          {menuOpen && (
            <div className="menu-dropdown">
              <div
                className="menu-item"
                onClick={(e) => {
                  e.stopPropagation();
                  !user ? navigate("/login") : navigate("/profile");
                  setMenuOpen(false);
                  setMenuActive(false);
                }}
              >
                <AccountCircleOutlinedIcon sx={{ fontSize: 30 }} />
                {!user ? (
                  <label className="log-label">Log in</label>
                ) : (
                  <label>Profile</label>
                )}
              </div>
              {user && (
                <>
                  <div
                    className="menu-item"
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate("/friend-request");
                      setMenuOpen(false);
                      setMenuActive(false);
                    }}
                  >
                    <PeopleIcon sx={{ fontSize: 30 }} />
                    <label>Friend Requests</label>
                  </div>
                  <div
                    onClick={() => navigate("/messages")}
                    className="menu-item"
                  >
                    <TextsmsIcon />
                    <label>Messages</label>
                  </div>
                  <div
                    onClick={() => navigate("/friends")}
                    className="menu-item"
                  >
                    <Diversity3Icon />
                    <label>Friends</label>
                  </div>
                  <div
                    onClick={() => navigate("/recommendations")}
                    className="menu-item"
                  >
                    <RecommendIcon />
                    <label>Recommendations</label>
                  </div>
                  <div
                    onClick={() => navigate("/favorites")}
                    className="menu-item"
                  >
                    <GradeIcon />
                    <label>Favorites</label>
                  </div>
                </>
              )}
              {user?.isAdmin === "admin" && (
                <div
                  onClick={() => navigate("/admin-panel")}
                  className="menu-item"
                >
                  <AdminPanelSettingsIcon />
                  <label>Admin</label>
                </div>
              )}
              <div className="menu-item" onClick={() => navigate("/explore")}>
                <SearchIcon />
                <label>Explore</label>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
