/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.realms.RealmsAbstractButtonProxy;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractRealmsButton<P extends AbstractButtonWidget> {
    public abstract P getProxy();

    public boolean active() {
        return ((RealmsAbstractButtonProxy)this.getProxy()).active();
    }

    public void active(boolean bl) {
        ((RealmsAbstractButtonProxy)this.getProxy()).active(bl);
    }

    public boolean isVisible() {
        return ((RealmsAbstractButtonProxy)this.getProxy()).isVisible();
    }

    public void setVisible(boolean bl) {
        ((RealmsAbstractButtonProxy)this.getProxy()).setVisible(bl);
    }

    public void render(int i, int j, float f) {
        ((AbstractButtonWidget)this.getProxy()).render(i, j, f);
    }

    public void blit(int i, int j, int k, int l, int m, int n) {
        ((DrawableHelper)this.getProxy()).blit(i, j, k, l, m, n);
    }

    public void tick() {
    }
}

