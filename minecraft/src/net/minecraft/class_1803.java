package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1803 extends class_1812 {
	public class_1803(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_1844.method_8065(arg, list, 0.25F);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_1799 lv2 = arg2.field_7503.field_7477 ? lv.method_7972() : lv.method_7971(1);
		arg.method_8465(
			null, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_14767, class_3419.field_15254, 0.5F, 0.4F / (field_8005.nextFloat() * 0.4F + 0.8F)
		);
		if (!arg.field_9236) {
			class_1686 lv3 = new class_1686(arg, arg2);
			lv3.method_7494(lv2);
			lv3.method_7489(arg2, arg2.field_5965, arg2.field_6031, -20.0F, 0.5F, 1.0F);
			arg.method_8649(lv3);
		}

		arg2.method_7259(class_3468.field_15372.method_14956(this));
		return new class_1271<>(class_1269.field_5812, lv);
	}
}
