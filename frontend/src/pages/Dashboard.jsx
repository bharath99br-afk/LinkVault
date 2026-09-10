import { useEffect, useState } from "react";
import { getDashboardSummary } from "../services/dashboardService";

function Dashboard() {
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadDashboard = async () => {
            try {
                const response = await getDashboardSummary();
                setSummary(response.data);
            } catch (error) {
                console.error("Dashboard API Error:", error);

                setError(
                    error.data?.message ||
                    "Failed to load dashboard"
                );
            } finally {
                setLoading(false);
            }
        };

        loadDashboard();
    }, []);

    if (loading) {
        return (
            <main className="container dashboard-page">
                <div className="dashboard-loading">
                    Loading your dashboard...
                </div>
            </main>
        );
    }

    if (error) {
        return (
            <main className="container dashboard-page">
                <div className="dashboard-error">
                    {error}
                </div>
            </main>
        );
    }

    return (
        <main className="container dashboard-page">

            {/* Dashboard header */}
            <section className="dashboard-header">
                <div>
                    <p className="dashboard-eyebrow">
                        Your LinkVault
                    </p>

                    <h1>
                        Welcome back, {summary.userName}.
                    </h1>

                    <p>
                        Keep track of what you want to buy
                        and make smarter payment decisions.
                    </p>
                </div>
            </section>

            {/* Dashboard summary cards */}
            <section className="dashboard-summary-grid">

                <div className="dashboard-stat-card">
                    <span className="dashboard-stat-label">
                        Links
                    </span>

                    <strong>
                        {summary.totalLinks}
                    </strong>

                    <span className="dashboard-stat-description">
                        Products you are tracking
                    </span>
                </div>

                <div className="dashboard-stat-card">
                    <span className="dashboard-stat-label">
                        Products
                    </span>

                    <strong>
                        {summary.totalProducts}
                    </strong>

                    <span className="dashboard-stat-description">
                        Saved products
                    </span>
                </div>

                <div className="dashboard-stat-card">
                    <span className="dashboard-stat-label">
                        Merchants
                    </span>

                    <strong>
                        {summary.totalMerchants}
                    </strong>

                    <span className="dashboard-stat-description">
                        Stores in your vault
                    </span>
                </div>

                <div className="dashboard-stat-card">
                    <span className="dashboard-stat-label">
                        Cards
                    </span>

                    <strong>
                        {summary.totalCards}
                    </strong>

                    <span className="dashboard-stat-description">
                        Payment cards available
                    </span>
                </div>

            </section>

            {/* Active offers highlight */}
            <section className="dashboard-offer-card">

                <div>
                    <span className="dashboard-offer-label">
                        Active offers
                    </span>

                    <h2>
                        {summary.activeOffers}
                    </h2>

                    <p>
                        Offers currently available in LinkVault.
                    </p>
                </div>

                <div className="dashboard-offer-icon">
                    %
                </div>

            </section>

            {/* Dashboard actions */}
            <section className="dashboard-actions">

                <div className="dashboard-section-heading">
                    <div>
                        <h2>
                            Continue with LinkVault
                        </h2>

                        <p>
                            Manage the things that help you
                            save more at checkout.
                        </p>
                    </div>
                </div>

                <div className="dashboard-action-grid">

                    <a
                        href="/links"
                        className="dashboard-action-card"
                    >
                        <span className="dashboard-action-icon">
                            ↗
                        </span>

                        <div>
                            <h3>
                                Manage your links
                            </h3>

                            <p>
                                View, search and organize
                                your saved products.
                            </p>
                        </div>
                    </a>

                    <div className="dashboard-action-card dashboard-action-card-disabled">
                        <span className="dashboard-action-icon">
                            +
                        </span>

                        <div>
                            <h3>
                                Add your cards
                            </h3>

                            <p>
                                Coming next — connect your
                                cards to unlock better deals.
                            </p>
                        </div>
                    </div>

                </div>

            </section>

        </main>
    );
}

export default Dashboard;