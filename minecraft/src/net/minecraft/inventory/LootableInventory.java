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

	/**
	 * {@return the loot table ID, or {@code null} if there is no associated loot table}
	 * 
	 * <p>This is usually stored under the {@value LOOT_TABLE_KEY} NBT key.
	 */
	@Nullable
	Identifier getLootTableId();

	/**
	 * Sets the loot table ID.
	 * 
	 * <p>This is usually stored under the {@value LOOT_TABLE_KEY} NBT key.
	 * 
	 * @param lootTableId the loot table ID, or {@code null} to remove the loot table
	 */
	void setLootTableId(@Nullable Identifier lootTableId);

	/**
	 * Sets the loot table ID and seed at once.
	 * This is useful for code-based structure generation.
	 * 
	 * @see #setLootTableId(Identifier)
	 * @see #setLootTableSeed(long)
	 * @see #setLootTable(BlockView, Random, BlockPos, Identifier)
	 */
	default void setLootTable(Identifier lootTableId, long lootTableSeed) {
		this.setLootTableId(lootTableId);
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
	 * and sets the loot table ID and seed if applicable.
	 * This is useful for code-based structure generation.
	 * 
	 * @see #setLootTableId(Identifier)
	 * @see #setLootTableSeed(long)
	 * @see #setLootTable(Identifier, long)
	 */
	static void setLootTable(BlockView world, Random random, BlockPos pos, Identifier lootTableId) {
		if (world.getBlockEntity(pos) instanceof LootableInventory lootableInventory) {
			lootableInventory.setLootTable(lootTableId, random.nextLong());
		}
	}

	/**
	 * Reads the loot table ID and seed from {@code nbt}, if the loot table ID
	 * exists in {@code nbt}. Implementations should skip reading the contents of
	 * the inventory if this returns {@code true}.
	 * 
	 * @return whether the loot table ID was found
	 */
	default boolean readLootTable(NbtCompound nbtCompound) {
		if (nbtCompound.contains("LootTable", NbtElement.STRING_TYPE)) {
			this.setLootTableId(new Identifier(nbtCompound.getString("LootTable")));
			if (nbtCompound.contains("LootTableSeed", NbtElement.LONG_TYPE)) {
				this.setLootTableSeed(nbtCompound.getLong("LootTableSeed"));
			} else {
				this.setLootTableSeed(0L);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Writes the loot table ID and seed to {@code nbt}, if {@linkplain #getLootTableId
	 * the loot table ID} is not {@code null}. Implementations should skip writing the
	 * contents of the inventory if this returns {@code true}.
	 * 
	 * <p>This skips writing the seed if it equals {@code 0L}. This has no practical
	 * difference in-game, as getting nonexistent {@code long} values return {@code 0L}.
	 * 
	 * @return whether the loot table ID was non-{@code null}
	 */
	default boolean writeLootTable(NbtCompound nbt) {
		Identifier identifier = this.getLootTableId();
		if (identifier == null) {
			return false;
		} else {
			nbt.putString("LootTable", identifier.toString());
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
		Identifier identifier = this.getLootTableId();
		if (identifier != null && world != null && world.getServer() != null) {
			LootTable lootTable = world.getServer().getLootManager().getLootTable(identifier);
			if (player instanceof ServerPlayerEntity) {
				Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, identifier);
			}

			this.setLootTableId(null);
			LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)world)
				.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos));
			if (player != null) {
				builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
			}

			lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST), this.getLootTableSeed());
		}
	}
}
