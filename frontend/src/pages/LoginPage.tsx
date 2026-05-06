import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";

interface LoginPageProps {
  onLogin: (token: string) => void;
}

function LoginPage({ onLogin }: LoginPageProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const manejarLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      const respuesta = await login({ email, password });

      onLogin(respuesta.token);
      navigate("/movimientos", { replace: true });
    } catch (err: unknown) {
      console.error(err);

      if (axios.isAxiosError(err)) {
        if (err.response) {
          setError(
            `Error ${err.response.status}: ${
              err.response.data?.mensaje || "Email o contraseña incorrectos"
            }`,
          );
        } else if (err.request) {
          setError("No se pudo conectar con el backend");
        } else {
          setError("Error inesperado al iniciar sesión");
        }
      } else {
        setError("Error inesperado al iniciar sesión");
      }
    }
  };

  return (
    <div style={styles.container}>
      <button
        type="button"
        onClick={() => navigate("/")}
        style={styles.backButton}
      >
        ← Volver
      </button>

      <div style={styles.card}>
        <div style={styles.brandBox}>
          <h1 style={styles.logo}>CapBase</h1>
          <p style={styles.subtitle}>
            Accede a tu panel financiero y controla tus movimientos,
            presupuestos y estadísticas.
          </p>
        </div>

        <form onSubmit={manejarLogin} style={styles.form}>
          <label style={styles.label}>Email</label>
          <input
            type="email"
            placeholder="tuemail@gmail.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={styles.input}
          />

          <label style={styles.label}>Contraseña</label>
          <input
            type="password"
            placeholder="Tu contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={styles.input}
          />

          <button type="submit" style={styles.button}>
            Iniciar sesión
          </button>
        </form>

        <p style={styles.footerText}>
          ¿No tienes cuenta?{" "}
          <button
            type="button"
            onClick={() => navigate("/register")}
            style={styles.linkButton}
          >
            Crear cuenta
          </button>
        </p>

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
    alignItems: "center",
    background:
      "linear-gradient(135deg, #eff6ff 0%, #f8fafc 45%, #ecfdf5 100%)",
    padding: "20px",
    position: "relative",
    boxSizing: "border-box",
  },
  backButton: {
    position: "absolute",
    top: "24px",
    left: "24px",
    padding: "10px 16px",
    borderRadius: "999px",
    border: "1px solid #cbd5e1",
    backgroundColor: "#ffffff",
    color: "#0f172a",
    fontWeight: 700,
    cursor: "pointer",
    boxShadow: "0 8px 20px rgba(15, 23, 42, 0.08)",
  },
  card: {
    width: "100%",
    maxWidth: "440px",
    backgroundColor: "#ffffff",
    padding: "36px",
    borderRadius: "24px",
    boxShadow: "0 24px 60px rgba(15, 23, 42, 0.14)",
    border: "1px solid #e5e7eb",
  },
  brandBox: {
    textAlign: "center",
    marginBottom: "28px",
  },
  logo: {
    margin: "0 0 8px 0",
    fontSize: "40px",
    color: "#0f172a",
    letterSpacing: "-1px",
  },
  subtitle: {
    margin: 0,
    color: "#64748b",
    fontSize: "15px",
    lineHeight: 1.6,
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "10px",
  },
  label: {
    fontSize: "14px",
    fontWeight: 700,
    color: "#334155",
    marginTop: "6px",
  },
  input: {
    padding: "14px",
    border: "1px solid #d1d5db",
    borderRadius: "14px",
    fontSize: "15px",
    outline: "none",
    backgroundColor: "#f8fafc",
  },
  button: {
    padding: "14px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    border: "none",
    borderRadius: "14px",
    fontSize: "16px",
    cursor: "pointer",
    fontWeight: 800,
    marginTop: "14px",
    boxShadow: "0 12px 24px rgba(37, 99, 235, 0.25)",
  },
  footerText: {
    textAlign: "center",
    color: "#64748b",
    marginTop: "22px",
    marginBottom: 0,
  },
  linkButton: {
    border: "none",
    background: "transparent",
    color: "#2563eb",
    cursor: "pointer",
    fontWeight: 800,
    padding: 0,
  },
  error: {
    marginTop: "16px",
    color: "#dc2626",
    textAlign: "center",
    fontWeight: 600,
  },
};

export default LoginPage;
