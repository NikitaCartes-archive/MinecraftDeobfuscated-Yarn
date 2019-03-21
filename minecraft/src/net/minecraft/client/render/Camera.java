package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;

@Environment(EnvType.CLIENT)
public class Camera {
	private boolean ready;
	private BlockView area;
	private Entity focusedEntity;
	private net.minecraft.util.math.Vec3d pos;
	private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
	private net.minecraft.util.math.Vec3d field_18714;
	private net.minecraft.util.math.Vec3d field_18715;
	private net.minecraft.util.math.Vec3d field_18716;
	private float pitch;
	private float yaw;
	private boolean thirdPerson;
	private boolean inverseView;
	private float field_18721;
	private float field_18722;

	public void update(BlockView blockView, Entity entity, boolean bl, boolean bl2, float f) {
		this.ready = true;
		this.area = blockView;
		this.focusedEntity = entity;
		this.thirdPerson = bl;
		this.inverseView = bl2;
		this.setRotation(entity.getYaw(f), entity.getPitch(f));
		this.setPos(
			MathHelper.lerp((double)f, entity.prevX, entity.x),
			MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)MathHelper.lerp(f, this.field_18722, this.field_18721),
			MathHelper.lerp((double)f, entity.prevZ, entity.z)
		);
		if (bl) {
			if (bl2) {
				this.addRotation(180.0F, 0.0F);
				this.addRotation(0.0F, -this.pitch * 2.0F);
			}

			this.moveBy(-this.method_19318(4.0), 0.0, 0.0);
			this.method_19319(
				MathHelper.lerp((double)f, entity.prevX, entity.x),
				MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)MathHelper.lerp(f, this.field_18722, this.field_18721),
				MathHelper.lerp((double)f, entity.prevZ, entity.z)
			);
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			Direction direction = ((LivingEntity)entity).method_18401();
			this.setRotation(direction != null ? direction.asRotation() - 180.0F : 0.0F, 0.0F);
			this.moveBy(0.0, 0.3, 0.0);
		} else {
			this.moveBy(-0.05F, 0.0, 0.0);
		}

		GlStateManager.rotatef(this.pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(this.yaw + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	public void updateEyeHeight() {
		if (this.focusedEntity != null) {
			this.field_18722 = this.field_18721;
			this.field_18721 = this.field_18721 + (this.focusedEntity.getStandingEyeHeight() - this.field_18721) * 0.5F;
		}
	}

	private double method_19318(double d) {
		for (int i = 0; i < 8; i++) {
			float f = (float)((i & 1) * 2 - 1);
			float g = (float)((i >> 1 & 1) * 2 - 1);
			float h = (float)((i >> 2 & 1) * 2 - 1);
			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;
			net.minecraft.util.math.Vec3d vec3d = this.pos.add((double)f, (double)g, (double)h);
			net.minecraft.util.math.Vec3d vec3d2 = new net.minecraft.util.math.Vec3d(
				this.pos.x - this.field_18714.x * d + (double)f + (double)h,
				this.pos.y - this.field_18714.y * d + (double)g,
				this.pos.z - this.field_18714.z * d + (double)h
			);
			HitResult hitResult = this.area
				.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this.focusedEntity));
			if (hitResult.getType() != HitResult.Type.NONE) {
				double e = hitResult.getPos().distanceTo(this.pos);
				if (e < d) {
					d = e;
				}
			}
		}

		return d;
	}

	protected void method_19319(double d, double e, double f) {
		double g = d - this.pos.x;
		double h = e - this.pos.y;
		double i = f - this.pos.z;
		double j = (double)MathHelper.sqrt(g * g + i * i);
		this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(h, j) * 180.0F / (float)Math.PI)));
		this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(i, g) * 180.0F / (float)Math.PI) - 90.0F);
		this.updateRotation();
	}

	protected void moveBy(double d, double e, double f) {
		double g = this.field_18714.x * d + this.field_18715.x * e + this.field_18716.x * f;
		double h = this.field_18714.y * d + this.field_18715.y * e + this.field_18716.y * f;
		double i = this.field_18714.z * d + this.field_18715.z * e + this.field_18716.z * f;
		this.setPos(new net.minecraft.util.math.Vec3d(this.pos.x + g, this.pos.y + h, this.pos.z + i));
	}

	protected void addRotation(float f, float g) {
		this.pitch += g;
		this.yaw += f;
		this.updateRotation();
	}

	protected void updateRotation() {
		float f = MathHelper.cos((this.yaw + 90.0F) * (float) (Math.PI / 180.0));
		float g = MathHelper.sin((this.yaw + 90.0F) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(-this.pitch * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-this.pitch * (float) (Math.PI / 180.0));
		float j = MathHelper.cos((-this.pitch + 90.0F) * (float) (Math.PI / 180.0));
		float k = MathHelper.sin((-this.pitch + 90.0F) * (float) (Math.PI / 180.0));
		this.field_18714 = new net.minecraft.util.math.Vec3d((double)(f * h), (double)i, (double)(g * h));
		this.field_18715 = new net.minecraft.util.math.Vec3d((double)(f * j), (double)k, (double)(g * j));
		this.field_18716 = this.field_18714.crossProduct(this.field_18715).multiply(-1.0);
	}

	protected void setRotation(float f, float g) {
		this.pitch = g;
		this.yaw = f;
		this.updateRotation();
	}

	protected void setPos(double d, double e, double f) {
		this.setPos(new net.minecraft.util.math.Vec3d(d, e, f));
	}

	protected void setPos(net.minecraft.util.math.Vec3d vec3d) {
		this.pos = vec3d;
		this.blockPos.set(vec3d.x, vec3d.y, vec3d.z);
	}

	public net.minecraft.util.math.Vec3d getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public Entity getFocusedEntity() {
		return this.focusedEntity;
	}

	public boolean isReady() {
		return this.ready;
	}

	public boolean isThirdPerson() {
		return this.thirdPerson;
	}

	public FluidState getSubmergedFluidState() {
		if (!this.ready) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			FluidState fluidState = this.area.getFluidState(this.blockPos);
			return !fluidState.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))
				? Fluids.EMPTY.getDefaultState()
				: fluidState;
		}
	}

	public final net.minecraft.util.math.Vec3d method_19335() {
		return this.field_18714;
	}

	public final net.minecraft.util.math.Vec3d method_19336() {
		return this.field_18715;
	}

	public void reset() {
		this.area = null;
		this.focusedEntity = null;
		this.ready = false;
	}
}
