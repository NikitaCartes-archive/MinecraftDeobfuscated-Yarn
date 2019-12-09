/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class StaticSound {
    @Nullable
    private ByteBuffer sample;
    private final AudioFormat format;
    private boolean hasBuffer;
    private int streamBufferPointer;

    public StaticSound(ByteBuffer sample, AudioFormat format) {
        this.sample = sample;
        this.format = format;
    }

    OptionalInt getStreamBufferPointer() {
        if (!this.hasBuffer) {
            if (this.sample == null) {
                return OptionalInt.empty();
            }
            int i = AlUtil.getFormatId(this.format);
            int[] is = new int[1];
            AL10.alGenBuffers(is);
            if (AlUtil.checkErrors("Creating buffer")) {
                return OptionalInt.empty();
            }
            AL10.alBufferData(is[0], i, this.sample, (int)this.format.getSampleRate());
            if (AlUtil.checkErrors("Assigning buffer data")) {
                return OptionalInt.empty();
            }
            this.streamBufferPointer = is[0];
            this.hasBuffer = true;
            this.sample = null;
        }
        return OptionalInt.of(this.streamBufferPointer);
    }

    public void close() {
        if (this.hasBuffer) {
            AL10.alDeleteBuffers(new int[]{this.streamBufferPointer});
            if (AlUtil.checkErrors("Deleting stream buffers")) {
                return;
            }
        }
        this.hasBuffer = false;
    }

    public OptionalInt takeStreamBufferPointer() {
        OptionalInt optionalInt = this.getStreamBufferPointer();
        this.hasBuffer = false;
        return optionalInt;
    }
}

