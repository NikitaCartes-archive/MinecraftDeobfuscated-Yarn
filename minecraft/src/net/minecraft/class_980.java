package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_980 implements LayerEntityRenderer<DrownedEntity> {
	private static final Identifier field_4854 = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
	private final DrownedEntityRenderer field_4853;
	private final DrownedEntityModel field_4855 = new DrownedEntityModel(0.25F, 0.0F, 64, 64);

	public class_980(DrownedEntityRenderer drownedEntityRenderer) {
		this.field_4853 = drownedEntityRenderer;
	}

	public void render(DrownedEntity drownedEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!drownedEntity.isInvisible()) {
			this.field_4855.setAttributes(this.field_4853.method_4038());
			this.field_4855.animateModel(drownedEntity, f, g, h);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_4853.bindTexture(field_4854);
			this.field_4855.render(drownedEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
