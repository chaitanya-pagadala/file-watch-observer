package com.pagadala.filewatchobserver;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileWatchObserverApplication implements CommandLineRunner {

	@Value("${aed.publish.dir}")
	private String aedPublishDir;

	public static void main(String[] args) {
		SpringApplication.run(FileWatchObserverApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException {

		AedWatcher.watch(Paths.get(aedPublishDir)).filter(event -> event.kind() == StandardWatchEventKinds.ENTRY_CREATE).subscribe(event -> {
			System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
		});
	}

}
