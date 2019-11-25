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
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
extends SinglePreparationResourceReloadListener<ModelLoader>
implements AutoCloseable {
    private Map<Identifier, BakedModel> models;
    private SpriteAtlasManager atlasManager;
    private final BlockModels blockModelCache;
    private final TextureManager textureManager;
    private final BlockColors colorMap;
    private int mipmap;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> stateLookup;

    public BakedModelManager(TextureManager textureManager, BlockColors blockColors, int i) {
        this.textureManager = textureManager;
        this.colorMap = blockColors;
        this.mipmap = i;
        this.blockModelCache = new BlockModels(this);
    }

    public BakedModel getModel(ModelIdentifier modelIdentifier) {
        return this.models.getOrDefault(modelIdentifier, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    protected ModelLoader prepare(ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        ModelLoader modelLoader = new ModelLoader(resourceManager, this.colorMap, profiler, this.mipmap);
        profiler.endTick();
        return modelLoader;
    }

    @Override
    protected void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        this.atlasManager = modelLoader.upload(this.textureManager, profiler);
        this.models = modelLoader.getBakedModelMap();
        this.stateLookup = modelLoader.getStateLookup();
        this.missingModel = this.models.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockModelCache.reload();
        profiler.pop();
        profiler.endTick();
    }

    public boolean shouldRerender(BlockState blockState, BlockState blockState2) {
        int j;
        if (blockState == blockState2) {
            return false;
        }
        int i = this.stateLookup.getInt(blockState);
        if (i != -1 && i == (j = this.stateLookup.getInt(blockState2))) {
            FluidState fluidState2;
            FluidState fluidState = blockState.getFluidState();
            return fluidState != (fluidState2 = blockState2.getFluidState());
        }
        return true;
    }

    public SpriteAtlasTexture method_24153(Identifier identifier) {
        return this.atlasManager.getAtlas(identifier);
    }

    @Override
    public void close() {
        this.atlasManager.close();
    }

    public void resetMipmapLevels(int i) {
        this.mipmap = i;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
        return this.prepare(resourceManager, profiler);
    }
}

