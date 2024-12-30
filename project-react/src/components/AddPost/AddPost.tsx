import "./AddPost.css";

export default function AddPost() {
  return (
    <div className="add-post-container">
      <form className="add-post-form">
        <div className="add-post-form-wrapper">
          <h1>Post Movie</h1>
        </div>
        <div className="add-post-form-wrapper">
          <label>Movie title:</label>
          <input type="text" />
        </div>
        <div className="add-post-form-wrapper">
          <label>Director:</label>
          <input type="text" />
        </div>
        <div className="add-post-form-wrapper">
          <label>Release Date:</label>
          <input type="text" />
        </div>
        <div className="add-post-form-wrapper">
          <label>Description:</label>
          <textarea />
        </div>
        <div className="add-post-form-wrapper">
          <label>Upload image:</label>
          <input type="file" />
        </div>
        <div className="add-post-form-wrapper">
            <button>Post</button>
            <button>Cancel</button>
        </div>
      </form>
    </div>
  );
}
