package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public abstract class Option {
	protected static final int field_37859 = 200;
	public static final DoubleOption CHAT_OPACITY = new DoubleOption(
		"options.chat.opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatOpacity, (gameOptions, chatOpacity) -> {
			gameOptions.chatOpacity = chatOpacity;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			return option.getPercentValueText(d * 0.9 + 0.1);
		}
	);
	public static final DoubleOption CHAT_SCALE = new DoubleOption(
		"options.chat.scale", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatScale, (gameOptions, chatScale) -> {
			gameOptions.chatScale = chatScale;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			return (Text)(d == 0.0 ? ScreenTexts.composeToggleText(option.getDisplayPrefix(), false) : option.getPercentValueText(d));
		}
	);
	public static final DoubleOption CHAT_WIDTH = new DoubleOption(
		"options.chat.width", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatWidth, (gameOptions, chatWidth) -> {
			gameOptions.chatWidth = chatWidth;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			return option.getPixelValueText(ChatHud.getWidth(d));
		}
	);
	public static final DoubleOption CHAT_LINE_SPACING = new DoubleOption(
		"options.chat.line_spacing",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.chatLineSpacing,
		(gameOptions, chatLineSpacing) -> gameOptions.chatLineSpacing = chatLineSpacing,
		(gameOptions, option) -> option.getPercentValueText(option.getRatio(option.get(gameOptions)))
	);
	public static final DoubleOption CHAT_DELAY_INSTANT = new DoubleOption(
		"options.chat.delay_instant",
		0.0,
		6.0,
		0.1F,
		gameOptions -> gameOptions.chatDelay,
		(gameOptions, chatDelay) -> gameOptions.chatDelay = chatDelay,
		(gameOptions, option) -> {
			double d = option.get(gameOptions);
			return d <= 0.0 ? new TranslatableText("options.chat.delay_none") : new TranslatableText("options.chat.delay", String.format("%.1f", d));
		}
	);
	private static final Text FOV_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.fovEffectScale.tooltip");
	public static final DoubleOption FOV_EFFECT_SCALE = new DoubleOption(
		"options.fovEffectScale",
		0.0,
		1.0,
		0.0F,
		gameOptions -> Math.pow((double)gameOptions.fovEffectScale, 2.0),
		(gameOptions, fovEffectScale) -> gameOptions.fovEffectScale = (float)Math.sqrt(fovEffectScale),
		(gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			return d == 0.0 ? option.getGenericValueText(ScreenTexts.OFF) : option.getPercentValueText(d);
		},
		client -> value -> client.textRenderer.wrapLines(FOV_EFFECT_SCALE_TOOLTIP, 200)
	);
	private static final Text DISTORTION_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.screenEffectScale.tooltip");
	public static final DoubleOption DISTORTION_EFFECT_SCALE = new DoubleOption(
		"options.screenEffectScale",
		0.0,
		1.0,
		0.0F,
		gameOptions -> (double)gameOptions.distortionEffectScale,
		(gameOptions, distortionEffectScale) -> gameOptions.distortionEffectScale = distortionEffectScale.floatValue(),
		(gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			return d == 0.0 ? option.getGenericValueText(ScreenTexts.OFF) : option.getPercentValueText(d);
		},
		client -> value -> client.textRenderer.wrapLines(DISTORTION_EFFECT_SCALE_TOOLTIP, 200)
	);
	public static final DoubleOption FRAMERATE_LIMIT = new DoubleOption(
		"options.framerateLimit",
		10.0,
		260.0,
		10.0F,
		gameOptions -> (double)gameOptions.maxFps,
		(gameOptions, maxFps) -> {
			gameOptions.maxFps = (int)maxFps.doubleValue();
			MinecraftClient.getInstance().getWindow().setFramerateLimit(gameOptions.maxFps);
		},
		(gameOptions, option) -> {
			double d = option.get(gameOptions);
			return d == option.getMax()
				? option.getGenericValueText(new TranslatableText("options.framerateLimit.max"))
				: option.getGenericValueText(new TranslatableText("options.framerate", (int)d));
		}
	);
	public static final DoubleOption GAMMA = new DoubleOption(
		"options.gamma", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.gamma, (gameOptions, gamma) -> gameOptions.gamma = gamma, (gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			int i = (int)(d * 100.0);
			if (i == 0) {
				return option.getGenericValueText(new TranslatableText("options.gamma.min"));
			} else if (i == 50) {
				return option.getGenericValueText(new TranslatableText("options.gamma.default"));
			} else {
				return i == 100 ? option.getGenericValueText(new TranslatableText("options.gamma.max")) : option.getGenericValueText(i);
			}
		}
	);
	public static final DoubleOption MIPMAP_LEVELS = new DoubleOption(
		"options.mipmapLevels",
		0.0,
		4.0,
		1.0F,
		gameOptions -> (double)gameOptions.mipmapLevels,
		(gameOptions, mipmapLevels) -> gameOptions.mipmapLevels = (int)mipmapLevels.doubleValue(),
		(gameOptions, option) -> {
			double d = option.get(gameOptions);
			return (Text)(d == 0.0 ? ScreenTexts.composeToggleText(option.getDisplayPrefix(), false) : option.getGenericValueText((int)d));
		}
	);
	public static final DoubleOption RENDER_DISTANCE = new DoubleOption(
		"options.renderDistance", 2.0, 16.0, 1.0F, gameOptions -> (double)gameOptions.viewDistance, (gameOptions, viewDistance) -> {
			gameOptions.viewDistance = viewDistance.intValue();
			MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
		}, (gameOptions, option) -> {
			double d = option.get(gameOptions);
			return option.getGenericValueText(new TranslatableText("options.chunks", (int)d));
		}
	);
	public static final DoubleOption SIMULATION_DISTANCE = new DoubleOption(
		"options.simulationDistance",
		5.0,
		16.0,
		1.0F,
		gameOptions -> (double)gameOptions.simulationDistance,
		(gameOptions, simulationDistance) -> gameOptions.simulationDistance = simulationDistance.intValue(),
		(gameOptions, option) -> {
			double d = option.get(gameOptions);
			return option.getGenericValueText(new TranslatableText("options.chunks", (int)d));
		}
	);
	public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption(
		"options.entityDistanceScaling",
		0.5,
		5.0,
		0.25F,
		gameOptions -> (double)gameOptions.entityDistanceScaling,
		(gameOptions, entityDistanceScaling) -> gameOptions.entityDistanceScaling = (float)entityDistanceScaling.doubleValue(),
		(gameOptions, option) -> {
			double d = option.get(gameOptions);
			return option.getPercentValueText(d);
		}
	);
	public static final DoubleOption SENSITIVITY = new DoubleOption(
		"options.sensitivity",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.mouseSensitivity,
		(gameOptions, mouseSensitivity) -> gameOptions.mouseSensitivity = mouseSensitivity,
		(gameOptions, option) -> {
			double d = option.getRatio(option.get(gameOptions));
			if (d == 0.0) {
				return option.getGenericValueText(new TranslatableText("options.sensitivity.min"));
			} else {
				return d == 1.0 ? option.getGenericValueText(new TranslatableText("options.sensitivity.max")) : option.getPercentValueText(2.0 * d);
			}
		}
	);
	public static final DoubleOption TEXT_BACKGROUND_OPACITY = new DoubleOption(
		"options.accessibility.text_background_opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, textBackgroundOpacity) -> {
			gameOptions.textBackgroundOpacity = textBackgroundOpacity;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, option) -> option.getPercentValueText(option.getRatio(option.get(gameOptions)))
	);
	public static final CyclingOption<AttackIndicator> ATTACK_INDICATOR = CyclingOption.create(
		"options.attackIndicator",
		AttackIndicator.values(),
		attackIndicator -> new TranslatableText(attackIndicator.getTranslationKey()),
		gameOptions -> gameOptions.attackIndicator,
		(gameOptions, option, attackIndicator) -> gameOptions.attackIndicator = attackIndicator
	);
	public static final CyclingOption<ChatVisibility> VISIBILITY = CyclingOption.create(
		"options.chat.visibility",
		ChatVisibility.values(),
		chatVisibility -> new TranslatableText(chatVisibility.getTranslationKey()),
		gameOptions -> gameOptions.chatVisibility,
		(gameOptions, option, chatVisibility) -> gameOptions.chatVisibility = chatVisibility
	);
	private static final Text FAST_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fast.tooltip");
	private static final Text FABULOUS_GRAPHICS_TOOLTIP = new TranslatableText(
		"options.graphics.fabulous.tooltip", new TranslatableText("options.graphics.fabulous").formatted(Formatting.ITALIC)
	);
	private static final Text FANCY_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fancy.tooltip");
	public static final CyclingOption<GraphicsMode> GRAPHICS = CyclingOption.<GraphicsMode>create(
			"options.graphics",
			Arrays.asList(GraphicsMode.values()),
			(List<GraphicsMode>)Stream.of(GraphicsMode.values()).filter(graphicsMode -> graphicsMode != GraphicsMode.FABULOUS).collect(Collectors.toList()),
			() -> MinecraftClient.getInstance().getVideoWarningManager().hasCancelledAfterWarning(),
			graphicsMode -> {
				MutableText mutableText = new TranslatableText(graphicsMode.getTranslationKey());
				return graphicsMode == GraphicsMode.FABULOUS ? mutableText.formatted(Formatting.ITALIC) : mutableText;
			},
			gameOptions -> gameOptions.graphicsMode,
			(gameOptions, option, graphicsMode) -> {
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
				if (graphicsMode == GraphicsMode.FABULOUS && videoWarningManager.canWarn()) {
					videoWarningManager.scheduleWarning();
				} else {
					gameOptions.graphicsMode = graphicsMode;
					minecraftClient.worldRenderer.reload();
				}
			}
		)
		.tooltip(client -> {
			List<OrderedText> list = client.textRenderer.wrapLines(FAST_GRAPHICS_TOOLTIP, 200);
			List<OrderedText> list2 = client.textRenderer.wrapLines(FANCY_GRAPHICS_TOOLTIP, 200);
			List<OrderedText> list3 = client.textRenderer.wrapLines(FABULOUS_GRAPHICS_TOOLTIP, 200);
			return graphicsMode -> {
				return switch (graphicsMode) {
					case FANCY -> list2;
					case FAST -> list;
					case FABULOUS -> list3;
				};
			};
		});
	public static final CyclingOption<Integer> GUI_SCALE = CyclingOption.create(
		"options.guiScale",
		(Supplier<List<Integer>>)(() -> (List)IntStream.rangeClosed(
					0, MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont())
				)
				.boxed()
				.collect(Collectors.toList())),
		guiScale -> (Text)(guiScale == 0 ? new TranslatableText("options.guiScale.auto") : new LiteralText(Integer.toString(guiScale))),
		gameOptions -> gameOptions.guiScale,
		(gameOptions, option, guiScale) -> gameOptions.guiScale = guiScale
	);
	public static final CyclingOption<String> AUDIO_DEVICE = CyclingOption.create(
		"options.audioDevice",
		(Supplier<List<String>>)(() -> Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList()),
		device -> {
			if ("".equals(device)) {
				return new TranslatableText("options.audioDevice.default");
			} else {
				return device.startsWith("OpenAL Soft on ") ? new LiteralText(device.substring(SoundSystem.OPENAL_SOFT_ON_LENGTH)) : new LiteralText(device);
			}
		},
		gameOptions -> gameOptions.soundDevice,
		(gameOptions, option, audioDevice) -> {
			gameOptions.soundDevice = audioDevice;
			SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
			soundManager.reloadSounds();
			soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	);
	public static final CyclingOption<Arm> MAIN_HAND = CyclingOption.create(
		"options.mainHand", Arm.values(), Arm::getOptionName, gameOptions -> gameOptions.mainArm, (gameOptions, option, mainArm) -> {
			gameOptions.mainArm = mainArm;
			gameOptions.sendClientSettings();
		}
	);
	public static final CyclingOption<NarratorMode> NARRATOR = CyclingOption.create(
		"options.narrator",
		NarratorMode.values(),
		narrator -> (Text)(NarratorManager.INSTANCE.isActive() ? narrator.getName() : new TranslatableText("options.narrator.notavailable")),
		gameOptions -> gameOptions.narrator,
		(gameOptions, option, narrator) -> {
			gameOptions.narrator = narrator;
			NarratorManager.INSTANCE.addToast(narrator);
		}
	);
	public static final CyclingOption<ParticlesMode> PARTICLES = CyclingOption.create(
		"options.particles",
		ParticlesMode.values(),
		particlesMode -> new TranslatableText(particlesMode.getTranslationKey()),
		gameOptions -> gameOptions.particles,
		(gameOptions, option, particlesMode) -> gameOptions.particles = particlesMode
	);
	public static final CyclingOption<CloudRenderMode> CLOUDS = CyclingOption.create(
		"options.renderClouds",
		CloudRenderMode.values(),
		cloudRenderMode -> new TranslatableText(cloudRenderMode.getTranslationKey()),
		gameOptions -> gameOptions.cloudRenderMode,
		(gameOptions, option, cloudRenderMode) -> {
			gameOptions.cloudRenderMode = cloudRenderMode;
			if (MinecraftClient.isFabulousGraphicsOrBetter()) {
				Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
				if (framebuffer != null) {
					framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
				}
			}
		}
	);
	public static final CyclingOption<Boolean> TEXT_BACKGROUND = CyclingOption.create(
		"options.accessibility.text_background",
		new TranslatableText("options.accessibility.text_background.chat"),
		new TranslatableText("options.accessibility.text_background.everywhere"),
		gameOptions -> gameOptions.backgroundForChatOnly,
		(gameOptions, option, backgroundForChatOnly) -> gameOptions.backgroundForChatOnly = backgroundForChatOnly
	);
	private static final Text HIDE_MATCHED_NAMES_TOOLTIP = new TranslatableText("options.hideMatchedNames.tooltip");
	public static final CyclingOption<Boolean> AUTO_JUMP = CyclingOption.create(
		"options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, option, autoJump) -> gameOptions.autoJump = autoJump
	);
	public static final CyclingOption<Boolean> AUTO_SUGGESTIONS = CyclingOption.create(
		"options.autoSuggestCommands",
		gameOptions -> gameOptions.autoSuggestions,
		(gameOptions, option, autoSuggestions) -> gameOptions.autoSuggestions = autoSuggestions
	);
	public static final CyclingOption<Boolean> CHAT_COLOR = CyclingOption.create(
		"options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, option, chatColors) -> gameOptions.chatColors = chatColors
	);
	public static final CyclingOption<Boolean> HIDE_MATCHED_NAMES = CyclingOption.create(
		"options.hideMatchedNames",
		HIDE_MATCHED_NAMES_TOOLTIP,
		gameOptions -> gameOptions.hideMatchedNames,
		(gameOptions, option, hideMatchedNames) -> gameOptions.hideMatchedNames = hideMatchedNames
	);
	public static final CyclingOption<Boolean> CHAT_LINKS = CyclingOption.create(
		"options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, option, chatLinks) -> gameOptions.chatLinks = chatLinks
	);
	public static final CyclingOption<Boolean> CHAT_LINKS_PROMPT = CyclingOption.create(
		"options.chat.links.prompt",
		gameOptions -> gameOptions.chatLinksPrompt,
		(gameOptions, option, chatLinksPrompt) -> gameOptions.chatLinksPrompt = chatLinksPrompt
	);
	public static final CyclingOption<Boolean> DISCRETE_MOUSE_SCROLL = CyclingOption.create(
		"options.discrete_mouse_scroll",
		gameOptions -> gameOptions.discreteMouseScroll,
		(gameOptions, option, discreteMouseScroll) -> gameOptions.discreteMouseScroll = discreteMouseScroll
	);
	public static final CyclingOption<Boolean> VSYNC = CyclingOption.create(
		"options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, option, enableVsync) -> {
			gameOptions.enableVsync = enableVsync;
			if (MinecraftClient.getInstance().getWindow() != null) {
				MinecraftClient.getInstance().getWindow().setVsync(gameOptions.enableVsync);
			}
		}
	);
	public static final CyclingOption<Boolean> ENTITY_SHADOWS = CyclingOption.create(
		"options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, option, entityShadows) -> gameOptions.entityShadows = entityShadows
	);
	public static final CyclingOption<Boolean> FORCE_UNICODE_FONT = CyclingOption.create(
		"options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, option, forceUnicodeFont) -> {
			gameOptions.forceUnicodeFont = forceUnicodeFont;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.getWindow() != null) {
				minecraftClient.initFont(forceUnicodeFont);
				minecraftClient.onResolutionChanged();
			}
		}
	);
	public static final CyclingOption<Boolean> INVERT_MOUSE = CyclingOption.create(
		"options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, option, invertYMouse) -> gameOptions.invertYMouse = invertYMouse
	);
	public static final CyclingOption<Boolean> REALMS_NOTIFICATIONS = CyclingOption.create(
		"options.realmsNotifications",
		gameOptions -> gameOptions.realmsNotifications,
		(gameOptions, option, realmsNotifications) -> gameOptions.realmsNotifications = realmsNotifications
	);
	private static final Text ALLOW_SERVER_LISTING_TOOLTIP = new TranslatableText("options.allowServerListing.tooltip");
	public static final CyclingOption<Boolean> ALLOW_SERVER_LISTING = CyclingOption.create(
		"options.allowServerListing", ALLOW_SERVER_LISTING_TOOLTIP, gameOptions -> gameOptions.allowServerListing, (gameOptions, option, allowServerListing) -> {
			gameOptions.allowServerListing = allowServerListing;
			gameOptions.sendClientSettings();
		}
	);
	public static final CyclingOption<Boolean> REDUCED_DEBUG_INFO = CyclingOption.create(
		"options.reducedDebugInfo",
		gameOptions -> gameOptions.reducedDebugInfo,
		(gameOptions, option, reducedDebugInfo) -> gameOptions.reducedDebugInfo = reducedDebugInfo
	);
	public static final CyclingOption<Boolean> SUBTITLES = CyclingOption.create(
		"options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, option, showSubtitles) -> gameOptions.showSubtitles = showSubtitles
	);
	private static final Text DIRECTIONAL_AUDIO_ON_TEXT = new TranslatableText("options.directionalAudio.on.tooltip");
	private static final Text DIRECTIONAL_AUDIO_OFF_TEXT = new TranslatableText("options.directionalAudio.off.tooltip");
	public static final CyclingOption<Boolean> DIRECTIONAL_AUDIO = CyclingOption.create(
			"options.directionalAudio", gameOptions -> gameOptions.directionalAudio, (gameOptions, option, directionalAudio) -> {
				gameOptions.directionalAudio = directionalAudio;
				SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
				soundManager.reloadSounds();
				soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		)
		.tooltip(
			client -> value -> value ? client.textRenderer.wrapLines(DIRECTIONAL_AUDIO_ON_TEXT, 200) : client.textRenderer.wrapLines(DIRECTIONAL_AUDIO_OFF_TEXT, 200)
		);
	private static final Text TOGGLE_TEXT = new TranslatableText("options.key.toggle");
	private static final Text HOLD_TEXT = new TranslatableText("options.key.hold");
	public static final CyclingOption<Boolean> SNEAK_TOGGLED = CyclingOption.create(
		"key.sneak", TOGGLE_TEXT, HOLD_TEXT, gameOptions -> gameOptions.sneakToggled, (gameOptions, option, sneakToggled) -> gameOptions.sneakToggled = sneakToggled
	);
	public static final CyclingOption<Boolean> SPRINT_TOGGLED = CyclingOption.create(
		"key.sprint",
		TOGGLE_TEXT,
		HOLD_TEXT,
		gameOptions -> gameOptions.sprintToggled,
		(gameOptions, option, sprintToggled) -> gameOptions.sprintToggled = sprintToggled
	);
	public static final CyclingOption<Boolean> TOUCHSCREEN = CyclingOption.create(
		"options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, option, touchscreen) -> gameOptions.touchscreen = touchscreen
	);
	public static final CyclingOption<Boolean> FULLSCREEN = CyclingOption.create(
		"options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, option, fullscreen) -> {
			gameOptions.fullscreen = fullscreen;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != gameOptions.fullscreen) {
				minecraftClient.getWindow().toggleFullscreen();
				gameOptions.fullscreen = minecraftClient.getWindow().isFullscreen();
			}
		}
	);
	public static final CyclingOption<Boolean> VIEW_BOBBING = CyclingOption.create(
		"options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, option, bobView) -> gameOptions.bobView = bobView
	);
	public static final CyclingOption<Boolean> SHOW_AUTOSAVE_INDICATOR = CyclingOption.create(
		"options.autosaveIndicator",
		gameOptions -> gameOptions.showAutosaveIndicator,
		(gameOptions, option, showAutosaveIndicator) -> gameOptions.showAutosaveIndicator = showAutosaveIndicator
	);
	private final Text key;

	public static <T> Function<MinecraftClient, Option.TooltipFactory<T>> emptyTooltipFactoryGetter() {
		return client -> value -> ImmutableList.of();
	}

	public Option(String key) {
		this.key = new TranslatableText(key);
	}

	public abstract ClickableWidget createButton(GameOptions options, int x, int y, int width);

	protected Text getDisplayPrefix() {
		return this.key;
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface TooltipFactory<T> extends Function<T, List<OrderedText>> {
	}
}
