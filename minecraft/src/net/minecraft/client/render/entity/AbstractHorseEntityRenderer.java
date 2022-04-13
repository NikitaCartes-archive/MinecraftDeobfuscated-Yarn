package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractHorseEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractHorseEntityRenderer<T extends AbstractHorseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M> {
	private final float scale;

	public AbstractHorseEntityRenderer(EntityRendererFactory.Context ctx, M model, float scale) {
		super(ctx, model, 0.75F);
		this.scale = scale;
	}

	protected void scale(T abstractHorseEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(this.scale, this.scale, this.scale);
		super.scale(abstractHorseEntity, matrixStack, f);
	}
}
