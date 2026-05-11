package com.coforge.hsbcdma.service.NotificationServices;

import com.coforge.hsbcdma.entity.Notification2;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.NotificationRepositories.Notification2Repository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Notification2Repository notificationRepository;
    private final UserAccountRepository userAccountRepository;

//    @Transactional
//    public void notifyAllUsers(
//            String module,
//            Long referenceId,
//            String title,
//            String message
//    ) {
//        List<User> users = userAccountRepository.findAllWithRefs();
//
//        for (User user : users) {
//            Notification2 n = new Notification2();
//            n.setUserId(user.getUserId());
//            n.setModule(module);
//            n.setReferenceId(referenceId);
//            n.setTitle(title);
//            n.setMessage(message);
//            n.setIsRead(false);
//            n.setIsDeleted(false);
//
//            notificationRepository.save(n);
//        }
//    }


    @Transactional
    public void notifyAllUsersExceptActor(
            String module,
            Long referenceId,
            String title,
            String message,
            String actorUserId   // 👈 logged-in userId (String)
    ) {
        List<User> users = userAccountRepository.findAllWithRefs();

        for (User user : users) {

            // ✅ Skip the user who performed the action
            if (user.getUserId().equals(actorUserId)) {
                continue;
            }

            Notification2 n = new Notification2();
            n.setUserId(user.getUserId());  // String business userId
            n.setModule(module);
            n.setReferenceId(referenceId);
            n.setTitle(title);
            n.setMessage(message);
            n.setIsRead(false);
            n.setIsDeleted(false);

            notificationRepository.save(n);
        }
    }

}
