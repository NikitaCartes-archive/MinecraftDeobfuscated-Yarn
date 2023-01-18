package net.minecraft.client.resource.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record LanguageDefinition(String region, String name, boolean rightToLeft) {
	public static final Codec<LanguageDefinition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageDefinition::region),
					Codecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageDefinition::name),
					Codec.BOOL.optionalFieldOf("bidirectional", Boolean.valueOf(false)).forGetter(LanguageDefinition::rightToLeft)
				)
				.apply(instance, LanguageDefinition::new)
	);

	public Text getDisplayText() {
		return Text.literal(this.name + " (" + this.region + ")");
	}
}
