import axios from "axios";
import type { AuthResponse, LoginRequest } from "../types/auth";

const API_URL = "http://localhost:8080";

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const response = await axios.post<AuthResponse>(
    `${API_URL}/auth/login`,
    data,
    {
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  return response.data;
};

export const register = async (
  data: RegisterRequest,
): Promise<AuthResponse> => {
  const response = await axios.post<AuthResponse>(
    `${API_URL}/auth/register`,
    data,
    {
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  return response.data;
};
