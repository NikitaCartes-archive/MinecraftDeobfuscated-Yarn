package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
		super(ctx, bodyModel, 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, legsArmorModel, bodyArmorModel, ctx.getModelManager()));
	}

	public Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(T zombieEntity) {
		return super.isShaking(zombieEntity) || zombieEntity.isConvertingInWater();
	}
}
