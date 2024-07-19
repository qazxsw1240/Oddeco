package org.hansung.oddeco.core.entity.item.event.scroll;

import org.hansung.oddeco.core.entity.item.Scroll;

public interface ScrollCreateListener extends ScrollAttachableListener {
    public abstract void onScrollCreate(Scroll scroll);
}
