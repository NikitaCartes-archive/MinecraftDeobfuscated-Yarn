package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1792 implements class_1935 {
	public static final Map<class_2248, class_1792> field_8003 = Maps.<class_2248, class_1792>newHashMap();
	private static final class_1800 field_8010 = (arg, arg2, arg3) -> arg.method_7986() ? 1.0F : 0.0F;
	private static final class_1800 field_8000 = (arg, arg2, arg3) -> class_3532.method_15363((float)arg.method_7919() / (float)arg.method_7936(), 0.0F, 1.0F);
	private static final class_1800 field_8011 = (arg, arg2, arg3) -> arg3 != null && arg3.method_6068() != class_1306.field_6183 ? 1.0F : 0.0F;
	private static final class_1800 field_8007 = (arg, arg2, arg3) -> arg3 instanceof class_1657
			? ((class_1657)arg3).method_7357().method_7905(arg.method_7909(), 0.0F)
			: 0.0F;
	private static final class_1800 field_8002 = (arg, arg2, arg3) -> arg.method_7985() ? (float)arg.method_7969().method_10550("CustomModelData") : 0.0F;
	protected static final UUID field_8006 = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID field_8001 = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final Random field_8005 = new Random();
	private final Map<class_2960, class_1800> field_8015 = Maps.<class_2960, class_1800>newHashMap();
	protected final class_1761 field_8004;
	private final class_1814 field_8009;
	private final int field_8013;
	private final int field_8012;
	private final class_1792 field_8008;
	@Nullable
	private String field_8014;
	@Nullable
	private final class_4174 field_18672;

	public static int method_7880(class_1792 arg) {
		return arg == null ? 0 : class_2378.field_11142.method_10249(arg);
	}

	public static class_1792 method_7875(int i) {
		return class_2378.field_11142.method_10200(i);
	}

	@Deprecated
	public static class_1792 method_7867(class_2248 arg) {
		return (class_1792)field_8003.getOrDefault(arg, class_1802.field_8162);
	}

	public class_1792(class_1792.class_1793 arg) {
		this.method_7863(new class_2960("lefthanded"), field_8011);
		this.method_7863(new class_2960("cooldown"), field_8007);
		this.method_7863(new class_2960("custom_model_data"), field_8002);
		this.field_8004 = arg.field_8017;
		this.field_8009 = arg.field_8016;
		this.field_8008 = arg.field_8018;
		this.field_8012 = arg.field_8019;
		this.field_8013 = arg.field_8020;
		this.field_18672 = arg.field_18673;
		if (this.field_8012 > 0) {
			this.method_7863(new class_2960("damaged"), field_8010);
			this.method_7863(new class_2960("damage"), field_8000);
		}
	}

	public void method_7852(class_1937 arg, class_1309 arg2, class_1799 arg3, int i) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1800 method_7868(class_2960 arg) {
		return (class_1800)this.field_8015.get(arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7845() {
		return !this.field_8015.isEmpty();
	}

	public boolean method_7860(class_2487 arg) {
		return false;
	}

	public boolean method_7885(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		return true;
	}

	@Override
	public class_1792 method_8389() {
		return this;
	}

	public final void method_7863(class_2960 arg, class_1800 arg2) {
		this.field_8015.put(arg, arg2);
	}

	public class_1269 method_7884(class_1838 arg) {
		return class_1269.field_5811;
	}

	public float method_7865(class_1799 arg, class_2680 arg2) {
		return 1.0F;
	}

	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		if (this.method_19263()) {
			class_1799 lv = arg2.method_5998(arg3);
			if (arg2.method_7332(this.method_19264().method_19233())) {
				arg2.method_6019(arg3);
				return new class_1271<>(class_1269.field_5812, lv);
			} else {
				return new class_1271<>(class_1269.field_5814, lv);
			}
		} else {
			return new class_1271<>(class_1269.field_5811, arg2.method_5998(arg3));
		}
	}

	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		return this.method_19263() ? arg3.method_18866(arg2, arg) : arg;
	}

	public final int method_7882() {
		return this.field_8013;
	}

	public final int method_7841() {
		return this.field_8012;
	}

	public boolean method_7846() {
		return this.field_8012 > 0;
	}

	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		return false;
	}

	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		return false;
	}

	public boolean method_7856(class_2680 arg) {
		return false;
	}

	public boolean method_7847(class_1799 arg, class_1657 arg2, class_1309 arg3, class_1268 arg4) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_7848() {
		return new class_2588(this.method_7876());
	}

	protected String method_7869() {
		if (this.field_8014 == null) {
			this.field_8014 = class_156.method_646("item", class_2378.field_11142.method_10221(this));
		}

		return this.field_8014;
	}

	public String method_7876() {
		return this.method_7869();
	}

	public String method_7866(class_1799 arg) {
		return this.method_7876();
	}

	public boolean method_7887() {
		return true;
	}

	@Nullable
	public final class_1792 method_7858() {
		return this.field_8008;
	}

	public boolean method_7857() {
		return this.field_8008 != null;
	}

	public void method_7888(class_1799 arg, class_1937 arg2, class_1297 arg3, int i, boolean bl) {
	}

	public void method_7843(class_1799 arg, class_1937 arg2, class_1657 arg3) {
	}

	public boolean method_16698() {
		return false;
	}

	public class_1839 method_7853(class_1799 arg) {
		return arg.method_7909().method_19263() ? class_1839.field_8950 : class_1839.field_8952;
	}

	public int method_7881(class_1799 arg) {
		if (arg.method_7909().method_19263()) {
			return this.method_19264().method_19234() ? 16 : 32;
		} else {
			return 0;
		}
	}

	public void method_7840(class_1799 arg, class_1937 arg2, class_1309 arg3, int i) {
	}

	@Environment(EnvType.CLIENT)
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
	}

	public class_2561 method_7864(class_1799 arg) {
		return new class_2588(this.method_7866(arg));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7886(class_1799 arg) {
		return arg.method_7942();
	}

	public class_1814 method_7862(class_1799 arg) {
		if (!arg.method_7942()) {
			return this.field_8009;
		} else {
			switch (this.field_8009) {
				case field_8906:
				case field_8907:
					return class_1814.field_8903;
				case field_8903:
					return class_1814.field_8904;
				case field_8904:
				default:
					return this.field_8009;
			}
		}
	}

	public boolean method_7870(class_1799 arg) {
		return this.method_7882() == 1 && this.method_7846();
	}

	protected static class_239 method_7872(class_1937 arg, class_1657 arg2, class_3959.class_242 arg3) {
		float f = arg2.field_5965;
		float g = arg2.field_6031;
		class_243 lv = arg2.method_5836(1.0F);
		float h = class_3532.method_15362(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = class_3532.method_15374(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -class_3532.method_15362(-f * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374(-f * (float) (Math.PI / 180.0));
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		class_243 lv2 = lv.method_1031((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
		return arg.method_17742(new class_3959(lv, lv2, class_3959.class_3960.field_17559, arg3, arg2));
	}

	public int method_7837() {
		return 0;
	}

	public void method_7850(class_1761 arg, class_2371<class_1799> arg2) {
		if (this.method_7877(arg)) {
			arg2.add(new class_1799(this));
		}
	}

	protected boolean method_7877(class_1761 arg) {
		class_1761 lv = this.method_7859();
		return lv != null && (arg == class_1761.field_7915 || arg == lv);
	}

	@Nullable
	public final class_1761 method_7859() {
		return this.field_8004;
	}

	public boolean method_7878(class_1799 arg, class_1799 arg2) {
		return false;
	}

	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		return HashMultimap.create();
	}

	public boolean method_7838(class_1799 arg) {
		return arg.method_7909() == class_1802.field_8399;
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_7854() {
		return new class_1799(this);
	}

	public boolean method_7855(class_3494<class_1792> arg) {
		return arg.method_15141(this);
	}

	public boolean method_19263() {
		return this.field_18672 != null;
	}

	@Nullable
	public class_4174 method_19264() {
		return this.field_18672;
	}

	public static class class_1793 {
		private int field_8020 = 64;
		private int field_8019;
		private class_1792 field_8018;
		private class_1761 field_8017;
		private class_1814 field_8016 = class_1814.field_8906;
		private class_4174 field_18673;

		public class_1792.class_1793 method_19265(class_4174 arg) {
			this.field_18673 = arg;
			return this;
		}

		public class_1792.class_1793 method_7889(int i) {
			if (this.field_8019 > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.field_8020 = i;
				return this;
			}
		}

		public class_1792.class_1793 method_7898(int i) {
			return this.field_8019 == 0 ? this.method_7895(i) : this;
		}

		public class_1792.class_1793 method_7895(int i) {
			this.field_8019 = i;
			this.field_8020 = 1;
			return this;
		}

		public class_1792.class_1793 method_7896(class_1792 arg) {
			this.field_8018 = arg;
			return this;
		}

		public class_1792.class_1793 method_7892(class_1761 arg) {
			this.field_8017 = arg;
			return this;
		}

		public class_1792.class_1793 method_7894(class_1814 arg) {
			this.field_8016 = arg;
			return this;
		}
	}
}
