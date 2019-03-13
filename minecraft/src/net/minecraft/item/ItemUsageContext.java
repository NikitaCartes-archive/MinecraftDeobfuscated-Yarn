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
	protected final BlockHitResult field_17543;
	protected final World field_8945;
	protected final ItemStack stack;

	public ItemUsageContext(PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		this(playerEntity.field_6002, playerEntity, itemStack, blockHitResult);
	}

	protected ItemUsageContext(World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockHitResult blockHitResult) {
		this.player = playerEntity;
		this.field_17543 = blockHitResult;
		this.stack = itemStack;
		this.field_8945 = world;
	}

	public BlockPos method_8037() {
		return this.field_17543.method_17777();
	}

	public Direction method_8038() {
		return this.field_17543.method_17780();
	}

	public Vec3d method_17698() {
		return this.field_17543.method_17784();
	}

	public boolean method_17699() {
		return this.field_17543.method_17781();
	}

	public ItemStack getItemStack() {
		return this.stack;
	}

	@Nullable
	public PlayerEntity getPlayer() {
		return this.player;
	}

	public World method_8045() {
		return this.field_8945;
	}

	public Direction method_8042() {
		return this.player == null ? Direction.NORTH : this.player.method_5735();
	}

	public boolean isPlayerSneaking() {
		return this.player != null && this.player.isSneaking();
	}

	public float getPlayerYaw() {
		return this.player == null ? 0.0F : this.player.yaw;
	}
}
