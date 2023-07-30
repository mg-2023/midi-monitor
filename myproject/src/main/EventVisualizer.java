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
	
	ProgramVisualizer[] channelProgramPanel;
	NoteVisualizer[] noteStatePanel;
	StateVisualizer[] channelStatePanel;
	
	TimeSigDisplayer timeSigDisplayPanel;
	TotalCounter totalCounterPanel;
	TempoDisplayer tempoDisplayPanel;
	
	public EventVisualizer() {
		this.totalNote = 0;
		
		this.setLayout(new GridLayout(17, 3));
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		this.channelProgramPanel = new ProgramVisualizer[16];
		this.noteStatePanel = new NoteVisualizer[16];
		this.channelStatePanel = new StateVisualizer[16];
		for(int i=0; i<16; i++) {
			this.channelProgramPanel[i] = new ProgramVisualizer(i+1);
			this.add(this.channelProgramPanel[i]);
			
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
	
	// program visualizer related methods
	void getProgramChange(byte[] msgByte, ProgramVisualizer[] panels) {
		int chn = (int)msgByte[0] & 0xF;
		int prog = panels[chn].getProgram();
		int bankMSB = panels[chn].getBankMSB();
		int bankLSB = panels[chn].getBankLSB();
		
		// process this method only in program change or bank select
		if((msgByte[0] & 0xF0) == 0xC0) {
			panels[chn].changeProgram(msgByte[1], bankMSB, bankLSB);
		}
		
		else if((msgByte[0] & 0xF0) == 0xB0) {
			switch(msgByte[1]) {
			case 0x00:
				panels[chn].changeProgram(prog, msgByte[2], bankLSB);
				break;
			
			case 0x20:
				panels[chn].changeProgram(prog, bankMSB, msgByte[2]);
				break;
			}
		}
	}
	
	// note visualizer related methods
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
	
	void getPitchChange(byte[] msgByte, NoteVisualizer[] panels) {
		int chn = (int)msgByte[0] & 0xF;
		// process this method only in pitch bend
		if((msgByte[0] & 0xF0) == 0xE0) {
			panels[chn].setPitch(msgByte[1], msgByte[2]);
		}
	}
	
	// state visualizer related methods
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
		this.getProgramChange(msgByte, channelProgramPanel);
		
		this.countNotes(msgByte, this.noteCountText);
		this.getPitchChange(msgByte, this.noteStatePanel);
		
		this.getStateChange(msgByte, this.channelStatePanel);
		this.totalCounterPanel.updateText(this.totalNote);
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
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
			
			this.setLayout(new BorderLayout());
			this.sig = new JLabel("Time Signature: - / -", SwingConstants.CENTER);
			this.add(this.sig);
			
			this.sig.setForeground(Color.WHITE);
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
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
			
			this.setLayout(new BorderLayout());
			this.noteCount = new JLabel("Total Note: 0", SwingConstants.CENTER);
			this.add(this.noteCount);
			
			this.noteCount.setForeground(Color.WHITE);
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
			this.setBackground(Color.BLACK);
			this.setOpaque(true);
			
			this.setLayout(new BorderLayout());
			this.BPM = new JLabel("BPM: ---.--", SwingConstants.CENTER);
			this.add(this.BPM);
			
			this.BPM.setForeground(Color.WHITE);
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
