package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected final BlockEntityRenderDispatcher field_20989;

	public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.field_20989 = blockEntityRenderDispatcher;
	}

	public abstract void render(T blockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i);

	protected Sprite method_23082(Identifier identifier) {
		return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
	}

	public boolean method_3563(T blockEntity) {
		return false;
	}
}
