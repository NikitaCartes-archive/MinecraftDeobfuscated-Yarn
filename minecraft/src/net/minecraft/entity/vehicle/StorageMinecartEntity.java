package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NameableContainerProvider {
	private DefaultedList<ItemStack> field_7735 = DefaultedList.create(36, ItemStack.EMPTY);
	private boolean field_7733 = true;
	@Nullable
	private Identifier field_7734;
	private long lootSeed;

	protected StorageMinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	protected StorageMinecartEntity(EntityType<?> entityType, double d, double e, double f, World world) {
		super(entityType, world, d, e, f);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			ItemScatterer.method_5452(this.field_6002, this, this);
		}
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_7735) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack method_5438(int i) {
		this.method_7563(null);
		return this.field_7735.get(i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		this.method_7563(null);
		return Inventories.method_5430(this.field_7735, i, j);
	}

	@Override
	public ItemStack method_5441(int i) {
		this.method_7563(null);
		ItemStack itemStack = this.field_7735.get(i);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.field_7735.set(i, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.method_7563(null);
		this.field_7735.set(i, itemStack);
		if (!itemStack.isEmpty() && itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (i >= 0 && i < this.getInvSize()) {
			this.method_5447(i, itemStack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.invalid ? false : !(playerEntity.squaredDistanceTo(this) > 64.0);
	}

	@Nullable
	@Override
	public Entity method_5731(DimensionType dimensionType) {
		this.field_7733 = false;
		return super.method_5731(dimensionType);
	}

	@Override
	public void invalidate() {
		if (!this.field_6002.isClient && this.field_7733) {
			ItemScatterer.method_5452(this.field_6002, this, this);
		}

		super.invalidate();
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (this.field_7734 != null) {
			compoundTag.putString("LootTable", this.field_7734.toString());
			if (this.lootSeed != 0L) {
				compoundTag.putLong("LootTableSeed", this.lootSeed);
			}
		} else {
			Inventories.method_5426(compoundTag, this.field_7735);
		}
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.field_7735 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (compoundTag.containsKey("LootTable", 8)) {
			this.field_7734 = new Identifier(compoundTag.getString("LootTable"));
			this.lootSeed = compoundTag.getLong("LootTableSeed");
		} else {
			Inventories.method_5429(compoundTag, this.field_7735);
		}
	}

	@Override
	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		playerEntity.openContainer(this);
		return true;
	}

	@Override
	protected void method_7525() {
		float f = 0.98F;
		if (this.field_7734 == null) {
			int i = 15 - Container.calculateComparatorOutput(this);
			f += (float)i * 0.001F;
		}

		this.method_18799(this.method_18798().multiply((double)f, 0.0, (double)f));
	}

	public void method_7563(@Nullable PlayerEntity playerEntity) {
		if (this.field_7734 != null && this.field_6002.getServer() != null) {
			LootSupplier lootSupplier = this.field_6002.getServer().getLootManager().method_367(this.field_7734);
			this.field_7734 = null;
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.field_6002)
				.method_312(LootContextParameters.field_1232, new BlockPos(this))
				.setRandom(this.lootSeed);
			if (playerEntity != null) {
				builder.setLuck(playerEntity.getLuck()).method_312(LootContextParameters.field_1226, playerEntity);
			}

			lootSupplier.supplyInventory(this, builder.method_309(LootContextTypes.CHEST));
		}
	}

	@Override
	public void clear() {
		this.method_7563(null);
		this.field_7735.clear();
	}

	public void method_7562(Identifier identifier, long l) {
		this.field_7734 = identifier;
		this.lootSeed = l;
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.field_7734 != null && playerEntity.isSpectator()) {
			return null;
		} else {
			this.method_7563(playerInventory.field_7546);
			return this.method_17357(i, playerInventory);
		}
	}

	protected abstract Container method_17357(int i, PlayerInventory playerInventory);
}
