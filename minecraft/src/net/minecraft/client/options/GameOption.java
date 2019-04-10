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
public abstract class GameOption {
	public static final DoubleGameOption BIOME_BLEND_RADIUS = new DoubleGameOption(
		"options.biomeBlendRadius", 0.0, 7.0, 1.0F, gameOptions -> (double)gameOptions.biomeBlendRadius, (gameOptions, double_) -> {
			gameOptions.biomeBlendRadius = MathHelper.clamp((int)double_.doubleValue(), 0, 7);
			MinecraftClient.getInstance().worldRenderer.reload();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			String string = doubleGameOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.off");
			} else {
				int i = (int)d * 2 + 1;
				return string + i + "x" + i;
			}
		}
	);
	public static final DoubleGameOption CHAT_HEIGHT_FOCUSED = new DoubleGameOption(
		"options.chat.height.focused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, double_) -> {
			gameOptions.chatHeightFocused = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			return doubleGameOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
		}
	);
	public static final DoubleGameOption SATURATION = new DoubleGameOption(
		"options.chat.height.unfocused", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, double_) -> {
			gameOptions.chatHeightUnfocused = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			return doubleGameOption.getDisplayPrefix() + ChatHud.getHeight(d) + "px";
		}
	);
	public static final DoubleGameOption CHAT_OPACITY = new DoubleGameOption(
		"options.chat.opacity", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatOpacity, (gameOptions, double_) -> {
			gameOptions.chatOpacity = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			return doubleGameOption.getDisplayPrefix() + (int)(d * 90.0 + 10.0) + "%";
		}
	);
	public static final DoubleGameOption CHAT_SCALE = new DoubleGameOption(
		"options.chat.scale", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatScale, (gameOptions, double_) -> {
			gameOptions.chatScale = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			String string = doubleGameOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.off") : string + (int)(d * 100.0) + "%";
		}
	);
	public static final DoubleGameOption CHAT_WIDTH = new DoubleGameOption(
		"options.chat.width", 0.0, 1.0, 0.0F, gameOptions -> gameOptions.chatWidth, (gameOptions, double_) -> {
			gameOptions.chatWidth = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			return doubleGameOption.getDisplayPrefix() + ChatHud.getWidth(d) + "px";
		}
	);
	public static final DoubleGameOption FOV = new DoubleGameOption(
		"options.fov", 30.0, 110.0, 1.0F, gameOptions -> gameOptions.fov, (gameOptions, double_) -> gameOptions.fov = double_, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			String string = doubleGameOption.getDisplayPrefix();
			if (d == 70.0) {
				return string + I18n.translate("options.fov.min");
			} else {
				return d == doubleGameOption.getMax() ? string + I18n.translate("options.fov.max") : string + (int)d;
			}
		}
	);
	public static final DoubleGameOption FRAMERATE_LIMIT = new DoubleGameOption(
		"options.framerateLimit", 10.0, 260.0, 10.0F, gameOptions -> (double)gameOptions.maxFps, (gameOptions, double_) -> {
			gameOptions.maxFps = (int)double_.doubleValue();
			MinecraftClient.getInstance().window.setFramerateLimit(gameOptions.maxFps);
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			String string = doubleGameOption.getDisplayPrefix();
			return d == doubleGameOption.getMax() ? string + I18n.translate("options.framerateLimit.max") : string + I18n.translate("options.framerate", (int)d);
		}
	);
	public static final DoubleGameOption FULLSCREEN_RESOLUTION = new DoubleGameOption(
		"options.fullscreen.resolution",
		0.0,
		0.0,
		1.0F,
		gameOptions -> (double)MinecraftClient.getInstance().window.method_4508(),
		(gameOptions, double_) -> MinecraftClient.getInstance().window.method_4505((int)double_.doubleValue()),
		(gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			String string = doubleGameOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.fullscreen.current") : string + MinecraftClient.getInstance().window.method_4487((int)d - 1);
		}
	);
	public static final DoubleGameOption GAMMA = new DoubleGameOption(
		"options.gamma",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.gamma,
		(gameOptions, double_) -> gameOptions.gamma = double_,
		(gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			String string = doubleGameOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.gamma.min");
			} else {
				return d == 1.0 ? string + I18n.translate("options.gamma.max") : string + "+" + (int)(d * 100.0) + "%";
			}
		}
	);
	public static final DoubleGameOption MIPMAP_LEVELS = new DoubleGameOption(
		"options.mipmapLevels",
		0.0,
		4.0,
		1.0F,
		gameOptions -> (double)gameOptions.mipmapLevels,
		(gameOptions, double_) -> gameOptions.mipmapLevels = (int)double_.doubleValue(),
		(gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			String string = doubleGameOption.getDisplayPrefix();
			return d == 0.0 ? string + I18n.translate("options.off") : string + (int)d;
		}
	);
	public static final DoubleGameOption MOUSE_WHEEL_SENSITIVITY = new DoubleGameOption(
		"options.mouseWheelSensitivity",
		1.0,
		10.0,
		0.5F,
		gameOptions -> gameOptions.mouseWheelSensitivity,
		(gameOptions, double_) -> gameOptions.mouseWheelSensitivity = double_,
		(gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			String string = doubleGameOption.getDisplayPrefix();
			return d == 1.0 ? string + I18n.translate("options.mouseWheelSensitivity.default") : string + "+" + (int)d + "." + (int)(d * 10.0) % 10;
		}
	);
	public static final DoubleGameOption RENDER_DISTANCE = new DoubleGameOption(
		"options.renderDistance", 2.0, 16.0, 1.0F, gameOptions -> (double)gameOptions.viewDistance, (gameOptions, double_) -> {
			gameOptions.viewDistance = (int)double_.doubleValue();
			MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
		}, (gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.get(gameOptions);
			return doubleGameOption.getDisplayPrefix() + I18n.translate("options.chunks", (int)d);
		}
	);
	public static final DoubleGameOption SENSITIVITY = new DoubleGameOption(
		"options.sensitivity",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.mouseSensitivity,
		(gameOptions, double_) -> gameOptions.mouseSensitivity = double_,
		(gameOptions, doubleGameOption) -> {
			double d = doubleGameOption.method_18611(doubleGameOption.get(gameOptions));
			String string = doubleGameOption.getDisplayPrefix();
			if (d == 0.0) {
				return string + I18n.translate("options.sensitivity.min");
			} else {
				return d == 1.0 ? string + I18n.translate("options.sensitivity.max") : string + (int)(d * 200.0) + "%";
			}
		}
	);
	public static final DoubleGameOption TEXT_BACKGROUND_OPACITY = new DoubleGameOption(
		"options.accessibility.text_background_opacity",
		0.0,
		1.0,
		0.0F,
		gameOptions -> gameOptions.textBackgroundOpacity,
		(gameOptions, double_) -> {
			gameOptions.textBackgroundOpacity = double_;
			MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		},
		(gameOptions, doubleGameOption) -> doubleGameOption.getDisplayPrefix()
				+ (int)(doubleGameOption.method_18611(doubleGameOption.get(gameOptions)) * 100.0)
				+ "%"
	);
	public static final StringGameOption AO = new StringGameOption("options.ao", (gameOptions, integer) -> {
		gameOptions.ao = AoOption.getOption(gameOptions.ao.getValue() + integer);
		MinecraftClient.getInstance().worldRenderer.reload();
	}, (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.ao.getTranslationKey()));
	public static final StringGameOption ATTACK_INDICATOR = new StringGameOption(
		"options.attackIndicator",
		(gameOptions, integer) -> gameOptions.attackIndicator = AttackIndicator.byId(gameOptions.attackIndicator.getId() + integer),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.attackIndicator.getTranslationKey())
	);
	public static final StringGameOption VISIBILITY = new StringGameOption(
		"options.chat.visibility",
		(gameOptions, integer) -> gameOptions.chatVisibility = ChatVisibility.byId((gameOptions.chatVisibility.getId() + integer) % 3),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.chatVisibility.getTranslationKey())
	);
	public static final StringGameOption GRAPHICS = new StringGameOption(
		"options.graphics",
		(gameOptions, integer) -> {
			gameOptions.fancyGraphics = !gameOptions.fancyGraphics;
			MinecraftClient.getInstance().worldRenderer.reload();
		},
		(gameOptions, stringGameOption) -> gameOptions.fancyGraphics
				? stringGameOption.getDisplayPrefix() + I18n.translate("options.graphics.fancy")
				: stringGameOption.getDisplayPrefix() + I18n.translate("options.graphics.fast")
	);
	public static final StringGameOption GUI_SCALE = new StringGameOption(
		"options.guiScale",
		(gameOptions, integer) -> gameOptions.guiScale = Integer.remainderUnsigned(
				gameOptions.guiScale + integer, MinecraftClient.getInstance().window.calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1
			),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix()
				+ (gameOptions.guiScale == 0 ? I18n.translate("options.guiScale.auto") : gameOptions.guiScale)
	);
	public static final StringGameOption MAIN_HAND = new StringGameOption(
		"options.mainHand",
		(gameOptions, integer) -> gameOptions.mainHand = gameOptions.mainHand.getOpposite(),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + gameOptions.mainHand
	);
	public static final StringGameOption NARRATOR = new StringGameOption(
		"options.narrator",
		(gameOptions, integer) -> {
			if (NarratorManager.INSTANCE.isActive()) {
				gameOptions.narrator = NarratorOption.byId(gameOptions.narrator.getId() + integer);
			} else {
				gameOptions.narrator = NarratorOption.field_18176;
			}

			NarratorManager.INSTANCE.addToast(gameOptions.narrator);
		},
		(gameOptions, stringGameOption) -> NarratorManager.INSTANCE.isActive()
				? stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.narrator.getTranslationKey())
				: stringGameOption.getDisplayPrefix() + I18n.translate("options.narrator.notavailable")
	);
	public static final StringGameOption PARTICLES = new StringGameOption(
		"options.particles",
		(gameOptions, integer) -> gameOptions.particles = ParticlesOption.byId(gameOptions.particles.getId() + integer),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.particles.getTranslationKey())
	);
	public static final StringGameOption CLOUDS = new StringGameOption(
		"options.renderClouds",
		(gameOptions, integer) -> gameOptions.cloudRenderMode = CloudRenderMode.fromId(gameOptions.cloudRenderMode.getRenderModeId() + integer),
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.cloudRenderMode.getRenderMode())
	);
	public static final StringGameOption TEXT_BACKGROUND = new StringGameOption(
		"options.accessibility.text_background",
		(gameOptions, integer) -> gameOptions.backgroundForChatOnly = !gameOptions.backgroundForChatOnly,
		(gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix()
				+ I18n.translate(gameOptions.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")
	);
	public static final BooleanGameOption AUTO_JUMP = new BooleanGameOption(
		"options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, boolean_) -> gameOptions.autoJump = boolean_
	);
	public static final BooleanGameOption AUTO_SUGGESTIONS = new BooleanGameOption(
		"options.autoSuggestCommands", gameOptions -> gameOptions.autoSuggestions, (gameOptions, boolean_) -> gameOptions.autoSuggestions = boolean_
	);
	public static final BooleanGameOption CHAT_COLOR = new BooleanGameOption(
		"options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, boolean_) -> gameOptions.chatColors = boolean_
	);
	public static final BooleanGameOption CHAT_LINKS = new BooleanGameOption(
		"options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, boolean_) -> gameOptions.chatLinks = boolean_
	);
	public static final BooleanGameOption CHAT_LINKS_PROMPT = new BooleanGameOption(
		"options.chat.links.prompt", gameOptions -> gameOptions.chatLinksPrompt, (gameOptions, boolean_) -> gameOptions.chatLinksPrompt = boolean_
	);
	public static final BooleanGameOption VSYNC = new BooleanGameOption("options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, boolean_) -> {
		gameOptions.enableVsync = boolean_;
		if (MinecraftClient.getInstance().window != null) {
			MinecraftClient.getInstance().window.setVsync(gameOptions.enableVsync);
		}
	});
	public static final BooleanGameOption ENTITY_SHADOWS = new BooleanGameOption(
		"options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, boolean_) -> gameOptions.entityShadows = boolean_
	);
	public static final BooleanGameOption FORCE_UNICODE_FONT = new BooleanGameOption(
		"options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, boolean_) -> {
			gameOptions.forceUnicodeFont = boolean_;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.getFontManager() != null) {
				minecraftClient.getFontManager().setForceUnicodeFont(gameOptions.forceUnicodeFont, SystemUtil.getServerWorkerExecutor(), minecraftClient);
			}
		}
	);
	public static final BooleanGameOption INVERT_MOUSE = new BooleanGameOption(
		"options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, boolean_) -> gameOptions.invertYMouse = boolean_
	);
	public static final BooleanGameOption REALMS_NOTIFICATIONS = new BooleanGameOption(
		"options.realmsNotifications", gameOptions -> gameOptions.realmsNotifications, (gameOptions, boolean_) -> gameOptions.realmsNotifications = boolean_
	);
	public static final BooleanGameOption REDUCED_DEBUG_INFO = new BooleanGameOption(
		"options.reducedDebugInfo", gameOptions -> gameOptions.reducedDebugInfo, (gameOptions, boolean_) -> gameOptions.reducedDebugInfo = boolean_
	);
	public static final BooleanGameOption SUBTITLES = new BooleanGameOption(
		"options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, boolean_) -> gameOptions.showSubtitles = boolean_
	);
	public static final BooleanGameOption SNOOPER = new BooleanGameOption("options.snooper", gameOptions -> {
		if (gameOptions.snooperEnabled) {
		}

		return false;
	}, (gameOptions, boolean_) -> gameOptions.snooperEnabled = boolean_);
	public static final BooleanGameOption TOUCHSCREEN = new BooleanGameOption(
		"options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, boolean_) -> gameOptions.touchscreen = boolean_
	);
	public static final BooleanGameOption FULLSCREEN = new BooleanGameOption(
		"options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, boolean_) -> {
			gameOptions.fullscreen = boolean_;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.window != null && minecraftClient.window.isFullscreen() != gameOptions.fullscreen) {
				minecraftClient.window.toggleFullscreen();
				gameOptions.fullscreen = minecraftClient.window.isFullscreen();
			}
		}
	);
	public static final BooleanGameOption VIEW_BOBBING = new BooleanGameOption(
		"options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, boolean_) -> gameOptions.bobView = boolean_
	);
	private final String key;

	public GameOption(String string) {
		this.key = string;
	}

	public abstract AbstractButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k);

	public String getDisplayPrefix() {
		return I18n.translate(this.key) + ": ";
	}
}
