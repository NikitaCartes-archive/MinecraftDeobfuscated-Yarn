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
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
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
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class InGameHud
extends DrawableHelper {
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    private static final Identifier SPYGLASS_SCOPE = new Identifier("textures/misc/spyglass_scope.png");
    private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");
    private static final Text DEMO_EXPIRED_MESSAGE = new TranslatableText("demo.demoExpired");
    private static final int field_32167 = 0xFFFFFF;
    private static final float field_32168 = 5.0f;
    private static final int field_32169 = 10;
    private static final int field_32170 = 10;
    private static final String field_32171 = ": ";
    private static final float field_32172 = 0.2f;
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
    private float spyglassScale;

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
        this.listeners.get((Object)MessageType.CHAT).add(new ChatHudListener(client));
        this.listeners.get((Object)MessageType.CHAT).add(clientChatListener);
        this.listeners.get((Object)MessageType.SYSTEM).add(new ChatHudListener(client));
        this.listeners.get((Object)MessageType.SYSTEM).add(clientChatListener);
        this.listeners.get((Object)MessageType.GAME_INFO).add(new GameInfoChatListener(client));
        this.setDefaultTitleFade();
    }

    public void setDefaultTitleFade() {
        this.titleFadeInTicks = 10;
        this.titleRemainTicks = 70;
        this.titleFadeOutTicks = 20;
    }

    public void render(MatrixStack matrices, float tickDelta) {
        int k;
        float g;
        this.scaledWidth = this.client.getWindow().getScaledWidth();
        this.scaledHeight = this.client.getWindow().getScaledHeight();
        TextRenderer textRenderer = this.getFontRenderer();
        RenderSystem.enableBlend();
        if (MinecraftClient.isFancyGraphicsOrBetter()) {
            this.renderVignetteOverlay(this.client.getCameraEntity());
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.defaultBlendFunc();
        }
        float f = this.client.getLastFrameDuration();
        this.spyglassScale = MathHelper.lerp(0.5f * f, this.spyglassScale, 1.125f);
        if (this.client.options.getPerspective().isFirstPerson()) {
            if (this.client.player.isUsingSpyglass()) {
                this.renderSpyglassOverlay(this.spyglassScale);
            } else {
                this.spyglassScale = 0.5f;
                ItemStack itemStack = this.client.player.getInventory().getArmorStack(3);
                if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
                    this.renderOverlay(PUMPKIN_BLUR, 1.0f);
                }
            }
        }
        if (this.client.player.getFrozenTicks() > 0) {
            this.renderOverlay(POWDER_SNOW_OUTLINE, this.client.player.getFreezingScale());
        }
        if ((g = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f && !this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
            this.renderPortalOverlay(g);
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            this.spectatorHud.render(matrices, tickDelta);
        } else if (!this.client.options.hudHidden) {
            this.renderHotbar(tickDelta, matrices);
        }
        if (!this.client.options.hudHidden) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            this.renderCrosshair(matrices);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.defaultBlendFunc();
            this.client.getProfiler().push("bossHealth");
            this.bossBarHud.render(matrices);
            this.client.getProfiler().pop();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBars(matrices);
            }
            this.renderMountHealth(matrices);
            RenderSystem.disableBlend();
            int i = this.scaledWidth / 2 - 91;
            if (this.client.player.hasJumpingMount()) {
                this.renderMountJumpBar(matrices, i);
            } else if (this.client.interactionManager.hasExperienceBar()) {
                this.renderExperienceBar(matrices, i);
            }
            if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
                this.renderHeldItemTooltip(matrices);
            } else if (this.client.player.isSpectator()) {
                this.spectatorHud.render(matrices);
            }
        }
        if (this.client.player.getSleepTimer() > 0) {
            this.client.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            float h = this.client.player.getSleepTimer();
            float j = h / 100.0f;
            if (j > 1.0f) {
                j = 1.0f - (h - 100.0f) / 10.0f;
            }
            k = (int)(220.0f * j) << 24 | 0x101020;
            InGameHud.fill(matrices, 0, 0, this.scaledWidth, this.scaledHeight, k);
            RenderSystem.enableDepthTest();
            this.client.getProfiler().pop();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.client.isDemo()) {
            this.renderDemoTimer(matrices);
        }
        this.renderStatusEffectOverlay(matrices);
        if (this.client.options.debugEnabled) {
            this.debugHud.render(matrices);
        }
        if (!this.client.options.hudHidden) {
            ScoreboardObjective scoreboardObjective2;
            int n;
            int m;
            if (this.overlayMessage != null && this.overlayRemaining > 0) {
                this.client.getProfiler().push("overlayMessage");
                float h = (float)this.overlayRemaining - tickDelta;
                int l = (int)(h * 255.0f / 20.0f);
                if (l > 255) {
                    l = 255;
                }
                if (l > 8) {
                    matrices.push();
                    matrices.translate(this.scaledWidth / 2, this.scaledHeight - 68, 0.0);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    k = 0xFFFFFF;
                    if (this.overlayTinted) {
                        k = MathHelper.hsvToRgb(h / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                    }
                    m = l << 24 & 0xFF000000;
                    n = textRenderer.getWidth(this.overlayMessage);
                    this.drawTextBackground(matrices, textRenderer, -4, n, 0xFFFFFF | m);
                    textRenderer.draw(matrices, this.overlayMessage, (float)(-n / 2), -4.0f, k | m);
                    RenderSystem.disableBlend();
                    matrices.pop();
                }
                this.client.getProfiler().pop();
            }
            if (this.title != null && this.titleTotalTicks > 0) {
                this.client.getProfiler().push("titleAndSubtitle");
                float h = (float)this.titleTotalTicks - tickDelta;
                int l = 255;
                if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
                    float o = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - h;
                    l = (int)(o * 255.0f / (float)this.titleFadeInTicks);
                }
                if (this.titleTotalTicks <= this.titleFadeOutTicks) {
                    l = (int)(h * 255.0f / (float)this.titleFadeOutTicks);
                }
                if ((l = MathHelper.clamp(l, 0, 255)) > 8) {
                    matrices.push();
                    matrices.translate(this.scaledWidth / 2, this.scaledHeight / 2, 0.0);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    matrices.push();
                    matrices.scale(4.0f, 4.0f, 4.0f);
                    int k2 = l << 24 & 0xFF000000;
                    m = textRenderer.getWidth(this.title);
                    this.drawTextBackground(matrices, textRenderer, -10, m, 0xFFFFFF | k2);
                    textRenderer.drawWithShadow(matrices, this.title, (float)(-m / 2), -10.0f, 0xFFFFFF | k2);
                    matrices.pop();
                    if (this.subtitle != null) {
                        matrices.push();
                        matrices.scale(2.0f, 2.0f, 2.0f);
                        n = textRenderer.getWidth(this.subtitle);
                        this.drawTextBackground(matrices, textRenderer, 5, n, 0xFFFFFF | k2);
                        textRenderer.drawWithShadow(matrices, this.subtitle, (float)(-n / 2), 5.0f, 0xFFFFFF | k2);
                        matrices.pop();
                    }
                    RenderSystem.disableBlend();
                    matrices.pop();
                }
                this.client.getProfiler().pop();
            }
            this.subtitlesHud.render(matrices);
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective scoreboardObjective = null;
            Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
            if (team != null && (m = team.getColor().getColorIndex()) >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + m);
            }
            ScoreboardObjective scoreboardObjective3 = scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
            if (scoreboardObjective2 != null) {
                this.renderScoreboardSidebar(matrices, scoreboardObjective2);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            matrices.push();
            matrices.translate(0.0, this.scaledHeight - 48, 0.0);
            this.client.getProfiler().push("chat");
            this.chatHud.render(matrices, this.ticks);
            this.client.getProfiler().pop();
            matrices.pop();
            scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
            if (this.client.options.keyPlayerList.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getPlayerList().size() > 1 || scoreboardObjective2 != null)) {
                this.playerListHud.tick(true);
                this.playerListHud.render(matrices, this.scaledWidth, scoreboard, scoreboardObjective2);
            } else {
                this.playerListHud.tick(false);
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void drawTextBackground(MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color) {
        int i = this.client.options.getTextBackgroundColor(0.0f);
        if (i != 0) {
            int j = -width / 2;
            InGameHud.fill(matrices, j - 2, yOffset - 2, j + width + 2, yOffset + textRenderer.fontHeight + 2, BackgroundHelper.ColorMixer.mixColor(i, color));
        }
    }

    private void renderCrosshair(MatrixStack matrices) {
        GameOptions gameOptions = this.client.options;
        if (!gameOptions.getPerspective().isFirstPerson()) {
            return;
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && !this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
            return;
        }
        if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.hasReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
            Camera camera = this.client.gameRenderer.getCamera();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(this.scaledWidth / 2, this.scaledHeight / 2, this.getZOffset());
            matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
            matrixStack.scale(-1.0f, -1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            int i = 15;
            this.drawTexture(matrices, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
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
                    this.drawTexture(matrices, k, j, 68, 94, 16, 16);
                } else if (f < 1.0f) {
                    int l = (int)(f * 17.0f);
                    this.drawTexture(matrices, k, j, 36, 94, 16, 4);
                    this.drawTexture(matrices, k, j, 52, 94, l, 4);
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

    protected void renderStatusEffectOverlay(MatrixStack matrices) {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        int i = 0;
        int j = 0;
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        ArrayList<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
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
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float f = 1.0f;
            if (statusEffectInstance.isAmbient()) {
                this.drawTexture(matrices, k, l, 165, 166, 24, 24);
            } else {
                this.drawTexture(matrices, k, l, 141, 166, 24, 24);
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
                RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, g);
                InGameHud.drawSprite(matrices, n + 3, o + 3, this.getZOffset(), 18, 18, sprite);
            });
        }
        list.forEach(Runnable::run);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbar(float tickDelta, MatrixStack matrices) {
        float f;
        int p;
        int o;
        int n;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        ItemStack itemStack = playerEntity.getOffHandStack();
        Arm arm = playerEntity.getMainArm().getOpposite();
        int i = this.scaledWidth / 2;
        int j = this.getZOffset();
        int k = 182;
        int l = 91;
        this.setZOffset(-90);
        this.drawTexture(matrices, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        this.drawTexture(matrices, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if (!itemStack.isEmpty()) {
            if (arm == Arm.LEFT) {
                this.drawTexture(matrices, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            } else {
                this.drawTexture(matrices, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }
        this.setZOffset(j);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int m = 1;
        for (n = 0; n < 9; ++n) {
            o = i - 90 + n * 20 + 2;
            p = this.scaledHeight - 16 - 3;
            this.renderHotbarItem(o, p, tickDelta, playerEntity, playerEntity.getInventory().main.get(n), m++);
        }
        if (!itemStack.isEmpty()) {
            n = this.scaledHeight - 16 - 3;
            if (arm == Arm.LEFT) {
                this.renderHotbarItem(i - 91 - 26, n, tickDelta, playerEntity, itemStack, m++);
            } else {
                this.renderHotbarItem(i + 91 + 10, n, tickDelta, playerEntity, itemStack, m++);
            }
        }
        if (this.client.options.attackIndicator == AttackIndicator.HOTBAR && (f = this.client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            o = this.scaledHeight - 20;
            p = i + 91 + 6;
            if (arm == Arm.RIGHT) {
                p = i - 91 - 22;
            }
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            int q = (int)(f * 19.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(matrices, p, o, 0, 94, 18, 18);
            this.drawTexture(matrices, p, o + 18 - q, 18, 112 - q, 18, q);
        }
        RenderSystem.disableBlend();
    }

    public void renderMountJumpBar(MatrixStack matrices, int x) {
        this.client.getProfiler().push("jumpBar");
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        float f = this.client.player.getMountJumpStrength();
        int i = 182;
        int j = (int)(f * 183.0f);
        int k = this.scaledHeight - 32 + 3;
        this.drawTexture(matrices, x, k, 0, 84, 182, 5);
        if (j > 0) {
            this.drawTexture(matrices, x, k, 0, 89, j, 5);
        }
        this.client.getProfiler().pop();
    }

    public void renderExperienceBar(MatrixStack matrices, int x) {
        int l;
        int k;
        this.client.getProfiler().push("expBar");
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        int i = this.client.player.getNextLevelExperience();
        if (i > 0) {
            int j = 182;
            k = (int)(this.client.player.experienceProgress * 183.0f);
            l = this.scaledHeight - 32 + 3;
            this.drawTexture(matrices, x, l, 0, 64, 182, 5);
            if (k > 0) {
                this.drawTexture(matrices, x, l, 0, 69, k, 5);
            }
        }
        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            k = (this.scaledWidth - this.getFontRenderer().getWidth(string)) / 2;
            l = this.scaledHeight - 31 - 4;
            this.getFontRenderer().draw(matrices, string, (float)(k + 1), (float)l, 0);
            this.getFontRenderer().draw(matrices, string, (float)(k - 1), (float)l, 0);
            this.getFontRenderer().draw(matrices, string, (float)k, (float)(l + 1), 0);
            this.getFontRenderer().draw(matrices, string, (float)k, (float)(l - 1), 0);
            this.getFontRenderer().draw(matrices, string, (float)k, (float)l, 8453920);
            this.client.getProfiler().pop();
        }
    }

    public void renderHeldItemTooltip(MatrixStack matrices) {
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
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                InGameHud.fill(matrices, j - 2, k - 2, j + i + 2, k + this.getFontRenderer().fontHeight + 2, this.client.options.getTextBackgroundColor(0));
                this.getFontRenderer().drawWithShadow(matrices, mutableText, (float)j, (float)k, 0xFFFFFF + (l << 24));
                RenderSystem.disableBlend();
            }
        }
        this.client.getProfiler().pop();
    }

    public void renderDemoTimer(MatrixStack matrices) {
        this.client.getProfiler().push("demo");
        Text text = this.client.world.getTime() >= 120500L ? DEMO_EXPIRED_MESSAGE : new TranslatableText("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
        int i = this.getFontRenderer().getWidth(text);
        this.getFontRenderer().drawWithShadow(matrices, text, (float)(this.scaledWidth - i - 10), 5.0f, 0xFFFFFF);
        this.client.getProfiler().pop();
    }

    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective) {
        int i;
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        List list = collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        ArrayList<Pair<ScoreboardPlayerScore, MutableText>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Text text = objective.getDisplayName();
        int j = i = this.getFontRenderer().getWidth(text);
        int k = this.getFontRenderer().getWidth(field_32171);
        for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
            MutableText text2 = Team.decorateName(team, new LiteralText(scoreboardPlayerScore2.getPlayerName()));
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
            String string = "" + Formatting.RED + scoreboardPlayerScore2.getScore();
            int s = o;
            int t = m - ++p * this.getFontRenderer().fontHeight;
            int u = this.scaledWidth - 3 + 2;
            InGameHud.fill(matrices, s - 2, t, u, t + this.getFontRenderer().fontHeight, q);
            this.getFontRenderer().draw(matrices, text3, (float)s, (float)t, -1);
            this.getFontRenderer().draw(matrices, string, (float)(u - this.getFontRenderer().getWidth(string)), (float)t, -1);
            if (p != collection.size()) continue;
            InGameHud.fill(matrices, s - 2, t - this.getFontRenderer().fontHeight - 1, u, t - 1, r);
            InGameHud.fill(matrices, s - 2, t - 1, u, t, q);
            this.getFontRenderer().draw(matrices, text, (float)(s + j / 2 - i / 2), (float)(t - this.getFontRenderer().fontHeight), -1);
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

    private void renderStatusBars(MatrixStack matrices) {
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
                this.drawTexture(matrices, y, s, 34, 9, 9, 9);
            }
            if (x * 2 + 1 == v) {
                this.drawTexture(matrices, y, s, 25, 9, 9, 9);
            }
            if (x * 2 + 1 <= v) continue;
            this.drawTexture(matrices, y, s, 16, 9, 9, 9);
        }
        this.client.getProfiler().swap("health");
        for (x = MathHelper.ceil((f + (float)p) / 2.0f) - 1; x >= 0; --x) {
            y = 16;
            if (u <= 0) {
                if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
                    y += 36;
                } else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
                    y += 72;
                } else if (playerEntity.isFreezing()) {
                    y += 126;
                }
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
            this.drawTexture(matrices, ab, ac, 16 + z * 9, 9 * ad, 9, 9);
            if (bl) {
                if (x * 2 + 1 < j) {
                    this.drawTexture(matrices, ab, ac, y + 54, 9 * ad, 9, 9);
                }
                if (x * 2 + 1 == j) {
                    this.drawTexture(matrices, ab, ac, y + 63, 9 * ad, 9, 9);
                }
            }
            if (u > 0) {
                if (u == p && p % 2 == 1) {
                    this.drawTexture(matrices, ab, ac, y + 153, 9 * ad, 9, 9);
                    --u;
                    continue;
                }
                this.drawTexture(matrices, ab, ac, y + 144, 9 * ad, 9, 9);
                u -= 2;
                continue;
            }
            if (x * 2 + 1 < i) {
                this.drawTexture(matrices, ab, ac, y + 36, 9 * ad, 9, 9);
            }
            if (x * 2 + 1 != i) continue;
            this.drawTexture(matrices, ab, ac, y + 45, 9 * ad, 9, 9);
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
                this.drawTexture(matrices, ad, aa, 16 + ac * 9, 27, 9, 9);
                if (z * 2 + 1 < k) {
                    this.drawTexture(matrices, ad, aa, ab + 36, 27, 9, 9);
                }
                if (z * 2 + 1 != k) continue;
                this.drawTexture(matrices, ad, aa, ab + 45, 27, 9, 9);
            }
            t -= 10;
        }
        this.client.getProfiler().swap("air");
        z = playerEntity.getMaxAir();
        aa = Math.min(playerEntity.getAir(), z);
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || aa < z) {
            ab = this.getHeartRows(y) - 1;
            t -= ab * 10;
            ac = MathHelper.ceil((double)(aa - 2) * 10.0 / (double)z);
            ad = MathHelper.ceil((double)aa * 10.0 / (double)z) - ac;
            for (int ae = 0; ae < ac + ad; ++ae) {
                if (ae < ac) {
                    this.drawTexture(matrices, n - ae * 8 - 9, t, 16, 18, 9, 9);
                    continue;
                }
                this.drawTexture(matrices, n - ae * 8 - 9, t, 25, 18, 9, 9);
            }
        }
        this.client.getProfiler().pop();
    }

    private void renderMountHealth(MatrixStack matrices) {
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
                this.drawTexture(matrices, s, m, 52 + r * 9, 9, 9, 9);
                if (p * 2 + 1 + n < j) {
                    this.drawTexture(matrices, s, m, 88, 9, 9, 9);
                }
                if (p * 2 + 1 + n != j) continue;
                this.drawTexture(matrices, s, m, 97, 9, 9, 9);
            }
            m -= 10;
            n += 20;
        }
    }

    private void renderOverlay(Identifier texture, float opacity) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderSpyglassOverlay(float scale) {
        float f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float g = f = (float)Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / g) * scale;
        float i = f * h;
        float j = g * h;
        float k = ((float)this.scaledWidth - i) / 2.0f;
        float l = ((float)this.scaledHeight - j) / 2.0f;
        float m = k + i;
        float n = l + j;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(k, n, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(m, n, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(m, l, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(k, l, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(k, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(k, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0.0, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(m, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, n, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.scaledWidth, l, -90.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(m, l, -90.0).color(0, 0, 0, 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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
        double d = Math.min(worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getSizeLerpTarget() - worldBorder.getSize()));
        double e = Math.max((double)worldBorder.getWarningBlocks(), d);
        f = (double)f < e ? 1.0f - (float)((double)f / e) : 0.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        if (f > 0.0f) {
            f = MathHelper.clamp(f, 0.0f, 1.0f);
            RenderSystem.setShaderColor(0.0f, f, f, 1.0f);
        } else {
            float g = this.vignetteDarkness;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            RenderSystem.setShaderColor(g, g, g, 1.0f);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
    }

    private void renderPortalOverlay(float nauseaStrength) {
        if (nauseaStrength < 1.0f) {
            nauseaStrength *= nauseaStrength;
            nauseaStrength *= nauseaStrength;
            nauseaStrength = nauseaStrength * 0.8f + 0.2f;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, nauseaStrength);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Sprite sprite = this.client.getBlockRenderManager().getModels().getSprite(Blocks.NETHER_PORTAL.getDefaultState());
        float f = sprite.getMinU();
        float g = sprite.getMinV();
        float h = sprite.getMaxU();
        float i = sprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, this.scaledHeight, -90.0).texture(f, i).next();
        bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(h, i).next();
        bufferBuilder.vertex(this.scaledWidth, 0.0, -90.0).texture(h, g).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(f, g).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int i) {
        if (stack.isEmpty()) {
            return;
        }
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        float f = (float)stack.getCooldown() - tickDelta;
        if (f > 0.0f) {
            float g = 1.0f + f / 5.0f;
            matrixStack.push();
            matrixStack.translate(x + 8, y + 12, 0.0);
            matrixStack.scale(1.0f / g, (g + 1.0f) / 2.0f, 1.0f);
            matrixStack.translate(-(x + 8), -(y + 12), 0.0);
            RenderSystem.applyModelViewMatrix();
        }
        this.itemRenderer.renderInGuiWithOverrides(player, stack, x, y, i);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (f > 0.0f) {
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        }
        this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, stack, x, y);
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
            ItemStack itemStack = this.client.player.getInventory().getMainHandStack();
            if (itemStack.isEmpty()) {
                this.heldItemTooltipFade = 0;
            } else if (this.currentStack.isEmpty() || !itemStack.isOf(this.currentStack.getItem()) || !itemStack.getName().equals(this.currentStack.getName())) {
                this.heldItemTooltipFade = 40;
            } else if (this.heldItemTooltipFade > 0) {
                --this.heldItemTooltipFade;
            }
            this.currentStack = itemStack;
        }
    }

    public void setRecordPlayingOverlay(Text description) {
        this.setOverlayMessage(new TranslatableText("record.nowPlaying", description), true);
    }

    public void setOverlayMessage(Text message, boolean tinted) {
        this.overlayMessage = message;
        this.overlayRemaining = 60;
        this.overlayTinted = tinted;
    }

    public void setTitleTicks(int fadeInTicks, int remainTicks, int fadeOutTicks) {
        if (fadeInTicks >= 0) {
            this.titleFadeInTicks = fadeInTicks;
        }
        if (remainTicks >= 0) {
            this.titleRemainTicks = remainTicks;
        }
        if (fadeOutTicks >= 0) {
            this.titleFadeOutTicks = fadeOutTicks;
        }
        if (this.titleTotalTicks > 0) {
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(Text title) {
        this.title = title;
        this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
    }

    public void clearTitle() {
        this.title = null;
        this.subtitle = null;
        this.titleTotalTicks = 0;
    }

    public UUID extractSender(Text message) {
        String string = TextVisitFactory.removeFormattingCodes(message);
        String string2 = StringUtils.substringBetween(string, "<", ">");
        if (string2 == null) {
            return Util.NIL_UUID;
        }
        return this.client.getSocialInteractionsManager().getUuid(string2);
    }

    public void addChatMessage(MessageType type, Text message, UUID sender) {
        if (this.client.shouldBlockMessages(sender)) {
            return;
        }
        if (this.client.options.hideMatchedNames && this.client.shouldBlockMessages(this.extractSender(message))) {
            return;
        }
        for (ClientChatListener clientChatListener : this.listeners.get((Object)type)) {
            clientChatListener.onChatMessage(type, message, sender);
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

    public PlayerListHud getPlayerListHud() {
        return this.playerListHud;
    }

    public void clear() {
        this.playerListHud.clear();
        this.bossBarHud.clear();
        this.client.getToastManager().clear();
        this.client.options.debugEnabled = false;
        this.chatHud.clear(true);
    }

    public BossBarHud getBossBarHud() {
        return this.bossBarHud;
    }

    public void resetDebugHudChunk() {
        this.debugHud.resetChunk();
    }
}

