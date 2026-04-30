import type { PresupuestoEstado } from "../types/presupuestoEstado";

interface Props {
  data: PresupuestoEstado[];
}

function PresupuestosResumen({ data }: Props) {
  if (data.length === 0) return null;

  const getColor = (porcentaje: number) => {
    if (porcentaje < 70) return "#16a34a"; // verde
    if (porcentaje < 100) return "#f59e0b"; // naranja
    return "#dc2626"; // rojo
  };

  const getMensaje = (gastado: number, limite: number) => {
    const restante = limite - gastado;

    if (gastado === 0) return "Sin gastos aún";

    if (restante > 0) {
      return `Te quedan ${restante.toFixed(2)}€`;
    }

    if (restante === 0) {
      return "Has alcanzado el límite";
    }

    return `⚠️ Te has pasado en ${Math.abs(restante).toFixed(2)}€`;
  };

  return (
    <div style={styles.container}>
      <h3 style={styles.title}>Presupuestos</h3>

      {data.map((item) => (
        <div key={item.categoria} style={styles.card}>
          <div style={styles.header}>
            <span>{item.categoria}</span>
            <span>
              {item.gastado}€ / {item.limite}€
            </span>
          </div>

          <div style={styles.barBackground}>
            <div
              style={{
                ...styles.barFill,
                width: `${Math.min(item.porcentaje, 100)}%`,
                backgroundColor: getColor(item.porcentaje),
              }}
            />
          </div>

          <div style={styles.footer}>
            <span style={styles.porcentaje}>{item.porcentaje.toFixed(1)}%</span>
            <span style={styles.mensaje}>
              {getMensaje(item.gastado, item.limite)}
            </span>
          </div>
        </div>
      ))}
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    backgroundColor: "#fff",
    borderRadius: "16px",
    padding: "16px",
    marginBottom: "24px",
  },
  title: {
    marginBottom: "12px",
  },
  card: {
    marginBottom: "16px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: "14px",
    marginBottom: "6px",
    fontWeight: 600,
  },
  barBackground: {
    width: "100%",
    height: "10px",
    backgroundColor: "#e5e7eb",
    borderRadius: "999px",
  },
  barFill: {
    height: "100%",
    borderRadius: "999px",
    transition: "width 0.4s ease",
  },
  footer: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: "12px",
    marginTop: "6px",
  },
  porcentaje: {
    fontWeight: 600,
  },
  mensaje: {
    color: "#6b7280",
  },
};

export default PresupuestosResumen;
