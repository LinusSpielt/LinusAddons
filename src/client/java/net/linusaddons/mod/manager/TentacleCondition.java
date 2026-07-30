package net.linusaddons.mod.manager;

import java.util.Map;
import java.util.function.Predicate;

/**
 * A single condition on a specific spawn box, evaluated against the live
 * {@link TentacleBoxState} map produced by {@code TentacleDetectFeature}.
 *
 * Available factories:
 *   TentacleCondition.mustBeEmpty("3")       — box 3 has NO tentacle
 *   TentacleCondition.maxSizeMedium("1")     — box 1 is EMPTY, MINI, or MEDIUM (not big)
 *   TentacleCondition.occupied("2")          — box 2 has ANY tentacle
 *   TentacleCondition.minSize(BIG, "5")      — box 5 has a big tentacle
 *   TentacleCondition.exactSize(MEDIUM, "7") — box 7 has exactly a medium tentacle
 */
public record TentacleCondition(
        String spawnBoxId,
        Predicate<TentacleBoxState> predicate,
        String description
) {

     /**
     * Returns true when this condition is satisfied for the given state map.
     * A box absent from the map is treated as EMPTY.
     */
    public boolean test(Map<String, TentacleBoxState> stateMap) {
        TentacleBoxState state = stateMap.getOrDefault(spawnBoxId, TentacleBoxState.EMPTY);return predicate.test(state);
    }

    // ----- Factories -----

    /** Box must have NO tentacle at all (state == EMPTY). */
    public static TentacleCondition mustBeEmpty(String id) {
        return new TentacleCondition(id, state -> state == TentacleBoxState.EMPTY, "box " + id + " must be empty");
    }

     /**
     * Passes for EMPTY, MINI, or MEDIUM — fails only for BIG.
     * "It is safe here for a medium Hollow Wand user."
     */
    public static TentacleCondition maxSizeMedium(String id) {
        return new TentacleCondition(id, TentacleBoxState::isAtMostMedium, "box " + id + " must be <= medium (no big tentacle)");
    }

    public static TentacleCondition maxSizeMini(String id) {
        return new TentacleCondition(id, TentacleBoxState::isAtMostMini, "box " + id + " must be <= mini (no big & medium tentacle)");
    }

    /** Box must have ANY tentacle present. */
    public static TentacleCondition occupied(String id) {
        return new TentacleCondition(id, TentacleBoxState::isOccupied, "box " + id + " must be occupied");
    }

    /** Box must have a tentacle of at least {@code minimumSize}. */
    public static TentacleCondition minSize(TentacleBoxState minimumSize, String id) {
        return new TentacleCondition(id, state -> state.ordinal() >= minimumSize.ordinal(), "box " + id + " must be >= " + minimumSize.name().toLowerCase());
    }

    /** Box must have a tentacle of exactly {@code size}. */
    public static TentacleCondition exactSize(TentacleBoxState size, String id) {
        return new TentacleCondition(id, state -> state == size, "box " + id + " must be exactly " + size.name().toLowerCase());
    }
}