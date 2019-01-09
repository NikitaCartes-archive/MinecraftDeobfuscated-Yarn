package net.minecraft;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_128 {
	private static final Logger field_1091 = LogManager.getLogger();
	private final String field_1087;
	private final Throwable field_1093;
	private final class_129 field_1092 = new class_129(this, "System Details");
	private final List<class_129> field_1089 = Lists.<class_129>newArrayList();
	private File field_1090;
	private boolean field_1086 = true;
	private StackTraceElement[] field_1088 = new StackTraceElement[0];

	public class_128(String string, Throwable throwable) {
		this.field_1087 = string;
		this.field_1093 = throwable;
		this.method_559();
	}

	private void method_559() {
		this.field_1092.method_577("Minecraft Version", () -> class_155.method_16673().getName());
		this.field_1092
			.method_577("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
		this.field_1092.method_577("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
		this.field_1092
			.method_577(
				"Java VM Version", () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor")
			);
		this.field_1092.method_577("Memory", () -> {
			Runtime runtime = Runtime.getRuntime();
			long l = runtime.maxMemory();
			long m = runtime.totalMemory();
			long n = runtime.freeMemory();
			long o = l / 1024L / 1024L;
			long p = m / 1024L / 1024L;
			long q = n / 1024L / 1024L;
			return n + " bytes (" + q + " MB) / " + m + " bytes (" + p + " MB) up to " + l + " bytes (" + o + " MB)";
		});
		this.field_1092.method_577("JVM Flags", () -> {
			List<String> list = (List<String>)class_156.method_651().collect(Collectors.toList());
			return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
		});
	}

	public String method_561() {
		return this.field_1087;
	}

	public Throwable method_564() {
		return this.field_1093;
	}

	public void method_555(StringBuilder stringBuilder) {
		if ((this.field_1088 == null || this.field_1088.length <= 0) && !this.field_1089.isEmpty()) {
			this.field_1088 = ArrayUtils.subarray(((class_129)this.field_1089.get(0)).method_575(), 0, 1);
		}

		if (this.field_1088 != null && this.field_1088.length > 0) {
			stringBuilder.append("-- Head --\n");
			stringBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
			stringBuilder.append("Stacktrace:\n");

			for (StackTraceElement stackTraceElement : this.field_1088) {
				stringBuilder.append("\t").append("at ").append(stackTraceElement);
				stringBuilder.append("\n");
			}

			stringBuilder.append("\n");
		}

		for (class_129 lv : this.field_1089) {
			lv.method_574(stringBuilder);
			stringBuilder.append("\n\n");
		}

		this.field_1092.method_574(stringBuilder);
	}

	public String method_557() {
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;
		Throwable throwable = this.field_1093;
		if (throwable.getMessage() == null) {
			if (throwable instanceof NullPointerException) {
				throwable = new NullPointerException(this.field_1087);
			} else if (throwable instanceof StackOverflowError) {
				throwable = new StackOverflowError(this.field_1087);
			} else if (throwable instanceof OutOfMemoryError) {
				throwable = new OutOfMemoryError(this.field_1087);
			}

			throwable.setStackTrace(this.field_1093.getStackTrace());
		}

		String var4;
		try {
			stringWriter = new StringWriter();
			printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			var4 = stringWriter.toString();
		} finally {
			IOUtils.closeQuietly(stringWriter);
			IOUtils.closeQuietly(printWriter);
		}

		return var4;
	}

	public String method_568() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Crash Report ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(method_573());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time: ");
		stringBuilder.append(new SimpleDateFormat().format(new Date()));
		stringBuilder.append("\n");
		stringBuilder.append("Description: ");
		stringBuilder.append(this.field_1087);
		stringBuilder.append("\n\n");
		stringBuilder.append(this.method_557());
		stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

		for (int i = 0; i < 87; i++) {
			stringBuilder.append("-");
		}

		stringBuilder.append("\n\n");
		this.method_555(stringBuilder);
		return stringBuilder.toString();
	}

	@Environment(EnvType.CLIENT)
	public File method_572() {
		return this.field_1090;
	}

	public boolean method_569(File file) {
		if (this.field_1090 != null) {
			return false;
		} else {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}

			Writer writer = null;

			boolean var4;
			try {
				writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				writer.write(this.method_568());
				this.field_1090 = file;
				return true;
			} catch (Throwable var8) {
				field_1091.error("Could not save crash report to {}", file, var8);
				var4 = false;
			} finally {
				IOUtils.closeQuietly(writer);
			}

			return var4;
		}
	}

	public class_129 method_567() {
		return this.field_1092;
	}

	public class_129 method_562(String string) {
		return this.method_556(string, 1);
	}

	public class_129 method_556(String string, int i) {
		class_129 lv = new class_129(this, string);
		if (this.field_1086) {
			int j = lv.method_579(i);
			StackTraceElement[] stackTraceElements = this.field_1093.getStackTrace();
			StackTraceElement stackTraceElement = null;
			StackTraceElement stackTraceElement2 = null;
			int k = stackTraceElements.length - j;
			if (k < 0) {
				System.out.println("Negative index in crash report handler (" + stackTraceElements.length + "/" + j + ")");
			}

			if (stackTraceElements != null && 0 <= k && k < stackTraceElements.length) {
				stackTraceElement = stackTraceElements[k];
				if (stackTraceElements.length + 1 - j < stackTraceElements.length) {
					stackTraceElement2 = stackTraceElements[stackTraceElements.length + 1 - j];
				}
			}

			this.field_1086 = lv.method_584(stackTraceElement, stackTraceElement2);
			if (j > 0 && !this.field_1089.isEmpty()) {
				class_129 lv2 = (class_129)this.field_1089.get(this.field_1089.size() - 1);
				lv2.method_580(j);
			} else if (stackTraceElements != null && stackTraceElements.length >= j && 0 <= k && k < stackTraceElements.length) {
				this.field_1088 = new StackTraceElement[k];
				System.arraycopy(stackTraceElements, 0, this.field_1088, 0, this.field_1088.length);
			} else {
				this.field_1086 = false;
			}
		}

		this.field_1089.add(lv);
		return lv;
	}

	private static String method_573() {
		String[] strings = new String[]{
			"Who set us up the TNT?",
			"Everything's going to plan. No, really, that was supposed to happen.",
			"Uh... Did I do that?",
			"Oops.",
			"Why did you do that?",
			"I feel sad now :(",
			"My bad.",
			"I'm sorry, Dave.",
			"I let you down. Sorry :(",
			"On the bright side, I bought you a teddy bear!",
			"Daisy, daisy...",
			"Oh - I know what I did wrong!",
			"Hey, that tickles! Hehehe!",
			"I blame Dinnerbone.",
			"You should try our sister game, Minceraft!",
			"Don't be sad. I'll do better next time, I promise!",
			"Don't be sad, have a hug! <3",
			"I just don't know what went wrong :(",
			"Shall we play a game?",
			"Quite honestly, I wouldn't worry myself about that.",
			"I bet Cylons wouldn't have this problem.",
			"Sorry :(",
			"Surprise! Haha. Well, this is awkward.",
			"Would you like a cupcake?",
			"Hi. I'm Minecraft, and I'm a crashaholic.",
			"Ooh. Shiny.",
			"This doesn't make any sense!",
			"Why is it breaking :(",
			"Don't do that.",
			"Ouch. That hurt :(",
			"You're mean.",
			"This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
			"There are four lights!",
			"But it works on my machine."
		};

		try {
			return strings[(int)(class_156.method_648() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	public static class_128 method_560(Throwable throwable, String string) {
		while (throwable instanceof CompletionException && throwable.getCause() != null) {
			throwable = throwable.getCause();
		}

		class_128 lv;
		if (throwable instanceof class_148) {
			lv = ((class_148)throwable).method_631();
		} else {
			lv = new class_128(string, throwable);
		}

		return lv;
	}
}
