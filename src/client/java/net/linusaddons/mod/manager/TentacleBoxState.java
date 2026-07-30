package net.linusaddons.mod.manager;

    public enum TentacleBoxState {
        EMPTY,   // no tentacle in this box
        MINI,    // mini tentacle  (MagmaCube size 13, y ≈ 67)
        MEDIUM,  // medium tentacle (MagmaCube size 16, y ≈ 65)
        BIG;     // big tentacle   (MagmaCube size 20, y ≈ 59)

        /** True for EMPTY, MINI, or MEDIUM — i.e. the box does not contain a big tentacle. */
        public boolean isAtMostMedium() {
            return this != BIG;
        }

        public boolean isAtMostMini() {
            return (this != BIG && this != MEDIUM);
        }

        /** True when any tentacle is present. */
        public boolean isOccupied() {
            return this != EMPTY;
        }
    }