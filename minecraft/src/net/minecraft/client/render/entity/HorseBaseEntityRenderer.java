package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M> {
	private final float scale;

	public HorseBaseEntityRenderer(class_5617.class_5618 arg, M model, float scale) {
		super(arg, model, 0.75F);
		this.scale = scale;
	}

	protected void scale(T horseBaseEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(this.scale, this.scale, this.scale);
		super.scale(horseBaseEntity, matrixStack, f);
	}
}
