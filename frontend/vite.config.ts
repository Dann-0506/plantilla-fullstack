/// <reference types="vitest/config" />
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    // Escucha en todas las interfaces para que funcione dentro de Docker.
    host: true,
    port: 5173,
    strictPort: true,
    // En Docker Desktop (Windows/macOS) los cambios de archivos montados a
    // veces no se detectan; VITE_WATCH_POLLING=true activa el sondeo.
    watch: { usePolling: process.env.VITE_WATCH_POLLING === 'true' },
  },
  test: {
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
})
