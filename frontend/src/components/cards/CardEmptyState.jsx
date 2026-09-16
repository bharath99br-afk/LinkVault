function CardEmptyState({ searching }) {
    return (
        <div className="cards-empty-state">
            <div className="cards-empty-icon">
                ▣
            </div>

            <h2>
                {searching
                    ? "No cards found"
                    : "No cards added yet"}
            </h2>

            <p>
                {searching
                    ? "Try a different search term."
                    : "Add your cards to unlock personalized offer matching."}
            </p>
        </div>
    );
}

export default CardEmptyState;