import org.jetbrains.kotlin.com.intellij.openapi.Disposable;
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.config.CommonConfigurationKeys;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.KtPsiFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Prueft nur die Grammatik jeder .kt-Datei ueber den Kotlin-Parser.
 * Keine Typpruefung, keine Aufloesung von Referenzen - dafuer fehlen in
 * dieser Umgebung die AndroidX-Artefakte (Google Maven ist gesperrt).
 */
public class SyntaxCheck {
    public static void main(String[] args) throws IOException {
        Disposable disposable = Disposer.newDisposable();
        try {
            CompilerConfiguration cfg = new CompilerConfiguration();
            cfg.put(CommonConfigurationKeys.MODULE_NAME, "syntaxcheck");
            cfg.put(org.jetbrains.kotlin.cli.common.CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                    MessageCollector.Companion.getNONE());
            KotlinCoreEnvironment env = KotlinCoreEnvironment.createForProduction(
                    disposable, cfg, EnvironmentConfigFiles.JVM_CONFIG_FILES);
            KtPsiFactory factory = new KtPsiFactory(env.getProject(), false);

            List<Path> files = new ArrayList<>();
            for (String root : args) {
                try (DirectoryStream<Path> ignored = Files.newDirectoryStream(Paths.get(root))) {
                    // nur zum Pruefen, ob es ein Verzeichnis ist
                }
                Files.walk(Paths.get(root))
                        .filter(p -> p.toString().endsWith(".kt"))
                        .sorted()
                        .forEach(files::add);
            }

            int fehler = 0;
            for (Path file : files) {
                String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                PsiFile psi = factory.createFile(file.getFileName().toString(), text);
                List<String> probleme = new ArrayList<>();
                psi.accept(new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof PsiErrorElement) {
                            PsiErrorElement e = (PsiErrorElement) element;
                            int offset = e.getTextOffset();
                            int zeile = 1;
                            for (int i = 0; i < offset && i < text.length(); i++) {
                                if (text.charAt(i) == '\n') zeile++;
                            }
                            probleme.add("  Zeile " + zeile + ": " + e.getErrorDescription());
                        }
                        super.visitElement(element);
                    }
                });
                if (!probleme.isEmpty()) {
                    fehler += probleme.size();
                    System.out.println("FEHLER " + file);
                    probleme.forEach(System.out::println);
                }
            }
            System.out.println("---");
            System.out.println(files.size() + " Dateien geprueft, " + fehler + " Syntaxfehler.");
            if (fehler > 0) System.exit(1);
        } finally {
            Disposer.dispose(disposable);
        }
    }
}
