package link.bek.poc.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import link.bek.poc.model.Vacation;

public class ApproveVacationTest extends JbpmJUnitBaseTestCase {

    private static final String PROCESS_PATH = "RequestVacation.bpmn2";
    private static final String PROCESS_ID = "vacation-process.RequestVacation";
    
    public ApproveVacationTest() {
        super(true, true, "org.jbpm.test.persistence");
        
        // preferably testing environment
        System.setProperty("user.service.host", "localhost");
        System.setProperty("user.service.port", "8182");
        System.setProperty("vacation.service.host", "localhost");
        System.setProperty("vacation.service.port", "8181");
    }

    public KieSession createKSession(String... process) {
        createRuntimeManager(process);
        return getRuntimeEngine().getKieSession();
    }
    
    @Test
    public void testAddVacation() throws Exception {
        addWorkItemHandler("Rest", new RESTWorkItemHandler());
        KieSession ksession = createKSession(PROCESS_PATH);
        Map<String, Object> params = new HashMap<String, Object>();
        Vacation vacation = new Vacation();
        vacation.setEmployee("ehorton");
        vacation.setDescription("Dovolena");
        vacation.setFrom(new Date(System.currentTimeMillis()));
        vacation.setTo(new Date(System.currentTimeMillis()));
        params.put("vacation", vacation);
        ProcessInstance processInstance = ksession.startProcess(PROCESS_ID, params);
        Object val = getVariableValue("vacation", processInstance.getId(), ksession);
        assertNotNull(val); // if null, Vacation changed and Vacation Service needs to be updated
        
    }

    
}
