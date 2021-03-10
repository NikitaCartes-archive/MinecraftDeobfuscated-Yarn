/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.option.AoMode;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.LogarithmicOption;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class Option {
    public static final DoubleOption BIOME_BLEND_RADIUS = new DoubleOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, gameOptions -> gameOptions.biomeBlendRadius, (gameOptions, biomeBlendRadius) -> {
        gameOptions.biomeBlendRadius = MathHelper.clamp((int)biomeBlendRadius.doubleValue(), 0, 7);
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        int i = (int)d * 2 + 1;
        return option.getGenericLabel(new TranslatableText("options.biomeBlendRadius." + i));
    });
    public static final DoubleOption CHAT_HEIGHT_FOCUSED = new DoubleOption("options.chat.height.focused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, chatHeightFocused) -> {
        gameOptions.chatHeightFocused = chatHeightFocused;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        return option.getPixelLabel(ChatHud.getHeight(d));
    });
    public static final DoubleOption SATURATION = new DoubleOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, chatHeightUnfocused) -> {
        gameOptions.chatHeightUnfocused = chatHeightUnfocused;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        return option.getPixelLabel(ChatHud.getHeight(d));
    });
    public static final DoubleOption CHAT_OPACITY = new DoubleOption("options.chat.opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatOpacity, (gameOptions, chatOpacity) -> {
        gameOptions.chatOpacity = chatOpacity;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        return option.getPercentLabel(d * 0.9 + 0.1);
    });
    public static final DoubleOption CHAT_SCALE = new DoubleOption("options.chat.scale", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatScale, (gameOptions, chatScale) -> {
        gameOptions.chatScale = chatScale;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        if (d == 0.0) {
            return ScreenTexts.composeToggleText(option.getDisplayPrefix(), false);
        }
        return option.getPercentLabel(d);
    });
    public static final DoubleOption CHAT_WIDTH = new DoubleOption("options.chat.width", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatWidth, (gameOptions, chatWidth) -> {
        gameOptions.chatWidth = chatWidth;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        return option.getPixelLabel(ChatHud.getWidth(d));
    });
    public static final DoubleOption CHAT_LINE_SPACING = new DoubleOption("options.chat.line_spacing", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatLineSpacing, (gameOptions, chatLineSpacing) -> {
        gameOptions.chatLineSpacing = chatLineSpacing;
    }, (gameOptions, option) -> option.getPercentLabel(option.getRatio(option.get((GameOptions)gameOptions))));
    public static final DoubleOption CHAT_DELAY_INSTANT = new DoubleOption("options.chat.delay_instant", 0.0, 6.0, 0.1f, gameOptions -> gameOptions.chatDelay, (gameOptions, chatDelay) -> {
        gameOptions.chatDelay = chatDelay;
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        if (d <= 0.0) {
            return new TranslatableText("options.chat.delay_none");
        }
        return new TranslatableText("options.chat.delay", String.format("%.1f", d));
    });
    public static final DoubleOption FOV = new DoubleOption("options.fov", 30.0, 110.0, 1.0f, gameOptions -> gameOptions.fov, (gameOptions, fov) -> {
        gameOptions.fov = fov;
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        if (d == 70.0) {
            return option.getGenericLabel(new TranslatableText("options.fov.min"));
        }
        if (d == option.getMax()) {
            return option.getGenericLabel(new TranslatableText("options.fov.max"));
        }
        return option.getGenericLabel((int)d);
    });
    private static final Text FOV_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.fovEffectScale.tooltip");
    public static final DoubleOption FOV_EFFECT_SCALE = new DoubleOption("options.fovEffectScale", 0.0, 1.0, 0.0f, gameOptions -> Math.pow(gameOptions.fovEffectScale, 2.0), (gameOptions, fovEffectScale) -> {
        gameOptions.fovEffectScale = MathHelper.sqrt(fovEffectScale);
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        if (d == 0.0) {
            return option.getGenericLabel(ScreenTexts.OFF);
        }
        return option.getPercentLabel(d);
    }, client -> client.textRenderer.wrapLines(FOV_EFFECT_SCALE_TOOLTIP, 200));
    private static final Text DISTORTION_EFFECT_SCALE_TOOLTIP = new TranslatableText("options.screenEffectScale.tooltip");
    public static final DoubleOption DISTORTION_EFFECT_SCALE = new DoubleOption("options.screenEffectScale", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.distortionEffectScale, (gameOptions, distortionEffectScale) -> {
        gameOptions.distortionEffectScale = distortionEffectScale.floatValue();
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        if (d == 0.0) {
            return option.getGenericLabel(ScreenTexts.OFF);
        }
        return option.getPercentLabel(d);
    }, client -> client.textRenderer.wrapLines(DISTORTION_EFFECT_SCALE_TOOLTIP, 200));
    public static final DoubleOption FRAMERATE_LIMIT = new DoubleOption("options.framerateLimit", 10.0, 260.0, 10.0f, gameOptions -> gameOptions.maxFps, (gameOptions, maxFps) -> {
        gameOptions.maxFps = (int)maxFps.doubleValue();
        MinecraftClient.getInstance().getWindow().setFramerateLimit(gameOptions.maxFps);
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        if (d == option.getMax()) {
            return option.getGenericLabel(new TranslatableText("options.framerateLimit.max"));
        }
        return option.getGenericLabel(new TranslatableText("options.framerate", (int)d));
    });
    public static final DoubleOption GAMMA = new DoubleOption("options.gamma", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.gamma, (gameOptions, gamma) -> {
        gameOptions.gamma = gamma;
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        if (d == 0.0) {
            return option.getGenericLabel(new TranslatableText("options.gamma.min"));
        }
        if (d == 1.0) {
            return option.getGenericLabel(new TranslatableText("options.gamma.max"));
        }
        return option.getPercentAdditionLabel((int)(d * 100.0));
    });
    public static final DoubleOption MIPMAP_LEVELS = new DoubleOption("options.mipmapLevels", 0.0, 4.0, 1.0f, gameOptions -> gameOptions.mipmapLevels, (gameOptions, mipmapLevels) -> {
        gameOptions.mipmapLevels = (int)mipmapLevels.doubleValue();
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        if (d == 0.0) {
            return ScreenTexts.composeToggleText(option.getDisplayPrefix(), false);
        }
        return option.getGenericLabel((int)d);
    });
    public static final DoubleOption MOUSE_WHEEL_SENSITIVITY = new LogarithmicOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, gameOptions -> gameOptions.mouseWheelSensitivity, (gameOptions, mouseWheelSensitivity) -> {
        gameOptions.mouseWheelSensitivity = mouseWheelSensitivity;
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        return option.getGenericLabel(new LiteralText(String.format("%.2f", option.getValue(d))));
    });
    public static final CyclingOption<Boolean> RAW_MOUSE_INPUT = CyclingOption.create("options.rawMouseInput", gameOptions -> gameOptions.rawMouseInput, (gameOptions, option, rawMouseInput) -> {
        gameOptions.rawMouseInput = rawMouseInput;
        Window window = MinecraftClient.getInstance().getWindow();
        if (window != null) {
            window.setRawMouseMotion((boolean)rawMouseInput);
        }
    });
    public static final DoubleOption RENDER_DISTANCE = new DoubleOption("options.renderDistance", 2.0, 16.0, 1.0f, gameOptions -> gameOptions.viewDistance, (gameOptions, viewDistance) -> {
        gameOptions.viewDistance = (int)viewDistance.doubleValue();
        MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        return option.getGenericLabel(new TranslatableText("options.chunks", (int)d));
    });
    public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption("options.entityDistanceScaling", 0.5, 5.0, 0.25f, gameOptions -> gameOptions.entityDistanceScaling, (gameOptions, entityDistanceScaling) -> {
        gameOptions.entityDistanceScaling = (float)entityDistanceScaling.doubleValue();
    }, (gameOptions, option) -> {
        double d = option.get((GameOptions)gameOptions);
        return option.getPercentLabel(d);
    });
    public static final DoubleOption SENSITIVITY = new DoubleOption("options.sensitivity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.mouseSensitivity, (gameOptions, mouseSensitivity) -> {
        gameOptions.mouseSensitivity = mouseSensitivity;
    }, (gameOptions, option) -> {
        double d = option.getRatio(option.get((GameOptions)gameOptions));
        if (d == 0.0) {
            return option.getGenericLabel(new TranslatableText("options.sensitivity.min"));
        }
        if (d == 1.0) {
            return option.getGenericLabel(new TranslatableText("options.sensitivity.max"));
        }
        return option.getPercentLabel(2.0 * d);
    });
    public static final DoubleOption TEXT_BACKGROUND_OPACITY = new DoubleOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, textBackgroundOpacity) -> {
        gameOptions.textBackgroundOpacity = textBackgroundOpacity;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (gameOptions, option) -> option.getPercentLabel(option.getRatio(option.get((GameOptions)gameOptions))));
    public static final CyclingOption<AoMode> AO = CyclingOption.create("options.ao", AoMode.values(), aoMode -> new TranslatableText(aoMode.getTranslationKey()), gameOptions -> gameOptions.ao, (gameOptions, option, aoMode) -> {
        gameOptions.ao = aoMode;
        MinecraftClient.getInstance().worldRenderer.reload();
    });
    public static final CyclingOption<AttackIndicator> ATTACK_INDICATOR = CyclingOption.create("options.attackIndicator", AttackIndicator.values(), attackIndicator -> new TranslatableText(attackIndicator.getTranslationKey()), gameOptions -> gameOptions.attackIndicator, (gameOptions, option, attackIndicator) -> {
        gameOptions.attackIndicator = attackIndicator;
    });
    public static final CyclingOption<ChatVisibility> VISIBILITY = CyclingOption.create("options.chat.visibility", ChatVisibility.values(), chatVisibility -> new TranslatableText(chatVisibility.getTranslationKey()), gameOptions -> gameOptions.chatVisibility, (gameOptions, option, chatVisibility) -> {
        gameOptions.chatVisibility = chatVisibility;
    });
    private static final Text FAST_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fast.tooltip");
    private static final Text FABULOUS_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fabulous.tooltip", new TranslatableText("options.graphics.fabulous").formatted(Formatting.ITALIC));
    private static final Text FANCY_GRAPHICS_TOOLTIP = new TranslatableText("options.graphics.fancy.tooltip");
    public static final CyclingOption<GraphicsMode> GRAPHICS = CyclingOption.create("options.graphics", Arrays.asList(GraphicsMode.values()), Stream.of(GraphicsMode.values()).filter(graphicsMode -> graphicsMode != GraphicsMode.FABULOUS).collect(Collectors.toList()), () -> MinecraftClient.getInstance().getVideoWarningManager().hasCancelledAfterWarning(), graphicsMode -> {
        TranslatableText mutableText = new TranslatableText(graphicsMode.getTranslationKey());
        if (graphicsMode == GraphicsMode.FABULOUS) {
            return mutableText.formatted(Formatting.ITALIC);
        }
        return mutableText;
    }, gameOptions -> gameOptions.graphicsMode, (gameOptions, option, graphicsMode) -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
        if (graphicsMode == GraphicsMode.FABULOUS && videoWarningManager.canWarn()) {
            videoWarningManager.scheduleWarning();
            return;
        }
        gameOptions.graphicsMode = graphicsMode;
        minecraftClient.worldRenderer.reload();
    }).tooltip(client -> {
        List<OrderedText> list = client.textRenderer.wrapLines(FAST_GRAPHICS_TOOLTIP, 200);
        List<OrderedText> list2 = client.textRenderer.wrapLines(FANCY_GRAPHICS_TOOLTIP, 200);
        List<OrderedText> list3 = client.textRenderer.wrapLines(FABULOUS_GRAPHICS_TOOLTIP, 200);
        return graphicsMode -> {
            switch (graphicsMode) {
                case FANCY: {
                    return list2;
                }
                case FAST: {
                    return list;
                }
                case FABULOUS: {
                    return list3;
                }
            }
            return ImmutableList.of();
        };
    });
    public static final CyclingOption GUI_SCALE = CyclingOption.create("options.guiScale", () -> IntStream.rangeClosed(0, MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont())).boxed().collect(Collectors.toList()), guiScale -> guiScale == 0 ? new TranslatableText("options.guiScale.auto") : new LiteralText(Integer.toString(guiScale)), gameOptions -> gameOptions.guiScale, (gameOptions, option, guiScale) -> {
        gameOptions.guiScale = guiScale;
    });
    public static final CyclingOption<Arm> MAIN_HAND = CyclingOption.create("options.mainHand", Arm.values(), Arm::getOptionName, gameOptions -> gameOptions.mainArm, (gameOptions, option, mainArm) -> {
        gameOptions.mainArm = mainArm;
        gameOptions.onPlayerModelPartChange();
    });
    public static final CyclingOption<NarratorMode> NARRATOR = CyclingOption.create("options.narrator", NarratorMode.values(), narrator -> {
        if (NarratorManager.INSTANCE.isActive()) {
            return narrator.getName();
        }
        return new TranslatableText("options.narrator.notavailable");
    }, gameOptions -> gameOptions.narrator, (gameOptions, option, narrator) -> {
        gameOptions.narrator = narrator;
        NarratorManager.INSTANCE.addToast((NarratorMode)((Object)narrator));
    });
    public static final CyclingOption<ParticlesMode> PARTICLES = CyclingOption.create("options.particles", ParticlesMode.values(), particlesMode -> new TranslatableText(particlesMode.getTranslationKey()), gameOptions -> gameOptions.particles, (gameOptions, option, particlesMode) -> {
        gameOptions.particles = particlesMode;
    });
    public static final CyclingOption<CloudRenderMode> CLOUDS = CyclingOption.create("options.renderClouds", CloudRenderMode.values(), cloudRenderMode -> new TranslatableText(cloudRenderMode.getTranslationKey()), gameOptions -> gameOptions.cloudRenderMode, (gameOptions, option, cloudRenderMode) -> {
        Framebuffer framebuffer;
        gameOptions.cloudRenderMode = cloudRenderMode;
        if (MinecraftClient.isFabulousGraphicsOrBetter() && (framebuffer = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer()) != null) {
            framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        }
    });
    public static final CyclingOption<Boolean> TEXT_BACKGROUND = CyclingOption.create("options.accessibility.text_background", new TranslatableText("options.accessibility.text_background.chat"), new TranslatableText("options.accessibility.text_background.everywhere"), gameOptions -> gameOptions.backgroundForChatOnly, (gameOptions, option, backgroundForChatOnly) -> {
        gameOptions.backgroundForChatOnly = backgroundForChatOnly;
    });
    private static final Text HIDE_MATCHED_NAMES_TOOLTIP = new TranslatableText("options.hideMatchedNames.tooltip");
    public static final CyclingOption<Boolean> AUTO_JUMP = CyclingOption.create("options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, option, autoJump) -> {
        gameOptions.autoJump = autoJump;
    });
    public static final CyclingOption<Boolean> AUTO_SUGGESTIONS = CyclingOption.create("options.autoSuggestCommands", gameOptions -> gameOptions.autoSuggestions, (gameOptions, option, autoSuggestions) -> {
        gameOptions.autoSuggestions = autoSuggestions;
    });
    public static final CyclingOption<Boolean> CHAT_COLOR = CyclingOption.create("options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, option, chatColors) -> {
        gameOptions.chatColors = chatColors;
    });
    public static final CyclingOption<Boolean> HIDE_MATCHED_NAMES = CyclingOption.create("options.hideMatchedNames", HIDE_MATCHED_NAMES_TOOLTIP, gameOptions -> gameOptions.hideMatchedNames, (gameOptions, option, hideMatchedNames) -> {
        gameOptions.hideMatchedNames = hideMatchedNames;
    });
    public static final CyclingOption<Boolean> CHAT_LINKS = CyclingOption.create("options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, option, chatLinks) -> {
        gameOptions.chatLinks = chatLinks;
    });
    public static final CyclingOption<Boolean> CHAT_LINKS_PROMPT = CyclingOption.create("options.chat.links.prompt", gameOptions -> gameOptions.chatLinksPrompt, (gameOptions, option, chatLinksPrompt) -> {
        gameOptions.chatLinksPrompt = chatLinksPrompt;
    });
    public static final CyclingOption<Boolean> DISCRETE_MOUSE_SCROLL = CyclingOption.create("options.discrete_mouse_scroll", gameOptions -> gameOptions.discreteMouseScroll, (gameOptions, option, discreteMouseScroll) -> {
        gameOptions.discreteMouseScroll = discreteMouseScroll;
    });
    public static final CyclingOption<Boolean> VSYNC = CyclingOption.create("options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, option, enableVsync) -> {
        gameOptions.enableVsync = enableVsync;
        if (MinecraftClient.getInstance().getWindow() != null) {
            MinecraftClient.getInstance().getWindow().setVsync(gameOptions.enableVsync);
        }
    });
    public static final CyclingOption<Boolean> ENTITY_SHADOWS = CyclingOption.create("options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, option, entityShadows) -> {
        gameOptions.entityShadows = entityShadows;
    });
    public static final CyclingOption<Boolean> FORCE_UNICODE_FONT = CyclingOption.create("options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, option, forceUnicodeFont) -> {
        gameOptions.forceUnicodeFont = forceUnicodeFont;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null) {
            minecraftClient.initFont((boolean)forceUnicodeFont);
        }
    });
    public static final CyclingOption<Boolean> INVERT_MOUSE = CyclingOption.create("options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, option, invertYMouse) -> {
        gameOptions.invertYMouse = invertYMouse;
    });
    public static final CyclingOption<Boolean> REALMS_NOTIFICATIONS = CyclingOption.create("options.realmsNotifications", gameOptions -> gameOptions.realmsNotifications, (gameOptions, option, realmsNotifications) -> {
        gameOptions.realmsNotifications = realmsNotifications;
    });
    public static final CyclingOption<Boolean> REDUCED_DEBUG_INFO = CyclingOption.create("options.reducedDebugInfo", gameOptions -> gameOptions.reducedDebugInfo, (gameOptions, option, reducedDebugInfo) -> {
        gameOptions.reducedDebugInfo = reducedDebugInfo;
    });
    public static final CyclingOption<Boolean> SUBTITLES = CyclingOption.create("options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, option, showSubtitles) -> {
        gameOptions.showSubtitles = showSubtitles;
    });
    public static final CyclingOption<Boolean> SNOOPER = CyclingOption.create("options.snooper", gameOptions -> {
        if (gameOptions.snooperEnabled) {
            // empty if block
        }
        return false;
    }, (gameOptions, option, snooperEnabled) -> {
        gameOptions.snooperEnabled = snooperEnabled;
    });
    private static final Text TOGGLE_TEXT = new TranslatableText("options.key.toggle");
    private static final Text HOLD_TEXT = new TranslatableText("options.key.hold");
    public static final CyclingOption<Boolean> SNEAK_TOGGLED = CyclingOption.create("key.sneak", TOGGLE_TEXT, HOLD_TEXT, gameOptions -> gameOptions.sneakToggled, (gameOptions, option, sneakToggled) -> {
        gameOptions.sneakToggled = sneakToggled;
    });
    public static final CyclingOption<Boolean> SPRINT_TOGGLED = CyclingOption.create("key.sprint", TOGGLE_TEXT, HOLD_TEXT, gameOptions -> gameOptions.sprintToggled, (gameOptions, option, sprintToggled) -> {
        gameOptions.sprintToggled = sprintToggled;
    });
    public static final CyclingOption<Boolean> TOUCHSCREEN = CyclingOption.create("options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, option, touchscreen) -> {
        gameOptions.touchscreen = touchscreen;
    });
    public static final CyclingOption<Boolean> FULLSCREEN = CyclingOption.create("options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, option, fullscreen) -> {
        gameOptions.fullscreen = fullscreen;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != gameOptions.fullscreen) {
            minecraftClient.getWindow().toggleFullscreen();
            gameOptions.fullscreen = minecraftClient.getWindow().isFullscreen();
        }
    });
    public static final CyclingOption<Boolean> VIEW_BOBBING = CyclingOption.create("options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, option, bobView) -> {
        gameOptions.bobView = bobView;
    });
    private final Text key;

    public Option(String key) {
        this.key = new TranslatableText(key);
    }

    public abstract AbstractButtonWidget createButton(GameOptions var1, int var2, int var3, int var4);

    protected Text getDisplayPrefix() {
        return this.key;
    }

    protected Text getPixelLabel(int pixel) {
        return new TranslatableText("options.pixel_value", this.getDisplayPrefix(), pixel);
    }

    protected Text getPercentLabel(double proportion) {
        return new TranslatableText("options.percent_value", this.getDisplayPrefix(), (int)(proportion * 100.0));
    }

    protected Text getPercentAdditionLabel(int percentage) {
        return new TranslatableText("options.percent_add_value", this.getDisplayPrefix(), percentage);
    }

    protected Text getGenericLabel(Text value) {
        return new TranslatableText("options.generic_value", this.getDisplayPrefix(), value);
    }

    protected Text getGenericLabel(int value) {
        return this.getGenericLabel(new LiteralText(Integer.toString(value)));
    }
}

