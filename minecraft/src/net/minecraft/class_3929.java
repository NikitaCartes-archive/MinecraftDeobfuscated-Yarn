package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_3929 {
	private static final Logger field_17408 = LogManager.getLogger();
	private static final Map<class_3917<?>, class_3929.class_3930<?, ?>> field_17409 = Maps.<class_3917<?>, class_3929.class_3930<?, ?>>newHashMap();

	public static <T extends class_1703> void method_17541(@Nullable class_3917<T> arg, class_310 arg2, int i, class_2561 arg3) {
		if (arg == null) {
			field_17408.warn("Trying to open invalid screen with name: {}", arg3.getString());
		} else {
			class_3929.class_3930<T, ?> lv = method_17540(arg);
			if (lv == null) {
				field_17408.warn("Failed to create screen for menu type: {}", class_2378.field_17429.method_10221(arg));
			} else {
				lv.method_17543(arg3, arg, arg2, i);
			}
		}
	}

	@Nullable
	private static <T extends class_1703> class_3929.class_3930<T, ?> method_17540(class_3917<T> arg) {
		return (class_3929.class_3930<T, ?>)field_17409.get(arg);
	}

	private static <M extends class_1703, U extends class_437 & class_3936<M>> void method_17542(class_3917<? extends M> arg, class_3929.class_3930<M, U> arg2) {
		class_3929.class_3930<?, ?> lv = (class_3929.class_3930<?, ?>)field_17409.put(arg, arg2);
		if (lv != null) {
			throw new IllegalStateException("Duplicate registration for " + class_2378.field_17429.method_10221(arg));
		}
	}

	public static boolean method_17539() {
		boolean bl = false;

		for (class_3917<?> lv : class_2378.field_17429) {
			if (!field_17409.containsKey(lv)) {
				field_17408.debug("Menu {} has no matching screen", class_2378.field_17429.method_10221(lv));
				bl = true;
			}
		}

		return bl;
	}

	static {
		method_17542(class_3917.field_18664, class_476::new);
		method_17542(class_3917.field_18665, class_476::new);
		method_17542(class_3917.field_17326, class_476::new);
		method_17542(class_3917.field_18666, class_476::new);
		method_17542(class_3917.field_18667, class_476::new);
		method_17542(class_3917.field_17327, class_476::new);
		method_17542(class_3917.field_17328, class_480::new);
		method_17542(class_3917.field_17329, class_471::new);
		method_17542(class_3917.field_17330, class_466::new);
		method_17542(class_3917.field_17331, class_3871::new);
		method_17542(class_3917.field_17332, class_472::new);
		method_17542(class_3917.field_17333, class_479::new);
		method_17542(class_3917.field_17334, class_486::new);
		method_17542(class_3917.field_17335, class_3873::new);
		method_17542(class_3917.field_17336, class_3802::new);
		method_17542(class_3917.field_17337, class_488::new);
		method_17542(class_3917.field_17338, class_3935::new);
		method_17542(class_3917.field_17339, class_494::new);
		method_17542(class_3917.field_17340, class_492::new);
		method_17542(class_3917.field_17341, class_495::new);
		method_17542(class_3917.field_17342, class_3874::new);
		method_17542(class_3917.field_17343, class_3934::new);
		method_17542(class_3917.field_17625, class_3979::new);
	}

	@Environment(EnvType.CLIENT)
	interface class_3930<T extends class_1703, U extends class_437 & class_3936<T>> {
		default void method_17543(class_2561 arg, class_3917<T> arg2, class_310 arg3, int i) {
			U lv = this.create(arg2.method_17434(i, arg3.field_1724.field_7514), arg3.field_1724.field_7514, arg);
			arg3.field_1724.field_7512 = lv.method_17577();
			arg3.method_1507(lv);
		}

		U create(T arg, class_1661 arg2, class_2561 arg3);
	}
}
