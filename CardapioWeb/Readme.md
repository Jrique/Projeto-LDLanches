# LDLanches - Sistema de Gestão de Lanchonete


**Integrantes:**
* José Henrique
* Davi
* David Braga


## Descrição do Projeto
O **LDLanches** é um sistema completo para gestão de pedidos e monitoramento de ambiente para lanchonetes. O projeto é composto por uma arquitetura moderna dividida em:

1.  **Backend (Spring Boot):** API RESTful responsável pela gestão de pedidos, produtos, categorias, autenticação (JWT) e integração com sensores IoT via MQTT.
2.  **Frontend Administrativo (Kotlin Multiplatform - KMP):** Aplicação multiplataforma (Android, Desktop e Web) para gestão de vendas em tempo real, cadastro de produtos e visualização de dashboards financeiros.
3.  **Monitoramento IoT:** Integração com sensores de temperatura (via Wokwi/MQTT) para monitoramento de freezers com alertas em tempo real.

## Instruções de Instalação e Execução

### Pré-requisitos
* JDK 17 ou superior.
* Android Studio (para executar o app Android).
* Docker (opcional, para rodar broker MQTT local, ou usar `test.mosquitto.org`).

### 1. Executando o Backend (Servidor)
O backend deve ser o primeiro a ser iniciado.
1.  Navegue até a pasta `demo` (ou onde está o `build.gradle.kts` do Spring).
2.  Execute o comando:

    ./gradlew bootRun

3.  O servidor iniciará em `http://localhost:8081`.

### 2. Executando o Frontend (Administrativo)
Com o backend rodando, inicie o aplicativo KMP.
1.  Navegue até a raiz do projeto KMP (`LDLanchesASW`).
2.  **Para Desktop:**
    ./gradlew :composeApp:run

3.  **Para Android:**
    Abra o projeto no Android Studio e execute no emulador/dispositivo.
    
4.  **Para Web:**
    ./gradlew :composeApp:wasmJsBrowserRun


### 3. Simulando Sensores (IoT)
O sistema se conecta automaticamente ao broker MQTT público (`test.mosquitto.org`).
* Para simular a temperatura, utilize o simulador Wokwi configurado para publicar no tópico `WOKWI/sensores`.

---