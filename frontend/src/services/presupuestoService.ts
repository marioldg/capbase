import axios from "axios";
import type { PresupuestoEstado } from "../types/presupuestoEstado";
export const obtenerEstadoPresupuestos = async (): Promise<
  PresupuestoEstado[]
> => {
  const token = localStorage.getItem("token");

  const response = await axios.get<PresupuestoEstado[]>(
    "http://localhost:8080/presupuestos/estado",
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

  return response.data;
};
