/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface SpectatorMenuCommand {
    public void use(SpectatorMenu var1);

    public Text getName();

    public void renderIcon(float var1, int var2);

    public boolean enabled();
}

