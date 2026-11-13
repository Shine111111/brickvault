# Stage 1: Build the Vite React application
FROM node:20-alpine AS builder
WORKDIR /app

COPY apps/web/package.json apps/web/package-lock.json ./apps/web/
RUN cd apps/web && npm ci

COPY libs/shared-types ./libs/shared-types
COPY apps/web ./apps/web

RUN cd apps/web && npm run build

# Stage 2: Serve static assets with Nginx
FROM nginx:alpine

COPY apps/web/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/apps/web/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
