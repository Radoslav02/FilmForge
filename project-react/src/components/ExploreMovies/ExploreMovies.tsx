import React, { useEffect, useState } from "react";
import "./ExploreMovies.css";
import "../Home/Home.css";
import SearchIcon from "@mui/icons-material/Search";

interface Category {
  id: number;
  name: string;
}

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

const ExploreMovies: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    null
  );
  const [movies, setMovies] = useState<Movie[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/movie/getAllMovies"
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

    fetchMovies();
  }, []);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/categories/allCategories"
        );
        if (!response.ok) {
          throw new Error("Failed to fetch categories");
        }
        const data: Category[] = await response.json();
        setCategories(data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);

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

  return (
    <div className="explore-movies-container">
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
      <h1 className="explore-page-title">Explore</h1>
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
                  {expandedDescription === movie.id ? "View Less" : "View More"}
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
        </div>
      ))}
    </div>
  );
};

export default ExploreMovies;
