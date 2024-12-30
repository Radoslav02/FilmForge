import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import { logout } from "../Redux/authSlice";
import "./AddPost.css";

export default function AddPost() {
  const dispatch = useDispatch();
  const user = useSelector((state: RootState) => state.auth.user); 
  const token = localStorage.getItem("jwtToken");

  const [categories, setCategories] = useState<{ id: string; name: string }[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("");
  const [movieData, setMovieData] = useState({
    title: "",
    director: "",
    releaseDate: "",
    description: "",
  });
  const [image, setImage] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null); 
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
      setError("You need to be logged in to add a movie.");
      return;
    }

    const fetchCategories = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/categories/allCategories", {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch categories.");
        }

        const data = await response.json();
        setCategories(data);
      } catch (err: any) {
        setError(err.message || "Error fetching categories.");
      }
    };

    fetchCategories();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setMovieData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && file.type.startsWith("image/")) {
      setImage(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string); 
      };
      reader.readAsDataURL(file);
    } else {
      alert("Please upload a valid image file (jpg, png, etc.)");
    }
  };

  const handleRemoveImage = () => {
    setImage(null);
    setImagePreview(null); 
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!token) {
      setError("You need to be logged in to submit a movie.");
      return;
    }

    if (
      !movieData.title ||
      !movieData.director ||
      !movieData.releaseDate ||
      !movieData.description ||
      !selectedCategory
    ) {
      alert("Please fill out all fields before submitting.");
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append("title", movieData.title);
      formData.append("director", movieData.director);
      formData.append("releaseDate", movieData.releaseDate);
      formData.append("description", movieData.description);

      if (selectedCategory) {
        formData.append("categoryId", selectedCategory);
      } else {
        throw new Error("Category is required");
      }

      if (image) formData.append("image", image);

      
      if (user && user.id) {
        formData.append("userId", user.id.toString()); 
      } else {
        throw new Error("User ID is required");
      }

      const response = await fetch("http://localhost:8080/api/movie/addMovie", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`, 
        },
        body: formData, 
      });

      if (!response.ok) {
        const errorData = await response.json();
        console.error("Error details:", errorData);
        throw new Error("Failed to add movie.");
      }

      alert("Movie added successfully!");
      setMovieData({
        title: "",
        director: "",
        releaseDate: "",
        description: "",
      });
      setSelectedCategory("");
      setImage(null);
      setImagePreview(null); 
    } catch (err: any) {
      setError(err.message || "An error occurred while adding the movie.");
    } finally {
      setIsLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="add-post-container">
        <p>You need to be logged in to add a movie.</p>
        <button onClick={() => dispatch(logout())}>Log Out</button>
      </div>
    );
  }

  return (
    <div className="add-post-container">
      <form className="add-post-form" onSubmit={handleSubmit}>
        <div className="add-post-form-wrapper">
          <h1>Post Movie</h1>
          {error && <p className="error-message">{error}</p>}
        </div>
        <div className="add-post-form-wrapper">
          <label>Movie title:</label>
          <input
            type="text"
            name="title"
            value={movieData.title}
            onChange={handleInputChange}
            disabled={isLoading}
          />
        </div>
        <div className="add-post-form-wrapper">
          <label>Director:</label>
          <input
            type="text"
            name="director"
            value={movieData.director}
            onChange={handleInputChange}
            disabled={isLoading}
          />
        </div>
        <div className="add-post-form-wrapper">
          <label>Release Date:</label>
          <input
            type="date"
            name="releaseDate"
            value={movieData.releaseDate}
            onChange={handleInputChange}
            disabled={isLoading}
          />
        </div>
        <div className="add-post-form-wrapper">
          <label>Description:</label>
          <textarea
            name="description"
            value={movieData.description}
            onChange={handleInputChange}
            disabled={isLoading}
          />
        </div>
        <div className="add-post-form-wrapper">
          <label>Category:</label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            disabled={isLoading}
          >
            <option value="">Select a category</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </div>
        <div className="add-post-form-wrapper">
          <label>Upload image:</label>
          <input type="file" accept="image/*" onChange={handleImageChange} disabled={isLoading} />
          {imagePreview && (
            <div className="image-preview">
              <img src={imagePreview} alt="Preview" style={{ width: "100px", height: "100px" }} />
              <button type="button" onClick={handleRemoveImage} disabled={isLoading}>
                Remove Image
              </button>
            </div>
          )}
        </div>
        <div className="add-post-form-wrapper">
          <button type="submit" disabled={isLoading}>
            {isLoading ? "Posting..." : "Post"}
          </button>
          <button
            type="button"
            onClick={() => {
              setMovieData({
                title: "",
                director: "",
                releaseDate: "",
                description: "",
              });
              setSelectedCategory("");
              setImage(null);
              setImagePreview(null); // Clear the preview when cancel is clicked
            }}
            disabled={isLoading}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}