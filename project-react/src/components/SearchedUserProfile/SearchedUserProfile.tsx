import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom'; 
import { RootState } from '../Redux/store';

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

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
          setError('No token found. Please log in.');
          return;
        }

        const response = await fetch(`http://localhost:8080/api/users/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`, 
          },
        });

        if (!response.ok) throw new Error('Failed to fetch profile.');
        const userProfile: UserProfile = await response.json();

        const moviesResponse = await fetch(`http://localhost:8080/api/users/${userId}/movies`, {
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

      const response = await fetch(`http://localhost:8080/api/requests/send?senderId=${senderId}&receiverId=${userId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        alert('Friend request sent successfully');
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
    <div>
      <h1>{profile.firstName} {profile.lastName} ({profile.username})</h1>
      <button onClick={handleSendFriendRequest} disabled={isSendingRequest}>
        {isSendingRequest ? 'Sending...' : 'Send Friend Request'}
      </button>

      <h2>Movies Added by {profile.username}</h2>
      {movies.length > 0 ? (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {movies.map((movie) => (
            <li key={movie.id} style={{ marginBottom: '20px', border: '1px solid #ccc', padding: '10px', borderRadius: '5px' }}>
              <h3>{movie.title}</h3>
              <p><strong>Director:</strong> {movie.director}</p>
              <p><strong>Release Date:</strong> {new Date(movie.releaseDate).toLocaleDateString()}</p>
              <p><strong>Category:</strong> {movie.categoryName}</p>
              <p><strong>Description:</strong> {movie.description}</p>
              {movie.imageUrl && (
                <img src={`http://localhost:8080${movie.imageUrl}`} alt={movie.title} style={{ maxWidth: '200px', display: 'block', marginTop: '10px' }} />
              )}
            </li>
          ))}
        </ul>
      ) : (
        <p>This user has no movies added yet.</p>
      )}
    </div>
  );
};

export default SearchedUserProfile;
