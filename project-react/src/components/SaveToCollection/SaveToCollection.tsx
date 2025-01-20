import { useEffect, useState } from "react";
import "./SaveToCollection.css";

interface FavoriteMoviesDTO {
  id: number;
  categoryName: string;
}

interface SaveToCollectionProps {
  movieId: number;  
  userId: number;   
}

export default function SaveToCollection({ movieId, userId }: SaveToCollectionProps) {
  const [favoriteMoviesList, setFavoriteMoviesList] = useState<FavoriteMoviesDTO[]>([]);

  useEffect(() => {
    if (userId) {
      fetchFavoriteMoviesList(userId);
    }
  }, [userId]);

  const fetchFavoriteMoviesList = async (userId: number) => {
    const jwtToken = localStorage.getItem("jwtToken");

    if (!jwtToken) {
      alert("No JWT token found");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/favorite-movies/list?userId=${userId}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${jwtToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch favorite movies list");
      }

      const data: FavoriteMoviesDTO[] = await response.json();
      setFavoriteMoviesList(data);
    } catch (err) {
      console.error("Failed to fetch favorite movies list:", err);
    }
  };

  const addMovieToFavorite = async (favoriteMoviesId: number, movieId: number) => {
    const jwtToken = localStorage.getItem("jwtToken");

    if (!jwtToken) {
      alert("No JWT token found");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/favorite-movies/add-movie/${favoriteMoviesId}/${movieId}`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${jwtToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to add movie to favorite list");
      }

      alert("Movie added to favorite list successfully!");
    } catch (error) {
      console.error("Error adding movie to favorite list:", error);
      alert("Failed to add movie to favorite list");
    }
  };

  return (
    <div className="save-to-collection-container">
      {favoriteMoviesList.length > 0 && (
        <div className="movie-lists-container">
          {favoriteMoviesList.map((list) => (
            <div key={list.id} className="favorite-movie-list">
              <button onClick={() => addMovieToFavorite(list.id, movieId)}>
                {list.categoryName}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
