package net.minecraft.component;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.block.entity.BeehiveBlockEntity;
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
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
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
import net.minecraft.util.dynamic.CodecCache;
import net.minecraft.util.dynamic.Codecs;

public class DataComponentTypes {
	static final CodecCache CACHE = new CodecCache(512);
	public static final ComponentType<NbtComponent> CUSTOM_DATA = register("custom_data", builder -> builder.codec(NbtComponent.CODEC));
	public static final ComponentType<Integer> MAX_STACK_SIZE = register(
		"max_stack_size", builder -> builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final ComponentType<Integer> MAX_DAMAGE = register("max_damage", builder -> builder.codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT));
	public static final ComponentType<Integer> DAMAGE = register("damage", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
	public static final ComponentType<UnbreakableComponent> UNBREAKABLE = register(
		"unbreakable", builder -> builder.codec(UnbreakableComponent.CODEC).packetCodec(UnbreakableComponent.PACKET_CODEC)
	);
	public static final ComponentType<Text> CUSTOM_NAME = register(
		"custom_name", builder -> builder.codec(TextCodecs.STRINGIFIED_CODEC).packetCodec(TextCodecs.REGISTRY_PACKET_CODEC).cache()
	);
	public static final ComponentType<Text> ITEM_NAME = register(
		"item_name", builder -> builder.codec(TextCodecs.STRINGIFIED_CODEC).packetCodec(TextCodecs.REGISTRY_PACKET_CODEC).cache()
	);
	public static final ComponentType<LoreComponent> LORE = register(
		"lore", builder -> builder.codec(LoreComponent.CODEC).packetCodec(LoreComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<Rarity> RARITY = register("rarity", builder -> builder.codec(Rarity.CODEC).packetCodec(Rarity.PACKET_CODEC));
	public static final ComponentType<ItemEnchantmentsComponent> ENCHANTMENTS = register(
		"enchantments", builder -> builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<BlockPredicatesChecker> CAN_PLACE_ON = register(
		"can_place_on", builder -> builder.codec(BlockPredicatesChecker.CODEC).packetCodec(BlockPredicatesChecker.PACKET_CODEC).cache()
	);
	public static final ComponentType<BlockPredicatesChecker> CAN_BREAK = register(
		"can_break", builder -> builder.codec(BlockPredicatesChecker.CODEC).packetCodec(BlockPredicatesChecker.PACKET_CODEC).cache()
	);
	public static final ComponentType<AttributeModifiersComponent> ATTRIBUTE_MODIFIERS = register(
		"attribute_modifiers", builder -> builder.codec(AttributeModifiersComponent.CODEC).packetCodec(AttributeModifiersComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = register(
		"custom_model_data", builder -> builder.codec(CustomModelDataComponent.CODEC).packetCodec(CustomModelDataComponent.PACKET_CODEC)
	);
	public static final ComponentType<Unit> HIDE_ADDITIONAL_TOOLTIP = register(
		"hide_additional_tooltip", builder -> builder.codec(Unit.CODEC).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final ComponentType<Unit> HIDE_TOOLTIP = register(
		"hide_tooltip", builder -> builder.codec(Codec.unit(Unit.INSTANCE)).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final ComponentType<Integer> REPAIR_COST = register(
		"repair_cost", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final ComponentType<Unit> CREATIVE_SLOT_LOCK = register("creative_slot_lock", builder -> builder.packetCodec(PacketCodec.unit(Unit.INSTANCE)));
	public static final ComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE = register(
		"enchantment_glint_override", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
	);
	public static final ComponentType<Unit> INTANGIBLE_PROJECTILE = register("intangible_projectile", builder -> builder.codec(Unit.CODEC));
	public static final ComponentType<FoodComponent> FOOD = register(
		"food", builder -> builder.codec(FoodComponent.CODEC).packetCodec(FoodComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<Unit> FIRE_RESISTANT = register(
		"fire_resistant", builder -> builder.codec(Unit.CODEC).packetCodec(PacketCodec.unit(Unit.INSTANCE))
	);
	public static final ComponentType<ToolComponent> TOOL = register(
		"tool", builder -> builder.codec(ToolComponent.CODEC).packetCodec(ToolComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<ItemEnchantmentsComponent> STORED_ENCHANTMENTS = register(
		"stored_enchantments", builder -> builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<DyedColorComponent> DYED_COLOR = register(
		"dyed_color", builder -> builder.codec(DyedColorComponent.CODEC).packetCodec(DyedColorComponent.PACKET_CODEC)
	);
	public static final ComponentType<MapColorComponent> MAP_COLOR = register(
		"map_color", builder -> builder.codec(MapColorComponent.CODEC).packetCodec(MapColorComponent.PACKET_CODEC)
	);
	public static final ComponentType<MapIdComponent> MAP_ID = register(
		"map_id", builder -> builder.codec(MapIdComponent.CODEC).packetCodec(MapIdComponent.PACKET_CODEC)
	);
	public static final ComponentType<MapDecorationsComponent> MAP_DECORATIONS = register(
		"map_decorations", builder -> builder.codec(MapDecorationsComponent.CODEC).cache()
	);
	public static final ComponentType<MapPostProcessingComponent> MAP_POST_PROCESSING = register(
		"map_post_processing", builder -> builder.packetCodec(MapPostProcessingComponent.PACKET_CODEC)
	);
	public static final ComponentType<ChargedProjectilesComponent> CHARGED_PROJECTILES = register(
		"charged_projectiles", builder -> builder.codec(ChargedProjectilesComponent.CODEC).packetCodec(ChargedProjectilesComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<BundleContentsComponent> BUNDLE_CONTENTS = register(
		"bundle_contents", builder -> builder.codec(BundleContentsComponent.CODEC).packetCodec(BundleContentsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<PotionContentsComponent> POTION_CONTENTS = register(
		"potion_contents", builder -> builder.codec(PotionContentsComponent.CODEC).packetCodec(PotionContentsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<SuspiciousStewEffectsComponent> SUSPICIOUS_STEW_EFFECTS = register(
		"suspicious_stew_effects", builder -> builder.codec(SuspiciousStewEffectsComponent.CODEC).packetCodec(SuspiciousStewEffectsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<WritableBookContentComponent> WRITABLE_BOOK_CONTENT = register(
		"writable_book_content", builder -> builder.codec(WritableBookContentComponent.CODEC).packetCodec(WritableBookContentComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<WrittenBookContentComponent> WRITTEN_BOOK_CONTENT = register(
		"written_book_content", builder -> builder.codec(WrittenBookContentComponent.CODEC).packetCodec(WrittenBookContentComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<ArmorTrim> TRIM = register("trim", builder -> builder.codec(ArmorTrim.CODEC).packetCodec(ArmorTrim.PACKET_CODEC).cache());
	public static final ComponentType<DebugStickStateComponent> DEBUG_STICK_STATE = register(
		"debug_stick_state", builder -> builder.codec(DebugStickStateComponent.CODEC).cache()
	);
	public static final ComponentType<NbtComponent> ENTITY_DATA = register(
		"entity_data", builder -> builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final ComponentType<NbtComponent> BUCKET_ENTITY_DATA = register(
		"bucket_entity_data", builder -> builder.codec(NbtComponent.CODEC).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final ComponentType<NbtComponent> BLOCK_ENTITY_DATA = register(
		"block_entity_data", builder -> builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC)
	);
	public static final ComponentType<RegistryEntry<Instrument>> INSTRUMENT = register(
		"instrument", builder -> builder.codec(Instrument.ENTRY_CODEC).packetCodec(Instrument.ENTRY_PACKET_CODEC).cache()
	);
	public static final ComponentType<Integer> OMINOUS_BOTTLE_AMPLIFIER = register(
		"ominous_bottle_amplifier", builder -> builder.codec(Codecs.rangedInt(0, 4)).packetCodec(PacketCodecs.VAR_INT)
	);
	public static final ComponentType<JukeboxPlayableComponent> JUKEBOX_PLAYABLE = register(
		"jukebox_playable", builder -> builder.codec(JukeboxPlayableComponent.CODEC).packetCodec(JukeboxPlayableComponent.PACKET_CODEC)
	);
	public static final ComponentType<List<Identifier>> RECIPES = register("recipes", builder -> builder.codec(Identifier.CODEC.listOf()).cache());
	public static final ComponentType<LodestoneTrackerComponent> LODESTONE_TRACKER = register(
		"lodestone_tracker", builder -> builder.codec(LodestoneTrackerComponent.CODEC).packetCodec(LodestoneTrackerComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<FireworkExplosionComponent> FIREWORK_EXPLOSION = register(
		"firework_explosion", builder -> builder.codec(FireworkExplosionComponent.CODEC).packetCodec(FireworkExplosionComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<FireworksComponent> FIREWORKS = register(
		"fireworks", builder -> builder.codec(FireworksComponent.CODEC).packetCodec(FireworksComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<ProfileComponent> PROFILE = register(
		"profile", builder -> builder.codec(ProfileComponent.CODEC).packetCodec(ProfileComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<Identifier> NOTE_BLOCK_SOUND = register(
		"note_block_sound", builder -> builder.codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC)
	);
	public static final ComponentType<BannerPatternsComponent> BANNER_PATTERNS = register(
		"banner_patterns", builder -> builder.codec(BannerPatternsComponent.CODEC).packetCodec(BannerPatternsComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<DyeColor> BASE_COLOR = register("base_color", builder -> builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC));
	public static final ComponentType<Sherds> POT_DECORATIONS = register(
		"pot_decorations", builder -> builder.codec(Sherds.CODEC).packetCodec(Sherds.PACKET_CODEC).cache()
	);
	public static final ComponentType<ContainerComponent> CONTAINER = register(
		"container", builder -> builder.codec(ContainerComponent.CODEC).packetCodec(ContainerComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<BlockStateComponent> BLOCK_STATE = register(
		"block_state", builder -> builder.codec(BlockStateComponent.CODEC).packetCodec(BlockStateComponent.PACKET_CODEC).cache()
	);
	public static final ComponentType<List<BeehiveBlockEntity.BeeData>> BEES = register(
		"bees",
		builder -> builder.codec(BeehiveBlockEntity.BeeData.LIST_CODEC).packetCodec(BeehiveBlockEntity.BeeData.PACKET_CODEC.collect(PacketCodecs.toList())).cache()
	);
	public static final ComponentType<ContainerLock> LOCK = register("lock", builder -> builder.codec(ContainerLock.CODEC));
	public static final ComponentType<ContainerLootComponent> CONTAINER_LOOT = register("container_loot", builder -> builder.codec(ContainerLootComponent.CODEC));
	public static final ComponentMap DEFAULT_ITEM_COMPONENTS = ComponentMap.builder()
		.add(MAX_STACK_SIZE, 64)
		.add(LORE, LoreComponent.DEFAULT)
		.add(ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
		.add(REPAIR_COST, 0)
		.add(ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT)
		.add(RARITY, Rarity.COMMON)
		.build();

	public static ComponentType<?> getDefault(Registry<ComponentType<?>> registry) {
		return CUSTOM_DATA;
	}

	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
	}
}
