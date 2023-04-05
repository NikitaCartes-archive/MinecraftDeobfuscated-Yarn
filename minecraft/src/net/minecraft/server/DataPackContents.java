package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.loot.LootManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
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
	private final CommandRegistryAccess.EntryListCreationPolicySettable commandRegistryAccess;
	private final CommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final TagManagerLoader registryTagManager;
	private final LootManager lootManager = new LootManager();
	private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootManager);
	private final FunctionLoader functionLoader;

	public DataPackContents(
		DynamicRegistryManager.Immutable dynamicRegistryManager,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		int functionPermissionLevel
	) {
		this.registryTagManager = new TagManagerLoader(dynamicRegistryManager);
		this.commandRegistryAccess = CommandRegistryAccess.of((DynamicRegistryManager)dynamicRegistryManager, enabledFeatures);
		this.commandManager = new CommandManager(environment, this.commandRegistryAccess);
		this.commandRegistryAccess.setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy.CREATE_NEW);
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

	/**
	 * @see MinecraftServer#getLootManager
	 */
	public LootManager getLootManager() {
		return this.lootManager;
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
		return List.of(this.registryTagManager, this.lootManager, this.recipeManager, this.functionLoader, this.serverAdvancementLoader);
	}

	/**
	 * Reloads the data packs contents.
	 * 
	 * @see MinecraftServer#reloadResources
	 */
	public static CompletableFuture<DataPackContents> reload(
		ResourceManager manager,
		DynamicRegistryManager.Immutable dynamicRegistryManager,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		int functionPermissionLevel,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		DataPackContents dataPackContents = new DataPackContents(dynamicRegistryManager, enabledFeatures, environment, functionPermissionLevel);
		return SimpleResourceReload.start(manager, dataPackContents.getContents(), prepareExecutor, applyExecutor, COMPLETED_UNIT, LOGGER.isDebugEnabled())
			.whenComplete()
			.whenComplete((void_, throwable) -> dataPackContents.commandRegistryAccess.setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy.FAIL))
			.thenApply(void_ -> dataPackContents);
	}

	public void refresh(DynamicRegistryManager dynamicRegistryManager) {
		this.registryTagManager.getRegistryTags().forEach(tags -> repopulateTags(dynamicRegistryManager, tags));
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
}
