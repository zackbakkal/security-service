package com.zack.projects.chatapp.security.service.service;

import com.zack.projects.chatapp.security.service.dao.ApplicationUserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {
	
	@Autowired
	private final ApplicationUserDao applicationUserDao;

	public ApplicationUserService(@Qualifier("fake") ApplicationUserDao applicationUserDao) {
		super();
		this.applicationUserDao = applicationUserDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return applicationUserDao.selectChatappApplicationUserByUserName(username)
				.orElseThrow(() 
						-> new UsernameNotFoundException(String.format("Username [%s] not found", username)));
		
	}

}
