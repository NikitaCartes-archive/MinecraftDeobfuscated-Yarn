package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public class ServerResourceManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final CompletableFuture<Unit> COMPLETED_UNIT = CompletableFuture.completedFuture(Unit.INSTANCE);
	private final CommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final TagManagerLoader registryTagManager;
	private final LootConditionManager lootConditionManager = new LootConditionManager();
	private final LootManager lootManager = new LootManager(this.lootConditionManager);
	private final LootFunctionManager lootFunctionManager = new LootFunctionManager(this.lootConditionManager, this.lootManager);
	private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootConditionManager);
	private final FunctionLoader functionLoader;

	public ServerResourceManager(
		DynamicRegistryManager.Immutable dynamicRegistryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel
	) {
		this.registryTagManager = new TagManagerLoader(dynamicRegistryManager);
		this.commandManager = new CommandManager(commandEnvironment);
		this.functionLoader = new FunctionLoader(functionPermissionLevel, this.commandManager.getDispatcher());
	}

	public FunctionLoader getFunctionLoader() {
		return this.functionLoader;
	}

	public LootConditionManager getLootConditionManager() {
		return this.lootConditionManager;
	}

	public LootManager getLootManager() {
		return this.lootManager;
	}

	public LootFunctionManager getLootFunctionManager() {
		return this.lootFunctionManager;
	}

	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	public CommandManager getCommandManager() {
		return this.commandManager;
	}

	public ServerAdvancementLoader getServerAdvancementLoader() {
		return this.serverAdvancementLoader;
	}

	public List<ResourceReloader> getResourceReloaders() {
		return List.of(
			this.registryTagManager,
			this.lootConditionManager,
			this.recipeManager,
			this.lootManager,
			this.lootFunctionManager,
			this.functionLoader,
			this.serverAdvancementLoader
		);
	}

	public static CompletableFuture<ServerResourceManager> reload(
		ResourceManager manager,
		DynamicRegistryManager.Immutable dynamicRegistryManager,
		CommandManager.RegistrationEnvironment commandEnvironment,
		int functionPermissionLevel,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		ServerResourceManager serverResourceManager = new ServerResourceManager(dynamicRegistryManager, commandEnvironment, functionPermissionLevel);
		return SimpleResourceReload.start(
				manager, serverResourceManager.getResourceReloaders(), prepareExecutor, applyExecutor, COMPLETED_UNIT, LOGGER.isDebugEnabled()
			)
			.whenComplete()
			.thenApply(object -> serverResourceManager);
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
			.collect(Collectors.toUnmodifiableMap(entry -> TagKey.of(registryKey, (Identifier)entry.getKey()), entry -> ((Tag)entry.getValue()).values()));
		dynamicRegistryManager.get(registryKey).populateTags(map);
	}
}
