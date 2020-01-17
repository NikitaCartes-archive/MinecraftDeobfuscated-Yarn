package net.minecraft.server.network;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
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

	public ServerPlayerInteractionManager(ServerWorld world) {
		this.world = world;
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
				float f = this.continueMining(blockState, this.failedMiningPos, this.failedStartMiningTime);
				if (f >= 1.0F) {
					this.failedToMine = false;
					this.tryBreakBlock(this.failedMiningPos);
				}
			}
		} else if (this.mining) {
			BlockState blockState = this.world.getBlockState(this.miningPos);
			if (blockState.isAir()) {
				this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, -1);
				this.blockBreakingProgress = -1;
				this.mining = false;
			} else {
				this.continueMining(blockState, this.miningPos, this.startMiningTime);
			}
		}
	}

	private float continueMining(BlockState blockState, BlockPos blockPos, int i) {
		int j = this.tickCounter - i;
		float f = blockState.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (float)(j + 1);
		int k = (int)(f * 10.0F);
		if (k != this.blockBreakingProgress) {
			this.world.setBlockBreakingInfo(this.player.getEntityId(), blockPos, k);
			this.blockBreakingProgress = k;
		}

		return f;
	}

	public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
		double d = this.player.getX() - ((double)pos.getX() + 0.5);
		double e = this.player.getY() - ((double)pos.getY() + 0.5) + 1.5;
		double f = this.player.getZ() - ((double)pos.getZ() + 0.5);
		double g = d * d + e * e + f * f;
		if (g > 36.0) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "too far"));
		} else if (pos.getY() >= worldHeight) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "too high"));
		} else {
			if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
				if (!this.world.canPlayerModifyAt(this.player, pos)) {
					this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "may not interact"));
					return;
				}

				if (this.isCreative()) {
					if (!this.world.extinguishFire(null, pos, direction)) {
						this.finishMining(pos, action, "creative destroy");
					} else {
						this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "fire put out"));
					}

					return;
				}

				if (this.player.canMine(this.world, pos, this.gameMode)) {
					this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "block action restricted"));
					return;
				}

				this.world.extinguishFire(null, pos, direction);
				this.startMiningTime = this.tickCounter;
				float h = 1.0F;
				BlockState blockState = this.world.getBlockState(pos);
				if (!blockState.isAir()) {
					blockState.onBlockBreakStart(this.world, pos, this.player);
					h = blockState.calcBlockBreakingDelta(this.player, this.player.world, pos);
				}

				if (!blockState.isAir() && h >= 1.0F) {
					this.finishMining(pos, action, "insta mine");
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
					this.miningPos = pos.toImmutable();
					int i = (int)(h * 10.0F);
					this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, i);
					this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "actual start of destroying"));
					this.blockBreakingProgress = i;
				}
			} else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
				if (pos.equals(this.miningPos)) {
					int j = this.tickCounter - this.startMiningTime;
					BlockState blockStatex = this.world.getBlockState(pos);
					if (!blockStatex.isAir()) {
						float k = blockStatex.calcBlockBreakingDelta(this.player, this.player.world, pos) * (float)(j + 1);
						if (k >= 0.7F) {
							this.mining = false;
							this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, -1);
							this.finishMining(pos, action, "destroyed");
							return;
						}

						if (!this.failedToMine) {
							this.mining = false;
							this.failedToMine = true;
							this.failedMiningPos = pos;
							this.failedStartMiningTime = this.startMiningTime;
						}
					}
				}

				this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "stopped destroying"));
			} else if (action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
				this.mining = false;
				if (!Objects.equals(this.miningPos, pos)) {
					LOGGER.warn("Mismatch in destroy block pos: " + this.miningPos + " " + pos);
					this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, -1);
					this.player
						.networkHandler
						.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), action, true, "aborted mismatched destroying"));
				}

				this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, -1);
				this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "aborted destroying"));
			}
		}
	}

	public void finishMining(BlockPos blockPos, PlayerActionC2SPacket.Action action, String reason) {
		if (this.tryBreakBlock(blockPos)) {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, true, reason));
		} else {
			this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false, reason));
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
					ItemStack itemStack2 = itemStack.copy();
					boolean bl2 = this.player.isUsingEffectiveTool(blockState);
					itemStack.postMine(this.world, blockState, blockPos, this.player);
					if (bl && bl2) {
						block.afterBreak(this.world, this.player, blockPos, blockState, blockEntity, itemStack2);
					}

					return true;
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity player, World world, ItemStack stack, Hand hand) {
		if (this.gameMode == GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
			return ActionResult.PASS;
		} else {
			int i = stack.getCount();
			int j = stack.getDamage();
			TypedActionResult<ItemStack> typedActionResult = stack.use(world, player, hand);
			ItemStack itemStack = typedActionResult.getValue();
			if (itemStack == stack && itemStack.getCount() == i && itemStack.getMaxUseTime() <= 0 && itemStack.getDamage() == j) {
				return typedActionResult.getResult();
			} else if (typedActionResult.getResult() == ActionResult.FAIL && itemStack.getMaxUseTime() > 0 && !player.isUsingItem()) {
				return typedActionResult.getResult();
			} else {
				player.setStackInHand(hand, itemStack);
				if (this.isCreative()) {
					itemStack.setCount(i);
					if (itemStack.isDamageable() && itemStack.getDamage() != j) {
						itemStack.setDamage(j);
					}
				}

				if (itemStack.isEmpty()) {
					player.setStackInHand(hand, ItemStack.EMPTY);
				}

				if (!player.isUsingItem()) {
					((ServerPlayerEntity)player).openContainer(player.playerContainer);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ActionResult interactBlock(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
		BlockPos blockPos = hitResult.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (this.gameMode == GameMode.SPECTATOR) {
			NameableContainerFactory nameableContainerFactory = blockState.createContainerFactory(world, blockPos);
			if (nameableContainerFactory != null) {
				player.openContainer(nameableContainerFactory);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		} else {
			boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
			boolean bl2 = player.shouldCancelInteraction() && bl;
			if (!bl2) {
				ActionResult actionResult = blockState.onUse(world, player, hand, hitResult);
				if (actionResult.isAccepted()) {
					return actionResult;
				}
			}

			if (!stack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
				ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
				if (this.isCreative()) {
					int i = stack.getCount();
					ActionResult actionResult2 = stack.useOnBlock(itemUsageContext);
					stack.setCount(i);
					return actionResult2;
				} else {
					return stack.useOnBlock(itemUsageContext);
				}
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}
}
