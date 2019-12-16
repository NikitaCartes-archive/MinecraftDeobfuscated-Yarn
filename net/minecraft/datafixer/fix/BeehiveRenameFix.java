/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.fix.PointOfInterestRenameFix;

public class BeehiveRenameFix
extends PointOfInterestRenameFix {
    public BeehiveRenameFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected String rename(String string) {
        return string.equals("minecraft:bee_hive") ? "minecraft:beehive" : string;
    }
}

