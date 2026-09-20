import { useEffect, useState } from "react";

import {
    addOfferBankApplicability,
    addOfferCardApplicability,
    createOffer,
    getOffers,
} from "../services/offerService";

import { getBanks } from "../services/bankService";
import { getGlobalMerchants } from "../services/merchantService";

import AddOfferForm from "../components/offers/AddOfferForm";
import OfferList from "../components/offers/OfferList";
import OfferEmptyState from "../components/offers/OfferEmptyState";
import Notification from "../components/Notification";

function Offers() {
    const [offers, setOffers] = useState([]);

    const [banks, setBanks] = useState([]);
    const [globalMerchants, setGlobalMerchants] =
        useState([]);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [searchTerm, setSearchTerm] = useState("");

    const [showAddForm, setShowAddForm] =
        useState(false);

    const [loading, setLoading] = useState(true);
    const [banksLoading, setBanksLoading] =
        useState(false);
    const [globalMerchantsLoading, setGlobalMerchantsLoading] =
        useState(false);

    const [submitting, setSubmitting] =
        useState(false);

    const [notification, setNotification] =
        useState({
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

            setOffers(response.data.content);
            setCurrentPage(response.data.page);
            setTotalPages(response.data.totalPages);
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

    const loadOfferFormData = async () => {
        setBanksLoading(true);
        setGlobalMerchantsLoading(true);

        try {
            const [
                banksResponse,
                merchantsResponse,
            ] = await Promise.all([
                getBanks({
                    page: 0,
                    size: 100,
                }),
                getGlobalMerchants({
                    page: 0,
                    size: 100,
                }),
            ]);

            setBanks(
                banksResponse?.data?.content || []
            );

            setGlobalMerchants(
                merchantsResponse?.data?.content || []
            );
        } catch (error) {
            console.error(
                "Offer form data error:",
                error
            );

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to load offer form data.",
                type: "error",
            });
        } finally {
            setBanksLoading(false);
            setGlobalMerchantsLoading(false);
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

    const handleOpenAddForm = () => {
        setShowAddForm(true);
        loadOfferFormData();

        window.requestAnimationFrame(() => {
            window.scrollTo({
                top: 0,
                behavior: "smooth",
            });
        });
    };

    const handleAddOffer = async ({
        offer,
        applicabilityMode,
        bankIds,
        cardProductIds,
    }) => {
        setSubmitting(true);

        try {
            const response = await createOffer(
                offer
            );

            const createdOfferId =
                response?.data?.id;

            if (!createdOfferId) {
                throw new Error(
                    "Offer was created but its ID was not returned."
                );
            }

            if (
                applicabilityMode === "BANKS"
            ) {
                for (const bankId of bankIds) {
                    await addOfferBankApplicability(
                        createdOfferId,
                        bankId
                    );
                }
            }

            if (
                applicabilityMode === "CARDS"
            ) {
                for (const cardProductId of cardProductIds) {
                    await addOfferCardApplicability(
                        createdOfferId,
                        cardProductId
                    );
                }
            }

            setShowAddForm(false);

            setNotification({
                message:
                    "Offer added successfully.",
                type: "success",
            });

            setSearchTerm("");

            await loadOffers("", 0);
        } catch (error) {
            console.error(
                "Offer create error:",
                error
            );

            setNotification({
                message:
                    error.data?.message ||
                    error.message ||
                    "Failed to add offer.",
                type: "error",
            });
        } finally {
            setSubmitting(false);
        }
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

                    <h1>Offers</h1>

                    <p>
                        Keep your discounts, merchant deals,
                        and payment benefits in one place.
                    </p>
                </div>

                <button
                    type="button"
                    className="offers-add-button"
                    onClick={handleOpenAddForm}
                >
                    + Add Offer
                </button>
            </section>

            {showAddForm && (
                <AddOfferForm
                    globalMerchants={
                        globalMerchants
                    }
                    globalMerchantsLoading={
                        globalMerchantsLoading
                    }
                    banks={banks}
                    banksLoading={banksLoading}
                    onSubmit={handleAddOffer}
                    onCancel={() =>
                        setShowAddForm(false)
                    }
                    submitting={submitting}
                />
            )}

            <section className="offers-section">
                <div className="offers-search-row">
                    <div className="offers-search-group">
                        <label htmlFor="offer-search">
                            Find an offer
                        </label>

                        <input
                            id="offer-search"
                            type="text"
                            value={searchTerm}
                            placeholder="Search offers by title..."
                            onChange={(event) =>
                                setSearchTerm(
                                    event.target.value
                                )
                            }
                            onKeyDown={(event) => {
                                if (
                                    event.key ===
                                    "Enter"
                                ) {
                                    handleSearch(
                                        event
                                            .currentTarget
                                            .value
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
                                handleSearch(
                                    searchTerm
                                )
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
                            Finding your offers...
                        </p>
                    </div>
                ) : offers.length === 0 ? (
                    <OfferEmptyState
                        searching={Boolean(
                            searchTerm.trim()
                        )}
                    />
                ) : (
                    <OfferList
                        offers={offers}
                    />
                )}

                {!loading && totalPages > 1 && (
                    <div className="offers-pagination">
                        <button
                            type="button"
                            disabled={
                                currentPage === 0
                            }
                            onClick={() =>
                                handlePageChange(
                                    currentPage - 1
                                )
                            }
                        >
                            Previous
                        </button>

                        <span>
                            Page{" "}
                            {currentPage + 1} of{" "}
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