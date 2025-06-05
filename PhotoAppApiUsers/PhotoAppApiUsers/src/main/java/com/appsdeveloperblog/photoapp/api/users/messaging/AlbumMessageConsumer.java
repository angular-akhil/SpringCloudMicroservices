package com.appsdeveloperblog.photoapp.api.users.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;

@Service
public class AlbumMessageConsumer {
	private final UsersService usersService;

	public AlbumMessageConsumer(UsersService usersService) {
		this.usersService = usersService;
	}
	
	@RabbitListener(queues="userAlbumQueue")
	public void consumeMessage(AlbumMessage albumMessage) {
		usersService.updateUserAlbum(albumMessage);
	}

}
