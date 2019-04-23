package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderer extends MobEntityRenderer<FoxEntity, FoxModel<FoxEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fox/fox.png");
	private static final Identifier SKIN_SLEEP = new Identifier("textures/entity/fox/fox_sleep.png");
	private static final Identifier SKIN_SNOW = new Identifier("textures/entity/fox/snow_fox.png");
	private static final Identifier SKIN_SNOW_SLEEp = new Identifier("textures/entity/fox/snow_fox_sleep.png");

	public FoxEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new FoxModel<>(), 0.4F);
		this.addFeature(new FoxHeldItemFeatureRenderer(this));
	}

	protected void method_18334(FoxEntity foxEntity, float f, float g, float h) {
		super.setupTransforms(foxEntity, f, g, h);
		if (foxEntity.isChasing() || foxEntity.isWalking()) {
			GlStateManager.rotatef(-MathHelper.lerp(h, foxEntity.prevPitch, foxEntity.pitch), 1.0F, 0.0F, 0.0F);
		}
	}

	@Nullable
	protected Identifier method_18333(FoxEntity foxEntity) {
		if (foxEntity.getFoxType() == FoxEntity.Type.field_17996) {
			return foxEntity.isSleeping() ? SKIN_SLEEP : SKIN;
		} else {
			return foxEntity.isSleeping() ? SKIN_SNOW_SLEEp : SKIN_SNOW;
		}
	}
}
