package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M> {
	private final float field_4641;

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M horseEntityModel, float f) {
		super(entityRenderDispatcher, horseEntityModel, 0.75F);
		this.field_4641 = f;
	}

	protected void method_3874(T horseBaseEntity, float f) {
		GlStateManager.scalef(this.field_4641, this.field_4641, this.field_4641);
		super.scale(horseBaseEntity, f);
	}
}
