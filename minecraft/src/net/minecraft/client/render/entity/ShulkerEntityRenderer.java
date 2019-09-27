package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4604;
import net.minecraft.client.render.entity.feature.ShulkerSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
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

	public Vec3d method_23189(ShulkerEntity shulkerEntity, double d, double e, double f, float g) {
		int i = shulkerEntity.method_7113();
		if (i > 0 && shulkerEntity.method_7117()) {
			BlockPos blockPos = shulkerEntity.getAttachedBlock();
			BlockPos blockPos2 = shulkerEntity.method_7120();
			double h = (double)((float)i - g) / 6.0;
			h *= h;
			double j = (double)(blockPos.getX() - blockPos2.getX()) * h;
			double k = (double)(blockPos.getY() - blockPos2.getY()) * h;
			double l = (double)(blockPos.getZ() - blockPos2.getZ()) * h;
			return new Vec3d(-j, -k, -l);
		} else {
			return super.method_23169(shulkerEntity, d, e, f, g);
		}
	}

	public boolean method_4112(ShulkerEntity shulkerEntity, class_4604 arg, double d, double e, double f) {
		if (super.method_4068(shulkerEntity, arg, d, e, f)) {
			return true;
		} else {
			if (shulkerEntity.method_7113() > 0 && shulkerEntity.method_7117()) {
				BlockPos blockPos = shulkerEntity.method_7120();
				BlockPos blockPos2 = shulkerEntity.getAttachedBlock();
				Vec3d vec3d = new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				Vec3d vec3d2 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				if (arg.method_23093(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
					return true;
				}
			}

			return false;
		}
	}

	public Identifier method_4111(ShulkerEntity shulkerEntity) {
		return shulkerEntity.getColor() == null ? SKIN : SKIN_COLOR[shulkerEntity.getColor().getId()];
	}

	protected void method_4114(ShulkerEntity shulkerEntity, class_4587 arg, float f, float g, float h) {
		super.setupTransforms(shulkerEntity, arg, f, g, h);
		arg.method_22904(0.0, 0.5, 0.0);
		arg.method_22907(shulkerEntity.getAttachedFace().getOpposite().method_23224());
		arg.method_22904(0.0, -0.5, 0.0);
	}

	protected void method_4109(ShulkerEntity shulkerEntity, class_4587 arg, float f) {
		float g = 0.999F;
		arg.method_22905(0.999F, 0.999F, 0.999F);
	}
}
