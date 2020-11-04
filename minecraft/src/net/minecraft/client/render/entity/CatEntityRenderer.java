package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CatEntityRenderer extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
	public CatEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new CatEntityModel<>(arg.method_32167(EntityModelLayers.CAT)), 0.4F);
		this.addFeature(new CatCollarFeatureRenderer(this, arg.method_32170()));
	}

	public Identifier getTexture(CatEntity catEntity) {
		return catEntity.getTexture();
	}

	protected void scale(CatEntity catEntity, MatrixStack matrixStack, float f) {
		super.scale(catEntity, matrixStack, f);
		matrixStack.scale(0.8F, 0.8F, 0.8F);
	}

	protected void setupTransforms(CatEntity catEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(catEntity, matrixStack, f, g, h);
		float i = catEntity.getSleepAnimation(h);
		if (i > 0.0F) {
			matrixStack.translate((double)(0.4F * i), (double)(0.15F * i), (double)(0.1F * i));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerpAngleDegrees(i, 0.0F, 90.0F)));
			BlockPos blockPos = catEntity.getBlockPos();

			for (PlayerEntity playerEntity : catEntity.world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(2.0, 2.0, 2.0))) {
				if (playerEntity.isSleeping()) {
					matrixStack.translate((double)(0.15F * i), 0.0, 0.0);
					break;
				}
			}
		}
	}
}
