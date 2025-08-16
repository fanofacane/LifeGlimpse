package com.sky.skybackend.domain.pojo;

import com.sky.skybackend.domain.vo.UserVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends UserVO {
        private String email;
        private String password;
        private LocalDateTime createTime;

}
