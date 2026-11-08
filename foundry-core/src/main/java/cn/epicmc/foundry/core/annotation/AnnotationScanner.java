package cn.epicmc.foundry.core.annotation;

import cn.epicmc.foundry.api.annotations.Foundry;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Scans classpath for classes with Foundry annotations.
 */
public class AnnotationScanner {

    private final Logger logger;

    public AnnotationScanner(Logger logger) {
        this.logger = logger;
    }

    /**
     * Scan a plugin's classes for @Foundry annotated types.
     *
     * @param plugin The plugin to scan
     * @return Set of annotated classes
     */
    public Set<Class<?>> scanPlugin(Plugin plugin) {
        Set<Class<?>> annotatedClasses = new HashSet<>();

        try {
            // Get plugin jar file
            File jarFile = getPluginJarFile(plugin);
            if (jarFile == null) {
                logger.warning("Could not find jar file for plugin: " + plugin.getName());
                return annotatedClasses;
            }

            // Scan jar for classes
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    // Filter for class files
                    if (name.endsWith(".class")) {
                        String className = name.replace('/', '.')
                                              .substring(0, name.length() - 6);

                        try {
                            Class<?> clazz = Class.forName(
                                className,
                                false,
                                plugin.getClass().getClassLoader()
                            );

                            // Check for @Foundry annotation
                            if (clazz.isAnnotationPresent(Foundry.class)) {
                                annotatedClasses.add(clazz);
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            // Skip classes that can't be loaded
                            logger.finest("Could not load class: " + className);
                        }
                    }
                }
            }

            logger.info("Found " + annotatedClasses.size() +
                       " @Foundry annotated classes in " + plugin.getName());

        } catch (IOException e) {
            logger.warning("Error scanning plugin " + plugin.getName() + ": " + e.getMessage());
        }

        return annotatedClasses;
    }

    /**
     * Get the jar file for a plugin.
     */
    private File getPluginJarFile(Plugin plugin) {
        try {
            URL url = plugin.getClass().getProtectionDomain()
                           .getCodeSource()
                           .getLocation();
            return new File(url.toURI());
        } catch (Exception e) {
            return null;
        }
    }
}
