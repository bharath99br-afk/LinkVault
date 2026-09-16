import CardCard from "./CardCard";

function CardList({
    cards,
    onEdit,
    onDelete,
}) {
    return (
        <div className="cards-list">
            {cards.map((card) => (
                <CardCard
                    key={card.id}
                    card={card}
                    onEdit={onEdit}
                    onDelete={onDelete}
                />
            ))}
        </div>
    );
}

export default CardList;