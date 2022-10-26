package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.SignType;

@Environment(EnvType.CLIENT)
public class EntityModels {
	private static final Dilation FISH_PATTERN_DILATION = new Dilation(0.008F);
	private static final Dilation ARMOR_DILATION = new Dilation(1.0F);
	private static final Dilation HAT_DILATION = new Dilation(0.5F);

	public static Map<EntityModelLayer, TexturedModelData> getModels() {
		Builder<EntityModelLayer, TexturedModelData> builder = ImmutableMap.builder();
		TexturedModelData texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0F), 64, 64);
		TexturedModelData texturedModelData2 = TexturedModelData.of(BipedEntityModel.getModelData(ARMOR_DILATION, 0.0F), 64, 32);
		TexturedModelData texturedModelData3 = TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(1.02F), 0.0F), 64, 32);
		TexturedModelData texturedModelData4 = TexturedModelData.of(BipedEntityModel.getModelData(HAT_DILATION, 0.0F), 64, 32);
		TexturedModelData texturedModelData5 = MinecartEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData6 = SkullEntityModel.getSkullTexturedModelData();
		TexturedModelData texturedModelData7 = TexturedModelData.of(HorseEntityModel.getModelData(Dilation.NONE), 64, 64);
		TexturedModelData texturedModelData8 = IllagerEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData9 = CowEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData10 = TexturedModelData.of(OcelotEntityModel.getModelData(Dilation.NONE), 64, 32);
		TexturedModelData texturedModelData11 = TexturedModelData.of(PiglinEntityModel.getModelData(Dilation.NONE), 64, 64);
		TexturedModelData texturedModelData12 = SkullEntityModel.getHeadTexturedModelData();
		TexturedModelData texturedModelData13 = LlamaEntityModel.getTexturedModelData(Dilation.NONE);
		TexturedModelData texturedModelData14 = StriderEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData15 = HoglinEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData16 = SkeletonEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData17 = TexturedModelData.of(VillagerResemblingModel.getModelData(), 64, 64);
		TexturedModelData texturedModelData18 = SpiderEntityModel.getTexturedModelData();
		builder.put(EntityModelLayers.ALLAY, AllayEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ARMOR_STAND, ArmorStandEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ARMOR_STAND_INNER_ARMOR, ArmorStandArmorEntityModel.getTexturedModelData(HAT_DILATION));
		builder.put(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR, ArmorStandArmorEntityModel.getTexturedModelData(ARMOR_DILATION));
		builder.put(EntityModelLayers.AXOLOTL, AxolotlEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.BANNER, BannerBlockEntityRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.BAT, BatEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.BED_FOOT, BedBlockEntityRenderer.getFootTexturedModelData());
		builder.put(EntityModelLayers.BED_HEAD, BedBlockEntityRenderer.getHeadTexturedModelData());
		builder.put(EntityModelLayers.BEE, BeeEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.BELL, BellBlockEntityRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.BLAZE, BlazeEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.BOOK, BookModel.getTexturedModelData());
		builder.put(EntityModelLayers.CAT, texturedModelData10);
		builder.put(EntityModelLayers.CAT_COLLAR, TexturedModelData.of(OcelotEntityModel.getModelData(new Dilation(0.01F)), 64, 32));
		builder.put(EntityModelLayers.CAMEL, CamelEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.CAVE_SPIDER, texturedModelData18);
		builder.put(EntityModelLayers.CHEST, ChestBlockEntityRenderer.getSingleTexturedModelData());
		builder.put(EntityModelLayers.DOUBLE_CHEST_LEFT, ChestBlockEntityRenderer.getLeftDoubleTexturedModelData());
		builder.put(EntityModelLayers.DOUBLE_CHEST_RIGHT, ChestBlockEntityRenderer.getRightDoubleTexturedModelData());
		builder.put(EntityModelLayers.CHEST_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.CHICKEN, ChickenEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.COD, CodEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.COMMAND_BLOCK_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.CONDUIT_EYE, ConduitBlockEntityRenderer.getEyeTexturedModelData());
		builder.put(EntityModelLayers.CONDUIT_WIND, ConduitBlockEntityRenderer.getWindTexturedModelData());
		builder.put(EntityModelLayers.CONDUIT_SHELL, ConduitBlockEntityRenderer.getShellTexturedModelData());
		builder.put(EntityModelLayers.CONDUIT, ConduitBlockEntityRenderer.getPlainTexturedModelData());
		builder.put(EntityModelLayers.COW, texturedModelData9);
		builder.put(EntityModelLayers.CREEPER, CreeperEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.CREEPER_ARMOR, CreeperEntityModel.getTexturedModelData(new Dilation(2.0F)));
		builder.put(EntityModelLayers.CREEPER_HEAD, texturedModelData6);
		builder.put(EntityModelLayers.DOLPHIN, DolphinEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.DONKEY, DonkeyEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.DRAGON_SKULL, DragonHeadEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.DROWNED, DrownedEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.DROWNED_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.DROWNED_OUTER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.DROWNED_OUTER, DrownedEntityModel.getTexturedModelData(new Dilation(0.25F)));
		builder.put(EntityModelLayers.ELDER_GUARDIAN, GuardianEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ELYTRA, ElytraEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ENDERMAN, EndermanEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ENDERMITE, EndermiteEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ENDER_DRAGON, EnderDragonEntityRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.END_CRYSTAL, EndCrystalEntityRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.EVOKER, texturedModelData8);
		builder.put(EntityModelLayers.EVOKER_FANGS, EvokerFangsEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.FOX, FoxEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.FROG, FrogEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.FURNACE_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.GHAST, GhastEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.GIANT, texturedModelData);
		builder.put(EntityModelLayers.GIANT_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.GIANT_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.GLOW_SQUID, SquidEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.GOAT, GoatEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.GUARDIAN, GuardianEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.HOGLIN, texturedModelData15);
		builder.put(EntityModelLayers.HOPPER_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.HORSE, texturedModelData7);
		builder.put(EntityModelLayers.HORSE_ARMOR, TexturedModelData.of(HorseEntityModel.getModelData(new Dilation(0.1F)), 64, 64));
		builder.put(EntityModelLayers.HUSK, texturedModelData);
		builder.put(EntityModelLayers.HUSK_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.HUSK_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.ILLUSIONER, texturedModelData8);
		builder.put(EntityModelLayers.IRON_GOLEM, IronGolemEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.LEASH_KNOT, LeashKnotEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.LLAMA, texturedModelData13);
		builder.put(EntityModelLayers.LLAMA_DECOR, LlamaEntityModel.getTexturedModelData(new Dilation(0.5F)));
		builder.put(EntityModelLayers.LLAMA_SPIT, LlamaSpitEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.MAGMA_CUBE, MagmaCubeEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.MINECART, texturedModelData5);
		builder.put(EntityModelLayers.MOOSHROOM, texturedModelData9);
		builder.put(EntityModelLayers.MULE, DonkeyEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.OCELOT, texturedModelData10);
		builder.put(EntityModelLayers.PANDA, PandaEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PARROT, ParrotEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PHANTOM, PhantomEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PIG, PigEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.PIG_SADDLE, PigEntityModel.getTexturedModelData(new Dilation(0.5F)));
		builder.put(EntityModelLayers.PIGLIN, texturedModelData11);
		builder.put(EntityModelLayers.PIGLIN_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.PIGLIN_OUTER_ARMOR, texturedModelData3);
		builder.put(EntityModelLayers.PIGLIN_BRUTE, texturedModelData11);
		builder.put(EntityModelLayers.PIGLIN_BRUTE_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, texturedModelData3);
		builder.put(EntityModelLayers.PILLAGER, texturedModelData8);
		builder.put(EntityModelLayers.PLAYER, TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64));
		builder.put(EntityModelLayers.PLAYER_HEAD, texturedModelData12);
		builder.put(EntityModelLayers.PLAYER_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.PLAYER_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.PLAYER_SLIM, TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, true), 64, 64));
		builder.put(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.SPIN_ATTACK, TridentRiptideFeatureRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.POLAR_BEAR, PolarBearEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PUFFERFISH_BIG, LargePufferfishEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PUFFERFISH_MEDIUM, MediumPufferfishEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.PUFFERFISH_SMALL, SmallPufferfishEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.RABBIT, RabbitEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.RAVAGER, RavagerEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SALMON, SalmonEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SHEEP, SheepEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SHEEP_FUR, SheepWoolEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SHIELD, ShieldEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SHULKER, ShulkerEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SHULKER_BULLET, ShulkerBulletEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SILVERFISH, SilverfishEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SKELETON, texturedModelData16);
		builder.put(EntityModelLayers.SKELETON_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.SKELETON_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.SKELETON_HORSE, texturedModelData7);
		builder.put(EntityModelLayers.SKELETON_SKULL, texturedModelData6);
		builder.put(EntityModelLayers.SLIME, SlimeEntityModel.getInnerTexturedModelData());
		builder.put(EntityModelLayers.SLIME_OUTER, SlimeEntityModel.getOuterTexturedModelData());
		builder.put(EntityModelLayers.SNOW_GOLEM, SnowGolemEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.SPAWNER_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.SPIDER, texturedModelData18);
		builder.put(EntityModelLayers.SQUID, SquidEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.STRAY, texturedModelData16);
		builder.put(EntityModelLayers.STRAY_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.STRAY_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.STRAY_OUTER, TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(0.25F), 0.0F), 64, 32));
		builder.put(EntityModelLayers.STRIDER, texturedModelData14);
		builder.put(EntityModelLayers.STRIDER_SADDLE, texturedModelData14);
		builder.put(EntityModelLayers.TADPOLE, TadpoleEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.TNT_MINECART, texturedModelData5);
		builder.put(EntityModelLayers.TRADER_LLAMA, texturedModelData13);
		builder.put(EntityModelLayers.TRIDENT, TridentEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.TROPICAL_FISH_LARGE, LargeTropicalFishEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN, LargeTropicalFishEntityModel.getTexturedModelData(FISH_PATTERN_DILATION));
		builder.put(EntityModelLayers.TROPICAL_FISH_SMALL, SmallTropicalFishEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN, SmallTropicalFishEntityModel.getTexturedModelData(FISH_PATTERN_DILATION));
		builder.put(EntityModelLayers.TURTLE, TurtleEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.VEX, VexEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.VILLAGER, texturedModelData17);
		builder.put(EntityModelLayers.VINDICATOR, texturedModelData8);
		builder.put(EntityModelLayers.WARDEN, WardenEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.WANDERING_TRADER, texturedModelData17);
		builder.put(EntityModelLayers.WITCH, WitchEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.WITHER, WitherEntityModel.getTexturedModelData(Dilation.NONE));
		builder.put(EntityModelLayers.WITHER_ARMOR, WitherEntityModel.getTexturedModelData(HAT_DILATION));
		builder.put(EntityModelLayers.WITHER_SKULL, WitherSkullEntityRenderer.getTexturedModelData());
		builder.put(EntityModelLayers.WITHER_SKELETON, texturedModelData16);
		builder.put(EntityModelLayers.WITHER_SKELETON_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.WITHER_SKELETON_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.WITHER_SKELETON_SKULL, texturedModelData6);
		builder.put(EntityModelLayers.WOLF, WolfEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ZOGLIN, texturedModelData15);
		builder.put(EntityModelLayers.ZOMBIE, texturedModelData);
		builder.put(EntityModelLayers.ZOMBIE_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.ZOMBIE_OUTER_ARMOR, texturedModelData2);
		builder.put(EntityModelLayers.ZOMBIE_HEAD, texturedModelData12);
		builder.put(EntityModelLayers.ZOMBIE_HORSE, texturedModelData7);
		builder.put(EntityModelLayers.ZOMBIE_VILLAGER, ZombieVillagerEntityModel.getTexturedModelData());
		builder.put(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, ZombieVillagerEntityModel.getArmorTexturedModelData(HAT_DILATION));
		builder.put(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, ZombieVillagerEntityModel.getArmorTexturedModelData(ARMOR_DILATION));
		builder.put(EntityModelLayers.ZOMBIFIED_PIGLIN, texturedModelData11);
		builder.put(EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, texturedModelData4);
		builder.put(EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, texturedModelData3);
		TexturedModelData texturedModelData19 = BoatEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData20 = ChestBoatEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData21 = RaftEntityModel.getTexturedModelData();
		TexturedModelData texturedModelData22 = ChestRaftEntityModel.getTexturedModelData();

		for (BoatEntity.Type type : BoatEntity.Type.values()) {
			if (type == BoatEntity.Type.BAMBOO) {
				builder.put(EntityModelLayers.createBoat(type), texturedModelData21);
				builder.put(EntityModelLayers.createChestBoat(type), texturedModelData22);
			} else {
				builder.put(EntityModelLayers.createBoat(type), texturedModelData19);
				builder.put(EntityModelLayers.createChestBoat(type), texturedModelData20);
			}
		}

		TexturedModelData texturedModelData23 = SignBlockEntityRenderer.getTexturedModelData();
		SignType.stream().forEach(signType -> builder.put(EntityModelLayers.createSign(signType), texturedModelData23));
		TexturedModelData texturedModelData24 = HangingSignBlockEntityRenderer.getTexturedModelData();
		SignType.stream().forEach(signType -> builder.put(EntityModelLayers.createHangingSign(signType), texturedModelData24));
		ImmutableMap<EntityModelLayer, TexturedModelData> immutableMap = builder.build();
		List<EntityModelLayer> list = (List<EntityModelLayer>)EntityModelLayers.getLayers()
			.filter(layer -> !immutableMap.containsKey(layer))
			.collect(Collectors.toList());
		if (!list.isEmpty()) {
			throw new IllegalStateException("Missing layer definitions: " + list);
		} else {
			return immutableMap;
		}
	}
}
