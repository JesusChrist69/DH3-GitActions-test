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
import eu.decentsoftware.holograms.hologram.line.content.objects.DecentItemStack;
import eu.decentsoftware.holograms.hologram.line.renderer.HeadLineRenderer;
import eu.decentsoftware.holograms.hologram.line.renderer.LineRenderer;
import eu.decentsoftware.holograms.hologram.line.renderer.SmallHeadLineRenderer;
import org.jetbrains.annotations.NotNull;

public class SmallHeadContentParser implements ContentParser {

    @Override
    public boolean parse(@NotNull HologramLine line) {
        String content = line.getContent();
        if (content == null || !content.startsWith("#SMALLHEAD:")) {
            return false;
        }

        DecentItemStack itemStack = DecentItemStack.fromString(content.substring("#SMALLHEAD:".length()));
        LineRenderer renderer = (LineRenderer) line.getRenderer();
        if (renderer instanceof HeadLineRenderer) {
            ((HeadLineRenderer) line.getRenderer()).setItemStack(itemStack);
            ((HeadLineRenderer) line.getRenderer()).setSmall(true);
            renderer.updateAll();
            return true;
        } else if (renderer != null) {
            renderer.hideAll();
        }

        renderer = new SmallHeadLineRenderer(line, itemStack);
        line.setRenderer(renderer);
        line.getPositionManager().getOffsets().setY(-1.1875d);
        line.getSettings().setHeight(0.6d);
        renderer.displayAll();
        return true;
    }

}
