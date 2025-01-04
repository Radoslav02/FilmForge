import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./UserSearch.css";
import SearchIcon from '@mui/icons-material/Search';

interface User {
  id: number;
  username: string;
}

const UserSearch: React.FC = () => {
  const [query, setQuery] = useState<string>("");
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isDropdownVisible, setIsDropdownVisible] = useState<boolean>(false); 
  const navigate = useNavigate();
  const inputRef = useRef<HTMLDivElement>(null); 

  const handleUserClick = (userId: number) => {
    navigate(`/user/${userId}`);
    setIsDropdownVisible(false);
  };

  const searchUsers = async () => {
    if (!query) {
      setUsers([]);
      return;
    }

    const token = localStorage.getItem("jwtToken");

    if (!token) {
      console.error("No token found, user not authenticated");
      setError("No token found. Please log in.");
      return;
    }

    setError(null);

    try {
      const response = await fetch(
        `http://localhost:8080/api/users/search?username=${query}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch users");
      }

      const data = await response.json();
      console.log("Users found:", data);

      if (Array.isArray(data)) {
        setUsers(data);
        setIsDropdownVisible(true); 
      } else {
        setUsers([]);
      }
    } catch (err: any) {
      console.error("Error fetching users:", err);
      setError(err.message || "An error occurred while fetching users");
    }
  };

  useEffect(() => {
    searchUsers();
  }, [query]);

 
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (inputRef.current && !inputRef.current.contains(event.target as Node)) {
        setIsDropdownVisible(false); 
      }
    };

    document.addEventListener("click", handleClickOutside);
    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, []);

  return (
    <div className="search-container" ref={inputRef}>
      <input
        type="text"
        placeholder="Search users by username..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onFocus={() => setIsDropdownVisible(true)} 
        className="search-input"
      />
      <SearchIcon className="search-icon" />
      {error && <p className="error-message">{error}</p>}
      {isDropdownVisible && query && (
        <ul className="search-results">
          {users.length > 0 ? (
            users.map((user) => (
              <li
                className="search-result"
                key={user.id}
                onClick={() => handleUserClick(user.id)}
              >
                {user.username}
              </li>
            ))
          ) : (
            <li>No users found</li>
          )}
        </ul>
      )}
    </div>
  );
};

export default UserSearch;
