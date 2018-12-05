package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemUsageContext {
	protected final PlayerEntity player;
	protected final float hitX;
	protected final float hitY;
	protected final float hitZ;
	protected final Direction field_8943;
	protected final World world;
	protected final ItemStack stack;
	protected final BlockPos pos;

	public ItemUsageContext(PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h) {
		this(playerEntity.world, playerEntity, itemStack, blockPos, direction, f, g, h);
	}

	protected ItemUsageContext(
		World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		this.player = playerEntity;
		this.field_8943 = direction;
		this.hitX = f;
		this.hitY = g;
		this.hitZ = h;
		this.pos = blockPos;
		this.stack = itemStack;
		this.world = world;
	}

	public BlockPos getPos() {
		return this.pos;
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

	public Direction method_8038() {
		return this.field_8943;
	}

	public float getHitX() {
		return this.hitX;
	}

	public float getHitY() {
		return this.hitY;
	}

	public float getHitZ() {
		return this.hitZ;
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
