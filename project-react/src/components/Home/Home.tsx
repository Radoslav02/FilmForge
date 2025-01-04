import { useEffect, useState } from "react";
import "./Home.css";


export default function Home() {
 
  const [categories, setCategories] = useState<{ id: number; name: string }[]>([]);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");

    const fetchCategories = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/categories/allCategories",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch categories.");
        }

        const data = await response.json();
        setCategories(data);
      } catch (err: any) {
        console.log(err);
      }
    };

    fetchCategories();
  }, []);

  return (
    <div className="home-container">
      <div className="categories-bar">
        <button className="category-button">All</button>
        {categories.map((category) => (
          <button key={category.id} className="category-button">
            {category.name}
          </button>
        ))}
      </div>
      <h1>Ovo je home page</h1>
    </div>
  );
}
