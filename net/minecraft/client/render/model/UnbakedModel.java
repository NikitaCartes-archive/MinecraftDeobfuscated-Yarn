/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel {
    public Collection<Identifier> getModelDependencies();

    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<String> var2);

    @Nullable
    public BakedModel bake(ModelLoader var1, Function<Identifier, Sprite> var2, ModelBakeSettings var3);
}

