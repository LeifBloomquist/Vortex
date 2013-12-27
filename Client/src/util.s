 -------------------------------------------------------------------------
; Vortex II Source Code
; Misc utilities

; -------------------------------------------------------------------------
; Wait 
waitforkey:
   
  ; Wait for key
  kernal_print WAITMESSAGE
    
gak0:
	jsr $FFE4  ;GETIN
	beq gak0

  rts
  
; EOF!