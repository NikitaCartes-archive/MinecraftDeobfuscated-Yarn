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
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
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
	private final boolean atAllowed;
	private int limit;
	private boolean includesNonPlayers;
	private boolean localWorldOnly;
	private NumberRange.FloatRange distance = NumberRange.FloatRange.ANY;
	private NumberRange.IntRange levelRange = NumberRange.IntRange.ANY;
	@Nullable
	private Double x;
	@Nullable
	private Double y;
	@Nullable
	private Double z;
	@Nullable
	private Double dx;
	@Nullable
	private Double dy;
	@Nullable
	private Double dz;
	private FloatRangeArgument pitchRange = FloatRangeArgument.ANY;
	private FloatRangeArgument yawRange = FloatRangeArgument.ANY;
	private Predicate<Entity> predicate = entity -> true;
	private BiConsumer<Vec3d, List<? extends Entity>> sorter = ARBITRARY;
	private boolean senderOnly;
	@Nullable
	private String playerName;
	private int startCursor;
	@Nullable
	private UUID uuid;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
	private boolean selectsName;
	private boolean excludesName;
	private boolean hasLimit;
	private boolean hasSorter;
	private boolean selectsGameMode;
	private boolean excludesGameMode;
	private boolean selectsTeam;
	private boolean excludesTeam;
	@Nullable
	private EntityType<?> entityType;
	private boolean excludesEntityType;
	private boolean selectsScores;
	private boolean selectsAdvancements;
	private boolean usesAt;

	public EntitySelectorReader(StringReader stringReader) {
		this(stringReader, true);
	}

	public EntitySelectorReader(StringReader stringReader, boolean bl) {
		this.reader = stringReader;
		this.atAllowed = bl;
	}

	public EntitySelector build() {
		Box box;
		if (this.dx == null && this.dy == null && this.dz == null) {
			if (this.distance.getMax() != null) {
				float f = (Float)this.distance.getMax();
				box = new Box((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
			} else {
				box = null;
			}
		} else {
			box = this.createBox(this.dx == null ? 0.0 : this.dx, this.dy == null ? 0.0 : this.dy, this.dz == null ? 0.0 : this.dz);
		}

		Function<Vec3d, Vec3d> function;
		if (this.x == null && this.y == null && this.z == null) {
			function = vec3d -> vec3d;
		} else {
			function = vec3d -> new Vec3d(this.x == null ? vec3d.x : this.x, this.y == null ? vec3d.y : this.y, this.z == null ? vec3d.z : this.z);
		}

		return new EntitySelector(
			this.limit,
			this.includesNonPlayers,
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
			this.usesAt
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
		if (this.pitchRange != FloatRangeArgument.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.pitchRange, entity -> (double)entity.pitch));
		}

		if (this.yawRange != FloatRangeArgument.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.yawRange, entity -> (double)entity.yaw));
		}

		if (!this.levelRange.isDummy()) {
			this.predicate = this.predicate
				.and(entity -> !(entity instanceof ServerPlayerEntity) ? false : this.levelRange.test(((ServerPlayerEntity)entity).experienceLevel));
		}
	}

	private Predicate<Entity> rotationPredicate(FloatRangeArgument floatRangeArgument, ToDoubleFunction<Entity> toDoubleFunction) {
		double d = (double)MathHelper.wrapDegrees(floatRangeArgument.getMin() == null ? 0.0F : floatRangeArgument.getMin());
		double e = (double)MathHelper.wrapDegrees(floatRangeArgument.getMax() == null ? 359.0F : floatRangeArgument.getMax());
		return entity -> {
			double f = MathHelper.wrapDegrees(toDoubleFunction.applyAsDouble(entity));
			return d > e ? f >= d || f <= e : f >= d && f <= e;
		};
	}

	protected void readAtVariable() throws CommandSyntaxException {
		this.usesAt = true;
		this.suggestionProvider = this::suggestSelectorRest;
		if (!this.reader.canRead()) {
			throw MISSING_EXCEPTION.createWithContext(this.reader);
		} else {
			int i = this.reader.getCursor();
			char c = this.reader.read();
			if (c == 'p') {
				this.limit = 1;
				this.includesNonPlayers = false;
				this.sorter = NEAREST;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'a') {
				this.limit = Integer.MAX_VALUE;
				this.includesNonPlayers = false;
				this.sorter = ARBITRARY;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'r') {
				this.limit = 1;
				this.includesNonPlayers = false;
				this.sorter = RANDOM;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 's') {
				this.limit = 1;
				this.includesNonPlayers = true;
				this.senderOnly = true;
			} else {
				if (c != 'e') {
					this.reader.setCursor(i);
					throw UNKNOWN_SELECTOR_EXCEPTION.createWithContext(this.reader, '@' + String.valueOf(c));
				}

				this.limit = Integer.MAX_VALUE;
				this.includesNonPlayers = true;
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
			this.includesNonPlayers = true;
		} catch (IllegalArgumentException var4) {
			if (string.isEmpty() || string.length() > 16) {
				this.reader.setCursor(i);
				throw INVALID_ENTITY_EXCEPTION.createWithContext(this.reader);
			}

			this.includesNonPlayers = false;
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

	public FloatRangeArgument getPitchRange() {
		return this.pitchRange;
	}

	public void setPitchRange(FloatRangeArgument floatRangeArgument) {
		this.pitchRange = floatRangeArgument;
	}

	public FloatRangeArgument getYawRange() {
		return this.yawRange;
	}

	public void setYawRange(FloatRangeArgument floatRangeArgument) {
		this.yawRange = floatRangeArgument;
	}

	@Nullable
	public Double getX() {
		return this.x;
	}

	@Nullable
	public Double getY() {
		return this.y;
	}

	@Nullable
	public Double getZ() {
		return this.z;
	}

	public void setX(double d) {
		this.x = d;
	}

	public void setY(double d) {
		this.y = d;
	}

	public void setZ(double d) {
		this.z = d;
	}

	public void setDx(double d) {
		this.dx = d;
	}

	public void setDy(double d) {
		this.dy = d;
	}

	public void setDz(double d) {
		this.dz = d;
	}

	@Nullable
	public Double getDx() {
		return this.dx;
	}

	@Nullable
	public Double getDy() {
		return this.dy;
	}

	@Nullable
	public Double getDz() {
		return this.dz;
	}

	public void setLimit(int i) {
		this.limit = i;
	}

	public void setIncludesNonPlayers(boolean bl) {
		this.includesNonPlayers = bl;
	}

	public void setSorter(BiConsumer<Vec3d, List<? extends Entity>> biConsumer) {
		this.sorter = biConsumer;
	}

	public EntitySelector read() throws CommandSyntaxException {
		this.startCursor = this.reader.getCursor();
		this.suggestionProvider = this::suggestSelector;
		if (this.reader.canRead() && this.reader.peek() == '@') {
			if (!this.atAllowed) {
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
		if (this.atAllowed) {
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

	public boolean selectsName() {
		return this.selectsName;
	}

	public void setSelectsName(boolean bl) {
		this.selectsName = bl;
	}

	public boolean excludesName() {
		return this.excludesName;
	}

	public void setExcludesName(boolean bl) {
		this.excludesName = bl;
	}

	public boolean hasLimit() {
		return this.hasLimit;
	}

	public void setHasLimit(boolean bl) {
		this.hasLimit = bl;
	}

	public boolean hasSorter() {
		return this.hasSorter;
	}

	public void setHasSorter(boolean bl) {
		this.hasSorter = bl;
	}

	public boolean selectsGameMode() {
		return this.selectsGameMode;
	}

	public void setSelectsGameMode(boolean bl) {
		this.selectsGameMode = bl;
	}

	public boolean excludesGameMode() {
		return this.excludesGameMode;
	}

	public void setHasNegatedGameMode(boolean bl) {
		this.excludesGameMode = bl;
	}

	public boolean selectsTeam() {
		return this.selectsTeam;
	}

	public void setSelectsTeam(boolean bl) {
		this.selectsTeam = bl;
	}

	public void setExcludesTeam(boolean bl) {
		this.excludesTeam = bl;
	}

	public void setEntityType(EntityType<?> entityType) {
		this.entityType = entityType;
	}

	public void setExcludesEntityType() {
		this.excludesEntityType = true;
	}

	public boolean selectsEntityType() {
		return this.entityType != null;
	}

	public boolean excludesEntityType() {
		return this.excludesEntityType;
	}

	public boolean selectsScores() {
		return this.selectsScores;
	}

	public void setSelectsScores(boolean bl) {
		this.selectsScores = bl;
	}

	public boolean selectsAdvancements() {
		return this.selectsAdvancements;
	}

	public void setSelectsAdvancements(boolean bl) {
		this.selectsAdvancements = bl;
	}
}
