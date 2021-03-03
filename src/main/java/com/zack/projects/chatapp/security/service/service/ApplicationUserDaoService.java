package com.zack.projects.chatapp.security.service.service;

import com.google.common.collect.Lists;
import com.zack.projects.chatapp.security.service.Repository.UserRepository;
import com.zack.projects.chatapp.security.service.dao.ApplicationUserDao;
import com.zack.projects.chatapp.security.service.dto.ApplicationUser;
import com.zack.projects.chatapp.security.service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.zack.projects.chatapp.security.service.custom.UserRole.*;

@Repository("fake")
@Slf4j
public class ApplicationUserDaoService implements ApplicationUserDao {

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	public ApplicationUserDaoService(UserRepository userRepository,
									 PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<ApplicationUser> selectChatappApplicationUserByUserName(String username) {

		log.info(String.format("Find the user with username [%s]", username));
		Optional<ApplicationUser> applicationUser = getApplicationUsers().stream()
				.filter(appUser -> username.equals(appUser.getUsername()))
				.findFirst();

		log.info(String.format("Check if the user with username [%s] exists.", username));
		if(applicationUser.isPresent()) {
			log.info("Retrieve the user object");
			User user = userRepository.findById(username).get();

			log.info("Create path variable to send with the put notification methods");
			Map< String, String > params = new HashMap< String, String >();
			params.put("username", username);

			log.info("Set the user [%s] status to online", username);
			restTemplate.put(
					"http://USER-SERVICE/users/status/" + username + "/online",
					null,
					params);

			log.info("Calling the notification service to notify all users");
			restTemplate
					.put(
							"http://NOTIFICATION-SERVICE/notifications/notifyUsers/" + username,
							null,
							params);
		}
		
		return applicationUser;
	}

	private List<ApplicationUser> getApplicationUsers() {
		
		List<ApplicationUser> applicationUsers = Lists.newArrayList();

		log.info("Retrieve the list of existing users.");
		userRepository.findAll()
					.stream()
					.forEach(user
							-> {
									applicationUsers.add(new ApplicationUser(
											USER.getGrantedAuthorities(),
											user.getPassword(),
											user.getUsername(),
											user.isAccountNonExpired(),
											user.isAccountNonLocked(),
											user.isCredentialsNonExpired(),
											user.isEnabled()));
							});
		
		return applicationUsers;
							
	}

}
