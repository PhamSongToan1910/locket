package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.USER_COLLECTION)
public class User extends BaseEntity {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String AVT = "avt";
    public static final String LAST_LOGIN_TIME = "last_login_time";
    public static final String AUTHORITIES = "authorities";
    public static final String LOGIN_TIME = "login_time";
    public static final String PLAYER_ID = "player_id";
    public static final String DEVICE_TOKEN = "device_token";


    @Field(USERNAME)
    private String username;

    @Field(PASSWORD)
    private String password;

    @Field(EMAIL)
    private String email;

    @Field(FIRSTNAME)
    private String firstName;

    @Field(LASTNAME)
    private String lastName;

    @Field(AVT)
    private String avt;

    @Field(LAST_LOGIN_TIME)
    private String lastLoginTime;

    @Field(AUTHORITIES)
    private Set<String> authorities = new HashSet<>();


    @Field(LOGIN_TIME)
    private String loginTime;

    @Field(PLAYER_ID)
    private String playerId;

    @Field(DEVICE_TOKEN)
    private Set<String> deviceToken;


    public String getFullName() {
        if(StringUtils.hasLength(this.firstName) && StringUtils.hasLength(this.lastName)) {
            return this.firstName + " " + this.lastName;
        }
        return null;
    }
}
