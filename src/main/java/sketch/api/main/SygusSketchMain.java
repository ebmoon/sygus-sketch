package sketch.api.main;

import sketch.api.sygus.lang.Output;
import sketch.api.sygus.lang.SygusProblem;
import sketch.api.sygus.lang.SynthFunction;
import sketch.api.sygus.lang.expr.*;
import sketch.api.sygus.lang.grammar.*;
import sketch.api.sygus.lang.type.TypePrimitive;
import sketch.api.sygus.solvers.ProblemSolver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SygusSketchMain {
    public static void main(String[] args) {
        SygusProblem prob = sem_synth_2();
        ProblemSolver solver = new ProblemSolver(prob);
        solver.setInlineAmnt(3);
        solver.setQuantifiedBits(10);
        Output output = solver.solve();

        if (output.isRealizable()) {
            for (Map.Entry entry : output.getSolutions().entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue().toString());
            }
        } else {
            System.out.println("Failed to solve problem");
        }
    }

    public static SygusProblem max2() {
        SygusProblem prob = SygusProblem.emptyProblem();

        List<Variable> vars = new ArrayList<Variable>();
        Variable x = new Variable("x", TypePrimitive.intType);
        Variable y = new Variable("y", TypePrimitive.intType);
        vars.add(x);
        vars.add(y);

        Nonterminal nonE = new Nonterminal("E", TypePrimitive.intType);
        Nonterminal nonB = new Nonterminal("B", TypePrimitive.boolType);
        RHSNonterminal rhsNonE = new RHSNonterminal(nonE);
        RHSNonterminal rhsNonB = new RHSNonterminal(nonB);

        List<RHSTerm> rhsE = new ArrayList<RHSTerm>();
        rhsE.add(new RHSVariable("x", TypePrimitive.intType));
        rhsE.add(new RHSVariable("y", TypePrimitive.intType));
        rhsE.add(new RHSIfThenElse(rhsNonB, rhsNonE, rhsNonE));

        List<RHSTerm> rhsB = new ArrayList<RHSTerm>();
        rhsB.add(new RHSConstBool(true));
        rhsB.add(new RHSConstBool(false));
        rhsB.add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_LT, rhsNonE, rhsNonE));

        Production prodE = new Production(nonE, rhsE);
        Production prodB = new Production(nonB, rhsB);

        Grammar g = Grammar.empty();
        g.addRule(prodE);
        g.addRule(prodB);

        SynthFunction f = new SynthFunction("f", vars, TypePrimitive.intType, g);
        prob.addTargetFunction(f);

        SygusExpression constraint1 = new ExprBinaryOp(
                ExprBinaryOp.BinaryOp.BINOP_EQ,
                new ExprFunctionCall("f",
                        Arrays.asList(new ConstInt(3), new ConstInt(5))
                ),
                new ConstInt(5)
        );

        SygusExpression constraint2 = new ExprBinaryOp(
                ExprBinaryOp.BinaryOp.BINOP_EQ,
                new ExprFunctionCall("f",
                        Arrays.asList(new ConstInt(4), new ConstInt(1))
                ),
                new ConstInt(4)
        );

        prob.addConstraint(constraint1);
        prob.addConstraint(constraint2);

        return prob;
    }

     public static SygusProblem sem_synth() {
        SygusProblem prob = SygusProblem.emptyProblem();

        List<Variable> vars = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            Variable xi = new Variable("x" + i, TypePrimitive.intType);
            vars.add(xi);
            prob.addVariable(xi);
        }
        for (int i = 0; i <= 4; i++) {
            Variable yi = new Variable("y" + i, TypePrimitive.intType);
            vars.add(yi);
            prob.addVariable(yi);
        }

        Nonterminal nonStart = new Nonterminal("Start", TypePrimitive.boolType);
        Nonterminal nonA = new Nonterminal("A", TypePrimitive.intType);
        Nonterminal nonB = new Nonterminal("B", TypePrimitive.intType);
        Nonterminal nonC = new Nonterminal("C", TypePrimitive.intType);
        Nonterminal nonD = new Nonterminal("D", TypePrimitive.intType);
        Nonterminal nonE = new Nonterminal("E", TypePrimitive.intType);

        List<RHSTerm> rhsStart = new ArrayList<RHSTerm>() {{
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                            new RHSVariable("x4", TypePrimitive.intType), new RHSNonterminal(nonA)
                    ),
                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                    new RHSVariable("x5", TypePrimitive.intType), new RHSNonterminal(nonB)
                            ),
                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                            new RHSVariable("x6", TypePrimitive.intType), new RHSNonterminal(nonC)
                                    ),
                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                    new RHSVariable("x7", TypePrimitive.intType), new RHSNonterminal(nonD)
                                            ),
                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                            new RHSVariable("x7", TypePrimitive.intType), new RHSNonterminal(nonD)
                                                    ),
                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                            new RHSVariable("y0", TypePrimitive.intType), new RHSNonterminal(nonE)
                                                    )
                                            )
                                    )
                            )
                    )
            ));
        }};
        List<RHSTerm> rhsA = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x0", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsB = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x1", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsC = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x2", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsD = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x3", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsE = new ArrayList<RHSTerm>() {{
            for (int i = 0; i <= 3; i++) {
                add(new RHSVariable("x" + i, TypePrimitive.intType));
                add(new RHSVariable("y" + (i + 1), TypePrimitive.intType));
            }
            add(new RHSConstInt(0));
            add(new RHSConstInt(1));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_ADD, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_SUB, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_MUL, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
        }};


        Grammar g = Grammar.empty();
        g.addRule(new Production(nonStart, rhsStart));
        g.addRule(new Production(nonA, rhsA));
        g.addRule(new Production(nonB, rhsB));
        g.addRule(new Production(nonC, rhsC));
        g.addRule(new Production(nonD, rhsD));
        g.addRule(new Production(nonE, rhsE));

        SynthFunction f = new SynthFunction("phi", vars, TypePrimitive.boolType, g);
        prob.addTargetFunction(f);

        SygusExpression constraint1 = new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprUnaryOp(ExprUnaryOp.UnaryOp.UNOP_NOT, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x0", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x1", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x2", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x3", TypePrimitive.intType), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", TypePrimitive.intType), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", TypePrimitive.intType), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", TypePrimitive.intType), new ConstInt(-6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", TypePrimitive.intType), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(21)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(-41)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(30)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(-1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(25)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(27)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(34)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1)))))))))))))))))),new ExprFunctionCall("phi", Arrays.asList(new Variable("x0", new TypePrimitive("Int")),new Variable("x1", new TypePrimitive("Int")),new Variable("x2", new TypePrimitive("Int")),new Variable("x3", new TypePrimitive("Int")),new Variable("x4", new TypePrimitive("Int")),new Variable("x5", new TypePrimitive("Int")),new Variable("x6", new TypePrimitive("Int")),new Variable("x7", new TypePrimitive("Int")),new Variable("y0", new TypePrimitive("Int")),new Variable("y1", new TypePrimitive("Int")),new Variable("y2", new TypePrimitive("Int")),new Variable("y3", new TypePrimitive("Int")),new Variable("y4", new TypePrimitive("Int")))))))))), new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y0", new TypePrimitive("Int")), new ConstInt(32)));
        SygusExpression constraint2 = new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprUnaryOp(ExprUnaryOp.UnaryOp.UNOP_NOT, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x0", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x1", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x2", TypePrimitive.intType), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x3", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", TypePrimitive.intType), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", TypePrimitive.intType), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", TypePrimitive.intType), new ConstInt(-6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", TypePrimitive.intType), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(21)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(-41)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(30)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(-1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(25)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(27)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(34)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(1)))))))))))))))))),new ExprFunctionCall("phi", Arrays.asList(new Variable("x0", new TypePrimitive("Int")),new Variable("x1", new TypePrimitive("Int")),new Variable("x2", new TypePrimitive("Int")),new Variable("x3", new TypePrimitive("Int")),new Variable("x4", new TypePrimitive("Int")),new Variable("x5", new TypePrimitive("Int")),new Variable("x6", new TypePrimitive("Int")),new Variable("x7", new TypePrimitive("Int")),new Variable("y0", new TypePrimitive("Int")),new Variable("y1", new TypePrimitive("Int")),new Variable("y2", new TypePrimitive("Int")),new Variable("y3", new TypePrimitive("Int")),new Variable("y4", new TypePrimitive("Int")))))))))), new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y0", new TypePrimitive("Int")), new ConstInt(-103)));
        SygusExpression constraint3 = new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprUnaryOp(ExprUnaryOp.UnaryOp.UNOP_NOT, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x0", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x1", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x2", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x3", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", TypePrimitive.intType), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", TypePrimitive.intType), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", TypePrimitive.intType), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", TypePrimitive.intType), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", TypePrimitive.intType), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", TypePrimitive.intType), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", TypePrimitive.intType), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", TypePrimitive.intType), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(2)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(6)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(7)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4))))))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(3)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(1)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(0)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(10)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y3", new TypePrimitive("Int")), new ConstInt(8)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y4", new TypePrimitive("Int")), new ConstInt(4)))))))))))))))))),new ExprFunctionCall("phi", Arrays.asList(new Variable("x0", new TypePrimitive("Int")),new Variable("x1", new TypePrimitive("Int")),new Variable("x2", new TypePrimitive("Int")),new Variable("x3", new TypePrimitive("Int")),new Variable("x4", new TypePrimitive("Int")),new Variable("x5", new TypePrimitive("Int")),new Variable("x6", new TypePrimitive("Int")),new Variable("x7", new TypePrimitive("Int")),new Variable("y0", new TypePrimitive("Int")),new Variable("y1", new TypePrimitive("Int")),new Variable("y2", new TypePrimitive("Int")),new Variable("y3", new TypePrimitive("Int")),new Variable("y4", new TypePrimitive("Int")))))))))), new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y0", new TypePrimitive("Int")), new ConstInt(0)));

        prob.addConstraint(constraint1);
        prob.addConstraint(constraint2);
        prob.addConstraint(constraint3);

        return prob;
    }

    public static SygusProblem sem_synth_2() {
        SygusProblem prob = SygusProblem.emptyProblem();

        List<Variable> vars = new ArrayList<>();
        for (int i = 0; i <= 11; i++) {
            Variable xi = new Variable("x" + i, TypePrimitive.intType);
            vars.add(xi);
            prob.addVariable(xi);
        }
        for (int i = 0; i <= 2; i++) {
            Variable yi = new Variable("y" + i, TypePrimitive.intType);
            vars.add(yi);
            prob.addVariable(yi);
        }

        Nonterminal nonStart = new Nonterminal("Start", TypePrimitive.boolType);
        Nonterminal nonA = new Nonterminal("A", TypePrimitive.intType);
        Nonterminal nonB = new Nonterminal("B", TypePrimitive.intType);
        Nonterminal nonC = new Nonterminal("C", TypePrimitive.intType);
        Nonterminal nonD = new Nonterminal("D", TypePrimitive.intType);
        Nonterminal nonE = new Nonterminal("E", TypePrimitive.intType);
        Nonterminal nonF = new Nonterminal("F", TypePrimitive.intType);
        Nonterminal nonG = new Nonterminal("G", TypePrimitive.intType);
        Nonterminal nonH = new Nonterminal("H", TypePrimitive.intType);
        Nonterminal nonI = new Nonterminal("I", TypePrimitive.intType);

        List<RHSTerm> rhsStart = new ArrayList<RHSTerm>() {{
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                            new RHSVariable("x4", TypePrimitive.intType), new RHSNonterminal(nonA)
                    ),
                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                    new RHSVariable("x5", TypePrimitive.intType), new RHSNonterminal(nonB)
                            ),
                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                            new RHSVariable("x6", TypePrimitive.intType), new RHSNonterminal(nonC)
                                    ),
                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                    new RHSVariable("x7", TypePrimitive.intType), new RHSNonterminal(nonD)
                                            ),
                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                            new RHSVariable("x7", TypePrimitive.intType), new RHSNonterminal(nonD)
                                                    ),
                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                                    new RHSVariable("x8", TypePrimitive.intType), new RHSNonterminal(nonE)
                                                            ),
                                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                                            new RHSVariable("x9", TypePrimitive.intType), new RHSNonterminal(nonF)
                                                                    ),

                                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                                                    new RHSVariable("x10", TypePrimitive.intType), new RHSNonterminal(nonG)
                                                                            ),
                                                                            new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND,
                                                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                                                            new RHSVariable("x11", TypePrimitive.intType), new RHSNonterminal(nonH)
                                                                                    ),
                                                                                    new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ,
                                                                                            new RHSVariable("y0", TypePrimitive.intType), new RHSNonterminal(nonI)
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )));
        }};
        List<RHSTerm> rhsA = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x0", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsB = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x1", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsC = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x2", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsD = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("x3", TypePrimitive.intType));
        }};

        List<RHSTerm> rhsE = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("y1", TypePrimitive.intType));
            add(new RHSVariable("x0", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsF = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("y1", TypePrimitive.intType));
            add(new RHSVariable("x1", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsG = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("y1", TypePrimitive.intType));
            add(new RHSVariable("x2", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsH = new ArrayList<RHSTerm>() {{
            add(new RHSVariable("y1", TypePrimitive.intType));
            add(new RHSVariable("x3", TypePrimitive.intType));
        }};
        List<RHSTerm> rhsI = new ArrayList<RHSTerm>() {{
            for (int i = 0; i <= 3; i++) {
                add(new RHSVariable("x" + i, TypePrimitive.intType));
            }
            for (int i = 1; i <= 2; i++) {
                add(new RHSVariable("y" + i, TypePrimitive.intType));
            }
            add(new RHSConstInt(0));
            add(new RHSConstInt(1));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_ADD, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_SUB, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
            add(new RHSBinaryOp(ExprBinaryOp.BinaryOp.BINOP_MUL, new RHSNonterminal(nonE), new RHSNonterminal(nonE)));
        }};


        Grammar g = Grammar.empty();
        g.addRule(new Production(nonStart, rhsStart));
        g.addRule(new Production(nonA, rhsA));
        g.addRule(new Production(nonB, rhsB));
        g.addRule(new Production(nonC, rhsC));
        g.addRule(new Production(nonD, rhsD));
        g.addRule(new Production(nonE, rhsE));
        g.addRule(new Production(nonF, rhsF));
        g.addRule(new Production(nonG, rhsG));
        g.addRule(new Production(nonH, rhsH));
        g.addRule(new Production(nonI, rhsI));

        SynthFunction f = new SynthFunction("phi", vars, TypePrimitive.boolType, g);
        prob.addTargetFunction(f);

        SygusExpression constraint1 = new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_OR, new ExprUnaryOp(ExprUnaryOp.UnaryOp.UNOP_NOT, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x0", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x1", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x2", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x3", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x4", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x5", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x6", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x7", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y1", new TypePrimitive("Int")), new ConstInt(2)))))),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x8", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x9", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x10", new TypePrimitive("Int")), new ConstInt(5)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_AND, new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("x11", new TypePrimitive("Int")), new ConstInt(4)),new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y2", new TypePrimitive("Int")), new ConstInt(64))))))),new ExprFunctionCall("phi", Arrays.asList(new Variable("x0", new TypePrimitive("Int")),new Variable("x1", new TypePrimitive("Int")),new Variable("x2", new TypePrimitive("Int")),new Variable("x3", new TypePrimitive("Int")),new Variable("x4", new TypePrimitive("Int")),new Variable("x5", new TypePrimitive("Int")),new Variable("x6", new TypePrimitive("Int")),new Variable("x7", new TypePrimitive("Int")),new Variable("x8", new TypePrimitive("Int")),new Variable("x9", new TypePrimitive("Int")),new Variable("x10", new TypePrimitive("Int")),new Variable("x11", new TypePrimitive("Int")),new Variable("y0", new TypePrimitive("Int")),new Variable("y1", new TypePrimitive("Int")),new Variable("y2", new TypePrimitive("Int")))))))))), new ExprBinaryOp(ExprBinaryOp.BinaryOp.BINOP_EQ, new Variable("y0", new TypePrimitive("Int")), new ConstInt(66)));
        prob.addConstraint(constraint1);

        return prob;
    }
}