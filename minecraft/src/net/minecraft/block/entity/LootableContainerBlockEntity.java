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
	protected Identifier lootTableId;
	protected long lootTableSeed;

	protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public static void setLootTable(BlockView blockView, Random random, BlockPos blockPos, Identifier identifier) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof LootableContainerBlockEntity) {
			((LootableContainerBlockEntity)blockEntity).setLootTable(identifier, random.nextLong());
		}
	}

	protected boolean deserializeLootTable(CompoundTag compoundTag) {
		if (compoundTag.containsKey("LootTable", 8)) {
			this.lootTableId = new Identifier(compoundTag.getString("LootTable"));
			this.lootTableSeed = compoundTag.getLong("LootTableSeed");
			return true;
		} else {
			return false;
		}
	}

	protected boolean serializeLootTable(CompoundTag compoundTag) {
		if (this.lootTableId == null) {
			return false;
		} else {
			compoundTag.putString("LootTable", this.lootTableId.toString());
			if (this.lootTableSeed != 0L) {
				compoundTag.putLong("LootTableSeed", this.lootTableSeed);
			}

			return true;
		}
	}

	public void checkLootInteraction(@Nullable PlayerEntity playerEntity) {
		if (this.lootTableId != null && this.world.getServer() != null) {
			LootSupplier lootSupplier = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
			this.lootTableId = null;
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
				.put(LootContextParameters.field_1232, new BlockPos(this.pos))
				.setRandom(this.lootTableSeed);
			if (playerEntity != null) {
				builder.setLuck(playerEntity.getLuck()).put(LootContextParameters.field_1226, playerEntity);
			}

			lootSupplier.supplyInventory(this, builder.build(LootContextTypes.field_1179));
		}
	}

	public void setLootTable(Identifier identifier, long l) {
		this.lootTableId = identifier;
		this.lootTableSeed = l;
	}

	@Override
	public ItemStack getInvStack(int i) {
		this.checkLootInteraction(null);
		return this.getInvStackList().get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		this.checkLootInteraction(null);
		ItemStack itemStack = Inventories.splitStack(this.getInvStackList(), i, j);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		this.checkLootInteraction(null);
		return Inventories.removeStack(this.getInvStackList(), i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.checkLootInteraction(null);
		this.getInvStackList().set(i, itemStack);
		if (itemStack.getCount() > this.getInvMaxStackAmount()) {
			itemStack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void clear() {
		this.getInvStackList().clear();
	}

	protected abstract DefaultedList<ItemStack> getInvStackList();

	protected abstract void setInvStackList(DefaultedList<ItemStack> defaultedList);

	@Override
	public boolean checkUnlocked(PlayerEntity playerEntity) {
		return super.checkUnlocked(playerEntity) && (this.lootTableId == null || !playerEntity.isSpectator());
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.checkUnlocked(playerEntity)) {
			this.checkLootInteraction(playerInventory.player);
			return this.createContainer(i, playerInventory);
		} else {
			return null;
		}
	}
}
