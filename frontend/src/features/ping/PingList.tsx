import { ApiError } from '../../lib/httpClient'
import { usePings } from './usePings'

const formato = new Intl.DateTimeFormat('es-MX', { dateStyle: 'short', timeStyle: 'medium' })

/** Listado de los últimos pings con indicador de conexión al backend. */
export function PingList() {
  const { data, isPending, error } = usePings()

  if (isPending) {
    return <p className="text-slate-500">Consultando el backend…</p>
  }

  if (error) {
    return (
      <div role="alert" className="rounded-md bg-red-50 px-4 py-3 text-red-800 dark:bg-red-950 dark:text-red-200">
        <p className="font-medium">Sin conexión con el backend</p>
        <p className="text-sm">{error instanceof ApiError ? error.message : String(error)}</p>
      </div>
    )
  }

  return (
    <section className="space-y-3">
      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-slate-600 dark:text-slate-300">
        <span className="flex items-center gap-2">
          <span className="size-2.5 rounded-full bg-green-600" aria-hidden />
          Conectado
        </span>
        <span>Total de pings: {data.total}</span>
        <span>Hora de la base de datos: {formato.format(new Date(data.horaBaseDeDatos))}</span>
      </div>

      {data.ultimos.length === 0 ? (
        <p className="text-slate-500">Aún no hay pings. Envíe el primero.</p>
      ) : (
        <ul className="divide-y divide-slate-200 rounded-md border border-slate-200 dark:divide-slate-700 dark:border-slate-700">
          {data.ultimos.map((ping) => (
            <li key={ping.id} className="flex justify-between gap-4 px-4 py-2">
              <span className={ping.mensaje ? '' : 'italic text-slate-500'}>
                {ping.mensaje ?? 'sin mensaje'}
              </span>
              <span className="shrink-0 text-sm text-slate-500">
                #{ping.id} · {formato.format(new Date(ping.creadoEn))}
              </span>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
