import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/v1/auth': { target: 'http://localhost:8081', changeOrigin: true },
      '/api/v1/claims': { target: 'http://localhost:8082', changeOrigin: true },
      '/api/v1/contributions': { target: 'http://localhost:8083', changeOrigin: true },
      '/api/v1/learning': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/events': { target: 'http://localhost:8085', changeOrigin: true },
      '/api/v1/rewards': { target: 'http://localhost:8086', changeOrigin: true },
      '/api/v1/payments': { target: 'http://localhost:8088', changeOrigin: true },
      '/api/v1/reviews': { target: 'http://localhost:8089', changeOrigin: true },
    }
  }
})
