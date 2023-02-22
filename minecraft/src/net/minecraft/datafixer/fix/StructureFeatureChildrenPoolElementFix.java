package net.minecraft.datafixer.fix;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

/**
 * A fix that updates a few children pool element features with complex
 * configurations to simple ID strings within structure features.
 * 
 * <p>This is known as {@index SavedDataFeaturePoolElementFix} in the
 * literal strings.
 */
public class StructureFeatureChildrenPoolElementFix extends DataFix {
	private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[(\\d+)\\]");
	private static final Set<String> TARGET_CHILDREN_IDS = Sets.<String>newHashSet(
		"minecraft:jigsaw", "minecraft:nvi", "minecraft:pcp", "minecraft:bastionremnant", "minecraft:runtime"
	);
	private static final Set<String> TARGET_FEATURES = Sets.<String>newHashSet(
		"minecraft:tree", "minecraft:flower", "minecraft:block_pile", "minecraft:random_patch"
	);

	public StructureFeatureChildrenPoolElementFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"SavedDataFeaturePoolElementFix",
			this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE),
			this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE),
			StructureFeatureChildrenPoolElementFix::updateStructureFeature
		);
	}

	private static <T> Dynamic<T> updateStructureFeature(Dynamic<T> structureFeature) {
		return structureFeature.update("Children", StructureFeatureChildrenPoolElementFix::updateChildren);
	}

	private static <T> Dynamic<T> updateChildren(Dynamic<T> children) {
		return (Dynamic<T>)children.asStreamOpt().map(StructureFeatureChildrenPoolElementFix::mapChildren).map(children::createList).result().orElse(children);
	}

	private static Stream<? extends Dynamic<?>> mapChildren(Stream<? extends Dynamic<?>> children) {
		return children.map(
			child -> {
				String string = child.get("id").asString("");
				if (!TARGET_CHILDREN_IDS.contains(string)) {
					return child;
				} else {
					OptionalDynamic<?> optionalDynamic = child.get("pool_element");
					return !optionalDynamic.get("element_type").asString("").equals("minecraft:feature_pool_element")
						? child
						: child.update("pool_element", poolElement -> poolElement.update("feature", StructureFeatureChildrenPoolElementFix::updatePoolElementFeature));
				}
			}
		);
	}

	private static <T> OptionalDynamic<T> findValueAt(Dynamic<T> root, String... pathParts) {
		if (pathParts.length == 0) {
			throw new IllegalArgumentException("Missing path");
		} else {
			OptionalDynamic<T> optionalDynamic = root.get(pathParts[0]);

			for (int i = 1; i < pathParts.length; i++) {
				String string = pathParts[i];
				Matcher matcher = ARRAY_INDEX_PATTERN.matcher(string);
				if (matcher.matches()) {
					int j = Integer.parseInt(matcher.group(1));
					List<? extends Dynamic<T>> list = optionalDynamic.asList(Function.identity());
					if (j >= 0 && j < list.size()) {
						optionalDynamic = new OptionalDynamic<>(root.getOps(), DataResult.success((Dynamic<T>)list.get(j)));
					} else {
						optionalDynamic = new OptionalDynamic<>(root.getOps(), DataResult.error(() -> "Missing id:" + j));
					}
				} else {
					optionalDynamic = optionalDynamic.get(string);
				}
			}

			return optionalDynamic;
		}
	}

	@VisibleForTesting
	protected static Dynamic<?> updatePoolElementFeature(Dynamic<?> feature) {
		Optional<String> optional = updateFeature(
			findValueAt(feature, "type").asString(""),
			findValueAt(feature, "name").asString(""),
			findValueAt(feature, "config", "state_provider", "type").asString(""),
			findValueAt(feature, "config", "state_provider", "state", "Name").asString(""),
			findValueAt(feature, "config", "state_provider", "entries", "[0]", "data", "Name").asString(""),
			findValueAt(feature, "config", "foliage_placer", "type").asString(""),
			findValueAt(feature, "config", "leaves_provider", "state", "Name").asString("")
		);
		return optional.isPresent() ? feature.createString((String)optional.get()) : feature;
	}

	private static Optional<String> updateFeature(
		String type,
		String name,
		String stateProviderType,
		String stateProviderStateName,
		String stateProviderFirstEntryName,
		String foliagePlacerType,
		String leavesProviderStateName
	) {
		String string;
		if (!type.isEmpty()) {
			string = type;
		} else {
			if (name.isEmpty()) {
				return Optional.empty();
			}

			if ("minecraft:normal_tree".equals(name)) {
				string = "minecraft:tree";
			} else {
				string = name;
			}
		}

		if (TARGET_FEATURES.contains(string)) {
			if ("minecraft:random_patch".equals(string)) {
				if ("minecraft:simple_state_provider".equals(stateProviderType)) {
					if ("minecraft:sweet_berry_bush".equals(stateProviderStateName)) {
						return Optional.of("minecraft:patch_berry_bush");
					}

					if ("minecraft:cactus".equals(stateProviderStateName)) {
						return Optional.of("minecraft:patch_cactus");
					}
				} else if ("minecraft:weighted_state_provider".equals(stateProviderType)
					&& ("minecraft:grass".equals(stateProviderFirstEntryName) || "minecraft:fern".equals(stateProviderFirstEntryName))) {
					return Optional.of("minecraft:patch_taiga_grass");
				}
			} else if ("minecraft:block_pile".equals(string)) {
				if (!"minecraft:simple_state_provider".equals(stateProviderType) && !"minecraft:rotated_block_provider".equals(stateProviderType)) {
					if ("minecraft:weighted_state_provider".equals(stateProviderType)) {
						if ("minecraft:packed_ice".equals(stateProviderFirstEntryName) || "minecraft:blue_ice".equals(stateProviderFirstEntryName)) {
							return Optional.of("minecraft:pile_ice");
						}

						if ("minecraft:jack_o_lantern".equals(stateProviderFirstEntryName) || "minecraft:pumpkin".equals(stateProviderFirstEntryName)) {
							return Optional.of("minecraft:pile_pumpkin");
						}
					}
				} else {
					if ("minecraft:hay_block".equals(stateProviderStateName)) {
						return Optional.of("minecraft:pile_hay");
					}

					if ("minecraft:melon".equals(stateProviderStateName)) {
						return Optional.of("minecraft:pile_melon");
					}

					if ("minecraft:snow".equals(stateProviderStateName)) {
						return Optional.of("minecraft:pile_snow");
					}
				}
			} else {
				if ("minecraft:flower".equals(string)) {
					return Optional.of("minecraft:flower_plain");
				}

				if ("minecraft:tree".equals(string)) {
					if ("minecraft:acacia_foliage_placer".equals(foliagePlacerType)) {
						return Optional.of("minecraft:acacia");
					}

					if ("minecraft:blob_foliage_placer".equals(foliagePlacerType) && "minecraft:oak_leaves".equals(leavesProviderStateName)) {
						return Optional.of("minecraft:oak");
					}

					if ("minecraft:pine_foliage_placer".equals(foliagePlacerType)) {
						return Optional.of("minecraft:pine");
					}

					if ("minecraft:spruce_foliage_placer".equals(foliagePlacerType)) {
						return Optional.of("minecraft:spruce");
					}
				}
			}
		}

		return Optional.empty();
	}
}
