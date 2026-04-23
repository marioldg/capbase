import axios from "axios";
import type { Resumen } from "../types/resumen";

const API_URL = "http://localhost:8080";

export const obtenerResumen = async (): Promise<Resumen> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<Resumen>(`${API_URL}/movimientos/resumen`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};
