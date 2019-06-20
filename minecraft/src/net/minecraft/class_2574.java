package net.minecraft;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_2574 extends class_2554 implements class_2566 {
	private static final Logger field_11777 = LogManager.getLogger();
	protected final boolean field_11778;
	protected final String field_11776;
	@Nullable
	protected final class_2203.class_2209 field_11779;

	@Nullable
	private static class_2203.class_2209 method_10919(String string) {
		try {
			return new class_2203().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public class_2574(String string, boolean bl) {
		this(string, method_10919(string), bl);
	}

	protected class_2574(String string, @Nullable class_2203.class_2209 arg, boolean bl) {
		this.field_11776 = string;
		this.field_11779 = arg;
		this.field_11778 = bl;
	}

	protected abstract Stream<class_2487> method_10916(class_2168 arg) throws CommandSyntaxException;

	@Override
	public String method_10851() {
		return "";
	}

	public String method_10920() {
		return this.field_11776;
	}

	public boolean method_10921() {
		return this.field_11778;
	}

	@Override
	public class_2561 method_10890(@Nullable class_2168 arg, @Nullable class_1297 arg2, int i) throws CommandSyntaxException {
		if (arg != null && this.field_11779 != null) {
			Stream<String> stream = this.method_10916(arg).flatMap(argx -> {
				try {
					return this.field_11779.method_9366(argx).stream();
				} catch (CommandSyntaxException var3) {
					return Stream.empty();
				}
			}).map(class_2520::method_10714);
			return (class_2561)(this.field_11778 ? (class_2561)stream.flatMap(string -> {
				try {
					class_2561 lv = class_2561.class_2562.method_10877(string);
					return Stream.of(class_2564.method_10881(arg, lv, arg2, i));
				} catch (Exception var5) {
					field_11777.warn("Failed to parse component: " + string, (Throwable)var5);
					return Stream.of();
				}
			}).reduce((argx, arg2x) -> argx.method_10864(", ").method_10852(arg2x)).orElse(new class_2585("")) : new class_2585(Joiner.on(", ").join(stream.iterator())));
		} else {
			return new class_2585("");
		}
	}

	public static class class_2575 extends class_2574 {
		private final String field_11780;
		@Nullable
		private final class_2267 field_16408;

		public class_2575(String string, boolean bl, String string2) {
			super(string, bl);
			this.field_11780 = string2;
			this.field_16408 = this.method_16121(this.field_11780);
		}

		@Nullable
		private class_2267 method_16121(String string) {
			try {
				return class_2262.method_9698().method_9699(new StringReader(string));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private class_2575(String string, @Nullable class_2203.class_2209 arg, boolean bl, String string2, @Nullable class_2267 arg2) {
			super(string, arg, bl);
			this.field_11780 = string2;
			this.field_16408 = arg2;
		}

		@Nullable
		public String method_10922() {
			return this.field_11780;
		}

		@Override
		public class_2561 method_10850() {
			return new class_2574.class_2575(this.field_11776, this.field_11779, this.field_11778, this.field_11780, this.field_16408);
		}

		@Override
		protected Stream<class_2487> method_10916(class_2168 arg) {
			if (this.field_16408 != null) {
				class_3218 lv = arg.method_9225();
				class_2338 lv2 = this.field_16408.method_9704(arg);
				if (lv.method_8477(lv2)) {
					class_2586 lv3 = lv.method_8321(lv2);
					if (lv3 != null) {
						return Stream.of(lv3.method_11007(new class_2487()));
					}
				}
			}

			return Stream.empty();
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof class_2574.class_2575)) {
				return false;
			} else {
				class_2574.class_2575 lv = (class_2574.class_2575)object;
				return Objects.equals(this.field_11780, lv.field_11780) && Objects.equals(this.field_11776, lv.field_11776) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='"
				+ this.field_11780
				+ '\''
				+ "path='"
				+ this.field_11776
				+ '\''
				+ ", siblings="
				+ this.field_11729
				+ ", style="
				+ this.method_10866()
				+ '}';
		}
	}

	public static class class_2576 extends class_2574 {
		private final String field_11782;
		@Nullable
		private final class_2300 field_11781;

		public class_2576(String string, boolean bl, String string2) {
			super(string, bl);
			this.field_11782 = string2;
			this.field_11781 = method_10923(string2);
		}

		@Nullable
		private static class_2300 method_10923(String string) {
			try {
				class_2303 lv = new class_2303(new StringReader(string));
				return lv.method_9882();
			} catch (CommandSyntaxException var2) {
				return null;
			}
		}

		private class_2576(String string, @Nullable class_2203.class_2209 arg, boolean bl, String string2, @Nullable class_2300 arg2) {
			super(string, arg, bl);
			this.field_11782 = string2;
			this.field_11781 = arg2;
		}

		public String method_10924() {
			return this.field_11782;
		}

		@Override
		public class_2561 method_10850() {
			return new class_2574.class_2576(this.field_11776, this.field_11779, this.field_11778, this.field_11782, this.field_11781);
		}

		@Override
		protected Stream<class_2487> method_10916(class_2168 arg) throws CommandSyntaxException {
			if (this.field_11781 != null) {
				List<? extends class_1297> list = this.field_11781.method_9816(arg);
				return list.stream().map(class_2105::method_9076);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof class_2574.class_2576)) {
				return false;
			} else {
				class_2574.class_2576 lv = (class_2574.class_2576)object;
				return Objects.equals(this.field_11782, lv.field_11782) && Objects.equals(this.field_11776, lv.field_11776) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='"
				+ this.field_11782
				+ '\''
				+ "path='"
				+ this.field_11776
				+ '\''
				+ ", siblings="
				+ this.field_11729
				+ ", style="
				+ this.method_10866()
				+ '}';
		}
	}
}
