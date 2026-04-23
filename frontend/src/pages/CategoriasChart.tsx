import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import type { ResumenCategoria } from "../types/resumenCategoria";

interface Props {
  data: ResumenCategoria[];
}

function CategoriasChart({ data }: Props) {
  if (data.length === 0) {
    return (
      <div style={styles.emptyContainer}>
        <p style={styles.emptyText}>
          Todavía no hay categorías para mostrar en la gráfica.
        </p>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      <h2 style={styles.title}>Gastos por categoría</h2>

      <div style={styles.chartContainer}>
        <ResponsiveContainer width="100%" height={320}>
          <BarChart data={data} barSize={42}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="categoria" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="total" radius={[8, 8, 0, 0]} fill="#2563eb" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  wrapper: {
    width: "100%",
    marginBottom: "24px",
    backgroundColor: "#ffffff",
    borderRadius: "16px",
    padding: "12px 12px 4px 12px",
  },
  title: {
    margin: "0 0 12px 0",
    fontSize: "24px",
  },
  chartContainer: {
    width: "100%",
    height: 320,
  },
  emptyContainer: {
    width: "100%",
    marginBottom: "24px",
    padding: "24px",
    borderRadius: "16px",
    backgroundColor: "#f8fafc",
    border: "1px solid #e5e7eb",
    textAlign: "center",
  },
  emptyText: {
    margin: 0,
    color: "#6b7280",
  },
};

export default CategoriasChart;
