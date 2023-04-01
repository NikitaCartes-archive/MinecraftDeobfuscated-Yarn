package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemModels {
	public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap<>(256);
	private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap<>(256);
	private final BakedModelManager modelManager;
	private int field_44395;

	public ItemModels(BakedModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public BakedModel getModel(ItemStack stack) {
		BakedModel bakedModel = this.getModel(stack.getItem());
		return bakedModel == null ? this.modelManager.getMissingModel() : bakedModel;
	}

	@Nullable
	public BakedModel getModel(Item item) {
		int i = class_8293.field_43517.method_50385();
		if (this.field_44395 != i) {
			this.reloadModels();
			this.field_44395 = i;
		}

		return this.models.get(getModelId(item));
	}

	private static int getModelId(Item item) {
		return Item.getRawId(item);
	}

	public void putModel(Item item, ModelIdentifier modelId) {
		this.modelIds.put(getModelId(item), modelId);
	}

	public BakedModelManager getModelManager() {
		return this.modelManager;
	}

	public void reloadModels() {
		this.models.clear();

		for (Entry<ModelIdentifier> entry : this.modelIds.int2ObjectEntrySet()) {
			int i = entry.getIntKey();
			Item item = Item.byRawId(i);
			Item item2 = class_8293.field_43517.method_50386(item);
			ModelIdentifier modelIdentifier;
			if (item == item2) {
				modelIdentifier = (ModelIdentifier)entry.getValue();
			} else {
				int j = Item.getRawId(item2);
				modelIdentifier = this.modelIds.get(j);
			}

			this.models.put(i, this.modelManager.getModel(modelIdentifier));
		}
	}
}
