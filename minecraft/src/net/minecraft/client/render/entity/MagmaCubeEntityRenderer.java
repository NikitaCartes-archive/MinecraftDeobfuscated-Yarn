package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity, MagmaCubeEntityModel<MagmaCubeEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/slime/magmacube.png");

	public MagmaCubeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new MagmaCubeEntityModel<>(), 0.25F);
	}

	protected Identifier method_4001(MagmaCubeEntity magmaCubeEntity) {
		return SKIN;
	}

	protected void method_4000(MagmaCubeEntity magmaCubeEntity, float f) {
		int i = magmaCubeEntity.getSize();
		float g = MathHelper.lerp(f, magmaCubeEntity.sizeZ, magmaCubeEntity.sizeY) / ((float)i * 0.5F + 1.0F);
		float h = 1.0F / (g + 1.0F);
		GlStateManager.scalef(h * (float)i, 1.0F / h * (float)i, h * (float)i);
	}
}
