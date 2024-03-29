package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/" + TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().getPath() + ".png");
	private static final Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES
		.stream()
		.map(spriteId -> new Identifier("textures/" + spriteId.getTextureId().getPath() + ".png"))
		.toArray(i -> new Identifier[i]);

	public ShulkerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ShulkerEntityModel<>(context.getPart(EntityModelLayers.SHULKER)), 0.0F);
		this.addFeature(new ShulkerHeadFeatureRenderer(this));
	}

	public Vec3d getPositionOffset(ShulkerEntity shulkerEntity, float f) {
		return ((Vec3d)shulkerEntity.getRenderPositionOffset(f).orElse(super.getPositionOffset(shulkerEntity, f))).multiply((double)shulkerEntity.getScale());
	}

	public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(shulkerEntity, frustum, d, e, f)
			? true
			: shulkerEntity.getRenderPositionOffset(0.0F)
				.filter(
					renderPositionOffset -> {
						EntityType<?> entityType = shulkerEntity.getType();
						float fxx = entityType.getHeight() / 2.0F;
						float g = entityType.getWidth() / 2.0F;
						Vec3d vec3d = Vec3d.ofBottomCenter(shulkerEntity.getBlockPos());
						return frustum.isVisible(
							new Box(renderPositionOffset.x, renderPositionOffset.y + (double)fxx, renderPositionOffset.z, vec3d.x, vec3d.y + (double)fxx, vec3d.z)
								.expand((double)g, (double)fxx, (double)g)
						);
					}
				)
				.isPresent();
	}

	public Identifier getTexture(ShulkerEntity shulkerEntity) {
		return getTexture(shulkerEntity.getColor());
	}

	public static Identifier getTexture(@Nullable DyeColor shulkerColor) {
		return shulkerColor == null ? TEXTURE : COLORED_TEXTURES[shulkerColor.getId()];
	}

	protected void setupTransforms(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(shulkerEntity, matrixStack, f, g + 180.0F, h, i);
		matrixStack.multiply(shulkerEntity.getAttachedFace().getOpposite().getRotationQuaternion(), 0.0F, 0.5F, 0.0F);
	}
}
