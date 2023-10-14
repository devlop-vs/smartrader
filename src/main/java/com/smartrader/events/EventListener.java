package com.smartrader.events;

// 1. 创建一个事件监听器接口
public interface EventListener {
    void onEvent(String message);
}
