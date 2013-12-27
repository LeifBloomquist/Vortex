@cls
@del *.o
@del *.prg

@bin\ca65 vortex.s
@bin\ld65  vortex.o -C ip65/cfg/c64prg-vortex.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib  -m vortex.map -vm   -o !vortex.prg
@dir *.prg
@copy *.prg p:\vortex\