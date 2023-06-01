package main;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

import files.MidiFileStream;

public class VirtualSequencer {
	Sequencer sequencer;
	Transmitter trans;
	
	public VirtualSequencer() {
		try {
			MidiFileStream demoStream = new MidiFileStream("demo.mid");
			Sequence sequence = MidiSystem.getSequence(demoStream.getInputStream());
			
			this.sequencer = MidiSystem.getSequencer();
			this.sequencer.setSequence(sequence);
			
			this.trans = this.sequencer.getTransmitter();
			
			this.sequencer.open();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Sequencer getSequencer() {
		return this.sequencer;
	}
	
	public Transmitter getTransmitter() {
		return this.trans;
	}
}
