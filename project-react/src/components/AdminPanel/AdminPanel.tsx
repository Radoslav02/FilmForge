import { useEffect, useState } from "react";
import "./AdminPanel.css";
import SearchIcon from "@mui/icons-material/Search";
import EditMovie from "../EditMovie/EditMovie";
import { toast } from "react-toastify";

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

export default function AdminPanel() {
  const [categories, setCategories] = useState<{ id: number; name: string }[]>(
    []
  );
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    null
  );
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState("");
  const [movies, setMovies] = useState<Movie[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );
  const [editingMovieId, setEditingMovieId] = useState<number | null>(null);

  const handleCloseEditMovie = () => {
    setEditingMovieId(null);
    fetchMovies();
  };

  const handleEditMovie = (movieId: number) => {
    setEditingMovieId(movieId);
  };

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchCategories = async () => {
      try {
        const response = await fetch(
          `${import.meta.env.VITE_APP_API_URL}/api/categories/allCategories`,
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

  const handleSaveCategory = async () => {
    const token = localStorage.getItem("jwtToken");

    try {
      const response = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/categories`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ name: newCategoryName }),
      });

      if (!response.ok) {
        throw new Error("Failed to save category.");
      }

      const savedCategory = await response.json();
      setCategories((prevCategories) => [...prevCategories, savedCategory]);
      setNewCategoryName("");
      setIsModalOpen(false);
    } catch (err: any) {
      console.error(err);
    }
  };

  const handleDeleteCategory = async (id: number) => {
    const token = localStorage.getItem("jwtToken");

    try {
      const response = await fetch(
        `${import.meta.env.VITE_APP_API_URL}/api/categories/${id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete category.");
      }

      setCategories((prevCategories) =>
        prevCategories.filter((category) => category.id !== id)
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const filteredMovies = movies
    .filter((movie) =>
      selectedCategoryId ? movie.categoryId === selectedCategoryId : true
    )
    .filter((movie) =>
      movie.title.toLowerCase().includes(searchQuery.toLowerCase())
    )
    .sort((a, b) => a.title.localeCompare(b.title))
    .map((movie) => ({
      ...movie,
      categoryName: categories.find(
        (category) => category.id === movie.categoryId
      )?.name,
    }));

  const handleViewMore = (movieId: number) => {
    setExpandedDescription(expandedDescription === movieId ? null : movieId);
  };

  useEffect(() => {
    fetchMovies();
  }, []);

  const fetchMovies = async () => {
    const token = localStorage.getItem("jwtToken");
    try {
      const response = await fetch(
        `${import.meta.env.VITE_APP_API_URL}/api/movie/getAllMovies`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch movies");
      }
      const data: Movie[] = await response.json();
      setMovies(data);
    } catch (error) {
      console.error("Error fetching movies:", error);
    }
  };

  const handleDeleteMovie = async (movieId: number) => {
    try {
      const token = localStorage.getItem("jwtToken");

      if (!token) {
        toast.error("No token found. Please log in");
        return;
      }

      const response = await fetch(
        `${import.meta.env.VITE_APP_API_URL}/api/movie/deleteMovie/${movieId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete movie");
      }

      setMovies((prevMovies) =>
        prevMovies.filter((movie) => movie.id !== movieId)
      );
      toast.success("Movie deleted successfully");
    } catch (error) {
      console.error("Error during delete:", error);
      toast.error("Failed to delete movie. Please try again later.");
    }
  };

  const handleGenerateReport = async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      toast.error("You must be logged in to generate the report.");
      return;
    }

    try {
      const response = await fetch(
        `${import.meta.env.VITE_APP_API_URL}/api/movie/generateReport`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to generate the report.");
      }

      const blob = await response.blob();
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "MoviesReport.pdf";
      link.click();
    } catch (error) {
      console.error("Error generating report:", error);
      toast.error("Failed to generate report. Please try again.");
    }
  };

  return (
    <div className="admin-page-container">
      {editingMovieId ? (
        <EditMovie movieId={editingMovieId} onClose={handleCloseEditMovie} />
      ) : (
        <>
          <div className="admin-categories-bar">
            <button
              className="admin-category-button"
              onClick={() => setIsModalOpen(true)}
            >
              +
            </button>
            <button
              className="admin-category-button"
              onClick={() => setSelectedCategoryId(null)}
            >
              All
            </button>
            {categories.map((category) => (
              <div
                key={category.id}
                className={`admin-category-button ${
                  selectedCategoryId === category.id ? "active" : ""
                }`}
              >
                <span onClick={() => setSelectedCategoryId(category.id)}>
                  {category.name}
                </span>
                <button
                  className="delete-x-button"
                  onClick={() => handleDeleteCategory(category.id)}
                >
                  ✖
                </button>
              </div>
            ))}
          </div>
          <h1 className="admin-panel-title">Admin panel</h1>
        

          {isModalOpen && (
            <div className="modal-overlay">
              <div className="modal">
                <h2>Add Category</h2>
                <input
                  type="text"
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  placeholder="Category Name"
                />
                <div className="modal-buttons">
                  <button onClick={() => setIsModalOpen(false)}>Cancel</button>
                  <button onClick={handleSaveCategory}>Save</button>
                </div>
              </div>
            </div>
          )}
          <div className="explore-search-bar">
            <input
              type="text"
              className="explore-search-input"
              placeholder="Search movies by title..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <SearchIcon className="explore-search-icon" />
          </div>
          <button
            className="generate-report-button"
            onClick={handleGenerateReport}
          >
            Generate Report
          </button>

          {filteredMovies.map((movie, index) => (
            <div key={index} className="movie-item">
              <div className="home-movie-info-wrapper">
                <div className="post-button-wrapper">
                  <button
                    className="edit-movie-button"
                    onClick={() => handleEditMovie(movie.id)}
                  >
                    Edit
                  </button>
                  <button
                    className="delete-movie-button"
                    onClick={() => handleDeleteMovie(movie.id)}
                  >
                    X
                  </button>
                </div>
                <h3 className="movie-username">{movie.username}</h3>
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
                  src={`${import.meta.env.VITE_APP_API_URL}${movie.imageUrl}`}
                  alt={`${movie.title} Poster`}
                  className="movie-image"
                />
              )}
            </div>
          ))}
        </>
      )}
    </div>
  );
}
