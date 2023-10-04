package net.minecraft.client.option;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameOptions {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Gson GSON = new Gson();
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
	public static final String EMPTY_STRING = "";
	private static final Text DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP = Text.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
	private final SimpleOption<Boolean> monochromeLogo = SimpleOption.ofBoolean(
		"options.darkMojangStudiosBackgroundColor", SimpleOption.constantTooltip(DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP), false
	);
	private static final Text HIDE_LIGHTNING_FLASHES_TOOLTIP = Text.translatable("options.hideLightningFlashes.tooltip");
	private final SimpleOption<Boolean> hideLightningFlashes = SimpleOption.ofBoolean(
		"options.hideLightningFlashes", SimpleOption.constantTooltip(HIDE_LIGHTNING_FLASHES_TOOLTIP), false
	);
	private final SimpleOption<Double> mouseSensitivity = new SimpleOption<>("options.sensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> {
		if (value == 0.0) {
			return getGenericValueText(optionText, Text.translatable("options.sensitivity.min"));
		} else {
			return value == 1.0 ? getGenericValueText(optionText, Text.translatable("options.sensitivity.max")) : getPercentValueText(optionText, 2.0 * value);
		}
	}, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {
	});
	private final SimpleOption<Integer> viewDistance;
	private final SimpleOption<Integer> simulationDistance;
	private int serverViewDistance = 0;
	private final SimpleOption<Double> entityDistanceScaling = new SimpleOption<>(
		"options.entityDistanceScaling",
		SimpleOption.emptyTooltip(),
		GameOptions::getPercentValueText,
		new SimpleOption.ValidatingIntSliderCallbacks(2, 20).withModifier(sliderProgressValue -> (double)sliderProgressValue / 4.0, value -> (int)(value * 4.0)),
		Codec.doubleRange(0.5, 5.0),
		1.0,
		value -> {
		}
	);
	public static final int MAX_FRAMERATE = 260;
	private final SimpleOption<Integer> maxFps = new SimpleOption<>(
		"options.framerateLimit",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> value == 260
				? getGenericValueText(optionText, Text.translatable("options.framerateLimit.max"))
				: getGenericValueText(optionText, Text.translatable("options.framerate", value)),
		new SimpleOption.ValidatingIntSliderCallbacks(1, 26).withModifier(value -> value * 10, value -> value / 10),
		Codec.intRange(10, 260),
		120,
		value -> MinecraftClient.getInstance().getWindow().setFramerateLimit(value)
	);
	private final SimpleOption<CloudRenderMode> cloudRenderMode = new SimpleOption<>(
		"options.renderClouds",
		SimpleOption.emptyTooltip(),
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(
			Arrays.asList(CloudRenderMode.values()), Codecs.either(CloudRenderMode.CODEC, Codec.BOOL, value -> value ? CloudRenderMode.FANCY : CloudRenderMode.OFF)
		),
		CloudRenderMode.FANCY,
		cloudRenderMode -> {
			if (MinecraftClient.isFabulousGraphicsOrBetter()) {
				Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
				if (framebuffer != null) {
					framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
				}
			}
		}
	);
	private static final Text FAST_GRAPHICS_TOOLTIP = Text.translatable("options.graphics.fast.tooltip");
	private static final Text FABULOUS_GRAPHICS_TOOLTIP = Text.translatable(
		"options.graphics.fabulous.tooltip", Text.translatable("options.graphics.fabulous").formatted(Formatting.ITALIC)
	);
	private static final Text FANCY_GRAPHICS_TOOLTIP = Text.translatable("options.graphics.fancy.tooltip");
	private final SimpleOption<GraphicsMode> graphicsMode = new SimpleOption<>(
		"options.graphics",
		value -> {
			return switch (value) {
				case FANCY -> Tooltip.of(FANCY_GRAPHICS_TOOLTIP);
				case FAST -> Tooltip.of(FAST_GRAPHICS_TOOLTIP);
				case FABULOUS -> Tooltip.of(FABULOUS_GRAPHICS_TOOLTIP);
			};
		},
		(optionText, value) -> {
			MutableText mutableText = Text.translatable(value.getTranslationKey());
			return value == GraphicsMode.FABULOUS ? mutableText.formatted(Formatting.ITALIC) : mutableText;
		},
		new SimpleOption.AlternateValuesSupportingCyclingCallbacks<>(
			Arrays.asList(GraphicsMode.values()),
			(List<GraphicsMode>)Stream.of(GraphicsMode.values()).filter(graphicsMode -> graphicsMode != GraphicsMode.FABULOUS).collect(Collectors.toList()),
			() -> MinecraftClient.getInstance().isRunning() && MinecraftClient.getInstance().getVideoWarningManager().hasCancelledAfterWarning(),
			(option, graphicsMode) -> {
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
				if (graphicsMode == GraphicsMode.FABULOUS && videoWarningManager.canWarn()) {
					videoWarningManager.scheduleWarning();
				} else {
					option.setValue(graphicsMode);
					minecraftClient.worldRenderer.reload();
				}
			},
			Codec.INT.xmap(GraphicsMode::byId, GraphicsMode::getId)
		),
		GraphicsMode.FANCY,
		value -> {
		}
	);
	private final SimpleOption<Boolean> ao = SimpleOption.ofBoolean("options.ao", true, value -> MinecraftClient.getInstance().worldRenderer.reload());
	private static final Text NONE_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.none.tooltip");
	private static final Text BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
	private static final Text NEARBY_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
	private final SimpleOption<ChunkBuilderMode> chunkBuilderMode = new SimpleOption<>(
		"options.prioritizeChunkUpdates",
		value -> {
			return switch (value) {
				case NONE -> Tooltip.of(NONE_CHUNK_BUILDER_MODE_TOOLTIP);
				case PLAYER_AFFECTED -> Tooltip.of(BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP);
				case NEARBY -> Tooltip.of(NEARBY_CHUNK_BUILDER_MODE_TOOLTIP);
			};
		},
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ChunkBuilderMode.values()), Codec.INT.xmap(ChunkBuilderMode::get, ChunkBuilderMode::getId)),
		ChunkBuilderMode.NONE,
		value -> {
		}
	);
	public List<String> resourcePacks = Lists.<String>newArrayList();
	public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
	private final SimpleOption<ChatVisibility> chatVisibility = new SimpleOption<>(
		"options.chat.visibility",
		SimpleOption.emptyTooltip(),
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ChatVisibility.values()), Codec.INT.xmap(ChatVisibility::byId, ChatVisibility::getId)),
		ChatVisibility.FULL,
		value -> {
		}
	);
	private final SimpleOption<Double> chatOpacity = new SimpleOption<>(
		"options.chat.opacity",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> getPercentValueText(optionText, value * 0.9 + 0.1),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> chatLineSpacing = new SimpleOption<>(
		"options.chat.line_spacing", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.0, value -> {
		}
	);
	private final SimpleOption<Double> textBackgroundOpacity = new SimpleOption<>(
		"options.accessibility.text_background_opacity",
		SimpleOption.emptyTooltip(),
		GameOptions::getPercentValueText,
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		0.5,
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> panoramaSpeed = new SimpleOption<>(
		"options.accessibility.panorama_speed",
		SimpleOption.emptyTooltip(),
		GameOptions::getPercentValueText,
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> {
		}
	);
	private static final Text HIGH_CONTRAST_TOOLTIP = Text.translatable("options.accessibility.high_contrast.tooltip");
	private final SimpleOption<Boolean> highContrast = SimpleOption.ofBoolean(
		"options.accessibility.high_contrast", SimpleOption.constantTooltip(HIGH_CONTRAST_TOOLTIP), false, value -> {
			ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
			boolean blx = resourcePackManager.getEnabledNames().contains("high_contrast");
			if (!blx && value) {
				if (resourcePackManager.enable("high_contrast")) {
					this.refreshResourcePacks(resourcePackManager);
				}
			} else if (blx && !value && resourcePackManager.disable("high_contrast")) {
				this.refreshResourcePacks(resourcePackManager);
			}
		}
	);
	private final SimpleOption<Boolean> narratorHotkey = SimpleOption.ofBoolean(
		"options.accessibility.narrator_hotkey", SimpleOption.constantTooltip(Text.translatable("options.accessibility.narrator_hotkey.tooltip")), true
	);
	@Nullable
	public String fullscreenResolution;
	public boolean hideServerAddress;
	public boolean advancedItemTooltips;
	public boolean pauseOnLostFocus = true;
	private final Set<PlayerModelPart> enabledPlayerModelParts = EnumSet.allOf(PlayerModelPart.class);
	private final SimpleOption<Arm> mainArm = new SimpleOption<>(
		"options.mainHand",
		SimpleOption.emptyTooltip(),
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(Arm.values()), Arm.CODEC),
		Arm.RIGHT,
		value -> this.sendClientSettings()
	);
	public int overrideWidth;
	public int overrideHeight;
	private final SimpleOption<Double> chatScale = new SimpleOption<>(
		"options.chat.scale",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> (Text)(value == 0.0 ? ScreenTexts.composeToggleText(optionText, false) : getPercentValueText(optionText, value)),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> chatWidth = new SimpleOption<>(
		"options.chat.width",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> getPixelValueText(optionText, ChatHud.getWidth(value)),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> chatHeightUnfocused = new SimpleOption<>(
		"options.chat.height.unfocused",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> getPixelValueText(optionText, ChatHud.getHeight(value)),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		ChatHud.getDefaultUnfocusedHeight(),
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> chatHeightFocused = new SimpleOption<>(
		"options.chat.height.focused",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> getPixelValueText(optionText, ChatHud.getHeight(value)),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset()
	);
	private final SimpleOption<Double> chatDelay = new SimpleOption<>(
		"options.chat.delay_instant",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> value <= 0.0
				? Text.translatable("options.chat.delay_none")
				: Text.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", value)),
		new SimpleOption.ValidatingIntSliderCallbacks(0, 60).withModifier(value -> (double)value / 10.0, value -> (int)(value * 10.0)),
		Codec.doubleRange(0.0, 6.0),
		0.0,
		value -> MinecraftClient.getInstance().getMessageHandler().setChatDelay(value)
	);
	private static final Text NOTIFICATION_DISPLAY_TIME_TOOLTIP = Text.translatable("options.notifications.display_time.tooltip");
	private final SimpleOption<Double> notificationDisplayTime = new SimpleOption<>(
		"options.notifications.display_time",
		SimpleOption.constantTooltip(NOTIFICATION_DISPLAY_TIME_TOOLTIP),
		(optionText, value) -> getGenericValueText(optionText, Text.translatable("options.multiplier", value)),
		new SimpleOption.ValidatingIntSliderCallbacks(5, 100).withModifier(sliderProgressValue -> (double)sliderProgressValue / 10.0, value -> (int)(value * 10.0)),
		Codec.doubleRange(0.5, 10.0),
		1.0,
		value -> {
		}
	);
	private final SimpleOption<Integer> mipmapLevels = new SimpleOption<>(
		"options.mipmapLevels",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> (Text)(value == 0 ? ScreenTexts.composeToggleText(optionText, false) : getGenericValueText(optionText, value)),
		new SimpleOption.ValidatingIntSliderCallbacks(0, 4),
		4,
		value -> {
		}
	);
	public boolean useNativeTransport = true;
	private final SimpleOption<AttackIndicator> attackIndicator = new SimpleOption<>(
		"options.attackIndicator",
		SimpleOption.emptyTooltip(),
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(AttackIndicator.values()), Codec.INT.xmap(AttackIndicator::byId, AttackIndicator::getId)),
		AttackIndicator.CROSSHAIR,
		value -> {
		}
	);
	public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
	public boolean joinedFirstServer = false;
	public boolean hideBundleTutorial = false;
	private final SimpleOption<Integer> biomeBlendRadius = new SimpleOption<>("options.biomeBlendRadius", SimpleOption.emptyTooltip(), (optionText, value) -> {
		int i = value * 2 + 1;
		return getGenericValueText(optionText, Text.translatable("options.biomeBlendRadius." + i));
	}, new SimpleOption.ValidatingIntSliderCallbacks(0, 7), 2, value -> MinecraftClient.getInstance().worldRenderer.reload());
	private final SimpleOption<Double> mouseWheelSensitivity = new SimpleOption<>(
		"options.mouseWheelSensitivity",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> getGenericValueText(optionText, Text.literal(String.format(Locale.ROOT, "%.2f", value))),
		new SimpleOption.ValidatingIntSliderCallbacks(-200, 100)
			.withModifier(GameOptions::toMouseWheelSensitivityValue, GameOptions::toMouseWheelSensitivitySliderProgressValue),
		Codec.doubleRange(toMouseWheelSensitivityValue(-200), toMouseWheelSensitivityValue(100)),
		toMouseWheelSensitivityValue(0),
		value -> {
		}
	);
	private final SimpleOption<Boolean> rawMouseInput = SimpleOption.ofBoolean("options.rawMouseInput", true, value -> {
		Window window = MinecraftClient.getInstance().getWindow();
		if (window != null) {
			window.setRawMouseMotion(value);
		}
	});
	public int glDebugVerbosity = 1;
	private final SimpleOption<Boolean> autoJump = SimpleOption.ofBoolean("options.autoJump", false);
	private final SimpleOption<Boolean> operatorItemsTab = SimpleOption.ofBoolean("options.operatorItemsTab", false);
	private final SimpleOption<Boolean> autoSuggestions = SimpleOption.ofBoolean("options.autoSuggestCommands", true);
	private final SimpleOption<Boolean> chatColors = SimpleOption.ofBoolean("options.chat.color", true);
	private final SimpleOption<Boolean> chatLinks = SimpleOption.ofBoolean("options.chat.links", true);
	private final SimpleOption<Boolean> chatLinksPrompt = SimpleOption.ofBoolean("options.chat.links.prompt", true);
	private final SimpleOption<Boolean> enableVsync = SimpleOption.ofBoolean("options.vsync", true, value -> {
		if (MinecraftClient.getInstance().getWindow() != null) {
			MinecraftClient.getInstance().getWindow().setVsync(value);
		}
	});
	private final SimpleOption<Boolean> entityShadows = SimpleOption.ofBoolean("options.entityShadows", true);
	private final SimpleOption<Boolean> forceUnicodeFont = SimpleOption.ofBoolean("options.forceUnicodeFont", false, value -> {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.getWindow() != null) {
			minecraftClient.initFont(value);
			minecraftClient.onResolutionChanged();
		}
	});
	private final SimpleOption<Boolean> invertYMouse = SimpleOption.ofBoolean("options.invertMouse", false);
	private final SimpleOption<Boolean> discreteMouseScroll = SimpleOption.ofBoolean("options.discrete_mouse_scroll", false);
	private final SimpleOption<Boolean> realmsNotifications = SimpleOption.ofBoolean("options.realmsNotifications", true);
	private static final Text ALLOW_SERVER_LISTING_TOOLTIP = Text.translatable("options.allowServerListing.tooltip");
	private final SimpleOption<Boolean> allowServerListing = SimpleOption.ofBoolean(
		"options.allowServerListing", SimpleOption.constantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, value -> this.sendClientSettings()
	);
	private final SimpleOption<Boolean> reducedDebugInfo = SimpleOption.ofBoolean("options.reducedDebugInfo", false);
	private final Map<SoundCategory, SimpleOption<Double>> soundVolumeLevels = Util.make(new EnumMap(SoundCategory.class), soundVolumeLevels -> {
		for (SoundCategory soundCategory : SoundCategory.values()) {
			soundVolumeLevels.put(soundCategory, this.createSoundVolumeOption("soundCategory." + soundCategory.getName(), soundCategory));
		}
	});
	private final SimpleOption<Boolean> showSubtitles = SimpleOption.ofBoolean("options.showSubtitles", false);
	private static final Text DIRECTIONAL_AUDIO_ON_TOOLTIP = Text.translatable("options.directionalAudio.on.tooltip");
	private static final Text DIRECTIONAL_AUDIO_OFF_TOOLTIP = Text.translatable("options.directionalAudio.off.tooltip");
	private final SimpleOption<Boolean> directionalAudio = SimpleOption.ofBoolean(
		"options.directionalAudio", value -> value ? Tooltip.of(DIRECTIONAL_AUDIO_ON_TOOLTIP) : Tooltip.of(DIRECTIONAL_AUDIO_OFF_TOOLTIP), false, value -> {
			SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
			soundManager.reloadSounds();
			soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	);
	private final SimpleOption<Boolean> backgroundForChatOnly = new SimpleOption<>(
		"options.accessibility.text_background",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> value
				? Text.translatable("options.accessibility.text_background.chat")
				: Text.translatable("options.accessibility.text_background.everywhere"),
		SimpleOption.BOOLEAN,
		true,
		value -> {
		}
	);
	private final SimpleOption<Boolean> touchscreen = SimpleOption.ofBoolean("options.touchscreen", false);
	private final SimpleOption<Boolean> fullscreen = SimpleOption.ofBoolean("options.fullscreen", false, value -> {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != value) {
			minecraftClient.getWindow().toggleFullscreen();
			this.getFullscreen().setValue(minecraftClient.getWindow().isFullscreen());
		}
	});
	private final SimpleOption<Boolean> bobView = SimpleOption.ofBoolean("options.viewBobbing", true);
	private static final Text TOGGLE_KEY_TEXT = Text.translatable("options.key.toggle");
	private static final Text HOLD_KEY_TEXT = Text.translatable("options.key.hold");
	private final SimpleOption<Boolean> sneakToggled = new SimpleOption<>(
		"key.sneak", SimpleOption.emptyTooltip(), (optionText, value) -> value ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, SimpleOption.BOOLEAN, false, value -> {
		}
	);
	private final SimpleOption<Boolean> sprintToggled = new SimpleOption<>(
		"key.sprint", SimpleOption.emptyTooltip(), (optionText, value) -> value ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, SimpleOption.BOOLEAN, false, value -> {
		}
	);
	public boolean skipMultiplayerWarning;
	public boolean skipRealms32BitWarning;
	private static final Text HIDE_MATCHED_NAMES_TOOLTIP = Text.translatable("options.hideMatchedNames.tooltip");
	private final SimpleOption<Boolean> hideMatchedNames = SimpleOption.ofBoolean(
		"options.hideMatchedNames", SimpleOption.constantTooltip(HIDE_MATCHED_NAMES_TOOLTIP), true
	);
	private final SimpleOption<Boolean> showAutosaveIndicator = SimpleOption.ofBoolean("options.autosaveIndicator", true);
	private static final Text ONLY_SHOW_SECURE_CHAT_TOOLTIP = Text.translatable("options.onlyShowSecureChat.tooltip");
	private final SimpleOption<Boolean> onlyShowSecureChat = SimpleOption.ofBoolean(
		"options.onlyShowSecureChat", SimpleOption.constantTooltip(ONLY_SHOW_SECURE_CHAT_TOOLTIP), false
	);
	/**
	 * A key binding for moving forward.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_W the W key} by default.
	 */
	public final KeyBinding forwardKey = new KeyBinding("key.forward", GLFW.GLFW_KEY_W, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving left.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_A the A key} by default.
	 */
	public final KeyBinding leftKey = new KeyBinding("key.left", GLFW.GLFW_KEY_A, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving backward.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_S the S key} by default.
	 */
	public final KeyBinding backKey = new KeyBinding("key.back", GLFW.GLFW_KEY_S, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for moving right.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_D the D key} by default.
	 */
	public final KeyBinding rightKey = new KeyBinding("key.right", GLFW.GLFW_KEY_D, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for jumping.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_SPACE the space key} by default.
	 */
	public final KeyBinding jumpKey = new KeyBinding("key.jump", GLFW.GLFW_KEY_SPACE, KeyBinding.MOVEMENT_CATEGORY);
	/**
	 * A key binding for sneaking.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_LEFT_SHIFT the left shift key} by default.
	 */
	public final KeyBinding sneakKey = new StickyKeyBinding("key.sneak", GLFW.GLFW_KEY_LEFT_SHIFT, KeyBinding.MOVEMENT_CATEGORY, this.sneakToggled::getValue);
	/**
	 * A key binding for sprinting.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_LEFT_CONTROL the left control key} by default.
	 */
	public final KeyBinding sprintKey = new StickyKeyBinding("key.sprint", GLFW.GLFW_KEY_LEFT_CONTROL, KeyBinding.MOVEMENT_CATEGORY, this.sprintToggled::getValue);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ingame.InventoryScreen the inventory screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_E the E key} by default.
	 */
	public final KeyBinding inventoryKey = new KeyBinding("key.inventory", GLFW.GLFW_KEY_E, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for swapping the items in the selected slot and the off hand.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F the F key} by default.
	 * 
	 * <p>The selected slot is the slot the mouse is over when in a screen.
	 * Otherwise, it is the main hand.
	 */
	public final KeyBinding swapHandsKey = new KeyBinding("key.swapOffhand", GLFW.GLFW_KEY_F, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for dropping the item in the selected slot.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_Q the Q key} by default.
	 * 
	 * <p>The selected slot is the slot the mouse is over when in a screen.
	 * Otherwise, it is the main hand.
	 */
	public final KeyBinding dropKey = new KeyBinding("key.drop", GLFW.GLFW_KEY_Q, KeyBinding.INVENTORY_CATEGORY);
	/**
	 * A key binding for using an item, such as placing a block.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_RIGHT the right mouse button} by default.
	 */
	public final KeyBinding useKey = new KeyBinding("key.use", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for attacking an entity or breaking a block.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_LEFT the left mouse button} by default.
	 */
	public final KeyBinding attackKey = new KeyBinding("key.attack", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for holding an item corresponding to the {@linkplain net.minecraft.entity.Entity#getPickBlockStack() entity}
	 * or {@linkplain net.minecraft.block.Block#getPickStack(net.minecraft.world.WorldView,
	 * net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) block} the player is looking at.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_MIDDLE the middle mouse button} by default.
	 */
	public final KeyBinding pickItemKey = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, KeyBinding.GAMEPLAY_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ChatScreen the chat screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_T the T key} by default.
	 */
	public final KeyBinding chatKey = new KeyBinding("key.chat", GLFW.GLFW_KEY_T, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for displaying {@linkplain net.minecraft.client.gui.hud.PlayerListHud the player list}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_TAB the tab key} by default.
	 */
	public final KeyBinding playerListKey = new KeyBinding("key.playerlist", GLFW.GLFW_KEY_TAB, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.ChatScreen
	 * the chat screen} with the {@code /} already typed.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_SLASH the slash key} by default.
	 */
	public final KeyBinding commandKey = new KeyBinding("key.command", GLFW.GLFW_KEY_SLASH, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen the social interactions screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_P the P key} by default.
	 */
	public final KeyBinding socialInteractionsKey = new KeyBinding("key.socialInteractions", GLFW.GLFW_KEY_P, KeyBinding.MULTIPLAYER_CATEGORY);
	/**
	 * A key binding for taking a screenshot.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F2 the F2 key} by default.
	 */
	public final KeyBinding screenshotKey = new KeyBinding("key.screenshot", GLFW.GLFW_KEY_F2, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling perspective.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F5 the F5 key} by default.
	 */
	public final KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", GLFW.GLFW_KEY_F5, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling smooth camera.
	 * Not bound to any keys by default.
	 */
	public final KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for toggling fullscreen.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_F11 the F11 key} by default.
	 */
	public final KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", GLFW.GLFW_KEY_F11, KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for highlighting players in {@linkplain net.minecraft.world.GameMode#SPECTATOR spectator mode}.
	 * Not bound to any keys by default.
	 */
	public final KeyBinding spectatorOutlinesKey = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.MISC_CATEGORY);
	/**
	 * A key binding for opening {@linkplain net.minecraft.client.gui.screen.advancement.AdvancementsScreen the advancements screen}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_L the L key} by default.
	 */
	public final KeyBinding advancementsKey = new KeyBinding("key.advancements", GLFW.GLFW_KEY_L, KeyBinding.MISC_CATEGORY);
	/**
	 * Key bindings for selecting hotbar slots.
	 * Bound to the corresponding number keys (from {@linkplain
	 * org.lwjgl.glfw.GLFW#GLFW_KEY_1 the 1 key} to {@linkplain
	 * org.lwjgl.glfw.GLFW#GLFW_KEY_9 the 9 key}) by default.
	 */
	public final KeyBinding[] hotbarKeys = new KeyBinding[]{
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
	public final KeyBinding saveToolbarActivatorKey = new KeyBinding("key.saveToolbarActivator", GLFW.GLFW_KEY_C, KeyBinding.CREATIVE_CATEGORY);
	/**
	 * A key binding for loading the hotbar items in {@linkplain net.minecraft.world.GameMode#CREATIVE creative mode}.
	 * Bound to {@linkplain org.lwjgl.glfw.GLFW#GLFW_KEY_X the X key} by default.
	 */
	public final KeyBinding loadToolbarActivatorKey = new KeyBinding("key.loadToolbarActivator", GLFW.GLFW_KEY_X, KeyBinding.CREATIVE_CATEGORY);
	/**
	 * An array of all key bindings.
	 * 
	 * <p>Key bindings in this array are shown and can be configured in
	 * {@linkplain net.minecraft.client.gui.screen.option.ControlsOptionsScreen
	 * the controls options screen}.
	 */
	public final KeyBinding[] allKeys = ArrayUtils.addAll(
		(KeyBinding[])(new KeyBinding[]{
			this.attackKey,
			this.useKey,
			this.forwardKey,
			this.leftKey,
			this.backKey,
			this.rightKey,
			this.jumpKey,
			this.sneakKey,
			this.sprintKey,
			this.dropKey,
			this.inventoryKey,
			this.chatKey,
			this.playerListKey,
			this.pickItemKey,
			this.commandKey,
			this.socialInteractionsKey,
			this.screenshotKey,
			this.togglePerspectiveKey,
			this.smoothCameraKey,
			this.fullscreenKey,
			this.spectatorOutlinesKey,
			this.swapHandsKey,
			this.saveToolbarActivatorKey,
			this.loadToolbarActivatorKey,
			this.advancementsKey
		}),
		(KeyBinding[])this.hotbarKeys
	);
	protected MinecraftClient client;
	private final File optionsFile;
	public boolean hudHidden;
	private Perspective perspective = Perspective.FIRST_PERSON;
	public String lastServer = "";
	public boolean smoothCameraEnabled;
	private final SimpleOption<Integer> fov = new SimpleOption<>(
		"options.fov",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> {
			return switch (value) {
				case 70 -> getGenericValueText(optionText, Text.translatable("options.fov.min"));
				case 110 -> getGenericValueText(optionText, Text.translatable("options.fov.max"));
				default -> getGenericValueText(optionText, value);
			};
		},
		new SimpleOption.ValidatingIntSliderCallbacks(30, 110),
		Codec.DOUBLE.xmap(value -> (int)(value * 40.0 + 70.0), value -> ((double)value.intValue() - 70.0) / 40.0),
		70,
		value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()
	);
	private static final Text TELEMETRY_TOOLTIP = Text.translatable(
		"options.telemetry.button.tooltip", Text.translatable("options.telemetry.state.minimal"), Text.translatable("options.telemetry.state.all")
	);
	private final SimpleOption<Boolean> telemetryOptInExtra = SimpleOption.ofBoolean(
		"options.telemetry.button",
		SimpleOption.constantTooltip(TELEMETRY_TOOLTIP),
		(optionText, value) -> {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (!minecraftClient.isTelemetryEnabledByApi()) {
				return Text.translatable("options.telemetry.state.none");
			} else {
				return value && minecraftClient.isOptionalTelemetryEnabledByApi()
					? Text.translatable("options.telemetry.state.all")
					: Text.translatable("options.telemetry.state.minimal");
			}
		},
		false,
		value -> {
		}
	);
	private static final Text SCREEN_EFFECT_SCALE_TOOLTIP = Text.translatable("options.screenEffectScale.tooltip");
	private final SimpleOption<Double> distortionEffectScale = new SimpleOption<>(
		"options.screenEffectScale",
		SimpleOption.constantTooltip(SCREEN_EFFECT_SCALE_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> {
		}
	);
	private static final Text FOV_EFFECT_SCALE_TOOLTIP = Text.translatable("options.fovEffectScale.tooltip");
	private final SimpleOption<Double> fovEffectScale = new SimpleOption<>(
		"options.fovEffectScale",
		SimpleOption.constantTooltip(FOV_EFFECT_SCALE_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt),
		Codec.doubleRange(0.0, 1.0),
		1.0,
		value -> {
		}
	);
	private static final Text DARKNESS_EFFECT_SCALE_TOOLTIP = Text.translatable("options.darknessEffectScale.tooltip");
	private final SimpleOption<Double> darknessEffectScale = new SimpleOption<>(
		"options.darknessEffectScale",
		SimpleOption.constantTooltip(DARKNESS_EFFECT_SCALE_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt),
		1.0,
		value -> {
		}
	);
	private static final Text GLINT_SPEED_TOOLTIP = Text.translatable("options.glintSpeed.tooltip");
	private final SimpleOption<Double> glintSpeed = new SimpleOption<>(
		"options.glintSpeed",
		SimpleOption.constantTooltip(GLINT_SPEED_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		0.5,
		value -> {
		}
	);
	private static final Text GLINT_STRENGTH_TOOLTIP = Text.translatable("options.glintStrength.tooltip");
	private final SimpleOption<Double> glintStrength = new SimpleOption<>(
		"options.glintStrength",
		SimpleOption.constantTooltip(GLINT_STRENGTH_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		0.75,
		RenderSystem::setShaderGlintAlpha
	);
	private static final Text DAMAGE_TILT_STRENGTH_TOOLTIP = Text.translatable("options.damageTiltStrength.tooltip");
	private final SimpleOption<Double> damageTiltStrength = new SimpleOption<>(
		"options.damageTiltStrength",
		SimpleOption.constantTooltip(DAMAGE_TILT_STRENGTH_TOOLTIP),
		(optionText, value) -> value == 0.0 ? getGenericValueText(optionText, ScreenTexts.OFF) : getPercentValueText(optionText, value),
		SimpleOption.DoubleSliderCallbacks.INSTANCE,
		1.0,
		value -> {
		}
	);
	private final SimpleOption<Double> gamma = new SimpleOption<>("options.gamma", SimpleOption.emptyTooltip(), (optionText, value) -> {
		int i = (int)(value * 100.0);
		if (i == 0) {
			return getGenericValueText(optionText, Text.translatable("options.gamma.min"));
		} else if (i == 50) {
			return getGenericValueText(optionText, Text.translatable("options.gamma.default"));
		} else {
			return i == 100 ? getGenericValueText(optionText, Text.translatable("options.gamma.max")) : getGenericValueText(optionText, i);
		}
	}, SimpleOption.DoubleSliderCallbacks.INSTANCE, 0.5, value -> {
	});
	public static final int field_43405 = 0;
	private static final int MAX_SERIALIZABLE_GUI_SCALE = 2147483646;
	private final SimpleOption<Integer> guiScale = new SimpleOption<>(
		"options.guiScale",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> value == 0 ? Text.translatable("options.guiScale.auto") : Text.literal(Integer.toString(value)),
		new SimpleOption.MaxSuppliableIntCallbacks(0, () -> {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return !minecraftClient.isRunning() ? 2147483646 : minecraftClient.getWindow().calculateScaleFactor(0, minecraftClient.forcesUnicodeFont());
		}, 2147483646),
		0,
		value -> {
		}
	);
	private final SimpleOption<ParticlesMode> particles = new SimpleOption<>(
		"options.particles",
		SimpleOption.emptyTooltip(),
		SimpleOption.enumValueText(),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ParticlesMode.values()), Codec.INT.xmap(ParticlesMode::byId, ParticlesMode::getId)),
		ParticlesMode.ALL,
		value -> {
		}
	);
	private final SimpleOption<NarratorMode> narrator = new SimpleOption<>(
		"options.narrator",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> (Text)(this.client.getNarratorManager().isActive() ? value.getName() : Text.translatable("options.narrator.notavailable")),
		new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(NarratorMode.values()), Codec.INT.xmap(NarratorMode::byId, NarratorMode::getId)),
		NarratorMode.OFF,
		value -> this.client.getNarratorManager().onModeChange(value)
	);
	public String language = "en_us";
	private final SimpleOption<String> soundDevice = new SimpleOption<>(
		"options.audioDevice",
		SimpleOption.emptyTooltip(),
		(optionText, value) -> {
			if ("".equals(value)) {
				return Text.translatable("options.audioDevice.default");
			} else {
				return value.startsWith("OpenAL Soft on ") ? Text.literal(value.substring(SoundSystem.OPENAL_SOFT_ON_LENGTH)) : Text.literal(value);
			}
		},
		new SimpleOption.LazyCyclingCallbacks<>(
			() -> Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList(),
			value -> MinecraftClient.getInstance().isRunning() && value != "" && !MinecraftClient.getInstance().getSoundManager().getSoundDevices().contains(value)
					? Optional.empty()
					: Optional.of(value),
			Codec.STRING
		),
		"",
		value -> {
			SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
			soundManager.reloadSounds();
			soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	);
	public boolean onboardAccessibility = true;
	public boolean syncChunkWrites;

	public SimpleOption<Boolean> getMonochromeLogo() {
		return this.monochromeLogo;
	}

	public SimpleOption<Boolean> getHideLightningFlashes() {
		return this.hideLightningFlashes;
	}

	public SimpleOption<Double> getMouseSensitivity() {
		return this.mouseSensitivity;
	}

	public SimpleOption<Integer> getViewDistance() {
		return this.viewDistance;
	}

	public SimpleOption<Integer> getSimulationDistance() {
		return this.simulationDistance;
	}

	public SimpleOption<Double> getEntityDistanceScaling() {
		return this.entityDistanceScaling;
	}

	public SimpleOption<Integer> getMaxFps() {
		return this.maxFps;
	}

	public SimpleOption<CloudRenderMode> getCloudRenderMode() {
		return this.cloudRenderMode;
	}

	public SimpleOption<GraphicsMode> getGraphicsMode() {
		return this.graphicsMode;
	}

	public SimpleOption<Boolean> getAo() {
		return this.ao;
	}

	public SimpleOption<ChunkBuilderMode> getChunkBuilderMode() {
		return this.chunkBuilderMode;
	}

	public void refreshResourcePacks(ResourcePackManager resourcePackManager) {
		List<String> list = ImmutableList.copyOf(this.resourcePacks);
		this.resourcePacks.clear();
		this.incompatibleResourcePacks.clear();

		for (ResourcePackProfile resourcePackProfile : resourcePackManager.getEnabledProfiles()) {
			if (!resourcePackProfile.isPinned()) {
				this.resourcePacks.add(resourcePackProfile.getName());
				if (!resourcePackProfile.getCompatibility().isCompatible()) {
					this.incompatibleResourcePacks.add(resourcePackProfile.getName());
				}
			}
		}

		this.write();
		List<String> list2 = ImmutableList.copyOf(this.resourcePacks);
		if (!list2.equals(list)) {
			this.client.reloadResources();
		}
	}

	public SimpleOption<ChatVisibility> getChatVisibility() {
		return this.chatVisibility;
	}

	public SimpleOption<Double> getChatOpacity() {
		return this.chatOpacity;
	}

	public SimpleOption<Double> getChatLineSpacing() {
		return this.chatLineSpacing;
	}

	public SimpleOption<Double> getTextBackgroundOpacity() {
		return this.textBackgroundOpacity;
	}

	public SimpleOption<Double> getPanoramaSpeed() {
		return this.panoramaSpeed;
	}

	public SimpleOption<Boolean> getHighContrast() {
		return this.highContrast;
	}

	public SimpleOption<Boolean> getNarratorHotkey() {
		return this.narratorHotkey;
	}

	public SimpleOption<Arm> getMainArm() {
		return this.mainArm;
	}

	public SimpleOption<Double> getChatScale() {
		return this.chatScale;
	}

	public SimpleOption<Double> getChatWidth() {
		return this.chatWidth;
	}

	public SimpleOption<Double> getChatHeightUnfocused() {
		return this.chatHeightUnfocused;
	}

	public SimpleOption<Double> getChatHeightFocused() {
		return this.chatHeightFocused;
	}

	public SimpleOption<Double> getChatDelay() {
		return this.chatDelay;
	}

	public SimpleOption<Double> getNotificationDisplayTime() {
		return this.notificationDisplayTime;
	}

	public SimpleOption<Integer> getMipmapLevels() {
		return this.mipmapLevels;
	}

	public SimpleOption<AttackIndicator> getAttackIndicator() {
		return this.attackIndicator;
	}

	public SimpleOption<Integer> getBiomeBlendRadius() {
		return this.biomeBlendRadius;
	}

	private static double toMouseWheelSensitivityValue(int value) {
		return Math.pow(10.0, (double)value / 100.0);
	}

	private static int toMouseWheelSensitivitySliderProgressValue(double value) {
		return MathHelper.floor(Math.log10(value) * 100.0);
	}

	public SimpleOption<Double> getMouseWheelSensitivity() {
		return this.mouseWheelSensitivity;
	}

	public SimpleOption<Boolean> getRawMouseInput() {
		return this.rawMouseInput;
	}

	public SimpleOption<Boolean> getAutoJump() {
		return this.autoJump;
	}

	public SimpleOption<Boolean> getOperatorItemsTab() {
		return this.operatorItemsTab;
	}

	public SimpleOption<Boolean> getAutoSuggestions() {
		return this.autoSuggestions;
	}

	public SimpleOption<Boolean> getChatColors() {
		return this.chatColors;
	}

	public SimpleOption<Boolean> getChatLinks() {
		return this.chatLinks;
	}

	public SimpleOption<Boolean> getChatLinksPrompt() {
		return this.chatLinksPrompt;
	}

	public SimpleOption<Boolean> getEnableVsync() {
		return this.enableVsync;
	}

	public SimpleOption<Boolean> getEntityShadows() {
		return this.entityShadows;
	}

	public SimpleOption<Boolean> getForceUnicodeFont() {
		return this.forceUnicodeFont;
	}

	public SimpleOption<Boolean> getInvertYMouse() {
		return this.invertYMouse;
	}

	public SimpleOption<Boolean> getDiscreteMouseScroll() {
		return this.discreteMouseScroll;
	}

	public SimpleOption<Boolean> getRealmsNotifications() {
		return this.realmsNotifications;
	}

	public SimpleOption<Boolean> getAllowServerListing() {
		return this.allowServerListing;
	}

	public SimpleOption<Boolean> getReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public final float getSoundVolume(SoundCategory category) {
		return this.getSoundVolumeOption(category).getValue().floatValue();
	}

	public final SimpleOption<Double> getSoundVolumeOption(SoundCategory category) {
		return (SimpleOption<Double>)Objects.requireNonNull((SimpleOption)this.soundVolumeLevels.get(category));
	}

	private SimpleOption<Double> createSoundVolumeOption(String key, SoundCategory category) {
		return new SimpleOption<>(
			key,
			SimpleOption.emptyTooltip(),
			(prefix, value) -> value == 0.0 ? getGenericValueText(prefix, ScreenTexts.OFF) : getPercentValueText(prefix, value),
			SimpleOption.DoubleSliderCallbacks.INSTANCE,
			1.0,
			value -> MinecraftClient.getInstance().getSoundManager().updateSoundVolume(category, value.floatValue())
		);
	}

	public SimpleOption<Boolean> getShowSubtitles() {
		return this.showSubtitles;
	}

	public SimpleOption<Boolean> getDirectionalAudio() {
		return this.directionalAudio;
	}

	public SimpleOption<Boolean> getBackgroundForChatOnly() {
		return this.backgroundForChatOnly;
	}

	public SimpleOption<Boolean> getTouchscreen() {
		return this.touchscreen;
	}

	public SimpleOption<Boolean> getFullscreen() {
		return this.fullscreen;
	}

	public SimpleOption<Boolean> getBobView() {
		return this.bobView;
	}

	public SimpleOption<Boolean> getSneakToggled() {
		return this.sneakToggled;
	}

	public SimpleOption<Boolean> getSprintToggled() {
		return this.sprintToggled;
	}

	public SimpleOption<Boolean> getHideMatchedNames() {
		return this.hideMatchedNames;
	}

	public SimpleOption<Boolean> getShowAutosaveIndicator() {
		return this.showAutosaveIndicator;
	}

	public SimpleOption<Boolean> getOnlyShowSecureChat() {
		return this.onlyShowSecureChat;
	}

	public SimpleOption<Integer> getFov() {
		return this.fov;
	}

	public SimpleOption<Boolean> getTelemetryOptInExtra() {
		return this.telemetryOptInExtra;
	}

	public SimpleOption<Double> getDistortionEffectScale() {
		return this.distortionEffectScale;
	}

	public SimpleOption<Double> getFovEffectScale() {
		return this.fovEffectScale;
	}

	public SimpleOption<Double> getDarknessEffectScale() {
		return this.darknessEffectScale;
	}

	public SimpleOption<Double> getGlintSpeed() {
		return this.glintSpeed;
	}

	public SimpleOption<Double> getGlintStrength() {
		return this.glintStrength;
	}

	public SimpleOption<Double> getDamageTiltStrength() {
		return this.damageTiltStrength;
	}

	public SimpleOption<Double> getGamma() {
		return this.gamma;
	}

	public SimpleOption<Integer> getGuiScale() {
		return this.guiScale;
	}

	public SimpleOption<ParticlesMode> getParticles() {
		return this.particles;
	}

	public SimpleOption<NarratorMode> getNarrator() {
		return this.narrator;
	}

	public SimpleOption<String> getSoundDevice() {
		return this.soundDevice;
	}

	public GameOptions(MinecraftClient client, File optionsFile) {
		this.client = client;
		this.optionsFile = new File(optionsFile, "options.txt");
		boolean bl = client.is64Bit();
		boolean bl2 = bl && Runtime.getRuntime().maxMemory() >= 1000000000L;
		this.viewDistance = new SimpleOption<>(
			"options.renderDistance",
			SimpleOption.emptyTooltip(),
			(optionText, value) -> getGenericValueText(optionText, Text.translatable("options.chunks", value)),
			new SimpleOption.ValidatingIntSliderCallbacks(2, bl2 ? 32 : 16),
			bl ? 12 : 8,
			value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()
		);
		this.simulationDistance = new SimpleOption<>(
			"options.simulationDistance",
			SimpleOption.emptyTooltip(),
			(optionText, value) -> getGenericValueText(optionText, Text.translatable("options.chunks", value)),
			new SimpleOption.ValidatingIntSliderCallbacks(5, bl2 ? 32 : 16),
			bl ? 12 : 8,
			value -> {
			}
		);
		this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
		this.load();
	}

	public float getTextBackgroundOpacity(float fallback) {
		return this.backgroundForChatOnly.getValue() ? fallback : this.getTextBackgroundOpacity().getValue().floatValue();
	}

	public int getTextBackgroundColor(float fallbackOpacity) {
		return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0F) << 24 & 0xFF000000;
	}

	public int getTextBackgroundColor(int fallbackColor) {
		return this.backgroundForChatOnly.getValue() ? fallbackColor : (int)(this.textBackgroundOpacity.getValue() * 255.0) << 24 & 0xFF000000;
	}

	public void setKeyCode(KeyBinding key, InputUtil.Key code) {
		key.setBoundKey(code);
		this.write();
	}

	private void accept(GameOptions.Visitor visitor) {
		visitor.accept("autoJump", this.autoJump);
		visitor.accept("operatorItemsTab", this.operatorItemsTab);
		visitor.accept("autoSuggestions", this.autoSuggestions);
		visitor.accept("chatColors", this.chatColors);
		visitor.accept("chatLinks", this.chatLinks);
		visitor.accept("chatLinksPrompt", this.chatLinksPrompt);
		visitor.accept("enableVsync", this.enableVsync);
		visitor.accept("entityShadows", this.entityShadows);
		visitor.accept("forceUnicodeFont", this.forceUnicodeFont);
		visitor.accept("discrete_mouse_scroll", this.discreteMouseScroll);
		visitor.accept("invertYMouse", this.invertYMouse);
		visitor.accept("realmsNotifications", this.realmsNotifications);
		visitor.accept("reducedDebugInfo", this.reducedDebugInfo);
		visitor.accept("showSubtitles", this.showSubtitles);
		visitor.accept("directionalAudio", this.directionalAudio);
		visitor.accept("touchscreen", this.touchscreen);
		visitor.accept("fullscreen", this.fullscreen);
		visitor.accept("bobView", this.bobView);
		visitor.accept("toggleCrouch", this.sneakToggled);
		visitor.accept("toggleSprint", this.sprintToggled);
		visitor.accept("darkMojangStudiosBackground", this.monochromeLogo);
		visitor.accept("hideLightningFlashes", this.hideLightningFlashes);
		visitor.accept("mouseSensitivity", this.mouseSensitivity);
		visitor.accept("fov", this.fov);
		visitor.accept("screenEffectScale", this.distortionEffectScale);
		visitor.accept("fovEffectScale", this.fovEffectScale);
		visitor.accept("darknessEffectScale", this.darknessEffectScale);
		visitor.accept("glintSpeed", this.glintSpeed);
		visitor.accept("glintStrength", this.glintStrength);
		visitor.accept("damageTiltStrength", this.damageTiltStrength);
		visitor.accept("highContrast", this.highContrast);
		visitor.accept("narratorHotkey", this.narratorHotkey);
		visitor.accept("gamma", this.gamma);
		visitor.accept("renderDistance", this.viewDistance);
		visitor.accept("simulationDistance", this.simulationDistance);
		visitor.accept("entityDistanceScaling", this.entityDistanceScaling);
		visitor.accept("guiScale", this.guiScale);
		visitor.accept("particles", this.particles);
		visitor.accept("maxFps", this.maxFps);
		visitor.accept("graphicsMode", this.graphicsMode);
		visitor.accept("ao", this.ao);
		visitor.accept("prioritizeChunkUpdates", this.chunkBuilderMode);
		visitor.accept("biomeBlendRadius", this.biomeBlendRadius);
		visitor.accept("renderClouds", this.cloudRenderMode);
		this.resourcePacks = visitor.visitObject("resourcePacks", this.resourcePacks, GameOptions::parseList, GSON::toJson);
		this.incompatibleResourcePacks = visitor.visitObject("incompatibleResourcePacks", this.incompatibleResourcePacks, GameOptions::parseList, GSON::toJson);
		this.lastServer = visitor.visitString("lastServer", this.lastServer);
		this.language = visitor.visitString("lang", this.language);
		visitor.accept("soundDevice", this.soundDevice);
		visitor.accept("chatVisibility", this.chatVisibility);
		visitor.accept("chatOpacity", this.chatOpacity);
		visitor.accept("chatLineSpacing", this.chatLineSpacing);
		visitor.accept("textBackgroundOpacity", this.textBackgroundOpacity);
		visitor.accept("backgroundForChatOnly", this.backgroundForChatOnly);
		this.hideServerAddress = visitor.visitBoolean("hideServerAddress", this.hideServerAddress);
		this.advancedItemTooltips = visitor.visitBoolean("advancedItemTooltips", this.advancedItemTooltips);
		this.pauseOnLostFocus = visitor.visitBoolean("pauseOnLostFocus", this.pauseOnLostFocus);
		this.overrideWidth = visitor.visitInt("overrideWidth", this.overrideWidth);
		this.overrideHeight = visitor.visitInt("overrideHeight", this.overrideHeight);
		visitor.accept("chatHeightFocused", this.chatHeightFocused);
		visitor.accept("chatDelay", this.chatDelay);
		visitor.accept("chatHeightUnfocused", this.chatHeightUnfocused);
		visitor.accept("chatScale", this.chatScale);
		visitor.accept("chatWidth", this.chatWidth);
		visitor.accept("notificationDisplayTime", this.notificationDisplayTime);
		visitor.accept("mipmapLevels", this.mipmapLevels);
		this.useNativeTransport = visitor.visitBoolean("useNativeTransport", this.useNativeTransport);
		visitor.accept("mainHand", this.mainArm);
		visitor.accept("attackIndicator", this.attackIndicator);
		visitor.accept("narrator", this.narrator);
		this.tutorialStep = visitor.visitObject("tutorialStep", this.tutorialStep, TutorialStep::byName, TutorialStep::getName);
		visitor.accept("mouseWheelSensitivity", this.mouseWheelSensitivity);
		visitor.accept("rawMouseInput", this.rawMouseInput);
		this.glDebugVerbosity = visitor.visitInt("glDebugVerbosity", this.glDebugVerbosity);
		this.skipMultiplayerWarning = visitor.visitBoolean("skipMultiplayerWarning", this.skipMultiplayerWarning);
		this.skipRealms32BitWarning = visitor.visitBoolean("skipRealms32bitWarning", this.skipRealms32BitWarning);
		visitor.accept("hideMatchedNames", this.hideMatchedNames);
		this.joinedFirstServer = visitor.visitBoolean("joinedFirstServer", this.joinedFirstServer);
		this.hideBundleTutorial = visitor.visitBoolean("hideBundleTutorial", this.hideBundleTutorial);
		this.syncChunkWrites = visitor.visitBoolean("syncChunkWrites", this.syncChunkWrites);
		visitor.accept("showAutosaveIndicator", this.showAutosaveIndicator);
		visitor.accept("allowServerListing", this.allowServerListing);
		visitor.accept("onlyShowSecureChat", this.onlyShowSecureChat);
		visitor.accept("panoramaScrollSpeed", this.panoramaSpeed);
		visitor.accept("telemetryOptInExtra", this.telemetryOptInExtra);
		this.onboardAccessibility = visitor.visitBoolean("onboardAccessibility", this.onboardAccessibility);

		for (KeyBinding keyBinding : this.allKeys) {
			String string = keyBinding.getBoundKeyTranslationKey();
			String string2 = visitor.visitString("key_" + keyBinding.getTranslationKey(), string);
			if (!string.equals(string2)) {
				keyBinding.setBoundKey(InputUtil.fromTranslationKey(string2));
			}
		}

		for (SoundCategory soundCategory : SoundCategory.values()) {
			visitor.accept("soundCategory_" + soundCategory.getName(), (SimpleOption)this.soundVolumeLevels.get(soundCategory));
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
					this.graphicsMode.setValue(GraphicsMode.FANCY);
				} else {
					this.graphicsMode.setValue(GraphicsMode.FAST);
				}
			}

			this.accept(
				new GameOptions.Visitor() {
					@Nullable
					private String find(String key) {
						return nbtCompound2.contains(key) ? nbtCompound2.getString(key) : null;
					}

					@Override
					public <T> void accept(String key, SimpleOption<T> option) {
						String string = this.find(key);
						if (string != null) {
							JsonReader jsonReader = new JsonReader(new StringReader(string.isEmpty() ? "\"\"" : string));
							JsonElement jsonElement = JsonParser.parseReader(jsonReader);
							DataResult<T> dataResult = option.getCodec().parse(JsonOps.INSTANCE, jsonElement);
							dataResult.error()
								.ifPresent(partialResult -> GameOptions.LOGGER.error("Error parsing option value " + string + " for option " + option + ": " + partialResult.message()));
							dataResult.result().ifPresent(option::setValue);
						}
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
				}
			);
			if (nbtCompound2.contains("fullscreenResolution")) {
				this.fullscreenResolution = nbtCompound2.getString("fullscreenResolution");
			}

			if (this.client.getWindow() != null) {
				this.client.getWindow().setFramerateLimit(this.maxFps.getValue());
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

		return DataFixTypes.OPTIONS.update(this.client.getDataFixer(), nbt, i);
	}

	public void write() {
		try {
			final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

			try {
				printWriter.println("version:" + SharedConstants.getGameVersion().getSaveVersion().getId());
				this.accept(new GameOptions.Visitor() {
					public void print(String key) {
						printWriter.print(key);
						printWriter.print(':');
					}

					@Override
					public <T> void accept(String key, SimpleOption<T> option) {
						DataResult<JsonElement> dataResult = option.getCodec().encodeStart(JsonOps.INSTANCE, option.getValue());
						dataResult.error().ifPresent(partialResult -> GameOptions.LOGGER.error("Error saving option " + option + ": " + partialResult));
						dataResult.result().ifPresent(json -> {
							this.print(key);
							printWriter.println(GameOptions.GSON.toJson(json));
						});
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

	public SyncedClientOptions getSyncedOptions() {
		int i = 0;

		for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
			i |= playerModelPart.getBitFlag();
		}

		return new SyncedClientOptions(
			this.language,
			this.viewDistance.getValue(),
			this.chatVisibility.getValue(),
			this.chatColors.getValue(),
			i,
			this.mainArm.getValue(),
			this.client.shouldFilterText(),
			this.allowServerListing.getValue()
		);
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
			this.client.player.networkHandler.sendPacket(new ClientOptionsC2SPacket(this.getSyncedOptions()));
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

	public CloudRenderMode getCloudRenderModeValue() {
		return this.getClampedViewDistance() >= 4 ? this.cloudRenderMode.getValue() : CloudRenderMode.OFF;
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

	public File getOptionsFile() {
		return this.optionsFile;
	}

	public String collectProfiledOptions() {
		Stream<Pair<String, Object>> stream = Stream.builder()
			.add(Pair.of("ao", this.ao.getValue()))
			.add(Pair.of("biomeBlendRadius", this.biomeBlendRadius.getValue()))
			.add(Pair.of("enableVsync", this.enableVsync.getValue()))
			.add(Pair.of("entityDistanceScaling", this.entityDistanceScaling.getValue()))
			.add(Pair.of("entityShadows", this.entityShadows.getValue()))
			.add(Pair.of("forceUnicodeFont", this.forceUnicodeFont.getValue()))
			.add(Pair.of("fov", this.fov.getValue()))
			.add(Pair.of("fovEffectScale", this.fovEffectScale.getValue()))
			.add(Pair.of("darknessEffectScale", this.darknessEffectScale.getValue()))
			.add(Pair.of("glintSpeed", this.glintSpeed.getValue()))
			.add(Pair.of("glintStrength", this.glintStrength.getValue()))
			.add(Pair.of("prioritizeChunkUpdates", this.chunkBuilderMode.getValue()))
			.add(Pair.of("fullscreen", this.fullscreen.getValue()))
			.add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenResolution)))
			.add(Pair.of("gamma", this.gamma.getValue()))
			.add(Pair.of("glDebugVerbosity", this.glDebugVerbosity))
			.add(Pair.of("graphicsMode", this.graphicsMode.getValue()))
			.add(Pair.of("guiScale", this.guiScale.getValue()))
			.add(Pair.of("maxFps", this.maxFps.getValue()))
			.add(Pair.of("mipmapLevels", this.mipmapLevels.getValue()))
			.add(Pair.of("narrator", this.narrator.getValue()))
			.add(Pair.of("overrideHeight", this.overrideHeight))
			.add(Pair.of("overrideWidth", this.overrideWidth))
			.add(Pair.of("particles", this.particles.getValue()))
			.add(Pair.of("reducedDebugInfo", this.reducedDebugInfo.getValue()))
			.add(Pair.of("renderClouds", this.cloudRenderMode.getValue()))
			.add(Pair.of("renderDistance", this.viewDistance.getValue()))
			.add(Pair.of("simulationDistance", this.simulationDistance.getValue()))
			.add(Pair.of("resourcePacks", this.resourcePacks))
			.add(Pair.of("screenEffectScale", this.distortionEffectScale.getValue()))
			.add(Pair.of("syncChunkWrites", this.syncChunkWrites))
			.add(Pair.of("useNativeTransport", this.useNativeTransport))
			.add(Pair.of("soundDevice", this.soundDevice.getValue()))
			.build();
		return (String)stream.map(option -> (String)option.getFirst() + ": " + option.getSecond()).collect(Collectors.joining(System.lineSeparator()));
	}

	public void setServerViewDistance(int serverViewDistance) {
		this.serverViewDistance = serverViewDistance;
	}

	public int getClampedViewDistance() {
		return this.serverViewDistance > 0 ? Math.min(this.viewDistance.getValue(), this.serverViewDistance) : this.viewDistance.getValue();
	}

	private static Text getPixelValueText(Text prefix, int value) {
		return Text.translatable("options.pixel_value", prefix, value);
	}

	private static Text getPercentValueText(Text prefix, double value) {
		return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	public static Text getGenericValueText(Text prefix, Text value) {
		return Text.translatable("options.generic_value", prefix, value);
	}

	public static Text getGenericValueText(Text prefix, int value) {
		return getGenericValueText(prefix, Text.literal(Integer.toString(value)));
	}

	@Environment(EnvType.CLIENT)
	interface Visitor {
		<T> void accept(String key, SimpleOption<T> option);

		int visitInt(String key, int current);

		boolean visitBoolean(String key, boolean current);

		String visitString(String key, String current);

		float visitFloat(String key, float current);

		<T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder);
	}
}
