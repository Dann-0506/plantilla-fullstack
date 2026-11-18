import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { PingList } from './PingList'

/**
 * Prueba de componente: se simula la respuesta del backend reemplazando
 * fetch, así que no se necesita el servidor en ejecución.
 */

function renderizar() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <PingList />
    </QueryClientProvider>,
  )
}

afterEach(() => vi.unstubAllGlobals())

describe('PingList', () => {
  it('muestra los pings que devuelve el backend', async () => {
    const resumen = {
      ultimos: [
        { id: 2, mensaje: 'Hola desde la prueba', creadoEn: '2026-10-31T18:04:12Z' },
        { id: 1, mensaje: null, creadoEn: '2026-10-31T18:00:00Z' },
      ],
      total: 2,
      horaBaseDeDatos: '2026-10-31T18:05:00Z',
    }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify(resumen), { status: 200 })))

    renderizar()

    expect(await screen.findByText('Hola desde la prueba')).toBeInTheDocument()
    expect(screen.getByText('sin mensaje')).toBeInTheDocument()
    expect(screen.getByText('Total de pings: 2')).toBeInTheDocument()
  })

  it('avisa cuando el backend no responde', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))

    renderizar()

    expect(await screen.findByText('Sin conexión con el backend')).toBeInTheDocument()
  })
})
