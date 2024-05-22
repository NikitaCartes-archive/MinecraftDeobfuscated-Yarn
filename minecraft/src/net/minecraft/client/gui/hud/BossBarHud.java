package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BossBarHud {
	private static final int WIDTH = 182;
	private static final int HEIGHT = 5;
	private static final Identifier[] BACKGROUND_TEXTURES = new Identifier[]{
		Identifier.ofVanilla("boss_bar/pink_background"),
		Identifier.ofVanilla("boss_bar/blue_background"),
		Identifier.ofVanilla("boss_bar/red_background"),
		Identifier.ofVanilla("boss_bar/green_background"),
		Identifier.ofVanilla("boss_bar/yellow_background"),
		Identifier.ofVanilla("boss_bar/purple_background"),
		Identifier.ofVanilla("boss_bar/white_background")
	};
	private static final Identifier[] PROGRESS_TEXTURES = new Identifier[]{
		Identifier.ofVanilla("boss_bar/pink_progress"),
		Identifier.ofVanilla("boss_bar/blue_progress"),
		Identifier.ofVanilla("boss_bar/red_progress"),
		Identifier.ofVanilla("boss_bar/green_progress"),
		Identifier.ofVanilla("boss_bar/yellow_progress"),
		Identifier.ofVanilla("boss_bar/purple_progress"),
		Identifier.ofVanilla("boss_bar/white_progress")
	};
	private static final Identifier[] NOTCHED_BACKGROUND_TEXTURES = new Identifier[]{
		Identifier.ofVanilla("boss_bar/notched_6_background"),
		Identifier.ofVanilla("boss_bar/notched_10_background"),
		Identifier.ofVanilla("boss_bar/notched_12_background"),
		Identifier.ofVanilla("boss_bar/notched_20_background")
	};
	private static final Identifier[] NOTCHED_PROGRESS_TEXTURES = new Identifier[]{
		Identifier.ofVanilla("boss_bar/notched_6_progress"),
		Identifier.ofVanilla("boss_bar/notched_10_progress"),
		Identifier.ofVanilla("boss_bar/notched_12_progress"),
		Identifier.ofVanilla("boss_bar/notched_20_progress")
	};
	private final MinecraftClient client;
	final Map<UUID, ClientBossBar> bossBars = Maps.<UUID, ClientBossBar>newLinkedHashMap();

	public BossBarHud(MinecraftClient client) {
		this.client = client;
	}

	public void render(DrawContext context) {
		if (!this.bossBars.isEmpty()) {
			this.client.getProfiler().push("bossHealth");
			int i = context.getScaledWindowWidth();
			int j = 12;

			for (ClientBossBar clientBossBar : this.bossBars.values()) {
				int k = i / 2 - 91;
				this.renderBossBar(context, k, j, clientBossBar);
				Text text = clientBossBar.getName();
				int m = this.client.textRenderer.getWidth(text);
				int n = i / 2 - m / 2;
				int o = j - 9;
				context.drawTextWithShadow(this.client.textRenderer, text, n, o, 16777215);
				j += 10 + 9;
				if (j >= context.getScaledWindowHeight() / 3) {
					break;
				}
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderBossBar(DrawContext context, int x, int y, BossBar bossBar) {
		this.renderBossBar(context, x, y, bossBar, 182, BACKGROUND_TEXTURES, NOTCHED_BACKGROUND_TEXTURES);
		int i = MathHelper.lerpPositive(bossBar.getPercent(), 0, 182);
		if (i > 0) {
			this.renderBossBar(context, x, y, bossBar, i, PROGRESS_TEXTURES, NOTCHED_PROGRESS_TEXTURES);
		}
	}

	private void renderBossBar(DrawContext context, int x, int y, BossBar bossBar, int width, Identifier[] textures, Identifier[] notchedTextures) {
		RenderSystem.enableBlend();
		context.drawGuiTexture(textures[bossBar.getColor().ordinal()], 182, 5, 0, 0, x, y, width, 5);
		if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
			context.drawGuiTexture(notchedTextures[bossBar.getStyle().ordinal() - 1], 182, 5, 0, 0, x, y, width, 5);
		}

		RenderSystem.disableBlend();
	}

	public void handlePacket(BossBarS2CPacket packet) {
		packet.accept(new BossBarS2CPacket.Consumer() {
			@Override
			public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
				BossBarHud.this.bossBars.put(uuid, new ClientBossBar(uuid, name, percent, color, style, darkenSky, dragonMusic, thickenFog));
			}

			@Override
			public void remove(UUID uuid) {
				BossBarHud.this.bossBars.remove(uuid);
			}

			@Override
			public void updateProgress(UUID uuid, float percent) {
				((ClientBossBar)BossBarHud.this.bossBars.get(uuid)).setPercent(percent);
			}

			@Override
			public void updateName(UUID uuid, Text name) {
				((ClientBossBar)BossBarHud.this.bossBars.get(uuid)).setName(name);
			}

			@Override
			public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
				ClientBossBar clientBossBar = (ClientBossBar)BossBarHud.this.bossBars.get(id);
				clientBossBar.setColor(color);
				clientBossBar.setStyle(style);
			}

			@Override
			public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
				ClientBossBar clientBossBar = (ClientBossBar)BossBarHud.this.bossBars.get(uuid);
				clientBossBar.setDarkenSky(darkenSky);
				clientBossBar.setDragonMusic(dragonMusic);
				clientBossBar.setThickenFog(thickenFog);
			}
		});
	}

	public void clear() {
		this.bossBars.clear();
	}

	public boolean shouldPlayDragonMusic() {
		if (!this.bossBars.isEmpty()) {
			for (BossBar bossBar : this.bossBars.values()) {
				if (bossBar.hasDragonMusic()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean shouldDarkenSky() {
		if (!this.bossBars.isEmpty()) {
			for (BossBar bossBar : this.bossBars.values()) {
				if (bossBar.shouldDarkenSky()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean shouldThickenFog() {
		if (!this.bossBars.isEmpty()) {
			for (BossBar bossBar : this.bossBars.values()) {
				if (bossBar.shouldThickenFog()) {
					return true;
				}
			}
		}

		return false;
	}
}
