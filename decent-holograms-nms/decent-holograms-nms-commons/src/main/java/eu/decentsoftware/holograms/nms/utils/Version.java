/*
 * DecentHolograms
 * Copyright (C) DecentSoftware.eu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.decentsoftware.holograms.nms.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

/**
 * Enum of supported NMS versions.
 *
 * @author d0by
 */
public enum Version {
    v1_8_R3(8),
    v1_9_R1(9),
    v1_9_R2(9),
    v1_10_R1(10),
    v1_11_R1(11),
    v1_12_R1(12),
    v1_13_R1(13),
    v1_13_R2(13),
    v1_14_R1(14),
    v1_15_R1(15),
    v1_16_R1(16),
    v1_16_R2(16),
    v1_16_R3(16),
    v1_17_R1(17),
    v1_18_R1(18),
    v1_18_R2(18),
    v1_19_R1(19),
    v1_19_R2(19),
    ;

    /*
     *  Static
     */

    public static final Version CURRENT;

    static {
        CURRENT = Version.fromString(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
    }

    /**
     * Parse a Version from string.
     *
     * @param version The string.
     * @return The parsed Version or null.
     */
    @Nullable
    public static Version fromString(String version) {
        if (version == null) {
            return null;
        }

        for (Version value : Version.values()) {
            if (value.name().equalsIgnoreCase(version)) {
                return value;
            }
        }
        return null;
    }

    public static boolean after(int minor) {
        return CURRENT.getMinor() > minor;
    }

    public static boolean afterOrEqual(int minor) {
        return CURRENT.getMinor() >= minor;
    }

    public static boolean is(int minor) {
        return CURRENT.getMinor() == minor;
    }

    public static boolean before(int minor) {
        return CURRENT.getMinor() < minor;
    }

    public static boolean beforeOrEqual(int minor) {
        return CURRENT.getMinor() <= minor;
    }

    public static boolean supportsHex() {
        return afterOrEqual(16);
    }

    /*
     *  Version
     */

    private final int minor;

    Version(int minor) {
        this.minor = minor;
    }

    public int getMinor() {
        return minor;
    }

}
