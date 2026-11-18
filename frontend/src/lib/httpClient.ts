/**
 * Cliente HTTP único de la aplicación.
 *
 * Todas las llamadas al backend pasan por aquí: la URL base se define una sola
 * vez (variable de entorno VITE_API_URL) y los errores se convierten siempre en
 * un ApiError con el cuerpo Problem Details que envía el backend.
 */

const BASE_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080').replace(/\/$/, '')

/** Formato de error del backend (RFC 9457). */
export interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  errores?: Record<string, string>
}

export class ApiError extends Error {
  readonly status: number
  readonly problema: ProblemDetail | null

  constructor(status: number, problema: ProblemDetail | null, mensaje: string) {
    super(mensaje)
    this.name = 'ApiError'
    this.status = status
    this.problema = problema
  }
}

async function solicitar<T>(metodo: string, ruta: string, cuerpo?: unknown): Promise<T> {
  let respuesta: Response
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      method: metodo,
      headers: {
        Accept: 'application/json',
        ...(cuerpo !== undefined && { 'Content-Type': 'application/json' }),
      },
      body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
    })
  } catch {
    // fetch solo falla así cuando no hay respuesta: backend apagado,
    // URL incorrecta o petición bloqueada por CORS.
    throw new ApiError(
      0,
      null,
      `No se pudo contactar al backend en ${BASE_URL}. Revise que esté en ejecución y que permita este origen (CORS).`,
    )
  }

  if (!respuesta.ok) {
    let problema: ProblemDetail | null = null
    try {
      problema = (await respuesta.json()) as ProblemDetail
    } catch {
      // La respuesta no traía JSON.
    }
    throw new ApiError(respuesta.status, problema, problema?.detail ?? `Error ${respuesta.status}`)
  }

  if (respuesta.status === 204) {
    return undefined as T
  }
  return (await respuesta.json()) as T
}

export const http = {
  get: <T>(ruta: string) => solicitar<T>('GET', ruta),
  post: <T>(ruta: string, cuerpo: unknown) => solicitar<T>('POST', ruta, cuerpo),
  put: <T>(ruta: string, cuerpo: unknown) => solicitar<T>('PUT', ruta, cuerpo),
  delete: <T>(ruta: string) => solicitar<T>('DELETE', ruta),
}
