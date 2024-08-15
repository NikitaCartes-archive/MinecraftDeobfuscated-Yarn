package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UndeadHorseEntityRenderer
	extends AbstractHorseEntityRenderer<AbstractHorseEntity, LivingHorseEntityRenderState, AbstractHorseEntityModel<LivingHorseEntityRenderState>> {
	private static final Identifier ZOMBIE_HORSE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/horse_zombie.png");
	private static final Identifier SKELETON_HORSE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/horse_skeleton.png");
	private final Identifier texture;

	public UndeadHorseEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer babyLayer, boolean skeleton) {
		super(ctx, new HorseEntityModel(ctx.getPart(layer)), new HorseEntityModel(ctx.getPart(babyLayer)), 1.0F);
		this.texture = skeleton ? SKELETON_HORSE_TEXTURE : ZOMBIE_HORSE_TEXTURE;
	}

	public Identifier getTexture(LivingHorseEntityRenderState livingHorseEntityRenderState) {
		return this.texture;
	}

	public LivingHorseEntityRenderState getRenderState() {
		return new LivingHorseEntityRenderState();
	}
}
