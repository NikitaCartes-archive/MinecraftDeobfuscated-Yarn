package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class class_1317 {
	private static final Map<class_1299<?>, class_1317.class_1318> field_6313 = Maps.<class_1299<?>, class_1317.class_1318>newHashMap();

	private static <T extends class_1308> void method_20637(
		class_1299<T> arg, class_1317.class_1319 arg2, class_2902.class_2903 arg3, class_1317.class_4306<T> arg4
	) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.put(arg, new class_1317.class_1318(arg3, arg2, arg4));
		if (lv != null) {
			throw new IllegalStateException("Duplicate registration for type " + class_2378.field_11145.method_10221(arg));
		}
	}

	public static class_1317.class_1319 method_6159(class_1299<?> arg) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null ? class_1317.class_1319.field_19350 : lv.field_6315;
	}

	public static class_2902.class_2903 method_6160(@Nullable class_1299<?> arg) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null ? class_2902.class_2903.field_13203 : lv.field_6314;
	}

	public static <T extends class_1297> boolean method_20638(class_1299<T> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		class_1317.class_1318 lv = (class_1317.class_1318)field_6313.get(arg);
		return lv == null || lv.field_19349.test(arg, arg2, arg3, arg4, random);
	}

	static {
		method_20637(class_1299.field_6070, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1422::method_20662);
		method_20637(class_1299.field_6087, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1433::method_20664);
		method_20637(class_1299.field_6123, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1551::method_20673);
		method_20637(class_1299.field_6118, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1577::method_20676);
		method_20637(class_1299.field_6062, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1422::method_20662);
		method_20637(class_1299.field_6073, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1422::method_20662);
		method_20637(class_1299.field_6114, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1477::method_20670);
		method_20637(class_1299.field_6111, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1422::method_20662);
		method_20637(class_1299.field_6108, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1420::method_20661);
		method_20637(class_1299.field_6099, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20681);
		method_20637(class_1299.field_6084, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6132, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6085, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6046, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6067, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6091, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6128, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1559::method_20674);
		method_20637(class_1299.field_6116, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_6107, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1571::method_20675);
		method_20637(class_1299.field_6095, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6139, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6071, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1576::method_20677);
		method_20637(class_1299.field_6147, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_6074, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6102, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1589::method_20678);
		method_20637(class_1299.field_6143, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1438::method_20665);
		method_20637(class_1299.field_6057, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6081, class_1317.class_1319.field_6317, class_2902.class_2903.field_13197, class_3701::method_20666);
		method_20637(class_1299.field_6104, class_1317.class_1319.field_6317, class_2902.class_2903.field_13197, class_1453::method_20667);
		method_20637(class_1299.field_6093, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6105, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_3732::method_20739);
		method_20637(class_1299.field_6042, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1456::method_20668);
		method_20637(class_1299.field_6140, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1463::method_20669);
		method_20637(class_1299.field_6115, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6125, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1614::method_20684);
		method_20637(class_1299.field_6137, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6075, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6069, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1621::method_20685);
		method_20637(class_1299.field_6047, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_6079, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6098, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1627::method_20686);
		method_20637(class_1299.field_6113, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1481::method_20671);
		method_20637(class_1299.field_6077, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_6145, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6119, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6076, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6055, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6051, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6048, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6050, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1590::method_20682);
		method_20637(class_1299.field_6054, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_16281, class_1317.class_1319.field_6317, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6086, class_1317.class_1319.field_6318, class_2902.class_2903.field_13203, class_1577::method_20676);
		method_20637(class_1299.field_6090, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_17943, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6065, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6146, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6078, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_6134, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6109, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1308::method_20636);
		method_20637(class_1299.field_17714, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1429::method_20663);
		method_20637(class_1299.field_6059, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_6117, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1588::method_20680);
		method_20637(class_1299.field_17713, class_1317.class_1319.field_19350, class_2902.class_2903.field_13203, class_1308::method_20636);
	}

	static class class_1318 {
		private final class_2902.class_2903 field_6314;
		private final class_1317.class_1319 field_6315;
		private final class_1317.class_4306<?> field_19349;

		public class_1318(class_2902.class_2903 arg, class_1317.class_1319 arg2, class_1317.class_4306<?> arg3) {
			this.field_6314 = arg;
			this.field_6315 = arg2;
			this.field_19349 = arg3;
		}
	}

	public static enum class_1319 {
		field_6317,
		field_6318,
		field_19350;
	}

	@FunctionalInterface
	public interface class_4306<T extends class_1297> {
		boolean test(class_1299<T> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random);
	}
}
