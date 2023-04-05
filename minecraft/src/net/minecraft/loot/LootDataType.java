package net.minecraft.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootDataType<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final LootDataType<LootCondition> PREDICATES = new LootDataType<>(
		LootGsons.getConditionGsonBuilder().create(), parserFactory(LootCondition.class, LootManager::and), "predicates", validator()
	);
	public static final LootDataType<LootFunction> ITEM_MODIFIERS = new LootDataType<>(
		LootGsons.getFunctionGsonBuilder().create(), parserFactory(LootFunction.class, LootManager::and), "item_modifiers", validator()
	);
	public static final LootDataType<LootTable> LOOT_TABLES = new LootDataType<>(
		LootGsons.getTableGsonBuilder().create(), parserFactory(LootTable.class), "loot_tables", tableValidator()
	);
	private final Gson gson;
	private final BiFunction<Identifier, JsonElement, Optional<T>> parser;
	private final String id;
	private final LootDataType.Validator<T> validator;

	private LootDataType(
		Gson gson, BiFunction<Gson, String, BiFunction<Identifier, JsonElement, Optional<T>>> parserFactory, String id, LootDataType.Validator<T> validator
	) {
		this.gson = gson;
		this.id = id;
		this.validator = validator;
		this.parser = (BiFunction<Identifier, JsonElement, Optional<T>>)parserFactory.apply(gson, id);
	}

	public Gson getGson() {
		return this.gson;
	}

	public String getId() {
		return this.id;
	}

	public void validate(LootTableReporter reporter, LootDataKey<T> key, T value) {
		this.validator.run(reporter, key, value);
	}

	public Optional<T> parse(Identifier id, JsonElement json) {
		return (Optional<T>)this.parser.apply(id, json);
	}

	public static Stream<LootDataType<?>> stream() {
		return Stream.of(PREDICATES, ITEM_MODIFIERS, LOOT_TABLES);
	}

	private static <T> BiFunction<Gson, String, BiFunction<Identifier, JsonElement, Optional<T>>> parserFactory(Class<T> clazz) {
		return (gson, dataTypeId) -> (id, json) -> {
				try {
					return Optional.of(gson.fromJson(json, clazz));
				} catch (Exception var6) {
					LOGGER.error("Couldn't parse element {}:{}", dataTypeId, id, var6);
					return Optional.empty();
				}
			};
	}

	private static <T> BiFunction<Gson, String, BiFunction<Identifier, JsonElement, Optional<T>>> parserFactory(Class<T> clazz, Function<T[], T> combiner) {
		Class<T[]> class_ = clazz.arrayType();
		return (gson2, string) -> (id, json) -> {
				try {
					if (json.isJsonArray()) {
						T[] objects = (T[])((Object[])gson2.fromJson(json, class_));
						return Optional.of(combiner.apply(objects));
					} else {
						return Optional.of(gson2.fromJson(json, clazz));
					}
				} catch (Exception var8) {
					LOGGER.error("Couldn't parse element {}:{}", string, id, var8);
					return Optional.empty();
				}
			};
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
