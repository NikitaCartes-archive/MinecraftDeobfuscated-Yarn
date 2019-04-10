package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BossBarHud extends DrawableHelper {
	private static final Identifier BAR_TEX = new Identifier("textures/gui/bars.png");
	private final MinecraftClient client;
	private final Map<UUID, ClientBossBar> bossBars = Maps.<UUID, ClientBossBar>newLinkedHashMap();

	public BossBarHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void draw() {
		if (!this.bossBars.isEmpty()) {
			int i = this.client.window.getScaledWidth();
			int j = 12;

			for (ClientBossBar clientBossBar : this.bossBars.values()) {
				int k = i / 2 - 91;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.client.getTextureManager().bindTexture(BAR_TEX);
				this.drawBossBar(k, j, clientBossBar);
				String string = clientBossBar.getName().getFormattedText();
				int m = this.client.textRenderer.getStringWidth(string);
				int n = i / 2 - m / 2;
				int o = j - 9;
				this.client.textRenderer.drawWithShadow(string, (float)n, (float)o, 16777215);
				j += 10 + 9;
				if (j >= this.client.window.getScaledHeight() / 3) {
					break;
				}
			}
		}
	}

	private void drawBossBar(int i, int j, BossBar bossBar) {
		this.blit(i, j, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5);
		if (bossBar.getOverlay() != BossBar.Overlay.field_5795) {
			this.blit(i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
		}

		int k = (int)(bossBar.getPercent() * 183.0F);
		if (k > 0) {
			this.blit(i, j, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, k, 5);
			if (bossBar.getOverlay() != BossBar.Overlay.field_5795) {
				this.blit(i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2 + 5, k, 5);
			}
		}
	}

	public void handlePacket(BossBarS2CPacket bossBarS2CPacket) {
		if (bossBarS2CPacket.getType() == BossBarS2CPacket.Type.ADD) {
			this.bossBars.put(bossBarS2CPacket.getUuid(), new ClientBossBar(bossBarS2CPacket));
		} else if (bossBarS2CPacket.getType() == BossBarS2CPacket.Type.REMOVE) {
			this.bossBars.remove(bossBarS2CPacket.getUuid());
		} else {
			((ClientBossBar)this.bossBars.get(bossBarS2CPacket.getUuid())).handlePacket(bossBarS2CPacket);
		}
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
				if (bossBar.getDarkenSky()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean shouldThickenFog() {
		if (!this.bossBars.isEmpty()) {
			for (BossBar bossBar : this.bossBars.values()) {
				if (bossBar.getThickenFog()) {
					return true;
				}
			}
		}

		return false;
	}
}
