package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PathUtil;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ShaderLoader extends SinglePreparationResourceReloader<ShaderLoader.Definitions> implements AutoCloseable {
	static final Logger LOGGER = LogUtils.getLogger();
	public static final String SHADERS_PATH = "shaders";
	public static final String INCLUDE_PATH = "shaders/include/";
	private static final ResourceFinder SHADERS_FINDER = ResourceFinder.json("shaders");
	private static final ResourceFinder POST_EFFECT_FINDER = ResourceFinder.json("post_effect");
	public static final int field_53936 = 32768;
	final TextureManager textureManager;
	private final Consumer<Exception> onError;
	private ShaderLoader.Cache cache = new ShaderLoader.Cache(ShaderLoader.Definitions.EMPTY);

	public ShaderLoader(TextureManager textureManager, Consumer<Exception> onError) {
		this.textureManager = textureManager;
		this.onError = onError;
	}

	protected ShaderLoader.Definitions prepare(ResourceManager resourceManager, Profiler profiler) {
		Builder<Identifier, ShaderProgramDefinition> builder = ImmutableMap.builder();
		Builder<ShaderLoader.ShaderSourceKey, String> builder2 = ImmutableMap.builder();
		Map<Identifier, Resource> map = resourceManager.findResources("shaders", id -> isDefinition(id) || isShaderSource(id));

		for (Entry<Identifier, Resource> entry : map.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			CompiledShader.Type type = CompiledShader.Type.fromId(identifier);
			if (type != null) {
				loadShaderSource(identifier, (Resource)entry.getValue(), type, map, builder2);
			} else if (isDefinition(identifier)) {
				loadDefinition(identifier, (Resource)entry.getValue(), builder);
			}
		}

		Builder<Identifier, PostEffectPipeline> builder3 = ImmutableMap.builder();

		for (Entry<Identifier, Resource> entry2 : POST_EFFECT_FINDER.findResources(resourceManager).entrySet()) {
			loadPostEffect((Identifier)entry2.getKey(), (Resource)entry2.getValue(), builder3);
		}

		return new ShaderLoader.Definitions(builder.build(), builder2.build(), builder3.build());
	}

	private static void loadShaderSource(
		Identifier id, Resource resource, CompiledShader.Type type, Map<Identifier, Resource> allResources, Builder<ShaderLoader.ShaderSourceKey, String> builder
	) {
		Identifier identifier = type.createFinder().toResourceId(id);
		GlImportProcessor glImportProcessor = createImportProcessor(allResources, id);

		try {
			Reader reader = resource.getReader();

			try {
				String string = IOUtils.toString(reader);
				builder.put(new ShaderLoader.ShaderSourceKey(identifier, type), String.join("", glImportProcessor.readSource(string)));
			} catch (Throwable var11) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var10) {
						var11.addSuppressed(var10);
					}
				}

				throw var11;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (IOException var12) {
			LOGGER.error("Failed to load shader source at {}", id, var12);
		}
	}

	private static GlImportProcessor createImportProcessor(Map<Identifier, Resource> allResources, Identifier id) {
		final Identifier identifier = id.withPath(PathUtil::getPosixFullPath);
		return new GlImportProcessor() {
			private final Set<Identifier> processed = new ObjectArraySet<>();

			@Override
			public String loadImport(boolean inline, String name) {
				Identifier identifier;
				try {
					if (inline) {
						identifier = identifier.withPath((UnaryOperator<String>)(path -> PathUtil.normalizeToPosix(path + name)));
					} else {
						identifier = Identifier.of(name).withPrefixedPath("shaders/include/");
					}
				} catch (InvalidIdentifierException var8) {
					ShaderLoader.LOGGER.error("Malformed GLSL import {}: {}", name, var8.getMessage());
					return "#error " + var8.getMessage();
				}

				if (!this.processed.add(identifier)) {
					return null;
				} else {
					try {
						Reader reader = ((Resource)allResources.get(identifier)).getReader();

						String var5;
						try {
							var5 = IOUtils.toString(reader);
						} catch (Throwable var9) {
							if (reader != null) {
								try {
									reader.close();
								} catch (Throwable var7) {
									var9.addSuppressed(var7);
								}
							}

							throw var9;
						}

						if (reader != null) {
							reader.close();
						}

						return var5;
					} catch (IOException var10) {
						ShaderLoader.LOGGER.error("Could not open GLSL import {}: {}", identifier, var10.getMessage());
						return "#error " + var10.getMessage();
					}
				}
			}
		};
	}

	private static void loadDefinition(Identifier id, Resource resource, Builder<Identifier, ShaderProgramDefinition> builder) {
		Identifier identifier = SHADERS_FINDER.toResourceId(id);

		try {
			Reader reader = resource.getReader();

			try {
				JsonElement jsonElement = JsonParser.parseReader(reader);
				ShaderProgramDefinition shaderProgramDefinition = ShaderProgramDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new);
				builder.put(identifier, shaderProgramDefinition);
			} catch (Throwable var8) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (JsonParseException | IOException var9) {
			LOGGER.error("Failed to parse shader config at {}", id, var9);
		}
	}

	private static void loadPostEffect(Identifier id, Resource resource, Builder<Identifier, PostEffectPipeline> builder) {
		Identifier identifier = POST_EFFECT_FINDER.toResourceId(id);

		try {
			Reader reader = resource.getReader();

			try {
				JsonElement jsonElement = JsonParser.parseReader(reader);
				builder.put(identifier, PostEffectPipeline.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new));
			} catch (Throwable var8) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (JsonParseException | IOException var9) {
			LOGGER.error("Failed to parse post chain at {}", id, var9);
		}
	}

	private static boolean isDefinition(Identifier id) {
		return id.getPath().endsWith(".json");
	}

	private static boolean isShaderSource(Identifier id) {
		return CompiledShader.Type.fromId(id) != null || id.getPath().endsWith(".glsl");
	}

	protected void apply(ShaderLoader.Definitions definitions, ResourceManager resourceManager, Profiler profiler) {
		ShaderLoader.Cache cache = new ShaderLoader.Cache(definitions);
		Map<ShaderProgramKey, ShaderLoader.LoadException> map = new HashMap();

		for (ShaderProgramKey shaderProgramKey : ShaderProgramKeys.getAll()) {
			try {
				cache.shaderPrograms.put(shaderProgramKey, Optional.of(cache.loadProgram(shaderProgramKey)));
			} catch (ShaderLoader.LoadException var9) {
				map.put(shaderProgramKey, var9);
			}
		}

		if (!map.isEmpty()) {
			cache.close();
			throw new RuntimeException(
				"Failed to load required shader programs:\n"
					+ (String)map.entrySet()
						.stream()
						.map(entry -> " - " + entry.getKey() + ": " + ((ShaderLoader.LoadException)entry.getValue()).getMessage())
						.collect(Collectors.joining("\n"))
			);
		} else {
			this.cache.close();
			this.cache = cache;
		}
	}

	@Override
	public String getName() {
		return "Shader Loader";
	}

	public void preload(ResourceFactory factory, ShaderProgramKey... keys) throws IOException, ShaderLoader.LoadException {
		for (ShaderProgramKey shaderProgramKey : keys) {
			Resource resource = factory.getResourceOrThrow(SHADERS_FINDER.toResourcePath(shaderProgramKey.configId()));
			Reader reader = resource.getReader();

			try {
				JsonElement jsonElement = JsonParser.parseReader(reader);
				ShaderProgramDefinition shaderProgramDefinition = ShaderProgramDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new);
				Defines defines = shaderProgramDefinition.defines().withMerged(shaderProgramKey.defines());
				CompiledShader compiledShader = this.compileShader(factory, shaderProgramDefinition.vertex(), CompiledShader.Type.VERTEX, defines);
				CompiledShader compiledShader2 = this.compileShader(factory, shaderProgramDefinition.fragment(), CompiledShader.Type.FRAGMENT, defines);
				ShaderProgram shaderProgram = createProgram(shaderProgramKey, shaderProgramDefinition, compiledShader, compiledShader2);
				this.cache.shaderPrograms.put(shaderProgramKey, Optional.of(shaderProgram));
			} catch (Throwable var16) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var15) {
						var16.addSuppressed(var15);
					}
				}

				throw var16;
			}

			if (reader != null) {
				reader.close();
			}
		}
	}

	private CompiledShader compileShader(ResourceFactory factory, Identifier id, CompiledShader.Type type, Defines defines) throws IOException, ShaderLoader.LoadException {
		Identifier identifier = type.createFinder().toResourcePath(id);
		Reader reader = factory.getResourceOrThrow(identifier).getReader();

		CompiledShader var10;
		try {
			String string = IOUtils.toString(reader);
			String string2 = GlImportProcessor.addDefines(string, defines);
			CompiledShader compiledShader = CompiledShader.compile(id, type, string2);
			this.cache.compiledShaders.put(new ShaderLoader.ShaderKey(id, type, defines), compiledShader);
			var10 = compiledShader;
		} catch (Throwable var12) {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable var11) {
					var12.addSuppressed(var11);
				}
			}

			throw var12;
		}

		if (reader != null) {
			reader.close();
		}

		return var10;
	}

	@Nullable
	public ShaderProgram getOrCreateProgram(ShaderProgramKey key) {
		try {
			return this.cache.getOrLoadProgram(key);
		} catch (ShaderLoader.LoadException var3) {
			LOGGER.error("Failed to load shader program: {}", key, var3);
			this.cache.shaderPrograms.put(key, Optional.empty());
			this.onError.accept(var3);
			return null;
		}
	}

	public ShaderProgram getProgramToLoad(ShaderProgramKey key) throws ShaderLoader.LoadException {
		ShaderProgram shaderProgram = this.cache.getOrLoadProgram(key);
		if (shaderProgram == null) {
			throw new ShaderLoader.LoadException("Shader '" + key + "' could not be found");
		} else {
			return shaderProgram;
		}
	}

	static ShaderProgram createProgram(ShaderProgramKey key, ShaderProgramDefinition definition, CompiledShader vertexShader, CompiledShader fragmentShader) throws ShaderLoader.LoadException {
		ShaderProgram shaderProgram = ShaderProgram.create(vertexShader, fragmentShader, key.vertexFormat());
		shaderProgram.set(definition.uniforms(), definition.samplers());
		return shaderProgram;
	}

	@Nullable
	public PostEffectProcessor loadPostEffect(Identifier id, Set<Identifier> availableExternalTargets) {
		try {
			return this.cache.getOrLoadProcessor(id, availableExternalTargets);
		} catch (ShaderLoader.LoadException var4) {
			LOGGER.error("Failed to load post chain: {}", id, var4);
			this.cache.postEffectProcessors.put(id, Optional.empty());
			this.onError.accept(var4);
			return null;
		}
	}

	public void close() {
		this.cache.close();
	}

	@Environment(EnvType.CLIENT)
	class Cache implements AutoCloseable {
		private final ShaderLoader.Definitions definitions;
		final Map<ShaderProgramKey, Optional<ShaderProgram>> shaderPrograms = new HashMap();
		final Map<ShaderLoader.ShaderKey, CompiledShader> compiledShaders = new HashMap();
		final Map<Identifier, Optional<PostEffectProcessor>> postEffectProcessors = new HashMap();

		Cache(final ShaderLoader.Definitions definitions) {
			this.definitions = definitions;
		}

		@Nullable
		public ShaderProgram getOrLoadProgram(ShaderProgramKey key) throws ShaderLoader.LoadException {
			Optional<ShaderProgram> optional = (Optional<ShaderProgram>)this.shaderPrograms.get(key);
			if (optional != null) {
				return (ShaderProgram)optional.orElse(null);
			} else {
				ShaderProgram shaderProgram = this.loadProgram(key);
				this.shaderPrograms.put(key, Optional.of(shaderProgram));
				return shaderProgram;
			}
		}

		ShaderProgram loadProgram(ShaderProgramKey key) throws ShaderLoader.LoadException {
			ShaderProgramDefinition shaderProgramDefinition = (ShaderProgramDefinition)this.definitions.programs.get(key.configId());
			if (shaderProgramDefinition == null) {
				throw new ShaderLoader.LoadException("Could not find program with id: " + key.configId());
			} else {
				Defines defines = shaderProgramDefinition.defines().withMerged(key.defines());
				CompiledShader compiledShader = this.loadShader(shaderProgramDefinition.vertex(), CompiledShader.Type.VERTEX, defines);
				CompiledShader compiledShader2 = this.loadShader(shaderProgramDefinition.fragment(), CompiledShader.Type.FRAGMENT, defines);
				return ShaderLoader.createProgram(key, shaderProgramDefinition, compiledShader, compiledShader2);
			}
		}

		private CompiledShader loadShader(Identifier id, CompiledShader.Type type, Defines defines) throws ShaderLoader.LoadException {
			ShaderLoader.ShaderKey shaderKey = new ShaderLoader.ShaderKey(id, type, defines);
			CompiledShader compiledShader = (CompiledShader)this.compiledShaders.get(shaderKey);
			if (compiledShader == null) {
				compiledShader = this.compileShader(shaderKey);
				this.compiledShaders.put(shaderKey, compiledShader);
			}

			return compiledShader;
		}

		private CompiledShader compileShader(ShaderLoader.ShaderKey key) throws ShaderLoader.LoadException {
			String string = (String)this.definitions.shaderSources.get(new ShaderLoader.ShaderSourceKey(key.id, key.type));
			if (string == null) {
				throw new ShaderLoader.LoadException("Could not find shader: " + key);
			} else {
				String string2 = GlImportProcessor.addDefines(string, key.defines);
				return CompiledShader.compile(key.id, key.type, string2);
			}
		}

		@Nullable
		public PostEffectProcessor getOrLoadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
			Optional<PostEffectProcessor> optional = (Optional<PostEffectProcessor>)this.postEffectProcessors.get(id);
			if (optional != null) {
				return (PostEffectProcessor)optional.orElse(null);
			} else {
				PostEffectProcessor postEffectProcessor = this.loadProcessor(id, availableExternalTargets);
				this.postEffectProcessors.put(id, Optional.of(postEffectProcessor));
				return postEffectProcessor;
			}
		}

		private PostEffectProcessor loadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
			PostEffectPipeline postEffectPipeline = (PostEffectPipeline)this.definitions.postChains.get(id);
			if (postEffectPipeline == null) {
				throw new ShaderLoader.LoadException("Could not find post chain with id: " + id);
			} else {
				return PostEffectProcessor.parseEffect(postEffectPipeline, ShaderLoader.this.textureManager, ShaderLoader.this, availableExternalTargets);
			}
		}

		public void close() {
			RenderSystem.assertOnRenderThread();
			this.shaderPrograms.values().forEach(program -> program.ifPresent(ShaderProgram::close));
			this.compiledShaders.values().forEach(CompiledShader::close);
			this.shaderPrograms.clear();
			this.compiledShaders.clear();
			this.postEffectProcessors.clear();
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Definitions(
		Map<Identifier, ShaderProgramDefinition> programs, Map<ShaderLoader.ShaderSourceKey, String> shaderSources, Map<Identifier, PostEffectPipeline> postChains
	) {
		public static final ShaderLoader.Definitions EMPTY = new ShaderLoader.Definitions(Map.of(), Map.of(), Map.of());
	}

	@Environment(EnvType.CLIENT)
	public static class LoadException extends Exception {
		public LoadException(String message) {
			super(message);
		}
	}

	@Environment(EnvType.CLIENT)
	static record ShaderKey(Identifier id, CompiledShader.Type type, Defines defines) {

		public String toString() {
			String string = this.id + " (" + this.type + ")";
			return !this.defines.isEmpty() ? string + " with " + this.defines : string;
		}
	}

	@Environment(EnvType.CLIENT)
	static record ShaderSourceKey(Identifier id, CompiledShader.Type type) {
		public String toString() {
			return this.id + " (" + this.type + ")";
		}
	}
}
