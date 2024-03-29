package sketch.api.sygus.solvers;

import sketch.api.sygus.lang.SygusNodeVisitor;
import sketch.api.sygus.lang.SygusProblem;
import sketch.api.sygus.lang.SynthFunction;
import sketch.api.sygus.lang.expr.*;
import sketch.api.sygus.lang.grammar.*;
import sketch.api.sygus.lang.type.TypePrimitive;
import sketch.api.sygus.util.exception.SketchConversionException;
import sketch.compiler.ast.core.Package;
import sketch.compiler.ast.core.*;
import sketch.compiler.ast.core.exprs.*;
import sketch.compiler.ast.core.stmts.*;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.Type;
import sketch.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SketchBuilder implements SygusNodeVisitor {

    private Program prog;
    private List<Parameter> generatorParams;
    private SynthFunction currSynthFunction;
    private Map<String, Type> nonterminalCxt;
    private Map<String, Integer> generatorCxt;
    private Map<String, Integer> maxCxt;
    private Map<String, List<List<Pair<String, Integer>>>> varDecls;
    private List<List<Pair<String, Integer>>> varDeclCurrentProd;

    public SketchBuilder() {
    }

    public Program program() {
        return prog;
    }

    public VarDeclHints varDeclHints() {
        return new VarDeclHints(varDecls);
    }

    @Override
    public Program visitSygusProblem(SygusProblem problem) {
        String pkgName = "SYGUS";
        List<ExprVar> vars = new ArrayList<ExprVar>();
        List<StructDef> structs = new ArrayList<StructDef>();
        List<Function> funcs = new ArrayList<Function>();
        List<StmtSpAssert> specialAsserts = new ArrayList<StmtSpAssert>();
        List<Package> namespaces = new ArrayList<Package>();

        prog = Program.emptyProgram();
        varDecls = new HashMap<>();

        // Add functions from SynthFunctions
        problem.getTargetFunctions().stream()
                .map(synthFunction -> (List<Function>) synthFunction.accept(this))
                .forEach(funcs::addAll);

        // Add constraint function
        funcs.add(constraintFunction(problem));

        // Must set package to prevent duplicate definition error
        funcs.forEach(func -> func.setPkg(pkgName));

        Package pkg = new Package((FENode) null, pkgName, structs, vars, funcs, specialAsserts);
        namespaces.add(pkg);

        prog = prog.creator().streams(namespaces).create();

        return prog;
    }

    private Function constraintFunction(SygusProblem problem) {
        String functionName = "constraints";
        Function.FunctionCreator fc = Function.creator((FEContext) null, functionName, Function.FcnType.Harness);

        List<Parameter> params = problem.getVariables().stream()
                .map(this::variableToParam)
                .collect(Collectors.toList());

        List<Statement> assertions = problem.getConstraints().stream()
                .map(expr -> (Expression) expr.accept(this))
                .map(expr -> new StmtAssert(prog, expr, null, 0))
                .collect(Collectors.toList());

        Statement body = new StmtBlock(assertions);

        fc.params(params);
        fc.body(body);

        return fc.create();
    }

    private String generatorFunctionName(String targetFunctionName, String nonterminalName) {
        return String.format("%s_%s_gen", targetFunctionName, nonterminalName);
    }

    private StmtVarDecl variableDeclWithHole(Variable v) {
        Type ty = (Type) v.getType().accept(this);
        return new StmtVarDecl((FENode) null, ty, v.getID(), new ExprStar(prog));
    }

    /**
     * Return a list of functions where the first one is the target function,
     * and others are corresponding generator functions
     *
     * @param func A synthesis target function
     * @return A list of functions
     */
    @Override
    public List<Function> visitSynthFunction(SynthFunction func) {
        ArrayList<Function> sketchFuncs = new ArrayList<Function>();
        Function.FunctionCreator fc = Function.creator((FEContext) null, func.getFunctionID(), Function.FcnType.Static);

        // Function arguments
        List<Parameter> params = func.getArgs().stream()
                .map(this::variableToParam)
                .collect(Collectors.toList());
        fc.params(params);

        // Function return type
        Type returnType = (Type) func.getReturnType().accept(this);
        fc.returnType(returnType);

        this.generatorParams = params;
        this.currSynthFunction = func;

        // Function body
        List<Function> generatorFuncs = (List<Function>) func.getGrammar().accept(this);
        if (generatorFuncs.isEmpty())
            throw new SketchConversionException("Grammar must have at least one symbol");
        Function startFunc = generatorFuncs.get(0);
        String startFuncName = startFunc.getName();
        List<Expression> paramVars = params.stream()
                .map(param -> new ExprVar((FENode) null, param.getName()))
                .collect(Collectors.toList());
        Expression generatorCall = new ExprFunCall((FENode) null, startFuncName, paramVars);
        Statement body = new StmtReturn((FENode) null, generatorCall);

        fc.body(body);

        this.generatorParams = null;
        this.currSynthFunction = null;

        sketchFuncs.add(fc.create());
        sketchFuncs.addAll(generatorFuncs);

        return sketchFuncs;
    }

    private Parameter variableToParam(Variable var) {
        Type ty = (Type) var.getType().accept(this);

        return new Parameter((FENode) null, ty, var.getID());
    }

    @Override
    public sketch.compiler.ast.core.typs.TypePrimitive visitTypePrimitive(TypePrimitive ty) {
        switch (ty.getPredefinedType()) {
            case TYPE_INT:
                return sketch.compiler.ast.core.typs.TypePrimitive.inttype;
            case TYPE_BOOLEAN:
                return sketch.compiler.ast.core.typs.TypePrimitive.bittype;
            default:
                throw new SketchConversionException("Unknown predefined type");
        }
    }

    @Override
    public ExprConstInt visitConstBool(ConstBool b) {
        return b.getValue() ? ExprConstInt.one : ExprConstInt.zero;
    }

    @Override
    public ExprConstInt visitConstInt(ConstInt n) {
        return ExprConstInt.createConstant(n.getValue());
    }

    @Override
    public ExprUnary visitExprUnaryOp(ExprUnaryOp e) {
        Expression subExpr = (Expression) e.getExpr().accept(this);

        switch (e.getOp()) {
            case UNOP_NOT:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_NOT, subExpr);
            case UNOP_BNOT:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_BNOT, subExpr);
            case UNOP_NEG:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_NEG, subExpr);
            default:
                throw new SketchConversionException("Unknown unary operator");
        }
    }

    @Override
    public ExprBinary visitExprBinaryOp(ExprBinaryOp e) {
        Expression left = (Expression) e.getLeft().accept(this);
        Expression right = (Expression) e.getRight().accept(this);

        switch (e.getOp()) {
            // Arithmetic
            case BINOP_ADD:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_ADD, left, right);
            case BINOP_SUB:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_SUB, left, right);
            case BINOP_MUL:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_MUL, left, right);
            case BINOP_DIV:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_DIV, left, right);
            case BINOP_MOD:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_MOD, left, right);
            // Boolean
            case BINOP_AND:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_AND, left, right);
            case BINOP_OR:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_OR, left, right);
            // Comparison
            case BINOP_EQ:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_EQ, left, right);
            case BINOP_NEQ:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_NEQ, left, right);
            case BINOP_LT:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_LT, left, right);
            case BINOP_LE:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_LE, left, right);
            case BINOP_GT:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_GT, left, right);
            case BINOP_GE:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_GE, left, right);
            default:
                throw new SketchConversionException("Unknown binary operator");
        }
    }

    @Override
    public ExprTernary visitExprIfThenElse(ExprIfThenElse e) {
        Expression condExpr = (Expression) e.getCond().accept(this);
        Expression consExpr = (Expression) e.getCons().accept(this);
        Expression altExpr = (Expression) e.getAlt().accept(this);

        return new ExprTernary((FENode) null, ExprTernary.TEROP_COND, condExpr, consExpr, altExpr);
    }

    @Override
    public ExprFunCall visitFunctionCall(ExprFunctionCall f) {
        List<Expression> params = f.getArgs().stream()
                .map(e -> (Expression) e.accept(this))
                .collect(Collectors.toList());

        return new ExprFunCall((FENode) null, f.getFunctionID(), params);
    }

    @Override
    public ExprVar visitVariable(Variable v) {
        return new ExprVar((FENode) null, v.getID());
    }

    /**
     * Returns a list of generator function,
     * where the first function is the starting symbol
     */
    @Override
    public List<Function> visitGrammar(Grammar g) {
        nonterminalCxt = new HashMap<>();
        for (Production rule : g.getRules()) {
            Nonterminal n = rule.getLHS();
            nonterminalCxt.put(n.getName(), (Type) n.getType().accept(this));
        }

        List<Function> generatorFuncs = g.getRules().stream()
                .map(rule -> (Function) rule.accept(this))
                .collect(Collectors.toList());

        return generatorFuncs;
    }

    /**
     * should not be called
     */
    @Override
    public Object visitNonterminal(Nonterminal n) {
        throw new SketchConversionException("visitNonterminal should not be called");
    }

    @Override
    public Function visitProduction(Production prod) {
        String currNonterminal = prod.getLHS().getName();
        String generatorID = generatorFunctionName(
                currSynthFunction.getFunctionID(),
                currNonterminal
        );

        Type returnType = (Type) prod.getLHS().getType().accept(this);
        maxCxt = new HashMap<>();

        varDeclCurrentProd = new ArrayList<>();
        varDecls.put(currNonterminal, varDeclCurrentProd);

        Function.FunctionCreator fc = Function.creator((FEContext) null, generatorID, Function.FcnType.Generator);
        List<Statement> bodyStmts = prod.getRHSList().stream()
                .flatMap(t -> doRHSTerm(t).stream())
                .collect(Collectors.toList());

        bodyStmts.add(new StmtAssert((FENode) null, new ExprConstInt(0), 0));
        Statement body = new StmtBlock((FENode) null, bodyStmts);

        fc.params(generatorParams);
        fc.returnType(returnType);
        fc.body(body);

        return fc.create();
    }

    public List<Statement> doRHSTerm(RHSTerm t) {
        generatorCxt = new HashMap<>();
        Expression e = (Expression) t.accept(this);

        List<Type> types = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Expression> inits = new ArrayList<>();

        List<Pair<String, Integer>> varDeclCurrentRule = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : generatorCxt.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            int numPrevNonterminals = maxCxt.getOrDefault(key, 0);
            if (numPrevNonterminals >= value)
                continue;

            // This value is new maximum
            maxCxt.put(key, value);

            for (int i = numPrevNonterminals; i < value; i++) {
                String varID = String.format("var_%s_%d", key, i);
                String funID = String.format(
                        "%s_%s_gen",
                        currSynthFunction.getFunctionID(),
                        key
                );

                List<Expression> paramVars = generatorParams.stream()
                        .map(param -> new ExprVar((FENode) null, param.getName()))
                        .collect(Collectors.toList());

                names.add(varID);
                types.add(nonterminalCxt.get(key));
                inits.add(new ExprFunCall((FENode) null, funID, paramVars));
                varDeclCurrentRule.add(new Pair(key, i));
            }
        }

        varDeclCurrentProd.add(varDeclCurrentRule);

        StmtVarDecl stmtVarDecl = new StmtVarDecl((FENode) null, types, names, inits);
        StmtReturn stmtReturn = new StmtReturn((FENode) null, e);
        StmtIfThen stmtIfThen = new StmtIfThen((FENode) null, new ExprStar(prog), stmtReturn, null);

        List<Statement> stmt;
        if (!names.isEmpty()) {
            stmt = Arrays.asList(stmtVarDecl, stmtIfThen);
        } else {
            stmt = Collections.singletonList(stmtIfThen);
        }

        return stmt;
    }

    @Override
    public ExprVar visitRHSVariable(RHSVariable v) {
        return new ExprVar((FENode) null, v.getID());
    }

    @Override
    public ExprVar visitRHSNonterminal(RHSNonterminal n) {
        String nonterminalID = n.getNonterminal().getName();
        int cnt = generatorCxt.getOrDefault(nonterminalID, 0);
        String varID = String.format("var_%s_%d", nonterminalID, cnt);

        generatorCxt.put(nonterminalID, cnt + 1);

        return new ExprVar((FENode) null, varID);
    }

    @Override
    public ExprConstInt visitRHSConstBool(RHSConstBool b) {
        return b.getValue() ? ExprConstInt.one : ExprConstInt.zero;
    }

    @Override
    public ExprConstInt visitRHSConstInt(RHSConstInt n) {
        return ExprConstInt.createConstant(n.getValue());
    }

    @Override
    public ExprFunCall visitRHSFunctionCall(RHSFunctionCall f) {
        List<Expression> params = f.getArgs().stream()
                .map(e -> (Expression) e.accept(this))
                .collect(Collectors.toList());

        return new ExprFunCall((FENode) null, f.getFunctionID(), params);
    }

    @Override
    public ExprUnary visitRHSUnaryOp(RHSUnaryOp e) {
        Expression subExpr = (Expression) e.getExpr().accept(this);

        switch (e.getOp()) {
            case UNOP_NOT:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_NOT, subExpr);
            case UNOP_BNOT:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_BNOT, subExpr);
            case UNOP_NEG:
                return new ExprUnary((FENode) null, ExprUnary.UNOP_NEG, subExpr);
            default:
                throw new SketchConversionException("Unknown unary operator");
        }
    }

    @Override
    public ExprBinary visitRHSBinaryOp(RHSBinaryOp e) {
        Expression left = (Expression) e.getLeft().accept(this);
        Expression right = (Expression) e.getRight().accept(this);

        switch (e.getOp()) {
            // Arithmetic
            case BINOP_ADD:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_ADD, left, right);
            case BINOP_SUB:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_SUB, left, right);
            case BINOP_MUL:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_MUL, left, right);
            case BINOP_DIV:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_DIV, left, right);
            case BINOP_MOD:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_MOD, left, right);
            // Boolean
            case BINOP_AND:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_AND, left, right);
            case BINOP_OR:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_OR, left, right);
            // Comparison
            case BINOP_EQ:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_EQ, left, right);
            case BINOP_NEQ:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_NEQ, left, right);
            case BINOP_LT:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_LT, left, right);
            case BINOP_LE:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_LE, left, right);
            case BINOP_GT:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_GT, left, right);
            case BINOP_GE:
                return new ExprBinary((FENode) null, ExprBinary.BINOP_GE, left, right);
            default:
                throw new SketchConversionException("Unknown binary operator");
        }
    }

    @Override
    public ExprTernary visitRHSIfThenElse(RHSIfThenElse e) {
        Expression condExpr = (Expression) e.getCond().accept(this);
        Expression consExpr = (Expression) e.getCons().accept(this);
        Expression altExpr = (Expression) e.getAlt().accept(this);

        return new ExprTernary((FENode) null, ExprTernary.TEROP_COND, condExpr, consExpr, altExpr);
    }
}
