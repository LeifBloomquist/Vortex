;------------------------------------------------------------------------
; Vortex II Source Code

; -------------------------------------------------------------------------
; Includes

  .include "ip65/inc/common.i"
  .include "ip65/inc/commonprint.i"
  .include "ip65/inc/net.i"
  
  .include "macros.s"
  
.ifndef KPR_API_VERSION_NUMBER
  .define EQU     =
  .include "ip65/inc/kipper_constants.i"
.endif

; -------------------------------------------------------------------------
; Imports
  
  .import  __CODE_LOAD__
  .import  __CODE_SIZE__
  .import  __RODATA_SIZE__
  .import  __DATA_SIZE__
  
	.segment "STARTUP"    ;this is what gets put at the start of the file on the C64

; -------------------------------------------------------------------------
; Load address and BASIC stub

	.word basicstub		; load address

basicstub:
	.word @nextline
	.word 2013
	.byte $9e
	.byte <(((init / 1000) .mod 10) + $30)
	.byte <(((init / 100 ) .mod 10) + $30)
	.byte <(((init / 10  ) .mod 10) + $30)
	.byte <(((init       ) .mod 10) + $30)
	.byte 0
@nextline:
	.word 0

.code

init:
  lda #$00
  sta $d020
  sta $d021

  jsr network_init_dhcp  
  jsr tftptests
  jsr network_init_udp
  jsr irq_init
 
  ; Wait for the first server packet
  kernal_print SERVERMESSAGE
  
:
  lda packetreceived
  beq :-
  
  kernal_print OKMESSAGE
  
  
  jsr screen_init
  jsr sprites_init                   
  
; -------------------------------------------------------------------------
; Main Loop - Idle.

loop: 
  jmp loop


; -------------------------------------------------------------------------
; More Includes  

  .include "joystick.s"
  .include "screen.s"
  .include "sprites.s"  
  .include "irq.s"
  .include "network.s"  

; -------------------------------------------------------------------------
; Binary data

.SEGMENT "CHARSET"
   .incbin "..\..\Server\data\chars.raw" 

.SEGMENT "SPRITES"
   .incbin "..\..\Server\data\Sprites.raw"
  
; EOF!