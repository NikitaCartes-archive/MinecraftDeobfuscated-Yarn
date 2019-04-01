package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4274 extends class_1792 {
	private final class_1767 field_19175;
	private final class_124 field_19176;

	public class_4274(class_1792.class_1793 arg, class_1767 arg2, class_124 arg3) {
		super(arg);
		this.field_19175 = arg2;
		this.field_19176 = arg3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		list.add(new class_2588("item.key.tooltip", new class_2588("color.minecraft." + this.field_19175.method_7792()).method_10854(this.field_19176)));
	}
}
