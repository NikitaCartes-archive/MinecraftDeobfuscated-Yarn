package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public abstract class LootableContainerBlockEntity extends LockableContainerBlockEntity {
	@Nullable
	protected Identifier field_12037;
	protected long lootTableSeed;

	protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public static void method_11287(BlockView blockView, Random random, BlockPos blockPos, Identifier identifier) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		if (blockEntity instanceof LootableContainerBlockEntity) {
			((LootableContainerBlockEntity)blockEntity).method_11285(identifier, random.nextLong());
		}
	}

	protected boolean method_11283(CompoundTag compoundTag) {
		if (compoundTag.containsKey("LootTable", 8)) {
			this.field_12037 = new Identifier(compoundTag.getString("LootTable"));
			this.lootTableSeed = compoundTag.getLong("LootTableSeed");
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_11286(CompoundTag compoundTag) {
		if (this.field_12037 == null) {
			return false;
		} else {
			compoundTag.putString("LootTable", this.field_12037.toString());
			if (this.lootTableSeed != 0L) {
				compoundTag.putLong("LootTableSeed", this.lootTableSeed);
			}

			return true;
		}
	}

	public void checkLootInteraction(@Nullable PlayerEntity playerEntity) {
		if (this.field_12037 != null && this.world.getServer() != null) {
			LootSupplier lootSupplier = this.world.getServer().getLootManager().method_367(this.field_12037);
			this.field_12037 = null;
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
				.method_312(LootContextParameters.field_1232, new BlockPos(this.field_11867))
				.setRandom(this.lootTableSeed);
			if (playerEntity != null) {
				builder.setLuck(playerEntity.getLuck()).method_312(LootContextParameters.field_1226, playerEntity);
			}

			lootSupplier.supplyInventory(this, builder.method_309(LootContextTypes.CHEST));
		}
	}

	public void method_11285(Identifier identifier, long l) {
		this.field_12037 = identifier;
		this.lootTableSeed = l;
	}

	@Override
	public ItemStack method_5438(int i) {
		this.checkLootInteraction(null);
		return this.method_11282().get(i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		this.checkLootInteraction(null);
		ItemStack itemStack = Inventories.method_5430(this.method_11282(), i, j);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack method_5441(int i) {
		this.checkLootInteraction(null);
		return Inventories.method_5428(this.method_11282(), i);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		this.checkLootInteraction(null);
		this.method_11282().set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.world.method_8321(this.field_11867) != this
			? false
			: !(
				playerEntity.squaredDistanceTo((double)this.field_11867.getX() + 0.5, (double)this.field_11867.getY() + 0.5, (double)this.field_11867.getZ() + 0.5) > 64.0
			);
	}

	@Override
	public void clear() {
		this.method_11282().clear();
	}

	protected abstract DefaultedList<ItemStack> method_11282();

	protected abstract void method_11281(DefaultedList<ItemStack> defaultedList);

	@Override
	public boolean checkUnlocked(PlayerEntity playerEntity) {
		return super.checkUnlocked(playerEntity) && (this.field_12037 == null || !playerEntity.isSpectator());
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.checkUnlocked(playerEntity)) {
			this.checkLootInteraction(playerInventory.field_7546);
			return this.createContainer(i, playerInventory);
		} else {
			return null;
		}
	}
}
