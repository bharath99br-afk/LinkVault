import { useState } from "react";

function SearchBar({ onSearch }) {

    const [searchTerm, setSearchTerm] = useState("");

    const handleSearch = () => {
        onSearch(searchTerm);
    };

    const handleClear = () => {
        setSearchTerm("");
        onSearch("");
    };

    return (
        <div className="search-bar">

            <input
                type="text"
                placeholder="Search links..."
                value={searchTerm}
                onChange={(event) => setSearchTerm(event.target.value)}
            />

            <button onClick={handleSearch}>
                Search
            </button>

            <button
                className="clear-button"
                onClick={handleClear}
            >
                Clear
            </button>

        </div>
    );
}

export default SearchBar;