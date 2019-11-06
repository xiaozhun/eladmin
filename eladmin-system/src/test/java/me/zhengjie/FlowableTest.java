package me.zhengjie;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowableTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TaskService taskService;
    @Test
    public void startAc(){
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskUser", 666);
        map.put("money", 400);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Expense","loan", map);
        System.out.println("提交成功.流程Id为：" + processInstance.getId());
    }
    @Test
    public void tasklists(){
        List<Task> tasks = taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
        for (Task task : tasks) {
//            taskService.complete(task.getId(),);
            System.out.println("办理人："+task.getAssignee());
        }

    }
    @Test
    public void aplysubmit(){
        Task task = taskService.createTaskQuery().taskAssignee("经理").orderByTaskCreateTime().desc().singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
//        map.put("taskUser", 456);
        map.put("outcome", "通过");
        taskService.complete(task.getId(),map);
        System.out.println(task.toString());
    }
    @Test
    public void startbybusikey(){
        ProcessInstance pi =runtimeService.createProcessInstanceQuery().processInstanceId("6a73971e-b3ea-11e9-b242-5cc5d4b510a3").singleResult();
        System.out.println(pi.getBusinessKey());
        System.out.println(pi.getProcessDefinitionName());
        //通过流程定义id获取流程实体
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
//        获取流程定义
        Process process = bpmnModel.getProcesses().get(0);
//        获取任务名称
        Collection<UserTask> flowElements = process.findFlowElementsOfType(UserTask.class);
        for (UserTask userTask : flowElements) {
            System.out.println(userTask.getName());

        }
        Collection<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class);
        for (StartEvent startEvent : startEvents) {
            System.out.println(startEvent.getName());

        }

    }
    @Test
    public void qianshou(){
        Task task = taskService.createTaskQuery().taskAssignee("1").orderByTaskCreateTime().desc().singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
//        设置审核人
        taskService.addCandidateUser(task.getId(), "2");
//        获得审核人列表
        taskService.getIdentityLinksForTask(task.getId());
//        签收的任务
        taskService.delegateTask(task.getId(), "2");

    }

}
