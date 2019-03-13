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
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;

@Environment(EnvType.CLIENT)
public class ItemModels {
	public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap<>(256);
	private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap<>(256);
	private final BakedModelManager field_4128;

	public ItemModels(BakedModelManager bakedModelManager) {
		this.field_4128 = bakedModelManager;
	}

	public Sprite method_3307(ItemProvider itemProvider) {
		return this.method_3305(new ItemStack(itemProvider));
	}

	public Sprite method_3305(ItemStack itemStack) {
		BakedModel bakedModel = this.method_3308(itemStack);
		return (bakedModel == this.field_4128.getMissingModel() || bakedModel.isBuiltin()) && itemStack.getItem() instanceof BlockItem
			? this.field_4128.getBlockStateMaps().method_3339(((BlockItem)itemStack.getItem()).method_7711().method_9564())
			: bakedModel.getSprite();
	}

	public BakedModel method_3308(ItemStack itemStack) {
		BakedModel bakedModel = this.method_3304(itemStack.getItem());
		return bakedModel == null ? this.field_4128.getMissingModel() : bakedModel;
	}

	@Nullable
	public BakedModel method_3304(Item item) {
		return this.models.get(getModelId(item));
	}

	private static int getModelId(Item item) {
		return Item.getRawIdByItem(item);
	}

	public void method_3309(Item item, ModelIdentifier modelIdentifier) {
		this.modelIds.put(getModelId(item), modelIdentifier);
	}

	public BakedModelManager method_3303() {
		return this.field_4128;
	}

	public void reloadModels() {
		this.models.clear();

		for (Entry<Integer, ModelIdentifier> entry : this.modelIds.entrySet()) {
			this.models.put((Integer)entry.getKey(), this.field_4128.method_4742((ModelIdentifier)entry.getValue()));
		}
	}
}
