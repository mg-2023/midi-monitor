# midi-monitor
Midi file player with simple visualizer

## JDK Version
20

## Main file
Open src/main/MidiMonitor.java

## What this program does
Reads midi message from sequencer and visualizes it

On windows it plays file with MS GS Wavetable synth (with java's default synthesizer)

* 1st column
    * Channel name, from 1 to 16
    * Time signature (bottom row)
* 2nd column
    * Note count of individual channel
    * Note visualization (if note on message is received bar appears on corresponding position)
    * Total note count (bottom row)
* 3rd column
    * Volume, Expression, Stereo Pan of individual channel
    * Tempo in BPM, decimal point 2 digit precision (bottom row)

## Caution
**Do not delete src/files/demo.mid and reset.mid!!!**
* demo.mid plays when user hits play button without selecting another file
* reset.mid plays to *reset* the sequencer's midi controller state when user hits stop button
