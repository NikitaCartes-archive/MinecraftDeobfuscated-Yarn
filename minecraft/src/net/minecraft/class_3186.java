package net.minecraft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;

public class class_3186 extends JComponent {
	private static final DecimalFormat field_13846 = class_156.method_654(
		new DecimalFormat("########0.000"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
	);
	private final int[] field_13845 = new int[256];
	private int field_13848;
	private final String[] field_13847 = new String[11];
	private final MinecraftServer field_13849;
	private final Timer field_16858;

	public class_3186(MinecraftServer minecraftServer) {
		this.field_13849 = minecraftServer;
		this.setPreferredSize(new Dimension(456, 246));
		this.setMinimumSize(new Dimension(456, 246));
		this.setMaximumSize(new Dimension(456, 246));
		this.field_16858 = new Timer(500, actionEvent -> this.method_13982());
		this.field_16858.start();
		this.setBackground(Color.BLACK);
	}

	private void method_13982() {
		long l = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.field_13847[0] = "Memory use: " + l / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
		this.field_13847[1] = "Avg tick: " + field_13846.format(this.method_13980(this.field_13849.field_4573) * 1.0E-6) + " ms";
		this.field_13845[this.field_13848++ & 0xFF] = (int)(l * 100L / Runtime.getRuntime().maxMemory());
		this.repaint();
	}

	private double method_13980(long[] ls) {
		long l = 0L;

		for (long m : ls) {
			l += m;
		}

		return (double)l / (double)ls.length;
	}

	public void paint(Graphics graphics) {
		graphics.setColor(new Color(16777215));
		graphics.fillRect(0, 0, 456, 246);

		for (int i = 0; i < 256; i++) {
			int j = this.field_13845[i + this.field_13848 & 0xFF];
			graphics.setColor(new Color(j + 28 << 16));
			graphics.fillRect(i, 100 - j, 1, j);
		}

		graphics.setColor(Color.BLACK);

		for (int i = 0; i < this.field_13847.length; i++) {
			String string = this.field_13847[i];
			if (string != null) {
				graphics.drawString(string, 32, 116 + i * 16);
			}
		}
	}

	public void method_16751() {
		this.field_16858.stop();
	}
}
