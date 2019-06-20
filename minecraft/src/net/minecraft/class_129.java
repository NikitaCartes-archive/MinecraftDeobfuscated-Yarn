package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_129 {
	private final class_128 field_1095;
	private final String field_1096;
	private final List<class_129.class_130> field_1094 = Lists.<class_129.class_130>newArrayList();
	private StackTraceElement[] field_1097 = new StackTraceElement[0];

	public class_129(class_128 arg, String string) {
		this.field_1095 = arg;
		this.field_1096 = string;
	}

	@Environment(EnvType.CLIENT)
	public static String method_583(double d, double e, double f) {
		return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, e, f, method_582(new class_2338(d, e, f)));
	}

	public static String method_582(class_2338 arg) {
		return method_581(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public static String method_581(int i, int j, int k) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
		} catch (Throwable var16) {
			stringBuilder.append("(Error finding world loc)");
		}

		stringBuilder.append(", ");

		try {
			int l = i >> 4;
			int m = k >> 4;
			int n = i & 15;
			int o = j >> 4;
			int p = k & 15;
			int q = l << 4;
			int r = m << 4;
			int s = (l + 1 << 4) - 1;
			int t = (m + 1 << 4) - 1;
			stringBuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n, o, p, l, m, q, r, s, t));
		} catch (Throwable var15) {
			stringBuilder.append("(Error finding chunk loc)");
		}

		stringBuilder.append(", ");

		try {
			int l = i >> 9;
			int m = k >> 9;
			int n = l << 5;
			int o = m << 5;
			int p = (l + 1 << 5) - 1;
			int q = (m + 1 << 5) - 1;
			int r = l << 9;
			int s = m << 9;
			int t = (l + 1 << 9) - 1;
			int u = (m + 1 << 9) - 1;
			stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", l, m, n, o, p, q, r, s, t, u));
		} catch (Throwable var14) {
			stringBuilder.append("(Error finding world loc)");
		}

		return stringBuilder.toString();
	}

	public void method_577(String string, class_133<String> arg) {
		try {
			this.method_578(string, arg.call());
		} catch (Throwable var4) {
			this.method_585(string, var4);
		}
	}

	public void method_578(String string, Object object) {
		this.field_1094.add(new class_129.class_130(string, object));
	}

	public void method_585(String string, Throwable throwable) {
		this.method_578(string, throwable);
	}

	public int method_579(int i) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length <= 0) {
			return 0;
		} else {
			this.field_1097 = new StackTraceElement[stackTraceElements.length - 3 - i];
			System.arraycopy(stackTraceElements, 3 + i, this.field_1097, 0, this.field_1097.length);
			return this.field_1097.length;
		}
	}

	public boolean method_584(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
		if (this.field_1097.length != 0 && stackTraceElement != null) {
			StackTraceElement stackTraceElement3 = this.field_1097[0];
			if (stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod()
				&& stackTraceElement3.getClassName().equals(stackTraceElement.getClassName())
				&& stackTraceElement3.getFileName().equals(stackTraceElement.getFileName())
				&& stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName())) {
				if (stackTraceElement2 != null != this.field_1097.length > 1) {
					return false;
				} else if (stackTraceElement2 != null && !this.field_1097[1].equals(stackTraceElement2)) {
					return false;
				} else {
					this.field_1097[0] = stackTraceElement;
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void method_580(int i) {
		StackTraceElement[] stackTraceElements = new StackTraceElement[this.field_1097.length - i];
		System.arraycopy(this.field_1097, 0, stackTraceElements, 0, stackTraceElements.length);
		this.field_1097 = stackTraceElements;
	}

	public void method_574(StringBuilder stringBuilder) {
		stringBuilder.append("-- ").append(this.field_1096).append(" --\n");
		stringBuilder.append("Details:");

		for (class_129.class_130 lv : this.field_1094) {
			stringBuilder.append("\n\t");
			stringBuilder.append(lv.method_588());
			stringBuilder.append(": ");
			stringBuilder.append(lv.method_587());
		}

		if (this.field_1097 != null && this.field_1097.length > 0) {
			stringBuilder.append("\nStacktrace:");

			for (StackTraceElement stackTraceElement : this.field_1097) {
				stringBuilder.append("\n\tat ");
				stringBuilder.append(stackTraceElement);
			}
		}
	}

	public StackTraceElement[] method_575() {
		return this.field_1097;
	}

	public static void method_586(class_129 arg, class_2338 arg2, @Nullable class_2680 arg3) {
		if (arg3 != null) {
			arg.method_577("Block", arg3::toString);
		}

		arg.method_577("Block location", () -> method_582(arg2));
	}

	static class class_130 {
		private final String field_1098;
		private final String field_1099;

		public class_130(String string, Object object) {
			this.field_1098 = string;
			if (object == null) {
				this.field_1099 = "~~NULL~~";
			} else if (object instanceof Throwable) {
				Throwable throwable = (Throwable)object;
				this.field_1099 = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
			} else {
				this.field_1099 = object.toString();
			}
		}

		public String method_588() {
			return this.field_1098;
		}

		public String method_587() {
			return this.field_1099;
		}
	}
}
