package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M> {
	private final float scale;

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M horseEntityModel, float f) {
		super(entityRenderDispatcher, horseEntityModel, 0.75F);
		this.scale = f;
	}

	protected void scale(T horseBaseEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(this.scale, this.scale, this.scale);
		super.scale(horseBaseEntity, matrixStack, f);
	}
}
