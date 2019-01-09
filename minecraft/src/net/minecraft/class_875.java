package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_875<T extends class_1496, M extends class_549<T>> extends class_927<T, M> {
	private final float field_4641;

	public class_875(class_898 arg, M arg2, float f) {
		super(arg, arg2, 0.75F);
		this.field_4641 = f;
	}

	protected void method_3874(T arg, float f) {
		GlStateManager.scalef(this.field_4641, this.field_4641, this.field_4641);
		super.method_4042(arg, f);
	}
}
