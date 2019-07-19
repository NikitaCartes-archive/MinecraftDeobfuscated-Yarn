package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NameableContainerFactory {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
	private boolean field_7733 = true;
	@Nullable
	private Identifier lootTableId;
	private long lootSeed;

	protected StorageMinecartEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	protected StorageMinecartEntity(EntityType<?> type, double x, double y, double z, World world) {
		super(type, world, x, y, z);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemScatterer.spawn(this.world, this, this);
		}
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		this.method_7563(null);
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.method_7563(null);
		return Inventories.splitStack(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		this.method_7563(null);
		ItemStack itemStack = this.inventory.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.inventory.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.method_7563(null);
		this.inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot >= 0 && slot < this.getInvSize()) {
			this.setInvStack(slot, item);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.removed ? false : !(player.squaredDistanceTo(this) > 64.0);
	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionType newDimension) {
		this.field_7733 = false;
		return super.changeDimension(newDimension);
	}

	@Override
	public void remove() {
		if (!this.world.isClient && this.field_7733) {
			ItemScatterer.spawn(this.world, this, this);
		}

		super.remove();
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		if (this.lootTableId != null) {
			tag.putString("LootTable", this.lootTableId.toString());
			if (this.lootSeed != 0L) {
				tag.putLong("LootTableSeed", this.lootSeed);
			}
		} else {
			Inventories.toTag(tag, this.inventory);
		}
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (tag.contains("LootTable", 8)) {
			this.lootTableId = new Identifier(tag.getString("LootTable"));
			this.lootSeed = tag.getLong("LootTableSeed");
		} else {
			Inventories.fromTag(tag, this.inventory);
		}
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		player.openContainer(this);
		return true;
	}

	@Override
	protected void method_7525() {
		float f = 0.98F;
		if (this.lootTableId == null) {
			int i = 15 - Container.calculateComparatorOutput(this);
			f += (float)i * 0.001F;
		}

		this.setVelocity(this.getVelocity().multiply((double)f, 0.0, (double)f));
	}

	public void method_7563(@Nullable PlayerEntity playerEntity) {
		if (this.lootTableId != null && this.world.getServer() != null) {
			LootTable lootTable = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
			this.lootTableId = null;
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
				.put(LootContextParameters.POSITION, new BlockPos(this))
				.setRandom(this.lootSeed);
			if (playerEntity != null) {
				builder.setLuck(playerEntity.getLuck()).put(LootContextParameters.THIS_ENTITY, playerEntity);
			}

			lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
		}
	}

	@Override
	public void clear() {
		this.method_7563(null);
		this.inventory.clear();
	}

	public void setLootTable(Identifier id, long l) {
		this.lootTableId = id;
		this.lootSeed = l;
	}

	@Nullable
	@Override
	public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.lootTableId != null && playerEntity.isSpectator()) {
			return null;
		} else {
			this.method_7563(playerInventory.player);
			return this.getContainer(syncId, playerInventory);
		}
	}

	protected abstract Container getContainer(int syncId, PlayerInventory playerInventory);
}
