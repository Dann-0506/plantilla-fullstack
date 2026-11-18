/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** URL base del backend, por ejemplo http://localhost:8080 */
  readonly VITE_API_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
