// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

// Set up first array of LEDs
#define ARRAY1PIXELS      20
#define ARRAY1PIN         6
Adafruit_NeoPixel array1 = Adafruit_NeoPixel(ARRAY1PIXELS, ARRAY1PIN, NEO_GRB + NEO_KHZ800);

#define COLORSLENGTH  20
char colors [COLORSLENGTH];

int delayval = 50; // delay for half a second

void setup() {
  Serial.begin(9600);
  array1.begin();

  for (int i = 0; i < COLORSLENGTH; i++) {
    colors[i] = 'n';
  }

  writeColorsToLEDs();
}

void loop() {
  char c;

  if (Serial.available()) {
    c = Serial.read();

    if (c != 0) {
      addToColors(c);
      writeColorsToLEDs();
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


