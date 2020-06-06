#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h> 
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>

#include <FirebaseArduino.h>                                                // firebase library

#define FIREBASE_HOST "smartgardener-833c2.firebaseio.com"                          // the project name address from firebase id
#define FIREBASE_AUTH "f1BiVsro8Ft3oDXKfu5XGXyssYNVd8Q2BauvthRy"            // the secret key generated from firebase

//SENSOR AREA
#include "DHT.h"        // including the library of DHT11 temperature and humidity sensor
#define DHTTYPE DHT11   // DHT 11

#define dht_dpin 0
DHT dht(dht_dpin, DHTTYPE); 
////////////////////////////////////////////

//Variable
  float sensorMoisture;
  float h;
  float t; 
  float moisture_percentage;
  int sensor_analog;
  float ET0;
  float KCET0;
  float Kc;
  float PrecipProb; 
  float G;
  float SumY;
  float tempMin;
  float tempMax;
  float Pres;
  float rh;
  float solarRad;
  float windSpd;
  float dni;

////////

/* Set these to your wifi credentials. */
const char *ssid = "Bsharma";  //ENTER YOUR WIFI SETTINGS
const char *password = "9993936448";

const char *host = "irrisat-cloud.appspot.com";
const int httpsPort = 443;  //HTTPS= 443 and HTTP = 80

//SHA1 finger print of certificate use web browser to view and copy
const char fingerprint[] PROGMEM = "e0 5c 17 d7 4a 85 68 8d d4 69 ba d3 d5 da 5c 89 cf 2d b0 29";
//=======================================================================
//                    Power on setup
//=======================================================================

void setup() {
  delay(1000);
  digitalWrite(4,HIGH);

  pinMode(4, OUTPUT);
  
  Serial.begin(115200);
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //Only Station No AP, This line hides the viewing of ESP as wifi hotspot
  
  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH); 
}

//=======================================================================
//                    Main Program Loop
//=======================================================================
void loop() {

 //=========================================IRRISTAT API============================//
  HTTPClient httpAgro;  //Declare an object of class HTTPClient
 
httpAgro.begin("http://api.weatherbit.io/v2.0/forecast/agweather?lat=23.2756&lon=77.4560&key=10fe4d3c09b64cb18e44c87dd9003578");//("http://api.weatherbit.io/v2.0/current?lat=23.2756&lon=77.4560&key=10fe4d3c09b64cb18e44c87dd9003578");  //Specify request destination
int httpAPICode = httpAgro.GET();                                                                  //Send the request
 
if (httpAPICode > 0) { //Check the returning code
 
String payload = httpAgro.getString();   //Get the request response payload

int ind = payload.indexOf('}');

///////////////////JSON PARSINNG///////////////////////////////
Serial.println("\n");
Serial.println(payload.substring(0,ind+1));                     //Print the response payload
String final =payload.substring(0,ind+1)+"]}";

 DynamicJsonBuffer jsonBuffer(907);

 char json[final.length() + 1];
 final.toCharArray(json, final.length() + 1);

  JsonObject& root = jsonBuffer.parseObject(json);

  // Test if parsing succeeds.
  if (!root.success()) {
    Serial.println("parseObject() failed");
    return;
  }
  ET0 = root["data"][0]["evapotranspiration"];
  Kc = Firebase.getFloat("/Kc");
  KCET0 = ET0*Kc;
  Firebase.setFloat("/KCET0",KCET0);
   PrecipProb = root["data"][0]["precip"];
  G = root["data"][0]["soilt_0_10cm"];  
Firebase.pushFloat("/ET0",ET0);
Firebase.pushFloat("/GSoilFlux",G);
Firebase.setFloat("/CurrentET0",ET0);
SumY = Firebase.getFloat("/SumY");
SumY = SumY +ET0;
Firebase.setFloat("/SumY",SumY);
Firebase.setFloat("/PrecipProbability",PrecipProb);
  // Print values.
  //Serial.println(ET0);
 
}
 
httpAgro.end();   //Close connection

  //====================================OpenWeatherAPI==========================================//

HTTPClient http;  //Declare an object of class HTTPClient
 
http.begin("http://api.openweathermap.org/data/2.5/weather?lat=23.2756&lon=77.4560&appid=8f9bc27b855db221dae3a2aa4880ce73");//Specify request destination
int httpCode = http.GET();                                                                  //Send the request
 
if (httpCode > 0) { //Check the returning code
 
  String openW = http.getString();   //Get the request response payload
  Serial.println(openW);
  

///////////////////JSON PARSINNG///////////////////////////////
DynamicJsonBuffer jsonBufferO(782);

 char jsonO[openW.length() + 1];
 openW.toCharArray(jsonO, openW.length() + 1);

  JsonObject& rootO = jsonBufferO.parseObject(jsonO);

  // Test if parsing succeeds.
  if (!rootO.success()) {
    Serial.println("parseObject() failed");
    return;
  }
  const char* desc = rootO["weather"][0]["description"];
  Firebase.setString("/CurrentWeather",desc);
  
  tempMin = (rootO["main"]["temp_min"]);
  tempMax = (rootO["main"]["temp_max"]);
  tempMin = tempMin - 273;
  tempMax = tempMax - 273;

  Firebase.pushFloat("/dataTempMin",tempMin);
  Firebase.pushFloat("/dataTempMax",tempMax);
  Firebase.setFloat("/DisplayTempMin",tempMin);
  Firebase.setFloat("/DisplayTempMax",tempMax); 

  float disPres = rootO["main"]["pressure"];
  float disHum = rootO["main"]["humidity"];
  Firebase.setFloat("/DisplayPressure",disPres);
  Firebase.setFloat("/DisplayHumidity",disHum);
 
}
 
http.end();   //Close connection

//======================================OPENWEATHERCLOSE_CONNECTION=======================================================//

//=======================================WEATHERBIT API===================================================================//

HTTPClient httpWeatherBit;  //Declare an object of class HTTPClient
 
httpWeatherBit.begin("http://api.weatherbit.io/v2.0/current?lat=23.2756&lon=77.4560&key=10fe4d3c09b64cb18e44c87dd9003578");  //Specify request destination
int httpBitCode = httpWeatherBit.GET();                                                                  //Send the request
 
if (httpBitCode > 0) { //Check the returning code
 
String WeatherBit = httpWeatherBit.getString();   //Get the request response payload
Serial.println(WeatherBit);

///////////////////JSON PARSINNG///////////////////////////////
DynamicJsonBuffer jsonBufferW(782);

 char jsonW[WeatherBit.length() + 1];
 WeatherBit.toCharArray(jsonW, WeatherBit.length() + 1);

  JsonObject& rootW = jsonBufferW.parseObject(jsonW);

  // Test if parsing succeeds.
  if (!rootW.success()) {
    Serial.println("parseObject() failed");
    return;
  }
  Pres = rootW["data"][0]["pres"];
  rh = rootW["data"][0]["rh"];
  solarRad = rootW["data"][0]["solar_rad"];
  windSpd = rootW["data"][0]["wind_spd"];
  dni = rootW["data"][0]["dni"];


  Firebase.pushFloat("/PressureCalc",Pres);
  Firebase.pushFloat("/RelativeHumidity",rh);
  Firebase.pushFloat("/SolarRad",solarRad);
  Firebase.pushFloat("/WindSpeed",windSpd);
  Firebase.setFloat("/DisplayWindSpeed",windSpd);
  Firebase.pushFloat("/DirectNSI",dni);

 
}
 
httpWeatherBit.end();   //Close connection

//====================PLSR ALGO===================================//

float tempMean = (tempMin+tempMax)/2;
float p = (17.27*tempMean)/(tempMean+273.3);
float del = (4098*( 0.6108 * pow(2.71828,p)))/pow((tempMean + 237.3),2);
float gama = 0.000665*Pres;
float Rn = solarRad;
float u2 = windSpd;
float p1 = (17.27*tempMax)/(tempMax+273.3);
float p2 = (17.27*tempMin)/(tempMin+273.3);
float eTmax = 0.6108*(pow(2.71828,p1));
float eTmin = 0.6108*(pow(2.71828,p2));
float es = (eTmax + eTmin)/2;
float ea = (eTmin)*(rh/100);

float X1 = (del*(Rn-G))/(del + (gama*(1+ 0.34*u2)));
float X2 = (gama*u2*(es-ea))/((tempMean+273)*(del + (gama*(1+ 0.34*u2))));
Firebase.pushFloat("/X1",X1);
Firebase.pushFloat("/X2",X2);
float Y = ET0;

float X1Y = X1*Y;
float X2square = X2*X2;
float X1X2 = X1*X2;
float X2Y = X2*Y;
float X1square = X1*X1;


float SumX1 = Firebase.getFloat("/SumX1");
SumX1 = SumX1+X1;
float SumX2 = Firebase.getFloat("/SumX2");
SumX2 = SumX2+X2;
float SumX1Y = Firebase.getFloat("/SumX1Y");
SumX1Y=SumX1Y+X1Y;
float SumX2square =  Firebase.getFloat("/SumX2square");
SumX2square=SumX2square+X2square;
float SumX1X2 =  Firebase.getFloat("/SumX1X2");
SumX1X2=SumX1X2+X1X2;
float SumX2Y =  Firebase.getFloat("/SumX2Y");
SumX2Y=SumX2Y+X2Y;
float SumX1square =  Firebase.getFloat("/SumX1square");
SumX1square=SumX1square+X1square;

Firebase.setFloat("/SumX1",SumX1);
Firebase.setFloat("/SumX2",SumX2);
Firebase.setFloat("/SumX1Y",SumX1Y);
Firebase.setFloat("/SumX2square",SumX2square);
Firebase.setFloat("/SumX1X2",SumX1X2);
Firebase.setFloat("/SumX2Y",SumX2Y);
Firebase.setFloat("/SumX1square",SumX1square);

int c = Firebase.getInt("/count");

float b1 = ((SumX2square*SumX1Y)-(SumX1X2*SumX2Y))/((SumX1square*SumX2square)-pow(SumX1X2,2));
float b2 = ((SumX1square*SumX2Y)-(SumX1X2*SumX1Y))/((SumX1square*SumX2square)-pow(SumX1X2,2));
float b0 = (SumY/c) - (b1*(SumX1/c)) - (b2*(SumX2/c));

float YFinal = b0 +(b1*X1)+(b2*X2);
float ET0F = (((0.408*X1)+(900*X2))*2)/10;

Firebase.pushFloat("/ET0F",ET0F);
Firebase.pushFloat("/b1",b1);
Firebase.pushFloat("/b2",b2);
Firebase.pushFloat("/b0",b0);

//===================================SENSOR VALUES TO CLOUD===================================//


  sensor_analog = analogRead(A0);
  moisture_percentage = ( 100 - ( ((sensor_analog-300)/724.00) * 100 ) );
  Serial.print("Moisture Percentage = ");
  Serial.print(moisture_percentage);
  Serial.print("%\n\n");

  h = dht.readHumidity();
  t = dht.readTemperature();         

  //Firebase.setFloat("/SensorTemperature",t);
  //Firebase.setFloat("/SensorHumidity",h);
 // Firebase.setFloat("/SensorMoisture",sensor_analog);
  sensorMoisture= ((float)(moisture_percentage)/10);
  delay(5000);

 if (sensorMoisture<6 && sensorMoisture<KCET0 && PrecipProb<KCET0){
    Serial.println("\MotorON");
    digitalWrite(4,LOW);
    delay(2000);
  }
  while(sensorMoisture<7 && sensorMoisture<KCET0 && PrecipProb<KCET0){
  sensor_analog = analogRead(A0);
  moisture_percentage = ( 100 - ( ((sensor_analog-300)/724.00) * 100 ) );
  sensorMoisture= ((float)(moisture_percentage)/10);   
    Serial.println("Updated Moisture:");
    Serial.println(sensorMoisture);
    delay(5000); 
  }
  digitalWrite(4,HIGH);
  Serial.println("\MOTOR OFF");

//============================================================================================//

Firebase.setInt("/count",c+1);
    
  delay(1800000); 
}
//=======================================================================
