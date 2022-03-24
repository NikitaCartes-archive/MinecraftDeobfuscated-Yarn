/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LightBlock;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.CompassItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelPredicateProviderRegistry {
    private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.newHashMap();
    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";
    private static final Identifier DAMAGED_ID = new Identifier("damaged");
    private static final Identifier DAMAGE_ID = new Identifier("damage");
    private static final UnclampedModelPredicateProvider DAMAGED_PROVIDER = (stack, world, entity, seed) -> stack.isDamaged() ? 1.0f : 0.0f;
    private static final UnclampedModelPredicateProvider DAMAGE_PROVIDER = (stack, world, entity, seed) -> MathHelper.clamp((float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();

    private static UnclampedModelPredicateProvider register(Identifier id, UnclampedModelPredicateProvider provider) {
        GLOBAL.put(id, provider);
        return provider;
    }

    private static void registerCustomModelData(ModelPredicateProvider provider) {
        GLOBAL.put(new Identifier("custom_model_data"), provider);
    }

    private static void register(Item item, Identifier id, UnclampedModelPredicateProvider provider) {
        ITEM_SPECIFIC.computeIfAbsent(item, key -> Maps.newHashMap()).put(id, provider);
    }

    @Nullable
    public static ModelPredicateProvider get(Item item, Identifier id) {
        ModelPredicateProvider modelPredicateProvider;
        if (item.getMaxDamage() > 0) {
            if (DAMAGE_ID.equals(id)) {
                return DAMAGE_PROVIDER;
            }
            if (DAMAGED_ID.equals(id)) {
                return DAMAGED_PROVIDER;
            }
        }
        if ((modelPredicateProvider = GLOBAL.get(id)) != null) {
            return modelPredicateProvider;
        }
        Map<Identifier, ModelPredicateProvider> map = ITEM_SPECIFIC.get(item);
        if (map == null) {
            return null;
        }
        return map.get(id);
    }

    static {
        ModelPredicateProviderRegistry.register(new Identifier("lefthanded"), (stack, world, entity, seed) -> entity == null || entity.getMainArm() == Arm.RIGHT ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(new Identifier("cooldown"), (stack, world, entity, seed) -> entity instanceof PlayerEntity ? ((PlayerEntity)entity).getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0f) : 0.0f);
        ModelPredicateProviderRegistry.registerCustomModelData((stack, world, entity, seed) -> stack.hasNbt() ? (float)stack.getNbt().getInt(CUSTOM_MODEL_DATA_KEY) : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
        ModelPredicateProviderRegistry.register(Items.CLOCK, new Identifier("time"), new UnclampedModelPredicateProvider(){
            private double time;
            private double step;
            private long lastTick;

            @Override
            public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
                Entity entity;
                Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getHolder();
                if (entity == null) {
                    return 0.0f;
                }
                if (clientWorld == null && entity.world instanceof ClientWorld) {
                    clientWorld = (ClientWorld)entity.world;
                }
                if (clientWorld == null) {
                    return 0.0f;
                }
                double d = clientWorld.getDimension().natural() ? (double)clientWorld.getSkyAngle(1.0f) : Math.random();
                d = this.getTime(clientWorld, d);
                return (float)d;
            }

            private double getTime(World world, double skyAngle) {
                if (world.getTime() != this.lastTick) {
                    this.lastTick = world.getTime();
                    double d = skyAngle - this.time;
                    d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
                    this.step += d * 0.1;
                    this.step *= 0.9;
                    this.time = MathHelper.floorMod(this.time + this.step, 1.0);
                }
                return this.time;
            }
        });
        ModelPredicateProviderRegistry.register(Items.COMPASS, new Identifier("angle"), new UnclampedModelPredicateProvider(){
            private final AngleInterpolator aimedInterpolator = new AngleInterpolator();
            private final AngleInterpolator aimlessInterpolator = new AngleInterpolator();

            @Override
            public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
                double g;
                Entity entity;
                Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getHolder();
                if (entity == null) {
                    return 0.0f;
                }
                if (clientWorld == null && entity.world instanceof ClientWorld) {
                    clientWorld = (ClientWorld)entity.world;
                }
                BlockPos blockPos = CompassItem.hasLodestone(itemStack) ? this.getLodestonePos(clientWorld, itemStack.getOrCreateNbt()) : this.getSpawnPos(clientWorld);
                long l = clientWorld.getTime();
                if (blockPos == null || entity.getPos().squaredDistanceTo((double)blockPos.getX() + 0.5, entity.getPos().getY(), (double)blockPos.getZ() + 0.5) < (double)1.0E-5f) {
                    if (this.aimlessInterpolator.shouldUpdate(l)) {
                        this.aimlessInterpolator.update(l, Math.random());
                    }
                    double d = this.aimlessInterpolator.value + (double)((float)this.scatter(i) / 2.14748365E9f);
                    return MathHelper.floorMod((float)d, 1.0f);
                }
                boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).isMainPlayer();
                double e = 0.0;
                if (bl) {
                    e = livingEntity.getYaw();
                } else if (entity instanceof ItemFrameEntity) {
                    e = this.getItemFrameAngleOffset((ItemFrameEntity)entity);
                } else if (entity instanceof ItemEntity) {
                    e = 180.0f - ((ItemEntity)entity).getRotation(0.5f) / ((float)Math.PI * 2) * 360.0f;
                } else if (livingEntity != null) {
                    e = livingEntity.bodyYaw;
                }
                e = MathHelper.floorMod(e / 360.0, 1.0);
                double f = this.getAngleToPos(Vec3d.ofCenter(blockPos), entity) / 6.2831854820251465;
                if (bl) {
                    if (this.aimedInterpolator.shouldUpdate(l)) {
                        this.aimedInterpolator.update(l, 0.5 - (e - 0.25));
                    }
                    g = f + this.aimedInterpolator.value;
                } else {
                    g = 0.5 - (e - 0.25 - f);
                }
                return MathHelper.floorMod((float)g, 1.0f);
            }

            /**
             * Scatters a seed by integer overflow in multiplication onto the whole
             * int domain.
             */
            private int scatter(int seed) {
                return seed * 1327217883;
            }

            @Nullable
            private BlockPos getSpawnPos(ClientWorld world) {
                return world.getDimension().natural() ? world.getSpawnPos() : null;
            }

            @Nullable
            private BlockPos getLodestonePos(World world, NbtCompound nbt) {
                Optional<RegistryKey<World>> optional;
                boolean bl = nbt.contains("LodestonePos");
                boolean bl2 = nbt.contains("LodestoneDimension");
                if (bl && bl2 && (optional = CompassItem.getLodestoneDimension(nbt)).isPresent() && world.getRegistryKey() == optional.get()) {
                    return NbtHelper.toBlockPos(nbt.getCompound("LodestonePos"));
                }
                return null;
            }

            private double getItemFrameAngleOffset(ItemFrameEntity itemFrame) {
                Direction direction = itemFrame.getHorizontalFacing();
                int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
                return MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + itemFrame.getRotation() * 45 + i);
            }

            private double getAngleToPos(Vec3d pos, Entity entity) {
                return Math.atan2(pos.getZ() - entity.getZ(), pos.getX() - entity.getX());
            }
        });
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(stack)) {
                return 0.0f;
            }
            return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack);
        });
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("charged"), (stack, world, entity, seed) -> entity != null && CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("firework"), (stack, world, entity, seed) -> entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.ELYTRA, new Identifier("broken"), (stack, world, entity, seed) -> ElytraItem.isUsable(stack) ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(Items.FISHING_ROD, new Identifier("cast"), (stack, world, entity, seed) -> {
            boolean bl2;
            if (entity == null) {
                return 0.0f;
            }
            boolean bl = entity.getMainHandStack() == stack;
            boolean bl3 = bl2 = entity.getOffHandStack() == stack;
            if (entity.getMainHandStack().getItem() instanceof FishingRodItem) {
                bl2 = false;
            }
            return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null ? 1.0f : 0.0f;
        });
        ModelPredicateProviderRegistry.register(Items.SHIELD, new Identifier("blocking"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.TRIDENT, new Identifier("throwing"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.LIGHT, new Identifier("level"), (stack, world, entity, seed) -> {
            NbtCompound nbtCompound = stack.getSubNbt("BlockStateTag");
            try {
                NbtElement nbtElement;
                if (nbtCompound != null && (nbtElement = nbtCompound.get(LightBlock.LEVEL_15.getName())) != null) {
                    return (float)Integer.parseInt(nbtElement.asString()) / 16.0f;
                }
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return 1.0f;
        });
    }

    @Environment(value=EnvType.CLIENT)
    static class AngleInterpolator {
        double value;
        private double speed;
        private long lastUpdateTime;

        AngleInterpolator() {
        }

        boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        void update(long time, double target) {
            this.lastUpdateTime = time;
            double d = target - this.value;
            d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
            this.speed += d * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
        }
    }
}

