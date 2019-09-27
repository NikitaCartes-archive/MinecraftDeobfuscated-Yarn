package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.Vector3f;
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

	@Override
	public Identifier method_4163(ZombieEntity zombieEntity) {
		return SKIN;
	}

	protected void method_4164(DrownedEntity drownedEntity, class_4587 arg, float f, float g, float h) {
		super.method_17144(drownedEntity, arg, f, g, h);
		float i = drownedEntity.getLeaningPitch(h);
		if (i > 0.0F) {
			arg.method_22907(Vector3f.field_20703.method_23214(MathHelper.lerp(i, drownedEntity.pitch, -10.0F - drownedEntity.pitch), true));
		}
	}
}
