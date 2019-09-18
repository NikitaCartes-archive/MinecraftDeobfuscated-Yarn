package net.minecraft.client;

import com.google.common.collect.Queues;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.RedirectedResourcePack;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixers.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.KeybindText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.NonBlockingThreadExecutor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MinecraftClient extends NonBlockingThreadExecutor<Runnable> implements SnooperListener, WindowEventHandler {
	private static MinecraftClient instance;
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean IS_SYSTEM_MAC = SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.OSX;
	public static final Identifier DEFAULT_TEXT_RENDERER_ID = new Identifier("default");
	public static final Identifier ALT_TEXT_RENDERER_ID = new Identifier("alt");
	private static final CompletableFuture<Unit> voidFuture = CompletableFuture.completedFuture(Unit.INSTANCE);
	private final File resourcePackDir;
	private final PropertyMap sessionPropertyMap;
	private final TextureManager textureManager;
	private final DataFixer dataFixer;
	private final WindowProvider windowProvider;
	private final Window window;
	private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0F, 0L);
	private final Snooper snooper = new Snooper("client", this, SystemUtil.getMeasuringTimeMs());
	public final WorldRenderer worldRenderer;
	private final EntityRenderDispatcher entityRenderManager;
	private final ItemRenderer itemRenderer;
	private final FirstPersonRenderer firstPersonRenderer;
	public final ParticleManager particleManager;
	private final SearchManager searchManager = new SearchManager();
	private final Session session;
	public final TextRenderer textRenderer;
	public final GameRenderer gameRenderer;
	public final DebugRenderer debugRenderer;
	private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
	public final InGameHud inGameHud;
	public final GameOptions options;
	private final HotbarStorage creativeHotbarStorage;
	public final Mouse mouse;
	public final Keyboard keyboard;
	public final File runDirectory;
	private final String gameVersion;
	private final String versionType;
	private final Proxy netProxy;
	private final LevelStorage levelStorage;
	public final MetricsData metricsData = new MetricsData();
	private final boolean is64Bit;
	private final boolean isDemo;
	private final DisableableProfiler profiler = new DisableableProfiler(() -> this.renderTickCounter.ticksThisFrame);
	private final ReloadableResourceManager resourceManager;
	private final ClientResourcePackCreator resourcePackCreator;
	private final ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;
	private final LanguageManager languageManager;
	private final BlockColors blockColorMap;
	private final ItemColors itemColorMap;
	private final GlFramebuffer framebuffer;
	private final SpriteAtlasTexture spriteAtlas;
	private final SoundManager soundManager;
	private final MusicTracker musicTracker;
	private final FontManager fontManager;
	private final SplashTextResourceSupplier splashTextLoader;
	private final MinecraftSessionService sessionService;
	private final PlayerSkinProvider skinProvider;
	private final BakedModelManager bakedModelManager;
	private final BlockRenderManager blockRenderManager;
	private final PaintingManager paintingManager;
	private final StatusEffectSpriteManager statusEffectSpriteManager;
	private final ToastManager toastManager;
	private final MinecraftClientGame game = new MinecraftClientGame(this);
	private final TutorialManager tutorialManager;
	public static byte[] memoryReservedForCrash = new byte[10485760];
	@Nullable
	public ClientPlayerInteractionManager interactionManager;
	@Nullable
	public ClientWorld world;
	@Nullable
	public ClientPlayerEntity player;
	@Nullable
	private IntegratedServer server;
	@Nullable
	private ServerInfo currentServerEntry;
	@Nullable
	private ClientConnection clientConnection;
	private boolean isIntegratedServerRunning;
	@Nullable
	public Entity cameraEntity;
	@Nullable
	public Entity targetedEntity;
	@Nullable
	public HitResult hitResult;
	private int itemUseCooldown;
	protected int attackCooldown;
	private boolean paused;
	private float pausedTickDelta;
	private long lastMetricsSampleTime = SystemUtil.getMeasuringTimeNano();
	private long nextDebugInfoUpdateTime;
	private int fpsCounter;
	public boolean skipGameRender;
	@Nullable
	public Screen currentScreen;
	@Nullable
	public Overlay overlay;
	private boolean connectedToRealms;
	private Thread thread;
	private volatile boolean running = true;
	@Nullable
	private CrashReport crashReport;
	private static int currentFps;
	public String fpsDebugString = "";
	public boolean field_1730 = true;
	private boolean windowFocused;
	private final Queue<Runnable> renderTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	@Nullable
	private CompletableFuture<Void> resourceReloadFuture;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		super("Client");
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		File file = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.versionType = runArgs.game.versionType;
		this.sessionPropertyMap = runArgs.network.profileProperties;
		this.resourcePackCreator = new ClientResourcePackCreator(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.getResourceIndex());
		this.resourcePackContainerManager = new ResourcePackContainerManager<>((stringx, bl, supplier, resourcePackx, packResourceMetadata, insertionPosition) -> {
			Supplier<ResourcePack> supplier2;
			if (packResourceMetadata.getPackFormat() < SharedConstants.getGameVersion().getPackVersion()) {
				supplier2 = () -> new RedirectedResourcePack((ResourcePack)supplier.get(), RedirectedResourcePack.NEW_TO_OLD_MAP);
			} else {
				supplier2 = supplier;
			}

			return new ClientResourcePackContainer(stringx, bl, supplier2, resourcePackx, packResourceMetadata, insertionPosition);
		});
		this.resourcePackContainerManager.addCreator(this.resourcePackCreator);
		this.resourcePackContainerManager.addCreator(new FileResourcePackCreator(this.resourcePackDir));
		this.netProxy = runArgs.network.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = runArgs.network.session;
		LOGGER.info("Setting user: {}", this.session.getUsername());
		LOGGER.debug("(Session ID is {})", this.session.getSessionId());
		this.isDemo = runArgs.game.demo;
		this.is64Bit = checkIs64Bit();
		this.server = null;
		String string;
		int i;
		if (runArgs.autoConnect.serverIP != null) {
			string = runArgs.autoConnect.serverIP;
			i = runArgs.autoConnect.serverPort;
		} else {
			string = null;
			i = 0;
		}

		Bootstrap.initialize();
		Bootstrap.logMissingTranslations();
		KeybindText.i18n = KeyBinding::getLocalizedName;
		this.dataFixer = Schemas.getFixer();
		this.toastManager = new ToastManager(this);
		this.tutorialManager = new TutorialManager(this);
		this.thread = Thread.currentThread();
		this.options = new GameOptions(this, this.runDirectory);
		this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
		this.startTimerHackThread();
		LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
		WindowSettings windowSettings;
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			windowSettings = new WindowSettings(
				this.options.overrideWidth,
				this.options.overrideHeight,
				runArgs.windowSettings.fullscreenWidth,
				runArgs.windowSettings.fullscreenHeight,
				runArgs.windowSettings.fullscreen
			);
		} else {
			windowSettings = runArgs.windowSettings;
		}

		SystemUtil.nanoTimeSupplier = RenderSystem.initBackendSystem();
		this.windowProvider = new WindowProvider(this);
		this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
		this.onWindowFocusChanged(true);

		try {
			InputStream inputStream = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
			InputStream inputStream2 = this.getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
			this.window.setIcon(inputStream, inputStream2);
		} catch (IOException var9) {
			LOGGER.error("Couldn't set icon", (Throwable)var9);
		}

		this.window.setFramerateLimit(this.options.maxFps);
		this.mouse = new Mouse(this);
		this.mouse.setup(this.window.getHandle());
		this.keyboard = new Keyboard(this);
		this.keyboard.setup(this.window.getHandle());
		RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
		this.framebuffer = new GlFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, IS_SYSTEM_MAC);
		this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES, this.thread);
		this.options.addResourcePackContainersToManager(this.resourcePackContainerManager);
		this.resourcePackContainerManager.callCreators();
		List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
			.getEnabledContainers()
			.stream()
			.map(ResourcePackContainer::createResourcePack)
			.collect(Collectors.toList());

		for (ResourcePack resourcePack : list) {
			this.resourceManager.addPack(resourcePack);
		}

		this.languageManager = new LanguageManager(this.options.language);
		this.resourceManager.registerListener(this.languageManager);
		this.languageManager.reloadResources(list);
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.registerListener(this.textureManager);
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(file, "skins"), this.sessionService);
		this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
		this.soundManager = new SoundManager(this.resourceManager, this.options);
		this.resourceManager.registerListener(this.soundManager);
		this.splashTextLoader = new SplashTextResourceSupplier(this.session);
		this.resourceManager.registerListener(this.splashTextLoader);
		this.musicTracker = new MusicTracker(this);
		this.fontManager = new FontManager(this.textureManager, this.forcesUnicodeFont());
		this.resourceManager.registerListener(this.fontManager.getResourceReloadListener());
		TextRenderer textRenderer = this.fontManager.getTextRenderer(DEFAULT_TEXT_RENDERER_ID);
		if (textRenderer == null) {
			throw new IllegalStateException("Default font is null");
		} else {
			this.textRenderer = textRenderer;
			this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
			this.resourceManager.registerListener(new GrassColormapResourceSupplier());
			this.resourceManager.registerListener(new FoliageColormapResourceSupplier());
			this.window.setPhase("Startup");
			RenderSystem.setupDefaultState(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
			this.window.setPhase("Post startup");
			this.spriteAtlas = new SpriteAtlasTexture("textures");
			this.spriteAtlas.setMipLevel(this.options.mipmapLevels);
			this.textureManager.registerTextureUpdateable(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.spriteAtlas);
			this.textureManager.method_22813(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.spriteAtlas.setFilter(false, this.options.mipmapLevels > 0);
			this.blockColorMap = BlockColors.create();
			this.itemColorMap = ItemColors.create(this.blockColorMap);
			this.bakedModelManager = new BakedModelManager(this.spriteAtlas, this.blockColorMap);
			this.resourceManager.registerListener(this.bakedModelManager);
			this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColorMap);
			this.entityRenderManager = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
			this.firstPersonRenderer = new FirstPersonRenderer(this);
			this.resourceManager.registerListener(this.itemRenderer);
			this.gameRenderer = new GameRenderer(this, this.resourceManager);
			this.resourceManager.registerListener(this.gameRenderer);
			this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockStateMaps(), this.blockColorMap);
			this.resourceManager.registerListener(this.blockRenderManager);
			this.worldRenderer = new WorldRenderer(this);
			this.resourceManager.registerListener(this.worldRenderer);
			this.initializeSearchableContainers();
			this.resourceManager.registerListener(this.searchManager);
			this.particleManager = new ParticleManager(this.world, this.textureManager);
			this.resourceManager.registerListener(this.particleManager);
			this.paintingManager = new PaintingManager(this.textureManager);
			this.resourceManager.registerListener(this.paintingManager);
			this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
			this.resourceManager.registerListener(this.statusEffectSpriteManager);
			this.inGameHud = new InGameHud(this);
			this.debugRenderer = new DebugRenderer(this);
			RenderSystem.setErrorCallback(this::handleGlErrorByDisableVsync);
			if (this.options.fullscreen && !this.window.isFullscreen()) {
				this.window.toggleFullscreen();
				this.options.fullscreen = this.window.isFullscreen();
			}

			this.window.setVsync(this.options.enableVsync);
			this.window.method_21668(this.options.field_20308);
			this.window.logOnGlError();
			this.onResolutionChanged();
			if (string != null) {
				this.openScreen(new ConnectScreen(new TitleScreen(), this, string, i));
			} else {
				this.openScreen(new TitleScreen(true));
			}

			SplashScreen.init(this);
			this.setOverlay(new SplashScreen(this, this.resourceManager.beginInitialMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, voidFuture), () -> {
				if (SharedConstants.isDevelopment) {
					this.checkGameData();
				}
			}, false));
		}
	}

	public void start() {
		this.thread = Thread.currentThread();

		try {
			boolean bl = false;

			while (this.running) {
				if (this.crashReport != null) {
					printCrashReport(this.crashReport);
					return;
				}

				try {
					this.render(!bl);
				} catch (OutOfMemoryError var3) {
					if (bl) {
						throw var3;
					}

					this.cleanUpAfterCrash();
					this.openScreen(new OutOfMemoryScreen());
					System.gc();
					LOGGER.fatal("Out of memory", (Throwable)var3);
					bl = true;
				}
			}
		} catch (CrashException var4) {
			this.populateCrashReport(var4.getReport());
			this.cleanUpAfterCrash();
			LOGGER.fatal("Reported exception thrown!", (Throwable)var4);
			printCrashReport(var4.getReport());
		} catch (Throwable var5) {
			CrashReport crashReport = this.populateCrashReport(new CrashReport("Unexpected error", var5));
			LOGGER.fatal("Unreported exception thrown!", var5);
			this.cleanUpAfterCrash();
			printCrashReport(crashReport);
		}
	}

	private void initializeSearchableContainers() {
		TextSearchableContainer<ItemStack> textSearchableContainer = new TextSearchableContainer<>(
			itemStack -> itemStack.getTooltip(null, TooltipContext.Default.NORMAL)
					.stream()
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty()),
			itemStack -> Stream.of(Registry.ITEM.getId(itemStack.getItem()))
		);
		IdentifierSearchableContainer<ItemStack> identifierSearchableContainer = new IdentifierSearchableContainer<>(
			itemStack -> ItemTags.getContainer().getTagsFor(itemStack.getItem()).stream()
		);
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Item item : Registry.ITEM) {
			item.appendStacks(ItemGroup.SEARCH, defaultedList);
		}

		defaultedList.forEach(itemStack -> {
			textSearchableContainer.add(itemStack);
			identifierSearchableContainer.add(itemStack);
		});
		TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<>(
			recipeResultCollection -> recipeResultCollection.getAllRecipes()
					.stream()
					.flatMap(recipe -> recipe.getOutput().getTooltip(null, TooltipContext.Default.NORMAL).stream())
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty()),
			recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem()))
		);
		this.searchManager.put(SearchManager.ITEM_TOOLTIP, textSearchableContainer);
		this.searchManager.put(SearchManager.ITEM_TAG, identifierSearchableContainer);
		this.searchManager.put(SearchManager.RECIPE_OUTPUT, textSearchableContainer2);
	}

	private void handleGlErrorByDisableVsync(int i, long l) {
		this.options.enableVsync = false;
		this.options.write();
	}

	private static boolean checkIs64Bit() {
		String[] strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String string : strings) {
			String string2 = System.getProperty(string);
			if (string2 != null && string2.contains("64")) {
				return true;
			}
		}

		return false;
	}

	public GlFramebuffer getFramebuffer() {
		return this.framebuffer;
	}

	public String getGameVersion() {
		return this.gameVersion;
	}

	public String getVersionType() {
		return this.versionType;
	}

	private void startTimerHackThread() {
		Thread thread = new Thread("Timer hack thread") {
			public void run() {
				while (MinecraftClient.this.running) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	public void setCrashReport(CrashReport crashReport) {
		this.crashReport = crashReport;
	}

	public static void printCrashReport(CrashReport crashReport) {
		File file = new File(getInstance().runDirectory, "crash-reports");
		File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
		Bootstrap.println(crashReport.asString());
		if (crashReport.getFile() != null) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
			System.exit(-1);
		} else if (crashReport.writeToFile(file2)) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
			System.exit(-1);
		} else {
			Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
			System.exit(-2);
		}
	}

	public boolean forcesUnicodeFont() {
		return this.options.forceUnicodeFont;
	}

	public CompletableFuture<Void> reloadResources() {
		if (this.resourceReloadFuture != null) {
			return this.resourceReloadFuture;
		} else {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			if (this.overlay instanceof SplashScreen) {
				this.resourceReloadFuture = completableFuture;
				return completableFuture;
			} else {
				this.resourcePackContainerManager.callCreators();
				List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
					.getEnabledContainers()
					.stream()
					.map(ResourcePackContainer::createResourcePack)
					.collect(Collectors.toList());
				this.setOverlay(new SplashScreen(this, this.resourceManager.beginMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, voidFuture, list), () -> {
					this.languageManager.reloadResources(list);
					this.worldRenderer.reload();
					completableFuture.complete(null);
				}, true));
				return completableFuture;
			}
		}
	}

	private void checkGameData() {
		boolean bl = false;
		BlockModels blockModels = this.getBlockRenderManager().getModels();
		BakedModel bakedModel = blockModels.getModelManager().getMissingModel();

		for (Block block : Registry.BLOCK) {
			for (BlockState blockState : block.getStateFactory().getStates()) {
				if (blockState.getRenderType() == BlockRenderType.MODEL) {
					BakedModel bakedModel2 = blockModels.getModel(blockState);
					if (bakedModel2 == bakedModel) {
						LOGGER.debug("Missing model for: {}", blockState);
						bl = true;
					}
				}
			}
		}

		Sprite sprite = bakedModel.getSprite();

		for (Block block2 : Registry.BLOCK) {
			for (BlockState blockState2 : block2.getStateFactory().getStates()) {
				Sprite sprite2 = blockModels.getSprite(blockState2);
				if (!blockState2.isAir() && sprite2 == sprite) {
					LOGGER.debug("Missing particle icon for: {}", blockState2);
					bl = true;
				}
			}
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Item item : Registry.ITEM) {
			defaultedList.clear();
			item.appendStacks(ItemGroup.SEARCH, defaultedList);

			for (ItemStack itemStack : defaultedList) {
				String string = itemStack.getTranslationKey();
				String string2 = new TranslatableText(string).getString();
				if (string2.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) {
					LOGGER.debug("Missing translation for: {} {} {}", itemStack, string, itemStack.getItem());
				}
			}
		}

		bl |= Screens.validateScreens();
		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	public void openScreen(@Nullable Screen screen) {
		if (this.currentScreen != null) {
			this.currentScreen.removed();
		}

		if (screen == null && this.world == null) {
			screen = new TitleScreen();
		} else if (screen == null && this.player.getHealth() <= 0.0F) {
			if (this.player.method_22419()) {
				screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
			} else {
				this.player.requestRespawn();
			}
		}

		if (screen instanceof TitleScreen || screen instanceof MultiplayerScreen) {
			this.options.debugEnabled = false;
			this.inGameHud.getChatHud().clear(true);
		}

		this.currentScreen = screen;
		if (screen != null) {
			this.mouse.unlockCursor();
			KeyBinding.unpressAll();
			screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
			this.skipGameRender = false;
			NarratorManager.INSTANCE.narrate(screen.getNarrationMessage());
		} else {
			this.soundManager.resumeAll();
			this.mouse.lockCursor();
		}
	}

	public void setOverlay(@Nullable Overlay overlay) {
		this.overlay = overlay;
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");
			NarratorManager.INSTANCE.destroy();

			try {
				if (this.world != null) {
					this.world.disconnect();
				}

				this.disconnect();
			} catch (Throwable var5) {
			}

			if (this.currentScreen != null) {
				this.currentScreen.removed();
			}

			this.close();
		} finally {
			SystemUtil.nanoTimeSupplier = System::nanoTime;
			if (this.crashReport == null) {
				System.exit(0);
			}
		}
	}

	@Override
	public void close() {
		try {
			this.spriteAtlas.clear();
			this.textRenderer.close();
			this.fontManager.close();
			this.gameRenderer.close();
			this.worldRenderer.close();
			this.soundManager.close();
			this.resourcePackContainerManager.close();
			this.particleManager.clearAtlas();
			this.statusEffectSpriteManager.close();
			this.paintingManager.close();
			SystemUtil.shutdownServerWorkerExecutor();
		} finally {
			this.windowProvider.close();
			this.window.close();
		}
	}

	private void render(boolean bl) {
		this.window.setPhase("Pre render");
		long l = SystemUtil.getMeasuringTimeNano();
		this.profiler.startTick();
		if (this.window.method_22093()) {
			this.scheduleStop();
		}

		if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashScreen)) {
			CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
			this.resourceReloadFuture = null;
			this.reloadResources().thenRun(() -> completableFuture.complete(null));
		}

		Runnable runnable;
		while ((runnable = (Runnable)this.renderTaskQueue.poll()) != null) {
			runnable.run();
		}

		if (bl) {
			this.renderTickCounter.beginRenderTick(SystemUtil.getMeasuringTimeMs());
			this.profiler.push("scheduledExecutables");
			this.executeQueuedTasks();
			this.profiler.pop();
		}

		long m = SystemUtil.getMeasuringTimeNano();
		this.profiler.push("tick");
		if (bl) {
			for (int i = 0; i < Math.min(10, this.renderTickCounter.ticksThisFrame); i++) {
				this.tick();
			}
		}

		this.mouse.updateMouse();
		this.window.setPhase("Render");
		RenderSystem.pollEvents();
		long n = SystemUtil.getMeasuringTimeNano() - m;
		this.profiler.swap("sound");
		this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
		this.profiler.pop();
		this.profiler.push("render");
		RenderSystem.pushMatrix();
		RenderSystem.clear(16640, IS_SYSTEM_MAC);
		this.framebuffer.beginWrite(true);
		this.profiler.push("display");
		RenderSystem.enableTexture();
		this.profiler.pop();
		if (!this.skipGameRender) {
			this.profiler.swap("gameRenderer");
			this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, bl);
			this.profiler.swap("toasts");
			this.toastManager.draw();
			this.profiler.pop();
		}

		this.profiler.endTick();
		if (this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden) {
			this.profiler.getController().enable();
			this.drawProfilerResults();
		} else {
			this.profiler.getController().disable();
		}

		this.framebuffer.endWrite();
		RenderSystem.popMatrix();
		RenderSystem.pushMatrix();
		this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		RenderSystem.popMatrix();
		this.profiler.startTick();
		this.updateDisplay(true);
		Thread.yield();
		this.window.setPhase("Post render");
		this.fpsCounter++;
		boolean bl2 = this.isIntegratedServerRunning()
			&& (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.overlay != null && this.overlay.pausesGame())
			&& !this.server.isRemote();
		if (this.paused != bl2) {
			if (this.paused) {
				this.pausedTickDelta = this.renderTickCounter.tickDelta;
			} else {
				this.renderTickCounter.tickDelta = this.pausedTickDelta;
			}

			this.paused = bl2;
		}

		long o = SystemUtil.getMeasuringTimeNano();
		this.metricsData.pushSample(o - this.lastMetricsSampleTime);
		this.lastMetricsSampleTime = o;

		while (SystemUtil.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps T: %s%s%s%s",
				currentFps,
				(double)this.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : this.options.maxFps,
				this.options.enableVsync ? " vsync" : "",
				this.options.fancyGraphics ? "" : " fast",
				this.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds")
			);
			this.nextDebugInfoUpdateTime += 1000L;
			this.fpsCounter = 0;
			this.snooper.update();
			if (!this.snooper.isActive()) {
				this.snooper.method_5482();
			}
		}

		this.profiler.endTick();
	}

	@Override
	public void updateDisplay(boolean bl) {
		RenderSystem.flipFrame();
		this.profiler.push("display_update");
		this.window.setFullscreen(this.options.fullscreen);
		this.profiler.pop();
		if (bl && this.isFramerateLimited()) {
			this.profiler.push("fpslimit_wait");
			this.window.waitForFramerateLimit();
			this.profiler.pop();
		}
	}

	@Override
	public void onResolutionChanged() {
		int i = this.window.calculateScaleFactor(this.options.guiScale, this.forcesUnicodeFont());
		this.window.setScaleFactor((double)i);
		if (this.currentScreen != null) {
			this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
		}

		GlFramebuffer glFramebuffer = this.getFramebuffer();
		glFramebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
		this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.mouse.onResolutionChanged();
	}

	private int getFramerateLimit() {
		return this.world != null || this.currentScreen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
	}

	private boolean isFramerateLimited() {
		return (double)this.getFramerateLimit() < Option.FRAMERATE_LIMIT.getMax();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.worldRenderer.method_3267();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			if (this.isIntegratedServerRunning && this.server != null) {
				this.server.stop(true);
			}

			this.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
		} catch (Throwable var2) {
		}

		System.gc();
	}

	void handleProfilerKeyPress(int i) {
		ProfileResult profileResult = this.profiler.getController().getResults();
		List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
		if (!list.isEmpty()) {
			ProfilerTiming profilerTiming = (ProfilerTiming)list.remove(0);
			if (i == 0) {
				if (!profilerTiming.name.isEmpty()) {
					int j = this.openProfilerSection.lastIndexOf(46);
					if (j >= 0) {
						this.openProfilerSection = this.openProfilerSection.substring(0, j);
					}
				}
			} else {
				i--;
				if (i < list.size() && !"unspecified".equals(((ProfilerTiming)list.get(i)).name)) {
					if (!this.openProfilerSection.isEmpty()) {
						this.openProfilerSection = this.openProfilerSection + ".";
					}

					this.openProfilerSection = this.openProfilerSection + ((ProfilerTiming)list.get(i)).name;
				}
			}
		}
	}

	private void drawProfilerResults() {
		if (this.profiler.getController().isEnabled()) {
			ProfileResult profileResult = this.profiler.getController().getResults();
			List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
			ProfilerTiming profilerTiming = (ProfilerTiming)list.remove(0);
			RenderSystem.clear(256, IS_SYSTEM_MAC);
			RenderSystem.matrixMode(5889);
			RenderSystem.enableColorMaterial();
			RenderSystem.loadIdentity();
			RenderSystem.ortho(0.0, (double)this.window.getFramebufferWidth(), (double)this.window.getFramebufferHeight(), 0.0, 1000.0, 3000.0);
			RenderSystem.matrixMode(5888);
			RenderSystem.loadIdentity();
			RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
			RenderSystem.lineWidth(1.0F);
			RenderSystem.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int i = 160;
			int j = this.window.getFramebufferWidth() - 160 - 10;
			int k = this.window.getFramebufferHeight() - 320;
			RenderSystem.enableBlend();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			tessellator.draw();
			RenderSystem.disableBlend();
			double d = 0.0;

			for (ProfilerTiming profilerTiming2 : list) {
				int l = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				int m = profilerTiming2.getColor();
				int n = m >> 16 & 0xFF;
				int o = m >> 8 & 0xFF;
				int p = m & 0xFF;
				bufferBuilder.vertex((double)j, (double)k, 0.0).color(n, o, p, 255).next();

				for (int q = l; q >= 0; q--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 160.0F;
					float h = MathHelper.cos(f) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(n, o, p, 255).next();
				}

				tessellator.draw();
				bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

				for (int q = l; q >= 0; q--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 160.0F;
					float h = MathHelper.cos(f) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h + 10.0F), 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
				}

				tessellator.draw();
				d += profilerTiming2.parentSectionUsagePercentage;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
			RenderSystem.enableTexture();
			String string = "";
			if (!"unspecified".equals(profilerTiming.name)) {
				string = string + "[0] ";
			}

			if (profilerTiming.name.isEmpty()) {
				string = string + "ROOT ";
			} else {
				string = string + profilerTiming.name + ' ';
			}

			int l = 16777215;
			this.textRenderer.drawWithShadow(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			this.textRenderer.drawWithShadow(string, (float)(j + 160 - this.textRenderer.getStringWidth(string)), (float)(k - 80 - 16), 16777215);

			for (int r = 0; r < list.size(); r++) {
				ProfilerTiming profilerTiming3 = (ProfilerTiming)list.get(r);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(r + 1).append("] ");
				}

				String string2 = stringBuilder.append(profilerTiming3.name).toString();
				this.textRenderer.drawWithShadow(string2, (float)(j - 160), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
				this.textRenderer
					.drawWithShadow(string2, (float)(j + 160 - 50 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
				this.textRenderer
					.drawWithShadow(string2, (float)(j + 160 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
			}
		}
	}

	public void scheduleStop() {
		this.running = false;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void openPauseMenu(boolean bl) {
		if (this.currentScreen == null) {
			boolean bl2 = this.isIntegratedServerRunning() && !this.server.isRemote();
			if (bl2) {
				this.openScreen(new GameMenuScreen(!bl));
				this.soundManager.pauseAll();
			} else {
				this.openScreen(new GameMenuScreen(true));
			}
		}
	}

	private void handleBlockBreaking(boolean bl) {
		if (!bl) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
			if (bl && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.world.getBlockState(blockPos).isAir()) {
					Direction direction = blockHitResult.getSide();
					if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
						this.particleManager.addBlockBreakingParticles(blockPos, direction);
						this.player.swingHand(Hand.MAIN_HAND);
					}
				}
			} else {
				this.interactionManager.cancelBlockBreaking();
			}
		}
	}

	private void doAttack() {
		if (this.attackCooldown <= 0) {
			if (this.hitResult == null) {
				LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
				if (this.interactionManager.hasLimitedAttackSpeed()) {
					this.attackCooldown = 10;
				}
			} else if (!this.player.isRiding()) {
				switch (this.hitResult.getType()) {
					case ENTITY:
						this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.hitResult).getEntity());
						break;
					case BLOCK:
						BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
						BlockPos blockPos = blockHitResult.getBlockPos();
						if (!this.world.getBlockState(blockPos).isAir()) {
							this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
							break;
						}
					case MISS:
						if (this.interactionManager.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}

						this.player.resetLastAttackedTicks();
				}

				this.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}

	private void doItemUse() {
		if (!this.interactionManager.isBreakingBlock()) {
			this.itemUseCooldown = 4;
			if (!this.player.isRiding()) {
				if (this.hitResult == null) {
					LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				for (Hand hand : Hand.values()) {
					ItemStack itemStack = this.player.getStackInHand(hand);
					if (this.hitResult != null) {
						switch (this.hitResult.getType()) {
							case ENTITY:
								EntityHitResult entityHitResult = (EntityHitResult)this.hitResult;
								Entity entity = entityHitResult.getEntity();
								if (this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand) == ActionResult.SUCCESS
									|| this.interactionManager.interactEntity(this.player, entity, hand) == ActionResult.SUCCESS) {
									this.player.swingHand(hand);
								}

								return;
							case BLOCK:
								BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
								int i = itemStack.getCount();
								ActionResult actionResult = this.interactionManager.interactBlock(this.player, this.world, hand, blockHitResult);
								if (actionResult == ActionResult.SUCCESS) {
									this.player.swingHand(hand);
									if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
										this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
									}

									return;
								}

								if (actionResult == ActionResult.FAIL) {
									return;
								}
						}
					}

					if (!itemStack.isEmpty()) {
						TypedActionResult<ItemStack> typedActionResult = this.interactionManager.interactItem(this.player, this.world, hand);
						if (typedActionResult.getResult() == ActionResult.SUCCESS) {
							if (typedActionResult.method_22429()) {
								this.player.swingHand(hand);
							}

							this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
							return;
						}
					}
				}
			}
		}
	}

	public MusicTracker getMusicTracker() {
		return this.musicTracker;
	}

	public void tick() {
		if (this.itemUseCooldown > 0) {
			this.itemUseCooldown--;
		}

		this.profiler.push("gui");
		if (!this.paused) {
			this.inGameHud.tick();
		}

		this.profiler.pop();
		this.gameRenderer.updateTargetedEntity(1.0F);
		this.tutorialManager.tick(this.world, this.hitResult);
		this.profiler.push("gameMode");
		if (!this.paused && this.world != null) {
			this.interactionManager.tick();
		}

		this.profiler.swap("textures");
		if (this.world != null) {
			this.textureManager.tick();
		}

		if (this.currentScreen == null && this.player != null) {
			if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof DeathScreen)) {
				this.openScreen(null);
			} else if (this.player.isSleeping() && this.world != null) {
				this.openScreen(new SleepingChatScreen());
			}
		} else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
			this.openScreen(null);
		}

		if (this.currentScreen != null) {
			this.attackCooldown = 10000;
		}

		if (this.currentScreen != null) {
			Screen.wrapScreenError(() -> this.currentScreen.tick(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
		}

		if (!this.options.debugEnabled) {
			this.inGameHud.resetDebugHudChunk();
		}

		if (this.overlay == null && (this.currentScreen == null || this.currentScreen.passEvents)) {
			this.profiler.swap("GLFW events");
			RenderSystem.pollEvents();
			this.handleInputEvents();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
		}

		if (this.world != null) {
			this.profiler.swap("gameRenderer");
			if (!this.paused) {
				this.gameRenderer.tick();
			}

			this.profiler.swap("levelRenderer");
			if (!this.paused) {
				this.worldRenderer.tick();
			}

			this.profiler.swap("level");
			if (!this.paused) {
				if (this.world.getTicksSinceLightning() > 0) {
					this.world.setTicksSinceLightning(this.world.getTicksSinceLightning() - 1);
				}

				this.world.tickEntities();
			}
		} else if (this.gameRenderer.isShaderEnabled()) {
			this.gameRenderer.disableShader();
		}

		if (!this.paused) {
			this.musicTracker.tick();
		}

		this.soundManager.tick(this.paused);
		if (this.world != null) {
			if (!this.paused) {
				this.world.setMobSpawnOptions(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
				this.tutorialManager.tick();

				try {
					this.world.tick(() -> true);
				} catch (Throwable var4) {
					CrashReport crashReport = CrashReport.create(var4, "Exception in world tick");
					if (this.world == null) {
						CrashReportSection crashReportSection = crashReport.addElement("Affected level");
						crashReportSection.add("Problem", "Level is null!");
					} else {
						this.world.addDetailsToCrashReport(crashReport);
					}

					throw new CrashException(crashReport);
				}
			}

			this.profiler.swap("animateTick");
			if (!this.paused && this.world != null) {
				this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
			}

			this.profiler.swap("particles");
			if (!this.paused) {
				this.particleManager.tick();
			}
		} else if (this.clientConnection != null) {
			this.profiler.swap("pendingConnection");
			this.clientConnection.tick();
		}

		this.profiler.swap("keyboard");
		this.keyboard.pollDebugCrash();
		this.profiler.pop();
	}

	private void handleInputEvents() {
		while (this.options.keyTogglePerspective.wasPressed()) {
			this.options.perspective++;
			if (this.options.perspective > 2) {
				this.options.perspective = 0;
			}

			if (this.options.perspective == 0) {
				this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
			} else if (this.options.perspective == 1) {
				this.gameRenderer.onCameraEntitySet(null);
			}

			this.worldRenderer.scheduleTerrainUpdate();
		}

		while (this.options.keySmoothCamera.wasPressed()) {
			this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.options.keySaveToolbarActivator.isPressed();
			boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
			if (this.options.keysHotbar[i].wasPressed()) {
				if (this.player.isSpectator()) {
					this.inGameHud.getSpectatorHud().selectSlot(i);
				} else if (!this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
					this.player.inventory.selectedSlot = i;
				} else {
					CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
				}
			}
		}

		while (this.options.keyInventory.wasPressed()) {
			if (this.interactionManager.hasRidingInventory()) {
				this.player.openRidingInventory();
			} else {
				this.tutorialManager.onInventoryOpened();
				this.openScreen(new InventoryScreen(this.player));
			}
		}

		while (this.options.keyAdvancements.wasPressed()) {
			this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
		}

		while (this.options.keySwapHands.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
			}
		}

		while (this.options.keyDrop.wasPressed()) {
			if (!this.player.isSpectator() && this.player.dropSelectedItem(Screen.hasControlDown())) {
				this.player.swingHand(Hand.MAIN_HAND);
			}
		}

		boolean bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
		if (bl3) {
			while (this.options.keyChat.wasPressed()) {
				this.openScreen(new ChatScreen(""));
			}

			if (this.currentScreen == null && this.overlay == null && this.options.keyCommand.wasPressed()) {
				this.openScreen(new ChatScreen("/"));
			}
		}

		if (this.player.isUsingItem()) {
			if (!this.options.keyUse.isPressed()) {
				this.interactionManager.stopUsingItem(this.player);
			}

			while (this.options.keyAttack.wasPressed()) {
			}

			while (this.options.keyUse.wasPressed()) {
			}

			while (this.options.keyPickItem.wasPressed()) {
			}
		} else {
			while (this.options.keyAttack.wasPressed()) {
				this.doAttack();
			}

			while (this.options.keyUse.wasPressed()) {
				this.doItemUse();
			}

			while (this.options.keyPickItem.wasPressed()) {
				this.doItemPick();
			}
		}

		if (this.options.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
			this.doItemUse();
		}

		this.handleBlockBreaking(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
	}

	public void startIntegratedServer(String string, String string2, @Nullable LevelInfo levelInfo) {
		this.disconnect();
		WorldSaveHandler worldSaveHandler = this.levelStorage.createSaveHandler(string, null);
		LevelProperties levelProperties = worldSaveHandler.readProperties();
		if (levelProperties == null && levelInfo != null) {
			levelProperties = new LevelProperties(levelInfo, string);
			worldSaveHandler.saveWorld(levelProperties);
		}

		if (levelInfo == null) {
			levelInfo = new LevelInfo(levelProperties);
		}

		this.worldGenProgressTracker.set(null);

		try {
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
			SkullBlockEntity.setUserCache(userCache);
			SkullBlockEntity.setSessionService(minecraftSessionService);
			UserCache.setUseRemote(false);
			this.server = new IntegratedServer(
				this, string, string2, levelInfo, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, i -> {
					WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i + 0);
					worldGenerationProgressTracker.start();
					this.worldGenProgressTracker.set(worldGenerationProgressTracker);
					return new QueueingWorldGenerationProgressListener(worldGenerationProgressTracker, this.renderTaskQueue::add);
				}
			);
			this.server.start();
			this.isIntegratedServerRunning = true;
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Starting integrated server");
			CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
			crashReportSection.add("Level ID", string);
			crashReportSection.add("Level Name", string2);
			throw new CrashException(crashReport);
		}

		while (this.worldGenProgressTracker.get() == null) {
			Thread.yield();
		}

		LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen((WorldGenerationProgressTracker)this.worldGenProgressTracker.get());
		this.openScreen(levelLoadingScreen);

		while (!this.server.isLoading()) {
			levelLoadingScreen.tick();
			this.render(false);

			try {
				Thread.sleep(16L);
			} catch (InterruptedException var10) {
			}

			if (this.crashReport != null) {
				printCrashReport(this.crashReport);
				return;
			}
		}

		SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connect(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, text -> {
		}));
		clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void joinWorld(ClientWorld clientWorld) {
		ProgressScreen progressScreen = new ProgressScreen();
		progressScreen.method_15412(new TranslatableText("connect.joining"));
		this.reset(progressScreen);
		this.world = clientWorld;
		this.setWorld(clientWorld);
		if (!this.isIntegratedServerRunning) {
			AuthenticationService authenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
			SkullBlockEntity.setUserCache(userCache);
			SkullBlockEntity.setSessionService(minecraftSessionService);
			UserCache.setUseRemote(false);
		}
	}

	public void disconnect() {
		this.disconnect(new ProgressScreen());
	}

	public void disconnect(Screen screen) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			this.clearTasks();
			clientPlayNetworkHandler.clearWorld();
		}

		IntegratedServer integratedServer = this.server;
		this.server = null;
		this.gameRenderer.reset();
		this.interactionManager = null;
		NarratorManager.INSTANCE.clear();
		this.reset(screen);
		if (this.world != null) {
			if (integratedServer != null) {
				while (!integratedServer.isStopping()) {
					this.render(false);
				}
			}

			this.resourcePackCreator.clear();
			this.inGameHud.clear();
			this.currentServerEntry = null;
			this.isIntegratedServerRunning = false;
			this.game.onLeaveGameSession();
		}

		this.world = null;
		this.setWorld(null);
		this.player = null;
	}

	private void reset(Screen screen) {
		this.musicTracker.stop();
		this.soundManager.stopAll();
		this.cameraEntity = null;
		this.clientConnection = null;
		this.openScreen(screen);
		this.render(false);
	}

	private void setWorld(@Nullable ClientWorld clientWorld) {
		this.worldRenderer.setWorld(clientWorld);
		this.particleManager.setWorld(clientWorld);
		BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
	}

	public final boolean isDemo() {
		return this.isDemo;
	}

	@Nullable
	public ClientPlayNetworkHandler getNetworkHandler() {
		return this.player == null ? null : this.player.networkHandler;
	}

	public static boolean isHudEnabled() {
		return !instance.options.hudHidden;
	}

	public static boolean isFancyGraphicsEnabled() {
		return instance.options.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance.options.ao != AoOption.OFF;
	}

	private void doItemPick() {
		if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
			boolean bl = this.player.abilities.creativeMode;
			BlockEntity blockEntity = null;
			HitResult.Type type = this.hitResult.getType();
			ItemStack itemStack;
			if (type == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult)this.hitResult).getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (blockState.isAir()) {
					return;
				}

				itemStack = block.getPickStack(this.world, blockPos, blockState);
				if (itemStack.isEmpty()) {
					return;
				}

				if (bl && Screen.hasControlDown() && block.hasBlockEntity()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}
			} else {
				if (type != HitResult.Type.ENTITY || !bl) {
					return;
				}

				Entity entity = ((EntityHitResult)this.hitResult).getEntity();
				if (entity instanceof PaintingEntity) {
					itemStack = new ItemStack(Items.PAINTING);
				} else if (entity instanceof LeadKnotEntity) {
					itemStack = new ItemStack(Items.LEAD);
				} else if (entity instanceof ItemFrameEntity) {
					ItemFrameEntity itemFrameEntity = (ItemFrameEntity)entity;
					ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
					if (itemStack2.isEmpty()) {
						itemStack = new ItemStack(Items.ITEM_FRAME);
					} else {
						itemStack = itemStack2.copy();
					}
				} else if (entity instanceof AbstractMinecartEntity) {
					AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
					Item item;
					switch (abstractMinecartEntity.getMinecartType()) {
						case FURNACE:
							item = Items.FURNACE_MINECART;
							break;
						case CHEST:
							item = Items.CHEST_MINECART;
							break;
						case TNT:
							item = Items.TNT_MINECART;
							break;
						case HOPPER:
							item = Items.HOPPER_MINECART;
							break;
						case COMMAND_BLOCK:
							item = Items.COMMAND_BLOCK_MINECART;
							break;
						default:
							item = Items.MINECART;
					}

					itemStack = new ItemStack(item);
				} else if (entity instanceof BoatEntity) {
					itemStack = new ItemStack(((BoatEntity)entity).asItem());
				} else if (entity instanceof ArmorStandEntity) {
					itemStack = new ItemStack(Items.ARMOR_STAND);
				} else if (entity instanceof EnderCrystalEntity) {
					itemStack = new ItemStack(Items.END_CRYSTAL);
				} else {
					SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(entity.getType());
					if (spawnEggItem == null) {
						return;
					}

					itemStack = new ItemStack(spawnEggItem);
				}
			}

			if (itemStack.isEmpty()) {
				String string = "";
				if (type == HitResult.Type.BLOCK) {
					string = Registry.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
				} else if (type == HitResult.Type.ENTITY) {
					string = Registry.ENTITY_TYPE.getId(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", type, string);
			} else {
				PlayerInventory playerInventory = this.player.inventory;
				if (blockEntity != null) {
					this.addBlockEntityNbt(itemStack, blockEntity);
				}

				int i = playerInventory.getSlotWithStack(itemStack);
				if (bl) {
					playerInventory.addPickBlock(itemStack);
					this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.MAIN_HAND), 36 + playerInventory.selectedSlot);
				} else if (i != -1) {
					if (PlayerInventory.isValidHotbarIndex(i)) {
						playerInventory.selectedSlot = i;
					} else {
						this.interactionManager.pickFromInventory(i);
					}
				}
			}
		}
	}

	private ItemStack addBlockEntityNbt(ItemStack itemStack, BlockEntity blockEntity) {
		CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
		if (itemStack.getItem() instanceof SkullItem && compoundTag.containsKey("Owner")) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Owner");
			itemStack.getOrCreateTag().put("SkullOwner", compoundTag2);
			return itemStack;
		} else {
			itemStack.putSubTag("BlockEntityTag", compoundTag);
			CompoundTag compoundTag2 = new CompoundTag();
			ListTag listTag = new ListTag();
			listTag.add(new StringTag("\"(+NBT)\""));
			compoundTag2.put("Lore", listTag);
			itemStack.putSubTag("display", compoundTag2);
			return itemStack;
		}
	}

	public CrashReport populateCrashReport(CrashReport crashReport) {
		method_22681(this.languageManager, this.gameVersion, this.options, crashReport);
		if (this.world != null) {
			this.world.addDetailsToCrashReport(crashReport);
		}

		return crashReport;
	}

	public static void method_22681(@Nullable LanguageManager languageManager, String string, @Nullable GameOptions gameOptions, CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.getSystemDetailsSection();
		crashReportSection.add("Launched Version", (CrashCallable<String>)(() -> string));
		crashReportSection.add("Backend library", RenderSystem::getBackendDescription);
		crashReportSection.add("Backend API", RenderSystem::getApiDescription);
		crashReportSection.add("GL Caps", RenderSystem::getCapsString);
		crashReportSection.add("Using VBOs", (CrashCallable<String>)(() -> "Yes"));
		crashReportSection.add(
			"Is Modded",
			(CrashCallable<String>)(() -> {
				String stringx = ClientBrandRetriever.getClientModName();
				if (!"vanilla".equals(stringx)) {
					return "Definitely; Client brand changed to '" + stringx + "'";
				} else {
					return MinecraftClient.class.getSigners() == null
						? "Very likely; Jar signature invalidated"
						: "Probably not. Jar signature remains and client brand is untouched.";
				}
			})
		);
		crashReportSection.add("Type", "Client (map_client.txt)");
		if (gameOptions != null) {
			crashReportSection.add("Resource Packs", (CrashCallable<String>)(() -> {
				StringBuilder stringBuilder = new StringBuilder();

				for (String stringx : gameOptions.resourcePacks) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(stringx);
					if (gameOptions.incompatibleResourcePacks.contains(stringx)) {
						stringBuilder.append(" (incompatible)");
					}
				}

				return stringBuilder.toString();
			}));
		}

		if (languageManager != null) {
			crashReportSection.add("Current Language", (CrashCallable<String>)(() -> languageManager.getLanguage().toString()));
		}

		crashReportSection.add("CPU", GlDebugInfo::getCpuInfo);
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public CompletableFuture<Void> reloadResourcesConcurrently() {
		return this.supply(this::reloadResources).thenCompose(completableFuture -> completableFuture);
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addInfo("fps", currentFps);
		snooper.addInfo("vsync_enabled", this.options.enableVsync);
		snooper.addInfo("display_frequency", this.window.method_22092());
		snooper.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
		snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("current_action", this.getCurrentAction());
		snooper.addInfo("language", this.options.language == null ? "en_us" : this.options.language);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addInfo("endianness", string);
		snooper.addInfo("subtitles", this.options.showSubtitles);
		snooper.addInfo("touch", this.options.touchscreen ? "touch" : "mouse");
		int i = 0;

		for (ClientResourcePackContainer clientResourcePackContainer : this.resourcePackContainerManager.getEnabledContainers()) {
			if (!clientResourcePackContainer.canBeSorted() && !clientResourcePackContainer.isPositionFixed()) {
				snooper.addInfo("resource_pack[" + i++ + "]", clientResourcePackContainer.getName());
			}
		}

		snooper.addInfo("resource_packs", i);
		if (this.server != null) {
			snooper.addInfo("snooper_partner", this.server.getSnooper().getToken());
		}
	}

	private String getCurrentAction() {
		if (this.server != null) {
			return this.server.isRemote() ? "hosting_lan" : "singleplayer";
		} else if (this.currentServerEntry != null) {
			return this.currentServerEntry.isLocal() ? "playing_lan" : "multiplayer";
		} else {
			return "out_of_game";
		}
	}

	public void setCurrentServerEntry(@Nullable ServerInfo serverInfo) {
		this.currentServerEntry = serverInfo;
	}

	@Nullable
	public ServerInfo getCurrentServerEntry() {
		return this.currentServerEntry;
	}

	public boolean isInSingleplayer() {
		return this.isIntegratedServerRunning;
	}

	public boolean isIntegratedServerRunning() {
		return this.isIntegratedServerRunning && this.server != null;
	}

	@Nullable
	public IntegratedServer getServer() {
		return this.server;
	}

	public Snooper getSnooper() {
		return this.snooper;
	}

	public Session getSession() {
		return this.session;
	}

	public PropertyMap getSessionProperties() {
		if (this.sessionPropertyMap.isEmpty()) {
			GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
			this.sessionPropertyMap.putAll(gameProfile.getProperties());
		}

		return this.sessionPropertyMap;
	}

	public Proxy getNetworkProxy() {
		return this.netProxy;
	}

	public TextureManager getTextureManager() {
		return this.textureManager;
	}

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public ResourcePackContainerManager<ClientResourcePackContainer> getResourcePackContainerManager() {
		return this.resourcePackContainerManager;
	}

	public ClientResourcePackCreator getResourcePackDownloader() {
		return this.resourcePackCreator;
	}

	public File getResourcePackDir() {
		return this.resourcePackDir;
	}

	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}

	public SpriteAtlasTexture getSpriteAtlas() {
		return this.spriteAtlas;
	}

	public boolean is64Bit() {
		return this.is64Bit;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public MusicTracker.MusicType getMusicType() {
		if (this.currentScreen instanceof CreditsScreen) {
			return MusicTracker.MusicType.CREDITS;
		} else if (this.player == null) {
			return MusicTracker.MusicType.MENU;
		} else if (this.player.world.dimension instanceof TheNetherDimension) {
			return MusicTracker.MusicType.NETHER;
		} else if (this.player.world.dimension instanceof TheEndDimension) {
			return this.inGameHud.getBossBarHud().shouldPlayDragonMusic() ? MusicTracker.MusicType.END_BOSS : MusicTracker.MusicType.END;
		} else {
			Biome.Category category = this.player.world.getBiome(new BlockPos(this.player)).getCategory();
			if (!this.musicTracker.isPlayingType(MusicTracker.MusicType.UNDER_WATER)
				&& (
					!this.player.isInWater()
						|| this.musicTracker.isPlayingType(MusicTracker.MusicType.GAME)
						|| category != Biome.Category.OCEAN && category != Biome.Category.RIVER
				)) {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.CREATIVE : MusicTracker.MusicType.GAME;
			} else {
				return MusicTracker.MusicType.UNDER_WATER;
			}
		}
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public PlayerSkinProvider getSkinProvider() {
		return this.skinProvider;
	}

	@Nullable
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}

	public void setCameraEntity(Entity entity) {
		this.cameraEntity = entity;
		this.gameRenderer.onCameraEntitySet(entity);
	}

	@Override
	protected Thread getThread() {
		return this.thread;
	}

	@Override
	protected Runnable createTask(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean canExecute(Runnable runnable) {
		return true;
	}

	public BlockRenderManager getBlockRenderManager() {
		return this.blockRenderManager;
	}

	public EntityRenderDispatcher getEntityRenderManager() {
		return this.entityRenderManager;
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	public FirstPersonRenderer getFirstPersonRenderer() {
		return this.firstPersonRenderer;
	}

	public <T> SearchableContainer<T> getSearchableContainer(SearchManager.Key<T> key) {
		return this.searchManager.get(key);
	}

	public static int getCurrentFps() {
		return currentFps;
	}

	public MetricsData getMetricsData() {
		return this.metricsData;
	}

	public boolean isConnectedToRealms() {
		return this.connectedToRealms;
	}

	public void setConnectedToRealms(boolean bl) {
		this.connectedToRealms = bl;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public float getTickDelta() {
		return this.renderTickCounter.tickDelta;
	}

	public float getLastFrameDuration() {
		return this.renderTickCounter.lastFrameDuration;
	}

	public BlockColors getBlockColorMap() {
		return this.blockColorMap;
	}

	public boolean hasReducedDebugInfo() {
		return this.player != null && this.player.getReducedDebugInfo() || this.options.reducedDebugInfo;
	}

	public ToastManager getToastManager() {
		return this.toastManager;
	}

	public TutorialManager getTutorialManager() {
		return this.tutorialManager;
	}

	public boolean isWindowFocused() {
		return this.windowFocused;
	}

	public HotbarStorage getCreativeHotbarStorage() {
		return this.creativeHotbarStorage;
	}

	public BakedModelManager getBakedModelManager() {
		return this.bakedModelManager;
	}

	public FontManager getFontManager() {
		return this.fontManager;
	}

	public PaintingManager getPaintingManager() {
		return this.paintingManager;
	}

	public StatusEffectSpriteManager getStatusEffectSpriteManager() {
		return this.statusEffectSpriteManager;
	}

	@Override
	public void onWindowFocusChanged(boolean bl) {
		this.windowFocused = bl;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public MinecraftClientGame getGame() {
		return this.game;
	}

	public SplashTextResourceSupplier getSplashTextLoader() {
		return this.splashTextLoader;
	}

	@Nullable
	public Overlay getOverlay() {
		return this.overlay;
	}

	public boolean shouldRenderAsync() {
		return false;
	}

	public Window method_22683() {
		return this.window;
	}
}
