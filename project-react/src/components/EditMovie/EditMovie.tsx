import React, { useState, useEffect, ChangeEvent, FormEvent } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import "./EditMovie.css";

interface Category {
  id: number;
  name: string;
}

interface Movie {
  title: string;
  director: string;
  releaseDate: string;
  description: string;
  categoryId: string | number;
  image: File | null;
  imageUrl: string;
}

interface EditMovieProps {
  movieId: number;
  onClose: () => void;
}

const EditMovie: React.FC<EditMovieProps> = ({ movieId, onClose }) => {
  const [movie, setMovie] = useState<Movie>({
    title: "",
    director: "",
    releaseDate: "",
    description: "",
    categoryId: "",
    image: null,
    imageUrl: "",
  });

  const [categories, setCategories] = useState<Category[]>([]);
  const user = useSelector((state: RootState) => state.auth.user);
  const token = localStorage.getItem("jwtToken") || "";

  useEffect(() => {
    if (!token) {
      console.error("No token found. Please log in.");
      return;
    }

    fetch(`http://localhost:8080/api/movie/${movieId}/getMovie`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch movie details");
        }
        return response.json();
      })
      .then((data) => {
        const {
          title,
          director,
          releaseDate,
          description,
          categoryId,
          imageUrl,
        } = data;
        setMovie({
          title,
          director,
          releaseDate,
          description,
          categoryId,
          image: null,
          imageUrl: imageUrl || "",
        });
      })
      .catch((error) => console.error("Error fetching movie details:", error));

    fetch("http://localhost:8080/api/categories/allCategories", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch categories");
        }
        return response.json();
      })
      .then((data) => setCategories(data))
      .catch((error) => console.error("Error fetching categories:", error));
  }, [movieId, token]);

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setMovie((prevState) => ({ ...prevState, [name]: value }));
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      e;
      setMovie((prevState) => ({
        ...prevState,
        image: files[0],
        imageUrl: URL.createObjectURL(files[0]),
      }));
    }
  };

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!token) {
      alert("No token found. Please log in.");
      return;
    }

    if (!user || !user.id) {
      alert("No user information found. Please log in.");
      return;
    }

    const formData = new FormData();
    formData.append("title", movie.title);
    formData.append("director", movie.director);
    formData.append("releaseDate", movie.releaseDate);
    formData.append("description", movie.description);
    formData.append("categoryId", movie.categoryId.toString());
    formData.append("userId", user.id.toString());
    if (movie.image) {
      formData.append("image", movie.image);
    }

    fetch(`http://localhost:8080/api/movie/${movieId}/edit`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to update movie");
        }
        return response.text();
      })
      .then((message) => {
        alert(message);
        onClose();
      })
      .catch((error) => {
        console.error("Error updating movie:", error);
        alert("Failed to update movie.");
      });
  };

  return (
    <div className="edit-movie-container">
      <h1>Edit Post</h1>
      <form className="edit-post-form" onSubmit={handleSubmit}>
        <div className="edit-post-pair">
          <div className="edit-post-form-wrapper">
            <label>Title:</label>
            <input
              type="text"
              name="title"
              value={movie.title}
              onChange={handleChange}
            />
          </div>
          <div className="edit-post-form-wrapper">
            <label>Director:</label>
            <input
              type="text"
              name="director"
              value={movie.director}
              onChange={handleChange}
            />
          </div>
        </div>
        <div className="edit-post-pair">
          <div className="edit-post-form-wrapper">
            <label>Release Date:</label>
            <input
              type="date"
              name="releaseDate"
              value={movie.releaseDate}
              onChange={handleChange}
            />
          </div>
          <div className="edit-post-form-wrapper">
            <label>Category:</label>
            <select
              name="categoryId"
              value={movie.categoryId}
              onChange={handleChange}
            >
              <option value="">Select Category</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className="edit-post-form-wrapper">
          <label>Description:</label>
          <textarea
            name="description"
            value={movie.description}
            onChange={handleChange}
          ></textarea>
        </div>

        <div className="edit-post-form-wrapper">
          <label>Image:</label>
          <input type="file" name="image" onChange={handleFileChange} />
          {movie.imageUrl && !movie.image && (
            <div className="edit-post-image-preview">
              <img
                src={`http://localhost:8080${movie.imageUrl}`}
                alt="Movie"
                style={{ width: "200px", height: "auto" }}
                className="edit-post-image"
              />
            </div>
          )}
          {movie.image && (
            <div className="edit-post-image-preview">
              <img
                src={URL.createObjectURL(movie.image)}
                alt="Preview"
                style={{ width: "200px", height: "auto" }}
                className="edit-post-image"
              />
            </div>
          )}
        </div>
        <div className="edit-post-button-wrapper">
          <button className="cancel-edit-movie" type="button" onClick={onClose}>
            Cancel
          </button>
          <button className="save-edit-movie" type="submit">
            Save Changes
          </button>
        </div>
      </form>
    </div>
  );
};

export default EditMovie;
