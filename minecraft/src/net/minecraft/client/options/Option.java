package net.minecraft.client.options;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class Option {
	public static final DoubleOption BIOME_BLEND_RADIUS = new DoubleOption(
		"options.biomeBlendRadius", 0.0, 7.0, 1.0F, gameOptions -> (double)gameOptions.biomeBlendRadius, (gameOptions, double_) -> {
			gameOptions.biomeBlendRadius = MathHelper.clamp((int)double_.doubleValue(), 0, 7);
			MinecraftClient.getInstance().worldRenderer.reload();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			int i = (int)d * 2 + 1;
			return doubleOption.method_30501(new TranslatableText("options.biomeBlendRadius." + i));
		}
	);
	public static final DoubleOption CHAT_HEIGHT_FOCUSED = new DoubleOption(
		"options.chat.height.focused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, double_) -> {
			gameOptions.chatHeightFocused = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return doubleOption.method_30500(ChatHud.getHeight(d));
		}
	);
	public static final DoubleOption SATURATION = new DoubleOption(
		"options.chat.height.unfocused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, double_) -> {
			gameOptions.chatHeightUnfocused = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return doubleOption.method_30500(ChatHud.getHeight(d));
		}
	);
	public static final DoubleOption CHAT_OPACITY = new DoubleOption(
		"options.chat.opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatOpacity, (gameOptions, double_) -> {
			gameOptions.chatOpacity = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return doubleOption.method_30503(d * 0.9 + 0.1);
		}
	);
	public static final DoubleOption CHAT_SCALE = new DoubleOption(
		"options.chat.scale", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatScale, (gameOptions, double_) -> {
			gameOptions.chatScale = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return (Text)(d == 0.0 ? ScreenTexts.method_30619(doubleOption.getDisplayPrefix(), false) : doubleOption.method_30503(d));
		}
	);
	public static final DoubleOption CHAT_WIDTH = new DoubleOption(
		"options.chat.width", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatWidth, (gameOptions, double_) -> {
			gameOptions.chatWidth = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return doubleOption.method_30500(ChatHud.getWidth(d));
		}
	);
	public static final DoubleOption CHAT_LINE_SPACING = new DoubleOption(
		"options.chat.line_spacing",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.chatLineSpacing,
		(gameOptions, double_) -> gameOptions.chatLineSpacing = double_,
		(gameOptions, doubleOption) -> doubleOption.method_30503(doubleOption.getRatio(doubleOption.get(gameOptions)))
	);
	public static final DoubleOption CHAT_DELAY_INSTANT = new DoubleOption(
		"options.chat.delay_instant",
		0.0,
		6.0,
		0.1F,
		gameOptions -> gameOptions.chatDelay,
		(gameOptions, double_) -> gameOptions.chatDelay = double_,
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return d <= 0.0 ? new TranslatableText("options.chat.delay_none") : new TranslatableText("options.chat.delay", String.format("%.1f", d));
		}
	);
	public static final DoubleOption FOV = new DoubleOption(
		"options.fov", 30.0, 110.0, 1.0F, gameOptions -> gameOptions.fov, (gameOptions, double_) -> gameOptions.fov = double_, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			if (d == 70.0) {
				return doubleOption.method_30501(new TranslatableText("options.fov.min"));
			} else {
				return d == doubleOption.getMax() ? doubleOption.method_30501(new TranslatableText("options.fov.max")) : doubleOption.method_30504((int)d);
			}
		}
	);
	private static final Text FOV_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.fovEffectScale.tooltip");
	public static final DoubleOption FOV_EFFECT_SCALE = new DoubleOption(
		"options.fovEffectScale",
		0.0,
		1.0,
		0.0F,
		gameOptions -> Math.pow((double)gameOptions.fovEffectScale, 2.0),
		(gameOptions, double_) -> gameOptions.fovEffectScale = MathHelper.sqrt(double_),
		(gameOptions, doubleOption) -> {
			doubleOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(FOV_EFFECT_SCALE_TOOLTIP, 200));
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return d == 0.0 ? doubleOption.method_30501(new TranslatableText("options.fovEffectScale.off")) : doubleOption.method_30503(d);
		}
	);
	private static final Text DISTORTION_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.screenEffectScale.tooltip");
	public static final DoubleOption DISTORTION_EFFECT_SCALE = new DoubleOption(
		"options.screenEffectScale",
		0.0,
		1.0,
		0.0F,
		gameOptions -> (double)gameOptions.distortionEffectScale,
		(gameOptions, double_) -> gameOptions.distortionEffectScale = double_.floatValue(),
		(gameOptions, doubleOption) -> {
			doubleOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(DISTORTION_EFFECT_SCALE_TOOLTIP, 200));
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return d == 0.0 ? doubleOption.method_30501(new TranslatableText("options.screenEffectScale.off")) : doubleOption.method_30503(d);
		}
	);
	public static final DoubleOption FRAMERATE_LIMIT = new DoubleOption(
		"options.framerateLimit",
		10.0,
		260.0,
		10.0F,
		gameOptions -> (double)gameOptions.maxFps,
		(gameOptions, double_) -> {
			gameOptions.maxFps = (int)double_.doubleValue();
			MinecraftClient.getInstance().getWindow().setFramerateLimit(gameOptions.maxFps);
		},
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return d == doubleOption.getMax()
				? doubleOption.method_30501(new TranslatableText("options.framerateLimit.max"))
				: doubleOption.method_30501(new TranslatableText("options.framerate", (int)d));
		}
	);
	public static final DoubleOption GAMMA = new DoubleOption(
		"options.gamma", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.gamma, (gameOptions, double_) -> gameOptions.gamma = double_, (gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			if (d == 0.0) {
				return doubleOption.method_30501(new TranslatableText("options.gamma.min"));
			} else {
				return d == 1.0 ? doubleOption.method_30501(new TranslatableText("options.gamma.max")) : doubleOption.method_30502((int)(d * 100.0));
			}
		}
	);
	public static final DoubleOption MIPMAP_LEVELS = new DoubleOption(
		"options.mipmapLevels",
		0.0,
		4.0,
		1.0F,
		gameOptions -> (double)gameOptions.mipmapLevels,
		(gameOptions, double_) -> gameOptions.mipmapLevels = (int)double_.doubleValue(),
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return (Text)(d == 0.0 ? ScreenTexts.method_30619(doubleOption.getDisplayPrefix(), false) : doubleOption.method_30504((int)d));
		}
	);
	public static final DoubleOption MOUSE_WHEEL_SENSITIVITY = new LogarithmicOption(
		"options.mouseWheelSensitivity",
		0.01,
		10.0,
		0.01F,
		gameOptions -> gameOptions.mouseWheelSensitivity,
		(gameOptions, double_) -> gameOptions.mouseWheelSensitivity = double_,
		(gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			return doubleOption.method_30501(new LiteralText(String.format("%.2f", doubleOption.getValue(d))));
		}
	);
	public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption(
		"options.rawMouseInput", gameOptions -> gameOptions.rawMouseInput, (gameOptions, boolean_) -> {
			gameOptions.rawMouseInput = boolean_;
			Window window = MinecraftClient.getInstance().getWindow();
			if (window != null) {
				window.setRawMouseMotion(boolean_);
			}
		}
	);
	public static final DoubleOption RENDER_DISTANCE = new DoubleOption(
		"options.renderDistance", 2.0, 16.0, 1.0F, gameOptions -> (double)gameOptions.viewDistance, (gameOptions, double_) -> {
			gameOptions.viewDistance = (int)double_.doubleValue();
			MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return doubleOption.method_30501(new TranslatableText("options.chunks", (int)d));
		}
	);
	public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption(
		"options.entityDistanceScaling",
		0.5,
		5.0,
		0.25F,
		gameOptions -> (double)gameOptions.entityDistanceScaling,
		(gameOptions, double_) -> gameOptions.entityDistanceScaling = (float)double_.doubleValue(),
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return doubleOption.method_30503(d);
		}
	);
	public static final DoubleOption SENSITIVITY = new DoubleOption(
		"options.sensitivity",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.mouseSensitivity,
		(gameOptions, double_) -> gameOptions.mouseSensitivity = double_,
		(gameOptions, doubleOption) -> {
			double d = doubleOption.getRatio(doubleOption.get(gameOptions));
			if (d == 0.0) {
				return doubleOption.method_30501(new TranslatableText("options.sensitivity.min"));
			} else {
				return d == 1.0 ? doubleOption.method_30501(new TranslatableText("options.sensitivity.max")) : doubleOption.method_30503(2.0 * d);
			}
		}
	);
	public static final DoubleOption TEXT_BACKGROUND_OPACITY = new DoubleOption(
		"options.accessibility.text_background_opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, double_) -> {
			gameOptions.textBackgroundOpacity = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleOption) -> doubleOption.method_30503(doubleOption.getRatio(doubleOption.get(gameOptions)))
	);
	public static final CyclingOption AO = new CyclingOption("options.ao", (gameOptions, integer) -> {
		gameOptions.ao = AoMode.byId(gameOptions.ao.getId() + integer);
		MinecraftClient.getInstance().worldRenderer.reload();
	}, (gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.ao.getTranslationKey())));
	public static final CyclingOption ATTACK_INDICATOR = new CyclingOption(
		"options.attackIndicator",
		(gameOptions, integer) -> gameOptions.attackIndicator = AttackIndicator.byId(gameOptions.attackIndicator.getId() + integer),
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.attackIndicator.getTranslationKey()))
	);
	public static final CyclingOption VISIBILITY = new CyclingOption(
		"options.chat.visibility",
		(gameOptions, integer) -> gameOptions.chatVisibility = ChatVisibility.byId((gameOptions.chatVisibility.getId() + integer) % 3),
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.chatVisibility.getTranslationKey()))
	);
	private static final Text FAST_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fast.tooltip");
	private static final Text FABULOUS_GRAPHICS_TOOLTIP = new TranslatableText(
		"options.graphics.fabulous.tooltip", new TranslatableText("options.graphics.fabulous").formatted(Formatting.ITALIC)
	);
	private static final Text FANCY_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fancy.tooltip");
	public static final CyclingOption GRAPHICS = new CyclingOption(
		"options.graphics",
		(options, count) -> {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
			if (options.graphicsMode == GraphicsMode.FANCY && videoWarningManager.canWarn()) {
				videoWarningManager.scheduleWarning();
			} else {
				options.graphicsMode = options.graphicsMode.next();
				if (options.graphicsMode == GraphicsMode.FABULOUS && (!GlStateManager.supportsGl30() || videoWarningManager.hasCancelledAfterWarning())) {
					options.graphicsMode = GraphicsMode.FAST;
				}

				minecraftClient.worldRenderer.reload();
			}
		},
		(options, cyclingOption) -> {
			switch (options.graphicsMode) {
				case FAST:
					cyclingOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(FAST_GRAPHICS_TOOLTIP, 200));
					break;
				case FANCY:
					cyclingOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(FANCY_GRAPHICS_TOOLTIP, 200));
					break;
				case FABULOUS:
					cyclingOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(FABULOUS_GRAPHICS_TOOLTIP, 200));
			}

			MutableText mutableText = new TranslatableText(options.graphicsMode.getTranslationKey());
			return options.graphicsMode == GraphicsMode.FABULOUS
				? cyclingOption.method_30501(mutableText.formatted(Formatting.ITALIC))
				: cyclingOption.method_30501(mutableText);
		}
	);
	public static final CyclingOption GUI_SCALE = new CyclingOption(
		"options.guiScale",
		(gameOptions, integer) -> gameOptions.guiScale = Integer.remainderUnsigned(
				gameOptions.guiScale + integer, MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1
			),
		(gameOptions, cyclingOption) -> gameOptions.guiScale == 0
				? cyclingOption.method_30501(new TranslatableText("options.guiScale.auto"))
				: cyclingOption.method_30504(gameOptions.guiScale)
	);
	public static final CyclingOption MAIN_HAND = new CyclingOption(
		"options.mainHand",
		(gameOptions, integer) -> gameOptions.mainArm = gameOptions.mainArm.getOpposite(),
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(gameOptions.mainArm.method_27301())
	);
	public static final CyclingOption NARRATOR = new CyclingOption(
		"options.narrator",
		(gameOptions, integer) -> {
			if (NarratorManager.INSTANCE.isActive()) {
				gameOptions.narrator = NarratorMode.byId(gameOptions.narrator.getId() + integer);
			} else {
				gameOptions.narrator = NarratorMode.OFF;
			}

			NarratorManager.INSTANCE.addToast(gameOptions.narrator);
		},
		(gameOptions, cyclingOption) -> NarratorManager.INSTANCE.isActive()
				? cyclingOption.method_30501(gameOptions.narrator.getName())
				: cyclingOption.method_30501(new TranslatableText("options.narrator.notavailable"))
	);
	public static final CyclingOption PARTICLES = new CyclingOption(
		"options.particles",
		(gameOptions, integer) -> gameOptions.particles = ParticlesMode.byId(gameOptions.particles.getId() + integer),
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.particles.getTranslationKey()))
	);
	public static final CyclingOption CLOUDS = new CyclingOption("options.renderClouds", (gameOptions, integer) -> {
		gameOptions.cloudRenderMode = CloudRenderMode.byId(gameOptions.cloudRenderMode.getId() + integer);
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
			if (framebuffer != null) {
				framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			}
		}
	}, (gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.cloudRenderMode.getTranslationKey())));
	public static final CyclingOption TEXT_BACKGROUND = new CyclingOption(
		"options.accessibility.text_background",
		(gameOptions, integer) -> gameOptions.backgroundForChatOnly = !gameOptions.backgroundForChatOnly,
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(
				new TranslatableText(gameOptions.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")
			)
	);
	public static final BooleanOption AUTO_JUMP = new BooleanOption(
		"options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, boolean_) -> gameOptions.autoJump = boolean_
	);
	public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption(
		"options.autoSuggestCommands", gameOptions -> gameOptions.autoSuggestions, (gameOptions, boolean_) -> gameOptions.autoSuggestions = boolean_
	);
	public static final BooleanOption CHAT_COLOR = new BooleanOption(
		"options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, boolean_) -> gameOptions.chatColors = boolean_
	);
	public static final BooleanOption CHAT_LINKS = new BooleanOption(
		"options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, boolean_) -> gameOptions.chatLinks = boolean_
	);
	public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption(
		"options.chat.links.prompt", gameOptions -> gameOptions.chatLinksPrompt, (gameOptions, boolean_) -> gameOptions.chatLinksPrompt = boolean_
	);
	public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption(
		"options.discrete_mouse_scroll", gameOptions -> gameOptions.discreteMouseScroll, (gameOptions, boolean_) -> gameOptions.discreteMouseScroll = boolean_
	);
	public static final BooleanOption VSYNC = new BooleanOption("options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, boolean_) -> {
		gameOptions.enableVsync = boolean_;
		if (MinecraftClient.getInstance().getWindow() != null) {
			MinecraftClient.getInstance().getWindow().setVsync(gameOptions.enableVsync);
		}
	});
	public static final BooleanOption ENTITY_SHADOWS = new BooleanOption(
		"options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, boolean_) -> gameOptions.entityShadows = boolean_
	);
	public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption(
		"options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, boolean_) -> {
			gameOptions.forceUnicodeFont = boolean_;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.getWindow() != null) {
				minecraftClient.initFont(boolean_);
			}
		}
	);
	public static final BooleanOption INVERT_MOUSE = new BooleanOption(
		"options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, boolean_) -> gameOptions.invertYMouse = boolean_
	);
	public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption(
		"options.realmsNotifications", gameOptions -> gameOptions.realmsNotifications, (gameOptions, boolean_) -> gameOptions.realmsNotifications = boolean_
	);
	public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption(
		"options.reducedDebugInfo", gameOptions -> gameOptions.reducedDebugInfo, (gameOptions, boolean_) -> gameOptions.reducedDebugInfo = boolean_
	);
	public static final BooleanOption SUBTITLES = new BooleanOption(
		"options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, boolean_) -> gameOptions.showSubtitles = boolean_
	);
	public static final BooleanOption SNOOPER = new BooleanOption("options.snooper", gameOptions -> {
		if (gameOptions.snooperEnabled) {
		}

		return false;
	}, (gameOptions, boolean_) -> gameOptions.snooperEnabled = boolean_);
	public static final CyclingOption SNEAK_TOGGLED = new CyclingOption(
		"key.sneak",
		(gameOptions, integer) -> gameOptions.sneakToggled = !gameOptions.sneakToggled,
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.sneakToggled ? "options.key.toggle" : "options.key.hold"))
	);
	public static final CyclingOption SPRINT_TOGGLED = new CyclingOption(
		"key.sprint",
		(gameOptions, integer) -> gameOptions.sprintToggled = !gameOptions.sprintToggled,
		(gameOptions, cyclingOption) -> cyclingOption.method_30501(new TranslatableText(gameOptions.sprintToggled ? "options.key.toggle" : "options.key.hold"))
	);
	public static final BooleanOption TOUCHSCREEN = new BooleanOption(
		"options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, boolean_) -> gameOptions.touchscreen = boolean_
	);
	public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, boolean_) -> {
		gameOptions.fullscreen = boolean_;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != gameOptions.fullscreen) {
			minecraftClient.getWindow().toggleFullscreen();
			gameOptions.fullscreen = minecraftClient.getWindow().isFullscreen();
		}
	});
	public static final BooleanOption VIEW_BOBBING = new BooleanOption(
		"options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, boolean_) -> gameOptions.bobView = boolean_
	);
	private final Text key;
	private Optional<List<OrderedText>> tooltip = Optional.empty();

	public Option(String key) {
		this.key = new TranslatableText(key);
	}

	public abstract AbstractButtonWidget createButton(GameOptions options, int x, int y, int width);

	protected Text getDisplayPrefix() {
		return this.key;
	}

	public void setTooltip(List<OrderedText> tooltip) {
		this.tooltip = Optional.of(tooltip);
	}

	public Optional<List<OrderedText>> getTooltip() {
		return this.tooltip;
	}

	protected Text method_30500(int i) {
		return new TranslatableText("options.pixel_value", this.getDisplayPrefix(), i);
	}

	protected Text method_30503(double d) {
		return new TranslatableText("options.percent_value", this.getDisplayPrefix(), (int)(d * 100.0));
	}

	protected Text method_30502(int i) {
		return new TranslatableText("options.percent_add_value", this.getDisplayPrefix(), i);
	}

	protected Text method_30501(Text text) {
		return new TranslatableText("options.generic_value", this.getDisplayPrefix(), text);
	}

	protected Text method_30504(int i) {
		return this.method_30501(new LiteralText(Integer.toString(i)));
	}
}
