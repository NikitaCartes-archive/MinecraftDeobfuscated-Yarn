package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class LootableContainerBlockEntity extends LockableContainerBlockEntity implements LootableInventory {
	@Nullable
	protected Identifier lootTableId;
	protected long lootTableSeed = 0L;

	protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
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
	public boolean isEmpty() {
		this.generateLoot(null);
		return super.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		this.generateLoot(null);
		return super.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.generateLoot(null);
		return super.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		this.generateLoot(null);
		return super.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.generateLoot(null);
		super.setStack(slot, stack);
	}

	@Override
	public boolean checkUnlocked(PlayerEntity player) {
		return super.checkUnlocked(player) && (this.lootTableId == null || !player.isSpectator());
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		if (this.checkUnlocked(playerEntity)) {
			this.generateLoot(playerInventory.player);
			return this.createScreenHandler(i, playerInventory);
		} else {
			return null;
		}
	}

	@Override
	public void readComponents(ComponentMap components) {
		super.readComponents(components);
		ContainerLootComponent containerLootComponent = components.get(DataComponentTypes.CONTAINER_LOOT);
		if (containerLootComponent != null) {
			this.lootTableId = containerLootComponent.lootTable();
			this.lootTableSeed = containerLootComponent.seed();
		}
	}

	@Override
	public void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		if (this.lootTableId != null) {
			componentMapBuilder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTableId, this.lootTableSeed));
		}
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("LootTable");
		nbt.remove("LootTableSeed");
	}
}
