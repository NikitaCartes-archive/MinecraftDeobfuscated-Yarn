/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public class SoundEvent {
    public static final Codec<SoundEvent> CODEC = Identifier.CODEC.xmap(SoundEvent::new, soundEvent -> soundEvent.id);
    private final Identifier id;
    private final float field_38690;
    private final boolean field_38691;

    public SoundEvent(Identifier id) {
        this(id, 16.0f, false);
    }

    public SoundEvent(Identifier identifier, float f) {
        this(identifier, f, true);
    }

    private SoundEvent(Identifier identifier, float f, boolean bl) {
        this.id = identifier;
        this.field_38690 = f;
        this.field_38691 = bl;
    }

    public Identifier getId() {
        return this.id;
    }

    public float method_43044(float f) {
        if (this.field_38691) {
            return this.field_38690;
        }
        return f > 1.0f ? 16.0f * f : 16.0f;
    }
}

