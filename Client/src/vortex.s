; -------------------------------------------------------------------------
; 



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
  
  .import udp_init
  .import udp_send
    .import udp_send_dest
    .import udp_send_dest_port
    .import udp_send_src_port
    .import udp_send_len
  
  .import udp_add_listener 
    .import udp_callback
    
  .import udp_inp
  .importzp udp_data
  
  udp_inp_data = udp_inp + udp_data
  
  .import copymem
    .importzp copy_src
    .importzp copy_dest  
  
  
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
	.word 2003
	.byte $9e
	.byte <(((init / 1000) .mod 10) + $30)
	.byte <(((init / 100 ) .mod 10) + $30)
	.byte <(((init / 10  ) .mod 10) + $30)
	.byte <(((init       ) .mod 10) + $30)
	.byte 0
@nextline:
	.word 0

.code

; -------------------------------------------------------------------------
; Network Initialization

init:
  lda #CG_DCS
  jsr $FFD2
  
  lda #CG_LCS
  jsr $FFD2

  jsr print_cr
  init_ip_via_dhcp 
  jsr print_ip_config
  jsr print_cr
  
  jsr udp_init
  
  ; UDP Sends 
  lda SERVER_IP+0
  sta udp_send_dest+0 
  lda SERVER_IP+1
  sta udp_send_dest+1
  lda SERVER_IP+2
  sta udp_send_dest+2
  lda SERVER_IP+3
  sta udp_send_dest+3
  
  ldax #SERVER_PORT
  stax udp_send_dest_port
  
  ldax #SRC_PORT
  stax udp_send_src_port
  
  lda #16 ; Decimal
  sta udp_send_len+0
  lda #0
  sta udp_send_len+1
  
  ; UDP Receives
  ldax #gotpacket
  stax udp_callback
     
  ldax #LISTEN_PORT
  jsr udp_add_listener


; -------------------------------------------------------------------------
; Wait 
    
  ; Wait for key
  kernal_print WAITMESSAGE
    
gak0:
	jsr $FFE4  ;GETIN
	beq gak0
  
  lda #CG_UCS
  jsr $FFD2

; -------------------------------------------------------------------------
; Screen Initialization

  lda #$00
  sta $d020
  sta $d021
  
  ; Blank screen
  lda #32
  ldx #$00     
:
  sta $8000,x
  sta $8100,x
  sta $8200,x
  sta $82e8,x 
  inx
  bne :-	
  
  
; Explicitly paint screen color, for older kernals
  lda #$0F
  ldx #$00  
SCREENCOLOR:
  sta $d800,x
  sta $d900,x
  sta $da00,x
  sta $dae8,x 
  inx
  bne SCREENCOLOR	
  
  ; Assume for now, don't have to bank out BASIC since VIC won't see it
  
  ; Set up VIC to Bank 2  ($8000-$BFFF)
  lda $DD00
  and #$FC
  ora #$01
  sta $DD00

  ; Set screen to $8000 
  lda #$05
  sta $d018            

; -------------------------------------------------------------------------
; IRQ Initialization

  jsr setupirq

; -------------------------------------------------------------------------
; Main Loop

loop: 
  jmp loop

; -------------------------------------------------------------------------
; Send the update packet

sendupdate:
  ldx RATE_X
  ldy RATE_Y
  stx TESTMESSAGE  
  sty TESTMESSAGE+1  
  
  ldax #TESTMESSAGE
  jsr udp_send
  rts  


; -------------------------------------------------------------------------
; Handle Received Packets (Dispatcher)

gotpacket:
  lda udp_inp_data+0

  cmp #PACKET_SCREEN     ; Screen Update
  beq screenpacket
  
  cmp #PACKET_COLOR      ; Color Data
  beq colorpacket
  
  ; Unknown, ignore.
  rts

; -------------------------------------------------------------------------
; Handle Received Screen Update Packet

screenpacket:  
  lda udp_inp_data+1
  sta finex
  lda udp_inp_data+2
  sta finey  
  lda udp_inp_data+3
  sta whichscreen
  
  lda #$01
  sta screenreceived   ; Flag for next IRQ frame
  rts

; -------------------------------------------------------------------------
; Copy screen data from UDP buffer to screen

copyscreen:
  BORDER #$07

  jsr finexy

  ldax #$8000
  stax copy_dest  
  ldax #udp_inp_data+4
  stax copy_src 
  ldax #800 ;#1000   ; Decimal 
  jsr copymem  


  rts

finex:
  .byte $00

finey:
  .byte $00  

; -------------------------------------------------------------------------
; Handle Received Color Update Packet

colorpacket:  
  ldax #udp_inp_data+1
  stax copy_src
  ldax #$D800
  stax copy_dest 
  ldax #1000   ; Decimal

  BORDER #$08
  jsr copymem  
  rts


; -------------------------------------------------------------------------
; Main IRQ Code

irqcode:
  lda $d019   ; Clear irq
  sta $d019

  ; IRQ code starts here
  BORDER #$02      
  jsr READJOYSTICK

  inc frametype  
  lda frametype
  
  cmp #$01
  beq irq_update
  
  cmp #$02
  beq irq_process
  
  cmp #$03
  beq irq_screen

 irq_reset:
  lda #$00
  sta frametype
  
irq_x:
  BORDER #$00
  ;jmp $ea31                     ; Exit to ROM.  Alternately, use below if we don't need ROM routines.
  pla                           ; we exit interrupt entirely.
  tay                           
  pla                           
  tax                          
  pla                        
  rti

; -------------------------------------------------------------------------
; Routines called within the IRQ.

irq_update:
  BORDER #$03
  jsr sendupdate
  jmp irq_x

irq_process:
  BORDER #$04
  jsr ip65_process
  jmp irq_x
   
irq_screen:
  BORDER #$05
  lda screenreceived
  beq :+

  jsr copyscreen
  lda #$00
  sta screenreceived
  
:  
  jmp irq_reset    ; For last frame type only

  
frametype:
  .byte $00     ; Used to round-robin through frame types

screenreceived:
  .byte $00     ; Set to 1 when a full screen is received, so irq can process.
  
whichscreen:
  .byte $00     ; 0=Screen at $8000   1=Screen at $8400
                ; This controls which screen is *visible* for doublebuffering.

; -------------------------------------------------------------------------
; IRQ Setup

setupirq:
  sei                           ; disable interrupts

  lda #$7f                      ; turn off the cia interrupts
  sta $dc0d

  lda $d01a                     ; enable raster irq
  ora #$01
  sta $d01a

  lda $d011                     ; clear high bit of raster line
  and #$7f
  sta $d011

  lda #230                      ; line number to go off at
  sta $d012                     ; low byte of raster line

  ldax #irqcode                 ; get address of target routine
  stax 788                      ; put into interrupt vector

  cli                           ; re-enable interrupts
  rts                           ; return to caller


; -------------------------------------------------------------------------
; Set Fine X and Fine Y

finexy:
 ; Fine X
  lda $d016
  and #%11110000
  ora finex
  sta $d016

  ; Fine Y
  lda $d011
  and #%11110000
  ora finey
  sta $d011
  
  rts
  
; -------------------------------------------------------------------------
; More Includes  

  .include "joystick.s"

      
; -------------------------------------------------------------------------
; Constants  
  
SERVER_IP:
; .byte 142,65,71,131     ; MOS Develop 2
  .byte 208,79,218,201    ; Vortex VPS  
  
SERVER_PORT = 3005
SRC_PORT    = 6464
LISTEN_PORT = 3000

TESTMESSAGE:
  .byte "Hello everyone."
  .byte 0
  
WAITMESSAGE:
  .byte "press any key to continue."
  .byte 0

BORDERMASK:
  .byte $FF

; Packet Types
PACKET_SCREEN = 100
PACKET_COLOR  = 101

; -------------------------------------------------------------------------
; Graphics Constants - move to a separate file  

  ;c64 c/g 
CG_BLK = 144
CG_WHT = 5
CG_RED = 28
CG_CYN = 159
CG_PUR = 156
CG_GRN = 30
CG_BLU = 31
CG_YEL = 158
CG_BRN = 149
CG_ORG = 129
CG_PNK = 150
CG_GR1 = 151
CG_GR2 = 152
CG_LGN = 153
CG_LBL = 154
CG_GR3 = 155
CG_RVS = 18 ;revs-on
CG_NRM = 146 ;revs-off

CG_DCS = 8  ;disable shift+C=
CG_ECS = 9  ;enable shift+C=

CG_LCS = 14 ;switch to lowercase
CG_UCS = 142 ;switch to uppercase
