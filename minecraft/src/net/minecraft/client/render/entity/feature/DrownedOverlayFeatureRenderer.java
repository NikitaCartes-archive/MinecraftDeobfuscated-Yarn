package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends ZombieEntity> extends FeatureRenderer<T, DrownedEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
	private final DrownedEntityModel<T> model = new DrownedEntityModel<>(0.25F, 0.0F, 64, 64);

	public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4182(T zombieEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!zombieEntity.isInvisible()) {
			this.getModel().setAttributes(this.model);
			this.model.method_17077(zombieEntity, f, g, h);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(SKIN);
			this.model.method_17088(zombieEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
