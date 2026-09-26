import { useEffect, useState } from "react";

import {
    getOffers, saveOffer, getOfferCardApplicability, getOfferBankApplicability, createOffer,
    addOfferBankApplicability,
    addOfferCardApplicability,
} from "../services/offerService";

import OfferList from "../components/offers/OfferList";
import OfferEmptyState from "../components/offers/OfferEmptyState";
import Notification from "../components/Notification";
import { getCards } from "../services/cardService";

import AddOfferForm from "../components/offers/AddOfferForm";
import { getGlobalMerchants } from "../services/merchantService";
import { getBanks } from "../services/bankService";

function Offers() {
    const [offers, setOffers] = useState([]);

    const [savedOfferIds, setSavedOfferIds] = useState(
        new Set()
    );

    const [savingOfferIds, setSavingOfferIds] = useState(
        new Set()
    );

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [searchTerm, setSearchTerm] = useState("");

    const [loading, setLoading] = useState(true);

    const [notification, setNotification] = useState({
        message: "",
        type: "",
    });

    const [userCards, setUserCards] = useState([]);

    const [offerApplicability, setOfferApplicability] =
        useState({});

    const [applicabilityLoading, setApplicabilityLoading] =
        useState(false);

    const [showAddOfferForm, setShowAddOfferForm] = useState(false);

    const [banks, setBanks] = useState([]);
    const [banksLoading, setBanksLoading] = useState(false);

    const [globalMerchants, setGlobalMerchants] = useState([]);
    const [globalMerchantsLoading, setGlobalMerchantsLoading] = useState(false);

    const [offerSubmitting, setOfferSubmitting] = useState(false);

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

            const loadedOffers =
                response?.data?.content || [];
            setOffers(loadedOffers);
            setCurrentPage(response?.data?.page || 0);
            setTotalPages(response?.data?.totalPages || 0);
            loadOfferApplicability(
                loadedOffers
            );
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

    const loadUserCards = async () => {
        try {
            const response = await getCards({
                page: 0,
                size: 100,
            });

            setUserCards(
                response?.data?.content || []
            );
        } catch (error) {
            console.error(
                "User card load error:",
                error
            );

            setUserCards([]);
        }
    };

    const loadOfferApplicability = async (
        offerList
    ) => {
        if (!offerList.length) {
            setOfferApplicability({});
            return;
        }

        setApplicabilityLoading(true);

        try {
            const results = await Promise.all(
                offerList.map(async (offer) => {
                    const [
                        bankResponse,
                        cardResponse,
                    ] = await Promise.all([
                        getOfferBankApplicability(
                            offer.id
                        ),
                        getOfferCardApplicability(
                            offer.id
                        ),
                    ]);

                    return {
                        offerId: offer.id,
                        banks:
                            bankResponse?.data || [],
                        cards:
                            cardResponse?.data || [],
                    };
                })
            );

            const nextApplicability = {};

            results.forEach((result) => {
                nextApplicability[result.offerId] =
                    result;
            });

            setOfferApplicability(
                nextApplicability
            );
        } catch (error) {
            console.error(
                "Offer applicability load error:",
                error
            );

            setOfferApplicability({});
        } finally {
            setApplicabilityLoading(false);
        }
    };

    useEffect(() => {
        if (!showAddOfferForm) {
            return;
        }

        const loadOfferFormData = async () => {
            try {
                setBanksLoading(true);
                setGlobalMerchantsLoading(true);

                const [banksResponse, merchantsResponse] = await Promise.all([
                    getBanks({ page: 0, size: 100 }),
                    getGlobalMerchants({ page: 0, size: 100 }),
                ]);

                setBanks(banksResponse?.data?.content || []);
                setGlobalMerchants(merchantsResponse?.data?.content || []);
            } catch (error) {
                console.error("Failed to load offer form data:", error);

                setNotification({
                    message: "Failed to load offer form data.",
                    type: "error",
                });
            } finally {
                setBanksLoading(false);
                setGlobalMerchantsLoading(false);
            }
        };

        loadOfferFormData();
    }, [showAddOfferForm]);

    const handleCreateOffer = async ({
        offer,
        applicabilityMode,
        bankIds,
        cardProductIds,
    }) => {
        try {
            setOfferSubmitting(true);

            const createdOffer = await createOffer(offer);

            const offerId = createdOffer.id;

            if (applicabilityMode === "BANK") {
                await Promise.all(
                    bankIds.map((bankId) =>
                        addOfferBankApplicability(offerId, bankId)
                    )
                );
            }

            if (applicabilityMode === "CARD") {
                await Promise.all(
                    cardProductIds.map((cardProductId) =>
                        addOfferCardApplicability(offerId, cardProductId)
                    )
                );
            }

            setNotification({
                message: "Offer created successfully.",
                type: "success",
            });

            setShowAddOfferForm(false);

            await loadOffers(searchTerm, currentPage);
        } catch (error) {
            console.error("Failed to create offer:", error);

            setNotification({
                message: error.message || "Failed to create offer.",
                type: "error",
            });
        } finally {
            setOfferSubmitting(false);
        }
    };

    useEffect(() => {
        loadOffers();
        loadUserCards();
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

    const handleSaveOffer = async (offerId) => {
        if (savedOfferIds.has(offerId)) {
            return;
        }

        setSavingOfferIds((current) => {
            const next = new Set(current);
            next.add(offerId);
            return next;
        });

        try {
            await saveOffer(offerId);

            setSavedOfferIds((current) => {
                const next = new Set(current);
                next.add(offerId);
                return next;
            });

            setNotification({
                message: "Offer saved to your savings vault.",
                type: "success",
            });
        } catch (error) {
            console.error(
                "Save offer error:",
                error
            );

            if (error.status === 409) {
                setSavedOfferIds((current) => {
                    const next = new Set(current);
                    next.add(offerId);
                    return next;
                });

                setNotification({
                    message: "Offer is already saved.",
                    type: "info",
                });
            } else {
                setNotification({
                    message:
                        error.data?.message ||
                        "Failed to save offer.",
                    type: "error",
                });
            }
        } finally {
            setSavingOfferIds((current) => {
                const next = new Set(current);
                next.delete(offerId);
                return next;
            });
        }
    };

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

                {/* {!showAddOfferForm && (
                    <div className="offers-test-action">
                        <button
                            type="button"
                            className="offers-add-button"
                            onClick={() => setShowAddOfferForm(true)}
                        >
                            + Add Offer
                        </button>
                    </div>
                )} */}
                {showAddOfferForm && (
                    <AddOfferForm
                        globalMerchants={globalMerchants}
                        globalMerchantsLoading={globalMerchantsLoading}
                        banks={banks}
                        banksLoading={banksLoading}
                        onSubmit={handleCreateOffer}
                        onCancel={() => setShowAddOfferForm(false)}
                        submitting={offerSubmitting}
                    />
                )}



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
                    <OfferList
                        offers={offers}
                        savedOfferIds={savedOfferIds}
                        savingOfferIds={savingOfferIds}
                        onSaveOffer={handleSaveOffer}
                        userCards={userCards}
                        offerApplicability={offerApplicability}
                        applicabilityLoading={applicabilityLoading}
                    />
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