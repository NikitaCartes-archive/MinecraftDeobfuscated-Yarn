/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Realms32BitWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DEMO_WORLD_NAME = "Demo_World";
    public static final Text COPYRIGHT = new LiteralText("Copyright Mojang AB. Do not distribute!");
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
    private final boolean isMinceraft;
    @Nullable
    private String splashText;
    private ButtonWidget buttonResetDemo;
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private Screen realmsNotificationGui;
    private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;
    @Nullable
    private DeprecationNotice deprecationNotice;
    private RealmsClient realms;
    private boolean displayedRealms32BitWarning = false;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean doBackgroundFade) {
        super(new TranslatableText("narrator.screen.title"));
        this.doBackgroundFade = doBackgroundFade;
        this.isMinceraft = (double)new Random().nextFloat() < 1.0E-4;
        this.realms = RealmsClient.createRealmsClient();
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.client.options.realmsNotifications && this.realmsNotificationGui != null;
    }

    @Override
    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.tick();
        }
        this.tryShowRealms32BitWarning();
    }

    private void tryShowRealms32BitWarning() {
        try {
            if (this.deprecationNotice != null && !this.client.options.skipRealms32BitWarning && !this.displayedRealms32BitWarning && this.deprecationNotice.realmsSubscriptionFuture.getNow(false).booleanValue()) {
                this.displayedRealms32BitWarning = true;
                this.client.setScreen(new Realms32BitWarningScreen(this));
            }
        } catch (CompletionException completionException) {
            LOGGER.warn("Failed to retrieve realms subscriptions", completionException);
            this.displayedRealms32BitWarning = true;
        }
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(EDITION_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor), PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }
        int i = this.textRenderer.getWidth(COPYRIGHT);
        int j = this.width - i - 2;
        int k = 24;
        int l = this.height / 4 + 48;
        if (this.client.isDemo()) {
            this.initWidgetsDemo(l, 24);
        } else {
            this.initWidgetsNormal(l, 24);
        }
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, l + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, button -> this.client.setScreen(new LanguageOptionsScreen((Screen)this, this.client.options, this.client.getLanguageManager())), new TranslatableText("narrator.button.language")));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, l + 72 + 12, 98, 20, new TranslatableText("menu.options"), button -> this.client.setScreen(new OptionsScreen(this, this.client.options))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20, new TranslatableText("menu.quit"), button -> this.client.scheduleStop()));
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, l + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, button -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), new TranslatableText("narrator.button.accessibility")));
        this.addDrawableChild(new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, button -> this.client.setScreen(new CreditsScreen(false, Runnables.doNothing())), this.textRenderer));
        this.client.setConnectedToRealms(false);
        if (this.client.options.realmsNotifications && this.realmsNotificationGui == null) {
            this.realmsNotificationGui = new RealmsNotificationsScreen();
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.init(this.client, this.width, this.height);
        }
        if (!this.client.is64Bit()) {
            CompletableFuture<Boolean> completableFuture = this.deprecationNotice != null ? this.deprecationNotice.realmsSubscriptionFuture : CompletableFuture.supplyAsync(this::fetchRealmsSubscribed, Util.getMainWorkerExecutor());
            this.deprecationNotice = new DeprecationNotice(MultilineText.create(this.textRenderer, (StringVisitable)new TranslatableText("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24, completableFuture);
        }
    }

    private boolean fetchRealmsSubscribed() {
        try {
            return this.realms.listWorlds().servers.stream().anyMatch(server -> server.ownerUUID != null && !server.expired && server.ownerUUID.equals(this.client.getSession().getUuid()));
        } catch (RealmsServiceException realmsServiceException) {
            return false;
        }
    }

    private void initWidgetsNormal(int y, int spacingY) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), button -> this.client.setScreen(new SelectWorldScreen(this))));
        boolean bl = this.client.isMultiplayerEnabled();
        ButtonWidget.TooltipSupplier tooltipSupplier = bl ? ButtonWidget.EMPTY : new ButtonWidget.TooltipSupplier(){
            private final Text MULTIPLAYER_DISABLED_TEXT = new TranslatableText("title.multiplayer.disabled");

            @Override
            public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
                if (!buttonWidget.active) {
                    TitleScreen.this.renderOrderedTooltip(matrixStack, TitleScreen.this.client.textRenderer.wrapLines(this.MULTIPLAYER_DISABLED_TEXT, Math.max(TitleScreen.this.width / 2 - 43, 170)), i, j);
                }
            }

            @Override
            public void supply(Consumer<Text> consumer) {
                consumer.accept(this.MULTIPLAYER_DISABLED_TEXT);
            }
        };
        this.addDrawableChild(new ButtonWidget((int)(this.width / 2 - 100), (int)(y + spacingY * 1), (int)200, (int)20, (Text)new TranslatableText((String)"menu.multiplayer"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, onMultiplayerButtonPressed(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this), (ButtonWidget.TooltipSupplier)tooltipSupplier)).active = bl;
        this.addDrawableChild(new ButtonWidget((int)(this.width / 2 - 100), (int)(y + spacingY * 2), (int)200, (int)20, (Text)new TranslatableText((String)"menu.online"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, onRealmsButtonPress(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this), (ButtonWidget.TooltipSupplier)tooltipSupplier)).active = bl;
    }

    private void initWidgetsDemo(int y, int spacingY) {
        boolean bl = this.canReadDemoWorldData();
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.playdemo"), button -> {
            if (bl) {
                this.client.startIntegratedServer(DEMO_WORLD_NAME);
            } else {
                DynamicRegistryManager dynamicRegistryManager = DynamicRegistryManager.BUILTIN.get();
                this.client.createWorld(DEMO_WORLD_NAME, MinecraftServer.DEMO_LEVEL_INFO, dynamicRegistryManager, GeneratorOptions.createDemo(dynamicRegistryManager));
            }
        }));
        this.buttonResetDemo = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, new TranslatableText("menu.resetdemo"), button -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            try (LevelStorage.Session session = levelStorage.createSession(DEMO_WORLD_NAME);){
                LevelSummary levelSummary = session.getLevelSummary();
                if (levelSummary != null) {
                    this.client.setScreen(new ConfirmScreen(this::onDemoDeletionConfirmed, new TranslatableText("selectWorld.deleteQuestion"), new TranslatableText("selectWorld.deleteWarning", levelSummary.getDisplayName()), new TranslatableText("selectWorld.deleteButton"), ScreenTexts.CANCEL));
                }
            } catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, DEMO_WORLD_NAME);
                LOGGER.warn("Failed to access demo world", iOException);
            }
        }));
        this.buttonResetDemo.active = bl;
    }

    private boolean canReadDemoWorldData() {
        boolean bl;
        block8: {
            LevelStorage.Session session = this.client.getLevelStorage().createSession(DEMO_WORLD_NAME);
            try {
                boolean bl2 = bl = session.getLevelSummary() != null;
                if (session == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (session != null) {
                        try {
                            session.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException iOException) {
                    SystemToast.addWorldAccessFailureToast(this.client, DEMO_WORLD_NAME);
                    LOGGER.warn("Failed to read demo world data", iOException);
                    return false;
                }
            }
            session.close();
        }
        return bl;
    }

    private void switchToRealms() {
        this.client.setScreen(new RealmsMainScreen(this));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0f : 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0f, 1.0f));
        int i = 274;
        int j = this.width / 2 - 137;
        int k = 30;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.drawTexture(matrices, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        int l = MathHelper.ceil(g * 255.0f) << 24;
        if ((l & 0xFC000000) == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, g);
        if (this.isMinceraft) {
            this.drawWithOutline(j, 30, (x, y) -> {
                this.drawTexture(matrices, x + 0, (int)y, 0, 0, 99, 44);
                this.drawTexture(matrices, x + 99, (int)y, 129, 0, 27, 44);
                this.drawTexture(matrices, x + 99 + 26, (int)y, 126, 0, 3, 44);
                this.drawTexture(matrices, x + 99 + 26 + 3, (int)y, 99, 0, 26, 44);
                this.drawTexture(matrices, x + 155, (int)y, 0, 45, 155, 44);
            });
        } else {
            this.drawWithOutline(j, 30, (x, y) -> {
                this.drawTexture(matrices, x + 0, (int)y, 0, 0, 155, 44);
                this.drawTexture(matrices, x + 155, (int)y, 0, 45, 155, 44);
            });
        }
        RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
        TitleScreen.drawTexture(matrices, j + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.deprecationNotice != null) {
            this.deprecationNotice.label.fillBackground(matrices, this.deprecationNotice.x, this.deprecationNotice.y, this.textRenderer.fontHeight, 2, 0x55200000);
            this.deprecationNotice.label.drawCenterWithShadow(matrices, this.deprecationNotice.x, this.deprecationNotice.y, this.textRenderer.fontHeight, 0xFFFFFF | l);
        }
        if (this.splashText != null) {
            matrices.push();
            matrices.translate(this.width / 2 + 90, 70.0, 0.0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0f));
            float h = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
            h = h * 100.0f / (float)(this.textRenderer.getWidth(this.splashText) + 32);
            matrices.scale(h, h, h);
            TitleScreen.drawCenteredText(matrices, this.textRenderer, this.splashText, 0, -8, 0xFFFF00 | l);
            matrices.pop();
        }
        String string = "Minecraft " + SharedConstants.getGameVersion().getName();
        string = this.client.isDemo() ? string + " Demo" : string + (String)("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
        if (MinecraftClient.getModStatus().isModded()) {
            string = string + I18n.translate("menu.modded", new Object[0]);
        }
        TitleScreen.drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 0xFFFFFF | l);
        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ((ClickableWidget)element).setAlpha(g);
        }
        super.render(matrices, mouseX, mouseY, delta);
        if (this.areRealmsNotificationsEnabled() && g >= 1.0f) {
            this.realmsNotificationGui.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    private void onDemoDeletionConfirmed(boolean delete) {
        if (delete) {
            try (LevelStorage.Session session = this.client.getLevelStorage().createSession(DEMO_WORLD_NAME);){
                session.deleteSessionLock();
            } catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast(this.client, DEMO_WORLD_NAME);
                LOGGER.warn("Failed to delete demo world", iOException);
            }
        }
        this.client.setScreen(this);
    }

    private /* synthetic */ void onRealmsButtonPress(ButtonWidget button) {
        this.switchToRealms();
    }

    private /* synthetic */ void onMultiplayerButtonPressed(ButtonWidget button) {
        Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
        this.client.setScreen(screen);
    }

    @Environment(value=EnvType.CLIENT)
    record DeprecationNotice(MultilineText label, int x, int y, CompletableFuture<Boolean> realmsSubscriptionFuture) {
    }
}

