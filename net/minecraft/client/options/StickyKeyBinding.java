/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public class StickyKeyBinding
extends KeyBinding {
    private final BooleanSupplier toggleGetter;

    public StickyKeyBinding(String string, int i, String string2, BooleanSupplier booleanSupplier) {
        super(string, InputUtil.Type.KEYSYM, i, string2);
        this.toggleGetter = booleanSupplier;
    }

    @Override
    public void setPressed(boolean bl) {
        if (this.toggleGetter.getAsBoolean()) {
            if (bl) {
                super.setPressed(!this.isPressed());
            }
        } else {
            super.setPressed(bl);
        }
    }
}

