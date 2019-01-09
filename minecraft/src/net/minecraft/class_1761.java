package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1761 {
	public static final class_1761[] field_7921 = new class_1761[12];
	public static final class_1761 field_7931 = (new class_1761(0, "buildingBlocks") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_2246.field_10104);
		}
	}).method_7739("building_blocks");
	public static final class_1761 field_7928 = new class_1761(1, "decorations") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_2246.field_10003);
		}
	};
	public static final class_1761 field_7914 = new class_1761(2, "redstone") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_1802.field_8725);
		}
	};
	public static final class_1761 field_7923 = new class_1761(3, "transportation") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_2246.field_10425);
		}
	};
	public static final class_1761 field_7932 = new class_1761(6, "misc") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_1802.field_8187);
		}
	};
	public static final class_1761 field_7915 = (new class_1761(5, "search") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_1802.field_8251);
		}
	}).method_7753("item_search.png");
	public static final class_1761 field_7922 = new class_1761(7, "food") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_1802.field_8279);
		}
	};
	public static final class_1761 field_7930 = (new class_1761(8, "tools") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_1802.field_8475);
		}
	}).method_7745(new class_1886[]{class_1886.field_9075, class_1886.field_9069, class_1886.field_9072, class_1886.field_9082});
	public static final class_1761 field_7916 = (new class_1761(9, "combat") {
			@Environment(EnvType.CLIENT)
			@Override
			public class_1799 method_7750() {
				return new class_1799(class_1802.field_8845);
			}
		})
		.method_7745(
			new class_1886[]{
				class_1886.field_9075,
				class_1886.field_9068,
				class_1886.field_9079,
				class_1886.field_9080,
				class_1886.field_9076,
				class_1886.field_9071,
				class_1886.field_9070,
				class_1886.field_9074,
				class_1886.field_9078,
				class_1886.field_9082,
				class_1886.field_9073,
				class_1886.field_9081
			}
		);
	public static final class_1761 field_7924 = new class_1761(10, "brewing") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8991);
		}
	};
	public static final class_1761 field_7929 = field_7932;
	public static final class_1761 field_7925 = new class_1761(4, "hotbar") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_2246.field_10504);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void method_7738(class_2371<class_1799> arg) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean method_7752() {
			return true;
		}
	};
	public static final class_1761 field_7918 = (new class_1761(11, "inventory") {
		@Environment(EnvType.CLIENT)
		@Override
		public class_1799 method_7750() {
			return new class_1799(class_2246.field_10034);
		}
	}).method_7753("inventory.png").method_7749().method_7748();
	private final int field_7933;
	private final String field_7935;
	private String field_7926;
	private String field_7919 = "items.png";
	private boolean field_7920 = true;
	private boolean field_7917 = true;
	private class_1886[] field_7927 = new class_1886[0];
	private class_1799 field_7934;

	public class_1761(int i, String string) {
		this.field_7933 = i;
		this.field_7935 = string;
		this.field_7934 = class_1799.field_8037;
		field_7921[i] = this;
	}

	@Environment(EnvType.CLIENT)
	public int method_7741() {
		return this.field_7933;
	}

	@Environment(EnvType.CLIENT)
	public String method_7746() {
		return this.field_7935;
	}

	public String method_7751() {
		return this.field_7926 == null ? this.field_7935 : this.field_7926;
	}

	@Environment(EnvType.CLIENT)
	public String method_7737() {
		return "itemGroup." + this.method_7746();
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_7747() {
		if (this.field_7934.method_7960()) {
			this.field_7934 = this.method_7750();
		}

		return this.field_7934;
	}

	@Environment(EnvType.CLIENT)
	public abstract class_1799 method_7750();

	@Environment(EnvType.CLIENT)
	public String method_7742() {
		return this.field_7919;
	}

	public class_1761 method_7753(String string) {
		this.field_7919 = string;
		return this;
	}

	public class_1761 method_7739(String string) {
		this.field_7926 = string;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7754() {
		return this.field_7917;
	}

	public class_1761 method_7748() {
		this.field_7917 = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7756() {
		return this.field_7920;
	}

	public class_1761 method_7749() {
		this.field_7920 = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public int method_7743() {
		return this.field_7933 % 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7755() {
		return this.field_7933 < 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7752() {
		return this.method_7743() == 5;
	}

	public class_1886[] method_7744() {
		return this.field_7927;
	}

	public class_1761 method_7745(class_1886... args) {
		this.field_7927 = args;
		return this;
	}

	public boolean method_7740(@Nullable class_1886 arg) {
		if (arg != null) {
			for (class_1886 lv : this.field_7927) {
				if (lv == arg) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public void method_7738(class_2371<class_1799> arg) {
		for (class_1792 lv : class_2378.field_11142) {
			lv.method_7850(this, arg);
		}
	}
}
