package com.kasirgalabs.etumulator.processor;

import com.kasirgalabs.etumulator.lang.Linker.ExecutableCode;
import com.kasirgalabs.etumulator.visitor.ArithmeticVisitor;
import com.kasirgalabs.etumulator.visitor.BitFieldVisitor;
import com.kasirgalabs.etumulator.visitor.BranchVisitor;
import com.kasirgalabs.etumulator.visitor.CompareVisitor;
import com.kasirgalabs.etumulator.visitor.LogicalVisitor;
import com.kasirgalabs.etumulator.visitor.MoveVisitor;
import com.kasirgalabs.etumulator.visitor.MultiplyAndDivideVisitor;
import com.kasirgalabs.etumulator.visitor.ReverseVisitor;
import com.kasirgalabs.etumulator.visitor.ShiftVisitor;
import com.kasirgalabs.etumulator.visitor.SingleDataMemoryVisitor;
import com.kasirgalabs.etumulator.visitor.StackVisitor;
import com.kasirgalabs.thumb2.ProcessorBaseVisitor;
import com.kasirgalabs.thumb2.ProcessorLexer;
import com.kasirgalabs.thumb2.ProcessorParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class BaseProcessor extends ProcessorBaseVisitor<Void> implements Processor {
    private final ArithmeticVisitor arithmeticVisitor;
    private final MultiplyAndDivideVisitor multiplyAndDivideVisitor;
    private final MoveVisitor moveVisitor;
    private final ShiftVisitor shiftVisitor;
    private final CompareVisitor compareVisitor;
    private final LogicalVisitor logicalVisitor;
    private final ReverseVisitor reverseVisitor;
    private final BranchVisitor branchVisitor;
    private final SingleDataMemoryVisitor singleDataMemoryVisitor;
    private final StackVisitor stackVisitor;
    private final PC pc;
    private final BitFieldVisitor bitFieldVisitor;

    public BaseProcessor(ProcessorUnits processorUnits) {
        arithmeticVisitor = new ArithmeticVisitor(processorUnits.getRegisterFile(),
                processorUnits.getAPSR());
        multiplyAndDivideVisitor = new MultiplyAndDivideVisitor(processorUnits.getRegisterFile(),
                processorUnits.getAPSR());
        moveVisitor = new MoveVisitor(processorUnits.getRegisterFile(), processorUnits.getAPSR());
        shiftVisitor = new ShiftVisitor(processorUnits.getRegisterFile(), processorUnits.getAPSR());
        compareVisitor = new CompareVisitor(processorUnits.getRegisterFile(),
                processorUnits.getAPSR());
        logicalVisitor = new LogicalVisitor(processorUnits.getRegisterFile(),
                processorUnits.getAPSR());
        reverseVisitor = new ReverseVisitor(processorUnits.getRegisterFile());
        branchVisitor = new BranchVisitor(processorUnits.getAPSR(), processorUnits.getUART(),
                processorUnits.getPC(), processorUnits.getLR());
        singleDataMemoryVisitor = new SingleDataMemoryVisitor(processorUnits.getRegisterFile(),
                processorUnits.getMemory());
        stackVisitor = new StackVisitor(processorUnits.getRegisterFile(), processorUnits.getPC(),
                processorUnits.getLR(), processorUnits.getStack()
        );
        pc = processorUnits.getPC();
        bitFieldVisitor=new BitFieldVisitor(processorUnits.getRegisterFile());
    }

    @Override
    public Void visitArithmetic(ProcessorParser.ArithmeticContext ctx) {
        return arithmeticVisitor.visit(ctx);
    }

    @Override
    public Void visitMultiplyAndDivide(ProcessorParser.MultiplyAndDivideContext ctx) {
        return multiplyAndDivideVisitor.visit(ctx);
    }

    @Override
    public Void visitMove(ProcessorParser.MoveContext ctx) {
        return moveVisitor.visit(ctx);
    }

    @Override
    public Void visitShift(ProcessorParser.ShiftContext ctx) {
        return shiftVisitor.visit(ctx);
    }

    @Override
    public Void visitCompare(ProcessorParser.CompareContext ctx) {
        return compareVisitor.visit(ctx);
    }

    @Override
    public Void visitLogical(ProcessorParser.LogicalContext ctx) {
        return logicalVisitor.visit(ctx);
    }

    @Override
    public Void visitReverse(ProcessorParser.ReverseContext ctx) {
        return reverseVisitor.visit(ctx);
    }

    @Override
    public Void visitBranch(ProcessorParser.BranchContext ctx) {
        return branchVisitor.visit(ctx);
    }

    @Override
    public Void visitSingleDataMemory(ProcessorParser.SingleDataMemoryContext ctx) {
        return singleDataMemoryVisitor.visit(ctx);
    }

    @Override
    public Void visitBitfield(ProcessorParser.BitfieldContext ctx){ return bitFieldVisitor.visit(ctx); }

    @Override
    public Void visitStack(ProcessorParser.StackContext ctx) {
        return stackVisitor.visit(ctx);
    }

    @Override
    public void run(ExecutableCode executableCode) {
        pc.setValue(0);
        final String[] instructions = executableCode.getCode();
        Breakpoint point = Breakpoint.getInstance();
        while(pc.getValue() < instructions.length && (point.getPoint() != pc.getValue())) {
            if(pc.getValue() < 0) {
                throw new IllegalPCException("PC can not be negative.");
            }
            String instruction = instructions[pc.getValue()];
            execute(instruction);
            pc.increment();
        }
    }

    private void execute(String instruction) {
        ProcessorLexer lexer = new ProcessorLexer(CharStreams.fromString(instruction));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProcessorParser parser = new ProcessorParser(tokens);
        ProcessorParser.ProgContext tree = parser.prog();
        this.visit(tree);
    }
}
