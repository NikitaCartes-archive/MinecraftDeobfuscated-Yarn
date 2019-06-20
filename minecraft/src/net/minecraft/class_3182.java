package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3182 extends JComponent {
	private static final Font field_13837 = new Font("Monospaced", 0, 12);
	private static final Logger field_13840 = LogManager.getLogger();
	private final class_3176 field_13839;
	private Thread field_13838;
	private final Collection<Runnable> field_16855 = Lists.<Runnable>newArrayList();
	private final AtomicBoolean field_16854 = new AtomicBoolean();

	public static class_3182 method_13969(class_3176 arg) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception var3) {
		}

		final JFrame jFrame = new JFrame("Minecraft server");
		final class_3182 lv = new class_3182(arg);
		jFrame.setDefaultCloseOperation(2);
		jFrame.add(lv);
		jFrame.pack();
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				if (!lv.field_16854.getAndSet(true)) {
					jFrame.setTitle("Minecraft server - shutting down!");
					arg.method_3747(true);
					lv.method_16747();
				}
			}
		});
		lv.method_16746(jFrame::dispose);
		lv.method_13974();
		return lv;
	}

	private class_3182(class_3176 arg) {
		this.field_13839 = arg;
		this.setPreferredSize(new Dimension(854, 480));
		this.setLayout(new BorderLayout());

		try {
			this.add(this.method_13973(), "Center");
			this.add(this.method_13976(), "West");
		} catch (Exception var3) {
			field_13840.error("Couldn't build server GUI", (Throwable)var3);
		}
	}

	public void method_16746(Runnable runnable) {
		this.field_16855.add(runnable);
	}

	private JComponent method_13976() {
		JPanel jPanel = new JPanel(new BorderLayout());
		class_3186 lv = new class_3186(this.field_13839);
		this.field_16855.add(lv::method_16751);
		jPanel.add(lv, "North");
		jPanel.add(this.method_13975(), "Center");
		jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
		return jPanel;
	}

	private JComponent method_13975() {
		JList<?> jList = new class_3184(this.field_13839);
		JScrollPane jScrollPane = new JScrollPane(jList, 22, 30);
		jScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
		return jScrollPane;
	}

	private JComponent method_13973() {
		JPanel jPanel = new JPanel(new BorderLayout());
		JTextArea jTextArea = new JTextArea();
		JScrollPane jScrollPane = new JScrollPane(jTextArea, 22, 30);
		jTextArea.setEditable(false);
		jTextArea.setFont(field_13837);
		JTextField jTextField = new JTextField();
		jTextField.addActionListener(actionEvent -> {
			String string = jTextField.getText().trim();
			if (!string.isEmpty()) {
				this.field_13839.method_13947(string, this.field_13839.method_3739());
			}

			jTextField.setText("");
		});
		jTextArea.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent focusEvent) {
			}
		});
		jPanel.add(jScrollPane, "Center");
		jPanel.add(jTextField, "South");
		jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
		this.field_13838 = new Thread(() -> {
			String string;
			while ((string = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
				this.method_13970(jTextArea, jScrollPane, string);
			}
		});
		this.field_13838.setUncaughtExceptionHandler(new class_140(field_13840));
		this.field_13838.setDaemon(true);
		return jPanel;
	}

	public void method_13974() {
		this.field_13838.start();
	}

	public void method_16750() {
		if (!this.field_16854.getAndSet(true)) {
			this.method_16747();
		}
	}

	private void method_16747() {
		this.field_16855.forEach(Runnable::run);
	}

	public void method_13970(JTextArea jTextArea, JScrollPane jScrollPane, String string) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> this.method_13970(jTextArea, jScrollPane, string));
		} else {
			Document document = jTextArea.getDocument();
			JScrollBar jScrollBar = jScrollPane.getVerticalScrollBar();
			boolean bl = false;
			if (jScrollPane.getViewport().getView() == jTextArea) {
				bl = (double)jScrollBar.getValue() + jScrollBar.getSize().getHeight() + (double)(field_13837.getSize() * 4) > (double)jScrollBar.getMaximum();
			}

			try {
				document.insertString(document.getLength(), string, null);
			} catch (BadLocationException var8) {
			}

			if (bl) {
				jScrollBar.setValue(Integer.MAX_VALUE);
			}
		}
	}
}
