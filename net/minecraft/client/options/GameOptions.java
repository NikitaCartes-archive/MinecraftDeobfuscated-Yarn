/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.AoMode;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.NarratorMode;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.ParticlesMode;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.options.StickyKeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameOptions {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> STRING_LIST_TYPE = new TypeToken<List<String>>(){};
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    public double mouseSensitivity = 0.5;
    public int viewDistance = -1;
    public float entityDistanceScaling = 1.0f;
    public int maxFps = 120;
    public CloudRenderMode cloudRenderMode = CloudRenderMode.FANCY;
    public GraphicsMode graphicsMode = GraphicsMode.FANCY;
    public AoMode ao = AoMode.MAX;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public ChatVisibility chatVisibility = ChatVisibility.FULL;
    public double chatOpacity = 1.0;
    public double chatLineSpacing = 0.0;
    public double textBackgroundOpacity = 0.5;
    @Nullable
    public String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> enabledPlayerModelParts = Sets.newHashSet(PlayerModelPart.values());
    public Arm mainArm = Arm.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public double chatScale = 1.0;
    public double chatWidth = 1.0;
    public double chatHeightUnfocused = 0.44366195797920227;
    public double chatHeightFocused = 1.0;
    public double chatDelay = 0.0;
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
    public final KeyBinding[] keysHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyBinding keySaveToolbarActivator = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyBinding keyLoadToolbarActivator = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyBinding[] keysAll = ArrayUtils.addAll(new KeyBinding[]{this.keyAttack, this.keyUse, this.keyForward, this.keyLeft, this.keyBack, this.keyRight, this.keyJump, this.keySneak, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapHands, this.keySaveToolbarActivator, this.keyLoadToolbarActivator, this.keyAdvancements}, this.keysHotbar);
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
    public float distortionEffectScale = 1.0f;
    public float fovEffectScale = 1.0f;
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
            Option.RENDER_DISTANCE.setMax(32.0f);
        } else {
            Option.RENDER_DISTANCE.setMax(16.0f);
        }
        this.viewDistance = client.is64Bit() ? 12 : 8;
        this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
        this.load();
    }

    public float getTextBackgroundOpacity(float fallback) {
        return this.backgroundForChatOnly ? fallback : (float)this.textBackgroundOpacity;
    }

    public int getTextBackgroundColor(float fallbackOpacity) {
        return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0f) << 24 & 0xFF000000;
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
            Object object = null;
            try {
                bufferedReader.lines().forEach(string -> {
                    try {
                        Iterator<String> iterator = COLON_SPLITTER.split((CharSequence)string).iterator();
                        compoundTag.putString(iterator.next(), iterator.next());
                    } catch (Exception exception) {
                        LOGGER.warn("Skipping bad option: {}", string);
                    }
                });
            } catch (Throwable throwable) {
                object = throwable;
                throw throwable;
            } finally {
                if (bufferedReader != null) {
                    if (object != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable) {
                            ((Throwable)object).addSuppressed(throwable);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }
            }
            CompoundTag compoundTag2 = this.update(compoundTag);
            if (!compoundTag2.contains("graphicsMode") && compoundTag2.contains("fancyGraphics")) {
                this.graphicsMode = "true".equals(compoundTag2.getString("fancyGraphics")) ? GraphicsMode.FANCY : GraphicsMode.FAST;
            }
            for (String string2 : compoundTag2.getKeys()) {
                String string22 = compoundTag2.getString(string2);
                try {
                    if ("autoJump".equals(string2)) {
                        Option.AUTO_JUMP.set(this, string22);
                    }
                    if ("autoSuggestions".equals(string2)) {
                        Option.AUTO_SUGGESTIONS.set(this, string22);
                    }
                    if ("chatColors".equals(string2)) {
                        Option.CHAT_COLOR.set(this, string22);
                    }
                    if ("chatLinks".equals(string2)) {
                        Option.CHAT_LINKS.set(this, string22);
                    }
                    if ("chatLinksPrompt".equals(string2)) {
                        Option.CHAT_LINKS_PROMPT.set(this, string22);
                    }
                    if ("enableVsync".equals(string2)) {
                        Option.VSYNC.set(this, string22);
                    }
                    if ("entityShadows".equals(string2)) {
                        Option.ENTITY_SHADOWS.set(this, string22);
                    }
                    if ("forceUnicodeFont".equals(string2)) {
                        Option.FORCE_UNICODE_FONT.set(this, string22);
                    }
                    if ("discrete_mouse_scroll".equals(string2)) {
                        Option.DISCRETE_MOUSE_SCROLL.set(this, string22);
                    }
                    if ("invertYMouse".equals(string2)) {
                        Option.INVERT_MOUSE.set(this, string22);
                    }
                    if ("realmsNotifications".equals(string2)) {
                        Option.REALMS_NOTIFICATIONS.set(this, string22);
                    }
                    if ("reducedDebugInfo".equals(string2)) {
                        Option.REDUCED_DEBUG_INFO.set(this, string22);
                    }
                    if ("showSubtitles".equals(string2)) {
                        Option.SUBTITLES.set(this, string22);
                    }
                    if ("snooperEnabled".equals(string2)) {
                        Option.SNOOPER.set(this, string22);
                    }
                    if ("touchscreen".equals(string2)) {
                        Option.TOUCHSCREEN.set(this, string22);
                    }
                    if ("fullscreen".equals(string2)) {
                        Option.FULLSCREEN.set(this, string22);
                    }
                    if ("bobView".equals(string2)) {
                        Option.VIEW_BOBBING.set(this, string22);
                    }
                    if ("toggleCrouch".equals(string2)) {
                        this.sneakToggled = "true".equals(string22);
                    }
                    if ("toggleSprint".equals(string2)) {
                        this.sprintToggled = "true".equals(string22);
                    }
                    if ("mouseSensitivity".equals(string2)) {
                        this.mouseSensitivity = GameOptions.parseFloat(string22);
                    }
                    if ("fov".equals(string2)) {
                        this.fov = GameOptions.parseFloat(string22) * 40.0f + 70.0f;
                    }
                    if ("screenEffectScale".equals(string2)) {
                        this.distortionEffectScale = GameOptions.parseFloat(string22);
                    }
                    if ("fovEffectScale".equals(string2)) {
                        this.fovEffectScale = GameOptions.parseFloat(string22);
                    }
                    if ("gamma".equals(string2)) {
                        this.gamma = GameOptions.parseFloat(string22);
                    }
                    if ("renderDistance".equals(string2)) {
                        this.viewDistance = Integer.parseInt(string22);
                    }
                    if ("entityDistanceScaling".equals(string2)) {
                        this.entityDistanceScaling = Float.parseFloat(string22);
                    }
                    if ("guiScale".equals(string2)) {
                        this.guiScale = Integer.parseInt(string22);
                    }
                    if ("particles".equals(string2)) {
                        this.particles = ParticlesMode.byId(Integer.parseInt(string22));
                    }
                    if ("maxFps".equals(string2)) {
                        this.maxFps = Integer.parseInt(string22);
                        if (this.client.getWindow() != null) {
                            this.client.getWindow().setFramerateLimit(this.maxFps);
                        }
                    }
                    if ("difficulty".equals(string2)) {
                        this.difficulty = Difficulty.byOrdinal(Integer.parseInt(string22));
                    }
                    if ("graphicsMode".equals(string2)) {
                        this.graphicsMode = GraphicsMode.byId(Integer.parseInt(string22));
                    }
                    if ("tutorialStep".equals(string2)) {
                        this.tutorialStep = TutorialStep.byName(string22);
                    }
                    if ("ao".equals(string2)) {
                        this.ao = "true".equals(string22) ? AoMode.MAX : ("false".equals(string22) ? AoMode.OFF : AoMode.byId(Integer.parseInt(string22)));
                    }
                    if ("renderClouds".equals(string2)) {
                        if ("true".equals(string22)) {
                            this.cloudRenderMode = CloudRenderMode.FANCY;
                        } else if ("false".equals(string22)) {
                            this.cloudRenderMode = CloudRenderMode.OFF;
                        } else if ("fast".equals(string22)) {
                            this.cloudRenderMode = CloudRenderMode.FAST;
                        }
                    }
                    if ("attackIndicator".equals(string2)) {
                        this.attackIndicator = AttackIndicator.byId(Integer.parseInt(string22));
                    }
                    if ("resourcePacks".equals(string2)) {
                        this.resourcePacks = JsonHelper.deserialize(GSON, string22, STRING_LIST_TYPE);
                        if (this.resourcePacks == null) {
                            this.resourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("incompatibleResourcePacks".equals(string2)) {
                        this.incompatibleResourcePacks = JsonHelper.deserialize(GSON, string22, STRING_LIST_TYPE);
                        if (this.incompatibleResourcePacks == null) {
                            this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("lastServer".equals(string2)) {
                        this.lastServer = string22;
                    }
                    if ("lang".equals(string2)) {
                        this.language = string22;
                    }
                    if ("chatVisibility".equals(string2)) {
                        this.chatVisibility = ChatVisibility.byId(Integer.parseInt(string22));
                    }
                    if ("chatOpacity".equals(string2)) {
                        this.chatOpacity = GameOptions.parseFloat(string22);
                    }
                    if ("chatLineSpacing".equals(string2)) {
                        this.chatLineSpacing = GameOptions.parseFloat(string22);
                    }
                    if ("textBackgroundOpacity".equals(string2)) {
                        this.textBackgroundOpacity = GameOptions.parseFloat(string22);
                    }
                    if ("backgroundForChatOnly".equals(string2)) {
                        this.backgroundForChatOnly = "true".equals(string22);
                    }
                    if ("fullscreenResolution".equals(string2)) {
                        this.fullscreenResolution = string22;
                    }
                    if ("hideServerAddress".equals(string2)) {
                        this.hideServerAddress = "true".equals(string22);
                    }
                    if ("advancedItemTooltips".equals(string2)) {
                        this.advancedItemTooltips = "true".equals(string22);
                    }
                    if ("pauseOnLostFocus".equals(string2)) {
                        this.pauseOnLostFocus = "true".equals(string22);
                    }
                    if ("overrideHeight".equals(string2)) {
                        this.overrideHeight = Integer.parseInt(string22);
                    }
                    if ("overrideWidth".equals(string2)) {
                        this.overrideWidth = Integer.parseInt(string22);
                    }
                    if ("heldItemTooltips".equals(string2)) {
                        this.heldItemTooltips = "true".equals(string22);
                    }
                    if ("chatHeightFocused".equals(string2)) {
                        this.chatHeightFocused = GameOptions.parseFloat(string22);
                    }
                    if ("chatDelay".equals(string2)) {
                        this.chatDelay = GameOptions.parseFloat(string22);
                    }
                    if ("chatHeightUnfocused".equals(string2)) {
                        this.chatHeightUnfocused = GameOptions.parseFloat(string22);
                    }
                    if ("chatScale".equals(string2)) {
                        this.chatScale = GameOptions.parseFloat(string22);
                    }
                    if ("chatWidth".equals(string2)) {
                        this.chatWidth = GameOptions.parseFloat(string22);
                    }
                    if ("mipmapLevels".equals(string2)) {
                        this.mipmapLevels = Integer.parseInt(string22);
                    }
                    if ("useNativeTransport".equals(string2)) {
                        this.useNativeTransport = "true".equals(string22);
                    }
                    if ("mainHand".equals(string2)) {
                        Arm arm = this.mainArm = "left".equals(string22) ? Arm.LEFT : Arm.RIGHT;
                    }
                    if ("narrator".equals(string2)) {
                        this.narrator = NarratorMode.byId(Integer.parseInt(string22));
                    }
                    if ("biomeBlendRadius".equals(string2)) {
                        this.biomeBlendRadius = Integer.parseInt(string22);
                    }
                    if ("mouseWheelSensitivity".equals(string2)) {
                        this.mouseWheelSensitivity = GameOptions.parseFloat(string22);
                    }
                    if ("rawMouseInput".equals(string2)) {
                        this.rawMouseInput = "true".equals(string22);
                    }
                    if ("glDebugVerbosity".equals(string2)) {
                        this.glDebugVerbosity = Integer.parseInt(string22);
                    }
                    if ("skipMultiplayerWarning".equals(string2)) {
                        this.skipMultiplayerWarning = "true".equals(string22);
                    }
                    if ("joinedFirstServer".equals(string2)) {
                        this.joinedFirstServer = "true".equals(string22);
                    }
                    if ("syncChunkWrites".equals(string2)) {
                        this.syncChunkWrites = "true".equals(string22);
                    }
                    for (KeyBinding keyBinding : this.keysAll) {
                        if (!string2.equals("key_" + keyBinding.getTranslationKey())) continue;
                        keyBinding.setBoundKey(InputUtil.fromTranslationKey(string22));
                    }
                    for (SoundCategory soundCategory : SoundCategory.values()) {
                        if (!string2.equals("soundCategory_" + soundCategory.getName())) continue;
                        this.soundVolumeLevels.put(soundCategory, Float.valueOf(GameOptions.parseFloat(string22)));
                    }
                    for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                        if (!string2.equals("modelPart_" + playerModelPart.getName())) continue;
                        this.setPlayerModelPart(playerModelPart, "true".equals(string22));
                    }
                } catch (Exception exception) {
                    LOGGER.warn("Skipping bad option: {}:{}", (Object)string2, (Object)string22);
                }
            }
            KeyBinding.updateKeysByCode();
        } catch (Exception exception2) {
            LOGGER.error("Failed to load options", (Throwable)exception2);
        }
    }

    private CompoundTag update(CompoundTag tag) {
        int i = 0;
        try {
            i = Integer.parseInt(tag.getString("version"));
        } catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return NbtHelper.update(this.client.getDataFixer(), DataFixTypes.OPTIONS, tag, i);
    }

    private static float parseFloat(String string) {
        if ("true".equals(string)) {
            return 1.0f;
        }
        if ("false".equals(string)) {
            return 0.0f;
        }
        return Float.parseFloat(string);
    }

    public void write() {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            printWriter.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
            printWriter.println("autoJump:" + Option.AUTO_JUMP.get(this));
            printWriter.println("autoSuggestions:" + Option.AUTO_SUGGESTIONS.get(this));
            printWriter.println("chatColors:" + Option.CHAT_COLOR.get(this));
            printWriter.println("chatLinks:" + Option.CHAT_LINKS.get(this));
            printWriter.println("chatLinksPrompt:" + Option.CHAT_LINKS_PROMPT.get(this));
            printWriter.println("enableVsync:" + Option.VSYNC.get(this));
            printWriter.println("entityShadows:" + Option.ENTITY_SHADOWS.get(this));
            printWriter.println("forceUnicodeFont:" + Option.FORCE_UNICODE_FONT.get(this));
            printWriter.println("discrete_mouse_scroll:" + Option.DISCRETE_MOUSE_SCROLL.get(this));
            printWriter.println("invertYMouse:" + Option.INVERT_MOUSE.get(this));
            printWriter.println("realmsNotifications:" + Option.REALMS_NOTIFICATIONS.get(this));
            printWriter.println("reducedDebugInfo:" + Option.REDUCED_DEBUG_INFO.get(this));
            printWriter.println("snooperEnabled:" + Option.SNOOPER.get(this));
            printWriter.println("showSubtitles:" + Option.SUBTITLES.get(this));
            printWriter.println("touchscreen:" + Option.TOUCHSCREEN.get(this));
            printWriter.println("fullscreen:" + Option.FULLSCREEN.get(this));
            printWriter.println("bobView:" + Option.VIEW_BOBBING.get(this));
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
                case FANCY: {
                    printWriter.println("renderClouds:true");
                    break;
                }
                case FAST: {
                    printWriter.println("renderClouds:fast");
                    break;
                }
                case OFF: {
                    printWriter.println("renderClouds:false");
                }
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
                printWriter.println("fullscreenResolution:" + this.client.getWindow().getVideoMode().get().asString());
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
            printWriter.println("rawMouseInput:" + Option.RAW_MOUSE_INPUT.get(this));
            printWriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
            printWriter.println("skipMultiplayerWarning:" + this.skipMultiplayerWarning);
            printWriter.println("joinedFirstServer:" + this.joinedFirstServer);
            printWriter.println("syncChunkWrites:" + this.syncChunkWrites);
            for (KeyBinding keyBinding : this.keysAll) {
                printWriter.println("key_" + keyBinding.getTranslationKey() + ":" + keyBinding.getBoundKeyTranslationKey());
            }
            for (SoundCategory soundCategory : SoundCategory.values()) {
                printWriter.println("soundCategory_" + soundCategory.getName() + ":" + this.getSoundVolume(soundCategory));
            }
            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                printWriter.println("modelPart_" + playerModelPart.getName() + ":" + this.enabledPlayerModelParts.contains((Object)playerModelPart));
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to save options", (Throwable)exception);
        }
        this.onPlayerModelPartChange();
    }

    public float getSoundVolume(SoundCategory category) {
        if (this.soundVolumeLevels.containsKey((Object)category)) {
            return this.soundVolumeLevels.get((Object)category).floatValue();
        }
        return 1.0f;
    }

    public void setSoundVolume(SoundCategory category, float volume) {
        this.soundVolumeLevels.put(category, Float.valueOf(volume));
        this.client.getSoundManager().updateSoundVolume(category, volume);
    }

    public void onPlayerModelPartChange() {
        if (this.client.player != null) {
            int i = 0;
            for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
                i |= playerModelPart.getBitFlag();
            }
            this.client.player.networkHandler.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, i, this.mainArm));
        }
    }

    public Set<PlayerModelPart> getEnabledPlayerModelParts() {
        return ImmutableSet.copyOf(this.enabledPlayerModelParts);
    }

    public void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
        if (enabled) {
            this.enabledPlayerModelParts.add(part);
        } else {
            this.enabledPlayerModelParts.remove((Object)part);
        }
        this.onPlayerModelPartChange();
    }

    public void togglePlayerModelPart(PlayerModelPart part) {
        if (this.getEnabledPlayerModelParts().contains((Object)part)) {
            this.enabledPlayerModelParts.remove((Object)part);
        } else {
            this.enabledPlayerModelParts.add(part);
        }
        this.onPlayerModelPartChange();
    }

    public CloudRenderMode getCloudRenderMode() {
        if (this.viewDistance >= 4) {
            return this.cloudRenderMode;
        }
        return CloudRenderMode.OFF;
    }

    public boolean shouldUseNativeTransport() {
        return this.useNativeTransport;
    }

    public void addResourcePackProfilesToManager(ResourcePackManager manager) {
        LinkedHashSet<String> set = Sets.newLinkedHashSet();
        Iterator<String> iterator = this.resourcePacks.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            ResourcePackProfile resourcePackProfile = manager.getProfile(string);
            if (resourcePackProfile == null && !string.startsWith("file/")) {
                resourcePackProfile = manager.getProfile("file/" + string);
            }
            if (resourcePackProfile == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)string);
                iterator.remove();
                continue;
            }
            if (!resourcePackProfile.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)string);
                iterator.remove();
                continue;
            }
            if (resourcePackProfile.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)string);
                this.incompatibleResourcePacks.remove(string);
                continue;
            }
            set.add(resourcePackProfile.getName());
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

