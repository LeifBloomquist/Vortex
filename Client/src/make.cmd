@cls
@del *.o
@del *.prg
@del *.bin

@cc65\ca65 vortexboot.s
@cc65\ld65 vortexboot.o -C cfg/c64prg-vortex-boot.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib -o !vortexboot.prg

@cc65\ca65 vortex.s
@cc65\ld65 vortex.o -C cfg/c64prg-vortex-8000.cfg ip65/ip65/ip65.lib  ip65/drivers/c64rrnet.lib  -Ln vortex.lbl  -o vortex8000.bin

@dir *.prg
@dir *.bin
@copy !*.prg p:\vortex\
@copy vortex8000.bin ..\..\Server\data\