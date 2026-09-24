function OfferEmptyState({ searching }) {
    return (
        <div className="offers-empty-state">
            <div className="offers-empty-icon">
                ✦
            </div>

            {searching ? (
                <>
                    <h2>
                        No matching offers
                    </h2>

                    <p>
                        We couldn't find an offer
                        matching your search. Try
                        another keyword or explore all
                        available offers.
                    </p>
                </>
            ) : (
                <>
                    <h2>
                        No offers available yet
                    </h2>

                    <p>
                        We're building your offer
                        discovery space. Check back as
                        more merchant deals and payment
                        offers become available.
                    </p>
                </>
            )}
        </div>
    );
}

export default OfferEmptyState;