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
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ClickWindowServerPacket;
import net.minecraft.server.network.packet.CraftRequestServerPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionServerPacket;
import net.minecraft.server.network.packet.PickFromInventoryServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotServerPacket;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.EntityHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
	private ItemStack field_3718 = ItemStack.EMPTY;
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
		if (!minecraftClient.world.method_8506(minecraftClient.player, blockPos, direction)) {
			clientPlayerInteractionManager.breakBlock(blockPos);
		}
	}

	public void copyAbilities(PlayerEntity playerEntity) {
		this.gameMode.setAbilitites(playerEntity.abilities);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.setAbilitites(this.client.player.abilities);
	}

	public void method_2898(PlayerEntity playerEntity) {
		playerEntity.yaw = -180.0F;
	}

	public boolean hasStatusBars() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean breakBlock(BlockPos blockPos) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
				if (!itemStack.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		World world = this.client.world;
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.client.player.getMainHandStack().getItem().beforeBlockBreak(blockState, world, blockPos, this.client.player)) {
			return false;
		} else {
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.client.player.method_7338()) {
				return false;
			} else if (blockState.isAir()) {
				return false;
			} else {
				block.onBreak(world, blockPos, blockState, this.client.player);
				FluidState fluidState = world.getFluidState(blockPos);
				boolean bl = world.setBlockState(blockPos, fluidState.getBlockState(), 11);
				if (bl) {
					block.onBroken(world, blockPos, blockState);
				}

				this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
				if (!this.gameMode.isCreative()) {
					ItemStack itemStack2 = this.client.player.getMainHandStack();
					if (!itemStack2.isEmpty()) {
						itemStack2.onBlockBroken(world, blockState, blockPos, this.client.player);
						if (itemStack2.isEmpty()) {
							this.client.player.setStackInHand(Hand.MAIN, ItemStack.EMPTY);
						}
					}
				}

				return bl;
			}
		}
	}

	public boolean attackBlock(BlockPos blockPos, Direction direction) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
				if (!itemStack.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, this.client.world.getBlockState(blockPos), 1.0F);
				this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12968, blockPos, direction));
				method_2921(this.client, this, blockPos, direction);
				this.field_3716 = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(blockPos)) {
				if (this.breakingBlock) {
					this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12971, this.currentBreakingPos, direction));
				}

				BlockState blockState = this.client.world.getBlockState(blockPos);
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, 0.0F);
				this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12968, blockPos, direction));
				boolean bl = !blockState.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.onBlockBreakStart(this.client.world, blockPos, this.client.player);
				}

				if (bl && blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos) >= 1.0F) {
					this.breakBlock(blockPos);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = blockPos;
					this.field_3718 = this.client.player.getMainHandStack();
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			this.client
				.getTutorialManager()
				.onBlockAttacked(this.client.world, this.currentBreakingPos, this.client.world.getBlockState(this.currentBreakingPos), -1.0F);
			this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12971, this.currentBreakingPos, Direction.DOWN));
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, -1);
			this.client.player.method_7350();
		}
	}

	public boolean method_2902(BlockPos blockPos, Direction direction) {
		this.syncSelectedSlot();
		if (this.field_3716 > 0) {
			this.field_3716--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(blockPos)) {
			this.field_3716 = 5;
			this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, this.client.world.getBlockState(blockPos), 1.0F);
			this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12968, blockPos, direction));
			method_2921(this.client, this, blockPos, direction);
			return true;
		} else if (this.isCurrentlyBreaking(blockPos)) {
			BlockState blockState = this.client.world.getBlockState(blockPos);
			if (blockState.isAir()) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos);
				if (this.field_3713 % 4.0F == 0.0F) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.client
						.getSoundLoader()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.getHitSound(), SoundCategory.field_15254, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, blockPos
							)
						);
				}

				this.field_3713++;
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12973, blockPos, direction));
					this.breakBlock(blockPos);
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3716 = 5;
				}

				this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
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
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.field_3718.isEmpty() && itemStack.isEmpty();
		if (!this.field_3718.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.field_3718.getItem()
				&& ItemStack.areTagsEqual(itemStack, this.field_3718)
				&& (itemStack.hasDurability() || itemStack.getDamage() == this.field_3718.getDamage());
		}

		return blockPos.equals(this.currentBreakingPos) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.player.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.sendPacket(new UpdateSelectedSlotServerPacket(this.lastSelectedSlot));
		}
	}

	public ActionResult interactBlock(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, Hand hand, BlockHitResult blockHitResult) {
		this.syncSelectedSlot();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Vec3d vec3d = blockHitResult.getPos();
		if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return ActionResult.FAILURE;
		} else {
			ItemStack itemStack = clientPlayerEntity.getStackInHand(hand);
			if (this.gameMode == GameMode.field_9219) {
				this.networkHandler.sendPacket(new PlayerInteractBlockServerPacket(hand, blockHitResult));
				return ActionResult.SUCCESS;
			} else {
				boolean bl = !clientPlayerEntity.getMainHandStack().isEmpty() || !clientPlayerEntity.getOffHandStack().isEmpty();
				boolean bl2 = clientPlayerEntity.isSneaking() && bl;
				if (!bl2 && clientWorld.getBlockState(blockPos).activate(clientWorld, clientPlayerEntity, hand, blockHitResult)) {
					this.networkHandler.sendPacket(new PlayerInteractBlockServerPacket(hand, blockHitResult));
					return ActionResult.SUCCESS;
				} else {
					this.networkHandler.sendPacket(new PlayerInteractBlockServerPacket(hand, blockHitResult));
					if (!itemStack.isEmpty() && !clientPlayerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
						ItemUsageContext itemUsageContext = new ItemUsageContext(clientPlayerEntity, clientPlayerEntity.getStackInHand(hand), blockHitResult);
						ActionResult actionResult;
						if (this.gameMode.isCreative()) {
							int i = itemStack.getAmount();
							actionResult = itemStack.useOnBlock(itemUsageContext);
							itemStack.setAmount(i);
						} else {
							actionResult = itemStack.useOnBlock(itemUsageContext);
						}

						return actionResult;
					} else {
						return ActionResult.PASS;
					}
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity playerEntity, World world, Hand hand) {
		if (this.gameMode == GameMode.field_9219) {
			return ActionResult.PASS;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new PlayerInteractItemServerPacket(hand));
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				int i = itemStack.getAmount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.use(world, playerEntity, hand);
				ItemStack itemStack2 = typedActionResult.getValue();
				if (itemStack2 != itemStack || itemStack2.getAmount() != i) {
					playerEntity.setStackInHand(hand, itemStack2);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ClientPlayerEntity createPlayer(World world, StatHandler statHandler, ClientRecipeBook clientRecipeBook) {
		return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler, clientRecipeBook);
	}

	public void attackEntity(PlayerEntity playerEntity, Entity entity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityServerPacket(entity));
		if (this.gameMode != GameMode.field_9219) {
			playerEntity.attack(entity);
			playerEntity.method_7350();
		}
	}

	public ActionResult interactEntity(PlayerEntity playerEntity, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityServerPacket(entity, hand));
		return this.gameMode == GameMode.field_9219 ? ActionResult.PASS : playerEntity.interact(entity, hand);
	}

	public ActionResult interactEntityAtLocation(PlayerEntity playerEntity, Entity entity, EntityHitResult entityHitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = entityHitResult.getPos().subtract(entity.x, entity.y, entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityServerPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.field_9219 ? ActionResult.PASS : entity.interactAt(playerEntity, vec3d, hand);
	}

	public ItemStack method_2906(int i, int j, int k, SlotActionType slotActionType, PlayerEntity playerEntity) {
		short s = playerEntity.container.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.container.onSlotClick(j, k, slotActionType, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowServerPacket(i, j, k, slotActionType, itemStack, s));
		return itemStack;
	}

	public void clickRecipe(int i, Recipe<?> recipe, boolean bl) {
		this.networkHandler.sendPacket(new CraftRequestServerPacket(i, recipe, bl));
	}

	public void clickButton(int i, int j) {
		this.networkHandler.sendPacket(new ButtonClickServerPacket(i, j));
	}

	public void method_2909(ItemStack itemStack, int i) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionServerPacket(i, itemStack));
		}
	}

	public void method_2915(ItemStack itemStack) {
		if (this.gameMode.isCreative() && !itemStack.isEmpty()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionServerPacket(-1, itemStack));
		}
	}

	public void method_2897(PlayerEntity playerEntity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12974, BlockPos.ORIGIN, Direction.DOWN));
		playerEntity.method_6075();
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
		return this.client.player.hasVehicle() && this.client.player.getRiddenEntity() instanceof HorseBaseEntity;
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
		this.networkHandler.sendPacket(new PickFromInventoryServerPacket(i));
	}
}
