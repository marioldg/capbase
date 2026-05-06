import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useState } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import MovimientosPage from "./pages/MovimientosPage";
import CrearMovimientoPage from "./pages/CrearMovimientoPage";
import WelcomePage from "./pages/WelcomePage";
function App() {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem("token"),
  );

  const handleLogin = (nuevoToken: string) => {
    localStorage.setItem("token", nuevoToken);
    setToken(nuevoToken);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    sessionStorage.removeItem("movimientoEditarId");
    setToken(null);
  };

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            token ? <Navigate to="/movimientos" replace /> : <WelcomePage />
          }
        />
        <Route
          path="/login"
          element={
            token ? (
              <Navigate to="/movimientos" replace />
            ) : (
              <LoginPage onLogin={handleLogin} />
            )
          }
        />

        <Route
          path="/register"
          element={
            token ? (
              <Navigate to="/movimientos" replace />
            ) : (
              <RegisterPage onLogin={handleLogin} />
            )
          }
        />

        <Route
          path="/movimientos"
          element={
            token ? (
              <MovimientosPage onLogout={handleLogout} />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />

        <Route
          path="/movimientos/nuevo"
          element={
            token ? <CrearMovimientoPage /> : <Navigate to="/login" replace />
          }
        />

        <Route
          path="/movimientos/editar"
          element={
            token ? <CrearMovimientoPage /> : <Navigate to="/login" replace />
          }
        />

        <Route
          path="*"
          element={<Navigate to={token ? "/movimientos" : "/"} replace />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
