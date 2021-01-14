package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static GameProfile toGameProfile(NbtCompound compound) {
		String string = null;
		UUID uUID = null;
		if (compound.contains("Name", 8)) {
			string = compound.getString("Name");
		}

		if (compound.containsUuid("Id")) {
			uUID = compound.getUuid("Id");
		}

		try {
			GameProfile gameProfile = new GameProfile(uUID, string);
			if (compound.contains("Properties", 10)) {
				NbtCompound nbtCompound = compound.getCompound("Properties");

				for (String string2 : nbtCompound.getKeys()) {
					NbtList nbtList = nbtCompound.getList(string2, 10);

					for (int i = 0; i < nbtList.size(); i++) {
						NbtCompound nbtCompound2 = nbtList.getCompound(i);
						String string3 = nbtCompound2.getString("Value");
						if (nbtCompound2.contains("Signature", 8)) {
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

	public static NbtCompound writeGameProfile(NbtCompound compound, GameProfile profile) {
		if (!ChatUtil.isEmpty(profile.getName())) {
			compound.putString("Name", profile.getName());
		}

		if (profile.getId() != null) {
			compound.putUuid("Id", profile.getId());
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

			compound.put("Properties", nbtCompound);
		}

		return compound;
	}

	@VisibleForTesting
	public static boolean matches(@Nullable NbtElement standard, @Nullable NbtElement subject, boolean equalValue) {
		if (standard == subject) {
			return true;
		} else if (standard == null) {
			return true;
		} else if (subject == null) {
			return false;
		} else if (!standard.getClass().equals(subject.getClass())) {
			return false;
		} else if (standard instanceof NbtCompound) {
			NbtCompound nbtCompound = (NbtCompound)standard;
			NbtCompound nbtCompound2 = (NbtCompound)subject;

			for (String string : nbtCompound.getKeys()) {
				NbtElement nbtElement = nbtCompound.get(string);
				if (!matches(nbtElement, nbtCompound2.get(string), equalValue)) {
					return false;
				}
			}

			return true;
		} else if (standard instanceof NbtList && equalValue) {
			NbtList nbtList = (NbtList)standard;
			NbtList nbtList2 = (NbtList)subject;
			if (nbtList.isEmpty()) {
				return nbtList2.isEmpty();
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtElement nbtElement2 = nbtList.get(i);
					boolean bl = false;

					for (int j = 0; j < nbtList2.size(); j++) {
						if (matches(nbtElement2, nbtList2.get(j), equalValue)) {
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
	public static NbtIntArray fromUuid(UUID uuid) {
		return new NbtIntArray(DynamicSerializableUuid.toIntArray(uuid));
	}

	/**
	 * Deserializes an NBT element into a {@link UUID}.
	 * The NBT element's data must have the same structure as the output of {@link #fromUuid}.
	 * 
	 * @throws IllegalArgumentException if {@code element} is not a valid representation of a UUID
	 * @since 20w10a
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

	public static BlockPos toBlockPos(NbtCompound compound) {
		return new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
	}

	public static NbtCompound fromBlockPos(BlockPos pos) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("X", pos.getX());
		nbtCompound.putInt("Y", pos.getY());
		nbtCompound.putInt("Z", pos.getZ());
		return nbtCompound;
	}

	public static BlockState toBlockState(NbtCompound compound) {
		if (!compound.contains("Name", 8)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Block block = Registry.BLOCK.get(new Identifier(compound.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (compound.contains("Properties", 10)) {
				NbtCompound nbtCompound = compound.getCompound("Properties");
				StateManager<Block, BlockState> stateManager = block.getStateManager();

				for (String string : nbtCompound.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = withProperty(blockState, property, string, nbtCompound, compound);
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

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
		return property.name((T)value);
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
}
