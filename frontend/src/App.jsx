import { useEffect, useState } from "react";
import "./App.css";
import { getLinks } from "./services/linkService";
import SearchBar from "./components/SearchBar";
import LinkTable from "./components/LinkTable";
import Pagination from "./components/Pagination";

function App() {

  const [links, setLinks] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");

  const loadLinks = async (title = "", page = 0) => {

    try {

      const response = await getLinks({
        title: title,
        page: page,
        size: 5
      });

      setLinks(response.data.content);
      setCurrentPage(response.data.page);
      setTotalPages(response.data.totalPages);

    } catch (error) {

      console.error("API Error:", error);

    }
  };

  useEffect(() => {
    loadLinks();
  }, []);

  const handleSearch = (term) => {
    setSearchTerm(term);
    loadLinks(term, 0);
  };

  const handlePageChange = (page) => {
    loadLinks(searchTerm, page);
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

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </main>

    </div>
  );
}

export default App;