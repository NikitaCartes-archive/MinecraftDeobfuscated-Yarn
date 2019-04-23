/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(value=EnvType.CLIENT)
public class Model {
    public final List<Cuboid> cuboidList = Lists.newArrayList();
    public int textureWidth = 64;
    public int textureHeight = 32;

    public Cuboid getRandomCuboid(Random random) {
        return this.cuboidList.get(random.nextInt(this.cuboidList.size()));
    }
}

