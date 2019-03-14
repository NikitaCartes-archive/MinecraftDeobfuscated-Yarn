package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemUsageContext {
	protected final PlayerEntity player;
	protected final BlockHitResult hitResult;
	protected final World world;
	protected final ItemStack stack;

	public ItemUsageContext(PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		this(playerEntity.world, playerEntity, itemStack, blockHitResult);
	}

	protected ItemUsageContext(World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		this.player = playerEntity;
		this.hitResult = blockHitResult;
		this.stack = itemStack;
		this.world = world;
	}

	public BlockPos getBlockPos() {
		return this.hitResult.getBlockPos();
	}

	public Direction getFacing() {
		return this.hitResult.getSide();
	}

	public Vec3d getPos() {
		return this.hitResult.getPos();
	}

	public boolean method_17699() {
		return this.hitResult.method_17781();
	}

	public ItemStack getItemStack() {
		return this.stack;
	}

	@Nullable
	public PlayerEntity getPlayer() {
		return this.player;
	}

	public World getWorld() {
		return this.world;
	}

	public Direction getPlayerHorizontalFacing() {
		return this.player == null ? Direction.NORTH : this.player.getHorizontalFacing();
	}

	public boolean isPlayerSneaking() {
		return this.player != null && this.player.isSneaking();
	}

	public float getPlayerYaw() {
		return this.player == null ? 0.0F : this.player.yaw;
	}
}
