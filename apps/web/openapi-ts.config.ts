import { defineConfig } from '@hey-api/openapi-ts';

export default defineConfig({
    input: 'http://localhost:8080/v3/api-docs',
    output: 'src/client/api',
    plugins: [
        '@hey-api/typescript',
        '@hey-api/client-fetch',
        '@tanstack/react-query',
        'zod',
    ],
});