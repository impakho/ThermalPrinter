#include <Ticker.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include "SoftwareSerial.h"

#define IO_PRINTER_RX 4
#define IO_PRINTER_TX 0
#define IO_PRINTER_DTR 2
#define IO_BLUETOOTH_SERIAL Serial
#define IO_BLUETOOTH_STATE 5
#define IO_LED_R 14
#define IO_LED_G 12
#define IO_LED_B 13
#define IO_BUTTON 15

WiFiServer server(8800);
SoftwareSerial printerSerial(IO_PRINTER_RX, IO_PRINTER_TX);
Ticker tickerWifi, tickerLed, tickerButton;

int printer_heat_time = 120;

void LedControl(int Red, int Green, int Blue){
  int setRed = 0, setGreen = 0, setBlue = 0;
  if (Red >= 0 && Red <= 255) setRed = Red;
  if (Green >= 0 && Green <= 255) setGreen = Green;
  if (Blue >= 0 && Blue <= 255) setBlue = Blue;
  analogWrite(IO_LED_R, setRed);
  analogWrite(IO_LED_G, setGreen);
  analogWrite(IO_LED_B, setBlue);
}

int ledBlinkState = 0;
int ledBlinkRed = 0, ledBlinkGreen = 0, ledBlinkBlue = 0;
void LedBlink(){
  if (ledBlinkState == 0){
    ledBlinkState = 1;
    LedControl(ledBlinkRed, ledBlinkGreen, ledBlinkBlue);
  }else{
    ledBlinkState = 0;
    LedControl(0, 0, 0);
  }
}

int wifiSmartConfiging = 0;
int buttonPreState = LOW;
unsigned long buttonPreMillis = -1;
int requestNext = 0;
void ButtonCheck(){
  int buttonState = digitalRead(IO_BUTTON);
  if (buttonState != buttonPreState){
    buttonPreState = buttonState;
    if (buttonState == LOW){
      unsigned long betweenMillis = millis() - buttonPreMillis;
      if (betweenMillis >=50 && betweenMillis < 1000){
        requestNext = 1;
      }
      if (betweenMillis >= 1000 && wifiSmartConfiging == 0){
        ledBlinkState = 0;
        ledBlinkRed = 0;
        ledBlinkGreen = 255;
        ledBlinkBlue = 0;
        LedBlink();
        tickerLed.attach(0.6, LedBlink);
        WiFi.beginSmartConfig();
        wifiSmartConfiging = 1;
        WifiCheck();
        tickerWifi.attach(0.3, WifiCheck);
      }
      buttonPreMillis = -1;
    }
  }
  if (buttonState == HIGH && buttonPreMillis == -1){
    buttonPreMillis = millis();
  }
}

void WifiCheck(){
  if (WiFi.status() == WL_CONNECTED){
    if (WiFi.smartConfigDone()){
      tickerLed.detach();
      tickerWifi.detach();
      ledBlinkState = 0;
      ledBlinkRed = 0;
      ledBlinkGreen = 0;
      ledBlinkBlue = 0;
      LedControl(0, 0, 255);
      wifiSmartConfiging = 0;
    }
  }
}

void pinConfig(){
  pinMode(IO_PRINTER_DTR, INPUT);
  pinMode(IO_BLUETOOTH_STATE, INPUT);
  pinMode(IO_LED_R, OUTPUT);
  analogWrite(IO_LED_R, 0);
  pinMode(IO_LED_G, OUTPUT);
  analogWrite(IO_LED_G, 0);
  pinMode(IO_LED_B, OUTPUT);
  analogWrite(IO_LED_B, 255);
  pinMode(IO_BUTTON, INPUT);
}

void setup(){
  pinConfig();

  WiFi.mode(WIFI_STA);
  server.begin();
  IO_BLUETOOTH_SERIAL.begin(9600);
  tickerButton.attach(0.02, ButtonCheck);
  printerSerial.begin(9600);
}

void printerTimeoutWait(){
  while (digitalRead(IO_PRINTER_DTR) == HIGH);
}

int bluetoothConnected = 0;
WiFiClient client;
void loop(){
  if (!client.connected()){
    client = server.available();
  }else{
    while (client.available() > 0){
      printerTimeoutWait();
      if (!client.connected()) break;
      client.write(0xFF);
      printerSerial.write(client.read());
    }
    if (requestNext == 1){
      requestNext = 0;
      client.write(0x01);
    }
  }
  if (digitalRead(IO_BLUETOOTH_STATE) == HIGH){
    if (bluetoothConnected == 0){
      bluetoothConnected = 1;
      IO_BLUETOOTH_SERIAL.flush();
    }
  }else{
    if (bluetoothConnected == 1) bluetoothConnected = 0;
  }
  if (bluetoothConnected == 1){
    while (IO_BLUETOOTH_SERIAL.available() > 0){
      printerTimeoutWait();
      if (digitalRead(IO_BLUETOOTH_STATE) != HIGH) break;
      IO_BLUETOOTH_SERIAL.write(0xFF);
      printerSerial.write(IO_BLUETOOTH_SERIAL.read());
    }
    if (requestNext == 1){
      requestNext = 0;
      IO_BLUETOOTH_SERIAL.write(0x01);
    }
  }
}
