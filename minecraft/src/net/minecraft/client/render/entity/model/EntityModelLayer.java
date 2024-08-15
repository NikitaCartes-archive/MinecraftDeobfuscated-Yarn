package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record EntityModelLayer(Identifier id, String name) {
	public String toString() {
		return this.id + "#" + this.name;
	}
}
