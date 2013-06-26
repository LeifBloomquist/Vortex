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
  
  
  
  
  MEMORY {
    ZP: start = $02, size = $1A, type = rw ;
    IP65ZP: start = $5f, size = $10, type = rw;
    RAM: start = $07FF, size = $77ab, file = %O;
    RAM3000: start = $3000, size = $4800, type=rw;
    RAM4000: start = $4000, size = $3800, type=rw;
    RAM6000: start = $6000, size = $1800, type=rw;
    DISCARD: start = $77FF, size = $10;
    
    CHARS:   start = $4000, size = $0800, type = ro, file = %O, fill = yes;
    SPRITES: start = $4C00, size = $2000, type = ro, file = %O, fill = yes;
    
}
SEGMENTS {
    STARTUP: load = RAM, type = ro ,define = yes, optional=yes;
    CODE: load = RAM, type = ro,define = yes;
    DATA: load = RAM, type = rw,define = yes;
    SELF_MODIFIED_CODE: load = RAM, type = rw,define = yes, optional=yes;
    VIC_DATA: load = RAM, type = rw,align = $800, optional=yes;
    RODATA: load = RAM, type = ro,define = yes, optional=yes;
    IP65_DEFAULTS: load = RAM, type = rw,define = yes, optional=yes;
    BSS: load = RAM, type = bss, optional=yes;
    SAFE_BSS: load = RAM3000, type = bss, optional=yes;
    BSS4K: load = RAM4000, type = bss, optional=yes;
    DATA6K:   load = RAM, run = RAM6000, type = rw, define = yes, optional=yes;
    APP_SCRATCH: load = RAM, type = bss, optional=yes;
    ZEROPAGE: load = ZP, type = zp, optional=yes;
    IP65ZP: load = IP65ZP, type = zp, optional=yes;
    EXEHDR: load = DISCARD, type = ro, optional=yes;
    TCP_VARS: load = RAM, type = bss, optional=yes;
    HTTP_VARS: load = RAM, type = bss, optional=yes;
    
    CHARS:      load = CHARS, type = ro,  optional=yes;
    SPRITES:    load = SPRITES, type = ro,  optional=yes;
}


   .segment    "CHARS"   
   .incbin "..\..\Server\data\chars.raw"
   
      .segment "SPRITES"
   .incbin "..\..\Server\data\Sprites.raw"
