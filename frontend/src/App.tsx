import { useEffect } from 'react'
import { PingForm } from './features/ping/PingForm'
import { PingList } from './features/ping/PingList'
import { useInterfazStore } from './lib/store'

export default function App() {
  const { tema, alternarTema } = useInterfazStore()

  useEffect(() => {
    document.documentElement.classList.toggle('dark', tema === 'oscuro')
  }, [tema])

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-slate-900 dark:text-slate-100">
      <main className="mx-auto max-w-2xl space-y-8 px-4 py-10">
        <header className="flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold">Plantilla fullstack</h1>
            <p className="text-slate-600 dark:text-slate-300">
              Ejemplo base: cada ping recorre React → Spring Boot → PostgreSQL.
            </p>
          </div>
          <button
            onClick={alternarTema}
            className="shrink-0 whitespace-nowrap rounded-md border border-slate-300 px-3 py-1.5 text-sm dark:border-slate-600"
          >
            Tema {tema === 'claro' ? 'oscuro' : 'claro'}
          </button>
        </header>

        <PingForm />
        <PingList />
      </main>
    </div>
  )
}
