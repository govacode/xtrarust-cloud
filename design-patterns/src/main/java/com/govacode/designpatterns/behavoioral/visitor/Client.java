package com.govacode.designpatterns.behavoioral.visitor;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 访问者模式测试
 *
 * @author gova
 * @see FileVisitor
 * @see javax.lang.model.element.Element
 * @see javax.lang.model.element.ElementVisitor
 * @see javax.lang.model.element.AnnotationValue
 * @see javax.lang.model.element.AnnotationValueVisitor
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {
        Commander commander = new Commander(
                new Sergeant(new Soldier(), new Soldier(), new Soldier()),
                new Sergeant(new Soldier(), new Soldier(), new Soldier())
        );
        commander.accept(new SoldierVisitor());

        commander.accept(new SergeantVisitor());

        commander.accept(new CommanderVisitor());

        Files.walkFileTree(Paths.get("/Library/Java/JavaVirtualMachines/jdk-11.0.15.jdk/Contents/Home"), new SimpleFileVisitor<>() {

            private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.info("--> pre visit dir: {}", dir.getFileName());
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.info("pre visit file: {} creationTime: {}",
                        file.getFileName(),
                        attrs.creationTime().toInstant().atZone(ZoneId.systemDefault()).format(FORMATTER));
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return super.visitFileFailed(file, exc);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                log.info("<-- post visit dir: {}", dir.getFileName());
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}
