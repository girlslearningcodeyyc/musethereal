// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

// Set up first array of LEDs
#define ARRAY1PIXELS      4
#define ARRAY1PIN         6
Adafruit_NeoPixel array1 = Adafruit_NeoPixel(ARRAY1PIXELS, ARRAY1PIN, NEO_GRB + NEO_KHZ800);

// Set up second array of LEDs
#define ARRAY2PIXELS      22
#define ARRAY2PIN         10
Adafruit_NeoPixel array2 = Adafruit_NeoPixel(ARRAY2PIXELS, ARRAY2PIN, NEO_GRB + NEO_KHZ800);

#define COLORSLENGTH  22
char colors [COLORSLENGTH];

int delayval = 50; // delay for half a second

void setup() {    
  Serial.begin(9600);
  array1.begin(); 
  array2.begin();

  for(int i=0;i<COLORSLENGTH;i++){
    colors[i] = 'n';
  }

  writeColorsToLEDs();
}

void loop() {
  char c;
  
  if(Serial.available()) {  
    c = Serial.read();  

    if (c != NULL) {
      addToColors(c);
      writeColorsToLEDs();
    }
  } 
}

void addToColors(char c){
  for (int i=COLORSLENGTH-1;i>0;i--) {
    colors[i] = colors[i-1];
  }

  colors[0] = c;
}

void writeColorsToLEDs(){
  for(int i=0;i<ARRAY1PIXELS;i++) {
    array1.setPixelColor(i, convertToColor(colors[i])); // Moderately bright green color.
  }

  for(int i=0;i<ARRAY2PIXELS;i++) {
    array2.setPixelColor(i, convertToColor(colors[i])); // Moderately bright green color.
  }

  array1.show(); 
  array2.show(); 
}

uint32_t convertToColor(char c) {
  switch(c){
    case 'r':
      return 0xFF0000;
    case 'o':
      return 0x992200;
    case 'y':
      return 0x999900;
    case 'g':
      return 0x009900;
    case 'c':
      return 0x009999;  
    case 'b':
      return 0x000099;
    case 'v':
      return 0x660099;
    case 'p':
      return 0x990099;  
    default:
      return 0x000000;
  }
}


