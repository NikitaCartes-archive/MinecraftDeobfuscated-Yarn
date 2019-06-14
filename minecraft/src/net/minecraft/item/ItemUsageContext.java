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
	protected final BlockHitResult field_17543;
	protected final World field_8945;
	protected final ItemStack stack;

	public ItemUsageContext(PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		this(playerEntity.field_6002, playerEntity, hand, playerEntity.getStackInHand(hand), blockHitResult);
	}

	protected ItemUsageContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		this.player = playerEntity;
		this.hand = hand;
		this.field_17543 = blockHitResult;
		this.stack = itemStack;
		this.field_8945 = world;
	}

	public BlockPos getBlockPos() {
		return this.field_17543.getBlockPos();
	}

	public Direction getSide() {
		return this.field_17543.getSide();
	}

	public Vec3d method_17698() {
		return this.field_17543.method_17784();
	}

	public boolean method_17699() {
		return this.field_17543.method_17781();
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

	public World method_8045() {
		return this.field_8945;
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
