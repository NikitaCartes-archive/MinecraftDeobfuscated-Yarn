/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class CompassItem
extends Item {
    public CompassItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("angle"), new ItemPropertyGetter(){
            @Environment(value=EnvType.CLIENT)
            private double angle;
            @Environment(value=EnvType.CLIENT)
            private double step;
            @Environment(value=EnvType.CLIENT)
            private long lastTick;

            @Override
            @Environment(value=EnvType.CLIENT)
            public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
                double f;
                CompoundTag compoundTag;
                boolean bl2;
                BlockPos blockPos;
                Entity entity;
                if (livingEntity == null && !itemStack.isInFrame()) {
                    return 0.0f;
                }
                boolean bl = livingEntity != null;
                Entity entity2 = entity = bl ? livingEntity : itemStack.getFrame();
                if (world == null) {
                    world = entity.world;
                }
                BlockPos blockPos2 = blockPos = (bl2 = CompassItem.method_26363(compoundTag = itemStack.getOrCreateTag())) ? CompassItem.this.method_26358(world, compoundTag) : CompassItem.this.method_26357(world);
                if (blockPos != null) {
                    double d = bl ? (double)entity.yaw : CompassItem.method_26361((ItemFrameEntity)entity);
                    d = MathHelper.floorMod(d / 360.0, 1.0);
                    double e = CompassItem.method_26362(Vec3d.method_24953(blockPos), entity) / 6.2831854820251465;
                    f = 0.5 - (d - 0.25 - e);
                } else {
                    f = Math.random();
                }
                if (bl && !bl2) {
                    f = this.getAngle(world, f);
                }
                return MathHelper.floorMod((float)f, 1.0f);
            }

            @Environment(value=EnvType.CLIENT)
            private double getAngle(World world, double entityYaw) {
                if (world.getTime() != this.lastTick) {
                    this.lastTick = world.getTime();
                    double d = entityYaw - this.angle;
                    d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
                    this.step += d * 0.1;
                    this.step *= 0.8;
                    this.angle = MathHelper.floorMod(this.angle + this.step, 1.0);
                }
                return this.angle;
            }
        });
    }

    private static boolean method_26363(CompoundTag compoundTag) {
        return compoundTag.contains("LodestoneDimension") || compoundTag.contains("LodestonePos");
    }

    private static boolean method_26365(ItemStack itemStack) {
        return CompassItem.method_26363(itemStack.getOrCreateTag());
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return CompassItem.method_26365(stack);
    }

    private static Optional<DimensionType> method_26364(CompoundTag compoundTag) {
        Identifier identifier = Identifier.tryParse(compoundTag.getString("LodestoneDimension"));
        if (identifier != null) {
            return Registry.DIMENSION_TYPE.getOrEmpty(identifier);
        }
        return Optional.empty();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private BlockPos method_26357(World world) {
        return world.dimension.hasVisibleSky() ? world.getSpawnPos() : null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private BlockPos method_26358(World world, CompoundTag compoundTag) {
        Optional<DimensionType> optional;
        boolean bl = compoundTag.contains("LodestonePos");
        boolean bl2 = compoundTag.contains("LodestonePos");
        if (bl && bl2 && (optional = CompassItem.method_26364(compoundTag)).isPresent() && world.dimension.getType().equals(optional.get())) {
            return NbtHelper.toBlockPos((CompoundTag)compoundTag.get("LodestonePos"));
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    private static double method_26361(ItemFrameEntity itemFrameEntity) {
        return MathHelper.wrapDegrees(180 + itemFrameEntity.getHorizontalFacing().getHorizontal() * 90);
    }

    @Environment(value=EnvType.CLIENT)
    private static double method_26362(Vec3d vec3d, Entity entity) {
        return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        Optional<DimensionType> optional;
        if (world.isClient) {
            return;
        }
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (CompassItem.method_26363(compoundTag) && (optional = CompassItem.method_26364(compoundTag)).isPresent() && optional.get().equals(world.dimension.getType()) && compoundTag.contains("LodestonePos") && !((ServerWorld)world).getPointOfInterestStorage().method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos((CompoundTag)compoundTag.get("LodestonePos")))) {
            compoundTag.remove("LodestonePos");
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.hit.getBlockPos();
        if (context.world.getBlockState(blockPos).getBlock() == Blocks.LODESTONE) {
            context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            CompoundTag compoundTag = context.stack.getOrCreateTag();
            compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
            compoundTag.putString("LodestoneDimension", DimensionType.getId(context.world.dimension.getType()).toString());
        }
        return super.useOnBlock(context);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return CompassItem.method_26365(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
    }
}

