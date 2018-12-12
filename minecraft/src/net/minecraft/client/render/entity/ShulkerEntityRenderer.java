package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_856;
import net.minecraft.client.render.entity.feature.ShulkerSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderer extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public static final Identifier field_4781 = new Identifier("textures/entity/shulker/shulker.png");
	public static final Identifier[] SKIN = new Identifier[]{
		new Identifier("textures/entity/shulker/shulker_white.png"),
		new Identifier("textures/entity/shulker/shulker_orange.png"),
		new Identifier("textures/entity/shulker/shulker_magenta.png"),
		new Identifier("textures/entity/shulker/shulker_light_blue.png"),
		new Identifier("textures/entity/shulker/shulker_yellow.png"),
		new Identifier("textures/entity/shulker/shulker_lime.png"),
		new Identifier("textures/entity/shulker/shulker_pink.png"),
		new Identifier("textures/entity/shulker/shulker_gray.png"),
		new Identifier("textures/entity/shulker/shulker_light_gray.png"),
		new Identifier("textures/entity/shulker/shulker_cyan.png"),
		new Identifier("textures/entity/shulker/shulker_purple.png"),
		new Identifier("textures/entity/shulker/shulker_blue.png"),
		new Identifier("textures/entity/shulker/shulker_brown.png"),
		new Identifier("textures/entity/shulker/shulker_green.png"),
		new Identifier("textures/entity/shulker/shulker_red.png"),
		new Identifier("textures/entity/shulker/shulker_black.png")
	};

	public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ShulkerEntityModel<>(), 0.0F);
		this.addFeature(new ShulkerSomethingFeatureRenderer(this));
	}

	public void method_4113(ShulkerEntity shulkerEntity, double d, double e, double f, float g, float h) {
		int i = shulkerEntity.method_7113();
		if (i > 0 && shulkerEntity.method_7117()) {
			BlockPos blockPos = shulkerEntity.getAttachedBlock();
			BlockPos blockPos2 = shulkerEntity.method_7120();
			double j = (double)((float)i - h) / 6.0;
			j *= j;
			double k = (double)(blockPos.getX() - blockPos2.getX()) * j;
			double l = (double)(blockPos.getY() - blockPos2.getY()) * j;
			double m = (double)(blockPos.getZ() - blockPos2.getZ()) * j;
			super.render(shulkerEntity, d - k, e - l, f - m, g, h);
		} else {
			super.render(shulkerEntity, d, e, f, g, h);
		}
	}

	public boolean method_4112(ShulkerEntity shulkerEntity, class_856 arg, double d, double e, double f) {
		if (super.method_4068(shulkerEntity, arg, d, e, f)) {
			return true;
		} else {
			if (shulkerEntity.method_7113() > 0 && shulkerEntity.method_7117()) {
				BlockPos blockPos = shulkerEntity.method_7120();
				BlockPos blockPos2 = shulkerEntity.getAttachedBlock();
				Vec3d vec3d = new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				Vec3d vec3d2 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				if (arg.method_3699(new BoundingBox(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
					return true;
				}
			}

			return false;
		}
	}

	protected Identifier getTexture(ShulkerEntity shulkerEntity) {
		return shulkerEntity.method_7121() == null ? field_4781 : SKIN[shulkerEntity.method_7121().getId()];
	}

	protected void method_4114(ShulkerEntity shulkerEntity, float f, float g, float h) {
		super.setupTransforms(shulkerEntity, f, g, h);
		switch (shulkerEntity.getAttachedFace()) {
			case DOWN:
			default:
				break;
			case EAST:
				GlStateManager.translatef(0.5F, 0.5F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case WEST:
				GlStateManager.translatef(-0.5F, 0.5F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case NORTH:
				GlStateManager.translatef(0.0F, 0.5F, -0.5F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.translatef(0.0F, 0.5F, 0.5F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				break;
			case UP:
				GlStateManager.translatef(0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		}
	}

	protected void method_4109(ShulkerEntity shulkerEntity, float f) {
		float g = 0.999F;
		GlStateManager.scalef(0.999F, 0.999F, 0.999F);
	}
}
