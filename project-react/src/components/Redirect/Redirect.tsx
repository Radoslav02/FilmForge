import React from "react";
import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { RootState } from "../Redux/store";


const Redirect: React.FC = () => {
  const user = useSelector((state: RootState) => state.auth.user);

  return user?.id ? <Navigate to="/home" replace /> : <Navigate to="/explore" replace />;
};

export default Redirect;
