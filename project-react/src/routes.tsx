import { createHashRouter } from "react-router-dom";
import App from "./App";
import Home from "./components/Home/Home";
import Login from "./components/Login/Login";
import Register from "./components/Register/Register";
import Profile from "./components/Profile/Profile";
import PrivateRoute from "./components/PrivateRoute/PrivateRoute";
import VerifyEmail from "./components/MailVerification/MailVerification";
import CheckEmail from "./components/CheckMail/CheckMail";
import AddPost from "./components/AddPost/AddPost";
import SearchedUserProfile from "./components/SearchedUserProfile/SearchedUserProfile";
import ShowFriendRequests from "./components/ShowFriendRequest/ShowFriendRequests";
import Messages from "./components/Messages/Messages";
 // Import UserProfile component

export const router = createHashRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        path: "/home",
        element: <Home />,
      },
      {
        path: "/login",
        element: <Login />,
      },
      {
        path: "/register",
        element: <Register />,
      },
      {
        path: "/profile",
        element: (
          <PrivateRoute>
            <Profile />
          </PrivateRoute>
        ),
      },
      {
        path: "/verify-email",
        element: <VerifyEmail />,
      },
      {
        path: "/check-email",
        element: <CheckEmail />,
      },
      {
        path: "/add-post",
        element: <AddPost />,
      },
      {
        path: "/user/:userId", 
        element: <SearchedUserProfile />, 
      },
      {
        path: "/friend-request", 
        element: <ShowFriendRequests />, 
      },
      {
        path: "/messages", 
        element: <Messages />, 
      }
    ],
  },
]);
