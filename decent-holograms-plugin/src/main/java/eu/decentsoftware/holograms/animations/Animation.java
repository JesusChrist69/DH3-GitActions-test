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

package eu.decentsoftware.holograms.animations;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a general animation.
 *
 * @author d0by
 */
@Getter
public abstract class Animation {

    protected final @NotNull String name;
    protected final @NotNull AnimationType type;
    protected final int totalSteps;
    protected final int speed;
    protected final int pause;

    /**
     * Creates a new animation.
     *
     * @param name       the name of the animation
     * @param totalSteps the total number of steps of the animation
     */
    @Contract(pure = true)
    public Animation(@NotNull String name, @NotNull AnimationType type, int totalSteps, int speed, int pause) {
        this.name = name;
        this.type = type;
        this.totalSteps = type == AnimationType.ASCEND_DESCEND ? totalSteps * 2 : totalSteps;
        this.speed = speed;
        this.pause = pause;
    }

    /**
     * Animates the given string.
     *
     * @param tick   The current tick of animations. (1 tick = 1/20 second)
     * @param string The string.
     * @return The animated string.
     */
    @NotNull
    public abstract String animate(int tick, @Nullable String string);

    /**
     * Get the current step of this animation.
     *
     * @param tick The current tick of animations. (1 tick = 1/20 second)
     * @return The current step of this animation.
     */
    protected int getActualStep(int tick) {
        if (totalSteps <= 0) {
            return 0;
        }
        int actualStep = tick / speed;
        int actualPause = pause <= 0 ? 0 : pause / speed;
        int currentStep = actualStep % (totalSteps + actualPause);
        return Math.min(currentStep, totalSteps - 1);
    }

}