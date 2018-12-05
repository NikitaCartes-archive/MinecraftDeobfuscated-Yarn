package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3729;
import net.minecraft.client.render.entity.model.PillagerEntityModel;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PillagerEntityRenderer extends class_3729 {
	private static final Identifier field_4757 = new Identifier("textures/entity/illager/pillager.png");

	public PillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PillagerEntityModel(0.0F, 0.0F, 64, 64), 0.5F);
		this.addLayer(new HeldItemEntityRenderer(this) {
			@Override
			protected void method_4193(OptionMainHand optionMainHand) {
				((PillagerEntityModel)this.renderer.method_4038()).method_2813(optionMainHand).method_2847(0.0625F);
			}
		});
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return field_4757;
	}
}
