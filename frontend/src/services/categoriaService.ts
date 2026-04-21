import axios from "axios";
import type { Categoria } from "../types/categoria";

const API_URL = "http://localhost:8080";

export const obtenerCategorias = async (): Promise<Categoria[]> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<Categoria[]>(`${API_URL}/categorias`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};
