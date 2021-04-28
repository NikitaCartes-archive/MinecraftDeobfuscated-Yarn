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
    private static final ModelPredicateProvider DAMAGED_PROVIDER = (itemStack, clientWorld, livingEntity, i) -> itemStack.isDamaged() ? 1.0f : 0.0f;
    private static final ModelPredicateProvider DAMAGE_PROVIDER = (itemStack, clientWorld, livingEntity, i) -> MathHelper.clamp((float)itemStack.getDamage() / (float)itemStack.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();

    private static ModelPredicateProvider register(Identifier id, ModelPredicateProvider provider) {
        GLOBAL.put(id, provider);
        return provider;
    }

    private static void register(Item item2, Identifier id, ModelPredicateProvider provider) {
        ITEM_SPECIFIC.computeIfAbsent(item2, item -> Maps.newHashMap()).put(id, provider);
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
        ModelPredicateProviderRegistry.register(new Identifier("lefthanded"), (itemStack, clientWorld, livingEntity, i) -> livingEntity == null || livingEntity.getMainArm() == Arm.RIGHT ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(new Identifier("cooldown"), (itemStack, clientWorld, livingEntity, i) -> livingEntity instanceof PlayerEntity ? ((PlayerEntity)livingEntity).getItemCooldownManager().getCooldownProgress(itemStack.getItem(), 0.0f) : 0.0f);
        ModelPredicateProviderRegistry.register(new Identifier("custom_model_data"), (itemStack, clientWorld, livingEntity, i) -> itemStack.hasTag() ? (float)itemStack.getTag().getInt(CUSTOM_MODEL_DATA_KEY) : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pull"), (itemStack, clientWorld, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            if (livingEntity.getActiveItem() != itemStack) {
                return 0.0f;
            }
            return (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pulling"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BUNDLE, new Identifier("filled"), (itemStack, clientWorld, livingEntity, i) -> BundleItem.getAmountFilled(itemStack));
        ModelPredicateProviderRegistry.register(Items.CLOCK, new Identifier("time"), new ModelPredicateProvider(){
            private double time;
            private double step;
            private long lastTick;

            @Override
            public float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
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
                double d = clientWorld.getDimension().isNatural() ? (double)clientWorld.getSkyAngle(1.0f) : Math.random();
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
        ModelPredicateProviderRegistry.register(Items.COMPASS, new Identifier("angle"), new ModelPredicateProvider(){
            private final AngleInterpolator value = new AngleInterpolator();
            private final AngleInterpolator speed = new AngleInterpolator();

            @Override
            public float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
                double g;
                Entity entity;
                Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getHolder();
                if (entity == null) {
                    return 0.0f;
                }
                if (clientWorld == null && entity.world instanceof ClientWorld) {
                    clientWorld = (ClientWorld)entity.world;
                }
                BlockPos blockPos = CompassItem.hasLodestone(itemStack) ? this.getLodestonePos(clientWorld, itemStack.getOrCreateTag()) : this.getSpawnPos(clientWorld);
                long l = clientWorld.getTime();
                if (blockPos == null || entity.getPos().squaredDistanceTo((double)blockPos.getX() + 0.5, entity.getPos().getY(), (double)blockPos.getZ() + 0.5) < (double)1.0E-5f) {
                    if (this.speed.shouldUpdate(l)) {
                        this.speed.update(l, Math.random());
                    }
                    double d = this.speed.value + (double)((float)this.method_32800(i) / 2.14748365E9f);
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
                    if (this.value.shouldUpdate(l)) {
                        this.value.update(l, 0.5 - (e - 0.25));
                    }
                    g = f + this.value.value;
                } else {
                    g = 0.5 - (e - 0.25 - f);
                }
                return MathHelper.floorMod((float)g, 1.0f);
            }

            private int method_32800(int i) {
                return i * 1327217883;
            }

            @Nullable
            private BlockPos getSpawnPos(ClientWorld world) {
                return world.getDimension().isNatural() ? world.getSpawnPos() : null;
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
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pull"), (itemStack, clientWorld, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(itemStack)) {
                return 0.0f;
            }
            return (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(itemStack);
        });
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pulling"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("charged"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("firework"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.hasProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.ELYTRA, new Identifier("broken"), (itemStack, clientWorld, livingEntity, i) -> ElytraItem.isUsable(itemStack) ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(Items.FISHING_ROD, new Identifier("cast"), (itemStack, clientWorld, livingEntity, i) -> {
            boolean bl2;
            if (livingEntity == null) {
                return 0.0f;
            }
            boolean bl = livingEntity.getMainHandStack() == itemStack;
            boolean bl3 = bl2 = livingEntity.getOffHandStack() == itemStack;
            if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
                bl2 = false;
            }
            return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0f : 0.0f;
        });
        ModelPredicateProviderRegistry.register(Items.SHIELD, new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.TRIDENT, new Identifier("throwing"), (itemStack, clientWorld, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.LIGHT, new Identifier("level"), (itemStack, clientWorld, livingEntity, i) -> {
            NbtCompound nbtCompound = itemStack.getSubTag("BlockStateTag");
            try {
                if (nbtCompound != null) {
                    return Integer.parseInt(nbtCompound.getString(LightBlock.LEVEL_15.getName()));
                }
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return 15.0f;
        });
    }

    @Environment(value=EnvType.CLIENT)
    static class AngleInterpolator {
        private double value;
        private double speed;
        private long lastUpdateTime;

        private AngleInterpolator() {
        }

        private boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        private void update(long time, double d) {
            this.lastUpdateTime = time;
            double e = d - this.value;
            e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
            this.speed += e * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
        }
    }
}

