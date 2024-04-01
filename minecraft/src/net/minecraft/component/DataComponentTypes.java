package net.minecraft.component;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.FletchingTableBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.HeatComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.LubricationComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotatoBaneComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.SnekComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.component.type.XpComponent;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.Instrument;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;

public class DataComponentTypes {
	public static final DataComponentType<NbtComponent> CUSTOM_DATA = register("custom_data", builder -> builder.codec(NbtComponent.CODEC));
	public static final DataComponentType<Integer> MAX_STACK_SIZE = register(
		"max_stack_size", builder -> builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> MAX_DAMAGE = register(
		"max_damage", builder -> builder.codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> DAMAGE = register("damage", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
	public static final DataComponentType<UnbreakableComponent> UNBREAKABLE = register(
		"unbreakable", builder -> builder.codec(UnbreakableComponent.CODEC).packetCodec(UnbreakableComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Text> CUSTOM_NAME = register(
		"custom_name", builder -> builder.codec(TextCodecs.STRINGIFIED_CODEC).packetCodec(TextCodecs.REGISTRY_PACKET_CODEC)
	);
	public static final DataComponentType<LoreComponent> LORE = register(
		"lore", builder -> builder.codec(LoreComponent.CODEC).packetCodec(LoreComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Rarity> RARITY = register("rarity", builder -> builder.codec(Rarity.CODEC).packetCodec(Rarity.PACKET_CODEC));
	public static final DataComponentType<ItemEnchantmentsComponent> ENCHANTMENTS = register(
		"enchantments", builder -> builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<BlockPredicatesChecker> CAN_PLACE_ON = register(
		"can_place_on", builder -> builder.codec(BlockPredicatesChecker.CODEC).packetCodec(BlockPredicatesChecker.PACKET_CODEC)
	);
	public static final DataComponentType<BlockPredicatesChecker> CAN_BREAK = register(
		"can_break", builder -> builder.codec(BlockPredicatesChecker.CODEC).packetCodec(BlockPredicatesChecker.PACKET_CODEC)
	);
	public static final DataComponentType<AttributeModifiersComponent> ATTRIBUTE_MODIFIERS = register(
		"attribute_modifiers", builder -> builder.codec(AttributeModifiersComponent.CODEC).packetCodec(AttributeModifiersComponent.PACKET_CODEC)
	);
	public static final DataComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = register(
		"custom_model_data", builder -> builder.codec(CustomModelDataComponent.CODEC).packetCodec(CustomModelDataComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Unit> HIDE_ADDITIONAL_TOOLTIP = register(
		"hide_additional_tooltip", builder -> builder.codec(Codec.unit(Unit.INSTANCE)).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final DataComponentType<Unit> HIDE_TOOLTIP = register(
		"hide_tooltip", builder -> builder.codec(Codec.unit(Unit.INSTANCE)).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final DataComponentType<Integer> REPAIR_COST = register(
		"repair_cost", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final DataComponentType<Unit> CREATIVE_SLOT_LOCK = register(
		"creative_slot_lock", builder -> builder.packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final DataComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE = register(
		"enchantment_glint_override", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
	);
	public static final DataComponentType<Unit> INTANGIBLE_PROJECTILE = register("intangible_projectile", builder -> builder.codec(Codec.unit(Unit.INSTANCE)));
	public static final DataComponentType<FoodComponent> FOOD = register(
		"food", builder -> builder.codec(FoodComponent.CODEC).packetCodec(FoodComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Unit> FIRE_RESISTANT = register(
		"fire_resistant", builder -> builder.codec(Codec.unit(Unit.INSTANCE)).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final DataComponentType<ToolComponent> TOOL = register(
		"tool", builder -> builder.codec(ToolComponent.CODEC).packetCodec(ToolComponent.PACKET_CODEC)
	);
	public static final DataComponentType<ItemEnchantmentsComponent> STORED_ENCHANTMENTS = register(
		"stored_enchantments", builder -> builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<DyedColorComponent> DYED_COLOR = register(
		"dyed_color", builder -> builder.codec(DyedColorComponent.CODEC).packetCodec(DyedColorComponent.PACKET_CODEC)
	);
	public static final DataComponentType<MapColorComponent> MAP_COLOR = register(
		"map_color", builder -> builder.codec(MapColorComponent.CODEC).packetCodec(MapColorComponent.PACKET_CODEC)
	);
	public static final DataComponentType<MapIdComponent> MAP_ID = register(
		"map_id", builder -> builder.codec(MapIdComponent.CODEC).packetCodec(MapIdComponent.PACKET_CODEC)
	);
	public static final DataComponentType<MapDecorationsComponent> MAP_DECORATIONS = register(
		"map_decorations", builder -> builder.codec(MapDecorationsComponent.CODEC)
	);
	public static final DataComponentType<MapPostProcessingComponent> MAP_POST_PROCESSING = register(
		"map_post_processing", builder -> builder.packetCodec(MapPostProcessingComponent.PACKET_CODEC)
	);
	public static final DataComponentType<ChargedProjectilesComponent> CHARGED_PROJECTILES = register(
		"charged_projectiles", builder -> builder.codec(ChargedProjectilesComponent.CODEC).packetCodec(ChargedProjectilesComponent.PACKET_CODEC)
	);
	public static final DataComponentType<BundleContentsComponent> BUNDLE_CONTENTS = register(
		"bundle_contents", builder -> builder.codec(BundleContentsComponent.CODEC).packetCodec(BundleContentsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<PotionContentsComponent> POTION_CONTENTS = register(
		"potion_contents", builder -> builder.codec(PotionContentsComponent.CODEC).packetCodec(PotionContentsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<SuspiciousStewEffectsComponent> SUSPICIOUS_STEW_EFFECTS = register(
		"suspicious_stew_effects", builder -> builder.codec(SuspiciousStewEffectsComponent.CODEC).packetCodec(SuspiciousStewEffectsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<WritableBookContentComponent> WRITABLE_BOOK_CONTENT = register(
		"writable_book_content", builder -> builder.codec(WritableBookContentComponent.CODEC).packetCodec(WritableBookContentComponent.PACKET_CODEC)
	);
	public static final DataComponentType<WrittenBookContentComponent> WRITTEN_BOOK_CONTENT = register(
		"written_book_content", builder -> builder.codec(WrittenBookContentComponent.CODEC).packetCodec(WrittenBookContentComponent.PACKET_CODEC)
	);
	public static final DataComponentType<ArmorTrim> TRIM = register("trim", builder -> builder.codec(ArmorTrim.CODEC).packetCodec(ArmorTrim.PACKET_CODEC));
	public static final DataComponentType<DebugStickStateComponent> DEBUG_STICK_STATE = register(
		"debug_stick_state", builder -> builder.codec(DebugStickStateComponent.CODEC)
	);
	public static final DataComponentType<NbtComponent> ENTITY_DATA = register(
		"entity_data", builder -> builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final DataComponentType<FletchingTableBlockEntity.FletchingComponent> FLETCHING = register(
		"fletching",
		builder -> builder.codec(FletchingTableBlockEntity.FletchingComponent.CODEC).packetCodec(FletchingTableBlockEntity.FletchingComponent.PACKET_CODEC)
	);
	public static final DataComponentType<FletchingTableBlockEntity.ResinComponent> RESIN = register(
		"resin", builder -> builder.codec(FletchingTableBlockEntity.ResinComponent.CODEC).packetCodec(FletchingTableBlockEntity.ResinComponent.PACKET_CODEC)
	);
	public static final DataComponentType<NbtComponent> BUCKET_ENTITY_DATA = register(
		"bucket_entity_data", builder -> builder.codec(NbtComponent.CODEC).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final DataComponentType<NbtComponent> BLOCK_ENTITY_DATA = register(
		"block_entity_data", builder -> builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final DataComponentType<RegistryEntry<Instrument>> INSTRUMENT = register(
		"instrument", builder -> builder.codec(Instrument.ENTRY_CODEC).packetCodec(Instrument.ENTRY_PACKET_CODEC)
	);
	public static final DataComponentType<List<Identifier>> RECIPES = register("recipes", builder -> builder.codec(Identifier.CODEC.listOf()));
	public static final DataComponentType<LodestoneTrackerComponent> LODESTONE_TRACKER = register(
		"lodestone_tracker", builder -> builder.codec(LodestoneTrackerComponent.CODEC).packetCodec(LodestoneTrackerComponent.PACKET_CODEC)
	);
	public static final DataComponentType<FireworkExplosionComponent> FIREWORK_EXPLOSION = register(
		"firework_explosion", builder -> builder.codec(FireworkExplosionComponent.CODEC).packetCodec(FireworkExplosionComponent.PACKET_CODEC)
	);
	public static final DataComponentType<FireworksComponent> FIREWORKS = register(
		"fireworks", builder -> builder.codec(FireworksComponent.CODEC).packetCodec(FireworksComponent.PACKET_CODEC)
	);
	public static final DataComponentType<ProfileComponent> PROFILE = register(
		"profile", builder -> builder.codec(ProfileComponent.CODEC).packetCodec(ProfileComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Identifier> NOTE_BLOCK_SOUND = register(
		"note_block_sound", builder -> builder.codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC)
	);
	public static final DataComponentType<BannerPatternsComponent> BANNER_PATTERNS = register(
		"banner_patterns", builder -> builder.codec(BannerPatternsComponent.CODEC).packetCodec(BannerPatternsComponent.PACKET_CODEC)
	);
	public static final DataComponentType<DyeColor> BASE_COLOR = register(
		"base_color", builder -> builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC)
	);
	public static final DataComponentType<Sherds> POT_DECORATIONS = register(
		"pot_decorations", builder -> builder.codec(Sherds.CODEC).packetCodec(Sherds.PACKET_CODEC)
	);
	public static final DataComponentType<ContainerComponent> CONTAINER = register(
		"container", builder -> builder.codec(ContainerComponent.CODEC).packetCodec(ContainerComponent.PACKET_CODEC)
	);
	public static final DataComponentType<BlockStateComponent> BLOCK_STATE = register(
		"block_state", builder -> builder.codec(BlockStateComponent.CODEC).packetCodec(BlockStateComponent.PACKET_CODEC)
	);
	public static final DataComponentType<List<BeehiveBlockEntity.BeeData>> BEES = register(
		"bees", builder -> builder.codec(BeehiveBlockEntity.BeeData.LIST_CODEC).packetCodec(BeehiveBlockEntity.BeeData.PACKET_CODEC.collect(PacketCodecs.toList()))
	);
	public static final DataComponentType<ContainerLock> LOCK = register("lock", builder -> builder.codec(ContainerLock.CODEC));
	public static final DataComponentType<ContainerLootComponent> CONTAINER_LOOT = register(
		"container_loot", builder -> builder.codec(ContainerLootComponent.CODEC)
	);
	public static final DataComponentType<XpComponent> XP = register("xp", builder -> builder.codec(XpComponent.CODEC).packetCodec(XpComponent.PACKET_CODEC));
	public static final DataComponentType<SnekComponent> SNEK = register(
		"snek", builder -> builder.codec(SnekComponent.CODEC).packetCodec(SnekComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Boolean> HOVERED = register("hovered", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
	public static final DataComponentType<Integer> CLICKS = register("clicks", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER));
	public static final DataComponentType<Integer> VIEWS = register("views", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER));
	public static final DataComponentType<Integer> UNDERCOVER_ID = register("undercover_id", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER));
	public static final DataComponentType<Int2IntMap> CONTACTS_MESSAGES = register(
		"contacts_messages", builder -> builder.codec(Codecs.method_58804()).packetCodec(PacketCodecs.codec(Codecs.method_58804()))
	);
	public static final DataComponentType<IntIntPair> SECRET_MESSAGE = register(
		"secret_message", builder -> builder.codec(Codecs.method_58811()).packetCodec(PacketCodecs.codec(Codecs.method_58811()))
	);
	public static final DataComponentType<LubricationComponent> LUBRICATION = register(
		"lubrication", builder -> builder.codec(LubricationComponent.CODEC).packetCodec(LubricationComponent.PACKET_CODEC)
	);
	public static final DataComponentType<Boolean> EXPLICIT_FOIL = register("explicit_foil", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
	public static final DataComponentType<HeatComponent> HEAT = register(
		"heat", builder -> builder.codec(HeatComponent.CODEC).packetCodec(HeatComponent.PACKET_CODEC)
	);
	public static final DataComponentType<PotatoBaneComponent> POTATO_BANE = register(
		"potato_bane", builder -> builder.codec(PotatoBaneComponent.CODEC).packetCodec(PotatoBaneComponent.PACKET_CODEC)
	);
	public static final ComponentMap DEFAULT_ITEM_COMPONENTS = ComponentMap.builder()
		.add(MAX_STACK_SIZE, 64)
		.add(LORE, LoreComponent.DEFAULT)
		.add(ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
		.add(REPAIR_COST, 0)
		.add(ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT)
		.add(RARITY, Rarity.COMMON)
		.build();

	public static DataComponentType<?> getDefault(Registry<DataComponentType<?>> registry) {
		return CUSTOM_DATA;
	}

	private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((DataComponentType.Builder)builderOperator.apply(DataComponentType.builder())).build());
	}
}
