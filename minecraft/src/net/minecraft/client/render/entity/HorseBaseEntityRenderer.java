package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity> extends EntityMobRenderer<HorseBaseEntity> {
	private final float field_4641;

	public HorseBaseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, Model model, float f) {
		super(entityRenderDispatcher, model, 0.75F);
		this.field_4641 = f;
	}

	protected void method_3874(HorseBaseEntity horseBaseEntity, float f) {
		GlStateManager.scalef(this.field_4641, this.field_4641, this.field_4641);
		super.method_4042((T)horseBaseEntity, f);
	}
}
