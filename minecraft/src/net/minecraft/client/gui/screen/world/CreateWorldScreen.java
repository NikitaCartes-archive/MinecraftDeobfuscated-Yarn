package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private static final int field_42165 = 1;
	private static final int field_42166 = 210;
	private static final int field_42167 = 36;
	private static final int field_42168 = 1;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String TEMP_DIR_PREFIX = "mcworld-";
	static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
	static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
	static final Text EXPERIMENTS_TEXT = Text.translatable("selectWorld.experiments");
	static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
	private static final Text PREPARING_TEXT = Text.translatable("createWorld.preparing");
	private static final int field_42170 = 10;
	private static final int field_42171 = 8;
	public static final Identifier HEADER_SEPARATOR_TEXTURE = new Identifier("textures/gui/header_separator.png");
	public static final Identifier FOOTER_SEPARATOR_TEXTURE = new Identifier("textures/gui/footer_separator.png");
	final WorldCreator worldCreator;
	private final TabManager tabManager = new TabManager(this::addDrawableChild, child -> this.remove(child));
	private boolean recreated;
	@Nullable
	private final Screen parent;
	@Nullable
	private Path dataPackTempDir;
	@Nullable
	private ResourcePackManager packManager;
	@Nullable
	private GridWidget grid;
	@Nullable
	private TabNavigationWidget tabNavigation;

	public static void create(MinecraftClient client, @Nullable Screen parent) {
		showMessage(client, PREPARING_TEXT);
		ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
		SaveLoading.ServerConfig serverConfig = createServerConfig(resourcePackManager, DataConfiguration.SAFE_MODE);
		CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(
			serverConfig,
			context -> new SaveLoading.LoadContext<>(
					new CreateWorldScreen.WorldCreationSettings(
						new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager())), context.dataConfiguration()
					),
					context.dimensionsRegistryManager()
				),
			(resourceManager, dataPackContents, combinedDynamicRegistries, generatorOptions) -> {
				resourceManager.close();
				return new GeneratorOptionsHolder(generatorOptions.worldGenSettings(), combinedDynamicRegistries, dataPackContents, generatorOptions.dataConfiguration());
			},
			Util.getMainWorkerExecutor(),
			client
		);
		client.runTasks(completableFuture::isDone);
		client.setScreen(
			new CreateWorldScreen(client, parent, (GeneratorOptionsHolder)completableFuture.join(), Optional.of(WorldPresets.DEFAULT), OptionalLong.empty())
		);
	}

	public static CreateWorldScreen create(
		MinecraftClient client, @Nullable Screen parent, LevelInfo levelInfo, GeneratorOptionsHolder generatorOptionsHolder, @Nullable Path dataPackTempDir
	) {
		CreateWorldScreen createWorldScreen = new CreateWorldScreen(
			client,
			parent,
			generatorOptionsHolder,
			WorldPresets.getWorldPreset(generatorOptionsHolder.selectedDimensions().dimensions()),
			OptionalLong.of(generatorOptionsHolder.generatorOptions().getSeed())
		);
		createWorldScreen.recreated = true;
		createWorldScreen.worldCreator.setWorldName(levelInfo.getLevelName());
		createWorldScreen.worldCreator.setCheatsEnabled(levelInfo.areCommandsAllowed());
		createWorldScreen.worldCreator.setDifficulty(levelInfo.getDifficulty());
		createWorldScreen.worldCreator.getGameRules().setAllValues(levelInfo.getGameRules(), null);
		if (levelInfo.isHardcore()) {
			createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
		} else if (levelInfo.getGameMode().isSurvivalLike()) {
			createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.SURVIVAL);
		} else if (levelInfo.getGameMode().isCreative()) {
			createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
		}

		createWorldScreen.dataPackTempDir = dataPackTempDir;
		return createWorldScreen;
	}

	private CreateWorldScreen(
		MinecraftClient client,
		@Nullable Screen parent,
		GeneratorOptionsHolder generatorOptionsHolder,
		Optional<RegistryKey<WorldPreset>> defaultWorldType,
		OptionalLong seed
	) {
		super(Text.translatable("selectWorld.create"));
		this.parent = parent;
		this.worldCreator = new WorldCreator(client.getLevelStorage().getSavesDirectory(), generatorOptionsHolder, defaultWorldType, seed);
	}

	public WorldCreator getWorldCreator() {
		return this.worldCreator;
	}

	@Override
	public void tick() {
		this.tabManager.tick();
	}

	@Override
	protected void init() {
		this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width)
			.tabs(new CreateWorldScreen.GameTab(), new CreateWorldScreen.WorldTab(), new CreateWorldScreen.MoreTab())
			.build();
		this.addDrawableChild(this.tabNavigation);
		this.grid = new GridWidget().setColumnSpacing(10);
		GridWidget.Adder adder = this.grid.createAdder(2);
		adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).build());
		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCloseScreen()).build());
		this.grid.forEachChild(child -> {
			child.setNavigationOrder(1);
			this.addDrawableChild(child);
		});
		this.tabNavigation.selectTab(0);
		this.worldCreator.update();
		this.initTabNavigation();
	}

	@Override
	public void initTabNavigation() {
		if (this.tabNavigation != null && this.grid != null) {
			this.tabNavigation.setWidth(this.width);
			this.tabNavigation.init();
			this.grid.refreshPositions();
			SimplePositioningWidget.setPos(this.grid, 0, this.height - 36, this.width, 36);
			int i = this.tabNavigation.getNavigationFocus().getBottom();
			FocusedRect focusedRect = new FocusedRect(0, i, this.width, this.grid.getY() - i);
			this.tabManager.setTabArea(focusedRect);
		}
	}

	private static void showMessage(MinecraftClient client, Text text) {
		client.setScreenAndRender(new MessageScreen(text));
	}

	private void createLevel() {
		GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
		DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = generatorOptionsHolder.selectedDimensions()
			.toConfig(generatorOptionsHolder.dimensionOptionsRegistry());
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = generatorOptionsHolder.combinedDynamicRegistries()
			.with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());
		Lifecycle lifecycle = FeatureFlags.isNotVanilla(generatorOptionsHolder.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
		Lifecycle lifecycle2 = combinedDynamicRegistries.getCombinedRegistryManager().getRegistryLifecycle();
		Lifecycle lifecycle3 = lifecycle2.add(lifecycle);
		boolean bl = !this.recreated && lifecycle2 == Lifecycle.stable();
		IntegratedServerLoader.tryLoad(
			this.client, this, lifecycle3, () -> this.startServer(dimensionsConfig.specialWorldProperty(), combinedDynamicRegistries, lifecycle3), bl
		);
	}

	private void startServer(
		LevelProperties.SpecialProperty specialProperty, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, Lifecycle lifecycle
	) {
		showMessage(this.client, PREPARING_TEXT);
		Optional<LevelStorage.Session> optional = this.createSession();
		if (!optional.isEmpty()) {
			this.clearDataPackTempDir();
			boolean bl = specialProperty == LevelProperties.SpecialProperty.DEBUG;
			GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
			LevelInfo levelInfo = this.createLevelInfo(bl);
			SaveProperties saveProperties = new LevelProperties(levelInfo, generatorOptionsHolder.generatorOptions(), specialProperty, lifecycle);
			this.client
				.createIntegratedServerLoader()
				.start((LevelStorage.Session)optional.get(), generatorOptionsHolder.dataPackContents(), combinedDynamicRegistries, saveProperties);
		}
	}

	private LevelInfo createLevelInfo(boolean debugWorld) {
		String string = this.worldCreator.getWorldName().trim();
		if (debugWorld) {
			GameRules gameRules = new GameRules();
			gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
			return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataConfiguration.SAFE_MODE);
		} else {
			return new LevelInfo(
				string,
				this.worldCreator.getGameMode().defaultGameMode,
				this.worldCreator.isHardcore(),
				this.worldCreator.getDifficulty(),
				this.worldCreator.areCheatsEnabled(),
				this.worldCreator.getGameRules(),
				this.worldCreator.getGeneratorOptionsHolder().dataConfiguration()
			);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.tabNavigation.trySwitchTabsWithKey(keyCode)) {
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return false;
		} else {
			this.createLevel();
			return true;
		}
	}

	@Override
	public void close() {
		this.onCloseScreen();
	}

	public void onCloseScreen() {
		this.client.setScreen(this.parent);
		this.clearDataPackTempDir();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		RenderSystem.setShaderTexture(0, FOOTER_SEPARATOR_TEXTURE);
		drawTexture(matrices, 0, MathHelper.roundUpToMultiple(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void renderBackgroundTexture(MatrixStack matrices) {
		RenderSystem.setShaderTexture(0, LIGHT_DIRT_BACKGROUND_TEXTURE);
		int i = 32;
		drawTexture(matrices, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
	}

	@Override
	protected <T extends Element & Selectable> T addSelectableChild(T child) {
		return super.addSelectableChild(child);
	}

	@Override
	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		return super.addDrawableChild(drawableElement);
	}

	@Nullable
	private Path getDataPackTempDir() {
		if (this.dataPackTempDir == null) {
			try {
				this.dataPackTempDir = Files.createTempDirectory("mcworld-");
			} catch (IOException var2) {
				LOGGER.warn("Failed to create temporary dir", (Throwable)var2);
				SystemToast.addPackCopyFailure(this.client, this.worldCreator.getWorldDirectoryName());
				this.onCloseScreen();
			}
		}

		return this.dataPackTempDir;
	}

	void openExperimentsScreen(DataConfiguration dataConfiguration) {
		Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
		if (pair != null) {
			this.client
				.setScreen(
					new ExperimentsScreen(this, pair.getSecond(), resourcePackManager -> this.applyDataPacks(resourcePackManager, false, this::openExperimentsScreen))
				);
		}
	}

	void openPackScreen(DataConfiguration dataConfiguration) {
		Pair<Path, ResourcePackManager> pair = this.getScannedPack(dataConfiguration);
		if (pair != null) {
			this.client
				.setScreen(
					new PackScreen(
						pair.getSecond(),
						resourcePackManager -> this.applyDataPacks(resourcePackManager, true, this::openPackScreen),
						pair.getFirst(),
						Text.translatable("dataPack.title")
					)
				);
		}
	}

	private void applyDataPacks(ResourcePackManager dataPackManager, boolean fromPackScreen, Consumer<DataConfiguration> configurationSetter) {
		List<String> list = ImmutableList.copyOf(dataPackManager.getEnabledNames());
		List<String> list2 = (List<String>)dataPackManager.getNames().stream().filter(name -> !list.contains(name)).collect(ImmutableList.toImmutableList());
		DataConfiguration dataConfiguration = new DataConfiguration(
			new DataPackSettings(list, list2), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures()
		);
		if (this.worldCreator.updateDataConfiguration(dataConfiguration)) {
			this.client.setScreen(this);
		} else {
			FeatureSet featureSet = dataPackManager.getRequestedFeatures();
			if (FeatureFlags.isNotVanilla(featureSet) && fromPackScreen) {
				this.client.setScreen(new ExperimentalWarningScreen(dataPackManager.getEnabledProfiles(), confirmed -> {
					if (confirmed) {
						this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
					} else {
						configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
					}
				}));
			} else {
				this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
			}
		}
	}

	private void validateDataPacks(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration, Consumer<DataConfiguration> configurationSetter) {
		this.client.setScreenAndRender(new MessageScreen(Text.translatable("dataPack.validation.working")));
		SaveLoading.ServerConfig serverConfig = createServerConfig(dataPackManager, dataConfiguration);
		SaveLoading.<CreateWorldScreen.WorldCreationSettings, GeneratorOptionsHolder>load(
				serverConfig,
				context -> {
					if (context.worldGenRegistryManager().get(RegistryKeys.WORLD_PRESET).size() == 0) {
						throw new IllegalStateException("Needs at least one world preset to continue");
					} else if (context.worldGenRegistryManager().get(RegistryKeys.BIOME).size() == 0) {
						throw new IllegalStateException("Needs at least one biome continue");
					} else {
						GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
						DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, generatorOptionsHolder.getCombinedRegistryManager());
						DataResult<JsonElement> dataResult = WorldGenSettings.encode(
								dynamicOps, generatorOptionsHolder.generatorOptions(), generatorOptionsHolder.selectedDimensions()
							)
							.setLifecycle(Lifecycle.stable());
						DynamicOps<JsonElement> dynamicOps2 = RegistryOps.of(JsonOps.INSTANCE, context.worldGenRegistryManager());
						WorldGenSettings worldGenSettings = dataResult.<WorldGenSettings>flatMap(json -> WorldGenSettings.CODEC.parse(dynamicOps2, json))
							.getOrThrow(false, Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
						return new SaveLoading.LoadContext<>(
							new CreateWorldScreen.WorldCreationSettings(worldGenSettings, context.dataConfiguration()), context.dimensionsRegistryManager()
						);
					}
				},
				(resourceManager, dataPackContents, combinedDynamicRegistries, context) -> {
					resourceManager.close();
					return new GeneratorOptionsHolder(context.worldGenSettings(), combinedDynamicRegistries, dataPackContents, context.dataConfiguration());
				},
				Util.getMainWorkerExecutor(),
				this.client
			)
			.thenAcceptAsync(this.worldCreator::setGeneratorOptionsHolder, this.client)
			.handle(
				(void_, throwable) -> {
					if (throwable != null) {
						LOGGER.warn("Failed to validate datapack", throwable);
						this.client
							.setScreen(
								new ConfirmScreen(
									confirmed -> {
										if (confirmed) {
											configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
										} else {
											configurationSetter.accept(DataConfiguration.SAFE_MODE);
										}
									},
									Text.translatable("dataPack.validation.failed"),
									ScreenTexts.EMPTY,
									Text.translatable("dataPack.validation.back"),
									Text.translatable("dataPack.validation.reset")
								)
							);
					} else {
						this.client.setScreen(this);
					}

					return null;
				}
			);
	}

	private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
		SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration, false, true);
		return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
	}

	private void clearDataPackTempDir() {
		if (this.dataPackTempDir != null) {
			try {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);

				try {
					stream.sorted(Comparator.reverseOrder()).forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException var2) {
							LOGGER.warn("Failed to remove temporary file {}", path, var2);
						}
					});
				} catch (Throwable var5) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (stream != null) {
					stream.close();
				}
			} catch (IOException var6) {
				LOGGER.warn("Failed to list temporary dir {}", this.dataPackTempDir);
			}

			this.dataPackTempDir = null;
		}
	}

	private static void copyDataPack(Path srcFolder, Path destFolder, Path dataPackFile) {
		try {
			Util.relativeCopy(srcFolder, destFolder, dataPackFile);
		} catch (IOException var4) {
			LOGGER.warn("Failed to copy datapack file from {} to {}", dataPackFile, destFolder);
			throw new UncheckedIOException(var4);
		}
	}

	private Optional<LevelStorage.Session> createSession() {
		String string = this.worldCreator.getWorldDirectoryName();

		try {
			LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
			if (this.dataPackTempDir == null) {
				return Optional.of(session);
			}

			try {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);

				Optional var5;
				try {
					Path path = session.getDirectory(WorldSavePath.DATAPACKS);
					PathUtil.createDirectories(path);
					stream.filter(pathx -> !pathx.equals(this.dataPackTempDir)).forEach(pathx -> copyDataPack(this.dataPackTempDir, path, pathx));
					var5 = Optional.of(session);
				} catch (Throwable var7) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var6) {
							var7.addSuppressed(var6);
						}
					}

					throw var7;
				}

				if (stream != null) {
					stream.close();
				}

				return var5;
			} catch (UncheckedIOException | IOException var8) {
				LOGGER.warn("Failed to copy datapacks to world {}", string, var8);
				session.close();
			}
		} catch (UncheckedIOException | IOException var9) {
			LOGGER.warn("Failed to create access for {}", string, var9);
		}

		SystemToast.addPackCopyFailure(this.client, string);
		this.onCloseScreen();
		return Optional.empty();
	}

	@Nullable
	public static Path copyDataPack(Path srcFolder, MinecraftClient client) {
		MutableObject<Path> mutableObject = new MutableObject<>();

		try {
			Stream<Path> stream = Files.walk(srcFolder);

			try {
				stream.filter(dataPackFile -> !dataPackFile.equals(srcFolder)).forEach(dataPackFile -> {
					Path path2 = mutableObject.getValue();
					if (path2 == null) {
						try {
							path2 = Files.createTempDirectory("mcworld-");
						} catch (IOException var5) {
							LOGGER.warn("Failed to create temporary dir");
							throw new UncheckedIOException(var5);
						}

						mutableObject.setValue(path2);
					}

					copyDataPack(srcFolder, path2, dataPackFile);
				});
			} catch (Throwable var7) {
				if (stream != null) {
					try {
						stream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (stream != null) {
				stream.close();
			}
		} catch (UncheckedIOException | IOException var8) {
			LOGGER.warn("Failed to copy datapacks from world {}", srcFolder, var8);
			SystemToast.addPackCopyFailure(client, srcFolder.toString());
			return null;
		}

		return mutableObject.getValue();
	}

	@Nullable
	private Pair<Path, ResourcePackManager> getScannedPack(DataConfiguration dataConfiguration) {
		Path path = this.getDataPackTempDir();
		if (path != null) {
			if (this.packManager == null) {
				this.packManager = VanillaDataPackProvider.createManager(path);
				this.packManager.scanPacks();
			}

			this.packManager.setEnabledProfiles(dataConfiguration.dataPacks().getEnabled());
			return Pair.of(path, this.packManager);
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	class GameTab extends GridScreenTab {
		private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
		private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
		private final TextFieldWidget worldNameField;

		GameTab() {
			super(GAME_TAB_TITLE_TEXT);
			GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
			Positioner positioner = adder.copyPositioner();
			GridWidget.Adder adder2 = new GridWidget().setRowSpacing(4).createAdder(1);
			adder2.add(new TextWidget(CreateWorldScreen.ENTER_NAME_TEXT, CreateWorldScreen.this.client.textRenderer), adder2.copyPositioner().marginLeft(1));
			this.worldNameField = adder2.add(
				new TextFieldWidget(CreateWorldScreen.this.textRenderer, 0, 0, 208, 20, Text.translatable("selectWorld.enterName")), adder2.copyPositioner().margin(1)
			);
			this.worldNameField.setText(CreateWorldScreen.this.worldCreator.getWorldName());
			this.worldNameField.setChangedListener(CreateWorldScreen.this.worldCreator::setWorldName);
			CreateWorldScreen.this.worldCreator
				.addListener(
					creator -> this.worldNameField
							.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC))))
				);
			CreateWorldScreen.this.setInitialFocus(this.worldNameField);
			adder.add(adder2.getGridWidget(), adder.copyPositioner().alignHorizontalCenter());
			CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(
				CyclingButtonWidget.<WorldCreator.Mode>builder(value -> value.name)
					.values(WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE)
					.build(0, 0, 210, 20, CreateWorldScreen.GAME_MODE_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setGameMode(value)),
				positioner
			);
			CreateWorldScreen.this.worldCreator.addListener(creator -> {
				cyclingButtonWidget.setValue(creator.getGameMode());
				cyclingButtonWidget.active = !creator.isDebug();
				cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
			});
			CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(
				CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
					.values(Difficulty.values())
					.build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> CreateWorldScreen.this.worldCreator.setDifficulty(value)),
				positioner
			);
			CreateWorldScreen.this.worldCreator.addListener(creator -> {
				cyclingButtonWidget2.setValue(CreateWorldScreen.this.worldCreator.getDifficulty());
				cyclingButtonWidget2.active = !CreateWorldScreen.this.worldCreator.isHardcore();
				cyclingButtonWidget2.setTooltip(Tooltip.of(CreateWorldScreen.this.worldCreator.getDifficulty().getInfo()));
			});
			CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(
				CyclingButtonWidget.onOffBuilder()
					.tooltip(value -> Tooltip.of(CreateWorldScreen.ALLOW_COMMANDS_INFO_TEXT))
					.build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setCheatsEnabled(value))
			);
			CreateWorldScreen.this.worldCreator.addListener(creator -> {
				cyclingButtonWidget3.setValue(CreateWorldScreen.this.worldCreator.areCheatsEnabled());
				cyclingButtonWidget3.active = !CreateWorldScreen.this.worldCreator.isDebug() && !CreateWorldScreen.this.worldCreator.isHardcore();
			});
			adder.add(
				ButtonWidget.builder(
						CreateWorldScreen.EXPERIMENTS_TEXT,
						button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())
					)
					.width(210)
					.build()
			);
		}

		@Override
		public void tick() {
			this.worldNameField.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	class MoreTab extends GridScreenTab {
		private static final Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
		private static final Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
		private static final Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");

		MoreTab() {
			super(MORE_TAB_TITLE_TEXT);
			GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
			adder.add(ButtonWidget.builder(GAME_RULES_TEXT, button -> this.openGameRulesScreen()).width(210).build());
			adder.add(
				ButtonWidget.builder(
						CreateWorldScreen.EXPERIMENTS_TEXT,
						button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())
					)
					.width(210)
					.build()
			);
			adder.add(
				ButtonWidget.builder(
						DATA_PACKS_TEXT, button -> CreateWorldScreen.this.openPackScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())
					)
					.width(210)
					.build()
			);
		}

		private void openGameRulesScreen() {
			CreateWorldScreen.this.client.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.worldCreator.getGameRules().copy(), gameRules -> {
				CreateWorldScreen.this.client.setScreen(CreateWorldScreen.this);
				gameRules.ifPresent(CreateWorldScreen.this.worldCreator::setGameRules);
			}));
		}
	}

	@Environment(EnvType.CLIENT)
	static record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
	}

	@Environment(EnvType.CLIENT)
	class WorldTab extends GridScreenTab {
		private static final Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
		private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
		private static final Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
		private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
		private static final Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
		private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
		static final Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo").formatted(Formatting.DARK_GRAY);
		private static final int field_42190 = 310;
		private final TextFieldWidget seedField;
		private final ButtonWidget customizeButton;

		WorldTab() {
			super(WORLD_TAB_TITLE_TEXT);
			GridWidget.Adder adder = this.grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);
			CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(
				CyclingButtonWidget.<WorldCreator.WorldType>builder(WorldCreator.WorldType::getName)
					.values(this.getWorldTypes())
					.narration(CreateWorldScreen.WorldTab::getWorldTypeNarrationMessage)
					.build(
						0, 0, 150, 20, Text.translatable("selectWorld.mapType"), (cyclingButtonWidgetx, worldType) -> CreateWorldScreen.this.worldCreator.setWorldType(worldType)
					)
			);
			cyclingButtonWidget.setValue(CreateWorldScreen.this.worldCreator.getWorldType());
			CreateWorldScreen.this.worldCreator.addListener(creator -> {
				WorldCreator.WorldType worldType = creator.getWorldType();
				cyclingButtonWidget.setValue(worldType);
				if (worldType.isAmplified()) {
					cyclingButtonWidget.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
				} else {
					cyclingButtonWidget.setTooltip(null);
				}

				cyclingButtonWidget.active = CreateWorldScreen.this.worldCreator.getWorldType().preset() != null;
			});
			this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen()).build());
			CreateWorldScreen.this.worldCreator.addListener(creator -> this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null);
			GridWidget.Adder adder2 = new GridWidget().setRowSpacing(4).createAdder(1);
			adder2.add(new TextWidget(ENTER_SEED_TEXT, CreateWorldScreen.this.textRenderer).alignLeft());
			this.seedField = adder2.add(new TextFieldWidget(CreateWorldScreen.this.textRenderer, 0, 0, 308, 20, Text.translatable("selectWorld.enterSeed")) {
				@Override
				protected MutableText getNarrationMessage() {
					return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(CreateWorldScreen.WorldTab.SEED_INFO_TEXT);
				}
			}, adder.copyPositioner().margin(1));
			this.seedField.setPlaceholder(SEED_INFO_TEXT);
			this.seedField.setText(CreateWorldScreen.this.worldCreator.getSeed());
			this.seedField.setChangedListener(seed -> CreateWorldScreen.this.worldCreator.setSeed(this.seedField.getText()));
			adder.add(adder2.getGridWidget(), 2);
			WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310).marginLeft(1);
			builder.add(MAP_FEATURES_TEXT, CreateWorldScreen.this.worldCreator::shouldGenerateStructures, CreateWorldScreen.this.worldCreator::setGenerateStructures)
				.toggleable(() -> !CreateWorldScreen.this.worldCreator.isDebug())
				.tooltip(MAP_FEATURES_INFO_TEXT);
			builder.add(BONUS_ITEMS_TEXT, CreateWorldScreen.this.worldCreator::isBonusChestEnabled, CreateWorldScreen.this.worldCreator::setBonusChestEnabled)
				.toggleable(() -> !CreateWorldScreen.this.worldCreator.isHardcore() && !CreateWorldScreen.this.worldCreator.isDebug());
			WorldScreenOptionGrid worldScreenOptionGrid = builder.build(widget -> adder.add(widget, 2));
			CreateWorldScreen.this.worldCreator.addListener(creator -> worldScreenOptionGrid.refresh());
		}

		private void openCustomizeScreen() {
			LevelScreenProvider levelScreenProvider = CreateWorldScreen.this.worldCreator.getLevelScreenProvider();
			if (levelScreenProvider != null) {
				CreateWorldScreen.this.client
					.setScreen(levelScreenProvider.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder()));
			}
		}

		private CyclingButtonWidget.Values<WorldCreator.WorldType> getWorldTypes() {
			return new CyclingButtonWidget.Values<WorldCreator.WorldType>() {
				@Override
				public List<WorldCreator.WorldType> getCurrent() {
					return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean()
						? CreateWorldScreen.this.worldCreator.getExtendedWorldTypes()
						: CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
				}

				@Override
				public List<WorldCreator.WorldType> getDefaults() {
					return CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
				}
			};
		}

		private static MutableText getWorldTypeNarrationMessage(CyclingButtonWidget<WorldCreator.WorldType> worldTypeButton) {
			return worldTypeButton.getValue().isAmplified()
				? ScreenTexts.joinSentences(worldTypeButton.getGenericNarrationMessage(), AMPLIFIED_GENERATOR_INFO_TEXT)
				: worldTypeButton.getGenericNarrationMessage();
		}

		@Override
		public void tick() {
			this.seedField.tick();
		}
	}
}
