package net.minecraft.server.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class ServerPlayerInteractionManager {
	public ServerWorld world;
	public ServerPlayerEntity player;
	private GameMode gameMode = GameMode.INVALID;
	private boolean field_14003;
	private int field_14002;
	private BlockPos field_14006 = BlockPos.ORIGIN;
	private int field_14000;
	private boolean field_14001;
	private BlockPos field_14004 = BlockPos.ORIGIN;
	private int field_14010;
	private int field_14009 = -1;

	public ServerPlayerInteractionManager(ServerWorld serverWorld) {
		this.world = serverWorld;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		gameMode.setAbilitites(this.player.abilities);
		this.player.method_7355();
		this.player.server.method_3760().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Type.UPDATE_GAMEMODE, this.player));
		this.world.updatePlayersSleeping();
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isSurvivalLike() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean isCreative() {
		return this.gameMode.isCreative();
	}

	public void setGameModeIfNotPresent(GameMode gameMode) {
		if (this.gameMode == GameMode.INVALID) {
			this.gameMode = gameMode;
		}

		this.setGameMode(this.gameMode);
	}

	public void update() {
		this.field_14000++;
		if (this.field_14001) {
			int i = this.field_14000 - this.field_14010;
			BlockState blockState = this.world.method_8320(this.field_14004);
			if (blockState.isAir()) {
				this.field_14001 = false;
			} else {
				float f = blockState.method_11589(this.player, this.player.field_6002, this.field_14004) * (float)(i + 1);
				int j = (int)(f * 10.0F);
				if (j != this.field_14009) {
					this.world.method_8517(this.player.getEntityId(), this.field_14004, j);
					this.field_14009 = j;
				}

				if (f >= 1.0F) {
					this.field_14001 = false;
					this.tryBreakBlock(this.field_14004);
				}
			}
		} else if (this.field_14003) {
			BlockState blockState2 = this.world.method_8320(this.field_14006);
			if (blockState2.isAir()) {
				this.world.method_8517(this.player.getEntityId(), this.field_14006, -1);
				this.field_14009 = -1;
				this.field_14003 = false;
			} else {
				int k = this.field_14000 - this.field_14002;
				float fx = blockState2.method_11589(this.player, this.player.field_6002, this.field_14004) * (float)(k + 1);
				int jx = (int)(fx * 10.0F);
				if (jx != this.field_14009) {
					this.world.method_8517(this.player.getEntityId(), this.field_14006, jx);
					this.field_14009 = jx;
				}
			}
		}
	}

	public void method_14263(BlockPos blockPos, Direction direction) {
		if (this.isCreative()) {
			if (!this.world.method_8506(null, blockPos, direction)) {
				this.tryBreakBlock(blockPos);
			}
		} else {
			if (this.gameMode.shouldLimitWorldModification()) {
				if (this.gameMode == GameMode.field_9219) {
					return;
				}

				if (!this.player.canModifyWorld()) {
					ItemStack itemStack = this.player.method_6047();
					if (itemStack.isEmpty()) {
						return;
					}

					CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos, false);
					if (!itemStack.method_7940(this.world.method_8514(), cachedBlockPosition)) {
						return;
					}
				}
			}

			this.world.method_8506(null, blockPos, direction);
			this.field_14002 = this.field_14000;
			float f = 1.0F;
			BlockState blockState = this.world.method_8320(blockPos);
			if (!blockState.isAir()) {
				blockState.method_11636(this.world, blockPos, this.player);
				f = blockState.method_11589(this.player, this.player.field_6002, blockPos);
			}

			if (!blockState.isAir() && f >= 1.0F) {
				this.tryBreakBlock(blockPos);
			} else {
				this.field_14003 = true;
				this.field_14006 = blockPos;
				int i = (int)(f * 10.0F);
				this.world.method_8517(this.player.getEntityId(), blockPos, i);
				this.player.field_13987.sendPacket(new BlockUpdateS2CPacket(this.world, blockPos));
				this.field_14009 = i;
			}
		}
	}

	public void method_14258(BlockPos blockPos) {
		if (blockPos.equals(this.field_14006)) {
			int i = this.field_14000 - this.field_14002;
			BlockState blockState = this.world.method_8320(blockPos);
			if (!blockState.isAir()) {
				float f = blockState.method_11589(this.player, this.player.field_6002, blockPos) * (float)(i + 1);
				if (f >= 0.7F) {
					this.field_14003 = false;
					this.world.method_8517(this.player.getEntityId(), blockPos, -1);
					this.tryBreakBlock(blockPos);
				} else if (!this.field_14001) {
					this.field_14003 = false;
					this.field_14001 = true;
					this.field_14004 = blockPos;
					this.field_14010 = this.field_14002;
				}
			}
		}
	}

	public void method_14269() {
		this.field_14003 = false;
		this.world.method_8517(this.player.getEntityId(), this.field_14006, -1);
	}

	private boolean destroyBlock(BlockPos blockPos) {
		BlockState blockState = this.world.method_8320(blockPos);
		blockState.getBlock().method_9576(this.world, blockPos, blockState, this.player);
		boolean bl = this.world.method_8650(blockPos);
		if (bl) {
			blockState.getBlock().method_9585(this.world, blockPos, blockState);
		}

		return bl;
	}

	public boolean tryBreakBlock(BlockPos blockPos) {
		BlockState blockState = this.world.method_8320(blockPos);
		if (!this.player.method_6047().getItem().method_7885(blockState, this.world, blockPos, this.player)) {
			return false;
		} else {
			BlockEntity blockEntity = this.world.method_8321(blockPos);
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
				this.world.method_8413(blockPos, blockState, blockState, 3);
				return false;
			} else {
				if (this.gameMode.shouldLimitWorldModification()) {
					if (this.gameMode == GameMode.field_9219) {
						return false;
					}

					if (!this.player.canModifyWorld()) {
						ItemStack itemStack = this.player.method_6047();
						if (itemStack.isEmpty()) {
							return false;
						}

						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, blockPos, false);
						if (!itemStack.method_7940(this.world.method_8514(), cachedBlockPosition)) {
							return false;
						}
					}
				}

				boolean bl = this.destroyBlock(blockPos);
				if (!this.isCreative()) {
					ItemStack itemStack2 = this.player.method_6047();
					boolean bl2 = this.player.method_7305(blockState);
					itemStack2.method_7952(this.world, blockState, blockPos, this.player);
					if (bl && bl2) {
						ItemStack itemStack3 = itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy();
						blockState.getBlock().method_9556(this.world, this.player, blockPos, blockState, blockEntity, itemStack3);
					}
				}

				return bl;
			}
		}
	}

	public ActionResult interactItem(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand) {
		if (this.gameMode == GameMode.field_9219) {
			return ActionResult.PASS;
		} else if (playerEntity.method_7357().isCooldown(itemStack.getItem())) {
			return ActionResult.PASS;
		} else {
			int i = itemStack.getAmount();
			int j = itemStack.getDamage();
			TypedActionResult<ItemStack> typedActionResult = itemStack.method_7913(world, playerEntity, hand);
			ItemStack itemStack2 = typedActionResult.getValue();
			if (itemStack2 == itemStack && itemStack2.getAmount() == i && itemStack2.getMaxUseTime() <= 0 && itemStack2.getDamage() == j) {
				return typedActionResult.getResult();
			} else if (typedActionResult.getResult() == ActionResult.field_5814 && itemStack2.getMaxUseTime() > 0 && !playerEntity.isUsingItem()) {
				return typedActionResult.getResult();
			} else {
				playerEntity.method_6122(hand, itemStack2);
				if (this.isCreative()) {
					itemStack2.setAmount(i);
					if (itemStack2.hasDurability()) {
						itemStack2.setDamage(j);
					}
				}

				if (itemStack2.isEmpty()) {
					playerEntity.method_6122(hand, ItemStack.EMPTY);
				}

				if (!playerEntity.isUsingItem()) {
					((ServerPlayerEntity)playerEntity).method_14204(playerEntity.field_7498);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ActionResult interactBlock(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand, BlockHitResult blockHitResult) {
		BlockPos blockPos = blockHitResult.method_17777();
		BlockState blockState = world.method_8320(blockPos);
		if (this.gameMode == GameMode.field_9219) {
			NameableContainerProvider nameableContainerProvider = blockState.method_17526(world, blockPos);
			if (nameableContainerProvider != null) {
				playerEntity.openContainer(nameableContainerProvider);
				return ActionResult.field_5812;
			} else {
				return ActionResult.PASS;
			}
		} else {
			boolean bl = !playerEntity.method_6047().isEmpty() || !playerEntity.method_6079().isEmpty();
			boolean bl2 = playerEntity.isSneaking() && bl;
			if (!bl2 && blockState.method_11629(world, playerEntity, hand, blockHitResult)) {
				return ActionResult.field_5812;
			} else if (!itemStack.isEmpty() && !playerEntity.method_7357().isCooldown(itemStack.getItem())) {
				ItemUsageContext itemUsageContext = new ItemUsageContext(playerEntity, playerEntity.method_5998(hand), blockHitResult);
				if (this.isCreative()) {
					int i = itemStack.getAmount();
					ActionResult actionResult = itemStack.method_7981(itemUsageContext);
					itemStack.setAmount(i);
					return actionResult;
				} else {
					return itemStack.method_7981(itemUsageContext);
				}
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public void setWorld(ServerWorld serverWorld) {
		this.world = serverWorld;
	}
}
