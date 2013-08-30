; Copy screen data from UDP buffer to screen

copyscreen:
  BORDER #$07

  ; Screen Data
  
  ; Copy to the screen that is not currently being shown
;  lda whichscreen
;  beq screen1copy      ; if 0, copy to screen 1
  
;screen0copy:
  ldax #$8000
;  jmp docopy
  
;screen1copy:
;  ldax #$8400
  
  ; Do the copy 
;docopy:
  stax copy_dest  
  ldax #udp_inp_data+4
  stax copy_src 
  ldax #100 ; #1000   ; Decimal
  
  
  