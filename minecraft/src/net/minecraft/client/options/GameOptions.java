package net.minecraft.client.options;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.datafixers.DataFixTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;
import net.minecraft.world.Difficulty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final Type STRING_LIST_TYPE = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	public static final Splitter COLON_SPLITTER = Splitter.on(':');
	public double mouseSensitivity = 0.5;
	public int viewDistance = -1;
	public int maxFps = 120;
	public CloudRenderMode cloudRenderMode = CloudRenderMode.field_18164;
	public boolean fancyGraphics = true;
	public AoOption ao = AoOption.field_18146;
	public List<String> resourcePacks = Lists.<String>newArrayList();
	public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
	public ChatVisibility chatVisibility = ChatVisibility.FULL;
	public double chatOpacity = 1.0;
	public double field_18726 = 0.5;
	@Nullable
	public String fullscreenResolution;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> enabledPlayerModelParts = Sets.<PlayerModelPart>newHashSet(PlayerModelPart.values());
	public AbsoluteHand mainHand = AbsoluteHand.field_6183;
	public int overrideWidth;
	public int overrideHeight;
	public boolean heldItemTooltips = true;
	public double chatScale = 1.0;
	public double chatWidth = 1.0;
	public double chatHeightUnfocused = 0.44366196F;
	public double chatHeightFocused = 1.0;
	public int mipmapLevels = 4;
	private final Map<SoundCategory, Float> soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
	public boolean useNativeTransport = true;
	public AttackIndicator attackIndicator = AttackIndicator.field_18152;
	public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
	public int biomeBlendRadius = 2;
	public double mouseWheelSensitivity = 1.0;
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
	public boolean realmsNotifications = true;
	public boolean reducedDebugInfo;
	public boolean snooperEnabled = true;
	public boolean showSubtitles;
	public boolean field_18725 = true;
	public boolean touchscreen;
	public boolean fullscreen;
	public boolean bobView = true;
	public final KeyBinding keyForward = new KeyBinding("key.forward", 87, "key.categories.movement");
	public final KeyBinding keyLeft = new KeyBinding("key.left", 65, "key.categories.movement");
	public final KeyBinding keyBack = new KeyBinding("key.back", 83, "key.categories.movement");
	public final KeyBinding keyRight = new KeyBinding("key.right", 68, "key.categories.movement");
	public final KeyBinding keyJump = new KeyBinding("key.jump", 32, "key.categories.movement");
	public final KeyBinding keySneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
	public final KeyBinding keySprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
	public final KeyBinding keyInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
	public final KeyBinding keySwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
	public final KeyBinding keyDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
	public final KeyBinding keyUse = new KeyBinding("key.use", InputUtil.Type.field_1672, 1, "key.categories.gameplay");
	public final KeyBinding keyAttack = new KeyBinding("key.attack", InputUtil.Type.field_1672, 0, "key.categories.gameplay");
	public final KeyBinding keyPickItem = new KeyBinding("key.pickItem", InputUtil.Type.field_1672, 2, "key.categories.gameplay");
	public final KeyBinding keyChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
	public final KeyBinding keyPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
	public final KeyBinding keyCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
	public final KeyBinding keyScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
	public final KeyBinding keyTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
	public final KeyBinding keySmoothCamera = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
	public final KeyBinding keyFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
	public final KeyBinding keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
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
	private File optionsFile;
	public Difficulty difficulty = Difficulty.NORMAL;
	public boolean hudHidden;
	public int perspective;
	public boolean debugEnabled;
	public boolean debugProfilerEnabled;
	public boolean debugTpsEnabled;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public double fov = 70.0;
	public double gamma;
	public int guiScale;
	public ParticlesOption particles = ParticlesOption.field_18197;
	public NarratorOption narrator = NarratorOption.field_18176;
	public String language = "en_us";

	public GameOptions(MinecraftClient minecraftClient, File file) {
		this.client = minecraftClient;
		this.optionsFile = new File(file, "options.txt");
		if (minecraftClient.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			GameOption.RENDER_DISTANCE.method_18612(32.0F);
		} else {
			GameOption.RENDER_DISTANCE.method_18612(16.0F);
		}

		this.viewDistance = minecraftClient.is64Bit() ? 12 : 8;
		this.load();
	}

	public float method_19343(float f) {
		return this.field_18725 ? f : (float)this.field_18726;
	}

	public int method_19345(float f) {
		return (int)(this.method_19343(f) * 255.0F) << 24 & 0xFF000000;
	}

	public int method_19344(int i) {
		return this.field_18725 ? i : (int)(this.field_18726 * 255.0) << 24 & 0xFF000000;
	}

	public void setKeyCode(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
		keyBinding.setKeyCode(keyCode);
		this.write();
	}

	public void load() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			this.soundVolumeLevels.clear();
			List<String> list = IOUtils.readLines(new FileInputStream(this.optionsFile));
			CompoundTag compoundTag = new CompoundTag();

			for (String string : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(string).iterator();
					compoundTag.putString((String)iterator.next(), (String)iterator.next());
				} catch (Exception var10) {
					LOGGER.warn("Skipping bad option: {}", string);
				}
			}

			compoundTag = this.method_1626(compoundTag);

			for (String string : compoundTag.getKeys()) {
				String string2 = compoundTag.getString(string);

				try {
					if ("autoJump".equals(string)) {
						GameOption.AUTO_JUMP.method_18492(this, string2);
					}

					if ("autoSuggestions".equals(string)) {
						GameOption.AUTO_SUGGESTIONS.method_18492(this, string2);
					}

					if ("chatColors".equals(string)) {
						GameOption.CHAT_COLOR.method_18492(this, string2);
					}

					if ("chatLinks".equals(string)) {
						GameOption.CHAT_LINKS.method_18492(this, string2);
					}

					if ("chatLinksPrompt".equals(string)) {
						GameOption.CHAT_LINKS_PROMPT.method_18492(this, string2);
					}

					if ("enableVsync".equals(string)) {
						GameOption.VSYNC.method_18492(this, string2);
					}

					if ("entityShadows".equals(string)) {
						GameOption.ENTITY_SHADOWS.method_18492(this, string2);
					}

					if ("forceUnicodeFont".equals(string)) {
						GameOption.FORCE_UNICODE_FONT.method_18492(this, string2);
					}

					if ("invertYMouse".equals(string)) {
						GameOption.INVERT_MOUSE.method_18492(this, string2);
					}

					if ("realmsNotifications".equals(string)) {
						GameOption.REALMS_NOTIFICATIONS.method_18492(this, string2);
					}

					if ("reducedDebugInfo".equals(string)) {
						GameOption.REDUCED_DEBUG_INFO.method_18492(this, string2);
					}

					if ("showSubtitles".equals(string)) {
						GameOption.SUBTITLES.method_18492(this, string2);
					}

					if ("snooperEnabled".equals(string)) {
						GameOption.SNOOPER.method_18492(this, string2);
					}

					if ("touchscreen".equals(string)) {
						GameOption.TOUCHSCREEN.method_18492(this, string2);
					}

					if ("fullscreen".equals(string)) {
						GameOption.FULLSCREEN.method_18492(this, string2);
					}

					if ("bobView".equals(string)) {
						GameOption.VIEW_BOBBING.method_18492(this, string2);
					}

					if ("mouseSensitivity".equals(string)) {
						this.mouseSensitivity = (double)parseFloat(string2);
					}

					if ("fov".equals(string)) {
						this.fov = (double)(parseFloat(string2) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string)) {
						this.gamma = (double)parseFloat(string2);
					}

					if ("renderDistance".equals(string)) {
						this.viewDistance = Integer.parseInt(string2);
					}

					if ("guiScale".equals(string)) {
						this.guiScale = Integer.parseInt(string2);
					}

					if ("particles".equals(string)) {
						this.particles = ParticlesOption.byId(Integer.parseInt(string2));
					}

					if ("maxFps".equals(string)) {
						this.maxFps = Integer.parseInt(string2);
						this.client.window.setFramerateLimit(this.maxFps);
					}

					if ("difficulty".equals(string)) {
						this.difficulty = Difficulty.getDifficulty(Integer.parseInt(string2));
					}

					if ("fancyGraphics".equals(string)) {
						this.fancyGraphics = "true".equals(string2);
					}

					if ("tutorialStep".equals(string)) {
						this.tutorialStep = TutorialStep.byName(string2);
					}

					if ("ao".equals(string)) {
						if ("true".equals(string2)) {
							this.ao = AoOption.field_18146;
						} else if ("false".equals(string2)) {
							this.ao = AoOption.field_18144;
						} else {
							this.ao = AoOption.getOption(Integer.parseInt(string2));
						}
					}

					if ("renderClouds".equals(string)) {
						if ("true".equals(string2)) {
							this.cloudRenderMode = CloudRenderMode.field_18164;
						} else if ("false".equals(string2)) {
							this.cloudRenderMode = CloudRenderMode.field_18162;
						} else if ("fast".equals(string2)) {
							this.cloudRenderMode = CloudRenderMode.field_18163;
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

					if ("textBackgroundOpacity".equals(string)) {
						this.field_18726 = (double)parseFloat(string2);
					}

					if ("backgroundForChatOnly".equals(string)) {
						this.field_18725 = "true".equals(string2);
					}

					if ("fullscreenResolution".equals(string)) {
						this.fullscreenResolution = string2;
					}

					if ("hideServerAddress".equals(string)) {
						this.hideServerAddress = "true".equals(string2);
					}

					if ("advancedItemTooltips".equals(string)) {
						this.advancedItemTooltips = "true".equals(string2);
					}

					if ("pauseOnLostFocus".equals(string)) {
						this.pauseOnLostFocus = "true".equals(string2);
					}

					if ("overrideHeight".equals(string)) {
						this.overrideHeight = Integer.parseInt(string2);
					}

					if ("overrideWidth".equals(string)) {
						this.overrideWidth = Integer.parseInt(string2);
					}

					if ("heldItemTooltips".equals(string)) {
						this.heldItemTooltips = "true".equals(string2);
					}

					if ("chatHeightFocused".equals(string)) {
						this.chatHeightFocused = (double)parseFloat(string2);
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
						this.useNativeTransport = "true".equals(string2);
					}

					if ("mainHand".equals(string)) {
						this.mainHand = "left".equals(string2) ? AbsoluteHand.field_6182 : AbsoluteHand.field_6183;
					}

					if ("narrator".equals(string)) {
						this.narrator = NarratorOption.byId(Integer.parseInt(string2));
					}

					if ("biomeBlendRadius".equals(string)) {
						this.biomeBlendRadius = Integer.parseInt(string2);
					}

					if ("mouseWheelSensitivity".equals(string)) {
						this.mouseWheelSensitivity = (double)parseFloat(string2);
					}

					if ("glDebugVerbosity".equals(string)) {
						this.glDebugVerbosity = Integer.parseInt(string2);
					}

					for (KeyBinding keyBinding : this.keysAll) {
						if (string.equals("key_" + keyBinding.getId())) {
							keyBinding.setKeyCode(InputUtil.fromName(string2));
						}
					}

					for (SoundCategory soundCategory : SoundCategory.values()) {
						if (string.equals("soundCategory_" + soundCategory.getName())) {
							this.soundVolumeLevels.put(soundCategory, parseFloat(string2));
						}
					}

					for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
						if (string.equals("modelPart_" + playerModelPart.getName())) {
							this.setPlayerModelPart(playerModelPart, "true".equals(string2));
						}
					}
				} catch (Exception var11) {
					LOGGER.warn("Skipping bad option: {}:{}", string, string2);
				}
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception var12) {
			LOGGER.error("Failed to load options", (Throwable)var12);
		}
	}

	private CompoundTag method_1626(CompoundTag compoundTag) {
		int i = 0;

		try {
			i = Integer.parseInt(compoundTag.getString("version"));
		} catch (RuntimeException var4) {
		}

		return TagHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, compoundTag, i);
	}

	private static float parseFloat(String string) {
		if ("true".equals(string)) {
			return 1.0F;
		} else {
			return "false".equals(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void write() {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			Throwable var2 = null;

			try {
				printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
				printWriter.println("autoJump:" + GameOption.AUTO_JUMP.method_18494(this));
				printWriter.println("autoSuggestions:" + GameOption.AUTO_SUGGESTIONS.method_18494(this));
				printWriter.println("chatColors:" + GameOption.CHAT_COLOR.method_18494(this));
				printWriter.println("chatLinks:" + GameOption.CHAT_LINKS.method_18494(this));
				printWriter.println("chatLinksPrompt:" + GameOption.CHAT_LINKS_PROMPT.method_18494(this));
				printWriter.println("enableVsync:" + GameOption.VSYNC.method_18494(this));
				printWriter.println("entityShadows:" + GameOption.ENTITY_SHADOWS.method_18494(this));
				printWriter.println("forceUnicodeFont:" + GameOption.FORCE_UNICODE_FONT.method_18494(this));
				printWriter.println("invertYMouse:" + GameOption.INVERT_MOUSE.method_18494(this));
				printWriter.println("realmsNotifications:" + GameOption.REALMS_NOTIFICATIONS.method_18494(this));
				printWriter.println("reducedDebugInfo:" + GameOption.REDUCED_DEBUG_INFO.method_18494(this));
				printWriter.println("snooperEnabled:" + GameOption.SNOOPER.method_18494(this));
				printWriter.println("showSubtitles:" + GameOption.SUBTITLES.method_18494(this));
				printWriter.println("touchscreen:" + GameOption.TOUCHSCREEN.method_18494(this));
				printWriter.println("fullscreen:" + GameOption.FULLSCREEN.method_18494(this));
				printWriter.println("bobView:" + GameOption.VIEW_BOBBING.method_18494(this));
				printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
				printWriter.println("fov:" + (this.fov - 70.0) / 40.0);
				printWriter.println("gamma:" + this.gamma);
				printWriter.println("renderDistance:" + this.viewDistance);
				printWriter.println("guiScale:" + this.guiScale);
				printWriter.println("particles:" + this.particles.getId());
				printWriter.println("maxFps:" + this.maxFps);
				printWriter.println("difficulty:" + this.difficulty.getId());
				printWriter.println("fancyGraphics:" + this.fancyGraphics);
				printWriter.println("ao:" + this.ao.getValue());
				printWriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
				switch (this.cloudRenderMode) {
					case field_18164:
						printWriter.println("renderClouds:true");
						break;
					case field_18163:
						printWriter.println("renderClouds:fast");
						break;
					case field_18162:
						printWriter.println("renderClouds:false");
				}

				printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
				printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
				printWriter.println("lastServer:" + this.lastServer);
				printWriter.println("lang:" + this.language);
				printWriter.println("chatVisibility:" + this.chatVisibility.getId());
				printWriter.println("chatOpacity:" + this.chatOpacity);
				printWriter.println("textBackgroundOpacity:" + this.field_18726);
				printWriter.println("backgroundForChatOnly:" + this.field_18725);
				if (this.client.window.getVideoMode().isPresent()) {
					printWriter.println("fullscreenResolution:" + ((VideoMode)this.client.window.getVideoMode().get()).asString());
				}

				printWriter.println("hideServerAddress:" + this.hideServerAddress);
				printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
				printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
				printWriter.println("overrideWidth:" + this.overrideWidth);
				printWriter.println("overrideHeight:" + this.overrideHeight);
				printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
				printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
				printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
				printWriter.println("chatScale:" + this.chatScale);
				printWriter.println("chatWidth:" + this.chatWidth);
				printWriter.println("mipmapLevels:" + this.mipmapLevels);
				printWriter.println("useNativeTransport:" + this.useNativeTransport);
				printWriter.println("mainHand:" + (this.mainHand == AbsoluteHand.field_6182 ? "left" : "right"));
				printWriter.println("attackIndicator:" + this.attackIndicator.getId());
				printWriter.println("narrator:" + this.narrator.getId());
				printWriter.println("tutorialStep:" + this.tutorialStep.getName());
				printWriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
				printWriter.println("glDebugVerbosity:" + this.glDebugVerbosity);

				for (KeyBinding keyBinding : this.keysAll) {
					printWriter.println("key_" + keyBinding.getId() + ":" + keyBinding.getName());
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

	public float getSoundVolume(SoundCategory soundCategory) {
		return this.soundVolumeLevels.containsKey(soundCategory) ? (Float)this.soundVolumeLevels.get(soundCategory) : 1.0F;
	}

	public void setSoundVolume(SoundCategory soundCategory, float f) {
		this.client.getSoundManager().updateSoundVolume(soundCategory, f);
		this.soundVolumeLevels.put(soundCategory, f);
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
				.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainHand));
		}
	}

	public Set<PlayerModelPart> getEnabledPlayerModelParts() {
		return ImmutableSet.copyOf(this.enabledPlayerModelParts);
	}

	public void setPlayerModelPart(PlayerModelPart playerModelPart, boolean bl) {
		if (bl) {
			this.enabledPlayerModelParts.add(playerModelPart);
		} else {
			this.enabledPlayerModelParts.remove(playerModelPart);
		}

		this.onPlayerModelPartChange();
	}

	public void togglePlayerModelPart(PlayerModelPart playerModelPart) {
		if (this.getEnabledPlayerModelParts().contains(playerModelPart)) {
			this.enabledPlayerModelParts.remove(playerModelPart);
		} else {
			this.enabledPlayerModelParts.add(playerModelPart);
		}

		this.onPlayerModelPartChange();
	}

	public CloudRenderMode getCloudRenderMode() {
		return this.viewDistance >= 4 ? this.cloudRenderMode : CloudRenderMode.field_18162;
	}

	public boolean shouldUseNativeTransport() {
		return this.useNativeTransport;
	}

	public void addResourcePackContainersToManager(ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager) {
		resourcePackContainerManager.callCreators();
		Set<ClientResourcePackContainer> set = Sets.<ClientResourcePackContainer>newLinkedHashSet();
		Iterator<String> iterator = this.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			ClientResourcePackContainer clientResourcePackContainer = resourcePackContainerManager.getContainer(string);
			if (clientResourcePackContainer == null && !string.startsWith("file/")) {
				clientResourcePackContainer = resourcePackContainerManager.getContainer("file/" + string);
			}

			if (clientResourcePackContainer == null) {
				LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string);
				iterator.remove();
			} else if (!clientResourcePackContainer.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
				LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", string);
				iterator.remove();
			} else if (clientResourcePackContainer.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
				LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", string);
				this.incompatibleResourcePacks.remove(string);
			} else {
				set.add(clientResourcePackContainer);
			}
		}

		resourcePackContainerManager.setEnabled(set);
	}
}
