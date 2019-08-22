/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatHudLine {
    private final int timestamp;
    private final Text text;
    private final int id;

    public ChatHudLine(int i, Text text, int j) {
        this.text = text;
        this.timestamp = i;
        this.id = j;
    }

    public Text getText() {
        return this.text;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public int getId() {
        return this.id;
    }
}

