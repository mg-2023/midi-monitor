package eventpanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public final class StateVisualizer extends JPanel {
	private static final long serialVersionUID = 1;
	
	private int vol, exp, pan;
	
	public StateVisualizer() {
		this.vol = 100;
		this.exp = 127;
		this.pan = 64;
		
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		
		this.setLayout(new GridLayout(1, 3));
		JLabel vol = new JLabel("  Vol");
		JLabel exp = new JLabel("  Exp");
		JLabel pan = new JLabel("  Pan");
		
		this.add(vol);
		this.add(exp);
		this.add(pan);
		
		vol.setForeground(Color.WHITE);
		exp.setForeground(Color.WHITE);
		pan.setForeground(Color.WHITE);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		float w = (float)this.getWidth();
		float w3 = w/3f;
		float h = (float)this.getHeight();
		
		float volStart = 0f;
		float expStart = w3;
		float panStart = w - w3;
		
		float volOffset = w3 * (this.vol/127f);
		float expOffset = w3 * (this.exp/127f);
		float panOffset = (w3-3f) * (this.pan/127f) + 1f;
		float panCenter = (w3-3f) * (64f/127f) + 1f;
		
		super.paintComponent(g);
		
		// individual bar
		g.setColor(new Color(192, 0, 0));
		g.fillRect((int)volStart, 0, (int)volOffset, (int)h);
		
		g.setColor(new Color(0, 192, 0));
		g.fillRect((int)expStart, 0, (int)expOffset, (int)h);
		
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine((int)(panStart+panCenter), 0, (int)(panStart+panCenter), (int)h);
		
		g.setColor(new Color(0, 255, 255));
		g.drawLine((int)(panStart+panOffset), 0, (int)(panStart+panOffset), (int)h);
		
		// outline
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, (int)w-1, (int)h-1);
		g.drawLine((int)expStart, 0, (int)expStart, (int)h);
		g.drawLine((int)panStart, 0, (int)panStart, (int)h);
		
	}
	
	public void setVol(int vol) {
		this.vol = vol;
		this.repaint();
	}
	
	public void setExp(int exp) {
		this.exp = exp;
		this.repaint();
	}
	
	public void setPan(int pan) {
		this.pan = pan;
		this.repaint();
	}
}
