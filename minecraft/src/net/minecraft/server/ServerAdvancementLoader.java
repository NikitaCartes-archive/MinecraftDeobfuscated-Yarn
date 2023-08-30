package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.loot.LootManager;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class ServerAdvancementLoader extends JsonDataLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	private Map<Identifier, AdvancementEntry> advancements = Map.of();
	private AdvancementManager manager = new AdvancementManager();
	private final LootManager conditionManager;

	public ServerAdvancementLoader(LootManager conditionManager) {
		super(GSON, "advancements");
		this.conditionManager = conditionManager;
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, AdvancementEntry> builder = ImmutableMap.builder();
		map.forEach((id, json) -> {
			try {
				JsonObject jsonObject = JsonHelper.asObject(json, "advancement");
				Advancement advancement = Advancement.fromJson(jsonObject, new AdvancementEntityPredicateDeserializer(id, this.conditionManager));
				builder.put(id, new AdvancementEntry(id, advancement));
			} catch (Exception var6) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", id, var6.getMessage());
			}
		});
		this.advancements = builder.buildOrThrow();
		AdvancementManager advancementManager = new AdvancementManager();
		advancementManager.addAll(this.advancements.values());

		for (PlacedAdvancement placedAdvancement : advancementManager.getRoots()) {
			if (placedAdvancement.getAdvancementEntry().value().display().isPresent()) {
				AdvancementPositioner.arrangeForTree(placedAdvancement);
			}
		}

		this.manager = advancementManager;
	}

	@Nullable
	public AdvancementEntry get(Identifier id) {
		return (AdvancementEntry)this.advancements.get(id);
	}

	public AdvancementManager getManager() {
		return this.manager;
	}

	public Collection<AdvancementEntry> getAdvancements() {
		return this.advancements.values();
	}
}
