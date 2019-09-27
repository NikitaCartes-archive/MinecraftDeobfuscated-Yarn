package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HuskEntityRenderer extends ZombieEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/husk.png");

	public HuskEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_3985(ZombieEntity zombieEntity, class_4587 arg, float f) {
		float g = 1.0625F;
		arg.method_22905(1.0625F, 1.0625F, 1.0625F);
		super.scale(zombieEntity, arg, f);
	}

	@Override
	public Identifier method_4163(ZombieEntity zombieEntity) {
		return SKIN;
	}
}
