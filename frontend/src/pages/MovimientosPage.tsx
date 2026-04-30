import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { obtenerCategorias } from "../services/categoriaService";
import {
  eliminarMovimiento,
  obtenerMovimientos,
} from "../services/movimientoService";
import { obtenerResumen } from "../services/resumenService";
import { obtenerTopCategorias } from "../services/resumenCategoriaService";
import { obtenerResumenMensual } from "../services/resumenMensualServices";
import { obtenerEstadoPresupuestos } from "../services/presupuestoService";
import ResumenChart from "../components/ResumenChart";
import CategoriasChart from "../components/CategoriasChart";
import ResumenMensualChart from "../components/ResumenMensualChart";
import PresupuestosResumen from "../components/PresupuestosResumen";
import type { Categoria } from "../types/categoria";
import type { Movimiento } from "../types/movimiento";
import type { Resumen } from "../types/resumen";
import type { ResumenCategoria } from "../types/resumenCategoria";
import type { ResumenMensual } from "../types/resumenMensual";
import type { PresupuestoEstado } from "../types/presupuestoEstado";

interface MovimientosPageProps {
  onLogout: () => void;
}

function MovimientosPage({ onLogout }: MovimientosPageProps) {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [resumen, setResumen] = useState<Resumen | null>(null);
  const [topCategorias, setTopCategorias] = useState<ResumenCategoria[]>([]);
  const [resumenMensual, setResumenMensual] = useState<ResumenMensual[]>([]);
  const [presupuestos, setPresupuestos] = useState<PresupuestoEstado[]>([]);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [categoriaId, setCategoriaId] = useState("");
  const [orden, setOrden] = useState("desc");

  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  const navigate = useNavigate();

  const isMobile = windowWidth <= 768;
  const anioActual = new Date().getFullYear();
  const [anioSeleccionado, setAnioSeleccionado] = useState(anioActual);
  const aniosDisponibles = [
    anioActual - 2,
    anioActual - 1,
    anioActual,
    anioActual + 1,
  ];

  useEffect(() => {
    const manejarResize = () => {
      setWindowWidth(window.innerWidth);
    };

    window.addEventListener("resize", manejarResize);

    return () => {
      window.removeEventListener("resize", manejarResize);
    };
  }, []);

  const cargarResumen = useCallback(async () => {
    try {
      const [
        resumenData,
        categoriasData,
        resumenMensualData,
        presupuestosData,
      ] = await Promise.all([
        obtenerResumen(),
        obtenerTopCategorias(),
        obtenerResumenMensual(anioSeleccionado),
        obtenerEstadoPresupuestos(),
      ]);

      setResumen(resumenData);
      setTopCategorias(categoriasData);
      setResumenMensual(resumenMensualData);
      setPresupuestos(presupuestosData);
    } catch (err) {
      console.error(err);
    }
  }, [anioSeleccionado]);

  useEffect(() => {
    let ignore = false;

    const init = async () => {
      try {
        const [
          categoriasData,
          resumenData,
          topCategoriasData,
          resumenMensualData,
          presupuestosData,
        ] = await Promise.all([
          obtenerCategorias(),
          obtenerResumen(),
          obtenerTopCategorias(),
          obtenerResumenMensual(anioSeleccionado),
          obtenerEstadoPresupuestos(),
        ]);

        if (!ignore) {
          setCategorias(categoriasData);
          setResumen(resumenData);
          setTopCategorias(topCategoriasData);
          setResumenMensual(resumenMensualData);
          setPresupuestos(presupuestosData);
        }
      } catch (err) {
        console.error(err);
      }
    };

    void init();

    return () => {
      ignore = true;
    };
  }, [anioSeleccionado]);

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

    if (!confirmar) return;

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

    try {
      const respuesta = await obtenerMovimientos({
        search: search.trim() || undefined,
        categoriaId: categoriaId ? Number(categoriaId) : undefined,
        orden,
        page: 0,
        size: 20,
      });

      setMovimientos(respuesta.content);
    } catch (err) {
      console.error(err);
      setError("No se pudieron aplicar los filtros");
    }
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

  const responsiveStyles = getStyles(isMobile);

  return (
    <div style={responsiveStyles.container}>
      <div style={responsiveStyles.card}>
        <div style={responsiveStyles.header}>
          <h1 style={responsiveStyles.title}>Mis movimientos</h1>

          <div style={responsiveStyles.actions}>
            <button
              type="button"
              onClick={irANuevoMovimiento}
              style={responsiveStyles.newButton}
            >
              Nuevo movimiento
            </button>

            <button
              type="button"
              onClick={cerrarSesion}
              style={responsiveStyles.logoutButton}
            >
              Cerrar sesión
            </button>
          </div>
        </div>

        {resumen && (
          <>
            <div style={responsiveStyles.resumenContainer}>
              <div style={responsiveStyles.resumenCard}>
                <h3 style={responsiveStyles.resumenTitle}>💰 Ingresos</h3>
                <p
                  style={{ ...responsiveStyles.resumenValue, color: "#16a34a" }}
                >
                  {resumen.totalIngresos} €
                </p>
              </div>

              <div style={responsiveStyles.resumenCard}>
                <h3 style={responsiveStyles.resumenTitle}>💸 Gastos</h3>
                <p
                  style={{ ...responsiveStyles.resumenValue, color: "#dc2626" }}
                >
                  {resumen.totalGastos} €
                </p>
              </div>

              <div style={responsiveStyles.resumenCard}>
                <h3 style={responsiveStyles.resumenTitle}>📈 Balance</h3>
                <p
                  style={{
                    ...responsiveStyles.resumenValue,
                    color: balanceColor,
                  }}
                >
                  {resumen.balance} €
                </p>
              </div>
            </div>

            <div style={responsiveStyles.chartsWrapper}>
              <ResumenChart
                ingresos={resumen.totalIngresos}
                gastos={resumen.totalGastos}
              />

              <CategoriasChart data={topCategorias} />

              <div style={responsiveStyles.yearSelectorContainer}>
                <label style={responsiveStyles.yearLabel}>Año:</label>

                <select
                  value={anioSeleccionado}
                  onChange={(e) => setAnioSeleccionado(Number(e.target.value))}
                  style={responsiveStyles.yearSelect}
                >
                  {aniosDisponibles.map((anio) => (
                    <option key={anio} value={anio}>
                      {anio}
                    </option>
                  ))}
                </select>
              </div>

              <ResumenMensualChart data={resumenMensual} />

              <PresupuestosResumen data={presupuestos} />
            </div>
          </>
        )}

        <div style={responsiveStyles.filtersContainer}>
          <input
            type="text"
            placeholder="Buscar por concepto o descripción"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={responsiveStyles.filterInput}
          />

          <select
            value={categoriaId}
            onChange={(e) => setCategoriaId(e.target.value)}
            style={responsiveStyles.filterInput}
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
            style={responsiveStyles.filterInput}
          >
            <option value="desc">Más recientes</option>
            <option value="asc">Más antiguos</option>
          </select>

          <button
            type="button"
            onClick={aplicarFiltros}
            style={responsiveStyles.filterButton}
          >
            Aplicar
          </button>

          <button
            type="button"
            onClick={limpiarFiltros}
            style={responsiveStyles.clearButton}
          >
            Limpiar
          </button>
        </div>

        {error && <p style={responsiveStyles.error}>{error}</p>}

        {movimientos.length === 0 && !error && (
          <p style={responsiveStyles.info}>No hay movimientos para mostrar.</p>
        )}

        {movimientos.map((movimiento) => (
          <div key={movimiento.id} style={responsiveStyles.movimiento}>
            <div style={responsiveStyles.movimientoHeader}>
              <div>
                <h3 style={responsiveStyles.movimientoTitle}>
                  {movimiento.concepto}
                </h3>

                {movimiento.descripcion && (
                  <p style={responsiveStyles.descripcion}>
                    {movimiento.descripcion}
                  </p>
                )}
              </div>

              <div style={responsiveStyles.cardActions}>
                <button
                  type="button"
                  onClick={() => irAEditarMovimiento(movimiento.id)}
                  style={responsiveStyles.editButton}
                >
                  Editar
                </button>

                <button
                  type="button"
                  onClick={() => manejarEliminar(movimiento.id)}
                  style={responsiveStyles.deleteButton}
                >
                  Eliminar
                </button>
              </div>
            </div>

            <div style={responsiveStyles.movimientoGrid}>
              <p>
                <strong>Cantidad:</strong> {movimiento.cantidad} €
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
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

const getStyles = (isMobile: boolean): Record<string, React.CSSProperties> => ({
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "flex-start",
    backgroundColor: "#f5f7fb",
    padding: isMobile ? "18px 10px" : "40px 20px",
    boxSizing: "border-box",
  },
  card: {
    width: "100%",
    maxWidth: "1000px",
    backgroundColor: "#ffffff",
    padding: isMobile ? "18px" : "32px",
    borderRadius: "16px",
    boxShadow: "0 10px 30px rgba(0, 0, 0, 0.08)",
    boxSizing: "border-box",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: isMobile ? "stretch" : "center",
    marginBottom: "24px",
    gap: "16px",
    flexDirection: isMobile ? "column" : "row",
  },
  title: {
    margin: 0,
    fontSize: isMobile ? "30px" : "42px",
    lineHeight: 1.1,
    textAlign: isMobile ? "center" : "left",
  },
  actions: {
    display: "flex",
    gap: "12px",
    flexDirection: isMobile ? "column" : "row",
    width: isMobile ? "100%" : "auto",
  },
  newButton: {
    width: isMobile ? "100%" : "auto",
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
    width: isMobile ? "100%" : "auto",
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
    display: "grid",
    gridTemplateColumns: isMobile
      ? "1fr"
      : "repeat(auto-fit, minmax(180px, 1fr))",
    gap: "16px",
    marginBottom: "24px",
  },
  resumenCard: {
    backgroundColor: "#f8fafc",
    padding: isMobile ? "16px" : "18px",
    borderRadius: "14px",
    textAlign: "center",
    border: "1px solid #e5e7eb",
  },
  resumenTitle: {
    margin: "0 0 8px 0",
    fontSize: isMobile ? "20px" : "24px",
  },
  resumenValue: {
    margin: 0,
    fontSize: isMobile ? "24px" : "28px",
    fontWeight: 700,
  },
  chartsWrapper: {
    display: "grid",
    gridTemplateColumns: "1fr",
    gap: "20px",
    marginBottom: "24px",
    overflowX: "hidden",
  },
  yearSelectorContainer: {
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
    gap: "10px",
    marginBottom: "-10px",
    flexWrap: "wrap",
  },
  yearLabel: {
    fontWeight: 700,
    color: "#111827",
  },
  yearSelect: {
    padding: "10px 14px",
    border: "1px solid #d1d5db",
    borderRadius: "10px",
    fontSize: "14px",
    fontWeight: 600,
    backgroundColor: "#ffffff",
    cursor: "pointer",
  },
  filtersContainer: {
    display: "grid",
    gridTemplateColumns: isMobile
      ? "1fr"
      : "minmax(220px, 1fr) minmax(180px, 220px) minmax(160px, 190px) auto auto",
    gap: "12px",
    marginBottom: "24px",
    alignItems: "center",
  },
  filterInput: {
    width: "100%",
    padding: "12px",
    border: "1px solid #d1d5db",
    borderRadius: "10px",
    fontSize: "15px",
    boxSizing: "border-box",
  },
  filterButton: {
    width: isMobile ? "100%" : "auto",
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
    width: isMobile ? "100%" : "auto",
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
    padding: isMobile ? "14px" : "18px",
    marginBottom: "16px",
    backgroundColor: "#fafafa",
  },
  movimientoHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: isMobile ? "stretch" : "flex-start",
    marginBottom: "12px",
    gap: "12px",
    flexDirection: isMobile ? "column" : "row",
  },
  movimientoTitle: {
    margin: "0 0 6px 0",
    fontSize: isMobile ? "18px" : "20px",
  },
  descripcion: {
    margin: 0,
    color: "#6b7280",
  },
  movimientoGrid: {
    display: "grid",
    gridTemplateColumns: isMobile
      ? "1fr"
      : "repeat(auto-fit, minmax(180px, 1fr))",
    gap: isMobile ? "4px" : "8px",
  },
  cardActions: {
    display: "flex",
    gap: "8px",
    width: isMobile ? "100%" : "auto",
  },
  editButton: {
    flex: isMobile ? 1 : undefined,
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
    flex: isMobile ? 1 : undefined,
    padding: "9px 14px",
    backgroundColor: "#ef4444",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "13px",
    fontWeight: 600,
  },
});

export default MovimientosPage;
