package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;

public class MapState extends PersistentState {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int SIZE = 128;
	private static final int SIZE_HALF = 64;
	public static final int MAX_SCALE = 4;
	public static final int MAX_DECORATIONS = 256;
	/**
	 * The scaled center coordinate of the map state on the X axis.
	 * <p>
	 * Always {@code 0} for the client.
	 */
	public final int centerX;
	/**
	 * The scaled center coordinate of the map state on the Z axis.
	 * <p>
	 * Always {@code 0} for the client.
	 */
	public final int centerZ;
	public final RegistryKey<World> dimension;
	private final boolean showDecorations;
	private final boolean unlimitedTracking;
	public final byte scale;
	public byte[] colors = new byte[16384];
	public final boolean locked;
	private final List<MapState.PlayerUpdateTracker> updateTrackers = Lists.<MapState.PlayerUpdateTracker>newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.<PlayerEntity, MapState.PlayerUpdateTracker>newHashMap();
	/**
	 * The banner markers to track in world.
	 * <p>
	 * Empty for the client.
	 */
	private final Map<String, MapBannerMarker> banners = Maps.<String, MapBannerMarker>newHashMap();
	final Map<String, MapDecoration> decorations = Maps.<String, MapDecoration>newLinkedHashMap();
	private final Map<String, MapFrameMarker> frames = Maps.<String, MapFrameMarker>newHashMap();
	private int decorationCount;

	public static PersistentState.Type<MapState> getPersistentStateType() {
		return new PersistentState.Type<>(() -> {
			throw new IllegalStateException("Should never create an empty map saved data");
		}, MapState::fromNbt, DataFixTypes.SAVED_DATA_MAP_DATA);
	}

	private MapState(int centerX, int centerZ, byte scale, boolean showDecorations, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension) {
		this.scale = scale;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.dimension = dimension;
		this.showDecorations = showDecorations;
		this.unlimitedTracking = unlimitedTracking;
		this.locked = locked;
		this.markDirty();
	}

	/**
	 * Creates a new map state instance.
	 * 
	 * @param centerX the absolute center X-coordinate
	 * @param centerZ the absolute center Z-coordinate
	 */
	public static MapState of(double centerX, double centerZ, byte scale, boolean showDecorations, boolean unlimitedTracking, RegistryKey<World> dimension) {
		int i = 128 * (1 << scale);
		int j = MathHelper.floor((centerX + 64.0) / (double)i);
		int k = MathHelper.floor((centerZ + 64.0) / (double)i);
		int l = j * i + i / 2 - 64;
		int m = k * i + i / 2 - 64;
		return new MapState(l, m, scale, showDecorations, unlimitedTracking, false, dimension);
	}

	/**
	 * Creates a new map state instance for the client.
	 * <p>
	 * The client is not aware of the coordinates of the map state so its center coordinates will always be {@code (0, 0)}.
	 */
	public static MapState of(byte scale, boolean locked, RegistryKey<World> dimension) {
		return new MapState(0, 0, scale, false, false, locked, dimension);
	}

	public static MapState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		RegistryKey<World> registryKey = (RegistryKey<World>)DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, nbt.get("dimension")))
			.resultOrPartial(LOGGER::error)
			.orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + nbt.get("dimension")));
		int i = nbt.getInt("xCenter");
		int j = nbt.getInt("zCenter");
		byte b = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
		boolean bl = !nbt.contains("trackingPosition", NbtElement.BYTE_TYPE) || nbt.getBoolean("trackingPosition");
		boolean bl2 = nbt.getBoolean("unlimitedTracking");
		boolean bl3 = nbt.getBoolean("locked");
		MapState mapState = new MapState(i, j, b, bl, bl2, bl3, registryKey);
		byte[] bs = nbt.getByteArray("colors");
		if (bs.length == 16384) {
			mapState.colors = bs;
		}

		RegistryOps<NbtElement> registryOps = registryLookup.getOps(NbtOps.INSTANCE);

		for (MapBannerMarker mapBannerMarker : (List)MapBannerMarker.LIST_CODEC
			.parse(registryOps, nbt.get("banners"))
			.resultOrPartial(banner -> LOGGER.warn("Failed to parse map banner: '{}'", banner))
			.orElse(List.of())) {
			mapState.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
			mapState.addDecoration(
				mapBannerMarker.getDecorationType(),
				null,
				mapBannerMarker.getKey(),
				(double)mapBannerMarker.pos().getX(),
				(double)mapBannerMarker.pos().getZ(),
				180.0,
				(Text)mapBannerMarker.name().orElse(null)
			);
		}

		NbtList nbtList = nbt.getList("frames", NbtElement.COMPOUND_TYPE);

		for (int k = 0; k < nbtList.size(); k++) {
			MapFrameMarker mapFrameMarker = MapFrameMarker.fromNbt(nbtList.getCompound(k));
			if (mapFrameMarker != null) {
				mapState.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
				mapState.addDecoration(
					MapDecorationTypes.FRAME,
					null,
					"frame-" + mapFrameMarker.getEntityId(),
					(double)mapFrameMarker.getPos().getX(),
					(double)mapFrameMarker.getPos().getZ(),
					(double)mapFrameMarker.getRotation(),
					null
				);
			}
		}

		return mapState;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		Identifier.CODEC
			.encodeStart(NbtOps.INSTANCE, this.dimension.getValue())
			.resultOrPartial(LOGGER::error)
			.ifPresent(nbtElement -> nbt.put("dimension", nbtElement));
		nbt.putInt("xCenter", this.centerX);
		nbt.putInt("zCenter", this.centerZ);
		nbt.putByte("scale", this.scale);
		nbt.putByteArray("colors", this.colors);
		nbt.putBoolean("trackingPosition", this.showDecorations);
		nbt.putBoolean("unlimitedTracking", this.unlimitedTracking);
		nbt.putBoolean("locked", this.locked);
		RegistryOps<NbtElement> registryOps = registryLookup.getOps(NbtOps.INSTANCE);
		nbt.put("banners", MapBannerMarker.LIST_CODEC.encodeStart(registryOps, List.copyOf(this.banners.values())).getOrThrow());
		NbtList nbtList = new NbtList();

		for (MapFrameMarker mapFrameMarker : this.frames.values()) {
			nbtList.add(mapFrameMarker.toNbt());
		}

		nbt.put("frames", nbtList);
		return nbt;
	}

	public MapState copy() {
		MapState mapState = new MapState(this.centerX, this.centerZ, this.scale, this.showDecorations, this.unlimitedTracking, true, this.dimension);
		mapState.banners.putAll(this.banners);
		mapState.decorations.putAll(this.decorations);
		mapState.decorationCount = this.decorationCount;
		System.arraycopy(this.colors, 0, mapState.colors, 0, this.colors.length);
		mapState.markDirty();
		return mapState;
	}

	/**
	 * Creates a new map state which is a zoomed out version of the current one.
	 * <p>
	 * The scale of the new map state is {@code currentScale + zoomOutScale} and clamped between {@code 0} and {@code 4}.
	 * <p>
	 * The colors are not copied, neither are the decorations.
	 */
	public MapState zoomOut() {
		return of(
			(double)this.centerX, (double)this.centerZ, (byte)MathHelper.clamp(this.scale + 1, 0, 4), this.showDecorations, this.unlimitedTracking, this.dimension
		);
	}

	private static Predicate<ItemStack> getEqualPredicate(ItemStack stack) {
		MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
		return other -> other == stack ? true : other.isOf(stack.getItem()) && Objects.equals(mapIdComponent, other.get(DataComponentTypes.MAP_ID));
	}

	public void update(PlayerEntity player, ItemStack stack) {
		if (!this.updateTrackersByPlayer.containsKey(player)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		Predicate<ItemStack> predicate = getEqualPredicate(stack);
		if (!player.getInventory().contains(predicate)) {
			this.removeDecoration(player.getName().getString());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			String string = playerUpdateTracker2.player.getName().getString();
			if (!playerUpdateTracker2.player.isRemoved() && (playerUpdateTracker2.player.getInventory().contains(predicate) || stack.isInFrame())) {
				if (!stack.isInFrame() && playerUpdateTracker2.player.getWorld().getRegistryKey() == this.dimension && this.showDecorations) {
					this.addDecoration(
						MapDecorationTypes.PLAYER,
						playerUpdateTracker2.player.getWorld(),
						string,
						playerUpdateTracker2.player.getX(),
						playerUpdateTracker2.player.getZ(),
						(double)playerUpdateTracker2.player.getYaw(),
						null
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
				this.removeDecoration(string);
			}
		}

		if (stack.isInFrame() && this.showDecorations) {
			ItemFrameEntity itemFrameEntity = stack.getFrame();
			BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
			MapFrameMarker mapFrameMarker = (MapFrameMarker)this.frames.get(MapFrameMarker.getKey(blockPos));
			if (mapFrameMarker != null && itemFrameEntity.getId() != mapFrameMarker.getEntityId() && this.frames.containsKey(mapFrameMarker.getKey())) {
				this.removeDecoration("frame-" + mapFrameMarker.getEntityId());
			}

			MapFrameMarker mapFrameMarker2 = new MapFrameMarker(blockPos, itemFrameEntity.getHorizontalFacing().getHorizontal() * 90, itemFrameEntity.getId());
			this.addDecoration(
				MapDecorationTypes.FRAME,
				player.getWorld(),
				"frame-" + itemFrameEntity.getId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.getHorizontalFacing().getHorizontal() * 90),
				null
			);
			this.frames.put(mapFrameMarker2.getKey(), mapFrameMarker2);
		}

		MapDecorationsComponent mapDecorationsComponent = stack.getOrDefault(DataComponentTypes.MAP_DECORATIONS, MapDecorationsComponent.DEFAULT);
		if (!this.decorations.keySet().containsAll(mapDecorationsComponent.decorations().keySet())) {
			mapDecorationsComponent.decorations().forEach((id, decoration) -> {
				if (!this.decorations.containsKey(id)) {
					this.addDecoration(decoration.type(), player.getWorld(), id, decoration.x(), decoration.z(), (double)decoration.rotation(), null);
				}
			});
		}
	}

	private void removeDecoration(String id) {
		MapDecoration mapDecoration = (MapDecoration)this.decorations.remove(id);
		if (mapDecoration != null && mapDecoration.type().value().trackCount()) {
			this.decorationCount--;
		}

		this.markDecorationsDirty();
	}

	public static void addDecorationsNbt(ItemStack stack, BlockPos pos, String id, RegistryEntry<MapDecorationType> decorationType) {
		MapDecorationsComponent.Decoration decoration = new MapDecorationsComponent.Decoration(decorationType, (double)pos.getX(), (double)pos.getZ(), 180.0F);
		stack.apply(DataComponentTypes.MAP_DECORATIONS, MapDecorationsComponent.DEFAULT, decorations -> decorations.with(id, decoration));
		if (decorationType.value().hasMapColor()) {
			stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(decorationType.value().mapColor()));
		}
	}

	private void addDecoration(
		RegistryEntry<MapDecorationType> type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text
	) {
		int i = 1 << this.scale;
		float f = (float)(x - (double)this.centerX) / (float)i;
		float g = (float)(z - (double)this.centerZ) / (float)i;
		byte b = (byte)((int)((double)(f * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(g * 2.0F) + 0.5));
		int j = 63;
		byte d;
		if (f >= -63.0F && g >= -63.0F && f <= 63.0F && g <= 63.0F) {
			rotation += rotation < 0.0 ? -8.0 : 8.0;
			d = (byte)((int)(rotation * 16.0 / 360.0));
			if (this.dimension == World.NETHER && world != null) {
				int k = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
				d = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
			}
		} else {
			if (!type.matches(MapDecorationTypes.PLAYER)) {
				this.removeDecoration(key);
				return;
			}

			int k = 320;
			if (Math.abs(f) < 320.0F && Math.abs(g) < 320.0F) {
				type = MapDecorationTypes.PLAYER_OFF_MAP;
			} else {
				if (!this.unlimitedTracking) {
					this.removeDecoration(key);
					return;
				}

				type = MapDecorationTypes.PLAYER_OFF_LIMITS;
			}

			d = 0;
			if (f <= -63.0F) {
				b = -128;
			}

			if (g <= -63.0F) {
				c = -128;
			}

			if (f >= 63.0F) {
				b = 127;
			}

			if (g >= 63.0F) {
				c = 127;
			}
		}

		MapDecoration mapDecoration = new MapDecoration(type, b, c, d, Optional.ofNullable(text));
		MapDecoration mapDecoration2 = (MapDecoration)this.decorations.put(key, mapDecoration);
		if (!mapDecoration.equals(mapDecoration2)) {
			if (mapDecoration2 != null && mapDecoration2.type().value().trackCount()) {
				this.decorationCount--;
			}

			if (type.value().trackCount()) {
				this.decorationCount++;
			}

			this.markDecorationsDirty();
		}
	}

	@Nullable
	public Packet<?> getPlayerMarkerPacket(MapIdComponent mapId, PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(mapId);
	}

	private void markDirty(int x, int z) {
		this.markDirty();

		for (MapState.PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
			playerUpdateTracker.markDirty(x, z);
		}
	}

	private void markDecorationsDirty() {
		this.markDirty();
		this.updateTrackers.forEach(MapState.PlayerUpdateTracker::markDecorationsDirty);
	}

	public MapState.PlayerUpdateTracker getPlayerSyncData(PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		if (playerUpdateTracker == null) {
			playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		return playerUpdateTracker;
	}

	public boolean addBanner(WorldAccess world, BlockPos pos) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getZ() + 0.5;
		int i = 1 << this.scale;
		double f = (d - (double)this.centerX) / (double)i;
		double g = (e - (double)this.centerZ) / (double)i;
		int j = 63;
		if (f >= -63.0 && g >= -63.0 && f <= 63.0 && g <= 63.0) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(world, pos);
			if (mapBannerMarker == null) {
				return false;
			}

			if (this.banners.remove(mapBannerMarker.getKey(), mapBannerMarker)) {
				this.removeDecoration(mapBannerMarker.getKey());
				return true;
			}

			if (!this.decorationCountNotLessThan(256)) {
				this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
				this.addDecoration(mapBannerMarker.getDecorationType(), world, mapBannerMarker.getKey(), d, e, 180.0, (Text)mapBannerMarker.name().orElse(null));
				return true;
			}
		}

		return false;
	}

	public void removeBanner(BlockView world, int x, int z) {
		Iterator<MapBannerMarker> iterator = this.banners.values().iterator();

		while (iterator.hasNext()) {
			MapBannerMarker mapBannerMarker = (MapBannerMarker)iterator.next();
			if (mapBannerMarker.pos().getX() == x && mapBannerMarker.pos().getZ() == z) {
				MapBannerMarker mapBannerMarker2 = MapBannerMarker.fromWorldBlock(world, mapBannerMarker.pos());
				if (!mapBannerMarker.equals(mapBannerMarker2)) {
					iterator.remove();
					this.removeDecoration(mapBannerMarker.getKey());
				}
			}
		}
	}

	public Collection<MapBannerMarker> getBanners() {
		return this.banners.values();
	}

	public void removeFrame(BlockPos pos, int id) {
		this.removeDecoration("frame-" + id);
		this.frames.remove(MapFrameMarker.getKey(pos));
	}

	/**
	 * Sets the color at the specified coordinates if the current color is different.
	 * 
	 * @return {@code true} if the color has been updated, else {@code false}
	 */
	public boolean putColor(int x, int z, byte color) {
		byte b = this.colors[x + z * 128];
		if (b != color) {
			this.setColor(x, z, color);
			return true;
		} else {
			return false;
		}
	}

	public void setColor(int x, int z, byte color) {
		this.colors[x + z * 128] = color;
		this.markDirty(x, z);
	}

	public boolean hasExplorationMapDecoration() {
		for (MapDecoration mapDecoration : this.decorations.values()) {
			if (mapDecoration.type().value().explorationMapElement()) {
				return true;
			}
		}

		return false;
	}

	public void replaceDecorations(List<MapDecoration> decorations) {
		this.decorations.clear();
		this.decorationCount = 0;

		for (int i = 0; i < decorations.size(); i++) {
			MapDecoration mapDecoration = (MapDecoration)decorations.get(i);
			this.decorations.put("icon-" + i, mapDecoration);
			if (mapDecoration.type().value().trackCount()) {
				this.decorationCount++;
			}
		}
	}

	public Iterable<MapDecoration> getDecorations() {
		return this.decorations.values();
	}

	public boolean decorationCountNotLessThan(int decorationCount) {
		return this.decorationCount >= decorationCount;
	}

	public class PlayerUpdateTracker {
		public final PlayerEntity player;
		private boolean dirty = true;
		private int startX;
		private int startZ;
		private int endX = 127;
		private int endZ = 127;
		private boolean decorationsDirty = true;
		private int emptyPacketsRequested;
		public int field_131;

		PlayerUpdateTracker(PlayerEntity player) {
			this.player = player;
		}

		private MapState.UpdateData getMapUpdateData() {
			int i = this.startX;
			int j = this.startZ;
			int k = this.endX + 1 - this.startX;
			int l = this.endZ + 1 - this.startZ;
			byte[] bs = new byte[k * l];

			for (int m = 0; m < k; m++) {
				for (int n = 0; n < l; n++) {
					bs[m + n * k] = MapState.this.colors[i + m + (j + n) * 128];
				}
			}

			return new MapState.UpdateData(i, j, k, l, bs);
		}

		@Nullable
		Packet<?> getPacket(MapIdComponent mapId) {
			MapState.UpdateData updateData;
			if (this.dirty) {
				this.dirty = false;
				updateData = this.getMapUpdateData();
			} else {
				updateData = null;
			}

			Collection<MapDecoration> collection;
			if (this.decorationsDirty && this.emptyPacketsRequested++ % 5 == 0) {
				this.decorationsDirty = false;
				collection = MapState.this.decorations.values();
			} else {
				collection = null;
			}

			return collection == null && updateData == null ? null : new MapUpdateS2CPacket(mapId, MapState.this.scale, MapState.this.locked, collection, updateData);
		}

		void markDirty(int startX, int startZ) {
			if (this.dirty) {
				this.startX = Math.min(this.startX, startX);
				this.startZ = Math.min(this.startZ, startZ);
				this.endX = Math.max(this.endX, startX);
				this.endZ = Math.max(this.endZ, startZ);
			} else {
				this.dirty = true;
				this.startX = startX;
				this.startZ = startZ;
				this.endX = startX;
				this.endZ = startZ;
			}
		}

		private void markDecorationsDirty() {
			this.decorationsDirty = true;
		}
	}

	public static record UpdateData(int startX, int startZ, int width, int height, byte[] colors) {
		public static final PacketCodec<ByteBuf, Optional<MapState.UpdateData>> CODEC = PacketCodec.ofStatic(MapState.UpdateData::encode, MapState.UpdateData::decode);

		private static void encode(ByteBuf buf, Optional<MapState.UpdateData> updateData) {
			if (updateData.isPresent()) {
				MapState.UpdateData updateData2 = (MapState.UpdateData)updateData.get();
				buf.writeByte(updateData2.width);
				buf.writeByte(updateData2.height);
				buf.writeByte(updateData2.startX);
				buf.writeByte(updateData2.startZ);
				PacketByteBuf.writeByteArray(buf, updateData2.colors);
			} else {
				buf.writeByte(0);
			}
		}

		private static Optional<MapState.UpdateData> decode(ByteBuf buf) {
			int i = buf.readUnsignedByte();
			if (i > 0) {
				int j = buf.readUnsignedByte();
				int k = buf.readUnsignedByte();
				int l = buf.readUnsignedByte();
				byte[] bs = PacketByteBuf.readByteArray(buf);
				return Optional.of(new MapState.UpdateData(k, l, i, j, bs));
			} else {
				return Optional.empty();
			}
		}

		public void setColorsTo(MapState mapState) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					mapState.setColor(this.startX + i, this.startZ + j, this.colors[i + j * this.width]);
				}
			}
		}
	}
}
