/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class LockButtonWidget
extends ButtonWidget {
    private boolean locked;

    public LockButtonWidget(int i, int j, ButtonWidget.PressAction pressAction) {
        super(i, j, 20, 20, I18n.translate("narrator.button.difficulty_lock", new Object[0]), pressAction);
    }

    @Override
    protected String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + (this.isLocked() ? I18n.translate("narrator.button.difficulty_lock.locked", new Object[0]) : I18n.translate("narrator.button.difficulty_lock.unlocked", new Object[0]));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean bl) {
        this.locked = bl;
    }

    @Override
    public void renderButton(int i, int j, float f) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        IconLocation iconLocation = !this.active ? (this.locked ? IconLocation.LOCKED_DISABLED : IconLocation.UNLOCKED_DISABLED) : (this.isHovered() ? (this.locked ? IconLocation.LOCKED_HOVER : IconLocation.UNLOCKED_HOVER) : (this.locked ? IconLocation.LOCKED : IconLocation.UNLOCKED));
        this.blit(this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
    }

    @Environment(value=EnvType.CLIENT)
    static enum IconLocation {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int u;
        private final int v;

        private IconLocation(int j, int k) {
            this.u = j;
            this.v = k;
        }

        public int getU() {
            return this.u;
        }

        public int getV() {
            return this.v;
        }
    }
}

