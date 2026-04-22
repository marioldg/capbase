import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { obtenerCategorias } from "../services/categoriaService";
import {
  actualizarMovimiento,
  crearMovimiento,
  obtenerMovimientoPorId,
} from "../services/movimientoService";
import type { Categoria } from "../types/categoria";

function CrearMovimientoPage() {
  const [concepto, setConcepto] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [cantidad, setCantidad] = useState("");
  const [fecha, setFecha] = useState("");
  const [tipo, setTipo] = useState("GASTO");
  const [categoriaId, setCategoriaId] = useState("");
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [error, setError] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [cargando, setCargando] = useState(false);

  const navigate = useNavigate();

  const movimientoEditarId = sessionStorage.getItem("movimientoEditarId");
  const esModoEdicion = Boolean(movimientoEditarId);

  useEffect(() => {
    const cargarCategorias = async () => {
      try {
        const data = await obtenerCategorias();
        setCategorias(data);
      } catch (err) {
        console.error(err);
        setError("No se pudieron cargar las categorías");
      }
    };

    void cargarCategorias();
  }, []);

  useEffect(() => {
    if (!esModoEdicion || !movimientoEditarId) {
      return;
    }

    let ignore = false;

    const cargarMovimiento = async () => {
      try {
        setCargando(true);

        const movimientoAEditar = await obtenerMovimientoPorId(
          Number(movimientoEditarId),
        );

        if (!movimientoAEditar) {
          if (!ignore) {
            setError("No se encontró el movimiento a editar");
          }
          return;
        }

        if (!ignore) {
          setConcepto(movimientoAEditar.concepto);
          setDescripcion(movimientoAEditar.descripcion ?? "");
          setCantidad(String(movimientoAEditar.cantidad));
          setFecha(movimientoAEditar.fecha);
          setTipo(movimientoAEditar.tipo);
        }
      } catch (err) {
        console.error(err);

        if (!ignore) {
          setError("No se pudo cargar el movimiento");
        }
      } finally {
        if (!ignore) {
          setCargando(false);
        }
      }
    };

    void cargarMovimiento();

    return () => {
      ignore = true;
    };
  }, [esModoEdicion, movimientoEditarId]);

  const manejarSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");
    setMensaje("");

    try {
      const payload = {
        concepto,
        descripcion,
        cantidad: Number(cantidad),
        fecha,
        tipo,
        categoriaId: Number(categoriaId),
      };

      if (esModoEdicion && movimientoEditarId) {
        await actualizarMovimiento(Number(movimientoEditarId), payload);
        setMensaje("Movimiento actualizado correctamente");
        sessionStorage.removeItem("movimientoEditarId");
      } else {
        await crearMovimiento(payload);
        setMensaje("Movimiento creado correctamente");
      }

      setTimeout(() => {
        navigate("/movimientos");
      }, 700);
    } catch (err: unknown) {
      console.error(err);

      if (axios.isAxiosError(err)) {
        if (err.response) {
          setError(
            `Error ${err.response.status}: ${
              err.response.data?.mensaje ||
              (esModoEdicion
                ? "No se pudo actualizar el movimiento"
                : "No se pudo crear el movimiento")
            }`,
          );
        } else if (err.request) {
          setError("No se pudo conectar con el backend");
        } else {
          setError(
            esModoEdicion
              ? "Error inesperado al actualizar el movimiento"
              : "Error inesperado al crear el movimiento",
          );
        }
      } else {
        setError(
          esModoEdicion
            ? "Error inesperado al actualizar el movimiento"
            : "Error inesperado al crear el movimiento",
        );
      }
    }
  };

  const volver = () => {
    sessionStorage.removeItem("movimientoEditarId");
    navigate("/movimientos");
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>
            {esModoEdicion ? "Editar movimiento" : "Nuevo movimiento"}
          </h1>
          <button type="button" onClick={volver} style={styles.secondaryButton}>
            Volver
          </button>
        </div>

        {cargando && <p style={styles.info}>Cargando movimiento...</p>}

        {!cargando && (
          <form onSubmit={manejarSubmit} style={styles.form}>
            <input
              type="text"
              placeholder="Concepto"
              value={concepto}
              onChange={(e) => setConcepto(e.target.value)}
              style={styles.input}
              required
            />

            <input
              type="text"
              placeholder="Descripción"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              style={styles.input}
            />

            <input
              type="number"
              step="0.01"
              placeholder="Cantidad"
              value={cantidad}
              onChange={(e) => setCantidad(e.target.value)}
              style={styles.input}
              required
            />

            <input
              type="date"
              value={fecha}
              onChange={(e) => setFecha(e.target.value)}
              style={styles.input}
              required
            />

            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
              style={styles.input}
              required
            >
              <option value="GASTO">GASTO</option>
              <option value="INGRESO">INGRESO</option>
            </select>

            <select
              value={categoriaId}
              onChange={(e) => setCategoriaId(e.target.value)}
              style={styles.input}
              required
            >
              <option value="">Selecciona una categoría</option>
              {categorias.map((categoria) => (
                <option key={categoria.id} value={categoria.id}>
                  {categoria.nombre}
                </option>
              ))}
            </select>

            <button type="submit" style={styles.primaryButton}>
              {esModoEdicion ? "Guardar cambios" : "Guardar movimiento"}
            </button>
          </form>
        )}

        {mensaje && <p style={styles.success}>{mensaje}</p>}
        {error && <p style={styles.error}>{error}</p>}
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
    maxWidth: "700px",
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
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "16px",
  },
  input: {
    padding: "12px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    fontSize: "16px",
  },
  primaryButton: {
    padding: "12px",
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    cursor: "pointer",
  },
  secondaryButton: {
    padding: "10px 16px",
    backgroundColor: "#6b7280",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "14px",
    cursor: "pointer",
  },
  info: {
    textAlign: "center",
    marginBottom: "16px",
    color: "#444",
  },
  success: {
    marginTop: "16px",
    color: "green",
    textAlign: "center",
  },
  error: {
    marginTop: "16px",
    color: "red",
    textAlign: "center",
  },
};

export default CrearMovimientoPage;
