// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

// Set up first array of LEDs
#define ARRAY1PIXELS      20
#define ARRAY1PIN         6
Adafruit_NeoPixel array1 = Adafruit_NeoPixel(14, ARRAY1PIN, NEO_GRB + NEO_KHZ800);

#define COLORSLENGTH  20
char colors [COLORSLENGTH];

//14 channels - 13 dress LEDs + 1 axon LED
//3 bytes per channel - one for R, one for G, one for B
#define DRESSLEDARRAYLENGTH 13
char dressLEDColors [DRESSLEDARRAYLENGTH][3];

int delayval = 250; // delay for half a second

void setup() {
  Serial.begin(9600);
  array1.begin();

  for(int i = 0; i < DRESSLEDARRAYLENGTH; i++){
    for(int j = 0; j < 3; j++){
      dressLEDColors[i][j] = 0x000000;
    }
  }
}

void loop() {
  //Only run through if serial port is avaiable and there is a valid stream coming in
  if(Serial.available() && Serial.peek() != -1) {
    //BIG ASSUMPTION HERE: if there is a valid next character, we are assuming there are enough valid characters to fill out dressLEDColors, which may not necessarily be true
    //Not robust coding...    
    for(int i = 0; i < DRESSLEDARRAYLENGTH; i++){
      for(int j = 0; j < 3; j++){
        char c;
        if (Serial.peek() == -1){
          c = 0x00;
        } else {
          c = Serial.read();
        }
        
        dressLEDColors[i][j] = c;
      }
    }

    writeColorsToLEDs();
  }
}


void writeColorsToLEDs() {
  for (int i = 0; i < DRESSLEDARRAYLENGTH; i++) {
    array1.setPixelColor(i, dressLEDColors[i][0], dressLEDColors[i][1], dressLEDColors[i][2]); 
  }

  array1.show();
}

/*
void setup() {
  Serial.begin(9600);
  array1.begin();

  for (int i = 0; i < COLORSLENGTH; i++) {
    colors[i] = 'n';
  }

  writeColorsToLEDs();
}

void loop () {
  char c;

  if (Serial.available()) {
    c = Serial.read();

    if (c != 0) {
      addToColors(c);
      writeColorsToLEDs();
    }
  }
}

//This is a test sequence
void loop2() {
  char rainbow [8] = {'r', 'o', 'y', 'g', 'c', 'b', 'v', 'p'};
  int count = 0;
  while(true) {
    addToColors(rainbow[count]);
    writeColorsToLEDs();
    delay(delayval);
    
    if (count == 7) {
      count = 0;
    }
    else {
      count++;
    }
  }
}

void addToColors(char c) {
  for (int i = COLORSLENGTH - 1; i > 0; i--) {
    colors[i] = colors[i - 1];
  }

  colors[0] = c;
}

void writeColorsToLEDs() {
  for (int i = 0; i < ARRAY1PIXELS; i++) {
    array1.setPixelColor(i, convertToColor(colors[i])); // Moderately bright green color.
  }

  array1.show();
}

uint32_t convertToColor(char c) {
  switch (c) {
    case 'r':
      return 0xFF0000;
    case 'o':
      return 0xFF8800;
    case 'y':
      return 0xFFFF00;
    case 'g':
      return 0x00FF00;
    case 'c':
      return 0x00FFFF;
    case 'b':
      return 0x0000FF;
    case 'v':
      return 0xAA00FF;
    case 'p':
      return 0xFF00FF;
    case 'w':
      return 0xFFFFFF;
    default:
      return 0x000000;
  }
}
*/

