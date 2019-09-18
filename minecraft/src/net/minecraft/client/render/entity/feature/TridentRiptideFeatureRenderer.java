package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
	private final TridentRiptideFeatureRenderer.TridentRiptideModel model = new TridentRiptideFeatureRenderer.TridentRiptideModel();

	public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4203(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (livingEntity.isUsingRiptide()) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(TEXTURE);

			for (int m = 0; m < 3; m++) {
				RenderSystem.pushMatrix();
				RenderSystem.rotatef(i * (float)(-(45 + m * 5)), 0.0F, 1.0F, 0.0F);
				float n = 0.75F * (float)m;
				RenderSystem.scalef(n, n, n);
				RenderSystem.translatef(0.0F, -0.2F + 0.6F * (float)m, 0.0F);
				this.model.method_17166(f, g, i, j, k, l);
				RenderSystem.popMatrix();
			}
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	static class TridentRiptideModel extends Model {
		private final ModelPart field_4900;

		public TridentRiptideModel() {
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.field_4900 = new ModelPart(this, 0, 0);
			this.field_4900.addCuboid(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
		}

		public void method_17166(float f, float g, float h, float i, float j, float k) {
			this.field_4900.render(k);
		}
	}
}
