@cls
@del *.prg
@del *.o

@bin\ca65 vortex.s
@bin\ld65  vortex.o  -C ip65/cfg/c64prg.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib   -o !vortex.prg
@dir *.prg