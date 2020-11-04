/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.AreaEffectCloudEntityRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.CaveSpiderEntityRenderer;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.CodEntityRenderer;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.DolphinEntityRenderer;
import net.minecraft.client.render.entity.DonkeyEntityRenderer;
import net.minecraft.client.render.entity.DragonFireballEntityRenderer;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EndermiteEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.render.entity.EvokerFangsEntityRenderer;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.FireworkEntityRenderer;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.render.entity.GiantEntityRenderer;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.HoglinEntityRenderer;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.render.entity.HuskEntityRenderer;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.entity.LeashKnotEntityRenderer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.LlamaSpitEntityRenderer;
import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.MooshroomEntityRenderer;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.PandaEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PolarBearEntityRenderer;
import net.minecraft.client.render.entity.PufferfishEntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.render.entity.RavagerEntityRenderer;
import net.minecraft.client.render.entity.SalmonEntityRenderer;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.SilverfishEntityRenderer;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.SnowGolemEntityRenderer;
import net.minecraft.client.render.entity.SpectralArrowEntityRenderer;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.StrayEntityRenderer;
import net.minecraft.client.render.entity.StriderEntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.client.render.entity.VexEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.VindicatorEntityRenderer;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.client.render.entity.WitchEntityRenderer;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.WitherSkeletonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.ZoglinEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.ZombieHorseEntityRenderer;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EntityRenderers {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<EntityType<?>, class_5617<?>> field_27768 = Maps.newHashMap();
    private static final Map<String, class_5617<AbstractClientPlayerEntity>> field_27769 = ImmutableMap.of("default", arg -> new PlayerEntityRenderer(arg, false), "slim", arg -> new PlayerEntityRenderer(arg, true));

    private static <T extends Entity> void register(EntityType<? extends T> type, class_5617<T> arg) {
        field_27768.put(type, arg);
    }

    public static Map<EntityType<?>, EntityRenderer<?>> method_32176(class_5617.class_5618 arg) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        field_27768.forEach((entityType, arg2) -> {
            try {
                builder.put(entityType, arg2.create(arg));
            } catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create model for " + Registry.ENTITY_TYPE.getId((EntityType<?>)entityType), exception);
            }
        });
        return builder.build();
    }

    public static Map<String, EntityRenderer<? extends PlayerEntity>> method_32177(class_5617.class_5618 arg) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        field_27769.forEach((string, arg2) -> {
            try {
                builder.put(string, arg2.create(arg));
            } catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create player model for " + string, exception);
            }
        });
        return builder.build();
    }

    public static boolean method_32172() {
        boolean bl = true;
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || field_27768.containsKey(entityType)) continue;
            LOGGER.warn("No renderer registered for {}", (Object)Registry.ENTITY_TYPE.getId(entityType));
            bl = false;
        }
        return !bl;
    }

    static {
        EntityRenderers.register(EntityType.AREA_EFFECT_CLOUD, AreaEffectCloudEntityRenderer::new);
        EntityRenderers.register(EntityType.ARMOR_STAND, ArmorStandEntityRenderer::new);
        EntityRenderers.register(EntityType.ARROW, ArrowEntityRenderer::new);
        EntityRenderers.register(EntityType.BAT, BatEntityRenderer::new);
        EntityRenderers.register(EntityType.BEE, BeeEntityRenderer::new);
        EntityRenderers.register(EntityType.BLAZE, BlazeEntityRenderer::new);
        EntityRenderers.register(EntityType.BOAT, BoatEntityRenderer::new);
        EntityRenderers.register(EntityType.CAT, CatEntityRenderer::new);
        EntityRenderers.register(EntityType.CAVE_SPIDER, CaveSpiderEntityRenderer::new);
        EntityRenderers.register(EntityType.CHEST_MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.CHEST_MINECART));
        EntityRenderers.register(EntityType.CHICKEN, ChickenEntityRenderer::new);
        EntityRenderers.register(EntityType.COD, CodEntityRenderer::new);
        EntityRenderers.register(EntityType.COMMAND_BLOCK_MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.COMMAND_BLOCK_MINECART));
        EntityRenderers.register(EntityType.COW, CowEntityRenderer::new);
        EntityRenderers.register(EntityType.CREEPER, CreeperEntityRenderer::new);
        EntityRenderers.register(EntityType.DOLPHIN, DolphinEntityRenderer::new);
        EntityRenderers.register(EntityType.DONKEY, arg -> new DonkeyEntityRenderer(arg, 0.87f, EntityModelLayers.DONKEY));
        EntityRenderers.register(EntityType.DRAGON_FIREBALL, DragonFireballEntityRenderer::new);
        EntityRenderers.register(EntityType.DROWNED, DrownedEntityRenderer::new);
        EntityRenderers.register(EntityType.EGG, FlyingItemEntityRenderer::new);
        EntityRenderers.register(EntityType.ELDER_GUARDIAN, ElderGuardianEntityRenderer::new);
        EntityRenderers.register(EntityType.ENDERMAN, EndermanEntityRenderer::new);
        EntityRenderers.register(EntityType.ENDERMITE, EndermiteEntityRenderer::new);
        EntityRenderers.register(EntityType.ENDER_DRAGON, EnderDragonEntityRenderer::new);
        EntityRenderers.register(EntityType.ENDER_PEARL, FlyingItemEntityRenderer::new);
        EntityRenderers.register(EntityType.END_CRYSTAL, EndCrystalEntityRenderer::new);
        EntityRenderers.register(EntityType.EVOKER, EvokerEntityRenderer::new);
        EntityRenderers.register(EntityType.EVOKER_FANGS, EvokerFangsEntityRenderer::new);
        EntityRenderers.register(EntityType.EXPERIENCE_BOTTLE, FlyingItemEntityRenderer::new);
        EntityRenderers.register(EntityType.EXPERIENCE_ORB, ExperienceOrbEntityRenderer::new);
        EntityRenderers.register(EntityType.EYE_OF_ENDER, arg -> new FlyingItemEntityRenderer(arg, 1.0f, true));
        EntityRenderers.register(EntityType.FALLING_BLOCK, FallingBlockEntityRenderer::new);
        EntityRenderers.register(EntityType.FIREBALL, arg -> new FlyingItemEntityRenderer(arg, 3.0f, true));
        EntityRenderers.register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
        EntityRenderers.register(EntityType.FISHING_BOBBER, FishingBobberEntityRenderer::new);
        EntityRenderers.register(EntityType.FOX, FoxEntityRenderer::new);
        EntityRenderers.register(EntityType.FURNACE_MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.FURNACE_MINECART));
        EntityRenderers.register(EntityType.GHAST, GhastEntityRenderer::new);
        EntityRenderers.register(EntityType.GIANT, arg -> new GiantEntityRenderer(arg, 6.0f));
        EntityRenderers.register(EntityType.GUARDIAN, GuardianEntityRenderer::new);
        EntityRenderers.register(EntityType.HOGLIN, HoglinEntityRenderer::new);
        EntityRenderers.register(EntityType.HOPPER_MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.HOPPER_MINECART));
        EntityRenderers.register(EntityType.HORSE, HorseEntityRenderer::new);
        EntityRenderers.register(EntityType.HUSK, HuskEntityRenderer::new);
        EntityRenderers.register(EntityType.ILLUSIONER, IllusionerEntityRenderer::new);
        EntityRenderers.register(EntityType.IRON_GOLEM, IronGolemEntityRenderer::new);
        EntityRenderers.register(EntityType.ITEM, ItemEntityRenderer::new);
        EntityRenderers.register(EntityType.ITEM_FRAME, ItemFrameEntityRenderer::new);
        EntityRenderers.register(EntityType.LEASH_KNOT, LeashKnotEntityRenderer::new);
        EntityRenderers.register(EntityType.LIGHTNING_BOLT, LightningEntityRenderer::new);
        EntityRenderers.register(EntityType.LLAMA, arg -> new LlamaEntityRenderer(arg, EntityModelLayers.LLAMA));
        EntityRenderers.register(EntityType.LLAMA_SPIT, LlamaSpitEntityRenderer::new);
        EntityRenderers.register(EntityType.MAGMA_CUBE, MagmaCubeEntityRenderer::new);
        EntityRenderers.register(EntityType.MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.MINECART));
        EntityRenderers.register(EntityType.MOOSHROOM, MooshroomEntityRenderer::new);
        EntityRenderers.register(EntityType.MULE, arg -> new DonkeyEntityRenderer(arg, 0.92f, EntityModelLayers.MULE));
        EntityRenderers.register(EntityType.OCELOT, OcelotEntityRenderer::new);
        EntityRenderers.register(EntityType.PAINTING, PaintingEntityRenderer::new);
        EntityRenderers.register(EntityType.PANDA, PandaEntityRenderer::new);
        EntityRenderers.register(EntityType.PARROT, ParrotEntityRenderer::new);
        EntityRenderers.register(EntityType.PHANTOM, PhantomEntityRenderer::new);
        EntityRenderers.register(EntityType.PIG, PigEntityRenderer::new);
        EntityRenderers.register(EntityType.PIGLIN, arg -> new PiglinEntityRenderer(arg, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_INNER_ARMOR, EntityModelLayers.PIGLIN_OUTER_ARMOR, false));
        EntityRenderers.register(EntityType.PIGLIN_BRUTE, arg -> new PiglinEntityRenderer(arg, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE_INNER_ARMOR, EntityModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, false));
        EntityRenderers.register(EntityType.PILLAGER, PillagerEntityRenderer::new);
        EntityRenderers.register(EntityType.POLAR_BEAR, PolarBearEntityRenderer::new);
        EntityRenderers.register(EntityType.POTION, FlyingItemEntityRenderer::new);
        EntityRenderers.register(EntityType.PUFFERFISH, PufferfishEntityRenderer::new);
        EntityRenderers.register(EntityType.RABBIT, RabbitEntityRenderer::new);
        EntityRenderers.register(EntityType.RAVAGER, RavagerEntityRenderer::new);
        EntityRenderers.register(EntityType.SALMON, SalmonEntityRenderer::new);
        EntityRenderers.register(EntityType.SHEEP, SheepEntityRenderer::new);
        EntityRenderers.register(EntityType.SHULKER, ShulkerEntityRenderer::new);
        EntityRenderers.register(EntityType.SHULKER_BULLET, ShulkerBulletEntityRenderer::new);
        EntityRenderers.register(EntityType.SILVERFISH, SilverfishEntityRenderer::new);
        EntityRenderers.register(EntityType.SKELETON, SkeletonEntityRenderer::new);
        EntityRenderers.register(EntityType.SKELETON_HORSE, arg -> new ZombieHorseEntityRenderer(arg, EntityModelLayers.SKELETON_HORSE));
        EntityRenderers.register(EntityType.SLIME, SlimeEntityRenderer::new);
        EntityRenderers.register(EntityType.SMALL_FIREBALL, arg -> new FlyingItemEntityRenderer(arg, 0.75f, true));
        EntityRenderers.register(EntityType.SNOWBALL, FlyingItemEntityRenderer::new);
        EntityRenderers.register(EntityType.SNOW_GOLEM, SnowGolemEntityRenderer::new);
        EntityRenderers.register(EntityType.SPAWNER_MINECART, arg -> new MinecartEntityRenderer(arg, EntityModelLayers.SPAWNER_MINECART));
        EntityRenderers.register(EntityType.SPECTRAL_ARROW, SpectralArrowEntityRenderer::new);
        EntityRenderers.register(EntityType.SPIDER, SpiderEntityRenderer::new);
        EntityRenderers.register(EntityType.SQUID, SquidEntityRenderer::new);
        EntityRenderers.register(EntityType.STRAY, StrayEntityRenderer::new);
        EntityRenderers.register(EntityType.STRIDER, StriderEntityRenderer::new);
        EntityRenderers.register(EntityType.TNT, TntEntityRenderer::new);
        EntityRenderers.register(EntityType.TNT_MINECART, TntMinecartEntityRenderer::new);
        EntityRenderers.register(EntityType.TRADER_LLAMA, arg -> new LlamaEntityRenderer(arg, EntityModelLayers.TRADER_LLAMA));
        EntityRenderers.register(EntityType.TRIDENT, TridentEntityRenderer::new);
        EntityRenderers.register(EntityType.TROPICAL_FISH, TropicalFishEntityRenderer::new);
        EntityRenderers.register(EntityType.TURTLE, TurtleEntityRenderer::new);
        EntityRenderers.register(EntityType.VEX, VexEntityRenderer::new);
        EntityRenderers.register(EntityType.VILLAGER, VillagerEntityRenderer::new);
        EntityRenderers.register(EntityType.VINDICATOR, VindicatorEntityRenderer::new);
        EntityRenderers.register(EntityType.WANDERING_TRADER, WanderingTraderEntityRenderer::new);
        EntityRenderers.register(EntityType.WITCH, WitchEntityRenderer::new);
        EntityRenderers.register(EntityType.WITHER, WitherEntityRenderer::new);
        EntityRenderers.register(EntityType.WITHER_SKELETON, WitherSkeletonEntityRenderer::new);
        EntityRenderers.register(EntityType.WITHER_SKULL, WitherSkullEntityRenderer::new);
        EntityRenderers.register(EntityType.WOLF, WolfEntityRenderer::new);
        EntityRenderers.register(EntityType.ZOGLIN, ZoglinEntityRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIE, ZombieEntityRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIE_HORSE, arg -> new ZombieHorseEntityRenderer(arg, EntityModelLayers.ZOMBIE_HORSE));
        EntityRenderers.register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerEntityRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIFIED_PIGLIN, arg -> new PiglinEntityRenderer(arg, EntityModelLayers.ZOMBIFIED_PIGLIN, EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true));
    }
}

