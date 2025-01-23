import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";
import "./Messages.css";
import SendIcon from "@mui/icons-material/Send";

interface User {
  id: number;
  username: string;
}

interface Message {
  id: number;
  senderId: number;
  receiverId: number;
  text: string;
  sentDate: string;
}

export default function Messages() {
  const [friends, setFriends] = useState<User[]>([]);
  const [filteredFriends, setFilteredFriends] = useState<User[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [messageText, setMessageText] = useState<string>("");
  const [searchText, setSearchText] = useState<string>("");
  const user = useSelector((state: RootState) => state.auth.user);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
      console.error("No token found in local storage");
      setLoading(false);
      return;
    }

    const fetchFriends = async () => {
      try {
        const userId = user?.id;
        if (!userId) {
          console.error("No user ID found in local storage");
          setLoading(false);
          return;
        }

        const response = await fetch(
          `http://localhost:8080/api/requests/friends/${userId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch friends.");
        }

        const data = await response.json();
        setFriends(data);
        setFilteredFriends(data);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching friends:", err);
        setLoading(false);
      }
    };

    fetchFriends();
  }, [user]);

  useEffect(() => {
    const lowercasedSearchText = searchText.toLowerCase();
    setFilteredFriends(
      friends.filter((friend) =>
        friend.username.toLowerCase().includes(lowercasedSearchText)
      )
    );
  }, [searchText, friends]);

  const fetchMessages = async (receiverId: number) => {
    const token = localStorage.getItem("jwtToken");
    if (!token || !user?.id) return;

    const response = await fetch(
      `http://localhost:8080/api/messages/conversation?senderId=${user.id}&receiverId=${receiverId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (response.ok) {
      const data = await response.json();
      setMessages(data);
    }
  };

  const handleUserClick = (user: User) => {
    setSelectedUser(user);
    fetchMessages(user.id);
  };

  const handleSendMessage = async () => {
    if (!messageText || !selectedUser || !user?.id) return;

    const token = localStorage.getItem("jwtToken");

    const response = await fetch(
      `http://localhost:8080/api/messages/send?senderId=${user.id}&receiverId=${selectedUser.id}&text=${messageText}`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    if (response.ok) {
      const newMessage = await response.json();
      setMessages((prevMessages) => [...prevMessages, newMessage]);
      setMessageText("");
    }
  };

  return (
    <div className="message-container">
      <h1 className="your-friends-title">Inbox</h1>
      <div className="chat-friends-wrapper">
        <div className="friends-container">
          <input
            type="text"
            placeholder="Search friends..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="friends-search"
          />
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ul className="friends-ul">
              {filteredFriends.length > 0 ? (
                filteredFriends.map((friend) => (
                  <li
                    className="friend-li"
                    key={friend.id}
                    onClick={() => handleUserClick(friend)}
                  >
                    {friend.username}
                  </li>
                ))
              ) : (
                <p>No friends found.</p>
              )}
            </ul>
          )}
        </div>
        {selectedUser && (
          <div className="chat-container">
            <h3 className="chat-title">Chat with {selectedUser.username}</h3>
            <div>
              <ul className="chat-ul">
                {messages.map((message) => (
                  <li
                    className={`message-li ${
                      message.senderId === (user?.id ?? -1)
                        ? "my-message"
                        : "friend-message"
                    }`}
                    key={message.id}
                  >
                    <strong>
                      {message.senderId === (user?.id ?? -1)
                        ? "You"
                        : selectedUser?.username || "Unknown User"}
                      :
                    </strong>
                    <p>{message.text}</p>
                    <small>
                      {new Date(message.sentDate).toLocaleTimeString()}
                    </small>
                  </li>
                ))}
              </ul>
            </div>
            <div
              className="send-message-wrapper"
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleSendMessage();
                }
              }}
            >
              <textarea
                value={messageText}
                onChange={(e) => setMessageText(e.target.value)}
                placeholder="Type your message"
                className="message-area"
              ></textarea>
              <div className="send-icon-wrapper">
                <SendIcon
                  className="send-icon"
                  sx={{ fontSize: 25 }}
                  onClick={handleSendMessage}
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
