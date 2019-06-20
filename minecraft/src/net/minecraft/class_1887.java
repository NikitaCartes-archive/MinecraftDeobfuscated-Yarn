package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1887 {
	private final class_1304[] field_9086;
	private final class_1887.class_1888 field_9085;
	@Nullable
	public class_1886 field_9083;
	@Nullable
	protected String field_9084;

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1887 method_8191(int i) {
		return class_2378.field_11160.method_10200(i);
	}

	protected class_1887(class_1887.class_1888 arg, class_1886 arg2, class_1304[] args) {
		this.field_9085 = arg;
		this.field_9083 = arg2;
		this.field_9086 = args;
	}

	public Map<class_1304, class_1799> method_8185(class_1309 arg) {
		Map<class_1304, class_1799> map = Maps.newEnumMap(class_1304.class);

		for (class_1304 lv : this.field_9086) {
			class_1799 lv2 = arg.method_6118(lv);
			if (!lv2.method_7960()) {
				map.put(lv, lv2);
			}
		}

		return map;
	}

	public class_1887.class_1888 method_8186() {
		return this.field_9085;
	}

	public int method_8187() {
		return 1;
	}

	public int method_8183() {
		return 1;
	}

	public int method_8182(int i) {
		return 1 + i * 10;
	}

	public int method_20742(int i) {
		return this.method_8182(i) + 5;
	}

	public int method_8181(int i, class_1282 arg) {
		return 0;
	}

	public float method_8196(int i, class_1310 arg) {
		return 0.0F;
	}

	public final boolean method_8188(class_1887 arg) {
		return this.method_8180(arg) && arg.method_8180(this);
	}

	protected boolean method_8180(class_1887 arg) {
		return this != arg;
	}

	protected String method_8190() {
		if (this.field_9084 == null) {
			this.field_9084 = class_156.method_646("enchantment", class_2378.field_11160.method_10221(this));
		}

		return this.field_9084;
	}

	public String method_8184() {
		return this.method_8190();
	}

	public class_2561 method_8179(int i) {
		class_2561 lv = new class_2588(this.method_8184());
		if (this.method_8195()) {
			lv.method_10854(class_124.field_1061);
		} else {
			lv.method_10854(class_124.field_1080);
		}

		if (i != 1 || this.method_8183() != 1) {
			lv.method_10864(" ").method_10852(new class_2588("enchantment.level." + i));
		}

		return lv;
	}

	public boolean method_8192(class_1799 arg) {
		return this.field_9083.method_8177(arg.method_7909());
	}

	public void method_8189(class_1309 arg, class_1297 arg2, int i) {
	}

	public void method_8178(class_1309 arg, class_1297 arg2, int i) {
	}

	public boolean method_8193() {
		return false;
	}

	public boolean method_8195() {
		return false;
	}

	public static enum class_1888 {
		field_9087(10),
		field_9090(5),
		field_9088(2),
		field_9091(1);

		private final int field_9089;

		private class_1888(int j) {
			this.field_9089 = j;
		}

		public int method_8197() {
			return this.field_9089;
		}
	}
}
