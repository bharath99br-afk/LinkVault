function MerchantEmptyState({ hasSearch }) {
    return (
        <div className="merchant-empty-state">
            <div className="merchant-empty-icon">
                🏪
            </div>

            <h2>
                {hasSearch
                    ? "No merchants found"
                    : "No merchants yet"}
            </h2>

            <p>
                {hasSearch
                    ? "Try a different search term."
                    : "Add the stores you regularly shop from to make your purchase decisions smarter."}
            </p>
        </div>
    );
}

export default MerchantEmptyState;