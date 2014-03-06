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
  
	.segment "CLIENTCODE"    ; Program start

; -------------------------------------------------------------------------
; Program entry point

init:
  lda #$02
  sta $d020
  lda #$00
  sta $d021

  jsr network_init_dhcp
  jsr network_init_udp  
  jsr irq_init   ; Needed for network
 
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
; Binary data is now loaded from server via TFTP
  
; EOF!