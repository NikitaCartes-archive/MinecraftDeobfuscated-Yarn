package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class class_1317 {
	private static final Map<class_1299<?>, class_1317.class_1318> field_6313 = Maps.<class_1299<?>, class_1317.class_1318>newHashMap();

	private static void method_6161(class_1299<?> arg, class_1317.class_1319 arg2, class_2902.class_2903 arg3) {
		method_6157(arg, arg2, arg3, null);
	}

	private static void method_6157(class_1299<?> arg, class_1317.class_1319 arg2, class_2902.class_2903 arg3, @Nullable class_3494<class_2248> arg4) {
		field_6313.put(arg, new class_1317.class_1318(arg3, arg2, arg4));
	}

	@Nullable
	public static class_1317.class_1319 method_6159(class_1299<?> arg) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null ? null : lv.field_6315;
	}

	public static class_2902.class_2903 method_6160(@Nullable class_1299<?> arg) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null ? class_2902.class_2903.field_13203 : lv.field_6314;
	}

	public static boolean method_6158(class_1299<?> arg, class_2680 arg2) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null ? false : lv.field_6316 != null && arg2.method_11602(lv.field_6316);
	}

	static {
		method_6161(class_1299.field_6070, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6087, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6123, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6118, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6062, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6073, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6114, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6111, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203);
		method_6157(class_1299.field_6081, class_1317.class_1319.field_6317, class_2902.class_2903.field_13197, class_3481.field_15503);
		method_6157(class_1299.field_6104, class_1317.class_1319.field_6317, class_2902.class_2903.field_13197, class_3481.field_15503);
		method_6157(class_1299.field_6042, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_3481.field_15467);
		method_6161(class_1299.field_6108, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6099, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6084, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6132, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6085, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6046, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6067, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6091, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6128, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6116, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6107, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6095, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6139, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6071, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6105, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6074, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6102, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6143, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6057, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6093, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6140, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6115, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6125, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6137, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6075, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6069, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6047, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6079, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6098, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6113, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6077, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6147, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6145, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6119, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6076, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6055, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6051, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6048, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6050, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
		method_6161(class_1299.field_6054, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203);
	}

	static class class_1318 {
		private final class_2902.class_2903 field_6314;
		private final class_1317.class_1319 field_6315;
		@Nullable
		private final class_3494<class_2248> field_6316;

		public class_1318(class_2902.class_2903 arg, class_1317.class_1319 arg2, @Nullable class_3494<class_2248> arg3) {
			this.field_6314 = arg;
			this.field_6315 = arg2;
			this.field_6316 = arg3;
		}
	}

	public static enum class_1319 {
		field_6317,
		field_6318;
	}
}
