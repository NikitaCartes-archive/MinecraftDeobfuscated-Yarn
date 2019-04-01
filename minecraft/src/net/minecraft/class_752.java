package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_752 {
	private double field_3954;
	private double field_3953;
	private double field_3957;
	protected final List<class_851> field_3955 = Lists.<class_851>newArrayListWithCapacity(17424);
	protected boolean field_3956;

	public void method_3158(double d, double e, double f) {
		this.field_3956 = true;
		this.field_3955.clear();
		this.field_3954 = d;
		this.field_3953 = e;
		this.field_3957 = f;
	}

	public void method_3157(class_851 arg) {
		class_2338 lv = arg.method_3670();
		GlStateManager.translatef(
			(float)((double)lv.method_10263() - this.field_3954),
			(float)((double)lv.method_10264() - this.field_3953),
			(float)((double)lv.method_10260() - this.field_3957)
		);
	}

	public void method_3159(class_851 arg, class_1921 arg2) {
		this.field_3955.add(arg);
	}

	public abstract void method_3160(class_1921 arg);
}
