package eventpanel;

import java.awt.*;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public final class NoteVisualizer extends JPanel {
	private static final long serialVersionUID = 1;
	
	private int channel;
	private int noteCount;
	private int pitch;
	private boolean[] noteState;
	private JLabel noteText;

	public NoteVisualizer(int channel) {
		this.setLayout(new BorderLayout());
		this.channel = channel;
		this.noteCount = 0;
		this.noteState = new boolean[128];
		Arrays.fill(this.noteState, false);
		
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		
		this.noteText = new JLabel("0 Notes", SwingConstants.CENTER);
		this.noteText.setForeground(Color.WHITE);
		this.add(noteText);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		float w = (float)this.getWidth();
		float h = (float)this.getHeight();
		float h2 = h/2f;
		int pOffset = this.pitch / 2048;
		
		g.setColor(new Color(64, 64, 64));
		for(int i=0; i<=120; i+=12) {
			int x = (int)(w*i / 128f);
			g.drawLine(x, 0, x, (int)h);
		}
		
		g.setColor(new Color(Color.HSBtoRGB((float)channel/16f, 0.667f, 1f)));
		for(int i=0; i<128; i++) {
			int x = (int)(w*i / 128f);
			if(this.noteState[i] == true) {
				g.drawLine(x, 0, x+pOffset, (int)h2);
				g.drawLine(x+pOffset, (int)h2, x, (int)h);
			}
		}
	}
	
	public int getNote() {
		return this.noteCount;
	}
	
	public void resetNote() {
		this.noteCount = 0;
		this.noteText.setText("0 Notes");
		this.repaint();
	}
	
	public void addNote() {
		this.noteText.setText(++this.noteCount + " Notes");
		this.repaint();
	}
	
	public void activateNote(int noteNum) {
		this.noteState[noteNum] = true;
		this.repaint();
	}
	
	public void deactivateNote(int noteNum) {
		this.noteState[noteNum] = false;
		this.repaint();
	}
	
	public void setPitch(byte b1, byte b2) {
		int pitch = b1 + (b2<<7);
		this.pitch = pitch-8192;
		this.repaint();
	}
}
