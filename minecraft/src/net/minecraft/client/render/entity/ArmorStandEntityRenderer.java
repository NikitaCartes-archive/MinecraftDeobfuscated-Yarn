package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity> {
	public static final Identifier TEX = new Identifier("textures/entity/armorstand/wood.png");

	public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0F);
		ArmorBipedEntityRenderer armorBipedEntityRenderer = new ArmorBipedEntityRenderer(this) {
			@Override
			protected void init() {
				this.modelLeggings = new ArmorStandArmorEntityModel(0.5F);
				this.modelBody = new ArmorStandArmorEntityModel(1.0F);
			}
		};
		this.addLayer(armorBipedEntityRenderer);
		this.addLayer(new HeldItemEntityRenderer(this));
		this.addLayer(new ElytraEntityRenderer(this));
		this.addLayer(new HeadEntityRenderer(this.method_3879().head));
	}

	protected Identifier getTexture(ArmorStandEntity armorStandEntity) {
		return TEX;
	}

	public ArmorStandEntityModel method_3879() {
		return (ArmorStandEntityModel)super.method_4038();
	}

	protected void method_3877(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.field_7112) + h;
		if (i < 5.0F) {
			GlStateManager.rotatef(MathHelper.sin(i / 1.5F * (float) Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	protected boolean method_3878(ArmorStandEntity armorStandEntity) {
		return armorStandEntity.isCustomNameVisible();
	}

	public void method_3876(ArmorStandEntity armorStandEntity, double d, double e, double f, float g, float h) {
		if (armorStandEntity.method_6912()) {
			this.field_4739 = true;
		}

		super.method_4054(armorStandEntity, d, e, f, g, h);
		if (armorStandEntity.method_6912()) {
			this.field_4739 = false;
		}
	}
}
