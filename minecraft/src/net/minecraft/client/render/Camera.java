package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;

@Environment(EnvType.CLIENT)
public class Camera {
	private boolean ready;
	private BlockView area;
	private Entity focusedEntity;
	private Vec3d pos = Vec3d.ZERO;
	private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
	private final Vector3f horizontalPlane = new Vector3f(0.0F, 0.0F, 1.0F);
	private final Vector3f verticalPlane = new Vector3f(0.0F, 1.0F, 0.0F);
	private final Vector3f diagonalPlane = new Vector3f(1.0F, 0.0F, 0.0F);
	private float pitch;
	private float yaw;
	private final Quaternion field_21518 = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private boolean thirdPerson;
	private boolean inverseView;
	private float cameraY;
	private float lastCameraY;

	public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
		this.ready = true;
		this.area = area;
		this.focusedEntity = focusedEntity;
		this.thirdPerson = thirdPerson;
		this.inverseView = inverseView;
		this.setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
		this.setPos(
			MathHelper.lerp((double)tickDelta, focusedEntity.prevX, focusedEntity.getX()),
			MathHelper.lerp((double)tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY),
			MathHelper.lerp((double)tickDelta, focusedEntity.prevZ, focusedEntity.getZ())
		);
		if (thirdPerson) {
			if (inverseView) {
				this.setRotation(this.yaw + 180.0F, -this.pitch);
			}

			this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
		} else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
			Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
			this.setRotation(direction != null ? direction.asRotation() - 180.0F : 0.0F, 0.0F);
			this.moveBy(0.0, 0.3, 0.0);
		}
	}

	public void updateEyeHeight() {
		if (this.focusedEntity != null) {
			this.lastCameraY = this.cameraY;
			this.cameraY = this.cameraY + (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5F;
		}
	}

	private double clipToSpace(double desiredCameraDistance) {
		for (int i = 0; i < 8; i++) {
			float f = (float)((i & 1) * 2 - 1);
			float g = (float)((i >> 1 & 1) * 2 - 1);
			float h = (float)((i >> 2 & 1) * 2 - 1);
			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;
			Vec3d vec3d = this.pos.add((double)f, (double)g, (double)h);
			Vec3d vec3d2 = new Vec3d(
				this.pos.x - (double)this.horizontalPlane.getX() * desiredCameraDistance + (double)f + (double)h,
				this.pos.y - (double)this.horizontalPlane.getY() * desiredCameraDistance + (double)g,
				this.pos.z - (double)this.horizontalPlane.getZ() * desiredCameraDistance + (double)h
			);
			HitResult hitResult = this.area
				.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this.focusedEntity));
			if (hitResult.getType() != HitResult.Type.MISS) {
				double d = hitResult.getPos().distanceTo(this.pos);
				if (d < desiredCameraDistance) {
					desiredCameraDistance = d;
				}
			}
		}

		return desiredCameraDistance;
	}

	protected void moveBy(double x, double y, double z) {
		double d = (double)this.horizontalPlane.getX() * x + (double)this.verticalPlane.getX() * y + (double)this.diagonalPlane.getX() * z;
		double e = (double)this.horizontalPlane.getY() * x + (double)this.verticalPlane.getY() * y + (double)this.diagonalPlane.getY() * z;
		double f = (double)this.horizontalPlane.getZ() * x + (double)this.verticalPlane.getZ() * y + (double)this.diagonalPlane.getZ() * z;
		this.setPos(new Vec3d(this.pos.x + d, this.pos.y + e, this.pos.z + f));
	}

	protected void setRotation(float yaw, float pitch) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.field_21518.method_23758(0.0F, 0.0F, 0.0F, 1.0F);
		this.field_21518.hamiltonProduct(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
		this.field_21518.hamiltonProduct(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
		this.horizontalPlane.set(0.0F, 0.0F, 1.0F);
		this.horizontalPlane.method_19262(this.field_21518);
		this.verticalPlane.set(0.0F, 1.0F, 0.0F);
		this.verticalPlane.method_19262(this.field_21518);
		this.diagonalPlane.set(1.0F, 0.0F, 0.0F);
		this.diagonalPlane.method_19262(this.field_21518);
	}

	protected void setPos(double x, double y, double z) {
		this.setPos(new Vec3d(x, y, z));
	}

	protected void setPos(Vec3d pos) {
		this.pos = pos;
		this.blockPos.set(pos.x, pos.y, pos.z);
	}

	public Vec3d getPos() {
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

	public Quaternion method_23767() {
		return this.field_21518;
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

	public final Vector3f getHorizontalPlane() {
		return this.horizontalPlane;
	}

	public final Vector3f getVerticalPlane() {
		return this.verticalPlane;
	}

	public void reset() {
		this.area = null;
		this.focusedEntity = null;
		this.ready = false;
	}
}
