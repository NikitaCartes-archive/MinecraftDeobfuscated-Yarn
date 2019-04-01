package net.minecraft;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3178 implements Runnable {
	private static final Logger field_13825 = LogManager.getLogger();
	private final class_3176 field_13823;
	private final long field_13824;

	public class_3178(class_3176 arg) {
		this.field_13823 = arg;
		this.field_13824 = arg.method_13944();
	}

	public void run() {
		while (this.field_13823.method_3806()) {
			long l = this.field_13823.method_3826();
			long m = class_156.method_658();
			long n = m - l;
			if (n > this.field_13824) {
				field_13825.fatal(
					"A single server tick took {} seconds (should be max {})",
					String.format(Locale.ROOT, "%.2f", (float)n / 1000.0F),
					String.format(Locale.ROOT, "%.2f", 0.05F)
				);
				field_13825.fatal("Considering it to be crashed, server will forcibly shutdown.");
				ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
				ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
				StringBuilder stringBuilder = new StringBuilder();
				Error error = new Error();

				for (ThreadInfo threadInfo : threadInfos) {
					if (threadInfo.getThreadId() == this.field_13823.method_3777().getId()) {
						error.setStackTrace(threadInfo.getStackTrace());
					}

					stringBuilder.append(threadInfo);
					stringBuilder.append("\n");
				}

				class_128 lv = new class_128("Watching Server", error);
				this.field_13823.method_3859(lv);
				class_129 lv2 = lv.method_562("Thread Dump");
				lv2.method_578("Threads", stringBuilder);
				File file = new File(
					new File(this.field_13823.method_3831(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
				);
				if (lv.method_569(file)) {
					field_13825.error("This crash report has been saved to: {}", file.getAbsolutePath());
				} else {
					field_13825.error("We were unable to save this crash report to disk.");
				}

				this.method_13954();
			}

			try {
				Thread.sleep(l + this.field_13824 - m);
			} catch (InterruptedException var15) {
			}
		}
	}

	private void method_13954() {
		try {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					Runtime.getRuntime().halt(1);
				}
			}, 10000L);
			System.exit(1);
		} catch (Throwable var2) {
			Runtime.getRuntime().halt(1);
		}
	}
}
