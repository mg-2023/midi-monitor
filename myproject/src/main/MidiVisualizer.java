package main;

import java.awt.*;
import javax.swing.*;

import javax.sound.midi.Transmitter;

public class MidiVisualizer extends JFrame {
	private static final long serialVersionUID = 1;
	
	public MidiVisualizer() {
		VirtualSequencer vSeq = new VirtualSequencer();
		Transmitter trans = vSeq.getTransmitter();
		
		this.setTitle("Midi File Monitor");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Container c = getContentPane();
		EventVisualizer rcv = new EventVisualizer();
		trans.setReceiver(rcv);
		
		c.add(rcv, BorderLayout.CENTER);
		
		c.add(new ControlPanel(vSeq.getSequencer(), rcv), BorderLayout.NORTH);
		
		this.setSize(640, 480);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new MidiVisualizer();
	}
}
