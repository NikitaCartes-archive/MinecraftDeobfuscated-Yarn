package net.minecraft.client.option;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GameOptions {
	static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final TypeToken<List<String>> STRING_LIST_TYPE = new TypeToken<List<String>>() {
	};
	public static final int field_32149 = 2;
	public static final int field_32150 = 4;
	public static final int field_32152 = 8;
	public static final int field_32153 = 12;
	public static final int field_32154 = 16;
	public static final int field_32155 = 32;
	private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
	private static final float field_32151 = 1.0F;
	public static final String field_34785 = "";
	public boolean monochromeLogo;
	public boolean hideLightningFlashes;
	public double mouseSensitivity = 0.5;
	public int viewDistance;
	public int simulationDistance;
	private int serverViewDistance = 0;
	public float entityDistanceScaling = 1.0F;
	public int maxFps = 120;
	public CloudRenderMode cloudRenderMode = CloudRenderMode.FANCY;
	public GraphicsMode graphicsMode = GraphicsMode.FANCY;
	public AoMode ao = AoMode.MAX;
	public ChunkBuilderMode chunkBuilderMode = ChunkBuilderMode.NONE;
	public List<String> resourcePacks = Lists.<String>newArrayList();
	public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
	public ChatVisibility chatVisibility = ChatVisibility.FULL;
	public double chatOpacity = 1.0;
	public double chatLineSpacing;
	public double textBackgroundOpacity = 0.5;
	@Nullable
	public String fullscreenResolution;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> enabledPlayerModelParts = EnumSet.allOf(PlayerModelPart.class);
	public Arm mainArm = Arm.RIGHT;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public double chatScale = 1.0;
	public double chatWidth = 1.0;
	public double chatHeightUnfocused = 0.44366196F;
	public double chatHeightFocused = 1.0;
	public double chatDelay;
	public int mipmapLevels = 4;
	private final Object2FloatMap<SoundCategory> soundVolumeLevels = Util.make(
		new Object2FloatOpenHashMap<>(), object2FloatOpenHashMap -> object2FloatOpenHashMap.defaultReturnValue(1.0F)
	);
	public boolean useNativeTransport = true;
	public AttackIndicator attackIndicator = AttackIndicator.CROSSHAIR;
	public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
	public boolean joinedFirstServer = false;
	public boolean hideBundleTutorial = false;
	public int biomeBlendRadius = 2;
	public double mouseWheelSensitivity = 1.0;
	public boolean rawMouseInput = true;
	public int glDebugVerbosity = 1;
	public boolean autoJump = true;
	public boolean autoSuggestions = true;
	public boolean chatColors = true;
	public boolean chatLinks = true;
	public boolean chatLinksPrompt = true;
	public boolean enableVsync = true;
	public boolean entityShadows = true;
	public boolean forceUnicodeFont;
	public boolean invertYMouse;
	public boolean discreteMouseScroll;
	public boolean realmsNotifications = true;
	public boolean allowServerListing = true;
	public boolean reducedDebugInfo;
	public boolean showSubtitles;
	public boolean backgroundForChatOnly = true;
	public boolean touchscreen;
	public boolean fullscreen;
	public boolean bobView = true;
	public boolean sneakToggled;
	public boolean sprintToggled;
	public boolean skipMultiplayerWarning;
	public boolean hideMatchedNames = true;
	public boolean showAutosaveIndicator = true;
	/**
	 * A key binding for moving forward.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_W the W key} by default.
	 */
	public final KeyBinding keyForward = new KeyBinding("key.forward", GLFW.GLFW_KEY_W, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving left.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_A the A key} by default.
	 */
	public final KeyBinding keyLeft = new KeyBinding("key.left", GLFW.GLFW_KEY_A, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving backward.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_S the S key} by default.
	 */
	public final KeyBinding keyBack = new KeyBinding("key.back", GLFW.GLFW_KEY_S, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving right.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_D the D key} by default.
	 */
	public final KeyBinding keyRight = new KeyBinding("key.right", GLFW.GLFW_KEY_D, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for jumping.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_SPACE the space key} by default.
	 */
	public final KeyBinding keyJump = new KeyBinding("key.jump", GLFW.GLFW_KEY_SPACE, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for sneaking.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_LEFT_SHIFT the left shift key} by default.
	 */
	public final KeyBinding keySneak = new StickyKeyBinding("key.sneak", GLFW.GLFW_KEY_LEFT_SHIFT, KeyBinding.MOVEMENT_CATEGORY, () -> this.sneakToggled);
	/**
	 * A key binding for sprinting.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_LEFT_CONTROL the left control key} by default.
	 */
	public final KeyBinding keySprint = new StickyKeyBinding("key.sprint", GLFW.GLFW_KEY_LEFT_CONTROL, KeyBinding.MOVEMENT_CATEGORY, () -> this.sprintToggled);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ingame.InventoryScreen the inventory screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_E the E key} by default.
	 */
	public final KeyBinding keyInventory = new KeyBinding("key.inventory", GLFW.GLFW_KEY_E, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for swapping the items in the selected slot and the off hand.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F the F key} by default.
	 * 
	 * <p>The selected slot is the slot the mouse is over when in a screen.
	 * Otherwise, it is the main hand.
	 */
	public final KeyBinding keySwapHands = new KeyBinding("key.swapOffhand", GLFW.GLFW_KEY_F, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for dropping the item in the selected slot.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_Q the Q key} by default.
	 * 
	 * <p>The selected slot is the slot the mouse is over when in a screen.
	 * Otherwise, it is the main hand.
	 */
	public final KeyBinding keyDrop = new KeyBinding("key.drop", GLFW.GLFW_KEY_Q, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for using an item, such as placing a block.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_RIGHT the right mouse button} by default.
	 */
	public final KeyBinding keyUse = new KeyBinding("key.use", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for attacking an entity or breaking a block.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_LEFT the left mouse button} by default.
	 */
	public final KeyBinding keyAttack = new KeyBinding("key.attack", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for holding an item corresponding to the {@linkplain net.minecraft.entity.Entity#getPickBlockStack() entity}
	 * or {@linkplain net.minecraft.block.Block#getPickStack(net.minecraft.world.BlockView,
	 * net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) block} the player is looking at.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_MIDDLE the middle mouse button} by default.
	 */
	public final KeyBinding keyPickItem = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ChatScreen the chat screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_T the T key} by default.
	 */
	public final KeyBinding keyChat = new KeyBinding("key.chat", GLFW.GLFW_KEY_T, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for displaying {@linkplain net.minecraft.client.gui.hud.PlayerListHud the player list}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_TAB the tab key} by default.
	 */
	public final KeyBinding keyPlayerList = new KeyBinding("key.playerlist", GLFW.GLFW_KEY_TAB, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ChatScreen
	 * the chat screen} with the {@code /} already typed.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_SLASH the slash key} by default.
	 */
	public final KeyBinding keyCommand = new KeyBinding("key.command", GLFW.GLFW_KEY_SLASH, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen the social interactions screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_P the P key} by default.
	 */
	public final KeyBinding keySocialInteractions = new KeyBinding("key.socialInteractions", GLFW.GLFW_KEY_P, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for taking a screenshot.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F2 the F2 key} by default.
	 */
	public final KeyBinding keyScreenshot = new KeyBinding("key.screenshot", GLFW.GLFW_KEY_F2, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling perspective.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F5 the F5 key} by default.
	 */
	public final KeyBinding keyTogglePerspective = new KeyBinding("key.togglePerspective", GLFW.GLFW_KEY_F5, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling smooth camera.
	 * Not bound to any keys by default.
	 */
	public final KeyBinding keySmoothCamera = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling fullscreen.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F11 the F11 key} by default.
	 */
	public final KeyBinding keyFullscreen = new KeyBinding("key.fullscreen", GLFW.GLFW_KEY_F11, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for highlighting players in {@linkplain net.minecraft.world.GameMode#SPECTATOR spectator mode}.
	 * Not bound to any keys by default.
	 */
	public final KeyBinding keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.advancement.AdvancementsScreen the advancements screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_L the L key} by default.
	 */
	public final KeyBinding keyAdvancements = new KeyBinding("key.advancements", GLFW.GLFW_KEY_L, KeyBinding.MISC_CATEGORY);
	/**
	 * Key bindings for selecting hotbar slots.
	 * Bound to the corresponding number keys (from {@linkplain
	 * org.lwjgl.glfw.GLFW#GLFW_KEY_1 the 1 key} to {@linkplain
	 * org.lwjgl.glfw.GLFW#GLFW_KEY_9 the 9 key}) by default.
	 */
	public final KeyBinding[] keysHotbar = new KeyBinding[]{
		new KeyBinding("key.hotbar.1", GLFW.GLFW_KEY_1, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.2", GLFW.GLFW_KEY_2, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.3", GLFW.GLFW_KEY_3, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.4", GLFW.GLFW_KEY_4, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.5", GLFW.GLFW_KEY_5, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.6", GLFW.GLFW_KEY_6, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.7", GLFW.GLFW_KEY_7, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.8", GLFW.GLFW_KEY_8, KeyBinding.INVENTORY_CATEGORY),
		new KeyBinding("key.hotbar.9", GLFW.GLFW_KEY_9, KeyBinding.INVENTORY_CATEGORY)
	};
	/**
	 * A key binding for saving the hotbar items in {@linkplain net.minecraft.world.GameMode#CREATIVE creative mode}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_C the C key} by default.
	 */
	public final KeyBinding keySaveToolbarActivator = new KeyBinding("key.saveToolbarActivator", GLFW.GLFW_KEY_C, KeyBinding.CREATIVE_CATEGORY);
	/**
	 * A key binding for loading the hotbar items in {@linkplain net.minecraft.world.GameMode#CREATIVE creative mode}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_X the X key} by default.
	 */
	public final KeyBinding keyLoadToolbarActivator = new KeyBinding("key.loadToolbarActivator", GLFW.GLFW_KEY_X, KeyBinding.CREATIVE_CATEGORY);
	/**
	 * An array of all key bindings.
	 * 
	 * <p>Key bindings in this array are shown and can be configured in
	 * {@linkplain net.minecraft.client.gui.screen.option.ControlsOptionsScreen
	 * the controls options screen}.
	 */
	public final KeyBinding[] keysAll = ArrayUtils.addAll(
		(KeyBinding[])(new KeyBinding[]{
			this.keyAttack,
			this.keyUse,
			this.keyForward,
			this.keyLeft,
			this.keyBack,
			this.keyRight,
			this.keyJump,
			this.keySneak,
			this.keySprint,
			this.keyDrop,
			this.keyInventory,
			this.keyChat,
			this.keyPlayerList,
			this.keyPickItem,
			this.keyCommand,
			this.keySocialInteractions,
			this.keyScreenshot,
			this.keyTogglePerspective,
			this.keySmoothCamera,
			this.keyFullscreen,
			this.keySpectatorOutlines,
			this.keySwapHands,
			this.keySaveToolbarActivator,
			this.keyLoadToolbarActivator,
			this.keyAdvancements
		}),
		(KeyBinding[])this.keysHotbar
	);
	protected MinecraftClient client;
	private final File optionsFile;
	public Difficulty difficulty = Difficulty.NORMAL;
	public boolean hudHidden;
	private Perspective perspective = Perspective.FIRST_PERSON;
	public boolean debugEnabled;
	public boolean debugProfilerEnabled;
	public boolean debugTpsEnabled;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public double fov = 70.0;
	public float distortionEffectScale = 1.0F;
	public float fovEffectScale = 1.0F;
	public float darknessEffectScale = 1.0F;
	public double gamma;
	public int guiScale;
	public ParticlesMode particles = ParticlesMode.ALL;
	public NarratorMode narrator = NarratorMode.OFF;
	public String language = "en_us";
	public String soundDevice = "";
	public boolean syncChunkWrites;

	public GameOptions(MinecraftClient client, File optionsFile) {
		this.client = client;
		this.optionsFile = new File(optionsFile, "options.txt");
		if (client.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			Option.RENDER_DISTANCE.setMax(32.0F);
			Option.SIMULATION_DISTANCE.setMax(32.0F);
		} else {
			Option.RENDER_DISTANCE.setMax(16.0F);
			Option.SIMULATION_DISTANCE.setMax(16.0F);
		}

		this.viewDistance = client.is64Bit() ? 12 : 8;
		this.simulationDistance = client.is64Bit() ? 12 : 8;
		this.gamma = 0.5;
		this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
		this.load();
	}

	public float getTextBackgroundOpacity(float fallback) {
		return this.backgroundForChatOnly ? fallback : (float)this.textBackgroundOpacity;
	}

	public int getTextBackgroundColor(float fallbackOpacity) {
		return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0F) << 24 & 0xFF000000;
	}

	public int getTextBackgroundColor(int fallbackColor) {
		return this.backgroundForChatOnly ? fallbackColor : (int)(this.textBackgroundOpacity * 255.0) << 24 & 0xFF000000;
	}

	public void setKeyCode(KeyBinding key, InputUtil.Key code) {
		key.setBoundKey(code);
		this.write();
	}

	private void accept(GameOptions.Visitor visitor) {
		this.autoJump = visitor.visitBoolean("autoJump", this.autoJump);
		this.autoSuggestions = visitor.visitBoolean("autoSuggestions", this.autoSuggestions);
		this.chatColors = visitor.visitBoolean("chatColors", this.chatColors);
		this.chatLinks = visitor.visitBoolean("chatLinks", this.chatLinks);
		this.chatLinksPrompt = visitor.visitBoolean("chatLinksPrompt", this.chatLinksPrompt);
		this.enableVsync = visitor.visitBoolean("enableVsync", this.enableVsync);
		this.entityShadows = visitor.visitBoolean("entityShadows", this.entityShadows);
		this.forceUnicodeFont = visitor.visitBoolean("forceUnicodeFont", this.forceUnicodeFont);
		this.discreteMouseScroll = visitor.visitBoolean("discrete_mouse_scroll", this.discreteMouseScroll);
		this.invertYMouse = visitor.visitBoolean("invertYMouse", this.invertYMouse);
		this.realmsNotifications = visitor.visitBoolean("realmsNotifications", this.realmsNotifications);
		this.reducedDebugInfo = visitor.visitBoolean("reducedDebugInfo", this.reducedDebugInfo);
		this.showSubtitles = visitor.visitBoolean("showSubtitles", this.showSubtitles);
		this.touchscreen = visitor.visitBoolean("touchscreen", this.touchscreen);
		this.fullscreen = visitor.visitBoolean("fullscreen", this.fullscreen);
		this.bobView = visitor.visitBoolean("bobView", this.bobView);
		this.sneakToggled = visitor.visitBoolean("toggleCrouch", this.sneakToggled);
		this.sprintToggled = visitor.visitBoolean("toggleSprint", this.sprintToggled);
		this.monochromeLogo = visitor.visitBoolean("darkMojangStudiosBackground", this.monochromeLogo);
		this.hideLightningFlashes = visitor.visitBoolean("hideLightningFlashes", this.hideLightningFlashes);
		this.mouseSensitivity = visitor.visitDouble("mouseSensitivity", this.mouseSensitivity);
		this.fov = visitor.visitDouble("fov", (this.fov - 70.0) / 40.0) * 40.0 + 70.0;
		this.distortionEffectScale = visitor.visitFloat("screenEffectScale", this.distortionEffectScale);
		this.fovEffectScale = visitor.visitFloat("fovEffectScale", this.fovEffectScale);
		this.gamma = visitor.visitDouble("gamma", this.gamma);
		this.viewDistance = (int)MathHelper.clamp(
			(double)visitor.visitInt("renderDistance", this.viewDistance), Option.RENDER_DISTANCE.getMin(), Option.RENDER_DISTANCE.getMax()
		);
		this.simulationDistance = (int)MathHelper.clamp(
			(double)visitor.visitInt("simulationDistance", this.simulationDistance), Option.SIMULATION_DISTANCE.getMin(), Option.SIMULATION_DISTANCE.getMax()
		);
		this.entityDistanceScaling = visitor.visitFloat("entityDistanceScaling", this.entityDistanceScaling);
		this.guiScale = visitor.visitInt("guiScale", this.guiScale);
		this.particles = visitor.visitObject("particles", this.particles, ParticlesMode::byId, ParticlesMode::getId);
		this.maxFps = visitor.visitInt("maxFps", this.maxFps);
		this.difficulty = visitor.visitObject("difficulty", this.difficulty, Difficulty::byOrdinal, Difficulty::getId);
		this.graphicsMode = visitor.visitObject("graphicsMode", this.graphicsMode, GraphicsMode::byId, GraphicsMode::getId);
		this.ao = visitor.visitObject("ao", this.ao, GameOptions::loadAo, ao -> Integer.toString(ao.getId()));
		this.chunkBuilderMode = visitor.visitObject("prioritizeChunkUpdates", this.chunkBuilderMode, ChunkBuilderMode::get, ChunkBuilderMode::getId);
		this.biomeBlendRadius = visitor.visitInt("biomeBlendRadius", this.biomeBlendRadius);
		this.cloudRenderMode = visitor.visitObject("renderClouds", this.cloudRenderMode, GameOptions::loadCloudRenderMode, GameOptions::saveCloudRenderMode);
		this.resourcePacks = visitor.visitObject("resourcePacks", this.resourcePacks, GameOptions::parseList, GSON::toJson);
		this.incompatibleResourcePacks = visitor.visitObject("incompatibleResourcePacks", this.incompatibleResourcePacks, GameOptions::parseList, GSON::toJson);
		this.lastServer = visitor.visitString("lastServer", this.lastServer);
		this.language = visitor.visitString("lang", this.language);
		this.soundDevice = visitor.visitString("soundDevice", this.soundDevice);
		this.chatVisibility = visitor.visitObject("chatVisibility", this.chatVisibility, ChatVisibility::byId, ChatVisibility::getId);
		this.chatOpacity = visitor.visitDouble("chatOpacity", this.chatOpacity);
		this.chatLineSpacing = visitor.visitDouble("chatLineSpacing", this.chatLineSpacing);
		this.textBackgroundOpacity = visitor.visitDouble("textBackgroundOpacity", this.textBackgroundOpacity);
		this.backgroundForChatOnly = visitor.visitBoolean("backgroundForChatOnly", this.backgroundForChatOnly);
		this.hideServerAddress = visitor.visitBoolean("hideServerAddress", this.hideServerAddress);
		this.advancedItemTooltips = visitor.visitBoolean("advancedItemTooltips", this.advancedItemTooltips);
		this.pauseOnLostFocus = visitor.visitBoolean("pauseOnLostFocus", this.pauseOnLostFocus);
		this.overrideWidth = visitor.visitInt("overrideWidth", this.overrideWidth);
		this.overrideHeight = visitor.visitInt("overrideHeight", this.overrideHeight);
		this.heldItemTooltips = visitor.visitBoolean("heldItemTooltips", this.heldItemTooltips);
		this.chatHeightFocused = visitor.visitDouble("chatHeightFocused", this.chatHeightFocused);
		this.chatDelay = visitor.visitDouble("chatDelay", this.chatDelay);
		this.chatHeightUnfocused = visitor.visitDouble("chatHeightUnfocused", this.chatHeightUnfocused);
		this.chatScale = visitor.visitDouble("chatScale", this.chatScale);
		this.chatWidth = visitor.visitDouble("chatWidth", this.chatWidth);
		this.mipmapLevels = visitor.visitInt("mipmapLevels", this.mipmapLevels);
		this.useNativeTransport = visitor.visitBoolean("useNativeTransport", this.useNativeTransport);
		this.mainArm = visitor.visitObject("mainHand", this.mainArm, GameOptions::loadArm, GameOptions::saveArm);
		this.attackIndicator = visitor.visitObject("attackIndicator", this.attackIndicator, AttackIndicator::byId, AttackIndicator::getId);
		this.narrator = visitor.visitObject("narrator", this.narrator, NarratorMode::byId, NarratorMode::getId);
		this.tutorialStep = visitor.visitObject("tutorialStep", this.tutorialStep, TutorialStep::byName, TutorialStep::getName);
		this.mouseWheelSensitivity = visitor.visitDouble("mouseWheelSensitivity", this.mouseWheelSensitivity);
		this.rawMouseInput = visitor.visitBoolean("rawMouseInput", this.rawMouseInput);
		this.glDebugVerbosity = visitor.visitInt("glDebugVerbosity", this.glDebugVerbosity);
		this.skipMultiplayerWarning = visitor.visitBoolean("skipMultiplayerWarning", this.skipMultiplayerWarning);
		this.hideMatchedNames = visitor.visitBoolean("hideMatchedNames", this.hideMatchedNames);
		this.joinedFirstServer = visitor.visitBoolean("joinedFirstServer", this.joinedFirstServer);
		this.hideBundleTutorial = visitor.visitBoolean("hideBundleTutorial", this.hideBundleTutorial);
		this.syncChunkWrites = visitor.visitBoolean("syncChunkWrites", this.syncChunkWrites);
		this.showAutosaveIndicator = visitor.visitBoolean("showAutosaveIndicator", this.showAutosaveIndicator);
		this.allowServerListing = visitor.visitBoolean("allowServerListing", this.allowServerListing);

		for (KeyBinding keyBinding : this.keysAll) {
			String string = keyBinding.getBoundKeyTranslationKey();
			String string2 = visitor.visitString("key_" + keyBinding.getTranslationKey(), string);
			if (!string.equals(string2)) {
				keyBinding.setBoundKey(InputUtil.fromTranslationKey(string2));
			}
		}

		for (SoundCategory soundCategory : SoundCategory.values()) {
			this.soundVolumeLevels
				.computeFloat(
					soundCategory, (category, currentLevel) -> visitor.visitFloat("soundCategory_" + category.getName(), currentLevel != null ? currentLevel : 1.0F)
				);
		}

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			boolean bl = this.enabledPlayerModelParts.contains(playerModelPart);
			boolean bl2 = visitor.visitBoolean("modelPart_" + playerModelPart.getName(), bl);
			if (bl2 != bl) {
				this.setPlayerModelPart(playerModelPart, bl2);
			}
		}
	}

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			this.soundVolumeLevels.clear();
			NbtCompound nbtCompound = new NbtCompound();
			BufferedReader bufferedReader = Files.newReader(this.optionsFile, Charsets.UTF_8);

			try {
				bufferedReader.lines().forEach(line -> {
					try {
						Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
						nbtCompound.putString((String)iterator.next(), (String)iterator.next());
					} catch (Exception var3) {
						LOGGER.warn("Skipping bad option: {}", line);
					}
				});
			} catch (Throwable var6) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			final NbtCompound nbtCompound2 = this.update(nbtCompound);
			if (!nbtCompound2.contains("graphicsMode") && nbtCompound2.contains("fancyGraphics")) {
				if (isTrue(nbtCompound2.getString("fancyGraphics"))) {
					this.graphicsMode = GraphicsMode.FANCY;
				} else {
					this.graphicsMode = GraphicsMode.FAST;
				}
			}

			this.accept(new GameOptions.Visitor() {
				@Nullable
				private String find(String key) {
					return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
				}

				@Override
				public int visitInt(String key, int current) {
					String string = this.find(key);
					if (string != null) {
						try {
							return Integer.parseInt(string);
						} catch (NumberFormatException var5) {
							GameOptions.LOGGER.warn("Invalid integer value for option {} = {}", key, string, var5);
						}
					}

					return current;
				}

				@Override
				public boolean visitBoolean(String key, boolean current) {
					String string = this.find(key);
					return string != null ? GameOptions.isTrue(string) : current;
				}

				@Override
				public String visitString(String key, String current) {
					return MoreObjects.firstNonNull(this.find(key), current);
				}

				@Override
				public double visitDouble(String key, double current) {
					String string = this.find(key);
					if (string == null) {
						return current;
					} else if (GameOptions.isTrue(string)) {
						return 1.0;
					} else if (GameOptions.isFalse(string)) {
						return 0.0;
					} else {
						try {
							return Double.parseDouble(string);
						} catch (NumberFormatException var6) {
							GameOptions.LOGGER.warn("Invalid floating point value for option {} = {}", key, string, var6);
							return current;
						}
					}
				}

				@Override
				public float visitFloat(String key, float current) {
					String string = this.find(key);
					if (string == null) {
						return current;
					} else if (GameOptions.isTrue(string)) {
						return 1.0F;
					} else if (GameOptions.isFalse(string)) {
						return 0.0F;
					} else {
						try {
							return Float.parseFloat(string);
						} catch (NumberFormatException var5) {
							GameOptions.LOGGER.warn("Invalid floating point value for option {} = {}", key, string, var5);
							return current;
						}
					}
				}

				@Override
				public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
					String string = this.find(key);
					return (T)(string == null ? current : decoder.apply(string));
				}

				@Override
				public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
					String string = this.find(key);
					if (string != null) {
						try {
							return (T)decoder.apply(Integer.parseInt(string));
						} catch (Exception var7) {
							GameOptions.LOGGER.warn("Invalid integer value for option {} = {}", key, string, var7);
						}
					}

					return current;
				}
			});
			if (nbtCompound2.contains("fullscreenResolution")) {
				this.fullscreenResolution = nbtCompound2.getString("fullscreenResolution");
			}

			if (this.client.getWindow() != null) {
				this.client.getWindow().setFramerateLimit(this.maxFps);
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception var7) {
			LOGGER.error("Failed to load options", (Throwable)var7);
		}
	}

	static boolean isTrue(String value) {
		return "true".equals(value);
	}

	static boolean isFalse(String value) {
		return "false".equals(value);
	}

	private NbtCompound update(NbtCompound nbt) {
		int i = 0;

		try {
			i = Integer.parseInt(nbt.getString("version"));
		} catch (RuntimeException var4) {
		}

		return NbtHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, nbt, i);
	}

	public void write() {
		try {
			final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

			try {
				printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
				this.accept(new GameOptions.Visitor() {
					public void print(String key) {
						printWriter.print(key);
						printWriter.print(':');
					}

					@Override
					public int visitInt(String key, int current) {
						this.print(key);
						printWriter.println(current);
						return current;
					}

					@Override
					public boolean visitBoolean(String key, boolean current) {
						this.print(key);
						printWriter.println(current);
						return current;
					}

					@Override
					public String visitString(String key, String current) {
						this.print(key);
						printWriter.println(current);
						return current;
					}

					@Override
					public double visitDouble(String key, double current) {
						this.print(key);
						printWriter.println(current);
						return current;
					}

					@Override
					public float visitFloat(String key, float current) {
						this.print(key);
						printWriter.println(current);
						return current;
					}

					@Override
					public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
						this.print(key);
						printWriter.println((String)encoder.apply(current));
						return current;
					}

					@Override
					public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
						this.print(key);
						printWriter.println(encoder.applyAsInt(current));
						return current;
					}
				});
				if (this.client.getWindow().getVideoMode().isPresent()) {
					printWriter.println("fullscreenResolution:" + ((VideoMode)this.client.getWindow().getVideoMode().get()).asString());
				}
			} catch (Throwable var5) {
				try {
					printWriter.close();
				} catch (Throwable var4) {
					var5.addSuppressed(var4);
				}

				throw var5;
			}

			printWriter.close();
		} catch (Exception var6) {
			LOGGER.error("Failed to save options", (Throwable)var6);
		}

		this.sendClientSettings();
	}

	public float getSoundVolume(SoundCategory category) {
		return this.soundVolumeLevels.getFloat(category);
	}

	public void setSoundVolume(SoundCategory category, float volume) {
		this.soundVolumeLevels.put(category, volume);
		this.client.getSoundManager().updateSoundVolume(category, volume);
	}

	/**
	 * Sends the current client settings to the server if the client is
	 * connected to a server.
	 * 
	 * <p>Called when a player joins the game or when client settings are
	 * changed.
	 */
	public void sendClientSettings() {
		if (this.client.player != null) {
			int i = 0;

			for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
				i |= playerModelPart.getBitFlag();
			}

			this.client
				.player
				.networkHandler
				.sendPacket(
					new ClientSettingsC2SPacket(
						this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainArm, this.client.shouldFilterText(), this.allowServerListing
					)
				);
		}
	}

	private void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
		if (enabled) {
			this.enabledPlayerModelParts.add(part);
		} else {
			this.enabledPlayerModelParts.remove(part);
		}
	}

	public boolean isPlayerModelPartEnabled(PlayerModelPart part) {
		return this.enabledPlayerModelParts.contains(part);
	}

	public void togglePlayerModelPart(PlayerModelPart part, boolean enabled) {
		this.setPlayerModelPart(part, enabled);
		this.sendClientSettings();
	}

	public CloudRenderMode getCloudRenderMode() {
		return this.getViewDistance() >= 4 ? this.cloudRenderMode : CloudRenderMode.OFF;
	}

	public boolean shouldUseNativeTransport() {
		return this.useNativeTransport;
	}

	public void addResourcePackProfilesToManager(ResourcePackManager manager) {
		Set<String> set = Sets.<String>newLinkedHashSet();
		Iterator<String> iterator = this.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			ResourcePackProfile resourcePackProfile = manager.getProfile(string);
			if (resourcePackProfile == null && !string.startsWith("file/")) {
				resourcePackProfile = manager.getProfile("file/" + string);
			}

			if (resourcePackProfile == null) {
				LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string);
				iterator.remove();
			} else if (!resourcePackProfile.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
				LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", string);
				iterator.remove();
			} else if (resourcePackProfile.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
				LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", string);
				this.incompatibleResourcePacks.remove(string);
			} else {
				set.add(resourcePackProfile.getName());
			}
		}

		manager.setEnabledProfiles(set);
	}

	public Perspective getPerspective() {
		return this.perspective;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	private static List<String> parseList(String content) {
		List<String> list = JsonHelper.deserialize(GSON, content, STRING_LIST_TYPE);
		return (List<String>)(list != null ? list : Lists.<String>newArrayList());
	}

	private static CloudRenderMode loadCloudRenderMode(String literal) {
		switch (literal) {
			case "true":
				return CloudRenderMode.FANCY;
			case "fast":
				return CloudRenderMode.FAST;
			case "false":
			default:
				return CloudRenderMode.OFF;
		}
	}

	private static String saveCloudRenderMode(CloudRenderMode mode) {
		switch (mode) {
			case FANCY:
				return "true";
			case FAST:
				return "fast";
			case OFF:
			default:
				return "false";
		}
	}

	private static AoMode loadAo(String value) {
		if (isTrue(value)) {
			return AoMode.MAX;
		} else {
			return isFalse(value) ? AoMode.OFF : AoMode.byId(Integer.parseInt(value));
		}
	}

	private static Arm loadArm(String arm) {
		return "left".equals(arm) ? Arm.LEFT : Arm.RIGHT;
	}

	private static String saveArm(Arm arm) {
		return arm == Arm.LEFT ? "left" : "right";
	}

	public File getOptionsFile() {
		return this.optionsFile;
	}

	public String collectProfiledOptions() {
		ImmutableList<Pair<String, String>> immutableList = ImmutableList.<Pair<String, String>>builder()
			.add(Pair.of("ao", String.valueOf(this.ao)))
			.add(Pair.of("biomeBlendRadius", String.valueOf(this.biomeBlendRadius)))
			.add(Pair.of("enableVsync", String.valueOf(this.enableVsync)))
			.add(Pair.of("entityDistanceScaling", String.valueOf(this.entityDistanceScaling)))
			.add(Pair.of("entityShadows", String.valueOf(this.entityShadows)))
			.add(Pair.of("forceUnicodeFont", String.valueOf(this.forceUnicodeFont)))
			.add(Pair.of("fov", String.valueOf(this.fov)))
			.add(Pair.of("fovEffectScale", String.valueOf(this.fovEffectScale)))
			.add(Pair.of("prioritizeChunkUpdates", String.valueOf(this.chunkBuilderMode)))
			.add(Pair.of("fullscreen", String.valueOf(this.fullscreen)))
			.add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenResolution)))
			.add(Pair.of("gamma", String.valueOf(this.gamma)))
			.add(Pair.of("glDebugVerbosity", String.valueOf(this.glDebugVerbosity)))
			.add(Pair.of("graphicsMode", String.valueOf(this.graphicsMode)))
			.add(Pair.of("guiScale", String.valueOf(this.guiScale)))
			.add(Pair.of("maxFps", String.valueOf(this.maxFps)))
			.add(Pair.of("mipmapLevels", String.valueOf(this.mipmapLevels)))
			.add(Pair.of("narrator", String.valueOf(this.narrator)))
			.add(Pair.of("overrideHeight", String.valueOf(this.overrideHeight)))
			.add(Pair.of("overrideWidth", String.valueOf(this.overrideWidth)))
			.add(Pair.of("particles", String.valueOf(this.particles)))
			.add(Pair.of("reducedDebugInfo", String.valueOf(this.reducedDebugInfo)))
			.add(Pair.of("renderClouds", String.valueOf(this.cloudRenderMode)))
			.add(Pair.of("renderDistance", String.valueOf(this.viewDistance)))
			.add(Pair.of("simulationDistance", String.valueOf(this.simulationDistance)))
			.add(Pair.of("resourcePacks", String.valueOf(this.resourcePacks)))
			.add(Pair.of("screenEffectScale", String.valueOf(this.distortionEffectScale)))
			.add(Pair.of("syncChunkWrites", String.valueOf(this.syncChunkWrites)))
			.add(Pair.of("useNativeTransport", String.valueOf(this.useNativeTransport)))
			.add(Pair.of("soundDevice", String.valueOf(this.soundDevice)))
			.build();
		return (String)immutableList.stream()
			.map(option -> (String)option.getFirst() + ": " + (String)option.getSecond())
			.collect(Collectors.joining(System.lineSeparator()));
	}

	public void setServerViewDistance(int viewDistance) {
		this.serverViewDistance = viewDistance;
	}

	public int getViewDistance() {
		return this.serverViewDistance > 0 ? Math.min(this.viewDistance, this.serverViewDistance) : this.viewDistance;
	}

	@Environment(EnvType.CLIENT)
	interface Visitor {
		int visitInt(String key, int current);

		boolean visitBoolean(String key, boolean current);

		String visitString(String key, String current);

		double visitDouble(String key, double current);

		float visitFloat(String key, float current);

		<T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder);

		<T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder);
	}
}
