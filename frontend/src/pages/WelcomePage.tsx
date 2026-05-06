import { useNavigate } from "react-router-dom";

function WelcomePage() {
  const navigate = useNavigate();

  return (
    <div style={styles.container}>
      <div style={styles.hero}>
        <div style={styles.badge}>Finanzas personales inteligentes</div>

        <h1 style={styles.title}>Controla tu dinero con claridad.</h1>

        <p style={styles.subtitle}>
          CapBase te ayuda a gestionar ingresos, gastos, presupuestos y
          estadísticas mensuales desde un dashboard moderno y sencillo.
        </p>

        <div style={styles.actions}>
          <button
            type="button"
            onClick={() => navigate("/register")}
            style={styles.primaryButton}
          >
            Crear cuenta gratis
          </button>

          <button
            type="button"
            onClick={() => navigate("/login")}
            style={styles.secondaryButton}
          >
            Iniciar sesión
          </button>
        </div>

        <div style={styles.features}>
          <div style={styles.featureCard}>
            <h3 style={styles.featureTitle}>📊 Estadísticas</h3>
            <p style={styles.featureText}>
              Visualiza ingresos, gastos y evolución mensual.
            </p>
          </div>

          <div style={styles.featureCard}>
            <h3 style={styles.featureTitle}>💸 Presupuestos</h3>
            <p style={styles.featureText}>
              Define límites por categoría y controla tus gastos.
            </p>
          </div>

          <div style={styles.featureCard}>
            <h3 style={styles.featureTitle}>🔐 Seguro</h3>
            <p style={styles.featureText}>
              Acceso protegido con autenticación JWT.
            </p>
          </div>
        </div>
      </div>

      <div style={styles.previewCard}>
        <div style={styles.previewHeader}>
          <span style={styles.dotGreen}></span>
          <span style={styles.dotYellow}></span>
          <span style={styles.dotRed}></span>
        </div>

        <h2 style={styles.previewTitle}>Resumen mensual</h2>

        <div style={styles.statRow}>
          <span>Ingresos</span>
          <strong style={styles.green}>1.480,65 €</strong>
        </div>

        <div style={styles.statRow}>
          <span>Gastos</span>
          <strong style={styles.red}>760,00 €</strong>
        </div>

        <div style={styles.statRow}>
          <span>Balance</span>
          <strong style={styles.green}>720,65 €</strong>
        </div>

        <div style={styles.progressBox}>
          <div style={styles.progressLabel}>
            <span>Presupuesto Salud</span>
            <span>99,7%</span>
          </div>
          <div style={styles.progressBackground}>
            <div style={styles.progressFill}></div>
          </div>
          <p style={styles.progressText}>Te quedan 1,00 €</p>
        </div>
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    minHeight: "100vh",
    display: "grid",
    gridTemplateColumns: "1.1fr 0.9fr",
    alignItems: "center",
    gap: "48px",
    padding: "48px",
    background:
      "linear-gradient(135deg, #eff6ff 0%, #f8fafc 45%, #ecfdf5 100%)",
    boxSizing: "border-box",
  },
  hero: {
    maxWidth: "720px",
  },
  badge: {
    display: "inline-block",
    padding: "8px 14px",
    borderRadius: "999px",
    backgroundColor: "#dbeafe",
    color: "#1d4ed8",
    fontWeight: 700,
    fontSize: "14px",
    marginBottom: "20px",
  },
  title: {
    margin: 0,
    fontSize: "64px",
    lineHeight: 1,
    letterSpacing: "-2px",
    color: "#0f172a",
  },
  subtitle: {
    marginTop: "22px",
    fontSize: "19px",
    lineHeight: 1.7,
    color: "#475569",
    maxWidth: "620px",
  },
  actions: {
    display: "flex",
    gap: "14px",
    marginTop: "32px",
    flexWrap: "wrap",
  },
  primaryButton: {
    padding: "14px 22px",
    borderRadius: "12px",
    border: "none",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    fontWeight: 800,
    fontSize: "16px",
    cursor: "pointer",
    boxShadow: "0 12px 24px rgba(37, 99, 235, 0.25)",
  },
  secondaryButton: {
    padding: "14px 22px",
    borderRadius: "12px",
    border: "1px solid #cbd5e1",
    backgroundColor: "#ffffff",
    color: "#0f172a",
    fontWeight: 800,
    fontSize: "16px",
    cursor: "pointer",
  },
  features: {
    display: "grid",
    gridTemplateColumns: "repeat(3, 1fr)",
    gap: "14px",
    marginTop: "42px",
  },
  featureCard: {
    padding: "18px",
    borderRadius: "16px",
    backgroundColor: "rgba(255, 255, 255, 0.75)",
    border: "1px solid #e2e8f0",
  },
  featureTitle: {
    margin: "0 0 8px 0",
    color: "#0f172a",
  },
  featureText: {
    margin: 0,
    color: "#64748b",
    fontSize: "14px",
    lineHeight: 1.5,
  },
  previewCard: {
    backgroundColor: "#ffffff",
    borderRadius: "28px",
    padding: "30px",
    boxShadow: "0 30px 70px rgba(15, 23, 42, 0.16)",
    border: "1px solid #e5e7eb",
  },
  previewHeader: {
    display: "flex",
    gap: "8px",
    marginBottom: "24px",
  },
  dotGreen: {
    width: "12px",
    height: "12px",
    borderRadius: "50%",
    backgroundColor: "#22c55e",
  },
  dotYellow: {
    width: "12px",
    height: "12px",
    borderRadius: "50%",
    backgroundColor: "#f59e0b",
  },
  dotRed: {
    width: "12px",
    height: "12px",
    borderRadius: "50%",
    backgroundColor: "#ef4444",
  },
  previewTitle: {
    margin: "0 0 22px 0",
    color: "#111827",
  },
  statRow: {
    display: "flex",
    justifyContent: "space-between",
    padding: "14px 0",
    borderBottom: "1px solid #e5e7eb",
    color: "#475569",
  },
  green: {
    color: "#16a34a",
  },
  red: {
    color: "#dc2626",
  },
  progressBox: {
    marginTop: "24px",
    padding: "18px",
    borderRadius: "18px",
    backgroundColor: "#f8fafc",
    border: "1px solid #e5e7eb",
  },
  progressLabel: {
    display: "flex",
    justifyContent: "space-between",
    fontWeight: 700,
    marginBottom: "10px",
  },
  progressBackground: {
    height: "12px",
    borderRadius: "999px",
    backgroundColor: "#e5e7eb",
    overflow: "hidden",
  },
  progressFill: {
    width: "99.7%",
    height: "100%",
    backgroundColor: "#f59e0b",
    borderRadius: "999px",
  },
  progressText: {
    margin: "8px 0 0 0",
    color: "#64748b",
    fontSize: "13px",
    textAlign: "right",
  },
};

export default WelcomePage;
