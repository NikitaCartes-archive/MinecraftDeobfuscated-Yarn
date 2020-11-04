package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class EntityModelLayer {
	private final Identifier identifier;
	private final String layer;

	public EntityModelLayer(Identifier identifier, String layer) {
		this.identifier = identifier;
		this.layer = layer;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof EntityModelLayer)) {
			return false;
		} else {
			EntityModelLayer entityModelLayer = (EntityModelLayer)other;
			return this.identifier.equals(entityModelLayer.identifier) && this.layer.equals(entityModelLayer.layer);
		}
	}

	public int hashCode() {
		int i = this.identifier.hashCode();
		return 31 * i + this.layer.hashCode();
	}

	public String toString() {
		return this.identifier + "#" + this.layer;
	}
}
