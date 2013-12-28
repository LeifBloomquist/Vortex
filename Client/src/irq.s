; -------------------------------------------------------------------------
; Vortex II IRQ Code

.define RASTER_TOP    230
.define RASTER_BOTTOM 220  

; -------------------------------------------------------------------------
; IRQ Initialization

irq_init:
  sei                           ; disable interrupts

  lda #$7f                      ; turn off the cia interrupts
  sta $dc0d

  lda $d01a                     ; enable raster irq
  ora #$01
  sta $d01a

  lda $d011                     ; clear high bit of raster line
  and #$7f
  sta $d011

  lda #RASTER_TOP               ; line number to go off at
  sta $d012                     ; low byte of raster line

  ldax #irqtop                  ; get address of target routine
  stax 788                      ; put into interrupt vector

  cli                           ; re-enable interrupts
  rts                           ; return to caller

; -------------------------------------------------------------------------
; Main IRQ Code (Top)

irqtop:
  lda $d019   ; Clear irq
  sta $d019

  ; IRQ code starts here
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
  
irqtop_x:
  BORDER $00
  
  ; Point to the bottom IRQ
  ldax #irqbottom 
  stax 788
  
  lda #RASTER_BOTTOM               ; line number to go off at        
  sta $d012
   
  ; Exit the current interrupt.  
  pla
  tay                           
  pla                           
  tax                          
  pla                        
  rti

; -------------------------------------------------------------------------
; Routines called within the (Top) IRQ.

irq_update:
  BORDER $03
  jsr sendupdate
  jmp irqtop_x

irq_process:
  BORDER $02
  jsr ip65_process 
  jmp irqtop_x
   
irq_screen:
  BORDER $07
  lda screenreceived
  beq :+

  jsr copyscreen
  lda #$00
  sta screenreceived
  
:  
  jmp irq_reset    ; For last frame type only

; -------------------------------------------------------------------------
; Misc flags used by the top IRQ
  
frametype:
  .byte $00     ; Used to round-robin through frame types

screenreceived:
  .byte $00     ; Set to 1 when a full screen is received, so irq can process.


; -------------------------------------------------------------------------
; Main IRQ Code (Bottom)

irqbottom:
  lda $d019   ; Clear irq
  sta $d019

  BORDER $01       
  
  
  ; Fix the screen
  
  
  
  ; Read joystick (this is quick, so put it here)
  jsr READJOYSTICK


  BORDER $00
  ; Point back to the top IRQ
  ldax #irqtop 
  stax 788
  
  lda #RASTER_TOP              ; line number to go off at        
  sta $d012

  ; Exit this interrupt. 
  ;jmp $ea31          ; Exit to ROM.  Alternately, use below if we don't need ROM routines. 
  pla                 ; we exit interrupt entirely.
  tay                           
  pla                           
  tax                          
  pla      
  rti
    
; EOF!