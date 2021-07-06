package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
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
		.map(spriteIdentifier -> new Identifier("textures/" + spriteIdentifier.getTextureId().getPath() + ".png"))
		.toArray(Identifier[]::new);

	public ShulkerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ShulkerEntityModel<>(context.getPart(EntityModelLayers.SHULKER)), 0.0F);
		this.addFeature(new ShulkerHeadFeatureRenderer(this));
	}

	public Vec3d getPositionOffset(ShulkerEntity shulkerEntity, float f) {
		return (Vec3d)shulkerEntity.method_33352(f).orElse(super.getPositionOffset(shulkerEntity, f));
	}

	public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(shulkerEntity, frustum, d, e, f)
			? true
			: shulkerEntity.method_33352(0.0F)
				.filter(
					vec3d -> {
						EntityType<?> entityType = shulkerEntity.getType();
						float fx = entityType.getHeight() / 2.0F;
						float g = entityType.getWidth() / 2.0F;
						Vec3d vec3d2 = Vec3d.ofBottomCenter(shulkerEntity.getBlockPos());
						return frustum.isVisible(
							new Box(vec3d.x, vec3d.y + (double)fx, vec3d.z, vec3d2.x, vec3d2.y + (double)fx, vec3d2.z).expand((double)g, (double)fx, (double)g)
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

	protected void setupTransforms(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(shulkerEntity, matrixStack, f, g + 180.0F, h);
		matrixStack.translate(0.0, 0.5, 0.0);
		matrixStack.multiply(shulkerEntity.getAttachedFace().getOpposite().getRotationQuaternion());
		matrixStack.translate(0.0, -0.5, 0.0);
	}
}
