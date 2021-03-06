package com.kasirgalabs.etumulator.lang;

import com.kasirgalabs.etumulator.lang.Linker.ExecutableCode;
import com.kasirgalabs.etumulator.processor.Memory;
import com.kasirgalabs.thumb2.AssemblerLexer;
import com.kasirgalabs.thumb2.AssemblerParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Assembler is a class that takes the source code generated by the user and outputs an
 * {@link ExecutableCode} which can directly run by the processor.
 *
 * @author Görkem Mülayim,
 * @see ExecutableCode
 */
public class Assembler {
    private final Linker linker;
    private final Loader loader;

    /**
     * Constructs an Assembler object with the given {@link Memory}. Generated address space layout
     * and the data will be loaded in the memory.
     *
     * @param memory Memory for the allocated data section.
     */
    public Assembler(Memory memory) {
        linker = new Linker();
        loader = new Loader(memory);
    }

    /**
     * Assembles the given code in to an {@link ExecutableCode}.
     *
     * @param code T code to be assembled.
     *
     * @return The executable code.
     *
     * @throws SyntaxError If the code contains syntax error(s).
     * @throws LabelError  If an undefined label used or duplicate labels exist.
     * @see ExecutableCode
     *
     */
    public ExecutableCode assemble(String code) throws SyntaxError, LabelError {
        AssemblerLexer lexer = new AssemblerLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AssemblerParser parser = new AssemblerParser(tokens);
        parser.prog();
        if(parser.getNumberOfSyntaxErrors() > 0) {
            throw new SyntaxError("You have error(s) in your code.");
        }
        ConstantValidator.validate(code);
        ExecutableCode executableCode = linker.link(code);
        loader.load(executableCode);
        return executableCode;
    }
}
