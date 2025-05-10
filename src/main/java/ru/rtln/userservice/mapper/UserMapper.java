package ru.rtln.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.rtln.userservice.entity.ProfilePicture;
import ru.rtln.userservice.entity.Role;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.UserModel;
import ru.rtln.userservice.redis.entity.RedisUser;
import ru.rtln.userservice.service.ObjectStoreService;
import ru.rtln.userservice.util.enumeration.UserRoleEnum;

import static ru.rtln.userservice.util.Constants.VORONEZH_PATTERN;
import static ru.rtln.userservice.util.exception.DoesNotHaveException.Code.USER_DOES_NOT_HAVE_DELIVERY_ABILITY;

@Mapper(uses = {
        RoleMapper.class, PermissionMapper.class
})
public abstract class UserMapper {

    @Autowired
    private ObjectStoreService objectStoreService;

    @Value("${minio.buckets.profile-picture}")
    private String profilePictureBucket;

    @Mapping(target = "gender", source = "gender.simpleName")
    @Mapping(target = "profilePictureUrl", source = "user", qualifiedByName = "pictureUrFromEntity")
    public abstract UserModel fromEntity(User user);

    @Mapping(target = "gender", source = "gender.simpleName")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "profilePictureUrl", source = "user", qualifiedByName = "pictureUrFromEntity")
    public abstract UserModel fromEntityToBriefView(User user);

    @Mapping(target = "permissions", source = "user.role.permissions")
    public abstract RedisUser toRedisEntity(User user);

    @Named("pictureUrFromEntity")
    protected String getPictureUrFromEntity(User user) {
        if (user.getProfilePicture() == null) return null;
        return objectStoreService.getFile(user.getProfilePicture().getId(), profilePictureBucket);
    }

    public User toEntity(
            UserModel userModel,
            ProfilePicture profilePicture
    ) {
        return User.builder()
                .username(userModel.getUsername())
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .patronymic(userModel.getPatronymic())
                .birthday(userModel.getBirthday())
                .active(true)
                .dateWorkFrom(userModel.getDateWorkFrom())
                .dateWorkTo(userModel.getDateWorkTo())
                .city(userModel.getCity())
                .address(userModel.getAddress())
                .role(UserRoleEnum.USER.getRole())
                .profilePicture(profilePicture)
                .build();
    }

    public User toEntityFromAuthenticationUserModel(
            AuthenticationUserModel userModel,
            ProfilePicture profilePicture
    ) {
        return User.builder()
                .username(userModel.getUsername())
                .email(userModel.getEmail())
                .lastName(userModel.getLastName())
                .firstName(userModel.getFirstName())
                .patronymic(userModel.getPatronymic())
                .active(userModel.getEnabled())
                .role(new Role(userModel.getAdmin()))
                .profilePicture(profilePicture)
                .build();
    }

    public void mergeFromAuthenticationUserModel(
            User user,
            AuthenticationUserModel userModel
    ) {
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        String patronymic = userModel.getPatronymic();
        if (patronymic != null) {
            user.setPatronymic(patronymic);
        }
        if (user.getRole() == null) {
            user.setRole(new Role(userModel.getAdmin()));
        }
    }

    public void mergeFromUserModel(
            User user,
            UserModel userModel
    ) {
        var address = userModel.getAddress();

        if (address != null && user.getCity() != null && VORONEZH_PATTERN.matcher(user.getCity()).matches()) {
            throw USER_DOES_NOT_HAVE_DELIVERY_ABILITY.get();
        }

        user.setAddress(address);
        user.setBirthday(userModel.getBirthday());
        user.setDateWorkFrom(userModel.getDateWorkFrom());
        user.setDateWorkTo(userModel.getDateWorkTo());
        user.setDateProbationEnd(userModel.getDateProbationEnd());
        user.setPositionName(userModel.getPositionName());
    }
}
