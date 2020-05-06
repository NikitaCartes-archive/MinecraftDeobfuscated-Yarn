package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public static final Identifier TEXTURE = new Identifier("textures/" + TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().getPath() + ".png");
	public static final Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES
		.stream()
		.map(spriteIdentifier -> new Identifier("textures/" + spriteIdentifier.getTextureId().getPath() + ".png"))
		.toArray(Identifier[]::new);

	public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ShulkerEntityModel<>(), 0.0F);
		this.addFeature(new ShulkerHeadFeatureRenderer(this));
	}

	public Vec3d getPositionOffset(ShulkerEntity shulkerEntity, float f) {
		int i = shulkerEntity.getTeleportLerpTimer();
		if (i > 0 && shulkerEntity.hasAttachedBlock()) {
			BlockPos blockPos = shulkerEntity.getAttachedBlock();
			BlockPos blockPos2 = shulkerEntity.getPrevAttachedBlock();
			double d = (double)((float)i - f) / 6.0;
			d *= d;
			double e = (double)(blockPos.getX() - blockPos2.getX()) * d;
			double g = (double)(blockPos.getY() - blockPos2.getY()) * d;
			double h = (double)(blockPos.getZ() - blockPos2.getZ()) * d;
			return new Vec3d(-e, -g, -h);
		} else {
			return super.getPositionOffset(shulkerEntity, f);
		}
	}

	public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
		if (super.shouldRender(shulkerEntity, frustum, d, e, f)) {
			return true;
		} else {
			if (shulkerEntity.getTeleportLerpTimer() > 0 && shulkerEntity.hasAttachedBlock()) {
				Vec3d vec3d = Vec3d.of(shulkerEntity.getAttachedBlock());
				Vec3d vec3d2 = Vec3d.of(shulkerEntity.getPrevAttachedBlock());
				if (frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
					return true;
				}
			}

			return false;
		}
	}

	public Identifier getTexture(ShulkerEntity shulkerEntity) {
		return shulkerEntity.getColor() == null ? TEXTURE : COLORED_TEXTURES[shulkerEntity.getColor().getId()];
	}

	protected void setupTransforms(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(shulkerEntity, matrixStack, f, g, h);
		matrixStack.translate(0.0, 0.5, 0.0);
		matrixStack.multiply(shulkerEntity.getAttachedFace().getOpposite().getRotationQuaternion());
		matrixStack.translate(0.0, -0.5, 0.0);
	}

	protected void scale(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f) {
		float g = 0.999F;
		matrixStack.scale(0.999F, 0.999F, 0.999F);
	}
}
