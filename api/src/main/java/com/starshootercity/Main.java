package com.starshootercity;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Main {

    static JavaParser javaParser;

    static {
        final ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

        javaParser = new JavaParser(parserConfiguration);
    }

    public static void main(String[] args) throws IOException {
        Path outputDir = Paths.get("Origins-Reborn API");  // Output directory

        for (Path path : Set.of(
                Path.of("core/src/main/java/com/starshootercity/"),
                Path.of("version/src/main/java/com/starshootercity/")
        )) {
            try (Stream<Path> pa = Files.walk(path)) {
                for (Path p : pa.toList()) {
                    if (!p.toString().endsWith(".java")) continue;

                    processFile(
                            p,
                            outputDir
                    );
                }
            }
        }
    }

    private static void processFile(Path javaFile, Path outputBase) {
        outputBase = Path.of(outputBase.toString(), javaFile.toString().replaceFirst("\\w+/", ""));

        try {
            Optional<CompilationUnit> optionalCompilationUnit = javaParser.parse(javaFile).getResult();
            if (optionalCompilationUnit.isEmpty()) return;
            CompilationUnit cu = optionalCompilationUnit.get();

            AtomicBoolean b = new AtomicBoolean(false);
            cu.findAll(AnnotationExpr.class).forEach(a -> {
                if (a.toString().equals("@APITarget")) {
                    b.set(true);
                    a.remove();
                }
            });

            cu.findAll(ImportDeclaration.class).forEach(i -> {
                if (i.toString().endsWith("APITarget;\n")) {
                    i.remove();
                }
            });

            if (!b.get()) return;
            boolean ignored = outputBase.getParent().toFile().mkdirs();

            // Remove method and constructor bodies
            cu.findAll(MethodDeclaration.class).forEach(m -> {
                if (m.isPrivate()) m.remove();
                else {
                    if (m.getBody().isPresent()) {
                        BlockStmt stmt = new BlockStmt();
                        if (!m.getType().toString().equals("void")) {
                            stmt.addStatement(new ReturnStmt(getValue(m.getType())));
                        }
                        m.setBody(stmt);
                    }
                }
            });

            cu.findAll(ConstructorDeclaration.class).forEach(c -> {
                if (c.isPrivate()) c.remove();
                else {
                    Statement s = c.getBody().getStatements().get(0);
                    if (s.toString().contains("super")) {
                        System.out.println(s);
                        System.out.println(s.getMetaModel());

                    }
                    c.setBody(new BlockStmt());
                }
            });

            cu.findAll(FieldDeclaration.class).forEach(field -> {
                if (field.isPrivate()) field.remove();
                else field.getVariables().forEach(var -> var.setInitializer("null"));
            });

            // Write the stripped file
            Files.write(outputBase, cu.toString().getBytes());
            System.out.println("Generated API stub: " + outputBase);

        } catch (IOException e) {
            System.err.println("Failed to process: " + javaFile + "\n" + e.getMessage());
        }
    }

    private static String getValue(Type type) {
        if (type.isPrimitiveType()) {
            return switch (type.toString()) {
                case "boolean" -> "false";
                case "char" -> "' '";
                case "int", "long", "double", "float", "byte", "short" -> "0";
                default -> "null";
            };
        } else return "null";
    }
}
