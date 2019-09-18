package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.loot.BinomialLootTableRange;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4567 extends JsonDataLoader {
	private static final Logger field_20753 = LogManager.getLogger();
	private static final Gson field_20754 = new GsonBuilder()
		.registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
		.registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer())
		.registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer())
		.registerTypeHierarchyAdapter(class_4570.class, new LootConditions.Factory())
		.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
		.create();
	private Map<Identifier, class_4570> field_20755 = ImmutableMap.of();

	public class_4567() {
		super(field_20754, "predicates");
	}

	@Nullable
	public class_4570 method_22564(Identifier identifier) {
		return (class_4570)this.field_20755.get(identifier);
	}

	public class_4570 method_22565(Identifier identifier, class_4570 arg) {
		return (class_4570)this.field_20755.getOrDefault(identifier, arg);
	}

	protected void method_22563(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, class_4570> builder = ImmutableMap.builder();
		map.forEach((identifier, jsonObject) -> {
			try {
				class_4570 lv = field_20754.fromJson(jsonObject, class_4570.class);
				builder.put(identifier, lv);
			} catch (Exception var4x) {
				field_20753.error("Couldn't parse loot table {}", identifier, var4x);
			}
		});
		Map<Identifier, class_4570> map2 = builder.build();
		LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, map2::get, identifier -> null);
		map2.forEach((identifier, arg) -> arg.check(lootTableReporter.method_22571("{" + identifier + "}", identifier)));
		lootTableReporter.getMessages().forEach((string, string2) -> field_20753.warn("Found validation problem in " + string + ": " + string2));
		this.field_20755 = map2;
	}

	public Set<Identifier> method_22559() {
		return Collections.unmodifiableSet(this.field_20755.keySet());
	}
}
