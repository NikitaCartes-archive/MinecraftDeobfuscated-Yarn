package net.minecraft.world.loot.function;

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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.field_88;
	private final String destination;
	private final MapIcon.Type decoration;
	private final byte zoom;
	private final int searchRadius;
	private final boolean skipExistingChunks;

	private ExplorationMapLootFunction(LootCondition[] lootConditions, String string, MapIcon.Type type, byte b, int i, boolean bl) {
		super(lootConditions);
		this.destination = string;
		this.decoration = type;
		this.zoom = b;
		this.searchRadius = i;
		this.skipExistingChunks = bl;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1232);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.getItem() != Items.field_8895) {
			return itemStack;
		} else {
			BlockPos blockPos = lootContext.get(LootContextParameters.field_1232);
			if (blockPos != null) {
				ServerWorld serverWorld = lootContext.getWorld();
				BlockPos blockPos2 = serverWorld.locateStructure(this.destination, blockPos, this.searchRadius, this.skipExistingChunks);
				if (blockPos2 != null) {
					ItemStack itemStack2 = FilledMapItem.createMap(serverWorld, blockPos2.getX(), blockPos2.getZ(), this.zoom, true, true);
					FilledMapItem.fillExplorationMap(serverWorld, itemStack2);
					MapState.addDecorationsTag(itemStack2, blockPos2, "+", this.decoration);
					itemStack2.setCustomName(new TranslatableText("filled_map." + this.destination.toLowerCase(Locale.ROOT)));
					return itemStack2;
				}
			}

			return itemStack;
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

		protected ExplorationMapLootFunction.Builder method_501() {
			return this;
		}

		public ExplorationMapLootFunction.Builder withDestination(String string) {
			this.destination = string;
			return this;
		}

		public ExplorationMapLootFunction.Builder withDecoration(MapIcon.Type type) {
			this.decoration = type;
			return this;
		}

		public ExplorationMapLootFunction.Builder withZoom(byte b) {
			this.zoom = b;
			return this;
		}

		public ExplorationMapLootFunction.Builder withSkipExistingChunks(boolean bl) {
			this.skipExistingChunks = bl;
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

		public void method_505(JsonObject jsonObject, ExplorationMapLootFunction explorationMapLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, explorationMapLootFunction, jsonSerializationContext);
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

		public ExplorationMapLootFunction method_504(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
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
