import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom'; 
import { RootState } from '../Redux/store';
import "./SearchedUserProfile.css";
import { toast } from 'react-toastify';

interface Movie {
  id: number;
  title: string;
  director: string;
  releaseDate: string;
  categoryName: string;
  description: string;
  imageUrl: string | null;
}

interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  movies: Movie[];
}

const SearchedUserProfile: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [movies, setMovies] = useState<Movie[]>([]);
  const [isSendingRequest, setIsSendingRequest] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const user = useSelector((state: RootState) => state.auth.user);
  const [expandedDescription, setExpandedDescription] = useState<number | null>(
    null
  );

  const handleViewMore = (movieId: number) => {
    setExpandedDescription(expandedDescription === movieId ? null : movieId);
  };


  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
          setError('No token found. Please log in.');
          return;
        }

        const response = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/users/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`, 
          },
        });

        if (!response.ok) throw new Error('Failed to fetch profile.');
        const userProfile: UserProfile = await response.json();

        const moviesResponse = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/users/${userId}/movies`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!moviesResponse.ok) throw new Error('Failed to fetch movies.');
        const movies = await moviesResponse.json();

        if (Array.isArray(movies)) {
          setMovies(movies);
        } else {
          setMovies([]);
        }

        setProfile({
          ...userProfile,
          movies: movies,
        });
      } catch (err: any) {
        setError(err.message || 'An error occurred');
      }
    };

    fetchProfile();
  }, [userId]);

  const handleSendFriendRequest = async () => {
    try {
      setIsSendingRequest(true);
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        setError('No token found. Please log in.');
        return;
      }

      const senderId = user?.id;

      const response = await fetch(`${import.meta.env.VITE_APP_API_URL}/api/requests/send?senderId=${senderId}&receiverId=${userId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        toast.success('Friend request sent successfully');
      } else {
        throw new Error('Failed to send friend request');
      }
    } catch (err: any) {
      setError(err.message || 'An error occurred');
    } finally {
      setIsSendingRequest(false);
    }
  };

  if (error) return <p>{error}</p>;
  if (!profile) return <p>Loading...</p>;

  return (
    <div className="searched-user-profile-container">

      <div className="profile-name-wrapper">
      <h1>{profile.firstName} {profile.lastName} ({profile.username})</h1>
      <button className="send-request-button" onClick={handleSendFriendRequest} disabled={isSendingRequest}>
        {isSendingRequest ? 'Sending...' : 'Send Friend Request'}
      </button>
      </div>

     
      {movies.length > 0 ? (
        <div className="movies-container">
          {movies.map((movie) => (
            <div className="movie-card" key={movie.id}>
              <div className="profile-movie-info-wrapper">
              <p>Title: {movie.title}</p>
              <p><strong>Director:</strong> {movie.director}</p>
              <p><strong>Release Date:</strong> {new Date(movie.releaseDate).toLocaleDateString()}</p>
              <p><strong>Category:</strong> {movie.categoryName}</p>
              <div className="profile-desc-wrapper">
                <p className="profile-desc-p">
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
              {movie.imageUrl && (
                <img
                  src={`${import.meta.env.VITE_APP_API_URL}${movie.imageUrl}`}
                  alt={movie.title}
                  className="profile-movie-image"
                />
              )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p>This user has no movies added yet.</p>
      )}
    </div>
  );
};

export default SearchedUserProfile;
