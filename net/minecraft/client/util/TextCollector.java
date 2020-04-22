/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    private boolean needsFakeRoot = true;
    @Nullable
    private MutableText root;

    public void add(MutableText text) {
        if (this.root == null) {
            this.root = text;
        } else {
            if (this.needsFakeRoot) {
                this.root = new LiteralText("").append(this.root);
                this.needsFakeRoot = false;
            }
            this.root.append(text);
        }
    }

    @Nullable
    public MutableText getRawCombined() {
        return this.root;
    }

    public MutableText getCombined() {
        return this.root != null ? this.root : new LiteralText("");
    }
}

