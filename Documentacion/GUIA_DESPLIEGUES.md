# 🚀 Guía de Despliegue - FlyTrack

## 📋 Descripción General

Este documento proporciona opciones para implementar los despliegues en **Staging** (rama `tests`) y **Producción** (rama `main`).

Actualmente, los jobs de despliegue tienen implementaciones básicas (`echo` statements). Aquí se describe cómo implementarlos realmente.

---

## 🎯 Nivel de Complejidad por Opción

```
Fácil     ► Docker Compose en VM
Medio     ► Docker Swarm / Nomad
Difícil   ► Kubernetes
Avanzado  ► GitOps (ArgoCD) + Kubernetes
```

---

## 📌 Opción 1: Docker Compose en VM (Recomendado para Fase 3)

### Características
- ✅ Simple de implementar
- ✅ Bajo overhead
- ✅ Ideal para staging y pequeña producción
- ❌ No es scalable horizontalmente

### Prerequisitos
- VM con Docker instalado
- SSH acceso a la VM
- Docker CLI instalado
- Private key configurado en GitHub Secrets

### Pasos de Implementación

#### 1. Crear Secret en GitHub

Settings → Secrets → New repository secret:
```
Nombre: DEPLOY_KEY
Valor: [contenido de tu clave privada SSH]

Nombre: DEPLOY_HOST
Valor: usuario@ip-o-dominio.com

Nombre: DEPLOY_PATH
Valor: /home/usuario/flytrack
```

#### 2. Crear Script de Despliegue

**Archivo**: `scripts/deploy-staging.sh`

```bash
#!/bin/bash

set -e

REGISTRY="${1:-ghcr.io}"
IMAGE="${2:-$REGISTRY/tu-usuario/flytrack:tests}"

echo "📥 Pulling latest image: $IMAGE"
docker pull $IMAGE

echo "🛑 Stopping old container..."
docker stop flytrack-staging || true
docker rm flytrack-staging || true

echo "🚀 Starting new container..."
docker run -d \
  --name flytrack-staging \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=staging \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/flytrack_db \
  -e SPRING_DATASOURCE_USERNAME=$DB_USER \
  -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
  --network flytrack-network \
  $IMAGE

echo "✅ Staging deployed successfully!"
```

#### 3. Actualizar Workflow

**Archivo**: `.github/workflows/ci-cd-pipeline.yml`

```yaml
deploy-staging:
  runs-on: ubuntu-latest
  name: Deploy to Staging
  needs: docker-build
  if: github.ref == 'refs/heads/tests' && github.event_name == 'push'
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Configure SSH
      run: |
        mkdir -p ~/.ssh
        echo "${{ secrets.DEPLOY_KEY }}" > ~/.ssh/id_rsa
        chmod 600 ~/.ssh/id_rsa
        ssh-keyscan -H ${{ secrets.DEPLOY_HOST }} >> ~/.ssh/known_hosts

    - name: Deploy via SCP & SSH
      run: |
        # Copiar script
        scp scripts/deploy-staging.sh \
          ${{ secrets.DEPLOY_HOST }}:${{ secrets.DEPLOY_PATH }}/
        
        # Ejecutar despliegue
        ssh ${{ secrets.DEPLOY_HOST }} \
          "cd ${{ secrets.DEPLOY_PATH }} && \
           bash deploy-staging.sh \
           ghcr.io \
           ${{ github.repository }}:tests"

    - name: Verify Deployment
      run: |
        ssh ${{ secrets.DEPLOY_HOST }} \
          "docker ps | grep flytrack-staging"
```

---

## 📌 Opción 2: Kubernetes (Recomendado para Producción Real)

### Características
- ✅ Scalable horizontalmente
- ✅ Auto-healing
- ✅ Load balancing
- ✅ Service mesh ready
- ❌ Mayor complejidad

### Prerequisitos
- Cluster Kubernetes activo
- `kubectl` configurado localmente
- Imagen en Container Registry
- Kubeconfig en GitHub Secrets

### Pasos de Implementación

#### 1. Crear Manifests de Kubernetes

**Archivo**: `k8s/namespace.yaml`
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: flytrack-staging
  labels:
    environment: staging
```

**Archivo**: `k8s/deployment-staging.yaml`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: flytrack
  namespace: flytrack-staging
  labels:
    app: flytrack
    environment: staging
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: flytrack
  template:
    metadata:
      labels:
        app: flytrack
        version: v1
    spec:
      containers:
      - name: flytrack
        image: ghcr.io/tu-usuario/flytrack:tests
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: staging
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: flytrack-config
              key: db.url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: flytrack-secrets
              key: db.username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: flytrack-secrets
              key: db.password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
      imagePullSecrets:
      - name: ghcr-secret
---
apiVersion: v1
kind: Service
metadata:
  name: flytrack
  namespace: flytrack-staging
spec:
  type: LoadBalancer
  selector:
    app: flytrack
  ports:
  - name: http
    port: 80
    targetPort: 8080
```

**Archivo**: `k8s/configmap.yaml`
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: flytrack-config
  namespace: flytrack-staging
data:
  db.url: jdbc:mariadb://mariadb.default:3306/flytrack_db
```

**Archivo**: `k8s/secrets.yaml`
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: flytrack-secrets
  namespace: flytrack-staging
type: Opaque
stringData:
  db.username: root
  db.password: EL_PASSWORD_FUERTE_AQUI
---
apiVersion: v1
kind: Secret
metadata:
  name: ghcr-secret
  namespace: flytrack-staging
type: kubernetes.io/dockerconfigjson
data:
  .dockerconfigjson: # Base64 de tu config docker
```

#### 2. Configurar GitHub Secrets

```
KUBE_CONFIG: [contenido de ~/.kube/config]
KUBE_NAMESPACE_STAGING: flytrack-staging
KUBE_NAMESPACE_PROD: flytrack-production
```

#### 3. Actualizar Workflow

```yaml
deploy-staging:
  runs-on: ubuntu-latest
  name: Deploy to Staging (Kubernetes)
  needs: docker-build
  if: github.ref == 'refs/heads/tests' && github.event_name == 'push'
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Setup Kubernetes
      uses: azure/setup-kubectl@v3
      with:
        version: 'v1.28.0'

    - name: Configure Kubeconfig
      run: |
        mkdir -p $HOME/.kube
        echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > $HOME/.kube/config
        chmod 600 $HOME/.kube/config

    - name: Deploy to Kubernetes
      run: |
        kubectl apply -f k8s/namespace.yaml
        kubectl apply -f k8s/configmap.yaml
        kubectl apply -f k8s/secrets.yaml
        kubectl apply -f k8s/deployment-staging.yaml --namespace=flytrack-staging
        
        # Esperar a que esté listo
        kubectl rollout status deployment/flytrack \
          --namespace=flytrack-staging \
          --timeout=5m

    - name: Verify Deployment
      run: |
        kubectl get pods --namespace=flytrack-staging
        kubectl get svc --namespace=flytrack-staging
```

---

## 📌 Opción 3: Heroku/Railway/Render (Simplificado)

### Características
- ✅ PaaS - muy simple
- ✅ Escalado automático
- ✅ HTTPS automático
- ✅ Logging centralizado
- ❌ Costo potencialmente alto
- ❌ Menos control

### Pasos (Ejemplo Heroku)

#### 1. Crear app en Heroku
```bash
heroku login
heroku create flytrack-staging
heroku config:set SPRING_PROFILES_ACTIVE=staging
```

#### 2. Configurar en GitHub
```
Secrets:
HEROKU_EMAIL: tu@email.com
HEROKU_API_KEY: tu_api_key
HEROKU_APP_NAME: flytrack-staging
```

#### 3. Workflow
```yaml
deploy-staging:
  runs-on: ubuntu-latest
  if: github.ref == 'refs/heads/tests'
  steps:
    - uses: actions/checkout@v4
    
    - name: Deploy to Heroku
      uses: AkhileshNS/heroku-deploy@v3.13.15
      with:
        heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
        heroku_app_name: ${{ secrets.HEROKU_APP_NAME }}
        heroku_email: ${{ secrets.HEROKU_EMAIL }}
```

---

## 📌 Opción 4: AWS ECS/AppRunner

### Características
- ✅ Escalable
- ✅ Integración con AWS
- ✅ Load balancing
- ❌ Configuración más compleja

### Pasos Básicos

```yaml
deploy-staging:
  runs-on: ubuntu-latest
  if: github.ref == 'refs/heads/tests'
  steps:
    - uses: actions/checkout@v4
    
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v2
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-east-1
    
    - name: Deploy to ECS
      run: |
        aws ecs update-service \
          --cluster flytrack-cluster \
          --service flytrack-staging \
          --force-new-deployment
```

---

## ✅ Checklist de Activación

### Para Docker Compose en VM

- [ ] VM creada y accesible vía SSH
- [ ] Docker instalado en VM
- [ ] SSH key generada y agregada a VM
- [ ] GitHub Secrets configurados (DEPLOY_KEY, DEPLOY_HOST, DEPLOY_PATH)
- [ ] Script de despliegue creado
- [ ] Workflow actualizado en `.github/workflows/ci-cd-pipeline.yml`
- [ ] Test: Push a rama `tests` y verificar despliegue

### Para Kubernetes

- [ ] Cluster K8s levantado (minikube, EKS, GKE, etc.)
- [ ] `kubectl` configurado localmente
- [ ] Kubeconfig en GitHub Secrets
- [ ] Namespaces creados
- [ ] ConfigMaps y Secrets configurados
- [ ] Manifests de Deployment creados
- [ ] Docker Compose para BD en K8s (StatefulSet)
- [ ] Workflow actualizado
- [ ] Test: Push a rama `tests` y verificar despliegue

---

## 🔄 Secuencia de Despliegue Recomendada

```
1. Desarrollador hace push a 'tests'
   ↓
2. GitHub Actions dispara el pipeline
   ├─ BUILD ✅
   ├─ TEST ✅
   ├─ DOCKER BUILD ✅
   └─ DEPLOY STAGING ⏳
      └─ Despliego en Docker Compose o K8s
         ├─ Health check
         ├─ Smoke tests
         └─ ✅ Staging ready
   
3. Validación Manual en Staging
   ├─ Tests de integración
   ├─ Testing de usuario
   └─ Aprobación

4. Merge a 'main' cuando esté OK
   ↓
5. GitHub Actions dispara para MAIN
   ├─ BUILD ✅
   ├─ TEST ✅
   ├─ DOCKER BUILD ✅
   └─ DEPLOY PRODUCTION ⏳ (Requiere aprobación)
      └─ GitHub Environment approval
         ├─ Aprobador revisa
         └─ Aprueba despliegue
            ├─ Deploy en K8s/VM
            ├─ Verificación
            └─ ✅ Production ready
```

---

## 🧪 Testing del Pipeline Localmente

```bash
# 1. Clonar repo
git clone <repositorio>
cd FlyTrack

# 2. Crear rama de test
git checkout -b test-pipeline

# 3. Hacer cambio pequeño
echo "# Test pipeline" >> README.md

# 4. Commit y push
git add .
git commit -m "Test pipeline"
git push origin test-pipeline

# 5. Ir a GitHub → Actions y ver ejecución

# 6. Después de validar
git checkout tests
git merge test-pipeline
git push origin tests
```

---

## 📊 Matriz de Decisión

| Criterio | Docker Compose | Kubernetes | Heroku | AWS |
|----------|---|---|---|---|
| Curva Aprendizaje | Baja | Alta | Muy Baja | Media |
| Costo | Bajo | Medio | Medio/Alto | Bajo-Alto |
| Escalabilidad | No | Sí | Sí | Sí |
| Control | Alto | Muy Alto | Bajo | Muy Alto |
| Tiempo Setup | 1 hora | 1–2 días | 30 min | 2-3 horas |
| Recomendación | ✅ Fase 3 | ✅ Producción Real | ✅ Prototipos | ✅ Corporativo |

---

## 🆘 Troubleshooting

### Error: "Connection refused"
```
Solución: Verificar que la BD está accesible desde contenedor
- Comprobar network: docker network ls
- Verificar port forwards
```

### Error: "Image pull failed"
```
Solución: Verificar credenciales de registry
- GitHub Secrets con token correcto
- Permisos de lectura en repositorio
```

### Pod en pending
```
Solución: Revisar recursos disponibles
- kubectl describe pod <pod-name>
- Verificar requests/limits
```

---

**Última actualización**: 2026-04-28
**Versión**: 1.0

