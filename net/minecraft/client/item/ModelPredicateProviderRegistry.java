/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LightBlock;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.CompassItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelPredicateProviderRegistry {
    private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.newHashMap();
    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";
    private static final Identifier DAMAGED_ID = new Identifier("damaged");
    private static final Identifier DAMAGE_ID = new Identifier("damage");
    private static final ClampedModelPredicateProvider DAMAGED_PROVIDER = (stack, world, entity, seed) -> stack.isDamaged() ? 1.0f : 0.0f;
    private static final ClampedModelPredicateProvider DAMAGE_PROVIDER = (stack, world, entity, seed) -> MathHelper.clamp((float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();

    private static ClampedModelPredicateProvider register(Identifier id, ClampedModelPredicateProvider provider) {
        GLOBAL.put(id, provider);
        return provider;
    }

    private static void registerCustomModelData(ModelPredicateProvider provider) {
        GLOBAL.put(new Identifier("custom_model_data"), provider);
    }

    private static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
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
        ClampedModelPredicateProvider clampedModelPredicateProvider = (stack, world, entity, seed) -> {
            if (!stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
                return Float.NEGATIVE_INFINITY;
            }
            if (world == null) {
                return 0.0f;
            }
            if (!world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
                return Float.NEGATIVE_INFINITY;
            }
            return ArmorTrim.getTrim(world.getRegistryManager(), stack).map(ArmorTrim::getMaterial).map(RegistryEntry::value).map(ArmorTrimMaterial::itemModelIndex).orElse(Float.valueOf(0.0f)).floatValue();
        };
        ModelPredicateProviderRegistry.register(ItemModelGenerator.TRIM_TYPE, clampedModelPredicateProvider);
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
        ModelPredicateProviderRegistry.register(Items.BRUSH, new Identifier("brushing"), (stack, world, entity, seed) -> {
            if (entity == null || entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float)(entity.getItemUseTimeLeft() % 10) / 10.0f;
        });
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
        ModelPredicateProviderRegistry.register(Items.CLOCK, new Identifier("time"), new ClampedModelPredicateProvider(){
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
        ModelPredicateProviderRegistry.register(Items.COMPASS, new Identifier("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
            if (CompassItem.hasLodestone(stack)) {
                return CompassItem.createLodestonePos(stack.getOrCreateNbt());
            }
            return CompassItem.createSpawnPos(world);
        }));
        ModelPredicateProviderRegistry.register(Items.RECOVERY_COMPASS, new Identifier("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)entity;
                return playerEntity.getLastDeathPos().orElse(null);
            }
            return null;
        }));
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
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("charged"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("firework"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
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
        ModelPredicateProviderRegistry.register(Items.GOAT_HORN, new Identifier("tooting"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}

