package net.minecraft.item.trim;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class ArmorTrim {
	public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material").forGetter(ArmorTrim::getMaterial),
					ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(ArmorTrim::getPattern)
				)
				.apply(instance, ArmorTrim::new)
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String NBT_KEY = "Trim";
	private static final Text UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.upgrade")))
		.formatted(Formatting.GRAY);
	private final RegistryEntry<ArmorTrimMaterial> material;
	private final RegistryEntry<ArmorTrimPattern> pattern;
	private final Function<ArmorMaterial, Identifier> leggingsModelIdGetter;
	private final Function<ArmorMaterial, Identifier> genericModelIdGetter;

	public ArmorTrim(RegistryEntry<ArmorTrimMaterial> material, RegistryEntry<ArmorTrimPattern> pattern) {
		this.material = material;
		this.pattern = pattern;
		this.leggingsModelIdGetter = Util.memoize((Function<ArmorMaterial, Identifier>)(armorMaterial -> {
			Identifier identifier = pattern.value().assetId();
			String string = this.getMaterialAssetNameFor(armorMaterial);
			return identifier.withPath((UnaryOperator<String>)(path -> "trims/models/armor/" + path + "_leggings_" + string));
		}));
		this.genericModelIdGetter = Util.memoize((Function<ArmorMaterial, Identifier>)(armorMaterial -> {
			Identifier identifier = pattern.value().assetId();
			String string = this.getMaterialAssetNameFor(armorMaterial);
			return identifier.withPath((UnaryOperator<String>)(path -> "trims/models/armor/" + path + "_" + string));
		}));
	}

	private String getMaterialAssetNameFor(ArmorMaterial armorMaterial) {
		Map<ArmorMaterials, String> map = this.material.value().overrideArmorMaterials();
		return armorMaterial instanceof ArmorMaterials && map.containsKey(armorMaterial) ? (String)map.get(armorMaterial) : this.material.value().assetName();
	}

	public boolean equals(RegistryEntry<ArmorTrimPattern> pattern, RegistryEntry<ArmorTrimMaterial> material) {
		return pattern == this.pattern && material == this.material;
	}

	public RegistryEntry<ArmorTrimPattern> getPattern() {
		return this.pattern;
	}

	public RegistryEntry<ArmorTrimMaterial> getMaterial() {
		return this.material;
	}

	public Identifier getLeggingsModelId(ArmorMaterial armorMaterial) {
		return (Identifier)this.leggingsModelIdGetter.apply(armorMaterial);
	}

	public Identifier getGenericModelId(ArmorMaterial armorMaterial) {
		return (Identifier)this.genericModelIdGetter.apply(armorMaterial);
	}

	public boolean equals(Object o) {
		return !(o instanceof ArmorTrim armorTrim) ? false : armorTrim.pattern == this.pattern && armorTrim.material == this.material;
	}

	public static boolean apply(DynamicRegistryManager registryManager, ItemStack stack, ArmorTrim trim) {
		if (stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
			stack.getOrCreateNbt().put("Trim", (NbtElement)CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
			return true;
		} else {
			return false;
		}
	}

	public static Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
		if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.getNbt() != null && stack.getNbt().contains("Trim")) {
			NbtCompound nbtCompound = stack.getSubNbt("Trim");
			ArmorTrim armorTrim = (ArmorTrim)CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtCompound).resultOrPartial(LOGGER::error).orElse(null);
			return Optional.ofNullable(armorTrim);
		} else {
			return Optional.empty();
		}
	}

	public static void appendTooltip(ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip) {
		Optional<ArmorTrim> optional = getTrim(registryManager, stack);
		if (optional.isPresent()) {
			ArmorTrim armorTrim = (ArmorTrim)optional.get();
			tooltip.add(UPGRADE_TEXT);
			tooltip.add(ScreenTexts.space().append(armorTrim.getPattern().value().getDescription(armorTrim.getMaterial())));
			tooltip.add(ScreenTexts.space().append(armorTrim.getMaterial().value().description()));
		}
	}
}
