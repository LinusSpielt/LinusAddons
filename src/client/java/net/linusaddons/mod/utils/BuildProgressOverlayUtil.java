package net.linusaddons.mod.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.linusaddons.mod.config.categories.LinusAddonsConfig;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public final class BuildProgressOverlayUtil {

	private static final Pattern PROGRESS_PATTERN = Pattern.compile("Building Progress:?\\s*(\\d+)%");
    private static final Pattern BUILDERS_PATTERN = Pattern.compile("\\((\\d+)\\s+Players? Helping\\)");
	private static final Pattern PILE_PROGRESS_PATTERN = Pattern.compile("PROGRESS:\\s*(?:§.)?(\\d+)%");
	private static final int TOTAL_PILE_COUNT = 6;
	public static final long BUILD_START_COUNTDOWN_MS = 6200L;

	private static boolean lastSimpleEnabled = LinusAddonsConfig.simpleBuildProgressOverlay;

	public static boolean isSimpleOverlayEnabled() {
		syncOverlayModes();
		return LinusAddonsConfig.simpleBuildProgressOverlay;
	}

	public static @Nullable String getCountdownColor(long remainingMs) {
		if (remainingMs <= 0) return null;

		double ratio = Math.min(1.0, Math.max(0.0, (double) remainingMs / BUILD_START_COUNTDOWN_MS));
		if (ratio > 0.75) return "§a";
		if (ratio > 0.50) return "§e";
		if (ratio > 0.25) return "§6";
		return "§c";
	}

	public static @NotNull String formatCountdownSeconds(long remainingMs) {
		double seconds = Math.max(0L, remainingMs) / 1000.0;
		return String.format(Locale.ROOT, "%.2f", seconds);
	}

	public static void syncOverlayModes() {
		boolean simpleEnabled = LinusAddonsConfig.simpleBuildProgressOverlay;

		if (simpleEnabled) {
			boolean simpleChanged = simpleEnabled != lastSimpleEnabled;

			if (!simpleChanged) {
				LinusAddonsConfig.simpleBuildProgressOverlay = false;
			} else {
				LinusAddonsConfig.simpleBuildProgressOverlay = false;
			}
		}

		lastSimpleEnabled = LinusAddonsConfig.simpleBuildProgressOverlay;
	}

	public static @Nullable BuildProgressData getBuildProgressFromArmorStand() {
		for (ArmorStand stand : EntityDetectorUtil.getAllArmorStands()) {
			if (!stand.hasCustomName() || stand.getCustomName() == null) continue;

			String stripped = Objects.requireNonNull(stand.getCustomName()).getString().replaceAll("§.", "");
			if (!stripped.contains("Building Progress")) continue;

			Matcher progressMatcher = PROGRESS_PATTERN.matcher(stripped);
			Matcher buildersMatcher = BUILDERS_PATTERN.matcher(stripped);
			if (!progressMatcher.find()) continue;

			try {
				int progress = Integer.parseInt(progressMatcher.group(1));
				int builders = buildersMatcher.find() ? Integer.parseInt(buildersMatcher.group(1)) : 0;
				return new BuildProgressData(progress, builders);
			} catch (NumberFormatException e) {
				log.warn("Failed to parse build progress armor stand: {}", stripped);
			}
		}

		return null;
	}

	public static @Nullable Integer getAggregatedPileProgress() {
		int sum = 0;
		int found = 0;

		for (ArmorStand stand : EntityDetectorUtil.getAllArmorStands()) {
			if (!stand.hasCustomName() || stand.getCustomName() == null) continue;

			String stripped = Objects.requireNonNull(stand.getCustomName()).getString().replaceAll("§.", "");
			if (!stripped.contains("PROGRESS:")) continue;

			if (stripped.contains("COMPLETE")) {
				sum += 100;
				found++;
				continue;
			}

			if (!stripped.contains("%")) continue;

			Matcher matcher = PILE_PROGRESS_PATTERN.matcher(stripped);
			if (!matcher.find()) continue;

			try {
				sum += Integer.parseInt(matcher.group(1));
				found++;
			} catch (NumberFormatException e) {
				log.warn("Failed to parse pile progress armor stand: {}", stripped);
			}
		}

		if (found == 0) return null;

		return Math.min(100, sum / TOTAL_PILE_COUNT);
	}

    public record BuildProgressData(
            int progress,
            int builders
    ) {
    }
}
