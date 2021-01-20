package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Unit;
import net.minecraft.util.registry.DynamicRegistryManager;

public class ServerResourceManager implements AutoCloseable {
	private static final CompletableFuture<Unit> field_25334 = CompletableFuture.completedFuture(Unit.INSTANCE);
	private final ReloadableResourceManager resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
	private final CommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final TagManagerLoader registryTagManager;
	private final LootConditionManager lootConditionManager = new LootConditionManager();
	private final LootManager lootManager = new LootManager(this.lootConditionManager);
	private final LootFunctionManager lootFunctionManager = new LootFunctionManager(this.lootConditionManager, this.lootManager);
	private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootConditionManager);
	private final FunctionLoader functionLoader;

	public ServerResourceManager(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment registrationEnvironment, int i) {
		this.registryTagManager = new TagManagerLoader(registryManager);
		this.commandManager = new CommandManager(registrationEnvironment);
		this.functionLoader = new FunctionLoader(i, this.commandManager.getDispatcher());
		this.resourceManager.registerListener(this.registryTagManager);
		this.resourceManager.registerListener(this.lootConditionManager);
		this.resourceManager.registerListener(this.recipeManager);
		this.resourceManager.registerListener(this.lootManager);
		this.resourceManager.registerListener(this.lootFunctionManager);
		this.resourceManager.registerListener(this.functionLoader);
		this.resourceManager.registerListener(this.serverAdvancementLoader);
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

	public TagManager getRegistryTagManager() {
		return this.registryTagManager.getTagManager();
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

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public static CompletableFuture<ServerResourceManager> reload(
		List<ResourcePack> packs,
		DynamicRegistryManager registryManager,
		CommandManager.RegistrationEnvironment registrationEnvironment,
		int i,
		Executor executor,
		Executor executor2
	) {
		ServerResourceManager serverResourceManager = new ServerResourceManager(registryManager, registrationEnvironment, i);
		CompletableFuture<Unit> completableFuture = serverResourceManager.resourceManager.beginReload(executor, executor2, packs, field_25334);
		return completableFuture.whenComplete((unit, throwable) -> {
			if (throwable != null) {
				serverResourceManager.close();
			}
		}).thenApply(unit -> serverResourceManager);
	}

	public void loadRegistryTags() {
		this.registryTagManager.getTagManager().apply();
	}

	public void close() {
		this.resourceManager.close();
	}
}
