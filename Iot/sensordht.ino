#include <WiFi.h>
#include <PubSubClient.h>
#include "DHT.h" 


#define DHTPIN 23
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);


const char* ssid = "David"; 
const char* password = "12345678";
const char* mqtt_server = "test.mosquitto.org";

WiFiClient espClient;
PubSubClient client(espClient);


const int LED_WIFI = 2;      // Azul WiFi
const int LED_MQTT = 15;     // Roxo MQTT
const int LED_ALERTA = 4;    // Vermelho Alerta


float limiteTemp = 30.0;
float limiteUmid = 70.0;

void setup_wifi() {
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi conectado");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}


void reconnect() {
  while (!client.connected()) {
    Serial.print("Tentando conectar ao MQTT... ");

    if (client.connect("ESP32_Client")) {
      Serial.println("Conectado!");

      

    } else {
      Serial.print("Falhou, rc=");
      Serial.print(client.state());
      Serial.println(" tentando novamente em 5s...");
      delay(5000);
    }
  }
}



void Enviar_Dados() {

  float temp = dht.readTemperature();
  float umid = dht.readHumidity();

  if (isnan(temp) || isnan(umid)) {
    Serial.println("Falha ao ler do DHT11!");
    return;
  }

  bool alerta = (temp > limiteTemp || umid > limiteUmid);

  
  String json = "{";
  json += "\"temperatura\":" + String(temp, 1) + ",";
  json += "\"umidade\":" + String(umid, 1) + ",";
  json += "\"alerta\":" + String(alerta ? "true" : "false");
  json += "}";


  client.publish("WOKWI/sensores", json.c_str());

  
  if (alerta) {
    digitalWrite(LED_ALERTA, HIGH);
    client.publish("WOKWI/status", "ALERTA! Temperatura ou umidade fora do normal!");
    Serial.println(" ALERTA!");
  } else {
    digitalWrite(LED_ALERTA, LOW);
    client.publish("WOKWI/status", "NORMAL");
  }

  Serial.println(json);
}


void setup() {
  Serial.begin(115200);

  pinMode(LED_WIFI, OUTPUT);
  pinMode(LED_MQTT, OUTPUT);
  pinMode(LED_ALERTA, OUTPUT);

  dht.begin();
  setup_wifi();

  client.setServer(mqtt_server, 1883);
}

void loop() {
  // LEDs de conexão
  digitalWrite(LED_WIFI, WiFi.status() == WL_CONNECTED);

  if (!client.connected()) {
    reconnect();
  }
  digitalWrite(LED_MQTT, client.connected());

  client.loop();

  Enviar_Dados();
  delay(1500);
}
