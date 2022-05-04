package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

/**
 * Helper methods for handling NBT.
 */
public final class NbtHelper {
	private static final Comparator<NbtList> BLOCK_POS_COMPARATOR = Comparator.comparingInt(nbt -> nbt.getInt(1))
		.thenComparingInt(nbt -> nbt.getInt(0))
		.thenComparingInt(nbt -> nbt.getInt(2));
	private static final Comparator<NbtList> ENTITY_POS_COMPARATOR = Comparator.comparingDouble(nbt -> nbt.getDouble(1))
		.thenComparingDouble(nbt -> nbt.getDouble(0))
		.thenComparingDouble(nbt -> nbt.getDouble(2));
	public static final String DATA_KEY = "data";
	private static final char LEFT_CURLY_BRACKET = '{';
	private static final char RIGHT_CURLY_BRACKET = '}';
	private static final String COMMA = ",";
	private static final char COLON = ':';
	private static final Splitter COMMA_SPLITTER = Splitter.on(",");
	private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_33229 = 2;
	private static final int field_33230 = -1;

	private NbtHelper() {
	}

	/**
	 * {@return the game profile converted from {@code nbt}}
	 * 
	 * @see #writeGameProfile(NbtCompound, GameProfile)
	 */
	@Nullable
	public static GameProfile toGameProfile(NbtCompound nbt) {
		String string = null;
		UUID uUID = null;
		if (nbt.contains("Name", NbtElement.STRING_TYPE)) {
			string = nbt.getString("Name");
		}

		if (nbt.containsUuid("Id")) {
			uUID = nbt.getUuid("Id");
		}

		try {
			GameProfile gameProfile = new GameProfile(uUID, string);
			if (nbt.contains("Properties", NbtElement.COMPOUND_TYPE)) {
				NbtCompound nbtCompound = nbt.getCompound("Properties");

				for (String string2 : nbtCompound.getKeys()) {
					NbtList nbtList = nbtCompound.getList(string2, NbtElement.COMPOUND_TYPE);

					for (int i = 0; i < nbtList.size(); i++) {
						NbtCompound nbtCompound2 = nbtList.getCompound(i);
						String string3 = nbtCompound2.getString("Value");
						if (nbtCompound2.contains("Signature", NbtElement.STRING_TYPE)) {
							gameProfile.getProperties().put(string2, new com.mojang.authlib.properties.Property(string2, string3, nbtCompound2.getString("Signature")));
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

	/**
	 * Writes the game profile to {@code nbt}. This modifies the passed compound.
	 * 
	 * @return the compound with the serialized game profile
	 * @see #toGameProfile(NbtCompound)
	 */
	public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
		if (!StringHelper.isEmpty(profile.getName())) {
			nbt.putString("Name", profile.getName());
		}

		if (profile.getId() != null) {
			nbt.putUuid("Id", profile.getId());
		}

		if (!profile.getProperties().isEmpty()) {
			NbtCompound nbtCompound = new NbtCompound();

			for (String string : profile.getProperties().keySet()) {
				NbtList nbtList = new NbtList();

				for (com.mojang.authlib.properties.Property property : profile.getProperties().get(string)) {
					NbtCompound nbtCompound2 = new NbtCompound();
					nbtCompound2.putString("Value", property.getValue());
					if (property.hasSignature()) {
						nbtCompound2.putString("Signature", property.getSignature());
					}

					nbtList.add(nbtCompound2);
				}

				nbtCompound.put(string, nbtList);
			}

			nbt.put("Properties", nbtCompound);
		}

		return nbt;
	}

	/**
	 * {@return whether {@code standard} is a subset of {@code subject}}
	 * 
	 * <p>Elements are matched based on the following order:
	 * <ol>
	 * <li>Passing the same reference to both parameters will return {@code true}.</li>
	 * <li>If {@code standard} is {@code null}, return {@code true}.</li>
	 * <li>If {@code subject} is {@code null}, return {@code false}.</li>
	 * <li>If the types of {@code standard} and {@code subject} are different,
	 * return {@code false}.</li>
	 * <li>If {@code standard} is {@link NbtCompound}, return {@code true} if all keys
	 * in the {@code standard} exist in {@code subject} and the values match (comparing
	 * recursively.)</li>
	 * <li>If {@code standard} is {@link NbtList} and {@code ignoreListOrder} is {@code true},
	 * return {@code true} if both lists are empty, or if there exists a "matching" value
	 * in {@code subject} for all values of {@code standard} (that is, if {@code standard}
	 * is a subset of {@code subject}, ignoring duplicates.), otherwise {@code false}.
	 * This means that the comparison ignores the ordering of the lists.</li>
	 * <li>Otherwise, return {@code standard.equals(subject)}.</li>
	 * </ol>
	 * 
	 * @param standard the standard (also called as "template" or "schema") element
	 * @param subject the element to test
	 * @param ignoreListOrder whether to ignore ordering for {@link NbtList}
	 */
	@VisibleForTesting
	public static boolean matches(@Nullable NbtElement standard, @Nullable NbtElement subject, boolean ignoreListOrder) {
		if (standard == subject) {
			return true;
		} else if (standard == null) {
			return true;
		} else if (subject == null) {
			return false;
		} else if (!standard.getClass().equals(subject.getClass())) {
			return false;
		} else if (standard instanceof NbtCompound nbtCompound) {
			NbtCompound nbtCompound2 = (NbtCompound)subject;

			for (String string : nbtCompound.getKeys()) {
				NbtElement nbtElement = nbtCompound.get(string);
				if (!matches(nbtElement, nbtCompound2.get(string), ignoreListOrder)) {
					return false;
				}
			}

			return true;
		} else if (standard instanceof NbtList && ignoreListOrder) {
			NbtList nbtList = (NbtList)standard;
			NbtList nbtList2 = (NbtList)subject;
			if (nbtList.isEmpty()) {
				return nbtList2.isEmpty();
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtElement nbtElement2 = nbtList.get(i);
					boolean bl = false;

					for (int j = 0; j < nbtList2.size(); j++) {
						if (matches(nbtElement2, nbtList2.get(j), ignoreListOrder)) {
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
	 * @see #toUuid(NbtElement)
	 */
	public static NbtIntArray fromUuid(UUID uuid) {
		return new NbtIntArray(DynamicSerializableUuid.toIntArray(uuid));
	}

	/**
	 * Deserializes an NBT element into a {@link UUID}.
	 * The NBT element's data must have the same structure as the output of {@link #fromUuid}.
	 * 
	 * @throws IllegalArgumentException if {@code element} is not a valid representation of a UUID
	 * @since 20w10a
	 * @see #fromUuid(UUID)
	 */
	public static UUID toUuid(NbtElement element) {
		if (element.getNbtType() != NbtIntArray.TYPE) {
			throw new IllegalArgumentException(
				"Expected UUID-Tag to be of type " + NbtIntArray.TYPE.getCrashReportName() + ", but found " + element.getNbtType().getCrashReportName() + "."
			);
		} else {
			int[] is = ((NbtIntArray)element).getIntArray();
			if (is.length != 4) {
				throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
			} else {
				return DynamicSerializableUuid.toUuid(is);
			}
		}
	}

	/**
	 * {@return the block position from the {@code nbt}}
	 * 
	 * @see #fromBlockPos(BlockPos)
	 */
	public static BlockPos toBlockPos(NbtCompound nbt) {
		return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
	}

	/**
	 * {@return the serialized block position}
	 * 
	 * @see #toBlockPos(NbtCompound)
	 */
	public static NbtCompound fromBlockPos(BlockPos pos) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("X", pos.getX());
		nbtCompound.putInt("Y", pos.getY());
		nbtCompound.putInt("Z", pos.getZ());
		return nbtCompound;
	}

	/**
	 * {@return the block state from the {@code nbt}}
	 * 
	 * <p>This returns the default state for {@link net.minecraft.block.Blocks#AIR}
	 * if the block name is not present.
	 * 
	 * @see #fromBlockState(BlockState)
	 */
	public static BlockState toBlockState(NbtCompound nbt) {
		if (!nbt.contains("Name", NbtElement.STRING_TYPE)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Block block = Registry.BLOCK.get(new Identifier(nbt.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (nbt.contains("Properties", NbtElement.COMPOUND_TYPE)) {
				NbtCompound nbtCompound = nbt.getCompound("Properties");
				StateManager<Block, BlockState> stateManager = block.getStateManager();

				for (String string : nbtCompound.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = withProperty(blockState, property, string, nbtCompound, nbt);
					}
				}
			}

			return blockState;
		}
	}

	private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(
		S state, Property<T> property, String key, NbtCompound properties, NbtCompound root
	) {
		Optional<T> optional = property.parse(properties.getString(key));
		if (optional.isPresent()) {
			return state.with(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, properties.getString(key), root.toString());
			return state;
		}
	}

	/**
	 * {@return the serialized block state}
	 * 
	 * @see #toBlockState(NbtCompound)
	 */
	public static NbtCompound fromBlockState(BlockState state) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Registry.BLOCK.getId(state.getBlock()).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
		if (!immutableMap.isEmpty()) {
			NbtCompound nbtCompound2 = new NbtCompound();

			for (Entry<Property<?>, Comparable<?>> entry : immutableMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();
				nbtCompound2.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			nbtCompound.put("Properties", nbtCompound2);
		}

		return nbtCompound;
	}

	/**
	 * {@return the serialized fluid state}
	 */
	public static NbtCompound fromFluidState(FluidState state) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Registry.FLUID.getId(state.getFluid()).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
		if (!immutableMap.isEmpty()) {
			NbtCompound nbtCompound2 = new NbtCompound();

			for (Entry<Property<?>, Comparable<?>> entry : immutableMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();
				nbtCompound2.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			nbtCompound.put("Properties", nbtCompound2);
		}

		return nbtCompound;
	}

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
	}

	/**
	 * {@return the human-readable, non-deserializable representation of {@code nbt}}
	 * 
	 * <p>This does not include contents of {@link NbtByteArray}, {@link NbtIntArray},
	 * and {@link NbtLongArray}. To include them, call
	 * {@link #toFormattedString(NbtElement, boolean)} with {@code withArrayContents}
	 * parameter set to true.
	 * 
	 * @see #toFormattedString(NbtElement, boolean)
	 */
	public static String toFormattedString(NbtElement nbt) {
		return toFormattedString(nbt, false);
	}

	/**
	 * {@return the human-readable, non-deserializable representation of {@code nbt}}
	 * 
	 * @param withArrayContents whether to include contents of {@link NbtByteArray}, {@link NbtIntArray},
	 * and {@link NbtLongArray}
	 */
	public static String toFormattedString(NbtElement nbt, boolean withArrayContents) {
		return appendFormattedString(new StringBuilder(), nbt, 0, withArrayContents).toString();
	}

	public static StringBuilder appendFormattedString(StringBuilder stringBuilder, NbtElement nbt, int depth, boolean withArrayContents) {
		switch (nbt.getType()) {
			case 0:
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 8:
				stringBuilder.append(nbt);
				break;
			case 7:
				NbtByteArray nbtByteArray = (NbtByteArray)nbt;
				byte[] bs = nbtByteArray.getByteArray();
				int ix = bs.length;
				appendIndent(depth, stringBuilder).append("byte[").append(ix).append("] {\n");
				if (withArrayContents) {
					appendIndent(depth + 1, stringBuilder);

					for (int j = 0; j < bs.length; j++) {
						if (j != 0) {
							stringBuilder.append(',');
						}

						if (j % 16 == 0 && j / 16 > 0) {
							stringBuilder.append('\n');
							if (j < bs.length) {
								appendIndent(depth + 1, stringBuilder);
							}
						} else if (j != 0) {
							stringBuilder.append(' ');
						}

						stringBuilder.append(String.format("0x%02X", bs[j] & 255));
					}
				} else {
					appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
				}

				stringBuilder.append('\n');
				appendIndent(depth, stringBuilder).append('}');
				break;
			case 9:
				NbtList nbtList = (NbtList)nbt;
				int k = nbtList.size();
				int i = nbtList.getHeldType();
				String string = i == 0 ? "undefined" : NbtTypes.byId(i).getCommandFeedbackName();
				appendIndent(depth, stringBuilder).append("list<").append(string).append(">[").append(k).append("] [");
				if (k != 0) {
					stringBuilder.append('\n');
				}

				for (int l = 0; l < k; l++) {
					if (l != 0) {
						stringBuilder.append(",\n");
					}

					appendIndent(depth + 1, stringBuilder);
					appendFormattedString(stringBuilder, nbtList.get(l), depth + 1, withArrayContents);
				}

				if (k != 0) {
					stringBuilder.append('\n');
				}

				appendIndent(depth, stringBuilder).append(']');
				break;
			case 10:
				NbtCompound nbtCompound = (NbtCompound)nbt;
				List<String> list = Lists.<String>newArrayList(nbtCompound.getKeys());
				Collections.sort(list);
				appendIndent(depth, stringBuilder).append('{');
				if (stringBuilder.length() - stringBuilder.lastIndexOf("\n") > 2 * (depth + 1)) {
					stringBuilder.append('\n');
					appendIndent(depth + 1, stringBuilder);
				}

				int ix = list.stream().mapToInt(String::length).max().orElse(0);
				String stringx = Strings.repeat(" ", ix);

				for (int l = 0; l < list.size(); l++) {
					if (l != 0) {
						stringBuilder.append(",\n");
					}

					String string2 = (String)list.get(l);
					appendIndent(depth + 1, stringBuilder).append('"').append(string2).append('"').append(stringx, 0, stringx.length() - string2.length()).append(": ");
					appendFormattedString(stringBuilder, nbtCompound.get(string2), depth + 1, withArrayContents);
				}

				if (!list.isEmpty()) {
					stringBuilder.append('\n');
				}

				appendIndent(depth, stringBuilder).append('}');
				break;
			case 11:
				NbtIntArray nbtIntArray = (NbtIntArray)nbt;
				int[] is = nbtIntArray.getIntArray();
				int ix = 0;

				for (int m : is) {
					ix = Math.max(ix, String.format("%X", m).length());
				}

				int j = is.length;
				appendIndent(depth, stringBuilder).append("int[").append(j).append("] {\n");
				if (withArrayContents) {
					appendIndent(depth + 1, stringBuilder);

					for (int l = 0; l < is.length; l++) {
						if (l != 0) {
							stringBuilder.append(',');
						}

						if (l % 16 == 0 && l / 16 > 0) {
							stringBuilder.append('\n');
							if (l < is.length) {
								appendIndent(depth + 1, stringBuilder);
							}
						} else if (l != 0) {
							stringBuilder.append(' ');
						}

						stringBuilder.append(String.format("0x%0" + ix + "X", is[l]));
					}
				} else {
					appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
				}

				stringBuilder.append('\n');
				appendIndent(depth, stringBuilder).append('}');
				break;
			case 12:
				NbtLongArray nbtLongArray = (NbtLongArray)nbt;
				long[] ls = nbtLongArray.getLongArray();
				long n = 0L;

				for (long o : ls) {
					n = Math.max(n, (long)String.format("%X", o).length());
				}

				long p = (long)ls.length;
				appendIndent(depth, stringBuilder).append("long[").append(p).append("] {\n");
				if (withArrayContents) {
					appendIndent(depth + 1, stringBuilder);

					for (int m = 0; m < ls.length; m++) {
						if (m != 0) {
							stringBuilder.append(',');
						}

						if (m % 16 == 0 && m / 16 > 0) {
							stringBuilder.append('\n');
							if (m < ls.length) {
								appendIndent(depth + 1, stringBuilder);
							}
						} else if (m != 0) {
							stringBuilder.append(' ');
						}

						stringBuilder.append(String.format("0x%0" + n + "X", ls[m]));
					}
				} else {
					appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
				}

				stringBuilder.append('\n');
				appendIndent(depth, stringBuilder).append('}');
				break;
			default:
				stringBuilder.append("<UNKNOWN :(>");
		}

		return stringBuilder;
	}

	private static StringBuilder appendIndent(int depth, StringBuilder stringBuilder) {
		int i = stringBuilder.lastIndexOf("\n") + 1;
		int j = stringBuilder.length() - i;

		for (int k = 0; k < 2 * depth - j; k++) {
			stringBuilder.append(' ');
		}

		return stringBuilder;
	}

	/**
	 * Uses the data fixer to update an NBT compound object to the latest data version.
	 * 
	 * @param fixer the data fixer
	 * @param fixTypes the fix types
	 * @param compound the NBT compound object to fix
	 * @param oldVersion the data version of the NBT compound object
	 */
	public static NbtCompound update(DataFixer fixer, DataFixTypes fixTypes, NbtCompound compound, int oldVersion) {
		return update(fixer, fixTypes, compound, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
	}

	/**
	 * Uses the data fixer to update an NBT compound object.
	 * 
	 * @param fixer the data fixer
	 * @param fixTypes the fix types
	 * @param compound the NBT compound object to fix
	 * @param oldVersion the data version of the NBT compound object
	 * @param targetVersion the data version to update the NBT compound object to
	 */
	public static NbtCompound update(DataFixer fixer, DataFixTypes fixTypes, NbtCompound compound, int oldVersion, int targetVersion) {
		return (NbtCompound)fixer.update(fixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, compound), oldVersion, targetVersion).getValue();
	}

	/**
	 * {@return the pretty-printed text representation of {@code element}}
	 * 
	 * @see net.minecraft.nbt.visitor.NbtTextFormatter
	 */
	public static Text toPrettyPrintedText(NbtElement element) {
		return new NbtTextFormatter("", 0).apply(element);
	}

	/**
	 * {@return the string representation of {@code compound} as used
	 * by the NBT provider in the data generator}
	 * 
	 * <p>The passed {@code compound} will be sorted and modified in-place
	 * to make it more human-readable e.g. by converting {@link NbtCompound}
	 * in the {@code palettes} {@code NbtList} to its short string
	 * representation. Therefore the returned value is not an accurate
	 * representation of the original NBT.
	 * 
	 * @see net.minecraft.data.dev.NbtProvider
	 * @see #fromNbtProviderString(String)
	 */
	public static String toNbtProviderString(NbtCompound compound) {
		return new NbtOrderedStringFormatter().apply(toNbtProviderFormat(compound));
	}

	/**
	 * {@return the {@code string} parsed as an NBT provider-formatted
	 * NBT compound}
	 * 
	 * <p>This method first parses the string as an NBT, then performs
	 * several conversions from human-readable {@link NbtCompound} items
	 * to the actual values used in-game.
	 * 
	 * @see net.minecraft.data.SnbtProvider
	 * @see #toNbtProviderString
	 */
	public static NbtCompound fromNbtProviderString(String string) throws CommandSyntaxException {
		return fromNbtProviderFormat(StringNbtReader.parse(string));
	}

	@VisibleForTesting
	static NbtCompound toNbtProviderFormat(NbtCompound compound) {
		boolean bl = compound.contains("palettes", NbtElement.LIST_TYPE);
		NbtList nbtList;
		if (bl) {
			nbtList = compound.getList("palettes", NbtElement.LIST_TYPE).getList(0);
		} else {
			nbtList = compound.getList("palette", NbtElement.COMPOUND_TYPE);
		}

		NbtList nbtList2 = (NbtList)nbtList.stream()
			.map(NbtCompound.class::cast)
			.map(NbtHelper::toNbtProviderFormattedPalette)
			.map(NbtString::of)
			.collect(Collectors.toCollection(NbtList::new));
		compound.put("palette", nbtList2);
		if (bl) {
			NbtList nbtList3 = new NbtList();
			NbtList nbtList4 = compound.getList("palettes", NbtElement.LIST_TYPE);
			nbtList4.stream().map(NbtList.class::cast).forEach(nbt -> {
				NbtCompound nbtCompound = new NbtCompound();

				for (int i = 0; i < nbt.size(); i++) {
					nbtCompound.putString(nbtList2.getString(i), toNbtProviderFormattedPalette(nbt.getCompound(i)));
				}

				nbtList3.add(nbtCompound);
			});
			compound.put("palettes", nbtList3);
		}

		if (compound.contains("entities", NbtElement.COMPOUND_TYPE)) {
			NbtList nbtList3 = compound.getList("entities", NbtElement.COMPOUND_TYPE);
			NbtList nbtList4 = (NbtList)nbtList3.stream()
				.map(NbtCompound.class::cast)
				.sorted(Comparator.comparing(nbt -> nbt.getList("pos", NbtElement.DOUBLE_TYPE), ENTITY_POS_COMPARATOR))
				.collect(Collectors.toCollection(NbtList::new));
			compound.put("entities", nbtList4);
		}

		NbtList nbtList3 = (NbtList)compound.getList("blocks", NbtElement.COMPOUND_TYPE)
			.stream()
			.map(NbtCompound.class::cast)
			.sorted(Comparator.comparing(nbt -> nbt.getList("pos", NbtElement.INT_TYPE), BLOCK_POS_COMPARATOR))
			.peek(nbt -> nbt.putString("state", nbtList2.getString(nbt.getInt("state"))))
			.collect(Collectors.toCollection(NbtList::new));
		compound.put("data", nbtList3);
		compound.remove("blocks");
		return compound;
	}

	@VisibleForTesting
	static NbtCompound fromNbtProviderFormat(NbtCompound compound) {
		NbtList nbtList = compound.getList("palette", NbtElement.STRING_TYPE);
		Map<String, NbtElement> map = (Map<String, NbtElement>)nbtList.stream()
			.map(NbtString.class::cast)
			.map(NbtString::asString)
			.collect(ImmutableMap.toImmutableMap(Function.identity(), NbtHelper::fromNbtProviderFormattedPalette));
		if (compound.contains("palettes", NbtElement.LIST_TYPE)) {
			compound.put(
				"palettes",
				(NbtElement)compound.getList("palettes", NbtElement.COMPOUND_TYPE)
					.stream()
					.map(NbtCompound.class::cast)
					.map(
						nbt -> (NbtList)map.keySet().stream().map(nbt::getString).map(NbtHelper::fromNbtProviderFormattedPalette).collect(Collectors.toCollection(NbtList::new))
					)
					.collect(Collectors.toCollection(NbtList::new))
			);
			compound.remove("palette");
		} else {
			compound.put("palette", (NbtElement)map.values().stream().collect(Collectors.toCollection(NbtList::new)));
		}

		if (compound.contains("data", NbtElement.LIST_TYPE)) {
			Object2IntMap<String> object2IntMap = new Object2IntOpenHashMap<>();
			object2IntMap.defaultReturnValue(-1);

			for (int i = 0; i < nbtList.size(); i++) {
				object2IntMap.put(nbtList.getString(i), i);
			}

			NbtList nbtList2 = compound.getList("data", NbtElement.COMPOUND_TYPE);

			for (int j = 0; j < nbtList2.size(); j++) {
				NbtCompound nbtCompound = nbtList2.getCompound(j);
				String string = nbtCompound.getString("state");
				int k = object2IntMap.getInt(string);
				if (k == -1) {
					throw new IllegalStateException("Entry " + string + " missing from palette");
				}

				nbtCompound.putInt("state", k);
			}

			compound.put("blocks", nbtList2);
			compound.remove("data");
		}

		return compound;
	}

	@VisibleForTesting
	static String toNbtProviderFormattedPalette(NbtCompound compound) {
		StringBuilder stringBuilder = new StringBuilder(compound.getString("Name"));
		if (compound.contains("Properties", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = compound.getCompound("Properties");
			String string = (String)nbtCompound.getKeys().stream().sorted().map(key -> key + ":" + nbtCompound.get(key).asString()).collect(Collectors.joining(","));
			stringBuilder.append('{').append(string).append('}');
		}

		return stringBuilder.toString();
	}

	@VisibleForTesting
	static NbtCompound fromNbtProviderFormattedPalette(String string) {
		NbtCompound nbtCompound = new NbtCompound();
		int i = string.indexOf(123);
		String string2;
		if (i >= 0) {
			string2 = string.substring(0, i);
			NbtCompound nbtCompound2 = new NbtCompound();
			if (i + 2 <= string.length()) {
				String string3 = string.substring(i + 1, string.indexOf(125, i));
				COMMA_SPLITTER.split(string3).forEach(property -> {
					List<String> list = COLON_SPLITTER.splitToList(property);
					if (list.size() == 2) {
						nbtCompound2.putString((String)list.get(0), (String)list.get(1));
					} else {
						LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", string);
					}
				});
				nbtCompound.put("Properties", nbtCompound2);
			}
		} else {
			string2 = string;
		}

		nbtCompound.putString("Name", string2);
		return nbtCompound;
	}
}
