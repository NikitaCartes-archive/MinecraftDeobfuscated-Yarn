/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CompassItem
extends Item {
    public CompassItem(Item.Settings settings) {
        super(settings);
        this.addProperty(new Identifier("angle"), new ItemPropertyGetter(){
            @Environment(value=EnvType.CLIENT)
            private double field_7907;
            @Environment(value=EnvType.CLIENT)
            private double field_7906;
            @Environment(value=EnvType.CLIENT)
            private long field_7908;

            @Override
            @Environment(value=EnvType.CLIENT)
            public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
                double f;
                Entity entity;
                if (livingEntity == null && !itemStack.isHeldInItemFrame()) {
                    return 0.0f;
                }
                boolean bl = livingEntity != null;
                Entity entity2 = entity = bl ? livingEntity : itemStack.getHoldingItemFrame();
                if (world == null) {
                    world = entity.world;
                }
                if (world.dimension.hasVisibleSky()) {
                    double d = bl ? (double)entity.yaw : this.method_7733((ItemFrameEntity)entity);
                    d = MathHelper.floorMod(d / 360.0, 1.0);
                    double e = this.method_7734(world, entity) / 6.2831854820251465;
                    f = 0.5 - (d - 0.25 - e);
                } else {
                    f = Math.random();
                }
                if (bl) {
                    f = this.method_7735(world, f);
                }
                return MathHelper.floorMod((float)f, 1.0f);
            }

            @Environment(value=EnvType.CLIENT)
            private double method_7735(World world, double d) {
                if (world.getTime() != this.field_7908) {
                    this.field_7908 = world.getTime();
                    double e = d - this.field_7907;
                    e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
                    this.field_7906 += e * 0.1;
                    this.field_7906 *= 0.8;
                    this.field_7907 = MathHelper.floorMod(this.field_7907 + this.field_7906, 1.0);
                }
                return this.field_7907;
            }

            @Environment(value=EnvType.CLIENT)
            private double method_7733(ItemFrameEntity itemFrameEntity) {
                return MathHelper.wrapDegrees(180 + itemFrameEntity.facing.getHorizontal() * 90);
            }

            @Environment(value=EnvType.CLIENT)
            private double method_7734(IWorld iWorld, Entity entity) {
                BlockPos blockPos = iWorld.getSpawnPos();
                return Math.atan2((double)blockPos.getZ() - entity.z, (double)blockPos.getX() - entity.x);
            }
        });
    }
}

