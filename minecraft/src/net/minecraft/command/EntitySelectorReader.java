package net.minecraft.command;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntitySelectorReader {
	public static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.invalid"));
	public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.selector.unknown", object)
	);
	public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.entity.selector.not_allowed")
	);
	public static final SimpleCommandExceptionType MISSING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.entity.selector.missing"));
	public static final SimpleCommandExceptionType UNTERMINATED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("argument.entity.options.unterminated")
	);
	public static final DynamicCommandExceptionType VALUELESS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.valueless", object)
	);
	public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (vec3d, list) -> {
	};
	public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity.squaredDistanceTo(vec3d), entity2.squaredDistanceTo(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity2.squaredDistanceTo(vec3d), entity.squaredDistanceTo(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM = (vec3d, list) -> Collections.shuffle(list);
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> DEFAULT_SUGGESTION_PROVIDER = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
	private final StringReader reader;
	private final boolean field_10846;
	private int limit;
	private boolean includingNonPlayer;
	private boolean localWorldOnly;
	private NumberRange.FloatRange distance = NumberRange.FloatRange.ANY;
	private NumberRange.IntRange levelRange = NumberRange.IntRange.ANY;
	@Nullable
	private Double offsetX;
	@Nullable
	private Double offsetY;
	@Nullable
	private Double offsetZ;
	@Nullable
	private Double boxX;
	@Nullable
	private Double boxY;
	@Nullable
	private Double boxZ;
	private FloatRange pitchRange = FloatRange.ANY;
	private FloatRange yawRange = FloatRange.ANY;
	private Predicate<Entity> predicate = entity -> true;
	private BiConsumer<Vec3d, List<? extends Entity>> sorter = ARBITRARY;
	private boolean senderOnly;
	@Nullable
	private String playerName;
	private int startCursor;
	@Nullable
	private UUID uuid;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
	private boolean field_10854;
	private boolean field_10874;
	private boolean field_10851;
	private boolean field_10873;
	private boolean field_10849;
	private boolean field_10871;
	private boolean field_10845;
	private boolean field_10868;
	@Nullable
	private EntityType<?> entityType;
	private boolean field_10865;
	private boolean field_10841;
	private boolean field_10864;
	private boolean checkPermissions;

	public EntitySelectorReader(StringReader stringReader) {
		this(stringReader, true);
	}

	public EntitySelectorReader(StringReader stringReader, boolean bl) {
		this.reader = stringReader;
		this.field_10846 = bl;
	}

	public EntitySelector build() {
		Box box;
		if (this.boxX == null && this.boxY == null && this.boxZ == null) {
			if (this.distance.getMax() != null) {
				float f = (Float)this.distance.getMax();
				box = new Box((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
			} else {
				box = null;
			}
		} else {
			box = this.createBox(this.boxX == null ? 0.0 : this.boxX, this.boxY == null ? 0.0 : this.boxY, this.boxZ == null ? 0.0 : this.boxZ);
		}

		Function<Vec3d, Vec3d> function;
		if (this.offsetX == null && this.offsetY == null && this.offsetZ == null) {
			function = vec3d -> vec3d;
		} else {
			function = vec3d -> new Vec3d(
					this.offsetX == null ? vec3d.x : this.offsetX, this.offsetY == null ? vec3d.y : this.offsetY, this.offsetZ == null ? vec3d.z : this.offsetZ
				);
		}

		return new EntitySelector(
			this.limit,
			this.includingNonPlayer,
			this.localWorldOnly,
			this.predicate,
			this.distance,
			function,
			box,
			this.sorter,
			this.senderOnly,
			this.playerName,
			this.uuid,
			this.entityType,
			this.checkPermissions
		);
	}

	private Box createBox(double d, double e, double f) {
		boolean bl = d < 0.0;
		boolean bl2 = e < 0.0;
		boolean bl3 = f < 0.0;
		double g = bl ? d : 0.0;
		double h = bl2 ? e : 0.0;
		double i = bl3 ? f : 0.0;
		double j = (bl ? 0.0 : d) + 1.0;
		double k = (bl2 ? 0.0 : e) + 1.0;
		double l = (bl3 ? 0.0 : f) + 1.0;
		return new Box(g, h, i, j, k, l);
	}

	private void buildPredicate() {
		if (this.pitchRange != FloatRange.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.pitchRange, entity -> (double)entity.pitch));
		}

		if (this.yawRange != FloatRange.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.yawRange, entity -> (double)entity.yaw));
		}

		if (!this.levelRange.isDummy()) {
			this.predicate = this.predicate
				.and(entity -> !(entity instanceof ServerPlayerEntity) ? false : this.levelRange.test(((ServerPlayerEntity)entity).experienceLevel));
		}
	}

	private Predicate<Entity> rotationPredicate(FloatRange floatRange, ToDoubleFunction<Entity> toDoubleFunction) {
		double d = (double)MathHelper.wrapDegrees(floatRange.getMin() == null ? 0.0F : floatRange.getMin());
		double e = (double)MathHelper.wrapDegrees(floatRange.getMax() == null ? 359.0F : floatRange.getMax());
		return entity -> {
			double f = MathHelper.wrapDegrees(toDoubleFunction.applyAsDouble(entity));
			return d > e ? f >= d || f <= e : f >= d && f <= e;
		};
	}

	protected void readAtVariable() throws CommandSyntaxException {
		this.checkPermissions = true;
		this.suggestionProvider = this::suggestSelectorRest;
		if (!this.reader.canRead()) {
			throw MISSING_EXCEPTION.createWithContext(this.reader);
		} else {
			int i = this.reader.getCursor();
			char c = this.reader.read();
			if (c == 'p') {
				this.limit = 1;
				this.includingNonPlayer = false;
				this.sorter = NEAREST;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'a') {
				this.limit = Integer.MAX_VALUE;
				this.includingNonPlayer = false;
				this.sorter = ARBITRARY;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'r') {
				this.limit = 1;
				this.includingNonPlayer = false;
				this.sorter = RANDOM;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 's') {
				this.limit = 1;
				this.includingNonPlayer = true;
				this.senderOnly = true;
			} else {
				if (c != 'e') {
					this.reader.setCursor(i);
					throw UNKNOWN_SELECTOR_EXCEPTION.createWithContext(this.reader, '@' + String.valueOf(c));
				}

				this.limit = Integer.MAX_VALUE;
				this.includingNonPlayer = true;
				this.sorter = ARBITRARY;
				this.predicate = Entity::isAlive;
			}

			this.suggestionProvider = this::suggestOpen;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.reader.skip();
				this.suggestionProvider = this::suggestOptionOrEnd;
				this.readArguments();
			}
		}
	}

	protected void readRegular() throws CommandSyntaxException {
		if (this.reader.canRead()) {
			this.suggestionProvider = this::suggestNormal;
		}

		int i = this.reader.getCursor();
		String string = this.reader.readString();

		try {
			this.uuid = UUID.fromString(string);
			this.includingNonPlayer = true;
		} catch (IllegalArgumentException var4) {
			if (string.isEmpty() || string.length() > 16) {
				this.reader.setCursor(i);
				throw INVALID_ENTITY_EXCEPTION.createWithContext(this.reader);
			}

			this.includingNonPlayer = false;
			this.playerName = string;
		}

		this.limit = 1;
	}

	protected void readArguments() throws CommandSyntaxException {
		this.suggestionProvider = this::suggestOption;
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != ']') {
			this.reader.skipWhitespace();
			int i = this.reader.getCursor();
			String string = this.reader.readString();
			EntitySelectorOptions.SelectorHandler selectorHandler = EntitySelectorOptions.getHandler(this, string, i);
			this.reader.skipWhitespace();
			if (!this.reader.canRead() || this.reader.peek() != '=') {
				this.reader.setCursor(i);
				throw VALUELESS_EXCEPTION.createWithContext(this.reader, string);
			}

			this.reader.skip();
			this.reader.skipWhitespace();
			this.suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
			selectorHandler.handle(this);
			this.reader.skipWhitespace();
			this.suggestionProvider = this::suggestEndNext;
			if (this.reader.canRead()) {
				if (this.reader.peek() != ',') {
					if (this.reader.peek() != ']') {
						throw UNTERMINATED_EXCEPTION.createWithContext(this.reader);
					}
					break;
				}

				this.reader.skip();
				this.suggestionProvider = this::suggestOption;
			}
		}

		if (this.reader.canRead()) {
			this.reader.skip();
			this.suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
		} else {
			throw UNTERMINATED_EXCEPTION.createWithContext(this.reader);
		}
	}

	public boolean readNegationCharacter() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '!') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public boolean readTagCharacter() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '#') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public StringReader getReader() {
		return this.reader;
	}

	public void setPredicate(Predicate<Entity> predicate) {
		this.predicate = this.predicate.and(predicate);
	}

	public void setLocalWorldOnly() {
		this.localWorldOnly = true;
	}

	public NumberRange.FloatRange getDistance() {
		return this.distance;
	}

	public void setDistance(NumberRange.FloatRange floatRange) {
		this.distance = floatRange;
	}

	public NumberRange.IntRange getLevelRange() {
		return this.levelRange;
	}

	public void setLevelRange(NumberRange.IntRange intRange) {
		this.levelRange = intRange;
	}

	public FloatRange getPitchRange() {
		return this.pitchRange;
	}

	public void setPitchRange(FloatRange floatRange) {
		this.pitchRange = floatRange;
	}

	public FloatRange getYawRange() {
		return this.yawRange;
	}

	public void setYawRange(FloatRange floatRange) {
		this.yawRange = floatRange;
	}

	@Nullable
	public Double getOffsetX() {
		return this.offsetX;
	}

	@Nullable
	public Double getOffsetY() {
		return this.offsetY;
	}

	@Nullable
	public Double getOffsetZ() {
		return this.offsetZ;
	}

	public void setOffsetX(double d) {
		this.offsetX = d;
	}

	public void setOffsetY(double d) {
		this.offsetY = d;
	}

	public void setOffsetZ(double d) {
		this.offsetZ = d;
	}

	public void setBoxX(double d) {
		this.boxX = d;
	}

	public void setBoxY(double d) {
		this.boxY = d;
	}

	public void setBoxZ(double d) {
		this.boxZ = d;
	}

	@Nullable
	public Double getBoxX() {
		return this.boxX;
	}

	@Nullable
	public Double getBoxY() {
		return this.boxY;
	}

	@Nullable
	public Double getBoxZ() {
		return this.boxZ;
	}

	public void setLimit(int i) {
		this.limit = i;
	}

	public void setIncludingNonPlayer(boolean bl) {
		this.includingNonPlayer = bl;
	}

	public void setSorter(BiConsumer<Vec3d, List<? extends Entity>> biConsumer) {
		this.sorter = biConsumer;
	}

	public EntitySelector read() throws CommandSyntaxException {
		this.startCursor = this.reader.getCursor();
		this.suggestionProvider = this::suggestSelector;
		if (this.reader.canRead() && this.reader.peek() == '@') {
			if (!this.field_10846) {
				throw NOT_ALLOWED_EXCEPTION.createWithContext(this.reader);
			}

			this.reader.skip();
			this.readAtVariable();
		} else {
			this.readRegular();
		}

		this.buildPredicate();
		return this.build();
	}

	private static void suggestSelector(SuggestionsBuilder suggestionsBuilder) {
		suggestionsBuilder.suggest("@p", new TranslatableText("argument.entity.selector.nearestPlayer"));
		suggestionsBuilder.suggest("@a", new TranslatableText("argument.entity.selector.allPlayers"));
		suggestionsBuilder.suggest("@r", new TranslatableText("argument.entity.selector.randomPlayer"));
		suggestionsBuilder.suggest("@s", new TranslatableText("argument.entity.selector.self"));
		suggestionsBuilder.suggest("@e", new TranslatableText("argument.entity.selector.allEntities"));
	}

	private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(suggestionsBuilder);
		if (this.field_10846) {
			suggestSelector(suggestionsBuilder);
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestNormal(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startCursor);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	private CompletableFuture<Suggestions> suggestSelectorRest(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
		suggestSelector(suggestionsBuilder2);
		suggestionsBuilder.add(suggestionsBuilder2);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOpen(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf('['));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptionOrEnd(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(']'));
		EntitySelectorOptions.suggestOptions(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOption(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		EntitySelectorOptions.suggestOptions(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestEndNext(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(','));
		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	public boolean isSenderOnly() {
		return this.senderOnly;
	}

	public void setSuggestionProvider(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
		this.suggestionProvider = biFunction;
	}

	public CompletableFuture<Suggestions> listSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		return (CompletableFuture<Suggestions>)this.suggestionProvider.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
	}

	public boolean method_9912() {
		return this.field_10854;
	}

	public void method_9899(boolean bl) {
		this.field_10854 = bl;
	}

	public boolean method_9844() {
		return this.field_10874;
	}

	public void method_9913(boolean bl) {
		this.field_10874 = bl;
	}

	public boolean method_9866() {
		return this.field_10851;
	}

	public void method_9877(boolean bl) {
		this.field_10851 = bl;
	}

	public boolean method_9889() {
		return this.field_10873;
	}

	public void method_9887(boolean bl) {
		this.field_10873 = bl;
	}

	public boolean method_9839() {
		return this.field_10849;
	}

	public void method_9890(boolean bl) {
		this.field_10849 = bl;
	}

	public boolean method_9837() {
		return this.field_10871;
	}

	public void method_9857(boolean bl) {
		this.field_10871 = bl;
	}

	public boolean method_9904() {
		return this.field_10845;
	}

	public void method_9865(boolean bl) {
		this.field_10845 = bl;
	}

	public void method_9833(boolean bl) {
		this.field_10868 = bl;
	}

	public void setEntityType(EntityType<?> entityType) {
		this.entityType = entityType;
	}

	public void method_9860() {
		this.field_10865 = true;
	}

	public boolean hasEntityType() {
		return this.entityType != null;
	}

	public boolean method_9910() {
		return this.field_10865;
	}

	public boolean method_9843() {
		return this.field_10841;
	}

	public void method_9848(boolean bl) {
		this.field_10841 = bl;
	}

	public boolean method_9861() {
		return this.field_10864;
	}

	public void method_9906(boolean bl) {
		this.field_10864 = bl;
	}
}
