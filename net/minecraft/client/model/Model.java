/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class Model
implements Consumer<ModelPart> {
    public int textureWidth = 64;
    public int textureHeight = 32;

    public void onPartAdded(ModelPart modelPart) {
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.onPartAdded((ModelPart)object);
    }
}

