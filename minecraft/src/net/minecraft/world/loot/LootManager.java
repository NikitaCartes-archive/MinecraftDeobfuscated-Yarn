package net.minecraft.world.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
		.registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer())
		.registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer())
		.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer())
		.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
		.registerTypeAdapter(LootSupplier.class, new LootSupplier.Serializer())
		.registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer())
		.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory())
		.registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory())
		.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
		.create();
	private Map<Identifier, LootSupplier> suppliers = ImmutableMap.of();

	public LootManager() {
		super(GSON, "loot_tables");
	}

	public LootSupplier getSupplier(Identifier identifier) {
		return (LootSupplier)this.suppliers.getOrDefault(identifier, LootSupplier.EMPTY);
	}

	protected void method_20712(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, LootSupplier> builder = ImmutableMap.builder();
		map.forEach((identifier, jsonObject) -> {
			try {
				LootSupplier lootSupplier = GSON.fromJson(jsonObject, LootSupplier.class);
				builder.put(identifier, lootSupplier);
			} catch (Exception var4x) {
				LOGGER.error("Couldn't parse loot table {}", identifier, var4x);
			}
		});
		builder.put(LootTables.EMPTY, LootSupplier.EMPTY);
		ImmutableMap<Identifier, LootSupplier> immutableMap = builder.build();
		LootTableReporter lootTableReporter = new LootTableReporter();
		immutableMap.forEach((identifier, lootSupplier) -> check(lootTableReporter, identifier, lootSupplier, immutableMap::get));
		lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
		this.suppliers = immutableMap;
	}

	public static void check(LootTableReporter lootTableReporter, Identifier identifier, LootSupplier lootSupplier, Function<Identifier, LootSupplier> function) {
		Set<Identifier> set = ImmutableSet.of(identifier);
		lootSupplier.check(lootTableReporter.makeChild("{" + identifier.toString() + "}"), function, set, lootSupplier.getType());
	}

	public static JsonElement toJson(LootSupplier lootSupplier) {
		return GSON.toJsonTree(lootSupplier);
	}

	public Set<Identifier> getSupplierNames() {
		return this.suppliers.keySet();
	}
}
