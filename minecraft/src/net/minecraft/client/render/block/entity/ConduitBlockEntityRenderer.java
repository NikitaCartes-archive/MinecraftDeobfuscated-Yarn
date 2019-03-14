package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.Camera;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
	private static final Identifier BASE_TEX = new Identifier("textures/entity/conduit/base.png");
	private static final Identifier CAGE_TEX = new Identifier("textures/entity/conduit/cage.png");
	private static final Identifier WIND_TEX = new Identifier("textures/entity/conduit/wind.png");
	private static final Identifier WIND_VERTICAL_TEX = new Identifier("textures/entity/conduit/wind_vertical.png");
	private static final Identifier OPEN_EYE_TEX = new Identifier("textures/entity/conduit/open_eye.png");
	private static final Identifier CLOSED_EYE_TEX = new Identifier("textures/entity/conduit/closed_eye.png");
	private final ConduitBlockEntityRenderer.BaseModel baseModel = new ConduitBlockEntityRenderer.BaseModel();
	private final ConduitBlockEntityRenderer.CageModel cageModel = new ConduitBlockEntityRenderer.CageModel();
	private final ConduitBlockEntityRenderer.WindModel windModel = new ConduitBlockEntityRenderer.WindModel();
	private final ConduitBlockEntityRenderer.EyeModel eyeModel = new ConduitBlockEntityRenderer.EyeModel();

	public void method_3572(ConduitBlockEntity conduitBlockEntity, double d, double e, double f, float g, int i) {
		float h = (float)conduitBlockEntity.ticks + g;
		if (!conduitBlockEntity.isActive()) {
			float j = conduitBlockEntity.getRotation(0.0F);
			this.bindTexture(BASE_TEX);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
			this.baseModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		} else if (conduitBlockEntity.isActive()) {
			float j = conduitBlockEntity.getRotation(g) * (180.0F / (float)Math.PI);
			float k = MathHelper.sin(h * 0.1F) / 2.0F + 0.5F;
			k = k * k + k;
			this.bindTexture(CAGE_TEX);
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.5F, 1.0F, 0.5F);
			this.cageModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
			int l = 3;
			int m = conduitBlockEntity.ticks / 3 % 22;
			this.windModel.method_3573(m);
			int n = conduitBlockEntity.ticks / 66 % 3;
			switch (n) {
				case 0:
					this.bindTexture(WIND_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 1:
					this.bindTexture(WIND_VERTICAL_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 2:
					this.bindTexture(WIND_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
			}

			Camera camera = this.renderManager.cameraEntity;
			if (conduitBlockEntity.isEyeOpen()) {
				this.bindTexture(OPEN_EYE_TEX);
			} else {
				this.bindTexture(CLOSED_EYE_TEX);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(-camera.getYaw(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(camera.getPitch(), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			this.eyeModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
			GlStateManager.popMatrix();
		}

		super.render(conduitBlockEntity, d, e, f, g, i);
	}

	@Environment(EnvType.CLIENT)
	static class BaseModel extends Model {
		private final Cuboid cuboid;

		public BaseModel() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.cuboid = new Cuboid(this, 0, 0);
			this.cuboid.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		}

		public void render(float f, float g, float h, float i, float j, float k) {
			this.cuboid.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class CageModel extends Model {
		private final Cuboid cuboid;

		public CageModel() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.cuboid = new Cuboid(this, 0, 0);
			this.cuboid.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		}

		public void render(float f, float g, float h, float i, float j, float k) {
			this.cuboid.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class EyeModel extends Model {
		private final Cuboid cuboid;

		public EyeModel() {
			this.textureWidth = 8;
			this.textureHeight = 8;
			this.cuboid = new Cuboid(this, 0, 0);
			this.cuboid.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
		}

		public void render(float f, float g, float h, float i, float j, float k) {
			this.cuboid.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class WindModel extends Model {
		private final Cuboid[] cuboids = new Cuboid[22];
		private int field_4384;

		public WindModel() {
			this.textureWidth = 64;
			this.textureHeight = 1024;

			for (int i = 0; i < 22; i++) {
				this.cuboids[i] = new Cuboid(this, 0, 32 * i);
				this.cuboids[i].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
			}
		}

		public void render(float f, float g, float h, float i, float j, float k) {
			this.cuboids[this.field_4384].render(k);
		}

		public void method_3573(int i) {
			this.field_4384 = i;
		}
	}
}
