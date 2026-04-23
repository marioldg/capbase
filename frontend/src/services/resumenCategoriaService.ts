import axios from "axios";
import type { ResumenCategoria } from "../types/resumenCategoria";

const API_URL = "http://localhost:8080";

export const obtenerTopCategorias = async (): Promise<ResumenCategoria[]> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<ResumenCategoria[]>(
    `${API_URL}/movimientos/top-categorias`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

  return response.data;
};
