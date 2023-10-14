package com.smartrader.events;
import java.util.List;
import java.util.ArrayList;

// 2. 创建一个事件触发器类
public class SymbolEventSource {
    private List<EventListener> listeners = new ArrayList<>();

    // 注册事件监听器
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    // 触发事件
    public void fireEvent(String message) {
        for (EventListener listener : listeners) {
            listener.onEvent(message);
        }
    }
}