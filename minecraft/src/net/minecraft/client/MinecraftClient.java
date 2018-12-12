package net.minecraft.client;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.client.audio.MusicTracker;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.font.FontRendererManager;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gui.CloseWorldGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.MainMenuGui;
import net.minecraft.client.gui.SplashGui;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.ingame.ChatGui;
import net.minecraft.client.gui.ingame.ChatSleepingGui;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryGui;
import net.minecraft.client.gui.ingame.DeathGui;
import net.minecraft.client.gui.ingame.PlayerInventoryGui;
import net.minecraft.client.gui.menu.AdvancementsGui;
import net.minecraft.client.gui.menu.EndCreditsGui;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.gui.menu.OutOfMemoryGui;
import net.minecraft.client.gui.menu.PauseMenuGui;
import net.minecraft.client.gui.menu.ServerConnectingGui;
import net.minecraft.client.gui.menu.WorkingGui;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.recipe.book.ClientRecipeBook;
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
import net.minecraft.client.render.debug.RenderDebug;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemColorMap;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.client.resource.FoliageColormapResourceLoader;
import net.minecraft.client.resource.GrassColormapResourceLoader;
import net.minecraft.client.resource.RedirectedResourcePack;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.client.sortme.PlayerSkinProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
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
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.nbt.Tag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.packet.LoginHelloServerPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.KeybindTextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
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
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MinecraftClient extends ThreadTaskQueue<Runnable> implements SnooperListener, WindowEventHandler, InputListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final boolean isSystemMac = SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.MAC;
	public static final Identifier defaultFontRendererId = new Identifier("default");
	public static final Identifier altFontRendererId = new Identifier("alt");
	public static byte[] memoryReservedForCrash = new byte[10485760];
	private static int cachedMaxTextureSize = -1;
	private final File resourcePackDir;
	private final PropertyMap sessionPropertyMap;
	private final WindowSettings windowSettings;
	private ServerEntry field_1699;
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
	public WorldRenderer field_1769;
	private EntityRenderDispatcher entityRenderManager;
	private ItemRenderer itemRenderer;
	private FirstPersonRenderer firstPersonRenderer;
	public ClientPlayerEntity player;
	@Nullable
	private Entity cameraEntity;
	@Nullable
	public Entity field_1692;
	public ParticleManager particleManager;
	private final SearchManager searchManager = new SearchManager();
	private final Session session;
	private boolean field_1734;
	private float field_1741;
	public FontRenderer fontRenderer;
	@Nullable
	public Gui currentGui;
	public GameRenderer field_1773;
	public RenderDebug renderDebug;
	int attackCooldown;
	@Nullable
	private IntegratedServer server;
	public InGameHud hudInGame;
	public boolean field_1743;
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
	private int field_1736;
	public final MetricsData metricsData = new MetricsData();
	private long field_1750 = SystemUtil.getMeasuringTimeNano();
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
	private FontRendererManager fontManager;
	private final MinecraftSessionService sessionService;
	private PlayerSkinProvider skinProvider;
	private final Thread thread = Thread.currentThread();
	private BakedModelManager bakedModelManager;
	private BlockRenderManager blockRenderManager;
	private final ToastManager toastManager;
	private final MinecraftClientGame game = new MinecraftClientGame(this);
	private volatile boolean isRunning = true;
	public String fpsDebugString = "";
	public boolean field_1730 = true;
	private long field_1712;
	private int fpsCounter;
	private final TutorialManager tutorialManager;
	private boolean isWindowFocused;
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
			crashReport.method_562("Initialization");
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
							this.openGui(new OutOfMemoryGui());
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
			SystemUtil.NANO_TIME_SUPPLIER = longSupplier;
		}

		this.windowProvider = new WindowProvider(this);
		this.window = this.windowProvider
			.createWindow(windowSettings, this.field_1690.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
		this.setWindowFocused(true);

		try {
			InputStream inputStream = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_16x16.png"));
			InputStream inputStream2 = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_32x32.png"));
			this.window.setWindowIcon(inputStream, inputStream2);
		} catch (IOException var5) {
			LOGGER.error("Couldn't set icon", (Throwable)var5);
		}

		this.window.setFramerateLimit(this.field_1690.maxFps);
		this.field_1729 = new Mouse(this);
		this.field_1729.setup(this.window.getHandle());
		this.keyboard = new Keyboard(this);
		this.keyboard.setup(this.window.getHandle());
		GLX.init();
		GlDebug.enableDebug(this.field_1690.glDebugVerbosity, false);
		this.framebuffer = new GlFramebuffer(this.window.getWindowWidth(), this.window.getWindowHeight(), true, isSystemMac);
		this.framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.ASSETS);
		this.languageManager = new LanguageManager(this.field_1690.language);
		this.resourceManager.addListener(this.languageManager);
		this.field_1690.method_1627(this.resourcePackContainerManager);
		this.reloadResources();
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.addListener(this.textureManager);
		this.onResolutionChanged();
		this.openGui(new SplashGui());
		this.drawGuiWithoutMouse();
		this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
		this.levelStorage = new AnvilLevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
		this.soundLoader = new SoundLoader(this.resourceManager, this.field_1690);
		this.resourceManager.addListener(this.soundLoader);
		this.musicTracker = new MusicTracker(this);
		this.fontManager = new FontRendererManager(this.textureManager, this.forcesUnicodeFont());
		this.resourceManager.addListener(this.fontManager);
		this.fontRenderer = this.fontManager.getFontRenderer(defaultFontRendererId);
		if (this.field_1690.language != null) {
			this.fontRenderer.setRightToLeft(this.languageManager.isRightToLeft());
		}

		this.resourceManager.addListener(new GrassColormapResourceLoader());
		this.resourceManager.addListener(new FoliageColormapResourceLoader());
		this.window.setPhase("Startup");
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.window.setPhase("Post startup");
		this.spriteAtlas = new SpriteAtlasTexture("textures");
		this.spriteAtlas.setMipLevel(this.field_1690.mipmapLevels);
		this.textureManager.registerTextureUpdateable(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.spriteAtlas);
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.spriteAtlas.setFilter(false, this.field_1690.mipmapLevels > 0);
		this.bakedModelManager = new BakedModelManager(this.spriteAtlas);
		this.resourceManager.addListener(this.bakedModelManager);
		this.blockColorMap = BlockColorMap.create();
		this.itemColorMap = ItemColorMap.create(this.blockColorMap);
		this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColorMap);
		this.entityRenderManager = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
		this.firstPersonRenderer = new FirstPersonRenderer(this);
		this.resourceManager.addListener(this.itemRenderer);
		this.field_1773 = new GameRenderer(this, this.resourceManager);
		this.resourceManager.addListener(this.field_1773);
		this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockStateMaps(), this.blockColorMap);
		this.resourceManager.addListener(this.blockRenderManager);
		this.field_1769 = new WorldRenderer(this);
		this.resourceManager.addListener(this.field_1769);
		this.method_1546();
		this.resourceManager.addListener(this.searchManager);
		GlStateManager.viewport(0, 0, this.window.getWindowWidth(), this.window.getWindowHeight());
		this.particleManager = new ParticleManager(this.world, this.textureManager);
		this.hudInGame = new InGameHud(this);
		if (this.autoConnectServerIp != null) {
			this.openGui(new ServerConnectingGui(new MainMenuGui(), this, this.autoConnectServerIp, this.autoConnectServerPort));
		} else {
			this.openGui(new MainMenuGui());
		}

		this.renderDebug = new RenderDebug(this);
		GLX.setGlfwErrorCallback(this::method_1506);
		if (this.field_1690.fullscreen && !this.window.isFullscreen()) {
			this.window.toggleFullscreen();
			this.field_1690.fullscreen = this.window.isFullscreen();
		}

		this.window.setVsync(this.field_1690.enableVsync);
		this.window.logOnGlError();
		this.field_1769.method_3296();
		if (SharedConstants.isDevelopment) {
			this.checkGameData();
		}
	}

	private void method_1546() {
		TextSearchableContainer<ItemStack> textSearchableContainer = new TextSearchableContainer<>(
			itemStack -> itemStack.getTooltipText(null, TooltipOptions.Instance.NORMAL)
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
			item.addStacksForDisplay(ItemGroup.SEARCH, defaultedList);
		}

		defaultedList.forEach(itemStack -> {
			textSearchableContainer.add(itemStack);
			identifierSearchableContainer.add(itemStack);
		});
		TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<>(
			recipeResultCollection -> recipeResultCollection.getAllRecipes()
					.stream()
					.flatMap(recipe -> recipe.getOutput().getTooltipText(null, TooltipOptions.Instance.NORMAL).stream())
					.map(textComponent -> TextFormat.stripFormatting(textComponent.getString()).trim())
					.filter(string -> !string.isEmpty()),
			recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem()))
		);
		this.searchManager.put(SearchManager.ITEMS_TOOLTIP, textSearchableContainer);
		this.searchManager.put(SearchManager.ITEMS_TAG, identifierSearchableContainer);
		this.searchManager.put(SearchManager.field_5496, textSearchableContainer2);
	}

	private void method_1506(int i, long l) {
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

	public void reloadResources() {
		this.resourcePackContainerManager.callCreators();
		List<ResourcePack> list = (List<ResourcePack>)this.resourcePackContainerManager
			.getEnabledContainers()
			.stream()
			.map(ResourcePackContainer::createResourcePack)
			.collect(Collectors.toList());
		if (this.server != null) {
			this.server.reload();
		}

		try {
			this.resourceManager.reload(list);
		} catch (RuntimeException var4) {
			LOGGER.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)var4);
			this.resourcePackContainerManager.resetEnabled(Collections.emptyList());
			List<ResourcePack> list2 = (List<ResourcePack>)this.resourcePackContainerManager
				.getEnabledContainers()
				.stream()
				.map(ResourcePackContainer::createResourcePack)
				.collect(Collectors.toList());
			this.resourceManager.reload(list2);
			this.field_1690.resourcePacks.clear();
			this.field_1690.incompatibleResourcePacks.clear();
			this.field_1690.write();
		}

		this.languageManager.reloadResources(list);
		if (this.field_1769 != null) {
			this.field_1769.reload();
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
			item.addStacksForDisplay(ItemGroup.SEARCH, defaultedList);

			for (ItemStack itemStack : defaultedList) {
				String string = new TranslatableTextComponent(itemStack.getTranslationKey()).getString();
				if (string.toLowerCase(Locale.ROOT).equals(string)) {
					LOGGER.debug("Missing translation for: {} {} {}", itemStack, itemStack.getTranslationKey(), itemStack.getItem());
					bl = true;
				}
			}
		}

		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	private void drawGuiWithoutMouse() {
		this.window.method_4493(isSystemMac);
		this.currentGui.draw(0, 0, 0.0F);
		this.updateDisplay(false);
	}

	public void method_1501(int i, int j, int k, int l, int m, int n, int o, int p, int q, int r) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		float f = 0.00390625F;
		float g = 0.00390625F;
		bufferBuilder.vertex((double)i, (double)(j + n), 0.0)
			.texture((double)((float)k * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.color(o, p, q, r)
			.next();
		bufferBuilder.vertex((double)(i + m), (double)(j + n), 0.0)
			.texture((double)((float)(k + m) * 0.00390625F), (double)((float)(l + n) * 0.00390625F))
			.color(o, p, q, r)
			.next();
		bufferBuilder.vertex((double)(i + m), (double)j, 0.0)
			.texture((double)((float)(k + m) * 0.00390625F), (double)((float)l * 0.00390625F))
			.color(o, p, q, r)
			.next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).texture((double)((float)k * 0.00390625F), (double)((float)l * 0.00390625F)).color(o, p, q, r).next();
		Tessellator.getInstance().draw();
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.currentGui;
	}

	public void openGui(@Nullable Gui gui) {
		if (this.currentGui != null) {
			this.currentGui.onClosed();
		}

		if (gui == null && this.world == null) {
			gui = new MainMenuGui();
		} else if (gui == null && this.player.getHealth() <= 0.0F) {
			gui = new DeathGui(null);
		}

		if (gui instanceof MainMenuGui || gui instanceof MultiplayerGui) {
			this.field_1690.debugEnabled = false;
			this.hudInGame.getHudChat().clear(true);
		}

		this.currentGui = gui;
		if (gui != null) {
			this.field_1729.unlockCursor();
			KeyBinding.method_1437();
			gui.initialize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
			this.field_1743 = false;
		} else {
			this.soundLoader.resume();
			this.field_1729.lockCursor();
		}
	}

	public void stop() {
		try {
			LOGGER.info("Stopping!");

			try {
				if (this.world != null) {
					this.world.method_8525();
				}

				this.method_1481(null);
			} catch (Throwable var5) {
			}

			if (this.currentGui != null) {
				this.currentGui.onClosed();
			}

			this.spriteAtlas.clear();
			this.fontRenderer.close();
			this.field_1773.close();
			this.field_1769.close();
			this.soundLoader.deinitialize();
		} finally {
			this.windowProvider.close();
			this.window.close();
			SystemUtil.NANO_TIME_SUPPLIER = System::nanoTime;
			if (!this.crashed) {
				System.exit(0);
			}
		}

		System.gc();
	}

	private void render(boolean bl) {
		this.window.setPhase("Pre render");
		long l = SystemUtil.getMeasuringTimeNano();
		this.profiler.startTick();
		if (GLX.shouldClose(this.window)) {
			this.stopThread();
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

		this.field_1729.updateMouse();
		this.window.setPhase("Render");
		GLX.pollEvents();
		long n = SystemUtil.getMeasuringTimeNano() - m;
		this.profiler.swap("sound");
		this.soundLoader.updateListenerPosition(this.player, this.renderTickCounter.tickDelta);
		this.profiler.pop();
		this.profiler.push("render");
		GlStateManager.pushMatrix();
		GlStateManager.clear(16640, isSystemMac);
		this.framebuffer.beginWrite(true);
		this.profiler.push("display");
		GlStateManager.enableTexture();
		this.profiler.pop();
		if (!this.field_1743) {
			this.profiler.swap("gameRenderer");
			this.field_1773.method_3192(this.field_1734 ? this.field_1741 : this.renderTickCounter.tickDelta, l, bl);
			this.profiler.swap("toasts");
			this.toastManager.draw();
			this.profiler.pop();
		}

		this.profiler.endTick();
		if (this.field_1690.debugEnabled && this.field_1690.debugProfilerEnabled && !this.field_1690.field_1842) {
			this.profiler.getController().method_16060();
			this.drawProfilerResults();
		} else {
			this.profiler.getController().method_16058();
		}

		this.framebuffer.endWrite();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.framebuffer.draw(this.window.getWindowWidth(), this.window.getWindowHeight());
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.field_1773.method_3200(this.renderTickCounter.tickDelta);
		GlStateManager.popMatrix();
		this.profiler.startTick();
		this.updateDisplay(true);
		Thread.yield();
		this.window.setPhase("Post render");
		this.fpsCounter++;
		boolean bl2 = this.method_1496() && this.currentGui != null && this.currentGui.isPauseScreen() && !this.server.isRemote();
		if (this.field_1734 != bl2) {
			if (this.field_1734) {
				this.field_1741 = this.renderTickCounter.tickDelta;
			} else {
				this.renderTickCounter.tickDelta = this.field_1741;
			}

			this.field_1734 = bl2;
		}

		long o = SystemUtil.getMeasuringTimeNano();
		this.metricsData.pushSample(o - this.field_1750);
		this.field_1750 = o;

		while (SystemUtil.getMeasuringTimeMs() >= this.field_1712 + 1000L) {
			currentFps = this.fpsCounter;
			this.fpsDebugString = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				currentFps,
				ChunkRenderer.chunkUpdateCount,
				ChunkRenderer.chunkUpdateCount == 1 ? "" : "s",
				(double)this.field_1690.maxFps == GameOptions.Option.FRAMERATE_LIMIT.getMaximumValue() ? "inf" : this.field_1690.maxFps,
				this.field_1690.enableVsync ? " vsync" : "",
				this.field_1690.fancyGraphics ? "" : " fast",
				this.field_1690.cloudRenderMode == 0 ? "" : (this.field_1690.cloudRenderMode == 1 ? " fast-clouds" : " fancy-clouds"),
				GLX.useVbo() ? " vbo" : ""
			);
			ChunkRenderer.chunkUpdateCount = 0;
			this.field_1712 += 1000L;
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
		int i = this.window.method_4476(this.field_1690.guiScale, this.forcesUnicodeFont());
		this.window.method_15997((double)i);
		if (this.currentGui != null) {
			this.currentGui.onScaleChanged(this, this.window.getScaledWidth(), this.window.getScaledHeight());
		}

		GlFramebuffer glFramebuffer = this.getFramebuffer();
		if (glFramebuffer != null) {
			glFramebuffer.resize(this.window.getWindowWidth(), this.window.getWindowHeight(), isSystemMac);
		}

		if (this.field_1773 != null) {
			this.field_1773.method_3169(this.window.getWindowWidth(), this.window.getWindowHeight());
		}

		if (this.field_1729 != null) {
			this.field_1729.onResolutionChanged();
		}
	}

	private int getFramerateLimit() {
		return this.world == null && this.currentGui != null ? 60 : this.window.getFramerateLimit();
	}

	private boolean isFramerateLimited() {
		return (double)this.getFramerateLimit() < GameOptions.Option.FRAMERATE_LIMIT.getMaximumValue();
	}

	public void cleanUpAfterCrash() {
		try {
			memoryReservedForCrash = new byte[0];
			this.field_1769.method_3267();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			if (this.method_1496()) {
				this.server.stop(true);
			}

			this.method_1550(null, new CloseWorldGui(I18n.translate("menu.savingLevel")));
		} catch (Throwable var2) {
		}

		System.gc();
	}

	void handleProfilerKeyPress(int i) {
		ProfileResult profileResult = this.profiler.getController().method_16059();
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
		if (this.profiler.getController().method_16057()) {
			ProfileResult profileResult = this.profiler.getController().method_16059();
			List<ProfilerTiming> list = profileResult.getTimings(this.openProfilerSection);
			ProfilerTiming profilerTiming = (ProfilerTiming)list.remove(0);
			GlStateManager.clear(256, isSystemMac);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)this.window.getWindowWidth(), (double)this.window.getWindowHeight(), 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
			GlStateManager.lineWidth(1.0F);
			GlStateManager.disableTexture();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int i = 160;
			int j = this.window.getWindowWidth() - 160 - 10;
			int k = this.window.getWindowHeight() - 320;
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
			this.fontRenderer.drawWithShadow(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			this.fontRenderer.drawWithShadow(string, (float)(j + 160 - this.fontRenderer.getStringWidth(string)), (float)(k - 80 - 16), 16777215);

			for (int s = 0; s < list.size(); s++) {
				ProfilerTiming profilerTiming3 = (ProfilerTiming)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string2 = stringBuilder.append(profilerTiming3.name).toString();
				this.fontRenderer.drawWithShadow(string2, (float)(j - 160), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
				this.fontRenderer
					.drawWithShadow(string2, (float)(j + 160 - 50 - this.fontRenderer.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
				string2 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
				this.fontRenderer
					.drawWithShadow(string2, (float)(j + 160 - this.fontRenderer.getStringWidth(string2)), (float)(k + 80 + s * 8 + 20), profilerTiming3.getColor());
			}
		}
	}

	public void stopThread() {
		this.isRunning = false;
	}

	public void openInGameMenu() {
		if (this.currentGui == null) {
			this.openGui(new PauseMenuGui());
			if (this.method_1496() && !this.server.isRemote()) {
				this.soundLoader.pause();
			}
		}
	}

	private void method_1590(boolean bl) {
		if (!bl) {
			this.attackCooldown = 0;
		}

		if (this.attackCooldown <= 0 && !this.player.method_6115()) {
			if (bl && this.hitResult != null && this.hitResult.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = this.hitResult.getBlockPos();
				if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.method_2902(blockPos, this.hitResult.side)) {
					this.particleManager.addBlockBreakingParticles(blockPos, this.hitResult.side);
					this.player.swingHand(Hand.MAIN);
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
				switch (this.hitResult.type) {
					case ENTITY:
						this.interactionManager.attackEntity(this.player, this.hitResult.entity);
						break;
					case BLOCK:
						BlockPos blockPos = this.hitResult.getBlockPos();
						if (!this.world.getBlockState(blockPos).isAir()) {
							this.interactionManager.attackBlock(blockPos, this.hitResult.side);
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
						switch (this.hitResult.type) {
							case ENTITY:
								if (this.interactionManager.interactEntityAtLocation(this.player, this.hitResult.entity, this.hitResult, hand) == ActionResult.SUCCESS) {
									return;
								}

								if (this.interactionManager.interactEntity(this.player, this.hitResult.entity, hand) == ActionResult.SUCCESS) {
									return;
								}
								break;
							case BLOCK:
								BlockPos blockPos = this.hitResult.getBlockPos();
								if (!this.world.getBlockState(blockPos).isAir()) {
									int i = itemStack.getAmount();
									ActionResult actionResult = this.interactionManager.interactBlock(this.player, this.world, blockPos, this.hitResult.side, this.hitResult.pos, hand);
									if (actionResult == ActionResult.SUCCESS) {
										this.player.swingHand(hand);
										if (!itemStack.isEmpty() && (itemStack.getAmount() != i || this.interactionManager.hasCreativeInventory())) {
											this.field_1773.firstPersonRenderer.resetEquipProgress(hand);
										}

										return;
									}

									if (actionResult == ActionResult.FAILURE) {
										return;
									}
								}
						}
					}

					if (!itemStack.isEmpty() && this.interactionManager.interactItem(this.player, this.world, hand) == ActionResult.SUCCESS) {
						this.field_1773.firstPersonRenderer.resetEquipProgress(hand);
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
		if (!this.field_1734) {
			this.hudInGame.tick();
		}

		this.profiler.pop();
		this.field_1773.method_3190(1.0F);
		this.tutorialManager.method_4911(this.world, this.hitResult);
		this.profiler.push("gameMode");
		if (!this.field_1734 && this.world != null) {
			this.interactionManager.tick();
		}

		this.profiler.swap("textures");
		if (this.world != null) {
			this.textureManager.tick();
		}

		if (this.currentGui == null && this.player != null) {
			if (this.player.getHealth() <= 0.0F && !(this.currentGui instanceof DeathGui)) {
				this.openGui(null);
			} else if (this.player.isSleeping() && this.world != null) {
				this.openGui(new ChatSleepingGui());
			}
		} else if (this.currentGui != null && this.currentGui instanceof ChatSleepingGui && !this.player.isSleeping()) {
			this.openGui(null);
		}

		if (this.currentGui != null) {
			this.attackCooldown = 10000;
		}

		if (this.currentGui != null) {
			Gui.method_2217(() -> this.currentGui.update(), "Ticking screen", this.currentGui.getClass().getCanonicalName());
		}

		if (!this.field_1690.debugEnabled) {
			this.hudInGame.method_1745();
		}

		if (this.currentGui == null || this.currentGui.field_2558) {
			this.profiler.swap("GLFW events");
			GLX.pollEvents();
			this.method_1508();
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
			}
		}

		if (this.world != null) {
			if (this.player != null) {
				this.field_1736++;
				if (this.field_1736 == 30) {
					this.field_1736 = 0;
					this.world.method_8443(this.player);
				}
			}

			this.profiler.swap("gameRenderer");
			if (!this.field_1734) {
				this.field_1773.tick();
			}

			this.profiler.swap("levelRenderer");
			if (!this.field_1734) {
				this.field_1769.tick();
			}

			this.profiler.swap("level");
			if (!this.field_1734) {
				if (this.world.getTicksSinceLightningClient() > 0) {
					this.world.setTicksSinceLightningClient(this.world.getTicksSinceLightningClient() - 1);
				}

				this.world.updateEntities();
			}
		} else if (this.field_1773.method_3175()) {
			this.field_1773.method_3207();
		}

		if (!this.field_1734) {
			this.musicTracker.tick();
			this.soundLoader.tick();
		}

		if (this.world != null) {
			if (!this.field_1734) {
				this.world.setMobSpawnOptions(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
				this.tutorialManager.tick();

				try {
					this.world.tick(() -> true);
				} catch (Throwable var4) {
					CrashReport crashReport = CrashReport.create(var4, "Exception in world tick");
					if (this.world == null) {
						CrashReportSection crashReportSection = crashReport.method_562("Affected level");
						crashReportSection.add("Problem", "Level is null!");
					} else {
						this.world.method_8538(crashReport);
					}

					throw new CrashException(crashReport);
				}
			}

			this.profiler.swap("animateTick");
			if (!this.field_1734 && this.world != null) {
				this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
			}

			this.profiler.swap("particles");
			if (!this.field_1734) {
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

	private void method_1508() {
		while (this.field_1690.keyTogglePerspective.method_1436()) {
			this.field_1690.field_1850++;
			if (this.field_1690.field_1850 > 2) {
				this.field_1690.field_1850 = 0;
			}

			if (this.field_1690.field_1850 == 0) {
				this.field_1773.onSetCameraEntity(this.getCameraEntity());
			} else if (this.field_1690.field_1850 == 1) {
				this.field_1773.onSetCameraEntity(null);
			}

			this.field_1769.method_3292();
		}

		while (this.field_1690.keySmoothCamera.method_1436()) {
			this.field_1690.smoothCameraEnabled = !this.field_1690.smoothCameraEnabled;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.field_1690.keySaveToolbarActivator.method_1434();
			boolean bl2 = this.field_1690.keyLoadToolbarActivator.method_1434();
			if (this.field_1690.keysHotbar[i].method_1436()) {
				if (this.player.isSpectator()) {
					this.hudInGame.getSpectatorWidget().method_1977(i);
				} else if (!this.player.isCreative() || this.currentGui != null || !bl2 && !bl) {
					this.player.inventory.selectedSlot = i;
				} else {
					CreativePlayerInventoryGui.method_2462(this, i, bl2, bl);
				}
			}
		}

		while (this.field_1690.keyInventory.method_1436()) {
			if (this.interactionManager.hasRidingInventory()) {
				this.player.method_3132();
			} else {
				this.tutorialManager.onInventoryOpened();
				this.openGui(new PlayerInventoryGui(this.player));
			}
		}

		while (this.field_1690.keyAdvancements.method_1436()) {
			this.openGui(new AdvancementsGui(this.player.networkHandler.getAdvancementHandler()));
		}

		while (this.field_1690.keySwapHands.method_1436()) {
			if (!this.player.isSpectator()) {
				this.getNetworkHandler().sendPacket(new PlayerActionServerPacket(PlayerActionServerPacket.Action.field_12969, BlockPos.ORIGIN, Direction.DOWN));
			}
		}

		while (this.field_1690.keyDrop.method_1436()) {
			if (!this.player.isSpectator()) {
				this.player.dropSelectedItem(Gui.isControlPressed());
			}
		}

		boolean bl3 = this.field_1690.chatVisibility != PlayerEntity.ChatVisibility.HIDDEN;
		if (bl3) {
			while (this.field_1690.keyChat.method_1436()) {
				this.openGui(new ChatGui());
			}

			if (this.currentGui == null && this.field_1690.keyCommand.method_1436()) {
				this.openGui(new ChatGui("/"));
			}
		}

		if (this.player.method_6115()) {
			if (!this.field_1690.keyUse.method_1434()) {
				this.interactionManager.method_2897(this.player);
			}

			while (this.field_1690.keyAttack.method_1436()) {
			}

			while (this.field_1690.keyUse.method_1436()) {
			}

			while (this.field_1690.keyPickItem.method_1436()) {
			}
		} else {
			while (this.field_1690.keyAttack.method_1436()) {
				this.doAttack();
			}

			while (this.field_1690.keyUse.method_1436()) {
				this.doItemUse();
			}

			while (this.field_1690.keyPickItem.method_1436()) {
				this.doItemPick();
			}
		}

		if (this.field_1690.keyUse.method_1434() && this.itemUseCooldown == 0 && !this.player.method_6115()) {
			this.doItemUse();
		}

		this.method_1590(this.currentGui == null && this.field_1690.keyAttack.method_1434() && this.field_1729.isCursorLocked());
	}

	public void startIntegratedServer(String string, String string2, @Nullable LevelInfo levelInfo) {
		this.method_1481(null);
		System.gc();
		WorldSaveHandler worldSaveHandler = this.levelStorage.method_242(string, null);
		LevelProperties levelProperties = worldSaveHandler.readProperties();
		if (levelProperties == null && levelInfo != null) {
			levelProperties = new LevelProperties(levelInfo, string);
			worldSaveHandler.saveWorld(levelProperties);
		}

		if (levelInfo == null) {
			levelInfo = new LevelInfo(levelProperties);
		}

		try {
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
			SkullBlockEntity.setUserCache(userCache);
			SkullBlockEntity.setSessionService(minecraftSessionService);
			UserCache.setUseRemote(false);
			this.server = new IntegratedServer(
				this, string, string2, levelInfo, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
			);
			this.server.start();
			this.isIntegratedServerRunning = true;
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Starting integrated server");
			CrashReportSection crashReportSection = crashReport.method_562("Starting integrated server");
			crashReportSection.add("Level ID", string);
			crashReportSection.add("Level Name", string2);
			throw new CrashException(crashReport);
		}

		WorkingGui workingGui = new WorkingGui();
		this.openGui(workingGui);
		workingGui.method_15412(new TranslatableTextComponent("menu.loadingLevel"));

		while (!this.server.method_3820()) {
			TextComponent textComponent = this.server.method_3810();
			if (textComponent != null) {
				TextComponent textComponent2 = this.server.method_3863();
				if (textComponent2 != null) {
					workingGui.method_15414(textComponent2);
				} else {
					workingGui.method_15414(textComponent);
				}
			} else {
				workingGui.method_15414(new StringTextComponent(""));
			}

			this.render(false);

			try {
				Thread.sleep(200L);
			} catch (InterruptedException var10) {
			}

			if (this.crashed && this.crashReport != null) {
				this.printCrashReport(this.crashReport);
				return;
			}
		}

		SocketAddress socketAddress = this.server.getNetworkIO().method_14353();
		ClientConnection clientConnection = ClientConnection.connect(socketAddress);
		clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, this, null, textComponentx -> {
		}));
		clientConnection.sendPacket(new HandshakeServerPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
		clientConnection.sendPacket(new LoginHelloServerPacket(this.getSession().getProfile()));
		this.clientConnection = clientConnection;
	}

	public void method_1481(@Nullable ClientWorld clientWorld) {
		WorkingGui workingGui = new WorkingGui();
		if (clientWorld != null) {
			workingGui.method_15412(new TranslatableTextComponent("connect.joining"));
		}

		this.method_1550(clientWorld, workingGui);
	}

	public void method_1550(@Nullable ClientWorld clientWorld, Gui gui) {
		IntegratedServer integratedServer = this.server;
		if (clientWorld == null) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				this.taskQueue.clear();
				clientPlayNetworkHandler.method_2868();
			}

			this.server = null;
			this.field_1773.method_3203();
			this.interactionManager = null;
			NarratorManager.INSTANCE.clear();
		}

		this.musicTracker.method_4859();
		this.soundLoader.stopAll();
		this.cameraEntity = null;
		this.clientConnection = null;
		this.openGui(gui);
		this.render(false);
		if (clientWorld == null && this.world != null) {
			if (integratedServer != null) {
				while (!integratedServer.isServerThreadAlive()) {
					this.render(false);
				}
			}

			this.resourcePackCreator.clear();
			this.hudInGame.clear();
			this.method_1584(null);
			this.isIntegratedServerRunning = false;
			this.game.onLeaveGameSession();
		}

		this.world = clientWorld;
		if (this.field_1769 != null) {
			this.field_1769.setWorld(clientWorld);
		}

		if (this.particleManager != null) {
			this.particleManager.setWorld(clientWorld);
		}

		BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
		if (clientWorld != null) {
			if (!this.isIntegratedServerRunning) {
				AuthenticationService authenticationService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
				MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
				GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
				UserCache userCache = new UserCache(gameProfileRepository, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
				SkullBlockEntity.setUserCache(userCache);
				SkullBlockEntity.setSessionService(minecraftSessionService);
				UserCache.setUseRemote(false);
			}

			if (this.player == null) {
				this.player = this.interactionManager.createPlayer(clientWorld, new StatHandler(), new ClientRecipeBook(clientWorld.getRecipeManager()));
				this.interactionManager.method_2898(this.player);
				if (this.server != null) {
					this.server.method_4817(this.player.getUuid());
				}
			}

			this.player.method_5823();
			clientWorld.spawnEntity(this.player);
			this.player.input = new KeyboardInput(this.field_1690);
			this.interactionManager.copyAbilities(this.player);
			this.cameraEntity = this.player;
		} else {
			this.player = null;
		}

		System.gc();
	}

	public void method_1585(DimensionType dimensionType) {
		this.world.setDefaultSpawnClient();
		this.world.method_2936();
		int i = 0;
		String string = null;
		if (this.player != null) {
			i = this.player.getEntityId();
			this.world.removeEntity(this.player);
			string = this.player.method_3135();
		}

		this.cameraEntity = null;
		ClientPlayerEntity clientPlayerEntity = this.player;
		this.player = this.interactionManager
			.createPlayer(
				this.world,
				this.player == null ? new StatHandler() : this.player.getStats(),
				this.player == null ? new ClientRecipeBook(new RecipeManager()) : this.player.getRecipeBook()
			);
		this.player.getDataTracker().method_12779(clientPlayerEntity.getDataTracker().getAllEntries());
		this.player.dimension = dimensionType;
		this.cameraEntity = this.player;
		this.player.method_5823();
		this.player.setServerBrand(string);
		this.world.spawnEntity(this.player);
		this.interactionManager.method_2898(this.player);
		this.player.input = new KeyboardInput(this.field_1690);
		this.player.setEntityId(i);
		this.interactionManager.copyAbilities(this.player);
		this.player.setReducedDebugInfo(clientPlayerEntity.getReducedDebugInfo());
		if (this.currentGui instanceof DeathGui) {
			this.openGui(null);
		}
	}

	public final boolean isDemo() {
		return this.isDemo;
	}

	@Nullable
	public ClientPlayNetworkHandler getNetworkHandler() {
		return this.player == null ? null : this.player.networkHandler;
	}

	public static boolean method_1498() {
		return instance == null || !instance.field_1690.field_1842;
	}

	public static boolean isFancyGraphicsEnabled() {
		return instance != null && instance.field_1690.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return instance != null && instance.field_1690.ao != 0;
	}

	private void doItemPick() {
		if (this.hitResult != null && this.hitResult.type != HitResult.Type.NONE) {
			boolean bl = this.player.abilities.creativeMode;
			BlockEntity blockEntity = null;
			ItemStack itemStack;
			if (this.hitResult.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = this.hitResult.getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (blockState.isAir()) {
					return;
				}

				itemStack = block.getPickStack(this.world, blockPos, blockState);
				if (itemStack.isEmpty()) {
					return;
				}

				if (bl && Gui.isControlPressed() && block.hasBlockEntity()) {
					blockEntity = this.world.getBlockEntity(blockPos);
				}
			} else {
				if (this.hitResult.type != HitResult.Type.ENTITY || this.hitResult.entity == null || !bl) {
					return;
				}

				if (this.hitResult.entity instanceof PaintingEntity) {
					itemStack = new ItemStack(Items.field_8892);
				} else if (this.hitResult.entity instanceof LeadKnotEntity) {
					itemStack = new ItemStack(Items.field_8719);
				} else if (this.hitResult.entity instanceof ItemFrameEntity) {
					ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.hitResult.entity;
					ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
					if (itemStack2.isEmpty()) {
						itemStack = new ItemStack(Items.field_8143);
					} else {
						itemStack = itemStack2.copy();
					}
				} else if (this.hitResult.entity instanceof AbstractMinecartEntity) {
					AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)this.hitResult.entity;
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
				} else if (this.hitResult.entity instanceof BoatEntity) {
					itemStack = new ItemStack(((BoatEntity)this.hitResult.entity).asItem());
				} else if (this.hitResult.entity instanceof ArmorStandEntity) {
					itemStack = new ItemStack(Items.field_8694);
				} else if (this.hitResult.entity instanceof EnderCrystalEntity) {
					itemStack = new ItemStack(Items.field_8301);
				} else {
					SpawnEggItem spawnEggItem = SpawnEggItem.method_8019(this.hitResult.entity.getType());
					if (spawnEggItem == null) {
						return;
					}

					itemStack = new ItemStack(spawnEggItem);
				}
			}

			if (itemStack.isEmpty()) {
				String string = "";
				if (this.hitResult.type == HitResult.Type.BLOCK) {
					string = Registry.BLOCK.getId(this.world.getBlockState(this.hitResult.getBlockPos()).getBlock()).toString();
				} else if (this.hitResult.type == HitResult.Type.ENTITY) {
					string = Registry.ENTITY_TYPE.getId(this.hitResult.entity.getType()).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", this.hitResult.type, string);
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
			listTag.add((Tag)(new StringTag("\"(+NBT)\"")));
			compoundTag2.put("Lore", listTag);
			itemStack.setChildTag("display", compoundTag2);
			return itemStack;
		}
	}

	public CrashReport populateCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = crashReport.method_567();
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
		crashReportSection.add("Current Language", (ICrashCallable<String>)(() -> this.languageManager.getLanguage().toString()));
		crashReportSection.add("CPU", GLX::getCpuInfo);
		if (this.world != null) {
			this.world.method_8538(crashReport);
		}

		return crashReport;
	}

	public static MinecraftClient getInstance() {
		return instance;
	}

	public CompletableFuture<Object> reloadResourcesConcurrently() {
		return this.executeFuture(this::reloadResources);
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

	public boolean isIntegratedServerRunning() {
		return this.isIntegratedServerRunning;
	}

	public boolean method_1496() {
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

	public File method_1479() {
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

	public boolean method_1493() {
		return this.field_1734;
	}

	public SoundLoader getSoundLoader() {
		return this.soundLoader;
	}

	public MusicTracker.MusicType getMusicType() {
		if (this.currentGui instanceof EndCreditsGui) {
			return MusicTracker.MusicType.CREDITS;
		} else if (this.player == null) {
			return MusicTracker.MusicType.MENU;
		} else if (this.player.world.dimension instanceof TheNetherDimension) {
			return MusicTracker.MusicType.NETHER;
		} else if (this.player.world.dimension instanceof TheEndDimension) {
			return this.hudInGame.getHudBossBar().shouldPlayDragonMusic() ? MusicTracker.MusicType.DRAGON : MusicTracker.MusicType.END;
		} else {
			Biome.Category category = this.player.world.getBiome(new BlockPos(this.player.x, this.player.y, this.player.z)).getCategory();
			if (!this.musicTracker.method_4860(MusicTracker.MusicType.field_5576)
				&& (
					!this.player.method_5869()
						|| this.musicTracker.method_4860(MusicTracker.MusicType.GAME)
						|| category != Biome.Category.OCEAN && category != Biome.Category.RIVER
				)) {
				return this.player.abilities.creativeMode && this.player.abilities.allowFlying ? MusicTracker.MusicType.CREATIVE : MusicTracker.MusicType.GAME;
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
		this.field_1773.onSetCameraEntity(entity);
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

	public float method_1534() {
		return this.renderTickCounter.field_1969;
	}

	public BlockColorMap getBlockColorMap() {
		return this.blockColorMap;
	}

	public boolean hasReducedDebugInfo() {
		return this.player != null && this.player.getReducedDebugInfo() || this.field_1690.reducedDebugInfo;
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

	public FontRendererManager getFontManager() {
		return this.fontManager;
	}

	@Override
	public void setWindowFocused(boolean bl) {
		this.isWindowFocused = bl;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public MinecraftClientGame getGame() {
		return this.game;
	}
}
