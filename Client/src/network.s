; -------------------------------------------------------------------------
; Vortex II Network Code

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

network_init:

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
  ;sta whichscreen
  
  lda #$01
  sta screenreceived   ; Flag for next IRQ frame
  rts

; -------------------------------------------------------------------------
; Copy screen data from UDP buffer to screen

copyscreen:
  BORDER #$07

  jsr finexy

  ldax #SCREEN_BASE
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
; Network Constants  
  
SERVER_IP:
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