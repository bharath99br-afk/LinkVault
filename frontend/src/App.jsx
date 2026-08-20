import "./App.css";
import SearchBar from "./components/SearchBar";

function App() {
  return (
    <div className="app">
      <header className="navbar">
        <h1>LinkVault</h1>
        <span>Link Management</span>
      </header>

      <main className="container">
        <section className="hero">
          <h2>Your Links</h2>
          <p>Manage all your important links in one place.</p>
        </section>

        <SearchBar />

        <section className="link-section">
          <p>No links loaded yet.</p>
        </section>
      </main>
    </div>
  );
}

export default App;