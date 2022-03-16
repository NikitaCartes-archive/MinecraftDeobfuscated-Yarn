package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BossBarHud extends DrawableHelper {
	private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");
	private static final int WIDTH = 182;
	private static final int HEIGHT = 5;
	private static final int NOTCHED_BAR_OVERLAY_V = 80;
	private final MinecraftClient client;
	final Map<UUID, ClientBossBar> bossBars = Maps.<UUID, ClientBossBar>newLinkedHashMap();

	public BossBarHud(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack matrices) {
		if (!this.bossBars.isEmpty()) {
			int i = this.client.getWindow().getScaledWidth();
			int j = 12;

			for (ClientBossBar clientBossBar : this.bossBars.values()) {
				int k = i / 2 - 91;
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, BARS_TEXTURE);
				this.renderBossBar(matrices, k, j, clientBossBar);
				Text text = clientBossBar.getName();
				int m = this.client.textRenderer.getWidth(text);
				int n = i / 2 - m / 2;
				int o = j - 9;
				this.client.textRenderer.drawWithShadow(matrices, text, (float)n, (float)o, 16777215);
				j += 10 + 9;
				if (j >= this.client.getWindow().getScaledHeight() / 3) {
					break;
				}
			}
		}
	}

	private void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
		this.renderBossBar(matrices, x, y, bossBar, 182, 0);
		int i = (int)(bossBar.getPercent() * 183.0F);
		if (i > 0) {
			this.renderBossBar(matrices, x, y, bossBar, i, 5);
		}
	}

	private void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar, int width, int height) {
		this.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2 + height, width, 5);
		if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			this.drawTexture(matrices, x, y, 0, 80 + (bossBar.getStyle().ordinal() - 1) * 5 * 2 + height, width, 5);
			RenderSystem.disableBlend();
		}
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
