package com.dev9.mvnwatcher;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class DefaultConfigTest {

    @Test
    public void basicConfigTest() {

        String cwd = Paths.get("").toAbsolutePath().toString();


        Path projectPath = Paths.get(cwd, "target", "test-classes", "sample-project");

        List<Task> taskList = new WatcherMojo().getDefaultTasks(projectPath);

        assertThat(taskList.size()).isEqualTo(2);

        for (Task task : taskList) {
            System.out.println(task.getExecutableDirectory().toAbsolutePath().toString());

            assertThat(
                    Files.exists(task.getExecutableDirectory())
            ).isTrue();
            System.out.println(task.getOutputFile().toString());
            assertThat(task.getOutputFile().exists()).isTrue();
        }

    }
}
