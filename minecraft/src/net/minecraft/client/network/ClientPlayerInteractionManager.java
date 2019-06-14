package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManager {
	private final MinecraftClient client;
	private final ClientPlayNetworkHandler networkHandler;
	private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
	private ItemStack selectedStack = ItemStack.EMPTY;
	private float currentBreakingProgress;
	private float field_3713;
	private int field_3716;
	private boolean breakingBlock;
	private GameMode gameMode = GameMode.field_9215;
	private int lastSelectedSlot;

	public ClientPlayerInteractionManager(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.client = minecraftClient;
		this.networkHandler = clientPlayNetworkHandler;
	}

	public static void method_2921(
		MinecraftClient minecraftClient, ClientPlayerInteractionManager clientPlayerInteractionManager, BlockPos blockPos, Direction direction
	) {
		if (!minecraftClient.field_1687.method_8506(minecraftClient.field_1724, blockPos, direction)) {
			clientPlayerInteractionManager.breakBlock(blockPos);
		}
	}

	public void copyAbilities(PlayerEntity playerEntity) {
		this.gameMode.setAbilitites(playerEntity.abilities);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.setAbilitites(this.client.field_1724.abilities);
	}

	public boolean hasStatusBars() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean breakBlock(BlockPos blockPos) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.field_1724.canModifyWorld()) {
				ItemStack itemStack = this.client.field_1724.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.field_1687, blockPos, false);
				if (!itemStack.method_7940(this.client.field_1687.getTagManager(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		World world = this.client.field_1687;
		BlockState blockState = world.method_8320(blockPos);
		if (!this.client.field_1724.getMainHandStack().getItem().method_7885(blockState, world, blockPos, this.client.field_1724)) {
			return false;
		} else {
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.client.field_1724.isCreativeLevelTwoOp()) {
				return false;
			} else if (blockState.isAir()) {
				return false;
			} else {
				block.method_9576(world, blockPos, blockState, this.client.field_1724);
				FluidState fluidState = world.method_8316(blockPos);
				boolean bl = world.method_8652(blockPos, fluidState.getBlockState(), 11);
				if (bl) {
					block.method_9585(world, blockPos, blockState);
				}

				this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
				return bl;
			}
		}
	}

	public boolean attackBlock(BlockPos blockPos, Direction direction) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.field_1724.canModifyWorld()) {
				ItemStack itemStack = this.client.field_1724.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.field_1687, blockPos, false);
				if (!itemStack.method_7940(this.client.field_1687.getTagManager(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		if (!this.client.field_1687.method_8621().contains(blockPos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				this.client.method_1577().onBlockAttacked(this.client.field_1687, blockPos, this.client.field_1687.method_8320(blockPos), 1.0F);
				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
				method_2921(this.client, this, blockPos, direction);
				this.field_3716 = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(blockPos)) {
				if (this.breakingBlock) {
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12971, this.currentBreakingPos, direction));
				}

				BlockState blockState = this.client.field_1687.method_8320(blockPos);
				this.client.method_1577().onBlockAttacked(this.client.field_1687, blockPos, blockState, 0.0F);
				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
				boolean bl = !blockState.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.onBlockBreakStart(this.client.field_1687, blockPos, this.client.field_1724);
				}

				if (bl && blockState.calcBlockBreakingDelta(this.client.field_1724, this.client.field_1724.field_6002, blockPos) >= 1.0F) {
					this.breakBlock(blockPos);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = blockPos;
					this.selectedStack = this.client.field_1724.getMainHandStack();
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.client
						.field_1687
						.setBlockBreakingProgress(this.client.field_1724.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			this.client
				.method_1577()
				.onBlockAttacked(this.client.field_1687, this.currentBreakingPos, this.client.field_1687.method_8320(this.currentBreakingPos), -1.0F);
			this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12971, this.currentBreakingPos, Direction.field_11033));
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.field_1687.setBlockBreakingProgress(this.client.field_1724.getEntityId(), this.currentBreakingPos, -1);
			this.client.field_1724.resetLastAttackedTicks();
		}
	}

	public boolean method_2902(BlockPos blockPos, Direction direction) {
		this.syncSelectedSlot();
		if (this.field_3716 > 0) {
			this.field_3716--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.field_1687.method_8621().contains(blockPos)) {
			this.field_3716 = 5;
			this.client.method_1577().onBlockAttacked(this.client.field_1687, blockPos, this.client.field_1687.method_8320(blockPos), 1.0F);
			this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
			method_2921(this.client, this, blockPos, direction);
			return true;
		} else if (this.isCurrentlyBreaking(blockPos)) {
			BlockState blockState = this.client.field_1687.method_8320(blockPos);
			if (blockState.isAir()) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress
					+ blockState.calcBlockBreakingDelta(this.client.field_1724, this.client.field_1724.field_6002, blockPos);
				if (this.field_3713 % 4.0F == 0.0F) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.client
						.method_1483()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.getHitSound(), SoundCategory.field_15254, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, blockPos
							)
						);
				}

				this.field_3713++;
				this.client.method_1577().onBlockAttacked(this.client.field_1687, blockPos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12973, blockPos, direction));
					this.breakBlock(blockPos);
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3716 = 5;
				}

				this.client
					.field_1687
					.setBlockBreakingProgress(this.client.field_1724.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				return true;
			}
		} else {
			return this.attackBlock(blockPos, direction);
		}
	}

	public float getReachDistance() {
		return this.gameMode.isCreative() ? 5.0F : 4.5F;
	}

	public void tick() {
		this.syncSelectedSlot();
		if (this.networkHandler.getClientConnection().isOpen()) {
			this.networkHandler.getClientConnection().tick();
		} else {
			this.networkHandler.getClientConnection().handleDisconnection();
		}
	}

	private boolean isCurrentlyBreaking(BlockPos blockPos) {
		ItemStack itemStack = this.client.field_1724.getMainHandStack();
		boolean bl = this.selectedStack.isEmpty() && itemStack.isEmpty();
		if (!this.selectedStack.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.selectedStack.getItem()
				&& ItemStack.areTagsEqual(itemStack, this.selectedStack)
				&& (itemStack.isDamageable() || itemStack.getDamage() == this.selectedStack.getDamage());
		}

		return blockPos.equals(this.currentBreakingPos) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.field_1724.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
		}
	}

	public ActionResult method_2896(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, Hand hand, BlockHitResult blockHitResult) {
		this.syncSelectedSlot();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Vec3d vec3d = blockHitResult.method_17784();
		if (!this.client.field_1687.method_8621().contains(blockPos)) {
			return ActionResult.field_5814;
		} else {
			ItemStack itemStack = clientPlayerEntity.getStackInHand(hand);
			if (this.gameMode == GameMode.field_9219) {
				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
				return ActionResult.field_5812;
			} else {
				boolean bl = !clientPlayerEntity.getMainHandStack().isEmpty() || !clientPlayerEntity.getOffHandStack().isEmpty();
				boolean bl2 = clientPlayerEntity.isSneaking() && bl;
				if (!bl2 && clientWorld.method_8320(blockPos).method_11629(clientWorld, clientPlayerEntity, hand, blockHitResult)) {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
					return ActionResult.field_5812;
				} else {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
					if (!itemStack.isEmpty() && !clientPlayerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
						ItemUsageContext itemUsageContext = new ItemUsageContext(clientPlayerEntity, hand, blockHitResult);
						ActionResult actionResult;
						if (this.gameMode.isCreative()) {
							int i = itemStack.getCount();
							actionResult = itemStack.useOnBlock(itemUsageContext);
							itemStack.setCount(i);
						} else {
							actionResult = itemStack.useOnBlock(itemUsageContext);
						}

						return actionResult;
					} else {
						return ActionResult.field_5811;
					}
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity playerEntity, World world, Hand hand) {
		if (this.gameMode == GameMode.field_9219) {
			return ActionResult.field_5811;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand));
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
				return ActionResult.field_5811;
			} else {
				int i = itemStack.getCount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.method_7913(world, playerEntity, hand);
				ItemStack itemStack2 = typedActionResult.getValue();
				if (itemStack2 != itemStack || itemStack2.getCount() != i) {
					playerEntity.setStackInHand(hand, itemStack2);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ClientPlayerEntity method_2901(ClientWorld clientWorld, StatHandler statHandler, ClientRecipeBook clientRecipeBook) {
		return new ClientPlayerEntity(this.client, clientWorld, this.networkHandler, statHandler, clientRecipeBook);
	}

	public void attackEntity(PlayerEntity playerEntity, Entity entity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity));
		if (this.gameMode != GameMode.field_9219) {
			playerEntity.attack(entity);
			playerEntity.resetLastAttackedTicks();
		}
	}

	public ActionResult interactEntity(PlayerEntity playerEntity, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == GameMode.field_9219 ? ActionResult.field_5811 : playerEntity.interact(entity, hand);
	}

	public ActionResult interactEntityAtLocation(PlayerEntity playerEntity, Entity entity, EntityHitResult entityHitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = entityHitResult.method_17784().subtract(entity.x, entity.y, entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.field_9219 ? ActionResult.field_5811 : entity.method_5664(playerEntity, vec3d, hand);
	}

	public ItemStack method_2906(int i, int j, int k, SlotActionType slotActionType, PlayerEntity playerEntity) {
		short s = playerEntity.container.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.container.onSlotClick(j, k, slotActionType, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, slotActionType, itemStack, s));
		return itemStack;
	}

	public void clickRecipe(int i, Recipe<?> recipe, boolean bl) {
		this.networkHandler.sendPacket(new CraftRequestC2SPacket(i, recipe, bl));
	}

	public void clickButton(int i, int j) {
		this.networkHandler.sendPacket(new ButtonClickC2SPacket(i, j));
	}

	public void clickCreativeStack(ItemStack itemStack, int i) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(i, itemStack));
		}
	}

	public void dropCreativeStack(ItemStack itemStack) {
		if (this.gameMode.isCreative() && !itemStack.isEmpty()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, itemStack));
		}
	}

	public void stopUsingItem(PlayerEntity playerEntity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12974, BlockPos.ORIGIN, Direction.field_11033));
		playerEntity.stopUsingItem();
	}

	public boolean hasExperienceBar() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean hasLimitedAttackSpeed() {
		return !this.gameMode.isCreative();
	}

	public boolean hasCreativeInventory() {
		return this.gameMode.isCreative();
	}

	public boolean hasExtendedReach() {
		return this.gameMode.isCreative();
	}

	public boolean hasRidingInventory() {
		return this.client.field_1724.hasVehicle() && this.client.field_1724.getVehicle() instanceof HorseBaseEntity;
	}

	public boolean isFlyingLocked() {
		return this.gameMode == GameMode.field_9219;
	}

	public GameMode getCurrentGameMode() {
		return this.gameMode;
	}

	public boolean isBreakingBlock() {
		return this.breakingBlock;
	}

	public void pickFromInventory(int i) {
		this.networkHandler.sendPacket(new PickFromInventoryC2SPacket(i));
	}
}
