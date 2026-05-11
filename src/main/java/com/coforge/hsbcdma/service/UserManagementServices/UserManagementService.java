package com.coforge.hsbcdma.service.UserManagementServices;

import com.coforge.hsbcdma.repository.UserManagementRepositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserManagementService {

    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private ModuleActionRepository moduleActionRepository;
    @Autowired
    private ChildModuleRepository childModuleRepository;
    @Autowired
    private ChildModuleActionRepository childModuleActionRepository;


    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);


//    public void createModuleAndAction(List<ModuleDTO> moduleDTOList){
//
//        if(moduleDTOList.isEmpty()){
//            throw new RuntimeException("Module Action List is Empty");
//        }
//
//        for(ModuleDTO moduleDTO : moduleDTOList){
//
//            String moduleName = moduleDTO.getModuleName();
//            List<String> moduleActions = moduleDTO.getModuleActions();
//
//            if(moduleActions == null || moduleActions.isEmpty()){
//                throw new RuntimeException("Module Action List is Empty");
//            }
//            assignActionsToModule(moduleName,moduleActions);
//            List<ChildModuleDTO> childModuleDTOList = moduleDTO.getChildModuleDTO();
//
//            if(childModuleDTOList.isEmpty()) continue;
//
//            for(ChildModuleDTO childModuleDTO : childModuleDTOList){
//                assignActionsToChildModule(moduleName,childModuleDTO.getChildModuleName(),childModuleDTO.getChildModuleActions());
//            }
//        }
//    }

    //Assinging Module and Actions names
//    public void assignActionsToModule(String moduleName, List<String> actionNames){
//
//        if(actionNames == null || actionNames.isEmpty()) return;
//
//        // Module (find or create)
//        Module module = moduleRepository.findByModule(moduleName)
//                .orElseGet(() -> moduleRepository.save(new Module(moduleName)));
//
//        //Action (find or create)
//        for(String actionName : actionNames){
//            Action action = actionRepository.findByAction(actionName)
//                    .orElseGet(() -> actionRepository.save(new Action(actionName)));
//        }
//
//        List<ModuleAction> toInsert = new ArrayList<>();
//        for (String name : actionNames) {
//            Action action = actionRepository.findByAction(name).get();
//            boolean exists = moduleActionRepository.existsByModuleIdAndActionId(module.getId(), action.getId());
//            if (!exists) {
//                ModuleAction ma = new ModuleAction();
//                ma.setModule(module);
//                ma.setAction(action);
//                toInsert.add(ma);
//            }
//        }
//
//        if (toInsert.isEmpty()) return;
//
//        List<ModuleAction> saved = moduleActionRepository.saveAll(toInsert);
//     }
//
//    //Assinging ChildModule and Actions names
//    public void assignActionsToChildModule(String moduleName,String childModuleName, List<String> actionNames){
//
//        if(actionNames == null || actionNames.isEmpty()) return;
//
//        // Find Module
//        Module module = moduleRepository.findByModule(moduleName)
//                .orElseThrow(() -> new RuntimeException(moduleName+" Module not found"));
//
//        ChildModule childModuleentity = new ChildModule();
//        childModuleentity.setChildModule(childModuleName);
//        childModuleentity.setModule(module);
//        childModuleRepository.save(childModuleentity);
//
//        //Action (find or create)
//        for(String actionName : actionNames){
//            Action action = actionRepository.findByAction(actionName)
//                    .orElseGet(() -> actionRepository.save(new Action(actionName)));
//        }
//
//        // Filter out existing links to avoid unique constraint violations
//        List<ChildModuleAction> toInsert = new ArrayList<>();
//        for (String name : actionNames) {
//            Action action = actionRepository.findByAction(name).get();
//            boolean exists = childModuleActionRepository.existsByChildModuleIdAndActionId(module.getId(), action.getId());
//            if (!exists) {
//                ChildModuleAction cma = new ChildModuleAction();
//                cma.setChildModule(childModuleentity);
//                cma.setAction(action);
//                toInsert.add(cma);
//            }
//        }
//
//        if (toInsert.isEmpty()) return;
//
//        childModuleActionRepository.saveAll(toInsert);
//    }
}
