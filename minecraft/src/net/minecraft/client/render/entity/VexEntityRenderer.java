package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VexEntityModel(), 0.3F);
	}

	public Identifier method_4144(VexEntity vexEntity) {
		return vexEntity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
	}

	protected void method_4143(VexEntity vexEntity, class_4587 arg, float f) {
		arg.method_22905(0.4F, 0.4F, 0.4F);
	}
}
