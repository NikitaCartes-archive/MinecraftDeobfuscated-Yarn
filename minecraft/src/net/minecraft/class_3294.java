package net.minecraft;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3294 implements class_3300 {
	private static final Logger field_14285 = LogManager.getLogger();
	protected final List<class_3262> field_14283 = Lists.<class_3262>newArrayList();
	private final class_3264 field_14284;

	public class_3294(class_3264 arg) {
		this.field_14284 = arg;
	}

	public void method_14475(class_3262 arg) {
		this.field_14283.add(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> method_14487() {
		return Collections.emptySet();
	}

	@Override
	public class_3298 method_14486(class_2960 arg) throws IOException {
		this.method_14472(arg);
		class_3262 lv = null;
		class_2960 lv2 = method_14473(arg);

		for (int i = this.field_14283.size() - 1; i >= 0; i--) {
			class_3262 lv3 = (class_3262)this.field_14283.get(i);
			if (lv == null && lv3.method_14411(this.field_14284, lv2)) {
				lv = lv3;
			}

			if (lv3.method_14411(this.field_14284, arg)) {
				InputStream inputStream = null;
				if (lv != null) {
					inputStream = this.method_14476(lv2, lv);
				}

				return new class_3306(lv3.method_14409(), arg, this.method_14476(arg, lv3), inputStream);
			}
		}

		throw new FileNotFoundException(arg.toString());
	}

	protected InputStream method_14476(class_2960 arg, class_3262 arg2) throws IOException {
		InputStream inputStream = arg2.method_14405(this.field_14284, arg);
		return (InputStream)(field_14285.isDebugEnabled() ? new class_3294.class_3295(inputStream, arg, arg2.method_14409()) : inputStream);
	}

	private void method_14472(class_2960 arg) throws IOException {
		if (arg.method_12832().contains("..")) {
			throw new IOException("Invalid relative path to resource: " + arg);
		}
	}

	@Override
	public List<class_3298> method_14489(class_2960 arg) throws IOException {
		this.method_14472(arg);
		List<class_3298> list = Lists.<class_3298>newArrayList();
		class_2960 lv = method_14473(arg);

		for (class_3262 lv2 : this.field_14283) {
			if (lv2.method_14411(this.field_14284, arg)) {
				InputStream inputStream = lv2.method_14411(this.field_14284, lv) ? this.method_14476(lv, lv2) : null;
				list.add(new class_3306(lv2.method_14409(), arg, this.method_14476(arg, lv2), inputStream));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(arg.toString());
		} else {
			return list;
		}
	}

	@Override
	public Collection<class_2960> method_14488(String string, Predicate<String> predicate) {
		List<class_2960> list = Lists.<class_2960>newArrayList();

		for (class_3262 lv : this.field_14283) {
			list.addAll(lv.method_14408(this.field_14284, string, Integer.MAX_VALUE, predicate));
		}

		Collections.sort(list);
		return list;
	}

	static class_2960 method_14473(class_2960 arg) {
		return new class_2960(arg.method_12836(), arg.method_12832() + ".mcmeta");
	}

	static class class_3295 extends InputStream {
		private final InputStream field_14286;
		private final String field_14288;
		private boolean field_14287;

		public class_3295(InputStream inputStream, class_2960 arg, String string) {
			this.field_14286 = inputStream;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
			this.field_14288 = "Leaked resource: '" + arg + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
		}

		public void close() throws IOException {
			this.field_14286.close();
			this.field_14287 = true;
		}

		protected void finalize() throws Throwable {
			if (!this.field_14287) {
				class_3294.field_14285.warn(this.field_14288);
			}

			super.finalize();
		}

		public int read() throws IOException {
			return this.field_14286.read();
		}
	}
}
