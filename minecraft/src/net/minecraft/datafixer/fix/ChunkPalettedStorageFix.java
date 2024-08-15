package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
	private static final int field_29871 = 128;
	private static final int field_29872 = 64;
	private static final int field_29873 = 32;
	private static final int field_29874 = 16;
	private static final int field_29875 = 8;
	private static final int field_29876 = 4;
	private static final int field_29877 = 2;
	private static final int field_29878 = 1;
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29870 = 4096;

	public ChunkPalettedStorageFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public static String getName(Dynamic<?> dynamic) {
		return dynamic.get("Name").asString("");
	}

	public static String getProperty(Dynamic<?> dynamic, String propertyKey) {
		return dynamic.get("Properties").get(propertyKey).asString("");
	}

	public static int addTo(Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, Dynamic<?> dynamic) {
		int i = int2ObjectBiMap.getRawId(dynamic);
		if (i == -1) {
			i = int2ObjectBiMap.add(dynamic);
		}

		return i;
	}

	private Dynamic<?> fixChunk(Dynamic<?> chunkDynamic) {
		Optional<? extends Dynamic<?>> optional = chunkDynamic.get("Level").result();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()
			? chunkDynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: chunkDynamic;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
	}

	public static int getSideToUpgradeFlag(boolean west, boolean east, boolean north, boolean south) {
		int i = 0;
		if (north) {
			if (east) {
				i |= 2;
			} else if (west) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (south) {
			if (west) {
				i |= 32;
			} else if (east) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (east) {
			i |= 4;
		} else if (west) {
			i |= 64;
		}

		return i;
	}

	static class ChunkNibbleArray {
		private static final int CONTENTS_LENGTH = 2048;
		private static final int field_29880 = 4;
		private final byte[] contents;

		public ChunkNibbleArray() {
			this.contents = new byte[2048];
		}

		public ChunkNibbleArray(byte[] contents) {
			this.contents = contents;
			if (contents.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + contents.length);
			}
		}

		public int get(int x, int y, int z) {
			int i = this.getRawIndex(y << 8 | z << 4 | x);
			return this.usesLowNibble(y << 8 | z << 4 | x) ? this.contents[i] & 15 : this.contents[i] >> 4 & 15;
		}

		private boolean usesLowNibble(int index) {
			return (index & 1) == 0;
		}

		private int getRawIndex(int index) {
			return index >> 1;
		}
	}

	public static enum Facing {
		DOWN(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		UP(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		NORTH(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		SOUTH(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		WEST(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.X),
		EAST(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.X);

		private final ChunkPalettedStorageFix.Facing.Axis axis;
		private final ChunkPalettedStorageFix.Facing.Direction direction;

		private Facing(final ChunkPalettedStorageFix.Facing.Direction direction, final ChunkPalettedStorageFix.Facing.Axis axis) {
			this.axis = axis;
			this.direction = direction;
		}

		public ChunkPalettedStorageFix.Facing.Direction getDirection() {
			return this.direction;
		}

		public ChunkPalettedStorageFix.Facing.Axis getAxis() {
			return this.axis;
		}

		public static enum Axis {
			X,
			Y,
			Z;
		}

		public static enum Direction {
			POSITIVE(1),
			NEGATIVE(-1);

			private final int offset;

			private Direction(final int offset) {
				this.offset = offset;
			}

			public int getOffset() {
				return this.offset;
			}
		}
	}

	static final class Level {
		private int sidesToUpgrade;
		private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
		private final Dynamic<?> level;
		private final int x;
		private final int z;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

		public Level(Dynamic<?> chunkTag) {
			this.level = chunkTag;
			this.x = chunkTag.get("xPos").asInt(0) << 4;
			this.z = chunkTag.get("zPos").asInt(0) << 4;
			chunkTag.get("TileEntities").asStreamOpt().ifSuccess(stream -> stream.forEach(blockEntityTag -> {
					int ix = blockEntityTag.get("x").asInt(0) - this.x & 15;
					int jx = blockEntityTag.get("y").asInt(0);
					int k = blockEntityTag.get("z").asInt(0) - this.z & 15;
					int l = jx << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, blockEntityTag) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, ix, jx, k);
					}
				}));
			boolean bl = chunkTag.get("convertedFromAlphaFormat").asBoolean(false);
			chunkTag.get("Sections").asStreamOpt().ifSuccess(stream -> stream.forEach(sectionTag -> {
					ChunkPalettedStorageFix.Section sectionx = new ChunkPalettedStorageFix.Section(sectionTag);
					this.sidesToUpgrade = sectionx.visit(this.sidesToUpgrade);
					this.sections[sectionx.y] = sectionx;
				}));

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					for (Entry<IntList> entry : section.inPlaceUpdates.int2ObjectEntrySet()) {
						int i = section.y << 12;
						switch (entry.getIntKey()) {
							case 2:
								for (int j : (IntList)entry.getValue()) {
									j |= i;
									Dynamic<?> dynamic = this.getBlock(j);
									if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.class_9855.field_52405);
										}
									}
								}
								break;
							case 3:
								for (int jxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxxx);
									if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxxxx, ChunkPalettedStorageFix.class_9855.field_52404);
										}
									}
								}
								break;
							case 25:
								for (int jxxxxx : (IntList)entry.getValue()) {
									jxxxxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxxxxx);
									if (dynamic != null) {
										String string = Boolean.toString(dynamic.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic.get("note").asInt(0), 0), 24);
										this.setBlock(
											jxxxxx,
											(Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52416
												.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.class_9855.field_52416.get("false0"))
										);
									}
								}
								break;
							case 26:
								for (int jxxxx : (IntList)entry.getValue()) {
									jxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxx);
									if (dynamic != null) {
										int k = dynamic.get("color").asInt(0);
										if (k != 14 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "occupied")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "part")
												+ k;
											if (ChunkPalettedStorageFix.class_9855.field_52418.containsKey(string2)) {
												this.setBlock(jxxxx, (Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52418.get(string2));
											}
										}
									}
								}
								break;
							case 64:
							case 71:
							case 193:
							case 194:
							case 195:
							case 196:
							case 197:
								for (int jxxx : (IntList)entry.getValue()) {
									jxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxx);
									if (ChunkPalettedStorageFix.getName(dynamic).endsWith("_door")) {
										Dynamic<?> dynamic2 = this.getBlock(jxxx);
										if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
											int k = adjacentTo(jxxx, ChunkPalettedStorageFix.Facing.UP);
											Dynamic<?> dynamic3 = this.getBlock(k);
											String string4 = ChunkPalettedStorageFix.getName(dynamic2);
											if (string4.equals(ChunkPalettedStorageFix.getName(dynamic3))) {
												String string5 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing");
												String string6 = ChunkPalettedStorageFix.getProperty(dynamic2, "open");
												String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic3, "hinge");
												String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic3, "powered");
												this.setBlock(jxxx, (Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52415.get(string4 + string5 + "lower" + string7 + string6 + string8));
												this.setBlock(k, (Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52415.get(string4 + string5 + "upper" + string7 + string6 + string8));
											}
										}
									}
								}
								break;
							case 86:
								for (int jxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxx);
									if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxx, ChunkPalettedStorageFix.Facing.DOWN)));
										if ("minecraft:grass_block".equals(string) || "minecraft:dirt".equals(string)) {
											this.setBlock(jxxxxxxxx, ChunkPalettedStorageFix.class_9855.field_52403);
										}
									}
								}
								break;
							case 110:
								for (int jxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxx);
									if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxx, ChunkPalettedStorageFix.class_9855.field_52406);
										}
									}
								}
								break;
							case 140:
								for (int jxx : (IntList)entry.getValue()) {
									jxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxx);
									if (dynamic != null) {
										String string = dynamic.get("Item").asString("") + dynamic.get("Data").asInt(0);
										this.setBlock(
											jxx,
											(Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52413
												.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.class_9855.field_52413.get("minecraft:air0"))
										);
									}
								}
								break;
							case 144:
								for (int jxxxxxx : (IntList)entry.getValue()) {
									jxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxx);
									if (dynamic != null) {
										String string = String.valueOf(dynamic.get("SkullType").asInt(0));
										String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(jxxxxxx), "facing");
										String string2;
										if (!"up".equals(string3) && !"down".equals(string3)) {
											string2 = string + string3;
										} else {
											string2 = string + dynamic.get("Rot").asInt(0);
										}

										dynamic.remove("SkullType");
										dynamic.remove("facing");
										dynamic.remove("Rot");
										this.setBlock(
											jxxxxxx,
											(Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52414
												.getOrDefault(string2, (Dynamic)ChunkPalettedStorageFix.class_9855.field_52414.get("0north"))
										);
									}
								}
								break;
							case 175:
								for (int jx : (IntList)entry.getValue()) {
									jx |= i;
									Dynamic<?> dynamic = this.getBlock(jx);
									if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic, "half"))) {
										Dynamic<?> dynamic2 = this.getBlock(adjacentTo(jx, ChunkPalettedStorageFix.Facing.DOWN));
										String string3 = ChunkPalettedStorageFix.getName(dynamic2);
										switch (string3) {
											case "minecraft:sunflower":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52407);
												break;
											case "minecraft:lilac":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52408);
												break;
											case "minecraft:tall_grass":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52409);
												break;
											case "minecraft:large_fern":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52410);
												break;
											case "minecraft:rose_bush":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52411);
												break;
											case "minecraft:peony":
												this.setBlock(jx, ChunkPalettedStorageFix.class_9855.field_52412);
										}
									}
								}
								break;
							case 176:
							case 177:
								for (int jxxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxxxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxxxxx);
									if (dynamic != null) {
										int k = dynamic.get("Base").asInt(0);
										if (k != 15 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, entry.getIntKey() == 176 ? "rotation" : "facing") + "_" + k;
											if (ChunkPalettedStorageFix.class_9855.field_52419.containsKey(string2)) {
												this.setBlock(jxxxxxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.class_9855.field_52419.get(string2));
											}
										}
									}
								}
						}
					}
				}
			}
		}

		@Nullable
		private Dynamic<?> getBlockEntity(int packedLocalPos) {
			return this.blockEntities.get(packedLocalPos);
		}

		@Nullable
		private Dynamic<?> removeBlockEntity(int packedLocalPos) {
			return this.blockEntities.remove(packedLocalPos);
		}

		public static int adjacentTo(int packedLocalPos, ChunkPalettedStorageFix.Facing direction) {
			int var10000;
			switch (direction.getAxis()) {
				case X: {
					int i = (packedLocalPos & 15) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 15 ? packedLocalPos & -16 | i : -1;
					break;
				}
				case Y: {
					int i = (packedLocalPos >> 8) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 255 ? packedLocalPos & 0xFF | i << 8 : -1;
					break;
				}
				case Z: {
					int i = (packedLocalPos >> 4 & 15) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 15 ? packedLocalPos & -241 | i << 4 : -1;
					break;
				}
				default:
					throw new MatchException(null, null);
			}

			return var10000;
		}

		private void setBlock(int packedLocalPos, Dynamic<?> dynamic) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				if (section != null) {
					section.setBlock(packedLocalPos & 4095, dynamic);
				}
			}
		}

		@Nullable
		private ChunkPalettedStorageFix.Section getSection(int packedLocalPos) {
			int i = packedLocalPos >> 12;
			return i < this.sections.length ? this.sections[i] : null;
		}

		public Dynamic<?> getBlock(int packedLocalPos) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				return section == null ? ChunkPalettedStorageFix.class_9855.field_52420 : section.getBlock(packedLocalPos & 4095);
			} else {
				return ChunkPalettedStorageFix.class_9855.field_52420;
			}
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.level;
			if (this.blockEntities.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			List<Dynamic<?>> list = Lists.<Dynamic<?>>newArrayList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					list.add(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
			dynamic3 = dynamic3.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = Int2ObjectBiMap.create(32);
		private final List<Dynamic<?>> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap<>();
		final IntList innerPositions = new IntArrayList();
		public final int y;
		private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
		private final int[] states = new int[4096];

		public Section(Dynamic<?> section) {
			this.paletteData = Lists.<Dynamic<?>>newArrayList();
			this.section = section;
			this.y = section.get("Y").asInt(0);
			this.hasBlocks = section.get("Blocks").result().isPresent();
		}

		public Dynamic<?> getBlock(int index) {
			if (index >= 0 && index <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
				return dynamic == null ? ChunkPalettedStorageFix.class_9855.field_52420 : dynamic;
			} else {
				return ChunkPalettedStorageFix.class_9855.field_52420;
			}
		}

		public void setBlock(int pos, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.class_9855.field_52420 : dynamic);
			}

			this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int sidesToUpgrade) {
			if (!this.hasBlocks) {
				return sidesToUpgrade;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.class_9855.field_52420);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.class_9855.field_52420);
				this.paletteData.add(ChunkPalettedStorageFix.class_9855.field_52420);

				for (int i = 0; i < 4096; i++) {
					int j = i & 15;
					int k = i >> 8 & 15;
					int l = i >> 4 & 15;
					int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer.get(i) & 255) << 4 | chunkNibbleArray.get(j, k, l);
					if (ChunkPalettedStorageFix.class_9855.field_52402.get(m >> 4)) {
						this.addInPlaceUpdate(m >> 4, i);
					}

					if (ChunkPalettedStorageFix.class_9855.field_52401.get(m >> 4)) {
						int n = ChunkPalettedStorageFix.getSideToUpgradeFlag(j == 0, j == 15, l == 0, l == 15);
						if (n == 0) {
							this.innerPositions.add(i);
						} else {
							sidesToUpgrade |= n;
						}
					}

					this.setBlock(i, BlockStateFlattening.lookupState(m));
				}

				return sidesToUpgrade;
			}
		}

		private void addInPlaceUpdate(int section, int index) {
			IntList intList = this.inPlaceUpdates.get(section);
			if (intList == null) {
				intList = new IntArrayList();
				this.inPlaceUpdates.put(section, intList);
			}

			intList.add(index);
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.section;
			if (!this.hasBlocks) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					wordPackedArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}

	static class class_9855 {
		static final BitSet field_52401 = new BitSet(256);
		static final BitSet field_52402 = new BitSet(256);
		static final Dynamic<?> field_52403 = FixUtil.createBlockState("minecraft:pumpkin");
		static final Dynamic<?> field_52404 = FixUtil.createBlockState("minecraft:podzol", Map.of("snowy", "true"));
		static final Dynamic<?> field_52405 = FixUtil.createBlockState("minecraft:grass_block", Map.of("snowy", "true"));
		static final Dynamic<?> field_52406 = FixUtil.createBlockState("minecraft:mycelium", Map.of("snowy", "true"));
		static final Dynamic<?> field_52407 = FixUtil.createBlockState("minecraft:sunflower", Map.of("half", "upper"));
		static final Dynamic<?> field_52408 = FixUtil.createBlockState("minecraft:lilac", Map.of("half", "upper"));
		static final Dynamic<?> field_52409 = FixUtil.createBlockState("minecraft:tall_grass", Map.of("half", "upper"));
		static final Dynamic<?> field_52410 = FixUtil.createBlockState("minecraft:large_fern", Map.of("half", "upper"));
		static final Dynamic<?> field_52411 = FixUtil.createBlockState("minecraft:rose_bush", Map.of("half", "upper"));
		static final Dynamic<?> field_52412 = FixUtil.createBlockState("minecraft:peony", Map.of("half", "upper"));
		static final Map<String, Dynamic<?>> field_52413 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			hashMap.put("minecraft:air0", FixUtil.createBlockState("minecraft:flower_pot"));
			hashMap.put("minecraft:red_flower0", FixUtil.createBlockState("minecraft:potted_poppy"));
			hashMap.put("minecraft:red_flower1", FixUtil.createBlockState("minecraft:potted_blue_orchid"));
			hashMap.put("minecraft:red_flower2", FixUtil.createBlockState("minecraft:potted_allium"));
			hashMap.put("minecraft:red_flower3", FixUtil.createBlockState("minecraft:potted_azure_bluet"));
			hashMap.put("minecraft:red_flower4", FixUtil.createBlockState("minecraft:potted_red_tulip"));
			hashMap.put("minecraft:red_flower5", FixUtil.createBlockState("minecraft:potted_orange_tulip"));
			hashMap.put("minecraft:red_flower6", FixUtil.createBlockState("minecraft:potted_white_tulip"));
			hashMap.put("minecraft:red_flower7", FixUtil.createBlockState("minecraft:potted_pink_tulip"));
			hashMap.put("minecraft:red_flower8", FixUtil.createBlockState("minecraft:potted_oxeye_daisy"));
			hashMap.put("minecraft:yellow_flower0", FixUtil.createBlockState("minecraft:potted_dandelion"));
			hashMap.put("minecraft:sapling0", FixUtil.createBlockState("minecraft:potted_oak_sapling"));
			hashMap.put("minecraft:sapling1", FixUtil.createBlockState("minecraft:potted_spruce_sapling"));
			hashMap.put("minecraft:sapling2", FixUtil.createBlockState("minecraft:potted_birch_sapling"));
			hashMap.put("minecraft:sapling3", FixUtil.createBlockState("minecraft:potted_jungle_sapling"));
			hashMap.put("minecraft:sapling4", FixUtil.createBlockState("minecraft:potted_acacia_sapling"));
			hashMap.put("minecraft:sapling5", FixUtil.createBlockState("minecraft:potted_dark_oak_sapling"));
			hashMap.put("minecraft:red_mushroom0", FixUtil.createBlockState("minecraft:potted_red_mushroom"));
			hashMap.put("minecraft:brown_mushroom0", FixUtil.createBlockState("minecraft:potted_brown_mushroom"));
			hashMap.put("minecraft:deadbush0", FixUtil.createBlockState("minecraft:potted_dead_bush"));
			hashMap.put("minecraft:tallgrass2", FixUtil.createBlockState("minecraft:potted_fern"));
			hashMap.put("minecraft:cactus0", FixUtil.createBlockState("minecraft:potted_cactus"));
		});
		static final Map<String, Dynamic<?>> field_52414 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			method_61376(hashMap, 0, "skeleton", "skull");
			method_61376(hashMap, 1, "wither_skeleton", "skull");
			method_61376(hashMap, 2, "zombie", "head");
			method_61376(hashMap, 3, "player", "head");
			method_61376(hashMap, 4, "creeper", "head");
			method_61376(hashMap, 5, "dragon", "head");
		});
		static final Map<String, Dynamic<?>> field_52415 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			method_61377(hashMap, "oak_door");
			method_61377(hashMap, "iron_door");
			method_61377(hashMap, "spruce_door");
			method_61377(hashMap, "birch_door");
			method_61377(hashMap, "jungle_door");
			method_61377(hashMap, "acacia_door");
			method_61377(hashMap, "dark_oak_door");
		});
		static final Map<String, Dynamic<?>> field_52416 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			for (int i = 0; i < 26; i++) {
				hashMap.put("true" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "true", "note", String.valueOf(i))));
				hashMap.put("false" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "false", "note", String.valueOf(i))));
			}
		});
		private static final Int2ObjectMap<String> field_52417 = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
			int2ObjectOpenHashMap.put(0, "white");
			int2ObjectOpenHashMap.put(1, "orange");
			int2ObjectOpenHashMap.put(2, "magenta");
			int2ObjectOpenHashMap.put(3, "light_blue");
			int2ObjectOpenHashMap.put(4, "yellow");
			int2ObjectOpenHashMap.put(5, "lime");
			int2ObjectOpenHashMap.put(6, "pink");
			int2ObjectOpenHashMap.put(7, "gray");
			int2ObjectOpenHashMap.put(8, "light_gray");
			int2ObjectOpenHashMap.put(9, "cyan");
			int2ObjectOpenHashMap.put(10, "purple");
			int2ObjectOpenHashMap.put(11, "blue");
			int2ObjectOpenHashMap.put(12, "brown");
			int2ObjectOpenHashMap.put(13, "green");
			int2ObjectOpenHashMap.put(14, "red");
			int2ObjectOpenHashMap.put(15, "black");
		});
		static final Map<String, Dynamic<?>> field_52418 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			for (Entry<String> entry : field_52417.int2ObjectEntrySet()) {
				if (!Objects.equals(entry.getValue(), "red")) {
					method_61375(hashMap, entry.getIntKey(), (String)entry.getValue());
				}
			}
		});
		static final Map<String, Dynamic<?>> field_52419 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
			for (Entry<String> entry : field_52417.int2ObjectEntrySet()) {
				if (!Objects.equals(entry.getValue(), "white")) {
					method_61379(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
				}
			}
		});
		static final Dynamic<?> field_52420 = FixUtil.createBlockState("minecraft:air");

		private class_9855() {
		}

		private static void method_61376(Map<String, Dynamic<?>> map, int i, String string, String string2) {
			map.put(i + "north", FixUtil.createBlockState("minecraft:" + string + "_wall_" + string2, Map.of("facing", "north")));
			map.put(i + "east", FixUtil.createBlockState("minecraft:" + string + "_wall_" + string2, Map.of("facing", "east")));
			map.put(i + "south", FixUtil.createBlockState("minecraft:" + string + "_wall_" + string2, Map.of("facing", "south")));
			map.put(i + "west", FixUtil.createBlockState("minecraft:" + string + "_wall_" + string2, Map.of("facing", "west")));

			for (int j = 0; j < 16; j++) {
				map.put("" + i + j, FixUtil.createBlockState("minecraft:" + string + "_" + string2, Map.of("rotation", String.valueOf(j))));
			}
		}

		private static void method_61377(Map<String, Dynamic<?>> map, String string) {
			String string2 = "minecraft:" + string;
			map.put(
				"minecraft:" + string + "eastlowerleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastlowerleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastlowerlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastlowerlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastlowerrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastlowerrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastlowerrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastlowerrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastupperleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastupperleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastupperlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastupperlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastupperrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastupperrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "eastupperrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "eastupperrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northlowerleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northlowerleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northlowerlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northlowerlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northlowerrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northlowerrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northlowerrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northlowerrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northupperleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northupperleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northupperlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northupperlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northupperrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northupperrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "northupperrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "northupperrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southlowerleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southlowerleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southlowerlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southlowerlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southlowerrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southlowerrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southlowerrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southlowerrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southupperleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southupperleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southupperlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southupperlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southupperrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southupperrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "southupperrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "southupperrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westlowerleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westlowerleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westlowerlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westlowerlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westlowerrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westlowerrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westlowerrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westlowerrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westupperleftfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westupperleftfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westupperlefttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westupperlefttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westupperrightfalsefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westupperrightfalsetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + string + "westupperrighttruefalse",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + string + "westupperrighttruetrue",
				FixUtil.createBlockState(string2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
		}

		private static void method_61375(Map<String, Dynamic<?>> map, int i, String string) {
			map.put("southfalsefoot" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "south", "occupied", "false", "part", "foot")));
			map.put("westfalsefoot" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "west", "occupied", "false", "part", "foot")));
			map.put("northfalsefoot" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "north", "occupied", "false", "part", "foot")));
			map.put("eastfalsefoot" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "east", "occupied", "false", "part", "foot")));
			map.put("southfalsehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "south", "occupied", "false", "part", "head")));
			map.put("westfalsehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "west", "occupied", "false", "part", "head")));
			map.put("northfalsehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "north", "occupied", "false", "part", "head")));
			map.put("eastfalsehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "east", "occupied", "false", "part", "head")));
			map.put("southtruehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "south", "occupied", "true", "part", "head")));
			map.put("westtruehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "west", "occupied", "true", "part", "head")));
			map.put("northtruehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "north", "occupied", "true", "part", "head")));
			map.put("easttruehead" + i, FixUtil.createBlockState("minecraft:" + string + "_bed", Map.of("facing", "east", "occupied", "true", "part", "head")));
		}

		private static void method_61379(Map<String, Dynamic<?>> map, int i, String string) {
			for (int j = 0; j < 16; j++) {
				map.put(j + "_" + i, FixUtil.createBlockState("minecraft:" + string + "_banner", Map.of("rotation", String.valueOf(j))));
			}

			map.put("north_" + i, FixUtil.createBlockState("minecraft:" + string + "_wall_banner", Map.of("facing", "north")));
			map.put("south_" + i, FixUtil.createBlockState("minecraft:" + string + "_wall_banner", Map.of("facing", "south")));
			map.put("west_" + i, FixUtil.createBlockState("minecraft:" + string + "_wall_banner", Map.of("facing", "west")));
			map.put("east_" + i, FixUtil.createBlockState("minecraft:" + string + "_wall_banner", Map.of("facing", "east")));
		}

		static {
			field_52402.set(2);
			field_52402.set(3);
			field_52402.set(110);
			field_52402.set(140);
			field_52402.set(144);
			field_52402.set(25);
			field_52402.set(86);
			field_52402.set(26);
			field_52402.set(176);
			field_52402.set(177);
			field_52402.set(175);
			field_52402.set(64);
			field_52402.set(71);
			field_52402.set(193);
			field_52402.set(194);
			field_52402.set(195);
			field_52402.set(196);
			field_52402.set(197);
			field_52401.set(54);
			field_52401.set(146);
			field_52401.set(25);
			field_52401.set(26);
			field_52401.set(51);
			field_52401.set(53);
			field_52401.set(67);
			field_52401.set(108);
			field_52401.set(109);
			field_52401.set(114);
			field_52401.set(128);
			field_52401.set(134);
			field_52401.set(135);
			field_52401.set(136);
			field_52401.set(156);
			field_52401.set(163);
			field_52401.set(164);
			field_52401.set(180);
			field_52401.set(203);
			field_52401.set(55);
			field_52401.set(85);
			field_52401.set(113);
			field_52401.set(188);
			field_52401.set(189);
			field_52401.set(190);
			field_52401.set(191);
			field_52401.set(192);
			field_52401.set(93);
			field_52401.set(94);
			field_52401.set(101);
			field_52401.set(102);
			field_52401.set(160);
			field_52401.set(106);
			field_52401.set(107);
			field_52401.set(183);
			field_52401.set(184);
			field_52401.set(185);
			field_52401.set(186);
			field_52401.set(187);
			field_52401.set(132);
			field_52401.set(139);
			field_52401.set(199);
		}
	}
}
