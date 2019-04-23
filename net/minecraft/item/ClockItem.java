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
        this.addProperty(new Identifier("time"), new ItemPropertyGetter(){
            @Environment(value=EnvType.CLIENT)
            private double lastClockTime;
            @Environment(value=EnvType.CLIENT)
            private double clockTimeChangeSpeed;
            @Environment(value=EnvType.CLIENT)
            private long lastWorldTime;

            @Override
            @Environment(value=EnvType.CLIENT)
            public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
                Entity entity;
                boolean bl = livingEntity != null;
                Entity entity2 = entity = bl ? livingEntity : itemStack.getHoldingItemFrame();
                if (world == null && entity != null) {
                    world = entity.world;
                }
                if (world == null) {
                    return 0.0f;
                }
                double d = world.dimension.hasVisibleSky() ? (double)world.getSkyAngle(1.0f) : Math.random();
                d = this.getClockTime(world, d);
                return (float)d;
            }

            @Environment(value=EnvType.CLIENT)
            private double getClockTime(World world, double d) {
                if (world.getTime() != this.lastWorldTime) {
                    this.lastWorldTime = world.getTime();
                    double e = d - this.lastClockTime;
                    e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
                    this.clockTimeChangeSpeed += e * 0.1;
                    this.clockTimeChangeSpeed *= 0.9;
                    this.lastClockTime = MathHelper.floorMod(this.lastClockTime + this.clockTimeChangeSpeed, 1.0);
                }
                return this.lastClockTime;
            }
        });
    }
}

