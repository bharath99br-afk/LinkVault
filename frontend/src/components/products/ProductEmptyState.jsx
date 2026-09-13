function ProductEmptyState({ hasFilters }) {
    return (
        <div className="product-empty-state">
            <div className="product-empty-icon">
                {hasFilters ? "⌕" : "+"}
            </div>

            <h2>
                {hasFilters
                    ? "No products found"
                    : "No saved products yet"}
            </h2>

            <p>
                {hasFilters
                    ? "Try a different product name or category."
                    : "Save your first product to start building your personal shopping vault."}
            </p>
        </div>
    );
}

export default ProductEmptyState;