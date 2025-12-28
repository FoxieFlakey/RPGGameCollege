Today created Camera and World class which renders pretty basic
world and allowing camera to view different part of world easily
instead fixed.

Then also moved to fully use Java AWT, because a need to do manual
render code. Although the events like mouse, keyboard and etc stil
is handled on seperate thread on EDT thread which caused a pain so
I plan to create Mouse and Keyboard class which handles the nitty
gritty details of capturing state of keyboard and mouse so it can
be handled on game thread which makes it easier as game no longer
need to be aware of other thread. All game logics run on same
thread. Currently no keyboard yet because my phone's battery ran
out and had to charge it. My laptop is dead and my sister's laptop
is on another city (she took it there). So I got a USB hub and
plug keyboard and mouse that way.

Coding with phone's on screen keyboard is very slow and error
prone so I decided to not do it like that. The USB hub sadly got
the one which cant charge while using the devices

~ Foxie Flakey the cute fox :3



