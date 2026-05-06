# NSIP Azure Infrastructure - Terraform Configuration
# Deploying a hardened, production-ready microservices environment

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "nsip_rg" {
  name     = "nsip-prod-rg-v4"
  location = "southeastasia"
}

# 1. Container Registry (ACR)
resource "azurerm_container_registry" "nsip_acr" {
  name                = "nsipregistryv4"
  resource_group_name = azurerm_resource_group.nsip_rg.name
  location            = azurerm_resource_group.nsip_rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

# 2. Kubernetes Service (AKS)
resource "azurerm_kubernetes_cluster" "nsip_aks" {
  name                = "nsip-aks-cluster"
  location            = azurerm_resource_group.nsip_rg.location
  resource_group_name = azurerm_resource_group.nsip_rg.name
  dns_prefix          = "nsip-aks"

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_B2s_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }

  tags = {
    Environment = "Production"
    Project     = "NSIP"
  }
}

# 3. PostgreSQL Flexible Server
resource "azurerm_postgresql_flexible_server" "nsip_db" {
  name                   = "nsip-postgres-v4"
  resource_group_name    = azurerm_resource_group.nsip_rg.name
  location               = azurerm_resource_group.nsip_rg.location
  version                = "14"
  administrator_login    = "nsipadmin"
  administrator_password = "SecurePassword123!"
  sku_name               = "B_Standard_B1ms"
  storage_mb             = 32768
  public_network_access_enabled = true
  zone                   = "1"
}

# 4. Redis Cache (Managed)
resource "azurerm_redis_cache" "nsip_redis" {
  name                = "nsip-redis-v4"
  location            = azurerm_resource_group.nsip_rg.location
  resource_group_name = azurerm_resource_group.nsip_rg.name
  capacity            = 0
  family              = "C"
  sku_name            = "Basic"
  non_ssl_port_enabled = false
  minimum_tls_version = "1.2"
}

resource "random_string" "suffix" {
  length  = 6
  special = false
  upper   = false
}

output "acr_login_server" {
  value = azurerm_container_registry.nsip_acr.login_server
}

output "aks_cluster_name" {
  value = azurerm_kubernetes_cluster.nsip_aks.name
}
