/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ClientBossBar
extends BossBar {
    private static final long field_32204 = 100L;
    protected float healthLatest;
    protected long timeHealthSet;

    public ClientBossBar(UUID uUID, Text text, float f, BossBar.Color color, BossBar.Style style, boolean bl, boolean bl2, boolean bl3) {
        super(uUID, text, color, style);
        this.healthLatest = f;
        this.percent = f;
        this.timeHealthSet = Util.getMeasuringTimeMs();
        this.setDarkenSky(bl);
        this.setDragonMusic(bl2);
        this.setThickenFog(bl3);
    }

    @Override
    public void setPercent(float percentage) {
        this.percent = this.getPercent();
        this.healthLatest = percentage;
        this.timeHealthSet = Util.getMeasuringTimeMs();
    }

    @Override
    public float getPercent() {
        long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
        float f = MathHelper.clamp((float)l / 100.0f, 0.0f, 1.0f);
        return MathHelper.lerp(f, this.percent, this.healthLatest);
    }
}

