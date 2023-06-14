#include "sensor.h"

// LED를 제어하는 함수
void led(uint8_t LED, getCommand getcmd) { digitalWrite(LED_PIN, LED); }

// 부저를 제어하는 함수
void buzzer(uint16_t Hertz, getCommand getcmd) { softToneWrite(BUZZER_PIN, Hertz); }

// 모터를 제어하는 함수
void motor(uint16_t pwm, uint8_t pos)
{
	softPwmCreate(MOTOR_PIN, 0, pwm); // 모터 핀을 설정하고 PWM 신호를 생성한다.
	softPwmWrite(MOTOR_PIN, pos);	  // 모터 핀에 PWM 신호를 출력한다.
}

// LCD 초기화 함수
void lcd_init()
{
	lcd_byte(0x33, LCD_CMD); // LCD 초기화 명령어를 전송한다.
	lcd_byte(0x32, LCD_CMD); // LCD 초기화 명령어를 전송한다.
	lcd_byte(0x06, LCD_CMD); // 커서 이동 방식을 설정한다.
	lcd_byte(0x0C, LCD_CMD); // 화면 표시 방식을 설정한다.
	lcd_byte(0x28, LCD_CMD); // 데이터 길이와 표시 줄 수를 설정한다.
	lcd_byte(0x01, LCD_CMD); // 화면을 지운다.
	delayMicroseconds(500);	 // 500us 지연시간을 가진다.
}

// 실수형 데이터를 출력하는 함수
void typeFloat(float myFloat)
{
	char buffer[20];				   // 출력할 데이터를 저장할 버퍼를 생성한다.
	sprintf(buffer, "%4.2f", myFloat); // 실수형 데이터를 문자열로 변환한다.
	typeln(buffer);					   // 문자열을 출력한다.
}

// 정수형 데이터를 출력하는 함수
void typeInt(int i)
{
	char array1[20];		  // 출력할 데이터를 저장할 버퍼를 생성한다.
	sprintf(array1, "%d", i); // 정수형 데이터를 문자열로 변환한다.
	typeln(array1);			  // 문자열을 출력한다.
}

// LCD 화면을 지우는 함수
void ClrLcd(void)
{
	lcd_byte(0x01, LCD_CMD); // 화면을 지우는 명령어를 전송한다.
	lcd_byte(0x02, LCD_CMD); // 커서를 홈 위치로 이동시키는 명령어를 전송한다.
}

// LCD 커서 위치를 설정하는 함수
void lcdLoc(uint8_t line) { lcd_byte(line, LCD_CMD); }

// 문자를 출력하는 함수
void typeChar(char val) { lcd_byte(val, LCD_CHR); }

// 문자열을 출력하는 함수
void typeln(const char *s)
{
	while (*s)
		lcd_byte(*(s++), LCD_CHR);
}

// LCD에 명령어나 데이터를 전송하는 함수
void lcd_byte(int bits, int mode)
{
	int bits_high;
	int bits_low;

	bits_high = mode | (bits & 0xF0) | LCD_BACKLIGHT;		// 상위 4비트를 설정한다.
	bits_low = mode | ((bits << 4) & 0xF0) | LCD_BACKLIGHT; // 하위 4비트를 설정한다.

	wiringPiI2CReadReg8(wiringPiI2CSetup(I2C_ADDR), bits_high); // 상위 4비트를 전송한다.
	lcd_toggle_enable(bits_high);								// Enable 신호를 토글한다.

	wiringPiI2CReadReg8(wiringPiI2CSetup(I2C_ADDR), bits_low); // 하위 4비트를 전송한다.
	lcd_toggle_enable(bits_low);							   // Enable 신호를 토글한다.
}

// Enable 신호를 토글하는 함수
void lcd_toggle_enable(int bits)
{
	delayMicroseconds(500);											   // 500us 지연시간을 가진다.
	wiringPiI2CReadReg8(wiringPiI2CSetup(I2C_ADDR), (bits | ENABLE));  // Enable 신호를 설정한다.
	delayMicroseconds(500);											   // 500us 지연시간을 가진다.
	wiringPiI2CReadReg8(wiringPiI2CSetup(I2C_ADDR), (bits & ~ENABLE)); // Enable 신호를 해제한다.
	delayMicroseconds(500);											   // 500us 지연시간을 가진다.
}
