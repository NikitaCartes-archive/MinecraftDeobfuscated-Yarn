package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.structure.Structure;

public class ExplorationMapLootFunction extends ConditionalLootFunction {
	public static final TagKey<Structure> DEFAULT_DESTINATION = StructureTags.ON_TREASURE_MAPS;
	public static final RegistryEntry<MapDecorationType> DEFAULT_DECORATION = MapDecorationTypes.MANSION;
	public static final byte DEFAULT_ZOOM = 2;
	public static final int DEFAULT_SEARCH_RADIUS = 50;
	public static final boolean DEFAULT_SKIP_EXISTING_CHUNKS = true;
	public static final MapCodec<ExplorationMapLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<TagKey<Structure>, RegistryEntry<MapDecorationType>, byte, int, boolean>and(
					instance.group(
						TagKey.unprefixedCodec(RegistryKeys.STRUCTURE).optionalFieldOf("destination", DEFAULT_DESTINATION).forGetter(function -> function.destination),
						MapDecorationType.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION).forGetter(function -> function.decoration),
						Codec.BYTE.optionalFieldOf("zoom", Byte.valueOf((byte)2)).forGetter(function -> function.zoom),
						Codec.INT.optionalFieldOf("search_radius", Integer.valueOf(50)).forGetter(function -> function.searchRadius),
						Codec.BOOL.optionalFieldOf("skip_existing_chunks", Boolean.valueOf(true)).forGetter(function -> function.skipExistingChunks)
					)
				)
				.apply(instance, ExplorationMapLootFunction::new)
	);
	private final TagKey<Structure> destination;
	private final RegistryEntry<MapDecorationType> decoration;
	private final byte zoom;
	private final int searchRadius;
	private final boolean skipExistingChunks;

	ExplorationMapLootFunction(
		List<LootCondition> conditions,
		TagKey<Structure> destination,
		RegistryEntry<MapDecorationType> decoration,
		byte zoom,
		int searchRadius,
		boolean skipExistingChunks
	) {
		super(conditions);
		this.destination = destination;
		this.decoration = decoration;
		this.zoom = zoom;
		this.searchRadius = searchRadius;
		this.skipExistingChunks = skipExistingChunks;
	}

	@Override
	public LootFunctionType<ExplorationMapLootFunction> getType() {
		return LootFunctionTypes.EXPLORATION_MAP;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.ORIGIN);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (!stack.isOf(Items.MAP)) {
			return stack;
		} else {
			Vec3d vec3d = context.get(LootContextParameters.ORIGIN);
			if (vec3d != null) {
				ServerWorld serverWorld = context.getWorld();
				BlockPos blockPos = serverWorld.locateStructure(this.destination, BlockPos.ofFloored(vec3d), this.searchRadius, this.skipExistingChunks);
				if (blockPos != null) {
					ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), this.zoom, true, true);
					FilledMapItem.fillExplorationMap(serverWorld, itemStack);
					MapState.addDecorationsNbt(itemStack, blockPos, "+", this.decoration);
					return itemStack;
				}
			}

			return stack;
		}
	}

	public static ExplorationMapLootFunction.Builder builder() {
		return new ExplorationMapLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<ExplorationMapLootFunction.Builder> {
		private TagKey<Structure> destination = ExplorationMapLootFunction.DEFAULT_DESTINATION;
		private RegistryEntry<MapDecorationType> decoration = ExplorationMapLootFunction.DEFAULT_DECORATION;
		private byte zoom = 2;
		private int searchRadius = 50;
		private boolean skipExistingChunks = true;

		protected ExplorationMapLootFunction.Builder getThisBuilder() {
			return this;
		}

		public ExplorationMapLootFunction.Builder withDestination(TagKey<Structure> destination) {
			this.destination = destination;
			return this;
		}

		public ExplorationMapLootFunction.Builder withDecoration(RegistryEntry<MapDecorationType> decoration) {
			this.decoration = decoration;
			return this;
		}

		public ExplorationMapLootFunction.Builder withZoom(byte zoom) {
			this.zoom = zoom;
			return this;
		}

		public ExplorationMapLootFunction.Builder searchRadius(int searchRadius) {
			this.searchRadius = searchRadius;
			return this;
		}

		public ExplorationMapLootFunction.Builder withSkipExistingChunks(boolean skipExistingChunks) {
			this.skipExistingChunks = skipExistingChunks;
			return this;
		}

		@Override
		public LootFunction build() {
			return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks);
		}
	}
}
