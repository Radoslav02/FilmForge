import { useEffect, useState } from "react";
import "./Recommendations.css";
import "../Profile/Profile.css";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import SaveToCollection from "../SaveToCollection/SaveToCollection";
import RecommendMovie from "../RecommendMovie/RecommendMovie";
import InsertCommentIcon from "@mui/icons-material/InsertComment";
import CommentsDisabledIcon from "@mui/icons-material/CommentsDisabled";
import BookmarkIcon from "@mui/icons-material/Bookmark";
import SendIcon from "@mui/icons-material/Send";

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
  senderName: string;
  comments: Comment[];
}

interface Comment {
  user: string | undefined;
  content: string;
}

export default function Recommendations() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const user = useSelector((state: RootState) => state.auth.user);
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );
  const [rating, setRating] = useState<{ [movieId: number]: number }>({});
  const [newComment, setNewComment] = useState<{ [movieId: number]: string }>(
    {}
  );
  const [feedback, setFeedback] = useState<string | null>(null);

  const [showSaveToCollection, setShowSaveToCollection] = useState(false);
  const [showFriends, setShowFriends] = useState(false);
  const [currentMovieId, setCurrentMovieId] = useState<number | null>(null);

  const handleShowLists = () => {
    setShowSaveToCollection(!showSaveToCollection);
    document.body.style.overflow = "hidden";
  };

  const handleShowFriends = (movieId: number) => {
    setCurrentMovieId(movieId);
    setShowFriends(true);
    document.body.style.overflow = "hidden";
  };

  const handleCloseFriends = () => {
    setShowFriends(false);
    document.body.style.overflow = "";
  };

  const handleClose = () => {
    setShowSaveToCollection(false);
    document.body.style.overflow = "";
  };

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

  useEffect(() => {
    const storedRatings = JSON.parse(
      localStorage.getItem("movieRatings") || "{}"
    );
    setRating(storedRatings);
  }, []);

  const handleViewMore = (movieId: number) => {
    setExpandedDescription(expandedDescription === movieId ? null : movieId);
  };

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchRecommendedMoves = async () => {
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
        console.log(data);

        const updatedMovies = data.map((movie: Movie) => ({
          ...movie,
          comments: movie.comments || [],
        }));

        setMovies(updatedMovies);

        fetchCommentsForMovies(updatedMovies);

        setMovies(data);
      } catch (err: any) {
        console.error(err);
      }
    };
    fetchRecommendedMoves();
  }, [user?.id]);

  return (
    <div className="recommendations-container">
      <div className="movies-section">
        <h1 className="recommendations-title">Recommended Movies</h1>
        {feedback && <p className="feedback">{feedback}</p>}
        {movies.length > 0 ? (
          <div className="profile-page-movies-container">
            {movies.map((movie,index) => (
              <div key={index} className="movie-item">
                <div className="home-movie-info-wrapper">
                  <h3 className="movie-username">
                    Recommended by: {movie.senderName}
                  </h3>
                  <p>
                    <strong>Posted by:</strong> {movie.username}
                  </p>
                  <p>
                    Release Date:{" "}
                    {new Date(movie.releaseDate).toLocaleDateString()}
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
                  <div className="post-buttons-wrapper">
                    <div className="list-names-container">
                      <BookmarkIcon
                        sx={{ fontSize: 30 }}
                        className="post-button-icon"
                        onClick={handleShowLists}
                      />

                      {showSaveToCollection && (
                        <>
                          <div
                            className="list-overlay"
                            onClick={handleClose}
                          ></div>
                          <div className="floating-container">
                            <h2>Save to:</h2>
                            <button
                              className="close-button"
                              onClick={handleClose}
                            >
                              &times;
                            </button>
                            <SaveToCollection
                              movieId={movie.id}
                              userId={user!.id}
                            />
                          </div>
                        </>
                      )}
                    </div>

                    <div className="friends-list-container">
                      <SendIcon
                        className="post-button-icon"
                        sx={{ fontSize: 30 }}
                        onClick={() => handleShowFriends(movie.id)}
                      />
                      {showFriends && (
                        <>
                          <div
                            className="friends-list-overlay"
                            onClick={handleCloseFriends}
                          ></div>
                          <div className="friends-floating-container">
                            <h2>Send to:</h2>
                            <button
                              className="close-button"
                              onClick={handleCloseFriends}
                            >
                              &times;
                            </button>
                            {currentMovieId !== null && (
                              <RecommendMovie
                                movieId={currentMovieId}
                                recommenderId={user!.id}
                              />
                            )}
                          </div>
                        </>
                      )}
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
                        {expandedDescription === movie.id ? (
                          <CommentsDisabledIcon
                            className="post-button-icon"
                            sx={{ fontSize: 30 }}
                          />
                        ) : (
                          <InsertCommentIcon
                            className="post-button-icon"
                            sx={{ fontSize: 30 }}
                          />
                        )}
                      </button>
                    </div>
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
                    <button
                      className="add-comment-button"
                      onClick={() => handleAddComment(movie.id)}
                    >
                      Add Comment
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <p>No movies available.</p>
        )}
      </div>
    </div>
  );
}
