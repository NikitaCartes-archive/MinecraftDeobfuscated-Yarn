package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NamedScreenHandlerFactory {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
	private boolean field_7733 = true;
	@Nullable
	private Identifier lootTableId;
	private long lootSeed;

	protected StorageMinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	protected StorageMinecartEntity(EntityType<?> type, double x, double y, double z, World world) {
		super(type, world, x, y, z);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemScatterer.spawn(this.world, this, this);
			if (!this.world.isClient) {
				Entity entity = damageSource.getSource();
				if (entity != null && entity.getType() == EntityType.PLAYER) {
					PiglinBrain.onGuardedBlockInteracted((PlayerEntity)entity, true);
				}
			}
		}
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		this.generateLoot(null);
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.generateLoot(null);
		return Inventories.splitStack(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		this.generateLoot(null);
		ItemStack itemStack = this.inventory.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.inventory.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.generateLoot(null);
		this.inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot >= 0 && slot < this.size()) {
			this.setStack(slot, item);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.removed ? false : !(player.squaredDistanceTo(this) > 64.0);
	}

	@Nullable
	@Override
	public Entity moveToWorld(ServerWorld destination) {
		this.field_7733 = false;
		return super.moveToWorld(destination);
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
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (tag.contains("LootTable", 8)) {
			this.lootTableId = new Identifier(tag.getString("LootTable"));
			this.lootSeed = tag.getLong("LootTableSeed");
		} else {
			Inventories.fromTag(tag, this.inventory);
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		player.openHandledScreen(this);
		if (!player.world.isClient) {
			PiglinBrain.onGuardedBlockInteracted(player, true);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.SUCCESS;
		}
	}

	@Override
	protected void applySlowdown() {
		float f = 0.98F;
		if (this.lootTableId == null) {
			int i = 15 - ScreenHandler.calculateComparatorOutput(this);
			f += (float)i * 0.001F;
		}

		this.setVelocity(this.getVelocity().multiply((double)f, 0.0, (double)f));
	}

	public void generateLoot(@Nullable PlayerEntity player) {
		if (this.lootTableId != null && this.world.getServer() != null) {
			LootTable lootTable = this.world.getServer().getLootManager().getTable(this.lootTableId);
			if (player instanceof ServerPlayerEntity) {
				Criteria.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity)player, this.lootTableId);
			}

			this.lootTableId = null;
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).parameter(LootContextParameters.ORIGIN, this.getPos()).random(this.lootSeed);
			if (player != null) {
				builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
			}

			lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
		}
	}

	@Override
	public void clear() {
		this.generateLoot(null);
		this.inventory.clear();
	}

	public void setLootTable(Identifier id, long lootSeed) {
		this.lootTableId = id;
		this.lootSeed = lootSeed;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.lootTableId != null && playerEntity.isSpectator()) {
			return null;
		} else {
			this.generateLoot(playerInventory.player);
			return this.getScreenHandler(i, playerInventory);
		}
	}

	protected abstract ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory);
}
