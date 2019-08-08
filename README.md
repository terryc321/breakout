# breakout.core

game clone of arkanoid , breakout , ball bouncing on bricks game

## Overview

simplify the programming task

needs a clock later on to tell how long level took and control the animation ?
maybe.


1 - get the ball to bounce around - done
2 - get the ball to interact with a brick - done
3 - increment score when ball hits brick - done
4 - figure out moving the bat 
5 - have ball bounce off bat
6 - have ball bounce off brick , removing that brick , bouncing off as if it hit a wall
    bound box and determine if ball came in from north south east or west.

## Development

initiated project directory using figwheel-main

    lein new figwheel-main breakout.core -- --reagent

    cd breakout.core

    lein fig:build

browser opens up at localhost:9500/
terminal repl is exceptionally good.

changing code in breakout.core , project gets rebuilt in about 10 seconds effects filter through to
browser.

example : 
(.setInterval js/window #(..%..) 20)
if we have some routine being executed every 20 milliseconds then
when code changed , the old interval keeps firing and new interval fires ,
so get twice number of signals speeds up intervals

======================================================================

to get some general psychological help try

    :repl/help

to quit from the magnificient repl

    :cljs/quit


======================================================================

To get server up and running using leniningen lein

To get an interactive development environment run:

    lein fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:min


## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
