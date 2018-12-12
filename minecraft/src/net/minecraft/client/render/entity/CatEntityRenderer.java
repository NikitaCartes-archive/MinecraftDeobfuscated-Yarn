package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;

@Environment(EnvType.CLIENT)
public class CatEntityRenderer extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
	public CatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CatEntityModel<>(0.0F), 0.4F);
		this.addFeature(new CatCollarFeatureRenderer(this));
	}

	@Nullable
	protected Identifier getTexture(CatEntity catEntity) {
		return catEntity.method_16092();
	}

	protected void method_4079(CatEntity catEntity, float f) {
		super.method_4042(catEntity, f);
		GlStateManager.scalef(0.8F, 0.8F, 0.8F);
	}

	protected void setupTransforms(CatEntity catEntity, float f, float g, float h) {
		super.setupTransforms(catEntity, f, g, h);
		float i = catEntity.method_16082(h);
		if (i > 0.0F) {
			GlStateManager.translatef(0.4F * i, 0.15F * i, 0.1F * i);
			GlStateManager.rotatef(this.linearRotationInterpolation(0.0F, 90.0F, i), 0.0F, 0.0F, 1.0F);
			BlockPos blockPos = new BlockPos(catEntity);

			for (PlayerEntity playerEntity : catEntity.world.getVisibleEntities(PlayerEntity.class, new BoundingBox(blockPos).expand(2.0, 2.0, 2.0))) {
				if (playerEntity.isSleeping()) {
					GlStateManager.translatef(0.15F * i, 0.0F, 0.0F);
					break;
				}
			}
		}
	}
}
