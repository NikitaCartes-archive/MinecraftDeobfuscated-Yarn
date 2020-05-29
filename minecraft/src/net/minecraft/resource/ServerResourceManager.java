package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.Unit;

public class ServerResourceManager {
	private static final CompletableFuture<Unit> field_25334 = CompletableFuture.completedFuture(Unit.INSTANCE);
	private final ReloadableResourceManager resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
	private final CommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final RegistryTagManager registryTagManager = new RegistryTagManager();
	private final LootConditionManager lootConditionManager = new LootConditionManager();
	private final LootManager lootManager = new LootManager(this.lootConditionManager);
	private final ServerAdvancementLoader serverAdvancementLoader = new ServerAdvancementLoader(this.lootConditionManager);
	private final FunctionLoader functionLoader;

	public ServerResourceManager(boolean dedicated, int i) {
		this.commandManager = new CommandManager(dedicated);
		this.functionLoader = new FunctionLoader(i, this.commandManager.getDispatcher());
		this.resourceManager.registerListener(this.registryTagManager);
		this.resourceManager.registerListener(this.lootConditionManager);
		this.resourceManager.registerListener(this.recipeManager);
		this.resourceManager.registerListener(this.lootManager);
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

	public RegistryTagManager getRegistryTagManager() {
		return this.registryTagManager;
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

	public static CompletableFuture<ServerResourceManager> reload(List<ResourcePack> list, boolean bl, int i, Executor executor, Executor executor2) {
		ServerResourceManager serverResourceManager = new ServerResourceManager(bl, i);
		CompletableFuture<Unit> completableFuture = serverResourceManager.resourceManager.beginReload(executor, executor2, list, field_25334);
		return completableFuture.thenApply(unit -> serverResourceManager);
	}

	public void method_29475() {
		this.registryTagManager.method_29226();
	}
}
