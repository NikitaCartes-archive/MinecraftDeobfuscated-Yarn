package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_2588 extends class_2554 implements class_2566 {
	private static final class_2477 field_11870 = new class_2477();
	private static final class_2477 field_11874 = class_2477.method_10517();
	private final String field_11876;
	private final Object[] field_11875;
	private final Object field_11873 = new Object();
	private long field_11871 = -1L;
	protected final List<class_2561> field_11877 = Lists.<class_2561>newArrayList();
	public static final Pattern field_11872 = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public class_2588(String string, Object... objects) {
		this.field_11876 = string;
		this.field_11875 = objects;

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof class_2561) {
				class_2561 lv = ((class_2561)object).method_10853();
				this.field_11875[i] = lv;
				lv.method_10866().method_10985(this.method_10866());
			} else if (object == null) {
				this.field_11875[i] = "null";
			}
		}
	}

	@VisibleForTesting
	synchronized void method_11025() {
		synchronized (this.field_11873) {
			long l = field_11874.method_10519();
			if (l == this.field_11871) {
				return;
			}

			this.field_11871 = l;
			this.field_11877.clear();
		}

		try {
			this.method_11024(field_11874.method_10520(this.field_11876));
		} catch (class_2590 var6) {
			this.field_11877.clear();

			try {
				this.method_11024(field_11870.method_10520(this.field_11876));
			} catch (class_2590 var5) {
				throw var6;
			}
		}
	}

	protected void method_11024(String string) {
		Matcher matcher = field_11872.matcher(string);

		try {
			int i = 0;
			int j = 0;

			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					class_2561 lv = new class_2585(String.format(string.substring(j, k)));
					lv.method_10866().method_10985(this.method_10866());
					this.field_11877.add(lv);
				}

				String string2 = matcher.group(2);
				String string3 = string.substring(k, l);
				if ("%".equals(string2) && "%%".equals(string3)) {
					class_2561 lv2 = new class_2585("%");
					lv2.method_10866().method_10985(this.method_10866());
					this.field_11877.add(lv2);
				} else {
					if (!"s".equals(string2)) {
						throw new class_2590(this, "Unsupported format: '" + string3 + "'");
					}

					String string4 = matcher.group(1);
					int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;
					if (m < this.field_11875.length) {
						this.field_11877.add(this.method_11021(m));
					}
				}

				j = l;
			}

			if (j < string.length()) {
				class_2561 lv3 = new class_2585(String.format(string.substring(j)));
				lv3.method_10866().method_10985(this.method_10866());
				this.field_11877.add(lv3);
			}
		} catch (IllegalFormatException var11) {
			throw new class_2590(this, var11);
		}
	}

	private class_2561 method_11021(int i) {
		if (i >= this.field_11875.length) {
			throw new class_2590(this, i);
		} else {
			Object object = this.field_11875[i];
			class_2561 lv;
			if (object instanceof class_2561) {
				lv = (class_2561)object;
			} else {
				lv = new class_2585(object == null ? "null" : object.toString());
				lv.method_10866().method_10985(this.method_10866());
			}

			return lv;
		}
	}

	@Override
	public class_2561 method_10862(class_2583 arg) {
		super.method_10862(arg);

		for (Object object : this.field_11875) {
			if (object instanceof class_2561) {
				((class_2561)object).method_10866().method_10985(this.method_10866());
			}
		}

		if (this.field_11871 > -1L) {
			for (class_2561 lv : this.field_11877) {
				lv.method_10866().method_10985(arg);
			}
		}

		return this;
	}

	@Override
	public Stream<class_2561> method_10865() {
		this.method_11025();
		return Streams.concat(this.field_11877.stream(), this.field_11729.stream()).flatMap(class_2561::method_10865);
	}

	@Override
	public String method_10851() {
		this.method_11025();
		StringBuilder stringBuilder = new StringBuilder();

		for (class_2561 lv : this.field_11877) {
			stringBuilder.append(lv.method_10851());
		}

		return stringBuilder.toString();
	}

	public class_2588 method_11020() {
		Object[] objects = new Object[this.field_11875.length];

		for (int i = 0; i < this.field_11875.length; i++) {
			if (this.field_11875[i] instanceof class_2561) {
				objects[i] = ((class_2561)this.field_11875[i]).method_10853();
			} else {
				objects[i] = this.field_11875[i];
			}
		}

		return new class_2588(this.field_11876, objects);
	}

	@Override
	public class_2561 method_10890(@Nullable class_2168 arg, @Nullable class_1297 arg2) throws CommandSyntaxException {
		Object[] objects = new Object[this.field_11875.length];

		for (int i = 0; i < objects.length; i++) {
			Object object = this.field_11875[i];
			if (object instanceof class_2561) {
				objects[i] = class_2564.method_10881(arg, (class_2561)object, arg2);
			} else {
				objects[i] = object;
			}
		}

		return new class_2588(this.field_11876, objects);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2588)) {
			return false;
		} else {
			class_2588 lv = (class_2588)object;
			return Arrays.equals(this.field_11875, lv.field_11875) && this.field_11876.equals(lv.field_11876) && super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		int i = super.hashCode();
		i = 31 * i + this.field_11876.hashCode();
		return 31 * i + Arrays.hashCode(this.field_11875);
	}

	@Override
	public String toString() {
		return "TranslatableComponent{key='"
			+ this.field_11876
			+ '\''
			+ ", args="
			+ Arrays.toString(this.field_11875)
			+ ", siblings="
			+ this.field_11729
			+ ", style="
			+ this.method_10866()
			+ '}';
	}

	public String method_11022() {
		return this.field_11876;
	}

	public Object[] method_11023() {
		return this.field_11875;
	}
}
