; -------------------------------------------------------------------------
; Vortex II Login Code

NAMELENGTH=15

login:  
  kernal_print TITLEMESSAGE
  
  ldax #ALPHANUMERIC
  ldy #NAMELENGTH
  jsr FILTERED_INPUT
  
  ;Name is now in GOTINPUT  
  rts  

; -------------------------------------------------------------------------
  
TITLEMESSAGE:
  .byte 147, CG_LCS, CG_DCS, CG_LBL
  .byte 13, 13, "vORTEX 2 vERSION 0.003", 13, 13
  .byte "lOGIN: ", CG_RED
  .byte 0
  
; EOF!