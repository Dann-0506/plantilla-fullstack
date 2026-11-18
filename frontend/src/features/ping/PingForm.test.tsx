import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { PingForm } from './PingForm'

afterEach(() => vi.unstubAllGlobals())

describe('PingForm', () => {
  it('muestra el detalle del error 400 que envía el backend', async () => {
    const problema = {
      type: 'about:blank',
      title: 'Bad Request',
      status: 400,
      detail: 'El mensaje no puede superar 140 caracteres.',
      instance: '/api/v1/pings',
    }
    const fetchSimulado = vi.fn().mockResolvedValue(
      new Response(JSON.stringify(problema), {
        status: 400,
        headers: { 'Content-Type': 'application/problem+json' },
      }),
    )
    vi.stubGlobal('fetch', fetchSimulado)

    render(
      <QueryClientProvider client={new QueryClient()}>
        <PingForm />
      </QueryClientProvider>,
    )
    await userEvent.type(screen.getByLabelText('Mensaje (opcional)'), 'texto')
    await userEvent.click(screen.getByRole('button', { name: 'Enviar ping' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('El mensaje no puede superar 140 caracteres.')
    const [url, opciones] = fetchSimulado.mock.calls[0]
    expect(url).toMatch(/\/api\/v1\/pings$/)
    expect(opciones.method).toBe('POST')
    expect(JSON.parse(opciones.body)).toEqual({ mensaje: 'texto' })
  })
})
