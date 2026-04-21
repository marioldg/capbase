export interface Movimiento {
  id: number;
  concepto: string;
  descripcion: string | null;
  cantidad: number;
  fecha: string;
  tipo: string;
  categoriaNombre: string;
  usuarioNombre: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface MovimientoCrearRequest {
  concepto: string;
  descripcion: string;
  cantidad: number;
  fecha: string;
  tipo: string;
  categoriaId: number;
}
