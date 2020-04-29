package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementLoader extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	private AdvancementManager manager = new AdvancementManager();
	private final LootConditionManager conditionManager;

	public ServerAdvancementLoader(LootConditionManager conditionManager) {
		super(GSON, "advancements");
		this.conditionManager = conditionManager;
	}

	protected void apply(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, Advancement.Task> map2 = Maps.<Identifier, Advancement.Task>newHashMap();
		map.forEach((identifier, jsonObject) -> {
			try {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonObject, "advancement");
				Advancement.Task task = Advancement.Task.fromJson(jsonObject2, new AdvancementEntityPredicateDeserializer(identifier, this.conditionManager));
				map2.put(identifier, task);
			} catch (IllegalArgumentException | JsonParseException var6) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", identifier, var6.getMessage());
			}
		});
		AdvancementManager advancementManager = new AdvancementManager();
		advancementManager.load(map2);

		for (Advancement advancement : advancementManager.getRoots()) {
			if (advancement.getDisplay() != null) {
				AdvancementPositioner.arrangeForTree(advancement);
			}
		}

		this.manager = advancementManager;
	}

	@Nullable
	public Advancement get(Identifier id) {
		return this.manager.get(id);
	}

	public Collection<Advancement> getAdvancements() {
		return this.manager.getAdvancements();
	}
}
