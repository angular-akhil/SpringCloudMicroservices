package com.appsdeveloperblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;

import com.appsdeveloperblog.photoapp.api.users.data.*;
import com.appsdeveloperblog.photoapp.api.users.messaging.AlbumMessage;

@Service
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	// RestTemplate restTemplate;
	Environment env;
	AlbumsServiceClient albumsServiceClient;
	Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
			AlbumsServiceClient albumsServiceClient, Environment env) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		// this.restTemplate = restTemplate;
		this.albumsServiceClient = albumsServiceClient;
		this.env = env;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {
		// TODO Auto-generated method stub

		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

		usersRepository.save(userEntity);

		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);

		List<GrantedAuthority> authorities = new ArrayList<>();
		Collection<RoleEntity> roles = userEntity.getRoles();
		roles.forEach((role) -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			Collection<AuthorityEntity> authorityEntities = role.getAuthorities();
			authorityEntities.forEach((authority) -> {
				authorities.add(new SimpleGrantedAuthority(authority.getName()));
			});

		});

		if (userEntity == null)
			throw new UsernameNotFoundException(username);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, authorities);
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = usersRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId, String authorization) {
		// TODO Auto-generated method stub
		UserEntity userEntity = usersRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("User Not Found");

		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		// 1. Http communication between microservices using RestTemplate -->>
		/*
		 * String albumsUrl = String.format(env.getProperty("albums.url"), userId);
		 * ResponseEntity<List<AlbumResponseModel>> response =
		 * restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<AlbumResponseModel>>() { });
		 * List<AlbumResponseModel> albumsList = response.getBody();
		 */

		// 2. Http communication between microservices using Feign Client -->>

		List<AlbumResponseModel> albumsList = null;
		logger.info("Before calling albums microservices");
		try {
			albumsList = albumsServiceClient.getAlbums(userId,authorization);

		} catch (FeignException e) {
			logger.error(e.getLocalizedMessage());
		}

		logger.info("After calling albums microservices");
		userDto.setAlbums(albumsList);
		return userDto;
	}

	@Override
	public void updateUserAlbum(AlbumMessage albumMessage) {
		// TODO Auto-generated method stub
		System.out.println(albumMessage.getDescription());

	}

}
