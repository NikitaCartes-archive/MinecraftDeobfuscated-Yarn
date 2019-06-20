package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1739 extends class_1792 {
	private final class_2248 field_7882;

	public class_1739(class_2248 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7882 = arg;
	}

	@Override
	public String method_7876() {
		return this.field_7882.method_9539();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		super.method_7851(arg, arg2, list, arg3);
		this.field_7882.method_9568(arg, arg2, list, arg3);
	}
}
