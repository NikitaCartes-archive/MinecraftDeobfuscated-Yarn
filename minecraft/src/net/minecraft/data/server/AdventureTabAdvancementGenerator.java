package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class AdventureTabAdvancementGenerator implements Consumer<Consumer<SimpleAdvancement>> {
	private static final Biome[] BIOMES = new Biome[]{
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
	};
	private static final EntityType<?>[] MONSTERS = new EntityType[]{
		EntityType.CAVE_SPIDER,
		EntityType.SPIDER,
		EntityType.ZOMBIE_PIGMAN,
		EntityType.ENDERMAN,
		EntityType.BLAZE,
		EntityType.CREEPER,
		EntityType.EVOKER,
		EntityType.GHAST,
		EntityType.GUARDIAN,
		EntityType.HUSK,
		EntityType.MAGMA_CUBE,
		EntityType.SHULKER,
		EntityType.SILVERFISH,
		EntityType.SKELETON,
		EntityType.SLIME,
		EntityType.STRAY,
		EntityType.VINDICATOR,
		EntityType.WITCH,
		EntityType.WITHER_SKELETON,
		EntityType.ZOMBIE,
		EntityType.ZOMBIE_VILLAGER,
		EntityType.PHANTOM,
		EntityType.DROWNED,
		EntityType.PILLAGER,
		EntityType.RAVAGER
	};

	public void method_10335(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.Task.create()
			.display(
				Items.field_8895,
				new TranslatableTextComponent("advancements.adventure.root.title"),
				new TranslatableTextComponent("advancements.adventure.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("killed_something", OnKilledCriterion.Conditions.method_8999())
			.criterion("killed_by_something", OnKilledCriterion.Conditions.method_8998())
			.build(consumer, "adventure/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement)
			.display(
				Blocks.field_10069,
				new TranslatableTextComponent("advancements.adventure.sleep_in_bed.title"),
				new TranslatableTextComponent("advancements.adventure.sleep_in_bed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("slept_in_bed", LocationArrivalCriterion.Conditions.method_9032())
			.build(consumer, "adventure/sleep_in_bed");
		SimpleAdvancement simpleAdvancement3 = this.method_10337(SimpleAdvancement.Task.create())
			.parent(simpleAdvancement2)
			.display(
				Items.field_8285,
				new TranslatableTextComponent("advancements.adventure.adventuring_time.title"),
				new TranslatableTextComponent("advancements.adventure.adventuring_time.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(500))
			.build(consumer, "adventure/adventuring_time");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8687,
				new TranslatableTextComponent("advancements.adventure.trade.title"),
				new TranslatableTextComponent("advancements.adventure.trade.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("traded", VillagerTradeCriterion.Conditions.any())
			.build(consumer, "adventure/trade");
		SimpleAdvancement simpleAdvancement5 = this.method_10336(SimpleAdvancement.Task.create())
			.parent(simpleAdvancement)
			.display(
				Items.field_8371,
				new TranslatableTextComponent("advancements.adventure.kill_a_mob.title"),
				new TranslatableTextComponent("advancements.adventure.kill_a_mob.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.build(consumer, "adventure/kill_a_mob");
		SimpleAdvancement simpleAdvancement6 = this.method_10336(SimpleAdvancement.Task.create())
			.parent(simpleAdvancement5)
			.display(
				Items.field_8802,
				new TranslatableTextComponent("advancements.adventure.kill_all_mobs.title"),
				new TranslatableTextComponent("advancements.adventure.kill_all_mobs.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(consumer, "adventure/kill_all_mobs");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8102,
				new TranslatableTextComponent("advancements.adventure.shoot_arrow.title"),
				new TranslatableTextComponent("advancements.adventure.shoot_arrow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"shot_arrow",
				PlayerHurtEntityCriterion.Conditions.method_9103(
					DamagePredicate.Builder.create()
						.type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.ARROW)))
				)
			)
			.build(consumer, "adventure/shoot_arrow");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8547,
				new TranslatableTextComponent("advancements.adventure.throw_trident.title"),
				new TranslatableTextComponent("advancements.adventure.throw_trident.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"shot_trident",
				PlayerHurtEntityCriterion.Conditions.method_9103(
					DamagePredicate.Builder.create()
						.type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT)))
				)
			)
			.build(consumer, "adventure/throw_trident");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement8)
			.display(
				Items.field_8547,
				new TranslatableTextComponent("advancements.adventure.very_very_frightening.title"),
				new TranslatableTextComponent("advancements.adventure.very_very_frightening.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build()))
			.build(consumer, "adventure/very_very_frightening");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement4)
			.display(
				Blocks.field_10147,
				new TranslatableTextComponent("advancements.adventure.summon_iron_golem.title"),
				new TranslatableTextComponent("advancements.adventure.summon_iron_golem.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("summoned_golem", SummonedEntityCriterion.Conditions.method_9129(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM)))
			.build(consumer, "adventure/summon_iron_golem");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement7)
			.display(
				Items.field_8107,
				new TranslatableTextComponent("advancements.adventure.sniper_duel.title"),
				new TranslatableTextComponent("advancements.adventure.sniper_duel.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_skeleton",
				OnKilledCriterion.Conditions.method_9001(
					EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.method_8860(NumberRange.FloatRange.atLeast(50.0F))),
					DamageSourcePredicate.Builder.create().projectile(true)
				)
			)
			.build(consumer, "adventure/sniper_duel");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8288,
				new TranslatableTextComponent("advancements.adventure.totem_of_undying.title"),
				new TranslatableTextComponent("advancements.adventure.totem_of_undying.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("used_totem", UsedTotemCriterion.Conditions.method_9170(Items.field_8288))
			.build(consumer, "adventure/totem_of_undying");
		SimpleAdvancement simpleAdvancement13 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8399,
				new TranslatableTextComponent("advancements.adventure.ol_betsy.title"),
				new TranslatableTextComponent("advancements.adventure.ol_betsy.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.field_8399))
			.build(consumer, "adventure/ol_betsy");
		SimpleAdvancement simpleAdvancement14 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement13)
			.display(
				Items.field_8399,
				new TranslatableTextComponent("advancements.adventure.whos_the_pillager_now.title"),
				new TranslatableTextComponent("advancements.adventure.whos_the_pillager_now.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.method_8986(EntityPredicate.Builder.create().type(EntityType.PILLAGER)))
			.build(consumer, "adventure/whos_the_pillager_now");
		SimpleAdvancement simpleAdvancement15 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement13)
			.display(
				Items.field_8399,
				new TranslatableTextComponent("advancements.adventure.two_birds_one_arrow.title"),
				new TranslatableTextComponent("advancements.adventure.two_birds_one_arrow.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(65))
			.criterion(
				"two_birds",
				KilledByCrossbowCriterion.Conditions.method_8986(
					EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM)
				)
			)
			.build(consumer, "adventure/two_birds_one_arrow");
		SimpleAdvancement simpleAdvancement16 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement13)
			.display(
				Items.field_8399,
				new TranslatableTextComponent("advancements.adventure.arbalistic.title"),
				new TranslatableTextComponent("advancements.adventure.arbalistic.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(85))
			.criterion("arbalistic", KilledByCrossbowCriterion.Conditions.method_8987(NumberRange.IntRange.exactly(5)))
			.build(consumer, "adventure/arbalistic");
	}

	private SimpleAdvancement.Task method_10336(SimpleAdvancement.Task task) {
		for (EntityType<?> entityType : MONSTERS) {
			task.criterion(Registry.ENTITY_TYPE.getId(entityType).toString(), OnKilledCriterion.Conditions.createKill(EntityPredicate.Builder.create().type(entityType)));
		}

		return task;
	}

	private SimpleAdvancement.Task method_10337(SimpleAdvancement.Task task) {
		for (Biome biome : BIOMES) {
			task.criterion(Registry.BIOME.getId(biome).toString(), LocationArrivalCriterion.Conditions.method_9034(LocationPredicate.method_9022(biome)));
		}

		return task;
	}
}
