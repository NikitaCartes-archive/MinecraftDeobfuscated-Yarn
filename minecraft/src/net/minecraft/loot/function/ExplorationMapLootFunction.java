package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<?> field_25032 = StructureFeature.BURIED_TREASURE;
	public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.MANSION;
	private final StructureFeature<?> destination;
	private final MapIcon.Type decoration;
	private final byte zoom;
	private final int searchRadius;
	private final boolean skipExistingChunks;

	private ExplorationMapLootFunction(
		LootCondition[] conditions, StructureFeature<?> structureFeature, MapIcon.Type decoration, byte zoom, int searchRadius, boolean skipExistingChunks
	) {
		super(conditions);
		this.destination = structureFeature;
		this.decoration = decoration;
		this.zoom = zoom;
		this.searchRadius = searchRadius;
		this.skipExistingChunks = skipExistingChunks;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.EXPLORATION_MAP;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.ORIGIN);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.getItem() != Items.MAP) {
			return stack;
		} else {
			Vec3d vec3d = context.get(LootContextParameters.ORIGIN);
			if (vec3d != null) {
				ServerWorld serverWorld = context.getWorld();
				BlockPos blockPos = serverWorld.locateStructure(this.destination, new BlockPos(vec3d), this.searchRadius, this.skipExistingChunks);
				if (blockPos != null) {
					ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), this.zoom, true, true);
					FilledMapItem.fillExplorationMap(serverWorld, itemStack);
					MapState.addDecorationsTag(itemStack, blockPos, "+", this.decoration);
					itemStack.setCustomName(new TranslatableText("filled_map." + this.destination.getName().toLowerCase(Locale.ROOT)));
					return itemStack;
				}
			}

			return stack;
		}
	}

	public static ExplorationMapLootFunction.Builder create() {
		return new ExplorationMapLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<ExplorationMapLootFunction.Builder> {
		private StructureFeature<?> destination = ExplorationMapLootFunction.field_25032;
		private MapIcon.Type decoration = ExplorationMapLootFunction.DEFAULT_DECORATION;
		private byte zoom = 2;
		private int searchRadius = 50;
		private boolean skipExistingChunks = true;

		protected ExplorationMapLootFunction.Builder getThisBuilder() {
			return this;
		}

		public ExplorationMapLootFunction.Builder withDestination(StructureFeature<?> structureFeature) {
			this.destination = structureFeature;
			return this;
		}

		public ExplorationMapLootFunction.Builder withDecoration(MapIcon.Type decoration) {
			this.decoration = decoration;
			return this;
		}

		public ExplorationMapLootFunction.Builder withZoom(byte zoom) {
			this.zoom = zoom;
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

	public static class Serializer extends ConditionalLootFunction.Serializer<ExplorationMapLootFunction> {
		public void toJson(JsonObject jsonObject, ExplorationMapLootFunction explorationMapLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, explorationMapLootFunction, jsonSerializationContext);
			if (!explorationMapLootFunction.destination.equals(ExplorationMapLootFunction.field_25032)) {
				jsonObject.add("destination", jsonSerializationContext.serialize(explorationMapLootFunction.destination.getName()));
			}

			if (explorationMapLootFunction.decoration != ExplorationMapLootFunction.DEFAULT_DECORATION) {
				jsonObject.add("decoration", jsonSerializationContext.serialize(explorationMapLootFunction.decoration.toString().toLowerCase(Locale.ROOT)));
			}

			if (explorationMapLootFunction.zoom != 2) {
				jsonObject.addProperty("zoom", explorationMapLootFunction.zoom);
			}

			if (explorationMapLootFunction.searchRadius != 50) {
				jsonObject.addProperty("search_radius", explorationMapLootFunction.searchRadius);
			}

			if (!explorationMapLootFunction.skipExistingChunks) {
				jsonObject.addProperty("skip_existing_chunks", explorationMapLootFunction.skipExistingChunks);
			}
		}

		public ExplorationMapLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			StructureFeature<?> structureFeature = method_29039(jsonObject);
			String string = jsonObject.has("decoration") ? JsonHelper.getString(jsonObject, "decoration") : "mansion";
			MapIcon.Type type = ExplorationMapLootFunction.DEFAULT_DECORATION;

			try {
				type = MapIcon.Type.valueOf(string.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException var10) {
				ExplorationMapLootFunction.LOGGER
					.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMapLootFunction.DEFAULT_DECORATION, string);
			}

			byte b = JsonHelper.getByte(jsonObject, "zoom", (byte)2);
			int i = JsonHelper.getInt(jsonObject, "search_radius", 50);
			boolean bl = JsonHelper.getBoolean(jsonObject, "skip_existing_chunks", true);
			return new ExplorationMapLootFunction(lootConditions, structureFeature, type, b, i, bl);
		}

		private static StructureFeature<?> method_29039(JsonObject jsonObject) {
			if (jsonObject.has("destination")) {
				String string = JsonHelper.getString(jsonObject, "destination");
				StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
				if (structureFeature != null) {
					return structureFeature;
				}
			}

			return ExplorationMapLootFunction.field_25032;
		}
	}
}
