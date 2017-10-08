package io;

import com.google.inject.Inject;

import java.nio.file.Path;

/**
 * Created by Mikaël on 2017-10-07.
 */
public interface TextonIoFactory {
    @Inject
    TextonIo create(Path path);
}
