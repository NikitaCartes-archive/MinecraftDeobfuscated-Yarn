package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.PigZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigZombieEntityRenderer extends BipedEntityRenderer<PigZombieEntity, ZombieEntityModel<PigZombieEntity>> {
	private static final Identifier field_4759 = new Identifier("textures/entity/zombie_pigman.png");

	public PigZombieEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ZombieEntityModel<>(), 0.5F);
		this.method_4046(new ArmorBipedFeatureRenderer<>(this, new ZombieEntityModel(0.5F, true), new ZombieEntityModel(1.0F, true)));
	}

	protected Identifier method_4093(PigZombieEntity pigZombieEntity) {
		return field_4759;
	}
}
