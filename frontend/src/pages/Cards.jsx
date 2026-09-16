import { useEffect, useState, useRef } from "react";

import {
    createCard,
    deleteCard,
    getCards,
    updateCard,
} from "../services/cardService";

import { getBanks } from "../services/bankService";

import AddCardForm from "../components/cards/AddCardForm";
import EditCardForm from "../components/cards/EditCardForm";
import CardList from "../components/cards/CardList";
import CardEmptyState from "../components/cards/CardEmptyState";
import ConfirmModal from "../components/ConfirmModal";
import Notification from "../components/Notification";

function Cards() {
    const [cards, setCards] = useState([]);
    const [banks, setBanks] = useState([]);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [searchTerm, setSearchTerm] = useState("");

    const [showAddForm, setShowAddForm] = useState(false);
    const [editingCard, setEditingCard] = useState(null);
    const [deletingCard, setDeletingCard] = useState(null);

    const [loading, setLoading] = useState(true);
    const [banksLoading, setBanksLoading] = useState(true);

    const [submitting, setSubmitting] = useState(false);

    const [notification, setNotification] = useState({
        message: "",
        type: "",
    });
    const editFormRef = useRef(null);

    const loadCards = async (
        name = "",
        page = 0
    ) => {
        setLoading(true);

        try {
            const response = await getCards({
                name,
                page,
                size: 6,
            });

            setCards(response.data.content);
            setCurrentPage(response.data.page);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error("Card load error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to load cards.",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    const loadBanks = async () => {
        setBanksLoading(true);

        try {
            const response = await getBanks({
                page: 0,
                size: 100,
            });

            setBanks(response.data.content);
        } catch (error) {
            console.error("Bank load error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to load banks.",
                type: "error",
            });
        } finally {
            setBanksLoading(false);
        }
    };

    useEffect(() => {
        loadCards();
        loadBanks();
    }, []);

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

    useEffect(() => {
        if (!editingCard) {
            return;
        }

        requestAnimationFrame(() => {
            editFormRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        });
    }, [editingCard]);

    const handleSearch = (term) => {
        const normalizedTerm = term.trim();

        setSearchTerm(normalizedTerm);
        loadCards(normalizedTerm, 0);
    };

    const handlePageChange = (page) => {
        loadCards(searchTerm, page);
    };

    const handleAddCard = async (card) => {
        setSubmitting(true);

        try {
            await createCard(card);

            setShowAddForm(false);
            setNotification({
                message: "Card added successfully.",
                type: "success",
            });

            setSearchTerm("");
            loadCards("", 0);
        } catch (error) {
            console.error("Card create error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to add card.",
                type: "error",
            });
        } finally {
            setSubmitting(false);
        }
    };

    const handleUpdateCard = async (card) => {
        setSubmitting(true);

        try {
            await updateCard(editingCard.id, card);

            setEditingCard(null);

            setNotification({
                message: "Card updated successfully.",
                type: "success",
            });

            loadCards(searchTerm, currentPage);
        } catch (error) {
            console.error("Card update error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to update card.",
                type: "error",
            });
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteCard = async () => {
        if (!deletingCard) {
            return;
        }

        setSubmitting(true);

        try {
            await deleteCard(deletingCard.id);

            setDeletingCard(null);

            setNotification({
                message: "Card deleted successfully.",
                type: "success",
            });

            if (
                cards.length === 1 &&
                currentPage > 0
            ) {
                loadCards(
                    searchTerm,
                    currentPage - 1
                );
            } else {
                loadCards(
                    searchTerm,
                    currentPage
                );
            }
        } catch (error) {
            console.error("Card delete error:", error);

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to delete card.",
                type: "error",
            });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <main className="container cards-page">
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

            <section className="cards-header">
                <div>
                    <p className="cards-eyebrow">
                        Your payment toolkit
                    </p>

                    <h1>Cards</h1>

                    <p>
                        Save the cards you use so LinkVault
                        can connect your purchases with
                        eligible offers.
                    </p>
                </div>

                <button
                    type="button"
                    className="cards-add-button"
                    onClick={() => {
                        setEditingCard(null);
                        setShowAddForm(true);
                    }}
                >
                    + Add Card
                </button>
            </section>

            {showAddForm && (
                <AddCardForm
                    banks={banks}
                    banksLoading={banksLoading}
                    onSubmit={handleAddCard}
                    onCancel={() =>
                        setShowAddForm(false)
                    }
                    submitting={submitting}
                />
            )}

            {editingCard && (
                <div ref={editFormRef}>
                    <EditCardForm
                        card={editingCard}
                        banks={banks}
                        banksLoading={banksLoading}
                        onSubmit={handleUpdateCard}
                        onCancel={() =>
                            setEditingCard(null)
                        }
                        submitting={submitting}
                    />
                </div>
            )}

            <section className="cards-section">
                <div className="cards-search-row">
                    <div className="cards-search-group">
                        <label htmlFor="card-search">
                            Search
                        </label>

                        <input
                            id="card-search"
                            type="text"
                            value={searchTerm}
                            placeholder="Search your cards..."
                            onChange={(event) =>
                                setSearchTerm(
                                    event.target.value
                                )
                            }
                            onKeyDown={(event) => {
                                if (
                                    event.key === "Enter"
                                ) {
                                    handleSearch(
                                        event.currentTarget
                                            .value
                                    );
                                }
                            }}
                        />
                    </div>

                    <button
                        type="button"
                        className="cards-search-button"
                        onClick={() =>
                            handleSearch(searchTerm)
                        }
                    >
                        Search
                    </button>
                    {searchTerm.trim() && (
                        <button
                            type="button"
                            className="cards-clear-button"
                            onClick={() =>
                                handleSearch("")
                            }
                        >
                            Clear
                        </button>
                    )}
                </div>

                {loading ? (
                    <div className="cards-loading">
                        Loading your cards...
                    </div>
                ) : cards.length === 0 ? (
                    <CardEmptyState
                        searching={Boolean(
                            searchTerm.trim()
                        )}
                    />
                ) : (
                    <CardList
                        cards={cards}
                        onEdit={(card) => {
                            setShowAddForm(false);
                            setEditingCard(card);
                        }}
                        onDelete={(id) => {
                            const card = cards.find(
                                (item) =>
                                    item.id === id
                            );

                            setDeletingCard(card);
                        }}
                    />
                )}

                {totalPages > 1 && (
                    <div className="cards-pagination">
                        <button
                            type="button"
                            disabled={currentPage === 0}
                            onClick={() =>
                                handlePageChange(
                                    currentPage - 1
                                )
                            }
                        >
                            Previous
                        </button>

                        <span>
                            Page {currentPage + 1} of{" "}
                            {totalPages}
                        </span>

                        <button
                            type="button"
                            disabled={
                                currentPage >=
                                totalPages - 1
                            }
                            onClick={() =>
                                handlePageChange(
                                    currentPage + 1
                                )
                            }
                        >
                            Next
                        </button>
                    </div>
                )}
            </section>

            <ConfirmModal
                isOpen={Boolean(deletingCard)}
                title="Delete card?"
                message={
                    deletingCard
                        ? `Remove "${deletingCard.name}" from your saved cards?`
                        : ""
                }
                confirmText={
                    submitting
                        ? "Deleting..."
                        : "Delete Card"
                }
                cancelText="Cancel"
                onConfirm={handleDeleteCard}
                onCancel={() => {
                    if (!submitting) {
                        setDeletingCard(null);
                    }
                }}
            />
        </main>
    );
}

export default Cards;