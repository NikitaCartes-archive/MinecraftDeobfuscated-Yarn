/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.Texture;
import net.minecraft.util.Identifier;

/**
 * Represents a model with texture variables defined.
 */
public class TexturedModel {
    public static final Factory CUBE_ALL = TexturedModel.makeFactory(Texture::all, Models.CUBE_ALL);
    public static final Factory CUBE_MIRRORED_ALL = TexturedModel.makeFactory(Texture::all, Models.CUBE_MIRRORED_ALL);
    public static final Factory CUBE_COLUMN = TexturedModel.makeFactory(Texture::sideEnd, Models.CUBE_COLUMN);
    public static final Factory CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(Texture::sideEnd, Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory CUBE_BOTTOM_TOP = TexturedModel.makeFactory(Texture::sideTopBottom, Models.CUBE_BOTTOM_TOP);
    public static final Factory CUBE_TOP = TexturedModel.makeFactory(Texture::sideAndTop, Models.CUBE_TOP);
    public static final Factory ORIENTABLE = TexturedModel.makeFactory(Texture::sideFrontTop, Models.ORIENTABLE);
    public static final Factory ORIENTABLE_WITH_BOTTOM = TexturedModel.makeFactory(Texture::sideFrontTopBottom, Models.ORIENTABLE_WITH_BOTTOM);
    public static final Factory CARPET = TexturedModel.makeFactory(Texture::wool, Models.CARPET);
    public static final Factory TEMPLATE_GLAZED_TERRACOTTA = TexturedModel.makeFactory(Texture::pattern, Models.TEMPLATE_GLAZED_TERRACOTTA);
    public static final Factory CORAL_FAN = TexturedModel.makeFactory(Texture::fan, Models.CORAL_FAN);
    public static final Factory PARTICLE = TexturedModel.makeFactory(Texture::particle, Models.PARTICLE);
    public static final Factory TEMPLATE_ANVIL = TexturedModel.makeFactory(Texture::top, Models.TEMPLATE_ANVIL);
    public static final Factory LEAVES = TexturedModel.makeFactory(Texture::all, Models.LEAVES);
    public static final Factory TEMPLATE_LANTERN = TexturedModel.makeFactory(Texture::lantern, Models.TEMPLATE_LANTERN);
    public static final Factory TEMPLATE_HANGING_LANTERN = TexturedModel.makeFactory(Texture::lantern, Models.TEMPLATE_HANGING_LANTERN);
    public static final Factory TEMPLATE_SEAGRASS = TexturedModel.makeFactory(Texture::texture, Models.TEMPLATE_SEAGRASS);
    public static final Factory END_FOR_TOP_CUBE_COLUMN = TexturedModel.makeFactory(Texture::sideAndEndForTop, Models.CUBE_COLUMN);
    public static final Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(Texture::sideAndEndForTop, Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory WALL_CUBE_BOTTOM_TOP = TexturedModel.makeFactory(Texture::wallSideTopBottom, Models.CUBE_BOTTOM_TOP);
    public static final Factory field_23959 = TexturedModel.makeFactory(Texture::method_27168, Models.CUBE_COLUMN);
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

    private static Factory makeFactory(Function<Block, Texture> textureGetter, Model model) {
        return block -> new TexturedModel((Texture)textureGetter.apply(block), model);
    }

    public static TexturedModel getCubeAll(Identifier id) {
        return new TexturedModel(Texture.all(id), Models.CUBE_ALL);
    }

    @FunctionalInterface
    public static interface Factory {
        public TexturedModel get(Block var1);

        default public Identifier upload(Block block, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
            return this.get(block).upload(block, writer);
        }

        default public Identifier upload(Block block, String suffix, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
            return this.get(block).upload(block, suffix, writer);
        }

        default public Factory withTexture(Consumer<Texture> textureConsumer) {
            return block -> this.get(block).texture(textureConsumer);
        }
    }
}

