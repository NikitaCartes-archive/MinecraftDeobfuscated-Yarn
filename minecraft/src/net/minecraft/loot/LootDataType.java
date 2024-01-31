package net.minecraft.loot;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootDataType<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final LootDataType<LootCondition> PREDICATES = new LootDataType<>(LootConditionTypes.CODEC, "predicates", validator());
	public static final LootDataType<LootFunction> ITEM_MODIFIERS = new LootDataType<>(LootFunctionTypes.CODEC, "item_modifiers", validator());
	public static final LootDataType<LootTable> LOOT_TABLES = new LootDataType<>(LootTable.CODEC, "loot_tables", tableValidator());
	private final Codec<T> codec;
	private final String id;
	private final LootDataType.Validator<T> validator;

	private LootDataType(Codec<T> codec, String id, LootDataType.Validator<T> validator) {
		this.codec = codec;
		this.id = id;
		this.validator = validator;
	}

	public String getId() {
		return this.id;
	}

	public void validate(LootTableReporter reporter, LootDataKey<T> key, T value) {
		this.validator.run(reporter, key, value);
	}

	public <V> Optional<T> parse(Identifier id, DynamicOps<V> ops, V json) {
		DataResult<T> dataResult = this.codec.parse(ops, json);
		dataResult.error().ifPresent(result -> LOGGER.error("Couldn't parse element {}:{} - {}", this.id, id, result.message()));
		return dataResult.result();
	}

	public static Stream<LootDataType<?>> stream() {
		return Stream.of(PREDICATES, ITEM_MODIFIERS, LOOT_TABLES);
	}

	private static <T extends LootContextAware> LootDataType.Validator<T> validator() {
		return (reporter, key, value) -> value.validate(reporter.makeChild("{" + key.type().id + ":" + key.id() + "}", key));
	}

	private static LootDataType.Validator<LootTable> tableValidator() {
		return (reporter, key, value) -> value.validate(reporter.withContextType(value.getType()).makeChild("{" + key.type().id + ":" + key.id() + "}", key));
	}

	@FunctionalInterface
	public interface Validator<T> {
		void run(LootTableReporter reporter, LootDataKey<T> key, T value);
	}
}
