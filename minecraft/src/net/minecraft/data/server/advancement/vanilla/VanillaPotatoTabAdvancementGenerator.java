package net.minecraft.data.server.advancement.vanilla;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.PotatoRefinedCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.advancement.criterion.ThrowLubricatedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;

public class VanillaPotatoTabAdvancementGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = create("root")
			.display(Items.POISONOUS_POTATO.getDefaultStack(), AdvancementFrame.TASK, false, false, false)
			.criterion("joined_world", TickCriterion.Conditions.createLocation(Optional.empty()))
			.build(exporter);
		create("get_peeled")
			.parent(advancementEntry)
			.display(Items.POTATO_PEELS.get(DyeColor.WHITE).getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("get_peeled", TickCriterion.Conditions.createGetPeeled())
			.build(exporter);
		AdvancementEntry advancementEntry2 = create("enter_the_potato")
			.parent(advancementEntry)
			.display(Items.POTATO_OF_KNOWLEDGE.getDefaultStack(), AdvancementFrame.TASK, true, true, true)
			.criterion("entered_potato", ChangedDimensionCriterion.Conditions.to(World.POTATO))
			.build(exporter);
		VanillaAdventureTabAdvancementGenerator.requireListedBiomesVisited(
				create("all_potatoed"), lookup, MultiNoiseBiomeSourceParameterList.Preset.POTATO.biomeStream().toList()
			)
			.parent(advancementEntry2)
			.display(Items.GRAVTATER.getDefaultStack(), AdvancementFrame.CHALLENGE, true, true, false)
			.build(exporter);
		create("eat_armor")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_CHESTPLATE.getDefaultStack(), AdvancementFrame.TASK, true, true, true)
			.criterion("eat_armor", TickCriterion.Conditions.createEatArmor())
			.build(exporter);
		AdvancementEntry advancementEntry3 = create("rumbled")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_PLANT.getDefaultStack(), AdvancementFrame.TASK, false, true, false)
			.criterion("rumble_plant", TickCriterion.Conditions.createRumblePlant())
			.build(exporter);
		create("good_plant")
			.parent(advancementEntry3)
			.display(Items.POTATO_STAFF.getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("compost_staff", TickCriterion.Conditions.createCompostStaff())
			.build(exporter);
		AdvancementEntry advancementEntry4 = create("get_oily")
			.parent(advancementEntry)
			.display(Items.POTATO_OIL.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("refine_potato_oil", PotatoRefinedCriterion.Conditions.createStandard(Items.POTATO_OIL))
			.build(exporter);
		AdvancementEntry advancementEntry5 = create("lubricate")
			.parent(advancementEntry4)
			.display(Items.POTATO_OIL.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("lubricate_item", PotatoRefinedCriterion.Conditions.createLubrication(1))
			.build(exporter);
		create("mega_lubricate")
			.parent(advancementEntry4)
			.display(Items.POTATO_OIL.getDefaultStack().setExplicitFoil(), AdvancementFrame.TASK, true, false, true)
			.criterion("mega_lubricate_item", PotatoRefinedCriterion.Conditions.createLubrication(10))
			.build(exporter);
		AdvancementEntry advancementEntry6 = create("lubricate_whee")
			.parent(advancementEntry5)
			.display(Items.ICE.getDefaultStack(), AdvancementFrame.TASK, true, true, true)
			.criterion("throw_lubricated_item", ThrowLubricatedCriterion.Conditions.create(1))
			.build(exporter);
		create("mega_lubricate_whee")
			.parent(advancementEntry6)
			.display(Items.ICE.getDefaultStack().setExplicitFoil(), AdvancementFrame.TASK, true, true, true)
			.criterion("throw_mega_lubricated_item", ThrowLubricatedCriterion.Conditions.create(10))
			.build(exporter);
		create("lubricate_boots")
			.parent(advancementEntry5)
			.display(Items.POISONOUS_POTA_TOES.getDefaultStack(), AdvancementFrame.TASK, true, true, true)
			.criterion("lubricate_boots", PotatoRefinedCriterion.Conditions.createLubrication(ItemPredicate.Builder.create().tag(ItemTags.FOOT_ARMOR).build(), 1))
			.build(exporter);
		create("sweet_potato_talker")
			.parent(advancementEntry)
			.display(Items.POTATO_FLOWER.getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("said_potato", TickCriterion.Conditions.createSaidPotato(99))
			.build(exporter);
		create("craft_poisonous_potato_sticks")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_STICKS.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("poisonous_potato_sticks", InventoryChangedCriterion.Conditions.items(Items.POISONOUS_POTATO_STICKS))
			.build(exporter);
		create("craft_poisonous_potato_slices")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_SLICES.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("poisonous_potato_slices", InventoryChangedCriterion.Conditions.items(Items.POISONOUS_POTATO_SLICES))
			.build(exporter);
		create("craft_poisonous_potato_fries")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_FRIES.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("poisonous_potato_fries", InventoryChangedCriterion.Conditions.items(Items.POISONOUS_POTATO_FRIES))
			.build(exporter);
		create("craft_poisonous_potato_chips")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_CHIPS.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("poisonous_potato_chips", InventoryChangedCriterion.Conditions.items(Items.POISONOUS_POTATO_CHIPS))
			.build(exporter);
		AdvancementEntry advancementEntry7 = create("poisonous_potato_taster")
			.parent(advancementEntry)
			.display(Items.POISONOUS_POTATO_STICKS.getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("ate_poisonous_potato_sticks", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_STICKS))
			.criterion("ate_poisonous_potato_slices", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_SLICES))
			.build(exporter);
		create("poisonous_potato_gourmet")
			.parent(advancementEntry7)
			.display(Items.POISONOUS_POTATO_CHIPS.getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("ate_poisonous_potato_sticks", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_STICKS))
			.criterion("ate_poisonous_potato_slices", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_SLICES))
			.criterion("ate_poisonous_potato_fries", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_FRIES))
			.criterion("ate_poisonous_potato_chips", ConsumeItemCriterion.Conditions.item(Items.POISONOUS_POTATO_CHIPS))
			.build(exporter);
		create("bring_home_the_corruption")
			.parent(advancementEntry2)
			.display(Items.CORRUPTED_PEELGRASS_BLOCK.getDefaultStack(), AdvancementFrame.TASK, true, true, true)
			.criterion("bring_home_the_corruption", TickCriterion.Conditions.createBringHomeCorruption())
			.build(exporter);
		AdvancementEntry advancementEntry8 = create("potato_peeler")
			.parent(advancementEntry)
			.display(Items.POTATO_PEELER.getDefaultStack(), AdvancementFrame.TASK, true, false, false)
			.criterion("potato_peeler", InventoryChangedCriterion.Conditions.items(Items.POTATO_PEELER))
			.build(exporter);
		create("peel_all_the_things")
			.parent(advancementEntry8)
			.display(Items.POTATO_PEELER.getDefaultStack(), AdvancementFrame.CHALLENGE, true, true, true)
			.criterion("peel_block", createEmptyCriterion(Criteria.PEEL_BLOCK))
			.criterion("peel_sheep", createEmptyCriterion(Criteria.PEEL_POTATO_SHEEP))
			.criterion("peel_armor", createEmptyCriterion(Criteria.PEEL_POTATO_ARMOR))
			.build(exporter);
		create("well_done")
			.parent(advancementEntry)
			.display(Items.CHARCOAL.getDefaultStack(), AdvancementFrame.TASK, true, true, false)
			.criterion("well_done", RecipeCraftedCriterion.Conditions.create(new Identifier("overcooked_potatoes")))
			.build(exporter);
	}

	private static VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder create(String id) {
		return new VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder(id).sendsTelemetryEvent();
	}

	private static AdvancementCriterion<TickCriterion.Conditions> createEmptyCriterion(TickCriterion criterion) {
		return criterion.create(new TickCriterion.Conditions(Optional.empty()));
	}

	static class PotatoAdvancementBuilder extends Advancement.Builder {
		private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/advancements/backgrounds/potato.png");
		private final String id;

		PotatoAdvancementBuilder(String id) {
			this.id = id;
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder sendsTelemetryEvent() {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.sendsTelemetryEvent();
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder display(
			ItemStack icon, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden
		) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)this.display(
				icon,
				Text.translatable("advancements.potato." + this.id + ".title"),
				Text.translatable("advancements.potato." + this.id + ".description"),
				BACKGROUND_TEXTURE,
				frame,
				showToast,
				announceToChat,
				hidden
			);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder parent(AdvancementEntry advancementEntry) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.parent(advancementEntry);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder rewards(AdvancementRewards.Builder builder) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.rewards(builder);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder rewards(AdvancementRewards advancementRewards) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.rewards(advancementRewards);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.criterion(string, advancementCriterion);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder criteriaMerger(AdvancementRequirements.CriterionMerger criterionMerger) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.criteriaMerger(criterionMerger);
		}

		public VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder requirements(AdvancementRequirements advancementRequirements) {
			return (VanillaPotatoTabAdvancementGenerator.PotatoAdvancementBuilder)super.requirements(advancementRequirements);
		}

		public AdvancementEntry build(Consumer<AdvancementEntry> exporter) {
			return this.build(exporter, "potato/" + this.id);
		}
	}
}
