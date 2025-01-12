import { useEffect, useState } from "react";
import "./Home.css";
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
  comments: Comment[];
}

interface Comment {
  user: string | undefined;
  content: string;
}

export default function Home() {
  const [categories, setCategories] = useState<{ id: number; name: string }[]>(
    []
  );
  const [movies, setMovies] = useState<Movie[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    null
  );
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );
  const [rating, setRating] = useState<{ [movieId: number]: number }>({});
  const [newComment, setNewComment] = useState<{ [movieId: number]: string }>(
    {}
  );
  const [feedback, setFeedback] = useState<string | null>(null);
  const user = useSelector((state: RootState) => state.auth.user);

  // Loading ratings from localStorage
  useEffect(() => {
    const storedRatings = JSON.parse(
      localStorage.getItem("movieRatings") || "{}"
    );
    setRating(storedRatings);
  }, []);

  // Fetching categories
  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchCategories = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/categories/allCategories",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch categories.");
        }

        const data = await response.json();
        setCategories(data);
      } catch (err: any) {
        console.error(err);
      }
    };

    fetchCategories();
  }, []);

  // Fetching movies by friends
  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchMoviesByFriends = async () => {
      try {
        if (!user?.id) return;

        const response = await fetch(
          `http://localhost:8080/api/movie/movies/friends/${user.id}`,
          {
            method: "POST",
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

        const updatedMovies = data.map((movie: Movie) => ({
          ...movie,
          comments: movie.comments || [],
        }));

        setMovies(updatedMovies);

        fetchCommentsForMovies(updatedMovies);
      } catch (err: any) {
        console.error(err);
      }
    };

    fetchMoviesByFriends();
  }, [user?.id]);

  // Fetch comments for a specific movie
  const fetchCommentsForMovies = async (movies: Movie[]) => {
    const token = localStorage.getItem("jwtToken");

    const updatedMovies = await Promise.all(
      movies.map(async (movie) => {
        try {
          const response = await fetch(
            `http://localhost:8080/api/movie/${movie.id}/comments`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );

          if (!response.ok) {
            throw new Error(`Failed to fetch comments for movie ${movie.id}`);
          }

          const comments = await response.json();
          console.log(comments);
          return { ...movie, comments };
        } catch (err: any) {
          console.error(err);
          return movie;
        }
      })
    );

    setMovies(updatedMovies);
  };

  const filteredMovies = selectedCategoryId
    ? movies
        .filter((movie) => movie.categoryId === selectedCategoryId)
        .map((movie) => ({
          ...movie,
          categoryName: categories.find(
            (category) => category.id === movie.categoryId
          )?.name,
        }))
    : movies.map((movie) => ({
        ...movie,
        categoryName: categories.find(
          (category) => category.id === movie.categoryId
        )?.name,
      }));

  const handleViewMore = (movieId: number) => {
    setExpandedDescription(expandedDescription === movieId ? null : movieId);
  };

  const handleRatingChange = async (movieId: number, value: number) => {
    setRating((prevRatings) => {
      const newRatings = { ...prevRatings, [movieId]: value };
      localStorage.setItem("movieRatings", JSON.stringify(newRatings));
      return newRatings;
    });

    const token = localStorage.getItem("jwtToken");

    if (!user?.id || value < 1 || value > 5) {
      setFeedback("Please provide a valid rating between 1 and 5.");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/movie/${movieId}/grade`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            Authorization: `Bearer ${token}`,
          },
          body: new URLSearchParams({
            userId: user.id.toString(),
            value: value.toString(),
          }),
        }
      );

      if (!response.ok) {
        throw new Error(await response.text());
      }

      setFeedback("Rating submitted successfully!");
      setTimeout(() => setFeedback(null), 3000);
    } catch (err: any) {
      console.error(err);
      setFeedback("Failed to submit rating. Please try again.");
    }
  };

  const handleAddComment = async (movieId: number) => {
    const token = localStorage.getItem("jwtToken");

    if (!newComment[movieId] || !user?.id) {
      console.error("Missing comment text or user information");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/movie/${movieId}/comment`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            userId: user.id,
            text: newComment[movieId],
          }),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        console.error("Error adding comment:", errorText);
        throw new Error(errorText || "Failed to add comment.");
      }

      const updatedMovies = movies.map((movie) =>
        movie.id === movieId
          ? {
              ...movie,
              comments: [
                ...movie.comments,
                { user: user.username, content: newComment[movieId] },
              ],
            }
          : movie
      );

      setMovies(updatedMovies);
      setNewComment((prev) => ({ ...prev, [movieId]: "" }));
    } catch (err: any) {
      console.error("Error:", err.message);
      setFeedback("Failed to add comment. Please try again.");
    }
  };

  return (
    <div className="home-container">
      <div className="categories-bar">
        <button
          className="category-button"
          onClick={() => setSelectedCategoryId(null)}
        >
          All
        </button>
        {categories.map((category) => (
          <button
            key={category.id}
            className={`category-button ${
              selectedCategoryId === category.id ? "active" : ""
            }`}
            onClick={() => setSelectedCategoryId(category.id)}
          >
            {category.name}
          </button>
        ))}
      </div>
      <div className="movies-list">
        <h1 className="home-page-title">Movies by Friends</h1>
        {feedback && <p className="feedback">{feedback}</p>}
        {filteredMovies.map((movie, index) => (
          <div key={index} className="movie-item">
            <div className="home-movie-info-wrapper">
              <h3 className="movie-username">{movie.username}</h3>
              <p>
                Release Date: {new Date(movie.releaseDate).toLocaleDateString()}
              </p>
              <p>Title: {movie.title}</p>
              <p>Director: {movie.director}</p>
              <p>Category: {movie.categoryName}</p>
              <p>Average Rating: {movie.averageGrade.toFixed(2)}</p>
              <div className="desc-wrapper">
                <p className="desc-p">
                  {expandedDescription === movie.id
                    ? movie.description
                    : `${movie.description.slice(0, 174)}`}
                </p>
                {movie.description.length > 174 && (
                  <button
                    className="view-more"
                    onClick={() => handleViewMore(movie.id)}
                  >
                    {expandedDescription === movie.id
                      ? "View Less"
                      : "View More"}
                  </button>
                )}
              </div>
            </div>
            {movie.imageUrl && (
              <img
                src={`http://localhost:8080${movie.imageUrl}`}
                alt={`${movie.title} Poster`}
                className="movie-image"
              />
            )}
            <div className="rating-comment-wrapper">
              <div className="rating-wrapper">
                <div className="rating-buttons">
                  {[1, 2, 3, 4, 5].map((val) => (
                    <button
                      key={val}
                      className={`star-button ${
                        rating[movie.id] >= val ? "selected" : ""
                      }`}
                      onClick={() => handleRatingChange(movie.id, val)}
                      aria-label={`Rate this movie ${val} stars`}
                    >
                      ★
                    </button>
                  ))}
                </div>
              </div>

              <div className="comments-section">
                <button
                  className="comments-toggle-button"
                  onClick={() =>
                    setExpandedDescription(
                      expandedDescription === movie.id ? null : movie.id
                    )
                  }
                >
                  {expandedDescription === movie.id
                    ? "Close"
                    : "Comments"}
                </button>
              </div>
              </div>
              {expandedDescription === movie.id && (
                <div className="comments-block">
                  <div className="comments-list">
                    {movie.comments.length > 0 ? (
                      movie.comments.map((comment, idx) => (
                        <div key={idx} className="comment">
                          <strong>{comment.user}</strong>: {comment.content}
                        </div>
                      ))
                    ) : (
                      <p>No comments yet</p>
                    )}
                  </div>
                  <textarea
                    value={newComment[movie.id] || ""}
                    onChange={(e) =>
                      setNewComment((prev) => ({
                        ...prev,
                        [movie.id]: e.target.value,
                      }))
                    }
                    className="add-comment-area"
                    placeholder="Add a comment..."
                  />
                  <button className="add-comment-button" onClick={() => handleAddComment(movie.id)}>
                    Add Comment
                  </button>
                </div>
              )}
            
          </div>
        ))}
      </div>
    </div>
  );
}
