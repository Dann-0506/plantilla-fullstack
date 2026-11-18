import { create } from 'zustand'
import { persist } from 'zustand/middleware'

/**
 * Estado propio de la interfaz (estado del cliente).
 *
 * Regla de la plantilla: los datos que vienen del backend NO se guardan aquí,
 * sino en TanStack Query. Zustand es para lo que solo existe en el navegador,
 * como el tema visual. En el taller, aquí se agrega la sesión del usuario.
 */

type Tema = 'claro' | 'oscuro'

interface EstadoInterfaz {
  tema: Tema
  alternarTema: () => void
}

export const useInterfazStore = create<EstadoInterfaz>()(
  persist(
    (set) => ({
      tema: 'claro',
      alternarTema: () => set((estado) => ({ tema: estado.tema === 'claro' ? 'oscuro' : 'claro' })),
    }),
    // persist guarda el tema en localStorage para recordarlo al recargar.
    { name: 'plantilla-interfaz' },
  ),
)
