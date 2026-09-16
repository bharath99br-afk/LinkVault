import {
    useCallback,
    useEffect,
    useMemo,
    useRef,
    useState,
} from "react";

import {
    createMerchant,
    deleteMerchant,
    getGlobalMerchants,
    getMerchants,
    updateMerchant,
} from "../services/merchantService";

import AddMerchantForm from "../components/merchants/AddMerchantForm";
import EditMerchantForm from "../components/merchants/EditMerchantForm";
import MerchantEmptyState from "../components/merchants/MerchantEmptyState";
import MerchantList from "../components/merchants/MerchantList";
import ConfirmModal from "../components/ConfirmModal";

function Merchants() {
    const [merchants, setMerchants] = useState([]);
    const [globalMerchants, setGlobalMerchants] =
        useState([]);

    const [loading, setLoading] = useState(true);
    const [globalMerchantsLoading, setGlobalMerchantsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [notification, setNotification] =
        useState(null);

    const [searchInput, setSearchInput] = useState("");
    const [search, setSearch] = useState("");

    const [showAddForm, setShowAddForm] =
        useState(false);
    const [editingMerchant, setEditingMerchant] =
        useState(null);

    const [deleteMerchantTarget, setDeleteMerchantTarget] =
        useState(null);

    const [submitting, setSubmitting] =
        useState(false);

    const merchantFormRef = useRef(null);

    const showNotification = (type, message) => {
        setNotification({
            type,
            message,
        });

        window.setTimeout(() => {
            setNotification(null);
        }, 3500);
    };

    const loadMerchants = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const response = await getMerchants({
                page: 0,
                size: 100,
            });

            setMerchants(
                response?.data?.content || []
            );
        } catch (requestError) {
            console.error(
                "Failed to load merchants:",
                requestError
            );

            setMerchants([]);

            setError(
                requestError?.message ||
                "Unable to load your merchants."
            );
        } finally {
            setLoading(false);
        }
    }, []);

    const loadGlobalMerchants = useCallback(async () => {
        setGlobalMerchantsLoading(true);

        try {
            const response =
                await getGlobalMerchants({
                    page: 0,
                    size: 100,
                });

            setGlobalMerchants(
                response?.data?.content || []
            );
        } catch (requestError) {
            console.error(
                "Failed to load global merchants:",
                requestError
            );

            setGlobalMerchants([]);
        } finally {
            setGlobalMerchantsLoading(false);
        }
    }, []);

    useEffect(() => {
        loadMerchants();
    }, [loadMerchants]);

    useEffect(() => {
        loadGlobalMerchants();
    }, [loadGlobalMerchants]);

    useEffect(() => {
        if (!showAddForm && !editingMerchant) {
            return;
        }

        window.requestAnimationFrame(() => {
            merchantFormRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        });
    }, [showAddForm, editingMerchant]);

    const handleSearch = (event) => {
        event.preventDefault();

        setSearch(searchInput.trim());
    };

    const handleClearSearch = () => {
        setSearchInput("");
        setSearch("");
    };

    const filteredMerchants = useMemo(() => {
        if (!search) {
            return merchants;
        }

        const normalizedSearch =
            search.toLowerCase();

        return merchants.filter((merchant) => {
            return (
                merchant.name
                    ?.toLowerCase()
                    .includes(normalizedSearch) ||
                merchant.websiteUrl
                    ?.toLowerCase()
                    .includes(normalizedSearch) ||
                merchant.globalMerchantName
                    ?.toLowerCase()
                    .includes(normalizedSearch)
            );
        });
    }, [merchants, search]);

    const handleAddMerchant = async (merchant) => {
        setSubmitting(true);

        try {
            await createMerchant(merchant);

            setShowAddForm(false);

            await loadMerchants();

            showNotification(
                "success",
                "Merchant saved successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to save the merchant."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const handleUpdateMerchant = async (
        merchantData
    ) => {
        if (!editingMerchant) {
            return;
        }

        setSubmitting(true);

        try {
            await updateMerchant(
                editingMerchant.id,
                merchantData
            );

            setEditingMerchant(null);

            await loadMerchants();

            showNotification(
                "success",
                "Merchant updated successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to update the merchant."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteMerchant = (merchant) => {
        setDeleteMerchantTarget(merchant);
    };

    const confirmDeleteMerchant = async () => {
        if (!deleteMerchantTarget) {
            return;
        }

        const merchant = deleteMerchantTarget;

        setDeleteMerchantTarget(null);

        try {
            await deleteMerchant(merchant.id);

            await loadMerchants();

            showNotification(
                "success",
                "Merchant deleted successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to delete the merchant."
            );
        }
    };

    const hasSearch = Boolean(search);

    return (
        <main className="merchants-page">
            <div className="container">
                {notification && (
                    <div
                        className={`merchants-notification ${notification.type}`}
                        role="status"
                    >
                        <span>
                            {notification.message}
                        </span>

                        <button
                            type="button"
                            onClick={() =>
                                setNotification(null)
                            }
                            aria-label="Close notification"
                        >
                            ×
                        </button>
                    </div>
                )}

                <header className="merchants-header">
                    <div>
                        <p className="merchants-eyebrow">
                            Your shopping ecosystem
                        </p>

                        <h1>Merchants</h1>

                        <p>
                            Keep your shopping destinations
                            organized and connected to
                            LinkVault's merchant intelligence.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="merchants-add-button"
                        onClick={() => {
                            setEditingMerchant(null);
                            setShowAddForm(true);
                        }}
                    >
                        + Add Merchant
                    </button>
                </header>

                {showAddForm && (
                    <div ref={merchantFormRef}>
                        <AddMerchantForm
                            globalMerchants={
                                globalMerchants
                            }
                            globalMerchantsLoading={
                                globalMerchantsLoading
                            }
                            onSubmit={
                                handleAddMerchant
                            }
                            onCancel={() =>
                                setShowAddForm(false)
                            }
                            submitting={submitting}
                        />
                    </div>
                )}

                {editingMerchant && (
                    <div ref={merchantFormRef}>
                        <EditMerchantForm
                            merchant={
                                editingMerchant
                            }
                            globalMerchants={
                                globalMerchants
                            }
                            globalMerchantsLoading={
                                globalMerchantsLoading
                            }
                            onSubmit={
                                handleUpdateMerchant
                            }
                            onCancel={() =>
                                setEditingMerchant(null)
                            }
                            submitting={submitting}
                        />
                    </div>
                )}

                <section className="merchants-content-card">
                    <form
                        className="merchants-filter-bar"
                        onSubmit={handleSearch}
                    >
                        <div className="merchants-search-field">
                            <label htmlFor="merchant-search">
                                Search
                            </label>

                            <input
                                id="merchant-search"
                                type="text"
                                value={searchInput}
                                onChange={(event) =>
                                    setSearchInput(
                                        event.target.value
                                    )
                                }
                                placeholder="Search your merchants..."
                            />
                        </div>

                        <div className="merchants-filter-actions">
                            <button
                                type="submit"
                                className="merchants-search-button"
                            >
                                Search
                            </button>

                            {hasSearch && (
                                <button
                                    type="button"
                                    className="merchants-clear-button"
                                    onClick={
                                        handleClearSearch
                                    }
                                >
                                    Clear
                                </button>
                            )}
                        </div>
                    </form>

                    {loading ? (
                        <div className="merchants-loading">
                            <div className="merchants-spinner" />

                            <p>
                                Loading your merchants...
                            </p>
                        </div>
                    ) : error ? (
                        <div className="merchants-error-state">
                            <h2>
                                We couldn't load your
                                merchants
                            </h2>

                            <p>{error}</p>

                            <button
                                type="button"
                                className="merchants-secondary-button"
                                onClick={
                                    loadMerchants
                                }
                            >
                                Try again
                            </button>
                        </div>
                    ) : filteredMerchants.length > 0 ? (
                        <MerchantList
                            merchants={
                                filteredMerchants
                            }
                            onEdit={(merchant) => {
                                setShowAddForm(false);
                                setEditingMerchant(
                                    merchant
                                );
                            }}
                            onDelete={
                                handleDeleteMerchant
                            }
                        />
                    ) : (
                        <MerchantEmptyState
                            hasSearch={hasSearch}
                        />
                    )}
                </section>
            </div>

            <ConfirmModal
                isOpen={Boolean(
                    deleteMerchantTarget
                )}
                title="Delete merchant?"
                message={
                    deleteMerchantTarget
                        ? `Are you sure you want to delete "${deleteMerchantTarget.name}" from your merchants?`
                        : ""
                }
                confirmText="Delete Merchant"
                cancelText="Keep Merchant"
                onConfirm={
                    confirmDeleteMerchant
                }
                onCancel={() =>
                    setDeleteMerchantTarget(null)
                }
            />
        </main>
    );
}

export default Merchants;