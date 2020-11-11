package net.minecraft.client.options;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final TypeToken<List<String>> STRING_LIST_TYPE = new TypeToken<List<String>>() {
	};
	private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
	public double mouseSensitivity = 0.5;
	public int viewDistance = -1;
	public float entityDistanceScaling = 1.0F;
	public int maxFps = 120;
	public CloudRenderMode cloudRenderMode = CloudRenderMode.FANCY;
	public GraphicsMode graphicsMode = GraphicsMode.FANCY;
	public AoMode ao = AoMode.MAX;
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
	private final Set<PlayerModelPart> enabledPlayerModelParts = Sets.<PlayerModelPart>newHashSet(PlayerModelPart.values());
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
	private final Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
	public boolean useNativeTransport = true;
	public AttackIndicator attackIndicator = AttackIndicator.CROSSHAIR;
	public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
	public boolean joinedFirstServer = false;
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
	public boolean reducedDebugInfo;
	public boolean snooperEnabled = true;
	public boolean showSubtitles;
	public boolean backgroundForChatOnly = true;
	public boolean touchscreen;
	public boolean fullscreen;
	public boolean bobView = true;
	public boolean sneakToggled;
	public boolean sprintToggled;
	public boolean skipMultiplayerWarning;
	public boolean hideMatchedNames = true;
	public final KeyBinding keyForward = new KeyBinding("key.forward", 87, "key.categories.movement");
	public final KeyBinding keyLeft = new KeyBinding("key.left", 65, "key.categories.movement");
	public final KeyBinding keyBack = new KeyBinding("key.back", 83, "key.categories.movement");
	public final KeyBinding keyRight = new KeyBinding("key.right", 68, "key.categories.movement");
	public final KeyBinding keyJump = new KeyBinding("key.jump", 32, "key.categories.movement");
	public final KeyBinding keySneak = new StickyKeyBinding("key.sneak", 340, "key.categories.movement", () -> this.sneakToggled);
	public final KeyBinding keySprint = new StickyKeyBinding("key.sprint", 341, "key.categories.movement", () -> this.sprintToggled);
	public final KeyBinding keyInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
	public final KeyBinding keySwapHands = new KeyBinding("key.swapOffhand", 70, "key.categories.inventory");
	public final KeyBinding keyDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
	public final KeyBinding keyUse = new KeyBinding("key.use", InputUtil.Type.MOUSE, 1, "key.categories.gameplay");
	public final KeyBinding keyAttack = new KeyBinding("key.attack", InputUtil.Type.MOUSE, 0, "key.categories.gameplay");
	public final KeyBinding keyPickItem = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, 2, "key.categories.gameplay");
	public final KeyBinding keyChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
	public final KeyBinding keyPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
	public final KeyBinding keyCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
	public final KeyBinding keySocialInteractions = new KeyBinding("key.socialInteractions", 80, "key.categories.multiplayer");
	public final KeyBinding keyScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
	public final KeyBinding keyTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
	public final KeyBinding keySmoothCamera = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
	public final KeyBinding keyFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
	public final KeyBinding keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
	public final KeyBinding keyAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
	public final KeyBinding[] keysHotbar = new KeyBinding[]{
		new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"),
		new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"),
		new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"),
		new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"),
		new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"),
		new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"),
		new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"),
		new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"),
		new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")
	};
	public final KeyBinding keySaveToolbarActivator = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
	public final KeyBinding keyLoadToolbarActivator = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
	public final KeyBinding[] keysAll = ArrayUtils.addAll(
		new KeyBinding[]{
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
		},
		this.keysHotbar
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
	public double gamma;
	public int guiScale;
	public ParticlesMode particles = ParticlesMode.ALL;
	public NarratorMode narrator = NarratorMode.OFF;
	public String language = "en_us";
	public boolean syncChunkWrites;

	public GameOptions(MinecraftClient client, File optionsFile) {
		this.client = client;
		this.optionsFile = new File(optionsFile, "options.txt");
		if (client.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			Option.RENDER_DISTANCE.setMax(32.0F);
		} else {
			Option.RENDER_DISTANCE.setMax(16.0F);
		}

		this.viewDistance = client.is64Bit() ? 12 : 8;
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

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			this.soundVolumeLevels.clear();
			CompoundTag compoundTag = new CompoundTag();
			BufferedReader bufferedReader = Files.newReader(this.optionsFile, Charsets.UTF_8);
			Throwable var3 = null;

			try {
				bufferedReader.lines().forEach(stringx -> {
					try {
						Iterator<String> iterator = COLON_SPLITTER.split(stringx).iterator();
						compoundTag.putString((String)iterator.next(), (String)iterator.next());
					} catch (Exception var3x) {
						LOGGER.warn("Skipping bad option: {}", stringx);
					}
				});
			} catch (Throwable var17) {
				var3 = var17;
				throw var17;
			} finally {
				if (bufferedReader != null) {
					if (var3 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var16) {
							var3.addSuppressed(var16);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			CompoundTag compoundTag2 = this.update(compoundTag);
			if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
				if (isTrue(compoundTag2.getString("fancyGraphics"))) {
					this.graphicsMode = GraphicsMode.FANCY;
				} else {
					this.graphicsMode = GraphicsMode.FAST;
				}
			}

			for (String string : compoundTag2.getKeys()) {
				String string2 = compoundTag2.getString(string);

				try {
					if ("autoJump".equals(string)) {
						this.autoJump = isTrue(string2);
					}

					if ("autoSuggestions".equals(string)) {
						this.autoSuggestions = isTrue(string2);
					}

					if ("chatColors".equals(string)) {
						this.chatColors = isTrue(string2);
					}

					if ("chatLinks".equals(string)) {
						this.chatLinks = isTrue(string2);
					}

					if ("chatLinksPrompt".equals(string)) {
						this.chatLinksPrompt = isTrue(string2);
					}

					if ("enableVsync".equals(string)) {
						this.enableVsync = isTrue(string2);
					}

					if ("entityShadows".equals(string)) {
						this.entityShadows = isTrue(string2);
					}

					if ("forceUnicodeFont".equals(string)) {
						this.forceUnicodeFont = isTrue(string2);
					}

					if ("discrete_mouse_scroll".equals(string)) {
						this.discreteMouseScroll = isTrue(string2);
					}

					if ("invertYMouse".equals(string)) {
						this.invertYMouse = isTrue(string2);
					}

					if ("realmsNotifications".equals(string)) {
						this.realmsNotifications = isTrue(string2);
					}

					if ("reducedDebugInfo".equals(string)) {
						this.reducedDebugInfo = isTrue(string2);
					}

					if ("showSubtitles".equals(string)) {
						this.showSubtitles = isTrue(string2);
					}

					if ("snooperEnabled".equals(string)) {
						this.snooperEnabled = isTrue(string2);
					}

					if ("touchscreen".equals(string)) {
						this.touchscreen = isTrue(string2);
					}

					if ("fullscreen".equals(string)) {
						this.fullscreen = isTrue(string2);
					}

					if ("bobView".equals(string)) {
						this.bobView = isTrue(string2);
					}

					if ("toggleCrouch".equals(string)) {
						this.sneakToggled = isTrue(string2);
					}

					if ("toggleSprint".equals(string)) {
						this.sprintToggled = isTrue(string2);
					}

					if ("mouseSensitivity".equals(string)) {
						this.mouseSensitivity = (double)parseFloat(string2);
					}

					if ("fov".equals(string)) {
						this.fov = (double)(parseFloat(string2) * 40.0F + 70.0F);
					}

					if ("screenEffectScale".equals(string)) {
						this.distortionEffectScale = parseFloat(string2);
					}

					if ("fovEffectScale".equals(string)) {
						this.fovEffectScale = parseFloat(string2);
					}

					if ("gamma".equals(string)) {
						this.gamma = (double)parseFloat(string2);
					}

					if ("renderDistance".equals(string)) {
						this.viewDistance = Integer.parseInt(string2);
					}

					if ("entityDistanceScaling".equals(string)) {
						this.entityDistanceScaling = Float.parseFloat(string2);
					}

					if ("guiScale".equals(string)) {
						this.guiScale = Integer.parseInt(string2);
					}

					if ("particles".equals(string)) {
						this.particles = ParticlesMode.byId(Integer.parseInt(string2));
					}

					if ("maxFps".equals(string)) {
						this.maxFps = Integer.parseInt(string2);
						if (this.client.getWindow() != null) {
							this.client.getWindow().setFramerateLimit(this.maxFps);
						}
					}

					if ("difficulty".equals(string)) {
						this.difficulty = Difficulty.byOrdinal(Integer.parseInt(string2));
					}

					if ("graphicsMode".equals(string)) {
						this.graphicsMode = GraphicsMode.byId(Integer.parseInt(string2));
					}

					if ("tutorialStep".equals(string)) {
						this.tutorialStep = TutorialStep.byName(string2);
					}

					if ("ao".equals(string)) {
						if (isTrue(string2)) {
							this.ao = AoMode.MAX;
						} else if (isFalse(string2)) {
							this.ao = AoMode.OFF;
						} else {
							this.ao = AoMode.byId(Integer.parseInt(string2));
						}
					}

					if ("renderClouds".equals(string)) {
						if (isTrue(string2)) {
							this.cloudRenderMode = CloudRenderMode.FANCY;
						} else if (isFalse(string2)) {
							this.cloudRenderMode = CloudRenderMode.OFF;
						} else if ("fast".equals(string2)) {
							this.cloudRenderMode = CloudRenderMode.FAST;
						}
					}

					if ("attackIndicator".equals(string)) {
						this.attackIndicator = AttackIndicator.byId(Integer.parseInt(string2));
					}

					if ("resourcePacks".equals(string)) {
						this.resourcePacks = JsonHelper.deserialize(GSON, string2, STRING_LIST_TYPE);
						if (this.resourcePacks == null) {
							this.resourcePacks = Lists.<String>newArrayList();
						}
					}

					if ("incompatibleResourcePacks".equals(string)) {
						this.incompatibleResourcePacks = JsonHelper.deserialize(GSON, string2, STRING_LIST_TYPE);
						if (this.incompatibleResourcePacks == null) {
							this.incompatibleResourcePacks = Lists.<String>newArrayList();
						}
					}

					if ("lastServer".equals(string)) {
						this.lastServer = string2;
					}

					if ("lang".equals(string)) {
						this.language = string2;
					}

					if ("chatVisibility".equals(string)) {
						this.chatVisibility = ChatVisibility.byId(Integer.parseInt(string2));
					}

					if ("chatOpacity".equals(string)) {
						this.chatOpacity = (double)parseFloat(string2);
					}

					if ("chatLineSpacing".equals(string)) {
						this.chatLineSpacing = (double)parseFloat(string2);
					}

					if ("textBackgroundOpacity".equals(string)) {
						this.textBackgroundOpacity = (double)parseFloat(string2);
					}

					if ("backgroundForChatOnly".equals(string)) {
						this.backgroundForChatOnly = isTrue(string2);
					}

					if ("fullscreenResolution".equals(string)) {
						this.fullscreenResolution = string2;
					}

					if ("hideServerAddress".equals(string)) {
						this.hideServerAddress = isTrue(string2);
					}

					if ("advancedItemTooltips".equals(string)) {
						this.advancedItemTooltips = isTrue(string2);
					}

					if ("pauseOnLostFocus".equals(string)) {
						this.pauseOnLostFocus = isTrue(string2);
					}

					if ("overrideHeight".equals(string)) {
						this.overrideHeight = Integer.parseInt(string2);
					}

					if ("overrideWidth".equals(string)) {
						this.overrideWidth = Integer.parseInt(string2);
					}

					if ("heldItemTooltips".equals(string)) {
						this.heldItemTooltips = isTrue(string2);
					}

					if ("chatHeightFocused".equals(string)) {
						this.chatHeightFocused = (double)parseFloat(string2);
					}

					if ("chatDelay".equals(string)) {
						this.chatDelay = (double)parseFloat(string2);
					}

					if ("chatHeightUnfocused".equals(string)) {
						this.chatHeightUnfocused = (double)parseFloat(string2);
					}

					if ("chatScale".equals(string)) {
						this.chatScale = (double)parseFloat(string2);
					}

					if ("chatWidth".equals(string)) {
						this.chatWidth = (double)parseFloat(string2);
					}

					if ("mipmapLevels".equals(string)) {
						this.mipmapLevels = Integer.parseInt(string2);
					}

					if ("useNativeTransport".equals(string)) {
						this.useNativeTransport = isTrue(string2);
					}

					if ("mainHand".equals(string)) {
						this.mainArm = "left".equals(string2) ? Arm.LEFT : Arm.RIGHT;
					}

					if ("narrator".equals(string)) {
						this.narrator = NarratorMode.byId(Integer.parseInt(string2));
					}

					if ("biomeBlendRadius".equals(string)) {
						this.biomeBlendRadius = Integer.parseInt(string2);
					}

					if ("mouseWheelSensitivity".equals(string)) {
						this.mouseWheelSensitivity = (double)parseFloat(string2);
					}

					if ("rawMouseInput".equals(string)) {
						this.rawMouseInput = isTrue(string2);
					}

					if ("glDebugVerbosity".equals(string)) {
						this.glDebugVerbosity = Integer.parseInt(string2);
					}

					if ("skipMultiplayerWarning".equals(string)) {
						this.skipMultiplayerWarning = isTrue(string2);
					}

					if ("hideMatchedNames".equals(string)) {
						this.hideMatchedNames = "true".equals(string2);
					}

					if ("joinedFirstServer".equals(string)) {
						this.joinedFirstServer = "true".equals(string2);
					}

					if ("syncChunkWrites".equals(string)) {
						this.syncChunkWrites = isTrue(string2);
					}

					for (KeyBinding keyBinding : this.keysAll) {
						if (string.equals("key_" + keyBinding.getTranslationKey())) {
							keyBinding.setBoundKey(InputUtil.fromTranslationKey(string2));
						}
					}

					for (SoundCategory soundCategory : SoundCategory.values()) {
						if (string.equals("soundCategory_" + soundCategory.getName())) {
							this.soundVolumeLevels.put(soundCategory, parseFloat(string2));
						}
					}

					for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
						if (string.equals("modelPart_" + playerModelPart.getName())) {
							this.setPlayerModelPart(playerModelPart, isTrue(string2));
						}
					}
				} catch (Exception var19) {
					LOGGER.warn("Skipping bad option: {}:{}", string, string2);
				}
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception var20) {
			LOGGER.error("Failed to load options", (Throwable)var20);
		}
	}

	private static boolean isTrue(String string) {
		return "true".equals(string);
	}

	private static boolean isFalse(String string) {
		return "false".equals(string);
	}

	private CompoundTag update(CompoundTag tag) {
		int i = 0;

		try {
			i = Integer.parseInt(tag.getString("version"));
		} catch (RuntimeException var4) {
		}

		return NbtHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, tag, i);
	}

	private static float parseFloat(String string) {
		if (isTrue(string)) {
			return 1.0F;
		} else {
			return isFalse(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void write() {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			Throwable var2 = null;

			try {
				printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
				printWriter.println("autoJump:" + this.autoJump);
				printWriter.println("autoSuggestions:" + this.autoSuggestions);
				printWriter.println("chatColors:" + this.chatColors);
				printWriter.println("chatLinks:" + this.chatLinks);
				printWriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
				printWriter.println("enableVsync:" + this.enableVsync);
				printWriter.println("entityShadows:" + this.entityShadows);
				printWriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
				printWriter.println("discrete_mouse_scroll:" + this.discreteMouseScroll);
				printWriter.println("invertYMouse:" + this.invertYMouse);
				printWriter.println("realmsNotifications:" + this.realmsNotifications);
				printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
				printWriter.println("snooperEnabled:" + this.snooperEnabled);
				printWriter.println("showSubtitles:" + this.showSubtitles);
				printWriter.println("touchscreen:" + this.touchscreen);
				printWriter.println("fullscreen:" + this.fullscreen);
				printWriter.println("bobView:" + this.bobView);
				printWriter.println("toggleCrouch:" + this.sneakToggled);
				printWriter.println("toggleSprint:" + this.sprintToggled);
				printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
				printWriter.println("fov:" + (this.fov - 70.0) / 40.0);
				printWriter.println("screenEffectScale:" + this.distortionEffectScale);
				printWriter.println("fovEffectScale:" + this.fovEffectScale);
				printWriter.println("gamma:" + this.gamma);
				printWriter.println("renderDistance:" + this.viewDistance);
				printWriter.println("entityDistanceScaling:" + this.entityDistanceScaling);
				printWriter.println("guiScale:" + this.guiScale);
				printWriter.println("particles:" + this.particles.getId());
				printWriter.println("maxFps:" + this.maxFps);
				printWriter.println("difficulty:" + this.difficulty.getId());
				printWriter.println("graphicsMode:" + this.graphicsMode.getId());
				printWriter.println("ao:" + this.ao.getId());
				printWriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
				switch (this.cloudRenderMode) {
					case FANCY:
						printWriter.println("renderClouds:true");
						break;
					case FAST:
						printWriter.println("renderClouds:fast");
						break;
					case OFF:
						printWriter.println("renderClouds:false");
				}

				printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
				printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
				printWriter.println("lastServer:" + this.lastServer);
				printWriter.println("lang:" + this.language);
				printWriter.println("chatVisibility:" + this.chatVisibility.getId());
				printWriter.println("chatOpacity:" + this.chatOpacity);
				printWriter.println("chatLineSpacing:" + this.chatLineSpacing);
				printWriter.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
				printWriter.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
				if (this.client.getWindow().getVideoMode().isPresent()) {
					printWriter.println("fullscreenResolution:" + ((VideoMode)this.client.getWindow().getVideoMode().get()).asString());
				}

				printWriter.println("hideServerAddress:" + this.hideServerAddress);
				printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
				printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
				printWriter.println("overrideWidth:" + this.overrideWidth);
				printWriter.println("overrideHeight:" + this.overrideHeight);
				printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
				printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
				printWriter.println("chatDelay: " + this.chatDelay);
				printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
				printWriter.println("chatScale:" + this.chatScale);
				printWriter.println("chatWidth:" + this.chatWidth);
				printWriter.println("mipmapLevels:" + this.mipmapLevels);
				printWriter.println("useNativeTransport:" + this.useNativeTransport);
				printWriter.println("mainHand:" + (this.mainArm == Arm.LEFT ? "left" : "right"));
				printWriter.println("attackIndicator:" + this.attackIndicator.getId());
				printWriter.println("narrator:" + this.narrator.getId());
				printWriter.println("tutorialStep:" + this.tutorialStep.getName());
				printWriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
				printWriter.println("rawMouseInput:" + this.rawMouseInput);
				printWriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
				printWriter.println("skipMultiplayerWarning:" + this.skipMultiplayerWarning);
				printWriter.println("hideMatchedNames:" + this.hideMatchedNames);
				printWriter.println("joinedFirstServer:" + this.joinedFirstServer);
				printWriter.println("syncChunkWrites:" + this.syncChunkWrites);

				for (KeyBinding keyBinding : this.keysAll) {
					printWriter.println("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.getBoundKeyTranslationKey());
				}

				for (SoundCategory soundCategory : SoundCategory.values()) {
					printWriter.println("soundCategory_" + soundCategory.getName() + ":" + this.getSoundVolume(soundCategory));
				}

				for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
					printWriter.println("modelPart_" + playerModelPart.getName() + ":" + this.enabledPlayerModelParts.contains(playerModelPart));
				}
			} catch (Throwable var15) {
				var2 = var15;
				throw var15;
			} finally {
				if (printWriter != null) {
					if (var2 != null) {
						try {
							printWriter.close();
						} catch (Throwable var14) {
							var2.addSuppressed(var14);
						}
					} else {
						printWriter.close();
					}
				}
			}
		} catch (Exception var17) {
			LOGGER.error("Failed to save options", (Throwable)var17);
		}

		this.onPlayerModelPartChange();
	}

	public float getSoundVolume(SoundCategory category) {
		return this.soundVolumeLevels.containsKey(category) ? (Float)this.soundVolumeLevels.get(category) : 1.0F;
	}

	public void setSoundVolume(SoundCategory category, float volume) {
		this.soundVolumeLevels.put(category, volume);
		this.client.getSoundManager().updateSoundVolume(category, volume);
	}

	public void onPlayerModelPartChange() {
		if (this.client.player != null) {
			int i = 0;

			for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
				i |= playerModelPart.getBitFlag();
			}

			this.client
				.player
				.networkHandler
				.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainArm));
		}
	}

	public void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
		if (enabled) {
			this.enabledPlayerModelParts.add(part);
		} else {
			this.enabledPlayerModelParts.remove(part);
		}

		this.onPlayerModelPartChange();
	}

	public boolean isPlayerModelPartEnabled(PlayerModelPart part) {
		return this.enabledPlayerModelParts.contains(part);
	}

	public void togglePlayerModelPart(PlayerModelPart part, boolean bl) {
		if (!bl) {
			this.enabledPlayerModelParts.remove(part);
		} else {
			this.enabledPlayerModelParts.add(part);
		}

		this.onPlayerModelPartChange();
	}

	public CloudRenderMode getCloudRenderMode() {
		return this.viewDistance >= 4 ? this.cloudRenderMode : CloudRenderMode.OFF;
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
}
