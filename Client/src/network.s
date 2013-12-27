; -------------------------------------------------------------------------
; Vortex II Network Code

SEND_LENGTH   = 0016

; Packet Types - C64 to CLIENT
PACKET_ANNOUNCE = 1
PACKET_CLIENT_UPDATE = 2

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
  
  .import tftp_download
    .import tftp_load_address
    .importzp tftp_filename
    .import tftp_ip 
    
  .import tftp_clear_callbacks 
  
  .import copymem
    .importzp copy_src
    .importzp copy_dest  
  

; -------------------------------------------------------------------------
; Network Initialization

network_init_dhcp:
  
  kernal_print NETWORKMESSAGE
  
  init_ip_via_dhcp   
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
; Send the update packet

sendupdate:
  lda #PACKET_CLIENT_UPDATE
  sta SENDBUFFER+0
  
  
  ldx RATE_X
  ldy RATE_Y  
  stx SENDBUFFER+5  
  sty SENDBUFFER+6       
  
  ; Direction - Tricky!
  lda #$FF ; TODO
  sta SENDBUFFER+7  
  
  lda JOYBUTTON
  sta SENDBUFFER+8
  
  lda #$00
  sta SENDBUFFER+9   ; Weapon
  sta SENDBUFFER+10  ; Powerup
  sta SENDBUFFER+11  ; Shields  
  
  ldax #SENDBUFFER  
  jsr udp_send
  rts  


; -------------------------------------------------------------------------
; Handle Received Packets (Dispatcher)

gotpacket:
  inc packetreceived  ; Flag that we got the first packet (this is ignored afterwards)
  
  lda udp_inp_data+0

  cmp #140
  beq gameupdate
  rts


;--------------------------------------------------------------------------
; Handle sprite position updates
;In:
; .A: the sprite to update the position of
;
gameupdate:
;   ldy #$00
;   ldx #$00
;updatesprites:
;   ; x-coordinate
;   lda udp_inp_data+1,x
;   sta $d000,x
;   lda udp_inp_data+2,x
;   lda bittab,y
;   ora $d010
;   sta $d010
   ; y-coordinate
;   lda udp_inp_data+2,x
;   sta $d001,x
   ; color
;   lda udp_inp_data+4,x
;   sta $d025,y
;   ; sprite #
;   lda udp_inp_data+5
;   sta SCREEN_BASE+$3f8,y

;   txa
;   clc
;   adc #10
;   tax

;   iny 
;   cpy #$07
;   bcc updatesprites

; update scroll
;  lda udp_inp_data+2
;  ora $d016
;  sta $d016
;  lda udp_inp_data+3
;  ora $d011
;  sta $d011

; -------------------------------------------------------------------------
; Handle Received Screen Update Packet
screenpacket:  
;  lda udp_inp_data+1
;  sta finex
;  lda udp_inp_data+2
;  sta finey  
;  lda udp_inp_data+3
  ;sta whichscreen
  
  lda #$01
  sta screenreceived   ; Flag for next IRQ frame
  rts

; -------------------------------------------------------------------------
; Copy screen data from UDP buffer to screen
copyscreen:
  ;jsr finexy

  ldax #SCREEN_BASE
  stax copy_dest  
  ldax #udp_inp_data+4
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


tftpget:
  kernal_print DOWNLOADMESSAGE

  lda SERVER_IP+0
  sta tftp_ip+0 
  lda SERVER_IP+1
  sta tftp_ip+1
  lda SERVER_IP+2
  sta tftp_ip+2
  lda SERVER_IP+3
  sta tftp_ip+3
  
  ldax #tftpname
  stax tftp_filename
  
  ldax #$4000
  stax tftp_load_address
  
  jsr tftp_clear_callbacks   ; To RAM directly
  
  jsr tftp_download  
  bcs tftperror

tftpok: 
  kernal_print OKMESSAGE 
  rts


tftperror:
  kernal_print FAILMESSAGE
:
  lda #$07
  sta $d020
  lda #$02
  sta $d020
  jmp :-
    

; -------------------------------------------------------------------------
; Network Constants and Data  
  
SERVER_IP:
  .byte 208,79,218,201    ; Vortex VPS  
  
SERVER_PORT = 3005
SRC_PORT    = 6464
LISTEN_PORT = 3000

SENDBUFFER:
  .res SEND_LENGTH
  
WAITMESSAGE:
  .byte "press any key to continue."
  .byte 0
  
NETWORKMESSAGE:
  .byte 147, CG_LCS, CG_DCS, CG_LBL
  .byte "vORTEX 2 nETWORK INITIALIZATION",13
  .byte "fORWARD udp pORT 3000 TO YOUR C64",13,13
  .byte 0

DOWNLOADMESSAGE:
  .byte "dOWNLOADING GAME DATA..."
  .byte 0

SERVERMESSAGE:
  .byte "wAITING FOR SERVER..."
  .byte 0       

OKMESSAGE:
  .byte "ok",13
  .byte 0                      

FAILMESSAGE:
  .byte "failed",13
  .byte 0                    
  
packetreceived:
   .byte 0           

tftpname:
  .byte "vortexdata", 0

bittab:
   .byte $80,$40,$20,$10,$08,$04,$02,$01
