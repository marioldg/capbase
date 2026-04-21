import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { obtenerMovimientos } from "../services/movimientoService";
import type { Movimiento } from "../types/movimiento";

interface MovimientosPageProps {
  onLogout: () => void;
}

function MovimientosPage({ onLogout }: MovimientosPageProps) {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const cargarMovimientos = async () => {
      try {
        const respuesta = await obtenerMovimientos();
        setMovimientos(respuesta.content);
      } catch (err: unknown) {
        console.error(err);

        if (axios.isAxiosError(err)) {
          if (err.response?.status === 403) {
            onLogout();
            navigate("/login", { replace: true });
            return;
          }

          if (err.response) {
            setError(
              `Error ${err.response.status}: no se pudieron cargar los movimientos`,
            );
          } else if (err.request) {
            setError("No se pudo conectar con el backend");
          } else {
            setError("Error inesperado al cargar movimientos");
          }
        } else {
          setError("Error inesperado al cargar movimientos");
        }
      }
    };

    cargarMovimientos();
  }, [navigate, onLogout]);

  const cerrarSesion = () => {
    onLogout();
    navigate("/login", { replace: true });
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Mis movimientos</h1>
          <button onClick={cerrarSesion} style={styles.logoutButton}>
            Cerrar sesión
          </button>
        </div>

        {error && <p style={styles.error}>{error}</p>}

        {movimientos.length === 0 && !error && (
          <p style={styles.info}>No hay movimientos para mostrar.</p>
        )}

        {movimientos.map((movimiento) => (
          <div key={movimiento.id} style={styles.movimiento}>
            <h3>{movimiento.concepto}</h3>
            <p>
              <strong>Cantidad:</strong> {movimiento.cantidad}
            </p>
            <p>
              <strong>Fecha:</strong> {movimiento.fecha}
            </p>
            <p>
              <strong>Tipo:</strong> {movimiento.tipo}
            </p>
            <p>
              <strong>Categoría:</strong> {movimiento.categoriaNombre}
            </p>
            {movimiento.descripcion && (
              <p>
                <strong>Descripción:</strong> {movimiento.descripcion}
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "flex-start",
    backgroundColor: "#f5f7fb",
    padding: "40px 20px",
  },
  card: {
    width: "100%",
    maxWidth: "800px",
    backgroundColor: "#ffffff",
    padding: "32px",
    borderRadius: "12px",
    boxShadow: "0 8px 24px rgba(0, 0, 0, 0.08)",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "24px",
    gap: "16px",
  },
  title: {
    margin: 0,
  },
  logoutButton: {
    padding: "10px 16px",
    backgroundColor: "#dc2626",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "14px",
  },
  info: {
    textAlign: "center",
    marginBottom: "16px",
    color: "#444",
  },
  error: {
    textAlign: "center",
    marginBottom: "16px",
    color: "red",
  },
  movimiento: {
    border: "1px solid #ddd",
    borderRadius: "10px",
    padding: "16px",
    marginBottom: "16px",
    backgroundColor: "#fafafa",
  },
};

export default MovimientosPage;
