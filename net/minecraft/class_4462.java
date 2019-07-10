/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class class_4462 {
    private final Vec3d field_20313;
    private final float field_20314;
    private final float field_20315;

    public class_4462(Vec3d vec3d, float f, float g) {
        this.field_20313 = vec3d;
        this.field_20314 = f;
        this.field_20315 = g;
    }

    public Vec3d method_21702() {
        return this.field_20313;
    }

    public float method_21703() {
        return this.field_20314;
    }

    public float method_21704() {
        return this.field_20315;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_4462 lv = (class_4462)object;
        return Float.compare(lv.field_20314, this.field_20314) == 0 && Float.compare(lv.field_20315, this.field_20315) == 0 && Objects.equals(this.field_20313, lv.field_20313);
    }

    public int hashCode() {
        return Objects.hash(this.field_20313, Float.valueOf(this.field_20314), Float.valueOf(this.field_20315));
    }
}

