package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned.png");

	public DrownedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DrownedEntityModel<>(0.0F, 0.0F, 64, 64), new DrownedEntityModel<>(0.5F, true), new DrownedEntityModel<>(1.0F, true));
		this.addFeature(new DrownedOverlayFeatureRenderer<>(this));
	}

	@Nullable
	@Override
	protected Identifier method_4163(ZombieEntity zombieEntity) {
		return SKIN;
	}

	protected void method_4164(DrownedEntity drownedEntity, float f, float g, float h) {
		float i = drownedEntity.method_6024(h);
		super.method_17144(drownedEntity, f, g, h);
		if (i > 0.0F) {
			GlStateManager.rotatef(MathHelper.lerp(i, drownedEntity.pitch, -10.0F - drownedEntity.pitch), 1.0F, 0.0F, 0.0F);
		}
	}
}
