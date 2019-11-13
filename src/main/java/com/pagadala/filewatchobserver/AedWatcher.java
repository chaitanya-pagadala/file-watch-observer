package com.pagadala.filewatchobserver;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import io.reactivex.Observable;

public class AedWatcher {

    private AedWatcher() {
    }

    /**
     * Creates an observable that watches the given directory but not its
     * subdirectories.
     * 
     * @param path Root directory to be watched
     * @return Observable that emits an event for each filesystem event.
     * @throws IOException
     */
    public static Observable<WatchEvent<?>> watch(final Path path) throws IOException {
        return ObservableFactory.create(path);
    }

    private static class ObservableFactory {

        private ObservableFactory() {
        }

        private static Observable<WatchEvent<?>> create(Path directory) {
            return Observable.create(subscriber -> {

                try (WatchService watcher = directory.getFileSystem().newWatchService()) {
                    try {
                        directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    } catch (IOException exception) {
                        subscriber.onError(exception);
                    }
                    for (;;) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        } catch (InterruptedException exception) {
                            if (!subscriber.isDisposed()) {
                                subscriber.onError(exception);
                            }
                            return;
                        }
                        for (final WatchEvent<?> event : key.pollEvents()) {
                            subscriber.onNext(event);
                        }
                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                    subscriber.onComplete();
                }
            });
        }
    }
}