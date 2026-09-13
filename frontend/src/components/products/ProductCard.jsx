import { getProductDomain } from "../../utils/productUtils";

function ProductCard({
    product,
    onEdit,
    onDelete,
}) {
    const domain = getProductDomain(product.websiteUrl);

    return (
        <article className="product-card">
            <div className="product-card-image">
                {product.imageUrl ? (
                    <img
                        src={product.imageUrl}
                        alt={product.name}
                        onError={(event) => {
                            event.currentTarget.style.display = "none";
                            event.currentTarget.nextElementSibling.style.display =
                                "flex";
                        }}
                    />
                ) : null}

                <div
                    className="product-image-placeholder"
                    style={{
                        display: product.imageUrl ? "none" : "flex",
                    }}
                    aria-hidden="true"
                >
                    <span>LV</span>
                </div>
            </div>

            <div className="product-card-content">
                <div className="product-card-top">
                    <div>
                        <h3 title={product.name}>
                            {product.name}
                        </h3>

                        {product.category && (
                            <span className="product-category">
                                {product.category}
                            </span>
                        )}
                    </div>
                </div>

                {product.description && (
                    <p className="product-description">
                        {product.description}
                    </p>
                )}

                <div className="product-meta">
                    <span>
                        {product.merchantName ||
                            product.globalMerchantName ||
                            "No merchant"}
                    </span>

                    {domain && (
                        <span className="product-domain">
                            {domain}
                        </span>
                    )}
                </div>

                <div className="product-actions">
                    <a
                        href={product.websiteUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="product-open-button"
                    >
                        Open ↗
                    </a>

                    <button
                        type="button"
                        className="product-edit-button"
                        onClick={() => onEdit(product)}
                    >
                        Edit
                    </button>

                    <button
                        type="button"
                        className="product-delete-button"
                        onClick={() => onDelete(product)}
                    >
                        Delete
                    </button>
                </div>
            </div>
        </article>
    );
}

export default ProductCard;