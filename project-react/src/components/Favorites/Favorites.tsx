import { useState, useEffect } from "react";
import "./Favorites.css";
import { RootState } from "../Redux/store";
import { useSelector } from "react-redux";
import DeleteIcon from '@mui/icons-material/Delete';

interface MovieDTO {
  id: number;
  username: string;
  title: string;
  director: string;
  releaseDate: string;
  description: string;
  categoryId: number;
  imageUrl: string;
  categoryName: string;
  averageGrade: number;
}

interface FavoriteMoviesDTO {
  id: number;
  userId: number;
  categoryName: string;
  movies: MovieDTO[];
}

export default function Favorites() {
  const [favoriteMoviesList, setFavoriteMoviesList] = useState<FavoriteMoviesDTO[]>([]);
  const [categoryName, setCategoryName] = useState<string>(""); 
  const user = useSelector((state: RootState) => state.auth.user);
  const [editingListId, setEditingListId] = useState<number | null>(null);

  const handleEditToggle = (listId: number) => {
    setEditingListId((prevId) => (prevId === listId ? null : listId));
  };

  const handleDeleteMovieList = async (listId: number) => {
    try {
      const token = localStorage.getItem("jwtToken");

      if (!token) {
        return;
      }

      const response = await fetch(
        `http://localhost:8080/api/favorite-movies/deleteList/${listId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete movie list");
      }

      setFavoriteMoviesList((prevMoviesLists) =>
        prevMoviesLists.filter((list) => list.id !== listId)
      );
      alert("Movie list deleted successfully");
    } catch (error) {
      console.error("Error during delete:", error);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchFavoriteMoviesList(user.id);
    }
  }, [user]);

  const fetchFavoriteMoviesList = async (userId: number) => {
    const jwtToken = localStorage.getItem("jwtToken");

    if (!jwtToken) {
      alert("No JWT token found");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/favorite-movies/list?userId=${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch favorite movies list");
      }

      const data: FavoriteMoviesDTO[] = await response.json();
      setFavoriteMoviesList(data);
    } catch (err) {}
  };

  const handleCreateEmptyList = async () => {
    const jwtToken = localStorage.getItem("jwtToken");

    if (!jwtToken) {
      alert("No JWT token found");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/favorite-movies/create-empty-list/${user?.id}/${categoryName}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
          body: JSON.stringify({
            userId: user?.id,
            categoryName,
          }),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to create empty favorite movies list");
      }

      const newList: FavoriteMoviesDTO = await response.json();
      setFavoriteMoviesList((prev) => [...prev, newList]);
      setCategoryName("");
    } catch (err) {}
  };

  const handleRemoveMovie = async (listId: number, movieId: number) => {
    const jwtToken = localStorage.getItem("jwtToken");
  
    if (!jwtToken) {
      alert("No JWT token found");
      return;
    }
  
    try {
      const response = await fetch(
        `http://localhost:8080/api/favorite-movies/deleteMovieFromList/${movieId}/${listId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
  
      if (!response.ok) {
        throw new Error("Failed to remove movie from list");
      }
  
      setFavoriteMoviesList((prevLists) =>
        prevLists.map((list) =>
          list.id === listId
            ? {
                ...list,
                movies: list.movies.filter((movie) => movie.id !== movieId),
              }
            : list
        )
      );
  
      alert("Movie removed successfully");
    } catch (err) {
      console.error("Error during remove:", err);
      alert("Failed to remove movie");
    }
  };

  return (
    <div className="favorites-container">
      <h1 className="favorites-title">Your Collection</h1>
      <div className="create-list-form">
        <input
          type="text"
          placeholder="Enter category name"
          value={categoryName}
          onChange={(e) => setCategoryName(e.target.value)}
        />
        <button onClick={handleCreateEmptyList}>Create New List</button>
      </div>

      {favoriteMoviesList.length === 0 ? (
        <div>No favorite movies lists found.</div>
      ) : (
        <div className="favorite-movies-lists">
          {favoriteMoviesList.map((favoriteList) => (
            <div key={favoriteList.id} className="favorite-movie-list-item">
              <div className="edit-list-wrapper">
                <h2 className="list-name">{favoriteList.categoryName}</h2>
                <div className="edit-list-button-wrapper">
                  <button
                    onClick={() => handleEditToggle(favoriteList.id)}
                    className="edit-list-button"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeleteMovieList(favoriteList.id)}
                    className="delete-list-button"
                  >
                    X
                  </button>
                </div>
              </div>
              <div className="list-movies-list">
                {favoriteList.movies.length === 0 ? (
                  <p>No movies in this list</p>
                ) : (
                  favoriteList.movies.map((movie) => (
                    <div key={movie.id} className="list-movie-item">
                      <div className="list-movie-info-wrapper">
                        <h4>Title: {movie.title}</h4>
                        <p>Rating: {movie.averageGrade}</p>
                      </div>
                      {editingListId === favoriteList.id && (
                        <DeleteIcon className="remove-movie-from-list"
                          onClick={() => handleRemoveMovie(favoriteList.id, movie.id)}
                        />
                      )}
                    </div>
                  ))
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
