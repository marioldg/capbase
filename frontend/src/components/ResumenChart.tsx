import { PieChart, Pie, Cell, ResponsiveContainer } from "recharts";

interface Props {
  ingresos: number;
  gastos: number;
}

const COLORS = {
  ingresos: "#16a34a",
  gastos: "#dc2626",
};

function ResumenChart({ ingresos, gastos }: Props) {
  const data = [
    { name: "Ingresos", value: ingresos, color: COLORS.ingresos },
    { name: "Gastos", value: gastos, color: COLORS.gastos },
  ].filter((item) => item.value > 0);

  if (data.length === 0) {
    return (
      <div style={styles.emptyContainer}>
        <p style={styles.emptyText}>
          Todavía no hay datos para mostrar en la gráfica.
        </p>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      <div
        style={styles.chartContainer}
        onMouseDown={(e) => e.preventDefault()}
      >
        <ResponsiveContainer width="100%" height={280}>
          <PieChart accessibilityLayer={false}>
            <Pie
              data={data}
              dataKey="value"
              nameKey="name"
              innerRadius={65}
              outerRadius={105}
              paddingAngle={3}
              stroke="none"
              isAnimationActive
            >
              {data.map((entry) => (
                <Cell
                  key={entry.name}
                  fill={entry.color}
                  style={{ outline: "none" }}
                />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
      </div>

      <div style={styles.legend}>
        {data.map((item) => (
          <div key={item.name} style={styles.legendItem}>
            <span
              style={{
                ...styles.legendDot,
                backgroundColor: item.color,
              }}
            />
            <span style={styles.legendLabel}>{item.name}</span>
            <span style={styles.legendValue}>{item.value} €</span>
          </div>
        ))}
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
  chartContainer: {
    width: "100%",
    height: 280,
    outline: "none",
    userSelect: "none",
  },
  legend: {
    display: "flex",
    justifyContent: "center",
    gap: "20px",
    flexWrap: "wrap",
    marginTop: "4px",
    paddingBottom: "8px",
  },
  legendItem: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    backgroundColor: "#f8fafc",
    border: "1px solid #e5e7eb",
    borderRadius: "999px",
    padding: "8px 14px",
  },
  legendDot: {
    width: "10px",
    height: "10px",
    borderRadius: "50%",
    display: "inline-block",
  },
  legendLabel: {
    fontWeight: 600,
    color: "#111827",
  },
  legendValue: {
    color: "#374151",
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

export default ResumenChart;
