import { useEffect, useState } from "react";

import {
    getSavedOffers,
    unsaveOffer,
} from "../services/offerService";

import SavedOfferList from "../components/offers/SavedOfferList";
import SavedOfferEmptyState from "../components/offers/SavedOfferEmptyState";
import Notification from "../components/Notification";
import ConfirmModal from "../components/ConfirmModal";

function SavedOffers() {
    const [offers, setOffers] = useState([]);

    const [loading, setLoading] = useState(true);

    const [unsavingOfferIds, setUnsavingOfferIds] =
        useState(new Set());

    const [unsaveTarget, setUnsaveTarget] =
        useState(null);

    const [notification, setNotification] = useState({
        message: "",
        type: "",
    });

    const loadSavedOffers = async () => {
        setLoading(true);

        try {
            const response = await getSavedOffers();

            setOffers(response?.data || []);
        } catch (error) {
            console.error(
                "Saved offers load error:",
                error
            );

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to load saved offers.",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadSavedOffers();
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

    const handleUnsaveOffer = (offerId) => {
        const offer = offers.find(
            (item) => item.offerId === offerId
        );

        if (!offer) {
            return;
        }

        setUnsaveTarget(offer);
    };

    const confirmUnsaveOffer = async () => {
        if (!unsaveTarget) {
            return;
        }

        const offerId = unsaveTarget.offerId;

        setUnsavingOfferIds((current) => {
            const next = new Set(current);
            next.add(offerId);
            return next;
        });

        try {
            await unsaveOffer(offerId);

            setOffers((current) =>
                current.filter(
                    (offer) =>
                        offer.offerId !== offerId
                )
            );

            setNotification({
                message:
                    "Offer removed from your savings vault.",
                type: "success",
            });
        } catch (error) {
            console.error(
                "Unsave offer error:",
                error
            );

            setNotification({
                message:
                    error.data?.message ||
                    "Failed to remove offer.",
                type: "error",
            });
        } finally {
            setUnsavingOfferIds((current) => {
                const next = new Set(current);
                next.delete(offerId);
                return next;
            });

            setUnsaveTarget(null);
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

                    <h1>Saved Offers</h1>

                    <p>
                        Keep the offers that matter to
                        you in one place and revisit them
                        when you're ready to buy.
                    </p>
                </div>
            </section>

            <section className="offers-discovery-intro">
                <div className="offers-discovery-icon">
                    ♡
                </div>

                <div>
                    <strong>
                        Your saved deals, ready when
                        you are.
                    </strong>

                    <p>
                        Offers stay connected to their
                        original details, so you always
                        see the latest information.
                    </p>
                </div>
            </section>

            <section className="offers-section">
                {loading ? (
                    <div className="offers-loading">
                        <div className="offers-loading-icon">
                            ✦
                        </div>

                        <p>
                            Loading your saved offers...
                        </p>
                    </div>
                ) : offers.length === 0 ? (
                    <SavedOfferEmptyState />
                ) : (
                    <SavedOfferList
                        offers={offers}
                        unsavingOfferIds={
                            unsavingOfferIds
                        }
                        onUnsaveOffer={
                            handleUnsaveOffer
                        }
                    />
                )}
            </section>
            <ConfirmModal
                isOpen={Boolean(unsaveTarget)}
                title="Remove saved offer?"
                message={
                    unsaveTarget
                        ? `Remove "${unsaveTarget.title}" from your savings vault?`
                        : ""
                }
                confirmText={
                    unsaveTarget &&
                        unsavingOfferIds.has(
                            unsaveTarget.offerId
                        )
                        ? "Removing..."
                        : "Remove Offer"
                }
                cancelText="Keep Offer"
                onConfirm={confirmUnsaveOffer}
                onCancel={() => {
                    if (
                        !unsaveTarget ||
                        !unsavingOfferIds.has(
                            unsaveTarget.offerId
                        )
                    ) {
                        setUnsaveTarget(null);
                    }
                }}
            />
        </main>
    );
}

export default SavedOffers;