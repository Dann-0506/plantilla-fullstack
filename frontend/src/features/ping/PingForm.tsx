import { useState, type FormEvent } from 'react'
import { ApiError } from '../../lib/httpClient'
import { useRegistrarPing } from './usePings'

const LIMITE = 140

/**
 * Formulario para registrar un ping.
 *
 * El campo NO bloquea el texto al llegar a 140 caracteres a propósito: así se
 * puede ver cómo el backend rechaza el dato con un error 400 (Problem Details).
 */
export function PingForm() {
  const [mensaje, setMensaje] = useState('')
  const registrar = useRegistrarPing()

  function enviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    registrar.mutate(mensaje, { onSuccess: () => setMensaje('') })
  }

  const excedido = mensaje.length > LIMITE
  const error = registrar.error instanceof ApiError ? registrar.error : null

  return (
    <form onSubmit={enviar} className="space-y-2">
      <label htmlFor="mensaje" className="block text-sm font-medium">
        Mensaje (opcional)
      </label>
      <div className="flex gap-2">
        <input
          id="mensaje"
          value={mensaje}
          onChange={(e) => setMensaje(e.target.value)}
          placeholder="Hola desde el frontend"
          className="flex-1 rounded-md border border-slate-300 bg-white px-3 py-2 dark:border-slate-600 dark:bg-slate-800"
        />
        <button
          type="submit"
          disabled={registrar.isPending}
          className="rounded-md bg-blue-700 px-4 py-2 font-medium text-white hover:bg-blue-800 disabled:opacity-60"
        >
          {registrar.isPending ? 'Enviando…' : 'Enviar ping'}
        </button>
      </div>
      <p className={`text-xs ${excedido ? 'text-red-600' : 'text-slate-500'}`}>
        {mensaje.length}/{LIMITE}
      </p>
      {error && (
        <p role="alert" className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-800 dark:bg-red-950 dark:text-red-200">
          {error.status > 0 && <strong>{error.status} · </strong>}
          {error.message}
        </p>
      )}
    </form>
  )
}
