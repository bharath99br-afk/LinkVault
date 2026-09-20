function OfferEmptyState({ searching }) {
    return (
        <div className="offers-empty-state">
            <div className="offers-empty-icon">
                $
            </div>

            {searching ? (
                <>
                    <h2>No offers found</h2>

                    <p>
                        We couldn't find any offers matching your search.
                        Try another title or clear your search.
                    </p>
                </>
            ) : (
                <>
                    <h2>Your savings vault is empty</h2>

                    <p>
                        Add offers to LinkVault and keep your discounts,
                        cashback opportunities, and payment benefits in one place.
                    </p>
                </>
            )}
        </div>
    );
}

export default OfferEmptyState;