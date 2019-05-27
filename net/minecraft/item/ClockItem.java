/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClockItem
extends Item {
    public ClockItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("time"), new ItemPropertyGetter(){
            @Environment(value=EnvType.CLIENT)
            private double time;
            @Environment(value=EnvType.CLIENT)
            private double step;
            @Environment(value=EnvType.CLIENT)
            private long lastTick;

            @Override
            @Environment(value=EnvType.CLIENT)
            public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
                Entity entity;
                boolean bl = livingEntity != null;
                Entity entity2 = entity = bl ? livingEntity : itemStack.getFrame();
                if (world == null && entity != null) {
                    world = entity.world;
                }
                if (world == null) {
                    return 0.0f;
                }
                double d = world.dimension.hasVisibleSky() ? (double)world.getSkyAngle(1.0f) : Math.random();
                d = this.getTime(world, d);
                return (float)d;
            }

            @Environment(value=EnvType.CLIENT)
            private double getTime(World world, double d) {
                if (world.getTime() != this.lastTick) {
                    this.lastTick = world.getTime();
                    double e = d - this.time;
                    e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
                    this.step += e * 0.1;
                    this.step *= 0.9;
                    this.time = MathHelper.floorMod(this.time + this.step, 1.0);
                }
                return this.time;
            }
        });
    }
}

