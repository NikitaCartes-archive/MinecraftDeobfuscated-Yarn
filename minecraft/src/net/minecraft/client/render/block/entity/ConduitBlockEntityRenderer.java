package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
	private static final Identifier BASE_TEX = new Identifier("textures/entity/conduit/base.png");
	private static final Identifier CAGE_TEX = new Identifier("textures/entity/conduit/cage.png");
	private static final Identifier WIND_TEX = new Identifier("textures/entity/conduit/wind.png");
	private static final Identifier WIND_VERTICAL_TEX = new Identifier("textures/entity/conduit/wind_vertical.png");
	private static final Identifier OPEN_EYE_TEX = new Identifier("textures/entity/conduit/open_eye.png");
	private static final Identifier CLOSED_EYE_TEX = new Identifier("textures/entity/conduit/closed_eye.png");
	private final Model field_4372 = new ConduitBlockEntityRenderer.class_832();
	private final Model field_4375 = new ConduitBlockEntityRenderer.class_830();
	private final ConduitBlockEntityRenderer.class_833 field_4374 = new ConduitBlockEntityRenderer.class_833();
	private final ConduitBlockEntityRenderer.class_831 field_4376 = new ConduitBlockEntityRenderer.class_831();

	public void render(ConduitBlockEntity conduitBlockEntity, double d, double e, double f, float g, int i) {
		float h = (float)conduitBlockEntity.field_11936 + g;
		if (!conduitBlockEntity.method_11065()) {
			float j = conduitBlockEntity.method_11061(0.0F);
			this.bindTexture(BASE_TEX);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
			this.field_4372.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		} else if (conduitBlockEntity.method_11065()) {
			float j = conduitBlockEntity.method_11061(g) * (180.0F / (float)Math.PI);
			float k = MathHelper.sin(h * 0.1F) / 2.0F + 0.5F;
			k = k * k + k;
			this.bindTexture(CAGE_TEX);
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.rotatef(j, 0.5F, 1.0F, 0.5F);
			this.field_4375.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
			int l = 3;
			int m = conduitBlockEntity.field_11936 / 3 % ConduitBlockEntityRenderer.class_833.field_4385;
			this.field_4374.method_3573(m);
			int n = conduitBlockEntity.field_11936 / (3 * ConduitBlockEntityRenderer.class_833.field_4385) % 3;
			switch (n) {
				case 0:
					this.bindTexture(WIND_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 1:
					this.bindTexture(WIND_VERTICAL_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					break;
				case 2:
					this.bindTexture(WIND_TEX);
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
					GlStateManager.scalef(0.875F, 0.875F, 0.875F);
					GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
					this.field_4374.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
			}

			Entity entity = MinecraftClient.getInstance().getCameraEntity();
			Vec2f vec2f = Vec2f.ZERO;
			if (entity != null) {
				vec2f = entity.getRotationClient();
			}

			if (conduitBlockEntity.method_11066()) {
				this.bindTexture(OPEN_EYE_TEX);
			} else {
				this.bindTexture(CLOSED_EYE_TEX);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.3F + k * 0.2F, (float)f + 0.5F);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(-vec2f.y, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(vec2f.x, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			this.field_4376.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
			GlStateManager.popMatrix();
		}

		super.render(conduitBlockEntity, d, e, f, g, i);
	}

	@Environment(EnvType.CLIENT)
	static class class_830 extends Model {
		private final Cuboid field_4381;

		public class_830() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.field_4381 = new Cuboid(this, 0, 0);
			this.field_4381.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		}

		@Override
		public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
			this.field_4381.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_831 extends Model {
		private final Cuboid field_4382;

		public class_831() {
			this.textureWidth = 8;
			this.textureHeight = 8;
			this.field_4382 = new Cuboid(this, 0, 0);
			this.field_4382.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
		}

		@Override
		public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
			this.field_4382.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_832 extends Model {
		private final Cuboid field_4383;

		public class_832() {
			this.textureWidth = 32;
			this.textureHeight = 16;
			this.field_4383 = new Cuboid(this, 0, 0);
			this.field_4383.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		}

		@Override
		public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
			this.field_4383.render(k);
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_833 extends Model {
		public static int field_4385 = 22;
		private final Cuboid[] field_4386 = new Cuboid[field_4385];
		private int field_4384;

		public class_833() {
			this.textureWidth = 64;
			this.textureHeight = 1024;

			for (int i = 0; i < field_4385; i++) {
				this.field_4386[i] = new Cuboid(this, 0, 32 * i);
				this.field_4386[i].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
			}
		}

		@Override
		public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
			this.field_4386[this.field_4384].render(k);
		}

		public void method_3573(int i) {
			this.field_4384 = i;
		}
	}
}
