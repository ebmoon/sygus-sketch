package sketch.api.sygus.lang.grammar;

import sketch.api.sygus.lang.SygusNodeVisitor;
import sketch.api.sygus.lang.expr.ExprUnaryOp.UnaryOp;

/**
 * Class for unary operator expressions
 *
 * @author Kanghee Park &lt;khpark@cs.wisc.edu&gt;
 */
public class RHSUnaryOp extends RHSTerm {

    private UnaryOp op;
    private RHSTerm expr;

    public RHSUnaryOp(UnaryOp op, RHSTerm expr) {
        super();
        this.op = op;
        this.expr = expr;
    }

    public Object accept(SygusNodeVisitor visitor) {
        return visitor.visitRHSUnaryOp(this);
    }

    public UnaryOp getOp() {
        return op;
    }

    public RHSTerm getExpr() {
        return expr;
    }

    public String unaryOpToString(UnaryOp op) {
        switch (op) {
            case UNOP_NOT:
            case UNOP_BNOT:
                return "not";
            case UNOP_NEG:
                return "neg";
            default:
                return "UNKNOWN";
        }
    }

    public String toString() {
        return String.format("(%s %s)", unaryOpToString(op), expr.toString());
    }
}
