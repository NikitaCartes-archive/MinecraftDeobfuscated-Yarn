package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static GameProfile toGameProfile(CompoundTag tag) {
		String string = null;
		String string2 = null;
		if (tag.contains("Name", 8)) {
			string = tag.getString("Name");
		}

		if (tag.contains("Id", 8)) {
			string2 = tag.getString("Id");
		}

		try {
			UUID uUID;
			try {
				uUID = UUID.fromString(string2);
			} catch (Throwable var12) {
				uUID = null;
			}

			GameProfile gameProfile = new GameProfile(uUID, string);
			if (tag.contains("Properties", 10)) {
				CompoundTag compoundTag = tag.getCompound("Properties");

				for (String string3 : compoundTag.getKeys()) {
					ListTag listTag = compoundTag.getList(string3, 10);

					for (int i = 0; i < listTag.size(); i++) {
						CompoundTag compoundTag2 = listTag.getCompound(i);
						String string4 = compoundTag2.getString("Value");
						if (compoundTag2.contains("Signature", 8)) {
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4, compoundTag2.getString("Signature")));
						} else {
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4));
						}
					}
				}
			}

			return gameProfile;
		} catch (Throwable var13) {
			return null;
		}
	}

	public static CompoundTag fromGameProfile(CompoundTag tag, GameProfile profile) {
		if (!ChatUtil.isEmpty(profile.getName())) {
			tag.putString("Name", profile.getName());
		}

		if (profile.getId() != null) {
			tag.putString("Id", profile.getId().toString());
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
					Tag tag2 = listTag.method_10534(i);
					boolean bl = false;

					for (int j = 0; j < listTag2.size(); j++) {
						if (matches(tag2, listTag2.method_10534(j), equalValue)) {
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

	public static CompoundTag fromUuid(UUID uuid) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putLong("M", uuid.getMostSignificantBits());
		compoundTag.putLong("L", uuid.getLeastSignificantBits());
		return compoundTag;
	}

	public static UUID toUuid(CompoundTag tag) {
		return new UUID(tag.getLong("M"), tag.getLong("L"));
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
				StateManager<Block, BlockState> stateManager = block.getStateFactory();

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

	private static <S extends State<S>, T extends Comparable<T>> S withProperty(
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

	public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion) {
		return update(fixer, fixTypes, tag, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
	}

	public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion, int currentVersion) {
		return (CompoundTag)fixer.update(fixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, tag), oldVersion, currentVersion).getValue();
	}
}