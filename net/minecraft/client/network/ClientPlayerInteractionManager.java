/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerInteractionManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private final ClientPlayNetworkHandler networkHandler;
    private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
    private ItemStack selectedStack = ItemStack.EMPTY;
    private float currentBreakingProgress;
    private float blockBreakingSoundCooldown;
    private int blockBreakingCooldown;
    private boolean breakingBlock;
    private GameMode gameMode = GameMode.DEFAULT;
    @Nullable
    private GameMode previousGameMode;
    private int lastSelectedSlot;

    public ClientPlayerInteractionManager(MinecraftClient client, ClientPlayNetworkHandler networkHandler) {
        this.client = client;
        this.networkHandler = networkHandler;
    }

    public void copyAbilities(PlayerEntity player) {
        this.gameMode.setAbilities(player.getAbilities());
    }

    public void setGameModes(GameMode gameMode, @Nullable GameMode previousGameMode) {
        this.gameMode = gameMode;
        this.previousGameMode = previousGameMode;
        this.gameMode.setAbilities(this.client.player.getAbilities());
    }

    public void setGameMode(GameMode gameMode) {
        if (gameMode != this.gameMode) {
            this.previousGameMode = this.gameMode;
        }
        this.gameMode = gameMode;
        this.gameMode.setAbilities(this.client.player.getAbilities());
    }

    public boolean hasStatusBars() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean breakBlock(BlockPos pos) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, pos, this.gameMode)) {
            return false;
        }
        ClientWorld world = this.client.world;
        BlockState blockState = world.getBlockState(pos);
        if (!this.client.player.getMainHandStack().getItem().canMine(blockState, world, pos, this.client.player)) {
            return false;
        }
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock && !this.client.player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (blockState.isAir()) {
            return false;
        }
        block.onBreak(world, pos, blockState, this.client.player);
        FluidState fluidState = world.getFluidState(pos);
        boolean bl = world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        if (bl) {
            block.onBroken(world, pos, blockState);
        }
        return bl;
    }

    public boolean attackBlock(BlockPos pos, Direction direction) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, pos, this.gameMode)) {
            return false;
        }
        if (!this.client.world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (this.gameMode.isCreative()) {
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 1.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                this.breakBlock(pos);
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
            this.blockBreakingCooldown = 5;
        } else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
            if (this.breakingBlock) {
                this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction));
            }
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 0.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                boolean bl;
                boolean bl2 = bl = !blockState.isAir();
                if (bl && this.currentBreakingProgress == 0.0f) {
                    blockState.onBlockBreakStart(this.client.world, pos, this.client.player);
                }
                if (bl && blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos) >= 1.0f) {
                    this.breakBlock(pos);
                } else {
                    this.breakingBlock = true;
                    this.currentBreakingPos = pos;
                    this.selectedStack = this.client.player.getMainHandStack();
                    this.currentBreakingProgress = 0.0f;
                    this.blockBreakingSoundCooldown = 0.0f;
                    this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
                }
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
        }
        return true;
    }

    public void cancelBlockBreaking() {
        if (this.breakingBlock) {
            BlockState blockState = this.client.world.getBlockState(this.currentBreakingPos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, this.currentBreakingPos, blockState, -1.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
            this.breakingBlock = false;
            this.currentBreakingProgress = 0.0f;
            this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, -1);
            this.client.player.resetLastAttackedTicks();
        }
    }

    public boolean updateBlockBreakingProgress(BlockPos pos, Direction direction) {
        this.syncSelectedSlot();
        if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            return true;
        }
        if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(pos)) {
            this.blockBreakingCooldown = 5;
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 1.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                this.breakBlock(pos);
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
            return true;
        }
        if (this.isCurrentlyBreaking(pos)) {
            BlockState blockState = this.client.world.getBlockState(pos);
            if (blockState.isAir()) {
                this.breakingBlock = false;
                return false;
            }
            this.currentBreakingProgress += blockState.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos);
            if (this.blockBreakingSoundCooldown % 4.0f == 0.0f) {
                BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                this.client.getSoundManager().play(new PositionedSoundInstance(blockSoundGroup.getHitSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 8.0f, blockSoundGroup.getPitch() * 0.5f, SoundInstance.createRandom(), pos));
            }
            this.blockBreakingSoundCooldown += 1.0f;
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0f, 1.0f));
            if (this.currentBreakingProgress >= 1.0f) {
                this.breakingBlock = false;
                this.sendSequencedPacket(this.client.world, sequence -> {
                    this.breakBlock(pos);
                    return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
                });
                this.currentBreakingProgress = 0.0f;
                this.blockBreakingSoundCooldown = 0.0f;
                this.blockBreakingCooldown = 5;
            }
        } else {
            return this.attackBlock(pos, direction);
        }
        this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
        return true;
    }

    private void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        try (PendingUpdateManager pendingUpdateManager = world.getPendingUpdateManager().incrementSequence();){
            int i = pendingUpdateManager.getSequence();
            Packet<ServerPlayPacketListener> packet = packetCreator.predict(i);
            this.networkHandler.sendPacket(packet);
        }
    }

    public float getReachDistance() {
        if (this.gameMode.isCreative()) {
            return 5.0f;
        }
        return 4.5f;
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
        boolean bl;
        ItemStack itemStack = this.client.player.getMainHandStack();
        boolean bl2 = bl = this.selectedStack.isEmpty() && itemStack.isEmpty();
        if (!this.selectedStack.isEmpty() && !itemStack.isEmpty()) {
            bl = itemStack.isOf(this.selectedStack.getItem()) && ItemStack.areNbtEqual(itemStack, this.selectedStack) && (itemStack.isDamageable() || itemStack.getDamage() == this.selectedStack.getDamage());
        }
        return pos.equals(this.currentBreakingPos) && bl;
    }

    private void syncSelectedSlot() {
        int i = this.client.player.getInventory().selectedSlot;
        if (i != this.lastSelectedSlot) {
            this.lastSelectedSlot = i;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
        }
    }

    public ActionResult interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        this.syncSelectedSlot();
        if (!this.client.world.getWorldBorder().contains(hitResult.getBlockPos())) {
            return ActionResult.FAIL;
        }
        MutableObject mutableObject = new MutableObject();
        this.sendSequencedPacket(this.client.world, sequence -> {
            mutableObject.setValue(this.interactBlockInternal(player, hand, hitResult));
            return new PlayerInteractBlockC2SPacket(hand, hitResult, sequence);
        });
        return (ActionResult)((Object)mutableObject.getValue());
    }

    private ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ActionResult actionResult2;
        boolean bl2;
        BlockPos blockPos = hitResult.getBlockPos();
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.SUCCESS;
        }
        boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        boolean bl3 = bl2 = player.shouldCancelInteraction() && bl;
        if (!bl2) {
            BlockState blockState = this.client.world.getBlockState(blockPos);
            if (!this.networkHandler.hasFeature(blockState.getBlock().getRequiredFeatures())) {
                return ActionResult.FAIL;
            }
            ActionResult actionResult = blockState.onUse(this.client.world, player, hand, hitResult);
            if (actionResult.isAccepted()) {
                return actionResult;
            }
        }
        if (itemStack.isEmpty() || player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            return ActionResult.PASS;
        }
        ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
        if (this.gameMode.isCreative()) {
            int i = itemStack.getCount();
            actionResult2 = itemStack.useOnBlock(itemUsageContext);
            itemStack.setCount(i);
        } else {
            actionResult2 = itemStack.useOnBlock(itemUsageContext);
        }
        return actionResult2;
    }

    public ActionResult interactItem(PlayerEntity player, Hand hand) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround()));
        MutableObject mutableObject = new MutableObject();
        this.sendSequencedPacket(this.client.world, sequence -> {
            PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(hand, sequence);
            ItemStack itemStack = player.getStackInHand(hand);
            if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
                mutableObject.setValue(ActionResult.PASS);
                return playerInteractItemC2SPacket;
            }
            TypedActionResult<ItemStack> typedActionResult = itemStack.use(this.client.world, player, hand);
            ItemStack itemStack2 = typedActionResult.getValue();
            if (itemStack2 != itemStack) {
                player.setStackInHand(hand, itemStack2);
            }
            mutableObject.setValue(typedActionResult.getResult());
            return playerInteractItemC2SPacket;
        });
        return (ActionResult)((Object)mutableObject.getValue());
    }

    public ClientPlayerEntity createPlayer(ClientWorld world, StatHandler statHandler, ClientRecipeBook recipeBook) {
        return this.createPlayer(world, statHandler, recipeBook, false, false);
    }

    public ClientPlayerEntity createPlayer(ClientWorld world, StatHandler statHandler, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
        return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler, recipeBook, lastSneaking, lastSprinting);
    }

    public void attackEntity(PlayerEntity player, Entity target) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, player.isSneaking()));
        if (this.gameMode != GameMode.SPECTATOR) {
            player.attack(target);
            player.resetLastAttackedTicks();
        }
    }

    public ActionResult interactEntity(PlayerEntity player, Entity entity, Hand hand) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(entity, player.isSneaking(), hand));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return player.interact(entity, hand);
    }

    public ActionResult interactEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand) {
        this.syncSelectedSlot();
        Vec3d vec3d = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
        this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interactAt(entity, player.isSneaking(), hand, vec3d));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return entity.interactAt(player, vec3d, hand);
    }

    /**
     * @see net.minecraft.screen.ScreenHandler#onSlotClick(int, int, net.minecraft.screen.slot.SlotActionType, net.minecraft.entity.player.PlayerEntity)
     */
    public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if (syncId != screenHandler.syncId) {
            LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", (Object)syncId, (Object)screenHandler.syncId);
            return;
        }
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
        }
        screenHandler.onSlotClick(slotId, button, actionType, player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<ItemStack>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = (ItemStack)list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }
        this.networkHandler.sendPacket(new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }

    public void clickRecipe(int syncId, Recipe<?> recipe, boolean craftAll) {
        this.networkHandler.sendPacket(new CraftRequestC2SPacket(syncId, recipe, craftAll));
    }

    public void clickButton(int syncId, int buttonId) {
        this.networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, buttonId));
    }

    public void clickCreativeStack(ItemStack stack, int slotId) {
        if (this.gameMode.isCreative() && this.networkHandler.hasFeature(stack.getItem().getRequiredFeatures())) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
        }
    }

    public void dropCreativeStack(ItemStack stack) {
        if (this.gameMode.isCreative() && !stack.isEmpty() && this.networkHandler.hasFeature(stack.getItem().getRequiredFeatures())) {
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
        return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof RideableInventory;
    }

    public boolean isFlyingLocked() {
        return this.gameMode == GameMode.SPECTATOR;
    }

    @Nullable
    public GameMode getPreviousGameMode() {
        return this.previousGameMode;
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
}

