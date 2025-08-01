import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  base: '/', // Spring 정적 리소스 경로와 일치
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9093',
        changeOrigin: true,
        // ⛔️ 삭제 또는 주석 처리
        // rewrite: (p) => p.replace(/^\/api/, '')
      },
    },
  },
  build: {
    outDir: '../src/main/resources/static/notice',
    emptyOutDir: true,
    manifest: true,
    manifestFileName: 'assets/manifest.json', // ✅ 필수
    rollupOptions: {
      input: path.resolve(__dirname, 'index.html'),
    },
  },
});
