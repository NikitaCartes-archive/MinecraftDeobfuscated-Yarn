/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemModels {
    public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap<ModelIdentifier>(256);
    private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap<BakedModel>(256);
    private final BakedModelManager modelManager;

    public ItemModels(BakedModelManager bakedModelManager) {
        this.modelManager = bakedModelManager;
    }

    public Sprite getSprite(ItemConvertible itemConvertible) {
        return this.getSprite(new ItemStack(itemConvertible));
    }

    public Sprite getSprite(ItemStack itemStack) {
        BakedModel bakedModel = this.getModel(itemStack);
        if (bakedModel == this.modelManager.getMissingModel() && itemStack.getItem() instanceof BlockItem) {
            return this.modelManager.getBlockStateMaps().getSprite(((BlockItem)itemStack.getItem()).getBlock().getDefaultState());
        }
        return bakedModel.getSprite();
    }

    public BakedModel getModel(ItemStack itemStack) {
        BakedModel bakedModel = this.getModel(itemStack.getItem());
        return bakedModel == null ? this.modelManager.getMissingModel() : bakedModel;
    }

    @Nullable
    public BakedModel getModel(Item item) {
        return (BakedModel)this.models.get(ItemModels.getModelId(item));
    }

    private static int getModelId(Item item) {
        return Item.getRawId(item);
    }

    public void putModel(Item item, ModelIdentifier modelIdentifier) {
        this.modelIds.put(ItemModels.getModelId(item), modelIdentifier);
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void reloadModels() {
        this.models.clear();
        for (Map.Entry entry : this.modelIds.entrySet()) {
            this.models.put((Integer)entry.getKey(), this.modelManager.getModel((ModelIdentifier)entry.getValue()));
        }
    }
}

