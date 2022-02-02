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
import net.minecraft.data.client.model.TextureMap;
import net.minecraft.util.Identifier;

/**
 * Represents a model with texture variables defined.
 */
public class TexturedModel {
    public static final Factory CUBE_ALL = TexturedModel.makeFactory(TextureMap::all, Models.CUBE_ALL);
    public static final Factory CUBE_MIRRORED_ALL = TexturedModel.makeFactory(TextureMap::all, Models.CUBE_MIRRORED_ALL);
    public static final Factory CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN);
    public static final Factory CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory CUBE_BOTTOM_TOP = TexturedModel.makeFactory(TextureMap::sideTopBottom, Models.CUBE_BOTTOM_TOP);
    public static final Factory CUBE_TOP = TexturedModel.makeFactory(TextureMap::sideAndTop, Models.CUBE_TOP);
    public static final Factory ORIENTABLE = TexturedModel.makeFactory(TextureMap::sideFrontTop, Models.ORIENTABLE);
    public static final Factory ORIENTABLE_WITH_BOTTOM = TexturedModel.makeFactory(TextureMap::sideFrontTopBottom, Models.ORIENTABLE_WITH_BOTTOM);
    public static final Factory CARPET = TexturedModel.makeFactory(TextureMap::wool, Models.CARPET);
    public static final Factory TEMPLATE_GLAZED_TERRACOTTA = TexturedModel.makeFactory(TextureMap::pattern, Models.TEMPLATE_GLAZED_TERRACOTTA);
    public static final Factory CORAL_FAN = TexturedModel.makeFactory(TextureMap::fan, Models.CORAL_FAN);
    public static final Factory PARTICLE = TexturedModel.makeFactory(TextureMap::particle, Models.PARTICLE);
    public static final Factory TEMPLATE_ANVIL = TexturedModel.makeFactory(TextureMap::top, Models.TEMPLATE_ANVIL);
    public static final Factory LEAVES = TexturedModel.makeFactory(TextureMap::all, Models.LEAVES);
    public static final Factory TEMPLATE_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, Models.TEMPLATE_LANTERN);
    public static final Factory TEMPLATE_HANGING_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, Models.TEMPLATE_HANGING_LANTERN);
    public static final Factory TEMPLATE_SEAGRASS = TexturedModel.makeFactory(TextureMap::texture, Models.TEMPLATE_SEAGRASS);
    public static final Factory END_FOR_TOP_CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN);
    public static final Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory SIDE_TOP_BOTTOM_WALL = TexturedModel.makeFactory(TextureMap::wallSideTopBottom, Models.CUBE_BOTTOM_TOP);
    public static final Factory SIDE_END_WALL = TexturedModel.makeFactory(TextureMap::wallSideEnd, Models.CUBE_COLUMN);
    private final TextureMap textures;
    private final Model model;

    private TexturedModel(TextureMap textures, Model model) {
        this.textures = textures;
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public TextureMap getTextures() {
        return this.textures;
    }

    public TexturedModel textures(Consumer<TextureMap> texturesConsumer) {
        texturesConsumer.accept(this.textures);
        return this;
    }

    public Identifier upload(Block block, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
        return this.model.upload(block, this.textures, writer);
    }

    public Identifier upload(Block block, String suffix, BiConsumer<Identifier, Supplier<JsonElement>> writer) {
        return this.model.upload(block, suffix, this.textures, writer);
    }

    private static Factory makeFactory(Function<Block, TextureMap> texturesGetter, Model model) {
        return block -> new TexturedModel((TextureMap)texturesGetter.apply(block), model);
    }

    public static TexturedModel getCubeAll(Identifier id) {
        return new TexturedModel(TextureMap.all(id), Models.CUBE_ALL);
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

        default public Factory andThen(Consumer<TextureMap> consumer) {
            return block -> this.get(block).textures(consumer);
        }
    }
}

