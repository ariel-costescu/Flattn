package bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class Flattn {
    public static void main(String[] args) {
        if (args != null && args.length >= 1) {
            String pathString = args[0];
            Path folderPath = Path.of(pathString);
            File directory = folderPath.toFile();
            if (directory.isDirectory()) {
                try {
                    recursivelyProcess(directory.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void recursivelyProcess(Path folderPath) throws IOException {
        Files.walkFileTree(folderPath, new PathFileVisitor(folderPath));
    }

    private record PathFileVisitor(Path folderPath) implements FileVisitor<Path> {

        @Override
            public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                Path parentPath = filePath.getParent().toAbsolutePath();
                if (!folderPath.toAbsolutePath().equals(parentPath)) {
                    String hashedParentPath = Integer.toHexString(Objects.hash(parentPath));
                    Path destination = folderPath.resolve(hashedParentPath + "-" + filePath.getFileName());
                    Path movedPath = Files.move(filePath,
                            destination);
                    System.out.printf("Moved '%s' to '%s' \n", filePath, movedPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path filePath, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dirPath, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        }
}
