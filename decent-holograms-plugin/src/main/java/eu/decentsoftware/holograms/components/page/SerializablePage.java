package eu.decentsoftware.holograms.components.page;

import eu.decentsoftware.holograms.actions.DefaultActionHolder;
import eu.decentsoftware.holograms.api.component.line.Line;
import eu.decentsoftware.holograms.api.conditions.ConditionHolder;
import eu.decentsoftware.holograms.api.utils.collection.DList;
import eu.decentsoftware.holograms.components.hologram.DefaultHologram;
import eu.decentsoftware.holograms.components.line.DefaultLine;
import eu.decentsoftware.holograms.components.line.SerializableLine;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SerializablePage {

    private final @NotNull DList<SerializableLine> lines;
    private final @NotNull ConditionHolder clickConditions;
    private final @NotNull DefaultActionHolder clickActions;

    @NotNull
    public static SerializablePage fromPage(@NotNull DefaultPage page) {
        DList<SerializableLine> lines = new DList<>();
        for (Line line : page.getLineHolder().getLines()) {
            DefaultLine defaultLine = (DefaultLine) line;
            SerializableLine serializableLine = SerializableLine.fromLine(defaultLine);
            lines.add(serializableLine);
        }
        return new SerializablePage(
                lines,
                page.getClickConditionHolder(),
                (DefaultActionHolder) page.getClickActionHolder()
        );
    }

    @NotNull
    public DefaultPage toPage(@NotNull DefaultHologram hologram) {
        DefaultPage page = new DefaultPage(hologram, clickConditions, clickActions);
        DList<Line> lines = new DList<>();
        for (SerializableLine line : this.lines) {
            DefaultLine defaultLine = line.toLine(page);
            lines.add(defaultLine);
        }
        page.getLineHolder().setLines(lines);
        return page;
    }

}
