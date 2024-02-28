package net.minecraft.item.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public class ArmorTrim implements TooltipAppender {
	public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material").forGetter(ArmorTrim::getMaterial),
					ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(ArmorTrim::getPattern),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "show_in_tooltip", true).forGetter(trim -> trim.showInTooltip)
				)
				.apply(instance, ArmorTrim::new)
	);
	public static final PacketCodec<RegistryByteBuf, ArmorTrim> PACKET_CODEC = PacketCodec.tuple(
		ArmorTrimMaterial.ENTRY_PACKET_CODEC,
		ArmorTrim::getMaterial,
		ArmorTrimPattern.ENTRY_PACKET_CODEC,
		ArmorTrim::getPattern,
		PacketCodecs.BOOL,
		trim -> trim.showInTooltip,
		ArmorTrim::new
	);
	private static final Text UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.upgrade")))
		.formatted(Formatting.GRAY);
	private final RegistryEntry<ArmorTrimMaterial> material;
	private final RegistryEntry<ArmorTrimPattern> pattern;
	private final boolean showInTooltip;
	private final Function<RegistryEntry<ArmorMaterial>, Identifier> leggingsModelIdGetter;
	private final Function<RegistryEntry<ArmorMaterial>, Identifier> genericModelIdGetter;

	public ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern, boolean showInTooltip) {
		this.material = material;
		this.pattern = pattern;
		this.leggingsModelIdGetter = Util.memoize((Function<RegistryEntry<ArmorMaterial>, Identifier>)(materialEntry -> {
			Identifier identifier = pattern.value().assetId();
			String string = this.getMaterialAssetNameFor(materialEntry);
			return identifier.withPath((UnaryOperator<String>)(materialName -> "trims/models/armor/" + materialName + "_leggings_" + string));
		}));
		this.genericModelIdGetter = Util.memoize((Function<RegistryEntry<ArmorMaterial>, Identifier>)(materialEntry -> {
			Identifier identifier = pattern.value().assetId();
			String string = this.getMaterialAssetNameFor(materialEntry);
			return identifier.withPath((UnaryOperator<String>)(materialName -> "trims/models/armor/" + materialName + "_" + string));
		}));
		this.showInTooltip = showInTooltip;
	}

	public ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern) {
		this(material, pattern, true);
	}

	private String getMaterialAssetNameFor(RegistryEntry<ArmorMaterial> armorMaterial) {
		Map<RegistryEntry<ArmorMaterial>, String> map = this.material.value().overrideArmorMaterials();
		String string = (String)map.get(armorMaterial);
		return string != null ? string : this.material.value().assetName();
	}

	public boolean equals(RegistryEntry<ArmorTrimPattern> pattern, RegistryEntry<ArmorTrimMaterial> material) {
		return pattern.equals(this.pattern) && material.equals(this.material);
	}

	public RegistryEntry<ArmorTrimPattern> getPattern() {
		return this.pattern;
	}

	public RegistryEntry<ArmorTrimMaterial> getMaterial() {
		return this.material;
	}

	public Identifier getLeggingsModelId(RegistryEntry<ArmorMaterial> armorMaterial) {
		return (Identifier)this.leggingsModelIdGetter.apply(armorMaterial);
	}

	public Identifier getGenericModelId(RegistryEntry<ArmorMaterial> armorMaterial) {
		return (Identifier)this.genericModelIdGetter.apply(armorMaterial);
	}

	public boolean equals(Object o) {
		return !(o instanceof ArmorTrim armorTrim)
			? false
			: this.showInTooltip == armorTrim.showInTooltip && this.pattern.equals(armorTrim.pattern) && this.material.equals(armorTrim.material);
	}

	public int hashCode() {
		int i = this.material.hashCode();
		i = 31 * i + this.pattern.hashCode();
		return 31 * i + (this.showInTooltip ? 1 : 0);
	}

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		if (this.showInTooltip) {
			textConsumer.accept(UPGRADE_TEXT);
			textConsumer.accept(ScreenTexts.space().append(this.pattern.value().getDescription(this.material)));
			textConsumer.accept(ScreenTexts.space().append(this.material.value().description()));
		}
	}
}
