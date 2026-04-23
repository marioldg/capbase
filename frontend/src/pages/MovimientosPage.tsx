import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { obtenerCategorias } from "../services/categoriaService";
import {
  eliminarMovimiento,
  obtenerMovimientos,
} from "../services/movimientoService";
import { obtenerResumen } from "../services/resumenService";
import type { Categoria } from "../types/categoria";
import type { Movimiento } from "../types/movimiento";
import type { Resumen } from "../types/resumen";

interface MovimientosPageProps {
  onLogout: () => void;
}

function MovimientosPage({ onLogout }: MovimientosPageProps) {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [resumen, setResumen] = useState<Resumen | null>(null);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [categoriaId, setCategoriaId] = useState("");
  const [orden, setOrden] = useState("desc");

  const navigate = useNavigate();

  const cargarResumen = useCallback(async () => {
    try {
      const resumenData = await obtenerResumen();
      setResumen(resumenData);
    } catch (err) {
      console.error(err);
    }
  }, []);

  const cargarMovimientos = useCallback(async () => {
    try {
      const respuesta = await obtenerMovimientos({
        search: search.trim() || undefined,
        categoriaId: categoriaId ? Number(categoriaId) : undefined,
        orden,
        page: 0,
        size: 20,
      });

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
  }, [search, categoriaId, orden, onLogout, navigate]);

  useEffect(() => {
    let ignore = false;

    const init = async () => {
      try {
        const [categoriasData, resumenData] = await Promise.all([
          obtenerCategorias(),
          obtenerResumen(),
        ]);

        if (!ignore) {
          setCategorias(categoriasData);
          setResumen(resumenData);
        }
      } catch (err) {
        console.error(err);
      }
    };

    void init();

    return () => {
      ignore = true;
    };
  }, []);

  useEffect(() => {
    let ignore = false;

    const cargar = async () => {
      try {
        const respuesta = await obtenerMovimientos({
          search: search.trim() || undefined,
          categoriaId: categoriaId ? Number(categoriaId) : undefined,
          orden,
          page: 0,
          size: 20,
        });

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
        } else if (!ignore) {
          setError("Error inesperado al cargar movimientos");
        }
      }
    };

    void cargar();

    return () => {
      ignore = true;
    };
  }, [search, categoriaId, orden, onLogout, navigate]);

  const cerrarSesion = () => {
    sessionStorage.removeItem("movimientoEditarId");
    onLogout();
    navigate("/login", { replace: true });
  };

  const irANuevoMovimiento = () => {
    sessionStorage.removeItem("movimientoEditarId");
    navigate("/movimientos/nuevo");
  };

  const irAEditarMovimiento = (id: number) => {
    sessionStorage.setItem("movimientoEditarId", String(id));
    navigate("/movimientos/editar");
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
      setMovimientos((prev) =>
        prev.filter((movimiento) => movimiento.id !== id),
      );
      await cargarResumen();
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

  const aplicarFiltros = async () => {
    setError("");
    await cargarMovimientos();
  };

  const limpiarFiltros = async () => {
    setSearch("");
    setCategoriaId("");
    setOrden("desc");
    setError("");

    try {
      const respuesta = await obtenerMovimientos({
        orden: "desc",
        page: 0,
        size: 20,
      });
      setMovimientos(respuesta.content);
    } catch (err) {
      console.error(err);
      setError("No se pudieron limpiar los filtros");
    }
  };

  const balanceColor = resumen && resumen.balance < 0 ? "#dc2626" : "#16a34a";

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Mis movimientos</h1>

          <div style={styles.actions}>
            <button
              type="button"
              onClick={irANuevoMovimiento}
              style={styles.newButton}
            >
              Nuevo movimiento
            </button>

            <button
              type="button"
              onClick={cerrarSesion}
              style={styles.logoutButton}
            >
              Cerrar sesión
            </button>
          </div>
        </div>

        {resumen && (
          <div style={styles.resumenContainer}>
            <div style={styles.resumenCard}>
              <h3 style={styles.resumenTitle}>💰 Ingresos</h3>
              <p style={{ ...styles.resumenValue, color: "#16a34a" }}>
                {resumen.totalIngresos} €
              </p>
            </div>

            <div style={styles.resumenCard}>
              <h3 style={styles.resumenTitle}>💸 Gastos</h3>
              <p style={{ ...styles.resumenValue, color: "#dc2626" }}>
                {resumen.totalGastos} €
              </p>
            </div>

            <div style={styles.resumenCard}>
              <h3 style={styles.resumenTitle}>📈 Balance</h3>
              <p style={{ ...styles.resumenValue, color: balanceColor }}>
                {resumen.balance} €
              </p>
            </div>
          </div>
        )}

        <div style={styles.filtersContainer}>
          <input
            type="text"
            placeholder="Buscar por concepto o descripción"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={styles.filterInput}
          />

          <select
            value={categoriaId}
            onChange={(e) => setCategoriaId(e.target.value)}
            style={styles.filterInput}
          >
            <option value="">Todas las categorías</option>
            {categorias.map((categoria) => (
              <option key={categoria.id} value={categoria.id}>
                {categoria.nombre}
              </option>
            ))}
          </select>

          <select
            value={orden}
            onChange={(e) => setOrden(e.target.value)}
            style={styles.filterInput}
          >
            <option value="desc">Más recientes</option>
            <option value="asc">Más antiguos</option>
          </select>

          <button
            type="button"
            onClick={aplicarFiltros}
            style={styles.filterButton}
          >
            Aplicar
          </button>

          <button
            type="button"
            onClick={limpiarFiltros}
            style={styles.clearButton}
          >
            Limpiar
          </button>
        </div>

        {error && <p style={styles.error}>{error}</p>}

        {movimientos.length === 0 && !error && (
          <p style={styles.info}>No hay movimientos para mostrar.</p>
        )}

        {movimientos.map((movimiento) => (
          <div key={movimiento.id} style={styles.movimiento}>
            <div style={styles.movimientoHeader}>
              <h3>{movimiento.concepto}</h3>

              <div style={styles.cardActions}>
                <button
                  type="button"
                  onClick={() => irAEditarMovimiento(movimiento.id)}
                  style={styles.editButton}
                >
                  Editar
                </button>

                <button
                  type="button"
                  onClick={() => manejarEliminar(movimiento.id)}
                  style={styles.deleteButton}
                >
                  Eliminar
                </button>
              </div>
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
    maxWidth: "1000px",
    backgroundColor: "#ffffff",
    padding: "32px",
    borderRadius: "16px",
    boxShadow: "0 10px 30px rgba(0, 0, 0, 0.08)",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "24px",
    gap: "16px",
    flexWrap: "wrap",
  },
  title: {
    margin: 0,
    fontSize: "42px",
    lineHeight: 1.1,
  },
  actions: {
    display: "flex",
    gap: "12px",
  },
  newButton: {
    padding: "12px 18px",
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: 600,
  },
  logoutButton: {
    padding: "12px 18px",
    backgroundColor: "#dc2626",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: 600,
  },
  resumenContainer: {
    display: "flex",
    gap: "16px",
    marginBottom: "24px",
    flexWrap: "wrap",
  },
  resumenCard: {
    flex: 1,
    minWidth: "180px",
    backgroundColor: "#f8fafc",
    padding: "18px",
    borderRadius: "14px",
    textAlign: "center",
    border: "1px solid #e5e7eb",
  },
  resumenTitle: {
    margin: "0 0 8px 0",
    fontSize: "24px",
  },
  resumenValue: {
    margin: 0,
    fontSize: "28px",
    fontWeight: 700,
  },
  filtersContainer: {
    display: "flex",
    gap: "12px",
    marginBottom: "24px",
    flexWrap: "wrap",
    alignItems: "center",
  },
  filterInput: {
    padding: "12px",
    border: "1px solid #d1d5db",
    borderRadius: "10px",
    fontSize: "15px",
    minWidth: "180px",
  },
  filterButton: {
    padding: "12px 16px",
    backgroundColor: "#111827",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: 600,
  },
  clearButton: {
    padding: "12px 16px",
    backgroundColor: "#6b7280",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: 600,
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
    border: "1px solid #e5e7eb",
    borderRadius: "14px",
    padding: "18px",
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
  cardActions: {
    display: "flex",
    gap: "8px",
  },
  editButton: {
    padding: "9px 14px",
    backgroundColor: "#f59e0b",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "13px",
    fontWeight: 600,
  },
  deleteButton: {
    padding: "9px 14px",
    backgroundColor: "#ef4444",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "13px",
    fontWeight: 600,
  },
};

export default MovimientosPage;
