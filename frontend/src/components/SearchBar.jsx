function SearchBar() {
    return (
        <div className="search-bar">
            <input
                type="text"
                placeholder="Search links..."
            />

            <button>
                Search
            </button>
        </div>
    );
}

export default SearchBar;