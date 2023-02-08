package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ChestBoatEntity extends BoatEntity implements RideableInventory, VehicleInventory {
	private static final int INVENTORY_SIZE = 27;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	@Nullable
	private Identifier lootTableId;
	private long lootTableSeed;

	public ChestBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
		super(entityType, world);
	}

	public ChestBoatEntity(World world, double d, double e, double f) {
		this(EntityType.CHEST_BOAT, world);
		this.setPosition(d, e, f);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	@Override
	protected float getPassengerHorizontalOffset() {
		return 0.15F;
	}

	@Override
	protected int getMaxPassengers() {
		return 1;
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.writeInventoryToNbt(nbt);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.readInventoryFromNbt(nbt);
	}

	@Override
	public void dropItems(DamageSource source) {
		super.dropItems(source);
		this.onBroken(source, this.world, this);
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (!this.world.isClient && reason.shouldDestroy()) {
			ItemScatterer.spawn(this.world, this, this);
		}

		super.remove(reason);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (this.canAddPassenger(player) && !player.shouldCancelInteraction()) {
			return super.interact(player, hand);
		} else {
			ActionResult actionResult = this.open(player);
			if (actionResult.isAccepted()) {
				this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinBrain.onGuardedBlockInteracted(player, true);
			}

			return actionResult;
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		player.openHandledScreen(this);
		if (!player.world.isClient) {
			this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
			PiglinBrain.onGuardedBlockInteracted(player, true);
		}
	}

	@Override
	public Item asItem() {
		return switch (this.getVariant()) {
			case SPRUCE -> Items.SPRUCE_CHEST_BOAT;
			case BIRCH -> Items.BIRCH_CHEST_BOAT;
			case JUNGLE -> Items.JUNGLE_CHEST_BOAT;
			case ACACIA -> Items.ACACIA_CHEST_BOAT;
			case DARK_OAK -> Items.DARK_OAK_CHEST_BOAT;
			case MANGROVE -> Items.MANGROVE_CHEST_BOAT;
			case BAMBOO -> Items.BAMBOO_CHEST_RAFT;
			default -> Items.OAK_CHEST_BOAT;
		};
	}

	@Override
	public void clear() {
		this.clearInventory();
	}

	@Override
	public int size() {
		return 27;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.getInventoryStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return this.removeInventoryStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeInventoryStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.setInventoryStack(slot, stack);
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return this.getInventoryStackReference(mappedIndex);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.canPlayerAccess(player);
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.lootTableId != null && playerEntity.isSpectator()) {
			return null;
		} else {
			this.generateLoot(playerInventory.player);
			return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, this);
		}
	}

	public void generateLoot(@Nullable PlayerEntity player) {
		this.generateInventoryLoot(player);
	}

	@Nullable
	@Override
	public Identifier getLootTableId() {
		return this.lootTableId;
	}

	@Override
	public void setLootTableId(@Nullable Identifier lootTableId) {
		this.lootTableId = lootTableId;
	}

	@Override
	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	@Override
	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}

	@Override
	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}

	@Override
	public void resetInventory() {
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
	}

	@Override
	public void onClose(PlayerEntity player) {
		this.world.emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of(player));
	}
}
