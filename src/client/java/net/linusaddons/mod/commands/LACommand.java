package net.linusaddons.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.linusaddons.mod.LAKeyBindings;
import net.linusaddons.mod.config.loader.ConditionalBoxConfigLoader;
import net.linusaddons.mod.features.kuudra.TentacleDetectFeature;
import net.linusaddons.mod.hud.HudManager;
import net.linusaddons.mod.model.ConditionalBoxData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

@Slf4j
public class LACommand {

    private static final Minecraft mc = Minecraft.getInstance();

    @Setter
    private static TentacleDetectFeature tentacleDetect;

    public static void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("la")
                        .executes(ctx -> {
                            LAKeyBindings.openConfigScreen(mc);
                            return 1;
                        })
                        .then(literal("hud").executes(ctx -> {
                            HudManager.get().openEditor();
                            return 1;
                        }))
                        .then(literal("reload").executes(ctx -> {
                            ConditionalBoxConfigLoader.get().reload();
                            int count = ConditionalBoxConfigLoader.get().getCached().size();
                            ctx.getSource().sendFeedback(Component.literal(
                                    "§l§8[§r§cL§6A§l§8] §r§fDynamic Waypoints reloaded. §e" + count + " §fwaypoint(s) active."));
                            return 1;
                        }))
                        .then(literal("waypoints").executes(ctx -> {
                            try {
                                Path configDir = FabricLoader.getInstance().getConfigDir().resolve("linusaddons");
                                Files.createDirectories(configDir);
                                Util.getPlatform().openFile(configDir.toFile());
                                ctx.getSource().sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§fOpening dynamic waypoints config folder..."));
                            } catch (Exception e) {
                                ctx.getSource().sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§fFailed to open config folder."));
                            }
                            return 1;
                        }))
                        .then(literal("discord").executes(ctx -> {
                            Util.getPlatform().openUri("https://discord.gg/fJeDhZbv2a");
                            ctx.getSource().sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§fOpening LinusAddons Discord invite..."));
                            return 1;
                        }))
                        .then(literal("listWaypoints").executes(ctx -> {
                            listConditionalBoxes(ctx.getSource());
                            return 1;
                        }))
                        .then(literal("createWaypoint")
                                .executes(ctx -> createConditionalBox(ctx.getSource(), null))
                                .then(com.mojang.brigadier.builder.RequiredArgumentBuilder
                                        .<FabricClientCommandSource, String>argument("label", StringArgumentType.greedyString())
                                        .executes(ctx -> createConditionalBox(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "label"))))
                        )
        );
    }


    // -----------------------------------------------------------------------
    // /la createWaypoint [name]
    // -----------------------------------------------------------------------

    private static int createConditionalBox(@NotNull FabricClientCommandSource source, String label) {
        if (mc.player == null) {
            source.sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§cYou must be in a world."));
            return 0;
        }

        Vec3 pos = mc.player.position();
        String resolvedLabel = (label != null && !label.isBlank())
                ? label
                : String.format(Locale.ROOT, "Box @ %.1f %.1f %.1f", pos.x, pos.y, pos.z);

        ConditionalBoxData data = new ConditionalBoxData(
                resolvedLabel,
                new double[]{
                        Math.round(pos.x * 10.0) / 10.0,
                        Math.round(pos.y * 10.0) / 10.0 + 0.5,
                        Math.round(pos.z * 10.0) / 10.0
                },
                1.0, 1.0, 1.0,
                new int[]{85, 255, 85, 136},
                "OUTLINE",
                Collections.emptyList()
        );

        try {
            ConditionalBoxConfigLoader.get().appendBox(data);
            int total = ConditionalBoxConfigLoader.get().getCached().size();
            source.sendFeedback(Component.literal(
                    "§l§8[§r§cL§6A§l§8] §r§fCreated dynamic Waypoint §e\"" + resolvedLabel + "\"§f. "
                    + "§7Edit §fdynamic_waypoints.json§7 to add conditions, then run §f/la reload§7. "
                    + "(" + total + " waypoint(s) total)"));
            source.sendFeedback(Component.literal(
                    "§8    Tip: run §f/la waypoints§8 to open the config folder."));
        } catch (Exception e) {
            source.sendFeedback(Component.literal(
                    "§l§8[§r§cL§6A§l§8] §r§cFailed to save dynamic waypoint: " + e.getMessage()));
            return 0;
        }

        return 1;
    }

    private static void listConditionalBoxes(@NotNull FabricClientCommandSource source) {
        var boxes = ConditionalBoxConfigLoader.get().getCached();
        if (boxes.isEmpty()) {
            source.sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§7No dynamic waypoints loaded. Use §f/la createWaypoint§7 to create one."));
            return;
        }

        source.sendFeedback(Component.literal("§l§8[§r§cL§6A§l§8] §r§fDynamic Waypoints (§e" + boxes.size() + "§f):"));
        for (int i = 0; i < boxes.size(); i++) {
            var box = boxes.get(i);
            source.sendFeedback(Component.literal(String.format(
                    "  §8%d. §f%s §8— §7%d condition(s)",
                    i + 1, box.label(), box.conditions().size())));
        }
    }
}