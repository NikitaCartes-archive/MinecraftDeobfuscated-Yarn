/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record LanguageDefinition(String region, String name, boolean rightToLeft) {
    public static final Codec<LanguageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.NON_EMPTY_STRING.fieldOf("region")).forGetter(LanguageDefinition::region), ((MapCodec)Codecs.NON_EMPTY_STRING.fieldOf("name")).forGetter(LanguageDefinition::name), Codec.BOOL.optionalFieldOf("bidirectional", false).forGetter(LanguageDefinition::rightToLeft)).apply((Applicative<LanguageDefinition, ?>)instance, LanguageDefinition::new));

    public Text getDisplayText() {
        return Text.literal(this.name + " (" + this.region + ")");
    }
}

