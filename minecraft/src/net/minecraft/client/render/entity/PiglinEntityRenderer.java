package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PiglinBipedArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/piglin/piglin.png");
	private static final Identifier field_22442 = new Identifier("textures/entity/piglin/zombified_piglin.png");

	public PiglinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
		super(entityRenderDispatcher, method_24875(bl), 0.5F);
		this.addFeature(new PiglinBipedArmorFeatureRenderer<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.0F), createEarlessPiglinModel()));
	}

	private static PiglinEntityModel<MobEntity> method_24875(boolean bl) {
		PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<>(0.0F, 128, 64);
		if (bl) {
			piglinEntityModel.leftEar.visible = false;
		}

		return piglinEntityModel;
	}

	private static <T extends PiglinEntity> PiglinEntityModel<T> createEarlessPiglinModel() {
		PiglinEntityModel<T> piglinEntityModel = new PiglinEntityModel<>(1.0F, 64, 16);
		piglinEntityModel.leftEar.visible = false;
		piglinEntityModel.rightEar.visible = false;
		return piglinEntityModel;
	}

	@Override
	public Identifier getTexture(MobEntity mobEntity) {
		return mobEntity instanceof PiglinEntity ? TEXTURE : field_22442;
	}

	protected void setupTransforms(MobEntity mobEntity, MatrixStack matrixStack, float f, float g, float h) {
		if (mobEntity instanceof PiglinEntity && ((PiglinEntity)mobEntity).canConvert()) {
			g += (float)(Math.cos((double)mobEntity.age * 3.25) * Math.PI * 0.5);
		}

		super.setupTransforms(mobEntity, matrixStack, f, g, h);
	}
}
