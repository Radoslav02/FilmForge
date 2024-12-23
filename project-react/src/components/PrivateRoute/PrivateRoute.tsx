import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";

const PrivateRoute = ({ children }: { children: JSX.Element }) => {
  const user = useSelector((state: RootState) => state.auth.user);

  if (!user) {
    return <Navigate to="/login" />;
  }

  return children;
};

export default PrivateRoute;
