package net.linusaddons.mod.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.linusaddons.mod.manager.ConditionalRenderBox;
import net.linusaddons.mod.model.ConditionalBoxData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ConditionalBoxConfigLoader {

    private static final ConditionalBoxConfigLoader INSTANCE = new ConditionalBoxConfigLoader();

    private static final Path CONFIG_DIR  = FabricLoader.getInstance().getConfigDir().resolve("linusaddons");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("conditional_boxes.json");
    private static final String DEFAULT_RESOURCE = "/default-config/linusaddons/conditional_boxes.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private volatile List<ConditionalRenderBox> cachedBoxes = Collections.emptyList();
    private volatile List<ConditionalBoxData>   cachedData  = Collections.emptyList();

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public @NotNull List<ConditionalRenderBox> load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                log.info("Loading conditional boxes from: {}", CONFIG_FILE);
                try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                    return parseAndCache(reader);
                }
            }

            log.info("conditional_boxes.json not found — writing bundled default");
            try (InputStream is = getClass().getResourceAsStream(DEFAULT_RESOURCE)) {
                if (is == null) {
                    log.warn("No bundled default for conditional_boxes.json — starting with empty list");
                    cachedBoxes = Collections.emptyList();
                    cachedData  = Collections.emptyList();
                    return cachedBoxes;
                }
                try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    List<ConditionalRenderBox> result = parseAndCache(reader);
                    saveCurrentDataToFile();
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Failed to load conditional_boxes.json", e);
            cachedBoxes = Collections.emptyList();
            cachedData  = Collections.emptyList();
            return cachedBoxes;
        }
    }

    public @NotNull List<ConditionalRenderBox> reload() {
        log.info("Reloading conditional_boxes.json");
        return load();
    }

    public @NotNull List<ConditionalRenderBox> getCached() {
        return cachedBoxes;
    }

    public void appendBox(@NotNull ConditionalBoxData data) throws IOException {
        JsonObject root;
        if (Files.exists(CONFIG_FILE)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else {
            root = buildSkeletonJson();
        }

        JsonArray boxes = root.has("boxes") ? root.getAsJsonArray("boxes") : new JsonArray();
        boxes.add(dataToJson(data));
        root.add("boxes", boxes);

        Files.createDirectories(CONFIG_FILE.getParent());
        Files.writeString(CONFIG_FILE, GSON.toJson(root), StandardCharsets.UTF_8);
        log.info("Appended conditional box '{}' to {}", data.label(), CONFIG_FILE);

        load();
    }

    public static ConditionalBoxConfigLoader get() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Manual JSON parsing — avoids Gson record deserialization bug in Gson 2.8
    // -----------------------------------------------------------------------

    private @NotNull List<ConditionalRenderBox> parseAndCache(@NotNull Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray array = root.has("boxes") ? root.getAsJsonArray("boxes") : new JsonArray();

        List<ConditionalBoxData>   dataList = new ArrayList<>(array.size());
        List<ConditionalRenderBox> boxList  = new ArrayList<>(array.size());

        for (JsonElement element : array) {
            try {
                ConditionalBoxData data = parseBoxData(element.getAsJsonObject());
                if (data == null) continue;
                ConditionalRenderBox box = data.toRenderBox();
                if (box == null) {
                    log.warn("Skipping invalid conditional box entry: {}", element);
                    continue;
                }
                dataList.add(data);
                boxList.add(box);
            } catch (Exception e) {
                log.warn("Failed to parse conditional box entry: {}", element, e);
            }
        }

        log.info("Loaded {} conditional box(es)", boxList.size());
        cachedData  = Collections.unmodifiableList(dataList);
        cachedBoxes = Collections.unmodifiableList(boxList);
        return cachedBoxes;
    }

    /** Manually reads one box object — does NOT use Gson.fromJson on a record. */
    private @Nullable ConditionalBoxData parseBoxData(@NotNull JsonObject obj) {
        if (!obj.has("label") || !obj.has("center")) {
            log.warn("Conditional box entry missing required 'label' or 'center' field: {}", obj);
            return null;
        }

        String label = obj.get("label").getAsString();

        JsonArray centerArr = obj.getAsJsonArray("center");
        double[] center = {
                centerArr.get(0).getAsDouble(),
                centerArr.get(1).getAsDouble(),
                centerArr.get(2).getAsDouble()
        };

        double sizeX = obj.has("sizeX") ? obj.get("sizeX").getAsDouble() : 1.0;
        double sizeY = obj.has("sizeY") ? obj.get("sizeY").getAsDouble() : 1.0;
        double sizeZ = obj.has("sizeZ") ? obj.get("sizeZ").getAsDouble() : 1.0;

        int[] color = {85, 255, 85, 136}; // default green
        if (obj.has("color")) {
            JsonArray c = obj.getAsJsonArray("color");
            color = new int[]{
                    c.get(0).getAsInt(),
                    c.get(1).getAsInt(),
                    c.get(2).getAsInt(),
                    c.size() >= 4 ? c.get(3).getAsInt() : 255
            };
        }

        String style = obj.has("style") ? obj.get("style").getAsString() : "OUTLINE";

        List<ConditionalBoxData.ConditionData> conditions = new ArrayList<>();
        if (obj.has("conditions")) {
            for (JsonElement el : obj.getAsJsonArray("conditions")) {
                JsonObject co = el.getAsJsonObject();
                String type  = co.has("type") ? co.get("type").getAsString() : null;
                String box   = co.has("box")  ? co.get("box").getAsString()  : null;
                String size  = co.has("size") ? co.get("size").getAsString() : null;
                if (type != null && box != null) {
                    conditions.add(new ConditionalBoxData.ConditionData(type, box, size));
                }
            }
        }

        return new ConditionalBoxData(label, center, sizeX, sizeY, sizeZ, color, style, conditions);
    }

    // -----------------------------------------------------------------------
    // Writing helpers
    // -----------------------------------------------------------------------

    private void saveCurrentDataToFile() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            JsonObject root = buildSkeletonJson();
            JsonArray boxes = new JsonArray();
            for (ConditionalBoxData data : cachedData) {
                boxes.add(dataToJson(data));
            }
            root.add("boxes", boxes);
            Files.writeString(CONFIG_FILE, GSON.toJson(root), StandardCharsets.UTF_8);
            log.info("Wrote default conditional_boxes.json to {}", CONFIG_FILE);
        } catch (Exception e) {
            log.warn("Failed to write default conditional_boxes.json", e);
        }
    }

    /** Serialises a {@link ConditionalBoxData} to JSON manually (safe with any Gson version). */
    private static @NotNull JsonObject dataToJson(@NotNull ConditionalBoxData data) {
        JsonObject obj = new JsonObject();
        obj.addProperty("label", data.label());

        JsonArray center = new JsonArray();
        center.add(data.center()[0]);
        center.add(data.center()[1]);
        center.add(data.center()[2]);
        obj.add("center", center);

        obj.addProperty("sizeX", data.sizeX());
        obj.addProperty("sizeY", data.sizeY());
        obj.addProperty("sizeZ", data.sizeZ());

        JsonArray color = new JsonArray();
        color.add(data.color()[0]);
        color.add(data.color()[1]);
        color.add(data.color()[2]);
        color.add(data.color()[3]);
        obj.add("color", color);

        obj.addProperty("style", data.style());

        JsonArray conditions = new JsonArray();
        if (data.conditions() != null) {
            for (ConditionalBoxData.ConditionData cond : data.conditions()) {
                JsonObject co = new JsonObject();
                co.addProperty("type", cond.type());
                co.addProperty("box",  cond.box());
                if (cond.size() != null) co.addProperty("size", cond.size());
                conditions.add(co);
            }
        }
        obj.add("conditions", conditions);

        return obj;
    }

    private static @NotNull JsonObject buildSkeletonJson() {
        JsonObject root = new JsonObject();
        root.addProperty("_comment",
                "LA Conditional Render Boxes — edit this file, then run /la reload (no restart needed).");

        JsonObject guide = new JsonObject();
        JsonObject boxFields = new JsonObject();
        boxFields.addProperty("label",      "Friendly name shown in logs. Can be anything.");
        boxFields.addProperty("center",     "[x, y, z] world-space centre of the box.");
        boxFields.addProperty("sizeX",      "Width  (X axis). Default: 1.0");
        boxFields.addProperty("sizeY",      "Height (Y axis). Default: 1.0");
        boxFields.addProperty("sizeZ",      "Depth  (Z axis). Default: 1.0");
        boxFields.addProperty("color",      "[R, G, B] or [R, G, B, A] — each 0-255. A defaults to 255.");
        boxFields.addProperty("style",      "OUTLINE | SOLID | BOTH");
        boxFields.addProperty("conditions", "All must pass to render (AND logic). Empty array = always render.");
        guide.add("boxFields", boxFields);

        JsonObject condFields = new JsonObject();
        condFields.addProperty("type", "MUST_BE_EMPTY | MAX_SIZE_MEDIUM | MAX_SIZE_MINI | OCCUPIED | MIN_SIZE | EXACT_SIZE");
        condFields.addProperty("box",  "Spawn-box ID string, e.g. \"1\", \"3\".");
        condFields.addProperty("size", "Only for MIN_SIZE / EXACT_SIZE: EMPTY | MINI | MEDIUM | BIG");
        guide.add("conditionFields", condFields);

        root.add("_fieldGuide", guide);
        root.add("boxes", new JsonArray());
        return root;
    }
}