function Pagination({
    currentPage,
    totalPages,
    onPageChange
}) {
    if (totalPages <= 1) {
        return null;
    }

    return (
        <div className="pagination">

            <button
                disabled={currentPage === 0}
                onClick={() => onPageChange(currentPage - 1)}
            >
                Previous
            </button>

            {Array.from({ length: totalPages }, (_, index) => (
                <button
                    key={index}
                    className={currentPage === index ? "active" : ""}
                    onClick={() => onPageChange(index)}
                >
                    {index + 1}
                </button>
            ))}

            <button
                disabled={currentPage === totalPages - 1}
                onClick={() => onPageChange(currentPage + 1)}
            >
                Next
            </button>

        </div>
    );
}

export default Pagination;