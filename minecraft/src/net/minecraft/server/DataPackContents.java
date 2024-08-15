package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
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
	private final CommandManager commandManager;
	private final RecipeManager recipeManager;
	private final ServerAdvancementLoader serverAdvancementLoader;
	private final FunctionLoader functionLoader;
	private final List<Registry.PendingTagLoad<?>> pendingTagLoads;

	private DataPackContents(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
		RegistryWrapper.WrapperLookup registryLookup,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		List<Registry.PendingTagLoad<?>> pendingTagLoads,
		int functionPermissionLevel
	) {
		this.reloadableRegistries = new ReloadableRegistries.Lookup(dynamicRegistries.getCombinedRegistryManager());
		this.pendingTagLoads = pendingTagLoads;
		this.recipeManager = new RecipeManager(registryLookup);
		this.commandManager = new CommandManager(environment, CommandRegistryAccess.of(registryLookup, enabledFeatures));
		this.serverAdvancementLoader = new ServerAdvancementLoader(registryLookup);
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
		return List.of(this.recipeManager, this.functionLoader, this.serverAdvancementLoader);
	}

	/**
	 * Reloads the data packs contents.
	 * 
	 * @see MinecraftServer#reloadResources
	 */
	public static CompletableFuture<DataPackContents> reload(
		ResourceManager resourceManager,
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
		List<Registry.PendingTagLoad<?>> pendingTagLoads,
		FeatureSet enabledFeatures,
		CommandManager.RegistrationEnvironment environment,
		int functionPermissionLevel,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return ReloadableRegistries.reload(dynamicRegistries, pendingTagLoads, resourceManager, prepareExecutor)
			.thenCompose(
				reloadResult -> {
					DataPackContents dataPackContents = new DataPackContents(
						reloadResult.layers(), reloadResult.lookupWithUpdatedTags(), enabledFeatures, environment, pendingTagLoads, functionPermissionLevel
					);
					return SimpleResourceReload.start(resourceManager, dataPackContents.getContents(), prepareExecutor, applyExecutor, COMPLETED_UNIT, LOGGER.isDebugEnabled())
						.whenComplete()
						.thenApply(void_ -> dataPackContents);
				}
			);
	}

	public void applyPendingTagLoads() {
		this.pendingTagLoads.forEach(Registry.PendingTagLoad::apply);
	}
}
