package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetLootTableLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<RegistryKey<LootTable>, long, RegistryEntry<BlockEntityType<?>>>and(
					instance.group(
						RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("name").forGetter(function -> function.lootTable),
						Codec.LONG.optionalFieldOf("seed", Long.valueOf(0L)).forGetter(function -> function.seed),
						Registries.BLOCK_ENTITY_TYPE.getEntryCodec().fieldOf("type").forGetter(function -> function.type)
					)
				)
				.apply(instance, SetLootTableLootFunction::new)
	);
	private final RegistryKey<LootTable> lootTable;
	private final long seed;
	private final RegistryEntry<BlockEntityType<?>> type;

	private SetLootTableLootFunction(
		List<LootCondition> conditions, RegistryKey<LootTable> lootTable, long seed, RegistryEntry<BlockEntityType<?>> blockEntityType
	) {
		super(conditions);
		this.lootTable = lootTable;
		this.seed = seed;
		this.type = blockEntityType;
	}

	@Override
	public LootFunctionType<SetLootTableLootFunction> getType() {
		return LootFunctionTypes.SET_LOOT_TABLE;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			stack.set(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.seed));
			return stack;
		}
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);
		if (!reporter.canUseReferences()) {
			reporter.report("Uses reference to " + this.lootTable.getValue() + ", but references are not allowed");
		} else {
			if (reporter.getDataLookup().getOptionalEntry(RegistryKeys.LOOT_TABLE, this.lootTable).isEmpty()) {
				reporter.report("Missing loot table used for container: " + this.lootTable.getValue());
			}
		}
	}

	public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, RegistryKey<LootTable> lootTable) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, lootTable, 0L, type.getRegistryEntry()));
	}

	public static ConditionalLootFunction.Builder<?> builder(BlockEntityType<?> type, RegistryKey<LootTable> lootTable, long seed) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, lootTable, seed, type.getRegistryEntry()));
	}
}
