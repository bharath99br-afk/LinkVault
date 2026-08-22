import { useEffect, useState } from "react";
import "./App.css";
import { getLinks, deleteLink } from "./services/linkService";
import SearchBar from "./components/SearchBar";
import LinkTable from "./components/LinkTable";
import Pagination from "./components/Pagination";
import AddLinkForm from "./components/AddLinkForm";
import EditLinkForm from "./components/EditLinkForm";
import Notification from "./components/Notification";

function App() {

  const [links, setLinks] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingLink, setEditingLink] = useState(null);
  const [notification, setNotification] = useState({
    message: "",
    type: ""
  });

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

    if (!notification.message) {
      return;
    }

    const timer = setTimeout(() => {

      setNotification({
        message: "",
        type: ""
      });

    }, 3000);

    return () => clearTimeout(timer);

  }, [notification]);

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

  const handleDelete = async (id) => {

    const confirmed = window.confirm(
      "Are you sure you want to delete this link?"
    );

    if (!confirmed) {
      return;
    }

    try {

      await deleteLink(id);

      setNotification({
        message: "Link deleted successfully",
        type: "success"
      });

      if (links.length === 1 && currentPage > 0) {
        loadLinks(searchTerm, currentPage - 1);
      } else {
        loadLinks(searchTerm, currentPage);
      }

    } catch (error) {

      console.error("Delete Error:", error);

      setNotification({
        message: "Failed to delete link",
        type: "error"
      });

    }
  };

  return (
    <div className="app">

      <Notification
        message={notification.message}
        type={notification.type}
        onClose={() =>
          setNotification({
            message: "",
            type: ""
          })
        }
      />

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

            onNotification={setNotification}

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

            onNotification={setNotification}

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
          onDelete={handleDelete}
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