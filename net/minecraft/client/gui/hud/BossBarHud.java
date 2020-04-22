/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BossBarHud
extends DrawableHelper {
    private static final Identifier BAR_TEX = new Identifier("textures/gui/bars.png");
    private final MinecraftClient client;
    private final Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

    public BossBarHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrixStack) {
        if (this.bossBars.isEmpty()) {
            return;
        }
        int i = this.client.getWindow().getScaledWidth();
        int j = 12;
        for (ClientBossBar clientBossBar : this.bossBars.values()) {
            int k = i / 2 - 91;
            int l = j;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(BAR_TEX);
            this.renderBossBar(matrixStack, k, l, clientBossBar);
            Text text = clientBossBar.getName();
            int m = this.client.textRenderer.getWidth(text);
            int n = i / 2 - m / 2;
            int o = l - 9;
            this.client.textRenderer.drawWithShadow(matrixStack, text, (float)n, (float)o, 0xFFFFFF);
            if ((j += 10 + this.client.textRenderer.fontHeight) < this.client.getWindow().getScaledHeight() / 3) continue;
            break;
        }
    }

    private void renderBossBar(MatrixStack matrixStack, int i, int j, BossBar bossBar) {
        int k;
        this.drawTexture(matrixStack, i, j, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5);
        if (bossBar.getOverlay() != BossBar.Style.PROGRESS) {
            this.drawTexture(matrixStack, i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        if ((k = (int)(bossBar.getPercent() * 183.0f)) > 0) {
            this.drawTexture(matrixStack, i, j, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, k, 5);
            if (bossBar.getOverlay() != BossBar.Style.PROGRESS) {
                this.drawTexture(matrixStack, i, j, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2 + 5, k, 5);
            }
        }
    }

    public void handlePacket(BossBarS2CPacket packet) {
        if (packet.getType() == BossBarS2CPacket.Type.ADD) {
            this.bossBars.put(packet.getUuid(), new ClientBossBar(packet));
        } else if (packet.getType() == BossBarS2CPacket.Type.REMOVE) {
            this.bossBars.remove(packet.getUuid());
        } else {
            this.bossBars.get(packet.getUuid()).handlePacket(packet);
        }
    }

    public void clear() {
        this.bossBars.clear();
    }

    public boolean shouldPlayDragonMusic() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.hasDragonMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenSky() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.getDarkenSky()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldThickenFog() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.getThickenFog()) continue;
                return true;
            }
        }
        return false;
    }
}

