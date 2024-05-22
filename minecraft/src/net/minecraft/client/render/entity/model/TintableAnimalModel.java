package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public abstract class TintableAnimalModel<E extends Entity> extends AnimalModel<E> {
	private int field_52151 = -1;

	public void setColorMultiplier(int i) {
		this.field_52151 = i;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int i) {
		super.render(matrices, vertices, light, overlay, ColorHelper.Argb.mixColor(i, this.field_52151));
	}
}
