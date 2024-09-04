package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, S extends ZombieEntityRenderState, M extends ZombieEntityModel<S>>
	extends BipedEntityRenderer<T, S, M> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

	protected ZombieBaseEntityRenderer(
		EntityRendererFactory.Context context, M mainModel, M babyMainModel, M armorInnerModel, M armorOuterModel, M babyArmorInnerModel, M babyArmorOuterModel
	) {
		super(context, mainModel, babyMainModel, 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, armorInnerModel, armorOuterModel, babyArmorInnerModel, babyArmorOuterModel, context.getEquipmentRenderer()));
	}

	public Identifier getTexture(S zombieEntityRenderState) {
		return TEXTURE;
	}

	public void updateRenderState(T zombieEntity, S zombieEntityRenderState, float f) {
		super.updateRenderState(zombieEntity, zombieEntityRenderState, f);
		zombieEntityRenderState.attacking = zombieEntity.isAttacking();
		zombieEntityRenderState.convertingInWater = zombieEntity.isConvertingInWater();
	}

	protected boolean isShaking(S zombieEntityRenderState) {
		return super.isShaking(zombieEntityRenderState) || zombieEntityRenderState.convertingInWater;
	}
}
