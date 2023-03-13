package net.minecraft.entity.decoration;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public abstract class DisplayEntity extends Entity {
	static final Logger field_42397 = LogUtils.getLogger();
	private static final float field_43150 = Float.POSITIVE_INFINITY;
	public static final int field_42384 = -1;
	private static final TrackedData<Integer> START_INTERPOLATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> INTERPOLATION_DURATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Vector3f> TRANSLATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
	private static final TrackedData<Vector3f> SCALE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
	private static final TrackedData<Quaternionf> LEFT_ROTATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.QUATERNIONF);
	private static final TrackedData<Quaternionf> RIGHT_ROTATION = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.QUATERNIONF);
	private static final TrackedData<Byte> BILLBOARD = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> BRIGHTNESS = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> VIEW_RANGE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> SHADOW_RADIUS = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> SHADOW_STRENGTH = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> WIDTH = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> HEIGHT = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> GLOW_COLOR_OVERRIDE = DataTracker.registerData(DisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final float field_42376 = 0.0F;
	private static final float field_42377 = 1.0F;
	private static final int field_42378 = -1;
	public static final String INTERPOLATION_DURATION_NBT_KEY = "interpolation_duration";
	public static final String START_INTERPOLATION_KEY = "start_interpolation";
	public static final String TRANSFORMATION_NBT_KEY = "transformation";
	public static final String BILLBOARD_NBT_KEY = "billboard";
	public static final String BRIGHTNESS_NBT_KEY = "brightness";
	public static final String VIEW_RANGE_NBT_KEY = "view_range";
	public static final String SHADOW_RADIUS_NBT_KEY = "shadow_radius";
	public static final String SHADOW_STRENGTH_NBT_KEY = "shadow_strength";
	public static final String WIDTH_NBT_KEY = "width";
	public static final String HEIGHT_NBT_KEY = "height";
	public static final String GLOW_COLOR_OVERRIDE_NBT_KEY = "glow_color_override";
	private final DisplayEntity.AbstractInterpolator<AffineTransformation> transformationInterpolator = new DisplayEntity.AbstractInterpolator<AffineTransformation>(
		AffineTransformation.identity()
	) {
		protected AffineTransformation interpolate(float f, AffineTransformation affineTransformation, AffineTransformation affineTransformation2) {
			return affineTransformation.interpolate(affineTransformation2, f);
		}
	};
	private final DisplayEntity.FloatLerper shadowRadiusLerper = new DisplayEntity.FloatLerper(0.0F);
	private final DisplayEntity.FloatLerper shadowStrengthLerper = new DisplayEntity.FloatLerper(1.0F);
	private final Quaternionf fixedRotation = new Quaternionf();
	protected final DisplayEntity.Interpolators interpolators = new DisplayEntity.Interpolators();
	private long interpolationStart;
	private float lerpProgress;
	private Box visibilityBoundingBox;
	private boolean startInterpolationOnNextTick;
	private boolean startInterpolationChanged;

	public DisplayEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
		this.ignoreCameraFrustum = true;
		this.visibilityBoundingBox = this.getBoundingBox();
		this.interpolators
			.addInterpolationUpdater(
				Set.of(TRANSLATION, LEFT_ROTATION, SCALE, RIGHT_ROTATION),
				(value, dataTracker) -> this.transformationInterpolator.setValue(value, getTransformation(dataTracker))
			);
		this.interpolators.addInterpolator(SHADOW_STRENGTH, this.shadowStrengthLerper);
		this.interpolators.addInterpolator(SHADOW_RADIUS, this.shadowRadiusLerper);
	}

	@Override
	public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> dataEntries) {
		super.onDataTrackerUpdate(dataEntries);
		boolean bl = false;

		for (DataTracker.SerializedEntry<?> serializedEntry : dataEntries) {
			bl |= this.interpolators.hasInterpolator(serializedEntry.id());
		}

		if (bl) {
			boolean bl2 = this.age <= 0;
			if (bl2) {
				this.interpolators.interpolate(Float.POSITIVE_INFINITY, this.dataTracker);
			} else {
				this.startInterpolationOnNextTick = true;
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (HEIGHT.equals(data) || WIDTH.equals(data)) {
			this.updateVisibilityBoundingBox();
		}

		if (START_INTERPOLATION.equals(data)) {
			this.startInterpolationChanged = true;
		}
	}

	private static AffineTransformation getTransformation(DataTracker dataTracker) {
		Vector3f vector3f = dataTracker.get(TRANSLATION);
		Quaternionf quaternionf = dataTracker.get(LEFT_ROTATION);
		Vector3f vector3f2 = dataTracker.get(SCALE);
		Quaternionf quaternionf2 = dataTracker.get(RIGHT_ROTATION);
		return new AffineTransformation(vector3f, quaternionf, vector3f2, quaternionf2);
	}

	@Override
	public void tick() {
		Entity entity = this.getVehicle();
		if (entity != null && entity.isRemoved()) {
			this.stopRiding();
		}

		if (this.world.isClient) {
			if (this.startInterpolationChanged) {
				this.startInterpolationChanged = false;
				int i = this.getStartInterpolation();
				this.interpolationStart = (long)(this.age + i);
			}

			if (this.startInterpolationOnNextTick) {
				this.startInterpolationOnNextTick = false;
				this.interpolators.interpolate(this.lerpProgress, this.dataTracker);
			}
		}
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(START_INTERPOLATION, 0);
		this.dataTracker.startTracking(INTERPOLATION_DURATION, 0);
		this.dataTracker.startTracking(TRANSLATION, new Vector3f());
		this.dataTracker.startTracking(SCALE, new Vector3f(1.0F, 1.0F, 1.0F));
		this.dataTracker.startTracking(RIGHT_ROTATION, new Quaternionf());
		this.dataTracker.startTracking(LEFT_ROTATION, new Quaternionf());
		this.dataTracker.startTracking(BILLBOARD, DisplayEntity.BillboardMode.FIXED.getIndex());
		this.dataTracker.startTracking(BRIGHTNESS, -1);
		this.dataTracker.startTracking(VIEW_RANGE, 1.0F);
		this.dataTracker.startTracking(SHADOW_RADIUS, 0.0F);
		this.dataTracker.startTracking(SHADOW_STRENGTH, 1.0F);
		this.dataTracker.startTracking(WIDTH, 0.0F);
		this.dataTracker.startTracking(HEIGHT, 0.0F);
		this.dataTracker.startTracking(GLOW_COLOR_OVERRIDE, -1);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("transformation")) {
			AffineTransformation.ANY_CODEC
				.decode(NbtOps.INSTANCE, nbt.get("transformation"))
				.resultOrPartial(Util.addPrefix("Display entity", field_42397::error))
				.ifPresent(pair -> this.setTransformation((AffineTransformation)pair.getFirst()));
		}

		if (nbt.contains("interpolation_duration", NbtElement.NUMBER_TYPE)) {
			int i = nbt.getInt("interpolation_duration");
			this.setInterpolationDuration(i);
		}

		if (nbt.contains("start_interpolation", NbtElement.NUMBER_TYPE)) {
			int i = nbt.getInt("start_interpolation");
			this.setStartInterpolation(i);
		}

		if (nbt.contains("billboard", NbtElement.STRING_TYPE)) {
			DisplayEntity.BillboardMode.CODEC
				.decode(NbtOps.INSTANCE, nbt.get("billboard"))
				.resultOrPartial(Util.addPrefix("Display entity", field_42397::error))
				.ifPresent(pair -> this.setBillboardMode((DisplayEntity.BillboardMode)pair.getFirst()));
		}

		if (nbt.contains("view_range", NbtElement.NUMBER_TYPE)) {
			this.setViewRange(nbt.getFloat("view_range"));
		}

		if (nbt.contains("shadow_radius", NbtElement.NUMBER_TYPE)) {
			this.setShadowRadius(nbt.getFloat("shadow_radius"));
		}

		if (nbt.contains("shadow_strength", NbtElement.NUMBER_TYPE)) {
			this.setShadowStrength(nbt.getFloat("shadow_strength"));
		}

		if (nbt.contains("width", NbtElement.NUMBER_TYPE)) {
			this.setDIsplayWidth(nbt.getFloat("width"));
		}

		if (nbt.contains("height", NbtElement.NUMBER_TYPE)) {
			this.setDisplayHeight(nbt.getFloat("height"));
		}

		if (nbt.contains("glow_color_override", NbtElement.NUMBER_TYPE)) {
			this.setGlowColorOverride(nbt.getInt("glow_color_override"));
		}

		if (nbt.contains("brightness", NbtElement.COMPOUND_TYPE)) {
			Brightness.CODEC
				.decode(NbtOps.INSTANCE, nbt.get("brightness"))
				.resultOrPartial(Util.addPrefix("Display entity", field_42397::error))
				.ifPresent(pair -> this.setBrightness((Brightness)pair.getFirst()));
		} else {
			this.setBrightness(null);
		}
	}

	private void setTransformation(AffineTransformation transformation) {
		this.dataTracker.set(TRANSLATION, transformation.getTranslation());
		this.dataTracker.set(LEFT_ROTATION, transformation.getLeftRotation());
		this.dataTracker.set(SCALE, transformation.getScale());
		this.dataTracker.set(RIGHT_ROTATION, transformation.getRightRotation());
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		AffineTransformation.ANY_CODEC
			.encodeStart(NbtOps.INSTANCE, getTransformation(this.dataTracker))
			.result()
			.ifPresent(transformations -> nbt.put("transformation", transformations));
		DisplayEntity.BillboardMode.CODEC.encodeStart(NbtOps.INSTANCE, this.getBillboardMode()).result().ifPresent(billboard -> nbt.put("billboard", billboard));
		nbt.putInt("interpolation_duration", this.getInterpolationDuration());
		nbt.putFloat("view_range", this.getViewRange());
		nbt.putFloat("shadow_radius", this.getShadowRadius());
		nbt.putFloat("shadow_strength", this.getShadowStrength());
		nbt.putFloat("width", this.getDisplayWidth());
		nbt.putFloat("height", this.getDisplayHeight());
		nbt.putInt("glow_color_override", this.getGlowColorOverride());
		Brightness brightness = this.getBrightnessUnpacked();
		if (brightness != null) {
			Brightness.CODEC.encodeStart(NbtOps.INSTANCE, brightness).result().ifPresent(brightnessx -> nbt.put("brightness", brightnessx));
		}
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	public Box getVisibilityBoundingBox() {
		return this.visibilityBoundingBox;
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	public Quaternionf getFixedRotation() {
		return this.fixedRotation;
	}

	public AffineTransformation lerpTransformation(float delta) {
		return this.transformationInterpolator.interpolate(delta);
	}

	private void setInterpolationDuration(int interpolationDuration) {
		this.dataTracker.set(INTERPOLATION_DURATION, interpolationDuration);
	}

	private int getInterpolationDuration() {
		return this.dataTracker.get(INTERPOLATION_DURATION);
	}

	private void setStartInterpolation(int startInterpolation) {
		this.dataTracker.set(START_INTERPOLATION, startInterpolation, true);
	}

	private int getStartInterpolation() {
		return this.dataTracker.get(START_INTERPOLATION);
	}

	private void setBillboardMode(DisplayEntity.BillboardMode billboardMode) {
		this.dataTracker.set(BILLBOARD, billboardMode.getIndex());
	}

	public DisplayEntity.BillboardMode getBillboardMode() {
		return (DisplayEntity.BillboardMode)DisplayEntity.BillboardMode.FROM_INDEX.apply(this.dataTracker.get(BILLBOARD));
	}

	private void setBrightness(@Nullable Brightness brightness) {
		this.dataTracker.set(BRIGHTNESS, brightness != null ? brightness.pack() : -1);
	}

	@Nullable
	private Brightness getBrightnessUnpacked() {
		int i = this.dataTracker.get(BRIGHTNESS);
		return i != -1 ? Brightness.unpack(i) : null;
	}

	public int getBrightness() {
		return this.dataTracker.get(BRIGHTNESS);
	}

	private void setViewRange(float viewRange) {
		this.dataTracker.set(VIEW_RANGE, viewRange);
	}

	private float getViewRange() {
		return this.dataTracker.get(VIEW_RANGE);
	}

	private void setShadowRadius(float shadowRadius) {
		this.dataTracker.set(SHADOW_RADIUS, shadowRadius);
	}

	private float getShadowRadius() {
		return this.dataTracker.get(SHADOW_RADIUS);
	}

	public float lerpShadowRadius(float delta) {
		return this.shadowRadiusLerper.lerp(delta);
	}

	private void setShadowStrength(float shadowStrength) {
		this.dataTracker.set(SHADOW_STRENGTH, shadowStrength);
	}

	private float getShadowStrength() {
		return this.dataTracker.get(SHADOW_STRENGTH);
	}

	public float lerpShadowStrength(float delta) {
		return this.shadowStrengthLerper.lerp(delta);
	}

	private void setDIsplayWidth(float width) {
		this.dataTracker.set(WIDTH, width);
	}

	private float getDisplayWidth() {
		return this.dataTracker.get(WIDTH);
	}

	private void setDisplayHeight(float height) {
		this.dataTracker.set(HEIGHT, height);
	}

	private int getGlowColorOverride() {
		return this.dataTracker.get(GLOW_COLOR_OVERRIDE);
	}

	private void setGlowColorOverride(int glowColorOverride) {
		this.dataTracker.set(GLOW_COLOR_OVERRIDE, glowColorOverride);
	}

	public float getLerpProgress(float delta) {
		int i = this.getInterpolationDuration();
		if (i <= 0) {
			return 1.0F;
		} else {
			float f = (float)((long)this.age - this.interpolationStart);
			float g = f + delta;
			float h = MathHelper.clamp(MathHelper.getLerpProgress(g, 0.0F, (float)i), 0.0F, 1.0F);
			this.lerpProgress = h;
			return h;
		}
	}

	private float getDisplayHeight() {
		return this.dataTracker.get(HEIGHT);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		this.updateVisibilityBoundingBox();
	}

	private void updateVisibilityBoundingBox() {
		float f = this.getDisplayWidth();
		float g = this.getDisplayHeight();
		if (f != 0.0F && g != 0.0F) {
			this.ignoreCameraFrustum = false;
			float h = f / 2.0F;
			double d = this.getX();
			double e = this.getY();
			double i = this.getZ();
			this.visibilityBoundingBox = new Box(d - (double)h, e, i - (double)h, d + (double)h, e + (double)g, i + (double)h);
		} else {
			this.ignoreCameraFrustum = true;
		}
	}

	@Override
	public void setPitch(float pitch) {
		super.setPitch(pitch);
		this.updateFixedRotation();
	}

	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw);
		this.updateFixedRotation();
	}

	private void updateFixedRotation() {
		this.fixedRotation.rotationYXZ((float) (-Math.PI / 180.0) * this.getYaw(), (float) (Math.PI / 180.0) * this.getPitch(), 0.0F);
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < MathHelper.square((double)this.getViewRange() * 64.0 * getRenderDistanceMultiplier());
	}

	@Override
	public int getTeamColorValue() {
		int i = this.getGlowColorOverride();
		return i != -1 ? i : super.getTeamColorValue();
	}

	abstract static class AbstractInterpolator<T> extends DisplayEntity.Interpolator<T> {
		protected AbstractInterpolator(T object) {
			super(object);
		}

		protected abstract T interpolate(float delta, T start, T end);

		public T interpolate(float delta) {
			return !((double)delta >= 1.0) && this.prevValue != null ? this.interpolate(delta, this.prevValue, this.value) : this.value;
		}

		@Override
		protected T apply(float value) {
			return this.interpolate(value);
		}
	}

	static class ArgbLerper extends DisplayEntity.IntLerper {
		protected ArgbLerper(int i) {
			super(i);
		}

		@Override
		protected int lerp(float delta, int start, int end) {
			return ColorHelper.Argb.lerp(delta, start, end);
		}
	}

	public static enum BillboardMode implements StringIdentifiable {
		FIXED((byte)0, "fixed"),
		VERTICAL((byte)1, "vertical"),
		HORIZONTAL((byte)2, "horizontal"),
		CENTER((byte)3, "center");

		public static final com.mojang.serialization.Codec<DisplayEntity.BillboardMode> CODEC = StringIdentifiable.createCodec(DisplayEntity.BillboardMode::values);
		public static final IntFunction<DisplayEntity.BillboardMode> FROM_INDEX = ValueLists.createIdToValueFunction(
			DisplayEntity.BillboardMode::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		private final byte index;
		private final String name;

		private BillboardMode(byte index, String name) {
			this.name = name;
			this.index = index;
		}

		@Override
		public String asString() {
			return this.name;
		}

		byte getIndex() {
			return this.index;
		}
	}

	public static class BlockDisplayEntity extends DisplayEntity {
		public static final String BLOCK_STATE_NBT_KEY = "block_state";
		private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(
			DisplayEntity.BlockDisplayEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE
		);

		public BlockDisplayEntity(EntityType<?> entityType, World world) {
			super(entityType, world);
		}

		@Override
		protected void initDataTracker() {
			super.initDataTracker();
			this.dataTracker.startTracking(BLOCK_STATE, Blocks.AIR.getDefaultState());
		}

		public BlockState getBlockState() {
			return this.dataTracker.get(BLOCK_STATE);
		}

		public void setBlockState(BlockState state) {
			this.dataTracker.set(BLOCK_STATE, state);
		}

		@Override
		protected void readCustomDataFromNbt(NbtCompound nbt) {
			super.readCustomDataFromNbt(nbt);
			this.setBlockState(NbtHelper.toBlockState(this.world.createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("block_state")));
		}

		@Override
		protected void writeCustomDataToNbt(NbtCompound nbt) {
			super.writeCustomDataToNbt(nbt);
			nbt.put("block_state", NbtHelper.fromBlockState(this.getBlockState()));
		}
	}

	static class FloatLerper extends DisplayEntity.Interpolator<Float> {
		protected FloatLerper(float value) {
			super(value);
		}

		protected float lerp(float delta, float start, float end) {
			return MathHelper.lerp(delta, start, end);
		}

		public float lerp(float delta) {
			return !((double)delta >= 1.0) && this.prevValue != null ? this.lerp(delta, this.prevValue, this.value) : this.value;
		}

		protected Float apply(float f) {
			return this.lerp(f);
		}
	}

	static class IntLerper extends DisplayEntity.Interpolator<Integer> {
		protected IntLerper(int value) {
			super(value);
		}

		protected int lerp(float delta, int start, int end) {
			return MathHelper.lerp(delta, start, end);
		}

		public int lerp(float value) {
			return !((double)value >= 1.0) && this.prevValue != null ? this.lerp(value, this.prevValue, this.value) : this.value;
		}

		protected Integer apply(float f) {
			return this.lerp(f);
		}
	}

	@FunctionalInterface
	interface InterpolationUpdater {
		void update(float value, DataTracker dataTracker);
	}

	abstract static class Interpolator<T> {
		@Nullable
		protected T prevValue;
		protected T value;

		protected Interpolator(T value) {
			this.value = value;
		}

		protected abstract T apply(float value);

		public void setValue(float prevValue, T value) {
			if (prevValue != Float.POSITIVE_INFINITY) {
				this.prevValue = this.apply(prevValue);
			}

			this.value = value;
		}
	}

	static class Interpolators {
		private final IntSet interpolatedIds = new IntOpenHashSet();
		private final List<DisplayEntity.InterpolationUpdater> interpolationUpdaters = new ArrayList();

		protected <T> void addInterpolator(TrackedData<T> data, DisplayEntity.Interpolator<T> interpolator) {
			this.interpolatedIds.add(data.getId());
			this.interpolationUpdaters.add((DisplayEntity.InterpolationUpdater)(value, dataTracker) -> interpolator.setValue(value, dataTracker.get(data)));
		}

		protected void addInterpolationUpdater(Set<TrackedData<?>> dataSet, DisplayEntity.InterpolationUpdater updater) {
			for (TrackedData<?> trackedData : dataSet) {
				this.interpolatedIds.add(trackedData.getId());
			}

			this.interpolationUpdaters.add(updater);
		}

		public boolean hasInterpolator(int id) {
			return this.interpolatedIds.contains(id);
		}

		public void interpolate(float value, DataTracker dataTracker) {
			for (DisplayEntity.InterpolationUpdater interpolationUpdater : this.interpolationUpdaters) {
				interpolationUpdater.update(value, dataTracker);
			}
		}
	}

	public static class ItemDisplayEntity extends DisplayEntity {
		private static final String ITEM_NBT_KEY = "item";
		private static final String ITEM_DISPLAY_NBT_KEY = "item_display";
		private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(DisplayEntity.ItemDisplayEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
		private static final TrackedData<Byte> ITEM_DISPLAY = DataTracker.registerData(DisplayEntity.ItemDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
		private final StackReference stackReference = new StackReference() {
			@Override
			public ItemStack get() {
				return ItemDisplayEntity.this.getItemStack();
			}

			@Override
			public boolean set(ItemStack stack) {
				ItemDisplayEntity.this.setItemStack(stack);
				return true;
			}
		};

		public ItemDisplayEntity(EntityType<?> entityType, World world) {
			super(entityType, world);
		}

		@Override
		protected void initDataTracker() {
			super.initDataTracker();
			this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
			this.dataTracker.startTracking(ITEM_DISPLAY, ModelTransformationMode.NONE.getIndex());
		}

		public ItemStack getItemStack() {
			return this.dataTracker.get(ITEM);
		}

		void setItemStack(ItemStack stack) {
			this.dataTracker.set(ITEM, stack);
		}

		private void setTransformationMode(ModelTransformationMode transformationMode) {
			this.dataTracker.set(ITEM_DISPLAY, transformationMode.getIndex());
		}

		public ModelTransformationMode getTransformationMode() {
			return (ModelTransformationMode)ModelTransformationMode.FROM_INDEX.apply(this.dataTracker.get(ITEM_DISPLAY));
		}

		@Override
		protected void readCustomDataFromNbt(NbtCompound nbt) {
			super.readCustomDataFromNbt(nbt);
			this.setItemStack(ItemStack.fromNbt(nbt.getCompound("item")));
			if (nbt.contains("item_display", NbtElement.STRING_TYPE)) {
				ModelTransformationMode.CODEC
					.decode(NbtOps.INSTANCE, nbt.get("item_display"))
					.resultOrPartial(Util.addPrefix("Display entity", DisplayEntity.field_42397::error))
					.ifPresent(mode -> this.setTransformationMode((ModelTransformationMode)mode.getFirst()));
			}
		}

		@Override
		protected void writeCustomDataToNbt(NbtCompound nbt) {
			super.writeCustomDataToNbt(nbt);
			nbt.put("item", this.getItemStack().writeNbt(new NbtCompound()));
			ModelTransformationMode.CODEC.encodeStart(NbtOps.INSTANCE, this.getTransformationMode()).result().ifPresent(nbtx -> nbt.put("item_display", nbtx));
		}

		@Override
		public StackReference getStackReference(int mappedIndex) {
			return mappedIndex == 0 ? this.stackReference : StackReference.EMPTY;
		}
	}

	public static class TextDisplayEntity extends DisplayEntity {
		public static final String TEXT_NBT_KEY = "text";
		private static final String LINE_WIDTH_NBT_KEY = "line_width";
		private static final String TEXT_OPACITY_NBT_KEY = "text_opacity";
		private static final String BACKGROUND_NBT_KEY = "background";
		private static final String SHADOW_NBT_KEY = "shadow";
		private static final String SEE_THROUGH_NBT_KEY = "see_through";
		private static final String DEFAULT_BACKGROUND_NBT_KEY = "default_background";
		private static final String ALIGNMENT_NBT_KEY = "alignment";
		public static final byte SHADOW_FLAG = 1;
		public static final byte SEE_THROUGH_FLAG = 2;
		public static final byte DEFAULT_BACKGROUND_FLAG = 4;
		public static final byte LEFT_ALIGNMENT_FLAG = 8;
		public static final byte RIGHT_ALIGNMENT_FLAG = 16;
		private static final byte INITIAL_TEXT_OPACITY = -1;
		public static final int INITIAL_BACKGROUND = 1073741824;
		private static final TrackedData<Text> TEXT = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
		private static final TrackedData<Integer> LINE_WIDTH = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
		private static final TrackedData<Integer> BACKGROUND = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.INTEGER);
		private static final TrackedData<Byte> TEXT_OPACITY = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
		private static final TrackedData<Byte> TEXT_DISPLAY_FLAGS = DataTracker.registerData(DisplayEntity.TextDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
		private final DisplayEntity.IntLerper textOpacityLerper = new DisplayEntity.IntLerper(-1);
		private final DisplayEntity.IntLerper backgroundLerper = new DisplayEntity.ArgbLerper(1073741824);
		@Nullable
		private DisplayEntity.TextDisplayEntity.TextLines textLines;

		public TextDisplayEntity(EntityType<?> entityType, World world) {
			super(entityType, world);
			this.interpolators.addInterpolator(BACKGROUND, this.backgroundLerper);
			this.interpolators
				.addInterpolationUpdater(
					Set.of(TEXT_OPACITY), (value, dataTracker) -> this.textOpacityLerper.setValue(value, Integer.valueOf(dataTracker.get(TEXT_OPACITY) & 255))
				);
		}

		@Override
		protected void initDataTracker() {
			super.initDataTracker();
			this.dataTracker.startTracking(TEXT, Text.empty());
			this.dataTracker.startTracking(LINE_WIDTH, 200);
			this.dataTracker.startTracking(BACKGROUND, 1073741824);
			this.dataTracker.startTracking(TEXT_OPACITY, (byte)-1);
			this.dataTracker.startTracking(TEXT_DISPLAY_FLAGS, (byte)0);
		}

		@Override
		public void onTrackedDataSet(TrackedData<?> data) {
			super.onTrackedDataSet(data);
			this.textLines = null;
		}

		public Text getText() {
			return this.dataTracker.get(TEXT);
		}

		private void setText(Text text) {
			this.dataTracker.set(TEXT, text);
		}

		public int getLineWidth() {
			return this.dataTracker.get(LINE_WIDTH);
		}

		private void setLineWidth(int lineWidth) {
			this.dataTracker.set(LINE_WIDTH, lineWidth);
		}

		public byte lerpTextOpacity(float delta) {
			return (byte)this.textOpacityLerper.lerp(delta);
		}

		private byte getTextOpacity() {
			return this.dataTracker.get(TEXT_OPACITY);
		}

		private void setTextOpacity(byte textOpacity) {
			this.dataTracker.set(TEXT_OPACITY, textOpacity);
		}

		public int lerpBackground(float delta) {
			return this.backgroundLerper.lerp(delta);
		}

		private int getBackground() {
			return this.dataTracker.get(BACKGROUND);
		}

		private void setBackground(int background) {
			this.dataTracker.set(BACKGROUND, background);
		}

		public byte getDisplayFlags() {
			return this.dataTracker.get(TEXT_DISPLAY_FLAGS);
		}

		private void setDisplayFlags(byte flags) {
			this.dataTracker.set(TEXT_DISPLAY_FLAGS, flags);
		}

		private static byte readFlag(byte flags, NbtCompound nbt, String nbtKey, byte flag) {
			return nbt.getBoolean(nbtKey) ? (byte)(flags | flag) : flags;
		}

		@Override
		protected void readCustomDataFromNbt(NbtCompound nbt) {
			super.readCustomDataFromNbt(nbt);
			if (nbt.contains("line_width", NbtElement.NUMBER_TYPE)) {
				this.setLineWidth(nbt.getInt("line_width"));
			}

			if (nbt.contains("text_opacity", NbtElement.NUMBER_TYPE)) {
				this.setTextOpacity(nbt.getByte("text_opacity"));
			}

			if (nbt.contains("background", NbtElement.NUMBER_TYPE)) {
				this.setBackground(nbt.getInt("background"));
			}

			byte b = readFlag((byte)0, nbt, "shadow", (byte)1);
			b = readFlag(b, nbt, "see_through", (byte)2);
			b = readFlag(b, nbt, "default_background", (byte)4);
			Optional<DisplayEntity.TextDisplayEntity.TextAlignment> optional = DisplayEntity.TextDisplayEntity.TextAlignment.CODEC
				.decode(NbtOps.INSTANCE, nbt.get("alignment"))
				.resultOrPartial(Util.addPrefix("Display entity", DisplayEntity.field_42397::error))
				.map(Pair::getFirst);
			if (optional.isPresent()) {
				b = switch ((DisplayEntity.TextDisplayEntity.TextAlignment)optional.get()) {
					case CENTER -> b;
					case LEFT -> (byte)(b | 8);
					case RIGHT -> (byte)(b | 16);
				};
			}

			this.setDisplayFlags(b);
			if (nbt.contains("text", NbtElement.STRING_TYPE)) {
				String string = nbt.getString("text");

				try {
					Text text = Text.Serializer.fromJson(string);
					if (text != null) {
						ServerCommandSource serverCommandSource = this.getCommandSource().withLevel(2);
						Text text2 = Texts.parse(serverCommandSource, text, this, 0);
						this.setText(text2);
					} else {
						this.setText(Text.empty());
					}
				} catch (Exception var8) {
					DisplayEntity.field_42397.warn("Failed to parse display entity text {}", string, var8);
				}
			}
		}

		private static void writeFlag(byte flags, NbtCompound nbt, String nbtKey, byte flag) {
			nbt.putBoolean(nbtKey, (flags & flag) != 0);
		}

		@Override
		protected void writeCustomDataToNbt(NbtCompound nbt) {
			super.writeCustomDataToNbt(nbt);
			nbt.putString("text", Text.Serializer.toJson(this.getText()));
			nbt.putInt("line_width", this.getLineWidth());
			nbt.putInt("background", this.getBackground());
			nbt.putByte("text_opacity", this.getTextOpacity());
			byte b = this.getDisplayFlags();
			writeFlag(b, nbt, "shadow", (byte)1);
			writeFlag(b, nbt, "see_through", (byte)2);
			writeFlag(b, nbt, "default_background", (byte)4);
			DisplayEntity.TextDisplayEntity.TextAlignment.CODEC
				.encodeStart(NbtOps.INSTANCE, getAlignment(b))
				.result()
				.ifPresent(nbtElement -> nbt.put("alignment", nbtElement));
		}

		public DisplayEntity.TextDisplayEntity.TextLines splitLines(DisplayEntity.TextDisplayEntity.LineSplitter splitter) {
			if (this.textLines == null) {
				int i = this.getLineWidth();
				this.textLines = splitter.split(this.getText(), i);
			}

			return this.textLines;
		}

		public static DisplayEntity.TextDisplayEntity.TextAlignment getAlignment(byte flags) {
			if ((flags & 8) != 0) {
				return DisplayEntity.TextDisplayEntity.TextAlignment.LEFT;
			} else {
				return (flags & 16) != 0 ? DisplayEntity.TextDisplayEntity.TextAlignment.RIGHT : DisplayEntity.TextDisplayEntity.TextAlignment.CENTER;
			}
		}

		@FunctionalInterface
		public interface LineSplitter {
			DisplayEntity.TextDisplayEntity.TextLines split(Text text, int lineWidth);
		}

		public static enum TextAlignment implements StringIdentifiable {
			CENTER("center"),
			LEFT("left"),
			RIGHT("right");

			public static final com.mojang.serialization.Codec<DisplayEntity.TextDisplayEntity.TextAlignment> CODEC = StringIdentifiable.createCodec(
				DisplayEntity.TextDisplayEntity.TextAlignment::values
			);
			private final String name;

			private TextAlignment(String name) {
				this.name = name;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}

		public static record TextLine(OrderedText contents, int width) {
		}

		public static record TextLines(List<DisplayEntity.TextDisplayEntity.TextLine> lines, int width) {
		}
	}
}
