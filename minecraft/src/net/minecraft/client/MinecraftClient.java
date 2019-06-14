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
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.ContainerScreenRegistry;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.EndCreditsScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.PauseScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.ServerEntry;
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
import net.minecraft.client.render.chunk.ChunkRenderer;
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
public class MinecraftClient extends NonBlockingThreadExecutor<Runnable> implements SnooperListener, WindowEventHandler, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean IS_SYSTEM_MAC = SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.MAC;
	public static final Identifier DEFAULT_TEXT_RENDERER_ID = new Identifier("default");
	public static final Identifier ALT_TEXT_RENDERER_ID = new Identifier("alt");
	public static CompletableFuture<Unit> voidFuture = CompletableFuture.completedFuture(Unit.field_17274);
	public static byte[] memoryReservedForCrash = new byte[10485760];
	private static int cachedMaxTextureSize = -1;
	private final File resourcePackDir;
	private final PropertyMap sessionPropertyMap;
	private final WindowSettings windowSettings;
	private ServerEntry field_1699;
	private TextureManager field_1764;
	private static MinecraftClient instance;
	private final DataFixer dataFixer;
	public ClientPlayerInteractionManager field_1761;
	private WindowProvider field_1686;
	public Window window;
	private boolean crashed;
	private CrashReport crashReport;
	private boolean connectedToRealms;
	private final RenderTickCounter field_1728 = new RenderTickCounter(20.0F, 0L);
	private final Snooper snooper = new Snooper("client", this, SystemUtil.getMeasuringTimeMs());
	public ClientWorld field_1687;
	public WorldRenderer field_1769;
	private EntityRenderDispatcher field_1731;
	private ItemRenderer field_1742;
	private FirstPersonRenderer field_1737;
	public ClientPlayerEntity field_1724;
	@Nullable
	public Entity cameraEntity;
	@Nullable
	public Entity targetedEntity;
	public ParticleManager field_1713;
	private final SearchManager field_1733 = new SearchManager();
	private final Session field_1726;
	private boolean paused;
	private float pausedTickDelta;
	public TextRenderer field_1772;
	@Nullable
	public Screen field_1755;
	@Nullable
	public Overlay field_18175;
	public GameRenderer field_1773;
	public DebugRenderer field_1709;
	protected int attackCooldown;
	@Nullable
	private IntegratedServer field_1766;
	private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
	public InGameHud field_1705;
	public boolean skipGameRender;
	public HitResult hitResult;
	public GameOptions field_1690;
	private HotbarStorage creativeHotbarStorage;
	public Mouse field_1729;
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
	private final DisableableProfiler profiler = new DisableableProfiler(() -> this.field_1728.ticksThisFrame);
	private ReloadableResourceManager resourceManager;
	private final ClientResourcePackCreator field_1722;
	private final ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;
	private LanguageManager field_1717;
	private BlockColors field_1751;
	private ItemColors field_1760;
	private GlFramebuffer framebuffer;
	private SpriteAtlasTexture field_1767;
	private SoundManager field_1727;
	private MusicTracker field_1714;
	private FontManager field_1708;
	private SplashTextResourceSupplier field_17763;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider field_1707;
	private final Thread thread = Thread.currentThread();
	private BakedModelManager field_1763;
	private BlockRenderManager field_1756;
	private PaintingManager field_18008;
	private StatusEffectSpriteManager field_18173;
	private final ToastManager field_1702;
	private final MinecraftClientGame game = new MinecraftClientGame(this);
	private volatile boolean isRunning = true;
	public String fpsDebugString = "";
	public boolean field_1730 = true;
	private long nextDebugInfoUpdateTime;
	private int fpsCounter;
	private final TutorialManager field_1758;
	private boolean windowFocused;
	private final Queue<Runnable> renderTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	private CompletableFuture<Void> resourceReloadFuture;
	private String openProfilerSection = "root";

	public MinecraftClient(RunArgs runArgs) {
		super("Client");
		this.windowSettings = runArgs.windowSettings;
		instance = this;
		this.runDirectory = runArgs.directories.runDir;
		this.assetDirectory = runArgs.directories.assetDir;
		this.resourcePackDir = runArgs.directories.resourcePackDir;
		this.gameVersion = runArgs.game.version;
		this.versionType = runArgs.game.versionType;
		this.sessionPropertyMap = runArgs.network.profileProperties;
		this.field_1722 = new ClientResourcePackCreator(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.method_2788());
		this.resourcePackContainerManager = new ResourcePackContainerManager<>((string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition) -> {
			Supplier<ResourcePack> supplier2;
			if (packResourceMetadata.getPackFormat() < SharedConstants.getGameVersion().getPackVersion()) {
				supplier2 = () -> new RedirectedResourcePack((ResourcePack)supplier.get(), RedirectedResourcePack.NEW_TO_OLD_MAP);
			} else {
				supplier2 = supplier;
			}

			return new ClientResourcePackContainer(string, bl, supplier2, resourcePack, packResourceMetadata, insertionPosition);
		});
		this.resourcePackContainerManager.addCreator(this.field_1722);
		this.resourcePackContainerManager.addCreator(new FileResourcePackCreator(this.resourcePackDir));
		this.netProxy = runArgs.network.netProxy == null ? Proxy.NO_PROXY : runArgs.network.netProxy;
		this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.field_1726 = runArgs.network.session;
		LOGGER.info("Setting user: {}", this.field_1726.getUsername());
		LOGGER.debug("(Session ID is {})", this.field_1726.getSessionId());
		this.isDemo = runArgs.game.demo;
		this.is64Bit = checkIs64Bit();
		this.field_1766 = null;
		if (runArgs.autoConnect.serverIP != null) {
			this.autoConnectServerIp = runArgs.autoConnect.serverIP;
			this.autoConnectServerPort = runArgs.autoConnect.serverPort;
		}

		Bootstrap.initialize();
		Bootstrap.logMissingTranslations();
		KeybindText.i18n = KeyBinding::getLocalizedName;
		this.dataFixer = Schemas.getFixer();
		this.field_1702 = new ToastManager(this);
		this.field_1758 = new TutorialManager(this);
	}

	public void start() {
		this.isRunning = true;

		try {
			this.init();
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Initializing game");
			crashReport.addElement("Initialization");
			this.printCrashReport(this.populateCrashReport(crashReport));
			return;
		}

		try {
			try {
				boolean bl = false;

				while (this.isRunning) {
					if (this.crashed && this.crashReport != null) {
						this.printCrashReport(this.crashReport);
						return;
					}

					try {
						this.render(!bl);
					} catch (OutOfMemoryError var10) {
						if (bl) {
							throw var10;
						}

						this.cleanUpAfterCrash();
						this.method_1507(new OutOfMemoryScreen());
						System.gc();
						LOGGER.fatal("Out of memory", (Throwable)var10);
						bl = true;
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
		this.field_1690 = new GameOptions(this, this.runDirectory);
		this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
		this.startTimerHackThread();
		LOGGER.info("LWJGL Version: {}", GLX.getLWJGLVersion());
		WindowSettings windowSettings = this.windowSettings;
		if (this.field_1690.overrideHeight > 0 && this.field_1690.overrideWidth > 0) {
			windowSettings = new WindowSettings(
				this.field_1690.overrideWidth, this.field_1690.overrideHeight, windowSettings.fullscreenWidth, windowSettings.fullscreenHeight, windowSettings.fullscreen
			);
		}

		LongSupplier longSupplier = GLX.initGlfw();
		if (longSupplier != null) {
			SystemUtil.nanoTimeSupplier = longSupplier;
		}

		this.field_1686 = new WindowProvider(this);
		this.window = this.field_1686.createWindow(windowSettings, this.field_1690.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
		this.onWindowFocusChanged(true);

		try {
			InputStream inputStream = this.method_1516().getPack().open(ResourceType.field_14188, new Identifier("icons/icon_16x16.png"));
			InputStream inputStream2 = this.method_1516().getPack().open(ResourceType.field_14188, new Identifier("icons/icon_32x32.png"));
			this.window.setIcon(inputStream, inputStream2);
		} catch (IOException var6) {
			LOGGER.error("Couldn't set icon", (Throwable)var6);
		}

		this.window.setFramerateLimit(this.field_1690.maxFps);
		this.field_1729 = new Mouse(this);
		this.field_1729.setup(this.window.getHandle());
		this.keyboard = new Keyboard(this);
		this.keyboard.setup(this.window.getHandle());
		GLX.init();
		GlDebug.enableDebug(this.field_1690.glDebugVerbosity, false);
		this.framebuffer = new GlFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, IS_SYSTEM_MAC);
		this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.field_14188, this.thread);
		this.field_1690.addResourcePackContainersToManager(this.resourcePackContainerManager);
		this.resourcePackContainerManager.callCreators();
		List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
			.getEnabledContainers()
			.stream()
			.map(ResourcePackContainer::createResourcePack)
			.collect(Collectors.toList());

		for (ResourcePack resourcePack : list) {
			this.resourceManager.addPack(resourcePack);
		}

		this.field_1717 = new LanguageManager(this.field_1690.language);
		this.resourceManager.registerListener(this.field_1717);
		this.field_1717.reloadResources(list);
		this.field_1764 = new TextureManager(this.resourceManager);
		this.resourceManager.registerListener(this.field_1764);
		this.onResolutionChanged();
		this.field_1707 = new PlayerSkinProvider(this.field_1764, new File(this.assetDirectory, "skins"), this.sessionService);
		this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
		this.field_1727 = new SoundManager(this.resourceManager, this.field_1690);
		this.resourceManager.registerListener(this.field_1727);
		this.field_17763 = new SplashTextResourceSupplier(this.field_1726);
		this.resourceManager.registerListener(this.field_17763);
		this.field_1714 = new MusicTracker(this);
		this.field_1708 = new FontManager(this.field_1764, this.forcesUnicodeFont());
		this.resourceManager.registerListener(this.field_1708.getResourceReloadListener());
		this.field_1772 = this.field_1708.getTextRenderer(DEFAULT_TEXT_RENDERER_ID);
		if (this.field_1690.language != null) {
			this.field_1772.setRightToLeft(this.field_1717.isRightToLeft());
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
		this.field_1767 = new SpriteAtlasTexture("textures");
		this.field_1767.setMipLevel(this.field_1690.mipmapLevels);
		this.field_1764.method_4620(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.field_1767);
		this.field_1764.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.field_1767.setFilter(false, this.field_1690.mipmapLevels > 0);
		this.field_1763 = new BakedModelManager(this.field_1767);
		this.resourceManager.registerListener(this.field_1763);
		this.field_1751 = BlockColors.create();
		this.field_1760 = ItemColors.create(this.field_1751);
		this.field_1742 = new ItemRenderer(this.field_1764, this.field_1763, this.field_1760);
		this.field_1731 = new EntityRenderDispatcher(this.field_1764, this.field_1742, this.resourceManager);
		this.field_1737 = new FirstPersonRenderer(this);
		this.resourceManager.registerListener(this.field_1742);
		this.field_1773 = new GameRenderer(this, this.resourceManager);
		this.resourceManager.registerListener(this.field_1773);
		this.field_1756 = new BlockRenderManager(this.field_1763.getBlockStateMaps(), this.field_1751);
		this.resourceManager.registerListener(this.field_1756);
		this.field_1769 = new WorldRenderer(this);
		this.resourceManager.registerListener(this.field_1769);
		this.initializeSearchableContainers();
		this.resourceManager.registerListener(this.field_1733);
		GlStateManager.viewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.field_1713 = new ParticleManager(this.field_1687, this.field_1764);
		this.resourceManager.registerListener(this.field_1713);
		this.field_18008 = new PaintingManager(this.field_1764);
		this.resourceManager.registerListener(this.field_18008);
		this.field_18173 = new StatusEffectSpriteManager(this.field_1764);
		this.resourceManager.registerListener(this.field_18173);
		this.field_1705 = new InGameHud(this);
		this.field_1709 = new DebugRenderer(this);
		GLX.setGlfwErrorCallback(this::handleGlErrorByDisableVsync);
		if (this.field_1690.fullscreen && !this.window.isFullscreen()) {
			this.window.toggleFullscreen();
			this.field_1690.fullscreen = this.window.isFullscreen();
		}

		this.window.setVsync(this.field_1690.enableVsync);
		this.window.logOnGlError();
		if (this.autoConnectServerIp != null) {
			this.method_1507(new ConnectScreen(new TitleScreen(), this, this.autoConnectServerIp, this.autoConnectServerPort));
		} else {
			this.method_1507(new TitleScreen(true));
		}

		SplashScreen.method_18819(this);
		this.method_18502(
			new SplashScreen(
				this,
				this.resourceManager.beginInitialMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, CompletableFuture.completedFuture(Unit.field_17274)),
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
			itemStack -> itemStack.getTooltip(null, TooltipContext.Default.field_8934)
					.stream()
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty()),
			itemStack -> Stream.of(Registry.ITEM.getId(itemStack.getItem()))
		);
		IdentifierSearchableContainer<ItemStack> identifierSearchableContainer = new IdentifierSearchableContainer<>(
			itemStack -> ItemTags.getContainer().getTagsFor(itemStack.getItem()).stream()
		);
		DefaultedList<ItemStack> defaultedList = DefaultedList.create();

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
					.flatMap(recipe -> recipe.getOutput().getTooltip(null, TooltipContext.Default.field_8934).stream())
					.map(text -> Formatting.strip(text.getString()).trim())
					.filter(string -> !string.isEmpty()),
			recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem()))
		);
		this.field_1733.put(SearchManager.ITEM_TOOLTIP, textSearchableContainer);
		this.field_1733.put(SearchManager.ITEM_TAG, identifierSearchableContainer);
		this.field_1733.put(SearchManager.RECIPE_OUTPUT, textSearchableContainer2);
	}

	private void handleGlErrorByDisableVsync(int i, long l) {
		this.field_1690.enableVsync = false;
		this.field_1690.write();
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
		return this.field_1690.forceUnicodeFont;
	}

	public CompletableFuture<Void> reloadResources() {
		if (this.resourceReloadFuture != null) {
			return this.resourceReloadFuture;
		} else {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			if (this.field_18175 instanceof SplashScreen) {
				this.resourceReloadFuture = completableFuture;
				return completableFuture;
			} else {
				this.resourcePackContainerManager.callCreators();
				List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
					.getEnabledContainers()
					.stream()
					.map(ResourcePackContainer::createResourcePack)
					.collect(Collectors.toList());
				this.method_18502(new SplashScreen(this, this.resourceManager.beginMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, voidFuture, list), () -> {
					this.field_1717.reloadResources(list);
					if (this.field_1769 != null) {
						this.field_1769.reload();
					}

					completableFuture.complete(null);
				}, true));
				return completableFuture;
			}
		}
	}

	private void checkGameData() {
		boolean bl = false;
		BlockModels blockModels = this.method_1541().getModels();
		BakedModel bakedModel = blockModels.method_3333().getMissingModel();

		for (Block block : Registry.BLOCK) {
			for (BlockState blockState : block.method_9595().getStates()) {
				if (blockState.getRenderType() == BlockRenderType.field_11458) {
					BakedModel bakedModel2 = blockModels.method_3335(blockState);
					if (bakedModel2 == bakedModel) {
						LOGGER.debug("Missing model for: {}", blockState);
						bl = true;
					}
				}
			}
		}

		Sprite sprite = bakedModel.getSprite();

		for (Block block2 : Registry.BLOCK) {
			for (BlockState blockState2 : block2.method_9595().getStates()) {
				Sprite sprite2 = blockModels.method_3339(blockState2);
				if (!blockState2.isAir() && sprite2 == sprite) {
					LOGGER.debug("Missing particle icon for: {}", blockState2);
					bl = true;
				}
			}
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.create();

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

		bl |= ContainerScreenRegistry.checkData();
		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	public void method_1507(@Nullable Screen screen) {
		if (this.field_1755 != null) {
			this.field_1755.removed();
		}

		if (screen == null && this.field_1687 == null) {
			screen = new TitleScreen();
		} else if (screen == null && this.field_1724.getHealth() <= 0.0F) {
			screen = new DeathScreen(null, this.field_1687.method_8401().isHardcore());
		}

		if (screen instanceof TitleScreen || screen instanceof MultiplayerScreen) {
			this.field_1690.debugEnabled = false;
			this.field_1705.method_1743().clear(true);
		}

		this.field_1755 = screen;
		if (screen != null) {
			this.field_1729.unlockCursor();
			KeyBinding.unpressAll();
			screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
			this.skipGameRender = false;
			NarratorManager.INSTANCE.narrate(screen.getNarrationMessage());
		} else {
			this.field_1727.resumeAll();
			this.field_1729.lockCursor();
		}
	}

	public void method_18502(@Nullable Overlay overlay) {
		this.field_18175 = overlay;
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");
			NarratorManager.INSTANCE.destroy();

			try {
				if (this.field_1687 != null) {
					this.field_1687.disconnect();
				}

				this.disconnect();
			} catch (Throwable var5) {
			}

			if (this.field_1755 != null) {
				this.field_1755.removed();
			}

			this.close();
		} finally {
			SystemUtil.nanoTimeSupplier = System::nanoTime;
			if (!this.crashed) {
				System.exit(0);
			}
		}
	}

	@Override
	public void close() {
		try {
			this.field_1767.clear();
			this.field_1772.close();
			this.field_1708.close();
			this.field_1773.close();
			this.field_1769.close();
			this.field_1727.close();
			this.resourcePackContainerManager.close();
			this.field_1713.clearAtlas();
			this.field_18173.close();
			this.field_18008.close();
			SystemUtil.shutdownServerWorkerExecutor();
		} finally {
			this.field_1686.close();
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

		if (this.resourceReloadFuture != null && !(this.field_18175 instanceof SplashScreen)) {
			CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
			this.resourceReloadFuture = null;
			this.reloadResources().thenRun(() -> completableFuture.complete(null));
		}

		Runnable runnable;
		while ((runnable = (Runnable)this.renderTaskQueue.poll()) != null) {
			runnable.run();
		}

		if (bl) {
			this.field_1728.beginRenderTick(SystemUtil.getMeasuringTimeMs());
			this.profiler.push("scheduledExecutables");
			this.executeTaskQueue();
			this.profiler.pop();
		}

		long m = SystemUtil.getMeasuringTimeNano();
		this.profiler.push("tick");
		if (bl) {
			for (int i = 0; i < Math.min(10, this.field_1728.ticksThisFrame); i++) {
				this.tick();
			}
		}

		this.field_1729.updateMouse();
		this.window.setPhase("Render");
		GLX.pollEvents();
		long n = SystemUtil.getMeasuringTimeNano() - m;
		this.profiler.swap("sound");
		this.field_1727.updateListenerPosition(this.field_1773.getCamera());
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
			this.field_1773.render(this.paused ? this.pausedTickDelta : this.field_1728.tickDelta, l, bl);
			this.profiler.swap("toasts");
			this.field_1702.draw();
			this.profiler.pop();
		}

		this.profiler.endTick();
		if (this.field_1690.debugEnabled && this.field_1690.debugProfilerEnabled && !this.field_1690.hudHidden) {
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
		this.profiler.startTick();
		this.updateDisplay(true);
		Thread.yield();
		this.window.setPhase("Post render");
		this.fpsCounter++;
		boolean bl2 = this.isIntegratedServerRunning()
			&& (this.field_1755 != null && this.field_1755.isPauseScreen() || this.field_18175 != null && this.field_18175.pausesGame())
			&& !this.field_1766.isRemote();
		if (this.paused != bl2) {
			if (this.paused) {
				this.pausedTickDelta = this.field_1728.tickDelta;
			} else {
				this.field_1728.tickDelta = this.pausedTickDelta;
			}

			this.paused = bl2;
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
				(double)this.field_1690.maxFps == Option.field_1935.getMax() ? "inf" : this.field_1690.maxFps,
				this.field_1690.enableVsync ? " vsync" : "",
				this.field_1690.fancyGraphics ? "" : " fast",
				this.field_1690.cloudRenderMode == CloudRenderMode.OFF ? "" : (this.field_1690.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"),
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
		this.window.setFullscreen(this.field_1690.fullscreen);
		this.profiler.pop();
		if (bl && this.isFramerateLimited()) {
			this.profiler.push("fpslimit_wait");
			this.window.waitForFramerateLimit();
			this.profiler.pop();
		}
	}

	@Override
	public void onResolutionChanged() {
		int i = this.window.calculateScaleFactor(this.field_1690.guiScale, this.forcesUnicodeFont());
		this.window.setScaleFactor((double)i);
		if (this.field_1755 != null) {
			this.field_1755.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
		}

		GlFramebuffer glFramebuffer = this.getFramebuffer();
		if (glFramebuffer != null) {
			glFramebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
		}

		if (this.field_1773 != null) {
			this.field_1773.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		}

		if (this.field_1729 != null) {
			this.field_1729.onResolutionChanged();
		}
	}

	private int getFramerateLimit() {
		return this.field_1687 != null || this.field_1755 == null && this.field_18175 == null ? this.window.getFramerateLimit() : 60;
	}

	private boolean isFramerateLimited() {
		return (double)this.getFramerateLimit() < Option.field_1935.getMax();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.field_1769.method_3267();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			if (this.isIntegratedServerRunning()) {
				this.field_1766.stop(true);
			}

			this.method_18096(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
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
			bufferBuilder.method_1328(7, VertexFormats.field_1576);
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
				bufferBuilder.method_1328(6, VertexFormats.field_1576);
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
				bufferBuilder.method_1328(5, VertexFormats.field_1576);

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
			this.field_1772.drawWithShadow(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			this.field_1772.drawWithShadow(string, (float)(j + 160 - this.field_1772.getStringWidth(string)), (float)(k - 80 - 16), 16777215);

			for (int s = 0; s < list.size(); s++) {
				ProfilerTiming profilerTiming3 = (ProfilerTiming)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string2 = stringBuilder.append(profilerTiming3.name).toString();
				this.field_1772.drawWithShadow(string2, (float)(j - 160), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
				this.field_1772
					.drawWithShadow(string2, (float)(j + 160 - 50 - this.field_1772.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
				this.field_1772
					.drawWithShadow(string2, (float)(j + 160 - this.field_1772.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
			}
		}
	}

	public void scheduleStop() {
		this.isRunning = false;
	}

	public void openPauseMenu(boolean bl) {
		if (this.field_1755 == null) {
			boolean bl2 = this.isIntegratedServerRunning() && !this.field_1766.isRemote();
			if (bl2) {
				this.method_1507(new PauseScreen(!bl));
				this.field_1727.pauseAll();
			} else {
				this.method_1507(new PauseScreen(true));
			}
		}
	}

	private void method_1590(boolean bl) {
		if (!bl) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.field_1724.isUsingItem()) {
			if (bl && this.hitResult != null && this.hitResult.getType() == HitResult.Type.field_1332) {
				BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.field_1687.method_8320(blockPos).isAir()) {
					Direction direction = blockHitResult.getSide();
					if (this.field_1761.method_2902(blockPos, direction)) {
						this.field_1713.addBlockBreakingParticles(blockPos, direction);
						this.field_1724.swingHand(Hand.field_5808);
					}
				}
			} else {
				this.field_1761.cancelBlockBreaking();
			}
		}
	}

	private void doAttack() {
		if (this.attackCooldown <= 0) {
			if (this.hitResult == null) {
				LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
				if (this.field_1761.hasLimitedAttackSpeed()) {
					this.attackCooldown = 10;
				}
			} else if (!this.field_1724.isRiding()) {
				switch (this.hitResult.getType()) {
					case field_1331:
						this.field_1761.attackEntity(this.field_1724, ((EntityHitResult)this.hitResult).getEntity());
						break;
					case field_1332:
						BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
						BlockPos blockPos = blockHitResult.getBlockPos();
						if (!this.field_1687.method_8320(blockPos).isAir()) {
							this.field_1761.attackBlock(blockPos, blockHitResult.getSide());
							break;
						}
					case field_1333:
						if (this.field_1761.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}

						this.field_1724.resetLastAttackedTicks();
				}

				this.field_1724.swingHand(Hand.field_5808);
			}
		}
	}

	private void doItemUse() {
		if (!this.field_1761.isBreakingBlock()) {
			this.itemUseCooldown = 4;
			if (!this.field_1724.isRiding()) {
				if (this.hitResult == null) {
					LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				for (Hand hand : Hand.values()) {
					ItemStack itemStack = this.field_1724.getStackInHand(hand);
					if (this.hitResult != null) {
						switch (this.hitResult.getType()) {
							case field_1331:
								EntityHitResult entityHitResult = (EntityHitResult)this.hitResult;
								Entity entity = entityHitResult.getEntity();
								if (this.field_1761.interactEntityAtLocation(this.field_1724, entity, entityHitResult, hand) == ActionResult.field_5812) {
									return;
								}

								if (this.field_1761.interactEntity(this.field_1724, entity, hand) == ActionResult.field_5812) {
									return;
								}
								break;
							case field_1332:
								BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
								int i = itemStack.getCount();
								ActionResult actionResult = this.field_1761.method_2896(this.field_1724, this.field_1687, hand, blockHitResult);
								if (actionResult == ActionResult.field_5812) {
									this.field_1724.swingHand(hand);
									if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.field_1761.hasCreativeInventory())) {
										this.field_1773.field_4012.resetEquipProgress(hand);
									}

									return;
								}

								if (actionResult == ActionResult.field_5814) {
									return;
								}
						}
					}

					if (!itemStack.isEmpty() && this.field_1761.interactItem(this.field_1724, this.field_1687, hand) == ActionResult.field_5812) {
						this.field_1773.field_4012.resetEquipProgress(hand);
						return;
					}
				}
			}
		}
	}

	public MusicTracker method_1538() {
		return this.field_1714;
	}

	public void tick() {
		if (this.itemUseCooldown > 0) {
			this.itemUseCooldown--;
		}

		this.profiler.push("gui");
		if (!this.paused) {
			this.field_1705.tick();
		}

		this.profiler.pop();
		this.field_1773.updateTargetedEntity(1.0F);
		this.field_1758.tick(this.field_1687, this.hitResult);
		this.profiler.push("gameMode");
		if (!this.paused && this.field_1687 != null) {
			this.field_1761.tick();
		}

		this.profiler.swap("textures");
		if (this.field_1687 != null) {
			this.field_1764.tick();
		}

		if (this.field_1755 == null && this.field_1724 != null) {
			if (this.field_1724.getHealth() <= 0.0F && !(this.field_1755 instanceof DeathScreen)) {
				this.method_1507(null);
			} else if (this.field_1724.isSleeping() && this.field_1687 != null) {
				this.method_1507(new SleepingChatScreen());
			}
		} else if (this.field_1755 != null && this.field_1755 instanceof SleepingChatScreen && !this.field_1724.isSleeping()) {
			this.method_1507(null);
		}

		if (this.field_1755 != null) {
			this.attackCooldown = 10000;
		}

		if (this.field_1755 != null) {
			Screen.wrapScreenError(() -> this.field_1755.tick(), "Ticking screen", this.field_1755.getClass().getCanonicalName());
		}

		if (!this.field_1690.debugEnabled) {
			this.field_1705.resetDebugHudChunk();
		}

		if (this.field_18175 == null && (this.field_1755 == null || this.field_1755.passEvents)) {
			this.profiler.swap("GLFW events");
			GLX.pollEvents();
			this.handleInputEvents();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
		}

		if (this.field_1687 != null) {
			this.profiler.swap("gameRenderer");
			if (!this.paused) {
				this.field_1773.tick();
			}

			this.profiler.swap("levelRenderer");
			if (!this.paused) {
				this.field_1769.tick();
			}

			this.profiler.swap("level");
			if (!this.paused) {
				if (this.field_1687.getTicksSinceLightning() > 0) {
					this.field_1687.setTicksSinceLightning(this.field_1687.getTicksSinceLightning() - 1);
				}

				this.field_1687.tickEntities();
			}
		} else if (this.field_1773.isShaderEnabled()) {
			this.field_1773.disableShader();
		}

		if (!this.paused) {
			this.field_1714.tick();
		}

		this.field_1727.tick(this.paused);
		if (this.field_1687 != null) {
			if (!this.paused) {
				this.field_1687.setMobSpawnOptions(this.field_1687.getDifficulty() != Difficulty.field_5801, true);
				this.field_1758.tick();

				try {
					this.field_1687.tick(() -> true);
				} catch (Throwable var4) {
					CrashReport crashReport = CrashReport.create(var4, "Exception in world tick");
					if (this.field_1687 == null) {
						CrashReportSection crashReportSection = crashReport.addElement("Affected level");
						crashReportSection.add("Problem", "Level is null!");
					} else {
						this.field_1687.addDetailsToCrashReport(crashReport);
					}

					throw new CrashException(crashReport);
				}
			}

			this.profiler.swap("animateTick");
			if (!this.paused && this.field_1687 != null) {
				this.field_1687.doRandomBlockDisplayTicks(MathHelper.floor(this.field_1724.x), MathHelper.floor(this.field_1724.y), MathHelper.floor(this.field_1724.z));
			}

			this.profiler.swap("particles");
			if (!this.paused) {
				this.field_1713.tick();
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
		while (this.field_1690.keyTogglePerspective.wasPressed()) {
			this.field_1690.perspective++;
			if (this.field_1690.perspective > 2) {
				this.field_1690.perspective = 0;
			}

			if (this.field_1690.perspective == 0) {
				this.field_1773.onCameraEntitySet(this.getCameraEntity());
			} else if (this.field_1690.perspective == 1) {
				this.field_1773.onCameraEntitySet(null);
			}

			this.field_1769.scheduleTerrainUpdate();
		}

		while (this.field_1690.keySmoothCamera.wasPressed()) {
			this.field_1690.smoothCameraEnabled = !this.field_1690.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.field_1690.keySaveToolbarActivator.isPressed();
			boolean bl2 = this.field_1690.keyLoadToolbarActivator.isPressed();
			if (this.field_1690.keysHotbar[i].wasPressed()) {
				if (this.field_1724.isSpectator()) {
					this.field_1705.method_1739().onHotbarKeyPress(i);
				} else if (!this.field_1724.isCreative() || this.field_1755 != null || !bl2 && !bl) {
					this.field_1724.inventory.selectedSlot = i;
				} else {
					CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
				}
			}
		}

		while (this.field_1690.keyInventory.wasPressed()) {
			if (this.field_1761.hasRidingInventory()) {
				this.field_1724.openRidingInventory();
			} else {
				this.field_1758.onInventoryOpened();
				this.method_1507(new InventoryScreen(this.field_1724));
			}
		}

		while (this.field_1690.keyAdvancements.wasPressed()) {
			this.method_1507(new AdvancementsScreen(this.field_1724.networkHandler.getAdvancementHandler()));
		}

		while (this.field_1690.keySwapHands.wasPressed()) {
			if (!this.field_1724.isSpectator()) {
				this.method_1562().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12969, BlockPos.ORIGIN, Direction.field_11033));
			}
		}

		while (this.field_1690.keyDrop.wasPressed()) {
			if (!this.field_1724.isSpectator()) {
				this.field_1724.dropSelectedItem(Screen.hasControlDown());
			}
		}

		boolean bl3 = this.field_1690.chatVisibility != ChatVisibility.HIDDEN;
		if (bl3) {
			while (this.field_1690.keyChat.wasPressed()) {
				this.method_1507(new ChatScreen(""));
			}

			if (this.field_1755 == null && this.field_18175 == null && this.field_1690.keyCommand.wasPressed()) {
				this.method_1507(new ChatScreen("/"));
			}
		}

		if (this.field_1724.isUsingItem()) {
			if (!this.field_1690.keyUse.isPressed()) {
				this.field_1761.stopUsingItem(this.field_1724);
			}

			while (this.field_1690.keyAttack.wasPressed()) {
			}

			while (this.field_1690.keyUse.wasPressed()) {
			}

			while (this.field_1690.keyPickItem.wasPressed()) {
			}
		} else {
			while (this.field_1690.keyAttack.wasPressed()) {
				this.doAttack();
			}

			while (this.field_1690.keyUse.wasPressed()) {
				this.doItemUse();
			}

			while (this.field_1690.keyPickItem.wasPressed()) {
				this.doItemPick();
			}
		}

		if (this.field_1690.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.field_1724.isUsingItem()) {
			this.doItemUse();
		}

		this.method_1590(this.field_1755 == null && this.field_1690.keyAttack.isPressed() && this.field_1729.isCursorLocked());
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
			this.field_1766 = new IntegratedServer(
				this, string, string2, levelInfo, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache, i -> {
					WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(i + 0);
					worldGenerationProgressTracker.start();
					this.worldGenProgressTracker.set(worldGenerationProgressTracker);
					return new QueueingWorldGenerationProgressListener(worldGenerationProgressTracker, this.renderTaskQueue::add);
				}
			);
			this.field_1766.start();
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
		this.method_1507(levelLoadingScreen);

		while (!this.field_1766.isLoading()) {
			levelLoadingScreen.tick();
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

		SocketAddress socketAddress = this.field_1766.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connect(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, text -> {
		}));
		clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.send(new LoginHelloC2SPacket(this.method_1548().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void method_1481(ClientWorld clientWorld) {
		ProgressScreen progressScreen = new ProgressScreen();
		progressScreen.method_15412(new TranslatableText("connect.joining"));
		this.method_18098(progressScreen);
		this.field_1687 = clientWorld;
		this.method_18097(clientWorld);
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
		this.method_18096(new ProgressScreen());
	}

	public void method_18096(Screen screen) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.method_1562();
		if (clientPlayNetworkHandler != null) {
			this.clear();
			clientPlayNetworkHandler.clearWorld();
		}

		IntegratedServer integratedServer = this.field_1766;
		this.field_1766 = null;
		this.field_1773.reset();
		this.field_1761 = null;
		NarratorManager.INSTANCE.clear();
		this.method_18098(screen);
		if (this.field_1687 != null) {
			if (integratedServer != null) {
				while (!integratedServer.isServerThreadAlive()) {
					this.render(false);
				}
			}

			this.field_1722.clear();
			this.field_1705.clear();
			this.method_1584(null);
			this.isIntegratedServerRunning = false;
			this.game.onLeaveGameSession();
		}

		this.field_1687 = null;
		this.method_18097(null);
		this.field_1724 = null;
	}

	private void method_18098(Screen screen) {
		this.field_1714.stop();
		this.field_1727.stopAll();
		this.cameraEntity = null;
		this.clientConnection = null;
		this.method_1507(screen);
		this.render(false);
	}

	private void method_18097(@Nullable ClientWorld clientWorld) {
		if (this.field_1769 != null) {
			this.field_1769.setWorld(clientWorld);
		}

		if (this.field_1713 != null) {
			this.field_1713.setWorld(clientWorld);
		}

		BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
	}

	public final boolean isDemo() {
		return this.isDemo;
	}

	@Nullable
	public ClientPlayNetworkHandler method_1562() {
		return this.field_1724 == null ? null : this.field_1724.networkHandler;
	}

	public static boolean isHudEnabled() {
		return instance == null || !instance.field_1690.hudHidden;
	}

	public static boolean isFancyGraphicsEnabled() {
		return instance != null && instance.field_1690.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance != null && instance.field_1690.ao != AoOption.OFF;
	}

	private void doItemPick() {
		if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.field_1333) {
			boolean bl = this.field_1724.abilities.creativeMode;
			BlockEntity blockEntity = null;
			HitResult.Type type = this.hitResult.getType();
			ItemStack itemStack;
			if (type == HitResult.Type.field_1332) {
				BlockPos blockPos = ((BlockHitResult)this.hitResult).getBlockPos();
				BlockState blockState = this.field_1687.method_8320(blockPos);
				Block block = blockState.getBlock();
				if (blockState.isAir()) {
					return;
				}

				itemStack = block.method_9574(this.field_1687, blockPos, blockState);
				if (itemStack.isEmpty()) {
					return;
				}

				if (bl && Screen.hasControlDown() && block.hasBlockEntity()) {
					blockEntity = this.field_1687.method_8321(blockPos);
				}
			} else {
				if (type != HitResult.Type.field_1331 || !bl) {
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
				if (type == HitResult.Type.field_1332) {
					string = Registry.BLOCK.getId(this.field_1687.method_8320(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
				} else if (type == HitResult.Type.field_1331) {
					string = Registry.ENTITY_TYPE.getId(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", type, string);
			} else {
				PlayerInventory playerInventory = this.field_1724.inventory;
				if (blockEntity != null) {
					this.addBlockEntityNbt(itemStack, blockEntity);
				}

				int i = playerInventory.getSlotWithStack(itemStack);
				if (bl) {
					playerInventory.addPickBlock(itemStack);
					this.field_1761.clickCreativeStack(this.field_1724.getStackInHand(Hand.field_5808), 36 + playerInventory.selectedSlot);
				} else if (i != -1) {
					if (PlayerInventory.isValidHotbarIndex(i)) {
						playerInventory.selectedSlot = i;
					} else {
						this.field_1761.pickFromInventory(i);
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
		CrashReportSection crashReportSection = crashReport.getSystemDetailsSection();
		crashReportSection.add("Launched Version", (CrashCallable<String>)(() -> this.gameVersion));
		crashReportSection.add("LWJGL", GLX::getLWJGLVersion);
		crashReportSection.add("OpenGL", GLX::getOpenGLVersionString);
		crashReportSection.add("GL Caps", GLX::getCapsString);
		crashReportSection.add("Using VBOs", (CrashCallable<String>)(() -> "Yes"));
		crashReportSection.add(
			"Is Modded",
			(CrashCallable<String>)(() -> {
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
		crashReportSection.add("Resource Packs", (CrashCallable<String>)(() -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (String string : this.field_1690.resourcePacks) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(string);
				if (this.field_1690.incompatibleResourcePacks.contains(string)) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		}));
		crashReportSection.add("Current Language", (CrashCallable<String>)(() -> this.field_1717.getLanguage().toString()));
		crashReportSection.add("CPU", GLX::getCpuInfo);
		if (this.field_1687 != null) {
			this.field_1687.addDetailsToCrashReport(crashReport);
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
		snooper.addInfo("vsync_enabled", this.field_1690.enableVsync);
		int i = GLX.getRefreshRate(this.window);
		snooper.addInfo("display_frequency", i);
		snooper.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
		snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("current_action", this.getCurrentAction());
		snooper.addInfo("language", this.field_1690.language == null ? "en_us" : this.field_1690.language);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		snooper.addInfo("endianness", string);
		snooper.addInfo("subtitles", this.field_1690.showSubtitles);
		snooper.addInfo("touch", this.field_1690.touchscreen ? "touch" : "mouse");
		int j = 0;

		for (ClientResourcePackContainer clientResourcePackContainer : this.resourcePackContainerManager.getEnabledContainers()) {
			if (!clientResourcePackContainer.canBeSorted() && !clientResourcePackContainer.isPositionFixed()) {
				snooper.addInfo("resource_pack[" + j++ + "]", clientResourcePackContainer.getName());
			}
		}

		snooper.addInfo("resource_packs", j);
		if (this.field_1766 != null && this.field_1766.getSnooper() != null) {
			snooper.addInfo("snooper_partner", this.field_1766.getSnooper().getToken());
		}
	}

	private String getCurrentAction() {
		if (this.field_1766 != null) {
			return this.field_1766.isRemote() ? "hosting_lan" : "singleplayer";
		} else if (this.field_1699 != null) {
			return this.field_1699.isLocal() ? "playing_lan" : "multiplayer";
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

	public void method_1584(ServerEntry serverEntry) {
		this.field_1699 = serverEntry;
	}

	@Nullable
	public ServerEntry method_1558() {
		return this.field_1699;
	}

	public boolean isInSingleplayer() {
		return this.isIntegratedServerRunning;
	}

	public boolean isIntegratedServerRunning() {
		return this.isIntegratedServerRunning && this.field_1766 != null;
	}

	@Nullable
	public IntegratedServer method_1576() {
		return this.field_1766;
	}

	public Snooper getSnooper() {
		return this.snooper;
	}

	public Session method_1548() {
		return this.field_1726;
	}

	public PropertyMap getSessionProperties() {
		if (this.sessionPropertyMap.isEmpty()) {
			GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.field_1726.getProfile(), false);
			this.sessionPropertyMap.putAll(gameProfile.getProperties());
		}

		return this.sessionPropertyMap;
	}

	public Proxy getNetworkProxy() {
		return this.netProxy;
	}

	public TextureManager method_1531() {
		return this.field_1764;
	}

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public ResourcePackContainerManager<ClientResourcePackContainer> getResourcePackContainerManager() {
		return this.resourcePackContainerManager;
	}

	public ClientResourcePackCreator method_1516() {
		return this.field_1722;
	}

	public File getResourcePackDir() {
		return this.resourcePackDir;
	}

	public LanguageManager method_1526() {
		return this.field_1717;
	}

	public SpriteAtlasTexture method_1549() {
		return this.field_1767;
	}

	public boolean is64Bit() {
		return this.is64Bit;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public SoundManager method_1483() {
		return this.field_1727;
	}

	public MusicTracker.MusicType getMusicType() {
		if (this.field_1755 instanceof EndCreditsScreen) {
			return MusicTracker.MusicType.field_5578;
		} else if (this.field_1724 == null) {
			return MusicTracker.MusicType.field_5585;
		} else if (this.field_1724.field_6002.field_9247 instanceof TheNetherDimension) {
			return MusicTracker.MusicType.field_5582;
		} else if (this.field_1724.field_6002.field_9247 instanceof TheEndDimension) {
			return this.field_1705.method_1740().shouldPlayDragonMusic() ? MusicTracker.MusicType.field_5580 : MusicTracker.MusicType.field_5583;
		} else {
			Biome.Category category = this.field_1724.field_6002.method_8310(new BlockPos(this.field_1724)).getCategory();
			if (!this.field_1714.isPlayingType(MusicTracker.MusicType.field_5576)
				&& (
					!this.field_1724.isInWater()
						|| this.field_1714.isPlayingType(MusicTracker.MusicType.field_5586)
						|| category != Biome.Category.field_9367 && category != Biome.Category.field_9369
				)) {
				return this.field_1724.abilities.creativeMode && this.field_1724.abilities.allowFlying
					? MusicTracker.MusicType.field_5581
					: MusicTracker.MusicType.field_5586;
			} else {
				return MusicTracker.MusicType.field_5576;
			}
		}
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public PlayerSkinProvider method_1582() {
		return this.field_1707;
	}

	@Nullable
	public Entity getCameraEntity() {
		return this.cameraEntity;
	}

	public void setCameraEntity(Entity entity) {
		this.cameraEntity = entity;
		this.field_1773.onCameraEntitySet(entity);
	}

	@Override
	protected Thread getThread() {
		return this.thread;
	}

	@Override
	protected Runnable prepareRunnable(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean canRun(Runnable runnable) {
		return true;
	}

	public BlockRenderManager method_1541() {
		return this.field_1756;
	}

	public EntityRenderDispatcher method_1561() {
		return this.field_1731;
	}

	public ItemRenderer method_1480() {
		return this.field_1742;
	}

	public FirstPersonRenderer method_1489() {
		return this.field_1737;
	}

	public <T> SearchableContainer<T> method_1484(SearchManager.Key<T> key) {
		return this.field_1733.get(key);
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
		return this.field_1728.tickDelta;
	}

	public float getLastFrameDuration() {
		return this.field_1728.lastFrameDuration;
	}

	public BlockColors method_1505() {
		return this.field_1751;
	}

	public boolean hasReducedDebugInfo() {
		return this.field_1724 != null && this.field_1724.getReducedDebugInfo() || this.field_1690.reducedDebugInfo;
	}

	public ToastManager method_1566() {
		return this.field_1702;
	}

	public TutorialManager method_1577() {
		return this.field_1758;
	}

	public boolean isWindowFocused() {
		return this.windowFocused;
	}

	public HotbarStorage getCreativeHotbarStorage() {
		return this.creativeHotbarStorage;
	}

	public BakedModelManager method_1554() {
		return this.field_1763;
	}

	public FontManager method_1568() {
		return this.field_1708;
	}

	public PaintingManager method_18321() {
		return this.field_18008;
	}

	public StatusEffectSpriteManager method_18505() {
		return this.field_18173;
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

	public SplashTextResourceSupplier method_18095() {
		return this.field_17763;
	}

	@Nullable
	public Overlay method_18506() {
		return this.field_18175;
	}
}
