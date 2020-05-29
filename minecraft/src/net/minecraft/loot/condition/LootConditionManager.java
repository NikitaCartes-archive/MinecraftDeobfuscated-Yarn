package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootConditionManager extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
	private Map<Identifier, LootCondition> conditions = ImmutableMap.of();

	public LootConditionManager() {
		super(GSON, "predicates");
	}

	@Nullable
	public LootCondition get(Identifier id) {
		return (LootCondition)this.conditions.get(id);
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, LootCondition> builder = ImmutableMap.builder();
		map.forEach((identifier, jsonElement) -> {
			try {
				if (jsonElement.isJsonArray()) {
					LootCondition[] lootConditions = GSON.fromJson(jsonElement, LootCondition[].class);
					builder.put(identifier, new LootConditionManager.class_5334(lootConditions));
				} else {
					LootCondition lootCondition = GSON.fromJson(jsonElement, LootCondition.class);
					builder.put(identifier, lootCondition);
				}
			} catch (Exception var4x) {
				LOGGER.error("Couldn't parse loot table {}", identifier, var4x);
			}
		});
		Map<Identifier, LootCondition> map2 = builder.build();
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, map2::get, identifier -> null);
		map2.forEach((identifier, lootCondition) -> lootCondition.validate(lootTableReporter.withCondition("{" + identifier + "}", identifier)));
		lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
		this.conditions = map2;
	}

	public Set<Identifier> getIds() {
		return Collections.unmodifiableSet(this.conditions.keySet());
	}

	static class class_5334 implements LootCondition {
		private final LootCondition[] field_25202;
		private final Predicate<LootContext> field_25203;

		private class_5334(LootCondition[] lootConditions) {
			this.field_25202 = lootConditions;
			this.field_25203 = LootConditionTypes.joinAnd(lootConditions);
		}

		public final boolean test(LootContext lootContext) {
			return this.field_25203.test(lootContext);
		}

		@Override
		public void validate(LootTableReporter reporter) {
			LootCondition.super.validate(reporter);

			for (int i = 0; i < this.field_25202.length; i++) {
				this.field_25202[i].validate(reporter.makeChild(".term[" + i + "]"));
			}
		}

		@Override
		public LootConditionType method_29325() {
			throw new UnsupportedOperationException();
		}
	}
}
