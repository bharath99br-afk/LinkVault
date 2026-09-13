import ProductCard from "./ProductCard";

function ProductList({
    products,
    onEdit,
    onDelete,
}) {
    return (
        <div className="products-list">
            {products.map((product) => (
                <ProductCard
                    key={product.id}
                    product={product}
                    onEdit={onEdit}
                    onDelete={onDelete}
                />
            ))}
        </div>
    );
}

export default ProductList;