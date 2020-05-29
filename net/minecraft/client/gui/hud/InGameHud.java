/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatListenerHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class InGameHud
extends DrawableHelper {
    private static final Identifier VIGNETTE_TEX = new Identifier("textures/misc/vignette.png");
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
    private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    private final Random random = new Random();
    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    private final ChatHud chatHud;
    private int ticks;
    @Nullable
    private Text overlayMessage;
    private int overlayRemaining;
    private boolean overlayTinted;
    public float vignetteDarkness = 1.0f;
    private int heldItemTooltipFade;
    private ItemStack currentStack = ItemStack.EMPTY;
    private final DebugHud debugHud;
    private final SubtitlesHud subtitlesHud;
    private final SpectatorHud spectatorHud;
    private final PlayerListHud playerListHud;
    private final BossBarHud bossBarHud;
    private int titleTotalTicks;
    @Nullable
    private Text title;
    @Nullable
    private Text subtitle;
    private int titleFadeInTicks;
    private int titleRemainTicks;
    private int titleFadeOutTicks;
    private int lastHealthValue;
    private int renderHealthValue;
    private long lastHealthCheckTime;
    private long heartJumpEndTick;
    private int scaledWidth;
    private int scaledHeight;
    private final Map<MessageType, List<ClientChatListener>> listeners = Maps.newHashMap();

    public InGameHud(MinecraftClient client) {
        this.client = client;
        this.itemRenderer = client.getItemRenderer();
        this.debugHud = new DebugHud(client);
        this.spectatorHud = new SpectatorHud(client);
        this.chatHud = new ChatHud(client);
        this.playerListHud = new PlayerListHud(client, this);
        this.bossBarHud = new BossBarHud(client);
        this.subtitlesHud = new SubtitlesHud(client);
        for (MessageType messageType : MessageType.values()) {
            this.listeners.put(messageType, Lists.newArrayList());
        }
        NarratorManager clientChatListener = NarratorManager.INSTANCE;
        this.listeners.get((Object)MessageType.CHAT).add(new ChatListenerHud(client));
        this.listeners.get((Object)MessageType.CHAT).add(clientChatListener);
        this.listeners.get((Object)MessageType.SYSTEM).add(new ChatListenerHud(client));
        this.listeners.get((Object)MessageType.SYSTEM).add(clientChatListener);
        this.listeners.get((Object)MessageType.GAME_INFO).add(new GameInfoChatListener(client));
        this.setDefaultTitleFade();
    }

    public void setDefaultTitleFade() {
        this.titleFadeInTicks = 10;
        this.titleRemainTicks = 70;
        this.titleFadeOutTicks = 20;
    }

    public void render(MatrixStack matrixStack, float f) {
        int j;
        float g;
        this.scaledWidth = this.client.getWindow().getScaledWidth();
        this.scaledHeight = this.client.getWindow().getScaledHeight();
        TextRenderer textRenderer = this.getFontRenderer();
        RenderSystem.enableBlend();
        if (MinecraftClient.isFancyGraphicsEnabled()) {
            this.renderVignetteOverlay(this.client.getCameraEntity());
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
        }
        ItemStack itemStack = this.client.player.inventory.getArmorStack(3);
        if (this.client.options.perspective == 0 && itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            this.renderPumpkinOverlay();
        }
        if (!this.client.player.hasStatusEffect(StatusEffects.NAUSEA) && (g = MathHelper.lerp(f, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f) {
            this.renderPortalOverlay(g);
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            this.spectatorHud.render(matrixStack, f);
        } else if (!this.client.options.hudHidden) {
            this.renderHotbar(f, matrixStack);
        }
        if (!this.client.options.hudHidden) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            this.renderCrosshair(matrixStack);
            RenderSystem.defaultBlendFunc();
            this.client.getProfiler().push("bossHealth");
            this.bossBarHud.render(matrixStack);
            this.client.getProfiler().pop();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBars(matrixStack);
            }
            this.renderMountHealth(matrixStack);
            RenderSystem.disableBlend();
            int i = this.scaledWidth / 2 - 91;
            if (this.client.player.hasJumpingMount()) {
                this.renderMountJumpBar(matrixStack, i);
            } else if (this.client.interactionManager.hasExperienceBar()) {
                this.renderExperienceBar(matrixStack, i);
            }
            if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
                this.renderHeldItemTooltip(matrixStack);
            } else if (this.client.player.isSpectator()) {
                this.spectatorHud.render(matrixStack);
            }
        }
        if (this.client.player.getSleepTimer() > 0) {
            this.client.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            g = this.client.player.getSleepTimer();
            float h = g / 100.0f;
            if (h > 1.0f) {
                h = 1.0f - (g - 100.0f) / 10.0f;
            }
            j = (int)(220.0f * h) << 24 | 0x101020;
            InGameHud.fill(matrixStack, 0, 0, this.scaledWidth, this.scaledHeight, j);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            this.client.getProfiler().pop();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.client.isDemo()) {
            this.renderDemoTimer(matrixStack);
        }
        this.renderStatusEffectOverlay(matrixStack);
        if (this.client.options.debugEnabled) {
            this.debugHud.render(matrixStack);
        }
        if (!this.client.options.hudHidden) {
            ScoreboardObjective scoreboardObjective2;
            int m;
            int l;
            if (this.overlayMessage != null && this.overlayRemaining > 0) {
                this.client.getProfiler().push("overlayMessage");
                g = (float)this.overlayRemaining - f;
                int k = (int)(g * 255.0f / 20.0f);
                if (k > 255) {
                    k = 255;
                }
                if (k > 8) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight - 68, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    j = 0xFFFFFF;
                    if (this.overlayTinted) {
                        j = MathHelper.hsvToRgb(g / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                    }
                    l = k << 24 & 0xFF000000;
                    m = textRenderer.getWidth(this.overlayMessage);
                    this.drawTextBackground(matrixStack, textRenderer, -4, m, 0xFFFFFF | l);
                    textRenderer.draw(matrixStack, this.overlayMessage, (float)(-m / 2), -4.0f, j | l);
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            if (this.title != null && this.titleTotalTicks > 0) {
                this.client.getProfiler().push("titleAndSubtitle");
                g = (float)this.titleTotalTicks - f;
                int k = 255;
                if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
                    float n = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - g;
                    k = (int)(n * 255.0f / (float)this.titleFadeInTicks);
                }
                if (this.titleTotalTicks <= this.titleFadeOutTicks) {
                    k = (int)(g * 255.0f / (float)this.titleFadeOutTicks);
                }
                if ((k = MathHelper.clamp(k, 0, 255)) > 8) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight / 2, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef(4.0f, 4.0f, 4.0f);
                    int j2 = k << 24 & 0xFF000000;
                    l = textRenderer.getWidth(this.title);
                    this.drawTextBackground(matrixStack, textRenderer, -10, l, 0xFFFFFF | j2);
                    textRenderer.drawWithShadow(matrixStack, this.title, (float)(-l / 2), -10.0f, 0xFFFFFF | j2);
                    RenderSystem.popMatrix();
                    if (this.subtitle != null) {
                        RenderSystem.pushMatrix();
                        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
                        m = textRenderer.getWidth(this.subtitle);
                        this.drawTextBackground(matrixStack, textRenderer, 5, m, 0xFFFFFF | j2);
                        textRenderer.drawWithShadow(matrixStack, this.subtitle, (float)(-m / 2), 5.0f, 0xFFFFFF | j2);
                        RenderSystem.popMatrix();
                    }
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            this.subtitlesHud.render(matrixStack);
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective scoreboardObjective = null;
            Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
            if (team != null && (l = team.getColor().getColorIndex()) >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + l);
            }
            ScoreboardObjective scoreboardObjective3 = scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
            if (scoreboardObjective2 != null) {
                this.renderScoreboardSidebar(matrixStack, scoreboardObjective2);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableAlphaTest();
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, this.scaledHeight - 48, 0.0f);
            this.client.getProfiler().push("chat");
            this.chatHud.render(matrixStack, this.ticks);
            this.client.getProfiler().pop();
            RenderSystem.popMatrix();
            scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
            if (this.client.options.keyPlayerList.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getPlayerList().size() > 1 || scoreboardObjective2 != null)) {
                this.playerListHud.tick(true);
                this.playerListHud.render(matrixStack, this.scaledWidth, scoreboard, scoreboardObjective2);
            } else {
                this.playerListHud.tick(false);
            }
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
    }

    private void drawTextBackground(MatrixStack matrixStack, TextRenderer textRenderer, int i, int j, int k) {
        int l = this.client.options.getTextBackgroundColor(0.0f);
        if (l != 0) {
            int m = -j / 2;
            InGameHud.fill(matrixStack, m - 2, i - 2, m + j + 2, i + textRenderer.fontHeight + 2, BackgroundHelper.ColorMixer.mixColor(l, k));
        }
    }

    private void renderCrosshair(MatrixStack matrixStack) {
        GameOptions gameOptions = this.client.options;
        if (gameOptions.perspective != 0) {
            return;
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && !this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
            return;
        }
        if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight / 2, this.getZOffset());
            Camera camera = this.client.gameRenderer.getCamera();
            RenderSystem.rotatef(camera.getPitch(), -1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(camera.getYaw(), 0.0f, 1.0f, 0.0f);
            RenderSystem.scalef(-1.0f, -1.0f, -1.0f);
            RenderSystem.renderCrosshair(10);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            int i = 15;
            this.drawTexture(matrixStack, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
            if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
                float f = this.client.player.getAttackCooldownProgress(0.0f);
                boolean bl = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0f) {
                    bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0f;
                    bl &= this.client.targetedEntity.isAlive();
                }
                int j = this.scaledHeight / 2 - 7 + 16;
                int k = this.scaledWidth / 2 - 8;
                if (bl) {
                    this.drawTexture(matrixStack, k, j, 68, 94, 16, 16);
                } else if (f < 1.0f) {
                    int l = (int)(f * 17.0f);
                    this.drawTexture(matrixStack, k, j, 36, 94, 16, 4);
                    this.drawTexture(matrixStack, k, j, 52, 94, l, 4);
                }
            }
        }
    }

    private boolean shouldRenderSpectatorCrosshair(HitResult hitResult) {
        if (hitResult == null) {
            return false;
        }
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)hitResult).getEntity() instanceof NamedScreenHandlerFactory;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            ClientWorld world = this.client.world;
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
        }
        return false;
    }

    protected void renderStatusEffectOverlay(MatrixStack matrixStack) {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        int i = 0;
        int j = 0;
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        ArrayList<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
        this.client.getTextureManager().bindTexture(HandledScreen.BACKGROUND_TEXTURE);
        for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            if (!statusEffectInstance.shouldShowIcon()) continue;
            int k = this.scaledWidth;
            int l = 1;
            if (this.client.isDemo()) {
                l += 15;
            }
            if (statusEffect.isBeneficial()) {
                k -= 25 * ++i;
            } else {
                k -= 25 * ++j;
                l += 26;
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f = 1.0f;
            if (statusEffectInstance.isAmbient()) {
                this.drawTexture(matrixStack, k, l, 165, 166, 24, 24);
            } else {
                this.drawTexture(matrixStack, k, l, 141, 166, 24, 24);
                if (statusEffectInstance.getDuration() <= 200) {
                    int m = 10 - statusEffectInstance.getDuration() / 20;
                    f = MathHelper.clamp((float)statusEffectInstance.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)statusEffectInstance.getDuration() * (float)Math.PI / 5.0f) * MathHelper.clamp((float)m / 10.0f * 0.25f, 0.0f, 0.25f);
                }
            }
            Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
            int n = k;
            int o = l;
            float g = f;
            list.add(() -> {
                this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, g);
                InGameHud.drawSprite(matrixStack, n + 3, o + 3, this.getZOffset(), 18, 18, sprite);
            });
        }
        list.forEach(Runnable::run);
    }

    protected void renderHotbar(float f, MatrixStack matrixStack) {
        float g;
        int o;
        int n;
        int m;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        ItemStack itemStack = playerEntity.getOffHandStack();
        Arm arm = playerEntity.getMainArm().getOpposite();
        int i = this.scaledWidth / 2;
        int j = this.getZOffset();
        int k = 182;
        int l = 91;
        this.setZOffset(-90);
        this.drawTexture(matrixStack, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        this.drawTexture(matrixStack, i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if (!itemStack.isEmpty()) {
            if (arm == Arm.LEFT) {
                this.drawTexture(matrixStack, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            } else {
                this.drawTexture(matrixStack, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }
        this.setZOffset(j);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (m = 0; m < 9; ++m) {
            n = i - 90 + m * 20 + 2;
            o = this.scaledHeight - 16 - 3;
            this.renderHotbarItem(n, o, f, playerEntity, playerEntity.inventory.main.get(m));
        }
        if (!itemStack.isEmpty()) {
            m = this.scaledHeight - 16 - 3;
            if (arm == Arm.LEFT) {
                this.renderHotbarItem(i - 91 - 26, m, f, playerEntity, itemStack);
            } else {
                this.renderHotbarItem(i + 91 + 10, m, f, playerEntity, itemStack);
            }
        }
        if (this.client.options.attackIndicator == AttackIndicator.HOTBAR && (g = this.client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            n = this.scaledHeight - 20;
            o = i + 91 + 6;
            if (arm == Arm.RIGHT) {
                o = i - 91 - 22;
            }
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
            int p = (int)(g * 19.0f);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(matrixStack, o, n, 0, 94, 18, 18);
            this.drawTexture(matrixStack, o, n + 18 - p, 18, 112 - p, 18, p);
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    public void renderMountJumpBar(MatrixStack matrixStack, int i) {
        this.client.getProfiler().push("jumpBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        float f = this.client.player.method_3151();
        int j = 182;
        int k = (int)(f * 183.0f);
        int l = this.scaledHeight - 32 + 3;
        this.drawTexture(matrixStack, i, l, 0, 84, 182, 5);
        if (k > 0) {
            this.drawTexture(matrixStack, i, l, 0, 89, k, 5);
        }
        this.client.getProfiler().pop();
    }

    public void renderExperienceBar(MatrixStack matrixStack, int i) {
        int m;
        int l;
        this.client.getProfiler().push("expBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        int j = this.client.player.getNextLevelExperience();
        if (j > 0) {
            int k = 182;
            l = (int)(this.client.player.experienceProgress * 183.0f);
            m = this.scaledHeight - 32 + 3;
            this.drawTexture(matrixStack, i, m, 0, 64, 182, 5);
            if (l > 0) {
                this.drawTexture(matrixStack, i, m, 0, 69, l, 5);
            }
        }
        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            l = (this.scaledWidth - this.getFontRenderer().getWidth(string)) / 2;
            m = this.scaledHeight - 31 - 4;
            this.getFontRenderer().draw(matrixStack, string, (float)(l + 1), (float)m, 0);
            this.getFontRenderer().draw(matrixStack, string, (float)(l - 1), (float)m, 0);
            this.getFontRenderer().draw(matrixStack, string, (float)l, (float)(m + 1), 0);
            this.getFontRenderer().draw(matrixStack, string, (float)l, (float)(m - 1), 0);
            this.getFontRenderer().draw(matrixStack, string, (float)l, (float)m, 8453920);
            this.client.getProfiler().pop();
        }
    }

    public void renderHeldItemTooltip(MatrixStack matrixStack) {
        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            int l;
            MutableText mutableText = new LiteralText("").append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }
            int i = this.getFontRenderer().getWidth(mutableText);
            int j = (this.scaledWidth - i) / 2;
            int k = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }
            if ((l = (int)((float)this.heldItemTooltipFade * 256.0f / 10.0f)) > 255) {
                l = 255;
            }
            if (l > 0) {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                InGameHud.fill(matrixStack, j - 2, k - 2, j + i + 2, k + this.getFontRenderer().fontHeight + 2, this.client.options.getTextBackgroundColor(0));
                this.getFontRenderer().drawWithShadow(matrixStack, mutableText, (float)j, (float)k, 0xFFFFFF + (l << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
        this.client.getProfiler().pop();
    }

    public void renderDemoTimer(MatrixStack matrixStack) {
        this.client.getProfiler().push("demo");
        String string = this.client.world.getTime() >= 120500L ? I18n.translate("demo.demoExpired", new Object[0]) : I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
        int i = this.getFontRenderer().getWidth(string);
        this.getFontRenderer().drawWithShadow(matrixStack, string, (float)(this.scaledWidth - i - 10), 5.0f, 0xFFFFFF);
        this.client.getProfiler().pop();
    }

    private void renderScoreboardSidebar(MatrixStack matrixStack, ScoreboardObjective scoreboardObjective) {
        int i;
        Scoreboard scoreboard = scoreboardObjective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
        List list = collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        ArrayList<Pair<ScoreboardPlayerScore, MutableText>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Text text = scoreboardObjective.getDisplayName();
        int j = i = this.getFontRenderer().getWidth(text);
        int k = this.getFontRenderer().getWidth(": ");
        for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
            MutableText text2 = Team.modifyText(team, new LiteralText(scoreboardPlayerScore2.getPlayerName()));
            list2.add(Pair.of(scoreboardPlayerScore2, text2));
            j = Math.max(j, this.getFontRenderer().getWidth(text2) + k + this.getFontRenderer().getWidth(Integer.toString(scoreboardPlayerScore2.getScore())));
        }
        int l = collection.size() * this.getFontRenderer().fontHeight;
        int m = this.scaledHeight / 2 + l / 3;
        int n = 3;
        int o = this.scaledWidth - j - 3;
        int p = 0;
        int q = this.client.options.getTextBackgroundColor(0.3f);
        int r = this.client.options.getTextBackgroundColor(0.4f);
        for (Pair pair : list2) {
            ScoreboardPlayerScore scoreboardPlayerScore2 = (ScoreboardPlayerScore)pair.getFirst();
            Text text3 = (Text)pair.getSecond();
            String string = (Object)((Object)Formatting.RED) + "" + scoreboardPlayerScore2.getScore();
            int s = o;
            int t = m - ++p * this.getFontRenderer().fontHeight;
            int u = this.scaledWidth - 3 + 2;
            InGameHud.fill(matrixStack, s - 2, t, u, t + this.getFontRenderer().fontHeight, q);
            this.getFontRenderer().draw(matrixStack, text3, (float)s, (float)t, -1);
            this.getFontRenderer().draw(matrixStack, string, (float)(u - this.getFontRenderer().getWidth(string)), (float)t, -1);
            if (p != collection.size()) continue;
            InGameHud.fill(matrixStack, s - 2, t - this.getFontRenderer().fontHeight - 1, u, t - 1, r);
            InGameHud.fill(matrixStack, s - 2, t - 1, u, t, q);
            this.getFontRenderer().draw(matrixStack, text, (float)(s + j / 2 - i / 2), (float)(t - this.getFontRenderer().fontHeight), -1);
        }
    }

    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }

    private LivingEntity getRiddenEntity() {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            Entity entity = playerEntity.getVehicle();
            if (entity == null) {
                return null;
            }
            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }
        return null;
    }

    private int getHeartCount(LivingEntity entity) {
        if (entity == null || !entity.isLiving()) {
            return 0;
        }
        float f = entity.getMaxHealth();
        int i = (int)(f + 0.5f) / 2;
        if (i > 30) {
            i = 30;
        }
        return i;
    }

    private int getHeartRows(int heartCount) {
        return (int)Math.ceil((double)heartCount / 10.0);
    }

    private void renderStatusBars(MatrixStack matrixStack) {
        int ad;
        int ac;
        int ab;
        int aa;
        int z;
        int y;
        int x;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        int i = MathHelper.ceil(playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (l - this.lastHealthCheckTime > 1000L) {
            this.lastHealthValue = i;
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }
        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        this.random.setSeed(this.ticks * 312871);
        HungerManager hungerManager = playerEntity.getHungerManager();
        int k = hungerManager.getFoodLevel();
        int m = this.scaledWidth / 2 - 91;
        int n = this.scaledWidth / 2 + 91;
        int o = this.scaledHeight - 39;
        float f = (float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0f / 10.0f);
        int r = Math.max(10 - (q - 2), 3);
        int s = o - (q - 1) * r - 10;
        int t = o - 10;
        int u = p;
        int v = playerEntity.getArmor();
        int w = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            w = this.ticks % MathHelper.ceil(f + 5.0f);
        }
        this.client.getProfiler().push("armor");
        for (x = 0; x < 10; ++x) {
            if (v <= 0) continue;
            y = m + x * 8;
            if (x * 2 + 1 < v) {
                this.drawTexture(matrixStack, y, s, 34, 9, 9, 9);
            }
            if (x * 2 + 1 == v) {
                this.drawTexture(matrixStack, y, s, 25, 9, 9, 9);
            }
            if (x * 2 + 1 <= v) continue;
            this.drawTexture(matrixStack, y, s, 16, 9, 9, 9);
        }
        this.client.getProfiler().swap("health");
        for (x = MathHelper.ceil((f + (float)p) / 2.0f) - 1; x >= 0; --x) {
            y = 16;
            if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
                y += 36;
            } else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
                y += 72;
            }
            z = 0;
            if (bl) {
                z = 1;
            }
            aa = MathHelper.ceil((float)(x + 1) / 10.0f) - 1;
            ab = m + x % 10 * 8;
            ac = o - aa * r;
            if (i <= 4) {
                ac += this.random.nextInt(2);
            }
            if (u <= 0 && x == w) {
                ac -= 2;
            }
            ad = 0;
            if (playerEntity.world.getLevelProperties().isHardcore()) {
                ad = 5;
            }
            this.drawTexture(matrixStack, ab, ac, 16 + z * 9, 9 * ad, 9, 9);
            if (bl) {
                if (x * 2 + 1 < j) {
                    this.drawTexture(matrixStack, ab, ac, y + 54, 9 * ad, 9, 9);
                }
                if (x * 2 + 1 == j) {
                    this.drawTexture(matrixStack, ab, ac, y + 63, 9 * ad, 9, 9);
                }
            }
            if (u > 0) {
                if (u == p && p % 2 == 1) {
                    this.drawTexture(matrixStack, ab, ac, y + 153, 9 * ad, 9, 9);
                    --u;
                    continue;
                }
                this.drawTexture(matrixStack, ab, ac, y + 144, 9 * ad, 9, 9);
                u -= 2;
                continue;
            }
            if (x * 2 + 1 < i) {
                this.drawTexture(matrixStack, ab, ac, y + 36, 9 * ad, 9, 9);
            }
            if (x * 2 + 1 != i) continue;
            this.drawTexture(matrixStack, ab, ac, y + 45, 9 * ad, 9, 9);
        }
        LivingEntity livingEntity = this.getRiddenEntity();
        y = this.getHeartCount(livingEntity);
        if (y == 0) {
            this.client.getProfiler().swap("food");
            for (z = 0; z < 10; ++z) {
                aa = o;
                ab = 16;
                ac = 0;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    ab += 36;
                    ac = 13;
                }
                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (k * 3 + 1) == 0) {
                    aa += this.random.nextInt(3) - 1;
                }
                ad = n - z * 8 - 9;
                this.drawTexture(matrixStack, ad, aa, 16 + ac * 9, 27, 9, 9);
                if (z * 2 + 1 < k) {
                    this.drawTexture(matrixStack, ad, aa, ab + 36, 27, 9, 9);
                }
                if (z * 2 + 1 != k) continue;
                this.drawTexture(matrixStack, ad, aa, ab + 45, 27, 9, 9);
            }
            t -= 10;
        }
        this.client.getProfiler().swap("air");
        z = playerEntity.getAir();
        aa = playerEntity.getMaxAir();
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < aa) {
            ab = this.getHeartRows(y) - 1;
            t -= ab * 10;
            ac = MathHelper.ceil((double)(z - 2) * 10.0 / (double)aa);
            ad = MathHelper.ceil((double)z * 10.0 / (double)aa) - ac;
            for (int ae = 0; ae < ac + ad; ++ae) {
                if (ae < ac) {
                    this.drawTexture(matrixStack, n - ae * 8 - 9, t, 16, 18, 9, 9);
                    continue;
                }
                this.drawTexture(matrixStack, n - ae * 8 - 9, t, 25, 18, 9, 9);
            }
        }
        this.client.getProfiler().pop();
    }

    private void renderMountHealth(MatrixStack matrixStack) {
        LivingEntity livingEntity = this.getRiddenEntity();
        if (livingEntity == null) {
            return;
        }
        int i = this.getHeartCount(livingEntity);
        if (i == 0) {
            return;
        }
        int j = (int)Math.ceil(livingEntity.getHealth());
        this.client.getProfiler().swap("mountHealth");
        int k = this.scaledHeight - 39;
        int l = this.scaledWidth / 2 + 91;
        int m = k;
        int n = 0;
        boolean bl = false;
        while (i > 0) {
            int o = Math.min(i, 10);
            i -= o;
            for (int p = 0; p < o; ++p) {
                int q = 52;
                int r = 0;
                int s = l - p * 8 - 9;
                this.drawTexture(matrixStack, s, m, 52 + r * 9, 9, 9, 9);
                if (p * 2 + 1 + n < j) {
                    this.drawTexture(matrixStack, s, m, 88, 9, 9, 9);
                }
                if (p * 2 + 1 + n != j) continue;
                this.drawTexture(matrixStack, s, m, 97, 9, 9, 9);
            }
            m -= 10;
            n += 20;
        }
    }

    private void renderPumpkinOverlay() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        this.client.getTextureManager().bindTexture(PUMPKIN_BLUR);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void updateVignetteDarkness(Entity entity) {
        if (entity == null) {
            return;
        }
        float f = MathHelper.clamp(1.0f - entity.getBrightnessAtEyes(), 0.0f, 1.0f);
        this.vignetteDarkness = (float)((double)this.vignetteDarkness + (double)(f - this.vignetteDarkness) * 0.01);
    }

    private void renderVignetteOverlay(Entity entity) {
        WorldBorder worldBorder = this.client.world.getWorldBorder();
        float f = (float)worldBorder.getDistanceInsideBorder(entity);
        double d = Math.min(worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getSize()));
        double e = Math.max((double)worldBorder.getWarningBlocks(), d);
        f = (double)f < e ? 1.0f - (float)((double)f / e) : 0.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        if (f > 0.0f) {
            RenderSystem.color4f(0.0f, f, f, 1.0f);
        } else {
            RenderSystem.color4f(this.vignetteDarkness, this.vignetteDarkness, this.vignetteDarkness, 1.0f);
        }
        this.client.getTextureManager().bindTexture(VIGNETTE_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
    }

    private void renderPortalOverlay(float f) {
        if (f < 1.0f) {
            f *= f;
            f *= f;
            f = f * 0.8f + 0.2f;
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Sprite sprite = this.client.getBlockRenderManager().getModels().getSprite(Blocks.NETHER_PORTAL.getDefaultState());
        float g = sprite.getMinU();
        float h = sprite.getMinV();
        float i = sprite.getMaxU();
        float j = sprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(g, j).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(i, j).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(i, h).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(g, h).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        float g = (float)itemStack.getCooldown() - f;
        if (g > 0.0f) {
            RenderSystem.pushMatrix();
            float h = 1.0f + g / 5.0f;
            RenderSystem.translatef(i + 8, j + 12, 0.0f);
            RenderSystem.scalef(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            RenderSystem.translatef(-(i + 8), -(j + 12), 0.0f);
        }
        this.itemRenderer.method_27951(playerEntity, itemStack, i, j);
        if (g > 0.0f) {
            RenderSystem.popMatrix();
        }
        this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, itemStack, i, j);
    }

    public void tick() {
        if (this.overlayRemaining > 0) {
            --this.overlayRemaining;
        }
        if (this.titleTotalTicks > 0) {
            --this.titleTotalTicks;
            if (this.titleTotalTicks <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }
        ++this.ticks;
        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            this.updateVignetteDarkness(entity);
        }
        if (this.client.player != null) {
            ItemStack itemStack = this.client.player.inventory.getMainHandStack();
            if (itemStack.isEmpty()) {
                this.heldItemTooltipFade = 0;
            } else if (this.currentStack.isEmpty() || itemStack.getItem() != this.currentStack.getItem() || !itemStack.getName().equals(this.currentStack.getName())) {
                this.heldItemTooltipFade = 40;
            } else if (this.heldItemTooltipFade > 0) {
                --this.heldItemTooltipFade;
            }
            this.currentStack = itemStack;
        }
    }

    public void setRecordPlayingOverlay(Text text) {
        this.setOverlayMessage(new TranslatableText("record.nowPlaying", text), true);
    }

    public void setOverlayMessage(Text message, boolean tinted) {
        this.overlayMessage = message;
        this.overlayRemaining = 60;
        this.overlayTinted = tinted;
    }

    public void setTitles(@Nullable Text text, @Nullable Text text2, int i, int j, int k) {
        if (text == null && text2 == null && i < 0 && j < 0 && k < 0) {
            this.title = null;
            this.subtitle = null;
            this.titleTotalTicks = 0;
            return;
        }
        if (text != null) {
            this.title = text;
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
            return;
        }
        if (text2 != null) {
            this.subtitle = text2;
            return;
        }
        if (i >= 0) {
            this.titleFadeInTicks = i;
        }
        if (j >= 0) {
            this.titleRemainTicks = j;
        }
        if (k >= 0) {
            this.titleFadeOutTicks = k;
        }
        if (this.titleTotalTicks > 0) {
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
    }

    public void addChatMessage(MessageType messageType, Text text, UUID uUID) {
        for (ClientChatListener clientChatListener : this.listeners.get((Object)messageType)) {
            clientChatListener.onChatMessage(messageType, text, uUID);
        }
    }

    public ChatHud getChatHud() {
        return this.chatHud;
    }

    public int getTicks() {
        return this.ticks;
    }

    public TextRenderer getFontRenderer() {
        return this.client.textRenderer;
    }

    public SpectatorHud getSpectatorHud() {
        return this.spectatorHud;
    }

    public PlayerListHud getPlayerListWidget() {
        return this.playerListHud;
    }

    public void clear() {
        this.playerListHud.clear();
        this.bossBarHud.clear();
        this.client.getToastManager().clear();
    }

    public BossBarHud getBossBarHud() {
        return this.bossBarHud;
    }

    public void resetDebugHudChunk() {
        this.debugHud.resetChunk();
    }
}

