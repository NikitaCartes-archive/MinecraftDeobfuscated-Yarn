package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public abstract class TintableCompositeModel<E extends Entity> extends SinglePartEntityModel<E> {
	private int field_52152 = -1;

	public void setColorMultiplier(int i) {
		this.field_52152 = i;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		super.render(matrices, vertices, light, overlay, ColorHelper.Argb.mixColor(color, this.field_52152));
	}
}
