package net.minecraft;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4730 {
	private final Identifier field_21769;
	private final Identifier field_21770;
	@Nullable
	private RenderLayer field_21771;

	public class_4730(Identifier identifier, Identifier identifier2) {
		this.field_21769 = identifier;
		this.field_21770 = identifier2;
	}

	public Identifier method_24144() {
		return this.field_21769;
	}

	public Identifier method_24147() {
		return this.field_21770;
	}

	public Sprite method_24148() {
		return (Sprite)MinecraftClient.getInstance().getSpriteAtlas(this.method_24144()).apply(this.method_24147());
	}

	public RenderLayer method_24146(Function<Identifier, RenderLayer> function) {
		if (this.field_21771 == null) {
			this.field_21771 = (RenderLayer)function.apply(this.field_21769);
		}

		return this.field_21771;
	}

	public VertexConsumer method_24145(VertexConsumerProvider vertexConsumerProvider, Function<Identifier, RenderLayer> function) {
		return this.method_24148().method_24108(vertexConsumerProvider.getBuffer(this.method_24146(function)));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4730 lv = (class_4730)object;
			return this.field_21769.equals(lv.field_21769) && this.field_21770.equals(lv.field_21770);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_21769, this.field_21770});
	}

	public String toString() {
		return "Material{atlasLocation=" + this.field_21769 + ", texture=" + this.field_21770 + '}';
	}
}
