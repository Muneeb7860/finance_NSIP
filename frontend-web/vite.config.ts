import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    target: 'esnext',
    minify: 'esbuild',
    cssCodeSplit: true,
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        // Standard chunking
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': { 
        target: 'http://localhost:8080', 
        changeOrigin: true,
        secure: false
      }
    }
  }
})
