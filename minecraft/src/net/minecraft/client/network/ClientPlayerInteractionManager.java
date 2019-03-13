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
	private BlockPos field_3714 = new BlockPos(-1, -1, -1);
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
		if (!minecraftClient.field_1687.method_8506(minecraftClient.field_1724, blockPos, direction)) {
			clientPlayerInteractionManager.method_2899(blockPos);
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

	public boolean method_2899(BlockPos blockPos) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.field_1724.canModifyWorld()) {
				ItemStack itemStack = this.client.field_1724.method_6047();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.field_1687, blockPos, false);
				if (!itemStack.method_7940(this.client.field_1687.method_8514(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		World world = this.client.field_1687;
		BlockState blockState = world.method_8320(blockPos);
		if (!this.client.field_1724.method_6047().getItem().method_7885(blockState, world, blockPos, this.client.field_1724)) {
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

				this.field_3714 = new BlockPos(this.field_3714.getX(), -1, this.field_3714.getZ());
				if (!this.gameMode.isCreative()) {
					ItemStack itemStack2 = this.client.field_1724.method_6047();
					if (!itemStack2.isEmpty()) {
						itemStack2.method_7952(world, blockState, blockPos, this.client.field_1724);
						if (itemStack2.isEmpty()) {
							this.client.field_1724.method_6122(Hand.MAIN, ItemStack.EMPTY);
						}
					}
				}

				return bl;
			}
		}
	}

	public boolean method_2910(BlockPos blockPos, Direction direction) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == GameMode.field_9219) {
				return false;
			}

			if (!this.client.field_1724.canModifyWorld()) {
				ItemStack itemStack = this.client.field_1724.method_6047();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.field_1687, blockPos, false);
				if (!itemStack.method_7940(this.client.field_1687.method_8514(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		if (!this.client.field_1687.method_8621().method_11952(blockPos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				this.client.method_1577().method_4907(this.client.field_1687, blockPos, this.client.field_1687.method_8320(blockPos), 1.0F);
				this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
				method_2921(this.client, this, blockPos, direction);
				this.field_3716 = 5;
			} else if (!this.breakingBlock || !this.method_2922(blockPos)) {
				if (this.breakingBlock) {
					this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12971, this.field_3714, direction));
				}

				BlockState blockState = this.client.field_1687.method_8320(blockPos);
				this.client.method_1577().method_4907(this.client.field_1687, blockPos, blockState, 0.0F);
				this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
				boolean bl = !blockState.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.method_11636(this.client.field_1687, blockPos, this.client.field_1724);
				}

				if (bl && blockState.method_11589(this.client.field_1724, this.client.field_1724.field_6002, blockPos) >= 1.0F) {
					this.method_2899(blockPos);
				} else {
					this.breakingBlock = true;
					this.field_3714 = blockPos;
					this.field_3718 = this.client.field_1724.method_6047();
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.client.field_1687.method_8517(this.client.field_1724.getEntityId(), this.field_3714, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			this.client.method_1577().method_4907(this.client.field_1687, this.field_3714, this.client.field_1687.method_8320(this.field_3714), -1.0F);
			this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12971, this.field_3714, Direction.DOWN));
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.field_1687.method_8517(this.client.field_1724.getEntityId(), this.field_3714, -1);
			this.client.field_1724.method_7350();
		}
	}

	public boolean method_2902(BlockPos blockPos, Direction direction) {
		this.syncSelectedSlot();
		if (this.field_3716 > 0) {
			this.field_3716--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.field_1687.method_8621().method_11952(blockPos)) {
			this.field_3716 = 5;
			this.client.method_1577().method_4907(this.client.field_1687, blockPos, this.client.field_1687.method_8320(blockPos), 1.0F);
			this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12968, blockPos, direction));
			method_2921(this.client, this, blockPos, direction);
			return true;
		} else if (this.method_2922(blockPos)) {
			BlockState blockState = this.client.field_1687.method_8320(blockPos);
			if (blockState.isAir()) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + blockState.method_11589(this.client.field_1724, this.client.field_1724.field_6002, blockPos);
				if (this.field_3713 % 4.0F == 0.0F) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.client
						.method_1483()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.method_10596(), SoundCategory.field_15254, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, blockPos
							)
						);
				}

				this.field_3713++;
				this.client.method_1577().method_4907(this.client.field_1687, blockPos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12973, blockPos, direction));
					this.method_2899(blockPos);
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3716 = 5;
				}

				this.client.field_1687.method_8517(this.client.field_1724.getEntityId(), this.field_3714, (int)(this.currentBreakingProgress * 10.0F) - 1);
				return true;
			}
		} else {
			return this.method_2910(blockPos, direction);
		}
	}

	public float getReachDistance() {
		return this.gameMode.isCreative() ? 5.0F : 4.5F;
	}

	public void tick() {
		this.syncSelectedSlot();
		if (this.networkHandler.method_2872().isOpen()) {
			this.networkHandler.method_2872().tick();
		} else {
			this.networkHandler.method_2872().handleDisconnection();
		}
	}

	private boolean method_2922(BlockPos blockPos) {
		ItemStack itemStack = this.client.field_1724.method_6047();
		boolean bl = this.field_3718.isEmpty() && itemStack.isEmpty();
		if (!this.field_3718.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.field_3718.getItem()
				&& ItemStack.areTagsEqual(itemStack, this.field_3718)
				&& (itemStack.hasDurability() || itemStack.getDamage() == this.field_3718.getDamage());
		}

		return blockPos.equals(this.field_3714) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.field_1724.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.method_2883(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
		}
	}

	public ActionResult method_2896(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, Hand hand, BlockHitResult blockHitResult) {
		this.syncSelectedSlot();
		BlockPos blockPos = blockHitResult.method_17777();
		Vec3d vec3d = blockHitResult.method_17784();
		if (!this.client.field_1687.method_8621().method_11952(blockPos)) {
			return ActionResult.field_5814;
		} else {
			ItemStack itemStack = clientPlayerEntity.method_5998(hand);
			if (this.gameMode == GameMode.field_9219) {
				this.networkHandler.method_2883(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
				return ActionResult.field_5812;
			} else {
				boolean bl = !clientPlayerEntity.method_6047().isEmpty() || !clientPlayerEntity.method_6079().isEmpty();
				boolean bl2 = clientPlayerEntity.isSneaking() && bl;
				if (!bl2 && clientWorld.method_8320(blockPos).method_11629(clientWorld, clientPlayerEntity, hand, blockHitResult)) {
					this.networkHandler.method_2883(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
					return ActionResult.field_5812;
				} else {
					this.networkHandler.method_2883(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
					if (!itemStack.isEmpty() && !clientPlayerEntity.method_7357().isCooldown(itemStack.getItem())) {
						ItemUsageContext itemUsageContext = new ItemUsageContext(clientPlayerEntity, clientPlayerEntity.method_5998(hand), blockHitResult);
						ActionResult actionResult;
						if (this.gameMode.isCreative()) {
							int i = itemStack.getAmount();
							actionResult = itemStack.method_7981(itemUsageContext);
							itemStack.setAmount(i);
						} else {
							actionResult = itemStack.method_7981(itemUsageContext);
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
			this.networkHandler.method_2883(new PlayerInteractItemC2SPacket(hand));
			ItemStack itemStack = playerEntity.method_5998(hand);
			if (playerEntity.method_7357().isCooldown(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				int i = itemStack.getAmount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.method_7913(world, playerEntity, hand);
				ItemStack itemStack2 = typedActionResult.getValue();
				if (itemStack2 != itemStack || itemStack2.getAmount() != i) {
					playerEntity.method_6122(hand, itemStack2);
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
		this.networkHandler.method_2883(new PlayerInteractEntityC2SPacket(entity));
		if (this.gameMode != GameMode.field_9219) {
			playerEntity.attack(entity);
			playerEntity.method_7350();
		}
	}

	public ActionResult interactEntity(PlayerEntity playerEntity, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.method_2883(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == GameMode.field_9219 ? ActionResult.PASS : playerEntity.interact(entity, hand);
	}

	public ActionResult interactEntityAtLocation(PlayerEntity playerEntity, Entity entity, EntityHitResult entityHitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = entityHitResult.method_17784().subtract(entity.x, entity.y, entity.z);
		this.networkHandler.method_2883(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.field_9219 ? ActionResult.PASS : entity.method_5664(playerEntity, vec3d, hand);
	}

	public ItemStack method_2906(int i, int j, int k, SlotActionType slotActionType, PlayerEntity playerEntity) {
		short s = playerEntity.field_7512.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.field_7512.method_7593(j, k, slotActionType, playerEntity);
		this.networkHandler.method_2883(new ClickWindowC2SPacket(i, j, k, slotActionType, itemStack, s));
		return itemStack;
	}

	public void clickRecipe(int i, Recipe<?> recipe, boolean bl) {
		this.networkHandler.method_2883(new CraftRequestC2SPacket(i, recipe, bl));
	}

	public void clickButton(int i, int j) {
		this.networkHandler.method_2883(new ButtonClickServerPacket(i, j));
	}

	public void method_2909(ItemStack itemStack, int i) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.method_2883(new CreativeInventoryActionC2SPacket(i, itemStack));
		}
	}

	public void method_2915(ItemStack itemStack) {
		if (this.gameMode.isCreative() && !itemStack.isEmpty()) {
			this.networkHandler.method_2883(new CreativeInventoryActionC2SPacket(-1, itemStack));
		}
	}

	public void stopUsingItem(PlayerEntity playerEntity) {
		this.syncSelectedSlot();
		this.networkHandler.method_2883(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12974, BlockPos.ORIGIN, Direction.DOWN));
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
		return this.client.field_1724.hasVehicle() && this.client.field_1724.getRiddenEntity() instanceof HorseBaseEntity;
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
		this.networkHandler.method_2883(new PickFromInventoryC2SPacket(i));
	}
}
