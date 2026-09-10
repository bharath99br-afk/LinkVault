import { Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./hooks/useAuth";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Links from "./pages/Links";
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./pages/Dashboard";

import "./App.css";

function App() {
  const { user, logout } = useAuth();

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
            </nav>
          </div>

          <div className="navbar-right">
            <span>
              {user.name}
            </span>

            <button onClick={logout}>
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
            path="/"
            element={<Links />}
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