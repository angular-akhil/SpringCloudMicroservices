package com.appsdeveloperblog.photoapp.api.albums.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.photoapp.api.albums.data.AlbumEntity;

@Service
public class AlbumMessageProducer {

	private final RabbitTemplate rabbitTemplate;

	public AlbumMessageProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	
	public void sendMessage(AlbumEntity albumEntity) {
		AlbumMessage albumMessage = new AlbumMessage();
		albumMessage.setId(albumEntity.getId());
		albumMessage.setName(albumEntity.getName());
		albumMessage.setDescription(albumEntity.getDescription());
		albumMessage.setAlbumId(albumEntity.getAlbumId());
		albumMessage.setUserId(albumEntity.getUserId());
		rabbitTemplate.convertAndSend("userAlbumQueue", albumMessage);
		//userAlbumQueue is the routingKey here ,where this albumMessage will be routed and stored
	}
}
