import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  base: './',
  plugins: [react()],
  server: {
    historyApiFallback: true, // 모든 경로에 대해 index.html을 반환하도록 설정
  },
  optimizeDeps: {
    include: ['jwt-decode'],
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    emptyOutDir: true,
  },
});
