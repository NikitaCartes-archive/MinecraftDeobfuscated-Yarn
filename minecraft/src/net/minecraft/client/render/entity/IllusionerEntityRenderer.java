package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3729;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderer extends class_3729 {
	private static final Identifier field_4718 = new Identifier("textures/entity/illager/illusioner.png");

	public IllusionerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.addLayer(new HeldItemEntityRenderer(this) {
			@Override
			public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
				if (((IllusionerEntity)livingEntity).method_7137() || ((IllusionerEntity)livingEntity).method_7067()) {
					super.render(livingEntity, f, g, h, i, j, k, l);
				}
			}

			@Override
			protected void method_4193(OptionMainHand optionMainHand) {
				((EvilVillagerEntityModel)this.renderer.method_4038()).method_2813(optionMainHand).method_2847(0.0625F);
			}
		});
		((EvilVillagerEntityModel)this.method_4038()).method_2812().visible = true;
	}

	protected Identifier getTexture(HostileEntity hostileEntity) {
		return field_4718;
	}

	public void method_3991(HostileEntity hostileEntity, double d, double e, double f, float g, float h) {
		if (hostileEntity.isInvisible()) {
			Vec3d[] vec3ds = ((IllusionerEntity)hostileEntity).method_7065(h);
			float i = this.method_4045(hostileEntity, h);

			for (int j = 0; j < vec3ds.length; j++) {
				super.method_4072(
					hostileEntity,
					d + vec3ds[j].x + (double)MathHelper.cos((float)j + i * 0.5F) * 0.025,
					e + vec3ds[j].y + (double)MathHelper.cos((float)j + i * 0.75F) * 0.0125,
					f + vec3ds[j].z + (double)MathHelper.cos((float)j + i * 0.7F) * 0.025,
					g,
					h
				);
			}
		} else {
			super.method_4072(hostileEntity, d, e, f, g, h);
		}
	}

	protected boolean method_3988(HostileEntity hostileEntity) {
		return true;
	}
}
