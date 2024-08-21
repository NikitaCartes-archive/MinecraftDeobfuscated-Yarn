package net.minecraft.entity.vehicle;

import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class MinecartController {
	protected final AbstractMinecartEntity minecart;

	protected MinecartController(AbstractMinecartEntity minecart) {
		this.minecart = minecart;
	}

	public void setPos(double x, double y, double z, float yaw, float pitch, int i) {
		this.setPos(x, y, z);
		this.setYaw(yaw % 360.0F);
		this.setPitch(pitch % 360.0F);
	}

	public double getLerpTargetX() {
		return this.getX();
	}

	public double getLerpTargetY() {
		return this.getY();
	}

	public double getLerpTargetZ() {
		return this.getZ();
	}

	public float getLerpTargetPitch() {
		return this.getPitch();
	}

	public float getLerpTargetYaw() {
		return this.getYaw();
	}

	public void setLerpTargetVelocity(double x, double y, double z) {
		this.setVelocity(x, y, z);
	}

	public abstract void tick();

	public World getWorld() {
		return this.minecart.getWorld();
	}

	public abstract void moveOnRail();

	public abstract double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement);

	public abstract boolean handleCollision();

	public Vec3d getVelocity() {
		return this.minecart.getVelocity();
	}

	public void setVelocity(Vec3d velocity) {
		this.minecart.setVelocity(velocity);
	}

	public void setVelocity(double x, double y, double z) {
		this.minecart.setVelocity(x, y, z);
	}

	public Vec3d getPos() {
		return this.minecart.getPos();
	}

	public double getX() {
		return this.minecart.getX();
	}

	public double getY() {
		return this.minecart.getY();
	}

	public double getZ() {
		return this.minecart.getZ();
	}

	public void setPos(Vec3d pos) {
		this.minecart.setPosition(pos);
	}

	public void setPos(double x, double y, double z) {
		this.minecart.setPosition(x, y, z);
	}

	public float getPitch() {
		return this.minecart.getPitch();
	}

	public void setPitch(float pitch) {
		this.minecart.setPitch(pitch);
	}

	public float getYaw() {
		return this.minecart.getYaw();
	}

	public void setYaw(float yaw) {
		this.minecart.setYaw(yaw);
	}

	public Direction getHorizontalFacing() {
		return this.minecart.getHorizontalFacing();
	}

	public Vec3d limitSpeed(Vec3d velocity) {
		return velocity;
	}

	public abstract double getMaxSpeed();

	public abstract double getSpeedRetention();
}
