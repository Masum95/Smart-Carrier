/*
 * main.c
 *
 *  Created on: May 27, 2018
 *      Author: masum
 */

#define F_CPU  1000000L
#include <avr/io.h>
#include <stdio.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#define USART_BaudRate 9600
#define BAUD_PRESCALE  (F_CPU / (USART_BaudRate *16L) -1)
#define UART_RxCHAR() { while((UCSRA & 1<<RXC)==0); }
#define UART_TxCHAR(ch) { while(!(UCSRA & 1<<UDRE)); UDR=ch; }
#define ffr(i,a,b) for(i=a;i<b;i++)
#define MXLIM 300
#define uchar unsigned char

int dataReceived = 0;
uchar degrees[MXLIM], times[MXLIM];
int deg_id = 0;
uchar degrees_to_rotate;
uchar received;
//let, leftwheel = motor 2, rightwheel = motor 1

#define mot1back 3 //37
#define mot1front 4 //36
#define mot2front 2 //38 //dark
#define mot2back 1 //39
#define carwidth 10
#define wheelrad 10
#define RPM 10
#define PI 3.141592
#define motor1ahead() {PORTA|=(1<<mot1front); PORTA&=(~(1<<mot1back));}
#define motor2ahead() {PORTA|=(1<<mot2front); PORTA&=(~(1<<mot2back));}
#define motor1back() {PORTA|=(1<<mot1back); PORTA&=(~(1<<mot1front));}
#define motor2back() {PORTA|=(1<<mot2back); PORTA&=(~(1<<mot2front));}
#define motor1stop() {PORTA&=(~(1<<mot1back)); PORTA&=(~(1<<mot1front));}
#define motor2stop() {PORTA&=(~(1<<mot2back)); PORTA&=(~(1<<mot2front));}
#define wheelstop(motorNum) {if(motorNum==1) motor1stop(); else if(motorNum==2) motor2stop();}
#define delay_by_8_ms(ms) { while(ms>0){_delay_ms(8); ms-=8;} }
#define carWidth 12 // in cm

#define straightAngleTime 1550 //in ms
#define rotateExpectByOriginal 10.0 //180.0/315.0
#define thirtyUnitTime 2000 //in ms
#define timePerMoves 10.0

#define carStop() {motor1stop(); motor2stop();}

int time2;
int car_is_stopped, input_fed;

void carRotateCW(uchar degree)
{
    //dan 2 bondho; bam,1 samne
    motor2stop();
    motor1ahead();

    time2=degree*PI*carWidth/9.0;
    delay_by_8_ms(time2);
    //carStop();
}

void carRotateCCW(uchar degree)
{
    //bam,1 bondho; dan,2 samne
    motor1stop();
    motor2ahead();

    //1 deg rotate = pi wheelrad / 9
    time2=degree*PI*carWidth/4.4;

    delay_by_8_ms(time2);
    //carStop();
}

void carAhead(uchar moves)
{
	//works
	double temprotate=moves/12.5;
	    carRotateCCW((int)temprotate);
    motor1ahead();
    motor2ahead();

    time2=moves*timePerMoves;
    delay_by_8_ms(time2); //delay 2000, move distance 100 cm, 200 unit

    temprotate*=3.0/4.0;
        	    carRotateCCW((int)temprotate);

    //carStop();
}

void carBack(uchar moves)
{
	//work
	//never to be used
    motor1back();
    motor2back();
}


void turncommand(uchar cc) //aaaa aaad
{
    if(cc&1) //1 means CCW
    {
        degrees_to_rotate=cc>>1;
        carRotateCCW(degrees_to_rotate);
    }
    else
    {
    	degrees_to_rotate=cc>>1;
        carRotateCW(degrees_to_rotate);
    }
}


void input_processing()
{
	int temp=deg_id, x;
    while(1)
    {
    	if(deg_id==80) break;

        while((UCSRA & 1<<RXC)==0);
        times[deg_id] = UDR;

        while((UCSRA & 1<<RXC)==0);
        degrees[deg_id] = UDR;

        if(times[deg_id]==0xFF && degrees[deg_id]==0xFF) {
        	break;
        }
        deg_id++;
    }

    return;
}

void start()
{
	int i;
	for(i=0;i<deg_id;i++)
	{
		turncommand(degrees[i]);

		carAhead(times[i]);
	}
}


void UART_Init()
{
	UCSRB |= (1<<RXEN);
	UCSRC |= (1<<URSEL) | (1<<UCSZ0) | (1<<UCSZ1);  // 8 BIT CHARACTER
	UBRRL = BAUD_PRESCALE;
	UBRRH = (BAUD_PRESCALE >> 8) ;

}


int main()
{
		DDRA = 0xFF;
		DDRB=0xFF; // ensure that Pin3 in POrt B is output as this is the OC0 pin that wll produce the PWM.

		TCCR0=0b01110010; //Configure TCCR0 as explained in the article

		TIMSK=0b00000000;

		OCR0=178; // Set OCR0 to 255 so that the duty cycle is initially 0 and the motor is not rotating

		UART_Init();

		input_processing();
		start();
		carStop();
	    while(1);

}

