package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.MapUpdateClientPacket;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.PersistedState;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.sortme.MapBannerInstance;
import net.minecraft.sortme.MapFrameInstance;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class MapState extends PersistedState {
	public int field_116;
	public int field_115;
	public DimensionType field_118;
	public boolean showIcons;
	public boolean field_113;
	public byte scale;
	public byte[] colorArray = new byte[16384];
	public List<MapState.class_23> field_112 = Lists.<MapState.class_23>newArrayList();
	private final Map<PlayerEntity, MapState.class_23> field_120 = Maps.<PlayerEntity, MapState.class_23>newHashMap();
	private final Map<String, MapBannerInstance> field_123 = Maps.<String, MapBannerInstance>newHashMap();
	public Map<String, MapIcon> icons = Maps.<String, MapIcon>newLinkedHashMap();
	private final Map<String, MapFrameInstance> field_121 = Maps.<String, MapFrameInstance>newHashMap();

	public MapState(String string) {
		super(string);
	}

	public void method_105(int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		this.scale = (byte)k;
		this.method_106((double)i, (double)j, this.scale);
		this.field_118 = dimensionType;
		this.showIcons = bl;
		this.field_113 = bl2;
		this.markDirty();
	}

	public void method_106(double d, double e, int i) {
		int j = 128 * (1 << i);
		int k = MathHelper.floor((d + 64.0) / (double)j);
		int l = MathHelper.floor((e + 64.0) / (double)j);
		this.field_116 = k * j + j / 2 - 64;
		this.field_115 = l * j + j / 2 - 64;
	}

	@Override
	public void deserialize(CompoundTag compoundTag) {
		this.field_118 = DimensionType.byRawId(compoundTag.getInt("dimension"));
		this.field_116 = compoundTag.getInt("xCenter");
		this.field_115 = compoundTag.getInt("zCenter");
		this.scale = (byte)MathHelper.clamp(compoundTag.getByte("scale"), 0, 4);
		this.showIcons = !compoundTag.containsKey("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition");
		this.field_113 = compoundTag.getBoolean("unlimitedTracking");
		this.colorArray = compoundTag.getByteArray("colors");
		if (this.colorArray.length != 16384) {
			this.colorArray = new byte[16384];
		}

		ListTag listTag = compoundTag.getList("banners", 10);

		for (int i = 0; i < listTag.size(); i++) {
			MapBannerInstance mapBannerInstance = MapBannerInstance.fromNbt(listTag.getCompoundTag(i));
			this.field_123.put(mapBannerInstance.method_71(), mapBannerInstance);
			this.method_107(
				mapBannerInstance.method_72(),
				null,
				mapBannerInstance.method_71(),
				(double)mapBannerInstance.getPos().getX(),
				(double)mapBannerInstance.getPos().getZ(),
				180.0,
				mapBannerInstance.getText()
			);
		}

		ListTag listTag2 = compoundTag.getList("frames", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			MapFrameInstance mapFrameInstance = MapFrameInstance.fromNbt(listTag2.getCompoundTag(j));
			this.field_121.put(mapFrameInstance.method_82(), mapFrameInstance);
			this.method_107(
				MapIcon.Direction.field_95,
				null,
				"frame-" + mapFrameInstance.getEntityId(),
				(double)mapFrameInstance.getPos().getX(),
				(double)mapFrameInstance.getPos().getZ(),
				(double)mapFrameInstance.getRotation(),
				null
			);
		}
	}

	@Override
	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putInt("dimension", this.field_118.getRawId());
		compoundTag.putInt("xCenter", this.field_116);
		compoundTag.putInt("zCenter", this.field_115);
		compoundTag.putByte("scale", this.scale);
		compoundTag.putByteArray("colors", this.colorArray);
		compoundTag.putBoolean("trackingPosition", this.showIcons);
		compoundTag.putBoolean("unlimitedTracking", this.field_113);
		ListTag listTag = new ListTag();

		for (MapBannerInstance mapBannerInstance : this.field_123.values()) {
			listTag.add((Tag)mapBannerInstance.getNbt());
		}

		compoundTag.put("banners", listTag);
		ListTag listTag2 = new ListTag();

		for (MapFrameInstance mapFrameInstance : this.field_121.values()) {
			listTag2.add((Tag)mapFrameInstance.getNbt());
		}

		compoundTag.put("frames", listTag2);
		return compoundTag;
	}

	public void method_102(PlayerEntity playerEntity, ItemStack itemStack) {
		if (!this.field_120.containsKey(playerEntity)) {
			MapState.class_23 lv = new MapState.class_23(playerEntity);
			this.field_120.put(playerEntity, lv);
			this.field_112.add(lv);
		}

		if (!playerEntity.inventory.method_7379(itemStack)) {
			this.icons.remove(playerEntity.getName().getString());
		}

		for (int i = 0; i < this.field_112.size(); i++) {
			MapState.class_23 lv2 = (MapState.class_23)this.field_112.get(i);
			String string = lv2.field_125.getName().getString();
			if (!lv2.field_125.invalid && (lv2.field_125.inventory.method_7379(itemStack) || itemStack.isHeldInItemFrame())) {
				if (!itemStack.isHeldInItemFrame() && lv2.field_125.dimension == this.field_118 && this.showIcons) {
					this.method_107(MapIcon.Direction.field_91, lv2.field_125.world, string, lv2.field_125.x, lv2.field_125.z, (double)lv2.field_125.yaw, null);
				}
			} else {
				this.field_120.remove(lv2.field_125);
				this.field_112.remove(lv2);
				this.icons.remove(string);
			}
		}

		if (itemStack.isHeldInItemFrame() && this.showIcons) {
			ItemFrameEntity itemFrameEntity = itemStack.getHoldingItemFrame();
			BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
			MapFrameInstance mapFrameInstance = (MapFrameInstance)this.field_121.get(MapFrameInstance.method_81(blockPos));
			if (mapFrameInstance != null && itemFrameEntity.getEntityId() != mapFrameInstance.getEntityId() && this.field_121.containsKey(mapFrameInstance.method_82())) {
				this.icons.remove("frame-" + mapFrameInstance.getEntityId());
			}

			MapFrameInstance mapFrameInstance2 = new MapFrameInstance(blockPos, itemFrameEntity.field_7099.getHorizontal() * 90, itemFrameEntity.getEntityId());
			this.method_107(
				MapIcon.Direction.field_95,
				playerEntity.world,
				"frame-" + itemFrameEntity.getEntityId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.field_7099.getHorizontal() * 90),
				null
			);
			this.field_121.put(mapFrameInstance2.method_82(), mapFrameInstance2);
		}

		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("Decorations", 9)) {
			ListTag listTag = compoundTag.getList("Decorations", 10);

			for (int j = 0; j < listTag.size(); j++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(j);
				if (!this.icons.containsKey(compoundTag2.getString("id"))) {
					this.method_107(
						MapIcon.Direction.method_99(compoundTag2.getByte("type")),
						playerEntity.world,
						compoundTag2.getString("id"),
						compoundTag2.getDouble("x"),
						compoundTag2.getDouble("z"),
						compoundTag2.getDouble("rot"),
						null
					);
				}
			}
		}
	}

	public static void method_110(ItemStack itemStack, BlockPos blockPos, String string, MapIcon.Direction direction) {
		ListTag listTag;
		if (itemStack.hasTag() && itemStack.getTag().containsKey("Decorations", 9)) {
			listTag = itemStack.getTag().getList("Decorations", 10);
		} else {
			listTag = new ListTag();
			itemStack.setChildTag("Decorations", listTag);
		}

		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putByte("type", direction.method_98());
		compoundTag.putString("id", string);
		compoundTag.putDouble("x", (double)blockPos.getX());
		compoundTag.putDouble("z", (double)blockPos.getZ());
		compoundTag.putDouble("rot", 180.0);
		listTag.add((Tag)compoundTag);
		if (direction.method_97()) {
			CompoundTag compoundTag2 = itemStack.getOrCreateSubCompoundTag("display");
			compoundTag2.putInt("MapColor", direction.method_96());
		}
	}

	private void method_107(
		MapIcon.Direction direction, @Nullable IWorld iWorld, String string, double d, double e, double f, @Nullable TextComponent textComponent
	) {
		int i = 1 << this.scale;
		float g = (float)(d - (double)this.field_116) / (float)i;
		float h = (float)(e - (double)this.field_115) / (float)i;
		byte b = (byte)((int)((double)(g * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(h * 2.0F) + 0.5));
		int j = 63;
		byte k;
		if (g >= -63.0F && h >= -63.0F && g <= 63.0F && h <= 63.0F) {
			f += f < 0.0 ? -8.0 : 8.0;
			k = (byte)((int)(f * 16.0 / 360.0));
			if (this.field_118 == DimensionType.field_13076 && iWorld != null) {
				int l = (int)(iWorld.getLevelProperties().getTimeOfDay() / 10L);
				k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
			}
		} else {
			if (direction != MapIcon.Direction.field_91) {
				this.icons.remove(string);
				return;
			}

			int l = 320;
			if (Math.abs(g) < 320.0F && Math.abs(h) < 320.0F) {
				direction = MapIcon.Direction.field_86;
			} else {
				if (!this.field_113) {
					this.icons.remove(string);
					return;
				}

				direction = MapIcon.Direction.field_87;
			}

			k = 0;
			if (g <= -63.0F) {
				b = -128;
			}

			if (h <= -63.0F) {
				c = -128;
			}

			if (g >= 63.0F) {
				b = 127;
			}

			if (h >= 63.0F) {
				c = 127;
			}
		}

		this.icons.put(string, new MapIcon(direction, b, c, k, textComponent));
	}

	@Nullable
	public Packet<?> method_100(ItemStack itemStack, BlockView blockView, PlayerEntity playerEntity) {
		MapState.class_23 lv = (MapState.class_23)this.field_120.get(playerEntity);
		return lv == null ? null : lv.method_112(itemStack);
	}

	public void method_103(int i, int j) {
		this.markDirty();

		for (MapState.class_23 lv : this.field_112) {
			lv.method_111(i, j);
		}
	}

	public MapState.class_23 method_101(PlayerEntity playerEntity) {
		MapState.class_23 lv = (MapState.class_23)this.field_120.get(playerEntity);
		if (lv == null) {
			lv = new MapState.class_23(playerEntity);
			this.field_120.put(playerEntity, lv);
			this.field_112.add(lv);
		}

		return lv;
	}

	public void method_108(IWorld iWorld, BlockPos blockPos) {
		float f = (float)blockPos.getX() + 0.5F;
		float g = (float)blockPos.getZ() + 0.5F;
		int i = 1 << this.scale;
		float h = (f - (float)this.field_116) / (float)i;
		float j = (g - (float)this.field_115) / (float)i;
		int k = 63;
		boolean bl = false;
		if (h >= -63.0F && j >= -63.0F && h <= 63.0F && j <= 63.0F) {
			MapBannerInstance mapBannerInstance = MapBannerInstance.fromWorldBlock(iWorld, blockPos);
			if (mapBannerInstance == null) {
				return;
			}

			boolean bl2 = true;
			if (this.field_123.containsKey(mapBannerInstance.method_71())
				&& ((MapBannerInstance)this.field_123.get(mapBannerInstance.method_71())).equals(mapBannerInstance)) {
				this.field_123.remove(mapBannerInstance.method_71());
				this.icons.remove(mapBannerInstance.method_71());
				bl2 = false;
				bl = true;
			}

			if (bl2) {
				this.field_123.put(mapBannerInstance.method_71(), mapBannerInstance);
				this.method_107(mapBannerInstance.method_72(), iWorld, mapBannerInstance.method_71(), (double)f, (double)g, 180.0, mapBannerInstance.getText());
				bl = true;
			}

			if (bl) {
				this.markDirty();
			}
		}
	}

	public void method_109(BlockView blockView, int i, int j) {
		Iterator<MapBannerInstance> iterator = this.field_123.values().iterator();

		while (iterator.hasNext()) {
			MapBannerInstance mapBannerInstance = (MapBannerInstance)iterator.next();
			if (mapBannerInstance.getPos().getX() == i && mapBannerInstance.getPos().getZ() == j) {
				MapBannerInstance mapBannerInstance2 = MapBannerInstance.fromWorldBlock(blockView, mapBannerInstance.getPos());
				if (!mapBannerInstance.equals(mapBannerInstance2)) {
					iterator.remove();
					this.icons.remove(mapBannerInstance.method_71());
				}
			}
		}
	}

	public void method_104(BlockPos blockPos, int i) {
		this.icons.remove("frame-" + i);
		this.field_121.remove(MapFrameInstance.method_81(blockPos));
	}

	public class class_23 {
		public final PlayerEntity field_125;
		private boolean field_130 = true;
		private int field_129;
		private int field_128;
		private int field_127 = 127;
		private int field_126 = 127;
		private int field_124;
		public int field_131;

		public class_23(PlayerEntity playerEntity) {
			this.field_125 = playerEntity;
		}

		@Nullable
		public Packet<?> method_112(ItemStack itemStack) {
			if (this.field_130) {
				this.field_130 = false;
				return new MapUpdateClientPacket(
					FilledMapItem.method_8003(itemStack),
					MapState.this.scale,
					MapState.this.showIcons,
					MapState.this.icons.values(),
					MapState.this.colorArray,
					this.field_129,
					this.field_128,
					this.field_127 + 1 - this.field_129,
					this.field_126 + 1 - this.field_128
				);
			} else {
				return this.field_124++ % 5 == 0
					? new MapUpdateClientPacket(
						FilledMapItem.method_8003(itemStack), MapState.this.scale, MapState.this.showIcons, MapState.this.icons.values(), MapState.this.colorArray, 0, 0, 0, 0
					)
					: null;
			}
		}

		public void method_111(int i, int j) {
			if (this.field_130) {
				this.field_129 = Math.min(this.field_129, i);
				this.field_128 = Math.min(this.field_128, j);
				this.field_127 = Math.max(this.field_127, i);
				this.field_126 = Math.max(this.field_126, j);
			} else {
				this.field_130 = true;
				this.field_129 = i;
				this.field_128 = j;
				this.field_127 = i;
				this.field_126 = j;
			}
		}
	}
}
