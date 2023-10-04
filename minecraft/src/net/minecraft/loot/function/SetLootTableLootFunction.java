package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	public static final Codec<SetLootTableLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<Identifier, long, RegistryEntry<BlockEntityType<?>>>and(
					instance.group(
						Identifier.CODEC.fieldOf("name").forGetter(function -> function.id),
						Codecs.createStrictOptionalFieldCodec(Codec.LONG, "seed", 0L).forGetter(function -> function.seed),
						Registries.BLOCK_ENTITY_TYPE.createEntryCodec().fieldOf("type").forGetter(function -> function.type)
					)
				)
				.apply(instance, SetLootTableLootFunction::new)
	);
	private final Identifier id;
	private final long seed;
	private final RegistryEntry<BlockEntityType<?>> type;

	private SetLootTableLootFunction(List<LootCondition> conditions, Identifier id, long seed, RegistryEntry<BlockEntityType<?>> blockEntityType) {
		super(conditions);
		this.id = id;
		this.seed = seed;
		this.type = blockEntityType;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_LOOT_TABLE;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
			if (nbtCompound == null) {
				nbtCompound = new NbtCompound();
			}

			nbtCompound.putString("LootTable", this.id.toString());
			if (this.seed != 0L) {
				nbtCompound.putLong("LootTableSeed", this.seed);
			}

			BlockItem.setBlockEntityNbt(stack, this.type.value(), nbtCompound);
			return stack;
		}
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);
		LootDataKey<LootTable> lootDataKey = new LootDataKey<>(LootDataType.LOOT_TABLES, this.id);
		if (reporter.getDataLookup().getElementOptional(lootDataKey).isEmpty()) {
			reporter.report("Missing loot table used for container: " + this.id);
		}
	}

	public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, Identifier id) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, id, 0L, type.getRegistryEntry()));
	}

	public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, Identifier id, long seed) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, id, seed, type.getRegistryEntry()));
	}
}
