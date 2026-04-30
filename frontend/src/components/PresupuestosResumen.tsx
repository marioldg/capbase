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

          <div style={styles.footer}>{item.porcentaje.toFixed(0)}%</div>
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
    marginBottom: "12px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: "14px",
    marginBottom: "4px",
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
  },
  footer: {
    fontSize: "12px",
    marginTop: "4px",
    textAlign: "right",
  },
};

export default PresupuestosResumen;
