package main;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

public class VirtualSequencer {
	Sequencer sequencer;
	Transmitter trans;
	
	public VirtualSequencer() {
		try {
			File midiFile = new File("src/files/demo.mid");
			Sequence sequence = MidiSystem.getSequence(midiFile);
			
			this.sequencer = MidiSystem.getSequencer();
			this.sequencer.setSequence(sequence);
			
			this.trans = this.sequencer.getTransmitter();
			
			this.sequencer.open();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Sequencer getSequencer() {
		return this.sequencer;
	}
	
	public Transmitter getTransmitter() {
		return this.trans;
	}
}
