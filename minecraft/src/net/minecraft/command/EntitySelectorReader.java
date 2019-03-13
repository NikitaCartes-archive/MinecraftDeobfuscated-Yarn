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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntitySelectorReader {
	public static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.invalid")
	);
	public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.selector.unknown", object)
	);
	public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.selector.not_allowed")
	);
	public static final SimpleCommandExceptionType MISSING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.selector.missing")
	);
	public static final SimpleCommandExceptionType UNTERMINATED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.entity.options.unterminated")
	);
	public static final DynamicCommandExceptionType VALUELESS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.entity.options.valueless", object)
	);
	public static final BiConsumer<Vec3d, List<? extends Entity>> UNSORTED = (vec3d, list) -> {
	};
	public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST_FIRST = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity.method_5707(vec3d), entity2.method_5707(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST_FIRST = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity2.method_5707(vec3d), entity.method_5707(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM = (vec3d, list) -> Collections.shuffle(list);
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_10867 = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
	private final StringReader reader;
	private final boolean field_10846;
	private int field_10858;
	private boolean field_10843;
	private boolean field_10866;
	private NumberRange.Float field_10838 = NumberRange.Float.ANY;
	private NumberRange.Integer experience = NumberRange.Integer.ANY;
	@Nullable
	private Double field_10857;
	@Nullable
	private Double field_10872;
	@Nullable
	private Double field_10839;
	@Nullable
	private Double field_10862;
	@Nullable
	private Double field_10852;
	@Nullable
	private Double field_10881;
	private FloatRange pitchRange = FloatRange.ANY;
	private FloatRange yawRange = FloatRange.ANY;
	private Predicate<Entity> predicate = entity -> true;
	private BiConsumer<Vec3d, List<? extends Entity>> sorter = UNSORTED;
	private boolean field_10879;
	@Nullable
	private String field_10876;
	private int field_10861;
	@Nullable
	private UUID field_10878;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = field_10867;
	private boolean field_10854;
	private boolean field_10874;
	private boolean field_10851;
	private boolean field_10873;
	private boolean field_10849;
	private boolean field_10871;
	private boolean field_10845;
	private boolean field_10868;
	@Nullable
	private EntityType<?> type;
	private boolean field_10865;
	private boolean field_10841;
	private boolean field_10864;
	private boolean field_10840;

	public EntitySelectorReader(StringReader stringReader) {
		this(stringReader, true);
	}

	public EntitySelectorReader(StringReader stringReader, boolean bl) {
		this.reader = stringReader;
		this.field_10846 = bl;
	}

	public EntitySelector build() {
		BoundingBox boundingBox;
		if (this.field_10862 == null && this.field_10852 == null && this.field_10881 == null) {
			if (this.field_10838.getMax() != null) {
				float f = (Float)this.field_10838.getMax();
				boundingBox = new BoundingBox((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
			} else {
				boundingBox = null;
			}
		} else {
			boundingBox = this.method_9894(
				this.field_10862 == null ? 0.0 : this.field_10862, this.field_10852 == null ? 0.0 : this.field_10852, this.field_10881 == null ? 0.0 : this.field_10881
			);
		}

		Function<Vec3d, Vec3d> function;
		if (this.field_10857 == null && this.field_10872 == null && this.field_10839 == null) {
			function = vec3d -> vec3d;
		} else {
			function = vec3d -> new Vec3d(
					this.field_10857 == null ? vec3d.x : this.field_10857,
					this.field_10872 == null ? vec3d.y : this.field_10872,
					this.field_10839 == null ? vec3d.z : this.field_10839
				);
		}

		return new EntitySelector(
			this.field_10858,
			this.field_10843,
			this.field_10866,
			this.predicate,
			this.field_10838,
			function,
			boundingBox,
			this.sorter,
			this.field_10879,
			this.field_10876,
			this.field_10878,
			this.type,
			this.field_10840
		);
	}

	private BoundingBox method_9894(double d, double e, double f) {
		boolean bl = d < 0.0;
		boolean bl2 = e < 0.0;
		boolean bl3 = f < 0.0;
		double g = bl ? d : 0.0;
		double h = bl2 ? e : 0.0;
		double i = bl3 ? f : 0.0;
		double j = (bl ? 0.0 : d) + 1.0;
		double k = (bl2 ? 0.0 : e) + 1.0;
		double l = (bl3 ? 0.0 : f) + 1.0;
		return new BoundingBox(g, h, i, j, k, l);
	}

	private void buildPredicate() {
		if (this.pitchRange != FloatRange.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.pitchRange, entity -> (double)entity.pitch));
		}

		if (this.yawRange != FloatRange.ANY) {
			this.predicate = this.predicate.and(this.rotationPredicate(this.yawRange, entity -> (double)entity.yaw));
		}

		if (!this.experience.isDummy()) {
			this.predicate = this.predicate
				.and(entity -> !(entity instanceof ServerPlayerEntity) ? false : this.experience.test(((ServerPlayerEntity)entity).experience));
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

	protected void method_9917() throws CommandSyntaxException {
		this.field_10840 = true;
		this.suggestions = this::method_9834;
		if (!this.reader.canRead()) {
			throw MISSING_EXCEPTION.createWithContext(this.reader);
		} else {
			int i = this.reader.getCursor();
			char c = this.reader.read();
			if (c == 'p') {
				this.field_10858 = 1;
				this.field_10843 = false;
				this.sorter = NEAREST_FIRST;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'a') {
				this.field_10858 = Integer.MAX_VALUE;
				this.field_10843 = false;
				this.sorter = UNSORTED;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 'r') {
				this.field_10858 = 1;
				this.field_10843 = false;
				this.sorter = RANDOM;
				this.setEntityType(EntityType.PLAYER);
			} else if (c == 's') {
				this.field_10858 = 1;
				this.field_10843 = true;
				this.field_10879 = true;
			} else {
				if (c != 'e') {
					this.reader.setCursor(i);
					throw UNKNOWN_SELECTOR_EXCEPTION.createWithContext(this.reader, '@' + String.valueOf(c));
				}

				this.field_10858 = Integer.MAX_VALUE;
				this.field_10843 = true;
				this.sorter = UNSORTED;
				this.predicate = Entity::isValid;
			}

			this.suggestions = this::suggestOpen;
			if (this.reader.canRead() && this.reader.peek() == '[') {
				this.reader.skip();
				this.suggestions = this::suggestOptionOrEnd;
				this.method_9874();
			}
		}
	}

	protected void method_9849() throws CommandSyntaxException {
		if (this.reader.canRead()) {
			this.suggestions = this::method_9858;
		}

		int i = this.reader.getCursor();
		String string = this.reader.readString();

		try {
			this.field_10878 = UUID.fromString(string);
			this.field_10843 = true;
		} catch (IllegalArgumentException var4) {
			if (string.isEmpty() || string.length() > 16) {
				this.reader.setCursor(i);
				throw INVALID_ENTITY_EXCEPTION.createWithContext(this.reader);
			}

			this.field_10843 = false;
			this.field_10876 = string;
		}

		this.field_10858 = 1;
	}

	protected void method_9874() throws CommandSyntaxException {
		this.suggestions = this::suggestOption;
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
			this.suggestions = field_10867;
			selectorHandler.handle(this);
			this.reader.skipWhitespace();
			this.suggestions = this::suggestEndNext;
			if (this.reader.canRead()) {
				if (this.reader.peek() != ',') {
					if (this.reader.peek() != ']') {
						throw UNTERMINATED_EXCEPTION.createWithContext(this.reader);
					}
					break;
				}

				this.reader.skip();
				this.suggestions = this::suggestOption;
			}
		}

		if (this.reader.canRead()) {
			this.reader.skip();
			this.suggestions = field_10867;
		} else {
			throw UNTERMINATED_EXCEPTION.createWithContext(this.reader);
		}
	}

	public boolean method_9892() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '!') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public boolean method_9915() {
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

	public void method_9852() {
		this.field_10866 = true;
	}

	public NumberRange.Float method_9873() {
		return this.field_10838;
	}

	public void method_9870(NumberRange.Float float_) {
		this.field_10838 = float_;
	}

	public NumberRange.Integer method_9895() {
		return this.experience;
	}

	public void method_9846(NumberRange.Integer integer) {
		this.experience = integer;
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
	public Double method_9902() {
		return this.field_10857;
	}

	@Nullable
	public Double method_9884() {
		return this.field_10872;
	}

	@Nullable
	public Double method_9868() {
		return this.field_10839;
	}

	public void method_9850(double d) {
		this.field_10857 = d;
	}

	public void method_9864(double d) {
		this.field_10872 = d;
	}

	public void method_9879(double d) {
		this.field_10839 = d;
	}

	public void method_9891(double d) {
		this.field_10862 = d;
	}

	public void method_9905(double d) {
		this.field_10852 = d;
	}

	public void method_9918(double d) {
		this.field_10881 = d;
	}

	@Nullable
	public Double method_9851() {
		return this.field_10862;
	}

	@Nullable
	public Double method_9840() {
		return this.field_10852;
	}

	@Nullable
	public Double method_9907() {
		return this.field_10881;
	}

	public void method_9900(int i) {
		this.field_10858 = i;
	}

	public void method_9841(boolean bl) {
		this.field_10843 = bl;
	}

	public void setOrdering(BiConsumer<Vec3d, List<? extends Entity>> biConsumer) {
		this.sorter = biConsumer;
	}

	public EntitySelector read() throws CommandSyntaxException {
		this.field_10861 = this.reader.getCursor();
		this.suggestions = this::method_9880;
		if (this.reader.canRead() && this.reader.peek() == '@') {
			if (!this.field_10846) {
				throw NOT_ALLOWED_EXCEPTION.createWithContext(this.reader);
			}

			this.reader.skip();
			this.method_9917();
		} else {
			this.method_9849();
		}

		this.buildPredicate();
		return this.build();
	}

	private static void suggestSelector(SuggestionsBuilder suggestionsBuilder) {
		suggestionsBuilder.suggest("@p", new TranslatableTextComponent("argument.entity.selector.nearestPlayer"));
		suggestionsBuilder.suggest("@a", new TranslatableTextComponent("argument.entity.selector.allPlayers"));
		suggestionsBuilder.suggest("@r", new TranslatableTextComponent("argument.entity.selector.randomPlayer"));
		suggestionsBuilder.suggest("@s", new TranslatableTextComponent("argument.entity.selector.self"));
		suggestionsBuilder.suggest("@e", new TranslatableTextComponent("argument.entity.selector.allEntities"));
	}

	private CompletableFuture<Suggestions> method_9880(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(suggestionsBuilder);
		if (this.field_10846) {
			suggestSelector(suggestionsBuilder);
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9858(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.field_10861);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	private CompletableFuture<Suggestions> method_9834(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
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

	public boolean method_9885() {
		return this.field_10879;
	}

	public void setSuggestionProvider(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
		this.suggestions = biFunction;
	}

	public CompletableFuture<Suggestions> listSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		return (CompletableFuture<Suggestions>)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
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
		this.type = entityType;
	}

	public void method_9860() {
		this.field_10865 = true;
	}

	public boolean hasEntityType() {
		return this.type != null;
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
