; -------------------------------------------------------------------------
; Vortex II Joystick code and vars

MAX=30

;Flag that the joystick may be used for input
JOYOK:
   .byte $01

;This holds the joystick button state
JOYBUTTON:
  .byte $00
                
;Flags for directions
JOYLEFT:
  .byte $00
JOYRIGHT:
  .byte $00
JOYUP:
  .byte $00
JOYDOWN:
  .byte $00

RATE_X:    ; Signed
  .byte 0

RATE_Y:    ; Signed
  .byte 0
  
DIRECTION:
  .byte 0
  
; ---------------------------------------------------------------------
; Joystick handler.

READJOYSTICK:     ; Thanks Jason aka TMR/C0S
  lda JOYOK       ; Joystick input is allowed
  bne JOYSTART
  rts

JOYSTART:
  lda #$00
  sta JOYLEFT
  sta JOYRIGHT
  sta JOYUP
  sta JOYDOWN  
  sta JOYBUTTON  
  
; ---------------------------------------------------------------------
; Check joystick bits.
  lda $dc00  ; Port 2
up:     
  lsr
  bcs down
  inc JOYUP
down:  
  lsr
  bcs left
  inc JOYDOWN
left: 
  lsr
  bcs right
  inc JOYLEFT
right:  
  lsr
  bcs fire
  inc JOYRIGHT
fire:
  lsr
  bcs JOY_DONE
  inc JOYBUTTON

; ---------------------------------------------------------------------
; Process movements.  Can use A again.
JOY_DONE:
  lda JOYUP
  beq DODOWN

  ; Up
  lda RATE_Y       ; Limit
  cmp #-MAX
  beq DODOWN
  
  dec RATE_Y 

; ---------------------------------------------------------------------
DODOWN:
  lda JOYDOWN
  beq DOLEFT
  
  ; Down
  lda RATE_Y       ; Limit
  cmp #MAX
  beq DOLEFT

  inc RATE_Y 

; ---------------------------------------------------------------------
DOLEFT:
  lda JOYLEFT
  beq DORIGHT
  
  ; Left
  lda RATE_X       ; Limit
  cmp #-MAX
  beq DORIGHT
  
  dec RATE_X
  
; ---------------------------------------------------------------------
DORIGHT:
  lda JOYRIGHT
  beq DOBUTTON

  ; Right
  lda RATE_X       ; Limit
  cmp #MAX
  beq DOBUTTON
  
  inc RATE_X

; ---------------------------------------------------------------------
DOBUTTON:
  lda JOYBUTTON
  beq JOYDIRECTION

  ; Fire button handling here, if needed

; ---------------------------------------------------------------------
JOYDIRECTION:
   
   ; Determine direction with a lookup table
   lda $dc00  ; Port 2
   and #$0F
   tax
   lda spritedirections,x
   cmp #$ff ; leave as-is
   beq JOY_x
   
   clc
   sta DIRECTION   
   adc #SPRITE_BASE
   adc #SPRITE_PLAYER
   sta SPRITE_POINTERS+00
  
; ---------------------------------------------------------------------
JOY_x:
  rts
   

LOCATION_X:
  .byte $88, $13    ; 5000  
   
LOCATION_Y:
  .byte $EC, $13    ; 5100    

; Lookup table to map joystick input to a sprite.  
; $FF=leave as before (centered or impossible)
spritedirections:  
  .byte $ff,$ff,$ff,$ff
  .byte $ff,$03,$01,$02
  .byte $ff,$05,$07,$06
  .byte $ff,$04,$00,$ff
  
; ---------------------------------------------------------------------
; Position calculation.
; TODO

