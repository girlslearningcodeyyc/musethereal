// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

// Set up first array of LEDs
#define ARRAY1PIN         6
//13 channels - 13 dress LEDs 
//3 bytes per channel - one for R, one for G, one for B
#define DRESSLEDARRAYLENGTH 13
#define RED     0xFF0000
#define ORANGE  0xFF7000
#define YELLOW  0xFFFF00
#define GREEN   0x00FF00
#define CYAN    0x00FFFF
#define BLUE    0x0000FF
#define VIOLET  0x7000FF
#define PINK    0xFF00FF
#define WHITE   0xFFFFFF
uint32_t currentColors [DRESSLEDARRAYLENGTH];
uint32_t targetColors [DRESSLEDARRAYLENGTH];
int stepMax = 30;
int stepTime = 50;
bool readyToRead = true;
Adafruit_NeoPixel array1 = Adafruit_NeoPixel(DRESSLEDARRAYLENGTH, ARRAY1PIN, NEO_GRB + NEO_KHZ800);

void setup() {
  Serial.begin(9600);
  array1.begin();

  for(int i = 0; i < DRESSLEDARRAYLENGTH; i++){
      currentColors[i] = 0x000000;
  }
}

void loop(){
  if(Serial.available() && Serial.peek() != -1 && readyToRead) {
      //We're processing, everything hold up
      readyToRead = false;
      
      //Read out data into target color array
      for(int i=0;i<DRESSLEDARRAYLENGTH;i++){
        char c = Serial.read();
        if (c == -1) {
          c = 'n';
        }
        targetColors[i] = convertToColor(c);
      }

      //Transition currentColors to targetColors
      uint32_t tempArray [DRESSLEDARRAYLENGTH];
      for(int i=1;i<stepMax+1;i++){
        for(int j=0;j<DRESSLEDARRAYLENGTH;j++){
          tempArray[j] = calculateTransitionColor(currentColors[j], targetColors[j], i, stepMax);
        }

        //Then show it briefly
        writeToArray(tempArray);
        delay(stepTime);
      }
      
      //Then assign the current array to what the target was (cause we are already there...right?)
      for(int i = 0; i < DRESSLEDARRAYLENGTH; i++){
        currentColors[i] = tempArray[i];
      }
      writeToArray(currentColors);

      //Ready to read again
      readyToRead = true;
      Serial.write('1');
  }
}

uint32_t calculateTransitionColor(uint32_t currentColor, uint32_t targetColor, int stepNum, int stepMax) {
  //Break out each RGB current and target value
  uint8_t rcur = (currentColor & 0xFF0000) >> 16;
  uint8_t rtar = (targetColor & 0xFF0000) >> 16;
  uint8_t gcur = (currentColor & 0x00FF00) >> 8;
  uint8_t gtar = (targetColor & 0x00FF00) >> 8;
  uint8_t bcur = (currentColor & 0x0000FF);
  uint8_t btar = (targetColor & 0x0000FF);
  
  //printf("rcur %#08x\n", rcur);
  //printf("rtar %#08x\n", rtar);
  //printf("gcur %#08x\n", gcur);
  //printf("gtar %#08x\n", gtar);
  //printf("bcur %#08x\n", bcur);
  //printf("btar %#08x\n", btar);
  
  int rstep = (rtar - rcur) / stepMax;
  int gstep = (gtar - gcur) / stepMax;
  int bstep = (btar - bcur) / stepMax;
  
  //printf("rstep %i\n", rstep);
  //printf("gstep %i\n", gstep);
  //printf("bstep %i\n", bstep);
  
  uint32_t rnow = rcur + rstep * stepNum;
  uint32_t gnow = gcur + gstep * stepNum;
  uint32_t bnow = bcur + bstep * stepNum;
  
  //printf("rnow %i\n", rnow);
  //printf("gnow %i\n", gnow);
  //printf("bnow %i\n", bnow);
  
  uint32_t calc = 0x000000 + (rnow << 16) + (gnow << 8) + bnow;
  return calc;
}

void writeToArray(uint32_t arr[]) { 
  for (int i = 0; i < DRESSLEDARRAYLENGTH; i++) {
    array1.setPixelColor(i, arr[i]); 
  }

  array1.show();
}

uint32_t convertToColor(char c) {
  switch (c) {
    case 'r': return RED;
    case 'o': return ORANGE;
    case 'y': return YELLOW;
    case 'g': return GREEN;
    case 'c': return CYAN;
    case 'b': return BLUE;
    case 'v': return VIOLET;
    case 'p': return PINK;
    case 'w': return WHITE;
    default:  return 0x000000;
  }
}

/*
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


*/

