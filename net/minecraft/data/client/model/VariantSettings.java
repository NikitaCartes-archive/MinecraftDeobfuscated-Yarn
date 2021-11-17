/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import com.google.gson.JsonPrimitive;
import net.minecraft.data.client.model.VariantSetting;
import net.minecraft.util.Identifier;

public class VariantSettings {
    public static final VariantSetting<Rotation> X = new VariantSetting<Rotation>("x", rotation -> new JsonPrimitive(rotation.degrees));
    public static final VariantSetting<Rotation> Y = new VariantSetting<Rotation>("y", rotation -> new JsonPrimitive(rotation.degrees));
    public static final VariantSetting<Identifier> MODEL = new VariantSetting<Identifier>("model", id -> new JsonPrimitive(id.toString()));
    public static final VariantSetting<Boolean> UVLOCK = new VariantSetting<Boolean>("uvlock", JsonPrimitive::new);
    public static final VariantSetting<Integer> WEIGHT = new VariantSetting<Integer>("weight", JsonPrimitive::new);

    public static enum Rotation {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        final int degrees;

        private Rotation(int degrees) {
            this.degrees = degrees;
        }
    }
}

