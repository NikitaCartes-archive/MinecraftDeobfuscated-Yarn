package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
	private static final Identifier PIGLIN_TEXTURE = new Identifier("textures/entity/piglin/piglin.png");
	private static final Identifier ZOMBIFIED_PIGLIN_TEXTURE = new Identifier("textures/entity/piglin/zombified_piglin.png");

	public PiglinEntityRenderer(EntityRenderDispatcher dispatcher, boolean zombified) {
		super(dispatcher, getPiglinModel(zombified), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
		this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.02F)));
	}

	private static PiglinEntityModel<MobEntity> getPiglinModel(boolean zombified) {
		PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<>(0.0F, 64, 64);
		if (zombified) {
			piglinEntityModel.leftEar.visible = false;
		}

		return piglinEntityModel;
	}

	@Override
	public Identifier getTexture(MobEntity mobEntity) {
		return mobEntity instanceof PiglinEntity ? PIGLIN_TEXTURE : ZOMBIFIED_PIGLIN_TEXTURE;
	}

	protected boolean isShaking(MobEntity mobEntity) {
		return mobEntity instanceof PiglinEntity && ((PiglinEntity)mobEntity).canConvert();
	}
}
