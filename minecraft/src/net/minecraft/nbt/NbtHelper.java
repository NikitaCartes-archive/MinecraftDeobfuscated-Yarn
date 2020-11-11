package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
	private static final Comparator<ListTag> field_27816 = Comparator.comparingInt(listTag -> listTag.getInt(1))
		.thenComparingInt(listTag -> listTag.getInt(0))
		.thenComparingInt(listTag -> listTag.getInt(2));
	private static final Comparator<ListTag> field_27817 = Comparator.comparingDouble(listTag -> listTag.getDouble(1))
		.thenComparingDouble(listTag -> listTag.getDouble(0))
		.thenComparingDouble(listTag -> listTag.getDouble(2));
	private static final Splitter field_27818 = Splitter.on(",");
	private static final Splitter field_27819 = Splitter.on(':').limit(2);
	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static GameProfile toGameProfile(CompoundTag tag) {
		String string = null;
		UUID uUID = null;
		if (tag.contains("Name", 8)) {
			string = tag.getString("Name");
		}

		if (tag.containsUuid("Id")) {
			uUID = tag.getUuid("Id");
		}

		try {
			GameProfile gameProfile = new GameProfile(uUID, string);
			if (tag.contains("Properties", 10)) {
				CompoundTag compoundTag = tag.getCompound("Properties");

				for (String string2 : compoundTag.getKeys()) {
					ListTag listTag = compoundTag.getList(string2, 10);

					for (int i = 0; i < listTag.size(); i++) {
						CompoundTag compoundTag2 = listTag.getCompound(i);
						String string3 = compoundTag2.getString("Value");
						if (compoundTag2.contains("Signature", 8)) {
							gameProfile.getProperties().put(string2, new com.mojang.authlib.properties.Property(string2, string3, compoundTag2.getString("Signature")));
						} else {
							gameProfile.getProperties().put(string2, new com.mojang.authlib.properties.Property(string2, string3));
						}
					}
				}
			}

			return gameProfile;
		} catch (Throwable var11) {
			return null;
		}
	}

	public static CompoundTag fromGameProfile(CompoundTag tag, GameProfile profile) {
		if (!ChatUtil.isEmpty(profile.getName())) {
			tag.putString("Name", profile.getName());
		}

		if (profile.getId() != null) {
			tag.putUuid("Id", profile.getId());
		}

		if (!profile.getProperties().isEmpty()) {
			CompoundTag compoundTag = new CompoundTag();

			for (String string : profile.getProperties().keySet()) {
				ListTag listTag = new ListTag();

				for (com.mojang.authlib.properties.Property property : profile.getProperties().get(string)) {
					CompoundTag compoundTag2 = new CompoundTag();
					compoundTag2.putString("Value", property.getValue());
					if (property.hasSignature()) {
						compoundTag2.putString("Signature", property.getSignature());
					}

					listTag.add(compoundTag2);
				}

				compoundTag.put(string, listTag);
			}

			tag.put("Properties", compoundTag);
		}

		return tag;
	}

	@VisibleForTesting
	public static boolean matches(@Nullable Tag standard, @Nullable Tag subject, boolean equalValue) {
		if (standard == subject) {
			return true;
		} else if (standard == null) {
			return true;
		} else if (subject == null) {
			return false;
		} else if (!standard.getClass().equals(subject.getClass())) {
			return false;
		} else if (standard instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag)standard;
			CompoundTag compoundTag2 = (CompoundTag)subject;

			for (String string : compoundTag.getKeys()) {
				Tag tag = compoundTag.get(string);
				if (!matches(tag, compoundTag2.get(string), equalValue)) {
					return false;
				}
			}

			return true;
		} else if (standard instanceof ListTag && equalValue) {
			ListTag listTag = (ListTag)standard;
			ListTag listTag2 = (ListTag)subject;
			if (listTag.isEmpty()) {
				return listTag2.isEmpty();
			} else {
				for (int i = 0; i < listTag.size(); i++) {
					Tag tag2 = listTag.get(i);
					boolean bl = false;

					for (int j = 0; j < listTag2.size(); j++) {
						if (matches(tag2, listTag2.get(j), equalValue)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		} else {
			return standard.equals(subject);
		}
	}

	/**
	 * Serializes a {@link UUID} into its equivalent NBT representation.
	 * 
	 * @since 20w10a
	 */
	public static IntArrayTag fromUuid(UUID uuid) {
		return new IntArrayTag(DynamicSerializableUuid.toIntArray(uuid));
	}

	/**
	 * Deserializes a tag into a {@link UUID}.
	 * The tag's data must have the same structure as the output of {@link #fromUuid}.
	 * 
	 * @throws IllegalArgumentException if {@code tag} is not a valid representation of a UUID
	 * @since 20w10a
	 */
	public static UUID toUuid(Tag tag) {
		if (tag.getReader() != IntArrayTag.READER) {
			throw new IllegalArgumentException(
				"Expected UUID-Tag to be of type " + IntArrayTag.READER.getCrashReportName() + ", but found " + tag.getReader().getCrashReportName() + "."
			);
		} else {
			int[] is = ((IntArrayTag)tag).getIntArray();
			if (is.length != 4) {
				throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
			} else {
				return DynamicSerializableUuid.toUuid(is);
			}
		}
	}

	public static BlockPos toBlockPos(CompoundTag tag) {
		return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
	}

	public static CompoundTag fromBlockPos(BlockPos pos) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putInt("X", pos.getX());
		compoundTag.putInt("Y", pos.getY());
		compoundTag.putInt("Z", pos.getZ());
		return compoundTag;
	}

	public static BlockState toBlockState(CompoundTag tag) {
		if (!tag.contains("Name", 8)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Block block = Registry.BLOCK.get(new Identifier(tag.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (tag.contains("Properties", 10)) {
				CompoundTag compoundTag = tag.getCompound("Properties");
				StateManager<Block, BlockState> stateManager = block.getStateManager();

				for (String string : compoundTag.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = withProperty(blockState, property, string, compoundTag, tag);
					}
				}
			}

			return blockState;
		}
	}

	private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(
		S state, Property<T> property, String key, CompoundTag propertiesTag, CompoundTag mainTag
	) {
		Optional<T> optional = property.parse(propertiesTag.getString(key));
		if (optional.isPresent()) {
			return state.with(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, propertiesTag.getString(key), mainTag.toString());
			return state;
		}
	}

	public static CompoundTag fromBlockState(BlockState state) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", Registry.BLOCK.getId(state.getBlock()).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
		if (!immutableMap.isEmpty()) {
			CompoundTag compoundTag2 = new CompoundTag();

			for (Entry<Property<?>, Comparable<?>> entry : immutableMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();
				compoundTag2.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			compoundTag.put("Properties", compoundTag2);
		}

		return compoundTag;
	}

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
	}

	/**
	 * Uses the data fixer to update a tag to the latest data version.
	 * 
	 * @param fixer the data fixer
	 * @param fixTypes the fix types
	 * @param tag the tag to fix
	 * @param oldVersion the data version of the compound tag
	 */
	public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion) {
		return update(fixer, fixTypes, tag, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
	}

	/**
	 * Uses the data fixer to update a tag.
	 * 
	 * @param fixer the data fixer
	 * @param fixTypes the fix types
	 * @param tag the tag to fix
	 * @param oldVersion the data version of the compound tag
	 * @param targetVersion the data version to update the tag to
	 */
	public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion, int targetVersion) {
		return (CompoundTag)fixer.update(fixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, tag), oldVersion, targetVersion).getValue();
	}

	public static Text toPrettyPrintedText(Tag tag) {
		return new NbtTextFormatter("", 0).apply(tag);
	}

	public static String toPrettyPrintedString(CompoundTag tag) {
		return new NbtOrderedStringFormatter().apply(method_32273(tag));
	}

	public static CompoundTag method_32260(String string) throws CommandSyntaxException {
		return method_32275(StringNbtReader.parse(string));
	}

	@VisibleForTesting
	static CompoundTag method_32273(CompoundTag tag) {
		boolean bl = tag.contains("palettes", 9);
		ListTag listTag;
		if (bl) {
			listTag = tag.getList("palettes", 9).getList(0);
		} else {
			listTag = tag.getList("palette", 10);
		}

		ListTag listTag2 = (ListTag)listTag.stream()
			.map(CompoundTag.class::cast)
			.map(NbtHelper::method_32277)
			.map(StringTag::of)
			.collect(Collectors.toCollection(ListTag::new));
		tag.put("palette", listTag2);
		if (bl) {
			ListTag listTag3 = new ListTag();
			ListTag listTag4 = tag.getList("palettes", 9);
			listTag4.stream().map(ListTag.class::cast).forEach(listTag3x -> {
				CompoundTag compoundTag = new CompoundTag();

				for (int i = 0; i < listTag3x.size(); i++) {
					compoundTag.putString(listTag2.getString(i), method_32277(listTag3x.getCompound(i)));
				}

				listTag3.add(compoundTag);
			});
			tag.put("palettes", listTag3);
		}

		if (tag.contains("entities", 10)) {
			ListTag listTag3 = tag.getList("entities", 10);
			ListTag listTag4 = (ListTag)listTag3.stream()
				.map(CompoundTag.class::cast)
				.sorted(Comparator.comparing(compoundTag -> compoundTag.getList("pos", 6), field_27817))
				.collect(Collectors.toCollection(ListTag::new));
			tag.put("entities", listTag4);
		}

		ListTag listTag3 = (ListTag)tag.getList("blocks", 10)
			.stream()
			.map(CompoundTag.class::cast)
			.sorted(Comparator.comparing(compoundTag -> compoundTag.getList("pos", 3), field_27816))
			.peek(compoundTag -> compoundTag.putString("state", listTag2.getString(compoundTag.getInt("state"))))
			.collect(Collectors.toCollection(ListTag::new));
		tag.put("data", listTag3);
		tag.remove("blocks");
		return tag;
	}

	@VisibleForTesting
	static CompoundTag method_32275(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("palette", 8);
		Map<String, Tag> map = (Map<String, Tag>)listTag.stream()
			.map(StringTag.class::cast)
			.map(StringTag::asString)
			.collect(ImmutableMap.toImmutableMap(Function.identity(), NbtHelper::method_32267));
		if (compoundTag.contains("palettes", 9)) {
			compoundTag.put(
				"palettes",
				(Tag)compoundTag.getList("palettes", 10)
					.stream()
					.map(CompoundTag.class::cast)
					.map(
						compoundTagx -> (ListTag)map.keySet().stream().map(compoundTagx::getString).map(NbtHelper::method_32267).collect(Collectors.toCollection(ListTag::new))
					)
					.collect(Collectors.toCollection(ListTag::new))
			);
			compoundTag.remove("palette");
		} else {
			compoundTag.put("palette", (Tag)map.values().stream().collect(Collectors.toCollection(ListTag::new)));
		}

		if (compoundTag.contains("data", 9)) {
			Object2IntMap<String> object2IntMap = new Object2IntOpenHashMap<>();
			object2IntMap.defaultReturnValue(-1);

			for (int i = 0; i < listTag.size(); i++) {
				object2IntMap.put(listTag.getString(i), i);
			}

			ListTag listTag2 = compoundTag.getList("data", 10);

			for (int j = 0; j < listTag2.size(); j++) {
				CompoundTag compoundTag2 = listTag2.getCompound(j);
				String string = compoundTag2.getString("state");
				int k = object2IntMap.getInt(string);
				if (k == -1) {
					throw new IllegalStateException("Entry " + string + " missing from palette");
				}

				compoundTag2.putInt("state", k);
			}

			compoundTag.put("blocks", listTag2);
			compoundTag.remove("data");
		}

		return compoundTag;
	}

	@VisibleForTesting
	static String method_32277(CompoundTag compoundTag) {
		StringBuilder stringBuilder = new StringBuilder(compoundTag.getString("Name"));
		if (compoundTag.contains("Properties", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Properties");
			String string = (String)compoundTag2.getKeys()
				.stream()
				.sorted()
				.map(stringx -> stringx + ':' + compoundTag2.get(stringx).asString())
				.collect(Collectors.joining(","));
			stringBuilder.append('{').append(string).append('}');
		}

		return stringBuilder.toString();
	}

	@VisibleForTesting
	static CompoundTag method_32267(String string) {
		CompoundTag compoundTag = new CompoundTag();
		int i = string.indexOf(123);
		String string2;
		if (i >= 0) {
			string2 = string.substring(0, i);
			CompoundTag compoundTag2 = new CompoundTag();
			if (i + 2 <= string.length()) {
				String string3 = string.substring(i + 1, string.indexOf(125, i));
				field_27818.split(string3).forEach(string2x -> {
					List<String> list = field_27819.splitToList(string2x);
					if (list.size() == 2) {
						compoundTag2.putString((String)list.get(0), (String)list.get(1));
					} else {
						LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", string);
					}
				});
				compoundTag.put("Properties", compoundTag2);
			}
		} else {
			string2 = string;
		}

		compoundTag.putString("Name", string2);
		return compoundTag;
	}
}
