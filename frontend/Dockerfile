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

# Reemplazar la URL del API en el environment correcto antes de compilar
RUN if [ "$BUILD_ENV" = "production" ]; then \
      sed -i "s|http://18.221.216.128:8080/api|${API_URL}|g" src/environments/environment.prod.ts; \
    elif [ "$BUILD_ENV" = "staging" ]; then \
      sed -i "s|http://18.220.140.37:8080/api|${API_URL}|g" src/environments/environment.staging.ts; \
    fi

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
