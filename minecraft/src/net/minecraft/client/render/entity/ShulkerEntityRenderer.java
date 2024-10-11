package net.minecraft.client.render.entity;

import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityRenderState, ShulkerEntityModel> {
	private static final Identifier TEXTURE = TexturedRenderLayers.SHULKER_TEXTURE_ID
		.getTextureId()
		.withPath((UnaryOperator<String>)(string -> "textures/" + string + ".png"));
	private static final Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES
		.stream()
		.map(spriteId -> spriteId.getTextureId().withPath((UnaryOperator<String>)(string -> "textures/" + string + ".png")))
		.toArray(Identifier[]::new);

	public ShulkerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ShulkerEntityModel(context.getPart(EntityModelLayers.SHULKER)), 0.0F);
	}

	public Vec3d getPositionOffset(ShulkerEntityRenderState shulkerEntityRenderState) {
		return shulkerEntityRenderState.renderPositionOffset;
	}

	public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
		if (super.shouldRender(shulkerEntity, frustum, d, e, f)) {
			return true;
		} else {
			Vec3d vec3d = shulkerEntity.getRenderPositionOffset(0.0F);
			if (vec3d == null) {
				return false;
			} else {
				EntityType<?> entityType = shulkerEntity.getType();
				float g = entityType.getHeight() / 2.0F;
				float h = entityType.getWidth() / 2.0F;
				Vec3d vec3d2 = Vec3d.ofBottomCenter(shulkerEntity.getBlockPos());
				return frustum.isVisible(new Box(vec3d.x, vec3d.y + (double)g, vec3d.z, vec3d2.x, vec3d2.y + (double)g, vec3d2.z).expand((double)h, (double)g, (double)h));
			}
		}
	}

	public Identifier getTexture(ShulkerEntityRenderState shulkerEntityRenderState) {
		return getTexture(shulkerEntityRenderState.color);
	}

	public ShulkerEntityRenderState createRenderState() {
		return new ShulkerEntityRenderState();
	}

	public void updateRenderState(ShulkerEntity shulkerEntity, ShulkerEntityRenderState shulkerEntityRenderState, float f) {
		super.updateRenderState(shulkerEntity, shulkerEntityRenderState, f);
		shulkerEntityRenderState.renderPositionOffset = (Vec3d)Objects.requireNonNullElse(shulkerEntity.getRenderPositionOffset(f), Vec3d.ZERO);
		shulkerEntityRenderState.color = shulkerEntity.getColor();
		shulkerEntityRenderState.openProgress = shulkerEntity.getOpenProgress(f);
		shulkerEntityRenderState.headYaw = shulkerEntity.headYaw;
		shulkerEntityRenderState.shellYaw = shulkerEntity.bodyYaw;
		shulkerEntityRenderState.facing = shulkerEntity.getAttachedFace();
	}

	public static Identifier getTexture(@Nullable DyeColor shulkerColor) {
		return shulkerColor == null ? TEXTURE : COLORED_TEXTURES[shulkerColor.getId()];
	}

	protected void setupTransforms(ShulkerEntityRenderState shulkerEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(shulkerEntityRenderState, matrixStack, f + 180.0F, g);
		matrixStack.multiply(shulkerEntityRenderState.facing.getOpposite().getRotationQuaternion(), 0.0F, 0.5F, 0.0F);
	}
}
