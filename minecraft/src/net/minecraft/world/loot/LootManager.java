package net.minecraft.world.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootManager implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson gson = new GsonBuilder()
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
	private final Map<Identifier, LootSupplier> suppliers = Maps.<Identifier, LootSupplier>newHashMap();
	private final Set<Identifier> supplierNames = Collections.unmodifiableSet(this.suppliers.keySet());
	public static final int lootTablesLength = "loot_tables/".length();
	public static final int jsonLength = ".json".length();

	public LootSupplier getSupplier(Identifier identifier) {
		return (LootSupplier)this.suppliers.getOrDefault(identifier, LootSupplier.EMPTY);
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.suppliers.clear();

		for (Identifier identifier : resourceManager.findResources("loot_tables", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(lootTablesLength, string.length() - jsonLength));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var7 = null;

				try {
					LootSupplier lootSupplier = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), LootSupplier.class);
					if (lootSupplier != null) {
						this.suppliers.put(identifier2, lootSupplier);
					}
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (resource != null) {
						if (var7 != null) {
							try {
								resource.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (Throwable var19) {
				LOGGER.error("Couldn't read loot table {} from {}", identifier2, identifier, var19);
			}
		}

		this.suppliers.put(LootTables.EMPTY, LootSupplier.EMPTY);
		LootTableReporter lootTableReporter = new LootTableReporter();
		this.suppliers.forEach((identifierx, lootSupplierx) -> check(lootTableReporter, identifierx, lootSupplierx, this.suppliers::get));
		lootTableReporter.getMessages().forEach((stringx, string2) -> LOGGER.warn("Found validation problem in " + stringx + ": " + string2));
	}

	public static void check(LootTableReporter lootTableReporter, Identifier identifier, LootSupplier lootSupplier, Function<Identifier, LootSupplier> function) {
		Set<Identifier> set = ImmutableSet.of(identifier);
		lootSupplier.check(lootTableReporter.makeChild("{" + identifier.toString() + "}"), function, set, lootSupplier.getType());
	}

	public static JsonElement toJson(LootSupplier lootSupplier) {
		return gson.toJsonTree(lootSupplier);
	}

	public Set<Identifier> getSupplierNames() {
		return this.supplierNames;
	}
}
