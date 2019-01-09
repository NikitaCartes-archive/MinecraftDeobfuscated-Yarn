package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1291 {
	private final Map<class_1320, class_1322> field_5885 = Maps.<class_1320, class_1322>newHashMap();
	private final boolean field_5887;
	private final int field_5886;
	@Nullable
	private String field_5883;
	private int field_5884 = -1;
	private double field_5888;
	private boolean field_5882;

	@Nullable
	public static class_1291 method_5569(int i) {
		return class_2378.field_11159.method_10200(i);
	}

	public static int method_5554(class_1291 arg) {
		return class_2378.field_11159.method_10249(arg);
	}

	protected class_1291(boolean bl, int i) {
		this.field_5887 = bl;
		if (bl) {
			this.field_5888 = 0.5;
		} else {
			this.field_5888 = 1.0;
		}

		this.field_5886 = i;
	}

	protected class_1291 method_5558(int i, int j) {
		this.field_5884 = i + j * 12;
		return this;
	}

	public void method_5572(class_1309 arg, int i) {
		if (this == class_1294.field_5924) {
			if (arg.method_6032() < arg.method_6063()) {
				arg.method_6025(1.0F);
			}
		} else if (this == class_1294.field_5899) {
			if (arg.method_6032() > 1.0F) {
				arg.method_5643(class_1282.field_5846, 1.0F);
			}
		} else if (this == class_1294.field_5920) {
			arg.method_5643(class_1282.field_5850, 1.0F);
		} else if (this == class_1294.field_5903 && arg instanceof class_1657) {
			((class_1657)arg).method_7322(0.005F * (float)(i + 1));
		} else if (this == class_1294.field_5922 && arg instanceof class_1657) {
			if (!arg.field_6002.field_9236) {
				((class_1657)arg).method_7344().method_7585(i + 1, 1.0F);
			}
		} else if ((this != class_1294.field_5915 || arg.method_5999()) && (this != class_1294.field_5921 || !arg.method_5999())) {
			if (this == class_1294.field_5921 && !arg.method_5999() || this == class_1294.field_5915 && arg.method_5999()) {
				arg.method_5643(class_1282.field_5846, (float)(6 << i));
			}
		} else {
			arg.method_6025((float)Math.max(4 << i, 0));
		}
	}

	public void method_5564(@Nullable class_1297 arg, @Nullable class_1297 arg2, class_1309 arg3, int i, double d) {
		if ((this != class_1294.field_5915 || arg3.method_5999()) && (this != class_1294.field_5921 || !arg3.method_5999())) {
			if (this == class_1294.field_5921 && !arg3.method_5999() || this == class_1294.field_5915 && arg3.method_5999()) {
				int j = (int)(d * (double)(6 << i) + 0.5);
				if (arg == null) {
					arg3.method_5643(class_1282.field_5846, (float)j);
				} else {
					arg3.method_5643(class_1282.method_5536(arg, arg2), (float)j);
				}
			} else {
				this.method_5572(arg3, i);
			}
		} else {
			int j = (int)(d * (double)(4 << i) + 0.5);
			arg3.method_6025((float)j);
		}
	}

	public boolean method_5552(int i, int j) {
		if (this == class_1294.field_5924) {
			int k = 50 >> j;
			return k > 0 ? i % k == 0 : true;
		} else if (this == class_1294.field_5899) {
			int k = 25 >> j;
			return k > 0 ? i % k == 0 : true;
		} else if (this == class_1294.field_5920) {
			int k = 40 >> j;
			return k > 0 ? i % k == 0 : true;
		} else {
			return this == class_1294.field_5903;
		}
	}

	public boolean method_5561() {
		return false;
	}

	protected String method_5559() {
		if (this.field_5883 == null) {
			this.field_5883 = class_156.method_646("effect", class_2378.field_11159.method_10221(this));
		}

		return this.field_5883;
	}

	public String method_5567() {
		return this.method_5559();
	}

	public class_2561 method_5560() {
		return new class_2588(this.method_5567());
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5568() {
		return this.field_5884 >= 0;
	}

	@Environment(EnvType.CLIENT)
	public int method_5553() {
		return this.field_5884;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5570() {
		return this.field_5887;
	}

	protected class_1291 method_5571(double d) {
		this.field_5888 = d;
		return this;
	}

	public int method_5556() {
		return this.field_5886;
	}

	public class_1291 method_5566(class_1320 arg, String string, double d, class_1322.class_1323 arg2) {
		class_1322 lv = new class_1322(UUID.fromString(string), this::method_5567, d, arg2);
		this.field_5885.put(arg, lv);
		return this;
	}

	@Environment(EnvType.CLIENT)
	public Map<class_1320, class_1322> method_5565() {
		return this.field_5885;
	}

	public void method_5562(class_1309 arg, class_1325 arg2, int i) {
		for (Entry<class_1320, class_1322> entry : this.field_5885.entrySet()) {
			class_1324 lv = arg2.method_6205((class_1320)entry.getKey());
			if (lv != null) {
				lv.method_6202((class_1322)entry.getValue());
			}
		}
	}

	public void method_5555(class_1309 arg, class_1325 arg2, int i) {
		for (Entry<class_1320, class_1322> entry : this.field_5885.entrySet()) {
			class_1324 lv = arg2.method_6205((class_1320)entry.getKey());
			if (lv != null) {
				class_1322 lv2 = (class_1322)entry.getValue();
				lv.method_6202(lv2);
				lv.method_6197(new class_1322(lv2.method_6189(), this.method_5567() + " " + i, this.method_5563(i, lv2), lv2.method_6182()));
			}
		}
	}

	public double method_5563(int i, class_1322 arg) {
		return arg.method_6186() * (double)(i + 1);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5573() {
		return this.field_5882;
	}

	public class_1291 method_5557() {
		this.field_5882 = true;
		return this;
	}
}
