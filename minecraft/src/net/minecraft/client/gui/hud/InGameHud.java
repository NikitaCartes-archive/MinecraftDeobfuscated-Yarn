package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.Window;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.joml.Matrix4fStack;

/**
 * Responsible for rendering the HUD elements while the player is in game.
 * 
 * <p>The current instance used by the client can be obtained by {@link
 * MinecraftClient#inGameHud MinecraftClient.getInstance().inGameHud}.
 */
@Environment(EnvType.CLIENT)
public class InGameHud {
	private static final Identifier CROSSHAIR_TEXTURE = new Identifier("hud/crosshair");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = new Identifier("hud/crosshair_attack_indicator_full");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = new Identifier("hud/crosshair_attack_indicator_background");
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = new Identifier("hud/crosshair_attack_indicator_progress");
	private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = new Identifier("hud/effect_background_ambient");
	private static final Identifier EFFECT_BACKGROUND_TEXTURE = new Identifier("hud/effect_background");
	private static final Identifier HOTBAR_TEXTURE = new Identifier("hud/hotbar");
	private static final Identifier HOTBAR_SELECTION_TEXTURE = new Identifier("hud/hotbar_selection");
	private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = new Identifier("hud/hotbar_offhand_left");
	private static final Identifier HOTBAR_OFFHAND_RIGHT_TEXTURE = new Identifier("hud/hotbar_offhand_right");
	private static final Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = new Identifier("hud/hotbar_attack_indicator_background");
	private static final Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE = new Identifier("hud/hotbar_attack_indicator_progress");
	private static final Identifier JUMP_BAR_BACKGROUND_TEXTURE = new Identifier("hud/jump_bar_background");
	private static final Identifier JUMP_BAR_COOLDOWN_TEXTURE = new Identifier("hud/jump_bar_cooldown");
	private static final Identifier JUMP_BAR_PROGRESS_TEXTURE = new Identifier("hud/jump_bar_progress");
	private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = new Identifier("hud/experience_bar_background");
	private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = new Identifier("hud/experience_bar_progress");
	private static final Identifier ARMOR_EMPTY_TEXTURE = new Identifier("hud/armor_empty");
	private static final Identifier ARMOR_HALF_TEXTURE = new Identifier("hud/armor_half");
	private static final Identifier ARMOR_FULL_TEXTURE = new Identifier("hud/armor_full");
	private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = new Identifier("hud/food_empty_hunger");
	private static final Identifier FOOD_HALF_HUNGER_TEXTURE = new Identifier("hud/food_half_hunger");
	private static final Identifier FOOD_FULL_HUNGER_TEXTURE = new Identifier("hud/food_full_hunger");
	private static final Identifier FOOD_EMPTY_TEXTURE = new Identifier("hud/food_empty");
	private static final Identifier FOOD_HALF_TEXTURE = new Identifier("hud/food_half");
	private static final Identifier FOOD_FULL_TEXTURE = new Identifier("hud/food_full");
	private static final Identifier AIR_TEXTURE = new Identifier("hud/air");
	private static final Identifier AIR_BURSTING_TEXTURE = new Identifier("hud/air_bursting");
	private static final Identifier VEHICLE_CONTAINER_HEART_TEXTURE = new Identifier("hud/heart/vehicle_container");
	private static final Identifier VEHICLE_FULL_HEART_TEXTURE = new Identifier("hud/heart/vehicle_full");
	private static final Identifier VEHICLE_HALF_HEART_TEXTURE = new Identifier("hud/heart/vehicle_half");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
	private static final Identifier SPYGLASS_SCOPE = new Identifier("textures/misc/spyglass_scope.png");
	private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");
	private static final Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(ScoreboardEntry::value)
		.reversed()
		.thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);
	private static final Text DEMO_EXPIRED_MESSAGE = Text.translatable("demo.demoExpired");
	private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
	private static final int WHITE = 16777215;
	private static final float field_32168 = 5.0F;
	private static final int field_32169 = 10;
	private static final int field_32170 = 10;
	private static final String SCOREBOARD_JOINER = ": ";
	private static final float field_32172 = 0.2F;
	private static final int field_33942 = 9;
	private static final int field_33943 = 8;
	private static final float field_35431 = 0.2F;
	private final Random random = Random.create();
	private final MinecraftClient client;
	private final ChatHud chatHud;
	private int ticks;
	@Nullable
	private Text overlayMessage;
	private int overlayRemaining;
	private boolean overlayTinted;
	private boolean canShowChatDisabledScreen;
	public float vignetteDarkness = 1.0F;
	private int heldItemTooltipFade;
	private ItemStack currentStack = ItemStack.EMPTY;
	private final DebugHud debugHud;
	private final SubtitlesHud subtitlesHud;
	private final SpectatorHud spectatorHud;
	private final PlayerListHud playerListHud;
	private final BossBarHud bossBarHud;
	private int titleRemainTicks;
	@Nullable
	private Text title;
	@Nullable
	private Text subtitle;
	private int titleFadeInTicks;
	private int titleStayTicks;
	private int titleFadeOutTicks;
	private int lastHealthValue;
	private int renderHealthValue;
	private long lastHealthCheckTime;
	private long heartJumpEndTick;
	private float autosaveIndicatorAlpha;
	private float lastAutosaveIndicatorAlpha;
	private final LayeredDrawer layeredDrawer = new LayeredDrawer();
	private float spyglassScale;

	public InGameHud(MinecraftClient client) {
		this.client = client;
		this.debugHud = new DebugHud(client);
		this.spectatorHud = new SpectatorHud(client);
		this.chatHud = new ChatHud(client);
		this.playerListHud = new PlayerListHud(client, this);
		this.bossBarHud = new BossBarHud(client);
		this.subtitlesHud = new SubtitlesHud(client);
		this.setDefaultTitleFade();
		LayeredDrawer layeredDrawer = new LayeredDrawer()
			.addLayer(this::renderMiscOverlays)
			.addLayer(this::renderCrosshair)
			.addLayer(this::renderMainHud)
			.addLayer(this::renderExperienceLevel)
			.addLayer(this::renderStatusEffectOverlay)
			.addLayer((context, tickDelta) -> this.bossBarHud.render(context));
		LayeredDrawer layeredDrawer2 = new LayeredDrawer()
			.addLayer(this::renderDemoTimer)
			.addLayer((context, tickDelta) -> {
				if (this.debugHud.shouldShowDebugHud()) {
					this.debugHud.render(context);
				}
			})
			.addLayer(this::renderScoreboardSidebar)
			.addLayer(this::renderOverlayMessage)
			.addLayer(this::renderTitleAndSubtitle)
			.addLayer(this::renderChat)
			.addLayer(this::renderPlayerList)
			.addLayer((context, tickDelta) -> this.subtitlesHud.render(context));
		this.layeredDrawer
			.addSubDrawer(layeredDrawer, () -> !client.options.hudHidden)
			.addLayer(this::renderSleepOverlay)
			.addSubDrawer(layeredDrawer2, () -> !client.options.hudHidden);
	}

	public void setDefaultTitleFade() {
		this.titleFadeInTicks = 10;
		this.titleStayTicks = 70;
		this.titleFadeOutTicks = 20;
	}

	public void render(DrawContext context, float tickDelta) {
		RenderSystem.enableDepthTest();
		this.layeredDrawer.render(context, tickDelta);
		RenderSystem.disableDepthTest();
	}

	private void renderMiscOverlays(DrawContext context, float tickDelta) {
		if (MinecraftClient.isFancyGraphicsOrBetter()) {
			this.renderVignetteOverlay(context, this.client.getCameraEntity());
		}

		float f = this.client.getLastFrameDuration();
		this.spyglassScale = MathHelper.lerp(0.5F * f, this.spyglassScale, 1.125F);
		if (this.client.options.getPerspective().isFirstPerson()) {
			if (this.client.player.isUsingSpyglass()) {
				this.renderSpyglassOverlay(context, this.spyglassScale);
			} else {
				this.spyglassScale = 0.5F;
				ItemStack itemStack = this.client.player.getInventory().getArmorStack(3);
				if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
					this.renderOverlay(context, PUMPKIN_BLUR, 1.0F);
				}
			}
		}

		if (this.client.player.getFrozenTicks() > 0) {
			this.renderOverlay(context, POWDER_SNOW_OUTLINE, this.client.player.getFreezingScale());
		}

		float g = MathHelper.lerp(tickDelta, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity);
		if (g > 0.0F && !this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
			this.renderPortalOverlay(context, g);
		}
	}

	private void renderSleepOverlay(DrawContext context, float tickDelta) {
		if (this.client.player.getSleepTimer() > 0) {
			this.client.getProfiler().push("sleep");
			float f = (float)this.client.player.getSleepTimer();
			float g = f / 100.0F;
			if (g > 1.0F) {
				g = 1.0F - (f - 100.0F) / 10.0F;
			}

			int i = (int)(220.0F * g) << 24 | 1052704;
			context.fill(RenderLayer.getGuiOverlay(), 0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), i);
			this.client.getProfiler().pop();
		}
	}

	private void renderOverlayMessage(DrawContext context, float tickDelta) {
		TextRenderer textRenderer = this.getTextRenderer();
		if (this.overlayMessage != null && this.overlayRemaining > 0) {
			this.client.getProfiler().push("overlayMessage");
			float f = (float)this.overlayRemaining - tickDelta;
			int i = (int)(f * 255.0F / 20.0F);
			if (i > 255) {
				i = 255;
			}

			if (i > 8) {
				context.getMatrices().push();
				context.getMatrices().translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() - 68), 0.0F);
				int j = 16777215;
				if (this.overlayTinted) {
					j = MathHelper.hsvToRgb(f / 50.0F, 0.7F, 0.6F) & 16777215;
				}

				int k = i << 24 & 0xFF000000;
				int l = textRenderer.getWidth(this.overlayMessage);
				this.drawTextBackground(context, textRenderer, -4, l, 16777215 | k);
				context.drawTextWithShadow(textRenderer, this.overlayMessage, -l / 2, -4, j | k);
				context.getMatrices().pop();
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderTitleAndSubtitle(DrawContext context, float tickDelta) {
		if (this.title != null && this.titleRemainTicks > 0) {
			TextRenderer textRenderer = this.getTextRenderer();
			this.client.getProfiler().push("titleAndSubtitle");
			float f = (float)this.titleRemainTicks - tickDelta;
			int i = 255;
			if (this.titleRemainTicks > this.titleFadeOutTicks + this.titleStayTicks) {
				float g = (float)(this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks) - f;
				i = (int)(g * 255.0F / (float)this.titleFadeInTicks);
			}

			if (this.titleRemainTicks <= this.titleFadeOutTicks) {
				i = (int)(f * 255.0F / (float)this.titleFadeOutTicks);
			}

			i = MathHelper.clamp(i, 0, 255);
			if (i > 8) {
				context.getMatrices().push();
				context.getMatrices().translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() / 2), 0.0F);
				context.getMatrices().push();
				context.getMatrices().scale(4.0F, 4.0F, 4.0F);
				int j = i << 24 & 0xFF000000;
				int k = textRenderer.getWidth(this.title);
				this.drawTextBackground(context, textRenderer, -10, k, 16777215 | j);
				context.drawTextWithShadow(textRenderer, this.title, -k / 2, -10, 16777215 | j);
				context.getMatrices().pop();
				if (this.subtitle != null) {
					context.getMatrices().push();
					context.getMatrices().scale(2.0F, 2.0F, 2.0F);
					int l = textRenderer.getWidth(this.subtitle);
					this.drawTextBackground(context, textRenderer, 5, l, 16777215 | j);
					context.drawTextWithShadow(textRenderer, this.subtitle, -l / 2, 5, 16777215 | j);
					context.getMatrices().pop();
				}

				context.getMatrices().pop();
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderChat(DrawContext context, float tickDelta) {
		if (!this.chatHud.isChatFocused()) {
			Window window = this.client.getWindow();
			int i = MathHelper.floor(this.client.mouse.getX() * (double)window.getScaledWidth() / (double)window.getWidth());
			int j = MathHelper.floor(this.client.mouse.getY() * (double)window.getScaledHeight() / (double)window.getHeight());
			this.chatHud.render(context, this.ticks, i, j, false);
		}
	}

	private void renderScoreboardSidebar(DrawContext context, float tickDelta) {
		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective scoreboardObjective = null;
		Team team = scoreboard.getScoreHolderTeam(this.client.player.getNameForScoreboard());
		if (team != null) {
			ScoreboardDisplaySlot scoreboardDisplaySlot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
			if (scoreboardDisplaySlot != null) {
				scoreboardObjective = scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
			}
		}

		ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
		if (scoreboardObjective2 != null) {
			this.renderScoreboardSidebar(context, scoreboardObjective2);
		}
	}

	private void renderPlayerList(DrawContext context, float tickDelta) {
		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
		if (!this.client.options.playerListKey.isPressed()
			|| this.client.isInSingleplayer() && this.client.player.networkHandler.getListedPlayerListEntries().size() <= 1 && scoreboardObjective == null) {
			this.playerListHud.setVisible(false);
		} else {
			this.playerListHud.setVisible(true);
			this.playerListHud.render(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
		}
	}

	private void drawTextBackground(DrawContext context, TextRenderer textRenderer, int yOffset, int width, int color) {
		int i = this.client.options.getTextBackgroundColor(0.0F);
		if (i != 0) {
			int j = -width / 2;
			context.fill(j - 2, yOffset - 2, j + width + 2, yOffset + 9 + 2, ColorHelper.Argb.mixColor(i, color));
		}
	}

	private void renderCrosshair(DrawContext context, float tickDelta) {
		GameOptions gameOptions = this.client.options;
		if (gameOptions.getPerspective().isFirstPerson()) {
			if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
				RenderSystem.enableBlend();
				if (this.debugHud.shouldShowDebugHud() && !this.client.player.hasReducedDebugInfo() && !gameOptions.getReducedDebugInfo().getValue()) {
					Camera camera = this.client.gameRenderer.getCamera();
					Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
					matrix4fStack.pushMatrix();
					matrix4fStack.mul(context.getMatrices().peek().getPositionMatrix());
					matrix4fStack.translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() / 2), 0.0F);
					matrix4fStack.rotateX(-camera.getPitch() * (float) (Math.PI / 180.0));
					matrix4fStack.rotateY(camera.getYaw() * (float) (Math.PI / 180.0));
					matrix4fStack.scale(-1.0F, -1.0F, -1.0F);
					RenderSystem.applyModelViewMatrix();
					RenderSystem.renderCrosshair(10);
					matrix4fStack.popMatrix();
					RenderSystem.applyModelViewMatrix();
				} else {
					RenderSystem.blendFuncSeparate(
						GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
					);
					int i = 15;
					context.drawGuiTexture(CROSSHAIR_TEXTURE, (context.getScaledWindowWidth() - 15) / 2, (context.getScaledWindowHeight() - 15) / 2, 15, 15);
					if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
						float f = this.client.player.getAttackCooldownProgress(0.0F);
						boolean bl = false;
						if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0F) {
							bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
							bl &= this.client.targetedEntity.isAlive();
						}

						int j = context.getScaledWindowHeight() / 2 - 7 + 16;
						int k = context.getScaledWindowWidth() / 2 - 8;
						if (bl) {
							context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
						} else if (f < 1.0F) {
							int l = (int)(f * 17.0F);
							context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
							context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
						}
					}

					RenderSystem.defaultBlendFunc();
				}

				RenderSystem.disableBlend();
			}
		}
	}

	private boolean shouldRenderSpectatorCrosshair(@Nullable HitResult hitResult) {
		if (hitResult == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.ENTITY) {
			return ((EntityHitResult)hitResult).getEntity() instanceof NamedScreenHandlerFactory;
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			World world = this.client.world;
			return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
		} else {
			return false;
		}
	}

	private void renderStatusEffectOverlay(DrawContext context, float tickDelta) {
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			Screen j = this.client.currentScreen;
			if (j instanceof AbstractInventoryScreen abstractInventoryScreen && abstractInventoryScreen.hideStatusEffectHud()) {
				return;
			}

			RenderSystem.enableBlend();
			int i = 0;
			int j = 0;
			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
			List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

			for(StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
				if (statusEffectInstance.shouldShowIcon()) {
					int k = context.getScaledWindowWidth();
					int l = 1;
					if (this.client.isDemo()) {
						l += 15;
					}

					if (registryEntry.value().isBeneficial()) {
						++i;
						k -= 25 * i;
					} else {
						++j;
						k -= 25 * j;
						l += 26;
					}

					float f = 1.0F;
					if (statusEffectInstance.isAmbient()) {
						context.drawGuiTexture(EFFECT_BACKGROUND_AMBIENT_TEXTURE, k, l, 24, 24);
					} else {
						context.drawGuiTexture(EFFECT_BACKGROUND_TEXTURE, k, l, 24, 24);
						if (statusEffectInstance.isDurationBelow(200)) {
							int m = statusEffectInstance.getDuration();
							int n = 10 - m / 20;
							f = MathHelper.clamp((float)m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
								+ MathHelper.cos((float)m * (float) Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
						}
					}

					Sprite sprite = statusEffectSpriteManager.getSprite(registryEntry);
					int o = l;
					float g = f;
					list.add((Runnable)() -> {
						context.setShaderColor(1.0F, 1.0F, 1.0F, g);
						context.drawSprite(k + 3, o + 3, 0, 18, 18, sprite);
						context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					});
				}
			}

			list.forEach(Runnable::run);
			RenderSystem.disableBlend();
		}
	}

	private void renderMainHud(DrawContext context, float tickDelta) {
		if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
			this.spectatorHud.renderSpectatorMenu(context);
		} else {
			this.renderHotbar(context, tickDelta);
		}

		int i = context.getScaledWindowWidth() / 2 - 91;
		JumpingMount jumpingMount = this.client.player.getJumpingMount();
		if (jumpingMount != null) {
			this.renderMountJumpBar(jumpingMount, context, i);
		} else if (this.shouldRenderExperience()) {
			this.renderExperienceBar(context, i);
		}

		if (this.client.interactionManager.hasStatusBars()) {
			this.renderStatusBars(context);
		}

		this.renderMountHealth(context);
		if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
			this.renderHeldItemTooltip(context);
		} else if (this.client.player.isSpectator()) {
			this.spectatorHud.render(context);
		}
	}

	private void renderHotbar(DrawContext context, float tickDelta) {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			ItemStack itemStack = playerEntity.getOffHandStack();
			Arm arm = playerEntity.getMainArm().getOpposite();
			int i = context.getScaledWindowWidth() / 2;
			int j = 182;
			int k = 91;
			RenderSystem.enableBlend();
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, -90.0F);
			context.drawGuiTexture(HOTBAR_TEXTURE, i - 91, context.getScaledWindowHeight() - 22, 182, 22);
			context.drawGuiTexture(
				HOTBAR_SELECTION_TEXTURE, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, context.getScaledWindowHeight() - 22 - 1, 24, 23
			);
			if (!itemStack.isEmpty()) {
				if (arm == Arm.LEFT) {
					context.drawGuiTexture(HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29, context.getScaledWindowHeight() - 23, 29, 24);
				} else {
					context.drawGuiTexture(HOTBAR_OFFHAND_RIGHT_TEXTURE, i + 91, context.getScaledWindowHeight() - 23, 29, 24);
				}
			}

			context.getMatrices().pop();
			RenderSystem.disableBlend();
			int l = 1;

			for(int m = 0; m < 9; ++m) {
				int n = i - 90 + m * 20 + 2;
				int o = context.getScaledWindowHeight() - 16 - 3;
				this.renderHotbarItem(context, n, o, tickDelta, playerEntity, playerEntity.getInventory().main.get(m), l++);
			}

			if (!itemStack.isEmpty()) {
				int m = context.getScaledWindowHeight() - 16 - 3;
				if (arm == Arm.LEFT) {
					this.renderHotbarItem(context, i - 91 - 26, m, tickDelta, playerEntity, itemStack, l++);
				} else {
					this.renderHotbarItem(context, i + 91 + 10, m, tickDelta, playerEntity, itemStack, l++);
				}
			}

			if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
				RenderSystem.enableBlend();
				float f = this.client.player.getAttackCooldownProgress(0.0F);
				if (f < 1.0F) {
					int n = context.getScaledWindowHeight() - 20;
					int o = i + 91 + 6;
					if (arm == Arm.RIGHT) {
						o = i - 91 - 22;
					}

					int p = (int)(f * 19.0F);
					context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, o, n, 18, 18);
					context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - p, o, n + 18 - p, 18, p);
				}

				RenderSystem.disableBlend();
			}
		}
	}

	private void renderMountJumpBar(JumpingMount mount, DrawContext context, int x) {
		this.client.getProfiler().push("jumpBar");
		float f = this.client.player.getMountJumpStrength();
		int i = 182;
		int j = (int)(f * 183.0F);
		int k = context.getScaledWindowHeight() - 32 + 3;
		RenderSystem.enableBlend();
		context.drawGuiTexture(JUMP_BAR_BACKGROUND_TEXTURE, x, k, 182, 5);
		if (mount.getJumpCooldown() > 0) {
			context.drawGuiTexture(JUMP_BAR_COOLDOWN_TEXTURE, x, k, 182, 5);
		} else if (j > 0) {
			context.drawGuiTexture(JUMP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, x, k, j, 5);
		}

		RenderSystem.disableBlend();
		this.client.getProfiler().pop();
	}

	private void renderExperienceBar(DrawContext context, int x) {
		this.client.getProfiler().push("expBar");
		int i = this.client.player.getNextLevelExperience();
		if (i > 0) {
			int j = 182;
			int k = (int)(this.client.player.experienceProgress * 183.0F);
			int l = context.getScaledWindowHeight() - 32 + 3;
			RenderSystem.enableBlend();
			context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, x, l, 182, 5);
			if (k > 0) {
				context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, x, l, k, 5);
			}

			RenderSystem.disableBlend();
		}

		this.client.getProfiler().pop();
	}

	private void renderExperienceLevel(DrawContext context, float x) {
		int i = this.client.player.experienceLevel;
		if (this.shouldRenderExperience() && i > 0) {
			this.client.getProfiler().push("expLevel");
			String string = i + "";
			int j = (context.getScaledWindowWidth() - this.getTextRenderer().getWidth(string)) / 2;
			int k = context.getScaledWindowHeight() - 31 - 4;
			context.drawText(this.getTextRenderer(), string, j + 1, k, 0, false);
			context.drawText(this.getTextRenderer(), string, j - 1, k, 0, false);
			context.drawText(this.getTextRenderer(), string, j, k + 1, 0, false);
			context.drawText(this.getTextRenderer(), string, j, k - 1, 0, false);
			context.drawText(this.getTextRenderer(), string, j, k, 8453920, false);
			this.client.getProfiler().pop();
		}
	}

	private boolean shouldRenderExperience() {
		return this.client.player.getJumpingMount() == null && this.client.interactionManager.hasExperienceBar();
	}

	private void renderHeldItemTooltip(DrawContext context) {
		this.client.getProfiler().push("selectedItemName");
		if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
			MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().getFormatting());
			if (this.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
				mutableText.formatted(Formatting.ITALIC);
			}

			int i = this.getTextRenderer().getWidth(mutableText);
			int j = (context.getScaledWindowWidth() - i) / 2;
			int k = context.getScaledWindowHeight() - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				k += 14;
			}

			int l = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
			if (l > 255) {
				l = 255;
			}

			if (l > 0) {
				context.fill(j - 2, k - 2, j + i + 2, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
				context.drawTextWithShadow(this.getTextRenderer(), mutableText, j, k, 16777215 + (l << 24));
			}
		}

		this.client.getProfiler().pop();
	}

	private void renderDemoTimer(DrawContext context, float tickDelta) {
		if (this.client.isDemo()) {
			this.client.getProfiler().push("demo");
			Text text;
			if (this.client.world.getTime() >= 120500L) {
				text = DEMO_EXPIRED_MESSAGE;
			} else {
				text = Text.translatable(
					"demo.remainingTime", StringHelper.formatTicks((int)(120500L - this.client.world.getTime()), this.client.world.getTickManager().getTickRate())
				);
			}

			int i = this.getTextRenderer().getWidth(text);
			context.drawTextWithShadow(this.getTextRenderer(), text, context.getScaledWindowWidth() - i - 10, 5, 16777215);
			this.client.getProfiler().pop();
		}
	}

	private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
		Scoreboard scoreboard = objective.getScoreboard();
		NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);

		@Environment(EnvType.CLIENT)
		record SidebarEntry(Text name, Text score, int scoreWidth) {
			final Text name;
			final Text score;
			final int scoreWidth;
		}

		SidebarEntry[] sidebarEntrys = (SidebarEntry[])scoreboard.getScoreboardEntries(objective)
			.stream()
			.filter(score -> !score.hidden())
			.sorted(SCOREBOARD_ENTRY_COMPARATOR)
			.limit(15L)
			.map(scoreboardEntry -> {
				Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.owner());
				Text textxx = scoreboardEntry.name();
				Text text2 = Team.decorateName(team, textxx);
				Text text3 = scoreboardEntry.formatted(numberFormat);
				int ixx = this.getTextRenderer().getWidth(text3);
				return new SidebarEntry(text2, text3, ixx);
			})
			.toArray(size -> new SidebarEntry[size]);
		Text text = objective.getDisplayName();
		int i = this.getTextRenderer().getWidth(text);
		int j = i;
		int k = this.getTextRenderer().getWidth(": ");

		for(SidebarEntry sidebarEntry : sidebarEntrys) {
			j = Math.max(j, this.getTextRenderer().getWidth(sidebarEntry.name) + (sidebarEntry.scoreWidth > 0 ? k + sidebarEntry.scoreWidth : 0));
		}

		int l = j;
		context.draw(() -> {
			int kxx = sidebarEntrys.length;
			int l = kxx * 9;
			int m = context.getScaledWindowHeight() / 2 + l / 3;
			int n = 3;
			int o = context.getScaledWindowWidth() - l - 3;
			int p = context.getScaledWindowWidth() - 3 + 2;
			int q = this.client.options.getTextBackgroundColor(0.3F);
			int r = this.client.options.getTextBackgroundColor(0.4F);
			int s = m - kxx * 9;
			context.fill(o - 2, s - 9 - 1, p, s - 1, r);
			context.fill(o - 2, s - 1, p, m, q);
			context.drawText(this.getTextRenderer(), text, o + l / 2 - i / 2, s - 9, Colors.WHITE, false);

			for(int t = 0; t < kxx; ++t) {
				SidebarEntry sidebarEntryxx = sidebarEntrys[t];
				int u = m - (kxx - t) * 9;
				context.drawText(this.getTextRenderer(), sidebarEntryxx.name, o, u, Colors.WHITE, false);
				context.drawText(this.getTextRenderer(), sidebarEntryxx.score, p - sidebarEntryxx.scoreWidth, u, Colors.WHITE, false);
			}
		});
	}

	@Nullable
	private PlayerEntity getCameraPlayer() {
		Entity var2 = this.client.getCameraEntity();
		return var2 instanceof PlayerEntity playerEntity ? playerEntity : null;
	}

	@Nullable
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

	private int getHeartCount(@Nullable LivingEntity entity) {
		if (entity != null && entity.isLiving()) {
			float f = entity.getMaxHealth();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}

	private int getHeartRows(int heartCount) {
		return (int)Math.ceil((double)heartCount / 10.0);
	}

	/**
	 * Renders the armor, health, air, and hunger bars.
	 */
	private void renderStatusBars(DrawContext context) {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			int i = MathHelper.ceil(playerEntity.getHealth());
			boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
			long l = Util.getMeasuringTimeMs();
			if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 20);
			} else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 10);
			}

			if (l - this.lastHealthCheckTime > 1000L) {
				this.lastHealthValue = i;
				this.renderHealthValue = i;
				this.lastHealthCheckTime = l;
			}

			this.lastHealthValue = i;
			int j = this.renderHealthValue;
			this.random.setSeed((long)(this.ticks * 312871));
			int k = context.getScaledWindowWidth() / 2 - 91;
			int m = context.getScaledWindowWidth() / 2 + 91;
			int n = context.getScaledWindowHeight() - 39;
			float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
			int o = MathHelper.ceil(playerEntity.getAbsorptionAmount());
			int p = MathHelper.ceil((f + (float)o) / 2.0F / 10.0F);
			int q = Math.max(10 - (p - 2), 3);
			int r = n - 10;
			int s = -1;
			if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
				s = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.getProfiler().push("armor");
			renderArmor(context, playerEntity, n, p, q, k);
			this.client.getProfiler().swap("health");
			this.renderHealthBar(context, playerEntity, k, n, q, s, f, i, j, o, bl);
			LivingEntity livingEntity = this.getRiddenEntity();
			int t = this.getHeartCount(livingEntity);
			if (t == 0) {
				this.client.getProfiler().swap("food");
				this.renderFood(context, playerEntity, n, m);
				r -= 10;
			}

			this.client.getProfiler().swap("air");
			int u = playerEntity.getMaxAir();
			int v = Math.min(playerEntity.getAir(), u);
			if (playerEntity.isSubmergedIn(FluidTags.WATER) || v < u) {
				int w = this.getHeartRows(t) - 1;
				r -= w * 10;
				int x = MathHelper.ceil((double)(v - 2) * 10.0 / (double)u);
				int y = MathHelper.ceil((double)v * 10.0 / (double)u) - x;
				RenderSystem.enableBlend();

				for(int z = 0; z < x + y; ++z) {
					if (z < x) {
						context.drawGuiTexture(AIR_TEXTURE, m - z * 8 - 9, r, 9, 9);
					} else {
						context.drawGuiTexture(AIR_BURSTING_TEXTURE, m - z * 8 - 9, r, 9, 9);
					}
				}

				RenderSystem.disableBlend();
			}

			this.client.getProfiler().pop();
		}
	}

	private static void renderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
		int l = player.getArmor();
		if (l > 0) {
			RenderSystem.enableBlend();
			int m = i - (j - 1) * k - 10;

			for(int n = 0; n < 10; ++n) {
				int o = x + n * 8;
				if (n * 2 + 1 < l) {
					context.drawGuiTexture(ARMOR_FULL_TEXTURE, o, m, 9, 9);
				}

				if (n * 2 + 1 == l) {
					context.drawGuiTexture(ARMOR_HALF_TEXTURE, o, m, 9, 9);
				}

				if (n * 2 + 1 > l) {
					context.drawGuiTexture(ARMOR_EMPTY_TEXTURE, o, m, 9, 9);
				}
			}

			RenderSystem.disableBlend();
		}
	}

	private void renderHealthBar(
		DrawContext context,
		PlayerEntity player,
		int x,
		int y,
		int lines,
		int regeneratingHeartIndex,
		float maxHealth,
		int lastHealth,
		int health,
		int absorption,
		boolean blinking
	) {
		InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(player);
		boolean bl = player.getWorld().getLevelProperties().isHardcore();
		int i = MathHelper.ceil((double)maxHealth / 2.0);
		int j = MathHelper.ceil((double)absorption / 2.0);
		int k = i * 2;

		for(int l = i + j - 1; l >= 0; --l) {
			int m = l / 10;
			int n = l % 10;
			int o = x + n * 8;
			int p = y - m * lines;
			if (lastHealth + absorption <= 4) {
				p += this.random.nextInt(2);
			}

			if (l < i && l == regeneratingHeartIndex) {
				p -= 2;
			}

			this.drawHeart(context, InGameHud.HeartType.CONTAINER, o, p, bl, blinking, false);
			int q = l * 2;
			boolean bl2 = l >= i;
			if (bl2) {
				int r = q - k;
				if (r < absorption) {
					boolean bl3 = r + 1 == absorption;
					this.drawHeart(context, heartType == InGameHud.HeartType.WITHERED ? heartType : InGameHud.HeartType.ABSORBING, o, p, bl, false, bl3);
				}
			}

			if (blinking && q < health) {
				boolean bl4 = q + 1 == health;
				this.drawHeart(context, heartType, o, p, bl, true, bl4);
			}

			if (q < lastHealth) {
				boolean bl4 = q + 1 == lastHealth;
				this.drawHeart(context, heartType, o, p, bl, false, bl4);
			}
		}
	}

	private void drawHeart(DrawContext context, InGameHud.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half) {
		RenderSystem.enableBlend();
		context.drawGuiTexture(type.getTexture(hardcore, half, blinking), x, y, 9, 9);
		RenderSystem.disableBlend();
	}

	private void renderFood(DrawContext context, PlayerEntity player, int top, int left) {
		HungerManager hungerManager = player.getHungerManager();
		int i = hungerManager.getFoodLevel();
		RenderSystem.enableBlend();

		for(int j = 0; j < 10; ++j) {
			int k = top;
			Identifier identifier;
			Identifier identifier2;
			Identifier identifier3;
			if (player.hasStatusEffect(StatusEffects.HUNGER)) {
				identifier = FOOD_EMPTY_HUNGER_TEXTURE;
				identifier2 = FOOD_HALF_HUNGER_TEXTURE;
				identifier3 = FOOD_FULL_HUNGER_TEXTURE;
			} else {
				identifier = FOOD_EMPTY_TEXTURE;
				identifier2 = FOOD_HALF_TEXTURE;
				identifier3 = FOOD_FULL_TEXTURE;
			}

			if (player.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (i * 3 + 1) == 0) {
				k = top + (this.random.nextInt(3) - 1);
			}

			int l = left - j * 8 - 9;
			context.drawGuiTexture(identifier, l, k, 9, 9);
			if (j * 2 + 1 < i) {
				context.drawGuiTexture(identifier3, l, k, 9, 9);
			}

			if (j * 2 + 1 == i) {
				context.drawGuiTexture(identifier2, l, k, 9, 9);
			}
		}

		RenderSystem.disableBlend();
	}

	private void renderMountHealth(DrawContext context) {
		LivingEntity livingEntity = this.getRiddenEntity();
		if (livingEntity != null) {
			int i = this.getHeartCount(livingEntity);
			if (i != 0) {
				int j = (int)Math.ceil((double)livingEntity.getHealth());
				this.client.getProfiler().swap("mountHealth");
				int k = context.getScaledWindowHeight() - 39;
				int l = context.getScaledWindowWidth() / 2 + 91;
				int m = k;
				int n = 0;
				RenderSystem.enableBlend();

				while(i > 0) {
					int o = Math.min(i, 10);
					i -= o;

					for(int p = 0; p < o; ++p) {
						int q = l - p * 8 - 9;
						context.drawGuiTexture(VEHICLE_CONTAINER_HEART_TEXTURE, q, m, 9, 9);
						if (p * 2 + 1 + n < j) {
							context.drawGuiTexture(VEHICLE_FULL_HEART_TEXTURE, q, m, 9, 9);
						}

						if (p * 2 + 1 + n == j) {
							context.drawGuiTexture(VEHICLE_HALF_HEART_TEXTURE, q, m, 9, 9);
						}
					}

					m -= 10;
					n += 20;
				}

				RenderSystem.disableBlend();
			}
		}
	}

	private void renderOverlay(DrawContext context, Identifier texture, float opacity) {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		context.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
		context.drawTexture(
			texture,
			0,
			0,
			-90,
			0.0F,
			0.0F,
			context.getScaledWindowWidth(),
			context.getScaledWindowHeight(),
			context.getScaledWindowWidth(),
			context.getScaledWindowHeight()
		);
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderSpyglassOverlay(DrawContext context, float scale) {
		float f = (float)Math.min(context.getScaledWindowWidth(), context.getScaledWindowHeight());
		float h = Math.min((float)context.getScaledWindowWidth() / f, (float)context.getScaledWindowHeight() / f) * scale;
		int i = MathHelper.floor(f * h);
		int j = MathHelper.floor(f * h);
		int k = (context.getScaledWindowWidth() - i) / 2;
		int l = (context.getScaledWindowHeight() - j) / 2;
		int m = k + i;
		int n = l + j;
		RenderSystem.enableBlend();
		context.drawTexture(SPYGLASS_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
		RenderSystem.disableBlend();
		context.fill(RenderLayer.getGuiOverlay(), 0, n, context.getScaledWindowWidth(), context.getScaledWindowHeight(), -90, Colors.BLACK);
		context.fill(RenderLayer.getGuiOverlay(), 0, 0, context.getScaledWindowWidth(), l, -90, Colors.BLACK);
		context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, Colors.BLACK);
		context.fill(RenderLayer.getGuiOverlay(), m, l, context.getScaledWindowWidth(), n, -90, Colors.BLACK);
	}

	private void updateVignetteDarkness(Entity entity) {
		BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
		float f = LightmapTextureManager.getBrightness(entity.getWorld().getDimension(), entity.getWorld().getLightLevel(blockPos));
		float g = MathHelper.clamp(1.0F - f, 0.0F, 1.0F);
		this.vignetteDarkness += (g - this.vignetteDarkness) * 0.01F;
	}

	private void renderVignetteOverlay(DrawContext context, @Nullable Entity entity) {
		WorldBorder worldBorder = this.client.world.getWorldBorder();
		float f = 0.0F;
		if (entity != null) {
			float g = (float)worldBorder.getDistanceInsideBorder(entity);
			double d = Math.min(
				worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getSizeLerpTarget() - worldBorder.getSize())
			);
			double e = Math.max((double)worldBorder.getWarningBlocks(), d);
			if ((double)g < e) {
				f = 1.0F - (float)((double)g / e);
			}
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
		if (f > 0.0F) {
			f = MathHelper.clamp(f, 0.0F, 1.0F);
			context.setShaderColor(0.0F, f, f, 1.0F);
		} else {
			float g = this.vignetteDarkness;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			context.setShaderColor(g, g, g, 1.0F);
		}

		context.drawTexture(
			VIGNETTE_TEXTURE,
			0,
			0,
			-90,
			0.0F,
			0.0F,
			context.getScaledWindowWidth(),
			context.getScaledWindowHeight(),
			context.getScaledWindowWidth(),
			context.getScaledWindowHeight()
		);
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}

	private void renderPortalOverlay(DrawContext context, float nauseaStrength) {
		if (nauseaStrength < 1.0F) {
			nauseaStrength *= nauseaStrength;
			nauseaStrength *= nauseaStrength;
			nauseaStrength = nauseaStrength * 0.8F + 0.2F;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		context.setShaderColor(1.0F, 1.0F, 1.0F, nauseaStrength);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
		context.drawSprite(0, 0, -90, context.getScaledWindowWidth(), context.getScaledWindowHeight(), sprite);
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(DrawContext context, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
		if (!stack.isEmpty()) {
			float f = (float)stack.getBobbingAnimationTime() - tickDelta;
			if (f > 0.0F) {
				float g = 1.0F + f / 5.0F;
				context.getMatrices().push();
				context.getMatrices().translate((float)(x + 8), (float)(y + 12), 0.0F);
				context.getMatrices().scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
				context.getMatrices().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
			}

			context.drawItem(player, stack, x, y, seed);
			if (f > 0.0F) {
				context.getMatrices().pop();
			}

			context.drawItemInSlot(this.client.textRenderer, stack, x, y);
		}
	}

	public void tick(boolean paused) {
		this.tickAutosaveIndicator();
		if (!paused) {
			this.tick();
		}
	}

	private void tick() {
		if (this.overlayRemaining > 0) {
			--this.overlayRemaining;
		}

		if (this.titleRemainTicks > 0) {
			--this.titleRemainTicks;
			if (this.titleRemainTicks <= 0) {
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
				this.heldItemTooltipFade = (int)(40.0 * this.client.options.getNotificationDisplayTime().getValue());
			} else if (this.heldItemTooltipFade > 0) {
				--this.heldItemTooltipFade;
			}

			this.currentStack = itemStack;
		}

		this.chatHud.tickRemovalQueueIfExists();
	}

	private void tickAutosaveIndicator() {
		MinecraftServer minecraftServer = this.client.getServer();
		boolean bl = minecraftServer != null && minecraftServer.isSaving();
		this.lastAutosaveIndicatorAlpha = this.autosaveIndicatorAlpha;
		this.autosaveIndicatorAlpha = MathHelper.lerp(0.2F, this.autosaveIndicatorAlpha, bl ? 1.0F : 0.0F);
	}

	public void setRecordPlayingOverlay(Text description) {
		Text text = Text.translatable("record.nowPlaying", description);
		this.setOverlayMessage(text, true);
		this.client.getNarratorManager().narrate(text);
	}

	public void setOverlayMessage(Text message, boolean tinted) {
		this.setCanShowChatDisabledScreen(false);
		this.overlayMessage = message;
		this.overlayRemaining = 60;
		this.overlayTinted = tinted;
	}

	public void setCanShowChatDisabledScreen(boolean canShowChatDisabledScreen) {
		this.canShowChatDisabledScreen = canShowChatDisabledScreen;
	}

	public boolean shouldShowChatDisabledScreen() {
		return this.canShowChatDisabledScreen && this.overlayRemaining > 0;
	}

	public void setTitleTicks(int fadeInTicks, int stayTicks, int fadeOutTicks) {
		if (fadeInTicks >= 0) {
			this.titleFadeInTicks = fadeInTicks;
		}

		if (stayTicks >= 0) {
			this.titleStayTicks = stayTicks;
		}

		if (fadeOutTicks >= 0) {
			this.titleFadeOutTicks = fadeOutTicks;
		}

		if (this.titleRemainTicks > 0) {
			this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
		}
	}

	public void setSubtitle(Text subtitle) {
		this.subtitle = subtitle;
	}

	public void setTitle(Text title) {
		this.title = title;
		this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
	}

	public void clearTitle() {
		this.title = null;
		this.subtitle = null;
		this.titleRemainTicks = 0;
	}

	public ChatHud getChatHud() {
		return this.chatHud;
	}

	public int getTicks() {
		return this.ticks;
	}

	public TextRenderer getTextRenderer() {
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
		this.debugHud.clear();
		this.chatHud.clear(true);
	}

	public BossBarHud getBossBarHud() {
		return this.bossBarHud;
	}

	public DebugHud getDebugHud() {
		return this.debugHud;
	}

	public void resetDebugHudChunk() {
		this.debugHud.resetChunk();
	}

	public void renderAutosaveIndicator(DrawContext context, float tickDelta) {
		if (this.client.options.getShowAutosaveIndicator().getValue() && (this.autosaveIndicatorAlpha > 0.0F || this.lastAutosaveIndicatorAlpha > 0.0F)) {
			int i = MathHelper.floor(
				255.0F * MathHelper.clamp(MathHelper.lerp(this.client.getTickDelta(), this.lastAutosaveIndicatorAlpha, this.autosaveIndicatorAlpha), 0.0F, 1.0F)
			);
			if (i > 8) {
				TextRenderer textRenderer = this.getTextRenderer();
				int j = textRenderer.getWidth(SAVING_LEVEL_TEXT);
				int k = 16777215 | i << 24 & Colors.BLACK;
				context.drawTextWithShadow(textRenderer, SAVING_LEVEL_TEXT, context.getScaledWindowWidth() - j - 10, context.getScaledWindowHeight() - 15, k);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static enum HeartType {
		CONTAINER(
			new Identifier("hud/heart/container"),
			new Identifier("hud/heart/container_blinking"),
			new Identifier("hud/heart/container"),
			new Identifier("hud/heart/container_blinking"),
			new Identifier("hud/heart/container_hardcore"),
			new Identifier("hud/heart/container_hardcore_blinking"),
			new Identifier("hud/heart/container_hardcore"),
			new Identifier("hud/heart/container_hardcore_blinking")
		),
		NORMAL(
			new Identifier("hud/heart/full"),
			new Identifier("hud/heart/full_blinking"),
			new Identifier("hud/heart/half"),
			new Identifier("hud/heart/half_blinking"),
			new Identifier("hud/heart/hardcore_full"),
			new Identifier("hud/heart/hardcore_full_blinking"),
			new Identifier("hud/heart/hardcore_half"),
			new Identifier("hud/heart/hardcore_half_blinking")
		),
		POISONED(
			new Identifier("hud/heart/poisoned_full"),
			new Identifier("hud/heart/poisoned_full_blinking"),
			new Identifier("hud/heart/poisoned_half"),
			new Identifier("hud/heart/poisoned_half_blinking"),
			new Identifier("hud/heart/poisoned_hardcore_full"),
			new Identifier("hud/heart/poisoned_hardcore_full_blinking"),
			new Identifier("hud/heart/poisoned_hardcore_half"),
			new Identifier("hud/heart/poisoned_hardcore_half_blinking")
		),
		WITHERED(
			new Identifier("hud/heart/withered_full"),
			new Identifier("hud/heart/withered_full_blinking"),
			new Identifier("hud/heart/withered_half"),
			new Identifier("hud/heart/withered_half_blinking"),
			new Identifier("hud/heart/withered_hardcore_full"),
			new Identifier("hud/heart/withered_hardcore_full_blinking"),
			new Identifier("hud/heart/withered_hardcore_half"),
			new Identifier("hud/heart/withered_hardcore_half_blinking")
		),
		ABSORBING(
			new Identifier("hud/heart/absorbing_full"),
			new Identifier("hud/heart/absorbing_full_blinking"),
			new Identifier("hud/heart/absorbing_half"),
			new Identifier("hud/heart/absorbing_half_blinking"),
			new Identifier("hud/heart/absorbing_hardcore_full"),
			new Identifier("hud/heart/absorbing_hardcore_full_blinking"),
			new Identifier("hud/heart/absorbing_hardcore_half"),
			new Identifier("hud/heart/absorbing_hardcore_half_blinking")
		),
		FROZEN(
			new Identifier("hud/heart/frozen_full"),
			new Identifier("hud/heart/frozen_full_blinking"),
			new Identifier("hud/heart/frozen_half"),
			new Identifier("hud/heart/frozen_half_blinking"),
			new Identifier("hud/heart/frozen_hardcore_full"),
			new Identifier("hud/heart/frozen_hardcore_full_blinking"),
			new Identifier("hud/heart/frozen_hardcore_half"),
			new Identifier("hud/heart/frozen_hardcore_half_blinking")
		);

		private final Identifier fullTexture;
		private final Identifier fullBlinkingTexture;
		private final Identifier halfTexture;
		private final Identifier halfBlinkingTexture;
		private final Identifier hardcoreFullTexture;
		private final Identifier hardcoreFullBlinkingTexture;
		private final Identifier hardcoreHalfTexture;
		private final Identifier hardcoreHalfBlinkingTexture;

		private HeartType(
			Identifier fullTexture,
			Identifier fullBlinkingTexture,
			Identifier halfTexture,
			Identifier halfBlinkingTexture,
			Identifier hardcoreFullTexture,
			Identifier hardcoreFullBlinkingTexture,
			Identifier hardcoreHalfTexture,
			Identifier hardcoreHalfBlinkingTexture
		) {
			this.fullTexture = fullTexture;
			this.fullBlinkingTexture = fullBlinkingTexture;
			this.halfTexture = halfTexture;
			this.halfBlinkingTexture = halfBlinkingTexture;
			this.hardcoreFullTexture = hardcoreFullTexture;
			this.hardcoreFullBlinkingTexture = hardcoreFullBlinkingTexture;
			this.hardcoreHalfTexture = hardcoreHalfTexture;
			this.hardcoreHalfBlinkingTexture = hardcoreHalfBlinkingTexture;
		}

		public Identifier getTexture(boolean hardcore, boolean half, boolean blinking) {
			if (!hardcore) {
				if (half) {
					return blinking ? this.halfBlinkingTexture : this.halfTexture;
				} else {
					return blinking ? this.fullBlinkingTexture : this.fullTexture;
				}
			} else if (half) {
				return blinking ? this.hardcoreHalfBlinkingTexture : this.hardcoreHalfTexture;
			} else {
				return blinking ? this.hardcoreFullBlinkingTexture : this.hardcoreFullTexture;
			}
		}

		static InGameHud.HeartType fromPlayerState(PlayerEntity player) {
			InGameHud.HeartType heartType;
			if (player.hasStatusEffect(StatusEffects.POISON)) {
				heartType = POISONED;
			} else if (player.hasStatusEffect(StatusEffects.WITHER)) {
				heartType = WITHERED;
			} else if (player.isFrozen()) {
				heartType = FROZEN;
			} else {
				heartType = NORMAL;
			}

			return heartType;
		}
	}
}
