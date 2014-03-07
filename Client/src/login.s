; -------------------------------------------------------------------------
; Vortex II Login Code

NAMELENGTH=16

login:  
  kernal_print TITLEMESSAGE
  
  ldax #TEXT
  ldy #NAMELENGTH
  jsr FILTERED_INPUT
  
  ; TODO: Copy to packet
  rts  

; -------------------------------------------------------------------------
  
TITLEMESSAGE:
  .byte 147, CG_LCS, CG_DCS, CG_LBL
  .byte "vORTEX 2 vERSION 0.001", 13, 13
  .byte "lOGIN: "
  .byte 0
  
  
username:
  .res NAMELENGTH
  .byte 0
  
; EOF!