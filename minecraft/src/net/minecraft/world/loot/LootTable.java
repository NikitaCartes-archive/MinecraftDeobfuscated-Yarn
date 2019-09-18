package net.minecraft.world.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final LootTable EMPTY = new LootTable(LootContextTypes.EMPTY, new LootPool[0], new LootFunction[0]);
	public static final LootContextType GENERIC = LootContextTypes.GENERIC;
	private final LootContextType type;
	private final LootPool[] pools;
	private final LootFunction[] functions;
	private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunction;

	private LootTable(LootContextType lootContextType, LootPool[] lootPools, LootFunction[] lootFunctions) {
		this.type = lootContextType;
		this.pools = lootPools;
		this.functions = lootFunctions;
		this.combinedFunction = LootFunctions.join(lootFunctions);
	}

	public static Consumer<ItemStack> limitedConsumer(Consumer<ItemStack> consumer) {
		return itemStack -> {
			if (itemStack.getCount() < itemStack.getMaxCount()) {
				consumer.accept(itemStack);
			} else {
				int i = itemStack.getCount();

				while (i > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(Math.min(itemStack.getMaxCount(), i));
					i -= itemStack2.getCount();
					consumer.accept(itemStack2);
				}
			}
		};
	}

	public void drop(LootContext lootContext, Consumer<ItemStack> consumer) {
		if (lootContext.addDrop(this)) {
			Consumer<ItemStack> consumer2 = LootFunction.apply(this.combinedFunction, consumer, lootContext);

			for (LootPool lootPool : this.pools) {
				lootPool.drop(consumer2, lootContext);
			}

			lootContext.removeDrop(this);
		} else {
			LOGGER.warn("Detected infinite loop in loot tables");
		}
	}

	public void dropLimited(LootContext lootContext, Consumer<ItemStack> consumer) {
		this.drop(lootContext, limitedConsumer(consumer));
	}

	public List<ItemStack> getDrops(LootContext lootContext) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		this.dropLimited(lootContext, list::add);
		return list;
	}

	public LootContextType getType() {
		return this.type;
	}

	public void check(LootTableReporter lootTableReporter) {
		for (int i = 0; i < this.pools.length; i++) {
			this.pools[i].check(lootTableReporter.makeChild(".pools[" + i + "]"));
		}

		for (int i = 0; i < this.functions.length; i++) {
			this.functions[i].check(lootTableReporter.makeChild(".functions[" + i + "]"));
		}
	}

	public void supplyInventory(Inventory inventory, LootContext lootContext) {
		List<ItemStack> list = this.getDrops(lootContext);
		Random random = lootContext.getRandom();
		List<Integer> list2 = this.getFreeSlots(inventory, random);
		this.shuffle(list, list2.size(), random);

		for (ItemStack itemStack : list) {
			if (list2.isEmpty()) {
				LOGGER.warn("Tried to over-fill a container");
				return;
			}

			if (itemStack.isEmpty()) {
				inventory.setInvStack((Integer)list2.remove(list2.size() - 1), ItemStack.EMPTY);
			} else {
				inventory.setInvStack((Integer)list2.remove(list2.size() - 1), itemStack);
			}
		}
	}

	private void shuffle(List<ItemStack> list, int i, Random random) {
		List<ItemStack> list2 = Lists.<ItemStack>newArrayList();
		Iterator<ItemStack> iterator = list.iterator();

		while (iterator.hasNext()) {
			ItemStack itemStack = (ItemStack)iterator.next();
			if (itemStack.isEmpty()) {
				iterator.remove();
			} else if (itemStack.getCount() > 1) {
				list2.add(itemStack);
				iterator.remove();
			}
		}

		while (i - list.size() - list2.size() > 0 && !list2.isEmpty()) {
			ItemStack itemStack2 = (ItemStack)list2.remove(MathHelper.nextInt(random, 0, list2.size() - 1));
			int j = MathHelper.nextInt(random, 1, itemStack2.getCount() / 2);
			ItemStack itemStack3 = itemStack2.split(j);
			if (itemStack2.getCount() > 1 && random.nextBoolean()) {
				list2.add(itemStack2);
			} else {
				list.add(itemStack2);
			}

			if (itemStack3.getCount() > 1 && random.nextBoolean()) {
				list2.add(itemStack3);
			} else {
				list.add(itemStack3);
			}
		}

		list.addAll(list2);
		Collections.shuffle(list, random);
	}

	private List<Integer> getFreeSlots(Inventory inventory, Random random) {
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			if (inventory.getInvStack(i).isEmpty()) {
				list.add(i);
			}
		}

		Collections.shuffle(list, random);
		return list;
	}

	public static LootTable.Builder builder() {
		return new LootTable.Builder();
	}

	public static class Builder implements LootFunctionConsumingBuilder<LootTable.Builder> {
		private final List<LootPool> pools = Lists.<LootPool>newArrayList();
		private final List<LootFunction> functions = Lists.<LootFunction>newArrayList();
		private LootContextType type = LootTable.GENERIC;

		public LootTable.Builder withPool(LootPool.Builder builder) {
			this.pools.add(builder.build());
			return this;
		}

		public LootTable.Builder withType(LootContextType lootContextType) {
			this.type = lootContextType;
			return this;
		}

		public LootTable.Builder method_335(LootFunction.Builder builder) {
			this.functions.add(builder.build());
			return this;
		}

		public LootTable.Builder method_337() {
			return this;
		}

		public LootTable create() {
			return new LootTable(this.type, (LootPool[])this.pools.toArray(new LootPool[0]), (LootFunction[])this.functions.toArray(new LootFunction[0]));
		}
	}

	public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
		public LootTable method_340(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot table");
			LootPool[] lootPools = JsonHelper.deserialize(jsonObject, "pools", new LootPool[0], jsonDeserializationContext, LootPool[].class);
			LootContextType lootContextType = null;
			if (jsonObject.has("type")) {
				String string = JsonHelper.getString(jsonObject, "type");
				lootContextType = LootContextTypes.get(new Identifier(string));
			}

			LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
			return new LootTable(lootContextType != null ? lootContextType : LootContextTypes.GENERIC, lootPools, lootFunctions);
		}

		public JsonElement method_339(LootTable lootTable, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (lootTable.type != LootTable.GENERIC) {
				Identifier identifier = LootContextTypes.getId(lootTable.type);
				if (identifier != null) {
					jsonObject.addProperty("type", identifier.toString());
				} else {
					LootTable.LOGGER.warn("Failed to find id for param set " + lootTable.type);
				}
			}

			if (lootTable.pools.length > 0) {
				jsonObject.add("pools", jsonSerializationContext.serialize(lootTable.pools));
			}

			if (!ArrayUtils.isEmpty((Object[])lootTable.functions)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(lootTable.functions));
			}

			return jsonObject;
		}
	}
}
