package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CatEntityRenderer extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
	public CatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CatEntityModel<>(0.0F), 0.4F);
		this.addFeature(new CatCollarFeatureRenderer(this));
	}

	public Identifier method_4078(CatEntity catEntity) {
		return catEntity.getTexture();
	}

	protected void method_4079(CatEntity catEntity, class_4587 arg, float f) {
		super.scale(catEntity, arg, f);
		arg.method_22905(0.8F, 0.8F, 0.8F);
	}

	protected void method_16045(CatEntity catEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(catEntity, arg, f, g, h);
		float i = catEntity.getSleepAnimation(h);
		if (i > 0.0F) {
			arg.method_22904((double)(0.4F * i), (double)(0.15F * i), (double)(0.1F * i));
			arg.method_22907(Vector3f.field_20707.method_23214(MathHelper.lerpAngleDegrees(i, 0.0F, 90.0F), true));
			BlockPos blockPos = new BlockPos(catEntity);

			for (PlayerEntity playerEntity : catEntity.world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(2.0, 2.0, 2.0))) {
				if (playerEntity.isSleeping()) {
					arg.method_22904((double)(0.15F * i), 0.0, 0.0);
					break;
				}
			}
		}
	}
}
