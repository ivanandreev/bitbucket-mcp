package io.ivanandreev.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class HelloTools {

    @Tool(description = "Returns a friendly hello-world greeting, optionally addressed to a name")
    public String helloWorld(
            @ToolParam(description = "Name to greet (default: World)", required = false) String name
    ) {
        String who = (name == null || name.isBlank()) ? "World" : name.trim();
        return "Hello, %s!".formatted(who);
    }
}
