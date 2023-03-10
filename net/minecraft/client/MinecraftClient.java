/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
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
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.util.Realms32BitWarningChecker;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchProvider;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.SoundManager;
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
import net.minecraft.client.util.Bans;
import net.minecraft.client.util.ClientSamplerSource;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.telemetry.TelemetryManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.KeybindTranslations;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ModStatus;
import net.minecraft.util.Nullables;
import net.minecraft.util.PathUtil;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

/**
 * Represents a logical Minecraft client.
 * The logical Minecraft client is responsible for rendering, sound playback and control input.
 * The Minecraft client also manages connections to a logical server which may be the client's {@link net.minecraft.server.integrated.IntegratedServer} or a remote server.
 * The Minecraft client instance may be obtained using {@link MinecraftClient#getInstance()}.
 * 
 * <p>Rendering on a Minecraft client is split into several facilities.
 * The primary entrypoint for rendering is {@link net.minecraft.client.render.GameRenderer#render(float, long, boolean)}.
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
@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements WindowEventHandler {
    static MinecraftClient instance;
    private static final Logger LOGGER;
    public static final boolean IS_SYSTEM_MAC;
    private static final int field_32145 = 10;
    public static final Identifier DEFAULT_FONT_ID;
    public static final Identifier UNICODE_FONT_ID;
    public static final Identifier ALT_TEXT_RENDERER_ID;
    private static final Identifier REGIONAL_COMPLIANCIES_ID;
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    private static final Text SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    /**
     * A message, in English, displayed in a dialog when a GLFW error is encountered.
     * 
     * @see net.minecraft.client.util.Window#throwGlError(int, long)
     */
    public static final String GL_ERROR_DIALOGUE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    private final Path resourcePackDir;
    private final PropertyMap sessionPropertyMap;
    private final TextureManager textureManager;
    private final DataFixer dataFixer;
    private final WindowProvider windowProvider;
    private final Window window;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(20.0f, 0L);
    private final BufferBuilderStorage bufferBuilders;
    public final WorldRenderer worldRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    public final ParticleManager particleManager;
    private final SearchManager searchManager = new SearchManager();
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
    public final MetricsData metricsData = new MetricsData();
    private final boolean is64Bit;
    private final boolean isDemo;
    private final boolean multiplayerEnabled;
    private final boolean onlineChatEnabled;
    private final ReloadableResourceManagerImpl resourceManager;
    private final DefaultResourcePack defaultResourcePack;
    private final ServerResourcePackProvider serverResourcePackProvider;
    private final ResourcePackManager resourcePackManager;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final ItemColors itemColors;
    private final Framebuffer framebuffer;
    private final SoundManager soundManager;
    private final MusicTracker musicTracker;
    private final FontManager fontManager;
    private final SplashTextResourceSupplier splashTextLoader;
    private final VideoWarningManager videoWarningManager;
    private final PeriodicNotificationManager regionalComplianciesManager = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES_ID, MinecraftClient::isCountrySetTo);
    private final YggdrasilAuthenticationService authenticationService;
    private final MinecraftSessionService sessionService;
    private final SignatureVerifier servicesSignatureVerifier;
    private final UserApiService userApiService;
    private final PlayerSkinProvider skinProvider;
    private final BakedModelManager bakedModelManager;
    private final BlockRenderManager blockRenderManager;
    private final PaintingManager paintingManager;
    private final StatusEffectSpriteManager statusEffectSpriteManager;
    private final ToastManager toastManager;
    private final TutorialManager tutorialManager;
    private final SocialInteractionsManager socialInteractionsManager;
    private final EntityModelLoader entityModelLoader;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final TelemetryManager telemetryManager;
    private final ProfileKeys profileKeys;
    private final RealmsPeriodicCheckers realmsPeriodicCheckers;
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
     * @see net.minecraft.client.gui.screen.ConnectScreen
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
    private float pausedTickDelta;
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
    private boolean connectedToRealms;
    private Thread thread;
    private volatile boolean running;
    @Nullable
    private Supplier<CrashReport> crashReportSupplier;
    private static int currentFps;
    public String fpsDebugString = "";
    private long renderTime;
    public boolean wireFrame;
    public boolean debugChunkInfo;
    public boolean debugChunkOcclusion;
    public boolean chunkCullingEnabled = true;
    private boolean windowFocused;
    private final Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> resourceReloadFuture;
    @Nullable
    private TutorialToast socialInteractionsToast;
    private Profiler profiler = DummyProfiler.INSTANCE;
    private int trackingTick;
    private final TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, () -> this.trackingTick);
    @Nullable
    private ProfileResult tickProfilerResult;
    private Recorder recorder = DummyRecorder.INSTANCE;
    private final ResourceReloadLogger resourceReloadLogger = new ResourceReloadLogger();
    private long metricsSampleDuration;
    private double gpuUtilizationPercentage;
    @Nullable
    private GlTimer.Query currentGlTimerQuery;
    private final Realms32BitWarningChecker realms32BitWarningChecker;
    private final NarratorManager narratorManager;
    private final MessageHandler messageHandler;
    private AbuseReportContext abuseReportContext;
    private String openProfilerSection = "root";

    public MinecraftClient(RunArgs args) {
        super("Client");
        int i;
        String string;
        instance = this;
        this.runDirectory = args.directories.runDir;
        File file = args.directories.assetDir;
        this.resourcePackDir = args.directories.resourcePackDir.toPath();
        this.gameVersion = args.game.version;
        this.versionType = args.game.versionType;
        this.sessionPropertyMap = args.network.profileProperties;
        DefaultClientResourcePackProvider defaultClientResourcePackProvider = new DefaultClientResourcePackProvider(args.directories.getAssetDir());
        this.serverResourcePackProvider = new ServerResourcePackProvider(new File(this.runDirectory, "server-resource-packs"));
        FileResourcePackProvider resourcePackProvider = new FileResourcePackProvider(this.resourcePackDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE);
        this.resourcePackManager = new ResourcePackManager(defaultClientResourcePackProvider, this.serverResourcePackProvider, resourcePackProvider);
        this.defaultResourcePack = defaultClientResourcePackProvider.getResourcePack();
        this.networkProxy = args.network.netProxy;
        this.authenticationService = new YggdrasilAuthenticationService(this.networkProxy);
        this.sessionService = this.authenticationService.createMinecraftSessionService();
        this.userApiService = this.createUserApiService(this.authenticationService, args);
        this.servicesSignatureVerifier = SignatureVerifier.create(this.authenticationService.getServicesKey());
        this.session = args.network.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = args.game.demo;
        this.multiplayerEnabled = !args.game.multiplayerDisabled;
        this.onlineChatEnabled = !args.game.onlineChatDisabled;
        this.is64Bit = MinecraftClient.checkIs64Bit();
        this.server = null;
        if (this.isMultiplayerEnabled() && args.autoConnect.serverAddress != null) {
            string = args.autoConnect.serverAddress;
            i = args.autoConnect.serverPort;
        } else {
            string = null;
            i = 0;
        }
        KeybindTranslations.setFactory(KeyBinding::getLocalizedName);
        this.dataFixer = Schemas.getFixer();
        this.toastManager = new ToastManager(this);
        this.thread = Thread.currentThread();
        this.options = new GameOptions(this, this.runDirectory);
        RenderSystem.setShaderGlintAlpha(this.options.getGlintStrength().getValue());
        this.running = true;
        this.tutorialManager = new TutorialManager(this, this.options);
        this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        WindowSettings windowSettings = this.options.overrideHeight > 0 && this.options.overrideWidth > 0 ? new WindowSettings(this.options.overrideWidth, this.options.overrideHeight, args.windowSettings.fullscreenWidth, args.windowSettings.fullscreenHeight, args.windowSettings.fullscreen) : args.windowSettings;
        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, this.getWindowTitle());
        this.onWindowFocusChanged(true);
        try {
            if (IS_SYSTEM_MAC) {
                MacWindowUtil.setApplicationIconImage(this.getDefaultResourceSupplier("icons", "minecraft.icns"));
            } else {
                this.window.setIcon(this.getDefaultResourceSupplier("icons", "icon_16x16.png"), this.getDefaultResourceSupplier("icons", "icon_32x32.png"));
            }
        } catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", iOException);
        }
        this.window.setFramerateLimit(this.options.getMaxFps().getValue());
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window.getHandle());
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window.getHandle());
        RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
        this.framebuffer = new WindowFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.framebuffer.clear(IS_SYSTEM_MAC);
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
        this.resourcePackManager.scanPacks();
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.languageManager = new LanguageManager(this.options.language);
        this.resourceManager.registerReloader(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloader(this.textureManager);
        this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(file, "skins"), this.sessionService);
        this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
        this.soundManager = new SoundManager(this.options);
        this.resourceManager.registerReloader(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerReloader(this.splashTextLoader);
        this.musicTracker = new MusicTracker(this);
        this.fontManager = new FontManager(this.textureManager);
        this.textRenderer = this.fontManager.createTextRenderer();
        this.advanceValidatingTextRenderer = this.fontManager.createAdvanceValidatingTextRenderer();
        this.resourceManager.registerReloader(this.fontManager.getResourceReloadListener());
        this.initFont(this.forcesUnicodeFont());
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
        this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.textRenderer, this.entityModelLoader, this::getBlockRenderManager, this::getItemRenderer, this::getEntityRenderDispatcher);
        this.resourceManager.registerReloader(this.blockEntityRenderDispatcher);
        BuiltinModelItemRenderer builtinModelItemRenderer = new BuiltinModelItemRenderer(this.blockEntityRenderDispatcher, this.entityModelLoader);
        this.resourceManager.registerReloader(builtinModelItemRenderer);
        this.itemRenderer = new ItemRenderer(this, this.textureManager, this.bakedModelManager, this.itemColors, builtinModelItemRenderer);
        this.resourceManager.registerReloader(this.itemRenderer);
        this.bufferBuilders = new BufferBuilderStorage();
        this.socialInteractionsManager = new SocialInteractionsManager(this, this.userApiService);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), builtinModelItemRenderer, this.blockColors);
        this.resourceManager.registerReloader(this.blockRenderManager);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this, this.textureManager, this.itemRenderer, this.blockRenderManager, this.textRenderer, this.options, this.entityModelLoader);
        this.resourceManager.registerReloader(this.entityRenderDispatcher);
        this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getHeldItemRenderer(), this.resourceManager, this.bufferBuilders);
        this.resourceManager.registerReloader(this.gameRenderer.createProgramReloader());
        this.worldRenderer = new WorldRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.bufferBuilders);
        this.resourceManager.registerReloader(this.worldRenderer);
        this.initializeSearchProviders();
        this.resourceManager.registerReloader(this.searchManager);
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerReloader(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerReloader(this.paintingManager);
        this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
        this.resourceManager.registerReloader(this.statusEffectSpriteManager);
        this.videoWarningManager = new VideoWarningManager();
        this.resourceManager.registerReloader(this.videoWarningManager);
        this.resourceManager.registerReloader(this.regionalComplianciesManager);
        this.inGameHud = new InGameHud(this, this.itemRenderer);
        this.debugRenderer = new DebugRenderer(this);
        this.realmsPeriodicCheckers = new RealmsPeriodicCheckers(RealmsClient.createRealmsClient(this));
        RenderSystem.setErrorCallback(this::handleGlErrorByDisableVsync);
        if (this.framebuffer.textureWidth != this.window.getFramebufferWidth() || this.framebuffer.textureHeight != this.window.getFramebufferHeight()) {
            StringBuilder stringBuilder = new StringBuilder("Recovering from unsupported resolution (" + this.window.getFramebufferWidth() + "x" + this.window.getFramebufferHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            if (GlDebug.isDebugMessageEnabled()) {
                stringBuilder.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", GlDebug.collectDebugMessages()));
            }
            this.window.setWindowedSize(this.framebuffer.textureWidth, this.framebuffer.textureHeight);
            TinyFileDialogs.tinyfd_messageBox("Minecraft", stringBuilder.toString(), "ok", "error", false);
        } else if (this.options.getFullscreen().getValue().booleanValue() && !this.window.isFullscreen()) {
            this.window.toggleFullscreen();
            this.options.getFullscreen().setValue(this.window.isFullscreen());
        }
        this.window.setVsync(this.options.getEnableVsync().getValue());
        this.window.setRawMouseMotion(this.options.getRawMouseInput().getValue());
        this.window.logOnGlError();
        this.onResolutionChanged();
        this.gameRenderer.preloadPrograms(this.defaultResourcePack.getFactory());
        this.telemetryManager = new TelemetryManager(this, this.userApiService, this.session);
        this.profileKeys = ProfileKeys.create(this.userApiService, this.session, this.runDirectory.toPath());
        this.realms32BitWarningChecker = new Realms32BitWarningChecker(this);
        this.narratorManager = new NarratorManager(this);
        this.messageHandler = new MessageHandler(this);
        this.messageHandler.setChatDelay(this.options.getChatDelay().getValue());
        this.abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), this.userApiService);
        SplashOverlay.init(this);
        List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
        this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.INITIAL, list);
        ResourceReload resourceReload = this.resourceManager.reload(Util.getMainWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list);
        this.setOverlay(new SplashOverlay(this, resourceReload, throwable -> Util.ifPresentOrElse(throwable, this::handleResourceReloadException, () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
            this.resourceReloadLogger.finish();
        }), false));
        if (string != null) {
            ServerAddress serverAddress = new ServerAddress(string, i);
            resourceReload.whenComplete().thenRunAsync(() -> ConnectScreen.connect(new TitleScreen(), this, serverAddress, new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), serverAddress.toString(), false)), this);
        } else if (this.isMultiplayerBanned()) {
            this.setScreen(Bans.createBanScreen(confirmed -> {
                if (confirmed) {
                    Util.getOperatingSystem().open("https://aka.ms/mcjavamoderation");
                }
                this.setScreen(new TitleScreen(true));
            }, this.getMultiplayerBanDetails()));
        } else if (this.options.onboardAccessibility) {
            this.setScreen(new AccessibilityOnboardingScreen(this.options));
        } else {
            this.setScreen(new TitleScreen(true));
        }
    }

    private InputSupplier<InputStream> getDefaultResourceSupplier(String ... segments) throws IOException {
        InputSupplier<InputStream> inputSupplier = this.defaultResourcePack.openRoot(segments);
        if (inputSupplier == null) {
            throw new FileNotFoundException(String.join((CharSequence)"/", segments));
        }
        return inputSupplier;
    }

    private static boolean isCountrySetTo(Object country) {
        try {
            return Locale.getDefault().getISO3Country().equals(country);
        } catch (MissingResourceException missingResourceException) {
            return false;
        }
    }

    public void updateWindowTitle() {
        this.window.setTitle(this.getWindowTitle());
    }

    private String getWindowTitle() {
        StringBuilder stringBuilder = new StringBuilder("Minecraft");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append("*");
        }
        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().getName());
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer", new Object[0]));
            } else if (this.isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms", new Object[0]));
            } else if (this.server != null || this.getCurrentServerEntry() != null && this.getCurrentServerEntry().isLocal()) {
                stringBuilder.append(I18n.translate("title.multiplayer.lan", new Object[0]));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.other", new Object[0]));
            }
        }
        return stringBuilder.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs) {
        try {
            return authService.createUserApiService(runArgs.network.session.getAccessToken());
        } catch (AuthenticationException authenticationException) {
            LOGGER.error("Failed to verify authentication", authenticationException);
            return UserApiService.OFFLINE;
        }
    }

    public static ModStatus getModStatus() {
        return ModStatus.check("vanilla", ClientBrandRetriever::getClientModName, "Client", MinecraftClient.class);
    }

    private void handleResourceReloadException(Throwable throwable) {
        if (this.resourcePackManager.getEnabledNames().size() > 1) {
            this.onResourceReloadFailure(throwable, null);
        } else {
            Util.throwUnchecked(throwable);
        }
    }

    public void onResourceReloadFailure(Throwable exception, @Nullable Text resourceName) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", exception);
        this.resourceReloadLogger.recover(exception);
        this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.write();
        this.reloadResources(true).thenRun(() -> this.showResourceReloadFailureToast(resourceName));
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

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }
        try {
            boolean bl = false;
            while (this.running) {
                if (this.crashReportSupplier != null) {
                    MinecraftClient.printCrashReport(this.crashReportSupplier.get());
                    return;
                }
                try {
                    TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create("Renderer");
                    boolean bl2 = this.shouldMonitorTickDuration();
                    this.profiler = this.startMonitor(bl2, tickDurationMonitor);
                    this.profiler.startTick();
                    this.recorder.startTick();
                    this.render(!bl);
                    this.recorder.endTick();
                    this.profiler.endTick();
                    this.endMonitor(bl2, tickDurationMonitor);
                } catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", outOfMemoryError);
                    bl = true;
                }
            }
        } catch (CrashException crashException) {
            this.addDetailsToCrashReport(crashException.getReport());
            this.cleanUpAfterCrash();
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", crashException);
            MinecraftClient.printCrashReport(crashException.getReport());
        } catch (Throwable throwable) {
            CrashReport crashReport = this.addDetailsToCrashReport(new CrashReport("Unexpected error", throwable));
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", throwable);
            this.cleanUpAfterCrash();
            MinecraftClient.printCrashReport(crashReport);
        }
    }

    void initFont(boolean forcesUnicode) {
        this.fontManager.setIdOverrides(forcesUnicode ? ImmutableMap.of(DEFAULT_FONT_ID, UNICODE_FONT_ID) : ImmutableMap.of());
    }

    private void initializeSearchProviders() {
        this.searchManager.put(SearchManager.ITEM_TOOLTIP, stacks -> new TextSearchProvider<ItemStack>(stack -> stack.getTooltip(null, TooltipContext.Default.BASIC.withCreative()).stream().map(tooltip -> Formatting.strip(tooltip.getString()).trim()).filter(string -> !string.isEmpty()), stack -> Stream.of(Registries.ITEM.getId(stack.getItem())), (List<ItemStack>)stacks));
        this.searchManager.put(SearchManager.ITEM_TAG, stacks -> new IdentifierSearchProvider<ItemStack>(stack -> stack.streamTags().map(TagKey::id), (List<ItemStack>)stacks));
        this.searchManager.put(SearchManager.RECIPE_OUTPUT, resultCollections -> new TextSearchProvider<RecipeResultCollection>(resultCollection -> resultCollection.getAllRecipes().stream().flatMap(recipe -> recipe.getOutput(resultCollection.getRegistryManager()).getTooltip(null, TooltipContext.Default.BASIC).stream()).map(text -> Formatting.strip(text.getString()).trim()).filter(text -> !text.isEmpty()), resultCollection -> resultCollection.getAllRecipes().stream().map(recipe -> Registries.ITEM.getId(recipe.getOutput(resultCollection.getRegistryManager()).getItem())), (List<RecipeResultCollection>)resultCollections));
        ItemGroups.getSearchGroup().setSearchProviderReloader(stacks -> {
            this.reloadSearchProvider(SearchManager.ITEM_TOOLTIP, (List)stacks);
            this.reloadSearchProvider(SearchManager.ITEM_TAG, (List)stacks);
        });
    }

    private void handleGlErrorByDisableVsync(int error, long description) {
        this.options.getEnableVsync().setValue(false);
        this.options.write();
    }

    private static boolean checkIs64Bit() {
        String[] strings;
        for (String string : strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"}) {
            String string2 = System.getProperty(string);
            if (string2 == null || !string2.contains("64")) continue;
            return true;
        }
        return false;
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

    public static void printCrashReport(CrashReport report) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "crash-reports");
        File file2 = new File(file, "crash-" + Util.getFormattedCurrentTime() + "-client.txt");
        Bootstrap.println(report.asString());
        if (report.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + report.getFile());
            System.exit(-1);
        } else if (report.writeToFile(file2)) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean forcesUnicodeFont() {
        return this.options.getForceUnicodeFont().getValue();
    }

    public CompletableFuture<Void> reloadResources() {
        return this.reloadResources(false);
    }

    private CompletableFuture<Void> reloadResources(boolean force) {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (!force && this.overlay instanceof SplashOverlay) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List<ResourcePack> list = this.resourcePackManager.createResourcePacks();
        if (!force) {
            this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
        }
        this.setOverlay(new SplashOverlay(this, this.resourceManager.reload(Util.getMainWorkerExecutor(), this, COMPLETED_UNIT_FUTURE, list), error -> Util.ifPresentOrElse(error, throwable -> {
            if (force) {
                this.onForcedResourceReloadFailure();
            } else {
                this.handleResourceReloadException((Throwable)throwable);
            }
        }, () -> {
            this.worldRenderer.reload();
            this.resourceReloadLogger.finish();
            completableFuture.complete(null);
        }), true));
        return completableFuture;
    }

    private void checkGameData() {
        boolean bl = false;
        BlockModels blockModels = this.getBlockRenderManager().getModels();
        BakedModel bakedModel = blockModels.getModelManager().getMissingModel();
        for (Block block : Registries.BLOCK) {
            for (BlockState blockState : block.getStateManager().getStates()) {
                BakedModel bakedModel2;
                if (blockState.getRenderType() != BlockRenderType.MODEL || (bakedModel2 = blockModels.getModel(blockState)) != bakedModel) continue;
                LOGGER.debug("Missing model for: {}", (Object)blockState);
                bl = true;
            }
        }
        Sprite sprite = bakedModel.getParticleSprite();
        for (Block block2 : Registries.BLOCK) {
            for (BlockState blockState2 : block2.getStateManager().getStates()) {
                Sprite sprite2 = blockModels.getModelParticleSprite(blockState2);
                if (blockState2.isAir() || sprite2 != sprite) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)blockState2);
                bl = true;
            }
        }
        for (Item item : Registries.ITEM) {
            ItemStack itemStack = item.getDefaultStack();
            String string = itemStack.getTranslationKey();
            String string2 = Text.translatable(string).getString();
            if (!string2.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) continue;
            LOGGER.debug("Missing translation for: {} {} {}", itemStack, string, item);
        }
        bl |= HandledScreens.isMissingScreens();
        if (bl |= EntityRenderers.isMissingRendererFactories()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    private void openChatScreen(String text) {
        ChatRestriction chatRestriction = this.getChatRestriction();
        if (!chatRestriction.allowsChat(this.isInSingleplayer())) {
            if (this.inGameHud.shouldShowChatDisabledScreen()) {
                this.inGameHud.setCanShowChatDisabledScreen(false);
                this.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open("https://aka.ms/JavaAccountSettings");
                    }
                    this.setScreen(null);
                }, ChatRestriction.MORE_INFO_TEXT, "https://aka.ms/JavaAccountSettings", true));
            } else {
                Text text2 = chatRestriction.getDescription();
                this.inGameHud.setOverlayMessage(text2, false);
                this.narratorManager.narrate(text2);
                this.inGameHud.setCanShowChatDisabledScreen(chatRestriction == ChatRestriction.DISABLED_BY_PROFILE);
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
        }
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

    public void setOverlay(@Nullable Overlay overlay) {
        this.overlay = overlay;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.narratorManager.destroy();
            } catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.disconnect();
            } catch (Throwable throwable) {
                // empty catch block
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
            this.worldRenderer.close();
            this.soundManager.close();
            this.particleManager.clearAtlas();
            this.statusEffectSpriteManager.close();
            this.paintingManager.close();
            this.textureManager.close();
            this.resourceManager.close();
            Util.shutdownExecutors();
        } catch (Throwable throwable) {
            LOGGER.error("Shutdown failure!", throwable);
            throw throwable;
        } finally {
            this.windowProvider.close();
            this.window.close();
        }
    }

    private void render(boolean tick) {
        boolean bl2;
        boolean bl;
        Runnable runnable;
        this.window.setPhase("Pre render");
        long l = Util.getMeasuringTimeNano();
        if (this.window.shouldClose()) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashOverlay)) {
            CompletableFuture<Void> completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        while ((runnable = this.renderTaskQueue.poll()) != null) {
            runnable.run();
        }
        if (tick) {
            int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
            this.profiler.push("scheduledExecutables");
            this.runTasks();
            this.profiler.pop();
            this.profiler.push("tick");
            for (int j = 0; j < Math.min(10, i); ++j) {
                this.profiler.visit("clientTick");
                this.tick();
            }
            this.profiler.pop();
        }
        this.mouse.updateMouse();
        this.window.setPhase("Render");
        this.profiler.push("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        this.profiler.pop();
        this.profiler.push("render");
        long m = Util.getMeasuringTimeNano();
        if (this.options.debugEnabled || this.recorder.isActive()) {
            boolean bl3 = bl = this.currentGlTimerQuery == null || this.currentGlTimerQuery.isResultAvailable();
            if (bl) {
                GlTimer.getInstance().ifPresent(GlTimer::beginProfile);
            }
        } else {
            bl = false;
            this.gpuUtilizationPercentage = 0.0;
        }
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, IS_SYSTEM_MAC);
        this.framebuffer.beginWrite(true);
        BackgroundRenderer.clearFog();
        this.profiler.push("display");
        RenderSystem.enableCull();
        this.profiler.pop();
        if (!this.skipGameRender) {
            this.profiler.swap("gameRenderer");
            this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, l, tick);
            this.profiler.pop();
        }
        if (this.tickProfilerResult != null) {
            this.profiler.push("fpsPie");
            this.drawProfilerResults(new MatrixStack(), this.tickProfilerResult);
            this.profiler.pop();
        }
        this.profiler.push("blit");
        this.framebuffer.endWrite();
        this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.renderTime = Util.getMeasuringTimeNano() - m;
        if (bl) {
            GlTimer.getInstance().ifPresent(glTimer -> {
                this.currentGlTimerQuery = glTimer.endProfile();
            });
        }
        this.profiler.swap("updateDisplay");
        this.window.swapBuffers();
        int k = this.getFramerateLimit();
        if (k < 260) {
            RenderSystem.limitDisplayFPS(k);
        }
        this.profiler.swap("yield");
        Thread.yield();
        this.profiler.pop();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl4 = bl2 = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.shouldPause() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (this.paused != bl2) {
            if (this.paused) {
                this.pausedTickDelta = this.renderTickCounter.tickDelta;
            } else {
                this.renderTickCounter.tickDelta = this.pausedTickDelta;
            }
            this.paused = bl2;
        }
        long n = Util.getMeasuringTimeNano();
        long o = n - this.lastMetricsSampleTime;
        if (bl) {
            this.metricsSampleDuration = o;
        }
        this.metricsData.pushSample(o);
        this.lastMetricsSampleTime = n;
        this.profiler.push("fpsUpdate");
        if (this.currentGlTimerQuery != null && this.currentGlTimerQuery.isResultAvailable()) {
            this.gpuUtilizationPercentage = (double)this.currentGlTimerQuery.queryResult() * 100.0 / (double)this.metricsSampleDuration;
        }
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            Object string = this.gpuUtilizationPercentage > 0.0 ? " GPU: " + (this.gpuUtilizationPercentage > 100.0 ? Formatting.RED + "100%" : Math.round(this.gpuUtilizationPercentage) + "%") : "";
            currentFps = this.fpsCounter;
            this.fpsDebugString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", currentFps, k == 260 ? "inf" : Integer.valueOf(k), this.options.getEnableVsync().getValue() != false ? " vsync" : "", this.options.getGraphicsMode().getValue(), this.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "" : (this.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds"), this.options.getBiomeBlendRadius().getValue(), string);
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
        }
        this.profiler.pop();
    }

    private boolean shouldMonitorTickDuration() {
        return this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden;
    }

    private Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        Profiler profiler;
        if (!active) {
            this.tickTimeTracker.disable();
            if (!this.recorder.isActive() && monitor == null) {
                return DummyProfiler.INSTANCE;
            }
        }
        if (active) {
            if (!this.tickTimeTracker.isActive()) {
                this.trackingTick = 0;
                this.tickTimeTracker.enable();
            }
            ++this.trackingTick;
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
        this.tickProfilerResult = active ? this.tickTimeTracker.getResult() : null;
        this.profiler = this.tickTimeTracker.getProfiler();
    }

    @Override
    public void onResolutionChanged() {
        int i = this.window.calculateScaleFactor(this.options.getGuiScale().getValue(), this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        Framebuffer framebuffer = this.getFramebuffer();
        framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), IS_SYSTEM_MAC);
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

    private int getFramerateLimit() {
        if (this.world == null && (this.currentScreen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }

    public void cleanUpAfterCrash() {
        try {
            CrashMemoryReserve.releaseMemory();
            this.worldRenderer.cleanUp();
        } catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            if (this.integratedServerRunning && this.server != null) {
                this.server.stop(true);
            }
            this.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        } catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean toggleDebugProfiler(Consumer<Text> chatMessageSender) {
        Consumer<Path> consumer4;
        if (this.recorder.isActive()) {
            this.stopRecorder();
            return false;
        }
        Consumer<ProfileResult> consumer = result -> {
            if (result == EmptyProfileResult.INSTANCE) {
                return;
            }
            int i = result.getTickSpan();
            double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
            this.execute(() -> chatMessageSender.accept(Text.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d))));
        };
        Consumer<Path> consumer2 = path -> {
            MutableText text = Text.literal(path.toString()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toFile().getParent())));
            this.execute(() -> chatMessageSender.accept(Text.translatable("debug.profiling.stop", text)));
        };
        SystemDetails systemDetails = MinecraftClient.addSystemDetailsToCrashReport(new SystemDetails(), this, this.languageManager, this.gameVersion, this.options);
        Consumer<List> consumer3 = files -> {
            Path path = this.saveProfilingResult(systemDetails, (List<Path>)files);
            consumer2.accept(path);
        };
        if (this.server == null) {
            consumer4 = path -> consumer3.accept(ImmutableList.of(path));
        } else {
            this.server.addSystemDetails(systemDetails);
            CompletableFuture completableFuture = new CompletableFuture();
            CompletableFuture completableFuture2 = new CompletableFuture();
            CompletableFuture.allOf(completableFuture, completableFuture2).thenRunAsync(() -> consumer3.accept(ImmutableList.of((Path)completableFuture.join(), (Path)completableFuture2.join())), Util.getIoWorkerExecutor());
            this.server.setupRecorder(result -> {}, completableFuture2::complete);
            consumer4 = completableFuture::complete;
        }
        this.recorder = DebugRecorder.of(new ClientSamplerSource(Util.nanoTimeSupplier, this.worldRenderer), Util.nanoTimeSupplier, Util.getIoWorkerExecutor(), new RecordDumper("client"), result -> {
            this.recorder = DummyRecorder.INSTANCE;
            consumer.accept((ProfileResult)result);
        }, consumer4);
        return true;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Path saveProfilingResult(SystemDetails details, List<Path> files) {
        Path path;
        ServerInfo serverInfo;
        String string = this.isInSingleplayer() ? this.getServer().getSaveProperties().getLevelName() : ((serverInfo = this.getCurrentServerEntry()) != null ? serverInfo.name : "unknown");
        try {
            String string2 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFormattedCurrentTime(), string, SharedConstants.getGameVersion().getId());
            String string3 = PathUtil.getNextUniqueName(RecordDumper.DEBUG_PROFILING_DIRECTORY, string2, ".zip");
            path = RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string3);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try (ZipCompressor zipCompressor = new ZipCompressor(path);){
            zipCompressor.write(Paths.get("system.txt", new String[0]), details.collect());
            zipCompressor.write(Paths.get("client", new String[0]).resolve(this.options.getOptionsFile().getName()), this.options.collectProfiledOptions());
            files.forEach(zipCompressor::copyAll);
        } finally {
            for (Path path2 : files) {
                try {
                    FileUtils.forceDelete(path2.toFile());
                } catch (IOException iOException2) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)path2, (Object)iOException2);
                }
            }
        }
        return path;
    }

    public void handleProfilerKeyPress(int digit) {
        if (this.tickProfilerResult == null) {
            return;
        }
        List<ProfilerTiming> list = this.tickProfilerResult.getTimings(this.openProfilerSection);
        if (list.isEmpty()) {
            return;
        }
        ProfilerTiming profilerTiming = list.remove(0);
        if (digit == 0) {
            int i;
            if (!profilerTiming.name.isEmpty() && (i = this.openProfilerSection.lastIndexOf(30)) >= 0) {
                this.openProfilerSection = this.openProfilerSection.substring(0, i);
            }
        } else if (--digit < list.size() && !"unspecified".equals(list.get((int)digit).name)) {
            if (!this.openProfilerSection.isEmpty()) {
                this.openProfilerSection = this.openProfilerSection + "\u001e";
            }
            this.openProfilerSection = this.openProfilerSection + list.get((int)digit).name;
        }
    }

    private void drawProfilerResults(MatrixStack matrices, ProfileResult profileResult) {
        int m;
        List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
        ProfilerTiming profilerTiming = list.remove(0);
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, IS_SYSTEM_MAC);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix(matrix4f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.loadIdentity();
        matrixStack.translate(0.0f, 0.0f, -2000.0f);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.lineWidth(1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int i = 160;
        int j = this.window.getFramebufferWidth() - 160 - 10;
        int k = this.window.getFramebufferHeight() - 320;
        RenderSystem.enableBlend();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((float)j - 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j - 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, k + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder.vertex((float)j + 176.0f, (float)k - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        double d = 0.0;
        for (ProfilerTiming profilerTiming2 : list) {
            float h;
            float g;
            float f;
            int q;
            int l = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            m = profilerTiming2.getColor();
            int n = m >> 16 & 0xFF;
            int o = m >> 8 & 0xFF;
            int p = m & 0xFF;
            bufferBuilder.vertex(j, k, 0.0).color(n, o, p, 255).next();
            for (q = l; q >= 0; --q) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(n, o, p, 255).next();
            }
            tessellator.draw();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (q = l; q >= 0; --q) {
                f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)q / (double)l) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 160.0f;
                h = MathHelper.cos(f) * 160.0f * 0.5f;
                if (h > 0.0f) continue;
                bufferBuilder.vertex((float)j + g, (float)k - h, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
                bufferBuilder.vertex((float)j + g, (float)k - h + 10.0f, 0.0).color(n >> 1, o >> 1, p >> 1, 255).next();
            }
            tessellator.draw();
            d += profilerTiming2.parentSectionUsagePercentage;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        String string = ProfileResult.getHumanReadableName(profilerTiming.name);
        Object string2 = "";
        if (!"unspecified".equals(string)) {
            string2 = (String)string2 + "[0] ";
        }
        string2 = string.isEmpty() ? (String)string2 + "ROOT " : (String)string2 + string + " ";
        m = 0xFFFFFF;
        this.textRenderer.drawWithShadow(matrices, (String)string2, (float)(j - 160), (float)(k - 80 - 16), 0xFFFFFF);
        string2 = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
        this.textRenderer.drawWithShadow(matrices, (String)string2, (float)(j + 160 - this.textRenderer.getWidth((String)string2)), (float)(k - 80 - 16), 0xFFFFFF);
        for (int r = 0; r < list.size(); ++r) {
            ProfilerTiming profilerTiming3 = list.get(r);
            StringBuilder stringBuilder = new StringBuilder();
            if ("unspecified".equals(profilerTiming3.name)) {
                stringBuilder.append("[?] ");
            } else {
                stringBuilder.append("[").append(r + 1).append("] ");
            }
            Object string3 = stringBuilder.append(profilerTiming3.name).toString();
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j - 160), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
            string3 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j + 160 - 50 - this.textRenderer.getWidth((String)string3)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
            string3 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(matrices, (String)string3, (float)(j + 160 - this.textRenderer.getWidth((String)string3)), (float)(k + 80 + r * 8 + 20), profilerTiming3.getColor());
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    public void scheduleStop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void openPauseMenu(boolean pause) {
        boolean bl;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl2 = bl = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl) {
            this.setScreen(new GameMenuScreen(!pause));
            this.soundManager.pauseAll();
        } else {
            this.setScreen(new GameMenuScreen(true));
        }
    }

    private void handleBlockBreaking(boolean breaking) {
        if (!breaking) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Direction direction;
            BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, direction = blockHitResult.getSide())) {
                this.particleManager.addBlockBreakingParticles(blockPos, direction);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private boolean doAttack() {
        if (this.attackCooldown > 0) {
            return false;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return false;
        }
        if (this.player.isRiding()) {
            return false;
        }
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
            return false;
        }
        boolean bl = false;
        switch (this.crosshairTarget.getType()) {
            case ENTITY: {
                this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir()) {
                    this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                    if (!this.world.getBlockState(blockPos).isAir()) break;
                    bl = true;
                    break;
                }
            }
            case MISS: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.resetLastAttackedTicks();
            }
        }
        this.player.swingHand(Hand.MAIN_HAND);
        return bl;
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (Hand hand : Hand.values()) {
            ActionResult actionResult3;
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                return;
            }
            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
                            return;
                        }
                        ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
                        if (!actionResult.isAccepted()) {
                            actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
                        }
                        if (!actionResult.isAccepted()) break;
                        if (actionResult.shouldSwingHand()) {
                            this.player.swingHand(hand);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
                        if (actionResult2.isAccepted()) {
                            if (actionResult2.shouldSwingHand()) {
                                this.player.swingHand(hand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                }
                            }
                            return;
                        }
                        if (actionResult2 != ActionResult.FAIL) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || !(actionResult3 = this.interactionManager.interactItem(this.player, hand)).isAccepted()) continue;
            if (actionResult3.shouldSwingHand()) {
                this.player.swingHand(hand);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            return;
        }
    }

    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }

    public void tick() {
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        this.profiler.push("gui");
        this.messageHandler.processDelayedMessages();
        this.inGameHud.tick(this.paused);
        this.profiler.pop();
        this.gameRenderer.updateTargetedEntity(1.0f);
        this.tutorialManager.tick(this.world, this.crosshairTarget);
        this.profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        this.profiler.swap("textures");
        if (this.world != null) {
            this.textureManager.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.isDead() && !(this.currentScreen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.setScreen(new SleepingChatScreen());
            }
        } else {
            Screen screen = this.currentScreen;
            if (screen instanceof SleepingChatScreen) {
                SleepingChatScreen sleepingChatScreen = (SleepingChatScreen)screen;
                if (!this.player.isSleeping()) {
                    sleepingChatScreen.closeChatIfEmpty();
                }
            }
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
            this.profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
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
                if (this.world.getLightningTicksLeft() > 0) {
                    this.world.setLightningTicksLeft(this.world.getLightningTicksLeft() - 1);
                }
                this.world.tickEntities();
            }
        } else if (this.gameRenderer.getPostProcessor() != null) {
            this.gameRenderer.disablePostProcessor();
        }
        if (!this.paused) {
            this.musicTracker.tick();
        }
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            if (!this.paused) {
                if (!this.options.joinedFirstServer && this.isConnectedToServer()) {
                    MutableText text = Text.translatable("tutorial.socialInteractions.title");
                    MutableText text2 = Text.translatable("tutorial.socialInteractions.description", TutorialManager.keyToText("socialInteractions"));
                    this.socialInteractionsToast = new TutorialToast(TutorialToast.Type.SOCIAL_INTERACTIONS, text, text2, true);
                    this.tutorialManager.add(this.socialInteractionsToast, 160);
                    this.options.joinedFirstServer = true;
                    this.options.write();
                }
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Exception in world tick");
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
                this.world.doRandomBlockDisplayTicks(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        } else if (this.integratedServerConnection != null) {
            this.profiler.swap("pendingConnection");
            this.integratedServerConnection.tick();
        }
        this.profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        this.profiler.pop();
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
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.saveToolbarActivatorKey.isPressed();
            boolean bl2 = this.options.loadToolbarActivatorKey.isPressed();
            if (!this.options.hotbarKeys[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isCreative() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                continue;
            }
            this.player.getInventory().selectedSlot = i;
        }
        while (this.options.socialInteractionsKey.wasPressed()) {
            if (!this.isConnectedToServer()) {
                this.player.sendMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                this.narratorManager.narrate(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.tutorialManager.remove(this.socialInteractionsToast);
                this.socialInteractionsToast = null;
            }
            this.setScreen(new SocialInteractionsScreen());
        }
        while (this.options.inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.setScreen(new InventoryScreen(this.player));
        }
        while (this.options.advancementsKey.wasPressed()) {
            this.setScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.swapHandsKey.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.dropKey.wasPressed()) {
            if (this.player.isSpectator() || !this.player.dropSelectedItem(Screen.hasControlDown())) continue;
            this.player.swingHand(Hand.MAIN_HAND);
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

    public void startIntegratedServer(String levelName, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld) {
        this.disconnect();
        this.worldGenProgressTracker.set(null);
        Instant instant = Instant.now();
        try {
            session.backupLevelDataFile(saveLoader.combinedDynamicRegistries().getCombinedRegistryManager(), saveLoader.saveProperties());
            ApiServices apiServices = ApiServices.create(this.authenticationService, this.runDirectory);
            apiServices.userCache().setExecutor(this);
            SkullBlockEntity.setServices(apiServices, this);
            UserCache.setUseRemote(false);
            this.server = MinecraftServer.startServer(thread -> new IntegratedServer((Thread)thread, this, session, dataPackManager, saveLoader, apiServices, spawnChunkRadius -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(spawnChunkRadius + 0);
                this.worldGenProgressTracker.set(worldGenerationProgressTracker);
                return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, this.renderTaskQueue::add);
            }));
            this.integratedServerRunning = true;
            this.ensureAbuseReportContext(ReporterEnvironment.ofIntegratedServer());
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting integrated server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
            crashReportSection.add("Level ID", levelName);
            crashReportSection.add("Level Name", () -> saveLoader.saveProperties().getLevelName());
            throw new CrashException(crashReport);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen(this.worldGenProgressTracker.get());
        this.setScreen(levelLoadingScreen);
        this.profiler.push("waitForServer");
        while (!this.server.isLoading()) {
            levelLoadingScreen.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            } catch (InterruptedException crashReport) {
                // empty catch block
            }
            if (this.crashReportSupplier == null) continue;
            MinecraftClient.printCrashReport(this.crashReportSupplier.get());
            return;
        }
        this.profiler.pop();
        Duration duration = Duration.between(instant, Instant.now());
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, null, newWorld, duration, status -> {}));
        clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        clientConnection.send(new LoginHelloC2SPacket(this.getSession().getUsername(), Optional.ofNullable(this.getSession().getUuidOrNull())));
        this.integratedServerConnection = clientConnection;
    }

    public void joinWorld(ClientWorld world) {
        ProgressScreen progressScreen = new ProgressScreen(true);
        progressScreen.setTitle(Text.translatable("connect.joining"));
        this.reset(progressScreen);
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
        this.disconnect(new ProgressScreen(true));
    }

    public void disconnect(Screen screen) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            this.cancelTasks();
            clientPlayNetworkHandler.clearWorld();
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
        this.reset(screen);
        if (this.world != null) {
            if (integratedServer != null) {
                this.profiler.push("waitForServer");
                while (!integratedServer.isStopping()) {
                    this.render(false);
                }
                this.profiler.pop();
            }
            this.serverResourcePackProvider.clear();
            this.inGameHud.clear();
            this.integratedServerRunning = false;
        }
        this.world = null;
        this.setWorld(null);
        this.player = null;
        SkullBlockEntity.clearServices();
    }

    private void reset(Screen screen) {
        this.profiler.push("forcedTick");
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.integratedServerConnection = null;
        this.setScreen(screen);
        this.render(false);
        this.profiler.pop();
    }

    public void setScreenAndRender(Screen screen) {
        this.profiler.push("forcedTick");
        this.setScreen(screen);
        this.render(false);
        this.profiler.pop();
    }

    private void setWorld(@Nullable ClientWorld world) {
        this.worldRenderer.setWorld(world);
        this.particleManager.setWorld(world);
        this.blockEntityRenderDispatcher.setWorld(world);
        this.updateWindowTitle();
    }

    public boolean isOptionalTelemetryEnabled() {
        return this.isOptionalTelemetryEnabledByApi() && this.options.getTelemetryOptInExtra().getValue() != false;
    }

    public boolean isOptionalTelemetryEnabledByApi() {
        return this.isTelemetryEnabledByApi() && this.userApiService.properties().flag(UserApiService.UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
    }

    public boolean isTelemetryEnabledByApi() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.TELEMETRY_ENABLED);
    }

    public boolean isMultiplayerEnabled() {
        return this.multiplayerEnabled && this.userApiService.properties().flag(UserApiService.UserFlag.SERVERS_ALLOWED) && this.getMultiplayerBanDetails() == null;
    }

    public boolean isRealmsEnabled() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.REALMS_ALLOWED) && this.getMultiplayerBanDetails() == null;
    }

    public boolean isMultiplayerBanned() {
        return this.getMultiplayerBanDetails() != null;
    }

    @Nullable
    public BanDetails getMultiplayerBanDetails() {
        return this.userApiService.properties().bannedScopes().get("MULTIPLAYER");
    }

    /**
     * Checks if the client should block messages from the {@code sender}.
     * 
     * <p>If true, messages will not be displayed in chat and narrator will not process
     * them.
     */
    public boolean shouldBlockMessages(UUID sender) {
        if (!this.getChatRestriction().allowsChat(false)) {
            return (this.player == null || !sender.equals(this.player.getUuid())) && !sender.equals(Util.NIL_UUID);
        }
        return this.socialInteractionsManager.isPlayerMuted(sender);
    }

    public ChatRestriction getChatRestriction() {
        if (this.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) {
            return ChatRestriction.DISABLED_BY_OPTIONS;
        }
        if (!this.onlineChatEnabled) {
            return ChatRestriction.DISABLED_BY_LAUNCHER;
        }
        if (!this.userApiService.properties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatRestriction.DISABLED_BY_PROFILE;
        }
        return ChatRestriction.ENABLED;
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean isFancyGraphicsOrBetter() {
        return MinecraftClient.instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FANCY.getId();
    }

    public static boolean isFabulousGraphicsOrBetter() {
        return !MinecraftClient.instance.gameRenderer.isRenderingPanorama() && MinecraftClient.instance.options.getGraphicsMode().getValue().getId() >= GraphicsMode.FABULOUS.getId();
    }

    public static boolean isAmbientOcclusionEnabled() {
        return MinecraftClient.instance.options.getAo().getValue();
    }

    private void doItemPick() {
        ItemStack itemStack;
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = this.player.getAbilities().creativeMode;
        BlockEntity blockEntity = null;
        HitResult.Type type = this.crosshairTarget.getType();
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
        } else if (type == HitResult.Type.ENTITY && bl) {
            Entity entity = ((EntityHitResult)this.crosshairTarget).getEntity();
            itemStack = entity.getPickBlockStack();
            if (itemStack == null) {
                return;
            }
        } else {
            return;
        }
        if (itemStack.isEmpty()) {
            String string = "";
            if (type == HitResult.Type.BLOCK) {
                string = Registries.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.crosshairTarget).getBlockPos()).getBlock()).toString();
            } else if (type == HitResult.Type.ENTITY) {
                string = Registries.ENTITY_TYPE.getId(((EntityHitResult)this.crosshairTarget).getEntity().getType()).toString();
            }
            LOGGER.warn("Picking on: [{}] {} gave null item", (Object)type, (Object)string);
            return;
        }
        PlayerInventory playerInventory = this.player.getInventory();
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

    private void addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity) {
        NbtCompound nbtCompound = blockEntity.createNbtWithIdentifyingData();
        BlockItem.setBlockEntityNbt(stack, blockEntity.getType(), nbtCompound);
        if (stack.getItem() instanceof SkullItem && nbtCompound.contains("SkullOwner")) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("SkullOwner");
            NbtCompound nbtCompound3 = stack.getOrCreateNbt();
            nbtCompound3.put("SkullOwner", nbtCompound2);
            NbtCompound nbtCompound4 = nbtCompound3.getCompound("BlockEntityTag");
            nbtCompound4.remove("SkullOwner");
            nbtCompound4.remove("x");
            nbtCompound4.remove("y");
            nbtCompound4.remove("z");
            return;
        }
        NbtCompound nbtCompound2 = new NbtCompound();
        NbtList nbtList = new NbtList();
        nbtList.add(NbtString.of("\"(+NBT)\""));
        nbtCompound2.put("Lore", nbtList);
        stack.setSubNbt("display", nbtCompound2);
    }

    public CrashReport addDetailsToCrashReport(CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport(systemDetails, this, this.languageManager, this.gameVersion, this.options);
        if (this.world != null) {
            this.world.addDetailsToCrashReport(report);
        }
        if (this.server != null) {
            this.server.addSystemDetails(systemDetails);
        }
        this.resourceReloadLogger.addReloadSection(report);
        return report;
    }

    public static void addSystemDetailsToCrashReport(@Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options, CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport(systemDetails, client, languageManager, version, options);
    }

    private static SystemDetails addSystemDetailsToCrashReport(SystemDetails systemDetails, @Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, GameOptions options) {
        systemDetails.addSection("Launched Version", () -> version);
        systemDetails.addSection("Backend library", RenderSystem::getBackendDescription);
        systemDetails.addSection("Backend API", RenderSystem::getApiDescription);
        systemDetails.addSection("Window size", () -> client != null ? minecraftClient.window.getFramebufferWidth() + "x" + minecraftClient.window.getFramebufferHeight() : "<not initialized>");
        systemDetails.addSection("GL Caps", RenderSystem::getCapsString);
        systemDetails.addSection("GL debug messages", () -> GlDebug.isDebugMessageEnabled() ? String.join((CharSequence)"\n", GlDebug.collectDebugMessages()) : "<disabled>");
        systemDetails.addSection("Using VBOs", () -> "Yes");
        systemDetails.addSection("Is Modded", () -> MinecraftClient.getModStatus().getMessage());
        systemDetails.addSection("Type", "Client (map_client.txt)");
        if (options != null) {
            String string;
            if (instance != null && (string = instance.getVideoWarningManager().getWarningsAsString()) != null) {
                systemDetails.addSection("GPU Warnings", string);
            }
            systemDetails.addSection("Graphics mode", options.getGraphicsMode().getValue().toString());
            systemDetails.addSection("Resource Packs", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (String string : gameOptions.resourcePacks) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(string);
                    if (!gameOptions.incompatibleResourcePacks.contains(string)) continue;
                    stringBuilder.append(" (incompatible)");
                }
                return stringBuilder.toString();
            });
        }
        if (languageManager != null) {
            systemDetails.addSection("Current Language", () -> languageManager.getLanguage());
        }
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
        return this.networkProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
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

    public ServerResourcePackProvider getServerResourcePackProvider() {
        return this.serverResourcePackProvider;
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

    public boolean is64Bit() {
        return this.is64Bit;
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
        if (this.currentScreen instanceof CreditsScreen) {
            return MusicType.CREDITS;
        }
        if (this.player != null) {
            if (this.player.world.getRegistryKey() == World.END) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    return MusicType.DRAGON;
                }
                return MusicType.END;
            }
            RegistryEntry<Biome> registryEntry = this.player.world.getBiome(this.player.getBlockPos());
            if (this.musicTracker.isPlayingType(MusicType.UNDERWATER) || this.player.isSubmergedInWater() && registryEntry.isIn(BiomeTags.PLAYS_UNDERWATER_MUSIC)) {
                return MusicType.UNDERWATER;
            }
            if (this.player.world.getRegistryKey() != World.NETHER && this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
                return MusicType.CREATIVE;
            }
            return registryEntry.value().getMusic().orElse(MusicType.GAME);
        }
        return MusicType.MENU;
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
        return entity.isGlowing() || this.player != null && this.player.isSpectator() && this.options.spectatorOutlinesKey.isPressed() && entity.getType() == EntityType.PLAYER;
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

    public <T> SearchProvider<T> getSearchProvider(SearchManager.Key<T> key) {
        return this.searchManager.get(key);
    }

    public <T> void reloadSearchProvider(SearchManager.Key<T> key, List<T> values) {
        this.searchManager.reload(key, values);
    }

    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean connectedToRealms) {
        this.connectedToRealms = connectedToRealms;
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

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.hasReducedDebugInfo() || this.options.getReducedDebugInfo().getValue() != false;
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

    @Override
    public void onWindowFocusChanged(boolean focused) {
        this.windowFocused = focused;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    /**
     * Takes a panorama. The panorama is stored in the given {@code directory}, in
     * where 6 screenshots of size {@code width} and {@code height} will be taken.
     * 
     * @return a user-oriented piece of text for screenshot result
     */
    public Text takePanorama(File directory, int width, int height) {
        int i = this.window.getFramebufferWidth();
        int j = this.window.getFramebufferHeight();
        SimpleFramebuffer framebuffer = new SimpleFramebuffer(width, height, true, IS_SYSTEM_MAC);
        float f = this.player.getPitch();
        float g = this.player.getYaw();
        float h = this.player.prevPitch;
        float k = this.player.prevYaw;
        this.gameRenderer.setBlockOutlineEnabled(false);
        try {
            this.gameRenderer.setRenderingPanorama(true);
            this.worldRenderer.reloadTransparencyPostProcessor();
            this.window.setFramebufferWidth(width);
            this.window.setFramebufferHeight(height);
            for (int l = 0; l < 6; ++l) {
                switch (l) {
                    case 0: {
                        this.player.setYaw(g);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYaw((g + 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYaw((g + 180.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYaw((g - 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYaw(g);
                        this.player.setPitch(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYaw(g);
                        this.player.setPitch(90.0f);
                    }
                }
                this.player.prevYaw = this.player.getYaw();
                this.player.prevPitch = this.player.getPitch();
                framebuffer.beginWrite(true);
                this.gameRenderer.renderWorld(1.0f, 0L, new MatrixStack());
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ScreenshotRecorder.saveScreenshot(directory, "panorama_" + l + ".png", framebuffer, message -> {});
            }
            MutableText text = Text.literal(directory.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, directory.getAbsolutePath())));
            MutableText mutableText = Text.translatable("screenshot.success", text);
            return mutableText;
        } catch (Exception exception) {
            LOGGER.error("Couldn't save image", exception);
            MutableText mutableText = Text.translatable("screenshot.failure", exception.getMessage());
            return mutableText;
        } finally {
            this.player.setPitch(f);
            this.player.setYaw(g);
            this.player.prevPitch = h;
            this.player.prevYaw = k;
            this.gameRenderer.setBlockOutlineEnabled(true);
            this.window.setFramebufferWidth(i);
            this.window.setFramebufferHeight(j);
            framebuffer.delete();
            this.gameRenderer.setRenderingPanorama(false);
            this.worldRenderer.reloadTransparencyPostProcessor();
            this.getFramebuffer().beginWrite(true);
        }
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
                    float k = (float)(width - unitWidth) / 2.0f * 2.0f - (float)(j * 2);
                    float l = (float)(height - unitHeight) / 2.0f * 2.0f - (float)(i * 2);
                    this.gameRenderer.renderWithZoom(h, k /= (float)unitWidth, l /= (float)unitHeight);
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
            MutableText text = Text.literal(file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
            return Text.translatable("screenshot.success", text);
        } catch (Exception exception) {
            LOGGER.warn("Couldn't save screenshot", exception);
            return Text.translatable("screenshot.failure", exception.getMessage());
        }
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    @Nullable
    public WorldGenerationProgressTracker getWorldGenerationProgressTracker() {
        return this.worldGenProgressTracker.get();
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

    public boolean shouldRenderAsync() {
        return false;
    }

    public Window getWindow() {
        return this.window;
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

    public boolean shouldFilterText() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void loadBlockList() {
        this.socialInteractionsManager.loadBlockList();
        this.getProfileKeys().fetchKeyPair();
    }

    public Realms32BitWarningChecker getRealms32BitWarningChecker() {
        return this.realms32BitWarningChecker;
    }

    public SignatureVerifier getServicesSignatureVerifier() {
        return this.servicesSignatureVerifier;
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

    static {
        LOGGER = LogUtils.getLogger();
        IS_SYSTEM_MAC = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
        DEFAULT_FONT_ID = new Identifier("default");
        UNICODE_FONT_ID = new Identifier("uniform");
        ALT_TEXT_RENDERER_ID = new Identifier("alt");
        REGIONAL_COMPLIANCIES_ID = new Identifier("regional_compliancies.json");
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = Text.translatable("multiplayer.socialInteractions.not_available");
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    @Environment(value=EnvType.CLIENT)
    public static enum ChatRestriction {
        ENABLED(ScreenTexts.EMPTY){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return true;
            }
        }
        ,
        DISABLED_BY_OPTIONS(Text.translatable("chat.disabled.options").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return false;
            }
        }
        ,
        DISABLED_BY_LAUNCHER(Text.translatable("chat.disabled.launcher").formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        }
        ,
        DISABLED_BY_PROFILE(Text.translatable("chat.disabled.profile", Text.keybind(MinecraftClient.instance.options.chatKey.getTranslationKey())).formatted(Formatting.RED)){

            @Override
            public boolean allowsChat(boolean singlePlayer) {
                return singlePlayer;
            }
        };

        static final Text MORE_INFO_TEXT;
        private final Text description;

        ChatRestriction(Text description) {
            this.description = description;
        }

        public Text getDescription() {
            return this.description;
        }

        public abstract boolean allowsChat(boolean var1);

        static {
            MORE_INFO_TEXT = Text.translatable("chat.disabled.profile.moreInfo");
        }
    }
}

