package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public record ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern, boolean showInTooltip) implements TooltipAppender {
	public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material").forGetter(ArmorTrim::material),
					ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(trim -> trim.showInTooltip)
				)
				.apply(instance, ArmorTrim::new)
	);
	public static final PacketCodec<RegistryByteBuf, ArmorTrim> PACKET_CODEC = PacketCodec.tuple(
		ArmorTrimMaterial.ENTRY_PACKET_CODEC,
		ArmorTrim::material,
		ArmorTrimPattern.ENTRY_PACKET_CODEC,
		ArmorTrim::pattern,
		PacketCodecs.BOOL,
		trim -> trim.showInTooltip,
		ArmorTrim::new
	);
	private static final Text UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.upgrade")))
		.formatted(Formatting.GRAY);

	public ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern) {
		this(material, pattern, true);
	}

	private static String getMaterialAssetNameFor(RegistryEntry<ArmorTrimMaterial> material, Identifier modelId) {
		String string = (String)material.value().overrideArmorMaterials().get(modelId);
		return string != null ? string : material.value().assetName();
	}

	public boolean equals(RegistryEntry<ArmorTrimPattern> pattern, RegistryEntry<ArmorTrimMaterial> material) {
		return pattern.equals(this.pattern) && material.equals(this.material);
	}

	public Identifier getTexture(EquipmentModel.LayerType layerType, Identifier modelId) {
		Identifier identifier = this.pattern.value().assetId();
		String string = getMaterialAssetNameFor(this.material, modelId);
		return identifier.withPath((UnaryOperator<String>)(patternName -> "trims/entity/" + layerType.asString() + "/" + patternName + "_" + string));
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		if (this.showInTooltip) {
			tooltip.accept(UPGRADE_TEXT);
			tooltip.accept(ScreenTexts.space().append(this.pattern.value().getDescription(this.material)));
			tooltip.accept(ScreenTexts.space().append(this.material.value().description()));
		}
	}

	public ArmorTrim withShowInTooltip(boolean showInTooltip) {
		return new ArmorTrim(this.material, this.pattern, showInTooltip);
	}
}
