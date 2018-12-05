package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BossBarHud extends Drawable {
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
				this.client.fontRenderer.drawWithShadow(string, (float)(i / 2 - this.client.fontRenderer.getStringWidth(string) / 2), (float)(j - 9), 16777215);
				j += 10 + this.client.fontRenderer.FONT_HEIGHT;
				if (j >= this.client.window.getScaledHeight() / 3) {
					break;
				}
			}
		}
	}

	private void drawBossBar(int i, int j, BossBar bossBar) {
		this.drawTexturedRect(i, j, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5);
		if (bossBar.getOverlay() != BossBar.Overlay.field_5795) {
			this.drawTexturedRect(i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
		}

		int k = (int)(bossBar.getPercent() * 183.0F);
		if (k > 0) {
			this.drawTexturedRect(i, j, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, k, 5);
			if (bossBar.getOverlay() != BossBar.Overlay.field_5795) {
				this.drawTexturedRect(i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2 + 5, k, 5);
			}
		}
	}

	public void handlePacket(BossBarClientPacket bossBarClientPacket) {
		if (bossBarClientPacket.getType() == BossBarClientPacket.Type.ADD) {
			this.bossBars.put(bossBarClientPacket.getUuid(), new ClientBossBar(bossBarClientPacket));
		} else if (bossBarClientPacket.getType() == BossBarClientPacket.Type.REMOVE) {
			this.bossBars.remove(bossBarClientPacket.getUuid());
		} else {
			((ClientBossBar)this.bossBars.get(bossBarClientPacket.getUuid())).handlePacket(bossBarClientPacket);
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
