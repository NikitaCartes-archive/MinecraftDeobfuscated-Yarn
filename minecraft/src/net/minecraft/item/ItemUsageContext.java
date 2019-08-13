package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemUsageContext {
	protected final PlayerEntity player;
	protected final Hand hand;
	protected final BlockHitResult hit;
	protected final World world;
	protected final ItemStack stack;

	public ItemUsageContext(PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		this(playerEntity.world, playerEntity, hand, playerEntity.getStackInHand(hand), blockHitResult);
	}

	protected ItemUsageContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		this.player = playerEntity;
		this.hand = hand;
		this.hit = blockHitResult;
		this.stack = itemStack;
		this.world = world;
	}

	public BlockPos getBlockPos() {
		return this.hit.getBlockPos();
	}

	public Direction getSide() {
		return this.hit.getSide();
	}

	public Vec3d getHitPos() {
		return this.hit.getPos();
	}

	public boolean method_17699() {
		return this.hit.method_17781();
	}

	public ItemStack getStack() {
		return this.stack;
	}

	@Nullable
	public PlayerEntity getPlayer() {
		return this.player;
	}

	public Hand getHand() {
		return this.hand;
	}

	public World getWorld() {
		return this.world;
	}

	public Direction getPlayerFacing() {
		return this.player == null ? Direction.field_11043 : this.player.getHorizontalFacing();
	}

	public boolean isPlayerSneaking() {
		return this.player != null && this.player.isSneaking();
	}

	public float getPlayerYaw() {
		return this.player == null ? 0.0F : this.player.yaw;
	}
}
