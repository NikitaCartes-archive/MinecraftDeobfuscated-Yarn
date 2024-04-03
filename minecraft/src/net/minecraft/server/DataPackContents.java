package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

/**
 * Contains loaders for contents controllable by data packs.
 * 
 * <p>This can be accessed via {@link
 * net.minecraft.server.MinecraftServer.ResourceManagerHolder#dataPackContents}.
 * There are shortcut methods to access individual loaders on {@link MinecraftServer}.
 */
public class DataPackContents {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final CompletableFuture<Unit> COMPLETED_UNIT = CompletableFuture.completedFuture(Unit.INSTANCE);
	private final ReloadableRegistries.Lookup reloadableRegistries;
	private final DataPackContents.ConfigurableWrapperLookup registryLookup;
	private final CommandManager commandManager;
	private final RecipeManager recipeManager;
	private final TagManagerLoader registryTagManager;
	private final ServerAdvancementLoader serverAdvancementLoader;
	private final FunctionLoader functionLoader;

	private DataPackContents(
		DynamicRegistryManager.Immutable dynamicRegistryManager,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		int functionPermissionLevel
	) {
		this.reloadableRegistries = new ReloadableRegistries.Lookup(dynamicRegistryManager);
		this.registryLookup = new DataPackContents.ConfigurableWrapperLookup(dynamicRegistryManager);
		this.registryLookup.setEntryListCreationPolicy(DataPackContents.EntryListCreationPolicy.CREATE_NEW);
		this.recipeManager = new RecipeManager(this.registryLookup);
		this.registryTagManager = new TagManagerLoader(dynamicRegistryManager);
		this.commandManager = new CommandManager(environment, CommandRegistryAccess.of(this.registryLookup, enabledFeatures));
		this.serverAdvancementLoader = new ServerAdvancementLoader(this.registryLookup);
		this.functionLoader = new FunctionLoader(functionPermissionLevel, this.commandManager.getDispatcher());
	}

	/**
	 * {@return the function loader}
	 * 
	 * <p>Function loader loads the {@linkplain net.minecraft.server.function.CommandFunction
	 * functions} in data packs.
	 */
	public FunctionLoader getFunctionLoader() {
		return this.functionLoader;
	}

	public ReloadableRegistries.Lookup getReloadableRegistries() {
		return this.reloadableRegistries;
	}

	/**
	 * @see MinecraftServer#getRecipeManager
	 */
	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	/**
	 * @see MinecraftServer#getCommandManager
	 */
	public CommandManager getCommandManager() {
		return this.commandManager;
	}

	/**
	 * @see MinecraftServer#getAdvancementLoader
	 */
	public ServerAdvancementLoader getServerAdvancementLoader() {
		return this.serverAdvancementLoader;
	}

	public List<ResourceReloader> getContents() {
		return List.of(this.registryTagManager, this.recipeManager, this.functionLoader, this.serverAdvancementLoader);
	}

	/**
	 * Reloads the data packs contents.
	 * 
	 * @see MinecraftServer#reloadResources
	 */
	public static CompletableFuture<DataPackContents> reload(
		ResourceManager manager,
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		int functionPermissionLevel,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return ReloadableRegistries.reload(dynamicRegistries, manager, prepareExecutor)
			.thenCompose(
				reloadedDynamicRegistries -> {
					DataPackContents dataPackContents = new DataPackContents(
						reloadedDynamicRegistries.getCombinedRegistryManager(), enabledFeatures, environment, functionPermissionLevel
					);
					return SimpleResourceReload.start(manager, dataPackContents.getContents(), prepareExecutor, applyExecutor, COMPLETED_UNIT, LOGGER.isDebugEnabled())
						.whenComplete()
						.whenComplete((void_, throwable) -> dataPackContents.registryLookup.setEntryListCreationPolicy(DataPackContents.EntryListCreationPolicy.FAIL))
						.thenApply(void_ -> dataPackContents);
				}
			);
	}

	public void refresh() {
		this.registryTagManager.getRegistryTags().forEach(tags -> repopulateTags(this.reloadableRegistries.getRegistryManager(), tags));
		AbstractFurnaceBlockEntity.clearFuelTimes();
		Blocks.refreshShapeCache();
	}

	private static <T> void repopulateTags(DynamicRegistryManager dynamicRegistryManager, TagManagerLoader.RegistryTags<T> tags) {
		RegistryKey<? extends Registry<T>> registryKey = tags.key();
		Map<TagKey<T>, List<RegistryEntry<T>>> map = (Map<TagKey<T>, List<RegistryEntry<T>>>)tags.tags()
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(entry -> TagKey.of(registryKey, (Identifier)entry.getKey()), entry -> List.copyOf((Collection)entry.getValue())));
		dynamicRegistryManager.get(registryKey).populateTags(map);
	}

	static class ConfigurableWrapperLookup implements RegistryWrapper.WrapperLookup {
		private final DynamicRegistryManager dynamicRegistryManager;
		DataPackContents.EntryListCreationPolicy entryListCreationPolicy = DataPackContents.EntryListCreationPolicy.FAIL;

		ConfigurableWrapperLookup(DynamicRegistryManager dynamicRegistryManager) {
			this.dynamicRegistryManager = dynamicRegistryManager;
		}

		public void setEntryListCreationPolicy(DataPackContents.EntryListCreationPolicy entryListCreationPolicy) {
			this.entryListCreationPolicy = entryListCreationPolicy;
		}

		@Override
		public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
			return this.dynamicRegistryManager.streamAllRegistryKeys();
		}

		@Override
		public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return this.dynamicRegistryManager
				.getOptional(registryRef)
				.map(registry -> this.getWrapper(registry.getReadOnlyWrapper(), registry.getTagCreatingWrapper()));
		}

		private <T> RegistryWrapper.Impl<T> getWrapper(RegistryWrapper.Impl<T> readOnlyWrapper, RegistryWrapper.Impl<T> tagCreatingWrapper) {
			return new RegistryWrapper.Impl.Delegating<T>() {
				@Override
				public RegistryWrapper.Impl<T> getBase() {
					return switch (ConfigurableWrapperLookup.this.entryListCreationPolicy) {
						case CREATE_NEW -> tagCreatingWrapper;
						case FAIL -> readOnlyWrapper;
					};
				}
			};
		}
	}

	/**
	 * A policy on how to handle a {@link net.minecraft.registry.tag.TagKey} that does not resolve
	 * to an existing tag (unrecognized tag) in {@link
	 * net.minecraft.registry.RegistryWrapper#getOptional(net.minecraft.registry.tag.TagKey)}.
	 */
	static enum EntryListCreationPolicy {
		/**
		 * Creates a new {@link net.minecraft.registry.entry.RegistryEntryList}, stores it and returns it.
		 */
		CREATE_NEW,
		/**
		 * Throws an exception.
		 */
		FAIL;
	}
}
