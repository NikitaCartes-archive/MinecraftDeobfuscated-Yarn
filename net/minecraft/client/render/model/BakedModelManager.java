/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
extends SinglePreparationResourceReloadListener<ModelLoader> {
    private Map<Identifier, BakedModel> modelMap;
    private final SpriteAtlasTexture spriteAtlas;
    private final BlockModels blockStateMaps;
    private final BlockColors colorMap;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> stateToModelIndex;

    public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture, BlockColors blockColors) {
        this.spriteAtlas = spriteAtlasTexture;
        this.colorMap = blockColors;
        this.blockStateMaps = new BlockModels(this);
    }

    public BakedModel getModel(ModelIdentifier modelIdentifier) {
        return this.modelMap.getOrDefault(modelIdentifier, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModels getBlockStateMaps() {
        return this.blockStateMaps;
    }

    protected ModelLoader method_18178(ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, this.colorMap, profiler);
        profiler.endTick();
        return modelLoader;
    }

    protected void method_18179(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        modelLoader.upload(profiler);
        this.modelMap = modelLoader.getBakedModelMap();
        this.stateToModelIndex = modelLoader.getStateToModelIndex();
        this.missingModel = this.modelMap.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockStateMaps.reload();
        profiler.pop();
        profiler.endTick();
    }

    public boolean shouldRerender(BlockState blockState, BlockState blockState2) {
        int j;
        if (blockState == blockState2) {
            return false;
        }
        int i = this.stateToModelIndex.getInt(blockState);
        if (i != -1 && i == (j = this.stateToModelIndex.getInt(blockState2))) {
            FluidState fluidState2;
            FluidState fluidState = blockState.getFluidState();
            return fluidState != (fluidState2 = blockState2.getFluidState());
        }
        return true;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
        return this.method_18178(resourceManager, profiler);
    }
}

