package net.linusaddons.mod.features.kuudra;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.linusaddons.mod.events.impl.ClientTickEvent;
import net.linusaddons.mod.events.impl.WorldRenderEvent;
import net.linusaddons.mod.features.KuudraFeature;
import net.linusaddons.mod.manager.BoundingBoxManager;
import net.linusaddons.mod.manager.TentacleBoxState;
import net.linusaddons.mod.model.kuudra.KuudraPhase;
import net.linusaddons.mod.utils.render.RenderColor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

@Getter
@Slf4j
public class TentacleDetectFeature extends KuudraFeature {

    private final List<MagmaCube> miniTentacleBases   = new ArrayList<>();
    private final List<MagmaCube> mediumTentacleBases = new ArrayList<>();
    private final List<MagmaCube> bigTentacleBases    = new ArrayList<>();
    public record SpawnBox(String id, BoundingBoxManager box) {}
    private final List<SpawnBox> occupiedSpawnBoxes = new ArrayList<>();
    private final Map<String, TentacleBoxState> spawnBoxStates = new HashMap<>();

    private static final List<SpawnBox> PossibleSpawnBoxes = List.of(
            // most efficient code ever written
            new SpawnBox("1", new BoundingBoxManager(new Vec3(-119.5, 58, -140.5), new Vec3(-109.5, 73, -134.5))),
            new SpawnBox("2", new BoundingBoxManager(new Vec3(-128.5, 58, -146.5), new Vec3(-122.5, 68, -140.5))),
            new SpawnBox("3", new BoundingBoxManager(new Vec3(-147.5, 58, -142.5), new Vec3(-140.5, 68, -135.5))),
            new SpawnBox("4", new BoundingBoxManager(new Vec3(-148.5, 58, -128.5), new Vec3(-143.5, 68, -122.5))),
            new SpawnBox("5", new BoundingBoxManager(new Vec3(-141.5, 58, -116.5), new Vec3(-135.5, 73, -106.5))),
            new SpawnBox("6", new BoundingBoxManager(new Vec3(-158.5, 58, -97.5), new Vec3(-152.5, 68, -91.5))),
            new SpawnBox("7", new BoundingBoxManager(new Vec3(-148.5, 58, -77.5), new Vec3(-141.5, 68, -70.5))),
            new SpawnBox("8", new BoundingBoxManager(new Vec3(-124.5, 58, -84.5),new Vec3(-128.5, 58, -80.5),new Vec3(-117.5, 58, -77.5),new Vec3(-121.5, 58, -73.5), 58, 70)),
            new SpawnBox("9", new BoundingBoxManager(new Vec3(-128.5, 58, -80.5),new Vec3(-132.5, 58, -76.5),new Vec3(-121.5, 58, -73.5),new Vec3(-125.5, 58, -69.5), 58, 68)),
            new SpawnBox("10", new BoundingBoxManager(new Vec3(-101.5, 58, -76.5), new Vec3(-91.5, 73, -70.5))),
            new SpawnBox("11", new BoundingBoxManager(new Vec3(-82.5, 58, -69.5), new Vec3(-76.5, 68, -64.5))),
            new SpawnBox("12", new BoundingBoxManager(new Vec3(-76.5, 58, -85.5), new Vec3(-66, 73, -75))),
            new SpawnBox("13", new BoundingBoxManager(new Vec3(-54.5, 58, -87.5), new Vec3(-47.5, 68, -81.5))),
            new SpawnBox("14", new BoundingBoxManager(new Vec3(-61.5, 58, -98.5), new Vec3(-54.5, 73, -87.5))),
            new SpawnBox("15", new BoundingBoxManager(new Vec3(-72.5, 58, -116.5), new Vec3(-65.5, 73, -106.5))),
            new SpawnBox("16", new BoundingBoxManager(new Vec3(-70.5, 58, -133.5), new Vec3(-62.5, 73, -124.5))),
            new SpawnBox("17", new BoundingBoxManager(new Vec3(-57.5, 58, -134.5), new Vec3(-50.5, 68, -128.5))),
            new SpawnBox("18", new BoundingBoxManager(new Vec3(-71.5, 58, -152.5), new Vec3(-65.5, 68, -145.5))),
            new SpawnBox("19", new BoundingBoxManager(new Vec3(-93.5, 58, -139.5), new Vec3(-82.5, 73, -132.5))),
            new SpawnBox("20", new BoundingBoxManager(new Vec3(-90.5, 58, -145.5), new Vec3(-83.5, 68, -139.5))),
            new SpawnBox("21", new BoundingBoxManager(new Vec3(-97.5, 58, -163.5), new Vec3(-85.5, 68, -154.5))),
            new SpawnBox("22", new BoundingBoxManager(new Vec3(-104.5, 58, -154.5), new Vec3(-98.5, 68, -149.5))),
            new SpawnBox("23", new BoundingBoxManager(new Vec3(-123.5, 58, -160.5), new Vec3(-116.5, 68, -153.5))),
            new SpawnBox("24", new BoundingBoxManager(new Vec3(-158.5, 58, -119.5), new Vec3(-152.5, 68, -113.5))),
            new SpawnBox("25", new BoundingBoxManager(new Vec3(-132.5, 58, -62.5), new Vec3(-125.5, 68, -55.5))),
            new SpawnBox("26", new BoundingBoxManager(new Vec3(-108.5, 58, -53.5), new Vec3(-102.5, 68, -47.5))),
            new SpawnBox("27", new BoundingBoxManager(new Vec3(-87.5, 58, -57.5), new Vec3(-81.5, 68, -50.5))),
            new SpawnBox("28", new BoundingBoxManager(new Vec3(-69.5, 58, -68.5), new Vec3(-62.5, 68, -61.5))),
            new SpawnBox("29", new BoundingBoxManager(new Vec3(-49.5, 58, -110.5), new Vec3(-43.5, 68, -104.5)))
                );

    public enum TentacleSize { MINI, MEDIUM, BIG, BOX }

    public TentacleDetectFeature() {
        super(
                "TentecleDetect",
                "Tentacle Detecter",
                () -> LinusAddonsConfig.TentDetector,
                KuudraPhase.SUPPLIES
        );
    }

    @Override
    protected void onKuudraActivate() {
        subscribe(ClientTickEvent.class, this::onClientTick);
        subscribe(WorldRenderEvent.class, this::onRender);
    }

    private void onClientTick(ClientTickEvent event) {
        if (mc.player == null) return;
        findTentacleBases(mc.player);
    }

    private void findTentacleBases(LocalPlayer player) {
        if (mc.level == null) return;

        AABB searchBox = new AABB(-200, 60, -200, -13, 120, -16);

        miniTentacleBases.clear();
        mediumTentacleBases.clear();
        bigTentacleBases.clear();
        occupiedSpawnBoxes.clear();
        spawnBoxStates.clear();

        mc.level.getEntities(
                player,
                searchBox,
                e -> e instanceof MagmaCube
        ).forEach(e -> {
            MagmaCube cube = (MagmaCube) e;
            double y    = cube.getY();
            double size = cube.getSize();

            TentacleBoxState cubeState;
            if (Math.abs(y-67.0) < 0.5 && size == 13) {
                cubeState = TentacleBoxState.MINI;
                miniTentacleBases.add(cube);
            } else if (Math.abs(y-65.0) < 0.5 && size == 16) {
                cubeState = TentacleBoxState.MEDIUM;
                mediumTentacleBases.add(cube);
            } else if (/*Math.abs(y-59.0) < 0.5 &&*/ size == 20) {
                cubeState = TentacleBoxState.BIG;
                bigTentacleBases.add(cube);
            } else {
                return;
            }

            Vec3 cubePos = cube.position();
            for (SpawnBox spawnBox : PossibleSpawnBoxes) {
                if (!spawnBox.box().containsPoint(cubePos)) continue;

                if (!occupiedSpawnBoxes.contains(spawnBox)) {
                    occupiedSpawnBoxes.add(spawnBox);
                }

                TentacleBoxState current = spawnBoxStates.getOrDefault(spawnBox.id(), TentacleBoxState.EMPTY);
                if (cubeState.ordinal() > current.ordinal()) {
                    spawnBoxStates.put(spawnBox.id(), cubeState);
                }
            }
        });
    }

    private void onRender(WorldRenderEvent event) {
        if (isSizeEnabled(TentacleSize.BOX)) {
            for (SpawnBox spawnBox : PossibleSpawnBoxes) {
                boolean occupied = occupiedSpawnBoxes.contains(spawnBox);
                RenderColor color = occupied
                        ? RenderColor.fromArgb(0x44FF4444)  // red tint = occupied
                        : RenderColor.fromArgb(0x44FFFFFF); // white = empty

                if (spawnBox.box().isRotated()) {
                    event.drawRotatedBoxOutline(spawnBox.box().getCorners(), true, color);
                } else {
                    event.drawOutline(spawnBox.box().toAABB(), true, color);
                }
            }
        }
        if (isSizeEnabled(TentacleSize.BIG)) {
            for (MagmaCube cube : bigTentacleBases) {

                AABB box = cube.getBoundingBox();

                double cx = (box.minX + box.maxX) / 2.0;
                double cy = box.minY;
                double cz = (box.minZ + box.maxZ) / 2.0;

                AABB customBox = new AABB(
                        cx - 0.5, cy,     cz - 0.5,
                        cx + 0.5, cy + 1, cz + 0.5
                );

                event.drawStyledBox(
                        customBox,
                        true,
                        RenderColor.fromArgb(LinusAddonsConfig.largeTentColor),
                        LinusAddonsConfig.tentStyle
                );
            }
        }

        if (isSizeEnabled(TentacleSize.MEDIUM)) {
            for (MagmaCube cube : mediumTentacleBases) {

                AABB box = cube.getBoundingBox();

                double cx = (box.minX + box.maxX) / 2.0;
                double cy = box.minY;
                double cz = (box.minZ + box.maxZ) / 2.0;

                AABB customBox = new AABB(
                        cx - 0.5, cy,
                        cz - 0.5,
                        cx + 0.5, cy + 1,
                        cz + 0.5
                );

                event.drawStyledBox(
                        customBox,
                        true,
                        RenderColor.fromArgb(LinusAddonsConfig.mediumTentColor),
                        LinusAddonsConfig.tentStyle
                );
            }
        }

        if (isSizeEnabled(TentacleSize.MINI)) {
            for (MagmaCube cube : miniTentacleBases) {

                AABB box = cube.getBoundingBox();

                double cx = (box.minX + box.maxX) / 2.0;
                double cy = box.minY;
                double cz = (box.minZ + box.maxZ) / 2.0;

                AABB customBox = new AABB(
                        cx - 0.5, cy,
                        cz - 0.5,
                        cx + 0.5, cy + 1,
                        cz + 0.5
                );

                event.drawStyledBox(
                        customBox,
                        true,
                        RenderColor.fromArgb(LinusAddonsConfig.miniTentColor),
                        LinusAddonsConfig.tentStyle
                );
            }
        }
    }

    private boolean isSizeEnabled(TentacleSize size) {
        for (TentacleDetectFeature.TentacleSize enabled : LinusAddonsConfig.renderTentacleSizes) {
            if (enabled == size) return true;
        }
        return false;
    }
}