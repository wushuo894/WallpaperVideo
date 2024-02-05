// vite.config.ts
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {NaiveUiResolver} from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://127.0.0.1:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, ''),
            }
        }
    },
    plugins: [
        vue(),
        AutoImport({
            imports: [
                'vue',
                {
                    'naive-ui': [
                        'useDialog',
                        'useMessage',
                        'useNotification',
                        'useLoadingBar'
                    ]
                }
            ]
        }),
        Components({
            resolvers: [NaiveUiResolver()]
        })
    ]
})
