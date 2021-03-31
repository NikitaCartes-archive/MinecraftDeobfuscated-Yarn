/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the flattening of "moj_" import strings in the loaded GLSL shader file.
 * Instances of an import are replaced by the contents of the referenced file
 * prefixed by a comment describing the line position and original file location
 * of the import.
 */
@Environment(value=EnvType.CLIENT)
public abstract class GLImportProcessor {
    private static final String field_32036 = "/\\*(?:[^*]|\\*+[^/])*\\*+/";
    private static final Pattern MOJ_IMPORT_PATTERN = Pattern.compile("(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(#(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))");
    private static final Pattern IMPORT_VERSION_PATTERN = Pattern.compile("(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(#(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^/])*\\*+/|\\h)*(\\d+))\\b");

    /**
     * Reads the source code supplied into a list of lines suitable for uploading to
     * the GL Shader cache.
     * 
     * <p>Imports are processed as per the description of this class.
     */
    public List<String> readSource(String source) {
        Context context = new Context();
        List<String> list = this.parseImports(source, context, "");
        list.set(0, this.readImport(list.get(0), context.column));
        return list;
    }

    private List<String> parseImports(String source, Context context, String path) {
        String string2;
        int i = context.line;
        int j = 0;
        String string = "";
        ArrayList<String> list = Lists.newArrayList();
        Matcher matcher = MOJ_IMPORT_PATTERN.matcher(source);
        while (matcher.find()) {
            int k;
            boolean bl;
            string2 = matcher.group(2);
            boolean bl2 = bl = string2 != null;
            if (!bl) {
                string2 = matcher.group(3);
            }
            if (string2 == null) continue;
            String string3 = source.substring(j, matcher.start(1));
            String string4 = path + string2;
            String string5 = this.loadImport(bl, string4);
            if (!Strings.isEmpty(string5)) {
                context.line = context.line + 1;
                k = context.line;
                List<String> list2 = this.parseImports(string5, context, bl ? FileNameUtil.getPosixFullPath(string4) : "");
                list2.set(0, String.format("#line %d %d\n%s", 0, k, this.extractVersion(list2.get(0), context)));
                if (!StringUtils.isBlank(string3)) {
                    list.add(string3);
                }
                list.addAll(list2);
            } else {
                String string6 = bl ? String.format("/*#moj_import \"%s\"*/", string2) : String.format("/*#moj_import <%s>*/", string2);
                list.add(string + string3 + string6);
            }
            k = ChatUtil.countLines(source.substring(0, matcher.end(1)));
            string = String.format("#line %d %d", k, i);
            j = matcher.end(1);
        }
        string2 = source.substring(j);
        if (!StringUtils.isBlank(string2)) {
            list.add(string + string2);
        }
        return list;
    }

    /**
     * Converts a line known to contain an import into a fully-qualified
     * version of itself for insertion as a comment.
     */
    private String extractVersion(String line, Context context) {
        Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
        if (matcher.find()) {
            context.column = Math.max(context.column, Integer.parseInt(matcher.group(2)));
            return line.substring(0, matcher.start(1)) + "/*" + line.substring(matcher.start(1), matcher.end(1)) + "*/" + line.substring(matcher.end(1));
        }
        return line;
    }

    private String readImport(String line, int start) {
        Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
        if (matcher.find()) {
            return line.substring(0, matcher.start(2)) + Math.max(start, Integer.parseInt(matcher.group(2))) + line.substring(matcher.end(2));
        }
        return line;
    }

    /**
     * Called to load an import reference's source code.
     */
    @Nullable
    public abstract String loadImport(boolean var1, String var2);

    @Environment(value=EnvType.CLIENT)
    static final class Context {
        private int column;
        private int line;

        private Context() {
        }
    }
}

