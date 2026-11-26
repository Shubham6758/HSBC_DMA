package com.coforge.hsbcdma.service.NotificationServices;

import com.coforge.hsbcdma.entity.Notification2;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.NotificationRepositories.Notification2Repository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final Notification2Repository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void notifyAllUsersExceptActor(
            String module,
            Long referenceId,
            String title,
            String message,
            String actorUserId
    ) {

        log.info("🔥 STEP 1: Entered notifyAllUsersExceptActor");

        List<User> users = userAccountRepository.findAll();

        log.info("🔥 STEP 2: Users fetched = {}", users.size());
        log.info("🔥 STEP 3: Actor userId = {}", actorUserId);

        for (User user : users) {

            Notification2 n = new Notification2();
            n.setUserId(user.getUserId());
            n.setModule(module);
            n.setReferenceId(referenceId);
            n.setTitle(title);
            n.setMessage(message);

            boolean isActor = user.getUserId().equals(actorUserId);

            n.setIsRead(isActor); // ✅ actor auto-read
            n.setIsDeleted(false);

            notificationRepository.save(n);
        }

        log.info("✅ STEP 4: Notification method completed");
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void notifyUsers(
//            String module,
//            Long referenceId,
//            String title,
//            String message,
//            String actorUserId
//    ) {
//
//        List<User> users = userAccountRepository.findAll();
//
//        // ✅ STEP 1: Get actor (creator)
//        User actor = userAccountRepository.findByUserId(actorUserId)
//                .orElseThrow(() -> new RuntimeException("Actor not found"));
//
//        // ✅ STEP 2: Extract actor HBU (subdepartment)
//        SubDepartment actorSubDept = actor.getSubdepartment();
//
//        for (User user : users) {
//
//            String roleName = user.getRole() != null
//                    ? user.getRole().getRole()
//                    : null;
//
//            boolean isActor = user.getUserId().equals(actorUserId);
//
//            // ✅ CONDITION 1: GLOBAL ROLES
//            if (isGlobalRole(roleName)) {
//                saveNotification(user, module, referenceId, title, message, isActor);
//                continue;
//            }
//
//            // ✅ CONDITION 2: HBU MATCHING
//            if (isSameHbu(user, actorSubDept)) {
//                saveNotification(user, module, referenceId, title, message, isActor);
//            }
//        }
//    }
//
//    private boolean isGlobalRole(String roleName) {
//        if (roleName == null) return false;
//
//        return roleName.equalsIgnoreCase("SYSTEM") ||
//                roleName.equalsIgnoreCase("DEVELOPER") ||
//                roleName.equalsIgnoreCase("PMO") ||
//                roleName.equalsIgnoreCase("SUPER-ADMIN");
//    }
//
//    private boolean isSameHbu(User user, SubDepartment actorSubDept) {
//
//        if (actorSubDept == null || user.getSubdepartment() == null) {
//            return false;
//        }
//
//        // ✅ Check department = HBU (safety)
//        if (actorSubDept.getDepartment() == null ||
//                user.getSubdepartment().getDepartment() == null) {
//            return false;
//        }
//
//        boolean isHbuDept =
//                "HBU".equalsIgnoreCase(actorSubDept.getDepartment().getName()) &&
//                        "HBU".equalsIgnoreCase(user.getSubdepartment().getDepartment().getName());
//
//        if (!isHbuDept) return false;
//
//        // ✅ Match subdepartment (Engineering, DPA, etc.)
//        return actorSubDept.getId().equals(user.getSubdepartment().getId());
//    }

//    private void saveNotification(User user,
//                                  String module,
//                                  Long referenceId,
//                                  String title,
//                                  String message,
//                                  boolean isActor) {
//
//        Notification2 n = new Notification2();
//        n.setUserId(user.getUserId());
//        n.setModule(module);
//        n.setReferenceId(referenceId);
//        n.setTitle(title);
//        n.setMessage(message);
//
//        // ✅ Actor sees it as read
//        n.setIsRead(isActor);
//        n.setIsDeleted(false);
//
//        notificationRepository.save(n);
//    }


}