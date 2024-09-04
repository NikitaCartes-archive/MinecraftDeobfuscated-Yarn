package net.minecraft.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record LootDataType<T>(RegistryKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator) {
	public static final LootDataType<LootCondition> PREDICATES = new LootDataType<>(RegistryKeys.PREDICATE, LootCondition.CODEC, simpleValidator());
	public static final LootDataType<LootFunction> ITEM_MODIFIERS = new LootDataType<>(RegistryKeys.ITEM_MODIFIER, LootFunctionTypes.CODEC, simpleValidator());
	public static final LootDataType<LootTable> LOOT_TABLES = new LootDataType<>(RegistryKeys.LOOT_TABLE, LootTable.CODEC, tableValidator());

	public void validate(LootTableReporter reporter, RegistryKey<T> key, T value) {
		this.validator.run(reporter, key, value);
	}

	public static Stream<LootDataType<?>> stream() {
		return Stream.of(PREDICATES, ITEM_MODIFIERS, LOOT_TABLES);
	}

	private static <T extends LootContextAware> LootDataType.Validator<T> simpleValidator() {
		return (reporter, key, value) -> value.validate(reporter.makeChild("{" + key.getRegistry() + "/" + key.getValue() + "}", key));
	}

	private static LootDataType.Validator<LootTable> tableValidator() {
		return (reporter, key, value) -> value.validate(
				reporter.withContextType(value.getType()).makeChild("{" + key.getRegistry() + "/" + key.getValue() + "}", key)
			);
	}

	@FunctionalInterface
	public interface Validator<T> {
		void run(LootTableReporter reporter, RegistryKey<T> key, T value);
	}
}
