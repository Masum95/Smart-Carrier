avr-gcc -g -Os -mmcu=atmega32 -c $1.c
avr-gcc -g -mmcu=atmega32 -o $1.elf $1.o
avr-objcopy -j .text -j .data -O ihex $1.elf $1.hex
avr-size --format=avr --mcu=atmega32 $1.elf 
avrdude -c usbasp -p m32 -P /dev/ttyACM0 -B10 -U flash:w:$1.hex -F


