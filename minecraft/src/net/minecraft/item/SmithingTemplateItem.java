package net.minecraft.item;

import java.util.List;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class SmithingTemplateItem extends Item {
	private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
	private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
	private static final Text INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.ingredients")))
		.formatted(TITLE_FORMATTING);
	private static final Text APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to")))
		.formatted(TITLE_FORMATTING);
	private static final Text NETHERITE_UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("upgrade", Identifier.ofVanilla("netherite_upgrade")))
		.formatted(TITLE_FORMATTING);
	private static final Text ARMOR_TRIM_APPLIES_TO_TEXT = Text.translatable(
			Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.applies_to"))
		)
		.formatted(DESCRIPTION_FORMATTING);
	private static final Text ARMOR_TRIM_INGREDIENTS_TEXT = Text.translatable(
			Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.ingredients"))
		)
		.formatted(DESCRIPTION_FORMATTING);
	private static final Text ARMOR_TRIM_BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(
		Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.base_slot_description"))
	);
	private static final Text ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(
		Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.additions_slot_description"))
	);
	private static final Text NETHERITE_UPGRADE_APPLIES_TO_TEXT = Text.translatable(
			Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.applies_to"))
		)
		.formatted(DESCRIPTION_FORMATTING);
	private static final Text NETHERITE_UPGRADE_INGREDIENTS_TEXT = Text.translatable(
			Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.ingredients"))
		)
		.formatted(DESCRIPTION_FORMATTING);
	private static final Text NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(
		Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.base_slot_description"))
	);
	private static final Text NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(
		Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.additions_slot_description"))
	);
	private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
	private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
	private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
	private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
	private static final Identifier EMPTY_SLOT_HOE_TEXTURE = Identifier.ofVanilla("item/empty_slot_hoe");
	private static final Identifier EMPTY_SLOT_AXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_axe");
	private static final Identifier EMPTY_SLOT_SWORD_TEXTURE = Identifier.ofVanilla("item/empty_slot_sword");
	private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = Identifier.ofVanilla("item/empty_slot_shovel");
	private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_pickaxe");
	private static final Identifier EMPTY_SLOT_INGOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_ingot");
	private static final Identifier EMPTY_SLOT_REDSTONE_DUST_TEXTURE = Identifier.ofVanilla("item/empty_slot_redstone_dust");
	private static final Identifier EMPTY_SLOT_QUARTZ_TEXTURE = Identifier.ofVanilla("item/empty_slot_quartz");
	private static final Identifier EMPTY_SLOT_EMERALD_TEXTURE = Identifier.ofVanilla("item/empty_slot_emerald");
	private static final Identifier EMPTY_SLOT_DIAMOND_TEXTURE = Identifier.ofVanilla("item/empty_slot_diamond");
	private static final Identifier EMPTY_SLOT_LAPIS_LAZULI_TEXTURE = Identifier.ofVanilla("item/empty_slot_lapis_lazuli");
	private static final Identifier EMPTY_SLOT_AMETHYST_SHARD_TEXTURE = Identifier.ofVanilla("item/empty_slot_amethyst_shard");
	private final Text appliesToText;
	private final Text ingredientsText;
	private final Text titleText;
	private final Text baseSlotDescriptionText;
	private final Text additionsSlotDescriptionText;
	private final List<Identifier> emptyBaseSlotTextures;
	private final List<Identifier> emptyAdditionsSlotTextures;

	public SmithingTemplateItem(
		Text appliesToText,
		Text ingredientsText,
		Text titleText,
		Text baseSlotDescriptionText,
		Text additionsSlotDescriptionText,
		List<Identifier> emptyBaseSlotTextures,
		List<Identifier> emptyAdditionsSlotTextures,
		FeatureFlag... requiredFeatures
	) {
		super(new Item.Settings().requires(requiredFeatures));
		this.appliesToText = appliesToText;
		this.ingredientsText = ingredientsText;
		this.titleText = titleText;
		this.baseSlotDescriptionText = baseSlotDescriptionText;
		this.additionsSlotDescriptionText = additionsSlotDescriptionText;
		this.emptyBaseSlotTextures = emptyBaseSlotTextures;
		this.emptyAdditionsSlotTextures = emptyAdditionsSlotTextures;
	}

	public static SmithingTemplateItem of(RegistryKey<ArmorTrimPattern> trimPattern, FeatureFlag... requiredFeatures) {
		return of(trimPattern.getValue(), requiredFeatures);
	}

	public static SmithingTemplateItem of(Identifier trimPatternIn, FeatureFlag... requiredFeatures) {
		return new SmithingTemplateItem(
			ARMOR_TRIM_APPLIES_TO_TEXT,
			ARMOR_TRIM_INGREDIENTS_TEXT,
			Text.translatable(Util.createTranslationKey("trim_pattern", trimPatternIn)).formatted(TITLE_FORMATTING),
			ARMOR_TRIM_BASE_SLOT_DESCRIPTION_TEXT,
			ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT,
			getArmorTrimEmptyBaseSlotTextures(),
			getArmorTrimEmptyAdditionsSlotTextures(),
			requiredFeatures
		);
	}

	public static SmithingTemplateItem createNetheriteUpgrade() {
		return new SmithingTemplateItem(
			NETHERITE_UPGRADE_APPLIES_TO_TEXT,
			NETHERITE_UPGRADE_INGREDIENTS_TEXT,
			NETHERITE_UPGRADE_TEXT,
			NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION_TEXT,
			NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION_TEXT,
			getNetheriteUpgradeEmptyBaseSlotTextures(),
			getNetheriteUpgradeEmptyAdditionsSlotTextures()
		);
	}

	private static List<Identifier> getArmorTrimEmptyBaseSlotTextures() {
		return List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE, EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE);
	}

	private static List<Identifier> getArmorTrimEmptyAdditionsSlotTextures() {
		return List.of(
			EMPTY_SLOT_INGOT_TEXTURE,
			EMPTY_SLOT_REDSTONE_DUST_TEXTURE,
			EMPTY_SLOT_LAPIS_LAZULI_TEXTURE,
			EMPTY_SLOT_QUARTZ_TEXTURE,
			EMPTY_SLOT_DIAMOND_TEXTURE,
			EMPTY_SLOT_EMERALD_TEXTURE,
			EMPTY_SLOT_AMETHYST_SHARD_TEXTURE
		);
	}

	private static List<Identifier> getNetheriteUpgradeEmptyBaseSlotTextures() {
		return List.of(
			EMPTY_ARMOR_SLOT_HELMET_TEXTURE,
			EMPTY_SLOT_SWORD_TEXTURE,
			EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE,
			EMPTY_SLOT_PICKAXE_TEXTURE,
			EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE,
			EMPTY_SLOT_AXE_TEXTURE,
			EMPTY_ARMOR_SLOT_BOOTS_TEXTURE,
			EMPTY_SLOT_HOE_TEXTURE,
			EMPTY_SLOT_SHOVEL_TEXTURE
		);
	}

	private static List<Identifier> getNetheriteUpgradeEmptyAdditionsSlotTextures() {
		return List.of(EMPTY_SLOT_INGOT_TEXTURE);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		tooltip.add(this.titleText);
		tooltip.add(ScreenTexts.EMPTY);
		tooltip.add(APPLIES_TO_TEXT);
		tooltip.add(ScreenTexts.space().append(this.appliesToText));
		tooltip.add(INGREDIENTS_TEXT);
		tooltip.add(ScreenTexts.space().append(this.ingredientsText));
	}

	public Text getBaseSlotDescription() {
		return this.baseSlotDescriptionText;
	}

	public Text getAdditionsSlotDescription() {
		return this.additionsSlotDescriptionText;
	}

	public List<Identifier> getEmptyBaseSlotTextures() {
		return this.emptyBaseSlotTextures;
	}

	public List<Identifier> getEmptyAdditionsSlotTextures() {
		return this.emptyAdditionsSlotTextures;
	}
}
