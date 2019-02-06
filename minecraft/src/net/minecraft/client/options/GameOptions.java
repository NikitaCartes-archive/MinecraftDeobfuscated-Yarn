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
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.VideoMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;
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
	private static final String[] PARTICLE_SETTINGS = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] AO_SETTINGS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	private static final String[] GRAPHICS_SETTINGS = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
	private static final String[] ATTACK_INDICATOR_SETTINGS = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
	public static final String[] NARRATOR_SETTINGS = new String[]{
		"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"
	};
	public double mouseSensitivity = 0.5;
	public boolean invertYMouse;
	public int viewDistance = -1;
	public boolean bobView = true;
	public int maxFps = 120;
	public int cloudRenderMode = 2;
	public boolean fancyGraphics = true;
	public int ao = 2;
	public List<String> resourcePacks = Lists.<String>newArrayList();
	public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
	public PlayerEntity.ChatVisibility chatVisibility = PlayerEntity.ChatVisibility.FULL;
	public boolean chatColors = true;
	public boolean chatLinks = true;
	public boolean chatLinksPrompt = true;
	public double chatOpacity = 1.0;
	public boolean snooperEnabled = true;
	public boolean fullscreen;
	@Nullable
	public String fullscreenResolution;
	public boolean enableVsync = true;
	public boolean reducedDebugInfo;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> enabledPlayerModelParts = Sets.<PlayerModelPart>newHashSet(PlayerModelPart.values());
	public boolean touchscreen;
	public OptionMainHand mainHand = OptionMainHand.field_6183;
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
	public boolean entityShadows = true;
	public int attackIndicator = 1;
	public boolean enableWeakAttacks;
	public boolean showSubtitles;
	public boolean realmsNotifications = true;
	public boolean autoJump = true;
	public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
	public boolean autoSuggestions = true;
	public int biomeBlendRadius = 2;
	public double mouseWheelSensitivity = 1.0;
	public int glDebugVerbosity = 1;
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
	public int field_1850;
	public boolean debugEnabled;
	public boolean debugProfilerEnabled;
	public boolean debugTpsEnabled;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	public boolean field_1821;
	public double fov = 70.0;
	public double gamma;
	public float saturation;
	public int guiScale;
	public int particles;
	public int narrator;
	public String language = "en_us";
	public boolean forceUnicodeFont;

	public GameOptions(MinecraftClient minecraftClient, File file) {
		this.client = minecraftClient;
		this.optionsFile = new File(file, "options.txt");
		if (minecraftClient.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			GameOptions.Option.RENDER_DISTANCE.setMaximumValue(32.0F);
		} else {
			GameOptions.Option.RENDER_DISTANCE.setMaximumValue(16.0F);
		}

		this.viewDistance = minecraftClient.is64Bit() ? 12 : 8;
		this.load();
	}

	public GameOptions() {
	}

	public void method_1641(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
		keyBinding.setKeyCode(keyCode);
		this.write();
	}

	public void method_1625(GameOptions.Option option, double d) {
		if (option == GameOptions.Option.SENSITIVITY) {
			this.mouseSensitivity = d;
		}

		if (option == GameOptions.Option.FOV) {
			this.fov = d;
		}

		if (option == GameOptions.Option.GAMMA) {
			this.gamma = d;
		}

		if (option == GameOptions.Option.FRAMERATE_LIMIT) {
			this.maxFps = (int)d;
			this.client.window.setFramerateLimit(this.maxFps);
		}

		if (option == GameOptions.Option.CHAT_OPACITY) {
			this.chatOpacity = d;
			this.client.inGameHud.getHudChat().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			this.chatHeightFocused = d;
			this.client.inGameHud.getHudChat().reset();
		}

		if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			this.chatHeightUnfocused = d;
			this.client.inGameHud.getHudChat().reset();
		}

		if (option == GameOptions.Option.CHAT_WIDTH) {
			this.chatWidth = d;
			this.client.inGameHud.getHudChat().reset();
		}

		if (option == GameOptions.Option.CHAT_SCALE) {
			this.chatScale = d;
			this.client.inGameHud.getHudChat().reset();
		}

		if (option == GameOptions.Option.MIPMAP_LEVELS) {
			int i = this.mipmapLevels;
			this.mipmapLevels = (int)d;
			if ((double)i != d) {
				this.client.getSpriteAtlas().setMipLevel(this.mipmapLevels);
				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.client.getSpriteAtlas().setFilter(false, this.mipmapLevels > 0);
				this.client.reloadResourcesConcurrently();
			}
		}

		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.viewDistance = (int)d;
			this.client.worldRenderer.method_3292();
		}

		if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
			this.biomeBlendRadius = MathHelper.clamp((int)d, 0, 7);
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
			this.client.window.method_4505((int)d);
		}

		if (option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY) {
			this.mouseWheelSensitivity = d;
		}
	}

	public void updateOption(GameOptions.Option option, int i) {
		if (option == GameOptions.Option.RENDER_DISTANCE) {
			this.method_1625(option, MathHelper.clamp((double)(this.viewDistance + i), option.getMinimumValue(), option.getMaximumValue()));
		}

		if (option == GameOptions.Option.MAIN_HAND) {
			this.mainHand = this.mainHand.getOpposite();
		}

		if (option == GameOptions.Option.INVERT_MOUSE) {
			this.invertYMouse = !this.invertYMouse;
		}

		if (option == GameOptions.Option.GUI_SCALE) {
			this.guiScale = Integer.remainderUnsigned(this.guiScale + i, this.client.window.calculateScaleFactor(0, this.client.forcesUnicodeFont()) + 1);
		}

		if (option == GameOptions.Option.PARTICLES) {
			this.particles = (this.particles + i) % 3;
		}

		if (option == GameOptions.Option.VIEW_BOBBING) {
			this.bobView = !this.bobView;
		}

		if (option == GameOptions.Option.RENDER_CLOUDS) {
			this.cloudRenderMode = (this.cloudRenderMode + i) % 3;
		}

		if (option == GameOptions.Option.FORCE_UNICODE_FONT) {
			this.forceUnicodeFont = !this.forceUnicodeFont;
			this.client.getFontManager().setForceUnicodeFont(this.forceUnicodeFont);
		}

		if (option == GameOptions.Option.GRAPHICS) {
			this.fancyGraphics = !this.fancyGraphics;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.AO) {
			this.ao = (this.ao + i) % 3;
			this.client.worldRenderer.reload();
		}

		if (option == GameOptions.Option.VISIBILITY) {
			this.chatVisibility = PlayerEntity.ChatVisibility.byId((this.chatVisibility.getId() + i) % 3);
		}

		if (option == GameOptions.Option.CHAT_COLOR) {
			this.chatColors = !this.chatColors;
		}

		if (option == GameOptions.Option.CHAT_LINKS) {
			this.chatLinks = !this.chatLinks;
		}

		if (option == GameOptions.Option.CHAT_LINKS_PROMPT) {
			this.chatLinksPrompt = !this.chatLinksPrompt;
		}

		if (option == GameOptions.Option.SNOOPER) {
			this.snooperEnabled = !this.snooperEnabled;
		}

		if (option == GameOptions.Option.TOUCHSCREEN) {
			this.touchscreen = !this.touchscreen;
		}

		if (option == GameOptions.Option.FULLSCREEN) {
			this.fullscreen = !this.fullscreen;
			if (this.client.window.isFullscreen() != this.fullscreen) {
				this.client.window.toggleFullscreen();
				this.fullscreen = this.client.window.isFullscreen();
			}
		}

		if (option == GameOptions.Option.VSYNC) {
			this.enableVsync = !this.enableVsync;
			this.client.window.setVsync(this.enableVsync);
		}

		if (option == GameOptions.Option.REDUCED_DEBUG_INFO) {
			this.reducedDebugInfo = !this.reducedDebugInfo;
		}

		if (option == GameOptions.Option.ENTITY_SHADOWS) {
			this.entityShadows = !this.entityShadows;
		}

		if (option == GameOptions.Option.ATTACK_INDICATOR) {
			this.attackIndicator = (this.attackIndicator + i) % 3;
		}

		if (option == GameOptions.Option.SHOW_SUBTITLES) {
			this.showSubtitles = !this.showSubtitles;
		}

		if (option == GameOptions.Option.REALMS_NOTIFICATIONS) {
			this.realmsNotifications = !this.realmsNotifications;
		}

		if (option == GameOptions.Option.AUTO_JUMP) {
			this.autoJump = !this.autoJump;
		}

		if (option == GameOptions.Option.AUTO_SUGGEST_COMMANDS) {
			this.autoSuggestions = !this.autoSuggestions;
		}

		if (option == GameOptions.Option.NARRATOR) {
			if (NarratorManager.INSTANCE.isActive()) {
				this.narrator = (this.narrator + i) % NARRATOR_SETTINGS.length;
			} else {
				this.narrator = 0;
			}

			NarratorManager.INSTANCE.addToast(this.narrator);
		}

		this.write();
	}

	public double method_1637(GameOptions.Option option) {
		if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
			return (double)this.biomeBlendRadius;
		} else if (option == GameOptions.Option.FOV) {
			return this.fov;
		} else if (option == GameOptions.Option.GAMMA) {
			return this.gamma;
		} else if (option == GameOptions.Option.SATURATION) {
			return (double)this.saturation;
		} else if (option == GameOptions.Option.SENSITIVITY) {
			return this.mouseSensitivity;
		} else if (option == GameOptions.Option.CHAT_OPACITY) {
			return this.chatOpacity;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
			return this.chatHeightFocused;
		} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
			return this.chatHeightUnfocused;
		} else if (option == GameOptions.Option.CHAT_SCALE) {
			return this.chatScale;
		} else if (option == GameOptions.Option.CHAT_WIDTH) {
			return this.chatWidth;
		} else if (option == GameOptions.Option.FRAMERATE_LIMIT) {
			return (double)this.maxFps;
		} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
			return (double)this.mipmapLevels;
		} else if (option == GameOptions.Option.RENDER_DISTANCE) {
			return (double)this.viewDistance;
		} else if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
			return (double)this.client.window.method_4508();
		} else {
			return option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY ? this.mouseWheelSensitivity : 0.0;
		}
	}

	public boolean isEnabled(GameOptions.Option option) {
		switch (option) {
			case INVERT_MOUSE:
				return this.invertYMouse;
			case VIEW_BOBBING:
				return this.bobView;
			case CHAT_COLOR:
				return this.chatColors;
			case CHAT_LINKS:
				return this.chatLinks;
			case CHAT_LINKS_PROMPT:
				return this.chatLinksPrompt;
			case SNOOPER:
				if (this.snooperEnabled) {
				}

				return false;
			case FULLSCREEN:
				return this.fullscreen;
			case VSYNC:
				return this.enableVsync;
			case TOUCHSCREEN:
				return this.touchscreen;
			case FORCE_UNICODE_FONT:
				return this.forceUnicodeFont;
			case REDUCED_DEBUG_INFO:
				return this.reducedDebugInfo;
			case ENTITY_SHADOWS:
				return this.entityShadows;
			case SHOW_SUBTITLES:
				return this.showSubtitles;
			case REALMS_NOTIFICATIONS:
				return this.realmsNotifications;
			case ENABLE_WEAK_ATTACK:
				return this.enableWeakAttacks;
			case AUTO_JUMP:
				return this.autoJump;
			case AUTO_SUGGEST_COMMANDS:
				return this.autoSuggestions;
			default:
				return false;
		}
	}

	private static String method_1638(String[] strings, int i) {
		if (i < 0 || i >= strings.length) {
			i = 0;
		}

		return I18n.translate(strings[i]);
	}

	public String getTranslatedName(GameOptions.Option option) {
		String string = I18n.translate(option.getTranslationKey()) + ": ";
		if (option.isSlider()) {
			double d = this.method_1637(option);
			double e = option.method_1651(d);
			if (option == GameOptions.Option.SENSITIVITY) {
				if (e == 0.0) {
					return string + I18n.translate("options.sensitivity.min");
				} else {
					return e == 1.0 ? string + I18n.translate("options.sensitivity.max") : string + (int)(e * 200.0) + "%";
				}
			} else if (option == GameOptions.Option.BIOME_BLEND_RADIUS) {
				if (e == 0.0) {
					return string + I18n.translate("options.off");
				} else {
					int i = this.biomeBlendRadius * 2 + 1;
					return string + i + "x" + i;
				}
			} else if (option == GameOptions.Option.FOV) {
				if (d == 70.0) {
					return string + I18n.translate("options.fov.min");
				} else {
					return d == 110.0 ? string + I18n.translate("options.fov.max") : string + (int)d;
				}
			} else if (option == GameOptions.Option.FRAMERATE_LIMIT) {
				return d == option.max ? string + I18n.translate("options.framerateLimit.max") : string + I18n.translate("options.framerate", (int)d);
			} else if (option == GameOptions.Option.RENDER_CLOUDS) {
				return d == option.min ? string + I18n.translate("options.cloudHeight.min") : string + ((int)d + 128);
			} else if (option == GameOptions.Option.GAMMA) {
				if (e == 0.0) {
					return string + I18n.translate("options.gamma.min");
				} else {
					return e == 1.0 ? string + I18n.translate("options.gamma.max") : string + "+" + (int)(e * 100.0) + "%";
				}
			} else if (option == GameOptions.Option.SATURATION) {
				return string + (int)(e * 400.0) + "%";
			} else if (option == GameOptions.Option.CHAT_OPACITY) {
				return string + (int)(e * 90.0 + 10.0) + "%";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
				return string + ChatHud.method_1818(e) + "px";
			} else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
				return string + ChatHud.method_1818(e) + "px";
			} else if (option == GameOptions.Option.CHAT_WIDTH) {
				return string + ChatHud.method_1806(e) + "px";
			} else if (option == GameOptions.Option.RENDER_DISTANCE) {
				return string + I18n.translate("options.chunks", (int)d);
			} else if (option == GameOptions.Option.MOUSE_WHEEL_SENSITIVITY) {
				return e == 1.0 ? string + I18n.translate("options.mouseWheelSensitivity.default") : string + "+" + (int)e + "." + (int)(e * 10.0) % 10;
			} else if (option == GameOptions.Option.MIPMAP_LEVELS) {
				return d == 0.0 ? string + I18n.translate("options.off") : string + (int)d;
			} else if (option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
				return d == 0.0 ? string + I18n.translate("options.fullscreen.current") : string + this.client.window.method_4487((int)d - 1);
			} else {
				return e == 0.0 ? string + I18n.translate("options.off") : string + (int)(e * 100.0) + "%";
			}
		} else if (option.isToggle()) {
			boolean bl = this.isEnabled(option);
			return bl ? string + I18n.translate("options.on") : string + I18n.translate("options.off");
		} else if (option == GameOptions.Option.MAIN_HAND) {
			return string + this.mainHand;
		} else if (option == GameOptions.Option.GUI_SCALE) {
			return string + (this.guiScale == 0 ? I18n.translate("options.guiScale.auto") : this.guiScale);
		} else if (option == GameOptions.Option.VISIBILITY) {
			return string + I18n.translate(this.chatVisibility.getTranslationKey());
		} else if (option == GameOptions.Option.PARTICLES) {
			return string + method_1638(PARTICLE_SETTINGS, this.particles);
		} else if (option == GameOptions.Option.AO) {
			return string + method_1638(AO_SETTINGS, this.ao);
		} else if (option == GameOptions.Option.RENDER_CLOUDS) {
			return string + method_1638(GRAPHICS_SETTINGS, this.cloudRenderMode);
		} else if (option == GameOptions.Option.GRAPHICS) {
			if (this.fancyGraphics) {
				return string + I18n.translate("options.graphics.fancy");
			} else {
				String string2 = "options.graphics.fast";
				return string + I18n.translate("options.graphics.fast");
			}
		} else if (option == GameOptions.Option.ATTACK_INDICATOR) {
			return string + method_1638(ATTACK_INDICATOR_SETTINGS, this.attackIndicator);
		} else if (option == GameOptions.Option.NARRATOR) {
			return NarratorManager.INSTANCE.isActive()
				? string + method_1638(NARRATOR_SETTINGS, this.narrator)
				: string + I18n.translate("options.narrator.notavailable");
		} else {
			return string;
		}
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
					if ("mouseSensitivity".equals(string)) {
						this.mouseSensitivity = (double)this.parseFloat(string2);
					}

					if ("fov".equals(string)) {
						this.fov = (double)(this.parseFloat(string2) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string)) {
						this.gamma = (double)this.parseFloat(string2);
					}

					if ("saturation".equals(string)) {
						this.saturation = this.parseFloat(string2);
					}

					if ("invertYMouse".equals(string)) {
						this.invertYMouse = "true".equals(string2);
					}

					if ("renderDistance".equals(string)) {
						this.viewDistance = Integer.parseInt(string2);
					}

					if ("guiScale".equals(string)) {
						this.guiScale = Integer.parseInt(string2);
					}

					if ("particles".equals(string)) {
						this.particles = Integer.parseInt(string2);
					}

					if ("bobView".equals(string)) {
						this.bobView = "true".equals(string2);
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
							this.ao = 2;
						} else if ("false".equals(string2)) {
							this.ao = 0;
						} else {
							this.ao = Integer.parseInt(string2);
						}
					}

					if ("renderClouds".equals(string)) {
						if ("true".equals(string2)) {
							this.cloudRenderMode = 2;
						} else if ("false".equals(string2)) {
							this.cloudRenderMode = 0;
						} else if ("fast".equals(string2)) {
							this.cloudRenderMode = 1;
						}
					}

					if ("attackIndicator".equals(string)) {
						if ("0".equals(string2)) {
							this.attackIndicator = 0;
						} else if ("1".equals(string2)) {
							this.attackIndicator = 1;
						} else if ("2".equals(string2)) {
							this.attackIndicator = 2;
						}
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
						this.chatVisibility = PlayerEntity.ChatVisibility.byId(Integer.parseInt(string2));
					}

					if ("chatColors".equals(string)) {
						this.chatColors = "true".equals(string2);
					}

					if ("chatLinks".equals(string)) {
						this.chatLinks = "true".equals(string2);
					}

					if ("chatLinksPrompt".equals(string)) {
						this.chatLinksPrompt = "true".equals(string2);
					}

					if ("chatOpacity".equals(string)) {
						this.chatOpacity = (double)this.parseFloat(string2);
					}

					if ("snooperEnabled".equals(string)) {
						this.snooperEnabled = "true".equals(string2);
					}

					if ("fullscreen".equals(string)) {
						this.fullscreen = "true".equals(string2);
					}

					if ("fullscreenResolution".equals(string)) {
						this.fullscreenResolution = string2;
					}

					if ("enableVsync".equals(string)) {
						this.enableVsync = "true".equals(string2);
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

					if ("touchscreen".equals(string)) {
						this.touchscreen = "true".equals(string2);
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
						this.chatHeightFocused = (double)this.parseFloat(string2);
					}

					if ("chatHeightUnfocused".equals(string)) {
						this.chatHeightUnfocused = (double)this.parseFloat(string2);
					}

					if ("chatScale".equals(string)) {
						this.chatScale = (double)this.parseFloat(string2);
					}

					if ("chatWidth".equals(string)) {
						this.chatWidth = (double)this.parseFloat(string2);
					}

					if ("mipmapLevels".equals(string)) {
						this.mipmapLevels = Integer.parseInt(string2);
					}

					if ("forceUnicodeFont".equals(string)) {
						this.forceUnicodeFont = "true".equals(string2);
					}

					if ("reducedDebugInfo".equals(string)) {
						this.reducedDebugInfo = "true".equals(string2);
					}

					if ("useNativeTransport".equals(string)) {
						this.useNativeTransport = "true".equals(string2);
					}

					if ("entityShadows".equals(string)) {
						this.entityShadows = "true".equals(string2);
					}

					if ("mainHand".equals(string)) {
						this.mainHand = "left".equals(string2) ? OptionMainHand.field_6182 : OptionMainHand.field_6183;
					}

					if ("showSubtitles".equals(string)) {
						this.showSubtitles = "true".equals(string2);
					}

					if ("realmsNotifications".equals(string)) {
						this.realmsNotifications = "true".equals(string2);
					}

					if ("enableWeakAttacks".equals(string)) {
						this.enableWeakAttacks = "true".equals(string2);
					}

					if ("autoJump".equals(string)) {
						this.autoJump = "true".equals(string2);
					}

					if ("narrator".equals(string)) {
						this.narrator = Integer.parseInt(string2);
					}

					if ("autoSuggestions".equals(string)) {
						this.autoSuggestions = "true".equals(string2);
					}

					if ("biomeBlendRadius".equals(string)) {
						this.biomeBlendRadius = Integer.parseInt(string2);
					}

					if ("mouseWheelSensitivity".equals(string)) {
						this.mouseWheelSensitivity = (double)this.parseFloat(string2);
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
							this.soundVolumeLevels.put(soundCategory, this.parseFloat(string2));
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

	private float parseFloat(String string) {
		if ("true".equals(string)) {
			return 1.0F;
		} else {
			return "false".equals(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void write() {
		PrintWriter printWriter = null;

		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
			printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
			printWriter.println("invertYMouse:" + this.invertYMouse);
			printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
			printWriter.println("fov:" + (this.fov - 70.0) / 40.0);
			printWriter.println("gamma:" + this.gamma);
			printWriter.println("saturation:" + this.saturation);
			printWriter.println("renderDistance:" + this.viewDistance);
			printWriter.println("guiScale:" + this.guiScale);
			printWriter.println("particles:" + this.particles);
			printWriter.println("bobView:" + this.bobView);
			printWriter.println("maxFps:" + this.maxFps);
			printWriter.println("difficulty:" + this.difficulty.getId());
			printWriter.println("fancyGraphics:" + this.fancyGraphics);
			printWriter.println("ao:" + this.ao);
			printWriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
			switch (this.cloudRenderMode) {
				case 0:
					printWriter.println("renderClouds:false");
					break;
				case 1:
					printWriter.println("renderClouds:fast");
					break;
				case 2:
					printWriter.println("renderClouds:true");
			}

			printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
			printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
			printWriter.println("lastServer:" + this.lastServer);
			printWriter.println("lang:" + this.language);
			printWriter.println("chatVisibility:" + this.chatVisibility.getId());
			printWriter.println("chatColors:" + this.chatColors);
			printWriter.println("chatLinks:" + this.chatLinks);
			printWriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
			printWriter.println("chatOpacity:" + this.chatOpacity);
			printWriter.println("snooperEnabled:" + this.snooperEnabled);
			printWriter.println("fullscreen:" + this.fullscreen);
			if (this.client.window.getVideoMode().isPresent()) {
				printWriter.println("fullscreenResolution:" + ((VideoMode)this.client.window.getVideoMode().get()).asString());
			}

			printWriter.println("enableVsync:" + this.enableVsync);
			printWriter.println("hideServerAddress:" + this.hideServerAddress);
			printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
			printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
			printWriter.println("touchscreen:" + this.touchscreen);
			printWriter.println("overrideWidth:" + this.overrideWidth);
			printWriter.println("overrideHeight:" + this.overrideHeight);
			printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
			printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
			printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
			printWriter.println("chatScale:" + this.chatScale);
			printWriter.println("chatWidth:" + this.chatWidth);
			printWriter.println("mipmapLevels:" + this.mipmapLevels);
			printWriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
			printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
			printWriter.println("useNativeTransport:" + this.useNativeTransport);
			printWriter.println("entityShadows:" + this.entityShadows);
			printWriter.println("mainHand:" + (this.mainHand == OptionMainHand.field_6182 ? "left" : "right"));
			printWriter.println("attackIndicator:" + this.attackIndicator);
			printWriter.println("showSubtitles:" + this.showSubtitles);
			printWriter.println("realmsNotifications:" + this.realmsNotifications);
			printWriter.println("enableWeakAttacks:" + this.enableWeakAttacks);
			printWriter.println("autoJump:" + this.autoJump);
			printWriter.println("narrator:" + this.narrator);
			printWriter.println("tutorialStep:" + this.tutorialStep.getName());
			printWriter.println("autoSuggestions:" + this.autoSuggestions);
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
		} catch (Exception var9) {
			LOGGER.error("Failed to save options", (Throwable)var9);
		} finally {
			IOUtils.closeQuietly(printWriter);
		}

		this.onPlayerModelPartChange();
	}

	public float getSoundVolume(SoundCategory soundCategory) {
		return this.soundVolumeLevels.containsKey(soundCategory) ? (Float)this.soundVolumeLevels.get(soundCategory) : 1.0F;
	}

	public void setSoundVolume(SoundCategory soundCategory, float f) {
		this.client.getSoundLoader().updateSoundVolume(soundCategory, f);
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

	public int getCloudRenderMode() {
		return this.viewDistance >= 4 ? this.cloudRenderMode : 0;
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

	@Environment(EnvType.CLIENT)
	public static enum Option {
		INVERT_MOUSE("options.invertMouse", false, true),
		SENSITIVITY("options.sensitivity", true, false),
		FOV("options.fov", true, false, 30.0, 110.0, 1.0F),
		GAMMA("options.gamma", true, false),
		SATURATION("options.saturation", true, false),
		RENDER_DISTANCE("options.renderDistance", true, false, 2.0, 16.0, 1.0F),
		VIEW_BOBBING("options.viewBobbing", false, true),
		FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0, 260.0, 10.0F),
		RENDER_CLOUDS("options.renderClouds", false, false),
		GRAPHICS("options.graphics", false, false),
		AO("options.ao", false, false),
		GUI_SCALE("options.guiScale", false, false),
		PARTICLES("options.particles", false, false),
		VISIBILITY("options.chat.visibility", false, false),
		CHAT_COLOR("options.chat.color", false, true),
		CHAT_LINKS("options.chat.links", false, true),
		CHAT_OPACITY("options.chat.opacity", true, false),
		CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
		SNOOPER("options.snooper", false, true),
		FULLSCREEN_RESOLUTION("options.fullscreen.resolution", true, false, 0.0, 0.0, 1.0F),
		FULLSCREEN("options.fullscreen", false, true),
		VSYNC("options.vsync", false, true),
		TOUCHSCREEN("options.touchscreen", false, true),
		CHAT_SCALE("options.chat.scale", true, false),
		CHAT_WIDTH("options.chat.width", true, false),
		CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
		CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
		MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0, 4.0, 1.0F),
		FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
		REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
		ENTITY_SHADOWS("options.entityShadows", false, true),
		MAIN_HAND("options.mainHand", false, false),
		ATTACK_INDICATOR("options.attackIndicator", false, false),
		ENABLE_WEAK_ATTACK("options.enableWeakAttacks", false, true),
		SHOW_SUBTITLES("options.showSubtitles", false, true),
		REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
		AUTO_JUMP("options.autoJump", false, true),
		NARRATOR("options.narrator", false, false),
		AUTO_SUGGEST_COMMANDS("options.autoSuggestCommands", false, true),
		BIOME_BLEND_RADIUS("options.biomeBlendRadius", true, false, 0.0, 7.0, 1.0F),
		MOUSE_WHEEL_SENSITIVITY("options.mouseWheelSensitivity", true, false, 1.0, 10.0, 0.5F);

		private final boolean slider;
		private final boolean toggle;
		private final String key;
		private final float step;
		private double min;
		private double max;

		public static GameOptions.Option byId(int i) {
			for (GameOptions.Option option : values()) {
				if (option.getId() == i) {
					return option;
				}
			}

			return null;
		}

		private Option(String string2, boolean bl, boolean bl2) {
			this(string2, bl, bl2, 0.0, 1.0, 0.0F);
		}

		private Option(String string2, boolean bl, boolean bl2, double d, double e, float f) {
			this.key = string2;
			this.slider = bl;
			this.toggle = bl2;
			this.min = d;
			this.max = e;
			this.step = f;
		}

		public boolean isSlider() {
			return this.slider;
		}

		public boolean isToggle() {
			return this.toggle;
		}

		public int getId() {
			return this.ordinal();
		}

		public String getTranslationKey() {
			return this.key;
		}

		public double getMinimumValue() {
			return this.min;
		}

		public double getMaximumValue() {
			return this.max;
		}

		public void setMaximumValue(float f) {
			this.max = (double)f;
		}

		public double method_1651(double d) {
			return MathHelper.clamp((this.method_1657(d) - this.min) / (this.max - this.min), 0.0, 1.0);
		}

		public double method_1645(double d) {
			return this.method_1657(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.min, this.max));
		}

		public double method_1657(double d) {
			d = this.roundToStep(d);
			return MathHelper.clamp(d, this.min, this.max);
		}

		private double roundToStep(double d) {
			if (this.step > 0.0F) {
				d = (double)(this.step * (float)Math.round(d / (double)this.step));
			}

			return d;
		}
	}
}
