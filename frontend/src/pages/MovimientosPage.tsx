import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {
  eliminarMovimiento,
  obtenerMovimientos,
} from "../services/movimientoService";
import type { Movimiento } from "../types/movimiento";

interface MovimientosPageProps {
  onLogout: () => void;
}

function MovimientosPage({ onLogout }: MovimientosPageProps) {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    let ignore = false;

    const cargarDatos = async () => {
      try {
        const respuesta = await obtenerMovimientos();

        if (!ignore) {
          setMovimientos(respuesta.content);
        }
      } catch (err: unknown) {
        console.error(err);

        if (axios.isAxiosError(err)) {
          if (err.response?.status === 403) {
            if (!ignore) {
              onLogout();
              navigate("/login", { replace: true });
            }
            return;
          }

          if (!ignore) {
            if (err.response) {
              setError(
                `Error ${err.response.status}: no se pudieron cargar los movimientos`,
              );
            } else if (err.request) {
              setError("No se pudo conectar con el backend");
            } else {
              setError("Error inesperado al cargar movimientos");
            }
          }
        } else {
          if (!ignore) {
            setError("Error inesperado al cargar movimientos");
          }
        }
      }
    };

    void cargarDatos();

    return () => {
      ignore = true;
    };
  }, [navigate, onLogout]);

  const cerrarSesion = () => {
    onLogout();
    navigate("/login", { replace: true });
  };

  const irANuevoMovimiento = () => {
    navigate("/movimientos/nuevo");
  };

  const manejarEliminar = async (id: number) => {
    const confirmar = window.confirm(
      "¿Seguro que quieres eliminar este movimiento?",
    );

    if (!confirmar) {
      return;
    }

    try {
      await eliminarMovimiento(id);

      // aqui actualizo la lista sin tener que recargar toda la pagina
      setMovimientos((prevMovimientos) =>
        prevMovimientos.filter((movimiento) => movimiento.id !== id),
      );
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
            `Error ${err.response.status}: no se pudo eliminar el movimiento`,
          );
        } else if (err.request) {
          setError("No se pudo conectar con el backend");
        } else {
          setError("Error inesperado al eliminar el movimiento");
        }
      } else {
        setError("Error inesperado al eliminar el movimiento");
      }
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Mis movimientos</h1>

          <div style={styles.actions}>
            <button onClick={irANuevoMovimiento} style={styles.newButton}>
              Nuevo movimiento
            </button>

            <button onClick={cerrarSesion} style={styles.logoutButton}>
              Cerrar sesión
            </button>
          </div>
        </div>

        {error && <p style={styles.error}>{error}</p>}

        {movimientos.length === 0 && !error && (
          <p style={styles.info}>No hay movimientos para mostrar.</p>
        )}

        {movimientos.map((movimiento) => (
          <div key={movimiento.id} style={styles.movimiento}>
            <div style={styles.movimientoHeader}>
              <h3>{movimiento.concepto}</h3>

              <button
                onClick={() => manejarEliminar(movimiento.id)}
                style={styles.deleteButton}
              >
                Eliminar
              </button>
            </div>

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
  actions: {
    display: "flex",
    gap: "12px",
  },
  newButton: {
    padding: "10px 16px",
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "14px",
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
  movimientoHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "12px",
    gap: "12px",
  },
  deleteButton: {
    padding: "8px 12px",
    backgroundColor: "#ef4444",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "13px",
  },
};

export default MovimientosPage;
