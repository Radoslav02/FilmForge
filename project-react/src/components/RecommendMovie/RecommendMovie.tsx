import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import "./RecommendMovie.css";

interface RecommendMovieProps {
  movieId: number;
  recommenderId: number | undefined;
}

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



const RecommendMovie: React.FC<RecommendMovieProps> = ({ movieId, recommenderId }) => {
  const [friends, setFriends] = useState<FriendRequestDTO[]>([]);


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
      } catch (err: any) {
        alert("Failed to load friends.");
        console.error(err);
      }
    };

    if (user?.id && token) {
      fetchFriends();
    } else {
        alert("User not authenticated.");
    }
  }, [user?.id]);

  const handleRecommendMovie = async (receiverId: number) => {
    const token = localStorage.getItem("jwtToken");

    console.log("Movie ID:", movieId);
    console.log("Recommender ID (user?.id):", recommenderId);
    console.log("Receiver ID (selectedFriend):", receiverId);

    try {
      const response = await fetch(`http://localhost:8080/api/recommendations/sendRecommendation/${movieId}/${recommenderId}/${receiverId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to send movie recommendation.");
      }

      alert("Movie recommendation sent successfully!");
    } catch (err: any) {
        alert("Failed to send recommendation.");
      console.error(err);
    }
  };

  return (
    <div className="recommend-movie-container">
      <div className="friend-list-wrapper">
        {friends.length > 0 ? (
          <ul className="recommend-list">
            {friends.map((friend) => (
              <li className="friend-list-item" key={friend.id}>
                <button className="friend-button" onClick={() => {
                  handleRecommendMovie(friend.friendId); 
                }}>
                  {friend.senderName} {friend.senderSurname}
                </button>
              </li>
            ))}
          </ul>
        ) : (
          <p>No friends found.</p>
        )}
      </div>

     
    </div>
  );
};

export default RecommendMovie;
