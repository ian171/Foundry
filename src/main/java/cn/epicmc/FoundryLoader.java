package cn.epicmc;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

/**
 * Foundry Plugin Loader
 * Handles dependency loading before plugin initialization
 */
@SuppressWarnings("UnstableApiUsage")
public class FoundryLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // Add Maven Central repository
        resolver.addRepository(new RemoteRepository.Builder(
            "central",
            "default",
            "https://repo1.maven.org/maven2/"
        ).build());

        // Add SQLite JDBC driver
        resolver.addDependency(new Dependency(
            new DefaultArtifact("org.xerial:sqlite-jdbc:3.46.1.3"),
            null
        ));

        classpathBuilder.addLibrary(resolver);
    }
}
