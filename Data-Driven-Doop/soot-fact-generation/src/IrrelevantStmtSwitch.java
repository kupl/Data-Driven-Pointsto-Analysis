import soot.jimple.*;
import soot.jimple.StmtSwitch;
import soot.Value;

public class IrrelevantStmtSwitch implements StmtSwitch
{
    public boolean relevant = true;

    public void caseAssignStmt(AssignStmt stmt)/*Done*/
    {
        boolean phantom = false;

        do {
            if (!soot.options.Options.v().allow_phantom_refs())
                break;

            Value right = stmt.getRightOp();

            if (!(right instanceof InvokeExpr))
                break;

            if (!((InvokeExpr) right).getMethodRef().declaringClass().isPhantom())
                break;

            phantom = true;

        } while (false);

        relevant = !phantom;
    }

    public void caseBreakpointStmt(BreakpointStmt stmt)
    {
        relevant = true;
    }
  
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
    {
        relevant = true;
    }
  
    public void caseExitMonitorStmt(ExitMonitorStmt stmt)
    {
        relevant = true;
    }
  
    public void caseGotoStmt(GotoStmt stmt)
    {
        relevant = true;
    }
  
    public void caseIdentityStmt(IdentityStmt stmt)/*Done*/
    {
        relevant = true;
    }

    public void caseIfStmt(IfStmt stmt)
    {
        relevant = true;
    }
  
    public void caseInvokeStmt(InvokeStmt stmt)/*Done*/
    {
        if (soot.options.Options.v().allow_phantom_refs())
            relevant = !stmt.getInvokeExpr().getMethodRef().declaringClass().isPhantom();
        else
            relevant = true;
    }

    public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
    {
        relevant = true;
    }
  
    public void caseNopStmt(NopStmt stmt)
    {
        relevant = true;
    }

    public void caseRetStmt(RetStmt stmt)/*Done*/
    {
        relevant = true;
    }
  
    public void caseReturnStmt(ReturnStmt stmt)
    {
        relevant = true;
    }
  
    public void caseReturnVoidStmt(ReturnVoidStmt stmt)
    {
        relevant = true;
    }
  
    public void caseTableSwitchStmt(TableSwitchStmt stmt)
    {
        relevant = true;
    }
  
    public void caseThrowStmt(ThrowStmt stmt)/*Done*/
    {
        relevant = true; 
    }
  
    public void defaultCase(Object obj)
    {
        throw new RuntimeException("uh, why is this invoked?");
    }
}
