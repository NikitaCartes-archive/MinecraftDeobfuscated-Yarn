package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class EntityRenderers {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<EntityType<?>, EntityRendererFactory<?>> RENDERER_FACTORIES = new Object2ObjectOpenHashMap<>();
	private static final Map<SkinTextures.Model, EntityRendererFactory<AbstractClientPlayerEntity>> PLAYER_RENDERER_FACTORIES = Map.of(
		SkinTextures.Model.WIDE,
		(EntityRendererFactory<>)context -> new PlayerEntityRenderer(context, false),
		SkinTextures.Model.SLIM,
		(EntityRendererFactory<>)context -> new PlayerEntityRenderer(context, true)
	);

	private static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
		RENDERER_FACTORIES.put(type, factory);
	}

	public static Map<EntityType<?>, EntityRenderer<?>> reloadEntityRenderers(EntityRendererFactory.Context ctx) {
		Builder<EntityType<?>, EntityRenderer<?>> builder = ImmutableMap.builder();
		RENDERER_FACTORIES.forEach((entityType, factory) -> {
			try {
				builder.put(entityType, factory.create(ctx));
			} catch (Exception var5) {
				throw new IllegalArgumentException("Failed to create model for " + Registries.ENTITY_TYPE.getId(entityType), var5);
			}
		});
		return builder.build();
	}

	public static Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity>> reloadPlayerRenderers(EntityRendererFactory.Context ctx) {
		Builder<SkinTextures.Model, EntityRenderer<? extends PlayerEntity>> builder = ImmutableMap.builder();
		PLAYER_RENDERER_FACTORIES.forEach((model, factory) -> {
			try {
				builder.put(model, factory.create(ctx));
			} catch (Exception var5) {
				throw new IllegalArgumentException("Failed to create player model for " + model, var5);
			}
		});
		return builder.build();
	}

	public static boolean isMissingRendererFactories() {
		boolean bl = true;

		for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
			if (entityType != EntityType.PLAYER && !RENDERER_FACTORIES.containsKey(entityType)) {
				LOGGER.warn("No renderer registered for {}", Registries.ENTITY_TYPE.getId(entityType));
				bl = false;
			}
		}

		return !bl;
	}

	static {
		register(EntityType.ALLAY, AllayEntityRenderer::new);
		register(EntityType.AREA_EFFECT_CLOUD, EmptyEntityRenderer::new);
		register(EntityType.ARMADILLO, ArmadilloEntityRenderer::new);
		register(EntityType.ARMOR_STAND, ArmorStandEntityRenderer::new);
		register(EntityType.ARROW, ArrowEntityRenderer::new);
		register(EntityType.AXOLOTL, AxolotlEntityRenderer::new);
		register(EntityType.BAT, BatEntityRenderer::new);
		register(EntityType.BEE, BeeEntityRenderer::new);
		register(EntityType.BLAZE, BlazeEntityRenderer::new);
		register(EntityType.BLOCK_DISPLAY, DisplayEntityRenderer.BlockDisplayEntityRenderer::new);
		register(EntityType.BOAT, context -> new BoatEntityRenderer(context, false));
		register(EntityType.BOGGED, BoggedEntityRenderer::new);
		register(EntityType.BREEZE, BreezeEntityRenderer::new);
		register(EntityType.BREEZE_WIND_CHARGE, WindChargeEntityRenderer::new);
		register(EntityType.CAT, CatEntityRenderer::new);
		register(EntityType.CAMEL, context -> new CamelEntityRenderer(context, EntityModelLayers.CAMEL));
		register(EntityType.CAVE_SPIDER, CaveSpiderEntityRenderer::new);
		register(EntityType.CHEST_BOAT, context -> new BoatEntityRenderer(context, true));
		register(EntityType.CHEST_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.CHEST_MINECART));
		register(EntityType.CHICKEN, ChickenEntityRenderer::new);
		register(EntityType.COD, CodEntityRenderer::new);
		register(EntityType.COMMAND_BLOCK_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.COMMAND_BLOCK_MINECART));
		register(EntityType.COW, CowEntityRenderer::new);
		register(EntityType.CREEPER, CreeperEntityRenderer::new);
		register(EntityType.DOLPHIN, DolphinEntityRenderer::new);
		register(EntityType.DONKEY, context -> new DonkeyEntityRenderer<>(context, 0.87F, EntityModelLayers.DONKEY));
		register(EntityType.DRAGON_FIREBALL, DragonFireballEntityRenderer::new);
		register(EntityType.DROWNED, DrownedEntityRenderer::new);
		register(EntityType.EGG, FlyingItemEntityRenderer::new);
		register(EntityType.ELDER_GUARDIAN, ElderGuardianEntityRenderer::new);
		register(EntityType.ENDERMAN, EndermanEntityRenderer::new);
		register(EntityType.ENDERMITE, EndermiteEntityRenderer::new);
		register(EntityType.ENDER_DRAGON, EnderDragonEntityRenderer::new);
		register(EntityType.ENDER_PEARL, FlyingItemEntityRenderer::new);
		register(EntityType.END_CRYSTAL, EndCrystalEntityRenderer::new);
		register(EntityType.EVOKER, EvokerEntityRenderer::new);
		register(EntityType.EVOKER_FANGS, EvokerFangsEntityRenderer::new);
		register(EntityType.EXPERIENCE_BOTTLE, FlyingItemEntityRenderer::new);
		register(EntityType.EXPERIENCE_ORB, ExperienceOrbEntityRenderer::new);
		register(EntityType.EYE_OF_ENDER, context -> new FlyingItemEntityRenderer<>(context, 1.0F, true));
		register(EntityType.FALLING_BLOCK, FallingBlockEntityRenderer::new);
		register(EntityType.FIREBALL, context -> new FlyingItemEntityRenderer<>(context, 3.0F, true));
		register(EntityType.FIREWORK_ROCKET, FireworkRocketEntityRenderer::new);
		register(EntityType.FISHING_BOBBER, FishingBobberEntityRenderer::new);
		register(EntityType.FOX, FoxEntityRenderer::new);
		register(EntityType.FROG, FrogEntityRenderer::new);
		register(EntityType.FURNACE_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.FURNACE_MINECART));
		register(EntityType.GHAST, GhastEntityRenderer::new);
		register(EntityType.GIANT, context -> new GiantEntityRenderer(context, 6.0F));
		register(EntityType.GLOW_ITEM_FRAME, ItemFrameEntityRenderer::new);
		register(EntityType.GLOW_SQUID, context -> new GlowSquidEntityRenderer(context, new SquidEntityModel<>(context.getPart(EntityModelLayers.GLOW_SQUID))));
		register(EntityType.GOAT, GoatEntityRenderer::new);
		register(EntityType.GUARDIAN, GuardianEntityRenderer::new);
		register(EntityType.HOGLIN, HoglinEntityRenderer::new);
		register(EntityType.HOPPER_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.HOPPER_MINECART));
		register(EntityType.HORSE, HorseEntityRenderer::new);
		register(EntityType.HUSK, HuskEntityRenderer::new);
		register(EntityType.ILLUSIONER, IllusionerEntityRenderer::new);
		register(EntityType.INTERACTION, EmptyEntityRenderer::new);
		register(EntityType.IRON_GOLEM, IronGolemEntityRenderer::new);
		register(EntityType.ITEM, ItemEntityRenderer::new);
		register(EntityType.ITEM_DISPLAY, DisplayEntityRenderer.ItemDisplayEntityRenderer::new);
		register(EntityType.ITEM_FRAME, ItemFrameEntityRenderer::new);
		register(EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerEntityRenderer::new);
		register(EntityType.LEASH_KNOT, LeashKnotEntityRenderer::new);
		register(EntityType.LIGHTNING_BOLT, LightningEntityRenderer::new);
		register(EntityType.LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.LLAMA));
		register(EntityType.LLAMA_SPIT, LlamaSpitEntityRenderer::new);
		register(EntityType.MAGMA_CUBE, MagmaCubeEntityRenderer::new);
		register(EntityType.MARKER, EmptyEntityRenderer::new);
		register(EntityType.MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.MINECART));
		register(EntityType.MOOSHROOM, MooshroomEntityRenderer::new);
		register(EntityType.MULE, context -> new DonkeyEntityRenderer<>(context, 0.92F, EntityModelLayers.MULE));
		register(EntityType.OCELOT, OcelotEntityRenderer::new);
		register(EntityType.PAINTING, PaintingEntityRenderer::new);
		register(EntityType.PANDA, PandaEntityRenderer::new);
		register(EntityType.PARROT, ParrotEntityRenderer::new);
		register(EntityType.PHANTOM, PhantomEntityRenderer::new);
		register(EntityType.PIG, PigEntityRenderer::new);
		register(
			EntityType.PIGLIN,
			context -> new PiglinEntityRenderer(context, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_INNER_ARMOR, EntityModelLayers.PIGLIN_OUTER_ARMOR, false)
		);
		register(
			EntityType.PIGLIN_BRUTE,
			context -> new PiglinEntityRenderer(
					context, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE_INNER_ARMOR, EntityModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, false
				)
		);
		register(EntityType.PILLAGER, PillagerEntityRenderer::new);
		register(EntityType.POLAR_BEAR, PolarBearEntityRenderer::new);
		register(EntityType.POTION, FlyingItemEntityRenderer::new);
		register(EntityType.PUFFERFISH, PufferfishEntityRenderer::new);
		register(EntityType.RABBIT, RabbitEntityRenderer::new);
		register(EntityType.RAVAGER, RavagerEntityRenderer::new);
		register(EntityType.SALMON, SalmonEntityRenderer::new);
		register(EntityType.SHEEP, SheepEntityRenderer::new);
		register(EntityType.SHULKER, ShulkerEntityRenderer::new);
		register(EntityType.SHULKER_BULLET, ShulkerBulletEntityRenderer::new);
		register(EntityType.SILVERFISH, SilverfishEntityRenderer::new);
		register(EntityType.SKELETON, SkeletonEntityRenderer::new);
		register(EntityType.SKELETON_HORSE, context -> new ZombieHorseEntityRenderer(context, EntityModelLayers.SKELETON_HORSE));
		register(EntityType.SLIME, SlimeEntityRenderer::new);
		register(EntityType.SMALL_FIREBALL, context -> new FlyingItemEntityRenderer<>(context, 0.75F, true));
		register(EntityType.SNIFFER, SnifferEntityRenderer::new);
		register(EntityType.SNOWBALL, FlyingItemEntityRenderer::new);
		register(EntityType.SNOW_GOLEM, SnowGolemEntityRenderer::new);
		register(EntityType.SPAWNER_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.SPAWNER_MINECART));
		register(EntityType.SPECTRAL_ARROW, SpectralArrowEntityRenderer::new);
		register(EntityType.SPIDER, SpiderEntityRenderer::new);
		register(EntityType.SQUID, context -> new SquidEntityRenderer<>(context, new SquidEntityModel<>(context.getPart(EntityModelLayers.SQUID))));
		register(EntityType.STRAY, StrayEntityRenderer::new);
		register(EntityType.STRIDER, StriderEntityRenderer::new);
		register(EntityType.TADPOLE, TadpoleEntityRenderer::new);
		register(EntityType.TEXT_DISPLAY, DisplayEntityRenderer.TextDisplayEntityRenderer::new);
		register(EntityType.TNT, TntEntityRenderer::new);
		register(EntityType.TNT_MINECART, TntMinecartEntityRenderer::new);
		register(EntityType.TRADER_LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.TRADER_LLAMA));
		register(EntityType.TRIDENT, TridentEntityRenderer::new);
		register(EntityType.TROPICAL_FISH, TropicalFishEntityRenderer::new);
		register(EntityType.TURTLE, TurtleEntityRenderer::new);
		register(EntityType.VEX, VexEntityRenderer::new);
		register(EntityType.VILLAGER, VillagerEntityRenderer::new);
		register(EntityType.VINDICATOR, VindicatorEntityRenderer::new);
		register(EntityType.WARDEN, WardenEntityRenderer::new);
		register(EntityType.WANDERING_TRADER, WanderingTraderEntityRenderer::new);
		register(EntityType.WIND_CHARGE, WindChargeEntityRenderer::new);
		register(EntityType.WITCH, WitchEntityRenderer::new);
		register(EntityType.WITHER, WitherEntityRenderer::new);
		register(EntityType.WITHER_SKELETON, WitherSkeletonEntityRenderer::new);
		register(EntityType.WITHER_SKULL, WitherSkullEntityRenderer::new);
		register(EntityType.WOLF, WolfEntityRenderer::new);
		register(EntityType.ZOGLIN, ZoglinEntityRenderer::new);
		register(EntityType.ZOMBIE, ZombieEntityRenderer::new);
		register(EntityType.ZOMBIE_HORSE, context -> new ZombieHorseEntityRenderer(context, EntityModelLayers.ZOMBIE_HORSE));
		register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerEntityRenderer::new);
		register(
			EntityType.ZOMBIFIED_PIGLIN,
			context -> new PiglinEntityRenderer(
					context, EntityModelLayers.ZOMBIFIED_PIGLIN, EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true
				)
		);
	}
}
