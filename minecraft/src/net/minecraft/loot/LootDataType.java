package net.minecraft.loot;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public record LootDataType<T>(RegistryKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator) {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final LootDataType<LootCondition> PREDICATES = new LootDataType<>(RegistryKeys.PREDICATE, LootCondition.CODEC, simpleValidator());
	public static final LootDataType<LootFunction> ITEM_MODIFIERS = new LootDataType<>(RegistryKeys.ITEM_MODIFIER, LootFunctionTypes.CODEC, simpleValidator());
	public static final LootDataType<LootTable> LOOT_TABLES = new LootDataType<>(RegistryKeys.LOOT_TABLE, LootTable.CODEC, tableValidator());

	public void validate(LootTableReporter reporter, RegistryKey<T> key, T value) {
		this.validator.run(reporter, key, value);
	}

	public <V> Optional<T> parse(Identifier id, DynamicOps<V> ops, V json) {
		DataResult<T> dataResult = this.codec.parse(ops, json);
		dataResult.error().ifPresent(error -> LOGGER.error("Couldn't parse element {}/{} - {}", this.registryKey.getValue(), id, error.message()));
		return dataResult.result();
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
