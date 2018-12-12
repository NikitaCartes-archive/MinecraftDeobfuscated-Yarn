package net.minecraft.client.render.model;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BakedModelManager implements ResourceReloadListener {
	private Map<Identifier, BakedModel> modelMap;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockModels blockStateMaps;
	private BakedModel missingModel;

	public BakedModelManager(SpriteAtlasTexture spriteAtlasTexture) {
		this.spriteAtlas = spriteAtlasTexture;
		this.blockStateMaps = new BlockModels(this);
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.modelMap = new ModelLoader(resourceManager, this.spriteAtlas).getBakedModelMap();
		this.missingModel = (BakedModel)this.modelMap.get(ModelLoader.MISSING);
		this.blockStateMaps.reload();
	}

	public BakedModel getModel(ModelIdentifier modelIdentifier) {
		return (BakedModel)this.modelMap.getOrDefault(modelIdentifier, this.missingModel);
	}

	public BakedModel getMissingModel() {
		return this.missingModel;
	}

	public BlockModels getBlockStateMaps() {
		return this.blockStateMaps;
	}
}
