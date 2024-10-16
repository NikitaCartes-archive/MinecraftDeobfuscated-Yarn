package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.debug.PieChart;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.InactivityFpsLimiter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.session.Bans;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.MapDecorationsAtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.ClientSamplerSource;
import net.minecraft.client.util.CommandHistoryManager;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressTracker;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.MusicType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.KeybindTranslations;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.util.Nullables;
import net.minecraft.util.PathUtil;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.Urls;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.tick.TickManager;
import org.apache.commons.io.FileUtils;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

/**
 * Represents a logical Minecraft client.
 * The logical Minecraft client is responsible for rendering, sound playback and control input.
 * The Minecraft client also manages connections to a logical server which may be the client's {@link net.minecraft.server.integrated.IntegratedServer} or a remote server.
 * The Minecraft client instance may be obtained using {@link MinecraftClient#getInstance()}.
 * 
 * <p>Rendering on a Minecraft client is split into several facilities.
 * The primary entrypoint for rendering is {@link net.minecraft.client.render.GameRenderer#render}.
 * <div class="fabric"><table border=1>
 * <caption>Rendering facilities</caption>
 * <tr>
 *  <th><b>Thing to render</b></th> <th><b>Rendering facility</b></th>
 * </tr>
 * <tr>
 *  <td>World</td> <td>{@link net.minecraft.client.render.WorldRenderer}</td>
 * </tr>
 * <tr>
 *  <td>Blocks and Fluids</td> <td>{@link net.minecraft.client.render.block.BlockRenderManager}</td>
 * </tr>
 * <tr>
 *  <td>Entities</td> <td>{@link net.minecraft.client.render.entity.EntityRenderDispatcher}</td>
 * </tr>
 * <tr>
 *  <td>Block entities</td> <td>{@link net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher}</td>
 * </tr>
 * <tr>
 *  <td>Items</td> <td>{@link net.minecraft.client.render.item.ItemRenderer}</td>
 * </tr>
 * <tr>
 *  <td>Items held in hand</td> <td>{@link net.minecraft.client.render.item.HeldItemRenderer}</td>
 * </tr>
 * <tr>
 *  <td>Text</td> <td>{@link net.minecraft.client.font.TextRenderer}</td>
 * </tr>
 * <tr>
 *  <td>Game hud (health bar, hunger bar)</td> <td>{@link net.minecraft.client.gui.hud.InGameHud}</td>
 * </tr>
 * </table></div>
 * 
 * @see net.minecraft.server.integrated.IntegratedServer
 * @see net.minecraft.client.render.GameRenderer
 */
@Environment(EnvType.CLIENT)
public class MinecraftClient extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {
	static MinecraftClient instance;
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final boolean IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
	private static final int field_32145 = 10;
	public static final Identifier DEFAULT_FONT_ID = Identifier.ofVanilla("default");
	public static final Identifier UNICODE_FONT_ID = Identifier.ofVanilla("uniform");
	public static final Identifier ALT_TEXT_RENDERER_ID = Identifier.ofVanilla("alt");
	private static final Identifier REGIONAL_COMPLIANCIES_ID = Identifier.ofVanilla("regional_compliancies.json");
	private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
	private static final Text SOCIAL_INTERACTIONS_NOT_AVAILABLE = Text.translatable("multiplayer.socialInteractions.not_available");
	/**
	 * A message, in English, displayed in a dialog when a GLFW error is encountered.
	 * 
	 * @see net.minecraft.client.util.Window#throwGlError(int, long)
	 */
	public static final String GL_ERROR_DIALOGUE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
	private final long UNIVERSE = Double.doubleToLongBits(Math.PI);
	private final Path resourcePackDir;
	private final CompletableFuture<com.mojang.authlib.yggdrasil.ProfileResult> gameProfileFuture;
	private final TextureManager textureManager;
	private final ShaderLoader shaderLoader;
	private final DataFixer dataFixer;
	private final WindowProvider windowProvider;
	private final Window window;
	private final RenderTickCounter.Dynamic renderTickCounter = new RenderTickCounter.Dynamic(20.0F, 0L, this::getTargetMillisPerTick);
	private final BufferBuilderStorage bufferBuilders;
	public final WorldRenderer worldRenderer;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private final ItemRenderer itemRenderer;
	private final MapRenderer mapRenderer;
	public final ParticleManager particleManager;
	private final Session session;
	public final TextRenderer textRenderer;
	public final TextRenderer advanceValidatingTextRenderer;
	public final GameRenderer gameRenderer;
	public final DebugRenderer debugRenderer;
	private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker = new AtomicReference();
	public final InGameHud inGameHud;
	public final GameOptions options;
	private final HotbarStorage creativeHotbarStorage;
	public final Mouse mouse;
	public final Keyboard keyboard;
	private GuiNavigationType navigationType = GuiNavigationType.NONE;
	/**
	 * The directory that stores options, worlds, resource packs, logs, etc.
	 */
	public final File runDirectory;
	private final String gameVersion;
	private final String versionType;
	private final Proxy networkProxy;
	private final LevelStorage levelStorage;
	private final boolean isDemo;
	private final boolean multiplayerEnabled;
	private final boolean onlineChatEnabled;
	private final ReloadableResourceManagerImpl resourceManager;
	private final DefaultResourcePack defaultResourcePack;
	private final ServerResourcePackLoader serverResourcePackLoader;
	private final ResourcePackManager resourcePackManager;
	private final LanguageManager languageManager;
	private final BlockColors blockColors;
	private final ItemColors itemColors;
	private final Framebuffer framebuffer;
	@Nullable
	private final TracyFrameCapturer tracyFrameCapturer;
	private final SoundManager soundManager;
	private final MusicTracker musicTracker;
	private final FontManager fontManager;
	private final SplashTextResourceSupplier splashTextLoader;
	private final VideoWarningManager videoWarningManager;
	private final PeriodicNotificationManager regionalComplianciesManager = new PeriodicNotificationManager(
		REGIONAL_COMPLIANCIES_ID, MinecraftClient::isCountrySetTo
	);
	private final YggdrasilAuthenticationService authenticationService;
	private final MinecraftSessionService sessionService;
	private final UserApiService userApiService;
	private final CompletableFuture<UserProperties> userPropertiesFuture;
	private final PlayerSkinProvider skinProvider;
	private final BakedModelManager bakedModelManager;
	private final BlockRenderManager blockRenderManager;
	private final EquipmentModelLoader equipmentModelLoader;
	private final PaintingManager paintingManager;
	private final StatusEffectSpriteManager statusEffectSpriteManager;
	private final MapTextureManager mapTextureManager;
	private final MapDecorationsAtlasManager mapDecorationsAtlasManager;
	private final GuiAtlasManager guiAtlasManager;
	private final ToastManager toastManager;
	private final TutorialManager tutorialManager;
	private final SocialInteractionsManager socialInteractionsManager;
	private final EntityModelLoader entityModelLoader;
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
	private final TelemetryManager telemetryManager;
	private final ProfileKeys profileKeys;
	private final RealmsPeriodicCheckers realmsPeriodicCheckers;
	private final QuickPlayLogger quickPlayLogger;
	@Nullable
	public ClientPlayerInteractionManager interactionManager;
	/**
	 * Represents the world the client is currently viewing.
	 * This field is not null when in game.
	 */
	@Nullable
	public ClientWorld world;
	/**
	 * Represents the client's own player.
	 * This field is not null when in game.
	 */
	@Nullable
	public ClientPlayerEntity player;
	@Nullable
	private IntegratedServer server;
	/**
	 * The client connection to the integrated server.
	 * This is only used when connecting to the integrated server.
	 * 
	 * @see net.minecraft.client.gui.screen.multiplayer.ConnectScreen
	 */
	@Nullable
	private ClientConnection integratedServerConnection;
	private boolean integratedServerRunning;
	@Nullable
	public Entity cameraEntity;
	@Nullable
	public Entity targetedEntity;
	@Nullable
	public HitResult crosshairTarget;
	/**
	 * The cooldown for using items when {@linkplain net.minecraft.client.option.GameOptions#useKey the item use button} is held down.
	 */
	private int itemUseCooldown;
	protected int attackCooldown;
	private volatile boolean paused;
	private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
	private long nextDebugInfoUpdateTime;
	private int fpsCounter;
	public boolean skipGameRender;
	/**
	 * The Minecraft client's currently open screen.
	 * This field should only be used to get the current screen.
	 * For changing the screen, use {@link MinecraftClient#setScreen(Screen)}.
	 * 
	 * @see MinecraftClient#setScreen(Screen)
	 */
	@Nullable
	public Screen currentScreen;
	@Nullable
	private Overlay overlay;
	private boolean disconnecting;
	Thread thread;
	private volatile boolean running;
	@Nullable
	private Supplier<CrashReport> crashReportSupplier;
	private static int currentFps;
	public String fpsDebugString = "";
	private long renderTime;
	private final InactivityFpsLimiter inactivityFpsLimiter;
	public boolean wireFrame;
	public boolean debugChunkInfo;
	public boolean debugChunkOcclusion;
	public boolean chunkCullingEnabled = true;
	private boolean windowFocused;
	private final Queue<Runnable> renderTaskQueue = Queues.<Runnable>newConcurrentLinkedQueue();
	@Nullable
	private CompletableFuture<Void> resourceReloadFuture;
	@Nullable
	private TutorialToast socialInteractionsToast;
	private int trackingTick;
	private final TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, () -> this.trackingTick);
	private Recorder recorder = DummyRecorder.INSTANCE;
	private final ResourceReloadLogger resourceReloadLogger = new ResourceReloadLogger();
	private long metricsSampleDuration;
	private double gpuUtilizationPercentage;
	@Nullable
	private GlTimer.Query currentGlTimerQuery;
	private final NarratorManager narratorManager;
	private final MessageHandler messageHandler;
	private AbuseReportContext abuseReportContext;
	private final CommandHistoryManager commandHistoryManager;
	private final SymlinkFinder symlinkFinder;
	private boolean finishedLoading;
	private final long startTime;
	private long uptimeInTicks;

	public MinecraftClient(RunArgs args) {
		super("Client");
		instance = this;
		this.startTime = System.currentTimeMillis();
		this.runDirectory = args.directories.runDir;
		File file = args.directories.assetDir;
		this.resourcePackDir = args.directories.resourcePackDir.toPath();
		this.gameVersion = args.game.version;
		this.versionType = args.game.versionType;
		Path path = this.runDirectory.toPath();
		this.symlinkFinder = LevelStorage.createSymlinkFinder(path.resolve("allowed_symlinks.txt"));
		DefaultClientResourcePackProvider defaultClientResourcePackProvider = new DefaultClientResourcePackProvider(
			args.directories.getAssetDir(), this.symlinkFinder
		);
		this.serverResourcePackLoader = new ServerResourcePackLoader(this, path.resolve("downloads"), args.network);
		ResourcePackProvider resourcePackProvider = new FileResourcePackProvider(
			this.resourcePackDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE, this.symlinkFinder
		);
		this.resourcePackManager = new ResourcePackManager(
			defaultClientResourcePackProvider, this.serverResourcePackLoader.getPassthroughPackProvider(), resourcePackProvider
		);
		this.defaultResourcePack = defaultClientResourcePackProvider.getResourcePack();
		this.networkProxy = args.network.netProxy;
		this.authenticationService = new YggdrasilAuthenticationService(this.networkProxy);
		this.sessionService = this.authenticationService.createMinecraftSessionService();
		this.session = args.network.session;
		this.gameProfileFuture = CompletableFuture.supplyAsync(
			() -> this.sessionService.fetchProfile(this.session.getUuidOrNull(), true), Util.getDownloadWorkerExecutor()
		);
		this.userApiService = this.createUserApiService(this.authenticationService, args);
		this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return this.userApiService.fetchProperties();
			} catch (AuthenticationException var2x) {
				LOGGER.error("Failed to fetch user properties", (Throwable)var2x);
				return UserApiService.OFFLINE_PROPERTIES;
			}
		}, Util.getDownloadWorkerExecutor());
		LOGGER.info("Setting user: {}", this.session.getUsername());
		LOGGER.debug("(Session ID is {})", this.session.getSessionId());
		this.isDemo = args.game.demo;
		this.multiplayerEnabled = !args.game.multiplayerDisabled;
		this.onlineChatEnabled = !args.game.onlineChatDisabled;
		this.server = null;
		KeybindTranslations.setFactory(KeyBinding::getLocalizedName);
		this.dataFixer = Schemas.getFixer();
		this.toastManager = new ToastManager(this);
		this.thread = Thread.currentThread();
		this.options = new GameOptions(this, this.runDirectory);
		RenderSystem.setShaderGlintAlpha(this.options.getGlintStrength().getValue());
		this.running = true;
		this.tutorialManager = new TutorialManager(this, this.options);
		this.creativeHotbarStorage = new HotbarStorage(path, this.dataFixer);
		LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
		WindowSettings windowSettings;
		if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
			windowSettings = new WindowSettings(
				this.options.overrideWidth,
				this.options.overrideHeight,
				args.windowSettings.fullscreenWidth,
				args.windowSettings.fullscreenHeight,
				args.windowSettings.fullscreen
			);
		} else {
			windowSettings = args.windowSettings;
		}

		Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
		this.windowProvider = new WindowProvider(this);
		this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, this.getWindowTitle());
		this.onWindowFocusChanged(true);
		this.window.setCloseCallback(new Runnable() {
			private boolean closed;

			public void run() {
				if (!this.closed) {
					this.closed = true;
					ClientWatchdog.shutdownClient(args.directories.runDir, MinecraftClient.this.thread.threadId());
				}
			}
		});
		GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS);

		try {
			this.window.setIcon(this.defaultResourcePack, SharedConstants.getGameVersion().isStable() ? Icons.RELEASE : Icons.SNAPSHOT);
		} catch (IOException var13) {
			LOGGER.error("Couldn't set icon", (Throwable)var13);
		}

		this.mouse = new Mouse(this);
		this.mouse.setup(this.window.getHandle());
		this.keyboard = new Keyboard(this);
		this.keyboard.setup(this.window.getHandle());
		RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
		this.framebuffer = new WindowFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.framebuffer.clear();
		this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
		this.resourcePackManager.scanPacks();
		this.options.addResourcePackProfilesToManager(this.resourcePackManager);
		this.languageManager = new LanguageManager(this.options.language, translationStorage -> {
			if (this.player != null) {
				this.player.networkHandler.refreshSearchManager();
			}
		});
		this.resourceManager.registerReloader(this.languageManager);
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.registerReloader(this.textureManager);
		this.shaderLoader = new ShaderLoader(this.textureManager, this::onShaderResourceReloadFailure);
		this.resourceManager.registerReloader(this.shaderLoader);
		this.skinProvider = new PlayerSkinProvider(this.textureManager, file.toPath().resolve("skins"), this.sessionService, this);
		this.levelStorage = new LevelStorage(path.resolve("saves"), path.resolve("backups"), this.symlinkFinder, this.dataFixer);
		this.commandHistoryManager = new CommandHistoryManager(path);
		this.soundManager = new SoundManager(this.options);
		this.resourceManager.registerReloader(this.soundManager);
		this.splashTextLoader = new SplashTextResourceSupplier(this.session);
		this.resourceManager.registerReloader(this.splashTextLoader);
		this.musicTracker = new MusicTracker(this);
		this.fontManager = new FontManager(this.textureManager);
		this.textRenderer = this.fontManager.createTextRenderer();
		this.advanceValidatingTextRenderer = this.fontManager.createAdvanceValidatingTextRenderer();
		this.resourceManager.registerReloader(this.fontManager);
		this.onFontOptionsChanged();
		this.resourceManager.registerReloader(new GrassColormapResourceSupplier());
		this.resourceManager.registerReloader(new FoliageColormapResourceSupplier());
		this.window.setPhase("Startup");
		RenderSystem.setupDefaultState(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.window.setPhase("Post startup");
		this.blockColors = BlockColors.create();
		this.itemColors = ItemColors.create(this.blockColors);
		this.bakedModelManager = new BakedModelManager(this.textureManager, this.blockColors, this.options.getMipmapLevels().getValue());
		this.resourceManager.registerReloader(this.bakedModelManager);
		this.entityModelLoader = new EntityModelLoader();
		this.resourceManager.registerReloader(this.entityModelLoader);
		this.equipmentModelLoader = new EquipmentModelLoader();
		this.resourceManager.registerReloader(this.equipmentModelLoader);
		this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(
			this.textRenderer, this.entityModelLoader, this::getBlockRenderManager, this::getItemRenderer, this::getEntityRenderDispatcher
		);
		this.resourceManager.registerReloader(this.blockEntityRenderDispatcher);
		BuiltinModelItemRenderer builtinModelItemRenderer = new BuiltinModelItemRenderer(this.blockEntityRenderDispatcher, this.entityModelLoader);
		this.resourceManager.registerReloader(builtinModelItemRenderer);
		this.itemRenderer = new ItemRenderer(this.bakedModelManager, this.itemColors, builtinModelItemRenderer);
		this.resourceManager.registerReloader(this.itemRenderer);
		this.mapTextureManager = new MapTextureManager(this.textureManager);
		this.mapDecorationsAtlasManager = new MapDecorationsAtlasManager(this.textureManager);
		this.resourceManager.registerReloader(this.mapDecorationsAtlasManager);
		this.mapRenderer = new MapRenderer(this.mapDecorationsAtlasManager, this.mapTextureManager);

		try {
			int i = Runtime.getRuntime().availableProcessors();
			Tessellator.initialize();
			this.bufferBuilders = new BufferBuilderStorage(i);
		} catch (OutOfMemoryError var12) {
			TinyFileDialogs.tinyfd_messageBox(
				"Minecraft",
				"Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: "
					+ Urls.MINECRAFT_SUPPORT,
				"ok",
				"error",
				true
			);
			throw new GlException("Unable to allocate render buffers", var12);
		}

		this.socialInteractionsManager = new SocialInteractionsManager(this, this.userApiService);
		this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), builtinModelItemRenderer, this.blockColors);
		this.resourceManager.registerReloader(this.blockRenderManager);
		this.entityRenderDispatcher = new EntityRenderDispatcher(
			this,
			this.textureManager,
			this.itemRenderer,
			this.mapRenderer,
			this.blockRenderManager,
			this.textRenderer,
			this.options,
			this.entityModelLoader,
			this.equipmentModelLoader
		);
		this.resourceManager.registerReloader(this.entityRenderDispatcher);
		this.particleManager = new ParticleManager(this.world, this.textureManager);
		this.resourceManager.registerReloader(this.particleManager);
		this.paintingManager = new PaintingManager(this.textureManager);
		this.resourceManager.registerReloader(this.paintingManager);
		this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
		this.resourceManager.registerReloader(this.statusEffectSpriteManager);
		this.guiAtlasManager = new GuiAtlasManager(this.textureManager);
		this.resourceManager.registerReloader(this.guiAtlasManager);
		this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getHeldItemRenderer(), this.resourceManager, this.bufferBuilders);
		this.worldRenderer = new WorldRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.bufferBuilders);
		this.resourceManager.registerReloader(this.worldRenderer);
		this.resourceManager.registerReloader(this.worldRenderer.getCloudRenderer());
		this.videoWarningManager = new VideoWarningManager();
		this.resourceManager.registerReloader(this.videoWarningManager);
		this.resourceManager.registerReloader(this.regionalComplianciesManager);
		this.inGameHud = new InGameHud(this);
		this.debugRenderer = new DebugRenderer(this);
		RealmsClient realmsClient = RealmsClient.createRealmsClient(this);
		this.realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
		RenderSystem.setErrorCallback(this::handleGlErrorByDisableVsync);
		if (this.framebuffer.textureWidth != this.window.getFramebufferWidth() || this.framebuffer.textureHeight != this.window.getFramebufferHeight()) {
			StringBuilder stringBuilder = new StringBuilder(
				"Recovering from unsupported resolution ("
					+ this.window.getFramebufferWidth()
					+ "x"
					+ this.window.getFramebufferHeight()
					+ ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."
			);
			if (GlDebug.isDebugMessageEnabled()) {
				stringBuilder.append("\n\nReported GL debug messages:\n").append(String.join("\n", GlDebug.collectDebugMessages()));
			}

			this.window.setWindowedSize(this.framebuffer.textureWidth, this.framebuffer.textureHeight);
			TinyFileDialogs.tinyfd_messageBox("Minecraft", stringBuilder.toString(), "ok", "error", false);
		} else if (this.options.getFullscreen().getValue() && !this.window.isFullscreen()) {
			this.window.toggleFullscreen();
			this.options.getFullscreen().setValue(this.window.isFullscreen());
		}

		this.window.setVsync(this.options.getEnableVsync().getValue());
		this.window.setRawMouseMotion(this.options.getRawMouseInput().getValue());
		this.window.logOnGlError();
		this.onResolutionChanged();
		this.gameRenderer.preloadPrograms(this.defaultResourcePack.getFactory());
		this.telemetryManager = new TelemetryManager(this, this.userApiService, this.session);
		this.profileKeys = ProfileKeys.create(this.userApiService, this.session, path);
		this.narratorManager = new NarratorManager(this);
		this.narratorManager.checkNarratorLibrary(this.options.getNarrator().getValue() != NarratorMode.OFF);
		this.messageHandler = new MessageHandler(this);
		this.messageHandler.setChatDelay(this.options.getChatDelay().getValue());
		this.abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), this.userApiService);
		SplashOverlay.init(this);
		this.setScreen(new MessageScreen(Text.translatable("gui.loadingMinecraft")));
		List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
		this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.INITIAL, list);
		ResourceReload resourceReload = this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), this, COMPLETED_UNIT_FUTURE, list);
		GameLoadTimeEvent.INSTANCE.startTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
		MinecraftClient.LoadingContext loadingContext = new MinecraftClient.LoadingContext(realmsClient, args.quickPlay);
		this.setOverlay(
			new SplashOverlay(
				this, resourceReload, error -> Util.ifPresentOrElse(error, throwable -> this.handleResourceReloadException(throwable, loadingContext), () -> {
						if (SharedConstants.isDevelopment) {
							this.checkGameData();
						}

						this.resourceReloadLogger.finish();
						this.onFinishedLoading(loadingContext);
					}), false
			)
		);
		this.quickPlayLogger = QuickPlayLogger.create(args.quickPlay.path());
		this.inactivityFpsLimiter = new InactivityFpsLimiter(this.options, this);
		if (TracyClient.isAvailable() && args.game.tracyEnabled) {
			this.tracyFrameCapturer = new TracyFrameCapturer();
		} else {
			this.tracyFrameCapturer = null;
		}
	}

	private void onFinishedLoading(@Nullable MinecraftClient.LoadingContext loadingContext) {
		if (!this.finishedLoading) {
			this.finishedLoading = true;
			this.collectLoadTimes(loadingContext);
		}
	}

	private void collectLoadTimes(@Nullable MinecraftClient.LoadingContext loadingContext) {
		Runnable runnable = this.onInitFinished(loadingContext);
		GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
		GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS);
		GameLoadTimeEvent.INSTANCE.send(this.telemetryManager.getSender());
		runnable.run();
	}

	public boolean isFinishedLoading() {
		return this.finishedLoading;
	}

	private Runnable onInitFinished(@Nullable MinecraftClient.LoadingContext loadingContext) {
		List<Function<Runnable, Screen>> list = new ArrayList();
		this.createInitScreens(list);
		Runnable runnable = () -> {
			if (loadingContext != null && loadingContext.quickPlayData().isEnabled()) {
				QuickPlay.startQuickPlay(this, loadingContext.quickPlayData(), loadingContext.realmsClient());
			} else {
				this.setScreen(new TitleScreen(true));
			}
		};

		for (Function<Runnable, Screen> function : Lists.reverse(list)) {
			Screen screen = (Screen)function.apply(runnable);
			runnable = () -> this.setScreen(screen);
		}

		return runnable;
	}

	private void createInitScreens(List<Function<Runnable, Screen>> list) {
		if (this.options.onboardAccessibility) {
			list.add((Function)onClose -> new AccessibilityOnboardingScreen(this.options, onClose));
		}

		BanDetails banDetails = this.getMultiplayerBanDetails();
		if (banDetails != null) {
			list.add((Function)onClose -> Bans.createBanScreen(confirmed -> {
					if (confirmed) {
						Util.getOperatingSystem().open(Urls.JAVA_MODERATION);
					}

					onClose.run();
				}, banDetails));
		}

		com.mojang.authlib.yggdrasil.ProfileResult profileResult = (com.mojang.authlib.yggdrasil.ProfileResult)this.gameProfileFuture.join();
		if (profileResult != null) {
			GameProfile gameProfile = profileResult.profile();
			Set<ProfileActionType> set = profileResult.actions();
			if (set.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
				list.add((Function)onClose -> Bans.createUsernameBanScreen(gameProfile.getName(), onClose));
			}

			if (set.contains(ProfileActionType.USING_BANNED_SKIN)) {
				list.add(Bans::createSkinBanScreen);
			}
		}
	}

	private static boolean isCountrySetTo(Object country) {
		try {
			return Locale.getDefault().getISO3Country().equals(country);
		} catch (MissingResourceException var2) {
			return false;
		}
	}

	public void updateWindowTitle() {
		this.window.setTitle(this.getWindowTitle());
	}

	private String getWindowTitle() {
		StringBuilder stringBuilder = new StringBuilder("Minecraft");
		if (getModStatus().isModded()) {
			stringBuilder.append("*");
		}

		stringBuilder.append(" ");
		stringBuilder.append(SharedConstants.getGameVersion().getName());
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
			stringBuilder.append(" - ");
			ServerInfo serverInfo = this.getCurrentServerEntry();
			if (this.server != null && !this.server.isRemote()) {
				stringBuilder.append(I18n.translate("title.singleplayer"));
			} else if (serverInfo != null && serverInfo.isRealm()) {
				stringBuilder.append(I18n.translate("title.multiplayer.realms"));
			} else if (this.server == null && (serverInfo == null || !serverInfo.isLocal())) {
				stringBuilder.append(I18n.translate("title.multiplayer.other"));
			} else {
				stringBuilder.append(I18n.translate("title.multiplayer.lan"));
			}
		}

		return stringBuilder.toString();
	}

	private UserApiService createUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs) {
		return runArgs.network.session.getAccountType() != Session.AccountType.MSA
			? UserApiService.OFFLINE
			: authService.createUserApiService(runArgs.network.session.getAccessToken());
	}

	public static ModStatus getModStatus() {
		return ModStatus.check("vanilla", ClientBrandRetriever::getClientModName, "Client", MinecraftClient.class);
	}

	private void handleResourceReloadException(Throwable throwable, @Nullable MinecraftClient.LoadingContext loadingContext) {
		if (this.resourcePackManager.getEnabledIds().size() > 1) {
			this.onResourceReloadFailure(throwable, null, loadingContext);
		} else {
			Util.throwUnchecked(throwable);
		}
	}

	public void onResourceReloadFailure(Throwable exception, @Nullable Text resourceName, @Nullable MinecraftClient.LoadingContext loadingContext) {
		LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", exception);
		this.resourceReloadLogger.recover(exception);
		this.serverResourcePackLoader.onReloadFailure();
		this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
		this.options.resourcePacks.clear();
		this.options.incompatibleResourcePacks.clear();
		this.options.write();
		this.reloadResources(true, loadingContext).thenRun(() -> this.showResourceReloadFailureToast(resourceName));
	}

	private void onForcedResourceReloadFailure() {
		this.setOverlay(null);
		if (this.world != null) {
			this.world.disconnect();
			this.disconnect();
		}

		this.setScreen(new TitleScreen());
		this.showResourceReloadFailureToast(null);
	}

	private void showResourceReloadFailureToast(@Nullable Text description) {
		ToastManager toastManager = this.getToastManager();
		SystemToast.show(toastManager, SystemToast.Type.PACK_LOAD_FAILURE, Text.translatable("resourcePack.load_fail"), description);
	}

	public void onShaderResourceReloadFailure(Exception exception) {
		if (!this.resourcePackManager.hasOptionalProfilesEnabled()) {
			if (this.resourcePackManager.getEnabledIds().size() <= 1) {
				LOGGER.error(LogUtils.FATAL_MARKER, exception.getMessage(), (Throwable)exception);
				this.printCrashReport(new CrashReport(exception.getMessage(), exception));
			} else {
				this.send(this::onForcedResourceReloadFailure);
			}
		} else {
			this.onResourceReloadFailure(exception, Text.translatable("resourcePack.runtime_failure"), null);
		}
	}

	public void run() {
		this.thread = Thread.currentThread();
		if (Runtime.getRuntime().availableProcessors() > 4) {
			this.thread.setPriority(10);
		}

		DiscontinuousFrame discontinuousFrame = TracyClient.createDiscontinuousFrame("Client Tick");

		try {
			boolean bl = false;

			while (this.running) {
				this.printCrashReport();

				try {
					TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create("Renderer");
					boolean bl2 = this.getDebugHud().shouldShowRenderingChart();

					try (Profilers.Scoped scoped = Profilers.using(this.startMonitor(bl2, tickDurationMonitor))) {
						this.recorder.startTick();
						discontinuousFrame.start();
						this.render(!bl);
						discontinuousFrame.end();
						this.recorder.endTick();
					}

					this.endMonitor(bl2, tickDurationMonitor);
				} catch (OutOfMemoryError var10) {
					if (bl) {
						throw var10;
					}

					this.cleanUpAfterCrash();
					this.setScreen(new OutOfMemoryScreen());
					System.gc();
					LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)var10);
					bl = true;
				}
			}
		} catch (CrashException var11) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)var11);
			this.printCrashReport(var11.getReport());
		} catch (Throwable var12) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", var12);
			this.printCrashReport(new CrashReport("Unexpected error", var12));
		}
	}

	public void onFontOptionsChanged() {
		this.fontManager.setActiveFilters(this.options);
	}

	private void handleGlErrorByDisableVsync(int error, long description) {
		this.options.getEnableVsync().setValue(false);
		this.options.write();
	}

	public Framebuffer getFramebuffer() {
		return this.framebuffer;
	}

	public String getGameVersion() {
		return this.gameVersion;
	}

	public String getVersionType() {
		return this.versionType;
	}

	public void setCrashReportSupplierAndAddDetails(CrashReport crashReport) {
		this.crashReportSupplier = () -> this.addDetailsToCrashReport(crashReport);
	}

	public void setCrashReportSupplier(CrashReport crashReport) {
		this.crashReportSupplier = () -> crashReport;
	}

	private void printCrashReport() {
		if (this.crashReportSupplier != null) {
			printCrashReport(this, this.runDirectory, (CrashReport)this.crashReportSupplier.get());
		}
	}

	public void printCrashReport(CrashReport crashReport) {
		CrashMemoryReserve.releaseMemory();
		CrashReport crashReport2 = this.addDetailsToCrashReport(crashReport);
		this.cleanUpAfterCrash();
		printCrashReport(this, this.runDirectory, crashReport2);
	}

	public static int saveCrashReport(File runDir, CrashReport crashReport) {
		Path path = runDir.toPath().resolve("crash-reports");
		Path path2 = path.resolve("crash-" + Util.getFormattedCurrentTime() + "-client.txt");
		Bootstrap.println(crashReport.asString(ReportType.MINECRAFT_CRASH_REPORT));
		if (crashReport.getFile() != null) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile().toAbsolutePath());
			return -1;
		} else if (crashReport.writeToFile(path2, ReportType.MINECRAFT_CRASH_REPORT)) {
			Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + path2.toAbsolutePath());
			return -1;
		} else {
			Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
			return -2;
		}
	}

	public static void printCrashReport(@Nullable MinecraftClient client, File runDirectory, CrashReport crashReport) {
		int i = saveCrashReport(runDirectory, crashReport);
		if (client != null) {
			client.soundManager.stopAbruptly();
		}

		System.exit(i);
	}

	public boolean forcesUnicodeFont() {
		return this.options.getForceUnicodeFont().getValue();
	}

	public CompletableFuture<Void> reloadResources() {
		return this.reloadResources(false, null);
	}

	private CompletableFuture<Void> reloadResources(boolean force, @Nullable MinecraftClient.LoadingContext loadingContext) {
		if (this.resourceReloadFuture != null) {
			return this.resourceReloadFuture;
		} else {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			if (!force && this.overlay instanceof SplashOverlay) {
				this.resourceReloadFuture = completableFuture;
				return completableFuture;
			} else {
				this.resourcePackManager.scanPacks();
				List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
				if (!force) {
					this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
				}

				this.setOverlay(
					new SplashOverlay(
						this,
						this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), this, COMPLETED_UNIT_FUTURE, list),
						error -> Util.ifPresentOrElse(error, throwable -> {
								if (force) {
									this.serverResourcePackLoader.onForcedReloadFailure();
									this.onForcedResourceReloadFailure();
								} else {
									this.handleResourceReloadException(throwable, loadingContext);
								}
							}, () -> {
								this.worldRenderer.reload();
								this.resourceReloadLogger.finish();
								this.serverResourcePackLoader.onReloadSuccess();
								completableFuture.complete(null);
								this.onFinishedLoading(loadingContext);
							}),
						!force
					)
				);
				return completableFuture;
			}
		}
	}

	private void checkGameData() {
		boolean bl = false;
		BlockModels blockModels = this.getBlockRenderManager().getModels();
		BakedModel bakedModel = blockModels.getModelManager().getMissingModel();

		for (Block block : Registries.BLOCK) {
			for (BlockState blockState : block.getStateManager().getStates()) {
				if (blockState.getRenderType() == BlockRenderType.MODEL) {
					BakedModel bakedModel2 = blockModels.getModel(blockState);
					if (bakedModel2 == bakedModel) {
						LOGGER.debug("Missing model for: {}", blockState);
						bl = true;
					}
				}
			}
		}

		Sprite sprite = bakedModel.getParticleSprite();

		for (Block block2 : Registries.BLOCK) {
			for (BlockState blockState2 : block2.getStateManager().getStates()) {
				Sprite sprite2 = blockModels.getModelParticleSprite(blockState2);
				if (!blockState2.isAir() && sprite2 == sprite) {
					LOGGER.debug("Missing particle icon for: {}", blockState2);
				}
			}
		}

		Registries.ITEM.streamEntries().forEach(reference -> {
			Item item = (Item)reference.value();
			String string = item.getTranslationKey();
			String string2 = Text.translatable(string).getString();
			if (string2.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) {
				LOGGER.debug("Missing translation for: {} {} {}", reference.registryKey().getValue(), string, item);
			}
		});
		bl |= HandledScreens.isMissingScreens();
		bl |= EntityRenderers.isMissingRendererFactories();
		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	private void openChatScreen(String text) {
		MinecraftClient.ChatRestriction chatRestriction = this.getChatRestriction();
		if (!chatRestriction.allowsChat(this.isInSingleplayer())) {
			if (this.inGameHud.shouldShowChatDisabledScreen()) {
				this.inGameHud.setCanShowChatDisabledScreen(false);
				this.setScreen(new ConfirmLinkScreen(confirmed -> {
					if (confirmed) {
						Util.getOperatingSystem().open(Urls.JAVA_ACCOUNT_SETTINGS);
					}

					this.setScreen(null);
				}, MinecraftClient.ChatRestriction.MORE_INFO_TEXT, Urls.JAVA_ACCOUNT_SETTINGS, true));
			} else {
				Text text2 = chatRestriction.getDescription();
				this.inGameHud.setOverlayMessage(text2, false);
				this.narratorManager.narrate(text2);
				this.inGameHud.setCanShowChatDisabledScreen(chatRestriction == MinecraftClient.ChatRestriction.DISABLED_BY_PROFILE);
			}
		} else {
			this.setScreen(new ChatScreen(text));
		}
	}

	/**
	 * Sets the current screen to a new screen.
	 * 
	 * <p>If the screen being opened is {@code null}:
	 * <ul>
	 * <li>if the client is not in game, the title screen will be opened</li>
	 * <li>if the {@linkplain #player} is dead, the death screen will be opened</li>
	 * </ul>
	 * 
	 * <p>If there is an open screen when the current screen is changed, {@link Screen#removed()}
	 * will be called on it to notify it of the closing.
	 * 
	 * @param screen the new screen, or {@code null} to just close the previous screen
	 */
	public void setScreen(@Nullable Screen screen) {
		if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
			LOGGER.error("setScreen called from non-game thread");
		}

		if (this.currentScreen != null) {
			this.currentScreen.removed();
		} else {
			this.setNavigationType(GuiNavigationType.NONE);
		}

		if (screen == null && this.disconnecting) {
			throw new IllegalStateException("Trying to return to in-game GUI during disconnection");
		} else {
			if (screen == null && this.world == null) {
				screen = new TitleScreen();
			} else if (screen == null && this.player.isDead()) {
				if (this.player.showsDeathScreen()) {
					screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
				} else {
					this.player.requestRespawn();
				}
			}

			this.currentScreen = screen;
			if (this.currentScreen != null) {
				this.currentScreen.onDisplayed();
			}

			BufferRenderer.reset();
			if (screen != null) {
				this.mouse.unlockCursor();
				KeyBinding.unpressAll();
				screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
				this.skipGameRender = false;
			} else {
				this.soundManager.resumeAll();
				this.mouse.lockCursor();
			}

			this.updateWindowTitle();
		}
	}

	public void setOverlay(@Nullable Overlay overlay) {
		this.overlay = overlay;
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");

			try {
				this.narratorManager.destroy();
			} catch (Throwable var7) {
			}

			try {
				if (this.world != null) {
					this.world.disconnect();
				}

				this.disconnect();
			} catch (Throwable var6) {
			}

			if (this.currentScreen != null) {
				this.currentScreen.removed();
			}

			this.close();
		} finally {
			Util.nanoTimeSupplier = System::nanoTime;
			if (this.crashReportSupplier == null) {
				System.exit(0);
			}
		}
	}

	@Override
	public void close() {
		if (this.currentGlTimerQuery != null) {
			this.currentGlTimerQuery.close();
		}

		try {
			this.telemetryManager.close();
			this.regionalComplianciesManager.close();
			this.bakedModelManager.close();
			this.fontManager.close();
			this.gameRenderer.close();
			this.shaderLoader.close();
			this.worldRenderer.close();
			this.soundManager.close();
			this.particleManager.clearAtlas();
			this.statusEffectSpriteManager.close();
			this.paintingManager.close();
			this.mapDecorationsAtlasManager.close();
			this.guiAtlasManager.close();
			this.mapTextureManager.close();
			this.textureManager.close();
			this.resourceManager.close();
			if (this.tracyFrameCapturer != null) {
				this.tracyFrameCapturer.close();
			}

			FreeTypeUtil.release();
			Util.shutdownExecutors();
		} catch (Throwable var5) {
			LOGGER.error("Shutdown failure!", var5);
			throw var5;
		} finally {
			this.windowProvider.close();
			this.window.close();
		}
	}

	private void render(boolean tick) {
		this.window.setPhase("Pre render");
		if (this.window.shouldClose()) {
			this.scheduleStop();
		}

		if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashOverlay)) {
			CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
			this.resourceReloadFuture = null;
			this.reloadResources().thenRun(() -> completableFuture.complete(null));
		}

		Runnable runnable;
		while ((runnable = (Runnable)this.renderTaskQueue.poll()) != null) {
			runnable.run();
		}

		int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs(), tick);
		Profiler profiler = Profilers.get();
		if (tick) {
			profiler.push("scheduledExecutables");
			this.runTasks();
			profiler.pop();
			profiler.push("tick");

			for (int j = 0; j < Math.min(10, i); j++) {
				profiler.visit("clientTick");
				this.tick();
			}

			profiler.pop();
		}

		this.window.setPhase("Render");
		profiler.push("sound");
		this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
		profiler.swap("toasts");
		this.toastManager.update();
		profiler.swap("render");
		long l = Util.getMeasuringTimeNano();
		boolean bl;
		if (!this.getDebugHud().shouldShowDebugHud() && !this.recorder.isActive()) {
			bl = false;
			this.gpuUtilizationPercentage = 0.0;
		} else {
			bl = this.currentGlTimerQuery == null || this.currentGlTimerQuery.isResultAvailable();
			if (bl) {
				GlTimer.getInstance().ifPresent(GlTimer::beginProfile);
			}
		}

		RenderSystem.clear(16640);
		this.framebuffer.beginWrite(true);
		RenderSystem.setShaderFog(Fog.DUMMY);
		profiler.push("display");
		RenderSystem.enableCull();
		profiler.swap("mouse");
		this.mouse.tick();
		profiler.pop();
		if (!this.skipGameRender) {
			profiler.swap("gameRenderer");
			this.gameRenderer.render(this.renderTickCounter, tick);
			profiler.pop();
		}

		profiler.push("blit");
		this.framebuffer.endWrite();
		this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.renderTime = Util.getMeasuringTimeNano() - l;
		if (bl) {
			GlTimer.getInstance().ifPresent(glTimer -> this.currentGlTimerQuery = glTimer.endProfile());
		}

		profiler.swap("updateDisplay");
		if (this.tracyFrameCapturer != null) {
			this.tracyFrameCapturer.upload();
			this.tracyFrameCapturer.capture(this.framebuffer);
		}

		this.window.swapBuffers(this.tracyFrameCapturer);
		int k = this.inactivityFpsLimiter.update();
		if (k < 260) {
			RenderSystem.limitDisplayFPS(k);
		}

		profiler.swap("yield");
		Thread.yield();
		profiler.pop();
		this.window.setPhase("Post render");
		this.fpsCounter++;
		this.paused = this.isIntegratedServerRunning()
			&& (this.currentScreen != null && this.currentScreen.shouldPause() || this.overlay != null && this.overlay.pausesGame())
			&& !this.server.isRemote();
		this.renderTickCounter.tick(this.paused);
		this.renderTickCounter.setTickFrozen(!this.shouldTick());
		long m = Util.getMeasuringTimeNano();
		long n = m - this.lastMetricsSampleTime;
		if (bl) {
			this.metricsSampleDuration = n;
		}

		this.getDebugHud().pushToFrameLog(n);
		this.lastMetricsSampleTime = m;
		profiler.push("fpsUpdate");
		if (this.currentGlTimerQuery != null && this.currentGlTimerQuery.isResultAvailable()) {
			this.gpuUtilizationPercentage = (double)this.currentGlTimerQuery.queryResult() * 100.0 / (double)this.metricsSampleDuration;
		}

		while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
			String string;
			if (this.gpuUtilizationPercentage > 0.0) {
				string = " GPU: " + (this.gpuUtilizationPercentage > 100.0 ? Formatting.RED + "100%" : Math.round(this.gpuUtilizationPercentage) + "%");
			} else {
				string = "";
			}

			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				Locale.ROOT,
				"%d fps T: %s%s%s%s B: %d%s",
				currentFps,
				k == 260 ? "inf" : k,
				this.options.getEnableVsync().getValue() ? " vsync " : " ",
				this.options.getGraphicsMode().getValue(),
				this.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF
					? ""
					: (this.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"),
				this.options.getBiomeBlendRadius().getValue(),
				string
			);
			this.nextDebugInfoUpdateTime += 1000L;
			this.fpsCounter = 0;
		}

		profiler.pop();
	}

	private Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
		if (!active) {
			this.tickTimeTracker.disable();
			if (!this.recorder.isActive() && monitor == null) {
				return DummyProfiler.INSTANCE;
			}
		}

		Profiler profiler;
		if (active) {
			if (!this.tickTimeTracker.isActive()) {
				this.trackingTick = 0;
				this.tickTimeTracker.enable();
			}

			this.trackingTick++;
			profiler = this.tickTimeTracker.getProfiler();
		} else {
			profiler = DummyProfiler.INSTANCE;
		}

		if (this.recorder.isActive()) {
			profiler = Profiler.union(profiler, this.recorder.getProfiler());
		}

		return TickDurationMonitor.tickProfiler(profiler, monitor);
	}

	private void endMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
		if (monitor != null) {
			monitor.endTick();
		}

		PieChart pieChart = this.getDebugHud().getPieChart();
		if (active) {
			pieChart.setProfileResult(this.tickTimeTracker.getResult());
		} else {
			pieChart.setProfileResult(null);
		}
	}

	@Override
	public void onResolutionChanged() {
		int i = this.window.calculateScaleFactor(this.options.getGuiScale().getValue(), this.forcesUnicodeFont());
		this.window.setScaleFactor((double)i);
		if (this.currentScreen != null) {
			this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
		}

		Framebuffer framebuffer = this.getFramebuffer();
		framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		this.mouse.onResolutionChanged();
	}

	@Override
	public void onCursorEnterChanged() {
		this.mouse.setResolutionChanged();
	}

	public int getCurrentFps() {
		return currentFps;
	}

	public long getRenderTime() {
		return this.renderTime;
	}

	private void cleanUpAfterCrash() {
		CrashMemoryReserve.releaseMemory();

		try {
			if (this.integratedServerRunning && this.server != null) {
				this.server.stop(true);
			}

			this.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
		} catch (Throwable var2) {
		}

		System.gc();
	}

	public boolean toggleDebugProfiler(Consumer<Text> chatMessageSender) {
		if (this.recorder.isActive()) {
			this.stopRecorder();
			return false;
		} else {
			Consumer<ProfileResult> consumer = result -> {
				if (result != EmptyProfileResult.INSTANCE) {
					int i = result.getTickSpan();
					double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
					this.execute(
						() -> chatMessageSender.accept(
								Text.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d))
							)
					);
				}
			};
			Consumer<Path> consumer2 = path -> {
				Text text = Text.literal(path.toString())
					.formatted(Formatting.UNDERLINE)
					.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toFile().getParent())));
				this.execute(() -> chatMessageSender.accept(Text.translatable("debug.profiling.stop", text)));
			};
			SystemDetails systemDetails = addSystemDetailsToCrashReport(new SystemDetails(), this, this.languageManager, this.gameVersion, this.options);
			Consumer<List<Path>> consumer3 = files -> {
				Path path = this.saveProfilingResult(systemDetails, files);
				consumer2.accept(path);
			};
			Consumer<Path> consumer4;
			if (this.server == null) {
				consumer4 = path -> consumer3.accept(ImmutableList.of(path));
			} else {
				this.server.addSystemDetails(systemDetails);
				CompletableFuture<Path> completableFuture = new CompletableFuture();
				CompletableFuture<Path> completableFuture2 = new CompletableFuture();
				CompletableFuture.allOf(completableFuture, completableFuture2)
					.thenRunAsync(() -> consumer3.accept(ImmutableList.of((Path)completableFuture.join(), (Path)completableFuture2.join())), Util.getIoWorkerExecutor());
				this.server.setupRecorder(result -> {
				}, completableFuture2::complete);
				consumer4 = completableFuture::complete;
			}

			this.recorder = DebugRecorder.of(
				new ClientSamplerSource(Util.nanoTimeSupplier, this.worldRenderer),
				Util.nanoTimeSupplier,
				Util.getIoWorkerExecutor(),
				new RecordDumper("client"),
				result -> {
					this.recorder = DummyRecorder.INSTANCE;
					consumer.accept(result);
				},
				consumer4
			);
			return true;
		}
	}

	private void stopRecorder() {
		this.recorder.stop();
		if (this.server != null) {
			this.server.stopRecorder();
		}
	}

	private void forceStopRecorder() {
		this.recorder.forceStop();
		if (this.server != null) {
			this.server.forceStopRecorder();
		}
	}

	private Path saveProfilingResult(SystemDetails details, List<Path> files) {
		String string;
		if (this.isInSingleplayer()) {
			string = this.getServer().getSaveProperties().getLevelName();
		} else {
			ServerInfo serverInfo = this.getCurrentServerEntry();
			string = serverInfo != null ? serverInfo.name : "unknown";
		}

		Path path;
		try {
			String string2 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFormattedCurrentTime(), string, SharedConstants.getGameVersion().getId());
			String string3 = PathUtil.getNextUniqueName(RecordDumper.DEBUG_PROFILING_DIRECTORY, string2, ".zip");
			path = RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string3);
		} catch (IOException var21) {
			throw new UncheckedIOException(var21);
		}

		try {
			ZipCompressor zipCompressor = new ZipCompressor(path);

			try {
				zipCompressor.write(Paths.get("system.txt"), details.collect());
				zipCompressor.write(Paths.get("client").resolve(this.options.getOptionsFile().getName()), this.options.collectProfiledOptions());
				files.forEach(zipCompressor::copyAll);
			} catch (Throwable var20) {
				try {
					zipCompressor.close();
				} catch (Throwable var19) {
					var20.addSuppressed(var19);
				}

				throw var20;
			}

			zipCompressor.close();
		} finally {
			for (Path path3 : files) {
				try {
					FileUtils.forceDelete(path3.toFile());
				} catch (IOException var18) {
					LOGGER.warn("Failed to delete temporary profiling result {}", path3, var18);
				}
			}
		}

		return path;
	}

	public void scheduleStop() {
		this.running = false;
	}

	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Opens the "game menu", also called "pause menu".
	 * 
	 * <p>This is also used for menu-less pausing, which can be triggered by
	 * pressing Esc and F3 keys at the same time.
	 * 
	 * @implNote Calling this does not immediately pause the game. Instead,
	 * the game is paused during {@linkplain #render the next rendering}.
	 * 
	 * @param pauseOnly whether to trigger menu-less pausing instead of opening the game menu
	 */
	public void openGameMenu(boolean pauseOnly) {
		if (this.currentScreen == null) {
			boolean bl = this.isIntegratedServerRunning() && !this.server.isRemote();
			if (bl) {
				this.setScreen(new GameMenuScreen(!pauseOnly));
				this.soundManager.pauseAll();
			} else {
				this.setScreen(new GameMenuScreen(true));
			}
		}
	}

	private void handleBlockBreaking(boolean breaking) {
		if (!breaking) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
			if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
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

	private boolean doAttack() {
		if (this.attackCooldown > 0) {
			return false;
		} else if (this.crosshairTarget == null) {
			LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
			if (this.interactionManager.hasLimitedAttackSpeed()) {
				this.attackCooldown = 10;
			}

			return false;
		} else if (this.player.isRiding()) {
			return false;
		} else {
			ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
				return false;
			} else {
				boolean bl = false;
				switch (this.crosshairTarget.getType()) {
					case ENTITY:
						this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
						break;
					case BLOCK:
						BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
						BlockPos blockPos = blockHitResult.getBlockPos();
						if (!this.world.getBlockState(blockPos).isAir()) {
							this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
							if (this.world.getBlockState(blockPos).isAir()) {
								bl = true;
							}
							break;
						}
					case MISS:
						if (this.interactionManager.hasLimitedAttackSpeed()) {
							this.attackCooldown = 10;
						}

						this.player.resetLastAttackedTicks();
				}

				this.player.swingHand(Hand.MAIN_HAND);
				return bl;
			}
		}
	}

	private void doItemUse() {
		if (!this.interactionManager.isBreakingBlock()) {
			this.itemUseCooldown = 4;
			if (!this.player.isRiding()) {
				if (this.crosshairTarget == null) {
					LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				for (Hand hand : Hand.values()) {
					ItemStack itemStack = this.player.getStackInHand(hand);
					if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
						return;
					}

					if (this.crosshairTarget != null) {
						switch (this.crosshairTarget.getType()) {
							case ENTITY:
								EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
								Entity entity = entityHitResult.getEntity();
								if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
									return;
								}

								ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
								if (!actionResult.isAccepted()) {
									actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
								}

								if (actionResult instanceof ActionResult.Success success) {
									if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
										this.player.swingHand(hand);
									}

									return;
								}
								break;
							case BLOCK:
								BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
								int i = itemStack.getCount();
								ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
								if (actionResult2 instanceof ActionResult.Success success2) {
									if (success2.swingSource() == ActionResult.SwingSource.CLIENT) {
										this.player.swingHand(hand);
										if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
											this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
										}
									}

									return;
								}

								if (actionResult2 instanceof ActionResult.Fail) {
									return;
								}
						}
					}

					if (!itemStack.isEmpty() && this.interactionManager.interactItem(this.player, hand) instanceof ActionResult.Success success3) {
						if (success3.swingSource() == ActionResult.SwingSource.CLIENT) {
							this.player.swingHand(hand);
						}

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
		this.uptimeInTicks++;
		if (this.world != null && !this.paused) {
			this.world.getTickManager().step();
		}

		if (this.itemUseCooldown > 0) {
			this.itemUseCooldown--;
		}

		Profiler profiler = Profilers.get();
		profiler.push("gui");
		this.messageHandler.processDelayedMessages();
		this.inGameHud.tick(this.paused);
		profiler.pop();
		this.gameRenderer.updateCrosshairTarget(1.0F);
		this.tutorialManager.tick(this.world, this.crosshairTarget);
		profiler.push("gameMode");
		if (!this.paused && this.world != null) {
			this.interactionManager.tick();
		}

		profiler.swap("textures");
		if (this.shouldTick()) {
			this.textureManager.tick();
		}

		if (this.currentScreen != null || this.player == null) {
			if (this.currentScreen instanceof SleepingChatScreen sleepingChatScreen && !this.player.isSleeping()) {
				sleepingChatScreen.closeChatIfEmpty();
			}
		} else if (this.player.isDead() && !(this.currentScreen instanceof DeathScreen)) {
			this.setScreen(null);
		} else if (this.player.isSleeping() && this.world != null) {
			this.setScreen(new SleepingChatScreen());
		}

		if (this.currentScreen != null) {
			this.attackCooldown = 10000;
		}

		if (this.currentScreen != null) {
			try {
				this.currentScreen.tick();
			} catch (Throwable var5) {
				CrashReport crashReport = CrashReport.create(var5, "Ticking screen");
				this.currentScreen.addCrashReportSection(crashReport);
				throw new CrashException(crashReport);
			}
		}

		if (!this.getDebugHud().shouldShowDebugHud()) {
			this.inGameHud.resetDebugHudChunk();
		}

		if (this.overlay == null && this.currentScreen == null) {
			profiler.swap("Keybindings");
			this.handleInputEvents();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
		}

		if (this.world != null) {
			profiler.swap("gameRenderer");
			if (!this.paused) {
				this.gameRenderer.tick();
			}

			profiler.swap("levelRenderer");
			if (!this.paused) {
				this.worldRenderer.tick();
			}

			profiler.swap("level");
			if (!this.paused) {
				this.world.tickEntities();
			}
		} else if (this.gameRenderer.getPostProcessorId() != null) {
			this.gameRenderer.clearPostProcessor();
		}

		if (!this.paused) {
			this.musicTracker.tick();
		}

		this.soundManager.tick(this.paused);
		if (this.world != null) {
			if (!this.paused) {
				if (!this.options.joinedFirstServer && this.isConnectedToServer()) {
					Text text = Text.translatable("tutorial.socialInteractions.title");
					Text text2 = Text.translatable("tutorial.socialInteractions.description", TutorialManager.keyToText("socialInteractions"));
					this.socialInteractionsToast = new TutorialToast(TutorialToast.Type.SOCIAL_INTERACTIONS, text, text2, true, 8000);
					this.toastManager.add(this.socialInteractionsToast);
					this.options.joinedFirstServer = true;
					this.options.write();
				}

				this.tutorialManager.tick();

				try {
					this.world.tick(() -> true);
				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Exception in world tick");
					if (this.world == null) {
						CrashReportSection crashReportSection = crashReport.addElement("Affected level");
						crashReportSection.add("Problem", "Level is null!");
					} else {
						this.world.addDetailsToCrashReport(crashReport);
					}

					throw new CrashException(crashReport);
				}
			}

			profiler.swap("animateTick");
			if (!this.paused && this.shouldTick()) {
				this.world.doRandomBlockDisplayTicks(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
			}

			profiler.swap("particles");
			if (!this.paused && this.shouldTick()) {
				this.particleManager.tick();
			}

			ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
			if (clientPlayNetworkHandler != null && !this.paused) {
				clientPlayNetworkHandler.sendPacket(ClientTickEndC2SPacket.INSTANCE);
			}
		} else if (this.integratedServerConnection != null) {
			profiler.swap("pendingConnection");
			this.integratedServerConnection.tick();
		}

		profiler.swap("keyboard");
		this.keyboard.pollDebugCrash();
		profiler.pop();
	}

	private boolean shouldTick() {
		return this.world == null || this.world.getTickManager().shouldTick();
	}

	private boolean isConnectedToServer() {
		return !this.integratedServerRunning || this.server != null && this.server.isRemote();
	}

	private void handleInputEvents() {
		while (this.options.togglePerspectiveKey.wasPressed()) {
			Perspective perspective = this.options.getPerspective();
			this.options.setPerspective(this.options.getPerspective().next());
			if (perspective.isFirstPerson() != this.options.getPerspective().isFirstPerson()) {
				this.gameRenderer.onCameraEntitySet(this.options.getPerspective().isFirstPerson() ? this.getCameraEntity() : null);
			}

			this.worldRenderer.scheduleTerrainUpdate();
		}

		while (this.options.smoothCameraKey.wasPressed()) {
			this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.options.saveToolbarActivatorKey.isPressed();
			boolean bl2 = this.options.loadToolbarActivatorKey.isPressed();
			if (this.options.hotbarKeys[i].wasPressed()) {
				if (this.player.isSpectator()) {
					this.inGameHud.getSpectatorHud().selectSlot(i);
				} else if (!this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
					this.player.getInventory().selectedSlot = i;
				} else {
					CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
				}
			}
		}

		while (this.options.socialInteractionsKey.wasPressed()) {
			if (!this.isConnectedToServer()) {
				this.player.sendMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
				this.narratorManager.narrate(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
			} else {
				if (this.socialInteractionsToast != null) {
					this.socialInteractionsToast.hide();
					this.socialInteractionsToast = null;
				}

				this.setScreen(new SocialInteractionsScreen());
			}
		}

		while (this.options.inventoryKey.wasPressed()) {
			if (this.interactionManager.hasRidingInventory()) {
				this.player.openRidingInventory();
			} else {
				this.tutorialManager.onInventoryOpened();
				this.setScreen(new InventoryScreen(this.player));
			}
		}

		while (this.options.advancementsKey.wasPressed()) {
			this.setScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
		}

		while (this.options.swapHandsKey.wasPressed()) {
			if (!this.player.isSpectator()) {
				this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
			}
		}

		while (this.options.dropKey.wasPressed()) {
			if (!this.player.isSpectator() && this.player.dropSelectedItem(Screen.hasControlDown())) {
				this.player.swingHand(Hand.MAIN_HAND);
			}
		}

		while (this.options.chatKey.wasPressed()) {
			this.openChatScreen("");
		}

		if (this.currentScreen == null && this.overlay == null && this.options.commandKey.wasPressed()) {
			this.openChatScreen("/");
		}

		boolean bl3 = false;
		if (this.player.isUsingItem()) {
			if (!this.options.useKey.isPressed()) {
				this.interactionManager.stopUsingItem(this.player);
			}

			while (this.options.attackKey.wasPressed()) {
			}

			while (this.options.useKey.wasPressed()) {
			}

			while (this.options.pickItemKey.wasPressed()) {
			}
		} else {
			while (this.options.attackKey.wasPressed()) {
				bl3 |= this.doAttack();
			}

			while (this.options.useKey.wasPressed()) {
				this.doItemUse();
			}

			while (this.options.pickItemKey.wasPressed()) {
				this.doItemPick();
			}
		}

		if (this.options.useKey.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
			this.doItemUse();
		}

		this.handleBlockBreaking(this.currentScreen == null && !bl3 && this.options.attackKey.isPressed() && this.mouse.isCursorLocked());
	}

	public TelemetryManager getTelemetryManager() {
		return this.telemetryManager;
	}

	public double getGpuUtilizationPercentage() {
		return this.gpuUtilizationPercentage;
	}

	public ProfileKeys getProfileKeys() {
		return this.profileKeys;
	}

	public IntegratedServerLoader createIntegratedServerLoader() {
		return new IntegratedServerLoader(this, this.levelStorage);
	}

	public void startIntegratedServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld) {
		this.disconnect();
		this.worldGenProgressTracker.set(null);
		Instant instant = Instant.now();

		try {
			session.backupLevelDataFile(saveLoader.combinedDynamicRegistries().getCombinedRegistryManager(), saveLoader.saveProperties());
			ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
			apiServices.userCache().setExecutor(this);
			SkullBlockEntity.setServices(apiServices, this);
			UserCache.setUseRemote(false);
			this.server = MinecraftServer.startServer(
				thread -> new IntegratedServer(thread, this, session, dataPackManager, saveLoader, apiServices, spawnChunkRadius -> {
						WorldGenerationProgressTracker worldGenerationProgressTracker = WorldGenerationProgressTracker.create(spawnChunkRadius + 0);
						this.worldGenProgressTracker.set(worldGenerationProgressTracker);
						return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, this.renderTaskQueue::add);
					})
			);
			this.integratedServerRunning = true;
			this.ensureAbuseReportContext(ReporterEnvironment.ofIntegratedServer());
			this.quickPlayLogger.setWorld(QuickPlayLogger.WorldType.SINGLEPLAYER, session.getDirectoryName(), saveLoader.saveProperties().getLevelName());
		} catch (Throwable var12) {
			CrashReport crashReport = CrashReport.create(var12, "Starting integrated server");
			CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
			crashReportSection.add("Level ID", session.getDirectoryName());
			crashReportSection.add("Level Name", (CrashCallable<String>)(() -> saveLoader.saveProperties().getLevelName()));
			throw new CrashException(crashReport);
		}

		while (this.worldGenProgressTracker.get() == null) {
			Thread.yield();
		}

		LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen((WorldGenerationProgressTracker)this.worldGenProgressTracker.get());
		Profiler profiler = Profilers.get();
		this.setScreen(levelLoadingScreen);
		profiler.push("waitForServer");

		for (; !this.server.isLoading() || this.overlay != null; this.printCrashReport()) {
			levelLoadingScreen.tick();
			this.render(false);

			try {
				Thread.sleep(16L);
			} catch (InterruptedException var11) {
			}
		}

		profiler.pop();
		Duration duration = Duration.between(instant, Instant.now());
		SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
		ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
		clientConnection.connect(socketAddress.toString(), 0, new ClientLoginNetworkHandler(clientConnection, this, null, null, newWorld, duration, status -> {
		}, null));
		clientConnection.send(new LoginHelloC2SPacket(this.getSession().getUsername(), this.getSession().getUuidOrNull()));
		this.integratedServerConnection = clientConnection;
	}

	public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
		this.reset(new DownloadingTerrainScreen(() -> false, worldEntryReason));
		this.world = world;
		this.setWorld(world);
		if (!this.integratedServerRunning) {
			ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
			apiServices.userCache().setExecutor(this);
			SkullBlockEntity.setServices(apiServices, this);
			UserCache.setUseRemote(false);
		}
	}

	public void disconnect() {
		this.disconnect(new ProgressScreen(true), false);
	}

	public void disconnect(Screen disconnectionScreen) {
		this.disconnect(disconnectionScreen, false);
	}

	public void disconnect(Screen disconnectionScreen, boolean transferring) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			this.cancelTasks();
			clientPlayNetworkHandler.unloadWorld();
			if (!transferring) {
				this.onDisconnected();
			}
		}

		this.socialInteractionsManager.unloadBlockList();
		if (this.recorder.isActive()) {
			this.forceStopRecorder();
		}

		IntegratedServer integratedServer = this.server;
		this.server = null;
		this.gameRenderer.reset();
		this.interactionManager = null;
		this.narratorManager.clear();
		this.disconnecting = true;

		try {
			this.reset(disconnectionScreen);
			if (this.world != null) {
				if (integratedServer != null) {
					Profiler profiler = Profilers.get();
					profiler.push("waitForServer");

					while (!integratedServer.isStopping()) {
						this.render(false);
					}

					profiler.pop();
				}

				this.inGameHud.clear();
				this.integratedServerRunning = false;
			}

			this.world = null;
			this.setWorld(null);
			this.player = null;
		} finally {
			this.disconnecting = false;
		}

		SkullBlockEntity.clearServices();
	}

	public void onDisconnected() {
		this.serverResourcePackLoader.clear();
		this.runTasks();
	}

	public void enterReconfiguration(Screen reconfigurationScreen) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.clearWorld();
		}

		if (this.recorder.isActive()) {
			this.forceStopRecorder();
		}

		this.gameRenderer.reset();
		this.interactionManager = null;
		this.narratorManager.clear();
		this.disconnecting = true;

		try {
			this.reset(reconfigurationScreen);
			this.inGameHud.clear();
			this.world = null;
			this.setWorld(null);
			this.player = null;
		} finally {
			this.disconnecting = false;
		}

		SkullBlockEntity.clearServices();
	}

	private void reset(Screen resettingScreen) {
		Profiler profiler = Profilers.get();
		profiler.push("forcedTick");
		this.soundManager.stopAll();
		this.cameraEntity = null;
		this.integratedServerConnection = null;
		this.setScreen(resettingScreen);
		this.render(false);
		profiler.pop();
	}

	public void setScreenAndRender(Screen screen) {
		try (ScopedProfiler scopedProfiler = Profilers.get().scoped("forcedTick")) {
			this.setScreen(screen);
			this.render(false);
		}
	}

	private void setWorld(@Nullable ClientWorld world) {
		this.worldRenderer.setWorld(world);
		this.particleManager.setWorld(world);
		this.blockEntityRenderDispatcher.setWorld(world);
		this.updateWindowTitle();
	}

	private UserProperties getUserProperties() {
		return (UserProperties)this.userPropertiesFuture.join();
	}

	public boolean isOptionalTelemetryEnabled() {
		return this.isOptionalTelemetryEnabledByApi() && this.options.getTelemetryOptInExtra().getValue();
	}

	public boolean isOptionalTelemetryEnabledByApi() {
		return this.isTelemetryEnabledByApi() && this.getUserProperties().flag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
	}

	public boolean isTelemetryEnabledByApi() {
		return SharedConstants.isDevelopment ? false : this.getUserProperties().flag(UserFlag.TELEMETRY_ENABLED);
	}

	public boolean isMultiplayerEnabled() {
		return this.multiplayerEnabled
			&& this.getUserProperties().flag(UserFlag.SERVERS_ALLOWED)
			&& this.getMultiplayerBanDetails() == null
			&& !this.isUsernameBanned();
	}

	public boolean isRealmsEnabled() {
		return this.getUserProperties().flag(UserFlag.REALMS_ALLOWED) && this.getMultiplayerBanDetails() == null;
	}

	@Nullable
	public BanDetails getMultiplayerBanDetails() {
		return (BanDetails)this.getUserProperties().bannedScopes().get("MULTIPLAYER");
	}

	public boolean isUsernameBanned() {
		com.mojang.authlib.yggdrasil.ProfileResult profileResult = (com.mojang.authlib.yggdrasil.ProfileResult)this.gameProfileFuture.getNow(null);
		return profileResult != null && profileResult.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
	}

	/**
	 * Checks if the client should block messages from the {@code sender}.
	 * 
	 * <p>If true, messages will not be displayed in chat and narrator will not process
	 * them.
	 */
	public boolean shouldBlockMessages(UUID sender) {
		return this.getChatRestriction().allowsChat(false)
			? this.socialInteractionsManager.isPlayerMuted(sender)
			: (this.player == null || !sender.equals(this.player.getUuid())) && !sender.equals(Util.NIL_UUID);
	}

	public MinecraftClient.ChatRestriction getChatRestriction() {
		if (this.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) {
			return MinecraftClient.ChatRestriction.DISABLED_BY_OPTIONS;
		} else if (!this.onlineChatEnabled) {
			return MinecraftClient.ChatRestriction.DISABLED_BY_LAUNCHER;
		} else {
			return !this.getUserProperties().flag(UserFlag.CHAT_ALLOWED) ? MinecraftClient.ChatRestriction.DISABLED_BY_PROFILE : MinecraftClient.ChatRestriction.ENABLED;
		}
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

	public static boolean isFancyGraphicsOrBetter() {
		return instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FANCY.getId();
	}

	public static boolean isFabulousGraphicsOrBetter() {
		return !instance.gameRenderer.isRenderingPanorama() && instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FABULOUS.getId();
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance.options.getAo().getValue();
	}

	private void doItemPick() {
		if (this.crosshairTarget != null && this.crosshairTarget.getType() != HitResult.Type.MISS) {
			boolean bl = this.player.getAbilities().creativeMode;
			BlockEntity blockEntity = null;
			HitResult.Type type = this.crosshairTarget.getType();
			ItemStack itemStack;
			if (type == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult)this.crosshairTarget).getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				if (blockState.isAir()) {
					return;
				}

				Block block = blockState.getBlock();
				itemStack = block.getPickStack(this.world, blockPos, blockState);
				if (itemStack.isEmpty()) {
					return;
				}

				if (bl && Screen.hasControlDown() && blockState.hasBlockEntity()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}
			} else {
				if (type != HitResult.Type.ENTITY || !bl) {
					return;
				}

				Entity entity = ((EntityHitResult)this.crosshairTarget).getEntity();
				itemStack = entity.getPickBlockStack();
				if (itemStack == null) {
					return;
				}
			}

			if (itemStack.isEmpty()) {
				String string = "";
				if (type == HitResult.Type.BLOCK) {
					string = Registries.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.crosshairTarget).getBlockPos()).getBlock()).toString();
				} else if (type == HitResult.Type.ENTITY) {
					string = Registries.ENTITY_TYPE.getId(((EntityHitResult)this.crosshairTarget).getEntity().getType()).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", type, string);
			} else {
				PlayerInventory playerInventory = this.player.getInventory();
				if (blockEntity != null) {
					this.addBlockEntityNbt(itemStack, blockEntity, this.world.getRegistryManager());
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

	private void addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity, DynamicRegistryManager registryManager) {
		NbtCompound nbtCompound = blockEntity.createComponentlessNbtWithIdentifyingData(registryManager);
		blockEntity.removeFromCopiedStackNbt(nbtCompound);
		BlockItem.setBlockEntityData(stack, blockEntity.getType(), nbtCompound);
		stack.applyComponentsFrom(blockEntity.createComponentMap());
	}

	public CrashReport addDetailsToCrashReport(CrashReport report) {
		SystemDetails systemDetails = report.getSystemDetailsSection();

		try {
			addSystemDetailsToCrashReport(systemDetails, this, this.languageManager, this.gameVersion, this.options);
			this.addUptimesToCrashReport(report.addElement("Uptime"));
			if (this.world != null) {
				this.world.addDetailsToCrashReport(report);
			}

			if (this.server != null) {
				this.server.addSystemDetails(systemDetails);
			}

			this.resourceReloadLogger.addReloadSection(report);
		} catch (Throwable var4) {
			LOGGER.error("Failed to collect details", var4);
		}

		return report;
	}

	public static void addSystemDetailsToCrashReport(
		@Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options, CrashReport report
	) {
		SystemDetails systemDetails = report.getSystemDetailsSection();
		addSystemDetailsToCrashReport(systemDetails, client, languageManager, version, options);
	}

	private static String formatSeconds(double seconds) {
		return String.format(Locale.ROOT, "%.3fs", seconds);
	}

	private void addUptimesToCrashReport(CrashReportSection section) {
		section.add("JVM uptime", (CrashCallable<String>)(() -> formatSeconds((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0)));
		section.add("Wall uptime", (CrashCallable<String>)(() -> formatSeconds((double)(System.currentTimeMillis() - this.startTime) / 1000.0)));
		section.add("High-res time", (CrashCallable<String>)(() -> formatSeconds((double)Util.getMeasuringTimeMs() / 1000.0)));
		section.add(
			"Client ticks", (CrashCallable<String>)(() -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.uptimeInTicks, (double)this.uptimeInTicks / 20.0))
		);
	}

	private static SystemDetails addSystemDetailsToCrashReport(
		SystemDetails systemDetails, @Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options
	) {
		systemDetails.addSection("Launched Version", (Supplier<String>)(() -> version));
		String string = getLauncherBrand();
		if (string != null) {
			systemDetails.addSection("Launcher name", string);
		}

		systemDetails.addSection("Backend library", RenderSystem::getBackendDescription);
		systemDetails.addSection("Backend API", RenderSystem::getApiDescription);
		systemDetails.addSection(
			"Window size",
			(Supplier<String>)(() -> client != null ? client.window.getFramebufferWidth() + "x" + client.window.getFramebufferHeight() : "<not initialized>")
		);
		systemDetails.addSection("GFLW Platform", Window::getGlfwPlatform);
		systemDetails.addSection("GL Caps", RenderSystem::getCapsString);
		systemDetails.addSection(
			"GL debug messages", (Supplier<String>)(() -> GlDebug.isDebugMessageEnabled() ? String.join("\n", GlDebug.collectDebugMessages()) : "<disabled>")
		);
		systemDetails.addSection("Is Modded", (Supplier<String>)(() -> getModStatus().getMessage()));
		systemDetails.addSection("Universe", (Supplier<String>)(() -> client != null ? Long.toHexString(client.UNIVERSE) : "404"));
		systemDetails.addSection("Type", "Client (map_client.txt)");
		if (options != null) {
			if (client != null) {
				String string2 = client.getVideoWarningManager().getWarningsAsString();
				if (string2 != null) {
					systemDetails.addSection("GPU Warnings", string2);
				}
			}

			systemDetails.addSection("Graphics mode", options.getGraphicsMode().getValue().toString());
			systemDetails.addSection("Render Distance", options.getClampedViewDistance() + "/" + options.getViewDistance().getValue() + " chunks");
		}

		if (client != null) {
			systemDetails.addSection("Resource Packs", (Supplier<String>)(() -> ResourcePackManager.listPacks(client.getResourcePackManager().getEnabledProfiles())));
		}

		if (languageManager != null) {
			systemDetails.addSection("Current Language", (Supplier<String>)(() -> languageManager.getLanguage()));
		}

		systemDetails.addSection("Locale", String.valueOf(Locale.getDefault()));
		systemDetails.addSection("System encoding", (Supplier<String>)(() -> System.getProperty("sun.jnu.encoding", "<not set>")));
		systemDetails.addSection("File encoding", (Supplier<String>)(() -> System.getProperty("file.encoding", "<not set>")));
		systemDetails.addSection("CPU", GlDebugInfo::getCpuInfo);
		return systemDetails;
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public CompletableFuture<Void> reloadResourcesConcurrently() {
		return this.submit(this::reloadResources).thenCompose(future -> future);
	}

	/**
	 * Recreates and resets {@link #abuseReportContext} if {@code environment} has
	 * changed.
	 */
	public void ensureAbuseReportContext(ReporterEnvironment environment) {
		if (!this.abuseReportContext.environmentEquals(environment)) {
			this.abuseReportContext = AbuseReportContext.create(environment, this.userApiService);
		}
	}

	@Nullable
	public ServerInfo getCurrentServerEntry() {
		return Nullables.map(this.getNetworkHandler(), ClientPlayNetworkHandler::getServerInfo);
	}

	public boolean isInSingleplayer() {
		return this.integratedServerRunning;
	}

	public boolean isIntegratedServerRunning() {
		return this.integratedServerRunning && this.server != null;
	}

	/**
	 * Gets this client's own integrated server.
	 * 
	 * <p>The integrated server is only present when a local single player world is open.
	 */
	@Nullable
	public IntegratedServer getServer() {
		return this.server;
	}

	public boolean isConnectedToLocalServer() {
		IntegratedServer integratedServer = this.getServer();
		return integratedServer != null && !integratedServer.isRemote();
	}

	public boolean uuidEquals(UUID uuid) {
		return uuid.equals(this.getSession().getUuidOrNull());
	}

	public Session getSession() {
		return this.session;
	}

	public GameProfile getGameProfile() {
		com.mojang.authlib.yggdrasil.ProfileResult profileResult = (com.mojang.authlib.yggdrasil.ProfileResult)this.gameProfileFuture.join();
		return profileResult != null ? profileResult.profile() : new GameProfile(this.session.getUuidOrNull(), this.session.getUsername());
	}

	public Proxy getNetworkProxy() {
		return this.networkProxy;
	}

	public TextureManager getTextureManager() {
		return this.textureManager;
	}

	public ShaderLoader getShaderLoader() {
		return this.shaderLoader;
	}

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public ResourcePackManager getResourcePackManager() {
		return this.resourcePackManager;
	}

	public DefaultResourcePack getDefaultResourcePack() {
		return this.defaultResourcePack;
	}

	public ServerResourcePackLoader getServerResourcePackProvider() {
		return this.serverResourcePackLoader;
	}

	public Path getResourcePackDir() {
		return this.resourcePackDir;
	}

	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}

	public Function<Identifier, Sprite> getSpriteAtlas(Identifier id) {
		return this.bakedModelManager.getAtlas(id)::getSprite;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public VideoWarningManager getVideoWarningManager() {
		return this.videoWarningManager;
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public MusicSound getMusicType() {
		MusicSound musicSound = Nullables.map(this.currentScreen, Screen::getMusic);
		if (musicSound != null) {
			return musicSound;
		} else if (this.player != null) {
			if (this.player.getWorld().getRegistryKey() == World.END) {
				return this.inGameHud.getBossBarHud().shouldPlayDragonMusic() ? MusicType.DRAGON : MusicType.END;
			} else {
				RegistryEntry<Biome> registryEntry = this.player.getWorld().getBiome(this.player.getBlockPos());
				if (!this.musicTracker.isPlayingType(MusicType.UNDERWATER) && (!this.player.isSubmergedInWater() || !registryEntry.isIn(BiomeTags.PLAYS_UNDERWATER_MUSIC))) {
					return this.player.getWorld().getRegistryKey() != World.NETHER && this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying
						? MusicType.CREATIVE
						: (MusicSound)registryEntry.value().getMusic().orElse(MusicType.GAME);
				} else {
					return MusicType.UNDERWATER;
				}
			}
		} else {
			return MusicType.MENU;
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

	/**
	 * Checks if the provided {@code entity} should display an outline around its model.
	 */
	public boolean hasOutline(Entity entity) {
		return entity.isGlowing()
			|| this.player != null && this.player.isSpectator() && this.options.spectatorOutlinesKey.isPressed() && entity.getType() == EntityType.PLAYER;
	}

	@Override
	protected Thread getThread() {
		return this.thread;
	}

	@Override
	public Runnable createTask(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean canExecute(Runnable task) {
		return true;
	}

	public BlockRenderManager getBlockRenderManager() {
		return this.blockRenderManager;
	}

	public EntityRenderDispatcher getEntityRenderDispatcher() {
		return this.entityRenderDispatcher;
	}

	public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
		return this.blockEntityRenderDispatcher;
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	public MapRenderer getMapRenderer() {
		return this.mapRenderer;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public RenderTickCounter getRenderTickCounter() {
		return this.renderTickCounter;
	}

	public BlockColors getBlockColors() {
		return this.blockColors;
	}

	public boolean hasReducedDebugInfo() {
		return this.player != null && this.player.hasReducedDebugInfo() || this.options.getReducedDebugInfo().getValue();
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

	public PaintingManager getPaintingManager() {
		return this.paintingManager;
	}

	public StatusEffectSpriteManager getStatusEffectSpriteManager() {
		return this.statusEffectSpriteManager;
	}

	public MapTextureManager getMapTextureManager() {
		return this.mapTextureManager;
	}

	public MapDecorationsAtlasManager getMapDecorationsAtlasManager() {
		return this.mapDecorationsAtlasManager;
	}

	public GuiAtlasManager getGuiAtlasManager() {
		return this.guiAtlasManager;
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		this.windowFocused = focused;
	}

	/**
	 * Takes a panorama. The panorama is stored in the given {@code directory}, in
	 * where 6 screenshots of size {@code width} and {@code height} will be taken.
	 * 
	 * @return a user-oriented piece of text for screenshot result
	 */
	public Text takePanorama(File directory, int width, int height) {
		int i = this.window.getFramebufferWidth();
		int j = this.window.getFramebufferHeight();
		Framebuffer framebuffer = this.getFramebuffer();
		float f = this.player.getPitch();
		float g = this.player.getYaw();
		float h = this.player.prevPitch;
		float k = this.player.prevYaw;
		this.gameRenderer.setBlockOutlineEnabled(false);

		MutableText var12;
		try {
			this.gameRenderer.setRenderingPanorama(true);
			this.window.setFramebufferWidth(width);
			this.window.setFramebufferHeight(height);
			framebuffer.resize(width, height);

			for (int l = 0; l < 6; l++) {
				switch (l) {
					case 0:
						this.player.setYaw(g);
						this.player.setPitch(0.0F);
						break;
					case 1:
						this.player.setYaw((g + 90.0F) % 360.0F);
						this.player.setPitch(0.0F);
						break;
					case 2:
						this.player.setYaw((g + 180.0F) % 360.0F);
						this.player.setPitch(0.0F);
						break;
					case 3:
						this.player.setYaw((g - 90.0F) % 360.0F);
						this.player.setPitch(0.0F);
						break;
					case 4:
						this.player.setYaw(g);
						this.player.setPitch(-90.0F);
						break;
					case 5:
					default:
						this.player.setYaw(g);
						this.player.setPitch(90.0F);
				}

				this.player.prevYaw = this.player.getYaw();
				this.player.prevPitch = this.player.getPitch();
				framebuffer.beginWrite(true);
				this.gameRenderer.renderWorld(RenderTickCounter.ONE);

				try {
					Thread.sleep(10L);
				} catch (InterruptedException var17) {
				}

				ScreenshotRecorder.saveScreenshot(directory, "panorama_" + l + ".png", framebuffer, message -> {
				});
			}

			Text text = Text.literal(directory.getName())
				.formatted(Formatting.UNDERLINE)
				.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, directory.getAbsolutePath())));
			return Text.translatable("screenshot.success", text);
		} catch (Exception var18) {
			LOGGER.error("Couldn't save image", (Throwable)var18);
			var12 = Text.translatable("screenshot.failure", var18.getMessage());
		} finally {
			this.player.setPitch(f);
			this.player.setYaw(g);
			this.player.prevPitch = h;
			this.player.prevYaw = k;
			this.gameRenderer.setBlockOutlineEnabled(true);
			this.window.setFramebufferWidth(i);
			this.window.setFramebufferHeight(j);
			framebuffer.resize(i, j);
			this.gameRenderer.setRenderingPanorama(false);
			this.getFramebuffer().beginWrite(true);
		}

		return var12;
	}

	/**
	 * Takes a huge screenshot in the tga file format.
	 * 
	 * <p>The {@code unitWidth} and {@code unitHeight} controls the size of the
	 * partial image rendered; it does not affect the screenshot outcome, but may
	 * affect the screenshot performance.
	 * 
	 * @return a user-oriented piece of text for screenshot result
	 */
	private Text takeHugeScreenshot(File gameDirectory, int unitWidth, int unitHeight, int width, int height) {
		try {
			ByteBuffer byteBuffer = GlDebugInfo.allocateMemory(unitWidth * unitHeight * 3);
			ScreenshotRecorder screenshotRecorder = new ScreenshotRecorder(gameDirectory, width, height, unitHeight);
			float f = (float)width / (float)unitWidth;
			float g = (float)height / (float)unitHeight;
			float h = f > g ? f : g;

			for (int i = (height - 1) / unitHeight * unitHeight; i >= 0; i -= unitHeight) {
				for (int j = 0; j < width; j += unitWidth) {
					RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
					float k = (float)(width - unitWidth) / 2.0F * 2.0F - (float)(j * 2);
					float l = (float)(height - unitHeight) / 2.0F * 2.0F - (float)(i * 2);
					k /= (float)unitWidth;
					l /= (float)unitHeight;
					this.gameRenderer.renderWithZoom(h, k, l);
					byteBuffer.clear();
					RenderSystem.pixelStore(3333, 1);
					RenderSystem.pixelStore(3317, 1);
					RenderSystem.readPixels(0, 0, unitWidth, unitHeight, 32992, 5121, byteBuffer);
					screenshotRecorder.getIntoBuffer(byteBuffer, j, i, unitWidth, unitHeight);
				}

				screenshotRecorder.writeToStream();
			}

			File file = screenshotRecorder.finish();
			GlDebugInfo.freeMemory(byteBuffer);
			Text text = Text.literal(file.getName())
				.formatted(Formatting.UNDERLINE)
				.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
			return Text.translatable("screenshot.success", text);
		} catch (Exception var15) {
			LOGGER.warn("Couldn't save screenshot", (Throwable)var15);
			return Text.translatable("screenshot.failure", var15.getMessage());
		}
	}

	@Nullable
	public WorldGenerationProgressTracker getWorldGenerationProgressTracker() {
		return (WorldGenerationProgressTracker)this.worldGenProgressTracker.get();
	}

	public SplashTextResourceSupplier getSplashTextLoader() {
		return this.splashTextLoader;
	}

	@Nullable
	public Overlay getOverlay() {
		return this.overlay;
	}

	public SocialInteractionsManager getSocialInteractionsManager() {
		return this.socialInteractionsManager;
	}

	public Window getWindow() {
		return this.window;
	}

	public InactivityFpsLimiter getInactivityFpsLimiter() {
		return this.inactivityFpsLimiter;
	}

	public DebugHud getDebugHud() {
		return this.inGameHud.getDebugHud();
	}

	public BufferBuilderStorage getBufferBuilders() {
		return this.bufferBuilders;
	}

	public void setMipmapLevels(int mipmapLevels) {
		this.bakedModelManager.setMipmapLevels(mipmapLevels);
	}

	public EntityModelLoader getEntityModelLoader() {
		return this.entityModelLoader;
	}

	public EquipmentModelLoader getEquipmentModelLoader() {
		return this.equipmentModelLoader;
	}

	public boolean shouldFilterText() {
		return this.getUserProperties().flag(UserFlag.PROFANITY_FILTER_ENABLED);
	}

	public void loadBlockList() {
		this.socialInteractionsManager.loadBlockList();
		this.getProfileKeys().fetchKeyPair();
	}

	@Nullable
	public SignatureVerifier getServicesSignatureVerifier() {
		return SignatureVerifier.create(this.authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
	}

	public boolean providesProfileKeys() {
		return !this.authenticationService.getServicesKeySet().keys(ServicesKeyType.PROFILE_KEY).isEmpty();
	}

	public GuiNavigationType getNavigationType() {
		return this.navigationType;
	}

	public void setNavigationType(GuiNavigationType navigationType) {
		this.navigationType = navigationType;
	}

	public NarratorManager getNarratorManager() {
		return this.narratorManager;
	}

	public MessageHandler getMessageHandler() {
		return this.messageHandler;
	}

	public AbuseReportContext getAbuseReportContext() {
		return this.abuseReportContext;
	}

	public RealmsPeriodicCheckers getRealmsPeriodicCheckers() {
		return this.realmsPeriodicCheckers;
	}

	public QuickPlayLogger getQuickPlayLogger() {
		return this.quickPlayLogger;
	}

	public CommandHistoryManager getCommandHistoryManager() {
		return this.commandHistoryManager;
	}

	public SymlinkFinder getSymlinkFinder() {
		return this.symlinkFinder;
	}

	private float getTargetMillisPerTick(float millis) {
		if (this.world != null) {
			TickManager tickManager = this.world.getTickManager();
			if (tickManager.shouldTick()) {
				return Math.max(millis, tickManager.getMillisPerTick());
			}
		}

		return millis;
	}

	@Nullable
	public static String getLauncherBrand() {
		return System.getProperty("minecraft.launcher.brand");
	}

	/**
	 * Represents the restrictions on chat on a Minecraft client.
	 * 
	 * @see MinecraftClient#getChatRestriction()
	 */
	@Environment(EnvType.CLIENT)
	public static enum ChatRestriction {
		ENABLED(ScreenTexts.EMPTY) {
			@Override
			public boolean allowsChat(boolean singlePlayer) {
				return true;
			}
		},
		DISABLED_BY_OPTIONS(Text.translatable("chat.disabled.options").formatted(Formatting.RED)) {
			@Override
			public boolean allowsChat(boolean singlePlayer) {
				return false;
			}
		},
		DISABLED_BY_LAUNCHER(Text.translatable("chat.disabled.launcher").formatted(Formatting.RED)) {
			@Override
			public boolean allowsChat(boolean singlePlayer) {
				return singlePlayer;
			}
		},
		DISABLED_BY_PROFILE(
			Text.translatable("chat.disabled.profile", Text.keybind(MinecraftClient.instance.options.chatKey.getTranslationKey())).formatted(Formatting.RED)
		) {
			@Override
			public boolean allowsChat(boolean singlePlayer) {
				return singlePlayer;
			}
		};

		static final Text MORE_INFO_TEXT = Text.translatable("chat.disabled.profile.moreInfo");
		private final Text description;

		ChatRestriction(final Text description) {
			this.description = description;
		}

		public Text getDescription() {
			return this.description;
		}

		public abstract boolean allowsChat(boolean singlePlayer);
	}

	@Environment(EnvType.CLIENT)
	static record LoadingContext(RealmsClient realmsClient, RunArgs.QuickPlay quickPlayData) {
	}
}
