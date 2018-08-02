################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(S_DEPS)),)
-include $(S_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
LSS += \
Serial_Communication.lss \

FLASH_IMAGE += \
Serial_Communication.hex \

EEPROM_IMAGE += \
Serial_Communication.eep \

SIZEDUMMY += \
sizedummy \


# All Target
all: Serial_Communication.elf secondary-outputs

# Tool invocations
Serial_Communication.elf: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: AVR C Linker'
	avr-gcc -Wl,-Map,Serial_Communication.map -mmcu=atmega32 -o "Serial_Communication.elf" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

Serial_Communication.lss: Serial_Communication.elf
	@echo 'Invoking: AVR Create Extended Listing'
	-avr-objdump -h -S Serial_Communication.elf  >"Serial_Communication.lss"
	@echo 'Finished building: $@'
	@echo ' '

Serial_Communication.hex: Serial_Communication.elf
	@echo 'Create Flash image (ihex format)'
	-avr-objcopy -R .eeprom -R .fuse -R .lock -R .signature -O ihex Serial_Communication.elf  "Serial_Communication.hex"
	@echo 'Finished building: $@'
	@echo ' '

Serial_Communication.eep: Serial_Communication.elf
	@echo 'Create eeprom image (ihex format)'
	-avr-objcopy -j .eeprom --no-change-warnings --change-section-lma .eeprom=0 -O ihex Serial_Communication.elf  "Serial_Communication.eep"
	@echo 'Finished building: $@'
	@echo ' '

sizedummy: Serial_Communication.elf
	@echo 'Invoking: Print Size'
	-avr-size --format=avr --mcu=atmega32 Serial_Communication.elf
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(FLASH_IMAGE)$(ELFS)$(OBJS)$(ASM_DEPS)$(EEPROM_IMAGE)$(S_DEPS)$(SIZEDUMMY)$(S_UPPER_DEPS)$(LSS)$(C_DEPS) Serial_Communication.elf
	-@echo ' '

secondary-outputs: $(LSS) $(FLASH_IMAGE) $(EEPROM_IMAGE) $(SIZEDUMMY)

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
