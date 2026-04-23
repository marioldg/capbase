import axios from "axios";
import type {
  Movimiento,
  MovimientoCrearRequest,
  PageResponse,
} from "../types/movimiento";

const API_URL = "http://localhost:8080";

interface ObtenerMovimientosParams {
  categoriaId?: number;
  search?: string;
  orden?: string;
  page?: number;
  size?: number;
}

export const obtenerMovimientos = async (
  params?: ObtenerMovimientosParams,
): Promise<PageResponse<Movimiento>> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<PageResponse<Movimiento>>(
    `${API_URL}/movimientos`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      params,
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

export const eliminarMovimiento = async (id: number): Promise<void> => {
  const token = localStorage.getItem("token");

  await axios.delete(`${API_URL}/movimientos/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};

export const actualizarMovimiento = async (
  id: number,
  data: MovimientoCrearRequest,
): Promise<Movimiento> => {
  const token = localStorage.getItem("token");

  const response = await axios.put<Movimiento>(
    `${API_URL}/movimientos/${id}`,
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

export const obtenerMovimientoPorId = async (
  id: number,
): Promise<Movimiento | null> => {
  const respuesta = await obtenerMovimientos();
  const movimiento = respuesta.content.find((item) => item.id === id);

  return movimiento ?? null;
};
