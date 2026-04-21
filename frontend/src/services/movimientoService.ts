import axios from "axios";
import type { Movimiento, PageResponse } from "../types/movimiento";

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
