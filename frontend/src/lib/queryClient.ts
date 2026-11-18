import { QueryClient } from '@tanstack/react-query'
import { ApiError } from './httpClient'

/** Configuración compartida de TanStack Query. */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // No reintentar errores 4xx: volver a pedir lo mismo no los corrige.
      retry: (intentos, error) =>
        !(error instanceof ApiError && error.status >= 400 && error.status < 500) && intentos < 2,
      staleTime: 10_000,
    },
  },
})
