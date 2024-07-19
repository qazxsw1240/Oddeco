package org.hansung.oddeco.core.entity.item.event.tool;

import org.hansung.oddeco.core.entity.item.Tool;

public interface ToolCreateListener extends ToolAttachableListener {
    public abstract void onToolCreate(Tool tool);
}
