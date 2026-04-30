import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  XAxis,
  YAxis,
} from "recharts";
import type { ResumenMensual } from "../types/resumenMensual";

interface Props {
  data: ResumenMensual[];
}

const MESES = [
  "Ene",
  "Feb",
  "Mar",
  "Abr",
  "May",
  "Jun",
  "Jul",
  "Ago",
  "Sep",
  "Oct",
  "Nov",
  "Dic",
];

function ResumenMensualChart({ data }: Props) {
  const chartData = data.map((item) => ({
    mes: MESES[item.mes - 1],
    ingresos: item.ingresos,
    gastos: item.gastos,
  }));

  const hayDatos = chartData.some(
    (item) => item.ingresos > 0 || item.gastos > 0,
  );

  if (!hayDatos) {
    return (
      <div style={styles.emptyContainer}>
        <p style={styles.emptyText}>
          Todavía no hay datos mensuales para mostrar.
        </p>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      <h2 style={styles.title}>Estadísticas mensuales</h2>

      <div style={styles.chartContainer}>
        <ResponsiveContainer width="100%" height={320}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="mes" />
            <YAxis />
            <Legend />
            <Bar
              dataKey="ingresos"
              name="Ingresos"
              fill="#16a34a"
              radius={[8, 8, 0, 0]}
            />
            <Bar
              dataKey="gastos"
              name="Gastos"
              fill="#dc2626"
              radius={[8, 8, 0, 0]}
            />
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
    padding: "18px 12px 8px 12px",
    border: "1px solid #e5e7eb",
  },
  title: {
    margin: "0 0 16px 0",
    textAlign: "center",
    fontSize: "24px",
    color: "#111827",
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

export default ResumenMensualChart;
