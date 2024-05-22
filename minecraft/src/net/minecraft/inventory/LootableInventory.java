package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * An inventory whose contents can be supplied from a loot table.
 * 
 * <p>This is usually implemented by block entities, which extend {@link
 * net.minecraft.block.entity.LootableContainerBlockEntity}.
 */
public interface LootableInventory extends Inventory {
	String LOOT_TABLE_KEY = "LootTable";
	String LOOT_TABLE_SEED_KEY = "LootTableSeed";

	@Nullable
	RegistryKey<LootTable> getLootTable();

	void setLootTable(@Nullable RegistryKey<LootTable> lootTable);

	/**
	 * Sets the loot table and seed at once.
	 * This is useful for code-based structure generation.
	 * 
	 * @see #setLootTable(RegistryKey)
	 * @see #setLootTableSeed(long)
	 * @see #setLootTable(BlockView, Random, BlockPos, RegistryKey)
	 */
	default void setLootTable(RegistryKey<LootTable> lootTableId, long lootTableSeed) {
		this.setLootTable(lootTableId);
		this.setLootTableSeed(lootTableSeed);
	}

	/**
	 * {@return the loot table's seed}
	 * 
	 * <p>Vanilla implementations return {@code 0} when there is no loot
	 * table associated with the inventory, although it is not necessary.
	 * 
	 * <p>This is usually stored under the {@value LOOT_TABLE_SEED_KEY} NBT key.
	 */
	long getLootTableSeed();

	/**
	 * Sets the loot table's seed.
	 * 
	 * <p>Vanilla implementations return {@code 0} when there is no loot
	 * table associated with the inventory, although it is not necessary.
	 * 
	 * <p>This is usually stored under the {@value LOOT_TABLE_SEED_KEY} NBT key.
	 */
	void setLootTableSeed(long lootTableSeed);

	BlockPos getPos();

	@Nullable
	World getWorld();

	/**
	 * Queries the block entity at {@code pos}, checks if it is a {@link LootableInventory},
	 * and sets the loot table and seed if applicable.
	 * This is useful for code-based structure generation.
	 * 
	 * @see #setLootTable(RegistryKey)
	 * @see #setLootTableSeed(long)
	 * @see #setLootTable(RegistryKey, long)
	 */
	static void setLootTable(BlockView world, Random random, BlockPos pos, RegistryKey<LootTable> lootTableId) {
		if (world.getBlockEntity(pos) instanceof LootableInventory lootableInventory) {
			lootableInventory.setLootTable(lootTableId, random.nextLong());
		}
	}

	/**
	 * Reads the loot table and seed from {@code nbt}, if the loot table
	 * exists in {@code nbt}. Implementations should skip reading the contents of
	 * the inventory if this returns {@code true}.
	 * 
	 * @return whether the loot table was found
	 */
	default boolean readLootTable(NbtCompound nbt) {
		if (nbt.contains("LootTable", NbtElement.STRING_TYPE)) {
			this.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.method_60654(nbt.getString("LootTable"))));
			if (nbt.contains("LootTableSeed", NbtElement.LONG_TYPE)) {
				this.setLootTableSeed(nbt.getLong("LootTableSeed"));
			} else {
				this.setLootTableSeed(0L);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Writes the loot table and seed to {@code nbt}, if {@linkplain #getLootTable
	 * the loot table} is not {@code null}. Implementations should skip writing the
	 * contents of the inventory if this returns {@code true}.
	 * 
	 * <p>This skips writing the seed if it equals {@code 0L}. This has no practical
	 * difference in-game, as getting nonexistent {@code long} values return {@code 0L}.
	 * 
	 * @return whether the loot table was non-{@code null}
	 */
	default boolean writeLootTable(NbtCompound nbt) {
		RegistryKey<LootTable> registryKey = this.getLootTable();
		if (registryKey == null) {
			return false;
		} else {
			nbt.putString("LootTable", registryKey.getValue().toString());
			long l = this.getLootTableSeed();
			if (l != 0L) {
				nbt.putLong("LootTableSeed", l);
			}

			return true;
		}
	}

	/**
	 * Replaces the contents of this inventory with the generated loot, if it exists.
	 * Does nothing if there is no loot table associated with this inventory.
	 * After generation, the loot table is removed from the inventory.
	 * 
	 * <p>Implementations should call this method whenever the inventory is accessed.
	 * 
	 * @param player the player that triggered this generation (by opening, breaking, etc), or
	 * {@code null} if there was no player involvement
	 */
	default void generateLoot(@Nullable PlayerEntity player) {
		World world = this.getWorld();
		BlockPos blockPos = this.getPos();
		RegistryKey<LootTable> registryKey = this.getLootTable();
		if (registryKey != null && world != null && world.getServer() != null) {
			LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(registryKey);
			if (player instanceof ServerPlayerEntity) {
				Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, registryKey);
			}

			this.setLootTable(null);
			LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)world)
				.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos));
			if (player != null) {
				builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
			}

			lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST), this.getLootTableSeed());
		}
	}
}
