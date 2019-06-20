package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;

public class class_3794 extends class_3491 {
	public static final class_3794 field_16871 = new class_3794();

	private class_3794() {
	}

	@Nullable
	@Override
	public class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5) {
		class_2248 lv = arg4.field_15596.method_11614();
		if (lv != class_2246.field_16540) {
			return arg4;
		} else {
			String string = arg4.field_15595.method_10558("final_state");
			class_2259 lv2 = new class_2259(new StringReader(string), false);

			try {
				lv2.method_9678(true);
			} catch (CommandSyntaxException var10) {
				throw new RuntimeException(var10);
			}

			return lv2.method_9669().method_11614() == class_2246.field_10369 ? null : new class_3499.class_3501(arg4.field_15597, lv2.method_9669(), null);
		}
	}

	@Override
	protected class_3828 method_16772() {
		return class_3828.field_16991;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
