package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingBannerBlock;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BannerBlockEntityModel;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity> {
	private final BannerBlockEntityModel model = new BannerBlockEntityModel();

	public void method_3546(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, int i) {
		float h = 0.6666667F;
		boolean bl = bannerBlockEntity.getWorld() == null;
		GlStateManager.pushMatrix();
		Cuboid cuboid = this.model.method_2791();
		long l;
		if (bl) {
			l = 0L;
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			cuboid.visible = true;
		} else {
			l = bannerBlockEntity.getWorld().getTime();
			BlockState blockState = bannerBlockEntity.method_11010();
			if (blockState.getBlock() instanceof StandingBannerBlock) {
				GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
				GlStateManager.rotatef((float)(-(Integer)blockState.method_11654(StandingBannerBlock.field_9924) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
				cuboid.visible = true;
			} else {
				GlStateManager.translatef((float)d + 0.5F, (float)e - 0.16666667F, (float)f + 0.5F);
				GlStateManager.rotatef(-((Direction)blockState.method_11654(WallBannerBlock.field_11722)).asRotation(), 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
				cuboid.visible = false;
			}
		}

		BlockPos blockPos = bannerBlockEntity.method_11016();
		float j = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
		this.model.method_2792().pitch = (-0.0125F + 0.01F * MathHelper.cos(j * (float) Math.PI * 0.02F)) * (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		Identifier identifier = this.method_3547(bannerBlockEntity);
		if (identifier != null) {
			this.method_3566(identifier);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
			this.model.method_2793();
			GlStateManager.popMatrix();
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@Nullable
	private Identifier method_3547(BannerBlockEntity bannerBlockEntity) {
		return TextureCache.BANNER.method_3331(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
	}
}
