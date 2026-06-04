import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import UnoCSS from 'unocss/vite'
import path from 'path'

export default defineConfig({
    plugins: [
        vue(),
        UnoCSS(), // 必须添加这个插件，否则样式不生效
    ],
    build: {
        rollupOptions: {
            input: 'index.html'
        }
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    server: {
        port: 3000, // 前端端口
        proxy: {
            // 关键：将 /api 开头的请求代理到后端 8080
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            },
            '/ws': {
                target: 'ws://localhost:8080',
                ws: true,
                changeOrigin: true
            }
        }
    }
})
