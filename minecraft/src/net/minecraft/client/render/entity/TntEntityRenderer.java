package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntEntityRenderer extends EntityRenderer<TntEntity> {
	public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void method_4135(TntEntity tntEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22904(0.0, 0.5, 0.0);
		if ((float)tntEntity.getFuseTimer() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			arg.method_22905(j, j, j);
		}

		int k = tntEntity.getLightmapCoordinates();
		arg.method_22907(Vector3f.field_20705.method_23214(-90.0F, true));
		arg.method_22904(-0.5, -0.5, 0.5);
		if (tntEntity.getFuseTimer() / 5 % 2 == 0) {
			TntMinecartEntityRenderer.method_23190(Blocks.TNT.getDefaultState(), arg, arg2, k);
		} else {
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.TNT.getDefaultState(), arg, arg2, k, 0, 10);
		}

		arg.method_22909();
		super.render(tntEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4136(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
