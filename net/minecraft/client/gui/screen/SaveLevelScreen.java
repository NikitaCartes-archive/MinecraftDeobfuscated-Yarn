/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SaveLevelScreen
extends Screen {
    public SaveLevelScreen(Text text) {
        super(text);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 70, 0xFFFFFF);
        super.render(i, j, f);
    }
}

