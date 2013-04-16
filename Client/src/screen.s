; -------------------------------------------------------------------------
; Vortex II Screen

; -------------------------------------------------------------------------
; Screen Initialization

screen_init:
  lda #CG_DCS
  jsr $FFD2
  
  lda #CG_LCS
  jsr $FFD2
  
  lda #$00
  sta $d020
  sta $d021
  
  ; Blank screen
  lda #32
  ldx #$00     
:
  sta SCREEN_BASE+$000,x
  sta SCREEN_BASE+$100,x
  sta SCREEN_BASE+$200,x
  sta SCREEN_BASE+$2E8,x
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
  
  ; Assume for now, don't have to bank out BASIC
  
  ; Set up VIC to Bank 1  ($4000-$7FFF)
  lda $DD00
  and #$FC
  ora #$02   ; $DD00 = %xxxxxx10 -> bank1: $4000-$7fff
  sta $DD00

  ; Set screen to $4800 and character set to $4000
  lda #%00100000
  sta $d018
  
  rts             


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
; Screen Constants

SCREEN_BASE=$4800
CHAR_BASE=$4000

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
