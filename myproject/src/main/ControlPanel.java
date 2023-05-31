package main;

import java.io.File;

import java.awt.event.*;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.sound.midi.*;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1;
	
	public ControlPanel(Sequencer seq, EventVisualizer rcv) {
		seq.addMetaEventListener(rcv);
		
		this.setLayout(new FlowLayout());
		JButton openBtn = new JButton("Open");
		JButton startBtn = new JButton("Start");
		JButton stopBtn = new JButton("Stop");
		
		stopBtn.setEnabled(false);
		
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(""));
				chooser.setFileFilter(new FileNameExtensionFilter("Midi files (.mid)", "mid"));
				int res = chooser.showOpenDialog(ControlPanel.this.getRootPane());
				
				if(res != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				else {
					try {
						File file = chooser.getSelectedFile();
						Sequence sequence = MidiSystem.getSequence(file);
						seq.setSequence(sequence);
						rcv.resetNotes();
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(ControlPanel.this.getRootPane(), "Failed to open selected file!\n" + ex.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rcv.resetNotes();
				
				seq.setMicrosecondPosition(0);
				seq.start();
				openBtn.setEnabled(false);
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
			}
		});
		
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seq.stop();
				
				try {
					File reset = new File("src/files/reset.mid");
					Sequence temp = seq.getSequence();
					Sequence sequence = MidiSystem.getSequence(reset);
					seq.setSequence(sequence);
					
					seq.start();
					Thread.sleep(10);
					seq.stop();
					seq.setSequence(temp);
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(ControlPanel.this.getRootPane(), "Failed to reset sequencer state!\n" + ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					openBtn.setEnabled(true);
					startBtn.setEnabled(true);
					stopBtn.setEnabled(false);
				}
			}
		});
		
		this.add(openBtn);
		this.add(startBtn);
		this.add(stopBtn);
	}
}
