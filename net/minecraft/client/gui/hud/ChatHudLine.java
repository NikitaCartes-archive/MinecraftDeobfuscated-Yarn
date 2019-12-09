/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatHudLine {
    private final int creationTick;
    private final Text text;
    private final int id;

    public ChatHudLine(int creationTick, Text text, int id) {
        this.text = text;
        this.creationTick = creationTick;
        this.id = id;
    }

    public Text getText() {
        return this.text;
    }

    public int getCreationTick() {
        return this.creationTick;
    }

    public int getId() {
        return this.id;
    }
}

