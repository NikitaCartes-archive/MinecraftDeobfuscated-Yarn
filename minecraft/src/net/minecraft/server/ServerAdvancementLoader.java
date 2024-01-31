package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.loot.LootManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class ServerAdvancementLoader extends JsonDataLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	private Map<Identifier, AdvancementEntry> advancements = Map.of();
	private AdvancementManager manager = new AdvancementManager();
	private final RegistryWrapper.WrapperLookup registryLookup;
	private final LootManager conditionManager;

	public ServerAdvancementLoader(RegistryWrapper.WrapperLookup registryLookup, LootManager conditionManager) {
		super(GSON, "advancements");
		this.registryLookup = registryLookup;
		this.conditionManager = conditionManager;
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
		RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, this.registryLookup);
		Builder<Identifier, AdvancementEntry> builder = ImmutableMap.builder();
		map.forEach((id, json) -> {
			try {
				Advancement advancement = Util.getResult(Advancement.CODEC.parse(registryOps, json), JsonParseException::new);
				this.validate(id, advancement);
				builder.put(id, new AdvancementEntry(id, advancement));
			} catch (Exception var6x) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", id, var6x.getMessage());
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

	private void validate(Identifier id, Advancement advancement) {
		ErrorReporter.Impl impl = new ErrorReporter.Impl();
		advancement.validate(impl, this.conditionManager);
		Multimap<String, String> multimap = impl.getErrors();
		if (!multimap.isEmpty()) {
			String string = (String)multimap.asMap()
				.entrySet()
				.stream()
				.map(entry -> "  at " + (String)entry.getKey() + ": " + String.join("; ", (Iterable)entry.getValue()))
				.collect(Collectors.joining("\n"));
			LOGGER.warn("Found validation problems in advancement {}: \n{}", id, string);
		}
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
