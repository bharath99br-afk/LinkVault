import { useEffect, useState } from "react";
import "./App.css";
import { getLinks } from "./services/linkService";
import SearchBar from "./components/SearchBar";
import LinkTable from "./components/LinkTable";
import Pagination from "./components/Pagination";
import AddLinkForm from "./components/AddLinkForm";
import EditLinkForm from "./components/EditLinkForm";

function App() {

  const [links, setLinks] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingLink, setEditingLink] = useState(null);

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
          <div className="page-header">

            <div>
              <h1>Your Links</h1>
              <p>Manage all your important links in one place.</p>
            </div>

            <button
              className="add-link-button"
              onClick={() => setShowAddForm(true)}
            >
              + Add Link
            </button>

          </div>
        </section>

        {showAddForm && (
          <AddLinkForm
            onCancel={() => setShowAddForm(false)}
            onLinkAdded={() => {
              setShowAddForm(false);
              setSearchTerm("");
              loadLinks("", 0);
            }}
          />
        )}

        {editingLink && (
          <EditLinkForm
            link={editingLink}
            onCancel={() => setEditingLink(null)}
            onLinkUpdated={() => {
              setEditingLink(null);
              loadLinks(searchTerm, currentPage);
            }}
          />
        )}

        <SearchBar onSearch={handleSearch} />

        <LinkTable
          links={links}
          onEdit={(link) => setEditingLink(link)}
        />

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