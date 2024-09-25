package net.minecraft.entity.vehicle;

import java.util.function.Supplier;
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
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractChestBoatEntity extends AbstractBoatEntity implements RideableInventory, VehicleInventory {
	private static final int INVENTORY_SIZE = 27;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	@Nullable
	private RegistryKey<LootTable> lootTable;
	private long lootTableSeed;

	public AbstractChestBoatEntity(EntityType<? extends AbstractChestBoatEntity> entityType, World world, Supplier<Item> supplier) {
		super(entityType, world, supplier);
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
		this.writeInventoryToNbt(nbt, this.getRegistryManager());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.readInventoryFromNbt(nbt, this.getRegistryManager());
	}

	@Override
	public void killAndDropSelf(ServerWorld world, DamageSource damageSource) {
		this.killAndDropItem(world, this.asItem());
		this.onBroken(damageSource, world, this);
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (!this.getWorld().isClient && reason.shouldDestroy()) {
			ItemScatterer.spawn(this.getWorld(), this, this);
		}

		super.remove(reason);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (!player.shouldCancelInteraction()) {
			ActionResult actionResult = super.interact(player, hand);
			if (actionResult != ActionResult.PASS) {
				return actionResult;
			}
		}

		if (this.canAddPassenger(player) && !player.shouldCancelInteraction()) {
			return ActionResult.PASS;
		} else {
			ActionResult actionResult = this.open(player);
			if (actionResult.isAccepted() && player.getWorld() instanceof ServerWorld serverWorld) {
				this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
			}

			return actionResult;
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		player.openHandledScreen(this);
		if (player.getWorld() instanceof ServerWorld serverWorld) {
			this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
			PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
		}
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
		if (this.lootTable != null && playerEntity.isSpectator()) {
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
	public RegistryKey<LootTable> getLootTable() {
		return this.lootTable;
	}

	@Override
	public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
		this.lootTable = lootTable;
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
		this.getWorld().emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of(player));
	}
}
