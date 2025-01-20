import { useEffect, useState } from "react";
import "./Recommendations.css"
import "../Profile/Profile.css"
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";


interface Movie {
  id: number;
  username: string;
  title: string;
  director: string;
  releaseDate: string;
  description: string;
  imageUrl?: string;
  categoryId: number;
  categoryName?: string;
  averageGrade: number;
}

export default function Recommendations() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const user = useSelector((state: RootState) => state.auth.user);
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );

   useEffect(() => {
      const token = localStorage.getItem("jwtToken");
  
      const fetchMoviesByFriends = async () => {
        try {
          if (!user?.id) return;
  
          const response = await fetch(
            `http://localhost:8080/api/recommendations/getRecommendations/${user.id}`,
            {
              method: "GET",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            }
          );
  
          if (!response.ok) {
            throw new Error("Failed to fetch movies by friends.");
          }
  
          const data = await response.json();
          console.log(data)
  
          setMovies(data);
  
        } catch (err: any) {
          console.error(err);
        }
      };
  
      fetchMoviesByFriends();
    }, [user?.id]);

    const handleViewMore = (movieId: number) => {
      setExpandedDescription(expandedDescription === movieId ? null : movieId);
    };

  return (
    <div className="recommendations-container">
      <div className="movies-section">
            <h2>Recommended Movies</h2>
            {movies.length > 0 ? (
              <div className="profile-page-movies-container">
                {movies.map((movie) => (
                  <div className="profile-movie-card" key={movie.id}>
                    <div className="profile-page-movie-info-wrapper">
                      <div className="post-button-wrapper">
                      </div>
                      <p>
                        <strong>Title:</strong> {movie.title}
                      </p>
                      <p>
                        <strong>Director:</strong> {movie.director}
                      </p>
                      <p>
                        <strong>Release Date:</strong>{" "}
                        {new Date(movie.releaseDate).toLocaleDateString()}
                      </p>
                      <p>
                        <strong>Category:</strong> {movie.categoryName}
                      </p>
                      <div className="profile-page-desc-wrapper">
                        <p className="profile-page-desc-p">
                          {expandedDescription === movie.id
                            ? movie.description
                            : `${movie.description.slice(0, 174)}`}
                        </p>
                        {movie.description.length > 174 && (
                          <button
                            className="profile-page-view-more"
                            onClick={() => handleViewMore(movie.id)}
                          >
                            {expandedDescription === movie.id
                              ? "View Less"
                              : "View More"}
                          </button>
                        )}
                      </div>
                      {movie.imageUrl && (
                        <img
                          src={`http://localhost:8080${movie.imageUrl}`}
                          alt={movie.title}
                          className="profile-movie-image"
                        />
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p>No movies available.</p>
            )}
          </div>
    </div>
  )
}
