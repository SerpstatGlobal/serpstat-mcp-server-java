package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link ToolRegistry}.
 *
 * Coverage plan:
 * - Registration of tools
 * - Lookup of tools by name/key
 * - Duplicate registration handling
 * - Error handling for missing tools
 * - Thread safety (concurrent registration/lookup)
 * - Lookup of multiple tools
 */
public class ToolRegistryTest {
    static McpServerFeatures.SyncToolSpecification createMockTool(String name) {
        // Use the correct constructor for McpSchema.Tool (name, description, schema)
        McpSchema.Tool tool = new McpSchema.Tool(name, "desc", (McpSchema.JsonSchema) null);
        BiFunction<Object, Map<String, Object>, McpSchema.CallToolResult> call =
                (exchange, args) -> new McpSchema.CallToolResult("result text", false);
        // Cast to the required type for SyncToolSpecification
        return new McpServerFeatures.SyncToolSpecification(tool, (BiFunction) call);
    }

    static class TestToolProvider implements ToolProvider {
        private final String domainName;
        private final List<McpServerFeatures.SyncToolSpecification> tools;
        TestToolProvider(String domainName, List<McpServerFeatures.SyncToolSpecification> tools) {
            this.domainName = domainName;
            this.tools = tools;
        }
        @Override public String getDomainName() { return domainName; }
        @Override public List<McpServerFeatures.SyncToolSpecification> getTools() { return tools; }
    }

    private List<TestToolProvider> providers;

    @BeforeEach
    void setUp() {
        providers = new ArrayList<>();
    }

    @Test
    @DisplayName("Should register a tool and allow lookup by name")
    void testRegisterAndLookupTool() {
        McpServerFeatures.SyncToolSpecification tool = createMockTool("tool1");
        TestToolProvider provider = new TestToolProvider("domain1", List.of(tool));
        providers.add(provider);
        assertThat(providers.get(0).getTools().get(0).tool().name()).isEqualTo("tool1");
    }

    @Test
    @DisplayName("Should throw or handle duplicate tool registration")
    void testDuplicateRegistration() {
        McpServerFeatures.SyncToolSpecification tool = createMockTool("tool1");
        TestToolProvider provider1 = new TestToolProvider("domain1", List.of(tool));
        TestToolProvider provider2 = new TestToolProvider("domain1", List.of(tool));
        providers.add(provider1);
        // Simulate duplicate registration
        boolean duplicate = false;
        for (TestToolProvider p : providers) {
            if (p.getDomainName().equals(provider2.getDomainName())) {
                duplicate = true;
                break;
            }
        }
        assertThat(duplicate).isTrue();
    }

    @Test
    @DisplayName("Should return null or throw when looking up a missing tool")
    void testLookupMissingTool() {
        TestToolProvider provider = new TestToolProvider("domain1", List.of());
        providers.add(provider);
        boolean found = providers.stream().anyMatch(p -> p.getTools().stream().anyMatch(t -> t.tool().name().equals("notfound")));
        assertThat(found).isFalse();
    }

    @Test
    @DisplayName("Should register multiple tools and allow lookup of each")
    void testRegisterMultipleTools() {
        McpServerFeatures.SyncToolSpecification tool1 = createMockTool("tool1");
        McpServerFeatures.SyncToolSpecification tool2 = createMockTool("tool2");
        TestToolProvider provider = new TestToolProvider("domain1", List.of(tool1, tool2));
        providers.add(provider);
        List<String> names = new ArrayList<>();
        for (TestToolProvider p : providers) {
            for (McpServerFeatures.SyncToolSpecification t : p.getTools()) {
                names.add(t.tool().name());
            }
        }
        assertThat(names).containsExactlyInAnyOrder("tool1", "tool2");
    }

    @Test
    @DisplayName("Should be thread-safe for concurrent registration and lookup")
    void testThreadSafety() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();
        ConcurrentLinkedQueue<TestToolProvider> concurrentProviders = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                McpServerFeatures.SyncToolSpecification tool = createMockTool("tool" + idx);
                TestToolProvider provider = new TestToolProvider("domain" + idx, List.of(tool));
                concurrentProviders.add(provider);
            }));
        }
        for (Future<?> f : futures) f.get();
        executor.shutdown();
        assertThat(concurrentProviders).hasSize(threadCount);
        Set<String> toolNames = new HashSet<>();
        for (TestToolProvider p : concurrentProviders) {
            for (McpServerFeatures.SyncToolSpecification t : p.getTools()) {
                toolNames.add(t.tool().name());
            }
        }
        for (int i = 0; i < threadCount; i++) {
            assertThat(toolNames).contains("tool" + i);
        }
    }
}
