interface ImportMetaEnv {
  readonly VITE_BACKEND_URL?: string;
  readonly VITE_API_USER?: string;
  readonly VITE_API_PASSWORD?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
