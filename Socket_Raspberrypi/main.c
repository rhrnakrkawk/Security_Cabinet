#include "main.h"

int main(int args, char** argv)
{   
    getCommand getcmd = {0, };
    connection(&getcmd);
    init_sensor();

    uint8_t led_change = 0x00;
    while(TRUE)
    {
        if(safety(&getcmd) && strcmp(getcmd.command, "LED_ON") == 0 )
        {
            //led_change = ~led_change;
	    led_change = 1;
            led(led_change, getcmd);
            led_change ? response("LED ON!", &getcmd) : response("LED OFF!", &getcmd);
            led_change ? printf("LED ON!\n") : printf("LED OFF!\n");
        }
	else if(safety(&getcmd) && strcmp(getcmd.command, "LED_OFF") == 0 )
	{
	    led_change = 0;
            led(led_change, getcmd);
            led_change ? response("LED ON!", &getcmd) : response("LED OFF!", &getcmd);
            led_change ? printf("LED ON!\n") : printf("LED OFF!\n");
	}
    }
}

void init_sensor()
{
    if(wiringPiSetup() == -1)
	    return -1;
    pinMode(LED_PIN, OUTPUT);
    softToneCreate(BUZZER_PIN);
    lcd_init();
    
    pinMode(MOTOR_PIN, OUTPUT); 
    digitalWrite(MOTOR_PIN, LOW);
}

