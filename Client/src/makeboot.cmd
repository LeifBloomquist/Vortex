@cls
@del *.o
@del !vortexboot.prg

@cc65\ca65 vortexboot.s
@cc65\ld65 vortexboot.o -C cfg/c64prg-vortex-boot.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib -o !vortexboot.prg

@dir *.prg
@rem @copy !*.prg p:\vortex\
@copy !*.prg C:\Leif\Commodore\ideserv\pclink\vortex