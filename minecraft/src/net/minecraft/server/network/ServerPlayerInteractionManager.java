package net.minecraft.server.network;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.PlayerActionResponseS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerInteractionManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public ServerWorld world;
	public ServerPlayerEntity player;
	private GameMode gameMode = GameMode.NOT_SET;
	private boolean mining;
	private int startMiningTime;
	private BlockPos miningPos = BlockPos.ORIGIN;
	private int tickCounter;
	private boolean failedToMine;
	private BlockPos failedMiningPos = BlockPos.ORIGIN;
	private int failedStartMiningTime;
	private int blockBreakingProgress = -1;

	public ServerPlayerInteractionManager(ServerWorld serverWorld) {
		this.world = serverWorld;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		gameMode.setAbilitites(this.player.abilities);
		this.player.sendAbilitiesUpdate();
		this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, this.player));
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
		if (this.gameMode == GameMode.NOT_SET) {
			this.gameMode = gameMode;
		}

		this.setGameMode(this.gameMode);
	}

	public void update() {
		this.tickCounter++;
		if (this.failedToMine) {
			BlockState blockState = this.world.getBlockState(this.failedMiningPos);
			if (blockState.isAir()) {
				this.failedToMine = false;
			} else {
				float f = this.continueMining(blockState, this.failedMiningPos);
				if (f >= 1.0F) {
					this.failedToMine = false;
					this.tryBreakBlock(this.failedMiningPos);
				}
			}
		} else if (this.mining) {
			BlockState blockState = this.world.getBlockState(this.miningPos);
			if (blockState.isAir()) {
				this.world.setBlockBreakingProgress(this.player.getEntityId(), this.miningPos, -1);
				this.blockBreakingProgress = -1;
				this.mining = false;
			} else {
				this.continueMining(blockState, this.miningPos);
			}
		}
	}

	private float continueMining(BlockState blockState, BlockPos blockPos) {
		int i = this.tickCounter - this.failedStartMiningTime;
		float f = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (float)(i + 1);
		int j = (int)(f * 10.0F);
		if (j != this.blockBreakingProgress) {
			this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, j);
			this.blockBreakingProgress = j;
		}

		return f;
	}

	public void processBlockBreakingAction(BlockPos blockPos, PlayerActionC2SPacket.Action action, Direction direction, int i) {
		double d = this.player.getX() - ((double)blockPos.getX() + 0.5);
		double e = this.player.getY() - ((double)blockPos.getY() + 0.5) + 1.5;
		double f = this.player.getZ() - ((double)blockPos.getZ() + 0.5);
		double g = d * d + e * e + f * f;
		if (g > 36.0) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, "too far"));
		} else if (blockPos.getY() >= i) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, "too high"));
		} else {
			if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
				if (!this.world.canPlayerModifyAt(this.player, blockPos)) {
					this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, "may not interact"));
					return;
				}

				if (this.isCreative()) {
					if (!this.world.extinguishFire(null, blockPos, direction)) {
						this.finishMining(blockPos, action, "creative destroy");
					} else {
						this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, "fire put out"));
					}

					return;
				}

				if (this.player.canMine(this.world, blockPos, this.gameMode)) {
					this.player
						.networkHandler
						.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, "block action restricted"));
					return;
				}

				this.world.extinguishFire(null, blockPos, direction);
				this.startMiningTime = this.tickCounter;
				float h = 1.0F;
				BlockState blockState = this.world.getBlockState(blockPos);
				if (!blockState.isAir()) {
					blockState.onBlockBreakStart(this.world, blockPos, this.player);
					h = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos);
				}

				if (!blockState.isAir() && h >= 1.0F) {
					this.finishMining(blockPos, action, "insta mine");
				} else {
					if (this.mining) {
						this.player
							.networkHandler
							.sendPacket(
								new PlayerActionResponseS2CPacket(
									this.miningPos,
									this.world.getBlockState(this.miningPos),
									PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
									false,
									"abort destroying since another started (client insta mine, server disagreed)"
								)
							);
					}

					this.mining = true;
					this.miningPos = blockPos.toImmutable();
					int j = (int)(h * 10.0F);
					this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, j);
					this.player
						.networkHandler
						.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, "actual start of destroying"));
					this.blockBreakingProgress = j;
				}
			} else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
				if (blockPos.equals(this.miningPos)) {
					int k = this.tickCounter - this.startMiningTime;
					BlockState blockStatex = this.world.getBlockState(blockPos);
					if (!blockStatex.isAir()) {
						float l = blockStatex.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (float)(k + 1);
						if (l >= 0.7F) {
							this.mining = false;
							this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, -1);
							this.finishMining(blockPos, action, "destroyed");
							return;
						}

						if (!this.failedToMine) {
							this.mining = false;
							this.failedToMine = true;
							this.failedMiningPos = blockPos;
							this.failedStartMiningTime = this.startMiningTime;
						}
					}
				}

				this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, "stopped destroying"));
			} else if (action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
				this.mining = false;
				if (!Objects.equals(this.miningPos, blockPos)) {
					LOGGER.warn("Mismatch in destroy block pos: " + this.miningPos + " " + blockPos);
					this.world.setBlockBreakingProgress(this.player.getEntityId(), this.miningPos, -1);
					this.player
						.networkHandler
						.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), action, true, "aborted mismatched destroying"));
				}

				this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, -1);
				this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, "aborted destroying"));
			}
		}
	}

	public void finishMining(BlockPos blockPos, PlayerActionC2SPacket.Action action, String string) {
		if (this.tryBreakBlock(blockPos)) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, string));
		} else {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, string));
		}
	}

	public boolean tryBreakBlock(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!this.player.getMainHandStack().getItem().canMine(blockState, this.world, blockPos, this.player)) {
			return false;
		} else {
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
				this.world.updateListeners(blockPos, blockState, blockState, 3);
				return false;
			} else if (this.player.canMine(this.world, blockPos, this.gameMode)) {
				return false;
			} else {
				block.onBreak(this.world, blockPos, blockState, this.player);
				boolean bl = this.world.removeBlock(blockPos, false);
				if (bl) {
					block.onBroken(this.world, blockPos, blockState);
				}

				if (this.isCreative()) {
					return true;
				} else {
					ItemStack itemStack = this.player.getMainHandStack();
					boolean bl2 = this.player.isUsingEffectiveTool(blockState);
					itemStack.postMine(this.world, blockState, blockPos, this.player);
					if (bl && bl2) {
						ItemStack itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
						block.afterBreak(this.world, this.player, blockPos, blockState, blockEntity, itemStack2);
					}

					return true;
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand) {
		if (this.gameMode == GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else if (playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
			return ActionResult.PASS;
		} else {
			int i = itemStack.getCount();
			int j = itemStack.getDamage();
			TypedActionResult<ItemStack> typedActionResult = itemStack.use(world, playerEntity, hand);
			ItemStack itemStack2 = typedActionResult.getValue();
			if (itemStack2 == itemStack && itemStack2.getCount() == i && itemStack2.getMaxUseTime() <= 0 && itemStack2.getDamage() == j) {
				return typedActionResult.getResult();
			} else if (typedActionResult.getResult() == ActionResult.FAIL && itemStack2.getMaxUseTime() > 0 && !playerEntity.isUsingItem()) {
				return typedActionResult.getResult();
			} else {
				playerEntity.setStackInHand(hand, itemStack2);
				if (this.isCreative()) {
					itemStack2.setCount(i);
					if (itemStack2.isDamageable() && itemStack2.getDamage() != j) {
						itemStack2.setDamage(j);
					}
				}

				if (itemStack2.isEmpty()) {
					playerEntity.setStackInHand(hand, ItemStack.EMPTY);
				}

				if (!playerEntity.isUsingItem()) {
					((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ActionResult interactBlock(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand, BlockHitResult blockHitResult) {
		BlockPos blockPos = blockHitResult.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (this.gameMode == GameMode.SPECTATOR) {
			NameableContainerProvider nameableContainerProvider = blockState.createContainerProvider(world, blockPos);
			if (nameableContainerProvider != null) {
				playerEntity.openContainer(nameableContainerProvider);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		} else {
			boolean bl = !playerEntity.getMainHandStack().isEmpty() || !playerEntity.getOffHandStack().isEmpty();
			boolean bl2 = playerEntity.shouldCancelInteraction() && bl;
			if (!bl2 && blockState.onUse(world, playerEntity, hand, blockHitResult)) {
				return ActionResult.SUCCESS;
			} else if (!itemStack.isEmpty() && !playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
				ItemUsageContext itemUsageContext = new ItemUsageContext(playerEntity, hand, blockHitResult);
				if (this.isCreative()) {
					int i = itemStack.getCount();
					ActionResult actionResult = itemStack.useOnBlock(itemUsageContext);
					itemStack.setCount(i);
					return actionResult;
				} else {
					return itemStack.useOnBlock(itemUsageContext);
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
