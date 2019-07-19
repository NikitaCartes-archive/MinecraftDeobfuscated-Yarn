package net.minecraft.client.network;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.PosAndRot;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.Recipe;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManager {
	private static final Logger field_20316 = LogManager.getLogger();
	private final MinecraftClient client;
	private final ClientPlayNetworkHandler networkHandler;
	private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
	private ItemStack selectedStack = ItemStack.EMPTY;
	private float currentBreakingProgress;
	private float field_3713;
	private int field_3716;
	private boolean breakingBlock;
	private GameMode gameMode = GameMode.SURVIVAL;
	private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, PlayerActionC2SPacket.Action>, PosAndRot> field_20317 = new Object2ObjectLinkedOpenHashMap<>();
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

	public void copyAbilities(PlayerEntity player) {
		this.gameMode.setAbilitites(player.abilities);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.setAbilitites(this.client.player.abilities);
	}

	public boolean hasStatusBars() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean breakBlock(BlockPos pos) {
		if (this.client.player.method_21701(this.client.world, pos, this.gameMode)) {
			return false;
		} else {
			World world = this.client.world;
			BlockState blockState = world.getBlockState(pos);
			if (!this.client.player.getMainHandStack().getItem().canMine(blockState, world, pos, this.client.player)) {
				return false;
			} else {
				Block block = blockState.getBlock();
				if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.client.player.isCreativeLevelTwoOp()) {
					return false;
				} else if (blockState.isAir()) {
					return false;
				} else {
					block.onBreak(world, pos, blockState, this.client.player);
					FluidState fluidState = world.getFluidState(pos);
					boolean bl = world.setBlockState(pos, fluidState.getBlockState(), 11);
					if (bl) {
						block.onBroken(world, pos, blockState);
					}

					return bl;
				}
			}
		}
	}

	public boolean attackBlock(BlockPos pos, Direction direction) {
		if (this.client.player.method_21701(this.client.world, pos, this.gameMode)) {
			return false;
		} else if (!this.client.world.getWorldBorder().contains(pos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				BlockState blockState = this.client.world.getBlockState(pos);
				this.client.getTutorialManager().onBlockAttacked(this.client.world, pos, blockState, 1.0F);
				this.method_21706(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction);
				method_2921(this.client, this, pos, direction);
				this.field_3716 = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
				if (this.breakingBlock) {
					this.method_21706(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction);
				}

				BlockState blockState = this.client.world.getBlockState(pos);
				this.client.getTutorialManager().onBlockAttacked(this.client.world, pos, blockState, 0.0F);
				this.method_21706(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction);
				boolean bl = !blockState.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.onBlockBreakStart(this.client.world, pos, this.client.player);
				}

				if (bl && blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos) >= 1.0F) {
					this.breakBlock(pos);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = pos;
					this.selectedStack = this.client.player.getMainHandStack();
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			BlockState blockState = this.client.world.getBlockState(this.currentBreakingPos);
			this.client.getTutorialManager().onBlockAttacked(this.client.world, this.currentBreakingPos, blockState, -1.0F);
			this.method_21706(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN);
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, -1);
			this.client.player.resetLastAttackedTicks();
		}
	}

	public boolean method_2902(BlockPos blockPos, Direction direction) {
		this.syncSelectedSlot();
		if (this.field_3716 > 0) {
			this.field_3716--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(blockPos)) {
			this.field_3716 = 5;
			BlockState blockState = this.client.world.getBlockState(blockPos);
			this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, 1.0F);
			this.method_21706(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction);
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
						.getSoundManager()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.getHitSound(), SoundCategory.NEUTRAL, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, blockPos
							)
						);
				}

				this.field_3713++;
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.method_21706(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction);
					this.breakBlock(blockPos);
					this.currentBreakingProgress = 0.0F;
					this.field_3713 = 0.0F;
					this.field_3716 = 5;
				}

				this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
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
		if (this.networkHandler.getConnection().isOpen()) {
			this.networkHandler.getConnection().tick();
		} else {
			this.networkHandler.getConnection().handleDisconnection();
		}
	}

	private boolean isCurrentlyBreaking(BlockPos pos) {
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.selectedStack.isEmpty() && itemStack.isEmpty();
		if (!this.selectedStack.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.selectedStack.getItem()
				&& ItemStack.areTagsEqual(itemStack, this.selectedStack)
				&& (itemStack.isDamageable() || itemStack.getDamage() == this.selectedStack.getDamage());
		}

		return pos.equals(this.currentBreakingPos) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.player.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
		}
	}

	public ActionResult interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult) {
		this.syncSelectedSlot();
		BlockPos blockPos = hitResult.getBlockPos();
		Vec3d vec3d = hitResult.getPos();
		if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return ActionResult.FAIL;
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			if (this.gameMode == GameMode.SPECTATOR) {
				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
				return ActionResult.SUCCESS;
			} else {
				boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
				boolean bl2 = player.isSneaking() && bl;
				if (!bl2 && world.getBlockState(blockPos).activate(world, player, hand, hitResult)) {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
					return ActionResult.SUCCESS;
				} else {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
					if (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
						ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
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
						return ActionResult.PASS;
					}
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity player, World world, Hand hand) {
		if (this.gameMode == GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand));
			ItemStack itemStack = player.getStackInHand(hand);
			if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				int i = itemStack.getCount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.use(world, player, hand);
				ItemStack itemStack2 = typedActionResult.getValue();
				if (itemStack2 != itemStack || itemStack2.getCount() != i) {
					player.setStackInHand(hand, itemStack2);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ClientPlayerEntity createPlayer(ClientWorld world, StatHandler stateHandler, ClientRecipeBook recipeBook) {
		return new ClientPlayerEntity(this.client, world, this.networkHandler, stateHandler, recipeBook);
	}

	public void attackEntity(PlayerEntity player, Entity target) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target));
		if (this.gameMode != GameMode.SPECTATOR) {
			player.attack(target);
			player.resetLastAttackedTicks();
		}
	}

	public ActionResult interactEntity(PlayerEntity player, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : player.interact(entity, hand);
	}

	public ActionResult interactEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = hitResult.getPos().subtract(entity.x, entity.y, entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : entity.interactAt(player, vec3d, hand);
	}

	public ItemStack method_2906(int i, int j, int k, SlotActionType slotActionType, PlayerEntity playerEntity) {
		short s = playerEntity.container.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.container.onSlotClick(j, k, slotActionType, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, slotActionType, itemStack, s));
		return itemStack;
	}

	public void clickRecipe(int syncId, Recipe<?> recipe, boolean craftAll) {
		this.networkHandler.sendPacket(new CraftRequestC2SPacket(syncId, recipe, craftAll));
	}

	public void clickButton(int syncId, int buttonId) {
		this.networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, buttonId));
	}

	public void clickCreativeStack(ItemStack stack, int slotId) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
		}
	}

	public void dropCreativeStack(ItemStack stack) {
		if (this.gameMode.isCreative() && !stack.isEmpty()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
		}
	}

	public void stopUsingItem(PlayerEntity player) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
		player.stopUsingItem();
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
		return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof HorseBaseEntity;
	}

	public boolean isFlyingLocked() {
		return this.gameMode == GameMode.SPECTATOR;
	}

	public GameMode getCurrentGameMode() {
		return this.gameMode;
	}

	public boolean isBreakingBlock() {
		return this.breakingBlock;
	}

	public void pickFromInventory(int slot) {
		this.networkHandler.sendPacket(new PickFromInventoryC2SPacket(slot));
	}

	private void method_21706(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		this.field_20317.put(Pair.of(blockPos, action), new PosAndRot(clientPlayerEntity.getPos(), clientPlayerEntity.pitch, clientPlayerEntity.yaw));
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, blockPos, direction));
	}

	public void method_21705(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, PlayerActionC2SPacket.Action action, boolean bl) {
		PosAndRot posAndRot = this.field_20317.remove(Pair.of(blockPos, action));
		if (posAndRot == null || !bl || action != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK && clientWorld.getBlockState(blockPos) != blockState) {
			clientWorld.setBlockStateWithoutNeighborUpdates(blockPos, blockState);
			if (posAndRot != null) {
				Vec3d vec3d = posAndRot.getPos();
				this.client.player.updatePositionAndAngles(vec3d.x, vec3d.y, vec3d.z, posAndRot.getYaw(), posAndRot.getPitch());
			}
		}

		while (this.field_20317.size() >= 50) {
			Pair<BlockPos, PlayerActionC2SPacket.Action> pair = this.field_20317.firstKey();
			this.field_20317.removeFirst();
			field_20316.error("Too many unacked block actions, dropping " + pair);
		}
	}
}
