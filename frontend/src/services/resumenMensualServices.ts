import axios from "axios";
import type { ResumenMensual } from "../types/resumenMensual";

export const obtenerResumenMensual = async (
  anio: number,
): Promise<ResumenMensual[]> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<ResumenMensual[]>(
    "http://localhost:8080/movimientos/resumen-mensual",
    {
      params: { anio },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

  return response.data;
};
