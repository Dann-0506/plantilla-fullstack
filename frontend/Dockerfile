# Servidor de desarrollo de Vite con recarga en caliente.
FROM node:26-alpine
WORKDIR /app

# Primero solo los manifiestos: Docker reutiliza esta capa (y las
# dependencias instaladas) mientras no cambien.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev"]
