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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.MANSION;
	private final String destination;
	private final MapIcon.Type decoration;
	private final byte zoom;
	private final int searchRadius;
	private final boolean skipExistingChunks;

	private ExplorationMapLootFunction(
		LootCondition[] conditions, String destination, MapIcon.Type decoration, byte zoom, int searchRadius, boolean skipExistingChunks
	) {
		super(conditions);
		this.destination = destination;
		this.decoration = decoration;
		this.zoom = zoom;
		this.searchRadius = searchRadius;
		this.skipExistingChunks = skipExistingChunks;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.POSITION);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.getItem() != Items.MAP) {
			return stack;
		} else {
			BlockPos blockPos = context.get(LootContextParameters.POSITION);
			if (blockPos != null) {
				ServerWorld serverWorld = context.getWorld();
				BlockPos blockPos2 = serverWorld.locateStructure(this.destination, blockPos, this.searchRadius, this.skipExistingChunks);
				if (blockPos2 != null) {
					ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos2.getX(), blockPos2.getZ(), this.zoom, true, true);
					FilledMapItem.fillExplorationMap(serverWorld, itemStack);
					MapState.addDecorationsTag(itemStack, blockPos2, "+", this.decoration);
					itemStack.setCustomName(new TranslatableText("filled_map." + this.destination.toLowerCase(Locale.ROOT)));
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
		private String destination = "Buried_Treasure";
		private MapIcon.Type decoration = ExplorationMapLootFunction.DEFAULT_DECORATION;
		private byte zoom = 2;
		private int searchRadius = 50;
		private boolean skipExistingChunks = true;

		protected ExplorationMapLootFunction.Builder getThisBuilder() {
			return this;
		}

		public ExplorationMapLootFunction.Builder withDestination(String destination) {
			this.destination = destination;
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

	public static class Factory extends ConditionalLootFunction.Factory<ExplorationMapLootFunction> {
		protected Factory() {
			super(new Identifier("exploration_map"), ExplorationMapLootFunction.class);
		}

		public void toJson(JsonObject jsonObject, ExplorationMapLootFunction explorationMapLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, explorationMapLootFunction, jsonSerializationContext);
			if (!explorationMapLootFunction.destination.equals("Buried_Treasure")) {
				jsonObject.add("destination", jsonSerializationContext.serialize(explorationMapLootFunction.destination));
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
			String string = jsonObject.has("destination") ? JsonHelper.getString(jsonObject, "destination") : "Buried_Treasure";
			string = Feature.STRUCTURES.containsKey(string.toLowerCase(Locale.ROOT)) ? string : "Buried_Treasure";
			String string2 = jsonObject.has("decoration") ? JsonHelper.getString(jsonObject, "decoration") : "mansion";
			MapIcon.Type type = ExplorationMapLootFunction.DEFAULT_DECORATION;

			try {
				type = MapIcon.Type.valueOf(string2.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException var10) {
				ExplorationMapLootFunction.LOGGER
					.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMapLootFunction.DEFAULT_DECORATION, string2);
			}

			byte b = JsonHelper.getByte(jsonObject, "zoom", (byte)2);
			int i = JsonHelper.getInt(jsonObject, "search_radius", 50);
			boolean bl = JsonHelper.getBoolean(jsonObject, "skip_existing_chunks", true);
			return new ExplorationMapLootFunction(lootConditions, string, type, b, i, bl);
		}
	}
}
