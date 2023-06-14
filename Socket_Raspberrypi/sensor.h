#ifndef __SENSOR_H__
#define __SENSOR_H__

#include <wiringPi.h>
#include <wiringPiI2C.h>
#include <softPwm.h>
#include "socket.h"

// LED, 부저, 모터 핀 번호 상수 선언
#define LED_PIN 0
#define BUZZER_PIN 2
#define MOTOR_PIN 1

// I2C 통신을 위한 주소 상수 선언
#define I2C_ADDR 0x27
#define LCD_CHR 1
#define LCD_CMD 0
#define LINE1 0x80
#define LINE2 0xC0

// LCD 백라이트 제어를 위한 비트 상수 선언
#define LCD_BACKLIGHT 0x08
#define ENABLE 0b00000100

// LED 제어 함수 선언
void led(uint8_t LED, getCommand getcmd);

// 부저 제어 함수 선언
void buzzer(uint16_t Hertz, getCommand getcmd);

// 모터 제어 함수 선언
void motor(uint16_t pwm, uint8_t pos);

// LCD 초기화 함수 선언
void lcd_init(void);

// LCD 바이트 전송 함수 선언
void lcd_byte(int bits, int mode);

// LCD 이전 명령어와 현재 명령어를 비교하여 이전 명령어와 다른 경우에만 enable 토글 함수 호출
void lcd_toggle_enable(int bits);

// 정수형 데이터를 문자열로 변환하여 LCD에 출력하는 함수 선언
void typeInt(int i);

// 실수형 데이터를 문자열로 변환하여 LCD에 출력하는 함수 선언
void typeFloat(float myFloat);

// LCD 커서 위치 설정 함수 선언
void lcdLoc(uint8_t line);

// LCD 화면 지우는 함수 선언
void ClrLcd(void);

// 문자열을 LCD에 출력하는 함수 선언
void typeln(const char *s);

// 문자를 LCD에 출력하는 함수 선언
void typeChar(char val);

#endif
