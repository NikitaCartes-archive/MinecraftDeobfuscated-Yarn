package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;

@Environment(EnvType.CLIENT)
public class ItemModels {
	public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap<>(256);
	private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap<>(256);
	private final BakedModelManager modelManager;

	public ItemModels(BakedModelManager bakedModelManager) {
		this.modelManager = bakedModelManager;
	}

	public Sprite getSprite(ItemContainer itemContainer) {
		return this.getSprite(new ItemStack(itemContainer));
	}

	public Sprite getSprite(ItemStack itemStack) {
		BakedModel bakedModel = this.getModel(itemStack);
		return (bakedModel == this.modelManager.getMissingModel() || bakedModel.isBuiltin()) && itemStack.getItem() instanceof BlockItem
			? this.modelManager.getBlockStateMaps().getSprite(((BlockItem)itemStack.getItem()).getBlock().getDefaultState())
			: bakedModel.getSprite();
	}

	public BakedModel getModel(ItemStack itemStack) {
		BakedModel bakedModel = this.getModel(itemStack.getItem());
		return bakedModel == null ? this.modelManager.getMissingModel() : bakedModel;
	}

	@Nullable
	public BakedModel getModel(Item item) {
		return this.models.get(getModelId(item));
	}

	private static int getModelId(Item item) {
		return Item.getRawIdByItem(item);
	}

	public void putModel(Item item, ModelIdentifier modelIdentifier) {
		this.modelIds.put(getModelId(item), modelIdentifier);
		this.models.put(getModelId(item), this.modelManager.getModel(modelIdentifier));
	}

	public BakedModelManager getModelManager() {
		return this.modelManager;
	}

	public void reloadModels() {
		this.models.clear();

		for (Entry<Integer, ModelIdentifier> entry : this.modelIds.entrySet()) {
			this.models.put((Integer)entry.getKey(), this.modelManager.getModel((ModelIdentifier)entry.getValue()));
		}
	}
}
