package net.minecraft.client.render;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

@Environment(EnvType.CLIENT)
public class Camera {
	private boolean ready;
	private BlockView area;
	private Entity focusedEntity;
	private Vec3d pos = Vec3d.ZERO;
	private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
	private final Vec3f horizontalPlane = new Vec3f(0.0F, 0.0F, 1.0F);
	private final Vec3f verticalPlane = new Vec3f(0.0F, 1.0F, 0.0F);
	private final Vec3f diagonalPlane = new Vec3f(1.0F, 0.0F, 0.0F);
	private float pitch;
	private float yaw;
	private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private boolean thirdPerson;
	private float cameraY;
	private float lastCameraY;
	public static final float field_32133 = 0.083333336F;

	public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
		this.ready = true;
		this.area = area;
		this.focusedEntity = focusedEntity;
		this.thirdPerson = thirdPerson;
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
				.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this.focusedEntity));
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
		this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
		this.rotation.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
		this.rotation.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
		this.horizontalPlane.set(0.0F, 0.0F, 1.0F);
		this.horizontalPlane.rotate(this.rotation);
		this.verticalPlane.set(0.0F, 1.0F, 0.0F);
		this.verticalPlane.rotate(this.rotation);
		this.diagonalPlane.set(1.0F, 0.0F, 0.0F);
		this.diagonalPlane.rotate(this.rotation);
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

	public Quaternion getRotation() {
		return this.rotation;
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

	/**
	 * {@return the field of vision of this camera}
	 * 
	 * @see GameRenderer#CAMERA_DEPTH
	 * @see net.minecraft.util.math.Matrix4f#viewboxMatrix
	 */
	public Camera.Projection getProjection() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		double d = (double)minecraftClient.getWindow().getFramebufferWidth() / (double)minecraftClient.getWindow().getFramebufferHeight();
		double e = Math.tan((double)((float)minecraftClient.options.getFov().getValue().intValue() * (float) (Math.PI / 180.0)) / 2.0) * 0.05F;
		double f = e * d;
		Vec3d vec3d = new Vec3d(this.horizontalPlane).multiply(0.05F);
		Vec3d vec3d2 = new Vec3d(this.diagonalPlane).multiply(f);
		Vec3d vec3d3 = new Vec3d(this.verticalPlane).multiply(e);
		return new Camera.Projection(vec3d, vec3d2, vec3d3);
	}

	public CameraSubmersionType getSubmersionType() {
		if (!this.ready) {
			return CameraSubmersionType.NONE;
		} else {
			FluidState fluidState = this.area.getFluidState(this.blockPos);
			if (fluidState.isIn(FluidTags.WATER) && this.pos.y < (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))) {
				return CameraSubmersionType.WATER;
			} else {
				Camera.Projection projection = this.getProjection();

				for (Vec3d vec3d : Arrays.asList(
					projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft()
				)) {
					Vec3d vec3d2 = this.pos.add(vec3d);
					BlockPos blockPos = new BlockPos(vec3d2);
					FluidState fluidState2 = this.area.getFluidState(blockPos);
					if (fluidState2.isIn(FluidTags.LAVA)) {
						if (vec3d2.y <= (double)(fluidState2.getHeight(this.area, blockPos) + (float)blockPos.getY())) {
							return CameraSubmersionType.LAVA;
						}
					} else {
						BlockState blockState = this.area.getBlockState(blockPos);
						if (blockState.isOf(Blocks.POWDER_SNOW)) {
							return CameraSubmersionType.POWDER_SNOW;
						}
					}
				}

				return CameraSubmersionType.NONE;
			}
		}
	}

	public final Vec3f getHorizontalPlane() {
		return this.horizontalPlane;
	}

	public final Vec3f getVerticalPlane() {
		return this.verticalPlane;
	}

	public final Vec3f getDiagonalPlane() {
		return this.diagonalPlane;
	}

	public void reset() {
		this.area = null;
		this.focusedEntity = null;
		this.ready = false;
	}

	/**
	 * A projection of a camera. It is a 2-D rectangle in a 3-D volume.
	 * 
	 * @see Camera#getProjection()
	 */
	@Environment(EnvType.CLIENT)
	public static class Projection {
		final Vec3d center;
		/**
		 * Half of the width (x) of the rectangle.
		 */
		private final Vec3d x;
		/**
		 * Half of the height (y) of the rectangle.
		 */
		private final Vec3d y;

		Projection(Vec3d center, Vec3d x, Vec3d y) {
			this.center = center;
			this.x = x;
			this.y = y;
		}

		public Vec3d getBottomRight() {
			return this.center.add(this.y).add(this.x);
		}

		public Vec3d getTopRight() {
			return this.center.add(this.y).subtract(this.x);
		}

		public Vec3d getBottomLeft() {
			return this.center.subtract(this.y).add(this.x);
		}

		public Vec3d getTopLeft() {
			return this.center.subtract(this.y).subtract(this.x);
		}

		public Vec3d getPosition(float factorX, float factorY) {
			return this.center.add(this.y.multiply((double)factorY)).subtract(this.x.multiply((double)factorX));
		}
	}
}
