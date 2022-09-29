package com.kbeauty.gbt.service;

import com.kbeauty.gbt.dao.UserMapper2;
import com.kbeauty.gbt.dao.UserRepo2;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.enums.UserRole2;
import com.kbeauty.gbt.entity.view.FplusUserView;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService2 extends CommonService {

    @Autowired
    private UserMapper2 userMapper2;

    @Autowired
    private UserRepo2 userRepo2;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Uploader uploader;

    @Autowired
    private StorageService uploadSerivce;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucket;

    @Value("${spring.cloud.gcp.storage.bucket.user}")
    private String userFolder;

    @Value("${spring.cloud.gcp.storage.url}")
    private String storageUrl;

    @Value("${spring.cloud.gcp.storage.bucket.training}")
    private String trainingFolder;

    public User2 save(User2 user2) {
        boolean isNew = false;


        String userId = user2.getUserId();
        if (StringUtil.isEmpty(userId)) {
//            유저아이디가 없으면 새로 가입
            isNew = true;
            userId = CommonUtil.getGuid();
            user2.setUserId(userId);
        }

//        String email = user2.getEmail();
//        if (email != null) {
//            try {
//                User2 findByEmail = userRepo2.findByEmail(email);
//                if (findByEmail != null) {
//                    if (findByEmail.isSame(user2)) {
//                        throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
//                    }
//                }
//            } catch (Exception e) {
//                throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
//            }
//        }

        String userRole = user2.getUserRole();
        if(userRole == null) {
            user2.setUserRole(UserRole2.USER.getCode());
        }

        String friendId = user2.getFriendId();
        if(friendId.isEmpty() || friendId == null) {
            user2.setFriendId(null);
        }
        else {
            user2.setFriendId(friendId);
        }

        String dateStr = CommonUtil.getSysTime();
        user2.setBasicInfo(dateStr, userId);

        user2.setPassword(passwordEncoder.encode(user2.getPassword()));

        userRepo2.save(user2);
        return user2;
    }

    public boolean checkEmail(String email) {
        return userRepo2.existsByEmail(email);
}

    public User2 getUser(String userid) {
        return userRepo2.findByUserId(userid);
    }

    public User2 saveImg(MultipartFile faceImg, User2 user, String uploadFileName) throws IOException {

        String orgFileName = faceImg.getOriginalFilename();
        String imgId = CommonUtil.getGuid();
        String neoFileName = FileUtil.getNeoFileName(imgId, orgFileName); // 확장자는 여기서 붙
        String fileName = FileUtil.getStoragePath(

                userFolder, user.getUserId(), neoFileName, StoragePath.CONTENT_USER);
        String dir = bucket;
        user.setImageDir(dir);
        user.setImageName(fileName);


        uploadSerivce.saveUserImg(faceImg, fileName);

        user = save(user);
        setImgUrl(user);

        return user;
    }

    private void setImgUrl(User2 user) {
        user.setImgUrl(getUrl(user)); // 사용자 이미지 위치 설정
    }


    public String getUrl(User2 user) {
        String imageDir = user.getImageDir();

        if (!StringUtil.isEmpty(imageDir) && imageDir.startsWith("http")) {
            return imageDir;
        }

        if (StringUtil.isEmpty(user.getImageName())) {
            return "/images/no_user_img.png";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(storageUrl).append("/").append(imageDir).append("/").append(user.getImageName());

        return sb.toString();
    }

    public User2 deleteImg(User2 user) {
        user.setImageDir(null);
        user.setImageName(null);

        user = save(user);
        setImgUrl(user);

        return user;
    }

    public User2 login(User2 user2) {
        return userMapper2.loginFplus(user2);
    }
}
