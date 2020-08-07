package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class AdventureTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	private static final List<RegistryKey<Biome>> BIOMES = ImmutableList.of(
		Biomes.field_9421,
		Biomes.field_9438,
		Biomes.field_9471,
		Biomes.field_9424,
		Biomes.field_9459,
		Biomes.field_9429,
		Biomes.field_9454,
		Biomes.field_9415,
		Biomes.field_9409,
		Biomes.field_9419,
		Biomes.field_9452,
		Biomes.field_9428,
		Biomes.field_9444,
		Biomes.field_9410,
		Biomes.field_9449,
		Biomes.field_9451,
		Biomes.field_9463,
		Biomes.field_9477,
		Biomes.field_9478,
		Biomes.field_9432,
		Biomes.field_9474,
		Biomes.field_9407,
		Biomes.field_9472,
		Biomes.field_9466,
		Biomes.field_9417,
		Biomes.field_9434,
		Biomes.field_9430,
		Biomes.field_9425,
		Biomes.field_9433,
		Biomes.field_9475,
		Biomes.field_9420,
		Biomes.field_9412,
		Biomes.field_9462,
		Biomes.field_9460,
		Biomes.field_9408,
		Biomes.field_9441,
		Biomes.field_9467,
		Biomes.field_9439,
		Biomes.field_9470,
		Biomes.field_9418,
		Biomes.field_9440,
		Biomes.field_9468
	);
	private static final EntityType<?>[] MONSTERS = new EntityType[]{
		EntityType.field_6099,
		EntityType.field_6084,
		EntityType.field_6046,
		EntityType.field_6123,
		EntityType.field_6086,
		EntityType.field_6116,
		EntityType.field_6091,
		EntityType.field_6128,
		EntityType.field_6090,
		EntityType.field_6107,
		EntityType.field_6118,
		EntityType.field_21973,
		EntityType.field_6071,
		EntityType.field_6102,
		EntityType.field_6078,
		EntityType.field_22281,
		EntityType.field_25751,
		EntityType.field_6105,
		EntityType.field_6134,
		EntityType.field_6109,
		EntityType.field_6125,
		EntityType.field_6137,
		EntityType.field_6069,
		EntityType.field_6079,
		EntityType.field_6098,
		EntityType.field_6059,
		EntityType.field_6117,
		EntityType.field_6145,
		EntityType.field_6076,
		EntityType.field_6119,
		EntityType.field_23696,
		EntityType.field_6054,
		EntityType.field_6051,
		EntityType.field_6050
	};

	public void method_10335(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Items.field_8895,
				new TranslatableText("advancements.adventure.root.title"),
				new TranslatableText("advancements.adventure.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
				AdvancementFrame.field_1254,
				false,
				false,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
			.criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer())
			.build(consumer, "adventure/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Blocks.field_10069,
				new TranslatableText("advancements.adventure.sleep_in_bed.title"),
				new TranslatableText("advancements.adventure.sleep_in_bed.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("slept_in_bed", LocationArrivalCriterion.Conditions.createSleptInBed())
			.build(consumer, "adventure/sleep_in_bed");
		requireListedBiomesVisited(Advancement.Task.create(), BIOMES)
			.parent(advancement2)
			.display(
				Items.field_8285,
				new TranslatableText("advancements.adventure.adventuring_time.title"),
				new TranslatableText("advancements.adventure.adventuring_time.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(500))
			.build(consumer, "adventure/adventuring_time");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8687,
				new TranslatableText("advancements.adventure.trade.title"),
				new TranslatableText("advancements.adventure.trade.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("traded", VillagerTradeCriterion.Conditions.any())
			.build(consumer, "adventure/trade");
		Advancement advancement4 = this.requireListedMobsKilled(Advancement.Task.create())
			.parent(advancement)
			.display(
				Items.field_8371,
				new TranslatableText("advancements.adventure.kill_a_mob.title"),
				new TranslatableText("advancements.adventure.kill_a_mob.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.build(consumer, "adventure/kill_a_mob");
		this.requireListedMobsKilled(Advancement.Task.create())
			.parent(advancement4)
			.display(
				Items.field_8802,
				new TranslatableText("advancements.adventure.kill_all_mobs.title"),
				new TranslatableText("advancements.adventure.kill_all_mobs.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "adventure/kill_all_mobs");
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8102,
				new TranslatableText("advancements.adventure.shoot_arrow.title"),
				new TranslatableText("advancements.adventure.shoot_arrow.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion(
				"shot_arrow",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityTypeTags.field_21508)))
				)
			)
			.build(consumer, "adventure/shoot_arrow");
		Advancement advancement6 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8547,
				new TranslatableText("advancements.adventure.throw_trident.title"),
				new TranslatableText("advancements.adventure.throw_trident.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion(
				"shot_trident",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.field_6127)))
				)
			)
			.build(consumer, "adventure/throw_trident");
		Advancement.Task.create()
			.parent(advancement6)
			.display(
				Items.field_8547,
				new TranslatableText("advancements.adventure.very_very_frightening.title"),
				new TranslatableText("advancements.adventure.very_very_frightening.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.field_6077).build()))
			.build(consumer, "adventure/very_very_frightening");
		Advancement.Task.create()
			.parent(advancement3)
			.display(
				Blocks.field_10147,
				new TranslatableText("advancements.adventure.summon_iron_golem.title"),
				new TranslatableText("advancements.adventure.summon_iron_golem.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.field_6147)))
			.build(consumer, "adventure/summon_iron_golem");
		Advancement.Task.create()
			.parent(advancement5)
			.display(
				Items.field_8107,
				new TranslatableText("advancements.adventure.sniper_duel.title"),
				new TranslatableText("advancements.adventure.sniper_duel.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_skeleton",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.field_6137).distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(50.0F))),
					DamageSourcePredicate.Builder.create().projectile(true)
				)
			)
			.build(consumer, "adventure/sniper_duel");
		Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8288,
				new TranslatableText("advancements.adventure.totem_of_undying.title"),
				new TranslatableText("advancements.adventure.totem_of_undying.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.field_8288))
			.build(consumer, "adventure/totem_of_undying");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8399,
				new TranslatableText("advancements.adventure.ol_betsy.title"),
				new TranslatableText("advancements.adventure.ol_betsy.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.field_8399))
			.build(consumer, "adventure/ol_betsy");
		Advancement.Task.create()
			.parent(advancement7)
			.display(
				Items.field_8399,
				new TranslatableText("advancements.adventure.whos_the_pillager_now.title"),
				new TranslatableText("advancements.adventure.whos_the_pillager_now.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.field_6105)))
			.build(consumer, "adventure/whos_the_pillager_now");
		Advancement.Task.create()
			.parent(advancement7)
			.display(
				Items.field_8399,
				new TranslatableText("advancements.adventure.two_birds_one_arrow.title"),
				new TranslatableText("advancements.adventure.two_birds_one_arrow.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(65))
			.criterion(
				"two_birds",
				KilledByCrossbowCriterion.Conditions.create(
					EntityPredicate.Builder.create().type(EntityType.field_6078), EntityPredicate.Builder.create().type(EntityType.field_6078)
				)
			)
			.build(consumer, "adventure/two_birds_one_arrow");
		Advancement.Task.create()
			.parent(advancement7)
			.display(
				Items.field_8399,
				new TranslatableText("advancements.adventure.arbalistic.title"),
				new TranslatableText("advancements.adventure.arbalistic.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(85))
			.criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5)))
			.build(consumer, "adventure/arbalistic");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Raid.getOminousBanner(),
				new TranslatableText("advancements.adventure.voluntary_exile.title"),
				new TranslatableText("advancements.adventure.voluntary_exile.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				true
			)
			.criterion(
				"voluntary_exile",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityTypeTags.field_19168).equipment(EntityEquipmentPredicate.OMINOUS_BANNER_ON_HEAD)
				)
			)
			.build(consumer, "adventure/voluntary_exile");
		Advancement.Task.create()
			.parent(advancement8)
			.display(
				Raid.getOminousBanner(),
				new TranslatableText("advancements.adventure.hero_of_the_village.title"),
				new TranslatableText("advancements.adventure.hero_of_the_village.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("hero_of_the_village", LocationArrivalCriterion.Conditions.createHeroOfTheVillage())
			.build(consumer, "adventure/hero_of_the_village");
		Advancement.Task.create()
			.parent(advancement)
			.display(
				Blocks.field_21211.asItem(),
				new TranslatableText("advancements.adventure.honey_block_slide.title"),
				new TranslatableText("advancements.adventure.honey_block_slide.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("honey_block_slide", SlideDownBlockCriterion.Conditions.create(Blocks.field_21211))
			.build(consumer, "adventure/honey_block_slide");
		Advancement.Task.create()
			.parent(advancement5)
			.display(
				Blocks.field_22422.asItem(),
				new TranslatableText("advancements.adventure.bullseye.title"),
				new TranslatableText("advancements.adventure.bullseye.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"bullseye",
				TargetHitCriterion.Conditions.create(
					NumberRange.IntRange.exactly(15),
					EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(30.0F))).build())
				)
			)
			.build(consumer, "adventure/bullseye");
	}

	private Advancement.Task requireListedMobsKilled(Advancement.Task task) {
		for (EntityType<?> entityType : MONSTERS) {
			task.criterion(
				Registry.ENTITY_TYPE.getId(entityType).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(entityType))
			);
		}

		return task;
	}

	protected static Advancement.Task requireListedBiomesVisited(Advancement.Task task, List<RegistryKey<Biome>> list) {
		for (RegistryKey<Biome> registryKey : list) {
			task.criterion(registryKey.getValue().toString(), LocationArrivalCriterion.Conditions.create(LocationPredicate.biome(registryKey)));
		}

		return task;
	}
}
