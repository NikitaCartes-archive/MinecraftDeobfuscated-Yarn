package net.minecraft.client;

import com.google.common.collect.Queues;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
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
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.class_4060;
import net.minecraft.class_4071;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.audio.MusicTracker;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.ContainerScreenRegistry;
import net.minecraft.client.gui.FocusedInputListener;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.SplashScreen;
import net.minecraft.client.gui.WorldGenerationProgressScreen;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;
import net.minecraft.client.gui.ingame.DeathScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.ingame.SleepingChatScreen;
import net.minecraft.client.gui.menu.AdvancementsScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.OutOfMemoryScreen;
import net.minecraft.client.gui.menu.PauseMenuScreen;
import net.minecraft.client.gui.menu.ServerConnectingScreen;
import net.minecraft.client.gui.menu.WorkingScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemColorMap;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.RedirectedResourcePack;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sortme.PlayerSkinProvider;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.NarratorManager;
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
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.block.SkullItem;
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
import net.minecraft.text.KeybindTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Session;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.ThreadTaskQueue;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
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
public class MinecraftClient extends ThreadTaskQueue<Runnable> implements SnooperListener, WindowEventHandler, FocusedInputListener, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean IS_SYSTEM_MAC = SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.MAC;
	public static final Identifier DEFAULT_TEXT_RENDERER_ID = new Identifier("default");
	public static final Identifier ALT_TEXT_RENDERER_ID = new Identifier("alt");
	public static CompletableFuture<net.minecraft.util.Void> voidFuture = CompletableFuture.completedFuture(net.minecraft.util.Void.INSTANCE);
	public static byte[] memoryReservedForCrash = new byte[10485760];
	private static int cachedMaxTextureSize = -1;
	private final File resourcePackDir;
	private final PropertyMap sessionPropertyMap;
	private final WindowSettings windowSettings;
	private ServerEntry currentServerEntry;
	private TextureManager textureManager;
	private static MinecraftClient instance;
	private final DataFixer dataFixer;
	public ClientPlayerInteractionManager interactionManager;
	private WindowProvider windowProvider;
	public Window window;
	private boolean crashed;
	private CrashReport crashReport;
	private boolean connectedToRealms;
	private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0F, 0L);
	private final Snooper snooper = new Snooper("client", this, SystemUtil.getMeasuringTimeMs());
	public ClientWorld world;
	public WorldRenderer worldRenderer;
	private EntityRenderDispatcher entityRenderManager;
	private ItemRenderer itemRenderer;
	private FirstPersonRenderer firstPersonRenderer;
	public ClientPlayerEntity player;
	@Nullable
	public Entity cameraEntity;
	@Nullable
	public Entity targetedEntity;
	public ParticleManager particleManager;
	private final SearchManager searchManager = new SearchManager();
	private final Session session;
	private boolean isPaused;
	private float pausedTickDelta;
	public TextRenderer textRenderer;
	@Nullable
	public Screen currentScreen;
	@Nullable
	public class_4071 field_18175;
	public GameRenderer gameRenderer;
	public DebugRenderer debugRenderer;
	protected int attackCooldown;
	@Nullable
	private IntegratedServer server;
	private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
	public InGameHud inGameHud;
	public boolean skipGameRender;
	public HitResult hitResult;
	public GameOptions options;
	private HotbarStorage creativeHotbarStorage;
	public Mouse mouse;
	public Keyboard keyboard;
	public final File runDirectory;
	private final File assetDirectory;
	private final String gameVersion;
	private final String versionType;
	private final Proxy netProxy;
	private LevelStorage levelStorage;
	private static int currentFps;
	private int itemUseCooldown;
	private String autoConnectServerIp;
	private int autoConnectServerPort;
	public final MetricsData metricsData = new MetricsData();
	private long lastMetricsSampleTime = SystemUtil.getMeasuringTimeNano();
	private final boolean is64Bit;
	private final boolean isDemo;
	@Nullable
	private ClientConnection clientConnection;
	private boolean isIntegratedServerRunning;
	private final DisableableProfiler profiler = new DisableableProfiler(() -> this.renderTickCounter.ticksThisFrame);
	private ReloadableResourceManager resourceManager;
	private final ClientResourcePackCreator resourcePackCreator;
	private final ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;
	private LanguageManager languageManager;
	private BlockColorMap blockColorMap;
	private ItemColorMap itemColorMap;
	private GlFramebuffer framebuffer;
	private SpriteAtlasTexture spriteAtlas;
	private SoundLoader soundLoader;
	private MusicTracker musicTracker;
	private FontManager fontManager;
	private SplashTextResourceSupplier splashTextLoader;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider skinProvider;
	private final Thread thread = Thread.currentThread();
	private BakedModelManager bakedModelManager;
	private BlockRenderManager blockRenderManager;
	private PaintingManager paintingManager;
	private StatusEffectSpriteManager field_18173;
	private final ToastManager toastManager;
	private final MinecraftClientGame game = new MinecraftClientGame(this);
	private volatile boolean isRunning = true;
	public String fpsDebugString = "";
	public boolean field_1730 = true;
	private long nextDebugInfoUpdateTime;
	private int fpsCounter;
	private final TutorialManager tutorialManager;
	private boolean isWindowFocused;
	private final Queue<Runnable> renderTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private CompletableFuture<Void> field_18174;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		this.windowSettings = runArgs.windowSettings;
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		this.assetDirectory = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.versionType = runArgs.game.versionType;
		this.sessionPropertyMap = runArgs.network.profileProperties;
		this.resourcePackCreator = new ClientResourcePackCreator(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.getResourceIndex());
		this.resourcePackContainerManager = new ResourcePackContainerManager<>((string, bl, supplier, resourcePack, packResourceMetadata, sortingDirection) -> {
			Supplier<ResourcePack> supplier2;
			if (packResourceMetadata.getPackFormat() < SharedConstants.getGameVersion().getPackVersion()) {
				supplier2 = () -> new RedirectedResourcePack((ResourcePack)supplier.get(), RedirectedResourcePack.NEW_TO_OLD_MAP);
			} else {
				supplier2 = supplier;
			}

			return new ClientResourcePackContainer(string, bl, supplier2, resourcePack, packResourceMetadata, sortingDirection);
		});
		this.resourcePackContainerManager.addCreator(this.resourcePackCreator);
		this.resourcePackContainerManager.addCreator(new FileResourcePackCreator(this.resourcePackDir));
		this.netProxy = runArgs.network.netProxy == null ? Proxy.NO_PROXY : runArgs.network.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = runArgs.network.session;
		LOGGER.info("Setting user: {}", this.session.getUsername());
		LOGGER.debug("(Session ID is {})", this.session.getSessionId());
		this.isDemo = runArgs.game.demo;
		this.is64Bit = checkIs64Bit();
		this.server = null;
		if (runArgs.autoConnect.serverIP != null) {
			this.autoConnectServerIp = runArgs.autoConnect.serverIP;
			this.autoConnectServerPort = runArgs.autoConnect.serverPort;
		}

		Bootstrap.initialize();
		Bootstrap.logMissingTranslations();
		KeybindTextComponent.field_11766 = KeyBinding::method_1419;
		this.dataFixer = Schemas.getFixer();
		this.toastManager = new ToastManager(this);
		this.tutorialManager = new TutorialManager(this);
	}

	public void start() {
		this.isRunning = true;

		try {
			this.init();
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Initializing game");
			crashReport.addElement("Initialization");
			this.printCrashReport(this.populateCrashReport(crashReport));
			return;
		}

		try {
			try {
				while (this.isRunning) {
					if (this.crashed && this.crashReport != null) {
						this.printCrashReport(this.crashReport);
						return;
					} else {
						try {
							this.render(true);
						} catch (OutOfMemoryError var9) {
							this.cleanUpAfterCrash();
							this.openScreen(new OutOfMemoryScreen());
							System.gc();
						}
					}
				}

				return;
			} catch (CrashException var11) {
				this.populateCrashReport(var11.getReport());
				this.cleanUpAfterCrash();
				LOGGER.fatal("Reported exception thrown!", (Throwable)var11);
				this.printCrashReport(var11.getReport());
			} catch (Throwable var12) {
				CrashReport crashReport = this.populateCrashReport(new CrashReport("Unexpected error", var12));
				LOGGER.fatal("Unreported exception thrown!", var12);
				this.cleanUpAfterCrash();
				this.printCrashReport(crashReport);
			}
		} finally {
			this.stop();
		}
	}

	private void init() {
		this.options = new GameOptions(this, this.runDirectory);
		this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
		this.startTimerHackThread();
		LOGGER.info("LWJGL Version: {}", GLX.getLWJGLVersion());
		WindowSettings windowSettings = this.windowSettings;
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			windowSettings = new WindowSettings(
				this.options.overrideWidth, this.options.overrideHeight, windowSettings.fullscreenWidth, windowSettings.fullscreenHeight, windowSettings.fullscreen
			);
		}

		LongSupplier longSupplier = GLX.initGlfw();
		if (longSupplier != null) {
			SystemUtil.nanoTimeSupplier = longSupplier;
		}

		this.windowProvider = new WindowProvider(this);
		this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
		this.onWindowFocusChanged(true);

		try {
			InputStream inputStream = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_16x16.png"));
			InputStream inputStream2 = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_32x32.png"));
			this.window.setIcon(inputStream, inputStream2);
		} catch (IOException var6) {
			LOGGER.error("Couldn't set icon", (Throwable)var6);
		}

		this.window.setFramerateLimit(this.options.maxFps);
		this.mouse = new Mouse(this);
		this.mouse.setup(this.window.getHandle());
		this.keyboard = new Keyboard(this);
		this.keyboard.setup(this.window.getHandle());
		GLX.init();
		GlDebug.enableDebug(this.options.glDebugVerbosity, false);
		this.framebuffer = new GlFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, IS_SYSTEM_MAC);
		this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.ASSETS, this.thread);
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
		this.onResolutionChanged();
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
		this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
		this.soundLoader = new SoundLoader(this.options);
		this.resourceManager.registerListener(this.soundLoader);
		this.splashTextLoader = new SplashTextResourceSupplier();
		this.resourceManager.registerListener(this.splashTextLoader);
		this.musicTracker = new MusicTracker(this);
		this.fontManager = new FontManager(this.textureManager, this.forcesUnicodeFont());
		this.resourceManager.registerListener(this.fontManager.method_18627());
		this.textRenderer = this.fontManager.getTextRenderer(DEFAULT_TEXT_RENDERER_ID);
		if (this.options.language != null) {
			this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
		}

		this.resourceManager.registerListener(new GrassColormapResourceSupplier());
		this.resourceManager.registerListener(new FoliageColormapResourceSupplier());
		this.window.setPhase("Startup");
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(GlStateManager.FaceSides.field_5070);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.window.setPhase("Post startup");
		this.spriteAtlas = new SpriteAtlasTexture("textures");
		this.spriteAtlas.setMipLevel(this.options.mipmapLevels);
		this.textureManager.registerTextureUpdateable(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.spriteAtlas);
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.spriteAtlas.setFilter(false, this.options.mipmapLevels > 0);
		this.bakedModelManager = new BakedModelManager(this.spriteAtlas);
		this.resourceManager.registerListener(this.bakedModelManager);
		this.blockColorMap = BlockColorMap.create();
		this.itemColorMap = ItemColorMap.create(this.blockColorMap);
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
		GlStateManager.viewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.particleManager = new ParticleManager(this.world, this.textureManager);
		this.resourceManager.registerListener(this.particleManager);
		this.paintingManager = new PaintingManager(this.textureManager);
		this.resourceManager.registerListener(this.paintingManager);
		this.field_18173 = new StatusEffectSpriteManager(this.textureManager);
		this.resourceManager.registerListener(this.field_18173);
		this.inGameHud = new InGameHud(this);
		this.debugRenderer = new DebugRenderer(this);
		GLX.setGlfwErrorCallback(this::handleGlErrorByDisableVsync);
		if (this.options.fullscreen && !this.window.isFullscreen()) {
			this.window.toggleFullscreen();
			this.options.fullscreen = this.window.isFullscreen();
		}

		this.window.setVsync(this.options.enableVsync);
		this.window.logOnGlError();
		if (this.autoConnectServerIp != null) {
			this.openScreen(new ServerConnectingScreen(new MainMenuScreen(), this, this.autoConnectServerIp, this.autoConnectServerPort));
		} else {
			this.openScreen(new MainMenuScreen(true));
		}

		SplashScreen.method_18819(this);
		this.method_18502(
			new SplashScreen(
				this,
				this.resourceManager
					.beginInitialMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, CompletableFuture.completedFuture(net.minecraft.util.Void.INSTANCE)),
				() -> {
					if (SharedConstants.isDevelopment) {
						this.checkGameData();
					}
				},
				false
			)
		);
	}

	private void initializeSearchableContainers() {
		TextSearchableContainer<ItemStack> textSearchableContainer = new TextSearchableContainer<>(
			itemStack -> itemStack.getTooltipText(null, TooltipContext.Default.NORMAL)
					.stream()
					.map(textComponent -> TextFormat.stripFormatting(textComponent.getString()).trim())
					.filter(string -> !string.isEmpty()),
			itemStack -> Stream.of(Registry.ITEM.getId(itemStack.getItem()))
		);
		IdentifierSearchableContainer<ItemStack> identifierSearchableContainer = new IdentifierSearchableContainer<>(
			itemStack -> ItemTags.getContainer().getTagsFor(itemStack.getItem()).stream()
		);
		DefaultedList<ItemStack> defaultedList = DefaultedList.create();

		for (Item item : Registry.ITEM) {
			item.appendItemsForGroup(ItemGroup.SEARCH, defaultedList);
		}

		defaultedList.forEach(itemStack -> {
			textSearchableContainer.add(itemStack);
			identifierSearchableContainer.add(itemStack);
		});
		TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<>(
			recipeResultCollection -> recipeResultCollection.getAllRecipes()
					.stream()
					.flatMap(recipe -> recipe.getOutput().getTooltipText(null, TooltipContext.Default.NORMAL).stream())
					.map(textComponent -> TextFormat.stripFormatting(textComponent.getString()).trim())
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
				while (MinecraftClient.this.isRunning) {
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
		this.crashed = true;
		this.crashReport = crashReport;
	}

	public void printCrashReport(CrashReport crashReport) {
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
		if (this.field_18174 != null) {
			return this.field_18174;
		} else {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			if (this.field_18175 instanceof SplashScreen) {
				this.field_18174 = completableFuture;
				return completableFuture;
			} else {
				this.resourcePackContainerManager.callCreators();
				List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
					.getEnabledContainers()
					.stream()
					.map(ResourcePackContainer::createResourcePack)
					.collect(Collectors.toList());
				this.method_18502(new SplashScreen(this, this.resourceManager.beginMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, voidFuture, list), () -> {
					this.languageManager.reloadResources(list);
					if (this.worldRenderer != null) {
						this.worldRenderer.reload();
					}

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
				if (blockState.getRenderType() == BlockRenderType.field_11458) {
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

		DefaultedList<ItemStack> defaultedList = DefaultedList.create();

		for (Item item : Registry.ITEM) {
			defaultedList.clear();
			item.appendItemsForGroup(ItemGroup.SEARCH, defaultedList);

			for (ItemStack itemStack : defaultedList) {
				String string = new TranslatableTextComponent(itemStack.getTranslationKey()).getString();
				if (string.toLowerCase(Locale.ROOT).equals(string)) {
					LOGGER.debug("Missing translation for: {} {} {}", itemStack, itemStack.getTranslationKey(), itemStack.getItem());
					bl = true;
				}
			}
		}

		bl |= ContainerScreenRegistry.checkData();
		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	@Nullable
	@Override
	public InputListener getFocused() {
		return this.field_18175 == null ? this.currentScreen : null;
	}

	public void openScreen(@Nullable Screen screen) {
		if (this.currentScreen != null) {
			this.currentScreen.onClosed();
		}

		if (screen == null && this.world == null) {
			screen = new MainMenuScreen();
		} else if (screen == null && this.player.getHealth() <= 0.0F) {
			screen = new DeathScreen(null);
		}

		if (screen instanceof MainMenuScreen || screen instanceof MultiplayerScreen) {
			this.options.debugEnabled = false;
			this.inGameHud.getChatHud().clear(true);
		}

		this.currentScreen = screen;
		if (screen != null) {
			this.mouse.unlockCursor();
			KeyBinding.unpressAll();
			screen.initialize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
			this.skipGameRender = false;
		} else {
			this.soundLoader.resume();
			this.mouse.lockCursor();
		}
	}

	public void method_18502(@Nullable class_4071 arg) {
		this.field_18175 = arg;
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");

			try {
				if (this.world != null) {
					this.world.disconnect();
				}

				this.openWorkingScreen();
			} catch (Throwable var5) {
			}

			if (this.currentScreen != null) {
				this.currentScreen.onClosed();
			}

			this.close();
		} finally {
			SystemUtil.nanoTimeSupplier = System::nanoTime;
			if (!this.crashed) {
				System.exit(0);
			}
		}
	}

	public void close() {
		try {
			this.spriteAtlas.clear();
			this.textRenderer.close();
			this.fontManager.close();
			this.gameRenderer.close();
			this.worldRenderer.close();
			this.soundLoader.deinitialize();
			this.resourcePackContainerManager.close();
			this.particleManager.method_18829();
			this.field_18173.close();
			this.paintingManager.close();
			SystemUtil.method_18350();
		} finally {
			this.windowProvider.close();
			this.window.close();
		}
	}

	private void render(boolean bl) {
		this.window.setPhase("Pre render");
		long l = SystemUtil.getMeasuringTimeNano();
		this.profiler.startTick();
		if (GLX.shouldClose(this.window)) {
			this.scheduleStop();
		}

		if (this.field_18174 != null && !(this.field_18175 instanceof SplashScreen)) {
			CompletableFuture<Void> completableFuture = this.field_18174;
			this.field_18174 = null;
			this.reloadResources().thenRun(() -> completableFuture.complete(null));
		}

		Runnable runnable;
		while ((runnable = (Runnable)this.renderTaskQueue.poll()) != null) {
			runnable.run();
		}

		if (bl) {
			this.renderTickCounter.method_1658(SystemUtil.getMeasuringTimeMs());
			this.profiler.push("scheduledExecutables");
			this.executeTaskQueue();
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
		GLX.pollEvents();
		long n = SystemUtil.getMeasuringTimeNano() - m;
		this.profiler.swap("sound");
		this.soundLoader.updateListenerPosition(this.player, this.renderTickCounter.tickDelta);
		this.profiler.pop();
		this.profiler.push("render");
		GlStateManager.pushMatrix();
		GlStateManager.clear(16640, IS_SYSTEM_MAC);
		this.framebuffer.beginWrite(true);
		this.profiler.push("display");
		GlStateManager.enableTexture();
		this.profiler.pop();
		if (!this.skipGameRender) {
			this.profiler.swap("gameRenderer");
			this.gameRenderer.render(this.isPaused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, bl);
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
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.gameRenderer.method_3200(this.renderTickCounter.tickDelta);
		GlStateManager.popMatrix();
		this.profiler.startTick();
		this.updateDisplay(true);
		Thread.yield();
		this.window.setPhase("Post render");
		this.fpsCounter++;
		boolean bl2 = this.isIntegratedServerRunning()
			&& (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.field_18175 != null && this.field_18175.method_18640())
			&& !this.server.isRemote();
		if (this.isPaused != bl2) {
			if (this.isPaused) {
				this.pausedTickDelta = this.renderTickCounter.tickDelta;
			} else {
				this.renderTickCounter.tickDelta = this.pausedTickDelta;
			}

			this.isPaused = bl2;
		}

		long o = SystemUtil.getMeasuringTimeNano();
		this.metricsData.pushSample(o - this.lastMetricsSampleTime);
		this.lastMetricsSampleTime = o;

		while (SystemUtil.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				currentFps,
				ChunkRenderer.chunkUpdateCount,
				ChunkRenderer.chunkUpdateCount == 1 ? "" : "s",
				(double)this.options.maxFps == GameOption.FRAMERATE_LIMIT.method_18617() ? "inf" : this.options.maxFps,
				this.options.enableVsync ? " vsync" : "",
				this.options.fancyGraphics ? "" : " fast",
				this.options.cloudRenderMode == CloudRenderMode.field_18162
					? ""
					: (this.options.cloudRenderMode == CloudRenderMode.field_18163 ? " fast-clouds" : " fancy-clouds"),
				GLX.useVbo() ? " vbo" : ""
			);
			ChunkRenderer.chunkUpdateCount = 0;
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
			this.currentScreen.onScaleChanged(this, this.window.getScaledWidth(), this.window.getScaledHeight());
		}

		GlFramebuffer glFramebuffer = this.getFramebuffer();
		if (glFramebuffer != null) {
			glFramebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
		}

		if (this.gameRenderer != null) {
			this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		}

		if (this.mouse != null) {
			this.mouse.onResolutionChanged();
		}
	}

	private int getFramerateLimit() {
		return this.world != null || this.currentScreen == null && this.field_18175 == null ? this.window.getFramerateLimit() : 60;
	}

	private boolean isFramerateLimited() {
		return (double)this.getFramerateLimit() < GameOption.FRAMERATE_LIMIT.method_18617();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.worldRenderer.method_3267();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			if (this.isIntegratedServerRunning()) {
				this.server.stop(true);
			}

			this.method_18096(new CloseWorldScreen(I18n.translate("menu.savingLevel")));
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
			GlStateManager.clear(256, IS_SYSTEM_MAC);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)this.window.getFramebufferWidth(), (double)this.window.getFramebufferHeight(), 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
			GlStateManager.lineWidth(1.0F);
			GlStateManager.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int i = 160;
			int j = this.window.getFramebufferWidth() - 160 - 10;
			int k = this.window.getFramebufferHeight() - 320;
			GlStateManager.enableBlend();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j - 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)(k + 320), 0.0).color(200, 0, 0, 0).next();
			bufferBuilder.vertex((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).next();
			tessellator.draw();
			GlStateManager.disableBlend();
			double d = 0.0;

			for (int l = 0; l < list.size(); l++) {
				ProfilerTiming profilerTiming2 = (ProfilerTiming)list.get(l);
				int m = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				int n = profilerTiming2.getColor();
				int o = n >> 16 & 0xFF;
				int p = n >> 8 & 0xFF;
				int q = n & 0xFF;
				bufferBuilder.vertex((double)j, (double)k, 0.0).color(o, p, q, 255).next();

				for (int r = m; r >= 0; r--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)r / (double)m) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 160.0F;
					float h = MathHelper.cos(f) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(o, p, q, 255).next();
				}

				tessellator.draw();
				bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

				for (int r = m; r >= 0; r--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)r / (double)m) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 160.0F;
					float h = MathHelper.cos(f) * 160.0F * 0.5F;
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
					bufferBuilder.vertex((double)((float)j + g), (double)((float)k - h + 10.0F), 0.0).color(o >> 1, p >> 1, q >> 1, 255).next();
				}

				tessellator.draw();
				d += profilerTiming2.parentSectionUsagePercentage;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
			GlStateManager.enableTexture();
			String string = "";
			if (!"unspecified".equals(profilerTiming.name)) {
				string = string + "[0] ";
			}

			if (profilerTiming.name.isEmpty()) {
				string = string + "ROOT ";
			} else {
				string = string + profilerTiming.name + ' ';
			}

			int m = 16777215;
			this.textRenderer.drawWithShadow(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			this.textRenderer.drawWithShadow(string, (float)(j + 160 - this.textRenderer.getStringWidth(string)), (float)(k - 80 - 16), 16777215);

			for (int s = 0; s < list.size(); s++) {
				ProfilerTiming profilerTiming3 = (ProfilerTiming)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string2 = stringBuilder.append(profilerTiming3.name).toString();
				this.textRenderer.drawWithShadow(string2, (float)(j - 160), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
				this.textRenderer
					.drawWithShadow(string2, (float)(j + 160 - 50 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
				this.textRenderer
					.drawWithShadow(string2, (float)(j + 160 - this.textRenderer.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
			}
		}
	}

	public void scheduleStop() {
		this.isRunning = false;
	}

	public void openPauseMenu() {
		if (this.currentScreen == null) {
			this.openScreen(new PauseMenuScreen());
			if (this.isIntegratedServerRunning() && !this.server.isRemote()) {
				this.soundLoader.pause();
			}
		}
	}

	private void method_1590(boolean bl) {
		if (!bl) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
			if (bl && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.world.getBlockState(blockPos).isAir()) {
					Direction direction = blockHitResult.getSide();
					if (this.interactionManager.method_2902(blockPos, direction)) {
						this.particleManager.addBlockBreakingParticles(blockPos, direction);
						this.player.swingHand(Hand.MAIN);
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
			} else if (!this.player.method_3144()) {
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
					case NONE:
						if (this.interactionManager.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}

						this.player.method_7350();
				}

				this.player.swingHand(Hand.MAIN);
			}
		}
	}

	private void doItemUse() {
		if (!this.interactionManager.isBreakingBlock()) {
			this.itemUseCooldown = 4;
			if (!this.player.method_3144()) {
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
								if (this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand) == ActionResult.field_5812) {
									return;
								}

								if (this.interactionManager.interactEntity(this.player, entity, hand) == ActionResult.field_5812) {
									return;
								}
								break;
							case BLOCK:
								BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
								int i = itemStack.getAmount();
								ActionResult actionResult = this.interactionManager.interactBlock(this.player, this.world, hand, blockHitResult);
								if (actionResult == ActionResult.field_5812) {
									this.player.swingHand(hand);
									if (!itemStack.isEmpty() && (itemStack.getAmount() != i || this.interactionManager.hasCreativeInventory())) {
										this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
									}

									return;
								}

								if (actionResult == ActionResult.field_5814) {
									return;
								}
						}
					}

					if (!itemStack.isEmpty() && this.interactionManager.interactItem(this.player, this.world, hand) == ActionResult.field_5812) {
						this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
						return;
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
		if (!this.isPaused) {
			this.inGameHud.tick();
		}

		this.profiler.pop();
		this.gameRenderer.updateTargetedEntity(1.0F);
		this.tutorialManager.method_4911(this.world, this.hitResult);
		this.profiler.push("gameMode");
		if (!this.isPaused && this.world != null) {
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
			Screen.method_2217(() -> this.currentScreen.update(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
		}

		if (!this.options.debugEnabled) {
			this.inGameHud.resetDebugHudChunk();
		}

		if (this.field_18175 == null && (this.currentScreen == null || this.currentScreen.field_2558)) {
			this.profiler.swap("GLFW events");
			GLX.pollEvents();
			this.handleInputEvents();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
		}

		if (this.world != null) {
			this.profiler.swap("gameRenderer");
			if (!this.isPaused) {
				this.gameRenderer.tick();
			}

			this.profiler.swap("levelRenderer");
			if (!this.isPaused) {
				this.worldRenderer.tick();
			}

			this.profiler.swap("level");
			if (!this.isPaused) {
				if (this.world.getTicksSinceLightning() > 0) {
					this.world.setTicksSinceLightning(this.world.getTicksSinceLightning() - 1);
				}

				this.world.method_18116();
			}
		} else if (this.gameRenderer.method_3175()) {
			this.gameRenderer.disableShader();
		}

		if (!this.isPaused) {
			this.musicTracker.method_18669();
			this.soundLoader.update();
		}

		if (this.world != null) {
			if (!this.isPaused) {
				this.world.setMobSpawnOptions(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
				this.tutorialManager.tick();

				try {
					this.world.method_8441(() -> true);
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
			if (!this.isPaused && this.world != null) {
				this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
			}

			this.profiler.swap("particles");
			if (!this.isPaused) {
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

			this.worldRenderer.method_3292();
		}

		while (this.options.keySmoothCamera.wasPressed()) {
			this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.options.keySaveToolbarActivator.isPressed();
			boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
			if (this.options.keysHotbar[i].wasPressed()) {
				if (this.player.isSpectator()) {
					this.inGameHud.getSpectatorWidget().onHotbarKeyPress(i);
				} else if (!this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
					this.player.inventory.selectedSlot = i;
				} else {
					CreativePlayerInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
				}
			}
		}

		while (this.options.keyInventory.wasPressed()) {
			if (this.interactionManager.hasRidingInventory()) {
				this.player.openRidingInventory();
			} else {
				this.tutorialManager.onInventoryOpened();
				this.openScreen(new PlayerInventoryScreen(this.player));
			}
		}

		while (this.options.keyAdvancements.wasPressed()) {
			this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
		}

		while (this.options.keySwapHands.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12969, BlockPos.ORIGIN, Direction.DOWN));
			}
		}

		while (this.options.keyDrop.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.player.dropSelectedItem(Screen.isControlPressed());
			}
		}

		boolean bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
		if (bl3) {
			while (this.options.keyChat.wasPressed()) {
				this.openScreen(new ChatScreen());
			}

			if (this.currentScreen == null && this.field_18175 == null && this.options.keyCommand.wasPressed()) {
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

		this.method_1590(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
	}

	public void startIntegratedServer(String string, String string2, @Nullable LevelInfo levelInfo) {
		this.openWorkingScreen();
		WorldSaveHandler worldSaveHandler = this.levelStorage.method_242(string, null);
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

		WorldGenerationProgressScreen worldGenerationProgressScreen = new WorldGenerationProgressScreen(
			(WorldGenerationProgressTracker)this.worldGenProgressTracker.get()
		);
		this.openScreen(worldGenerationProgressScreen);

		while (!this.server.method_3820()) {
			worldGenerationProgressScreen.update();
			this.render(false);

			try {
				Thread.sleep(16L);
			} catch (InterruptedException var10) {
			}

			if (this.crashed && this.crashReport != null) {
				this.printCrashReport(this.crashReport);
				return;
			}
		}

		SocketAddress socketAddress = this.server.getNetworkIO().method_14353();
		ClientConnection clientConnection = ClientConnection.connect(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, textComponent -> {
		}));
		clientConnection.sendPacket(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.sendPacket(new LoginHelloC2SPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void method_1481(ClientWorld clientWorld) {
		WorkingScreen workingScreen = new WorkingScreen();
		workingScreen.method_15412(new TranslatableTextComponent("connect.joining"));
		this.method_18098(workingScreen);
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

	public void openWorkingScreen() {
		this.method_18096(new WorkingScreen());
	}

	public void method_18096(Screen screen) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			this.taskQueue.clear();
			clientPlayNetworkHandler.method_2868();
		}

		IntegratedServer integratedServer = this.server;
		this.server = null;
		this.gameRenderer.method_3203();
		this.interactionManager = null;
		NarratorManager.INSTANCE.clear();
		this.method_18098(screen);
		if (this.world != null) {
			if (integratedServer != null) {
				while (!integratedServer.isServerThreadAlive()) {
					this.render(false);
				}
			}

			this.resourcePackCreator.clear();
			this.inGameHud.clear();
			this.setCurrentServerEntry(null);
			this.isIntegratedServerRunning = false;
			this.game.onLeaveGameSession();
		}

		this.world = null;
		this.setWorld(null);
		this.player = null;
	}

	private void method_18098(Screen screen) {
		this.musicTracker.stop();
		this.soundLoader.stopAll();
		this.cameraEntity = null;
		this.clientConnection = null;
		this.openScreen(screen);
		this.render(false);
	}

	private void setWorld(@Nullable ClientWorld clientWorld) {
		if (this.worldRenderer != null) {
			this.worldRenderer.setWorld(clientWorld);
		}

		if (this.particleManager != null) {
			this.particleManager.setWorld(clientWorld);
		}

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
		return instance == null || !instance.options.hudHidden;
	}

	public static boolean isFancyGraphicsEnabled() {
		return instance != null && instance.options.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance != null && instance.options.ao != class_4060.field_18144;
	}

	private void doItemPick() {
		if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.NONE) {
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

				if (bl && Screen.isControlPressed() && block.hasBlockEntity()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}
			} else {
				if (type != HitResult.Type.ENTITY || !bl) {
					return;
				}

				Entity entity = ((EntityHitResult)this.hitResult).getEntity();
				if (entity instanceof PaintingEntity) {
					itemStack = new ItemStack(Items.field_8892);
				} else if (entity instanceof LeadKnotEntity) {
					itemStack = new ItemStack(Items.field_8719);
				} else if (entity instanceof ItemFrameEntity) {
					ItemFrameEntity itemFrameEntity = (ItemFrameEntity)entity;
					ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
					if (itemStack2.isEmpty()) {
						itemStack = new ItemStack(Items.field_8143);
					} else {
						itemStack = itemStack2.copy();
					}
				} else if (entity instanceof AbstractMinecartEntity) {
					AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
					Item item;
					switch (abstractMinecartEntity.getMinecartType()) {
						case field_7679:
							item = Items.field_8063;
							break;
						case field_7678:
							item = Items.field_8388;
							break;
						case field_7675:
							item = Items.field_8069;
							break;
						case field_7677:
							item = Items.field_8836;
							break;
						case field_7681:
							item = Items.field_8220;
							break;
						default:
							item = Items.field_8045;
					}

					itemStack = new ItemStack(item);
				} else if (entity instanceof BoatEntity) {
					itemStack = new ItemStack(((BoatEntity)entity).asItem());
				} else if (entity instanceof ArmorStandEntity) {
					itemStack = new ItemStack(Items.field_8694);
				} else if (entity instanceof EnderCrystalEntity) {
					itemStack = new ItemStack(Items.field_8301);
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
					this.interactionManager.method_2909(this.player.getStackInHand(Hand.MAIN), 36 + playerInventory.selectedSlot);
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
			itemStack.setChildTag("BlockEntityTag", compoundTag);
			CompoundTag compoundTag2 = new CompoundTag();
			ListTag listTag = new ListTag();
			listTag.add(new StringTag("\"(+NBT)\""));
			compoundTag2.put("Lore", listTag);
			itemStack.setChildTag("display", compoundTag2);
			return itemStack;
		}
	}

	public CrashReport populateCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.getSystemDetailsSection();
		crashReportSection.add("Launched Version", (ICrashCallable<String>)(() -> this.gameVersion));
		crashReportSection.add("LWJGL", GLX::getLWJGLVersion);
		crashReportSection.add("OpenGL", GLX::getOpenGLVersionString);
		crashReportSection.add("GL Caps", GLX::getCapsString);
		crashReportSection.add("Using VBOs", (ICrashCallable<String>)(() -> "Yes"));
		crashReportSection.add(
			"Is Modded",
			(ICrashCallable<String>)(() -> {
				String string = ClientBrandRetriever.getClientModName();
				if (!"vanilla".equals(string)) {
					return "Definitely; Client brand changed to '" + string + "'";
				} else {
					return MinecraftClient.class.getSigners() == null
						? "Very likely; Jar signature invalidated"
						: "Probably not. Jar signature remains and client brand is untouched.";
				}
			})
		);
		crashReportSection.add("Type", "Client (map_client.txt)");
		crashReportSection.add("Resource Packs", (ICrashCallable<String>)(() -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (String string : this.options.resourcePacks) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(string);
				if (this.options.incompatibleResourcePacks.contains(string)) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		}));
		crashReportSection.add("Current Language", (ICrashCallable<String>)(() -> this.languageManager.getLanguage().toString()));
		crashReportSection.add("CPU", GLX::getCpuInfo);
		if (this.world != null) {
			this.world.addDetailsToCrashReport(crashReport);
		}

		return crashReport;
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public CompletableFuture<Void> reloadResourcesConcurrently() {
		return this.executeFuture(this::reloadResources).thenCompose(completableFuture -> completableFuture);
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addInfo("fps", currentFps);
		snooper.addInfo("vsync_enabled", this.options.enableVsync);
		int i = GLX.getRefreshRate(this.window);
		snooper.addInfo("display_frequency", i);
		snooper.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
		snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("current_action", this.getCurrentAction());
		snooper.addInfo("language", this.options.language == null ? "en_us" : this.options.language);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addInfo("endianness", string);
		snooper.addInfo("subtitles", this.options.showSubtitles);
		snooper.addInfo("touch", this.options.touchscreen ? "touch" : "mouse");
		int j = 0;

		for (ClientResourcePackContainer clientResourcePackContainer : this.resourcePackContainerManager.getEnabledContainers()) {
			if (!clientResourcePackContainer.canBeSorted() && !clientResourcePackContainer.sortsTillEnd()) {
				snooper.addInfo("resource_pack[" + j++ + "]", clientResourcePackContainer.getName());
			}
		}

		snooper.addInfo("resource_packs", j);
		if (this.server != null && this.server.getSnooper() != null) {
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

	public static int getMaxTextureSize() {
		if (cachedMaxTextureSize == -1) {
			for (int i = 16384; i > 0; i >>= 1) {
				GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
				int j = GlStateManager.getTexLevelParameter(32868, 0, 4096);
				if (j != 0) {
					cachedMaxTextureSize = i;
					return i;
				}
			}
		}

		return cachedMaxTextureSize;
	}

	public void setCurrentServerEntry(ServerEntry serverEntry) {
		this.currentServerEntry = serverEntry;
	}

	@Nullable
	public ServerEntry getCurrentServerEntry() {
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

	public ResourcePackContainerManager<ClientResourcePackContainer> method_1520() {
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
		return this.isPaused;
	}

	public SoundLoader getSoundLoader() {
		return this.soundLoader;
	}

	public MusicTracker.MusicType getMusicType() {
		if (this.currentScreen instanceof EndCreditsScreen) {
			return MusicTracker.MusicType.field_5578;
		} else if (this.player == null) {
			return MusicTracker.MusicType.field_5585;
		} else if (this.player.world.dimension instanceof TheNetherDimension) {
			return MusicTracker.MusicType.field_5582;
		} else if (this.player.world.dimension instanceof TheEndDimension) {
			return this.inGameHud.getBossBarHud().shouldPlayDragonMusic() ? MusicTracker.MusicType.field_5580 : MusicTracker.MusicType.field_5583;
		} else {
			Biome.Category category = this.player.world.getBiome(new BlockPos(this.player.x, this.player.y, this.player.z)).getCategory();
			if (!this.musicTracker.isPlayingType(MusicTracker.MusicType.field_5576)
				&& (
					!this.player.isInWater()
						|| this.musicTracker.isPlayingType(MusicTracker.MusicType.field_5586)
						|| category != Biome.Category.OCEAN && category != Biome.Category.RIVER
				)) {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.field_5581 : MusicTracker.MusicType.field_5586;
			} else {
				return MusicTracker.MusicType.field_5576;
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
	public boolean isMainThread() {
		return Thread.currentThread() == this.thread;
	}

	@Override
	protected Runnable method_16211(Runnable runnable) {
		return runnable;
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

	public BlockColorMap getBlockColorMap() {
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
		return this.isWindowFocused;
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

	public StatusEffectSpriteManager method_18505() {
		return this.field_18173;
	}

	@Override
	public void onWindowFocusChanged(boolean bl) {
		this.isWindowFocused = bl;
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
	public class_4071 method_18506() {
		return this.field_18175;
	}
}
