package sketch.api.sygus.lang.expr;

/**
 * Abstract class for constant
 *
 * @author Kanghee Park &lt;khpark@cs.wisc.edu&gt;
 */
public abstract class Constant extends SygusExpression {
    @Override
    public boolean isConstant() {
        return true;
    }
}
