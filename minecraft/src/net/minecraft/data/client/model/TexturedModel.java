package net.minecraft.data.client.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

/**
 * Represents a model with texture variables defined.
 */
public class TexturedModel {
	public static final TexturedModel.Factory CUBE_ALL = makeFactory(Texture::all, Models.field_22972);
	public static final TexturedModel.Factory CUBE_MIRRORED_ALL = makeFactory(Texture::all, Models.field_22973);
	public static final TexturedModel.Factory CUBE_COLUMN = makeFactory(Texture::sideEnd, Models.field_22974);
	public static final TexturedModel.Factory CUBE_COLUMN_HORIZONTAL = makeFactory(Texture::sideEnd, Models.field_22975);
	public static final TexturedModel.Factory CUBE_BOTTOM_TOP = makeFactory(Texture::sideTopBottom, Models.field_22977);
	public static final TexturedModel.Factory CUBE_TOP = makeFactory(Texture::sideAndTop, Models.field_22976);
	public static final TexturedModel.Factory ORIENTABLE = makeFactory(Texture::sideFrontTop, Models.field_22978);
	public static final TexturedModel.Factory ORIENTABLE_WITH_BOTTOM = makeFactory(Texture::sideFrontTopBottom, Models.field_22979);
	public static final TexturedModel.Factory CARPET = makeFactory(Texture::wool, Models.field_22929);
	public static final TexturedModel.Factory TEMPLATE_GLAZED_TERRACOTTA = makeFactory(Texture::pattern, Models.field_22948);
	public static final TexturedModel.Factory CORAL_FAN = makeFactory(Texture::fan, Models.field_22946);
	public static final TexturedModel.Factory PARTICLE = makeFactory(Texture::particle, Models.PARTICLE);
	public static final TexturedModel.Factory TEMPLATE_ANVIL = makeFactory(Texture::top, Models.field_22957);
	public static final TexturedModel.Factory LEAVES = makeFactory(Texture::all, Models.field_22911);
	public static final TexturedModel.Factory TEMPLATE_LANTERN = makeFactory(Texture::lantern, Models.field_22967);
	public static final TexturedModel.Factory TEMPLATE_HANGING_LANTERN = makeFactory(Texture::lantern, Models.field_22968);
	public static final TexturedModel.Factory TEMPLATE_SEAGRASS = makeFactory(Texture::texture, Models.field_22932);
	public static final TexturedModel.Factory END_FOR_TOP_CUBE_COLUMN = makeFactory(Texture::sideAndEndForTop, Models.field_22974);
	public static final TexturedModel.Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = makeFactory(Texture::sideAndEndForTop, Models.field_22975);
	public static final TexturedModel.Factory WALL_CUBE_BOTTOM_TOP = makeFactory(Texture::wallSideTopBottom, Models.field_22977);
	public static final TexturedModel.Factory field_23959 = makeFactory(Texture::method_27168, Models.field_22974);
	private final Texture texture;
	private final Model model;

	private TexturedModel(Texture texture, Model model) {
		this.texture = texture;
		this.model = model;
	}

	public Model getModel() {
		return this.model;
	}

	public Texture getTexture() {
		return this.texture;
	}

	public TexturedModel texture(Consumer<Texture> textureConsumer) {
		textureConsumer.accept(this.texture);
		return this;
	}

	public Identifier upload(Block block, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
		return this.model.upload(block, this.texture, writer);
	}

	public Identifier upload(Block block, String suffix, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
		return this.model.upload(block, suffix, this.texture, writer);
	}

	private static TexturedModel.Factory makeFactory(Function<Block, Texture> textureGetter, Model model) {
		return block -> new TexturedModel((Texture)textureGetter.apply(block), model);
	}

	public static TexturedModel getCubeAll(Identifier id) {
		return new TexturedModel(Texture.all(id), Models.field_22972);
	}

	@FunctionalInterface
	public interface Factory {
		TexturedModel get(Block block);

		default Identifier upload(Block block, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
			return this.get(block).upload(block, writer);
		}

		default Identifier upload(Block block, String suffix, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
			return this.get(block).upload(block, suffix, writer);
		}

		default TexturedModel.Factory withTexture(Consumer<Texture> textureConsumer) {
			return block -> this.get(block).texture(textureConsumer);
		}
	}
}
