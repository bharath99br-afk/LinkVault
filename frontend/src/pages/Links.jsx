import { useEffect, useRef, useState } from "react";

import { getLinks, deleteLink } from "../services/linkService";

import SearchBar from "../components/SearchBar";
import LinkTable from "../components/LinkTable";
import Pagination from "../components/Pagination";
import AddLinkForm from "../components/AddLinkForm";
import EditLinkForm from "../components/EditLinkForm";
import Notification from "../components/Notification";
import ConfirmModal from "../components/ConfirmModal";

function Links() {
    const [links, setLinks] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchTerm, setSearchTerm] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [editingLink, setEditingLink] = useState(null);
    const [pendingDeleteLink, setPendingDeleteLink] = useState(null);

    // Tracks whether a delete request is currently in progress.
    const [deleting, setDeleting] = useState(false);

    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");

    const [notification, setNotification] = useState({
        message: "",
        type: "",
    });

    // Used to automatically bring the edit form into the user's viewport.
    const editFormRef = useRef(null);

    const loadLinks = async (title = "", page = 0) => {
        setLoading(true);
        setLoadError("");

        try {
            const response = await getLinks({
                title,
                page,
                size: 5,
            });

            setLinks(response.data.content);
            setCurrentPage(response.data.page);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error("API Error:", error);

            setLoadError(
                error.data?.message ||
                "We couldn't load your saved products. Please try again."
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadLinks();
    }, []);

    // Scroll to the edit form whenever a product enters edit mode.
    useEffect(() => {
        if (!editingLink || !editFormRef.current) {
            return;
        }

        requestAnimationFrame(() => {
            editFormRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        });
    }, [editingLink]);

    useEffect(() => {
        if (!notification.message) {
            return;
        }

        const timer = setTimeout(() => {
            setNotification({
                message: "",
                type: "",
            });
        }, 3000);

        return () => clearTimeout(timer);
    }, [notification]);

    const handleSearch = (term) => {
        setSearchTerm(term);
        loadLinks(term, 0);
    };

    const handlePageChange = (page) => {
        loadLinks(searchTerm, page);
    };

    const handleDeleteRequest = (link) => {
        setPendingDeleteLink(link);
    };

    const handleDeleteConfirm = async () => {
        if (!pendingDeleteLink || deleting) {
            return;
        }

        setDeleting(true);

        try {
            await deleteLink(pendingDeleteLink.id);

            setPendingDeleteLink(null);

            setNotification({
                message: "Product removed successfully",
                type: "success",
            });

            if (links.length === 1 && currentPage > 0) {
                await loadLinks(searchTerm, currentPage - 1);
            } else {
                await loadLinks(searchTerm, currentPage);
            }
        } catch (error) {
            console.error("Delete Error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to remove the saved product",
                type: "error",
            });
        } finally {
            setDeleting(false);
        }
    };

    const handleRetry = () => {
        loadLinks(searchTerm, currentPage);
    };

    const isSearchActive = Boolean(searchTerm);

    return (
        <main className="container">
            <Notification
                message={notification.message}
                type={notification.type}
                onClose={() =>
                    setNotification({
                        message: "",
                        type: "",
                    })
                }
            />

            {/* ---------- Delete Confirmation Modal ---------- */}

            {pendingDeleteLink && (
                <ConfirmModal
                    isOpen={Boolean(pendingDeleteLink)}
                    title="Remove saved product?"
                    message={`Are you sure you want to remove "${pendingDeleteLink.title}"? This action cannot be undone.`}
                    confirmText={deleting ? "Removing..." : "Remove Product"}
                    cancelText="Cancel"
                    onConfirm={handleDeleteConfirm}
                    onCancel={() => {
                        if (!deleting) {
                            setPendingDeleteLink(null);
                        }
                    }}
                />
            )}

            {/* ---------- Page Header ---------- */}

            <section className="hero">
                <div className="page-header">
                    <div>
                        <span className="page-eyebrow">
                            Your shopping collection
                        </span>

                        <h1>Your Saved Products</h1>

                        <p>
                            Save products you&apos;re interested in and let
                            LinkVault help you make smarter purchase decisions.
                        </p>
                    </div>

                    <button
                        className="add-link-button"
                        onClick={() => setShowAddForm(true)}
                    >
                        + Save Product
                    </button>
                </div>
            </section>

            {/* ---------- Add Product Form ---------- */}

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

            {/* ---------- Edit Product Form ---------- */}

            {editingLink && (
                <div ref={editFormRef} className="edit-form-anchor">
                    <EditLinkForm
                        link={editingLink}
                        onCancel={() => setEditingLink(null)}
                        onNotification={setNotification}
                        onLinkUpdated={() => {
                            setEditingLink(null);
                            loadLinks(searchTerm, currentPage);
                        }}
                    />
                </div>
            )}

            {/* ---------- Search ---------- */}

            <SearchBar onSearch={handleSearch} />

            {/* ---------- Loading State ---------- */}

            {loading && (
                <section className="link-section link-state">
                    <div className="loading-spinner" aria-hidden="true"></div>

                    <h2>Loading your saved products...</h2>

                    <p>
                        Give us a moment while we fetch your shopping
                        collection.
                    </p>
                </section>
            )}

            {/* ---------- Load Error State ---------- */}

            {!loading && loadError && (
                <section className="link-section link-state error-state">
                    <div className="state-icon" aria-hidden="true">
                        !
                    </div>

                    <h2>Something went wrong</h2>

                    <p>{loadError}</p>

                    <button
                        className="retry-button"
                        onClick={handleRetry}
                    >
                        Try Again
                    </button>
                </section>
            )}

            {/* ---------- Product Results ---------- */}

            {!loading && !loadError && (
                <>
                    <LinkTable
                        links={links}
                        onEdit={(link) => setEditingLink(link)}
                        onDelete={handleDeleteRequest}
                        isSearchActive={isSearchActive}
                    />

                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />
                </>
            )}
        </main>
    );
}

export default Links;