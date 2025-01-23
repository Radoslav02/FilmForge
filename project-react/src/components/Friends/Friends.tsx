import { useSelector } from "react-redux";
import "./Friends.css";
import { useEffect, useState } from "react";
import { RootState } from "../Redux/store";
import DeleteIcon from '@mui/icons-material/Delete';

interface FriendRequestDTO {
  id: number;
  senderId: number;
  receiverId: number;
  requestDate: string;  
  senderUsername: string; 
  senderName: string;    
  senderSurname: string;
  accepted: boolean; 
  friendId: number; 
}

export default function Friends() {
  const [friends, setFriends] = useState<FriendRequestDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchFriends = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/api/requests/usersFriends/${user?.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch friends.");
        }

        const data: FriendRequestDTO[] = await response.json();
        setFriends(data);
        console.log(data); 
      } catch (err: any) {
        setError("Failed to load friends.");
        console.error(err);
      }
    };

    if (user?.id && token) {
      fetchFriends();
    } else {
      setError("User not authenticated.");
    }
  }, [user?.id]); 

  const handleDeleteRequest = async (requestId: number) => {
    const token = localStorage.getItem("jwtToken");

    try {
      const response = await fetch(
        `http://localhost:8080/api/requests/delete/${requestId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
      
        setFriends(friends.filter(friend => friend.id !== requestId));
      } else {
        throw new Error("Failed to delete friend request.");
      }
    } catch (err: any) {
      console.error(err);
      setError("Failed to delete friend request.");
    }
  };

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  return (
    <div className="friends-page-container">
      <h1>Your Friends</h1>
      {friends.length > 0 ? (
        <ul className="friends-list">
          {friends.map((friend) => (
            <li key={friend.id} className="friend-item">
              <div className="friend-info-wrapper">
              <h3>
                {friend.senderName} {friend.senderSurname}
              </h3>
              <p>Username: {friend.senderUsername}</p>
              <p>Friends since: {new Date(friend.requestDate).toLocaleString()}</p>
              </div>
              <DeleteIcon className="delete-friend-item" sx={{fontSize:30}} onClick={() => handleDeleteRequest(friend.id)} />
            </li>
          ))}
        </ul>
      ) : (
        <p>No friends found.</p>
      )}
    </div>
  );
}
