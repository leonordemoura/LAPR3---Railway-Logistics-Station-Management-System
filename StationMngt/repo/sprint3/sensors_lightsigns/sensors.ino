#include <Arduino.h>
#include <DHT.h>

#define DHTPIN 4
#define DHTTYPE DHT11

#define MAX_TRACKS 99

DHT dht(DHTPIN, DHTTYPE);

// Define os pins digitais a que os leds estão conectados no trilho 1
#define LED_GT1 15
#define LED_YT1 14
#define LED_RT1 13

// Define os pins digitais a que os leds estão conectados no trilho 2
#define LED_GT2 16
#define LED_YT2 17
#define LED_RT2 18

bool blinking[MAX_TRACKS + 1];
unsigned long previousMillis[MAX_TRACKS + 1];
bool blinkState[MAX_TRACKS + 1];
const long blinkInterval = 300;


void setup() {
  Serial.begin(9600);
  dht.begin();

  for(int i = 1; i <= MAX_TRACKS; i++) {
    blinking[i] = false;
    blinkState[i] = false;
    previousMillis[i] = 0;
  }

  // Definir as LEDS como Output
  pinMode(LED_GT1, OUTPUT);
  pinMode(LED_YT1, OUTPUT);
  pinMode(LED_RT1, OUTPUT);
  pinMode(LED_GT2, OUTPUT);
  pinMode(LED_YT2, OUTPUT);
  pinMode(LED_RT2, OUTPUT);
}

void loop() {

  // --- Comandos via Serial ---
  if (Serial.available() > 0) {
    String cmd = Serial.readStringUntil('\n');
    cmd.trim();

    // === SENSOR ===
    if (cmd == "GTH") {

      float t = dht.readTemperature();
      float h = dht.readHumidity();

      if (isnan(t) || isnan(h)) {
        Serial.println("ERR:SENSOR");
      } else {
        Serial.print("TEMP&unit:celsius&value:");
        Serial.print((int)t);
        Serial.print("#HUM&unit:percentage&value:");
        Serial.println((int)h);
      }

    } else {
      processCommand(cmd);
    }
  }

  handleBlinking();
}


/* ================== TRACK COMMANDS ================== */

void processCommand(String command) {
  command.trim();

  // Encontra a posição da vírgula
  int commaIndex = command.indexOf(',');

  if(commaIndex == -1) {
    Serial.println("O formato do comando é inválido");
    return;
  }

  // Define o tipo do comando
  String cmdType = command.substring(0, commaIndex);

  // Define the track number
  String trackStr = command.substring(commaIndex + 1);
  int trackNum = trackStr.toInt();

  // Faz a validação do número do trilhp
  if(trackNum < 1 || trackNum > 99) {
    Serial.println("Número do trilho inválido");
    return;
  }

  if(trackNum > 2) {
    Serial.println("INFO: Comando aceite mas o trilho " + String(trackNum) + " não existe fisicamente");
    return;
  }

  // Executa o comando
  executeCommand(cmdType, trackNum);
}

void executeCommand(String cmdType, int trackNum) {
  // Desliga todas as luzes do trilho
  turnOffTrack(trackNum);

  if(cmdType != "RB") {
    blinking[trackNum] = false;
  }

  // Liga a LED pedida
  if(cmdType == "RE") {
    setTrackLED(trackNum, 'R', HIGH);
    Serial.println("Ligou a led VERMELHA no trilho " + String(trackNum));

  } else if(cmdType == "YE") {
    setTrackLED(trackNum, 'Y', HIGH);
    Serial.println("Ligou a led AMARELA no trilho " + String(trackNum));

  } else if(cmdType == "GE") {
    setTrackLED(trackNum, 'G', HIGH);
    Serial.println("Ligou a led VERDE no trilho " + String(trackNum));

  } else if(cmdType == "RB") {
    startBlinking(trackNum);
    Serial.println("Ligou a led VERMELHA a piscar no trilho " + String(trackNum));

  } else {
    Serial.println("Tipo de comando desconhecido");
  }
}

void turnOffTrack(int trackNum) {
  if(trackNum == 1) {
    digitalWrite(LED_GT1, LOW);
    digitalWrite(LED_YT1, LOW);
    digitalWrite(LED_RT1, LOW);
  } else if(trackNum == 2) {
    digitalWrite(LED_GT2, LOW);
    digitalWrite(LED_YT2, LOW);
    digitalWrite(LED_RT2, LOW);
  }
}

void setTrackLED(int trackNum, char color, int state) {
  if(trackNum == 1) {
    if(color == 'R') digitalWrite(LED_RT1, state);
    else if(color == 'Y') digitalWrite(LED_YT1, state);
    else if(color == 'G') digitalWrite(LED_GT1, state);

  } else if(trackNum == 2) {
    if(color == 'R') digitalWrite(LED_RT2, state);
    else if(color == 'Y') digitalWrite(LED_YT2, state);
    else if(color == 'G') digitalWrite(LED_GT2, state);
  }
}

void startBlinking(int trackNum) {
  blinking[trackNum] = true;
  previousMillis[trackNum] = millis();
  blinkState[trackNum] = false;
}

void handleBlinking() {
  unsigned long currentMillis = millis();

  for(int track = 1; track <= MAX_TRACKS; track++) {
    if(blinking[track]) {
      if(currentMillis - previousMillis[track] >= blinkInterval) {
        previousMillis[track] = currentMillis;
        blinkState[track] = !blinkState[track];

        if(track <= 2) { // só acende se existir fisicamente
          setTrackLED(track, 'R', blinkState[track] ? HIGH : LOW);
        }
      }
    }
  }
}
