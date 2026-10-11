# SportSpace Backend API

Spring Boot 3.3 (Java 17/21, Maven) REST API for SportSpace.

## Railway Deployment Guide

1. Create a new service on [Railway](https://railway.app/) and choose **Deploy from GitHub repo** -> select `sportspace-backend`.
2. Add a **MySQL** database in the same project:
   - Click **New** -> **Database** -> **Add MySQL**.
3. Link MySQL credentials to the backend:
   - Click on the `sportspace-backend` service -> **Variables** tab -> **New Variable** -> **Add Reference** -> select `MySQL`.
4. Generate a public domain under `Settings` -> `Networking` (e.g. `https://sportspace-backend.up.railway.app`).
5. Health check endpoint: `/api/health`.

## Local Development

```powershell
mvn spring-boot:run
```
Backend will start on `http://localhost:8080`.
