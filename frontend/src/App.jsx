import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { useAuth } from "./hooks/useAuth";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Links from "./pages/Links";
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./pages/Dashboard";
import Products from "./pages/Products";
import Merchants from "./pages/Merchants";
import Cards from "./pages/Cards";
import Offers from "./pages/Offers";
import SavedOffers from "./pages/SavedOffers";

import "./App.css";
import "./styles/auth.css";
import "./styles/dashboard.css";
import "./styles/forms.css";
import "./styles/links.css";
import "./styles/products.css";
import "./styles/merchants.css";
import "./styles/cards.css";
import "./styles/offers.css";

function App() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="app">
      {user && (
        <header className="navbar">

          <div className="navbar-left">
            <h1>LinkVault</h1>

            <nav className="navbar-links">
              <a href="/">
                Dashboard
              </a>

              <a href="/links">
                Links
              </a>

              <a href="/products">
                Products
              </a>

              <a href="/merchants">
                Merchants
              </a>

              <a href="/cards">
                Cards
              </a>

              <a href="/offers">
                Offers
              </a>

              <a href="/saved-offers">
                Saved
              </a>
            </nav>
          </div>

          <div className="navbar-right">
            <span>
              {user.name}
            </span>

            <button onClick={handleLogout}>
              Logout
            </button>
          </div>

        </header>
      )}

      <Routes>
        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        <Route element={<ProtectedRoute />}>
          <Route
            path="/"
            element={<Dashboard />}
          />
          <Route
            path="/links"
            element={<Links />}
          />
          <Route
            path="/products"
            element={<Products />}
          />

          <Route
            path="/merchants"
            element={<Merchants />}
          />

          <Route
            path="/cards"
            element={<Cards />}
          />

          <Route
            path="/offers"
            element={<Offers />}
          />

          <Route
            path="/saved-offers"
            element={<SavedOffers />}
          />
        </Route>

        <Route
          path="*"
          element={
            <Navigate
              to="/"
              replace
            />
          }
        />
      </Routes>
    </div>
  );
}

export default App;