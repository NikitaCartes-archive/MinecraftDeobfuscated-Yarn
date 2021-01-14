package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity> extends CompositeEntityModel<T> {
	private final ModelPart base;
	private final ModelPart lid = new ModelPart(64, 64, 0, 0);
	private final ModelPart head;

	public ShulkerEntityModel() {
		super(RenderLayer::getEntityCutoutNoCullZOffset);
		this.base = new ModelPart(64, 64, 0, 28);
		this.head = new ModelPart(64, 64, 0, 52);
		this.lid.addCuboid(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F);
		this.lid.setPivot(0.0F, 24.0F, 0.0F);
		this.base.addCuboid(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F);
		this.base.setPivot(0.0F, 24.0F, 0.0F);
		this.head.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F);
		this.head.setPivot(0.0F, 12.0F, 0.0F);
	}

	public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)shulkerEntity.age;
		float l = (0.5F + shulkerEntity.getOpenProgress(k)) * (float) Math.PI;
		float m = -1.0F + MathHelper.sin(l);
		float n = 0.0F;
		if (l > (float) Math.PI) {
			n = MathHelper.sin(h * 0.1F) * 0.7F;
		}

		this.lid.setPivot(0.0F, 16.0F + MathHelper.sin(l) * 8.0F + n, 0.0F);
		if (shulkerEntity.getOpenProgress(k) > 0.3F) {
			this.lid.yaw = m * m * m * m * (float) Math.PI * 0.125F;
		} else {
			this.lid.yaw = 0.0F;
		}

		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = (shulkerEntity.headYaw - 180.0F - shulkerEntity.bodyYaw) * (float) (Math.PI / 180.0);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.base, this.lid);
	}

	public ModelPart getBottomShell() {
		return this.base;
	}

	public ModelPart getTopShell() {
		return this.lid;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
