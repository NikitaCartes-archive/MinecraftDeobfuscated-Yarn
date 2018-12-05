package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_998 implements LayerEntityRenderer<AbstractClientPlayerEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
	private final PlayerEntityRenderer field_4899;
	private final class_998.class_999 field_4897;

	public class_998(PlayerEntityRenderer playerEntityRenderer) {
		this.field_4899 = playerEntityRenderer;
		this.field_4897 = new class_998.class_999();
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (abstractClientPlayerEntity.method_6123()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_4899.bindTexture(TEXTURE);

			for (int m = 0; m < 3; m++) {
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(i * (float)(-(45 + m * 5)), 0.0F, 1.0F, 0.0F);
				float n = 0.75F * (float)m;
				GlStateManager.scalef(n, n, n);
				GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)m, 0.0F);
				this.field_4897.render(abstractClientPlayerEntity, f, g, i, j, k, l);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	static class class_999 extends Model {
		private final Cuboid field_4900;

		public class_999() {
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.field_4900 = new Cuboid(this, 0, 0);
			this.field_4900.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
		}

		@Override
		public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
			this.field_4900.render(k);
		}
	}
}
