package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1001;
import net.minecraft.class_612;
import net.minecraft.class_615;
import net.minecraft.client.model.Model;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends EntityMobRenderer<TropicalFishEntity> {
	private final class_612 field_4800 = new class_612();
	private final class_615 field_4799 = new class_615();

	public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_612(), 0.15F);
		this.addLayer(new class_1001(this));
	}

	@Nullable
	protected Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.method_6650();
	}

	public void method_4140(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h) {
		this.model = (Model)(tropicalFishEntity.method_6654() == 0 ? this.field_4800 : this.field_4799);
		float[] fs = tropicalFishEntity.method_6658();
		GlStateManager.color3f(fs[0], fs[1], fs[2]);
		super.method_4072(tropicalFishEntity, d, e, f, g, h);
	}

	protected void method_4142(TropicalFishEntity tropicalFishEntity, float f, float g, float h) {
		super.method_4058(tropicalFishEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		GlStateManager.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!tropicalFishEntity.isInsideWater()) {
			GlStateManager.translatef(0.2F, 0.1F, 0.0F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
