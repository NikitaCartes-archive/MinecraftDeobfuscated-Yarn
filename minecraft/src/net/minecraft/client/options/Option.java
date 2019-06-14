package net.minecraft.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class Option {
	public static final DoubleOption field_18189 = new DoubleOption(
		"options.biomeBlendRadius", 0.0, 7.0, 1.0F, gameOptions -> (double)gameOptions.biomeBlendRadius, (gameOptions, double_) -> {
			gameOptions.biomeBlendRadius = MathHelper.clamp((int)double_.doubleValue(), 0, 7);
			MinecraftClient.getInstance().field_1769.reload();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			String string = doubleOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.off");
			} else {
				int i = (int)d * 2 + 1;
				return string + i + "x" + i;
			}
		}
	);
	public static final DoubleOption field_1940 = new DoubleOption(
		"options.chat.height.focused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, double_) -> {
			gameOptions.chatHeightFocused = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			return doubleOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
		}
	);
	public static final DoubleOption field_1939 = new DoubleOption(
		"options.chat.height.unfocused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, double_) -> {
			gameOptions.chatHeightUnfocused = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			return doubleOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
		}
	);
	public static final DoubleOption field_1921 = new DoubleOption(
		"options.chat.opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatOpacity, (gameOptions, double_) -> {
			gameOptions.chatOpacity = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			return doubleOption.getDisplayPrefix() + (int)(d * 90.0 + 10.0) + "%";
		}
	);
	public static final DoubleOption field_1946 = new DoubleOption(
		"options.chat.scale", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatScale, (gameOptions, double_) -> {
			gameOptions.chatScale = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			String string = doubleOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.off") : string + (int)(d * 100.0) + "%";
		}
	);
	public static final DoubleOption field_1941 = new DoubleOption(
		"options.chat.width", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatWidth, (gameOptions, double_) -> {
			gameOptions.chatWidth = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			return doubleOption.getDisplayPrefix() + ChatHud.getWidth(d) + "px";
		}
	);
	public static final DoubleOption field_1964 = new DoubleOption(
		"options.fov", 30.0, 110.0, 1.0F, gameOptions -> gameOptions.fov, (gameOptions, double_) -> gameOptions.fov = double_, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			String string = doubleOption.getDisplayPrefix();
			if (d == 70.0) {
				return string + I18n.translate("options.fov.min");
			} else {
				return d == doubleOption.getMax() ? string + I18n.translate("options.fov.max") : string + (int)d;
			}
		}
	);
	public static final DoubleOption field_1935 = new DoubleOption(
		"options.framerateLimit", 10.0, 260.0, 10.0F, gameOptions -> (double)gameOptions.maxFps, (gameOptions, double_) -> {
			gameOptions.maxFps = (int)double_.doubleValue();
			MinecraftClient.getInstance().window.setFramerateLimit(gameOptions.maxFps);
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			String string = doubleOption.getDisplayPrefix();
			return d == doubleOption.getMax() ? string + I18n.translate("options.framerateLimit.max") : string + I18n.translate("options.framerate", (int)d);
		}
	);
	public static final DoubleOption field_1931 = new DoubleOption(
		"options.fullscreen.resolution",
		0.0,
		0.0,
		1.0F,
		gameOptions -> (double)MinecraftClient.getInstance().window.method_4508(),
		(gameOptions, double_) -> MinecraftClient.getInstance().window.method_4505((int)double_.doubleValue()),
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			String string = doubleOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.fullscreen.current") : string + MinecraftClient.getInstance().window.method_4487((int)d - 1);
		}
	);
	public static final DoubleOption field_1945 = new DoubleOption(
		"options.gamma", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.gamma, (gameOptions, double_) -> gameOptions.gamma = double_, (gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			String string = doubleOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.gamma.min");
			} else {
				return d == 1.0 ? string + I18n.translate("options.gamma.max") : string + "+" + (int)(d * 100.0) + "%";
			}
		}
	);
	public static final DoubleOption field_18190 = new DoubleOption(
		"options.mipmapLevels",
		0.0,
		4.0,
		1.0F,
		gameOptions -> (double)gameOptions.mipmapLevels,
		(gameOptions, double_) -> gameOptions.mipmapLevels = (int)double_.doubleValue(),
		(gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			String string = doubleOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.off") : string + (int)d;
		}
	);
	public static final DoubleOption field_18191 = new LogarithmicOption(
		"options.mouseWheelSensitivity",
		0.01,
		10.0,
		0.01F,
		gameOptions -> gameOptions.mouseWheelSensitivity,
		(gameOptions, double_) -> gameOptions.mouseWheelSensitivity = double_,
		(gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			return doubleOption.getDisplayPrefix() + String.format("%.2f", doubleOption.method_18616(d));
		}
	);
	public static final DoubleOption field_1933 = new DoubleOption(
		"options.renderDistance", 2.0, 16.0, 1.0F, gameOptions -> (double)gameOptions.viewDistance, (gameOptions, double_) -> {
			gameOptions.viewDistance = (int)double_.doubleValue();
			MinecraftClient.getInstance().field_1769.scheduleTerrainUpdate();
		}, (gameOptions, doubleOption) -> {
			double d = doubleOption.get(gameOptions);
			return doubleOption.getDisplayPrefix() + I18n.translate("options.chunks", (int)d);
		}
	);
	public static final DoubleOption field_1944 = new DoubleOption(
		"options.sensitivity",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.mouseSensitivity,
		(gameOptions, double_) -> gameOptions.mouseSensitivity = double_,
		(gameOptions, doubleOption) -> {
			double d = doubleOption.method_18611(doubleOption.get(gameOptions));
			String string = doubleOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.sensitivity.min");
			} else {
				return d == 1.0 ? string + I18n.translate("options.sensitivity.max") : string + (int)(d * 200.0) + "%";
			}
		}
	);
	public static final DoubleOption field_18723 = new DoubleOption(
		"options.accessibility.text_background_opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, double_) -> {
			gameOptions.textBackgroundOpacity = double_;
			MinecraftClient.getInstance().field_1705.method_1743().reset();
		}, (gameOptions, doubleOption) -> doubleOption.getDisplayPrefix() + (int)(doubleOption.method_18611(doubleOption.get(gameOptions)) * 100.0) + "%"
	);
	public static final CyclingOption AO = new CyclingOption("options.ao", (gameOptions, integer) -> {
		gameOptions.ao = AoOption.getOption(gameOptions.ao.getValue() + integer);
		MinecraftClient.getInstance().field_1769.reload();
	}, (gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.ao.getTranslationKey()));
	public static final CyclingOption ATTACK_INDICATOR = new CyclingOption(
		"options.attackIndicator",
		(gameOptions, integer) -> gameOptions.attackIndicator = AttackIndicator.byId(gameOptions.attackIndicator.getId() + integer),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.attackIndicator.getTranslationKey())
	);
	public static final CyclingOption VISIBILITY = new CyclingOption(
		"options.chat.visibility",
		(gameOptions, integer) -> gameOptions.chatVisibility = ChatVisibility.byId((gameOptions.chatVisibility.getId() + integer) % 3),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.chatVisibility.getTranslationKey())
	);
	public static final CyclingOption GRAPHICS = new CyclingOption(
		"options.graphics",
		(gameOptions, integer) -> {
			gameOptions.fancyGraphics = !gameOptions.fancyGraphics;
			MinecraftClient.getInstance().field_1769.reload();
		},
		(gameOptions, cyclingOption) -> gameOptions.fancyGraphics
				? cyclingOption.getDisplayPrefix() + I18n.translate("options.graphics.fancy")
				: cyclingOption.getDisplayPrefix() + I18n.translate("options.graphics.fast")
	);
	public static final CyclingOption GUI_SCALE = new CyclingOption(
		"options.guiScale",
		(gameOptions, integer) -> gameOptions.guiScale = Integer.remainderUnsigned(
				gameOptions.guiScale + integer, MinecraftClient.getInstance().window.calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1
			),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix()
				+ (gameOptions.guiScale == 0 ? I18n.translate("options.guiScale.auto") : gameOptions.guiScale)
	);
	public static final CyclingOption MAIN_HAND = new CyclingOption(
		"options.mainHand",
		(gameOptions, integer) -> gameOptions.mainHand = gameOptions.mainHand.getOpposite(),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + gameOptions.mainHand
	);
	public static final CyclingOption NARRATOR = new CyclingOption(
		"options.narrator",
		(gameOptions, integer) -> {
			if (NarratorManager.INSTANCE.isActive()) {
				gameOptions.narrator = NarratorOption.byId(gameOptions.narrator.getId() + integer);
			} else {
				gameOptions.narrator = NarratorOption.OFF;
			}

			NarratorManager.INSTANCE.addToast(gameOptions.narrator);
		},
		(gameOptions, cyclingOption) -> NarratorManager.INSTANCE.isActive()
				? cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.narrator.getTranslationKey())
				: cyclingOption.getDisplayPrefix() + I18n.translate("options.narrator.notavailable")
	);
	public static final CyclingOption PARTICLES = new CyclingOption(
		"options.particles",
		(gameOptions, integer) -> gameOptions.field_1882 = ParticlesOption.byId(gameOptions.field_1882.getId() + integer),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.field_1882.getTranslationKey())
	);
	public static final CyclingOption CLOUDS = new CyclingOption(
		"options.renderClouds",
		(gameOptions, integer) -> gameOptions.cloudRenderMode = CloudRenderMode.getOption(gameOptions.cloudRenderMode.getValue() + integer),
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix() + I18n.translate(gameOptions.cloudRenderMode.getTranslationKey())
	);
	public static final CyclingOption TEXT_BACKGROUND = new CyclingOption(
		"options.accessibility.text_background",
		(gameOptions, integer) -> gameOptions.backgroundForChatOnly = !gameOptions.backgroundForChatOnly,
		(gameOptions, cyclingOption) -> cyclingOption.getDisplayPrefix()
				+ I18n.translate(gameOptions.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")
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
		if (MinecraftClient.getInstance().window != null) {
			MinecraftClient.getInstance().window.setVsync(gameOptions.enableVsync);
		}
	});
	public static final BooleanOption ENTITY_SHADOWS = new BooleanOption(
		"options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, boolean_) -> gameOptions.entityShadows = boolean_
	);
	public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption(
		"options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, boolean_) -> {
			gameOptions.forceUnicodeFont = boolean_;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.method_1568() != null) {
				minecraftClient.method_1568().setForceUnicodeFont(gameOptions.forceUnicodeFont, SystemUtil.getServerWorkerExecutor(), minecraftClient);
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
	public static final BooleanOption TOUCHSCREEN = new BooleanOption(
		"options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, boolean_) -> gameOptions.touchscreen = boolean_
	);
	public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, boolean_) -> {
		gameOptions.fullscreen = boolean_;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.window != null && minecraftClient.window.isFullscreen() != gameOptions.fullscreen) {
			minecraftClient.window.toggleFullscreen();
			gameOptions.fullscreen = minecraftClient.window.isFullscreen();
		}
	});
	public static final BooleanOption VIEW_BOBBING = new BooleanOption(
		"options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, boolean_) -> gameOptions.bobView = boolean_
	);
	private final String key;

	public Option(String string) {
		this.key = string;
	}

	public abstract AbstractButtonWidget method_18520(GameOptions gameOptions, int i, int j, int k);

	public String getDisplayPrefix() {
		return I18n.translate(this.key) + ": ";
	}
}
