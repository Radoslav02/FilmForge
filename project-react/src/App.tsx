import { useEffect } from "react";
import "./App.css";
import { Outlet, useNavigate } from "react-router-dom";
import Header from "./components/Header/Header";

function App() {
  const navigate = useNavigate();

  useEffect(() => {
    navigate("/home");
  }, []);

  return (
    <div className="container-fluid">
      <Header />
      <Outlet />
    </div>
  );
}

export default App;
