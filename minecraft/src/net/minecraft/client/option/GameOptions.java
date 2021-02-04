package net.minecraft.client.option;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
	public int viewDistance;
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
		this.snooperEnabled = visitor.visitBoolean("snooperEnabled", this.snooperEnabled);
		this.showSubtitles = visitor.visitBoolean("showSubtitles", this.showSubtitles);
		this.touchscreen = visitor.visitBoolean("touchscreen", this.touchscreen);
		this.fullscreen = visitor.visitBoolean("fullscreen", this.fullscreen);
		this.bobView = visitor.visitBoolean("bobView", this.bobView);
		this.sneakToggled = visitor.visitBoolean("toggleCrouch", this.sneakToggled);
		this.sprintToggled = visitor.visitBoolean("toggleSprint", this.sprintToggled);
		this.mouseSensitivity = visitor.visitDouble("mouseSensitivity", this.mouseSensitivity);
		this.fov = visitor.visitDouble("fov", (this.fov - 70.0) / 40.0) * 40.0 + 70.0;
		this.distortionEffectScale = visitor.visitFloat("screenEffectScale", this.distortionEffectScale);
		this.fovEffectScale = visitor.visitFloat("fovEffectScale", this.fovEffectScale);
		this.gamma = visitor.visitDouble("gamma", this.gamma);
		this.viewDistance = visitor.visitInt("renderDistance", this.viewDistance);
		this.entityDistanceScaling = visitor.visitFloat("entityDistanceScaling", this.entityDistanceScaling);
		this.guiScale = visitor.visitInt("guiScale", this.guiScale);
		this.particles = visitor.visitObject("particles", this.particles, ParticlesMode::byId, ParticlesMode::getId);
		this.maxFps = visitor.visitInt("maxFps", this.maxFps);
		this.difficulty = visitor.visitObject("difficulty", this.difficulty, Difficulty::byOrdinal, Difficulty::getId);
		this.graphicsMode = visitor.visitObject("graphicsMode", this.graphicsMode, GraphicsMode::byId, GraphicsMode::getId);
		this.ao = visitor.visitObject("ao", this.ao, GameOptions::loadAo, ao -> Integer.toString(ao.getId()));
		this.biomeBlendRadius = visitor.visitInt("biomeBlendRadius", this.biomeBlendRadius);
		this.cloudRenderMode = visitor.visitObject("renderClouds", this.cloudRenderMode, GameOptions::loadCloudRenderMode, GameOptions::saveCloudRenderMode);
		this.resourcePacks = visitor.visitObject("resourcePacks", this.resourcePacks, GameOptions::parseList, GSON::toJson);
		this.incompatibleResourcePacks = visitor.visitObject("incompatibleResourcePacks", this.incompatibleResourcePacks, GameOptions::parseList, GSON::toJson);
		this.lastServer = visitor.visitString("lastServer", this.lastServer);
		this.language = visitor.visitString("lang", this.language);
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
			CompoundTag compoundTag = new CompoundTag();
			BufferedReader bufferedReader = Files.newReader(this.optionsFile, Charsets.UTF_8);
			Throwable var3 = null;

			try {
				bufferedReader.lines().forEach(line -> {
					try {
						Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
						compoundTag.putString((String)iterator.next(), (String)iterator.next());
					} catch (Exception var3x) {
						LOGGER.warn("Skipping bad option: {}", line);
					}
				});
			} catch (Throwable var13) {
				var3 = var13;
				throw var13;
			} finally {
				if (bufferedReader != null) {
					if (var3 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var12) {
							var3.addSuppressed(var12);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			final CompoundTag compoundTag2 = this.update(compoundTag);
			if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
				if (isTrue(compoundTag2.getString("fancyGraphics"))) {
					this.graphicsMode = GraphicsMode.FANCY;
				} else {
					this.graphicsMode = GraphicsMode.FAST;
				}
			}

			this.accept(new GameOptions.Visitor() {
				@Nullable
				private String find(String key) {
					return compoundTag2.contains(key) ? compoundTag2.getString(key) : null;
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
			if (compoundTag2.contains("fullscreenResolution")) {
				this.fullscreenResolution = compoundTag2.getString("fullscreenResolution");
			}

			if (this.client.getWindow() != null) {
				this.client.getWindow().setFramerateLimit(this.maxFps);
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception var15) {
			LOGGER.error("Failed to load options", (Throwable)var15);
		}
	}

	private static boolean isTrue(String value) {
		return "true".equals(value);
	}

	private static boolean isFalse(String value) {
		return "false".equals(value);
	}

	private CompoundTag update(CompoundTag tag) {
		int i = 0;

		try {
			i = Integer.parseInt(tag.getString("version"));
		} catch (RuntimeException var4) {
		}

		return NbtHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, tag, i);
	}

	public void write() {
		try {
			final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			Throwable var2 = null;

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
			} catch (Throwable var12) {
				var2 = var12;
				throw var12;
			} finally {
				if (printWriter != null) {
					if (var2 != null) {
						try {
							printWriter.close();
						} catch (Throwable var11) {
							var2.addSuppressed(var11);
						}
					} else {
						printWriter.close();
					}
				}
			}
		} catch (Exception var14) {
			LOGGER.error("Failed to save options", (Throwable)var14);
		}

		this.onPlayerModelPartChange();
	}

	public float getSoundVolume(SoundCategory category) {
		return this.soundVolumeLevels.getFloat(category);
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
