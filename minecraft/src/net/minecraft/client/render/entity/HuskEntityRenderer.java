package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HuskEntityRenderer extends ZombieEntityRenderer {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/husk.png");

	public HuskEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.HUSK, EntityModelLayers.HUSK_INNER_ARMOR, EntityModelLayers.HUSK_OUTER_ARMOR);
	}

	protected void scale(ZombieEntity zombieEntity, MatrixStack matrixStack, float f) {
		float g = 1.0625F;
		matrixStack.scale(1.0625F, 1.0625F, 1.0625F);
		super.scale(zombieEntity, matrixStack, f);
	}

	@Override
	public Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}
}
