import { useEffect, useState } from "react";
import "./App.css";
import { getLinks } from "./services/linkService";
import SearchBar from "./components/SearchBar";
import LinkTable from "./components/LinkTable";

function App() {

  const [links, setLinks] = useState([]);

  const loadLinks = async (title = "") => {

    try {

      const response = await getLinks({
        title: title,
        page: 0,
        size: 5
      });

      setLinks(response.data.content);

    } catch (error) {

      console.error("API Error:", error);

    }
  };

  useEffect(() => {
    loadLinks();
  }, []);

  const handleSearch = (searchTerm) => {
    loadLinks(searchTerm);
  };

  return (
    <div className="app">

      <header className="navbar">
        <h1>LinkVault</h1>
        <span>Link Management</span>
      </header>

      <main className="container">

        <section className="hero">
          <h2>Your Links</h2>
          <p>Manage all your important links in one place.</p>
        </section>

        <SearchBar onSearch={handleSearch} />

        <LinkTable links={links} />

      </main>

    </div>
  );
}

export default App;