; -------------------------------------------------------------------------
; Vortex II IRQ Code

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

  lda #230                      ; line number to go off at
  sta $d012                     ; low byte of raster line

  ldax #irqcode                 ; get address of target routine
  stax 788                      ; put into interrupt vector

  cli                           ; re-enable interrupts
  rts                           ; return to caller

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
  
; EOF!