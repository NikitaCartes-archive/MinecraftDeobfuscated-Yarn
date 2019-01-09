package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_2185 extends class_2237 {
	private final class_1767 field_9855;

	protected class_2185(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_9855 = arg;
	}

	@Override
	public boolean method_9538() {
		return true;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2573(this.field_9855);
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2573) {
				((class_2573)lv).method_16842(arg5.method_7964());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_2586 lv = arg.method_8321(arg2);
		return lv instanceof class_2573 ? ((class_2573)lv).method_10907(arg3) : super.method_9574(arg, arg2, arg3);
	}

	public class_1767 method_9303() {
		return this.field_9855;
	}
}
