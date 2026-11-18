import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { http } from '../../lib/httpClient'

/** Tipos que reflejan los DTOs del backend (PingResponse y PingResumen). */
export interface Ping {
  id: number
  mensaje: string | null
  creadoEn: string
}

export interface PingResumen {
  ultimos: Ping[]
  total: number
  horaBaseDeDatos: string
}

const RUTA = '/api/v1/pings'
/** Clave con la que TanStack Query guarda estos datos en su caché. */
export const CLAVE_PINGS = ['pings'] as const

/** Lectura: GET /api/v1/pings. */
export function usePings() {
  return useQuery({
    queryKey: CLAVE_PINGS,
    queryFn: () => http.get<PingResumen>(RUTA),
  })
}

/** Escritura: POST /api/v1/pings. Al terminar, refresca el listado. */
export function useRegistrarPing() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (mensaje: string) => http.post<Ping>(RUTA, { mensaje }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CLAVE_PINGS }),
  })
}
