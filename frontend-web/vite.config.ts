import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'mask-icon.svg'],
      manifest: {
        name: 'National Social Insurance Platform',
        short_name: 'NSIP',
        description: 'Manage benefits, claims, and rewards securely.',
        theme_color: '#1a1a2e',
        icons: [
          {
            src: '/pwa-192x192.png',
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: '/pwa-512x512.png',
            sizes: '512x512',
            type: 'image/png'
          }
        ]
      }
    })
  ],
  build: {
    target: 'esnext',
    minify: 'esbuild',
    cssCodeSplit: true,
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('@mui')) return 'mui';
            if (id.includes('@livekit')) return 'livekit';
            if (id.includes('react')) return 'react';
            return 'vendor';
          }
        }
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': { 
        target: 'http://20.43.189.164', 
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes) => {
            delete proxyRes.headers['www-authenticate'];
          });
        }
      }
    }
  }
})
