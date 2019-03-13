package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier[] field_4648 = new Identifier[]{
		new Identifier("textures/entity/boat/oak.png"),
		new Identifier("textures/entity/boat/spruce.png"),
		new Identifier("textures/entity/boat/birch.png"),
		new Identifier("textures/entity/boat/jungle.png"),
		new Identifier("textures/entity/boat/acacia.png"),
		new Identifier("textures/entity/boat/dark_oak.png")
	};
	protected final BoatEntityModel field_4647 = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.8F;
	}

	public void method_3888(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3890(d, e, f);
		this.method_3889(boatEntity, g, h);
		this.bindEntityTexture(boatEntity);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(boatEntity));
		}

		this.field_4647.method_17071(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(boatEntity, d, e, f, g, h);
	}

	public void method_3889(BoatEntity boatEntity, float f, float g) {
		GlStateManager.rotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
		float h = (float)boatEntity.method_7533() - g;
		float i = boatEntity.method_7554() - g;
		if (i < 0.0F) {
			i = 0.0F;
		}

		if (h > 0.0F) {
			GlStateManager.rotatef(MathHelper.sin(h) * h * i / 10.0F * (float)boatEntity.method_7543(), 1.0F, 0.0F, 0.0F);
		}

		float j = boatEntity.method_7547(g);
		if (!MathHelper.equalsApproximate(j, 0.0F)) {
			GlStateManager.rotatef(boatEntity.method_7547(g), 1.0F, 0.0F, 1.0F);
		}

		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
	}

	public void method_3890(double d, double e, double f) {
		GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
	}

	protected Identifier method_3891(BoatEntity boatEntity) {
		return field_4648[boatEntity.getBoatType().ordinal()];
	}

	@Override
	public boolean hasSecondPass() {
		return true;
	}

	public void method_3887(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3890(d, e, f);
		this.method_3889(boatEntity, g, h);
		this.bindEntityTexture(boatEntity);
		this.field_4647.renderPass(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
