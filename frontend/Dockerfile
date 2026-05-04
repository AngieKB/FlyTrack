# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar dependencias
COPY package.json package-lock.json ./
RUN npm ci

# Copiar código fuente completo
COPY . .

# Argumentos de build
ARG BUILD_ENV=production
ARG API_URL=http://localhost:8080/api

# Argumentos de build
ARG BUILD_ENV=production
ARG API_URL=http://localhost:8080/api

# Sobrescribir los archivos dinámicamente. Esto garantiza 100% que usarán la IP inyectada por GitHub Actions.
RUN echo "export const environment = { production: false, apiUrl: '${API_URL}' };" > src/environments/environment.ts && \
    echo "export const environment = { production: false, apiUrl: '${API_URL}' };" > src/environments/environment.staging.ts && \
    echo "export const environment = { production: true, apiUrl: '${API_URL}' };" > src/environments/environment.prod.ts

# Compilar Angular SSR con el environment correcto
RUN npm run build -- --configuration=${BUILD_ENV}

# Stage 2: Runtime con Node.js
FROM node:20-alpine

WORKDIR /app

COPY --from=builder /app/dist/frontend/server ./server
COPY --from=builder /app/dist/frontend/browser ./browser
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 4000
ENV PORT=4000

CMD ["node", "server/server.mjs"]
