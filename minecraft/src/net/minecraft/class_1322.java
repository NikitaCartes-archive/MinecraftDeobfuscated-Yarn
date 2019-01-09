package net.minecraft;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class class_1322 {
	private final double field_6323;
	private final class_1322.class_1323 field_6324;
	private final Supplier<String> field_6326;
	private final UUID field_6327;
	private boolean field_6325 = true;

	public class_1322(String string, double d, class_1322.class_1323 arg) {
		this(class_3532.method_15378(ThreadLocalRandom.current()), (Supplier<String>)(() -> string), d, arg);
	}

	public class_1322(UUID uUID, String string, double d, class_1322.class_1323 arg) {
		this(uUID, (Supplier<String>)(() -> string), d, arg);
	}

	public class_1322(UUID uUID, Supplier<String> supplier, double d, class_1322.class_1323 arg) {
		this.field_6327 = uUID;
		this.field_6326 = supplier;
		this.field_6323 = d;
		this.field_6324 = arg;
	}

	public UUID method_6189() {
		return this.field_6327;
	}

	public String method_6185() {
		return (String)this.field_6326.get();
	}

	public class_1322.class_1323 method_6182() {
		return this.field_6324;
	}

	public double method_6186() {
		return this.field_6323;
	}

	public boolean method_6188() {
		return this.field_6325;
	}

	public class_1322 method_6187(boolean bl) {
		this.field_6325 = bl;
		return this;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_1322 lv = (class_1322)object;
			return Objects.equals(this.field_6327, lv.field_6327);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_6327 != null ? this.field_6327.hashCode() : 0;
	}

	public String toString() {
		return "AttributeModifier{amount="
			+ this.field_6323
			+ ", operation="
			+ this.field_6324
			+ ", name='"
			+ (String)this.field_6326.get()
			+ '\''
			+ ", id="
			+ this.field_6327
			+ ", serialize="
			+ this.field_6325
			+ '}';
	}

	public static enum class_1323 {
		field_6328(0),
		field_6330(1),
		field_6331(2);

		private static final class_1322.class_1323[] field_6332 = new class_1322.class_1323[]{field_6328, field_6330, field_6331};
		private final int field_6329;

		private class_1323(int j) {
			this.field_6329 = j;
		}

		public int method_6191() {
			return this.field_6329;
		}

		public static class_1322.class_1323 method_6190(int i) {
			if (i >= 0 && i < field_6332.length) {
				return field_6332[i];
			} else {
				throw new IllegalArgumentException("No operation with value " + i);
			}
		}
	}
}
