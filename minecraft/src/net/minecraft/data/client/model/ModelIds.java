package net.minecraft.data.client.model;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModelIds {
	@Deprecated
	public static Identifier getMinecraftNamespacedBlock(String name) {
		return new Identifier("minecraft", "block/" + name);
	}

	public static Identifier getMinecraftNamespacedItem(String name) {
		return new Identifier("minecraft", "item/" + name);
	}

	public static Identifier getBlockSubModelId(Block block, String suffix) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath() + suffix);
	}

	public static Identifier getBlockModelId(Block block) {
		Identifier identifier = Registry.BLOCK.getId(block);
		return new Identifier(identifier.getNamespace(), "block/" + identifier.getPath());
	}

	public static Identifier getItemModelId(Item item) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
	}

	public static Identifier getItemSubModelId(Item item, String suffix) {
		Identifier identifier = Registry.ITEM.getId(item);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath() + suffix);
	}
}
