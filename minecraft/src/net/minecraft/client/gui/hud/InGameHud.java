package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_336;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;

@Environment(EnvType.CLIENT)
public class InGameHud extends Drawable {
	private static final Identifier VIGNETTE_TEX = new Identifier("textures/misc/vignette.png");
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
	private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
	private final Random random = new Random();
	private final MinecraftClient client;
	private final ItemRenderer itemRenderer;
	private final ChatHud hudChat;
	private int ticks;
	private String overlayMessage = "";
	private int overlayRemaining;
	private boolean overlayTinted;
	public float field_2013 = 1.0F;
	private int field_2040;
	private ItemStack currentStack = ItemStack.EMPTY;
	private final DebugHud hudDebug;
	private final SubtitlesHud hudSubtitles;
	private final SpectatorHud hudSpectator;
	private final ScoreboardHud hudScoreboard;
	private final BossBarHud hudBossBar;
	private int field_2023;
	private String field_2016 = "";
	private String field_2039 = "";
	private int field_2037;
	private int field_2017;
	private int field_2036;
	private int field_2014;
	private int field_2033;
	private long field_2012;
	private long field_2032;
	private int scaledWidth;
	private int scaledHeight;
	private final Map<ChatMessageType, List<ClientChatListener>> listeners = Maps.<ChatMessageType, List<ClientChatListener>>newHashMap();

	public InGameHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.hudDebug = new DebugHud(minecraftClient);
		this.hudSpectator = new SpectatorHud(minecraftClient);
		this.hudChat = new ChatHud(minecraftClient);
		this.hudScoreboard = new ScoreboardHud(minecraftClient, this);
		this.hudBossBar = new BossBarHud(minecraftClient);
		this.hudSubtitles = new SubtitlesHud(minecraftClient);

		for (ChatMessageType chatMessageType : ChatMessageType.values()) {
			this.listeners.put(chatMessageType, Lists.newArrayList());
		}

		ClientChatListener clientChatListener = NarratorManager.INSTANCE;
		((List)this.listeners.get(ChatMessageType.field_11737)).add(new ChatListenerHud(minecraftClient));
		((List)this.listeners.get(ChatMessageType.field_11737)).add(clientChatListener);
		((List)this.listeners.get(ChatMessageType.field_11735)).add(new ChatListenerHud(minecraftClient));
		((List)this.listeners.get(ChatMessageType.field_11735)).add(clientChatListener);
		((List)this.listeners.get(ChatMessageType.field_11733)).add(new class_336(minecraftClient));
		this.method_1742();
	}

	public void method_1742() {
		this.field_2037 = 10;
		this.field_2017 = 70;
		this.field_2036 = 20;
	}

	public void draw(float f) {
		this.scaledWidth = this.client.window.getScaledWidth();
		this.scaledHeight = this.client.window.getScaledHeight();
		FontRenderer fontRenderer = this.getFontRenderer();
		GlStateManager.enableBlend();
		if (MinecraftClient.isFancyGraphicsEnabled()) {
			this.method_1735(this.client.getCameraEntity());
		} else {
			GlStateManager.enableDepthTest();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
		}

		ItemStack itemStack = this.client.player.inventory.getArmorStack(3);
		if (this.client.field_1690.field_1850 == 0 && itemStack.getItem() == Blocks.field_10147.getItem()) {
			this.method_1761();
		}

		if (!this.client.player.hasPotionEffect(StatusEffects.field_5916)) {
			float g = MathHelper.lerp(f, this.client.player.field_3911, this.client.player.field_3929);
			if (g > 0.0F) {
				this.method_1746(g);
			}
		}

		if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9219) {
			this.hudSpectator.draw(f);
		} else if (!this.client.field_1690.field_1842) {
			this.method_1759(f);
		}

		if (!this.client.field_1690.field_1842) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(ICONS);
			GlStateManager.enableBlend();
			GlStateManager.enableAlphaTest();
			this.method_1736(f);
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			this.client.getProfiler().push("bossHealth");
			this.hudBossBar.draw();
			this.client.getProfiler().pop();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(ICONS);
			if (this.client.interactionManager.hasStatusBars()) {
				this.method_1760();
			}

			this.method_1741();
			GlStateManager.disableBlend();
			int i = this.scaledWidth / 2 - 91;
			if (this.client.player.method_3131()) {
				this.method_1752(i);
			} else if (this.client.interactionManager.hasExperienceBar()) {
				this.method_1754(i);
			}

			if (this.client.field_1690.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.field_9219) {
				this.method_1749();
			} else if (this.client.player.isSpectator()) {
				this.hudSpectator.method_1979();
			}
		}

		if (this.client.player.getSleepTimer() > 0) {
			this.client.getProfiler().push("sleep");
			GlStateManager.disableDepthTest();
			GlStateManager.disableAlphaTest();
			float g = (float)this.client.player.getSleepTimer();
			float h = g / 100.0F;
			if (h > 1.0F) {
				h = 1.0F - (g - 100.0F) / 10.0F;
			}

			int j = (int)(220.0F * h) << 24 | 1052704;
			drawRect(0, 0, this.scaledWidth, this.scaledHeight, j);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableDepthTest();
			this.client.getProfiler().pop();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.client.isDemo()) {
			this.renderDemoTimer();
		}

		this.method_1765();
		if (this.client.field_1690.debugEnabled) {
			this.hudDebug.draw();
		}

		if (!this.client.field_1690.field_1842) {
			if (this.overlayRemaining > 0) {
				this.client.getProfiler().push("overlayMessage");
				float g = (float)this.overlayRemaining - f;
				int k = (int)(g * 255.0F / 20.0F);
				if (k > 255) {
					k = 255;
				}

				if (k > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight - 68), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(
						GlStateManager.SrcBlendFactor.SRC_ALPHA,
						GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SrcBlendFactor.ONE,
						GlStateManager.DstBlendFactor.ZERO
					);
					int j = 16777215;
					if (this.overlayTinted) {
						j = MathHelper.hsvToRgb(g / 50.0F, 0.7F, 0.6F) & 16777215;
					}

					fontRenderer.draw(this.overlayMessage, (float)(-fontRenderer.getStringWidth(this.overlayMessage) / 2), -4.0F, j + (k << 24 & 0xFF000000));
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.client.getProfiler().pop();
			}

			if (this.field_2023 > 0) {
				this.client.getProfiler().push("titleAndSubtitle");
				float gx = (float)this.field_2023 - f;
				int kx = 255;
				if (this.field_2023 > this.field_2036 + this.field_2017) {
					float l = (float)(this.field_2037 + this.field_2017 + this.field_2036) - gx;
					kx = (int)(l * 255.0F / (float)this.field_2037);
				}

				if (this.field_2023 <= this.field_2036) {
					kx = (int)(gx * 255.0F / (float)this.field_2036);
				}

				kx = MathHelper.clamp(kx, 0, 255);
				if (kx > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(
						GlStateManager.SrcBlendFactor.SRC_ALPHA,
						GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SrcBlendFactor.ONE,
						GlStateManager.DstBlendFactor.ZERO
					);
					GlStateManager.pushMatrix();
					GlStateManager.scalef(4.0F, 4.0F, 4.0F);
					int j = kx << 24 & 0xFF000000;
					fontRenderer.drawWithShadow(this.field_2016, (float)(-fontRenderer.getStringWidth(this.field_2016) / 2), -10.0F, 16777215 | j);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.scalef(2.0F, 2.0F, 2.0F);
					fontRenderer.drawWithShadow(this.field_2039, (float)(-fontRenderer.getStringWidth(this.field_2039) / 2), 5.0F, 16777215 | j);
					GlStateManager.popMatrix();
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.client.getProfiler().pop();
			}

			this.hudSubtitles.method_1957();
			Scoreboard scoreboard = this.client.world.getScoreboard();
			ScoreboardObjective scoreboardObjective = null;
			ScoreboardTeam scoreboardTeam = scoreboard.getPlayerTeam(this.client.player.getEntityName());
			if (scoreboardTeam != null) {
				int m = scoreboardTeam.getColor().getId();
				if (m >= 0) {
					scoreboardObjective = scoreboard.getObjectiveForSlot(3 + m);
				}
			}

			ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
			if (scoreboardObjective2 != null) {
				this.method_1757(scoreboardObjective2);
			}

			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, (float)(this.scaledHeight - 48), 0.0F);
			this.client.getProfiler().push("chat");
			this.hudChat.method_1805(this.ticks);
			this.client.getProfiler().pop();
			GlStateManager.popMatrix();
			scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
			if (!this.client.field_1690.keyPlayerList.method_1434()
				|| this.client.isIntegratedServerRunning() && this.client.player.networkHandler.method_2880().size() <= 1 && scoreboardObjective2 == null) {
				this.hudScoreboard.method_1921(false);
			} else {
				this.hudScoreboard.method_1921(true);
				this.hudScoreboard.method_1919(this.scaledWidth, scoreboard, scoreboardObjective2);
			}
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlphaTest();
	}

	private void method_1736(float f) {
		GameOptions gameOptions = this.client.field_1690;
		if (gameOptions.field_1850 == 0) {
			if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9219 && this.client.field_1692 == null) {
				HitResult hitResult = this.client.hitResult;
				if (hitResult == null || hitResult.type != HitResult.Type.BLOCK) {
					return;
				}

				BlockPos blockPos = hitResult.getBlockPos();
				if (!this.client.world.getBlockState(blockPos).getBlock().hasBlockEntity() || !(this.client.world.getBlockEntity(blockPos) instanceof Inventory)) {
					return;
				}
			}

			if (gameOptions.debugEnabled && !gameOptions.field_1842 && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), this.zOffset);
				Entity entity = this.client.getCameraEntity();
				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevPitch, entity.pitch), -1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevYaw, entity.yaw), 0.0F, 1.0F, 0.0F);
				GlStateManager.scalef(-1.0F, -1.0F, -1.0F);
				GLX.renderCrosshair(10);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.blendFuncSeparate(
					GlStateManager.SrcBlendFactor.ONE_MINUS_DST_COLOR,
					GlStateManager.DstBlendFactor.ONE_MINUS_SRC_COLOR,
					GlStateManager.SrcBlendFactor.ONE,
					GlStateManager.DstBlendFactor.ZERO
				);
				int i = 15;
				this.drawTexturedRect((float)this.scaledWidth / 2.0F - 7.5F, (float)this.scaledHeight / 2.0F - 7.5F, 0, 0, 15, 15);
				if (this.client.field_1690.attackIndicator == 1) {
					float g = this.client.player.method_7261(0.0F);
					boolean bl = false;
					if (this.client.field_1692 != null && this.client.field_1692 instanceof LivingEntity && g >= 1.0F) {
						bl = this.client.player.method_7279() > 5.0F;
						bl &= this.client.field_1692.isValid();
					}

					int j = this.scaledHeight / 2 - 7 + 16;
					int k = this.scaledWidth / 2 - 8;
					if (bl) {
						this.drawTexturedRect(k, j, 68, 94, 16, 16);
					} else if (g < 1.0F) {
						int l = (int)(g * 17.0F);
						this.drawTexturedRect(k, j, 36, 94, 16, 4);
						this.drawTexturedRect(k, j, 52, 94, l, 4);
					}
				}
			}
		}
	}

	protected void method_1765() {
		Collection<StatusEffectInstance> collection = this.client.player.getPotionEffects();
		if (!collection.isEmpty()) {
			this.client.getTextureManager().bindTexture(ContainerGui.BACKGROUND_TEXTURE);
			GlStateManager.enableBlend();
			int i = 0;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				if (statusEffect.hasIcon() && statusEffectInstance.shouldShowIcon()) {
					int k = this.scaledWidth;
					int l = 1;
					if (this.client.isDemo()) {
						l += 15;
					}

					int m = statusEffect.getIconIndex();
					if (statusEffect.method_5573()) {
						i++;
						k -= 25 * i;
					} else {
						j++;
						k -= 25 * j;
						l += 26;
					}

					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float f = 1.0F;
					if (statusEffectInstance.isAmbient()) {
						this.drawTexturedRect(k, l, 165, 166, 24, 24);
					} else {
						this.drawTexturedRect(k, l, 141, 166, 24, 24);
						if (statusEffectInstance.getDuration() <= 200) {
							int n = 10 - statusEffectInstance.getDuration() / 20;
							f = MathHelper.clamp((float)statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
								+ MathHelper.cos((float)statusEffectInstance.getDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
						}
					}

					GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
					int n = m % 12;
					int o = m / 12;
					this.drawTexturedRect(k + 3, l + 3, n * 18, 198 + o * 18, 18, 18);
				}
			}
		}
	}

	protected void method_1759(float f) {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(WIDGETS_TEX);
			ItemStack itemStack = playerEntity.getOffHandStack();
			OptionMainHand optionMainHand = playerEntity.getMainHand().getOpposite();
			int i = this.scaledWidth / 2;
			float g = this.zOffset;
			int j = 182;
			int k = 91;
			this.zOffset = -90.0F;
			this.drawTexturedRect(i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
			this.drawTexturedRect(i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
			if (!itemStack.isEmpty()) {
				if (optionMainHand == OptionMainHand.field_6182) {
					this.drawTexturedRect(i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
				} else {
					this.drawTexturedRect(i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
				}
			}

			this.zOffset = g;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			GuiLighting.enableForItems();

			for (int l = 0; l < 9; l++) {
				int m = i - 90 + l * 20 + 2;
				int n = this.scaledHeight - 16 - 3;
				this.method_1762(m, n, f, playerEntity, playerEntity.inventory.main.get(l));
			}

			if (!itemStack.isEmpty()) {
				int l = this.scaledHeight - 16 - 3;
				if (optionMainHand == OptionMainHand.field_6182) {
					this.method_1762(i - 91 - 26, l, f, playerEntity, itemStack);
				} else {
					this.method_1762(i + 91 + 10, l, f, playerEntity, itemStack);
				}
			}

			if (this.client.field_1690.attackIndicator == 2) {
				float h = this.client.player.method_7261(0.0F);
				if (h < 1.0F) {
					int m = this.scaledHeight - 20;
					int n = i + 91 + 6;
					if (optionMainHand == OptionMainHand.field_6183) {
						n = i - 91 - 22;
					}

					this.client.getTextureManager().bindTexture(Drawable.ICONS);
					int o = (int)(h * 19.0F);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexturedRect(n, m, 0, 94, 18, 18);
					this.drawTexturedRect(n, m + 18 - o, 18, 112 - o, 18, o);
				}
			}

			GuiLighting.disable();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

	public void method_1752(int i) {
		this.client.getProfiler().push("jumpBar");
		this.client.getTextureManager().bindTexture(Drawable.ICONS);
		float f = this.client.player.method_3151();
		int j = 182;
		int k = (int)(f * 183.0F);
		int l = this.scaledHeight - 32 + 3;
		this.drawTexturedRect(i, l, 0, 84, 182, 5);
		if (k > 0) {
			this.drawTexturedRect(i, l, 0, 89, k, 5);
		}

		this.client.getProfiler().pop();
	}

	public void method_1754(int i) {
		this.client.getProfiler().push("expBar");
		this.client.getTextureManager().bindTexture(Drawable.ICONS);
		int j = this.client.player.method_7349();
		if (j > 0) {
			int k = 182;
			int l = (int)(this.client.player.experienceBarProgress * 183.0F);
			int m = this.scaledHeight - 32 + 3;
			this.drawTexturedRect(i, m, 0, 64, 182, 5);
			if (l > 0) {
				this.drawTexturedRect(i, m, 0, 69, l, 5);
			}
		}

		this.client.getProfiler().pop();
		if (this.client.player.experience > 0) {
			this.client.getProfiler().push("expLevel");
			String string = "" + this.client.player.experience;
			int l = (this.scaledWidth - this.getFontRenderer().getStringWidth(string)) / 2;
			int m = this.scaledHeight - 31 - 4;
			this.getFontRenderer().draw(string, (float)(l + 1), (float)m, 0);
			this.getFontRenderer().draw(string, (float)(l - 1), (float)m, 0);
			this.getFontRenderer().draw(string, (float)l, (float)(m + 1), 0);
			this.getFontRenderer().draw(string, (float)l, (float)(m - 1), 0);
			this.getFontRenderer().draw(string, (float)l, (float)m, 8453920);
			this.client.getProfiler().pop();
		}
	}

	public void method_1749() {
		this.client.getProfiler().push("selectedItemName");
		if (this.field_2040 > 0 && !this.currentStack.isEmpty()) {
			TextComponent textComponent = new StringTextComponent("").append(this.currentStack.getDisplayName()).applyFormat(this.currentStack.getRarity().formatting);
			if (this.currentStack.hasDisplayName()) {
				textComponent.applyFormat(TextFormat.ITALIC);
			}

			String string = textComponent.getFormattedText();
			int i = (this.scaledWidth - this.getFontRenderer().getStringWidth(string)) / 2;
			int j = this.scaledHeight - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				j += 14;
			}

			int k = (int)((float)this.field_2040 * 256.0F / 10.0F);
			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SrcBlendFactor.SRC_ALPHA,
					GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SrcBlendFactor.ONE,
					GlStateManager.DstBlendFactor.ZERO
				);
				this.getFontRenderer().drawWithShadow(string, (float)i, (float)j, 16777215 + (k << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}

		this.client.getProfiler().pop();
	}

	public void renderDemoTimer() {
		this.client.getProfiler().push("demo");
		String string;
		if (this.client.world.getTime() >= 120500L) {
			string = I18n.translate("demo.demoExpired");
		} else {
			string = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
		}

		int i = this.getFontRenderer().getStringWidth(string);
		this.getFontRenderer().drawWithShadow(string, (float)(this.scaledWidth - i - 10), 5.0F, 16777215);
		this.client.getProfiler().pop();
	}

	private void method_1757(ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = scoreboardObjective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
		List<ScoreboardPlayerScore> list = (List<ScoreboardPlayerScore>)collection.stream()
			.filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());
		if (list.size() > 15) {
			collection = Lists.<ScoreboardPlayerScore>newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		String string = scoreboardObjective.getDisplayName().getFormattedText();
		int i = this.getFontRenderer().getStringWidth(string);
		int j = i;

		for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
			ScoreboardTeam scoreboardTeam = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			String string2 = ScoreboardTeam.method_1142(scoreboardTeam, new StringTextComponent(scoreboardPlayerScore.getPlayerName())).getFormattedText()
				+ ": "
				+ TextFormat.RED
				+ scoreboardPlayerScore.getScore();
			j = Math.max(j, this.getFontRenderer().getStringWidth(string2));
		}

		int k = collection.size() * this.getFontRenderer().fontHeight;
		int l = this.scaledHeight / 2 + k / 3;
		int m = 3;
		int n = this.scaledWidth - j - 3;
		int o = 0;

		for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
			o++;
			ScoreboardTeam scoreboardTeam2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string3 = ScoreboardTeam.method_1142(scoreboardTeam2, new StringTextComponent(scoreboardPlayerScore2.getPlayerName())).getFormattedText();
			String string4 = TextFormat.RED + "" + scoreboardPlayerScore2.getScore();
			int q = l - o * this.getFontRenderer().fontHeight;
			int r = this.scaledWidth - 3 + 2;
			drawRect(n - 2, q, r, q + this.getFontRenderer().fontHeight, 1342177280);
			this.getFontRenderer().draw(string3, (float)n, (float)q, 553648127);
			this.getFontRenderer().draw(string4, (float)(r - this.getFontRenderer().getStringWidth(string4)), (float)q, 553648127);
			if (o == collection.size()) {
				drawRect(n - 2, q - this.getFontRenderer().fontHeight - 1, r, q - 1, 1610612736);
				drawRect(n - 2, q - 1, r, q, 1342177280);
				this.getFontRenderer().draw(string, (float)(n + j / 2 - i / 2), (float)(q - this.getFontRenderer().fontHeight), 553648127);
			}
		}
	}

	private PlayerEntity getCameraPlayer() {
		return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
	}

	private LivingEntity method_1734() {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			Entity entity = playerEntity.getRiddenEntity();
			if (entity == null) {
				return null;
			}

			if (entity instanceof LivingEntity) {
				return (LivingEntity)entity;
			}
		}

		return null;
	}

	private int method_1744(LivingEntity livingEntity) {
		if (livingEntity != null && livingEntity.method_5709()) {
			float f = livingEntity.getHealthMaximum();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}

	private int method_1733(int i) {
		return (int)Math.ceil((double)i / 10.0);
	}

	private void method_1760() {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			int i = MathHelper.ceil(playerEntity.getHealth());
			boolean bl = this.field_2032 > (long)this.ticks && (this.field_2032 - (long)this.ticks) / 3L % 2L == 1L;
			long l = SystemUtil.getMeasuringTimeMs();
			if (i < this.field_2014 && playerEntity.field_6008 > 0) {
				this.field_2012 = l;
				this.field_2032 = (long)(this.ticks + 20);
			} else if (i > this.field_2014 && playerEntity.field_6008 > 0) {
				this.field_2012 = l;
				this.field_2032 = (long)(this.ticks + 10);
			}

			if (l - this.field_2012 > 1000L) {
				this.field_2014 = i;
				this.field_2033 = i;
				this.field_2012 = l;
			}

			this.field_2014 = i;
			int j = this.field_2033;
			this.random.setSeed((long)(this.ticks * 312871));
			HungerManager hungerManager = playerEntity.getHungerManager();
			int k = hungerManager.getFoodLevel();
			EntityAttributeInstance entityAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
			int m = this.scaledWidth / 2 - 91;
			int n = this.scaledWidth / 2 + 91;
			int o = this.scaledHeight - 39;
			float f = (float)entityAttributeInstance.getValue();
			int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
			int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
			int r = Math.max(10 - (q - 2), 3);
			int s = o - (q - 1) * r - 10;
			int t = o - 10;
			int u = p;
			int v = playerEntity.method_6096();
			int w = -1;
			if (playerEntity.hasPotionEffect(StatusEffects.field_5924)) {
				w = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.getProfiler().push("armor");

			for (int x = 0; x < 10; x++) {
				if (v > 0) {
					int y = m + x * 8;
					if (x * 2 + 1 < v) {
						this.drawTexturedRect(y, s, 34, 9, 9, 9);
					}

					if (x * 2 + 1 == v) {
						this.drawTexturedRect(y, s, 25, 9, 9, 9);
					}

					if (x * 2 + 1 > v) {
						this.drawTexturedRect(y, s, 16, 9, 9, 9);
					}
				}
			}

			this.client.getProfiler().swap("health");

			for (int xx = MathHelper.ceil((f + (float)p) / 2.0F) - 1; xx >= 0; xx--) {
				int yx = 16;
				if (playerEntity.hasPotionEffect(StatusEffects.field_5899)) {
					yx += 36;
				} else if (playerEntity.hasPotionEffect(StatusEffects.field_5920)) {
					yx += 72;
				}

				int z = 0;
				if (bl) {
					z = 1;
				}

				int aa = MathHelper.ceil((float)(xx + 1) / 10.0F) - 1;
				int ab = m + xx % 10 * 8;
				int ac = o - aa * r;
				if (i <= 4) {
					ac += this.random.nextInt(2);
				}

				if (u <= 0 && xx == w) {
					ac -= 2;
				}

				int ad = 0;
				if (playerEntity.world.getLevelProperties().isHardcore()) {
					ad = 5;
				}

				this.drawTexturedRect(ab, ac, 16 + z * 9, 9 * ad, 9, 9);
				if (bl) {
					if (xx * 2 + 1 < j) {
						this.drawTexturedRect(ab, ac, yx + 54, 9 * ad, 9, 9);
					}

					if (xx * 2 + 1 == j) {
						this.drawTexturedRect(ab, ac, yx + 63, 9 * ad, 9, 9);
					}
				}

				if (u > 0) {
					if (u == p && p % 2 == 1) {
						this.drawTexturedRect(ab, ac, yx + 153, 9 * ad, 9, 9);
						u--;
					} else {
						this.drawTexturedRect(ab, ac, yx + 144, 9 * ad, 9, 9);
						u -= 2;
					}
				} else {
					if (xx * 2 + 1 < i) {
						this.drawTexturedRect(ab, ac, yx + 36, 9 * ad, 9, 9);
					}

					if (xx * 2 + 1 == i) {
						this.drawTexturedRect(ab, ac, yx + 45, 9 * ad, 9, 9);
					}
				}
			}

			LivingEntity livingEntity = this.method_1734();
			int yxx = this.method_1744(livingEntity);
			if (yxx == 0) {
				this.client.getProfiler().swap("food");

				for (int zx = 0; zx < 10; zx++) {
					int aax = o;
					int abx = 16;
					int acx = 0;
					if (playerEntity.hasPotionEffect(StatusEffects.field_5903)) {
						abx += 36;
						acx = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						aax = o + (this.random.nextInt(3) - 1);
					}

					int adx = n - zx * 8 - 9;
					this.drawTexturedRect(adx, aax, 16 + acx * 9, 27, 9, 9);
					if (zx * 2 + 1 < k) {
						this.drawTexturedRect(adx, aax, abx + 36, 27, 9, 9);
					}

					if (zx * 2 + 1 == k) {
						this.drawTexturedRect(adx, aax, abx + 45, 27, 9, 9);
					}
				}

				t -= 10;
			}

			this.client.getProfiler().swap("air");
			int zx = playerEntity.getBreath();
			int aaxx = playerEntity.getMaxBreath();
			if (playerEntity.method_5777(FluidTags.field_15517) || zx < aaxx) {
				int abxx = this.method_1733(yxx) - 1;
				t -= abxx * 10;
				int acxx = MathHelper.ceil((double)(zx - 2) * 10.0 / (double)aaxx);
				int adxx = MathHelper.ceil((double)zx * 10.0 / (double)aaxx) - acxx;

				for (int ae = 0; ae < acxx + adxx; ae++) {
					if (ae < acxx) {
						this.drawTexturedRect(n - ae * 8 - 9, t, 16, 18, 9, 9);
					} else {
						this.drawTexturedRect(n - ae * 8 - 9, t, 25, 18, 9, 9);
					}
				}
			}

			this.client.getProfiler().pop();
		}
	}

	private void method_1741() {
		LivingEntity livingEntity = this.method_1734();
		if (livingEntity != null) {
			int i = this.method_1744(livingEntity);
			if (i != 0) {
				int j = (int)Math.ceil((double)livingEntity.getHealth());
				this.client.getProfiler().swap("mountHealth");
				int k = this.scaledHeight - 39;
				int l = this.scaledWidth / 2 + 91;
				int m = k;
				int n = 0;

				for (boolean bl = false; i > 0; n += 20) {
					int o = Math.min(i, 10);
					i -= o;

					for (int p = 0; p < o; p++) {
						int q = 52;
						int r = 0;
						int s = l - p * 8 - 9;
						this.drawTexturedRect(s, m, 52 + r * 9, 9, 9, 9);
						if (p * 2 + 1 + n < j) {
							this.drawTexturedRect(s, m, 88, 9, 9, 9);
						}

						if (p * 2 + 1 + n == j) {
							this.drawTexturedRect(s, m, 97, 9, 9, 9);
						}
					}

					m -= 10;
				}
			}
		}
	}

	private void method_1761() {
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlphaTest();
		this.client.getTextureManager().bindTexture(PUMPKIN_BLUR);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_1731(Entity entity) {
		if (entity != null) {
			float f = MathHelper.clamp(1.0F - entity.method_5718(), 0.0F, 1.0F);
			this.field_2013 = (float)((double)this.field_2013 + (double)(f - this.field_2013) * 0.01);
		}
	}

	private void method_1735(Entity entity) {
		WorldBorder worldBorder = this.client.world.getWorldBorder();
		float f = (float)worldBorder.contains(entity);
		double d = Math.min(worldBorder.method_11974() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getSize()));
		double e = Math.max((double)worldBorder.getWarningBlocks(), d);
		if ((double)f < e) {
			f = 1.0F - (float)((double)f / e);
		} else {
			f = 0.0F;
		}

		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.ZERO, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO
		);
		if (f > 0.0F) {
			GlStateManager.color4f(0.0F, f, f, 1.0F);
		} else {
			GlStateManager.color4f(this.field_2013, this.field_2013, this.field_2013, 1.0F);
		}

		this.client.getTextureManager().bindTexture(VIGNETTE_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
	}

	private void method_1746(float f) {
		if (f < 1.0F) {
			f *= f;
			f *= f;
			f = f * 0.8F + 0.2F;
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getSprite(Blocks.field_10316.getDefaultState());
		float g = sprite.getMinU();
		float h = sprite.getMinV();
		float i = sprite.getMaxU();
		float j = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture((double)g, (double)j).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture((double)i, (double)j).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture((double)i, (double)h).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture((double)g, (double)h).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_1762(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			float g = (float)itemStack.getUpdateCooldown() - f;
			if (g > 0.0F) {
				GlStateManager.pushMatrix();
				float h = 1.0F + g / 5.0F;
				GlStateManager.translatef((float)(i + 8), (float)(j + 12), 0.0F);
				GlStateManager.scalef(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translatef((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
			}

			this.itemRenderer.renderItemInGui(playerEntity, itemStack, i, j);
			if (g > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.itemRenderer.renderItemOverlaysInGUI(this.client.fontRenderer, itemStack, i, j);
		}
	}

	public void tick() {
		if (this.overlayRemaining > 0) {
			this.overlayRemaining--;
		}

		if (this.field_2023 > 0) {
			this.field_2023--;
			if (this.field_2023 <= 0) {
				this.field_2016 = "";
				this.field_2039 = "";
			}
		}

		this.ticks++;
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			this.method_1731(entity);
		}

		if (this.client.player != null) {
			ItemStack itemStack = this.client.player.inventory.getMainHandStack();
			if (itemStack.isEmpty()) {
				this.field_2040 = 0;
			} else if (this.currentStack.isEmpty()
				|| itemStack.getItem() != this.currentStack.getItem()
				|| !itemStack.getDisplayName().equals(this.currentStack.getDisplayName())) {
				this.field_2040 = 40;
			} else if (this.field_2040 > 0) {
				this.field_2040--;
			}

			this.currentStack = itemStack;
		}
	}

	public void setRecordPlayingOverlay(String string) {
		this.setOverlayMessage(I18n.translate("record.nowPlaying", string), true);
	}

	public void setOverlayMessage(String string, boolean bl) {
		this.overlayMessage = string;
		this.overlayRemaining = 60;
		this.overlayTinted = bl;
	}

	public void method_1763(String string, String string2, int i, int j, int k) {
		if (string == null && string2 == null && i < 0 && j < 0 && k < 0) {
			this.field_2016 = "";
			this.field_2039 = "";
			this.field_2023 = 0;
		} else if (string != null) {
			this.field_2016 = string;
			this.field_2023 = this.field_2037 + this.field_2017 + this.field_2036;
		} else if (string2 != null) {
			this.field_2039 = string2;
		} else {
			if (i >= 0) {
				this.field_2037 = i;
			}

			if (j >= 0) {
				this.field_2017 = j;
			}

			if (k >= 0) {
				this.field_2036 = k;
			}

			if (this.field_2023 > 0) {
				this.field_2023 = this.field_2037 + this.field_2017 + this.field_2036;
			}
		}
	}

	public void setOverlayMessage(TextComponent textComponent, boolean bl) {
		this.setOverlayMessage(textComponent.getString(), bl);
	}

	public void method_1755(ChatMessageType chatMessageType, TextComponent textComponent) {
		for (ClientChatListener clientChatListener : (List)this.listeners.get(chatMessageType)) {
			clientChatListener.method_1794(chatMessageType, textComponent);
		}
	}

	public ChatHud getHudChat() {
		return this.hudChat;
	}

	public int getTicks() {
		return this.ticks;
	}

	public FontRenderer getFontRenderer() {
		return this.client.fontRenderer;
	}

	public SpectatorHud getSpectatorWidget() {
		return this.hudSpectator;
	}

	public ScoreboardHud getScoreboardWidget() {
		return this.hudScoreboard;
	}

	public void clear() {
		this.hudScoreboard.clear();
		this.hudBossBar.clear();
		this.client.getToastManager().clear();
	}

	public BossBarHud getHudBossBar() {
		return this.hudBossBar;
	}

	public void method_1745() {
		this.hudDebug.resetChunk();
	}
}
