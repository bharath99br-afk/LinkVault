import { useState } from "react";

function SearchBar({ onSearch }) {

    const [searchTerm, setSearchTerm] = useState("");

    const handleSearch = () => {
        onSearch(searchTerm.trim());
    };

    const handleKeyDown = (event) => {
        if (event.key === "Enter") {
            handleSearch();
        }
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
                onKeyDown={handleKeyDown}
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