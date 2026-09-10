function LinkTable({ links, onEdit, onDelete, isSearchActive }) {
    if (links.length === 0) {
        return (
            <section className="link-section link-state empty-state">
                <div className="state-icon" aria-hidden="true">
                    {isSearchActive ? "⌕" : "＋"}
                </div>

                <h2>
                    {isSearchActive
                        ? "No products found"
                        : "Your saved products will appear here"}
                </h2>

                <p>
                    {isSearchActive
                        ? "Try a different search term or clear your search to see all your saved products."
                        : "Save a product from any store and keep everything you want to buy in one place."}
                </p>
            </section>
        );
    }

    return (
        <section className="link-section">
            <div className="link-section-header">
                <div>
                    <h2>Saved Products</h2>

                    <p>
                        Products you&apos;re keeping an eye on.
                    </p>
                </div>
            </div>

            <div className="table-wrapper">
                <table className="link-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Source</th>
                            <th className="actions-heading">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        {links.map((link) => (
                            <tr key={link.id}>
                                <td className="product-cell">
                                    <div className="product-info">
                                        <span className="product-title">
                                            {link.title}
                                        </span>
                                    </div>
                                </td>

                                <td className="source-cell">
                                    <span
                                        className="product-url"
                                        title={link.url}
                                    >
                                        {link.url}
                                    </span>
                                </td>

                                <td className="actions-cell">
                                    <div className="product-actions">
                                        <a
                                            className="open-button"
                                            href={link.url}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                        >
                                            Open ↗
                                        </a>

                                        <button
                                            className="edit-button"
                                            type="button"
                                            onClick={() => onEdit(link)}
                                        >
                                            Edit
                                        </button>

                                        <button
                                            className="delete-button"
                                            type="button"
                                            onClick={() => onDelete(link)}
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </section>
    );
}

export default LinkTable;