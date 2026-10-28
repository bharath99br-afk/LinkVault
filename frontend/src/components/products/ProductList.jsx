import ProductCard from "./ProductCard";

function ProductList({
    products,
    onEdit,
    onDelete,
    onFindBestDeal,
}) {
    return (
        <div className="products-list">
            {products.map((product) => (
                <ProductCard
                    key={product.id}
                    product={product}
                    onEdit={onEdit}
                    onDelete={onDelete}
                    onFindBestDeal={onFindBestDeal}
                />
            ))}
        </div>
    );
}

export default ProductList;
