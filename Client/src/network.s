; -------------------------------------------------------------------------
; Vortex II Network Code

SEND_LENGTH   = 0017

; Packet Types - C64 to CLIENT
PACKET_ANNOUNCE       = 1
PACKET_CLIENT_UPDATE  = 2

; Packet Types - Server to C64
PACKET_ANNOUNCE_REPLY = 128
PACKET_SERVER_UPDATE  = 129


; -------------------------------------------------------------------------
; IP65 Imports
  
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
  

; -------------------------------------------------------------------------
; Network Initialization

network_init_dhcp:
  
  kernal_print NETWORKMESSAGE
  
  init_ip_via_dhcp   
  
  lda SERVER_IP+0
  sta cfg_tftp_server+0 
  lda SERVER_IP+1
  sta cfg_tftp_server+1
  lda SERVER_IP+2
  sta cfg_tftp_server+2
  lda SERVER_IP+3
  sta cfg_tftp_server+3
  
  jsr print_cr
  jsr print_ip_config
  jsr print_cr  
  rts
    
network_init_udp:
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
  
  ldax #SEND_LENGTH
  stax udp_send_len
  
  ; UDP Receives
  ldax #gotpacket
  stax udp_callback
     
  ldax #LISTEN_PORT
  jsr udp_add_listener 
  rts
  

; -------------------------------------------------------------------------
; Send the Game Update packet

sendupdate:

  ; At the start of the game we send the Announce packet instead.
  lda gamepacketreceived
  beq sendannounce

  lda #PACKET_CLIENT_UPDATE
  sta SENDBUFFER+0      
  
  ldx RATE_X
  ldy RATE_Y  
  stx SENDBUFFER+5  
  sty SENDBUFFER+6       
  
  ; Direction
  lda DIRECTION
  sta SENDBUFFER+7  
  
  ; Fire button
  lda JOYBUTTON
  sta SENDBUFFER+8
  
  lda #$00
  sta SENDBUFFER+9   ; Weapon
  sta SENDBUFFER+10  ; Powerup
  sta SENDBUFFER+11  ; Shields  
  
  ldax #SENDBUFFER  
  jsr udp_send
  rts  

sendannounce:
  lda #PACKET_ANNOUNCE
  sta SENDBUFFER+0      
  
  ; Player Ship Color
  lda playerspritecolor
  sta SENDBUFFER+1
  
  ; Player name
  lda GOTINPUT+0
  sta SENDBUFFER+2
  
  lda GOTINPUT+1
  sta SENDBUFFER+3
  
  lda GOTINPUT+2
  sta SENDBUFFER+4
  
  lda GOTINPUT+3
  sta SENDBUFFER+5
  
  lda GOTINPUT+4
  sta SENDBUFFER+6
  
  lda GOTINPUT+5
  sta SENDBUFFER+7
  
  lda GOTINPUT+6
  sta SENDBUFFER+8
  
  lda GOTINPUT+7
  sta SENDBUFFER+9
  
  lda GOTINPUT+8
  sta SENDBUFFER+10
  
  lda GOTINPUT+9
  sta SENDBUFFER+11
  
  lda GOTINPUT+10
  sta SENDBUFFER+12
  
  lda GOTINPUT+11
  sta SENDBUFFER+13
  
  lda GOTINPUT+12
  sta SENDBUFFER+14
  
  lda GOTINPUT+13
  sta SENDBUFFER+15
  
  lda GOTINPUT+14
  sta SENDBUFFER+16
  
  
  ldax #SENDBUFFER  
  jsr udp_send
  rts  


; -------------------------------------------------------------------------
; Handle Received Packets (Dispatcher)

gotpacket:
  lda udp_inp_data+0   
  cmp #PACKET_SERVER_UPDATE
  beq gameupdate
  rts


;--------------------------------------------------------------------------
; Handle sprite position updates

gameupdate:
  lda #$01
  sta gamepacketreceived  ; Flag that we got the first game packet
  
  ; update scroll
  ldx udp_inp_data+2
  ldy udp_inp_data+3
  stx finex
  sty finey
  
  lda #$00
  sta $d010  

; This first loop is for sprite parameters that count up by 1.

   ldx #$00    ; Offset into packet
   ldy #$01    ; Current sprite number  (Start at 1 because Sprite 0 is local player)       
   
updatesprites1:   
   ; x-coordinate MSB  (Cool trick from Bryce)
   lda udp_inp_data+7,x
   beq :+   ; Zero means no MSB 
   
   lda bittab,y
   ora $d010
   sta $d010

:      
   ; color
   lda udp_inp_data+12,x
   sta $d027,y
   
   ; sprite #
   lda udp_inp_data+13,x
   clc
   adc #SPRITE_BASE 
   sta SPRITE_POINTERS,y

   txa
   clc
   adc #10
   tax

   iny 
   cpy #$07
   bcc updatesprites1
   
     
   ; This second loop is for sprite parameters that count up by 2

   ldx #$00    ; Offset into packet
   ldy #$02    ; Current sprite number  (Start at 1*2 because Sprite 0 is local player)       
   
updatesprites2:
   ; x-coordinate LSB
   lda udp_inp_data+6,x
   sta $d000,y
 
   ; y-coordinate
   lda udp_inp_data+8,x
   sta $d001,y

   txa
   clc
   adc #10
   tax

   iny
   iny 
   cpy #14
   bcc updatesprites2
   
  
  lda #$01
  sta screenreceived   ; Flag for next IRQ frame
  rts

; -------------------------------------------------------------------------
; Copy screen data from UDP buffer to screen
copyscreen:
  jsr finexy

  ldax #SCREEN_BASE
  stax copy_dest  
  ldax #udp_inp_data+140
  stax copy_src 
  ldax #800 ;#1000   ; Decimal 
  jsr copymem  
  rts

; -------------------------------------------------------------------------
; Handle Received Color Update Packet

;colorpacket:  
;  ldax #udp_inp_data+1
;  stax copy_src
;  ldax #$D800
;  stax copy_dest 
;  ldax #1000   ; Decimal
;
;  BORDER #$08
;  jsr copymem  
;  rts

; -------------------------------------------------------------------------
; Network Constants and Data  
  
SERVER_IP:
  .byte 208,79,218,201    ; Vortex VPS  
  
SERVER_PORT = 3005
SRC_PORT    = 3000  ; In theory, having these match makes NAT work on some routers
LISTEN_PORT = 3000

SENDBUFFER:
  .res SEND_LENGTH
  
NETWORKMESSAGE:
  .byte 147, CG_LCS, CG_DCS, CG_LBL
  .byte "vORTEX 2 nETWORK iNITIALIZATION",13
  .byte CG_YEL, "fORWARD udp pORT 3000 TO YOUR c64", CG_LBL, 13,13
  .byte 0

SERVERMESSAGE:
  .byte "wAITING FOR SERVER..."
  .byte 0       

OKMESSAGE:
  .byte "ok",13
  .byte 0                      

FAILMESSAGE:
  .byte "...failed",13
  .byte 0                    
  
gamepacketreceived:
   .byte 0           

bittab:
   .byte $01,$02,$04,$08,$10,$20,$40,$80
