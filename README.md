# RSO: Products microservice

## Prerequisites

```bash
docker run -d --name pg-products -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=products -p 5433:5432 postgres:13
```

## Build and run commands
```bash
mvn clean package
cd api/target
java -jar products-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8080/v1/products

## Run in IntelliJ IDEA
Add new Run configuration and select the Application type. In the next step, select the module api and for the main class com.kumuluz.ee.EeApplication.

Available at: localhost:8080/v1/products

## Docker commands
```bash
docker build -t products -f Dockerfile_with_maven_build .   
docker images
docker run products    
docker tag products onlygregor/products   
docker push onlygregor/products
docker ps
```

```bash
docker network ls  
docker network rm rso
docker network create rso
docker run -d --name pg-products -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=products -p 5432:5432 --network rso postgres:13
docker inspect pg-products
docker run -p 8080:8080 --name app_products --network rso -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://pg-products:5432/postgres barbaralipnik/products:latest
```

## Consul
```bash
consul agent -dev
```
Available at: localhost:8500

Key: environments/dev/services/products-service/1.0.0/config/rest-properties/maintenance-mode

Value: true or false

## Kubernetes
```bash
kubectl version
kubectl --help
kubectl get nodes
kubectl create -f products-deployment.yaml 
kubectl apply -f products-deployment.yaml 
kubectl get services 
kubectl get deployments
kubectl get pods
kubectl logs products-deployment-6f59c5d96c-rjz46
kubectl delete pod products-deployment-6f59c5d96c-rjz46
```
Secrets: https://kubernetes.io/docs/concepts/configuration/secret/



### Useful stuff
```bash
helm install consul hashicorp/consul --set global.name=consul --set global.server.replicas=3 --set global.ui.enabled=true --set global.client.enabled=true --set global.client.replicas=3
kubectl port-forward svc/consul-ui 6080:80    # Potem pojdi na localhost:6080 :)

```

### Kubernetes secrets
```bash
kubectl create secret generic SERVICENAME-db --from-literal=username=USERNAME --from-literal=password=PASSWORD
```
Available secret names:
- `products-db`
- `merchants-db`

## GraphQl

Available at: `localhost:8081/graphql`

User interface at: `localhost:8081/graphiql/`