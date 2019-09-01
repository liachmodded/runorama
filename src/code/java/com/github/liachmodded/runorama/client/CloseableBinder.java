package com.github.liachmodded.runorama.client;

public interface CloseableBinder extends AutoCloseable {

    void bind(int face);

    void close();
}
