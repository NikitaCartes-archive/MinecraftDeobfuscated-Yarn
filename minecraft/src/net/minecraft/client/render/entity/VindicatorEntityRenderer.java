package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3729;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends class_3729 {
	private static final Identifier field_4804 = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.addLayer(new HeldItemEntityRenderer(this) {
			@Override
			public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
				if (((VindicatorEntity)livingEntity).method_7169()) {
					super.render(livingEntity, f, g, h, i, j, k, l);
				}
			}

			@Override
			protected void method_4193(OptionMainHand optionMainHand) {
				((EvilVillagerEntityModel)this.renderer.method_4038()).method_2813(optionMainHand).method_2847(0.0625F);
			}
		});
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return field_4804;
	}
}
