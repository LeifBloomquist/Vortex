@cls
@del *.o
@del *.prg

@cc65\ca65 vortexboot.s
@cc65\ld65 vortexboot.o -C cfg/c64prg-vortex.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib -o !vortexboot.prg

@cc65\ca65 vortex.s
@cc65\ld65 vortex.o -C cfg/c64prg-vortex8000.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib  -Ln vortex.lbl  -o vortex8000.prg

@dir *.prg
@copy !*.prg p:\vortex\
@copy vortex8000.prg ..\..\Server\data