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

package eu.decentsoftware.holograms.hologram.line.content.parsers;

import eu.decentsoftware.holograms.api.hologram.line.HologramLine;
import eu.decentsoftware.holograms.hologram.line.renderer.LineRenderer;
import eu.decentsoftware.holograms.hologram.line.renderer.TextLineRenderer;
import org.jetbrains.annotations.NotNull;

public class TextContentParser implements ContentParser {

    @Override
    public boolean parse(@NotNull HologramLine line) {
        String content = line.getContent();
        if (content == null) {
            content = "";
        }

        LineRenderer renderer = (LineRenderer) line.getRenderer();
        if (renderer instanceof TextLineRenderer) {
            ((TextLineRenderer) line.getRenderer()).setText(content);
            renderer.updateAll();
            return true;
        } else if (renderer != null) {
            renderer.hideAll();
            line.getSettings().setHeight(0.3d);
        }

        renderer = new TextLineRenderer(line, content);
        line.setRenderer(renderer);
        line.getPositionManager().getOffsets().setY(-0.5d);
        renderer.displayAll();
        return true;
    }

}
