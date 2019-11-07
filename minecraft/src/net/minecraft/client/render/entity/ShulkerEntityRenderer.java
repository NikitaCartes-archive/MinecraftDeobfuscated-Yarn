package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.feature.ShulkerSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public static final Identifier SKIN = new Identifier("textures/" + ModelLoader.field_20845.getPath() + ".png");
	public static final Identifier[] SKIN_COLOR = (Identifier[])ModelLoader.field_20846
		.stream()
		.map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png"))
		.toArray(Identifier[]::new);

	public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ShulkerEntityModel<>(), 0.0F);
		this.addFeature(new ShulkerSomethingFeatureRenderer(this));
	}

	public Vec3d method_23189(ShulkerEntity shulkerEntity, float f) {
		int i = shulkerEntity.method_7113();
		if (i > 0 && shulkerEntity.method_7117()) {
			BlockPos blockPos = shulkerEntity.getAttachedBlock();
			BlockPos blockPos2 = shulkerEntity.method_7120();
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

	public boolean method_4112(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
		if (super.method_4068(shulkerEntity, frustum, d, e, f)) {
			return true;
		} else {
			if (shulkerEntity.method_7113() > 0 && shulkerEntity.method_7117()) {
				Vec3d vec3d = new Vec3d(shulkerEntity.getAttachedBlock());
				Vec3d vec3d2 = new Vec3d(shulkerEntity.method_7120());
				if (frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
					return true;
				}
			}

			return false;
		}
	}

	public Identifier method_4111(ShulkerEntity shulkerEntity) {
		return shulkerEntity.getColor() == null ? SKIN : SKIN_COLOR[shulkerEntity.getColor().getId()];
	}

	protected void method_4114(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(shulkerEntity, matrixStack, f, g, h);
		matrixStack.translate(0.0, 0.5, 0.0);
		matrixStack.multiply(shulkerEntity.getAttachedFace().getOpposite().getRotationQuaternion());
		matrixStack.translate(0.0, -0.5, 0.0);
	}

	protected void method_4109(ShulkerEntity shulkerEntity, MatrixStack matrixStack, float f) {
		float g = 0.999F;
		matrixStack.scale(0.999F, 0.999F, 0.999F);
	}
}
