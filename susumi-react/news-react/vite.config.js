import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  base: '/news/', // ✅ React 앱이 /news 경로에 mount됨
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173, // React 개발서버 기본 포트
    proxy: {
      '/api': {
        target: 'http://localhost:9093', // ✅ Spring Boot 서버 주소
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: path.resolve(__dirname, '../../src/main/resources/static/news'), // ✅ 빌드 결과 Spring에 포함
    emptyOutDir: true,
    manifest: true,
    manifestFileName: 'assets/manifest.json',
    rollupOptions: {
      input: path.resolve(__dirname, 'index.html'),
    },
  },
});
