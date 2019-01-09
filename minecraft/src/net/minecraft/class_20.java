package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_20 {
	private final class_20.class_21 field_77;
	private byte field_76;
	private byte field_80;
	private byte field_79;
	private final class_2561 field_78;

	public class_20(class_20.class_21 arg, byte b, byte c, byte d, @Nullable class_2561 arg2) {
		this.field_77 = arg;
		this.field_76 = b;
		this.field_80 = c;
		this.field_79 = d;
		this.field_78 = arg2;
	}

	@Environment(EnvType.CLIENT)
	public byte method_92() {
		return this.field_77.method_98();
	}

	public class_20.class_21 method_93() {
		return this.field_77;
	}

	public byte method_90() {
		return this.field_76;
	}

	public byte method_91() {
		return this.field_80;
	}

	public byte method_89() {
		return this.field_79;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_94() {
		return this.field_77.method_95();
	}

	@Nullable
	public class_2561 method_88() {
		return this.field_78;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_20)) {
			return false;
		} else {
			class_20 lv = (class_20)object;
			if (this.field_77 != lv.field_77) {
				return false;
			} else if (this.field_79 != lv.field_79) {
				return false;
			} else if (this.field_76 != lv.field_76) {
				return false;
			} else {
				return this.field_80 != lv.field_80 ? false : Objects.equals(this.field_78, lv.field_78);
			}
		}
	}

	public int hashCode() {
		int i = this.field_77.method_98();
		i = 31 * i + this.field_76;
		i = 31 * i + this.field_80;
		i = 31 * i + this.field_79;
		return 31 * i + Objects.hashCode(this.field_78);
	}

	public static enum class_21 {
		field_91(false),
		field_95(true),
		field_89(false),
		field_83(false),
		field_84(true),
		field_85(true),
		field_86(false),
		field_87(false),
		field_88(true, 5393476),
		field_98(true, 3830373),
		field_96(true),
		field_92(true),
		field_97(true),
		field_90(true),
		field_93(true),
		field_94(true),
		field_100(true),
		field_101(true),
		field_107(true),
		field_108(true),
		field_104(true),
		field_105(true),
		field_106(true),
		field_102(true),
		field_99(true),
		field_103(true),
		field_110(true);

		private final byte field_81 = (byte)this.ordinal();
		private final boolean field_111;
		private final int field_82;

		private class_21(boolean bl) {
			this(bl, -1);
		}

		private class_21(boolean bl, int j) {
			this.field_111 = bl;
			this.field_82 = j;
		}

		public byte method_98() {
			return this.field_81;
		}

		@Environment(EnvType.CLIENT)
		public boolean method_95() {
			return this.field_111;
		}

		public boolean method_97() {
			return this.field_82 >= 0;
		}

		public int method_96() {
			return this.field_82;
		}

		public static class_20.class_21 method_99(byte b) {
			return values()[class_3532.method_15340(b, 0, values().length - 1)];
		}
	}
}
