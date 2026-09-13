import { useCallback, useEffect, useState, useRef } from "react";

import {
    createProduct,
    deleteProduct,
    getProducts,
    updateProduct,
} from "../services/productService";

import { getMerchants } from "../services/merchantService";

import AddProductForm from "../components/products/AddProductForm";
import EditProductForm from "../components/products/EditProductForm";
import ProductEmptyState from "../components/products/ProductEmptyState";
import ProductList from "../components/products/ProductList";
import ConfirmModal from "../components/ConfirmModal";


function Products() {
    const [products, setProducts] = useState([]);

    const [merchants, setMerchants] = useState([]);

    const [loading, setLoading] = useState(true);
    const [merchantsLoading, setMerchantsLoading] =
        useState(true);

    const [page, setPage] = useState(0);
    const [pageData, setPageData] = useState(null);

    const [searchInput, setSearchInput] = useState("");
    const [categoryInput, setCategoryInput] = useState("");

    const [search, setSearch] = useState("");
    const [category, setCategory] = useState("");

    const [showAddForm, setShowAddForm] = useState(false);
    const [editingProduct, setEditingProduct] =
        useState(null);

    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");
    const [notification, setNotification] = useState(null);
    const [deleteProductTarget, setDeleteProductTarget] =
        useState(null);
    const productFormRef = useRef(null);

    const showNotification = (type, message) => {
        setNotification({
            type,
            message,
        });

        window.setTimeout(() => {
            setNotification(null);
        }, 3500);
    };

    const loadProducts = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const response = await getProducts({
                name: search,
                category,
                page,
                size: 8,
            });

            const data = response?.data;

            setProducts(data?.content || []);
            setPageData(data || null);
        } catch (requestError) {
            console.error(
                "Failed to load products:",
                requestError
            );

            setProducts([]);
            setPageData(null);
            setError(
                requestError?.message ||
                "Unable to load your products."
            );
        } finally {
            setLoading(false);
        }
    }, [search, category, page]);

    const loadMerchants = useCallback(async () => {
        setMerchantsLoading(true);

        try {
            const response = await getMerchants({
                page: 0,
                size: 100,
            });

            setMerchants(
                response?.data?.content || []
            );
        } catch (requestError) {
            console.error(
                "Failed to load merchants:",
                requestError
            );

            setMerchants([]);
        } finally {
            setMerchantsLoading(false);
        }
    }, []);

    useEffect(() => {
        loadProducts();
    }, [loadProducts]);

    useEffect(() => {
        loadMerchants();
    }, [loadMerchants]);

    useEffect(() => {
        if (!showAddForm && !editingProduct) {
            return;
        }

        window.requestAnimationFrame(() => {
            productFormRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        });
    }, [showAddForm, editingProduct]);

    const handleSearch = (event) => {
        event.preventDefault();

        setPage(0);
        setSearch(searchInput.trim());
        setCategory(categoryInput.trim());
    };

    const handleClearFilters = () => {
        setSearchInput("");
        setCategoryInput("");
        setSearch("");
        setCategory("");
        setPage(0);
    };

    const handleAddProduct = async (product) => {
        setSubmitting(true);

        try {
            await createProduct(product);

            setShowAddForm(false);

            setPage(0);

            if (page === 0) {
                await loadProducts();
            }

            showNotification(
                "success",
                "Product saved successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to save the product."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const handleUpdateProduct = async (productData) => {
        if (!editingProduct) {
            return;
        }

        setSubmitting(true);

        try {
            await updateProduct(
                editingProduct.id,
                productData
            );

            setEditingProduct(null);

            await loadProducts();

            showNotification(
                "success",
                "Product updated successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to update the product."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteProduct = (product) => {
        setDeleteProductTarget(product);
    };

    const confirmDeleteProduct = async () => {
        if (!deleteProductTarget) {
            return;
        }

        const product = deleteProductTarget;

        setDeleteProductTarget(null);

        try {
            await deleteProduct(product.id);

            if (
                products.length === 1 &&
                page > 0
            ) {
                setPage((current) => current - 1);
            } else {
                await loadProducts();
            }

            showNotification(
                "success",
                "Product deleted successfully."
            );
        } catch (requestError) {
            showNotification(
                "error",
                requestError?.message ||
                "Unable to delete the product."
            );
        }
    };

    const totalPages = pageData?.totalPages || 0;

    const hasFilters =
        Boolean(search) || Boolean(category);

    const hasProducts =
        (pageData?.totalElements || 0) > 0;

    const showPagination =
        !loading && totalPages > 1;

    return (
        <main className="products-page">
            <div className="container">
                {notification && (
                    <div
                        className={`products-notification ${notification.type}`}
                        role="status"
                    >
                        <span>
                            {notification.message}
                        </span>

                        <button
                            type="button"
                            onClick={() =>
                                setNotification(null)
                            }
                            aria-label="Close notification"
                        >
                            ×
                        </button>
                    </div>
                )}

                <header className="products-header">
                    <div>
                        <p className="products-eyebrow">
                            Your shopping vault
                        </p>

                        <h1>Saved Products</h1>

                        <p>
                            Keep products organized and ready
                            for smarter purchase decisions.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="products-add-button"
                        onClick={() => {
                            setEditingProduct(null);
                            setShowAddForm(true);
                        }}
                    >
                        + Add Product
                    </button>
                </header>

                {showAddForm && (
                    <div ref={productFormRef}>
                        <AddProductForm
                            merchants={merchants}
                            merchantsLoading={merchantsLoading}
                            onSubmit={handleAddProduct}
                            onCancel={() =>
                                setShowAddForm(false)
                            }
                            submitting={submitting}
                        />
                    </div>
                )}

                {editingProduct && (
                    <div ref={productFormRef}>
                        <EditProductForm
                            product={editingProduct}
                            merchants={merchants}
                            merchantsLoading={merchantsLoading}
                            onSubmit={handleUpdateProduct}
                            onCancel={() =>
                                setEditingProduct(null)
                            }
                            submitting={submitting}
                        />
                    </div>
                )}

                <section className="products-content-card">
                    <form
                        className="products-filter-bar"
                        onSubmit={handleSearch}
                    >
                        <div className="products-search-field">
                            <label
                                htmlFor="product-search"
                            >
                                Search
                            </label>

                            <input
                                id="product-search"
                                type="text"
                                value={searchInput}
                                onChange={(event) =>
                                    setSearchInput(
                                        event.target.value
                                    )
                                }
                                placeholder="Search your saved products..."
                            />
                        </div>

                        <div className="products-category-field">
                            <label
                                htmlFor="product-category-filter"
                            >
                                Category
                            </label>

                            <input
                                id="product-category-filter"
                                type="text"
                                value={categoryInput}
                                onChange={(event) =>
                                    setCategoryInput(
                                        event.target.value
                                    )
                                }
                                placeholder="e.g. Electronics"
                            />
                        </div>

                        <div className="products-filter-actions">
                            <button
                                type="submit"
                                className="products-search-button"
                            >
                                Search
                            </button>

                            {hasFilters && (
                                <button
                                    type="button"
                                    className="products-clear-button"
                                    onClick={
                                        handleClearFilters
                                    }
                                >
                                    Clear
                                </button>
                            )}
                        </div>
                    </form>

                    {loading ? (
                        <div className="products-loading">
                            <div className="products-spinner" />
                            <p>
                                Loading your saved products...
                            </p>
                        </div>
                    ) : error ? (
                        <div className="products-error-state">
                            <h2>
                                We couldn't load your
                                products
                            </h2>

                            <p>{error}</p>

                            <button
                                type="button"
                                className="products-secondary-button"
                                onClick={loadProducts}
                            >
                                Try again
                            </button>
                        </div>
                    ) : products.length > 0 ? (
                        <>
                            <ProductList
                                products={products}
                                onEdit={(product) => {
                                    setShowAddForm(false);
                                    setEditingProduct(
                                        product
                                    );
                                }}
                                onDelete={
                                    handleDeleteProduct
                                }
                            />

                            {showPagination && (
                                <div className="products-pagination">
                                    <button
                                        type="button"
                                        disabled={
                                            pageData?.first
                                        }
                                        onClick={() =>
                                            setPage(
                                                (current) =>
                                                    current - 1
                                            )
                                        }
                                    >
                                        ← Previous
                                    </button>

                                    <span>
                                        Page {page + 1} of{" "}
                                        {totalPages}
                                    </span>

                                    <button
                                        type="button"
                                        disabled={
                                            pageData?.last
                                        }
                                        onClick={() =>
                                            setPage(
                                                (current) =>
                                                    current + 1
                                            )
                                        }
                                    >
                                        Next →
                                    </button>
                                </div>
                            )}
                        </>
                    ) : (
                        <ProductEmptyState
                            hasFilters={
                                hasFilters || hasProducts
                            }
                        />
                    )}
                </section>
            </div>

            <ConfirmModal
                isOpen={Boolean(deleteProductTarget)}
                title="Delete product?"
                message={
                    deleteProductTarget
                        ? `Are you sure you want to delete "${deleteProductTarget.name}" from your saved products?`
                        : ""
                }
                confirmText="Delete Product"
                cancelText="Keep Product"
                onConfirm={confirmDeleteProduct}
                onCancel={() =>
                    setDeleteProductTarget(null)
                }
            />
        </main>
    );
}

export default Products;