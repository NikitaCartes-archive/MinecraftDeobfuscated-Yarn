package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MapDecorationsAtlasManager extends SpriteAtlasHolder {
	public MapDecorationsAtlasManager(TextureManager manager) {
		super(manager, new Identifier("textures/atlas/map_decorations.png"), new Identifier("map_decorations"));
	}

	public Sprite getSprite(MapDecoration decoration) {
		return this.getSprite(decoration.getAssetId());
	}
}
