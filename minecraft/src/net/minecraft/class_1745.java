package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1745 extends class_1792 {
	private final class_2582 field_7900;

	public class_1745(class_2582 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7900 = arg;
	}

	public class_2582 method_7704() {
		return this.field_7900;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		list.add(this.method_7703().method_10854(class_124.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_7703() {
		return new class_2588(this.method_7876() + ".desc");
	}
}
