import axios from "axios";
import type {
  Movimiento,
  MovimientoCrearRequest,
  PageResponse,
} from "../types/movimiento";

const API_URL = "http://localhost:8080";

export const obtenerMovimientos = async (): Promise<
  PageResponse<Movimiento>
> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<PageResponse<Movimiento>>(
    `${API_URL}/movimientos`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

  return response.data;
};

export const crearMovimiento = async (
  data: MovimientoCrearRequest,
): Promise<Movimiento> => {
  const token = localStorage.getItem("token");

  const response = await axios.post<Movimiento>(
    `${API_URL}/movimientos`,
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    },
  );

  return response.data;
};
