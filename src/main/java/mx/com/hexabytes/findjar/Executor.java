package mx.com.hexabytes.findjar;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application's command executor.
 * @author rherrera
 */
public class Executor {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Executor.class);
    /**
     * Scans a jar file looking for the searched class.
     * @param jar the jar file to look in.
     * @param clazz the name class pattern to find.
     * @throws IOException if some IO exception occurs.
     */
    public static void scanJar(File jar, String clazz) throws IOException {
        String name;
        Enumeration<JarEntry> entries = new JarFile(jar).entries();
        LOG.debug("[+] {}", jar.toString());
        while(entries.hasMoreElements()) {
            name = entries.nextElement().getName();
            if (!name.endsWith(".class"))
                LOG.trace("[-] {}: not a class", name);
            else if (name.contains("$"))
                LOG.trace("[-] {}: inner class", name);
            else {
                name = name.replaceAll("/", ".");
                name = name.substring(0, name.indexOf(".class"));
                if (name.equals(clazz)) {
                    LOG.info("{} found at {}", name, jar.getAbsolutePath());
                }
            }
        }
    }

    private static void traverse(Deque<File> paths, String clazz) throws IOException {
        File path, content[];
        while(!paths.isEmpty()) {
            path = paths.removeLast();
            LOG.debug("Scanning: {}", path.toString());
            content = path.listFiles();
            for (File element : content) {
                if (element.isDirectory())
                    if (element.canExecute())
                        paths.addFirst(element);
                    else
                        LOG.debug("[-] {} cannot read", element.toString());
                else if (element.getName().endsWith("jar"))
                    scanJar(element, clazz);
                else
                    LOG.debug("[-] {}: not a jar", element.toString());
            }
        }
    }
    /**
     * Executes the command given from console.
     * @param command the command to execute.
     * @throws IOException if some IO exception occurs.
     */
    public static void execute(CommandLine command) throws IOException {
        Deque<File> paths;
        String dir = command.getOptionValue(Args.dir.name(), ".");
        File dirFile = new File(dir);
        dir = dirFile.getAbsolutePath();
        if (dirFile.exists())
            if (dirFile.isDirectory())
                if (dirFile.canExecute()) {
                    paths = new LinkedList<>();
                    paths.add(dirFile);
                    traverse(paths, command.getOptionValue(Args.file.name()));
                } else
                    LOG.info("Path: {} is not accessible.", dir);
            else
                LOG.info("Path: {} is not a directory.", dir);
        else
            LOG.info("Path {} does not exists.", dir);
    }

}