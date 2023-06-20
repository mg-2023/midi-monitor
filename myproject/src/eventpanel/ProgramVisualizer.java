package eventpanel;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public final class ProgramVisualizer extends JPanel {
	private static final long serialVersionUID = 1;
	
	JLabel channelNum;
	JLabel channelProg;
	int chn;
	int prog, bankMSB, bankLSB;
	
	public ProgramVisualizer(int chn) {
		this.setLayout(new GridLayout(1, 2));
		
		this.chn = chn;
		this.channelNum = new JLabel("Channel " + this.chn, SwingConstants.CENTER);
		this.add(this.channelNum);
		
		this.prog = 0;
		this.bankMSB = 0;
		this.bankLSB = 0;
		
		this.channelProg = new JLabel(String.format("[%d, %d, %d]", this.prog, this.bankMSB, this.bankLSB), SwingConstants.CENTER);
		this.add(this.channelProg);
	}
	
	public int getProgram() {
		return this.prog;
	}
	
	public int getBankMSB() {
		return this.bankMSB;
	}
	
	public int getBankLSB() {
		return this.bankLSB;
	}
	
	public void changeProgram(int prog, int bankMSB, int bankLSB) {
		this.prog = prog;
		this.bankMSB = bankMSB;
		this.bankLSB = bankLSB;
		this.channelProg.setText(String.format("[%d, %d, %d]", this.prog, this.bankMSB, this.bankLSB));
	}
}
