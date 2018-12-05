package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieEntityRenderer extends BipedEntityRenderer<ZombieEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/zombie.png");

	public ZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BipedEntityModel bipedEntityModel) {
		super(entityRenderDispatcher, bipedEntityModel, 0.5F);
		this.addLayer(this.method_4162());
	}

	public ZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, new ZombieEntityModel());
	}

	protected ArmorBipedEntityRenderer method_4162() {
		return new ArmorBipedEntityRenderer(this) {
			@Override
			protected void init() {
				this.modelLeggings = new ZombieEntityModel(0.5F, true);
				this.modelBody = new ZombieEntityModel(1.0F, true);
			}
		};
	}

	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return SKIN;
	}

	protected void method_4164(ZombieEntity zombieEntity, float f, float g, float h) {
		if (zombieEntity.method_7206()) {
			g += (float)(Math.cos((double)zombieEntity.age * 3.25) * Math.PI * 0.25);
		}

		super.method_4058(zombieEntity, f, g, h);
	}
}
