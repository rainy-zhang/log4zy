package org.rainy.log4zy.processor;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import org.rainy.log4zy.Log4zy;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * <p>日志侵入处理器 </br>
 * 对使用了{@link Log4zy}注解的类，在代码开始处添加以下代码：
 * <pre>
 * {@code
 * @Autowired
 * private Logger logger;
 * }
 * </pre>
 * </p>
 */
@SupportedAnnotationTypes("org.rainy.log4zy.Log4zy")    // 要监听的注解
@SupportedSourceVersion(SourceVersion.RELEASE_8)    // 要对什么版本的源代码做处理
public class LoggerProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final String logPack = "org.rainy.log4zy";
        final String logType = "Logger";
        final String logSymbol = "logger";
        final String autowiredPack = "org.springframework.beans.factory.annotation";
        final String autowiredType = "Autowired";
        
        // 拿到所有被注解标记的类
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Log4zy.class);
        messager.printMessage(Diagnostic.Kind.NOTE, "拿到所有被注解标记的类");
        
        for (Element element : elements) {
            JCTree.JCFieldAccess logTree = treeMaker.Select(
                    treeMaker.Ident(names.fromString(logPack)),
                    names.fromString(logType)
            );
            JCTree.JCImport logImport = treeMaker.Import(logTree, false);

            JCTree.JCFieldAccess autowiredTree = treeMaker.Select(
                    treeMaker.Ident(names.fromString(autowiredPack)),
                    names.fromString(autowiredType)
            );
            JCTree.JCImport autowiredImport = treeMaker.Import(autowiredTree, false);

            TreePath treePath = javacTrees.getPath(element);
            JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();


            messager.printMessage(Diagnostic.Kind.NOTE, "logImport: " + logImport.toString() + ", autowiredImport: " + autowiredImport.toString());

            boolean containsLog = false;
            boolean containsAutowired = false;
            // 判断是否已经引入了Logger和Autowired
            for (JCTree jcTree : compilationUnit.defs) {
                if (!(jcTree instanceof JCTree.JCImport)) {
                    continue;
                }
                messager.printMessage(Diagnostic.Kind.NOTE, "DEFS -> " + jcTree);

                if (jcTree.toString().equals(logImport.toString())) {
                    containsLog = true;
                }
                if (jcTree.toString().equals(autowiredImport.toString())) {
                    containsAutowired = true;
                }
            }
            // 如果已经引入了Logger和Autowired，则不进行注入
            if (!containsLog) {
                compilationUnit.defs = compilationUnit.defs.append(logImport);
            }
            if (!containsAutowired) {
                compilationUnit.defs = compilationUnit.defs.append(autowiredImport);
            }

            // 获取类的抽象树结构
            JCTree jcTree = javacTrees.getTree(element);
            // 遍历该类，对其进行修改
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    List<JCTree.JCAnnotation> annotationList = List.nil();

                    JCTree.JCAnnotation annotation = treeMaker.Annotation(treeMaker.Ident(names.fromString(autowiredType)), List.nil());
                    annotationList = annotationList.append(annotation);

                    JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PRIVATE, annotationList),
                            names.fromString(logSymbol),
                            treeMaker.Ident(names.fromString(logType)),
                            null
                    );
                    jcClassDecl.defs = jcClassDecl.defs.append(variableDecl);
                    super.visitClassDef(jcClassDecl);
                }
            });
        }
        return false;
    }
}
