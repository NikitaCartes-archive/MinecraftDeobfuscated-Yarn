package net.minecraft.client.render.item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ItemModels {
	private final Map<Identifier, BakedModel> models = new HashMap();
	private final Supplier<BakedModel> missingModelSupplier;
	private final Function<Identifier, BakedModel> idToModel;

	public ItemModels(BakedModelManager modelManager) {
		this.missingModelSupplier = modelManager::getMissingModel;
		this.idToModel = id -> modelManager.getModel(ModelIdentifier.ofInventoryVariant(id));
	}

	public BakedModel getModel(ItemStack stack) {
		Identifier identifier = stack.get(DataComponentTypes.ITEM_MODEL);
		return identifier == null ? (BakedModel)this.missingModelSupplier.get() : this.getModel(identifier);
	}

	public BakedModel getModel(Identifier id) {
		return (BakedModel)this.models.computeIfAbsent(id, this.idToModel);
	}

	public void clearModels() {
		this.models.clear();
	}
}
