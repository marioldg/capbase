import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { register } from "../services/authService";

interface RegisterPageProps {
  onLogin: (token: string) => void;
}

function RegisterPage({ onLogin }: RegisterPageProps) {
  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const manejarRegistro = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      const respuesta = await register({ nombre, email, password });

      onLogin(respuesta.token);
      navigate("/movimientos", { replace: true });
    } catch (err: unknown) {
      console.error(err);

      if (axios.isAxiosError(err)) {
        if (err.response) {
          setError(
            `Error ${err.response.status}: ${
              err.response.data?.mensaje || "No se pudo crear la cuenta"
            }`,
          );
        } else if (err.request) {
          setError("No se pudo conectar con el backend");
        } else {
          setError("Error inesperado al crear la cuenta");
        }
      } else {
        setError("Error inesperado al crear la cuenta");
      }
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.brandBox}>
          <h1 style={styles.logo}>CapBase</h1>
          <p style={styles.subtitle}>
            Crea tu cuenta y empieza a controlar tus finanzas.
          </p>
        </div>

        <form onSubmit={manejarRegistro} style={styles.form}>
          <input
            type="text"
            placeholder="Nombre"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            style={styles.input}
          />

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={styles.input}
          />

          <input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={styles.input}
          />

          <button type="submit" style={styles.button}>
            Crear cuenta
          </button>
        </form>

        <p style={styles.footerText}>
          ¿Ya tienes cuenta?{" "}
          <button
            type="button"
            onClick={() => navigate("/login")}
            style={styles.linkButton}
          >
            Inicia sesión
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
  },
  card: {
    width: "100%",
    maxWidth: "430px",
    backgroundColor: "#ffffff",
    padding: "34px",
    borderRadius: "20px",
    boxShadow: "0 20px 45px rgba(15, 23, 42, 0.12)",
    border: "1px solid #e5e7eb",
  },
  brandBox: {
    textAlign: "center",
    marginBottom: "26px",
  },
  logo: {
    margin: "0 0 8px 0",
    fontSize: "38px",
    color: "#111827",
  },
  subtitle: {
    margin: 0,
    color: "#6b7280",
    fontSize: "15px",
    lineHeight: 1.5,
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "14px",
  },
  input: {
    padding: "13px 14px",
    border: "1px solid #d1d5db",
    borderRadius: "12px",
    fontSize: "15px",
    outline: "none",
  },
  button: {
    padding: "13px",
    backgroundColor: "#2563eb",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    fontSize: "16px",
    cursor: "pointer",
    fontWeight: 700,
    marginTop: "4px",
  },
  footerText: {
    textAlign: "center",
    color: "#6b7280",
    marginTop: "20px",
    marginBottom: 0,
  },
  linkButton: {
    border: "none",
    background: "transparent",
    color: "#2563eb",
    cursor: "pointer",
    fontWeight: 700,
    padding: 0,
  },
  error: {
    marginTop: "16px",
    color: "#dc2626",
    textAlign: "center",
  },
};

export default RegisterPage;
