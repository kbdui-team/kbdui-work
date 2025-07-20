import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
// import viteConfig from './vite.config'

export default mergeConfig(
  // viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
    },

  server: {
    proxy: {
      '/user': 'http://localhost:5555',
      // 其他后端接口前缀也可以加
    }
  }
}
  )
   
)