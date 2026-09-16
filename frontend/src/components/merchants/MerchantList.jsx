function MerchantList({
    merchants,
    onEdit,
    onDelete,
}) {
    return (
        <div className="merchants-list">
            {merchants.map((merchant) => (
                <article
                    key={merchant.id}
                    className="merchant-card"
                >
                    <div className="merchant-card-main">
                        <div className="merchant-card-icon">
                            {merchant.name
                                ?.charAt(0)
                                .toUpperCase() || "M"}
                        </div>

                        <div className="merchant-card-details">
                            <div className="merchant-card-title-row">
                                <h3>{merchant.name}</h3>

                                {merchant.globalMerchantName && (
                                    <span className="merchant-recognized-badge">
                                        ✓ Recognized
                                    </span>
                                )}
                            </div>

                            <a
                                href={merchant.websiteUrl}
                                target="_blank"
                                rel="noreferrer"
                                className="merchant-website"
                            >
                                {merchant.websiteUrl}
                            </a>

                            {merchant.globalMerchantName && (
                                <p className="merchant-global-info">
                                    Linked to{" "}
                                    <strong>
                                        {merchant.globalMerchantName}
                                    </strong>{" "}
                                    in LinkVault's merchant
                                    directory.
                                </p>
                            )}
                        </div>
                    </div>

                    <div className="merchant-actions">
                        <a
                            href={merchant.websiteUrl}
                            target="_blank"
                            rel="noreferrer"
                            className="merchant-open-button"
                        >
                            Open ↗
                        </a>

                        <button
                            type="button"
                            className="merchant-edit-button"
                            onClick={() => onEdit(merchant)}
                        >
                            Edit
                        </button>

                        <button
                            type="button"
                            className="merchant-delete-button"
                            onClick={() => onDelete(merchant)}
                        >
                            Delete
                        </button>
                    </div>
                </article>
            ))}
        </div>
    );
}

export default MerchantList;