
Something's amiss with the Eclipse project setup, the best workaround for building vortexserver-runnable.jar reliably with all the dependencies (gson lib, fonts, etc) included is two steps:

1. Build vortexserver.jar by double-clicking on vortexjardefault.jardesc and accepting defaults

2. Build vortexserver-runnable.jar by going to File->Export->Java->Runnable Jar File and accepting defaults.

There will be some warnings about duplicate entries but these can be ignored.

