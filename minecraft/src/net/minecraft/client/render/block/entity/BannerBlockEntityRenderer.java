package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.ModelPart;
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
		RenderSystem.pushMatrix();
		ModelPart modelPart = this.model.method_2791();
		long l;
		if (bl) {
			l = 0L;
			RenderSystem.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			modelPart.visible = true;
		} else {
			l = bannerBlockEntity.getWorld().getTime();
			BlockState blockState = bannerBlockEntity.getCachedState();
			if (blockState.getBlock() instanceof BannerBlock) {
				RenderSystem.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
				RenderSystem.rotatef((float)(-(Integer)blockState.get(BannerBlock.ROTATION) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
				modelPart.visible = true;
			} else {
				RenderSystem.translatef((float)d + 0.5F, (float)e - 0.16666667F, (float)f + 0.5F);
				RenderSystem.rotatef(-((Direction)blockState.get(WallBannerBlock.FACING)).asRotation(), 0.0F, 1.0F, 0.0F);
				RenderSystem.translatef(0.0F, -0.3125F, -0.4375F);
				modelPart.visible = false;
			}
		}

		BlockPos blockPos = bannerBlockEntity.getPos();
		float j = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
		this.model.method_2792().pitch = (-0.0125F + 0.01F * MathHelper.cos(j * (float) Math.PI * 0.02F)) * (float) Math.PI;
		RenderSystem.enableRescaleNormal();
		Identifier identifier = this.getTextureId(bannerBlockEntity);
		if (identifier != null) {
			this.bindTexture(identifier);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6666667F, -0.6666667F, -0.6666667F);
			this.model.method_2793();
			RenderSystem.popMatrix();
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.popMatrix();
	}

	@Nullable
	private Identifier getTextureId(BannerBlockEntity bannerBlockEntity) {
		return TextureCache.BANNER.get(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
	}
}
