function CardCard({ card, onEdit, onDelete }) {
    const lastFour = card.lastFourDigits || "----";

    return (
        <article className="card-item">
            <div className="card-visual">
                <div className="card-visual-top">
                    <span className="card-visual-label">
                        LinkVault Card
                    </span>

                    <span className="card-type-badge">
                        {card.cardType}
                    </span>
                </div>

                <div className="card-number">
                    •••• •••• •••• {lastFour}
                </div>

                <div className="card-visual-bottom">
                    <span>{card.name}</span>
                    <span>{card.bankName || "Bank"}</span>
                </div>
            </div>

            <div className="card-item-actions">
                <button
                    type="button"
                    className="card-edit-button"
                    onClick={() => onEdit(card)}
                >
                    Edit
                </button>

                <button
                    type="button"
                    className="card-delete-button"
                    onClick={() => onDelete(card.id)}
                >
                    Delete
                </button>
            </div>
        </article>
    );
}

export default CardCard;