import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./UserSearch.css";
import SearchIcon from "@mui/icons-material/Search";

interface User {
  id: number;
  username: string;
}

const UserSearch: React.FC = () => {
  const [query, setQuery] = useState<string>("");
  const [users, setUsers] = useState<User[]>([]);
  const [isDropdownVisible, setIsDropdownVisible] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const inputRef = useRef<HTMLDivElement>(null);

  const handleUserClick = (userId: number) => {
    navigate(`/user/${userId}`);
    setIsDropdownVisible(false);
  };

  const searchUsers = async () => {
    if (!query.trim()) {
      setUsers([]);
      return;
    }
  
    const token = localStorage.getItem("jwtToken");
  
    if (!token) {
      setError("User not authenticated");
      return;
    }
  
    setIsLoading(true);
    setError(null);
  
    try {
      const response = await fetch(
        `http://localhost:8080/api/users/search?username=${encodeURIComponent(query)}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
  
      if (response.ok) {
        const data = await response.json();
        console.log("Parsed response:", data);
  
        if (Array.isArray(data) && data.length > 0) {
          setUsers(data);
          setIsDropdownVisible(true);
        } else {
          setUsers([]);
          setError("No users found");
        }
      } else {
        const errorData = await response.json();
        setError(errorData.message || "No users found");
      }
    } catch (err) {
      console.error("Error fetching users:", err);
      setError("An error occurred while searching for users");
    } finally {
      setIsLoading(false);
    }
  };
  

  const debounceTimeout = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (debounceTimeout.current) {
      clearTimeout(debounceTimeout.current);
    }

    debounceTimeout.current = setTimeout(() => {
      searchUsers();
    }, 500);
  }, [query]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        inputRef.current &&
        !inputRef.current.contains(event.target as Node)
      ) {
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
      <div className="search-input-wrapper">
        <input
          type="text"
          placeholder="Search users by username..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => setIsDropdownVisible(true)}
          className="search-input"
        />
        <SearchIcon className="search-icon" />
      </div>

      {isLoading && <p className="loading-message">Loading...</p>}

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
            <li className="no-results">
              {error || "No users found"}
            </li>
          )}
        </ul>
      )}
    </div>
  );
};

export default UserSearch;
