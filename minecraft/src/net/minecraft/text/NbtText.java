package net.minecraft.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NbtText extends BaseText implements ParsableText {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final boolean interpret;
	protected final String rawPath;
	@Nullable
	protected final NbtPathArgumentType.NbtPath path;

	@Nullable
	private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
		try {
			return new NbtPathArgumentType().parse(new StringReader(rawPath));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public NbtText(String rawPath, boolean interpret) {
		this(rawPath, parsePath(rawPath), interpret);
	}

	protected NbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret) {
		this.rawPath = rawPath;
		this.path = path;
		this.interpret = interpret;
	}

	protected abstract Stream<CompoundTag> toNbt(ServerCommandSource source) throws CommandSyntaxException;

	@Override
	public String asString() {
		return "";
	}

	public String getPath() {
		return this.rawPath;
	}

	public boolean shouldInterpret() {
		return this.interpret;
	}

	@Override
	public Text parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		if (source != null && this.path != null) {
			Stream<String> stream = this.toNbt(source).flatMap(nbt -> {
				try {
					return this.path.get(nbt).stream();
				} catch (CommandSyntaxException var3) {
					return Stream.empty();
				}
			}).map(Tag::asString);
			return (Text)(this.interpret ? (Text)stream.flatMap(text -> {
				try {
					Text text2 = Text.Serializer.fromJson(text);
					return Stream.of(Texts.parse(source, text2, sender, depth));
				} catch (Exception var5) {
					LOGGER.warn("Failed to parse component: " + text, (Throwable)var5);
					return Stream.of();
				}
			}).reduce((a, b) -> a.append(", ").append(b)).orElse(new LiteralText("")) : new LiteralText(Joiner.on(", ").join(stream.iterator())));
		} else {
			return new LiteralText("");
		}
	}

	public static class BlockNbtText extends NbtText {
		private final String rawPos;
		@Nullable
		private final PosArgument pos;

		public BlockNbtText(String rawPath, boolean rawJson, String rawPos) {
			super(rawPath, rawJson);
			this.rawPos = rawPos;
			this.pos = this.parsePos(this.rawPos);
		}

		@Nullable
		private PosArgument parsePos(String rawPos) {
			try {
				return BlockPosArgumentType.blockPos().parse(new StringReader(rawPos));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private BlockNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawPos, @Nullable PosArgument pos) {
			super(rawPath, path, interpret);
			this.rawPos = rawPos;
			this.pos = pos;
		}

		@Nullable
		public String getPos() {
			return this.rawPos;
		}

		@Override
		public Text copy() {
			return new NbtText.BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos);
		}

		@Override
		protected Stream<CompoundTag> toNbt(ServerCommandSource source) {
			if (this.pos != null) {
				ServerWorld serverWorld = source.getWorld();
				BlockPos blockPos = this.pos.toAbsoluteBlockPos(source);
				if (serverWorld.canSetBlock(blockPos)) {
					BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
					if (blockEntity != null) {
						return Stream.of(blockEntity.toTag(new CompoundTag()));
					}
				}
			}

			return Stream.empty();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (!(o instanceof NbtText.BlockNbtText)) {
				return false;
			} else {
				NbtText.BlockNbtText blockNbtText = (NbtText.BlockNbtText)o;
				return Objects.equals(this.rawPos, blockNbtText.rawPos) && Objects.equals(this.rawPath, blockNbtText.rawPath) && super.equals(o);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='" + this.rawPos + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
		}
	}

	public static class EntityNbtText extends NbtText {
		private final String rawSelector;
		@Nullable
		private final EntitySelector selector;

		public EntityNbtText(String rawPath, boolean interpret, String rawSelector) {
			super(rawPath, interpret);
			this.rawSelector = rawSelector;
			this.selector = parseSelector(rawSelector);
		}

		@Nullable
		private static EntitySelector parseSelector(String rawSelector) {
			try {
				EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
				return entitySelectorReader.read();
			} catch (CommandSyntaxException var2) {
				return null;
			}
		}

		private EntityNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, String rawSelector, @Nullable EntitySelector selector) {
			super(rawPath, path, interpret);
			this.rawSelector = rawSelector;
			this.selector = selector;
		}

		public String getSelector() {
			return this.rawSelector;
		}

		@Override
		public Text copy() {
			return new NbtText.EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector);
		}

		@Override
		protected Stream<CompoundTag> toNbt(ServerCommandSource source) throws CommandSyntaxException {
			if (this.selector != null) {
				List<? extends Entity> list = this.selector.getEntities(source);
				return list.stream().map(NbtPredicate::entityToTag);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (!(o instanceof NbtText.EntityNbtText)) {
				return false;
			} else {
				NbtText.EntityNbtText entityNbtText = (NbtText.EntityNbtText)o;
				return Objects.equals(this.rawSelector, entityNbtText.rawSelector) && Objects.equals(this.rawPath, entityNbtText.rawPath) && super.equals(o);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='"
				+ this.rawSelector
				+ '\''
				+ "path='"
				+ this.rawPath
				+ '\''
				+ ", siblings="
				+ this.siblings
				+ ", style="
				+ this.getStyle()
				+ '}';
		}
	}

	public static class StorageNbtText extends NbtText {
		private final Identifier id;

		public StorageNbtText(String rawPath, boolean interpret, Identifier id) {
			super(rawPath, interpret);
			this.id = id;
		}

		public StorageNbtText(String rawPath, @Nullable NbtPathArgumentType.NbtPath nbtPath, boolean interpret, Identifier id) {
			super(rawPath, nbtPath, interpret);
			this.id = id;
		}

		public Identifier method_23728() {
			return this.id;
		}

		@Override
		public Text copy() {
			return new NbtText.StorageNbtText(this.rawPath, this.path, this.interpret, this.id);
		}

		@Override
		protected Stream<CompoundTag> toNbt(ServerCommandSource source) {
			CompoundTag compoundTag = source.getMinecraftServer().getDataCommandStorage().get(this.id);
			return Stream.of(compoundTag);
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof NbtText.StorageNbtText)) {
				return false;
			} else {
				NbtText.StorageNbtText storageNbtText = (NbtText.StorageNbtText)object;
				return Objects.equals(this.id, storageNbtText.id) && Objects.equals(this.rawPath, storageNbtText.rawPath) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "StorageNbtComponent{id='" + this.id + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
		}
	}
}
