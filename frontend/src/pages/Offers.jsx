import { useEffect, useState } from "react";

import { getOffers } from "../services/offerService";

import OfferList from "../components/offers/OfferList";
import OfferEmptyState from "../components/offers/OfferEmptyState";
import Notification from "../components/Notification";

function Offers() {
    const [offers, setOffers] = useState([]);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [searchTerm, setSearchTerm] = useState("");

    const [loading, setLoading] = useState(true);

    const [notification, setNotification] = useState({
        message: "",
        type: "",
    });

    const loadOffers = async (
        title = "",
        page = 0
    ) => {
        setLoading(true);

        try {
            const response = await getOffers({
                title,
                page,
                size: 10,
            });

            setOffers(response?.data?.content || []);
            setCurrentPage(response?.data?.page || 0);
            setTotalPages(response?.data?.totalPages || 0);
        } catch (error) {
            console.error(
                "Offer load error:",
                error
            );

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to load offers.",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOffers();
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
        }, 3500);

        return () => clearTimeout(timer);
    }, [notification]);

    const handleSearch = (term) => {
        const normalizedTerm = term.trim();

        setSearchTerm(normalizedTerm);
        loadOffers(normalizedTerm, 0);
    };

    const handlePageChange = (page) => {
        loadOffers(searchTerm, page);
    };

    return (
        <main className="container offers-page">
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

            <section className="offers-header">
                <div>
                    <p className="offers-eyebrow">
                        Your savings vault
                    </p>

                    <h1>Discover Offers</h1>

                    <p>
                        Explore discounts, merchant deals,
                        cashback opportunities, and payment
                        benefits — all in one place.
                    </p>
                </div>
            </section>

            <section className="offers-discovery-intro">
                <div className="offers-discovery-icon">
                    ✦
                </div>

                <div>
                    <strong>
                        Find a deal before you pay.
                    </strong>

                    <p>
                        Browse offers from across merchants
                        and payment providers. Save the ones
                        that matter to you as your personal
                        savings vault grows.
                    </p>
                </div>
            </section>

            <section className="offers-section">
                <div className="offers-search-row">
                    <div className="offers-search-group">
                        <label htmlFor="offer-search">
                            Search offers
                        </label>

                        <input
                            id="offer-search"
                            type="text"
                            value={searchTerm}
                            placeholder="Search by offer title..."
                            onChange={(event) =>
                                setSearchTerm(
                                    event.target.value
                                )
                            }
                            onKeyDown={(event) => {
                                if (event.key === "Enter") {
                                    handleSearch(
                                        event.currentTarget.value
                                    );
                                }
                            }}
                        />
                    </div>

                    <div className="offers-search-actions">
                        <button
                            type="button"
                            className="offers-search-button"
                            onClick={() =>
                                handleSearch(searchTerm)
                            }
                        >
                            Search
                        </button>

                        {searchTerm.trim() && (
                            <button
                                type="button"
                                className="offers-clear-button"
                                onClick={() =>
                                    handleSearch("")
                                }
                            >
                                Clear
                            </button>
                        )}
                    </div>
                </div>


                {loading ? (
                    <div className="offers-loading">
                        <div className="offers-loading-icon">
                            ✦
                        </div>

                        <p>
                            Finding offers for you...
                        </p>
                    </div>
                ) : offers.length === 0 ? (
                    <OfferEmptyState
                        searching={Boolean(
                            searchTerm.trim()
                        )}
                    />
                ) : (
                    <OfferList offers={offers} />
                )}

                {!loading && totalPages > 1 && (
                    <div className="offers-pagination">
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
        </main>
    );
}

export default Offers;