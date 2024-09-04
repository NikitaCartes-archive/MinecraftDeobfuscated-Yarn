package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class ServerAdvancementLoader extends JsonDataLoader<Advancement> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<Identifier, AdvancementEntry> advancements = Map.of();
	private AdvancementManager manager = new AdvancementManager();
	private final RegistryWrapper.WrapperLookup registries;

	public ServerAdvancementLoader(RegistryWrapper.WrapperLookup registries) {
		super(registries, Advancement.CODEC, RegistryKeys.getPath(RegistryKeys.ADVANCEMENT));
		this.registries = registries;
	}

	protected void apply(Map<Identifier, Advancement> map, ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, AdvancementEntry> builder = ImmutableMap.builder();
		map.forEach((id, advancement) -> {
			this.validate(id, advancement);
			builder.put(id, new AdvancementEntry(id, advancement));
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
		advancement.validate(impl, this.registries);
		impl.getErrorsAsString().ifPresent(error -> LOGGER.warn("Found validation problems in advancement {}: \n{}", id, error));
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
