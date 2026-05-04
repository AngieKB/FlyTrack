# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar dependencias
COPY package.json package-lock.json ./
RUN npm ci

# Copiar código fuente completo
COPY . .

# Argumento para elegir el environment
ARG BUILD_ENV=production

# Compilar Angular SSR con el environment correcto
RUN npm run build -- --configuration=${BUILD_ENV}

# Stage 2: Runtime con Node.js
FROM node:20-alpine

WORKDIR /app

# Copiar el build completo
COPY --from=builder /app/dist/frontend/server ./server
COPY --from=builder /app/dist/frontend/browser ./browser

# Instalar dependencias del servidor SSR
COPY --from=builder /app/node_modules ./node_modules

# Puerto del servidor SSR
EXPOSE 4000

ENV PORT=4000

# Arrancar el servidor SSR
CMD ["node", "server/server.mjs"]
