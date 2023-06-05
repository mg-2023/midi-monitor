package main;

import java.awt.*;

import javax.swing.*;

import javax.sound.midi.*;

import eventpanel.*;

public class EventVisualizer extends JPanel implements Receiver, MetaEventListener {
	private static final long serialVersionUID = 1;
	
	int totalNote;
	
	JLabel[] noteCountText;
	JLabel[] channelStateText;
	
	NoteVisualizer[] noteStatePanel;
	StateVisualizer[] channelStatePanel;
	
	TimeSigDisplayer timeSigDisplayPanel;
	TotalCounter totalCounterPanel;
	TempoDisplayer tempoDisplayPanel;
	
	public EventVisualizer() {
		this.totalNote = 0;
		
		this.setLayout(new GridLayout(17, 3));
		this.channelStateText = new JLabel[16];
		this.noteStatePanel = new NoteVisualizer[16];
		this.channelStatePanel = new StateVisualizer[16];
		for(int i=0; i<16; i++) {
			this.add(new JLabel("Channel " + (i+1), SwingConstants.CENTER));
			
			this.noteStatePanel[i] = new NoteVisualizer(i);
			this.add(this.noteStatePanel[i]);
			
			this.channelStatePanel[i] = new StateVisualizer();
			this.add(this.channelStatePanel[i]);
		}
		
		for(int i=0; i<3; i++) {
			switch(i) {
			case 0:
				this.timeSigDisplayPanel = new TimeSigDisplayer();
				this.add(this.timeSigDisplayPanel);
				break;
				
			case 1:
				this.totalCounterPanel = new TotalCounter();
				this.add(this.totalCounterPanel);
				break;
				
			case 2:
				this.tempoDisplayPanel = new TempoDisplayer();
				this.add(this.tempoDisplayPanel);
				break;
			}
		}
	}
	
	void countNotes(byte[] msgByte, JLabel[] labels) {
		int chn = (int)msgByte[0] & 0xF;
		if((msgByte[0] & 0xF0) == 0x90 && msgByte[2] > 0x00) {
			this.noteStatePanel[chn].addNote();
			this.noteStatePanel[chn].activateNote(msgByte[1]);
			
			this.totalNote++;
		}
		
		else if((msgByte[0] & 0xF0) == 0x90 && msgByte[2] == 0x00) {
			this.noteStatePanel[chn].deactivateNote(msgByte[1]);
		}
		
		else if((msgByte[0] & 0xF0) == 0x80) {
			this.noteStatePanel[chn].deactivateNote(msgByte[1]);
		}
	}
	
	void resetNotes() {
		for(int i=0; i<16; i++) {
			this.noteStatePanel[i].resetNote();
		}
		
		this.totalNote = 0;
	}
	
	void getStateChange(byte[] msgByte, StateVisualizer[] panels) {
		int chn = (int)msgByte[0] & 0xF;
		// process this method only in control change
		if((msgByte[0] & 0xF0) == 0xB0) {
			switch(msgByte[1]) {
			case 0x07:
				panels[chn].setVol(msgByte[2]);
				break;
				
			case 0x0B:
				panels[chn].setExp(msgByte[2]);
				break;
				
			case 0x0A:
				panels[chn].setPan(msgByte[2]);
				break;
			}
		}
	}
	
	void getPitchChange(byte[] msgByte, NoteVisualizer[] panels) {
		int chn = (int)msgByte[0] & 0xF;
		// process this method only in pitch bend
		if((msgByte[0] & 0xF0) == 0xE0) {
			panels[chn].setPitch(msgByte[1], msgByte[2]);
		}
	}
	
	public int getAllNotes() {
		int res = 0;
		for(NoteVisualizer notePanel : this.noteStatePanel) {
			res += notePanel.getNote();
		}
		
		return res;
	}
	
	@Override
	public void send(MidiMessage msg, long timeStamp) {
		byte[] msgByte = msg.getMessage();
		this.countNotes(msgByte, this.noteCountText);
		this.getStateChange(msgByte, this.channelStatePanel);
		this.totalCounterPanel.updateText(this.totalNote);
		this.getPitchChange(msgByte, this.noteStatePanel);
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public void meta(MetaMessage meta) {
		byte[] msgByte = meta.getMessage();
		this.timeSigDisplayPanel.updateText(msgByte);
		this.tempoDisplayPanel.updateText(msgByte);
	}
	
	// additional class 1; displays time signature
	class TimeSigDisplayer extends JPanel {
		private static final long serialVersionUID = 1;
		
		JLabel sig;
		
		public TimeSigDisplayer() {
			this.setLayout(new BorderLayout());
			this.sig = new JLabel("Time Signature: - / -", SwingConstants.CENTER);
			this.add(this.sig);
		}
		
		public void updateText(byte[] msgByte) {
			if(msgByte[1] == 0x58) {
				this.sig.setText(String.format("Time Signature: %d/%d", msgByte[3], 1<<msgByte[4]));
			}
		}
	}
	
	// additional class 2; counts total note
	class TotalCounter extends JPanel {
		private static final long serialVersionUID = 1;
		
		JLabel noteCount;
		
		public TotalCounter() {
			this.setLayout(new BorderLayout());
			this.noteCount = new JLabel("Total Note: 0", SwingConstants.CENTER);
			this.add(this.noteCount);
		}
		
		public void updateText(int note) {
			this.noteCount.setText("Total Note: " + EventVisualizer.this.totalNote);
		}
	}
	
	// additional class 3; displays tempo in BPM
	class TempoDisplayer extends JPanel {
		private static final long serialVersionUID = 1;
		
		JLabel BPM;
		
		public TempoDisplayer() {
			this.setLayout(new BorderLayout());
			this.BPM = new JLabel("BPM: ---.--", SwingConstants.CENTER);
			this.add(this.BPM);
		}
		
		// invoked by method 'meta'
		public void updateText(byte[] msgByte) {
			if(msgByte[1] == 0x51) {
				int b1 = Byte.toUnsignedInt(msgByte[3]);
				int b2 = Byte.toUnsignedInt(msgByte[4]);
				int b3 = Byte.toUnsignedInt(msgByte[5]);
				
				int us = (b1<<16) + (b2<<8) + b3;
				double tempo = 60d * (1e6/(double)us);
				this.BPM.setText(String.format("BPM: %.2f", tempo));
			}
		}
	}
}
